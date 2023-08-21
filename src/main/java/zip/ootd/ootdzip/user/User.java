package zip.ootd.ootdzip.user;

import jakarta.persistence.*;
import lombok.*;
import zip.ootd.ootdzip.common.entity.BaseEntity;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private UserGender gender = UserGender.UNKNOWN;

    @Column(nullable = false)
    private LocalDate birthdate;

    private Integer height;

    private Integer weight;

    private Boolean isOpenBody;

    @Column(length = 2048)
    private String profileImage;

    @Column(nullable = false)
    private Boolean isDeleted = false;
}
