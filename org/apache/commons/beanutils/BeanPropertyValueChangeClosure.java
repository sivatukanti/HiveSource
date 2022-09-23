// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.beanutils;

import java.lang.reflect.InvocationTargetException;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.collections.Closure;

public class BeanPropertyValueChangeClosure implements Closure
{
    private final Log log;
    private String propertyName;
    private Object propertyValue;
    private boolean ignoreNull;
    
    public BeanPropertyValueChangeClosure(final String propertyName, final Object propertyValue) {
        this(propertyName, propertyValue, false);
    }
    
    public BeanPropertyValueChangeClosure(final String propertyName, final Object propertyValue, final boolean ignoreNull) {
        this.log = LogFactory.getLog(this.getClass());
        if (propertyName != null && propertyName.length() > 0) {
            this.propertyName = propertyName;
            this.propertyValue = propertyValue;
            this.ignoreNull = ignoreNull;
            return;
        }
        throw new IllegalArgumentException("propertyName cannot be null or empty");
    }
    
    @Override
    public void execute(final Object object) {
        try {
            PropertyUtils.setProperty(object, this.propertyName, this.propertyValue);
        }
        catch (IllegalArgumentException e) {
            final String errorMsg = "Unable to execute Closure. Null value encountered in property path...";
            if (!this.ignoreNull) {
                final IllegalArgumentException iae = new IllegalArgumentException("Unable to execute Closure. Null value encountered in property path...");
                if (!BeanUtils.initCause(iae, e)) {
                    this.log.error("Unable to execute Closure. Null value encountered in property path...", e);
                }
                throw iae;
            }
            this.log.warn("WARNING: Unable to execute Closure. Null value encountered in property path..." + e);
        }
        catch (IllegalAccessException e2) {
            final String errorMsg = "Unable to access the property provided.";
            final IllegalArgumentException iae = new IllegalArgumentException("Unable to access the property provided.");
            if (!BeanUtils.initCause(iae, e2)) {
                this.log.error("Unable to access the property provided.", e2);
            }
            throw iae;
        }
        catch (InvocationTargetException e3) {
            final String errorMsg = "Exception occurred in property's getter";
            final IllegalArgumentException iae = new IllegalArgumentException("Exception occurred in property's getter");
            if (!BeanUtils.initCause(iae, e3)) {
                this.log.error("Exception occurred in property's getter", e3);
            }
            throw iae;
        }
        catch (NoSuchMethodException e4) {
            final String errorMsg = "Property not found";
            final IllegalArgumentException iae = new IllegalArgumentException("Property not found");
            if (!BeanUtils.initCause(iae, e4)) {
                this.log.error("Property not found", e4);
            }
            throw iae;
        }
    }
    
    public String getPropertyName() {
        return this.propertyName;
    }
    
    public Object getPropertyValue() {
        return this.propertyValue;
    }
    
    public boolean isIgnoreNull() {
        return this.ignoreNull;
    }
}
