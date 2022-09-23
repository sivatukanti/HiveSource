// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.access;

import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.error.StandardException;

public interface StoreCostController extends RowCountable
{
    public static final double BASE_CACHED_ROW_FETCH_COST = 0.17;
    public static final double BASE_UNCACHED_ROW_FETCH_COST = 1.5;
    public static final double BASE_GROUPSCAN_ROW_COST = 0.12;
    public static final double BASE_NONGROUPSCAN_ROW_FETCH_COST = 0.25;
    public static final double BASE_HASHSCAN_ROW_FETCH_COST = 0.14;
    public static final double BASE_ROW_PER_BYTECOST = 0.004;
    public static final int STORECOST_CLUSTERED = 1;
    public static final int STORECOST_SCAN_SET = 1;
    public static final int STORECOST_SCAN_NORMAL = 2;
    
    void close() throws StandardException;
    
    double getFetchFromRowLocationCost(final FormatableBitSet p0, final int p1) throws StandardException;
    
    double getFetchFromFullKeyCost(final FormatableBitSet p0, final int p1) throws StandardException;
    
    void getScanCost(final int p0, final long p1, final int p2, final boolean p3, final FormatableBitSet p4, final DataValueDescriptor[] p5, final DataValueDescriptor[] p6, final int p7, final DataValueDescriptor[] p8, final int p9, final boolean p10, final int p11, final StoreCostResult p12) throws StandardException;
    
    RowLocation newRowLocationTemplate() throws StandardException;
}
