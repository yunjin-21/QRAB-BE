package QRAB.QRAB.bookmark.controller;

import QRAB.QRAB.bookmark.dto.BookmarkRequestDTO;
import QRAB.QRAB.bookmark.dto.BookmarkResponseDTO;
import QRAB.QRAB.bookmark.service.BookmarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<String> deleteBookmark(@PathVariable Long bookmarkId) {
        bookmarkService.deleteBookmark(bookmarkId); // 서비스 호출
        return ResponseEntity.ok("북마크가 삭제되었습니다.");
    }
}
