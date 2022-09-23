// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.valuegenerator;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Properties;

public class TimestampGenerator extends AbstractGenerator
{
    public TimestampGenerator(final String name, final Properties props) {
        super(name, props);
    }
    
    @Override
    protected ValueGenerationBlock reserveBlock(final long size) {
        final Calendar cal = Calendar.getInstance();
        final Timestamp[] ts = { new Timestamp(cal.getTimeInMillis()) };
        final ValueGenerationBlock block = new ValueGenerationBlock(ts);
        return block;
    }
}
