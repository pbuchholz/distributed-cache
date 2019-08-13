package distributedcache.configuration.boundary;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to describe to which Kafka Consumer Group a Kafka Consumer should
 * subscribe to.
 * 
 * @author Philipp Buchholz
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConsumerGroup {

	String value();

}
