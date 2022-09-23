// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql;

import java.util.Collections;
import org.apache.derby.iapi.util.ReuseFactory;
import org.apache.derby.iapi.util.StringUtil;
import java.util.HashMap;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import java.util.Map;
import java.sql.ResultSetMetaData;
import org.apache.derby.iapi.sql.ResultColumnDescriptor;
import org.apache.derby.iapi.services.io.Formatable;
import org.apache.derby.iapi.sql.ResultDescription;

public final class GenericResultDescription implements ResultDescription, Formatable
{
    private ResultColumnDescriptor[] columns;
    private String statementType;
    private transient ResultSetMetaData metaData;
    private Map columnNameMap;
    
    public GenericResultDescription() {
    }
    
    public GenericResultDescription(final ResultColumnDescriptor[] columns, final String statementType) {
        this.columns = columns;
        this.statementType = statementType;
    }
    
    public GenericResultDescription(final ResultDescription resultDescription, final int[] array) {
        this.columns = new ResultColumnDescriptor[array.length];
        for (int i = 0; i < array.length; ++i) {
            this.columns[i] = resultDescription.getColumnDescriptor(array[i]);
        }
        this.statementType = resultDescription.getStatementType();
    }
    
    public String getStatementType() {
        return this.statementType;
    }
    
    public int getColumnCount() {
        return (this.columns == null) ? 0 : this.columns.length;
    }
    
    public ResultColumnDescriptor[] getColumnInfo() {
        return this.columns;
    }
    
    public ResultColumnDescriptor getColumnDescriptor(final int n) {
        return this.columns[n - 1];
    }
    
    public ResultDescription truncateColumns(final int n) {
        final ResultColumnDescriptor[] array = new ResultColumnDescriptor[n - 1];
        System.arraycopy(this.columns, 0, array, 0, array.length);
        return new GenericResultDescription(array, this.statementType);
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        int n = (this.columns == null) ? 0 : this.columns.length;
        objectOutput.writeObject(this.statementType);
        objectOutput.writeInt(n);
        while (n-- > 0) {
            if (!(this.columns[n] instanceof GenericColumnDescriptor)) {
                this.columns[n] = new GenericColumnDescriptor(this.columns[n]);
            }
            objectOutput.writeObject(this.columns[n]);
        }
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        this.columns = null;
        this.statementType = (String)objectInput.readObject();
        int int1 = objectInput.readInt();
        if (int1 > 0) {
            this.columns = new GenericColumnDescriptor[int1];
            while (int1-- > 0) {
                this.columns[int1] = (ResultColumnDescriptor)objectInput.readObject();
            }
        }
    }
    
    public int getTypeFormatId() {
        return 228;
    }
    
    public String toString() {
        return "";
    }
    
    public synchronized void setMetaData(final ResultSetMetaData metaData) {
        if (this.metaData == null) {
            this.metaData = metaData;
        }
    }
    
    public synchronized ResultSetMetaData getMetaData() {
        return this.metaData;
    }
    
    public int findColumnInsenstive(final String s) {
        final Map columnNameMap;
        synchronized (this) {
            if (this.columnNameMap == null) {
                final HashMap<String, Integer> m = new HashMap<String, Integer>();
                for (int i = this.getColumnCount(); i >= 1; --i) {
                    m.put(StringUtil.SQLToUpperCase(this.getColumnDescriptor(i).getName()), ReuseFactory.getInteger(i));
                }
                this.columnNameMap = Collections.unmodifiableMap((Map<?, ?>)m);
            }
            columnNameMap = this.columnNameMap;
        }
        Integer n = columnNameMap.get(s);
        if (n == null) {
            n = columnNameMap.get(StringUtil.SQLToUpperCase(s));
        }
        if (n == null) {
            return -1;
        }
        return n;
    }
}
