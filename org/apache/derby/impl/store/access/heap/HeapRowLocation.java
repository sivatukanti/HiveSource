// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.heap;

import org.apache.derby.impl.store.raw.data.RecordId;
import org.apache.derby.iapi.services.cache.ClassSize;
import org.apache.derby.iapi.services.io.ArrayInputStream;
import java.io.DataInput;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.DataOutput;
import org.apache.derby.iapi.services.io.CompressedNumber;
import java.io.ObjectOutput;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.raw.ContainerHandle;
import org.apache.derby.iapi.types.DataValueDescriptor;
import java.sql.ResultSet;
import org.apache.derby.iapi.store.raw.RecordHandle;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.types.DataType;

public class HeapRowLocation extends DataType implements RowLocation
{
    private long pageno;
    private int recid;
    private RecordHandle rh;
    private static final int BASE_MEMORY_USAGE;
    private static final int RECORD_HANDLE_MEMORY_USAGE;
    
    public int estimateMemoryUsage() {
        int base_MEMORY_USAGE = HeapRowLocation.BASE_MEMORY_USAGE;
        if (null != this.rh) {
            base_MEMORY_USAGE += HeapRowLocation.RECORD_HANDLE_MEMORY_USAGE;
        }
        return base_MEMORY_USAGE;
    }
    
    public String getTypeName() {
        return "RowLocation";
    }
    
    public void setValueFromResultSet(final ResultSet set, final int n, final boolean b) {
    }
    
    public DataValueDescriptor getNewNull() {
        return new HeapRowLocation();
    }
    
    public Object getObject() {
        return null;
    }
    
    public DataValueDescriptor cloneValue(final boolean b) {
        return new HeapRowLocation(this);
    }
    
    public DataValueDescriptor recycle() {
        this.pageno = 0L;
        this.recid = 0;
        this.rh = null;
        return this;
    }
    
    public int getLength() {
        return 10;
    }
    
    public String getString() {
        return this.toString();
    }
    
    public boolean compare(final int n, final DataValueDescriptor dataValueDescriptor, final boolean b, final boolean b2) {
        final int compare = this.compare(dataValueDescriptor);
        switch (n) {
            case 1: {
                return compare < 0;
            }
            case 2: {
                return compare == 0;
            }
            case 3: {
                return compare <= 0;
            }
            default: {
                return false;
            }
        }
    }
    
    public int compare(final DataValueDescriptor dataValueDescriptor) {
        final HeapRowLocation heapRowLocation = (HeapRowLocation)dataValueDescriptor;
        final long pageno = this.pageno;
        final long pageno2 = heapRowLocation.pageno;
        if (pageno < pageno2) {
            return -1;
        }
        if (pageno > pageno2) {
            return 1;
        }
        final int recid = this.recid;
        final int recid2 = heapRowLocation.recid;
        if (recid == recid2) {
            return 0;
        }
        if (recid < recid2) {
            return -1;
        }
        return 1;
    }
    
    HeapRowLocation(final RecordHandle from) {
        this.setFrom(from);
    }
    
    public HeapRowLocation() {
        this.pageno = 0L;
        this.recid = 0;
    }
    
    private HeapRowLocation(final HeapRowLocation heapRowLocation) {
        this.pageno = heapRowLocation.pageno;
        this.recid = heapRowLocation.recid;
        this.rh = heapRowLocation.rh;
    }
    
    public RecordHandle getRecordHandle(final ContainerHandle containerHandle) throws StandardException {
        if (this.rh != null) {
            return this.rh;
        }
        return this.rh = containerHandle.makeRecordHandle(this.pageno, this.recid);
    }
    
    void setFrom(final RecordHandle rh) {
        this.pageno = rh.getPageNumber();
        this.recid = rh.getId();
        this.rh = rh;
    }
    
    public int getTypeFormatId() {
        return 90;
    }
    
    public boolean isNull() {
        return false;
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        CompressedNumber.writeLong(objectOutput, this.pageno);
        CompressedNumber.writeInt(objectOutput, this.recid);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        this.pageno = CompressedNumber.readLong(objectInput);
        this.recid = CompressedNumber.readInt(objectInput);
        this.rh = null;
    }
    
    public void readExternalFromArray(final ArrayInputStream arrayInputStream) throws IOException, ClassNotFoundException {
        this.pageno = arrayInputStream.readCompressedLong();
        this.recid = arrayInputStream.readCompressedInt();
        this.rh = null;
    }
    
    public void restoreToNull() {
    }
    
    protected void setFrom(final DataValueDescriptor dataValueDescriptor) {
        final HeapRowLocation heapRowLocation = (HeapRowLocation)dataValueDescriptor;
        this.pageno = heapRowLocation.pageno;
        this.recid = heapRowLocation.recid;
        this.rh = heapRowLocation.rh;
    }
    
    public boolean equals(final Object o) {
        if (o instanceof HeapRowLocation) {
            final HeapRowLocation heapRowLocation = (HeapRowLocation)o;
            return this.pageno == heapRowLocation.pageno && this.recid == heapRowLocation.recid;
        }
        return false;
    }
    
    public int hashCode() {
        return (int)this.pageno ^ this.recid;
    }
    
    public String toString() {
        return "(" + this.pageno + "," + this.recid + ")";
    }
    
    static {
        BASE_MEMORY_USAGE = ClassSize.estimateBaseFromCatalog(HeapRowLocation.class);
        RECORD_HANDLE_MEMORY_USAGE = ClassSize.estimateBaseFromCatalog(RecordId.class);
    }
}
