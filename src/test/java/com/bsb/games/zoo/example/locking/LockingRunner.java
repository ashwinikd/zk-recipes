package com.bsb.games.zoo.example.locking;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class LockingRunner
{
    private static final int NUM_CLIENTS = 10;
    private static final int RETRY_COUNT = 10;
    private static final String LOCK_PATH = "/zootest/lock";
    private static final String ZOO_CONN_STR = "localhost:2181";
    
    public static void main(String[] args) throws InterruptedException {
        final FakeLimitedResource limitedResource = new FakeLimitedResource();
        ExecutorService service = Executors.newFixedThreadPool(NUM_CLIENTS);
        
        for(int i = 0; i < NUM_CLIENTS; i++) {
            final int n = i;
            
            Callable<Void> task = new Callable<Void>() {

                public Void call() throws Exception {
                    CuratorFramework client = CuratorFrameworkFactory.newClient(ZOO_CONN_STR, new ExponentialBackoffRetry(1000, 3));
                    client.start();
                    LockingClient lockingClient = new LockingClient(client, limitedResource, "Client#" + n, LOCK_PATH);
                    try {
                        for(int j = 0; j < RETRY_COUNT; ++j) {
                            lockingClient.run(10, TimeUnit.SECONDS);
                        }
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } finally {
                        client.close();
                    }
                    return null;
                }
            };
            
            service.submit(task);
        }
        service.shutdown();
        service.awaitTermination(10, TimeUnit.MINUTES);
    }
}
