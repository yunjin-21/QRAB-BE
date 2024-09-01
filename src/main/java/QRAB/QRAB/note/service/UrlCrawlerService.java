package QRAB.QRAB.note.service;

import QRAB.QRAB.category.domain.Category;
import QRAB.QRAB.category.repository.CategoryRepository;
import QRAB.QRAB.chatgpt.dto.ChatgptRequestDTO;
import QRAB.QRAB.chatgpt.dto.ChatgptResponseDTO;
import QRAB.QRAB.chatgpt.service.ChatgptService;
import QRAB.QRAB.note.config.S3Config;
import QRAB.QRAB.user.domain.User;
import QRAB.QRAB.user.excepiton.NotFoundMemberException;
import QRAB.QRAB.user.repository.UserRepository;
import QRAB.QRAB.note.domain.Note;
import QRAB.QRAB.note.dto.BlogRequestDTO;
import QRAB.QRAB.note.repository.NoteRepository;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
//블로그 2개 + 스크린 샷 의 경우는 url 사용함
@Service //db접근을 위한 repository와 사용자 인터페이스를 처리하는 controller 사이의 중간 계층 역할
@Transactional(readOnly = true)//데이터를 조회만 하기 - 읽기전용
@RequiredArgsConstructor // final 필드 + @NonNull 애노테이션이 붙은 필드에 대한 생성자를 자동으로 생성
public class UrlCrawlerService {
    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final S3Config s3Config;
    private final OCRService ocrService;
    private final ChatgptService chatgptService;

    @Transactional(readOnly = false)
    public ResponseEntity<?> crawlBlogAndSave(BlogRequestDTO blogRequestDTO) throws IOException{
        User user = userRepository.findOneWithAuthoritiesByUsername(blogRequestDTO.getEmail())
                .orElseThrow(() -> new NotFoundMemberException("Could not find user with email: " + blogRequestDTO.getEmail()));
        Category category = categoryRepository.findById(blogRequestDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Could not find category"));
        Note note = blogRequestDTO.toEntity(user, category); //Note 객체 생성

        /*if(category.getParentCategory() != null){ //상위 카테고리에서 노트 생성 불가

        }*/
        if(blogRequestDTO.getUrl().contains("velog")){
            Document doc = Jsoup.connect(blogRequestDTO.getUrl()).get(); // Jsoup을 사용하여 URL에서 HTML문서 가져오기
            note.setTitle(doc.title());//제목 저장
            Elements elements = doc.body().select("*"); //모든 텍스트 요소 가져오기
            StringBuilder stringBuilder = new StringBuilder();
            for(Element element : elements){
                stringBuilder.append(element.ownText()).append("\n");
            }
            note.setContent(stringBuilder.toString()); //텍스트를 content 필드에 저장 - 객체 상태를 설정
        }else if(blogRequestDTO.getUrl().contains("tistory")){
            Document doc = Jsoup.connect(blogRequestDTO.getUrl()).get(); // Jsoup을 사용하여 URL에서 HTML문서 가져오기
            note.setTitle(doc.title()); //제목 가져오기
            String content = getContentFromDocument(doc);
            if(content != null){
                note.setContent(content);
            }else{
                throw new RuntimeException("Could not find content in tistory");
            }
        }

        String chatgptContent = chatgptService.getSummary(note.getContent());
        System.out.println(chatgptContent);
        note.setChatgptContent(chatgptContent);


        Note savedNote = noteRepository.save(note); // note 객체를 db에 저장
        System.out.println(chatgptContent);
        System.out.println(savedNote.getTitle());
        System.out.println(savedNote.getContent());
        System.out.println(savedNote.getUrl());
        return ResponseEntity.ok(blogRequestDTO);
    }
    private String getContentFromDocument(Document document){
        String[] possibleSelectors = {
                ".article_view",  // 일반적인 선택자
                ".post-content",  // 다른 가능성
                ".entry-content", // 또 다른 가능성
                "#content",       // ID 선택자
                "article",        // article 태그
                ".blog-content",  // 추가 가능성
                ".post-body",     // 추가 가능성
                ".content",       // 추가 가능성
                ".entry",         // 추가 가능성
                "#post",           // 추가 가능성
                ".tt_article_useless_p_margin.contents_style"
        };
        for(String selector : possibleSelectors){
            Elements elements = document.select(selector);
            if(!elements.isEmpty()){
                StringBuilder sb = new StringBuilder();
                for(Element element : elements){
                    sb.append(element.text()).append("\n");
                }
                return sb.toString();
            }
        }
        return null;
    }

    // 스크린샷 캡쳐 후 naver clover ocr api를 사용해서 text 저장
    @Transactional(readOnly = false)
    public ResponseEntity<?> capturePageAndSave(BlogRequestDTO blogRequestDTO) throws IOException{
        User user = userRepository.findOneWithAuthoritiesByUsername(blogRequestDTO.getEmail())
                .orElseThrow(() -> new NotFoundMemberException("Could not find user with email: " + blogRequestDTO.getEmail()));
        Category category = categoryRepository.findById(blogRequestDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Could not find category"));
        Note note = blogRequestDTO.toEntity(user, category); //Note 객체 생성

        WebDriverManager.chromedriver().setup(); //Selenium WebDriver 를 사용할 때 ChromeDriver 를 자동으로 설정
        ChromeOptions options = new ChromeOptions(); // Chrome 브라우저의 동작 방식을 세밀하게 제어 가능
        options.addArguments("--headless"); // 브라우저 UI를 표시하지 않음
        options.addArguments("--disable-gpu"); //GPU 비활성화
        options.addArguments("--window-size=1920,1080"); // 브라우저 너비 1920 픽셀 높이 1080 픽셀

        WebDriver driver = new ChromeDriver(options); //Selenium WebDriver에서 Chrome 브라우저를 실행

        try{
            driver.get(note.getUrl()); // Selenium WebDriver 가 브라우저를 열고, note.getUrl() 메서드로 반환된 URL 의 웹 페이지를 로드

            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            System.out.println("file : " + screenshot.toString());
            String screenshotFilename = System.currentTimeMillis() + ".png";
            String imageUrl = s3Config.uploadScreenshot(screenshot, screenshotFilename);

            String extractedText = ocrService.processImage(imageUrl);

            note.setContent(extractedText);
            //스크린샷은 file path + url 둘 다 가짐 -> url 만 가지는 것도 좋을 듯
            //imageUrl : https://qrab.s3.ap-northeast-2.amazonaws.com/1725037133946.png
            //url : https://cse.ewha.ac.kr/cse/index.do

            String totalImageUrl = note.getUrl();
            int idx1 = 0;
            for(int i = 0; i < totalImageUrl.length(); i++){
                if(totalImageUrl.charAt(i) == '/'){
                    idx1 = i;
                }
            }
            //제목 저장
            note.setTitle(totalImageUrl.substring(idx1 + 1));

            String chatgptContent = chatgptService.getSummary(note.getContent());
            System.out.println(chatgptContent);
            note.setChatgptContent(chatgptContent);


            Note savedNote = noteRepository.save(note);
            System.out.println(savedNote.getContent());
            System.out.println(savedNote.getTitle());
            System.out.println(savedNote.getUrl());
            System.out.println(savedNote.getChatgptContent());
            return ResponseEntity.ok(blogRequestDTO);
        }catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to capture and process the page" , HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            driver.quit();
        }
    }
}
