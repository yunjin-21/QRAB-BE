package QRAB.QRAB.category.domain;

import QRAB.QRAB.BaseTimeEntity;
import QRAB.QRAB.user.domain.User;
import QRAB.QRAB.note.domain.Note;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor //기본 생성자를 자동 생성
@Table(name = "Category")
public class Category extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//기본 키 값을 자동으로 생성
    @Column(name = "category_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "parent_id")
    @JsonIgnore
    private Category parentCategory; //부모 카테고리를 나타냄

    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Category> children = new ArrayList<>();


    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="user_id")
    private User user;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Note> notes = new ArrayList<>();

    @Builder
    public Category(String name, User user, List<Note> notes){
        this.name = name;
        this.user = user;
        this.notes = notes != null ? notes : new ArrayList<>();
    }
}
