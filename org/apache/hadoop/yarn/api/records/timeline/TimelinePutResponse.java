// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records.timeline;

import java.util.Collection;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.NONE)
@InterfaceAudience.Public
@InterfaceStability.Unstable
public class TimelinePutResponse
{
    private List<TimelinePutError> errors;
    
    public TimelinePutResponse() {
        this.errors = new ArrayList<TimelinePutError>();
    }
    
    @XmlElement(name = "errors")
    public List<TimelinePutError> getErrors() {
        return this.errors;
    }
    
    public void addError(final TimelinePutError error) {
        this.errors.add(error);
    }
    
    public void addErrors(final List<TimelinePutError> errors) {
        this.errors.addAll(errors);
    }
    
    public void setErrors(final List<TimelinePutError> errors) {
        this.errors.clear();
        this.errors.addAll(errors);
    }
    
    @XmlRootElement(name = "error")
    @XmlAccessorType(XmlAccessType.NONE)
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public static class TimelinePutError
    {
        public static final int NO_START_TIME = 1;
        public static final int IO_EXCEPTION = 2;
        public static final int SYSTEM_FILTER_CONFLICT = 3;
        public static final int ACCESS_DENIED = 4;
        public static final int NO_DOMAIN = 5;
        public static final int FORBIDDEN_RELATION = 6;
        private String entityId;
        private String entityType;
        private int errorCode;
        
        @XmlElement(name = "entity")
        public String getEntityId() {
            return this.entityId;
        }
        
        public void setEntityId(final String entityId) {
            this.entityId = entityId;
        }
        
        @XmlElement(name = "entitytype")
        public String getEntityType() {
            return this.entityType;
        }
        
        public void setEntityType(final String entityType) {
            this.entityType = entityType;
        }
        
        @XmlElement(name = "errorcode")
        public int getErrorCode() {
            return this.errorCode;
        }
        
        public void setErrorCode(final int errorCode) {
            this.errorCode = errorCode;
        }
    }
}
