package zip.ootd.ootdzip.clothes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import zip.ootd.ootdzip.clothes.domain.Clothes;
import zip.ootd.ootdzip.user.domain.User;

@Repository
public interface ClothesRepository extends JpaRepository<Clothes, Long> {

    List<Clothes> findByUser(User user);

    List<Clothes> findByUserAndIsOpenTrue(User user);
}
