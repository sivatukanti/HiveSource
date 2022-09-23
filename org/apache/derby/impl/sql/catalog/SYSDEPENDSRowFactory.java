// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.catalog;

import org.apache.derby.iapi.sql.dictionary.SystemColumn;
import org.apache.derby.catalog.DependableFinder;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.types.UserType;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.types.SQLChar;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.dictionary.DependencyDescriptor;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.sql.dictionary.TupleDescriptor;
import org.apache.derby.iapi.types.DataValueFactory;
import org.apache.derby.iapi.sql.execute.ExecutionFactory;
import org.apache.derby.iapi.services.uuid.UUIDFactory;
import org.apache.derby.iapi.sql.dictionary.CatalogRowFactory;

public class SYSDEPENDSRowFactory extends CatalogRowFactory
{
    private static final String TABLENAME_STRING = "SYSDEPENDS";
    protected static final int SYSDEPENDS_COLUMN_COUNT = 4;
    protected static final int SYSDEPENDS_DEPENDENTID = 1;
    protected static final int SYSDEPENDS_DEPENDENTTYPE = 2;
    protected static final int SYSDEPENDS_PROVIDERID = 3;
    protected static final int SYSDEPENDS_PROVIDERTYPE = 4;
    protected static final int SYSDEPENDS_INDEX1_ID = 0;
    protected static final int SYSDEPENDS_INDEX2_ID = 1;
    private static final boolean[] uniqueness;
    private static final int[][] indexColumnPositions;
    private static final String[] uuids;
    
    SYSDEPENDSRowFactory(final UUIDFactory uuidFactory, final ExecutionFactory executionFactory, final DataValueFactory dataValueFactory) {
        super(uuidFactory, executionFactory, dataValueFactory);
        this.initInfo(4, "SYSDEPENDS", SYSDEPENDSRowFactory.indexColumnPositions, SYSDEPENDSRowFactory.uniqueness, SYSDEPENDSRowFactory.uuids);
    }
    
    public ExecRow makeRow(final TupleDescriptor tupleDescriptor, final TupleDescriptor tupleDescriptor2) throws StandardException {
        String string = null;
        Object dependentFinder = null;
        String string2 = null;
        Object providerFinder = null;
        if (tupleDescriptor != null) {
            final DependencyDescriptor dependencyDescriptor = (DependencyDescriptor)tupleDescriptor;
            string = dependencyDescriptor.getUUID().toString();
            dependentFinder = dependencyDescriptor.getDependentFinder();
            if (dependentFinder == null) {
                throw StandardException.newException("XD004.S");
            }
            string2 = dependencyDescriptor.getProviderID().toString();
            providerFinder = dependencyDescriptor.getProviderFinder();
            if (providerFinder == null) {
                throw StandardException.newException("XD004.S");
            }
        }
        final ExecRow valueRow = this.getExecutionFactory().getValueRow(4);
        valueRow.setColumn(1, new SQLChar(string));
        valueRow.setColumn(2, new UserType(dependentFinder));
        valueRow.setColumn(3, new SQLChar(string2));
        valueRow.setColumn(4, new UserType(providerFinder));
        return valueRow;
    }
    
    public TupleDescriptor buildDescriptor(final ExecRow execRow, final TupleDescriptor tupleDescriptor, final DataDictionary dataDictionary) throws StandardException {
        return new DependencyDescriptor(this.getUUIDFactory().recreateUUID(execRow.getColumn(1).getString()), (DependableFinder)execRow.getColumn(2).getObject(), this.getUUIDFactory().recreateUUID(execRow.getColumn(3).getString()), (DependableFinder)execRow.getColumn(4).getObject());
    }
    
    public SystemColumn[] buildColumnList() throws StandardException {
        return new SystemColumn[] { SystemColumnImpl.getUUIDColumn("DEPENDENTID", false), SystemColumnImpl.getJavaColumn("DEPENDENTFINDER", "org.apache.derby.catalog.DependableFinder", false), SystemColumnImpl.getUUIDColumn("PROVIDERID", false), SystemColumnImpl.getJavaColumn("PROVIDERFINDER", "org.apache.derby.catalog.DependableFinder", false) };
    }
    
    static {
        uniqueness = new boolean[] { false, false };
        indexColumnPositions = new int[][] { { 1 }, { 3 } };
        uuids = new String[] { "8000003e-00d0-fd77-3ed8-000a0a0b1900", "80000043-00d0-fd77-3ed8-000a0a0b1900", "80000040-00d0-fd77-3ed8-000a0a0b1900", "80000042-00d0-fd77-3ed8-000a0a0b1900" };
    }
}
