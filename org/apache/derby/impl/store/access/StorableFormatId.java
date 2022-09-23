// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access;

import org.apache.derby.iapi.services.cache.ClassSize;
import java.sql.SQLException;
import java.sql.ResultSet;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.error.StandardException;
import java.io.DataInput;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.DataOutput;
import org.apache.derby.iapi.services.io.FormatIdUtil;
import java.io.ObjectOutput;
import org.apache.derby.iapi.types.DataType;

public class StorableFormatId extends DataType
{
    private int format_id;
    private static final int BASE_MEMORY_USAGE;
    
    public int estimateMemoryUsage() {
        return StorableFormatId.BASE_MEMORY_USAGE;
    }
    
    public StorableFormatId() {
    }
    
    public StorableFormatId(final int format_id) {
        this.format_id = format_id;
    }
    
    public int getValue() {
        return this.format_id;
    }
    
    public void setValue(final int format_id) {
        this.format_id = format_id;
    }
    
    public int getTypeFormatId() {
        return 93;
    }
    
    public boolean isNull() {
        return false;
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        FormatIdUtil.writeFormatIdInteger(objectOutput, this.format_id);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException {
        this.format_id = FormatIdUtil.readFormatIdInteger(objectInput);
    }
    
    public void restoreToNull() {
        this.format_id = 0;
    }
    
    public int getLength() throws StandardException {
        throw StandardException.newException("XSCH8.S");
    }
    
    public String getString() throws StandardException {
        throw StandardException.newException("XSCH8.S");
    }
    
    public Object getObject() throws StandardException {
        return this;
    }
    
    public DataValueDescriptor cloneValue(final boolean b) {
        return null;
    }
    
    public DataValueDescriptor getNewNull() {
        return null;
    }
    
    public void setValueFromResultSet(final ResultSet set, final int n, final boolean b) throws StandardException, SQLException {
        throw StandardException.newException("XSCH8.S");
    }
    
    protected void setFrom(final DataValueDescriptor dataValueDescriptor) throws StandardException {
        throw StandardException.newException("XSCH8.S");
    }
    
    public String getTypeName() {
        return null;
    }
    
    public int compare(final DataValueDescriptor dataValueDescriptor) throws StandardException {
        throw StandardException.newException("XSCH8.S");
    }
    
    static {
        BASE_MEMORY_USAGE = ClassSize.estimateBaseFromCatalog(StorableFormatId.class);
    }
}
