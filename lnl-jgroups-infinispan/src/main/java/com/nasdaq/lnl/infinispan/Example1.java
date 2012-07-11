package com.nasdaq.lnl.infinispan;

import org.infinispan.Cache;
import org.infinispan.config.Configuration;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

public class Example1 {

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		// Create a cache container to hold the caches
		EmbeddedCacheManager manager = new DefaultCacheManager();
		// Get the default cache
		// Since we passed in neither a Configuration nor a configuration file, default configuration will be used.
		Cache<Object, Object> cache = manager.getCache();

		
		Configuration defaultConfig = manager.getDefaultConfiguration();

		Configuration overrides = new Configuration().fluent()
			.expiration()
				.lifespan(1000l)
			.build();
		defaultConfig.applyOverrides(overrides);
		
		manager.defineConfiguration("named", overrides);
		Cache<Object, Object> cache2 = manager.getCache("named");

		cache.put("foo", "bar");
		Thread.sleep(2000l);
		System.out.println(cache.get("foo"));

		
		cache2.put("foo", "bar");
		Thread.sleep(2000l);
		System.out.println(cache2.get("foo"));
		
		
		
	}
}
