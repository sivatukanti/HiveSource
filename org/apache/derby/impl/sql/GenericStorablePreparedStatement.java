// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql;

import org.apache.derby.iapi.sql.ResultDescription;
import org.apache.derby.iapi.sql.execute.ConstantAction;
import java.io.ObjectInput;
import java.io.IOException;
import org.apache.derby.iapi.services.io.ArrayUtil;
import java.io.ObjectOutput;
import org.apache.derby.iapi.services.context.ContextService;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.loader.GeneratedClass;
import org.apache.derby.iapi.sql.Statement;
import org.apache.derby.iapi.util.ByteArray;
import org.apache.derby.iapi.sql.StorablePreparedStatement;
import org.apache.derby.iapi.services.io.Formatable;

public class GenericStorablePreparedStatement extends GenericPreparedStatement implements Formatable, StorablePreparedStatement
{
    private ByteArray byteCode;
    private String className;
    
    public GenericStorablePreparedStatement() {
    }
    
    GenericStorablePreparedStatement(final Statement statement) {
        super(statement);
    }
    
    ByteArray getByteCodeSaver() {
        if (this.byteCode == null) {
            this.byteCode = new ByteArray();
        }
        return this.byteCode;
    }
    
    public GeneratedClass getActivationClass() throws StandardException {
        if (this.activationClass == null) {
            this.loadGeneratedClass();
        }
        return this.activationClass;
    }
    
    void setActivationClass(final GeneratedClass activationClass) {
        super.setActivationClass(activationClass);
        if (activationClass != null) {
            this.className = activationClass.getName();
            if (this.byteCode != null && this.byteCode.getArray() == null) {
                this.byteCode = null;
            }
        }
    }
    
    public void loadGeneratedClass() throws StandardException {
        this.setActivationClass(((LanguageConnectionContext)ContextService.getContext("LanguageConnectionContext")).getLanguageConnectionFactory().getClassFactory().loadGeneratedClass(this.className, this.byteCode));
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        objectOutput.writeObject(this.getCursorInfo());
        objectOutput.writeBoolean(this.needsSavepoint());
        objectOutput.writeBoolean(this.isAtomic);
        objectOutput.writeObject(this.executionConstants);
        objectOutput.writeObject(this.resultDesc);
        if (this.savedObjects == null) {
            objectOutput.writeBoolean(false);
        }
        else {
            objectOutput.writeBoolean(true);
            ArrayUtil.writeArrayLength(objectOutput, this.savedObjects);
            ArrayUtil.writeArrayItems(objectOutput, this.savedObjects);
        }
        objectOutput.writeObject(this.className);
        objectOutput.writeBoolean(this.byteCode != null);
        if (this.byteCode != null) {
            this.byteCode.writeExternal(objectOutput);
        }
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        this.setCursorInfo((CursorInfo)objectInput.readObject());
        this.setNeedsSavepoint(objectInput.readBoolean());
        this.isAtomic = objectInput.readBoolean();
        this.executionConstants = (ConstantAction)objectInput.readObject();
        this.resultDesc = (ResultDescription)objectInput.readObject();
        if (objectInput.readBoolean()) {
            ArrayUtil.readArrayItems(objectInput, this.savedObjects = new Object[ArrayUtil.readArrayLength(objectInput)]);
        }
        this.className = (String)objectInput.readObject();
        if (objectInput.readBoolean()) {
            (this.byteCode = new ByteArray()).readExternal(objectInput);
        }
        else {
            this.byteCode = null;
        }
    }
    
    public int getTypeFormatId() {
        return 225;
    }
    
    public boolean isStorable() {
        return true;
    }
    
    public String toString() {
        return "";
    }
}
