// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.valuegenerator;

import org.datanucleus.util.TypeConversionHelper;
import java.util.Properties;

public class UUIDStringGenerator extends AbstractUUIDGenerator
{
    public UUIDStringGenerator(final String name, final Properties props) {
        super(name, props);
    }
    
    @Override
    protected String getIdentifier() {
        final byte[] ipAddrBytes = TypeConversionHelper.getBytesFromInt(UUIDStringGenerator.IP_ADDRESS);
        final byte[] jvmBytes = TypeConversionHelper.getBytesFromInt(UUIDStringGenerator.JVM_UNIQUE);
        final short timeHigh = (short)(System.currentTimeMillis() >>> 32);
        final byte[] timeHighBytes = TypeConversionHelper.getBytesFromShort(timeHigh);
        final int timeLow = (int)System.currentTimeMillis();
        final byte[] timeLowBytes = TypeConversionHelper.getBytesFromInt(timeLow);
        final short count = this.getCount();
        final byte[] countBytes = TypeConversionHelper.getBytesFromShort(count);
        final byte[] bytes = new byte[16];
        int pos = 0;
        for (int i = 0; i < 4; ++i) {
            bytes[pos++] = ipAddrBytes[i];
        }
        for (int i = 0; i < 4; ++i) {
            bytes[pos++] = jvmBytes[i];
        }
        for (int i = 0; i < 2; ++i) {
            bytes[pos++] = timeHighBytes[i];
        }
        for (int i = 0; i < 4; ++i) {
            bytes[pos++] = timeLowBytes[i];
        }
        for (int i = 0; i < 2; ++i) {
            bytes[pos++] = countBytes[i];
        }
        try {
            return new String(bytes, "ISO-8859-1");
        }
        catch (Exception e) {
            return new String(bytes);
        }
    }
}
