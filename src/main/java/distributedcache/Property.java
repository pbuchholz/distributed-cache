package distributedcache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates the property to describe which property value should be injected.
 * 
 * @author Philipp Buchholz
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
public @interface Property {

	/**
	 * The name of the property which should be injected.
	 * 
	 * @return
	 */
	String value();

	/**
	 * Type information to perform correct casts at runtime.
	 * 
	 * @return
	 */
	Class<?> type() default String.class;

}
