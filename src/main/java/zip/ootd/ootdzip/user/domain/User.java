package zip.ootd.ootdzip.user.domain;

import jakarta.persistence.*;
import lombok.*;
import zip.ootd.ootdzip.common.entity.BaseEntity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "followings")
    private final Set<User> followers = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "followers",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "follower_id")})
    private final Set<User> followings = new HashSet<>();

    public boolean addFollower(User user) {
        return followers.add(user)
                && user.followings.add(this);
    }

    public boolean removeFollower(User user) {
        return followers.remove(user)
                && user.followings.remove(this);
    }

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
