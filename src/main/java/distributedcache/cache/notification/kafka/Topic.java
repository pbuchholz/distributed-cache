package distributedcache.cache.notification.kafka;

/**
 * Represents a {@link Topic} we can subscribe to.
 * 
 * @author Philipp Buchholz
 */
public final class Topic {

	private Topic() {
		/* Builder only. */
	}

	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Builds instances {@link Topic}.
	 * 
	 * @author Philipp Buchholz
	 *
	 */
	public static final class Builder {

		private Topic topic;

		public Builder() {
			this.topic = new Topic();
		}

		Builder name(String name) {
			this.topic.name = name;
			return this;
		}

		public Topic build() {
			return this.topic;
		}

	}

	private String name;

	public String getTopicName() {
		return this.name;
	}
	
	public boolean isPresent() {
		return !"".equals(name);
	}

}
