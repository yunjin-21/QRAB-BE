package QRAB.QRAB.note.controller;

import QRAB.QRAB.category.dto.CategoryChildResponseDTO;
import QRAB.QRAB.category.dto.CategoryParentResponseDTO;
import QRAB.QRAB.category.service.CategoryService;
import QRAB.QRAB.note.dto.BlogRequestDTO;
import QRAB.QRAB.note.dto.FileRequestDTO;
import QRAB.QRAB.note.dto.NoteResponseDTO;
import QRAB.QRAB.note.service.NoteService;
import QRAB.QRAB.note.service.UrlCrawlerService;
import QRAB.QRAB.note.service.FileCrawlerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/notes")
@RestController //http 요청을 처리하고 json 형식으로 데이터를 반환 - restful web service  @controller와 @responseBody를 함께 사용
@RequiredArgsConstructor // final 필드 + @NonNull 애노테이션이 붙은 필드에 대한 생성자를 자동으로 생성
public class NoteController {
    private final UrlCrawlerService urlCrawlerService;
    private final FileCrawlerService fileCrawlerService;
    private final NoteService noteService;

    private final CategoryService categoryService;

    @PostMapping("/crawl/url")
    public ResponseEntity<?> crawlNote(BlogRequestDTO blogRequestDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        blogRequestDTO.setEmail(authentication.getName());
        try{
            if(blogRequestDTO.getUrl().contains("velog") || blogRequestDTO.getUrl().contains("tistory")){
                return urlCrawlerService.crawlBlogAndSave(blogRequestDTO);
            }else{
                return urlCrawlerService.capturePageAndSave(blogRequestDTO);
            }
        }catch (IOException e) {
            return new ResponseEntity<>("Failed to crawl and save note", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/crawl/file")
    public ResponseEntity<?> crawlFile(FileRequestDTO fileRequestDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        fileRequestDTO.setEmail(authentication.getName());
        try{
            if(fileRequestDTO.getFile().getOriginalFilename().contains("pdf")){
                return fileCrawlerService.crawlPDFAndSave(fileRequestDTO);
            }else{
                return fileCrawlerService.crawlImageAndSave(fileRequestDTO);
            }

        }catch (IOException e){
            return new ResponseEntity<>("Failed to extract text from PDF or Image FILE", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{noteId}/summary")
    public ResponseEntity<?> getNoteSummary(@PathVariable("noteId") Long noteId){
        return ResponseEntity.ok(noteService.getNoteSummary(noteId));
    }

    @GetMapping
    public ResponseEntity<?> getNotePage(@RequestParam(name = "page", defaultValue = "0") int page){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        // 사용자가 생성한 상위 카테고리 조회
        List<CategoryParentResponseDTO> parentCategories = categoryService.getUserParentCategories(username);

        // 하위 카테고리 조회
        List<CategoryChildResponseDTO> childCategories = categoryService.getUserChildCategories(username);

        // 노트의 제목, 요약본 (10자), 카테고리 조회
        List<NoteResponseDTO> sixNotesInfo = noteService.getUserRecentNotes(username, page);

        Map<String, Object> result = new HashMap<>();
        result.put("parentCategories", parentCategories);
        result.put("childCategories", childCategories);
        result.put("sixNotesInfo", sixNotesInfo);

        return ResponseEntity.ok(result);

    }
}
