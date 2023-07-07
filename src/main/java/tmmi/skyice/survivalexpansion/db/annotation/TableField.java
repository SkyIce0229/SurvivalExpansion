package tmmi.skyice.survivalexpansion.db.annotation;

import tmmi.skyice.survivalexpansion.db.table.typehandler.TypeHandle;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TableField {
    String value() default "";

    Class<? extends TypeHandle<?>> typeHandle();
}
