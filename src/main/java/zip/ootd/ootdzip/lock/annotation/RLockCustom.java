package zip.ootd.ootdzip.lock.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import zip.ootd.ootdzip.lock.domain.RLockType;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RLockCustom {

    RLockType type();

    String key() default "";

    String[] keys() default {};
}
