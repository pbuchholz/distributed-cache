package distributedcache.cache.configuration.boundary;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Qualifier to inject the configured Kafka consumer.
 * 
 * @author Philipp Buchholz
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface Consume {

	/**
	 * Type which should be consumed.
	 * 
	 * @return The type which is consumed.
	 */
	Class<?> type() default Object.class;

}
