package com.nasdaq.lnl.infinispan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang.ArrayUtils;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.jgroups.Message;

import com.nasdaq.lnl.domain.Quote;

public abstract class AbstractNode2 {

	private static EmbeddedCacheManager createCacheManagerProgramatically() {
		return new DefaultCacheManager(GlobalConfigurationBuilder.defaultClusteredBuilder()
				.transport().addProperty("configurationFile", "jgroups_tcp.xml").build(),
				new ConfigurationBuilder().clustering().cacheMode(CacheMode.REPL_SYNC).build());
	}

	private static EmbeddedCacheManager createCacheManagerFromXml() throws IOException {
//		return new DefaultCacheManager("infinispan-replication.xml");
		return new DefaultCacheManager("infinispan-distribution.xml");
		
	}

	public static final int CLUSTER_SIZE = 4;

	private final EmbeddedCacheManager cacheManager;

	public AbstractNode2() {
		// this.cacheManager = createCacheManagerProgramatically();
		// Uncomment to create cache from XML
		try {
			this.cacheManager = createCacheManagerFromXml();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected EmbeddedCacheManager getCacheManager() {
		return cacheManager;
	}

	protected void waitForClusterToForm() {
		// Wait for the cluster to form, erroring if it doesn't form after the
		// timeout
		if (!ClusterValidation.waitForClusterToForm(getCacheManager(), getNodeId(), CLUSTER_SIZE)) {
			throw new IllegalStateException("Error forming cluster, check the log");
		}
	}

	protected abstract int getNodeId();

	// basic test
	// protected void loop(Cache<String, Quote> cache) throws IOException {
	// BufferedReader reader = new BufferedReader(new
	// InputStreamReader(System.in));
	// String userInput;
	// while ((userInput = reader.readLine()) != null) {
	//
	// StringTokenizer msgTypeTokenizer = new StringTokenizer(userInput, ":");
	// char msgType = Character.valueOf(msgTypeTokenizer.nextToken().charAt(0));
	// String und;
	// switch (msgType) {
	// case 'P':
	// msgTypeTokenizer = new StringTokenizer(userInput.substring(2), " ");
	// und = msgTypeTokenizer.nextToken().toUpperCase();
	// cache.put(und, new Quote(und, new
	// BigDecimal(msgTypeTokenizer.nextToken()),
	// new BigDecimal(msgTypeTokenizer.nextToken())));
	// break;
	// case 'G':
	// und = userInput.substring(2).toUpperCase();
	// System.out.println(cache.get(und).toString());
	// break;
	//
	// default:
	// break;
	// }
	//
	// }
	// }

	// expiration
	protected void loop(Cache<String, Quote> cache) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String userInput;
		while ((userInput = reader.readLine()) != null) {

			StringTokenizer msgTypeTokenizer = new StringTokenizer(userInput, ":");
			char msgType = Character.valueOf(msgTypeTokenizer.nextToken().charAt(0));
			String und;
			Quote qte;
			switch (msgType) {
			case 'P':
				msgTypeTokenizer = new StringTokenizer(userInput.substring(2), " ");
				boolean setExpiration = msgTypeTokenizer.countTokens() == 4;
				und = msgTypeTokenizer.nextToken().toUpperCase();
				BigDecimal bid = new BigDecimal(msgTypeTokenizer.nextToken());
				BigDecimal ask = new BigDecimal(msgTypeTokenizer.nextToken());
				qte = new Quote(und, bid, ask);
				if (setExpiration) {
					cache.put(und, qte, Long.parseLong(msgTypeTokenizer.nextToken()),
							TimeUnit.SECONDS);
				} else {
					cache.put(und, qte);
				}
				break;
			case 'G':
				und = userInput.substring(2).toUpperCase();
				qte = cache.get(und);
				if (qte != null)
					System.out.println(cache.get(und).toString());
				break;
			case 'L':
				Set<Entry<String, Quote>> entrySet = cache.entrySet();
				for (Iterator iterator = entrySet.iterator(); iterator.hasNext();) {
					Entry<String, Quote> entry = (Entry<String, Quote>) iterator.next();
					System.out.println(entry.getValue().toString());
				}
				break;
			case 'E':
				und = userInput.substring(2).toUpperCase();
				cache.evict(und);
				System.out.println("Entry for und " + und + " evicted from Node");
				break;
			case 'R':
				und = userInput.substring(2).toUpperCase();
				cache.remove(und);
				System.out.println("Entry for und " + und + " removed from Cluster");
				break;
			default:
				break;
			}

		}
	}
}
