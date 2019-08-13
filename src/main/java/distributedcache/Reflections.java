package distributedcache;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;

import javax.enterprise.inject.spi.Annotated;

/**
 * Contains several methods to work with reflection.
 * 
 * @author Philipp Buchholz
 */
public final class Reflections {

	private Reflections() {

	}

	/**
	 * Reads the value from an {@link Annotation} defined on an {@link Annotated}.
	 * 
	 * @param <A>
	 * @param annotated
	 * @param annotationType
	 * @param annotationInstance
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public static <A extends Annotation> String fromAnnotated(Annotated annotated, Class<A> annotationType,
			Annotation annotationInstance) throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		return (String) annotated //
				.getAnnotation(annotationType) //
				.getClass() //
				.getMethod("value") //
				.invoke(annotationInstance);
	}

}
