package com.nasdaq.lnl.infinispan.demo1;
/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other
 * contributors as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a full listing of
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

import java.io.IOException;

import org.infinispan.Cache;
import org.infinispan.util.logging.Log;
import org.infinispan.util.logging.LogFactory;

import com.nasdaq.lnl.infinispan.AbstractNode;
import com.nasdaq.lnl.infinispan.LoggingListener;

public class Node3 extends AbstractNode {

	private Log log = LogFactory.getLog(LoggingListener.class);

	public static void main(String[] args) throws Exception {
		new Node3().run();
	}

	public void run() throws IOException, InterruptedException {
		Cache<String, String> cache = getCacheManager().getCache("Demo");
		cache.addListener(new LoggingListener());

		waitForClusterToForm();

		log.info("About to put key, value into cache on node " + getNodeId());
		// Put some information in the cache that we can display on the other
		// node
		cache.put("key", "value");
		inputCache(cache, 3000);
	}

	@Override
	protected int getNodeId() {
		return 2;
	}

}
