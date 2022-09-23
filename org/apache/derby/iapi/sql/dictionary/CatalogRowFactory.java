// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.dictionary;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.execute.ExecRow;
import java.util.Properties;
import org.apache.derby.iapi.services.uuid.UUIDFactory;
import org.apache.derby.iapi.sql.execute.ExecutionFactory;
import org.apache.derby.iapi.types.DataValueFactory;
import org.apache.derby.catalog.UUID;

public abstract class CatalogRowFactory
{
    protected String[] indexNames;
    protected int[][] indexColumnPositions;
    protected boolean[] indexUniqueness;
    protected UUID tableUUID;
    protected UUID heapUUID;
    protected UUID[] indexUUID;
    protected DataValueFactory dvf;
    private final ExecutionFactory ef;
    private UUIDFactory uuidf;
    private int indexCount;
    private int columnCount;
    private String catalogName;
    
    public CatalogRowFactory(final UUIDFactory uuidf, final ExecutionFactory ef, final DataValueFactory dvf) {
        this.uuidf = uuidf;
        this.dvf = dvf;
        this.ef = ef;
    }
    
    public ExecutionFactory getExecutionFactory() {
        return this.ef;
    }
    
    public UUIDFactory getUUIDFactory() {
        return this.uuidf;
    }
    
    public UUID getCanonicalTableUUID() {
        return this.tableUUID;
    }
    
    public UUID getCanonicalHeapUUID() {
        return this.heapUUID;
    }
    
    public UUID getCanonicalIndexUUID(final int n) {
        return this.indexUUID[n];
    }
    
    public int getIndexColumnCount(final int n) {
        return this.indexColumnPositions[n].length;
    }
    
    public String getCanonicalHeapName() {
        return this.catalogName + "_HEAP";
    }
    
    public String getIndexName(final int n) {
        return this.indexNames[n];
    }
    
    public boolean isIndexUnique(final int n) {
        return this.indexUniqueness == null || this.indexUniqueness[n];
    }
    
    public DataValueFactory getDataValueFactory() {
        return this.dvf;
    }
    
    public String generateIndexName(int i) {
        ++i;
        return this.catalogName + "_INDEX" + i;
    }
    
    public int getNumIndexes() {
        return this.indexCount;
    }
    
    public String getCatalogName() {
        return this.catalogName;
    }
    
    public void initInfo(final int columnCount, final String catalogName, final int[][] indexColumnPositions, final boolean[] indexUniqueness, final String[] array) {
        this.indexCount = ((indexColumnPositions != null) ? indexColumnPositions.length : 0);
        this.catalogName = catalogName;
        this.columnCount = columnCount;
        final UUIDFactory uuidFactory = this.getUUIDFactory();
        this.tableUUID = uuidFactory.recreateUUID(array[0]);
        this.heapUUID = uuidFactory.recreateUUID(array[1]);
        if (this.indexCount > 0) {
            this.indexNames = new String[this.indexCount];
            this.indexUUID = new UUID[this.indexCount];
            for (int i = 0; i < this.indexCount; ++i) {
                this.indexNames[i] = this.generateIndexName(i);
                this.indexUUID[i] = uuidFactory.recreateUUID(array[i + 2]);
            }
            this.indexColumnPositions = indexColumnPositions;
            this.indexUniqueness = indexUniqueness;
        }
    }
    
    public Properties getCreateHeapProperties() {
        final Properties properties = new Properties();
        properties.put("derby.storage.pageSize", "1024");
        properties.put("derby.storage.pageReservedSpace", "0");
        properties.put("derby.storage.minimumRecordSize", "1");
        return properties;
    }
    
    public Properties getCreateIndexProperties(final int n) {
        final Properties properties = new Properties();
        properties.put("derby.storage.pageSize", "1024");
        return properties;
    }
    
    public int getPrimaryKeyIndexNumber() {
        return 0;
    }
    
    public final int getHeapColumnCount() {
        return this.columnCount;
    }
    
    public ExecRow makeEmptyRow() throws StandardException {
        return this.makeRow(null, null);
    }
    
    public ExecRow makeRow(final TupleDescriptor tupleDescriptor, final TupleDescriptor tupleDescriptor2) throws StandardException {
        return null;
    }
    
    public abstract TupleDescriptor buildDescriptor(final ExecRow p0, final TupleDescriptor p1, final DataDictionary p2) throws StandardException;
    
    public abstract SystemColumn[] buildColumnList() throws StandardException;
    
    public int[] getIndexColumnPositions(final int n) {
        return this.indexColumnPositions[n];
    }
}
