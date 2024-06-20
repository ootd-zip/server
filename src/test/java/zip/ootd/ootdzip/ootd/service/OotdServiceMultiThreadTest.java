package zip.ootd.ootdzip.ootd.service;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.persistence.EntityManager;
import zip.ootd.ootdzip.DBCleanUp;
import zip.ootd.ootdzip.brand.domain.Brand;
import zip.ootd.ootdzip.brand.repository.BrandRepository;
import zip.ootd.ootdzip.category.data.SizeType;
import zip.ootd.ootdzip.category.domain.Category;
import zip.ootd.ootdzip.category.domain.Color;
import zip.ootd.ootdzip.category.domain.Size;
import zip.ootd.ootdzip.category.domain.Style;
import zip.ootd.ootdzip.category.repository.CategoryRepository;
import zip.ootd.ootdzip.category.repository.ColorRepository;
import zip.ootd.ootdzip.category.repository.SizeRepository;
import zip.ootd.ootdzip.category.repository.StyleRepository;
import zip.ootd.ootdzip.clothes.data.PurchaseStoreType;
import zip.ootd.ootdzip.clothes.domain.Clothes;
import zip.ootd.ootdzip.clothes.domain.ClothesColor;
import zip.ootd.ootdzip.clothes.repository.ClothesRepository;
import zip.ootd.ootdzip.common.dao.RedisDao;
import zip.ootd.ootdzip.images.domain.Images;
import zip.ootd.ootdzip.ootd.domain.Ootd;
import zip.ootd.ootdzip.ootd.repository.OotdRepository;
import zip.ootd.ootdzip.ootdimage.domain.OotdImage;
import zip.ootd.ootdzip.ootdimageclothe.domain.Coordinate;
import zip.ootd.ootdzip.ootdimageclothe.domain.DeviceSize;
import zip.ootd.ootdzip.ootdimageclothe.domain.OotdImageClothes;
import zip.ootd.ootdzip.ootdstyle.domain.OotdStyle;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.repository.UserRepository;

/**
 * SpringBootTest 에서 @Transactional 사용시 끝날때 롤백하는 특징을 가집니다.
 * 메인쓰레드에서 쿼리를 날려도 실제 DB 에 반영되지 않고, 엔티티매니저가 1차캐시에 내용을 보관한뒤 롤백후 파기합니다.
 * 1차캐시에 내용이 저장되어있기에 단일쓰레드에서는 정상적으로 로직이 수행됩니다.
 * 하지만 멀티스레드에서는, 새로운 트랜잭션을 생성하고, 이에따라 새로운 엔티티매니저가 생깁니다. 당연히 서로간에 1차캐시 내용은 공유되지 않습니다.
 * 그렇기에 다른 스레드에서는 1차캐시에 저장된게 없으므로, DB 를 직접조회하지만 실제 DB 에도 커밋된건 없으므로 조회에 실패합니다.
 * 그래서 Transcational 은 멀티스레드 환경에서 사용할 수 없습니다.
 */
@SpringBootTest
public class OotdServiceMultiThreadTest {

    @Autowired
    private OotdService ootdService;

    @Autowired
    private StyleRepository styleRepository;

    @Autowired
    private ClothesRepository clothesRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SizeRepository sizeRepository;

    @Autowired
    private ColorRepository colorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OotdRepository ootdRepository;

    @Autowired
    private RedisDao redisDao;

    @Autowired
    private DBCleanUp dbCleanUp;

    @AfterEach
    void tearDown() {
        dbCleanUp.execute();
        redisDao.deleteAll();
    }

    @Autowired
    private EntityManager entityManager;

    private static final String OOTD_IMAGE_URL = "https://ootdzip.com/8c00f7f4-3f47-4238-2024-06-14.png";

    @DisplayName("ootd 조회수의 동시성이 보장된다.")
    @Test
    public void getConcurrencyViewCount() throws InterruptedException {

        // given
        User user = createUserBy("유저");
        User user1 = createUserBy("유저1");
        User user2 = createUserBy("유저2");
        User user3 = createUserBy("유저3");
        User user4 = createUserBy("유저4");
        Ootd ootd = createOotdBy(user, "안녕", false);

        int numberOfThreads = 6;
        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        // when
        service.execute(() -> {
            ootdService.increaseViewCount(ootd.getId(), user);
            latch.countDown();
        });
        service.execute(() -> { // 중복 유저는 조회수 카운트 X
            ootdService.increaseViewCount(ootd.getId(), user);
            latch.countDown();
        });
        service.execute(() -> {
            ootdService.increaseViewCount(ootd.getId(), user1);
            latch.countDown();
        });
        service.execute(() -> {
            ootdService.increaseViewCount(ootd.getId(), user2);
            latch.countDown();
        });
        service.execute(() -> {
            ootdService.increaseViewCount(ootd.getId(), user3);
            latch.countDown();
        });
        service.execute(() -> {
            ootdService.increaseViewCount(ootd.getId(), user4);
            latch.countDown();
        });

        latch.await();

        // then
        Thread.sleep(500L); // 비동기 처리 대기시간
        Ootd result = ootdRepository.findById(ootd.getId()).orElseThrow();
        assertThat(result.getViewCount()).isEqualTo(5);
    }

    @DisplayName("ootd 좋아요 수 증가에 대한 동시성이 보장된다.")
    @Test
    public void increaseConcurrencyLikeCount() throws InterruptedException {

        // given
        User user = createUserBy("유저");
        Ootd ootd = createOotdBy(user, "안녕", false);

        int numberOfThreads = 100;
        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        // 좋아요 증가
        // when
        Runnable task = () -> {
            try {
                ootdService.increaseLikeCount(ootd.getId());
            } finally {
                latch.countDown();
            }
        };

        // 모든 작업을 ExecutorService로 제출
        for (int i = 0; i < numberOfThreads; i++) {
            service.execute(task);
        }

        latch.await();

        // then
        Ootd result = ootdRepository.findById(ootd.getId()).orElseThrow();
        assertThat(result.getLikeCount()).isEqualTo(100);
    }

    @DisplayName("ootd 좋아요 수 감소에 대한 동시성이 보장된다.")
    @Test
    public void decreaseConcurrencyLikeCount() throws InterruptedException {

        // given
        User user = createUserBy("유저");
        Ootd ootd = createOotdBy(user, "안녕", false, 150, 150);

        int numberOfThreads = 100;
        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        // 좋아요 감소
        // when
        Runnable task = () -> {
            try {
                ootdService.decreaseLikeCount(ootd.getId());
            } finally {
                latch.countDown();
            }
        };

        // 모든 작업을 ExecutorService로 제출
        for (int i = 0; i < numberOfThreads; i++) {
            service.execute(task);
        }

        latch.await();

        // then
        Ootd result = ootdRepository.findById(ootd.getId()).orElseThrow();
        assertThat(result.getLikeCount()).isEqualTo(50);
    }

    @DisplayName("ootd 북마크 수 증가에 대한 동시성이 보장된다.")
    @Test
    public void increaseConcurrencyBookmarkCount() throws InterruptedException {

        // given
        User user = createUserBy("유저");
        Ootd ootd = createOotdBy(user, "안녕", false);

        int numberOfThreads = 100;
        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        // when
        Runnable task = () -> {
            try {
                ootdService.increaseBookmarkCount(ootd.getId());
            } finally {
                latch.countDown();
            }
        };

        // 모든 작업을 ExecutorService로 제출
        for (int i = 0; i < numberOfThreads; i++) {
            service.execute(task);
        }

        latch.await();

        // then
        Ootd result = ootdRepository.findById(ootd.getId()).orElseThrow();
        assertThat(result.getBookmarkCount()).isEqualTo(100);
    }

    @DisplayName("ootd 북마크 수 감소에 대한 동시성이 보장된다.")
    @Test
    public void decreaseConcurrencyBookmarkCount() throws InterruptedException {

        // given
        User user = createUserBy("유저");
        Ootd ootd = createOotdBy(user, "안녕", false, 150, 150);

        int numberOfThreads = 100;
        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        // when
        Runnable task = () -> {
            try {
                ootdService.decreaseBookmarkCount(ootd.getId());
            } finally {
                latch.countDown();
            }
        };

        // 모든 작업을 ExecutorService로 제출
        for (int i = 0; i < numberOfThreads; i++) {
            service.execute(task);
        }

        latch.await();

        // then
        Ootd result = ootdRepository.findById(ootd.getId()).orElseThrow();
        assertThat(result.getBookmarkCount()).isEqualTo(50);
    }

    private Ootd createOotdBy(User user, String content, boolean isPrivate, int likeCount, int bookmarkCount) {

        Clothes clothes = createClothesBy(user, true, "1");
        Clothes clothes1 = createClothesBy(user, true, "2");

        Coordinate coordinate = new Coordinate("22.33", "33.44");
        Coordinate coordinate1 = new Coordinate("33.44", "44.55");

        DeviceSize deviceSize = new DeviceSize(100L, 50L);
        DeviceSize deviceSize1 = new DeviceSize(100L, 50L);

        OotdImageClothes ootdImageClothes = OotdImageClothes.builder().clothes(clothes)
                .coordinate(coordinate)
                .deviceSize(deviceSize)
                .build();

        OotdImageClothes ootdImageClothes1 = OotdImageClothes.builder().clothes(clothes1)
                .coordinate(coordinate1)
                .deviceSize(deviceSize1)
                .build();

        OotdImage ootdImage = OotdImage.createOotdImageBy(Images.of(OOTD_IMAGE_URL),
                Arrays.asList(ootdImageClothes, ootdImageClothes1));

        Style style = Style.builder().name("올드머니").build();
        styleRepository.save(style);
        Style style1 = Style.builder().name("블루코어").build();
        styleRepository.save(style1);

        OotdStyle ootdStyle = OotdStyle.createOotdStyleBy(style);
        OotdStyle ootdStyle1 = OotdStyle.createOotdStyleBy(style1);

        Ootd ootd = Ootd.createOotd(user,
                content,
                isPrivate,
                Arrays.asList(ootdImage),
                Arrays.asList(ootdStyle, ootdStyle1));

        ootd.setLikeCount(likeCount);
        ootd.setBookmarkCount(bookmarkCount);

        return ootdRepository.save(ootd);
    }

    private Ootd createOotdBy(User user, String content, boolean isPrivate) {

        Clothes clothes = createClothesBy(user, true, "1");
        Clothes clothes1 = createClothesBy(user, true, "2");

        Coordinate coordinate = new Coordinate("22.33", "33.44");
        Coordinate coordinate1 = new Coordinate("33.44", "44.55");

        DeviceSize deviceSize = new DeviceSize(100L, 50L);
        DeviceSize deviceSize1 = new DeviceSize(100L, 50L);

        OotdImageClothes ootdImageClothes = OotdImageClothes.builder().clothes(clothes)
                .coordinate(coordinate)
                .deviceSize(deviceSize)
                .build();

        OotdImageClothes ootdImageClothes1 = OotdImageClothes.builder().clothes(clothes1)
                .coordinate(coordinate1)
                .deviceSize(deviceSize1)
                .build();

        OotdImage ootdImage = OotdImage.createOotdImageBy(Images.of(OOTD_IMAGE_URL),
                Arrays.asList(ootdImageClothes, ootdImageClothes1));

        Style style = Style.builder().name("올드머니").build();
        styleRepository.save(style);
        Style style1 = Style.builder().name("블루코어").build();
        styleRepository.save(style1);

        OotdStyle ootdStyle = OotdStyle.createOotdStyleBy(style);
        OotdStyle ootdStyle1 = OotdStyle.createOotdStyleBy(style1);

        Ootd ootd = Ootd.createOotd(user,
                content,
                isPrivate,
                Arrays.asList(ootdImage),
                Arrays.asList(ootdStyle, ootdStyle1));

        return ootdRepository.save(ootd);
    }

    private Clothes createClothesBy(User user, boolean isOpen, String idx) {

        Brand brand = Brand.builder().name("브랜드" + idx).build();

        Brand savedBrand = brandRepository.save(brand);

        Category parentCategory = Category.createLargeCategoryBy("상위카테고리" + idx, SizeType.TOP);

        Category savedParentCategory = categoryRepository.save(parentCategory);

        Category category = Category.createDetailCategoryBy("카테고리" + idx, savedParentCategory, SizeType.TOP);

        Category savedCategory = categoryRepository.save(category);

        Size size = Size.builder().sizeType(SizeType.TOP).name("사이즈" + idx).lineNo((byte)1).build();

        Size savedSize = sizeRepository.save(size);

        Color color = Color.builder().name("색" + idx).colorCode("#fffff").build();

        Color savedColor = colorRepository.save(color);

        List<ClothesColor> clothesColors = ClothesColor.createClothesColorsBy(List.of(savedColor));

        Clothes clothes = Clothes.createClothes(user, savedBrand, "구매처" + idx, PurchaseStoreType.Write, "제품명" + idx,
                isOpen, savedCategory, savedSize, "메모입니다" + idx, "구매일" + idx,
                "https://ootdzip.com/8c00f7f4-3f47-4238-2024-06-15.png" + idx + ".jpg", clothesColors);

        return clothesRepository.save(clothes);
    }

    private User createUserBy(String userName) {
        User user = User.getDefault();
        user.setName(userName);
        return userRepository.save(user);
    }
}
