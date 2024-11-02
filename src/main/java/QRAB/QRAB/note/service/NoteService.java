package QRAB.QRAB.note.service;

import QRAB.QRAB.category.domain.Category;
import QRAB.QRAB.category.repository.CategoryRepository;
import QRAB.QRAB.note.domain.Note;
import QRAB.QRAB.note.dto.*;
import QRAB.QRAB.note.repository.NoteRepository;
import QRAB.QRAB.user.domain.User;
import QRAB.QRAB.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NoteService {
    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    public final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public SummaryResponseDTO getNoteSummary(Long noteId){
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Could not find noteId"));

        return SummaryResponseDTO.fromEntity(note);
    }

    @Transactional(readOnly = false)
    public ResponseEntity<?> viewNote(Long noteId, ViewRequestDTO viewRequestDTO){
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Could not find noteId"));
        if(note.getRestrictedAccess() == 0){ //(0인 경우)공개면
            note.setRestrictedAccess(1); //1로 변경
        }else{ //비공개면
            note.setRestrictedAccess(0); //공개로 변경
        }
        Note savedNote = noteRepository.save(note); // note 객체를 db에 저장
        return ResponseEntity.ok(viewRequestDTO);
    }

    @Transactional(readOnly = true)
    public List<NoteResponseDTO> getUserRecentNotes(String username, int page){
        User user = userRepository.findOneWithAuthoritiesByUsername(username)
                .orElseThrow(()-> new RuntimeException("Could not find user with email"));
        Pageable pageable = PageRequest.of(page, 6, Sort.by(Sort.Direction.DESC, "createdAt")); //최신순으로 6개씩 조회
        Page<Note> notes = noteRepository.findByUser(user, pageable);

        List<NoteResponseDTO> noteResponseDTOs = notes.getContent().stream()
                .map(NoteResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return noteResponseDTOs;
    }

    @Transactional(readOnly = true)
    public List<NoteResponseDTO> getNotesByCategory(String username, Long categoryId, int page) {
        User user = userRepository.findOneWithAuthoritiesByUsername(username)
                .orElseThrow(() -> new RuntimeException("Could not find user with email"));

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Could not find category with ID: " + categoryId));

        List<Category> categories = new ArrayList<>();
        if(category.getParentCategory() == null){
            categories.add(category);
            categories.addAll(categoryRepository.findByParentCategory(category));
        }else{
            categories.add(category);
        }
        Pageable pageable = PageRequest.of(page, 6, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Note> notes = noteRepository.findByCategoriesAndUser(categories, user, pageable);

        return notes.getContent().stream()
                .map(NoteResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RecentNoteDTO> getUserRecentNotesBy3(String username){
        User user = userRepository.findOneWithAuthoritiesByUsername(username)
                .orElseThrow(()-> new RuntimeException("Could not find user with email"));

        List<Note> userNotes = noteRepository.findByUserOrderByCreatedAtDesc(user);

        List<RecentNoteDTO> recentNoteDTOS = userNotes.stream()
                .limit(3)
                .map(RecentNoteDTO::fromEntity)
                .collect(Collectors.toList());
        return recentNoteDTOS;
    }

    // 추가: 퀴즈 연구소에서 사용될 저장된 노트 조회 메소드

    @Transactional(readOnly = true)
    public List<QuizLabNoteResponseDTO> getStoredNotesForQuizLab(String username, int page) {
        // SecurityContext에서 현재 인증된 사용자 정보 가져오기
        User user = userRepository.findOneWithAuthoritiesByUsername(username)
                .orElseThrow(() -> new RuntimeException("Could not find user with email: " + username));
        Pageable pageable = PageRequest.of(page, 6, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Note> notes = noteRepository.findByUser(user, pageable);

        return notes.getContent().stream()
                .map(QuizLabNoteResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long getUserTotalNotesCount(String username) {
        User user = userRepository.findOneWithAuthoritiesByUsername(username)
                .orElseThrow(()-> new RuntimeException("Could not find user with email"));

        return noteRepository.countByUser(user);
    }



}
