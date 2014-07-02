/*
 * Copyright 2013 Netflix, Inc.
 * Copyright 2014 Scalr, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.scalr.cassandra;

import org.apache.cassandra.locator.SeedProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScalrSeedProvider implements SeedProvider {
    private static final Logger logger = LoggerFactory.getLogger(ScalrSeedProvider.class);

    public ScalrSeedProvider(Map<String, String> args)
    {    }

    @Override
    public List<InetAddress> getSeeds() {
        List<InetAddress> seeds = new ArrayList<InetAddress>();

        Integer cassandraAgentPort;

        try
        {
            cassandraAgentPort = Integer.parseInt(System.getProperty("com.scalr.cassandra.agent.port"));
        }
        catch (NumberFormatException e)
        {
            logger.info("Defaulting agent port to 8020");
            cassandraAgentPort = 8020;
        }

        try
        {
            String url = String.format("http://127.0.0.1:%s/seeds", cassandraAgentPort);
            String cassandraSeeds = DataFetcher.fetchData(url);
            for (String seed : cassandraSeeds.split(","))
                seeds.add(InetAddress.getByName(seed));
        }
        catch (Exception e)
        {
            logger.error("Failed to load seed data", e);
        }
        return seeds;
    }
}
