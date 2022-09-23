// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.rule;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.helpers.UtilLoggingLevel;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Level;

public class NotLevelEqualsRule extends AbstractRule
{
    static final long serialVersionUID = -3638386582899583994L;
    private transient Level level;
    private static List levelList;
    
    private NotLevelEqualsRule(final Level level) {
        this.level = level;
    }
    
    private static void populateLevels() {
        (NotLevelEqualsRule.levelList = new LinkedList()).add(Level.FATAL.toString());
        NotLevelEqualsRule.levelList.add(Level.ERROR.toString());
        NotLevelEqualsRule.levelList.add(Level.WARN.toString());
        NotLevelEqualsRule.levelList.add(Level.INFO.toString());
        NotLevelEqualsRule.levelList.add(Level.DEBUG.toString());
        final Level trace = Level.toLevel(5000, null);
        if (trace != null) {
            NotLevelEqualsRule.levelList.add(trace.toString());
        }
    }
    
    public static Rule getRule(final String value) {
        Level thisLevel;
        if (NotLevelEqualsRule.levelList.contains(value.toUpperCase())) {
            thisLevel = Level.toLevel(value.toUpperCase());
        }
        else {
            thisLevel = UtilLoggingLevel.toLevel(value.toUpperCase());
        }
        return new NotLevelEqualsRule(thisLevel);
    }
    
    public boolean evaluate(final LoggingEvent event, final Map matches) {
        final Level eventLevel = event.getLevel();
        final boolean result = this.level.toInt() != eventLevel.toInt();
        if (result && matches != null) {
            Set entries = matches.get("LEVEL");
            if (entries == null) {
                entries = new HashSet();
                matches.put("LEVEL", entries);
            }
            entries.add(eventLevel);
        }
        return result;
    }
    
    private void readObject(final ObjectInputStream in) throws IOException {
        populateLevels();
        final boolean isUtilLogging = in.readBoolean();
        final int levelInt = in.readInt();
        if (isUtilLogging) {
            this.level = UtilLoggingLevel.toLevel(levelInt);
        }
        else {
            this.level = Level.toLevel(levelInt);
        }
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.writeBoolean(this.level instanceof UtilLoggingLevel);
        out.writeInt(this.level.toInt());
    }
    
    static {
        NotLevelEqualsRule.levelList = new LinkedList();
        populateLevels();
    }
}
