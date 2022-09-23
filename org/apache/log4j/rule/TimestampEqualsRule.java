// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.rule;

import java.text.SimpleDateFormat;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import org.apache.log4j.spi.LoggingEvent;
import java.text.ParseException;
import java.text.DateFormat;
import org.apache.log4j.spi.LoggingEventFieldResolver;

public class TimestampEqualsRule extends AbstractRule
{
    static final long serialVersionUID = 1639079557187790321L;
    private static final LoggingEventFieldResolver RESOLVER;
    private static final DateFormat DATE_FORMAT;
    private long timeStamp;
    
    private TimestampEqualsRule(final String value) {
        try {
            this.timeStamp = TimestampEqualsRule.DATE_FORMAT.parse(value).getTime();
        }
        catch (ParseException pe) {
            throw new IllegalArgumentException("Could not parse date: " + value);
        }
    }
    
    public static Rule getRule(final String value) {
        return new TimestampEqualsRule(value);
    }
    
    public boolean evaluate(final LoggingEvent event, final Map matches) {
        final String eventTimeStampString = TimestampEqualsRule.RESOLVER.getValue("TIMESTAMP", event).toString();
        final long eventTimeStamp = Long.parseLong(eventTimeStampString) / 1000L * 1000L;
        final boolean result = eventTimeStamp == this.timeStamp;
        if (result && matches != null) {
            Set entries = matches.get("TIMESTAMP");
            if (entries == null) {
                entries = new HashSet();
                matches.put("TIMESTAMP", entries);
            }
            entries.add(eventTimeStampString);
        }
        return result;
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.timeStamp = in.readLong();
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.writeLong(this.timeStamp);
    }
    
    static {
        RESOLVER = LoggingEventFieldResolver.getInstance();
        DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    }
}
