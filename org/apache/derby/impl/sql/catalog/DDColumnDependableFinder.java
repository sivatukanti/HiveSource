// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.catalog;

import java.io.ObjectOutput;
import java.io.IOException;
import org.apache.derby.iapi.services.io.FormatableHashtable;
import java.io.ObjectInput;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.catalog.Dependable;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;

public class DDColumnDependableFinder extends DDdependableFinder
{
    private byte[] columnBitMap;
    
    public DDColumnDependableFinder(final int n) {
        super(n);
    }
    
    public DDColumnDependableFinder(final int n, final byte[] columnBitMap) {
        super(n);
        this.columnBitMap = columnBitMap;
    }
    
    public byte[] getColumnBitMap() {
        return this.columnBitMap;
    }
    
    public void setColumnBitMap(final byte[] columnBitMap) {
        this.columnBitMap = columnBitMap;
    }
    
    Dependable findDependable(final DataDictionary dataDictionary, final UUID uuid) throws StandardException {
        final TableDescriptor tableDescriptor = dataDictionary.getTableDescriptor(uuid);
        if (tableDescriptor != null) {
            tableDescriptor.setReferencedColumnMap(new FormatableBitSet(this.columnBitMap));
        }
        return tableDescriptor;
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        super.readExternal(objectInput);
        this.columnBitMap = ((FormatableHashtable)objectInput.readObject()).get("columnBitMap");
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        super.writeExternal(objectOutput);
        final FormatableHashtable formatableHashtable = new FormatableHashtable();
        formatableHashtable.put("columnBitMap", this.columnBitMap);
        objectOutput.writeObject(formatableHashtable);
    }
}
