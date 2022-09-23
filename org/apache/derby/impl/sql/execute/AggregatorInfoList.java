// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import org.apache.derby.iapi.services.io.Formatable;
import java.util.Vector;

public class AggregatorInfoList extends Vector implements Formatable
{
    public boolean hasDistinct() {
        for (int size = this.size(), i = 0; i < size; ++i) {
            if (((AggregatorInfo)this.elementAt(i)).isDistinct()) {
                return true;
            }
        }
        return false;
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        final int size = this.size();
        objectOutput.writeInt(size);
        for (int i = 0; i < size; ++i) {
            objectOutput.writeObject(this.elementAt(i));
        }
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        final int int1 = objectInput.readInt();
        this.ensureCapacity(int1);
        for (int i = 0; i < int1; ++i) {
            this.addElement((AggregatorInfo)objectInput.readObject());
        }
    }
    
    public int getTypeFormatId() {
        return 224;
    }
}
