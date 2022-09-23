// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.property;

import org.apache.derby.iapi.store.access.TransactionController;
import java.util.Dictionary;
import java.io.Serializable;
import org.apache.derby.iapi.error.StandardException;
import java.util.Properties;

public interface PropertyFactory
{
    void addPropertySetNotification(final PropertySetCallback p0);
    
    void verifyPropertySet(final Properties p0, final Properties p1) throws StandardException;
    
    void validateSingleProperty(final String p0, final Serializable p1, final Dictionary p2) throws StandardException;
    
    Serializable doValidateApplyAndMap(final TransactionController p0, final String p1, final Serializable p2, final Dictionary p3, final boolean p4) throws StandardException;
    
    Serializable doMap(final String p0, final Serializable p1, final Dictionary p2) throws StandardException;
}
