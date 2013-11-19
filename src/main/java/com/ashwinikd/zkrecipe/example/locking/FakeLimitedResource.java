package com.ashwinikd.zkrecipe.example.locking;

import java.util.concurrent.atomic.AtomicBoolean;

public class FakeLimitedResource {
    private AtomicBoolean inUse = new AtomicBoolean(false);
    
    public void use() throws InterruptedException {
        if(! inUse.compareAndSet(false, true)) {
            throw new IllegalStateException("Only one client can use.");
        }
        
        try {
            Thread.sleep((long) (3* Math.random()));
        } finally {
            inUse.set(false);
        }
    }
}
