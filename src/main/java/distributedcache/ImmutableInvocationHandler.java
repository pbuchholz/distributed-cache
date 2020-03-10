package distributedcache;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

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

	public ImmutableInvocationHandler(Object target) {
		this.target = target;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		/* Setters are not allowed in immutable objects. */
		if (Reflections.isSetter(method)) {
			throw new MutationNotAllowedException();
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
