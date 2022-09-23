// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.access;

import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.services.io.Storable;

public interface StaticCompiledOpenConglomInfo extends Storable
{
    DataValueDescriptor getConglom();
}
