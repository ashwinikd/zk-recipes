package com.bsb.games.zoo.example.leader;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.framework.state.ConnectionState;

public class LeaderClient extends LeaderSelectorListenerAdapter implements Closeable
{
    private String name;
    private LeaderSelector selector;
    private AtomicInteger count;
    private Logger log;
    
    public LeaderClient(CuratorFramework curatorClient, String path, String strName) {
        name = strName;
        log = Logger.getLogger(name);
        log.info("Starting " + name);
        selector = new LeaderSelector(curatorClient, path, this);
        selector.autoRequeue();
    }
    
    public void takeLeadership(CuratorFramework arg0) throws Exception {
        int waitTime = (int)(5 * Math.random()) + 1;
        log.warning("Client-" + name + " has been leader for " + count.getAndIncrement() + "times before." );
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(waitTime));
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            log.warning(name + " is relinquishing leadership.");
        }
    }
    
    public void start() {
        selector.start();
    }

    public void close() throws IOException {
        selector.close();
    }
    
    @Override
    public void stateChanged(CuratorFramework client, ConnectionState newState) {
        // TODO Auto-generated method stub
        log.info("State of " + name + " changed to " + newState.toString());
        super.stateChanged(client, newState);
    }
}
