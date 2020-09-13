package distributedcache.cache;

import java.io.Serializable;

/**
 * Represents a Region in a {@link Cache}.
 * 
 * @author Philipp Buchholz
 */
public class CacheRegion<K extends CacheKey<K>, T extends Serializable> extends BaseCache<K, T> {

	private String regionName;

	public String getRegionName() {
		return this.regionName;
	}

	private CacheRegion() {
		super();
		/* Builder */
	}

	public static <K extends CacheKey<K>, T extends Serializable> CacheRegionBuilder<K, T> cacheRegionBuilder() {
		return new CacheRegionBuilder<>();
	}

	public static class CacheRegionBuilder<K extends CacheKey<K>, T extends Serializable>
			extends BaseCache.Builder<K, T, CacheRegionBuilder<K, T>> {

		private String regionName;

		public CacheRegionBuilder<K, T> regionName(String regionName) {
			this.regionName = regionName;
			return this;
		}

		@Override
		protected CacheRegionBuilder<K, T> self() {
			return this;
		}

		@Override
		public CacheRegion<K, T> build() {
			CacheRegion<K, T> cacheRegion = new CacheRegion<>();
			cacheRegion.regionName = regionName;
			cacheRegion.cacheConfiguration = this.cacheConfiguration;
			cacheRegion.cacheConfiguration = this.cacheConfiguration;
			return cacheRegion;
		}

	}

}
