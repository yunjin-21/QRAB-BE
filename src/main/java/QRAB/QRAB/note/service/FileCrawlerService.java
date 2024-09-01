package QRAB.QRAB.note.service;

import QRAB.QRAB.category.domain.Category;
import QRAB.QRAB.category.repository.CategoryRepository;
import QRAB.QRAB.chatgpt.service.ChatgptService;
import QRAB.QRAB.user.domain.User;
import QRAB.QRAB.user.excepiton.NotFoundMemberException;
import QRAB.QRAB.user.repository.UserRepository;
import QRAB.QRAB.note.config.S3Config;
import QRAB.QRAB.note.domain.Note;
import QRAB.QRAB.note.dto.FileRequestDTO;
import QRAB.QRAB.note.repository.NoteRepository;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;

//pdf + 이미지 파일의 경우는 로컬에서 파일 가져오는 방식 사용함
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FileCrawlerService {
    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    private final CategoryRepository categoryRepository;
    private final S3Config s3Config;

    private final OCRService ocrService;
    private final ChatgptService chatgptService;

    @Transactional(readOnly = false)
    public ResponseEntity<?> crawlPDFAndSave(FileRequestDTO fileRequestDTO) throws IOException{
        User user = userRepository.findOneWithAuthoritiesByUsername(fileRequestDTO.getEmail())
                .orElseThrow(() -> new NotFoundMemberException("Could not find user with email: " + fileRequestDTO.getEmail()));
        Category category = categoryRepository.findById(fileRequestDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Could not find category"));
        Note note = fileRequestDTO.toEntity(user, category);
        try {
            if(fileRequestDTO.getFile()!= null){
                String filePath = s3Config.upload(fileRequestDTO.getFile());// S3 버킷에 파일을 업르드하고 파일의 공개 url 반환
                note.setFile(filePath); //파일 경로 저장
                //S3 클라이언트에서 getObject 메서드를 호출해서 S3버킷에서 PDF 파일을 가져옴
                S3Object s3Object = s3Config.getS3Client().getObject(s3Config.getBucket(), fileRequestDTO.getFile().getOriginalFilename());
                //S3에서 가져온 PDF 파일을 InputStream 으로 변환
                try(InputStream inputStream = s3Object.getObjectContent()){
                    PDDocument document = PDDocument.load(inputStream); //PDF 파일을 읽어서 PDDDocument 객체로 로드
                    PDFTextStripper pdfTextStripper = new PDFTextStripper(); // PDF 페이지를 순회하며 텍스트를 추출하는 역할
                    String text = pdfTextStripper.getText(document); // PDFTextStripper 객체의 getText 메서드를 사용하여 PDDocument 객체에서 텍스트를 추출
                    note.setContent(text); //추출된 text 를 content 필드에 저장

                    //제목을 추출
                    String title = fileRequestDTO.getFile().getOriginalFilename();
                    int dotIdx = title.lastIndexOf('.'); // abc.pdf 에서 abc만 가져오기 위해
                    if(dotIdx > 0){
                        title = title.substring(0, dotIdx);
                    }
                    note.setTitle(title); // 파일 제목 저장
                    document.close();//문서를 닫기
                }
            }
            String chatgptContent = chatgptService.getSummary(note.getContent());
            System.out.println(chatgptContent);
            note.setChatgptContent(chatgptContent);

            Note savedNote = noteRepository.save(note); // note 객체를 db에 저장
            System.out.println(savedNote.getContent());
            System.out.println(savedNote.getTitle());
            System.out.println(savedNote.getFile());
            System.out.println(savedNote.getChatgptContent());

            return ResponseEntity.ok(fileRequestDTO);
        }catch (IOException e){
            return new ResponseEntity<>("Failed to extract text from PDF", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //naver clover ocr api 를 사용해서 이미지를 텍스트로 변환
    @Transactional(readOnly = false)
    public ResponseEntity<?> crawlImageAndSave(FileRequestDTO fileRequestDTO) throws IOException {
        User user = userRepository.findOneWithAuthoritiesByUsername(fileRequestDTO.getEmail())
                .orElseThrow(() -> new NotFoundMemberException("Could not find user with email: " + fileRequestDTO.getEmail()));
        Category category = categoryRepository.findById(fileRequestDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Could not find category"));
        Note note = fileRequestDTO.toEntity(user, category);
        try{
            if(fileRequestDTO.getFile() != null){
                //이미지 url 생성
                String imageUrl = s3Config.upload(fileRequestDTO.getFile());
                note.setFile(imageUrl);
                // String imageUrl = s3Config.getS3Client().getObject(s3Config.getBucket(), fileRequestDTO.getFile().getOriginalFilename()).toString();// S3Object [key=무야.jpg,bucket=qrab]
                //OCR API 호출
                String extractedText = ocrService.processImage(imageUrl);
                note.setContent(extractedText);
                String totalFileName = fileRequestDTO.getFile().getOriginalFilename(); //사진.png
                int idx = 0;
                for(int i = 0; i < totalFileName.length(); i++){
                    if(totalFileName.charAt(i) == '.'){
                        idx = i;
                    }
                }
                note.setTitle(totalFileName.substring(0, idx));
            }
            String chatgptContent = chatgptService.getSummary(note.getContent());
            System.out.println(chatgptContent);
            note.setChatgptContent(chatgptContent);

            Note savedNote = noteRepository.save(note); // note 객체를 db에 저장
            System.out.println(savedNote.getContent());
            System.out.println(savedNote.getTitle());
            System.out.println(savedNote.getFile());
            System.out.println(savedNote.getChatgptContent());

            return ResponseEntity.ok(fileRequestDTO);
        }catch (IOException e) {
            return new ResponseEntity<>("Failed to extract text from IMAGE", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

}