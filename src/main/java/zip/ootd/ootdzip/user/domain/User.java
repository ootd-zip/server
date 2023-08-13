package zip.ootd.ootdzip.user.domain;

import jakarta.persistence.*;
import lombok.*;
import zip.ootd.ootdzip.BaseEntity;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

    @Column(unique = true)
    private String name;

    @Enumerated(EnumType.ORDINAL)
    private UserGender gender = UserGender.UNKNOWN;

    private LocalDate birthdate;

    private Integer height;

    private Boolean showHeight;

    private Integer weight;

    private Boolean showWeight;

    @Column(length = 2048)
    private String profileImage;

    @Column(nullable = false)
    private Boolean isCompleted = false;

    @Column(nullable = false)
    private Boolean isDeleted = false;

    public static User getDefault() {
        return new User(
                null,
                null,
                null,
                0,
                false,
                0,
                false,
                null,
                false,
                false);
    }
}
