// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.access.conglomerate;

import org.apache.derby.iapi.store.access.StoreCostController;
import org.apache.derby.iapi.store.access.Qualifier;
import org.apache.derby.iapi.store.access.ConglomerateController;
import org.apache.derby.iapi.store.access.RowLocationRetRowSource;
import org.apache.derby.iapi.store.access.DynamicCompiledOpenConglomInfo;
import org.apache.derby.iapi.store.access.StaticCompiledOpenConglomInfo;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.store.raw.ContainerKey;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.store.raw.LockingPolicy;
import org.apache.derby.iapi.store.raw.Transaction;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.services.io.Storable;

public interface Conglomerate extends Storable, DataValueDescriptor
{
    void addColumn(final TransactionManager p0, final int p1, final Storable p2, final int p3) throws StandardException;
    
    void drop(final TransactionManager p0) throws StandardException;
    
    boolean fetchMaxOnBTree(final TransactionManager p0, final Transaction p1, final long p2, final int p3, final int p4, final LockingPolicy p5, final int p6, final FormatableBitSet p7, final DataValueDescriptor[] p8) throws StandardException;
    
    long getContainerid();
    
    ContainerKey getId();
    
    StaticCompiledOpenConglomInfo getStaticCompiledConglomInfo(final TransactionController p0, final long p1) throws StandardException;
    
    DynamicCompiledOpenConglomInfo getDynamicCompiledConglomInfo() throws StandardException;
    
    boolean isTemporary();
    
    long load(final TransactionManager p0, final boolean p1, final RowLocationRetRowSource p2) throws StandardException;
    
    ConglomerateController open(final TransactionManager p0, final Transaction p1, final boolean p2, final int p3, final int p4, final LockingPolicy p5, final StaticCompiledOpenConglomInfo p6, final DynamicCompiledOpenConglomInfo p7) throws StandardException;
    
    ScanManager openScan(final TransactionManager p0, final Transaction p1, final boolean p2, final int p3, final int p4, final LockingPolicy p5, final int p6, final FormatableBitSet p7, final DataValueDescriptor[] p8, final int p9, final Qualifier[][] p10, final DataValueDescriptor[] p11, final int p12, final StaticCompiledOpenConglomInfo p13, final DynamicCompiledOpenConglomInfo p14) throws StandardException;
    
    ScanManager defragmentConglomerate(final TransactionManager p0, final Transaction p1, final boolean p2, final int p3, final int p4, final LockingPolicy p5, final int p6) throws StandardException;
    
    void purgeConglomerate(final TransactionManager p0, final Transaction p1) throws StandardException;
    
    void compressConglomerate(final TransactionManager p0, final Transaction p1) throws StandardException;
    
    StoreCostController openStoreCost(final TransactionManager p0, final Transaction p1) throws StandardException;
}
