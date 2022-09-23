// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.webapp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "YARN", "MapReduce" })
@XmlRootElement(name = "RemoteException")
@XmlAccessorType(XmlAccessType.FIELD)
public class RemoteExceptionData
{
    private String exception;
    private String message;
    private String javaClassName;
    
    public RemoteExceptionData() {
    }
    
    public RemoteExceptionData(final String excep, final String message, final String className) {
        this.exception = excep;
        this.message = message;
        this.javaClassName = className;
    }
    
    public String getException() {
        return this.exception;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public String getJavaClassName() {
        return this.javaClassName;
    }
}
