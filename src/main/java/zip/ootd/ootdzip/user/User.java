package zip.ootd.ootdzip.user;

import jakarta.persistence.*;
import lombok.*;
import zip.ootd.ootdzip.BaseEntity;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

    private String name;

    private UserGender gender = UserGender.UNKNOWN;

    private LocalDate birthdate;

    private Integer height;

    private Integer weight;

    private Boolean isOpenBody;

    @Column(length = 2048)
    private String profileImage;

    private Boolean isDeleted = false;
}
