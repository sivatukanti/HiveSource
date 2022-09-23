// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.execute;

import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.store.access.Qualifier;
import org.apache.derby.iapi.services.loader.GeneratedMethod;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.ResultSet;
import org.apache.derby.iapi.sql.Activation;

public interface ResultSetFactory
{
    public static final String MODULE = "org.apache.derby.iapi.sql.execute.ResultSetFactory";
    
    ResultSet getDDLResultSet(final Activation p0) throws StandardException;
    
    ResultSet getMiscResultSet(final Activation p0) throws StandardException;
    
    ResultSet getSetTransactionResultSet(final Activation p0) throws StandardException;
    
    ResultSet getInsertResultSet(final NoPutResultSet p0, final GeneratedMethod p1, final GeneratedMethod p2, final int p3) throws StandardException;
    
    ResultSet getInsertVTIResultSet(final NoPutResultSet p0, final NoPutResultSet p1) throws StandardException;
    
    ResultSet getDeleteVTIResultSet(final NoPutResultSet p0) throws StandardException;
    
    ResultSet getDeleteResultSet(final NoPutResultSet p0) throws StandardException;
    
    ResultSet getDeleteCascadeResultSet(final NoPutResultSet p0, final int p1, final ResultSet[] p2, final String p3) throws StandardException;
    
    ResultSet getUpdateResultSet(final NoPutResultSet p0, final GeneratedMethod p1, final GeneratedMethod p2) throws StandardException;
    
    ResultSet getUpdateVTIResultSet(final NoPutResultSet p0) throws StandardException;
    
    ResultSet getDeleteCascadeUpdateResultSet(final NoPutResultSet p0, final GeneratedMethod p1, final GeneratedMethod p2, final int p3, final int p4) throws StandardException;
    
    ResultSet getCallStatementResultSet(final GeneratedMethod p0, final Activation p1) throws StandardException;
    
    NoPutResultSet getProjectRestrictResultSet(final NoPutResultSet p0, final GeneratedMethod p1, final GeneratedMethod p2, final int p3, final GeneratedMethod p4, final int p5, final int p6, final boolean p7, final boolean p8, final double p9, final double p10) throws StandardException;
    
    NoPutResultSet getHashTableResultSet(final NoPutResultSet p0, final GeneratedMethod p1, final Qualifier[][] p2, final GeneratedMethod p3, final int p4, final int p5, final boolean p6, final int p7, final boolean p8, final long p9, final int p10, final float p11, final double p12, final double p13) throws StandardException;
    
    NoPutResultSet getSortResultSet(final NoPutResultSet p0, final boolean p1, final boolean p2, final int p3, final int p4, final int p5, final int p6, final double p7, final double p8) throws StandardException;
    
    NoPutResultSet getScalarAggregateResultSet(final NoPutResultSet p0, final boolean p1, final int p2, final int p3, final int p4, final int p5, final int p6, final boolean p7, final double p8, final double p9) throws StandardException;
    
    NoPutResultSet getDistinctScalarAggregateResultSet(final NoPutResultSet p0, final boolean p1, final int p2, final int p3, final int p4, final int p5, final int p6, final boolean p7, final double p8, final double p9) throws StandardException;
    
    NoPutResultSet getGroupedAggregateResultSet(final NoPutResultSet p0, final boolean p1, final int p2, final int p3, final int p4, final int p5, final int p6, final double p7, final double p8, final boolean p9) throws StandardException;
    
    NoPutResultSet getDistinctGroupedAggregateResultSet(final NoPutResultSet p0, final boolean p1, final int p2, final int p3, final int p4, final int p5, final int p6, final double p7, final double p8, final boolean p9) throws StandardException;
    
    NoPutResultSet getAnyResultSet(final NoPutResultSet p0, final GeneratedMethod p1, final int p2, final int p3, final int p4, final double p5, final double p6) throws StandardException;
    
    NoPutResultSet getOnceResultSet(final NoPutResultSet p0, final GeneratedMethod p1, final int p2, final int p3, final int p4, final int p5, final double p6, final double p7) throws StandardException;
    
    NoPutResultSet getRowResultSet(final Activation p0, final GeneratedMethod p1, final boolean p2, final int p3, final double p4, final double p5) throws StandardException;
    
    NoPutResultSet getVTIResultSet(final Activation p0, final int p1, final int p2, final GeneratedMethod p3, final String p4, final Qualifier[][] p5, final int p6, final boolean p7, final boolean p8, final int p9, final boolean p10, final int p11, final double p12, final double p13, final boolean p14, final int p15, final int p16, final int p17) throws StandardException;
    
    NoPutResultSet getHashScanResultSet(final Activation p0, final long p1, final int p2, final int p3, final int p4, final GeneratedMethod p5, final int p6, final GeneratedMethod p7, final int p8, final boolean p9, final Qualifier[][] p10, final Qualifier[][] p11, final int p12, final float p13, final int p14, final int p15, final String p16, final String p17, final String p18, final boolean p19, final boolean p20, final int p21, final int p22, final int p23, final boolean p24, final int p25, final double p26, final double p27) throws StandardException;
    
    NoPutResultSet getDistinctScanResultSet(final Activation p0, final long p1, final int p2, final int p3, final int p4, final int p5, final String p6, final String p7, final String p8, final boolean p9, final int p10, final int p11, final boolean p12, final int p13, final double p14, final double p15) throws StandardException;
    
    NoPutResultSet getTableScanResultSet(final Activation p0, final long p1, final int p2, final int p3, final int p4, final GeneratedMethod p5, final int p6, final GeneratedMethod p7, final int p8, final boolean p9, final Qualifier[][] p10, final String p11, final String p12, final String p13, final boolean p14, final boolean p15, final int p16, final int p17, final int p18, final boolean p19, final int p20, final boolean p21, final double p22, final double p23) throws StandardException;
    
    NoPutResultSet getBulkTableScanResultSet(final Activation p0, final long p1, final int p2, final int p3, final int p4, final GeneratedMethod p5, final int p6, final GeneratedMethod p7, final int p8, final boolean p9, final Qualifier[][] p10, final String p11, final String p12, final String p13, final boolean p14, final boolean p15, final int p16, final int p17, final int p18, final boolean p19, final int p20, final int p21, final boolean p22, final boolean p23, final double p24, final double p25) throws StandardException;
    
    NoPutResultSet getMultiProbeTableScanResultSet(final Activation p0, final long p1, final int p2, final int p3, final int p4, final GeneratedMethod p5, final int p6, final GeneratedMethod p7, final int p8, final boolean p9, final Qualifier[][] p10, final DataValueDescriptor[] p11, final int p12, final String p13, final String p14, final String p15, final boolean p16, final boolean p17, final int p18, final int p19, final int p20, final boolean p21, final int p22, final boolean p23, final double p24, final double p25) throws StandardException;
    
    NoPutResultSet getIndexRowToBaseRowResultSet(final long p0, final int p1, final NoPutResultSet p2, final int p3, final int p4, final String p5, final int p6, final int p7, final int p8, final int p9, final GeneratedMethod p10, final boolean p11, final double p12, final double p13) throws StandardException;
    
    NoPutResultSet getWindowResultSet(final Activation p0, final NoPutResultSet p1, final int p2, final int p3, final int p4, final GeneratedMethod p5, final double p6, final double p7) throws StandardException;
    
    NoPutResultSet getNestedLoopJoinResultSet(final NoPutResultSet p0, final int p1, final NoPutResultSet p2, final int p3, final GeneratedMethod p4, final int p5, final boolean p6, final boolean p7, final double p8, final double p9, final String p10) throws StandardException;
    
    NoPutResultSet getHashJoinResultSet(final NoPutResultSet p0, final int p1, final NoPutResultSet p2, final int p3, final GeneratedMethod p4, final int p5, final boolean p6, final boolean p7, final double p8, final double p9, final String p10) throws StandardException;
    
    NoPutResultSet getNestedLoopLeftOuterJoinResultSet(final NoPutResultSet p0, final int p1, final NoPutResultSet p2, final int p3, final GeneratedMethod p4, final int p5, final GeneratedMethod p6, final boolean p7, final boolean p8, final boolean p9, final double p10, final double p11, final String p12) throws StandardException;
    
    NoPutResultSet getHashLeftOuterJoinResultSet(final NoPutResultSet p0, final int p1, final NoPutResultSet p2, final int p3, final GeneratedMethod p4, final int p5, final GeneratedMethod p6, final boolean p7, final boolean p8, final boolean p9, final double p10, final double p11, final String p12) throws StandardException;
    
    NoPutResultSet getMaterializedResultSet(final NoPutResultSet p0, final int p1, final double p2, final double p3) throws StandardException;
    
    NoPutResultSet getScrollInsensitiveResultSet(final NoPutResultSet p0, final Activation p1, final int p2, final int p3, final boolean p4, final double p5, final double p6) throws StandardException;
    
    NoPutResultSet getNormalizeResultSet(final NoPutResultSet p0, final int p1, final int p2, final double p3, final double p4, final boolean p5) throws StandardException;
    
    NoPutResultSet getCurrentOfResultSet(final String p0, final Activation p1, final int p2);
    
    NoPutResultSet getUnionResultSet(final NoPutResultSet p0, final NoPutResultSet p1, final int p2, final double p3, final double p4) throws StandardException;
    
    NoPutResultSet getSetOpResultSet(final NoPutResultSet p0, final NoPutResultSet p1, final Activation p2, final int p3, final long p4, final double p5, final int p6, final boolean p7, final int p8, final int p9, final int p10) throws StandardException;
    
    NoPutResultSet getLastIndexKeyResultSet(final Activation p0, final int p1, final int p2, final long p3, final String p4, final String p5, final String p6, final int p7, final int p8, final boolean p9, final int p10, final double p11, final double p12) throws StandardException;
    
    NoPutResultSet getRaDependentTableScanResultSet(final Activation p0, final long p1, final int p2, final int p3, final int p4, final GeneratedMethod p5, final int p6, final GeneratedMethod p7, final int p8, final boolean p9, final Qualifier[][] p10, final String p11, final String p12, final String p13, final boolean p14, final boolean p15, final int p16, final int p17, final int p18, final boolean p19, final int p20, final boolean p21, final double p22, final double p23, final String p24, final long p25, final int p26, final int p27) throws StandardException;
    
    NoPutResultSet getRowCountResultSet(final NoPutResultSet p0, final Activation p1, final int p2, final GeneratedMethod p3, final GeneratedMethod p4, final boolean p5, final double p6, final double p7) throws StandardException;
}
