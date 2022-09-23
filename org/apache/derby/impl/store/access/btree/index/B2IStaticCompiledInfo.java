// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.btree.index;

import java.io.ObjectOutput;
import org.apache.derby.iapi.services.io.ArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.store.access.StaticCompiledOpenConglomInfo;

public class B2IStaticCompiledInfo implements StaticCompiledOpenConglomInfo
{
    B2I b2i;
    StaticCompiledOpenConglomInfo base_table_static_info;
    
    public B2IStaticCompiledInfo() {
    }
    
    B2IStaticCompiledInfo(final TransactionController transactionController, final B2I b2i) throws StandardException {
        this.b2i = b2i;
        this.base_table_static_info = transactionController.getStaticCompiledConglomInfo(b2i.baseConglomerateId);
    }
    
    public DataValueDescriptor getConglom() {
        return this.b2i;
    }
    
    public boolean isNull() {
        return this.b2i == null;
    }
    
    public void restoreToNull() {
        this.b2i = null;
    }
    
    public int getTypeFormatId() {
        return 360;
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        (this.b2i = new B2I()).readExternal(objectInput);
        this.base_table_static_info = (StaticCompiledOpenConglomInfo)objectInput.readObject();
    }
    
    public void readExternalFromArray(final ArrayInputStream arrayInputStream) throws IOException, ClassNotFoundException {
        (this.b2i = new B2I()).readExternal(arrayInputStream);
        this.base_table_static_info = (StaticCompiledOpenConglomInfo)arrayInputStream.readObject();
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        this.b2i.writeExternal(objectOutput);
        objectOutput.writeObject(this.base_table_static_info);
    }
}
