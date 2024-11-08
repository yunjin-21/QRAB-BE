package QRAB.QRAB.note.controller;

import QRAB.QRAB.category.dto.CategoryChildResponseDTO;
import QRAB.QRAB.category.dto.CategoryParentResponseDTO;
import QRAB.QRAB.category.service.CategoryService;
import QRAB.QRAB.note.dto.*;
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

    @PostMapping("/{noteId}/view")
    public ResponseEntity<?> viewNote(@PathVariable("noteId") Long noteId, ViewRequestDTO viewRequestDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        viewRequestDTO.setEmail(authentication.getName());
        return noteService.viewNote(noteId, viewRequestDTO);
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

        // 최근 노트 3개 조회
        List<RecentNoteDTO> threeNoteInfo = noteService.getUserRecentNotesBy3(username);
        long totalNotesCount = noteService.getUserTotalNotesCount(username);

        Map<String, Object> result = new HashMap<>();
        result.put("parentCategories", parentCategories);
        result.put("childCategories", childCategories);
        result.put("sixNotesInfo", sixNotesInfo);
        result.put("threeNoteInfo", threeNoteInfo);
        result.put("totalNotesCount", totalNotesCount);

        return ResponseEntity.ok(result);

    }

    private ResponseEntity<?> getNotesByCategoryResponse(Long categoryId, int page) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // 상위 카테고리 조회
        List<CategoryParentResponseDTO> parentCategories = categoryService.getUserParentCategories(username);

        // 하위 카테고리 조회
        List<CategoryChildResponseDTO> childCategories = categoryService.getUserChildCategories(username);

        // 카테고리 ID가 제공된 경우 해당 카테고리의 노트 조회, 그렇지 않으면 최신 노트 조회
        List<NoteResponseDTO> sixNotesInfo = noteService.getNotesByCategory(username, categoryId, page);

        // 최근 노트 3개 조회
        List<RecentNoteDTO> threeNoteInfo = noteService.getUserRecentNotesBy3(username);

        Map<String, Object> result = new HashMap<>();
        result.put("parentCategories", parentCategories);
        result.put("childCategories", childCategories);
        result.put("sixNotesInfo", sixNotesInfo);
        result.put("threeNoteInfo", threeNoteInfo);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<?> getNotePageByCategory(@PathVariable("categoryId") Long categoryId,
                                                   @RequestParam(name = "page", defaultValue = "0") int page) {
        return getNotesByCategoryResponse(categoryId, page);
    }

}
