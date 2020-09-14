package distributedcache.cache;

import java.io.Serializable;

public class BaseCacheRegion<K extends CacheKey<K>, T extends Serializable> //
		extends BaseCache<K, T> //
		implements CacheRegion<K, T> {

	private String regionName;

	public String getRegionName() {
		return this.regionName;
	}

	private BaseCacheRegion() {
		super();
		/* Builder */
	}

	public static <K extends CacheKey<K>, T extends Serializable> BaseCacheRegionBuilder<K, T> cacheRegionBuilder() {
		return new BaseCacheRegionBuilder<>();
	}

	public static class BaseCacheRegionBuilder<K extends CacheKey<K>, T extends Serializable>
			extends BaseCache.Builder<K, T, BaseCacheRegionBuilder<K, T>> {

		private String regionName;

		public BaseCacheRegionBuilder<K, T> regionName(String regionName) {
			this.regionName = regionName;
			return this;
		}

		@Override
		protected BaseCacheRegionBuilder<K, T> self() {
			return this;
		}

		@Override
		public CacheRegion<K, T> build() {
			BaseCacheRegion<K, T> bcr = new BaseCacheRegion<>();
			bcr.regionName = regionName;
			bcr.cacheConfiguration = this.cacheConfiguration;
			bcr.cacheConfiguration = this.cacheConfiguration;
			bcr.cacheRegions = this.cacheRegions;
			return bcr;
		}

	}

}
