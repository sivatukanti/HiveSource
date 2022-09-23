// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.access.conglomerate;

import org.apache.derby.iapi.store.raw.ContainerKey;
import org.apache.derby.iapi.error.StandardException;
import java.util.Properties;
import org.apache.derby.iapi.store.access.ColumnOrdering;
import org.apache.derby.iapi.types.DataValueDescriptor;

public interface ConglomerateFactory extends MethodFactory
{
    public static final int HEAP_FACTORY_ID = 0;
    public static final int BTREE_FACTORY_ID = 1;
    
    int getConglomerateFactoryId();
    
    Conglomerate createConglomerate(final TransactionManager p0, final int p1, final long p2, final DataValueDescriptor[] p3, final ColumnOrdering[] p4, final int[] p5, final Properties p6, final int p7) throws StandardException;
    
    Conglomerate readConglomerate(final TransactionManager p0, final ContainerKey p1) throws StandardException;
}
