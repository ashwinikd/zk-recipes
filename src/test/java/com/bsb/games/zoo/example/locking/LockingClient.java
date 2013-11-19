package com.bsb.games.zoo.example.locking;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;

public class LockingClient {
    private final FakeLimitedResource resource;
    private final InterProcessMutex lock;
    private final String name;
    private final Logger log;
    
    public LockingClient(CuratorFramework client, FakeLimitedResource res, String strName, String lockPath) {
        name = strName;
        resource = res;
        lock = new InterProcessMutex(client, lockPath);
        log = Logger.getLogger(name);
    }
    
    public void run(long time, TimeUnit unit) throws Exception {
        if(! lock.acquire(time, unit)) {
            throw new IllegalStateException(name + " could not acquire lock");
        }
        
        try {
            log.info(name + " got lock.");
            resource.use();
        } finally {
            log.info(name + " is releasing lock.");
            lock.release();
        }
    }
}
