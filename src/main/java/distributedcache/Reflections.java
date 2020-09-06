package distributedcache;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.introspect.Annotated;

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
		assert type.isPrimitive() : "Given type must be primitive";

		return Class.forName(type.getPackageName() //
				.concat(".") //
				.concat(type.getSimpleName().substring(0, 1).toUpperCase()) //
				.concat(type.getSimpleName().equals("int") ? "nteger" : type.getSimpleName().substring(1)));
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
	public static <A extends Annotation> String valueFromAnnotatedElement(AnnotatedElement annotatedElement,
			Class<A> annotationType, Annotation annotationInstance) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		return fromAnnotatedElement(annotatedElement, annotationType, annotationInstance, "value");
	}

	@SuppressWarnings("unchecked")
	public static <T, A extends Annotation> T fromAnnotatedElement(AnnotatedElement annotatedElement,
			Class<A> annotationType, Annotation annotationInstance, String methodName) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		return (T) annotatedElement //
				.getAnnotation(annotationType) //
				.getClass() //
				.getMethod(methodName) //
				.invoke(annotationInstance);
	}

	/**
	 * Returns <code>true</code> if the passed in type defines a valueOf method.
	 * 
	 * @param type
	 * @return
	 */
	public static boolean definesValueOf(Class<?> type) {
		return Stream.of(type.getDeclaredMethods()) //
				.filter(m -> "valueOf".equals(m.getName())) //
				.count() > 0;
	}

	/**
	 * Constructs a new instance of T using the static valueOf factory method. To
	 * work the type to be constructed must defined a static valueOf method.
	 * 
	 * @param <T>
	 * @param <P>
	 * @param type
	 * @param parameter
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@SuppressWarnings("unchecked")
	public static <T, P> T constructWithValueOf(Class<T> type, P parameter) throws NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (!definesValueOf(type)) {
			throw new IllegalArgumentException("Passed in type cannot be constructed with valueOf factory method.");
		}
		Method valueOfMethod = type.getDeclaredMethod("valueOf", parameter.getClass());
		return (T) valueOfMethod.invoke(null, parameter);
	}

}
