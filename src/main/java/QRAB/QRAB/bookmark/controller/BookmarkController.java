package QRAB.QRAB.bookmark.controller;

import QRAB.QRAB.bookmark.dto.BookmarkRequestDTO;
import QRAB.QRAB.bookmark.dto.BookmarkResponseDTO;
import QRAB.QRAB.bookmark.dto.BookmarkedNoteResponseDTO;
import QRAB.QRAB.bookmark.dto.BookmarkedQuizResponseDTO;
import QRAB.QRAB.bookmark.service.BookmarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @Autowired
    public BookmarkController(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    // 북마크 생성
    @PostMapping
    public ResponseEntity<BookmarkResponseDTO> createBookmark(@RequestBody BookmarkRequestDTO requestDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        BookmarkResponseDTO responseDTO = bookmarkService.createBookmark(requestDTO, username);
        return ResponseEntity.ok(responseDTO);
    }

    // 북마크 삭제
    @DeleteMapping("/{bookmarkId}")
    public ResponseEntity<String> deleteBookmark(@PathVariable("bookmarkId") Long bookmarkId) {
        bookmarkService.deleteBookmark(bookmarkId);
        return ResponseEntity.ok("북마크가 삭제되었습니다.");
    }

    // 북마크 노트 조회
    @GetMapping("/notes")
    public ResponseEntity<Map<String, Object>> getBookmarkedNotes(@RequestParam(name = "page", defaultValue = "0") int page) {
        Page<BookmarkedNoteResponseDTO> bookmarkedNotesPage = bookmarkService.getBookmarkedNotes(page);
        Map<String, Object> response = new HashMap<>();
        response.put("bookmarkedNotes", bookmarkedNotesPage.getContent());
        return ResponseEntity.ok(response);
    }

    // 특정 노트 북마크 조회
    @GetMapping("/notes/{noteId}/quizzes")
    public ResponseEntity<BookmarkedQuizResponseDTO> getBookmarkedQuizzes(@PathVariable("noteId") Long noteId) {
        BookmarkedQuizResponseDTO response = bookmarkService.getBookmarkedQuizzes(noteId);
        return ResponseEntity.ok(response);
    }
}
