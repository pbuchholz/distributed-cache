package distributedcache.notification;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class NotificationRegistry {

	private Set<UUID> processedNotificationIdentifiers = new HashSet<>();

	public void register(Notification notification) {
		this.processedNotificationIdentifiers.add(notification.identifier());
	}

	public boolean alreadyProcessed(Notification notification) {
		return this.processedNotificationIdentifiers.contains(notification.identifier());
	}

}
