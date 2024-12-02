package QRAB.QRAB.bookmark.service;

import QRAB.QRAB.bookmark.domain.Bookmark;
import QRAB.QRAB.bookmark.dto.BookmarkRequestDTO;
import QRAB.QRAB.bookmark.dto.BookmarkResponseDTO;
import QRAB.QRAB.bookmark.dto.BookmarkedNoteResponseDTO;
import QRAB.QRAB.bookmark.dto.BookmarkedQuizResponseDTO;
import QRAB.QRAB.bookmark.repository.BookmarkRepository;
import QRAB.QRAB.quiz.domain.Quiz;
import QRAB.QRAB.quiz.repository.QuizAnswerRepository;
import QRAB.QRAB.quiz.repository.QuizRepository;
import QRAB.QRAB.user.domain.User;
import QRAB.QRAB.user.repository.UserRepository;
import QRAB.QRAB.user.util.SecurityUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final QuizAnswerRepository quizAnswerRepository;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;

    @Autowired
    public BookmarkService(BookmarkRepository bookmarkRepository, QuizRepository quizRepository, QuizAnswerRepository quizAnswerRepository, UserRepository userRepository) {
        this.bookmarkRepository = bookmarkRepository;
        this.quizRepository = quizRepository;
        this.quizAnswerRepository = quizAnswerRepository;
        this.userRepository = userRepository;
    }

    // 북마크 생성
    public BookmarkResponseDTO createBookmark(BookmarkRequestDTO requestDTO, String username) {
        User user = userRepository.findOneWithAuthoritiesByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        Quiz quiz = quizAnswerRepository.findByQuizIdAndIsCorrectFalse(requestDTO.getQuizId())
                .orElseThrow(() -> new RuntimeException("Incorrect quiz not found for quizId: " + requestDTO.getQuizId()))
                .getQuiz();

        // 북마크 객체 생성
        Bookmark bookmark = new Bookmark();
        bookmark.setUser(user);
        bookmark.setQuiz(quiz);
        bookmark.setBookmarkedAt(LocalDateTime.now());

        // 북마크 저장
        Bookmark savedBookmark = bookmarkRepository.save(bookmark);

        // 응답 DTO 생성 및 반환
        BookmarkResponseDTO responseDTO = new BookmarkResponseDTO();
        responseDTO.setBookmarkId(savedBookmark.getBookmarkId());
        responseDTO.setQuizId(quiz.getQuizId());
        responseDTO.setUserId(user.getUserId());
        responseDTO.setBookmarkedAt(savedBookmark.getBookmarkedAt().toString());
        return responseDTO;
    }

    // 북마크 삭제
    public void deleteBookmark(Long bookmarkId) {
        // 북마크 존재 여부 확인
        boolean exists = bookmarkRepository.existsById(bookmarkId);
        if (!exists) {
            throw new EntityNotFoundException("Bookmark not found with ID: " + bookmarkId);
        }
        bookmarkRepository.deleteById(bookmarkId);
    }

    // 북마크가 있는 노트 조회
    public Page<BookmarkedNoteResponseDTO> getBookmarkedNotes(int page) {
        String username = SecurityUtil.getCurrentUsername()
                .orElseThrow(() -> new RuntimeException("현재 인증된 사용자가 없습니다."));
        User user = userRepository.findOneWithAuthoritiesByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        Pageable pageable = PageRequest.of(page, 6);
        return bookmarkRepository.findBookmarkedNotesWithCounts(user.getUserId(), pageable);
    }

    // 특정 노트 북마크 조회
    public BookmarkedQuizResponseDTO getBookmarkedQuizzes(Long noteId) {
        String username = SecurityUtil.getCurrentUsername()
                .orElseThrow(() -> new RuntimeException("현재 인증된 사용자가 없습니다."));

        User user = userRepository.findOneWithAuthoritiesByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        List<Object[]> results = bookmarkRepository.findBookmarkedQuizzesByNoteId(noteId, user.getUserId());

        if (results.isEmpty()) {
            return null;
        }

        BookmarkedQuizResponseDTO response = new BookmarkedQuizResponseDTO();
        response.setNoteId(noteId);
        response.setTitle((String) results.get(0)[1]);  // title은 첫 번째 결과에서 가져옴

        List<BookmarkedQuizResponseDTO.BookmarkedQuiz> quizzes = results.stream()
                .map(result -> new BookmarkedQuizResponseDTO.BookmarkedQuiz(
                        (Long) result[2],       // quizId
                        (Long) result[3],       // quizSetId
                        (String) result[4],     // question
                        (String) result[5],     // choices
                        (Integer) result[6],    // userAnswer
                        (Integer) result[7],    // correctAnswer
                        (LocalDateTime) result[8] // solvedAt
                ))
                .collect(Collectors.toList());

        response.setBookmarkedQuizzes(quizzes);
        return response;
    }
}
