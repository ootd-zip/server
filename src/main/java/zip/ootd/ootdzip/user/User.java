package zip.ootd.ootdzip.user;

import jakarta.persistence.*;
import lombok.*;
import zip.ootd.ootdzip.BaseEntity;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private UserGender gender = UserGender.UNKNOWN;

    @Column(nullable = false)
    private LocalDate birthdate;

    private Integer height;

    private Boolean showHeight;

    private Integer weight;

    private Boolean showWeight;

    @Column(length = 2048)
    private String profileImage;

    @Column(nullable = false)
    private Boolean isDeleted = false;
}
