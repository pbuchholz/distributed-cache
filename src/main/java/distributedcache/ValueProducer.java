package distributedcache;

import static distributedcache.Reflections.valueFromAnnotated;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

/**
 * Producer which injects values into properties annotated with {@link Value}.
 * 
 * @author Philipp Buchholz
 */
@ApplicationScoped
public class ValueProducer {

	public static final String APPLICATION_PROPERTIES = "/application.properties";

	private Properties properties;

	@PostConstruct
	public void readProperties() {
		properties = new Properties();
		try (InputStream is = ValueProducer.class.getResourceAsStream(APPLICATION_PROPERTIES)) {
			properties.load(is);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Value
	@Produces
	public <T> Optional<T> produceValue(InjectionPoint injectionPoint) {
		/* Preconditions */
		assert null != injectionPoint;
		assert null != this.properties;
		assert injectionPoint.getAnnotated().isAnnotationPresent(Property.class);

		Class<?> propertyType = this.readPropertyType(injectionPoint);
		String propertyName = this.readPropertyName(injectionPoint);
		String propertyValue = this.properties.getProperty(propertyName);

		if (Objects.isNull(propertyValue)) {
			throw new IllegalArgumentException(
					String.format("Property %s has not been configured correctly.", propertyName));
		}

		return (Optional<T>) Optional.of(propertyType.cast(propertyValue));
	}

	/**
	 * Reads the name of the property which should be injected.
	 * 
	 * @param injectionPoint
	 * @return
	 */
	private String readPropertyName(InjectionPoint injectionPoint) {
		try {
			return valueFromAnnotated(injectionPoint.getAnnotated(), Property.class,
					injectionPoint.getAnnotated().getAnnotation(Property.class));
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Reads thte type of the property which should be injected.
	 * 
	 * @param injectionPoint
	 * @return
	 */
	private Class<?> readPropertyType(InjectionPoint injectionPoint) {
		try {
			return Reflections.typeFromAnnotated(injectionPoint.getAnnotated(),
					injectionPoint.getAnnotated().getAnnotation(Property.class));
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			throw new RuntimeException(e);
		}
	}

}
