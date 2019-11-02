package distributedcache;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class JsonReflector {

	private final static String GETTER_PREFIX = "get";
	private final static String EMPTY = "";

	private List<Class<?>> terminatingTypes = Arrays.asList(String.class, //
			Integer.class, //
			Short.class, //
			Long.class, //
			Boolean.class, //
			Class.class);

	private List<String> methodNamesToTraverse;

	public static Builder builder() {
		return new Builder();
	}

	private JsonReflector() {

	}

	public static class Builder {
		private JsonReflector jsonReflector = new JsonReflector();

		private Builder() {
			jsonReflector.methodNamesToTraverse = new ArrayList<>();
		}

		public Builder terminatingType(Class<?> terminatingType) {
			jsonReflector.terminatingTypes.add(terminatingType);
			return this;
		}

		public Builder traverseMethod(String methodNameToTraverse) {
			jsonReflector.methodNamesToTraverse.add(methodNameToTraverse);
			return this;
		}

		public JsonReflector build() {
			return jsonReflector;
		}
	}

	public JsonObject buildJsonObject(Object target) throws ReflectiveOperationException {
		return this.processObject(target).build();
	}

	private JsonObjectBuilder processObject(Object target) throws ReflectiveOperationException {
		JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
		for (Method method : this.filterMethods(target)) {
			jsonObjectBuilder.addAll(this.processMethod(method.getReturnType(), //
					this.fieldNameFromGetter(method), target, method));
		}

		return jsonObjectBuilder;
	}

	@SuppressWarnings("unchecked")
	private JsonObjectBuilder processMethod(Class<?> returnType, String filteredMethodName, Object target,
			Method method) throws ReflectiveOperationException {
		JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();

		Object result = method.invoke(target);

		if (mustTerminateAt(returnType)) {
			/* Termination types. */
			jsonObjectBuilder.add(filteredMethodName, String.valueOf(result));
		} else if (returnType.isAssignableFrom(Map.class)) {
			/* Handling of Maps. */
			jsonObjectBuilder.addAll(this.handleMap(Map.class.cast(result)));
		} else if (returnType.isAssignableFrom(Collection.class)) {
			jsonObjectBuilder.add(filteredMethodName,
					this.handleCollection(filteredMethodName, Collection.class.cast(result)));
		} else {
			/* Go down in hierarchie. */
			jsonObjectBuilder.add(filteredMethodName, this.processObject(result));
		}
		return jsonObjectBuilder;
	}

	private <V> JsonArrayBuilder handleCollection(String filteredMethodName, Collection<V> collection) {
		JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
		collection.forEach(v -> {
			try {
				jsonArrayBuilder.add(this.processObject(v));
			} catch (ReflectiveOperationException e) {
				throw new RuntimeException(e);
			}
		});
		return jsonArrayBuilder;
	}

	private <K, V> JsonObjectBuilder handleMap(Map<K, V> map) {
		JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
		map.keySet().forEach(k -> {
			try {
				jsonObjectBuilder.add(String.valueOf(k), this.processObject(map.get(k)));
			} catch (ReflectiveOperationException e) {
				throw new RuntimeException(e);
			}
		});
		return jsonObjectBuilder;
	}

	private boolean mustTerminateAt(Class<?> type) throws ClassNotFoundException {

		if (type.isPrimitive()) {
			type = Reflections.loadComplexForPrimitive(type);
		}

		for (Class<?> t : this.terminatingTypes) {
			if (t.isAssignableFrom(type)) {
				return true;
			}
		}

		return false;
	}

	private String fieldNameFromGetter(Method getter) {
		return getter.getName() //
				.replace(GETTER_PREFIX, EMPTY) //
				.toLowerCase();
	}

	private List<Method> filterMethods(Object target) {
		return Stream.of(target.getClass().getMethods()) //
				.filter(m -> m.getName().startsWith(GETTER_PREFIX) //
						|| this.methodNamesToTraverse.contains(m.getName())) //
				.collect(Collectors.toList());

	}

}
