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
		return this.processObject(target, Json.createObjectBuilder()).build();
	}

	private JsonObjectBuilder processObject(Object target, JsonObjectBuilder jsonObjectBuilder)
			throws ReflectiveOperationException {

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
			jsonObjectBuilder.addAll(this.handleCollection(filteredMethodName, Collection.class.cast(result)));
		} else {
			/* Go down in hierarchie. */
			jsonObjectBuilder.addAll(this.processObject(result, jsonObjectBuilder));
		}
		return jsonObjectBuilder;
	}

	private <V> JsonObjectBuilder handleCollection(String filteredMethodName, Collection<V> collection) {
		JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
		collection.forEach(v -> {
			try {
				jsonObjectBuilder.add(filteredMethodName, this.processObject(v, jsonObjectBuilder));
			} catch (ReflectiveOperationException e) {
				// TODO!
			}
		});
		return jsonObjectBuilder;
	}

	private <K, V> JsonObjectBuilder handleMap(Map<K, V> map) {
		JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
		map.keySet().forEach(k -> {
			try {
				jsonObjectBuilder.add(String.valueOf(k), this.processObject(map.get(k), jsonObjectBuilder));
			} catch (ReflectiveOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		return jsonObjectBuilder;
	}

	private boolean mustTerminateAt(Class<?> type) throws ClassNotFoundException {

		if (type.isPrimitive()) {
			/* Load complex type for primitive. */
			type = Class.forName(type.getPackageName() //
					.concat(".") //
					.concat(type.getSimpleName().substring(0, 1).toUpperCase()) //
					.concat(type.getSimpleName().substring(1)));
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
