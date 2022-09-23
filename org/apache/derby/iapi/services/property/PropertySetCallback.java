// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.property;

import org.apache.derby.iapi.services.daemon.Serviceable;
import org.apache.derby.iapi.error.StandardException;
import java.io.Serializable;
import java.util.Dictionary;

public interface PropertySetCallback
{
    void init(final boolean p0, final Dictionary p1);
    
    boolean validate(final String p0, final Serializable p1, final Dictionary p2) throws StandardException;
    
    Serviceable apply(final String p0, final Serializable p1, final Dictionary p2) throws StandardException;
    
    Serializable map(final String p0, final Serializable p1, final Dictionary p2) throws StandardException;
}
