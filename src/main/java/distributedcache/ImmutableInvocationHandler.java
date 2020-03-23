package distributedcache;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Wraps an object and attemps to make it immutable. This is done by blocking
 * calls to setters.
 * 
 * @author Philipp Buchholz
 *
 * @param <T> Type of object wrapped.
 */
public class ImmutableInvocationHandler implements InvocationHandler {

	private Object target;
	private Map<String, Method> immutableMethodsByName;

	public ImmutableInvocationHandler(Object target) {
		assert null != target;

		this.target = target;
		this.immutableMethodsByName = Reflections.immutableMethods(target.getClass()).stream() //
				.collect(Collectors.toMap(Method::getName, Function.identity()));
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		/* If method is not immutable... */
		if (!this.immutableMethodsByName.containsKey(method.getName())) {
			/* Just invoke it. */
			return method.invoke(target, args);
		}

		/* Setters are not allowed if the method is immutable. */
		if (Reflections.isSetter(method)) {
			throw new MutationNotAllowedException();
		}

		/* Also disallow modifications through the returned complex types. */
		if (!method.getReturnType().isPrimitive()) {
			if (method.getReturnType().isAssignableFrom(Map.class)) {
				return Collections.unmodifiableMap((Map<?, ?>) method.invoke(target, args));
			} else if (method.getReturnType().isAssignableFrom(Collection.class)) {
				Collections.unmodifiableCollection((Collection<?>) method.invoke(target, args));
			} else {
				Object complexReturn = method.invoke(target, args);

				return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
						complexReturn.getClass().getInterfaces(), new ImmutableInvocationHandler(complexReturn));
			}
		}

		return method.invoke(target, args);
	}

	/**
	 * {@link RuntimeException} (not checked) which is thrown if a mutation is tried
	 * on an immutable object.
	 * 
	 * @author Philipp Buchholz
	 */
	public final static class MutationNotAllowedException extends RuntimeException {

		private static final long serialVersionUID = 6852061590238013249L;

	}

}
