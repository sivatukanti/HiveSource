// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.property;

import java.util.Properties;
import org.apache.derby.iapi.error.StandardException;
import java.io.Serializable;

public interface PersistentSet
{
    Serializable getProperty(final String p0) throws StandardException;
    
    Serializable getPropertyDefault(final String p0) throws StandardException;
    
    boolean propertyDefaultIsVisible(final String p0) throws StandardException;
    
    void setProperty(final String p0, final Serializable p1, final boolean p2) throws StandardException;
    
    void setPropertyDefault(final String p0, final Serializable p1) throws StandardException;
    
    Properties getProperties() throws StandardException;
}
