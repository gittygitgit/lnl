import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map.Entry;
import java.util.Set;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

public abstract class AbstractNode {

	private static EmbeddedCacheManager createCacheManagerProgramatically() {
		return new DefaultCacheManager(GlobalConfigurationBuilder.defaultClusteredBuilder().transport()
				.addProperty("configurationFile", "jgroups_tcp.xml").build(), new ConfigurationBuilder().clustering()
				.cacheMode(CacheMode.REPL_SYNC).build());
	}

	private static EmbeddedCacheManager createCacheManagerFromXml() throws IOException {
		return new DefaultCacheManager("infinispan-replication.xml");
	}

	public static final int CLUSTER_SIZE = 4;

	private final EmbeddedCacheManager cacheManager;

	public AbstractNode() {
		this.cacheManager = createCacheManagerProgramatically();
		// Uncomment to create cache from XML
		// try {
		// this.cacheManager = createCacheManagerFromXml();
		// } catch (IOException e) {
		// throw new RuntimeException(e);
		// }
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

	protected void inputCache(Cache<String, String> cache, int startKey) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		int keyCtr = startKey;
		String userInput;
		while ((userInput = reader.readLine()) != null) {
			if (userInput.equals("info")) {
				Set<Entry<String, String>> entrySet = cache.entrySet();
				for (Entry<String, String> entry : entrySet) {
					System.out.printf("Entry: [key=%-5s, value=%20s\n",entry.getKey(), entry.getValue());
				}
			} else {
				System.out.println("Adding cache entry [nodeId=" + getNodeId() + ", key=" + keyCtr + ", value="
						+ userInput + "]");
				cache.put("" + keyCtr++, userInput);
			}
		}
	}

}
