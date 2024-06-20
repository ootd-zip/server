package zip.ootd.ootdzip;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.CaseFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class DBCleanUp implements InitializingBean {
    @PersistenceContext
    private EntityManager entityManager;

    private Set<String> tableNames;

    @Override
    public void afterPropertiesSet() {
        tableNames = entityManager.getMetamodel().getEntities().stream()
                .filter(e -> e.getJavaType().getAnnotation(Entity.class) != null)
                .map(e -> CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, e.getName()) + "s")
                .collect(Collectors.toSet());

        // 일반적인 테이블네임과 다른 경우 수정이 필요 합니다
        tableNames.remove("ootd_image_clothess");
        tableNames.remove("ootd_bookmarks");
        tableNames.remove("ootd_likes");
        tableNames.remove("ootd_styles");
        tableNames.remove("user_blocks");
        tableNames.remove("user_styles");
        tableNames.remove("clothes_styles");
        tableNames.remove("clothes_colors");
        tableNames.remove("clothess");
        tableNames.remove("report_clothess");
        tableNames.remove("categorys");

        tableNames.add("ootd_image_clothes");
        tableNames.add("ootd_bookmark");
        tableNames.add("ootd_like");
        tableNames.add("ootd_style");
        tableNames.add("user_block");
        tableNames.add("user_style");
        tableNames.add("clothes_styles_map");
        tableNames.add("clothes_colors_map");
        tableNames.add("clothes");
        tableNames.add("report_clothes");
        tableNames.add("categories");
    }

    @Transactional
    public void execute() {
        entityManager.flush();
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

        for (String tableName : tableNames) {
            entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE " + tableName + " ALTER COLUMN ID RESTART WITH 1")
                    .executeUpdate();
        }

        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }
}
