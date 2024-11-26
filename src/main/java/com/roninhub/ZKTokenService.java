package com.roninhub;

import org.apache.zookeeper.*;
import java.io.IOException;


public class ZKTokenService implements TokenService {
    private static final String ZK_SERVER = "localhost:2181";
    private static final String RANGE_NODE = "/tokenRanges";
    private static final long RANGE_SIZE = 100000000; // Size of each token range
    private ZooKeeper zooKeeper;

    public ZKTokenService() throws IOException {
        this.zooKeeper = new ZooKeeper(ZK_SERVER, 3000, event -> {});
    }

    public synchronized long[] allocateRange() throws Exception {
        // Atomically increment range counter in Zookeeper
        String counterPath = RANGE_NODE + "/counter";

        // Ensure parent node exists
        if (zooKeeper.exists(RANGE_NODE, false) == null) {
            zooKeeper.create(RANGE_NODE, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }

        // Ensure counter node exists
        if (zooKeeper.exists(counterPath, false) == null) {
            zooKeeper.create(counterPath, "0".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }

        // Atomically fetch and increment the counter
        byte[] currentData = zooKeeper.getData(counterPath, false, null);
        long currentRange = Long.parseLong(new String(currentData));
        long newRange = currentRange + 1;

        // Update counter in Zookeeper
        zooKeeper.setData(counterPath, String.valueOf(newRange).getBytes(), -1); // version -1 for new value
        // After this, ZK increase version (=0)

        long start = currentRange * RANGE_SIZE;
        long end = start + RANGE_SIZE - 1;

        System.out.printf("Fetched a token range: %d - %d\n", start, end);
        return new long[]{start, end};
    }
}
