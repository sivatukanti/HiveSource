// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.raw;

import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.error.StandardException;
import java.util.Properties;

public interface StreamContainerHandle
{
    public static final int TEMPORARY_SEGMENT = -1;
    
    ContainerKey getId();
    
    void getContainerProperties(final Properties p0) throws StandardException;
    
    boolean fetchNext(final DataValueDescriptor[] p0) throws StandardException;
    
    void close();
    
    void removeContainer() throws StandardException;
}
