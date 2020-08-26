import java.lang.annotation.*;

/**
 * @author cstockhoff
 */
public class LoggingAnnotation {

    @Inherited
    @Target( ElementType.TYPE )
    @Retention( RetentionPolicy.RUNTIME )
    public @interface SkipClassLogging {
        boolean value() default true;
    }

}
