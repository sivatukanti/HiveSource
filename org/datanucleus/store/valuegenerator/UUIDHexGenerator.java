// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.valuegenerator;

import org.datanucleus.util.TypeConversionHelper;
import java.util.Properties;

public class UUIDHexGenerator extends AbstractUUIDGenerator
{
    public UUIDHexGenerator(final String name, final Properties props) {
        super(name, props);
    }
    
    @Override
    protected String getIdentifier() {
        final StringBuffer str = new StringBuffer(32);
        str.append(TypeConversionHelper.getHexFromInt(UUIDHexGenerator.IP_ADDRESS));
        str.append(TypeConversionHelper.getHexFromInt(UUIDHexGenerator.JVM_UNIQUE));
        final short timeHigh = (short)(System.currentTimeMillis() >>> 32);
        str.append(TypeConversionHelper.getHexFromShort(timeHigh));
        final int timeLow = (int)System.currentTimeMillis();
        str.append(TypeConversionHelper.getHexFromInt(timeLow));
        str.append(TypeConversionHelper.getHexFromShort(this.getCount()));
        return str.toString();
    }
}
