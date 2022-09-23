// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.sql.conn.Authorizer;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.store.access.StaticCompiledOpenConglomInfo;
import org.apache.derby.iapi.store.access.Qualifier;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.ResultSet;
import org.apache.derby.iapi.services.loader.GeneratedMethod;
import org.apache.derby.iapi.sql.execute.NoPutResultSet;
import org.apache.derby.iapi.sql.execute.ResultSetFactory;

public class GenericResultSetFactory implements ResultSetFactory
{
    public ResultSet getInsertResultSet(final NoPutResultSet set, final GeneratedMethod generatedMethod, final GeneratedMethod generatedMethod2, final int n) throws StandardException {
        final Activation activation = set.getActivation();
        getAuthorizer(activation).authorize(activation, 0);
        return new InsertResultSet(set, generatedMethod, generatedMethod2, n, activation);
    }
    
    public ResultSet getInsertVTIResultSet(final NoPutResultSet set, final NoPutResultSet set2) throws StandardException {
        final Activation activation = set.getActivation();
        getAuthorizer(activation).authorize(activation, 0);
        return new InsertVTIResultSet(set, set2, activation);
    }
    
    public ResultSet getDeleteVTIResultSet(final NoPutResultSet set) throws StandardException {
        final Activation activation = set.getActivation();
        getAuthorizer(activation).authorize(activation, 0);
        return new DeleteVTIResultSet(set, activation);
    }
    
    public ResultSet getDeleteResultSet(final NoPutResultSet set) throws StandardException {
        final Activation activation = set.getActivation();
        getAuthorizer(activation).authorize(activation, 0);
        return new DeleteResultSet(set, activation);
    }
    
    public ResultSet getDeleteCascadeResultSet(final NoPutResultSet set, final int n, final ResultSet[] array, final String s) throws StandardException {
        final Activation activation = set.getActivation();
        getAuthorizer(activation).authorize(activation, 0);
        return new DeleteCascadeResultSet(set, activation, n, array, s);
    }
    
    public ResultSet getUpdateResultSet(final NoPutResultSet set, final GeneratedMethod generatedMethod, final GeneratedMethod generatedMethod2) throws StandardException {
        final Activation activation = set.getActivation();
        getAuthorizer(activation).authorize(activation, 0);
        return new UpdateResultSet(set, generatedMethod, generatedMethod2, activation);
    }
    
    public ResultSet getUpdateVTIResultSet(final NoPutResultSet set) throws StandardException {
        final Activation activation = set.getActivation();
        getAuthorizer(activation).authorize(activation, 0);
        return new UpdateVTIResultSet(set, activation);
    }
    
    public ResultSet getDeleteCascadeUpdateResultSet(final NoPutResultSet set, final GeneratedMethod generatedMethod, final GeneratedMethod generatedMethod2, final int n, final int n2) throws StandardException {
        final Activation activation = set.getActivation();
        getAuthorizer(activation).authorize(activation, 0);
        return new UpdateResultSet(set, generatedMethod, generatedMethod2, activation, n, n2);
    }
    
    public ResultSet getCallStatementResultSet(final GeneratedMethod generatedMethod, final Activation activation) throws StandardException {
        getAuthorizer(activation).authorize(activation, 3);
        return new CallStatementResultSet(generatedMethod, activation);
    }
    
    public NoPutResultSet getProjectRestrictResultSet(final NoPutResultSet set, final GeneratedMethod generatedMethod, final GeneratedMethod generatedMethod2, final int n, final GeneratedMethod generatedMethod3, final int n2, final int n3, final boolean b, final boolean b2, final double n4, final double n5) throws StandardException {
        return new ProjectRestrictResultSet(set, set.getActivation(), generatedMethod, generatedMethod2, n, generatedMethod3, n2, n3, b, b2, n4, n5);
    }
    
    public NoPutResultSet getHashTableResultSet(final NoPutResultSet set, final GeneratedMethod generatedMethod, final Qualifier[][] array, final GeneratedMethod generatedMethod2, final int n, final int n2, final boolean b, final int n3, final boolean b2, final long n4, final int n5, final float n6, final double n7, final double n8) throws StandardException {
        return new HashTableResultSet(set, set.getActivation(), generatedMethod, array, generatedMethod2, n, n2, b, n3, b2, n4, n5, n6, true, n7, n8);
    }
    
    public NoPutResultSet getSortResultSet(final NoPutResultSet set, final boolean b, final boolean b2, final int n, final int n2, final int n3, final int n4, final double n5, final double n6) throws StandardException {
        return new SortResultSet(set, b, b2, n, set.getActivation(), n2, n3, n4, n5, n6);
    }
    
    public NoPutResultSet getScalarAggregateResultSet(final NoPutResultSet set, final boolean b, final int n, final int n2, final int n3, final int n4, final int n5, final boolean b2, final double n6, final double n7) throws StandardException {
        return new ScalarAggregateResultSet(set, b, n, set.getActivation(), n3, n5, b2, n6, n7);
    }
    
    public NoPutResultSet getDistinctScalarAggregateResultSet(final NoPutResultSet set, final boolean b, final int n, final int n2, final int n3, final int n4, final int n5, final boolean b2, final double n6, final double n7) throws StandardException {
        return new DistinctScalarAggregateResultSet(set, b, n, n2, set.getActivation(), n3, n4, n5, b2, n6, n7);
    }
    
    public NoPutResultSet getGroupedAggregateResultSet(final NoPutResultSet set, final boolean b, final int n, final int n2, final int n3, final int n4, final int n5, final double n6, final double n7, final boolean b2) throws StandardException {
        return new GroupedAggregateResultSet(set, b, n, n2, set.getActivation(), n3, n4, n5, n6, n7, b2);
    }
    
    public NoPutResultSet getDistinctGroupedAggregateResultSet(final NoPutResultSet set, final boolean b, final int n, final int n2, final int n3, final int n4, final int n5, final double n6, final double n7, final boolean b2) throws StandardException {
        return new DistinctGroupedAggregateResultSet(set, b, n, n2, set.getActivation(), n3, n4, n5, n6, n7, b2);
    }
    
    public NoPutResultSet getAnyResultSet(final NoPutResultSet set, final GeneratedMethod generatedMethod, final int n, final int n2, final int n3, final double n4, final double n5) throws StandardException {
        return new AnyResultSet(set, set.getActivation(), generatedMethod, n, n2, n3, n4, n5);
    }
    
    public NoPutResultSet getOnceResultSet(final NoPutResultSet set, final GeneratedMethod generatedMethod, final int n, final int n2, final int n3, final int n4, final double n5, final double n6) throws StandardException {
        return new OnceResultSet(set, set.getActivation(), generatedMethod, n, n2, n3, n4, n5, n6);
    }
    
    public NoPutResultSet getRowResultSet(final Activation activation, final GeneratedMethod generatedMethod, final boolean b, final int n, final double n2, final double n3) {
        return new RowResultSet(activation, generatedMethod, b, n, n2, n3);
    }
    
    public NoPutResultSet getVTIResultSet(final Activation activation, final int n, final int n2, final GeneratedMethod generatedMethod, final String s, final Qualifier[][] array, final int n3, final boolean b, final boolean b2, final int n4, final boolean b3, final int n5, final double n6, final double n7, final boolean b4, final int n8, final int n9, final int n10) throws StandardException {
        return new VTIResultSet(activation, n, n2, generatedMethod, s, array, n3, b, b2, n4, b3, n5, n6, n7, b4, n8, n9, n10);
    }
    
    public NoPutResultSet getHashScanResultSet(final Activation activation, final long n, final int n2, final int n3, final int n4, final GeneratedMethod generatedMethod, final int n5, final GeneratedMethod generatedMethod2, final int n6, final boolean b, final Qualifier[][] array, final Qualifier[][] array2, final int n7, final float n8, final int n9, final int n10, final String s, final String s2, final String s3, final boolean b2, final boolean b3, final int n11, final int n12, final int n13, final boolean b4, final int n14, final double n15, final double n16) throws StandardException {
        return new HashScanResultSet(n, (StaticCompiledOpenConglomInfo)activation.getPreparedStatement().getSavedObject(n2), activation, n3, n4, generatedMethod, n5, generatedMethod2, n6, b, array, array2, n7, n8, n9, n10, s, s2, s3, b2, b3, n11, n13, b4, n14, true, n15, n16);
    }
    
    public NoPutResultSet getDistinctScanResultSet(final Activation activation, final long n, final int n2, final int n3, final int n4, final int n5, final String s, final String s2, final String s3, final boolean b, final int n6, final int n7, final boolean b2, final int n8, final double n9, final double n10) throws StandardException {
        return new DistinctScanResultSet(n, (StaticCompiledOpenConglomInfo)activation.getPreparedStatement().getSavedObject(n2), activation, n3, n4, n5, s, s2, s3, b, n6, n7, b2, n8, n9, n10);
    }
    
    public NoPutResultSet getTableScanResultSet(final Activation activation, final long n, final int n2, final int n3, final int n4, final GeneratedMethod generatedMethod, final int n5, final GeneratedMethod generatedMethod2, final int n6, final boolean b, final Qualifier[][] array, final String s, final String s2, final String s3, final boolean b2, final boolean b3, final int n7, final int n8, final int n9, final boolean b4, final int n10, final boolean b5, final double n11, final double n12) throws StandardException {
        return new TableScanResultSet(n, (StaticCompiledOpenConglomInfo)activation.getPreparedStatement().getSavedObject(n2), activation, n3, n4, generatedMethod, n5, generatedMethod2, n6, b, array, s, s2, s3, b2, b3, n7, n8, n9, b4, n10, 1, b5, n11, n12);
    }
    
    public NoPutResultSet getBulkTableScanResultSet(final Activation activation, final long n, final int n2, final int n3, final int n4, final GeneratedMethod generatedMethod, final int n5, final GeneratedMethod generatedMethod2, final int n6, final boolean b, final Qualifier[][] array, final String s, final String s2, final String s3, final boolean b2, final boolean b3, final int n7, final int n8, final int n9, final boolean b4, final int n10, final int n11, final boolean b5, final boolean b6, final double n12, final double n13) throws StandardException {
        return new BulkTableScanResultSet(n, (StaticCompiledOpenConglomInfo)activation.getPreparedStatement().getSavedObject(n2), activation, n3, n4, generatedMethod, n5, generatedMethod2, n6, b, array, s, s2, s3, b2, b3, n7, n8, n9, b4, n10, n11, b5, b6, n12, n13);
    }
    
    public NoPutResultSet getMultiProbeTableScanResultSet(final Activation activation, final long n, final int n2, final int n3, final int n4, final GeneratedMethod generatedMethod, final int n5, final GeneratedMethod generatedMethod2, final int n6, final boolean b, final Qualifier[][] array, final DataValueDescriptor[] array2, final int n7, final String s, final String s2, final String s3, final boolean b2, final boolean b3, final int n8, final int n9, final int n10, final boolean b4, final int n11, final boolean b5, final double n12, final double n13) throws StandardException {
        return new MultiProbeTableScanResultSet(n, (StaticCompiledOpenConglomInfo)activation.getPreparedStatement().getSavedObject(n2), activation, n3, n4, generatedMethod, n5, generatedMethod2, n6, b, array, array2, n7, s, s2, s3, b2, b3, n8, n9, n10, b4, n11, b5, n12, n13);
    }
    
    public NoPutResultSet getIndexRowToBaseRowResultSet(final long n, final int n2, final NoPutResultSet set, final int n3, final int n4, final String s, final int n5, final int n6, final int n7, final int n8, final GeneratedMethod generatedMethod, final boolean b, final double n9, final double n10) throws StandardException {
        return new IndexRowToBaseRowResultSet(n, n2, set.getActivation(), set, n3, n4, s, n5, n6, n7, n8, generatedMethod, b, n9, n10);
    }
    
    public NoPutResultSet getWindowResultSet(final Activation activation, final NoPutResultSet set, final int n, final int n2, final int n3, final GeneratedMethod generatedMethod, final double n4, final double n5) throws StandardException {
        return new WindowResultSet(activation, set, n, n2, n3, generatedMethod, n4, n5);
    }
    
    public NoPutResultSet getNestedLoopJoinResultSet(final NoPutResultSet set, final int n, final NoPutResultSet set2, final int n2, final GeneratedMethod generatedMethod, final int n3, final boolean b, final boolean b2, final double n4, final double n5, final String s) throws StandardException {
        return new NestedLoopJoinResultSet(set, n, set2, n2, set.getActivation(), generatedMethod, n3, b, b2, n4, n5, s);
    }
    
    public NoPutResultSet getHashJoinResultSet(final NoPutResultSet set, final int n, final NoPutResultSet set2, final int n2, final GeneratedMethod generatedMethod, final int n3, final boolean b, final boolean b2, final double n4, final double n5, final String s) throws StandardException {
        return new HashJoinResultSet(set, n, set2, n2, set.getActivation(), generatedMethod, n3, b, b2, n4, n5, s);
    }
    
    public NoPutResultSet getNestedLoopLeftOuterJoinResultSet(final NoPutResultSet set, final int n, final NoPutResultSet set2, final int n2, final GeneratedMethod generatedMethod, final int n3, final GeneratedMethod generatedMethod2, final boolean b, final boolean b2, final boolean b3, final double n4, final double n5, final String s) throws StandardException {
        return new NestedLoopLeftOuterJoinResultSet(set, n, set2, n2, set.getActivation(), generatedMethod, n3, generatedMethod2, b, b2, b3, n4, n5, s);
    }
    
    public NoPutResultSet getHashLeftOuterJoinResultSet(final NoPutResultSet set, final int n, final NoPutResultSet set2, final int n2, final GeneratedMethod generatedMethod, final int n3, final GeneratedMethod generatedMethod2, final boolean b, final boolean b2, final boolean b3, final double n4, final double n5, final String s) throws StandardException {
        return new HashLeftOuterJoinResultSet(set, n, set2, n2, set.getActivation(), generatedMethod, n3, generatedMethod2, b, b2, b3, n4, n5, s);
    }
    
    public ResultSet getSetTransactionResultSet(final Activation activation) throws StandardException {
        getAuthorizer(activation).authorize(activation, 2);
        return new SetTransactionResultSet(activation);
    }
    
    public NoPutResultSet getMaterializedResultSet(final NoPutResultSet set, final int n, final double n2, final double n3) throws StandardException {
        return new MaterializedResultSet(set, set.getActivation(), n, n2, n3);
    }
    
    public NoPutResultSet getScrollInsensitiveResultSet(final NoPutResultSet set, final Activation activation, final int n, final int n2, final boolean b, final double n3, final double n4) throws StandardException {
        if (b) {
            return new ScrollInsensitiveResultSet(set, activation, n, n2, n3, n4);
        }
        return set;
    }
    
    public NoPutResultSet getNormalizeResultSet(final NoPutResultSet set, final int n, final int n2, final double n3, final double n4, final boolean b) throws StandardException {
        return new NormalizeResultSet(set, set.getActivation(), n, n2, n3, n4, b);
    }
    
    public NoPutResultSet getCurrentOfResultSet(final String s, final Activation activation, final int n) {
        return new CurrentOfResultSet(s, activation, n);
    }
    
    public ResultSet getDDLResultSet(final Activation activation) throws StandardException {
        getAuthorizer(activation).authorize(activation, 4);
        return this.getMiscResultSet(activation);
    }
    
    public ResultSet getMiscResultSet(final Activation activation) throws StandardException {
        getAuthorizer(activation).authorize(activation, 2);
        return new MiscResultSet(activation);
    }
    
    public NoPutResultSet getUnionResultSet(final NoPutResultSet set, final NoPutResultSet set2, final int n, final double n2, final double n3) throws StandardException {
        return new UnionResultSet(set, set2, set.getActivation(), n, n2, n3);
    }
    
    public NoPutResultSet getSetOpResultSet(final NoPutResultSet set, final NoPutResultSet set2, final Activation activation, final int n, final long n2, final double n3, final int n4, final boolean b, final int n5, final int n6, final int n7) throws StandardException {
        return new SetOpResultSet(set, set2, activation, n, n2, n3, n4, b, n5, n6, n7);
    }
    
    public NoPutResultSet getLastIndexKeyResultSet(final Activation activation, final int n, final int n2, final long n3, final String s, final String s2, final String s3, final int n4, final int n5, final boolean b, final int n6, final double n7, final double n8) throws StandardException {
        return new LastIndexKeyResultSet(activation, n, n2, n3, s, s2, s3, n4, n5, b, n6, n7, n8);
    }
    
    public NoPutResultSet getRaDependentTableScanResultSet(final Activation activation, final long n, final int n2, final int n3, final int n4, final GeneratedMethod generatedMethod, final int n5, final GeneratedMethod generatedMethod2, final int n6, final boolean b, final Qualifier[][] array, final String s, final String s2, final String s3, final boolean b2, final boolean b3, final int n7, final int n8, final int n9, final boolean b4, final int n10, final boolean b5, final double n11, final double n12, final String s4, final long n13, final int n14, final int n15) throws StandardException {
        return new DependentResultSet(n, (StaticCompiledOpenConglomInfo)activation.getPreparedStatement().getSavedObject(n2), activation, n3, n4, generatedMethod, n5, generatedMethod2, n6, b, array, s, s2, s3, b2, b3, n7, n9, b4, n10, 1, b5, n11, n12, s4, n13, n14, n15);
    }
    
    public NoPutResultSet getRowCountResultSet(final NoPutResultSet set, final Activation activation, final int n, final GeneratedMethod generatedMethod, final GeneratedMethod generatedMethod2, final boolean b, final double n2, final double n3) throws StandardException {
        return new RowCountResultSet(set, activation, n, generatedMethod, generatedMethod2, b, n2, n3);
    }
    
    private static Authorizer getAuthorizer(final Activation activation) {
        return activation.getLanguageConnectionContext().getAuthorizer();
    }
}
