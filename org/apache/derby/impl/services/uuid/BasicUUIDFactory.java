// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.uuid;

import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.services.monitor.Monitor;
import org.apache.derby.iapi.services.uuid.UUIDFactory;

public final class BasicUUIDFactory implements UUIDFactory
{
    private long majorId;
    private long timemillis;
    private static final long MODULUS = 4294967296L;
    private static final long MULTIPLIER = 16385L;
    private static final long STEP = 134217729L;
    private static final long INITIAL_VALUE = 2551218188L;
    private long currentValue;
    
    public BasicUUIDFactory() {
        Object environment = Monitor.getMonitor().getEnvironment();
        if (environment != null) {
            final String string = environment.toString();
            if (string != null) {
                environment = string;
            }
            this.majorId = environment.hashCode();
        }
        else {
            this.majorId = Runtime.getRuntime().freeMemory();
        }
        this.majorId &= 0xFFFFFFFFFFFFL;
        this.resetCounters();
    }
    
    public synchronized UUID createUUID() {
        final long currentValue = (16385L * this.currentValue + 134217729L) % 4294967296L;
        this.currentValue = currentValue;
        final long n = currentValue;
        if (n == 2551218188L) {
            this.bumpMajor();
        }
        return new BasicUUID(this.majorId, this.timemillis, (int)n);
    }
    
    public UUID recreateUUID(final String s) {
        return new BasicUUID(s);
    }
    
    private void bumpMajor() {
        this.majorId = (this.majorId + 1L & 0xFFFFFFFFFFFFL);
        if (this.majorId == 0L) {
            this.resetCounters();
        }
    }
    
    private void resetCounters() {
        this.timemillis = System.currentTimeMillis();
        this.currentValue = 2551218188L;
    }
}
