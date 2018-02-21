package cash.xcl.server.exch;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(value = {ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface CouldBeNaN {

}
