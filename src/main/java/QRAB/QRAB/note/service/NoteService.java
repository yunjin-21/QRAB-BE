package QRAB.QRAB.note.service;

import QRAB.QRAB.category.domain.Category;
import QRAB.QRAB.category.repository.CategoryRepository;
import QRAB.QRAB.note.domain.Note;
import QRAB.QRAB.note.dto.NoteResponseDTO;
import QRAB.QRAB.note.dto.RecentNoteDTO;
import QRAB.QRAB.note.dto.SummaryResponseDTO;
import QRAB.QRAB.note.repository.NoteRepository;
import QRAB.QRAB.user.domain.User;
import QRAB.QRAB.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        Pageable pageable = PageRequest.of(page, 6, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Note> notes = noteRepository.findByCategoryAndUser(category, user, pageable);

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
}
