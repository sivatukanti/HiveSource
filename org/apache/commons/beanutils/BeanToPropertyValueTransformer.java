// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.beanutils;

import java.lang.reflect.InvocationTargetException;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.collections.Transformer;

public class BeanToPropertyValueTransformer implements Transformer
{
    private final Log log;
    private String propertyName;
    private boolean ignoreNull;
    
    public BeanToPropertyValueTransformer(final String propertyName) {
        this(propertyName, false);
    }
    
    public BeanToPropertyValueTransformer(final String propertyName, final boolean ignoreNull) {
        this.log = LogFactory.getLog(this.getClass());
        if (propertyName != null && propertyName.length() > 0) {
            this.propertyName = propertyName;
            this.ignoreNull = ignoreNull;
            return;
        }
        throw new IllegalArgumentException("propertyName cannot be null or empty");
    }
    
    @Override
    public Object transform(final Object object) {
        Object propertyValue = null;
        try {
            propertyValue = PropertyUtils.getProperty(object, this.propertyName);
        }
        catch (IllegalArgumentException e) {
            final String errorMsg = "Problem during transformation. Null value encountered in property path...";
            if (!this.ignoreNull) {
                final IllegalArgumentException iae = new IllegalArgumentException("Problem during transformation. Null value encountered in property path...");
                if (!BeanUtils.initCause(iae, e)) {
                    this.log.error("Problem during transformation. Null value encountered in property path...", e);
                }
                throw iae;
            }
            this.log.warn("WARNING: Problem during transformation. Null value encountered in property path..." + e);
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
            final String errorMsg = "No property found for name [" + this.propertyName + "]";
            final IllegalArgumentException iae = new IllegalArgumentException(errorMsg);
            if (!BeanUtils.initCause(iae, e4)) {
                this.log.error(errorMsg, e4);
            }
            throw iae;
        }
        return propertyValue;
    }
    
    public String getPropertyName() {
        return this.propertyName;
    }
    
    public boolean isIgnoreNull() {
        return this.ignoreNull;
    }
}
