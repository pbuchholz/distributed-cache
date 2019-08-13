package distributedcache.configuration.boundary;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to describe which ValueDeserializer should be used.
 * 
 * @author Philipp Buchholz
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValueDeserializer {

	String value() default "org.apache.kafka.common.serialization.StringDeserializer";

}
