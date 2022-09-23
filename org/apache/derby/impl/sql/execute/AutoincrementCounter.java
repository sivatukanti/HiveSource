// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.dictionary.ColumnDescriptor;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;

public class AutoincrementCounter
{
    private Long start;
    private long increment;
    private String identity;
    private long finalValue;
    private String schemaName;
    private String tableName;
    private String columnName;
    private long counter;
    private int columnPosition;
    private boolean initialized;
    
    public AutoincrementCounter(final Long start, final long increment, final long finalValue, final String schemaName, final String tableName, final String columnName, final int columnPosition) {
        this.initialized = false;
        this.increment = increment;
        this.start = start;
        this.initialized = false;
        this.identity = makeIdentity(schemaName, tableName, columnName);
        this.finalValue = finalValue;
        this.schemaName = schemaName;
        this.tableName = tableName;
        this.columnName = columnName;
        this.columnPosition = columnPosition;
    }
    
    public static String makeIdentity(final String str, final String str2, final String str3) {
        return str + "." + str2 + "." + str3;
    }
    
    public static String makeIdentity(final TableDescriptor tableDescriptor, final ColumnDescriptor columnDescriptor) {
        return tableDescriptor.getSchemaName() + "." + tableDescriptor.getName() + "." + columnDescriptor.getColumnName();
    }
    
    public void reset(final boolean b) {
        if (b) {
            this.initialized = false;
        }
        else {
            this.counter = this.finalValue;
            this.initialized = true;
        }
    }
    
    public long update(final long counter) {
        this.counter = counter;
        this.initialized = true;
        return this.counter;
    }
    
    public long update() throws StandardException {
        if (!this.initialized) {
            this.initialized = true;
            if (this.start == null) {
                throw StandardException.newException("42Z25");
            }
            this.counter = this.start;
        }
        else {
            this.counter += this.increment;
        }
        return this.counter;
    }
    
    public Long getCurrentValue() {
        if (!this.initialized) {
            return null;
        }
        return new Long(this.counter);
    }
    
    public String getIdentity() {
        return this.identity;
    }
    
    public void flushToDisk(final TransactionController transactionController, final DataDictionary dataDictionary, final UUID uuid) throws StandardException {
        dataDictionary.setAutoincrementValue(transactionController, uuid, this.columnName, this.counter, true);
    }
    
    public int getColumnPosition() {
        return this.columnPosition;
    }
    
    public Long getStartValue() {
        return this.start;
    }
    
    public String toString() {
        return "counter: " + this.identity + " current: " + this.counter + " start: " + this.start + " increment: " + this.increment + " final: " + this.finalValue;
    }
}
