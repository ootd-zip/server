package zip.ootd.ootdzip.clothes;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.BaseEntity;
import zip.ootd.ootdzip.user.User;

@Entity
@Table(name = "Clothes")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Clothes extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
