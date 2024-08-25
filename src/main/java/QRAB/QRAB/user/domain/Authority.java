package QRAB.QRAB.user.domain;


import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "authority")
@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Authority {

    @Id
    @Column(name = "authority_name", length = 50)
    private String authorityName;

    /*@ManyToMany(mappedBy = "authorities")
    private Set<User> users;*/
}