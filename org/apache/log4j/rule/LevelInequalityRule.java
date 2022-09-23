// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.rule;

import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.helpers.UtilLoggingLevel;
import org.apache.log4j.Level;
import java.util.LinkedList;
import java.util.List;

public class LevelInequalityRule
{
    private static List levelList;
    private static List utilLoggingLevelList;
    
    private LevelInequalityRule() {
    }
    
    private static void populateLevels() {
        (LevelInequalityRule.levelList = new LinkedList()).add(Level.FATAL.toString());
        LevelInequalityRule.levelList.add(Level.ERROR.toString());
        LevelInequalityRule.levelList.add(Level.WARN.toString());
        LevelInequalityRule.levelList.add(Level.INFO.toString());
        LevelInequalityRule.levelList.add(Level.DEBUG.toString());
        final Level trace = Level.toLevel(5000, null);
        if (trace != null) {
            LevelInequalityRule.levelList.add(trace.toString());
        }
        (LevelInequalityRule.utilLoggingLevelList = new LinkedList()).add(UtilLoggingLevel.SEVERE.toString());
        LevelInequalityRule.utilLoggingLevelList.add(UtilLoggingLevel.WARNING.toString());
        LevelInequalityRule.utilLoggingLevelList.add(UtilLoggingLevel.INFO.toString());
        LevelInequalityRule.utilLoggingLevelList.add(UtilLoggingLevel.CONFIG.toString());
        LevelInequalityRule.utilLoggingLevelList.add(UtilLoggingLevel.FINE.toString());
        LevelInequalityRule.utilLoggingLevelList.add(UtilLoggingLevel.FINER.toString());
        LevelInequalityRule.utilLoggingLevelList.add(UtilLoggingLevel.FINEST.toString());
    }
    
    public static Rule getRule(final String inequalitySymbol, final String value) {
        Level thisLevel;
        if (LevelInequalityRule.levelList.contains(value.toUpperCase())) {
            thisLevel = Level.toLevel(value.toUpperCase());
        }
        else {
            if (!LevelInequalityRule.utilLoggingLevelList.contains(value.toUpperCase())) {
                throw new IllegalArgumentException("Invalid level inequality rule - " + value + " is not a supported level");
            }
            thisLevel = UtilLoggingLevel.toLevel(value.toUpperCase());
        }
        if ("<".equals(inequalitySymbol)) {
            return new LessThanRule(thisLevel);
        }
        if (">".equals(inequalitySymbol)) {
            return new GreaterThanRule(thisLevel);
        }
        if ("<=".equals(inequalitySymbol)) {
            return new LessThanEqualsRule(thisLevel);
        }
        if (">=".equals(inequalitySymbol)) {
            return new GreaterThanEqualsRule(thisLevel);
        }
        return null;
    }
    
    static {
        populateLevels();
    }
    
    private static final class LessThanRule extends AbstractRule
    {
        private final int newLevelInt;
        
        public LessThanRule(final Level level) {
            this.newLevelInt = level.toInt();
        }
        
        public boolean evaluate(final LoggingEvent event, final Map matches) {
            final Level eventLevel = event.getLevel();
            final boolean result = eventLevel.toInt() < this.newLevelInt;
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
    }
    
    private static final class GreaterThanRule extends AbstractRule
    {
        private final int newLevelInt;
        
        public GreaterThanRule(final Level level) {
            this.newLevelInt = level.toInt();
        }
        
        public boolean evaluate(final LoggingEvent event, final Map matches) {
            final Level eventLevel = event.getLevel();
            final boolean result = eventLevel.toInt() > this.newLevelInt;
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
    }
    
    private static final class GreaterThanEqualsRule extends AbstractRule
    {
        private final int newLevelInt;
        
        public GreaterThanEqualsRule(final Level level) {
            this.newLevelInt = level.toInt();
        }
        
        public boolean evaluate(final LoggingEvent event, final Map matches) {
            final Level eventLevel = event.getLevel();
            final boolean result = eventLevel.toInt() >= this.newLevelInt;
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
    }
    
    private static final class LessThanEqualsRule extends AbstractRule
    {
        private final int newLevelInt;
        
        public LessThanEqualsRule(final Level level) {
            this.newLevelInt = level.toInt();
        }
        
        public boolean evaluate(final LoggingEvent event, final Map matches) {
            final Level eventLevel = event.getLevel();
            final boolean result = eventLevel.toInt() <= this.newLevelInt;
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
    }
}
