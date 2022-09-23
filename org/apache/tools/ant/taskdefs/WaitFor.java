// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import java.util.Locale;
import java.util.HashMap;
import java.util.Map;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.condition.ConditionBase;

public class WaitFor extends ConditionBase
{
    public static final long ONE_MILLISECOND = 1L;
    public static final long ONE_SECOND = 1000L;
    public static final long ONE_MINUTE = 60000L;
    public static final long ONE_HOUR = 3600000L;
    public static final long ONE_DAY = 86400000L;
    public static final long ONE_WEEK = 604800000L;
    public static final long DEFAULT_MAX_WAIT_MILLIS = 180000L;
    public static final long DEFAULT_CHECK_MILLIS = 500L;
    private long maxWait;
    private long maxWaitMultiplier;
    private long checkEvery;
    private long checkEveryMultiplier;
    private String timeoutProperty;
    
    public WaitFor() {
        super("waitfor");
        this.maxWait = 180000L;
        this.maxWaitMultiplier = 1L;
        this.checkEvery = 500L;
        this.checkEveryMultiplier = 1L;
    }
    
    public WaitFor(final String taskName) {
        super(taskName);
        this.maxWait = 180000L;
        this.maxWaitMultiplier = 1L;
        this.checkEvery = 500L;
        this.checkEveryMultiplier = 1L;
    }
    
    public void setMaxWait(final long time) {
        this.maxWait = time;
    }
    
    public void setMaxWaitUnit(final Unit unit) {
        this.maxWaitMultiplier = unit.getMultiplier();
    }
    
    public void setCheckEvery(final long time) {
        this.checkEvery = time;
    }
    
    public void setCheckEveryUnit(final Unit unit) {
        this.checkEveryMultiplier = unit.getMultiplier();
    }
    
    public void setTimeoutProperty(final String p) {
        this.timeoutProperty = p;
    }
    
    public void execute() throws BuildException {
        if (this.countConditions() > 1) {
            throw new BuildException("You must not nest more than one condition into " + this.getTaskName());
        }
        if (this.countConditions() < 1) {
            throw new BuildException("You must nest a condition into " + this.getTaskName());
        }
        final Condition c = this.getConditions().nextElement();
        try {
            final long maxWaitMillis = this.calculateMaxWaitMillis();
            final long checkEveryMillis = this.calculateCheckEveryMillis();
            final long start = System.currentTimeMillis();
            final long end = start + maxWaitMillis;
            while (System.currentTimeMillis() < end) {
                if (c.eval()) {
                    this.processSuccess();
                    return;
                }
                Thread.sleep(checkEveryMillis);
            }
        }
        catch (InterruptedException e) {
            this.log("Task " + this.getTaskName() + " interrupted, treating as timed out.");
        }
        this.processTimeout();
    }
    
    public long calculateCheckEveryMillis() {
        return this.checkEvery * this.checkEveryMultiplier;
    }
    
    public long calculateMaxWaitMillis() {
        return this.maxWait * this.maxWaitMultiplier;
    }
    
    protected void processSuccess() {
        this.log(this.getTaskName() + ": condition was met", 3);
    }
    
    protected void processTimeout() {
        this.log(this.getTaskName() + ": timeout", 3);
        if (this.timeoutProperty != null) {
            this.getProject().setNewProperty(this.timeoutProperty, "true");
        }
    }
    
    public static class Unit extends EnumeratedAttribute
    {
        public static final String MILLISECOND = "millisecond";
        public static final String SECOND = "second";
        public static final String MINUTE = "minute";
        public static final String HOUR = "hour";
        public static final String DAY = "day";
        public static final String WEEK = "week";
        private static final String[] UNITS;
        private Map timeTable;
        
        public Unit() {
            (this.timeTable = new HashMap()).put("millisecond", new Long(1L));
            this.timeTable.put("second", new Long(1000L));
            this.timeTable.put("minute", new Long(60000L));
            this.timeTable.put("hour", new Long(3600000L));
            this.timeTable.put("day", new Long(86400000L));
            this.timeTable.put("week", new Long(604800000L));
        }
        
        public long getMultiplier() {
            final String key = this.getValue().toLowerCase(Locale.ENGLISH);
            final Long l = this.timeTable.get(key);
            return l;
        }
        
        @Override
        public String[] getValues() {
            return Unit.UNITS;
        }
        
        static {
            UNITS = new String[] { "millisecond", "second", "minute", "hour", "day", "week" };
        }
    }
}
