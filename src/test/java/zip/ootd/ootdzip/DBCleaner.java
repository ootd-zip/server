package zip.ootd.ootdzip;

import static org.springframework.util.StringUtils.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.Column;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Id;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Table;
import jakarta.persistence.metamodel.EntityType;

@Component
public class DBCleaner {

    @PersistenceContext
    private EntityManager entityManager;

    private List<Entity> entities;

    @PostConstruct
    public void loadEntities() {
        entities = entityManager.getMetamodel().getEntities().stream().map(this::toEntity).toList();
    }

    @Transactional
    public void execute() {
        entityManager.flush();
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();
        for (Entity entity : entities) {
            String truncate = "TRUNCATE TABLE " + entity.table();
            entityManager.createNativeQuery(truncate).executeUpdate();
            if (entity.id() != null) {
                String restart = "ALTER TABLE " + entity.table() + " ALTER COLUMN " + entity.id() + " RESTART WITH 1";
                entityManager.createNativeQuery(restart).executeUpdate();
            }
        }
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }

    private Entity toEntity(EntityType<?> type) {
        return new Entity(getTableName(type), getIdColumnName(type));
    }

    private String getTableName(EntityType<?> type) {
        Table table = type.getJavaType().getAnnotation(Table.class);
        if (table != null && hasText(table.name())) {
            return table.name();
        }
        return javaNameToSqlName(type.getName());
    }

    private String getIdColumnName(EntityType<?> type) {
        return findIdField(type).map(field -> {
            Column column = field.getAnnotation(Column.class);
            if (column != null && hasText(column.name())) {
                return column.name();
            }
            return javaNameToSqlName(field.getName());
        }).orElse(null);
    }

    private Optional<Field> findIdField(EntityType<?> type) {
        Class<?> javaType = type.getJavaType();
        while (javaType != null) {
            for (Field field : javaType.getDeclaredFields()) {
                if (field.isAnnotationPresent(Id.class)) {
                    return Optional.of(field);
                }
            }
            javaType = javaType.getSuperclass();
        }
        return Optional.empty();
    }

    private String javaNameToSqlName(String javaName) {
        if (containsConsecutiveUppers(javaName)) {
            return javaName;
        }
        return camelCaseToSnakeCase(javaName);
    }

    private boolean containsConsecutiveUppers(String string) {
        return string.matches(".*?[A-Z]{2,}.*?");
    }

    private String camelCaseToSnakeCase(String camelCase) {
        return camelCase.replaceAll("(?<!^)[A-Z]", "_$0");
    }

    private record Entity(String table, String id) {
    }
}
