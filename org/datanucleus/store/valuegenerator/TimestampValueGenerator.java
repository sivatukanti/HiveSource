// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.valuegenerator;

import java.util.Calendar;
import java.util.Properties;

public class TimestampValueGenerator extends AbstractGenerator
{
    public TimestampValueGenerator(final String name, final Properties props) {
        super(name, props);
    }
    
    @Override
    protected ValueGenerationBlock reserveBlock(final long size) {
        final Calendar cal = Calendar.getInstance();
        final Long[] ids = { cal.getTimeInMillis() };
        final ValueGenerationBlock block = new ValueGenerationBlock(ids);
        return block;
    }
}
