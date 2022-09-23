// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional;

import java.util.HashMap;
import java.util.Map;
import org.apache.tools.ant.types.EnumeratedAttribute;
import java.text.DecimalFormat;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.apache.tools.ant.util.FileUtils;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import org.apache.tools.ant.util.LayoutPreservingProperties;
import java.util.Enumeration;
import org.apache.tools.ant.BuildException;
import java.util.Vector;
import java.io.File;
import java.util.Properties;
import org.apache.tools.ant.Task;

public class PropertyFile extends Task
{
    private String comment;
    private Properties properties;
    private File propertyfile;
    private boolean useJDKProperties;
    private Vector entries;
    
    public PropertyFile() {
        this.entries = new Vector();
    }
    
    @Override
    public void execute() throws BuildException {
        this.checkParameters();
        this.readFile();
        this.executeOperation();
        this.writeFile();
    }
    
    public Entry createEntry() {
        final Entry e = new Entry();
        this.entries.addElement(e);
        return e;
    }
    
    private void executeOperation() throws BuildException {
        final Enumeration e = this.entries.elements();
        while (e.hasMoreElements()) {
            final Entry entry = e.nextElement();
            entry.executeOn(this.properties);
        }
    }
    
    private void readFile() throws BuildException {
        if (this.useJDKProperties) {
            this.properties = new Properties();
        }
        else {
            this.properties = new LayoutPreservingProperties();
        }
        try {
            if (this.propertyfile.exists()) {
                this.log("Updating property file: " + this.propertyfile.getAbsolutePath());
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(this.propertyfile);
                    final BufferedInputStream bis = new BufferedInputStream(fis);
                    this.properties.load(bis);
                }
                finally {
                    if (fis != null) {
                        fis.close();
                    }
                }
            }
            else {
                this.log("Creating new property file: " + this.propertyfile.getAbsolutePath());
                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(this.propertyfile.getAbsolutePath());
                    out.flush();
                }
                finally {
                    if (out != null) {
                        out.close();
                    }
                }
            }
        }
        catch (IOException ioe) {
            throw new BuildException(ioe.toString());
        }
    }
    
    private void checkParameters() throws BuildException {
        if (!this.checkParam(this.propertyfile)) {
            throw new BuildException("file token must not be null.", this.getLocation());
        }
    }
    
    public void setFile(final File file) {
        this.propertyfile = file;
    }
    
    public void setComment(final String hdr) {
        this.comment = hdr;
    }
    
    public void setJDKProperties(final boolean val) {
        this.useJDKProperties = val;
    }
    
    private void writeFile() throws BuildException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            this.properties.store(baos, this.comment);
        }
        catch (IOException x) {
            throw new BuildException(x, this.getLocation());
        }
        try {
            final OutputStream os = new FileOutputStream(this.propertyfile);
            try {
                try {
                    os.write(baos.toByteArray());
                }
                finally {
                    os.close();
                }
            }
            catch (IOException x2) {
                FileUtils.getFileUtils().tryHardToDelete(this.propertyfile);
                throw x2;
            }
        }
        catch (IOException x) {
            throw new BuildException(x, this.getLocation());
        }
    }
    
    private boolean checkParam(final File param) {
        return param != null;
    }
    
    public static class Entry
    {
        private static final int DEFAULT_INT_VALUE = 0;
        private static final String DEFAULT_DATE_VALUE = "now";
        private static final String DEFAULT_STRING_VALUE = "";
        private String key;
        private int type;
        private int operation;
        private String value;
        private String defaultValue;
        private String newValue;
        private String pattern;
        private int field;
        
        public Entry() {
            this.key = null;
            this.type = 2;
            this.operation = 2;
            this.value = null;
            this.defaultValue = null;
            this.newValue = null;
            this.pattern = null;
            this.field = 5;
        }
        
        public void setKey(final String value) {
            this.key = value;
        }
        
        public void setValue(final String value) {
            this.value = value;
        }
        
        public void setOperation(final Operation value) {
            this.operation = Operation.toOperation(value.getValue());
        }
        
        public void setType(final Type value) {
            this.type = Type.toType(value.getValue());
        }
        
        public void setDefault(final String value) {
            this.defaultValue = value;
        }
        
        public void setPattern(final String value) {
            this.pattern = value;
        }
        
        public void setUnit(final Unit unit) {
            this.field = unit.getCalendarField();
        }
        
        protected void executeOn(final Properties props) throws BuildException {
            this.checkParameters();
            if (this.operation == 3) {
                props.remove(this.key);
                return;
            }
            final String oldValue = (String)props.get(this.key);
            try {
                if (this.type == 0) {
                    this.executeInteger(oldValue);
                }
                else if (this.type == 1) {
                    this.executeDate(oldValue);
                }
                else {
                    if (this.type != 2) {
                        throw new BuildException("Unknown operation type: " + this.type);
                    }
                    this.executeString(oldValue);
                }
            }
            catch (NullPointerException npe) {
                npe.printStackTrace();
            }
            if (this.newValue == null) {
                this.newValue = "";
            }
            props.put(this.key, this.newValue);
        }
        
        private void executeDate(final String oldValue) throws BuildException {
            final Calendar currentValue = Calendar.getInstance();
            if (this.pattern == null) {
                this.pattern = "yyyy/MM/dd HH:mm";
            }
            final DateFormat fmt = new SimpleDateFormat(this.pattern);
            String currentStringValue = this.getCurrentValue(oldValue);
            if (currentStringValue == null) {
                currentStringValue = "now";
            }
            if ("now".equals(currentStringValue)) {
                currentValue.setTime(new Date());
            }
            else {
                try {
                    currentValue.setTime(fmt.parse(currentStringValue));
                }
                catch (ParseException ex) {}
            }
            if (this.operation != 2) {
                int offset = 0;
                try {
                    offset = Integer.parseInt(this.value);
                    if (this.operation == 1) {
                        offset *= -1;
                    }
                }
                catch (NumberFormatException e) {
                    throw new BuildException("Value not an integer on " + this.key);
                }
                currentValue.add(this.field, offset);
            }
            this.newValue = fmt.format(currentValue.getTime());
        }
        
        private void executeInteger(final String oldValue) throws BuildException {
            int currentValue = 0;
            int newV = 0;
            final DecimalFormat fmt = (this.pattern != null) ? new DecimalFormat(this.pattern) : new DecimalFormat();
            try {
                final String curval = this.getCurrentValue(oldValue);
                if (curval != null) {
                    currentValue = fmt.parse(curval).intValue();
                }
                else {
                    currentValue = 0;
                }
            }
            catch (NumberFormatException nfe) {}
            catch (ParseException ex) {}
            if (this.operation == 2) {
                newV = currentValue;
            }
            else {
                int operationValue = 1;
                if (this.value != null) {
                    try {
                        operationValue = fmt.parse(this.value).intValue();
                    }
                    catch (NumberFormatException nfe2) {}
                    catch (ParseException ex2) {}
                }
                if (this.operation == 0) {
                    newV = currentValue + operationValue;
                }
                else if (this.operation == 1) {
                    newV = currentValue - operationValue;
                }
            }
            this.newValue = fmt.format(newV);
        }
        
        private void executeString(final String oldValue) throws BuildException {
            String newV = "";
            String currentValue = this.getCurrentValue(oldValue);
            if (currentValue == null) {
                currentValue = "";
            }
            if (this.operation == 2) {
                newV = currentValue;
            }
            else if (this.operation == 0) {
                newV = currentValue + this.value;
            }
            this.newValue = newV;
        }
        
        private void checkParameters() throws BuildException {
            if (this.type == 2 && this.operation == 1) {
                throw new BuildException("- is not supported for string properties (key:" + this.key + ")");
            }
            if (this.value == null && this.defaultValue == null && this.operation != 3) {
                throw new BuildException("\"value\" and/or \"default\" attribute must be specified (key:" + this.key + ")");
            }
            if (this.key == null) {
                throw new BuildException("key is mandatory");
            }
            if (this.type == 2 && this.pattern != null) {
                throw new BuildException("pattern is not supported for string properties (key:" + this.key + ")");
            }
        }
        
        private String getCurrentValue(final String oldValue) {
            String ret = null;
            if (this.operation == 2) {
                if (this.value != null && this.defaultValue == null) {
                    ret = this.value;
                }
                if (this.value == null && this.defaultValue != null && oldValue != null) {
                    ret = oldValue;
                }
                if (this.value == null && this.defaultValue != null && oldValue == null) {
                    ret = this.defaultValue;
                }
                if (this.value != null && this.defaultValue != null && oldValue != null) {
                    ret = this.value;
                }
                if (this.value != null && this.defaultValue != null && oldValue == null) {
                    ret = this.defaultValue;
                }
            }
            else {
                ret = ((oldValue == null) ? this.defaultValue : oldValue);
            }
            return ret;
        }
        
        public static class Operation extends EnumeratedAttribute
        {
            public static final int INCREMENT_OPER = 0;
            public static final int DECREMENT_OPER = 1;
            public static final int EQUALS_OPER = 2;
            public static final int DELETE_OPER = 3;
            
            @Override
            public String[] getValues() {
                return new String[] { "+", "-", "=", "del" };
            }
            
            public static int toOperation(final String oper) {
                if ("+".equals(oper)) {
                    return 0;
                }
                if ("-".equals(oper)) {
                    return 1;
                }
                if ("del".equals(oper)) {
                    return 3;
                }
                return 2;
            }
        }
        
        public static class Type extends EnumeratedAttribute
        {
            public static final int INTEGER_TYPE = 0;
            public static final int DATE_TYPE = 1;
            public static final int STRING_TYPE = 2;
            
            @Override
            public String[] getValues() {
                return new String[] { "int", "date", "string" };
            }
            
            public static int toType(final String type) {
                if ("int".equals(type)) {
                    return 0;
                }
                if ("date".equals(type)) {
                    return 1;
                }
                return 2;
            }
        }
    }
    
    public static class Unit extends EnumeratedAttribute
    {
        private static final String MILLISECOND = "millisecond";
        private static final String SECOND = "second";
        private static final String MINUTE = "minute";
        private static final String HOUR = "hour";
        private static final String DAY = "day";
        private static final String WEEK = "week";
        private static final String MONTH = "month";
        private static final String YEAR = "year";
        private static final String[] UNITS;
        private Map calendarFields;
        
        public Unit() {
            (this.calendarFields = new HashMap()).put("millisecond", new Integer(14));
            this.calendarFields.put("second", new Integer(13));
            this.calendarFields.put("minute", new Integer(12));
            this.calendarFields.put("hour", new Integer(11));
            this.calendarFields.put("day", new Integer(5));
            this.calendarFields.put("week", new Integer(3));
            this.calendarFields.put("month", new Integer(2));
            this.calendarFields.put("year", new Integer(1));
        }
        
        public int getCalendarField() {
            final String key = this.getValue().toLowerCase();
            final Integer i = this.calendarFields.get(key);
            return i;
        }
        
        @Override
        public String[] getValues() {
            return Unit.UNITS;
        }
        
        static {
            UNITS = new String[] { "millisecond", "second", "minute", "hour", "day", "week", "month", "year" };
        }
    }
}
