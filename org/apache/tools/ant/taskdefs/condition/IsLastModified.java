// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.condition;

import org.apache.tools.ant.types.EnumeratedAttribute;
import java.util.Date;
import java.text.ParseException;
import org.apache.tools.ant.BuildException;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.taskdefs.Touch;
import org.apache.tools.ant.ProjectComponent;

public class IsLastModified extends ProjectComponent implements Condition
{
    private long millis;
    private String dateTime;
    private Touch.DateFormatFactory dfFactory;
    private Resource resource;
    private CompareMode mode;
    
    public IsLastModified() {
        this.millis = -1L;
        this.dateTime = null;
        this.dfFactory = Touch.DEFAULT_DF_FACTORY;
        this.mode = CompareMode.EQUALS;
    }
    
    public void setMillis(final long millis) {
        this.millis = millis;
    }
    
    public void setDatetime(final String dateTime) {
        this.dateTime = dateTime;
    }
    
    public void setPattern(final String pattern) {
        this.dfFactory = new Touch.DateFormatFactory() {
            public DateFormat getPrimaryFormat() {
                return new SimpleDateFormat(pattern);
            }
            
            public DateFormat getFallbackFormat() {
                return null;
            }
        };
    }
    
    public void add(final Resource r) {
        if (this.resource != null) {
            throw new BuildException("only one resource can be tested");
        }
        this.resource = r;
    }
    
    public void setMode(final CompareMode mode) {
        this.mode = mode;
    }
    
    protected void validate() throws BuildException {
        if (this.millis >= 0L && this.dateTime != null) {
            throw new BuildException("Only one of dateTime and millis can be set");
        }
        if (this.millis < 0L && this.dateTime == null) {
            throw new BuildException("millis or dateTime is required");
        }
        if (this.resource == null) {
            throw new BuildException("resource is required");
        }
    }
    
    protected long getMillis() throws BuildException {
        if (this.millis >= 0L) {
            return this.millis;
        }
        if ("now".equalsIgnoreCase(this.dateTime)) {
            return System.currentTimeMillis();
        }
        DateFormat df = this.dfFactory.getPrimaryFormat();
        ParseException pe = null;
        try {
            return df.parse(this.dateTime).getTime();
        }
        catch (ParseException peOne) {
            df = this.dfFactory.getFallbackFormat();
            if (df == null) {
                pe = peOne;
            }
            else {
                try {
                    return df.parse(this.dateTime).getTime();
                }
                catch (ParseException peTwo) {
                    pe = peTwo;
                }
            }
            if (pe != null) {
                throw new BuildException(pe.getMessage(), pe, this.getLocation());
            }
            return 0L;
        }
    }
    
    public boolean eval() throws BuildException {
        this.validate();
        final long expected = this.getMillis();
        final long actual = this.resource.getLastModified();
        this.log("expected timestamp: " + expected + " (" + new Date(expected) + ")" + ", actual timestamp: " + actual + " (" + new Date(actual) + ")", 3);
        if ("equals".equals(this.mode.getValue())) {
            return expected == actual;
        }
        if ("before".equals(this.mode.getValue())) {
            return expected > actual;
        }
        if ("not-before".equals(this.mode.getValue())) {
            return expected <= actual;
        }
        if ("after".equals(this.mode.getValue())) {
            return expected < actual;
        }
        if ("not-after".equals(this.mode.getValue())) {
            return expected >= actual;
        }
        throw new BuildException("Unknown mode " + this.mode.getValue());
    }
    
    public static class CompareMode extends EnumeratedAttribute
    {
        private static final String EQUALS_TEXT = "equals";
        private static final String BEFORE_TEXT = "before";
        private static final String AFTER_TEXT = "after";
        private static final String NOT_BEFORE_TEXT = "not-before";
        private static final String NOT_AFTER_TEXT = "not-after";
        private static final CompareMode EQUALS;
        
        public CompareMode() {
            this("equals");
        }
        
        public CompareMode(final String s) {
            this.setValue(s);
        }
        
        @Override
        public String[] getValues() {
            return new String[] { "equals", "before", "after", "not-before", "not-after" };
        }
        
        static {
            EQUALS = new CompareMode("equals");
        }
    }
}
