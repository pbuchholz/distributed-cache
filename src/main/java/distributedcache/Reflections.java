package distributedcache;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
	 * Returns a {@link List} of {@link Method} which are marked as
	 * {@link Immutable}.
	 * 
	 * @param type
	 * @return
	 */
	public static List<Method> immutableMethods(Class<?> type) {
		return Stream.of(type.getMethods()) //
				.filter(m -> m.getAnnotation(Immutable.class) != null || //
						isAnnotatedInInterface(type.getInterfaces(), Immutable.class, m)) //
				.collect(Collectors.toList());
	}

	private static boolean isAnnotatedInInterface(Class<?>[] interfaces, Class<?> annotationType, Method method) {
		return Stream.of(interfaces) //
				.map(i -> methodFromType(method.getName(), method.getParameterTypes(), i)) //
				.filter(Objects::nonNull) //
				.filter(m -> m.isAnnotationPresent(Immutable.class)) //
				.findAny() //
				.isPresent();
	}

	private static Method methodFromType(String methodName, Class<?>[] parameterTypes, Class<?> type) {
		try {
			return type.getMethod(methodName, parameterTypes);
		} catch (NoSuchMethodException nsme) {
			return null;
		}
	}

	/**
	 * Returns <code>true</code> if the passed in {@link Method} is a Setter method
	 * of a property.
	 * 
	 * @param method
	 * @return
	 */
	public static boolean isSetter(Method method) {
		return method.getName().startsWith("set");
	}

	/**
	 * Returns the complex type for the passed in primitive type.
	 * 
	 * @param type
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static Class<?> loadComplexForPrimitive(Class<?> type) throws ClassNotFoundException {
		assert Objects.nonNull(type);
		assert type.isPrimitive() : "Given type must ne primitive";

		return Class.forName(type.getPackageName() //
				.concat(".") //
				.concat(type.getSimpleName().substring(0, 1).toUpperCase()) //
				.concat(type.getSimpleName().substring(1)));
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
