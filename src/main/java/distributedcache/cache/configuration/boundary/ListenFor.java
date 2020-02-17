package distributedcache.cache.configuration.boundary;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks to which Notifications a Listener is listening for.
 * 
 * @author Philipp Buchholz
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER, ElementType.FIELD })
public @interface ListenFor {

	NotificationType value();

	public enum NotificationType {
		PUT_NOTIFICATION
	}

}
