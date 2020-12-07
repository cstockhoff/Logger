package logging;

import java.lang.annotation.*;

/**
 * @author cstockhoff
 */
public class LoggingAnnotation {

	@Inherited
	@Target( { ElementType.TYPE, ElementType.METHOD } )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface SkipLogging {
		boolean value() default true;
	}

}
