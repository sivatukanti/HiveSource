// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.conf;

public class ReconfigurationException extends Exception
{
    private static final long serialVersionUID = 1L;
    private String property;
    private String newVal;
    private String oldVal;
    
    private static String constructMessage(final String property, final String newVal, final String oldVal) {
        String message = "Could not change property " + property;
        if (oldVal != null) {
            message = message + " from '" + oldVal;
        }
        if (newVal != null) {
            message = message + "' to '" + newVal + "'";
        }
        return message;
    }
    
    public ReconfigurationException() {
        super("Could not change configuration.");
        this.property = null;
        this.newVal = null;
        this.oldVal = null;
    }
    
    public ReconfigurationException(final String property, final String newVal, final String oldVal, final Throwable cause) {
        super(constructMessage(property, newVal, oldVal), cause);
        this.property = property;
        this.newVal = newVal;
        this.oldVal = oldVal;
    }
    
    public ReconfigurationException(final String property, final String newVal, final String oldVal) {
        super(constructMessage(property, newVal, oldVal));
        this.property = property;
        this.newVal = newVal;
        this.oldVal = oldVal;
    }
    
    public String getProperty() {
        return this.property;
    }
    
    public String getNewValue() {
        return this.newVal;
    }
    
    public String getOldValue() {
        return this.oldVal;
    }
}
