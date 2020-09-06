package distributedcache.cache;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import distributedcache.cache.invalidation.InvalidationStrategy;

/**
 * Represents the configuration of a {@link Cache} instance.
 * 
 * @author Philipp Buchholz
 */
public class CacheConfiguration {

	private Class<? extends InvalidationStrategy<?, ?>> invalidationStrategy; // TODO Is this a good idea?
	private Map<Class<?>, Duration> validationTimespanPerType;
	private int validationPeriod;

	public void registerValidationTimespan(Class<?> type, Duration duration) {
		if (Objects.isNull(validationTimespanPerType)) {
			validationTimespanPerType = new HashMap<>();
		}

		validationTimespanPerType.put(type, duration);
	}

	public Duration validationTimespanForType(Class<?> type) {
		return validationTimespanPerType.get(type);
	}

	public int getValidationPeriod() {
		return this.validationPeriod;
	}

	public void setValidationPeriod(int validationPeriod) {
		this.validationPeriod = validationPeriod;
	}

	public Class<? extends InvalidationStrategy<?, ?>> getInvalidationStrategy() {
		return invalidationStrategy;
	}

	public void setInvalidationStrategy(Class<? extends InvalidationStrategy<?, ?>> invalidationStrategy) {
		this.invalidationStrategy = invalidationStrategy;
	}

}
