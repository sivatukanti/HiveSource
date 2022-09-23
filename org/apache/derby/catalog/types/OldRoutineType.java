// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.catalog.types;

import java.io.ObjectOutput;
import java.io.IOException;
import java.io.DataInput;
import org.apache.derby.iapi.services.io.FormatIdUtil;
import java.io.ObjectInput;
import org.apache.derby.catalog.TypeDescriptor;
import org.apache.derby.iapi.services.io.Formatable;

final class OldRoutineType implements Formatable
{
    private TypeDescriptor catalogType;
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        FormatIdUtil.readFormatIdInteger(objectInput);
        objectInput.readObject();
        this.catalogType = (TypeDescriptor)objectInput.readObject();
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
    }
    
    public int getTypeFormatId() {
        return 14;
    }
    
    TypeDescriptor getCatalogType() {
        return this.catalogType;
    }
}
