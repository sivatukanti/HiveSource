// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

import org.apache.derby.iapi.services.cache.ClassSize;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import org.apache.derby.iapi.error.StandardException;

public class SQLRef extends DataType implements RefDataValue
{
    protected RowLocation value;
    private static final int BASE_MEMORY_USAGE;
    
    public int estimateMemoryUsage() {
        int base_MEMORY_USAGE = SQLRef.BASE_MEMORY_USAGE;
        if (null != this.value) {
            base_MEMORY_USAGE += this.value.estimateMemoryUsage();
        }
        return base_MEMORY_USAGE;
    }
    
    public String getString() {
        if (this.value != null) {
            return this.value.toString();
        }
        return null;
    }
    
    public Object getObject() {
        return this.value;
    }
    
    protected void setFrom(final DataValueDescriptor dataValueDescriptor) throws StandardException {
        if (dataValueDescriptor.isNull()) {
            this.setToNull();
        }
        else {
            this.value = (RowLocation)dataValueDescriptor.getObject();
        }
    }
    
    public int getLength() {
        return -1;
    }
    
    public String getTypeName() {
        return "REF";
    }
    
    public int getTypeFormatId() {
        return 82;
    }
    
    public boolean isNull() {
        return this.value == null;
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        objectOutput.writeObject(this.value);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        this.value = (RowLocation)objectInput.readObject();
    }
    
    public void restoreToNull() {
        this.value = null;
    }
    
    public boolean compare(final int n, final DataValueDescriptor dataValueDescriptor, final boolean b, final boolean b2) throws StandardException {
        return this.value.compare(n, ((SQLRef)dataValueDescriptor).value, b, b2);
    }
    
    public int compare(final DataValueDescriptor dataValueDescriptor) throws StandardException {
        return this.value.compare(((SQLRef)dataValueDescriptor).value);
    }
    
    public DataValueDescriptor cloneValue(final boolean b) {
        if (this.value == null) {
            return new SQLRef();
        }
        return new SQLRef((RowLocation)this.value.cloneValue(false));
    }
    
    public DataValueDescriptor getNewNull() {
        return new SQLRef();
    }
    
    public void setValueFromResultSet(final ResultSet set, final int n, final boolean b) {
    }
    
    public void setInto(final PreparedStatement preparedStatement, final int n) {
    }
    
    public SQLRef() {
    }
    
    public SQLRef(final RowLocation value) {
        this.value = value;
    }
    
    public void setValue(final RowLocation value) {
        this.value = value;
    }
    
    public String toString() {
        if (this.value == null) {
            return "NULL";
        }
        return this.value.toString();
    }
    
    static {
        BASE_MEMORY_USAGE = ClassSize.estimateBaseFromCatalog(SQLRef.class);
    }
}
