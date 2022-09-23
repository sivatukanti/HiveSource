// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.valuegenerator;

import org.datanucleus.util.TypeConversionHelper;
import java.net.InetAddress;
import java.util.Properties;

public abstract class AbstractUUIDGenerator extends AbstractUIDGenerator
{
    static final int IP_ADDRESS;
    static final int JVM_UNIQUE;
    static short counter;
    
    public AbstractUUIDGenerator(final String name, final Properties props) {
        super(name, props);
    }
    
    protected short getCount() {
        synchronized (AbstractUUIDGenerator.class) {
            if (AbstractUUIDGenerator.counter < 0) {
                AbstractUUIDGenerator.counter = 0;
            }
            final short counter = AbstractUUIDGenerator.counter;
            AbstractUUIDGenerator.counter = (short)(counter + 1);
            return counter;
        }
    }
    
    static {
        int ipAddr = 0;
        try {
            ipAddr = TypeConversionHelper.getIntFromByteArray(InetAddress.getLocalHost().getAddress());
        }
        catch (Exception e) {
            ipAddr = 0;
        }
        IP_ADDRESS = ipAddr;
        JVM_UNIQUE = (int)(System.currentTimeMillis() >>> 8);
        AbstractUUIDGenerator.counter = 0;
    }
}
