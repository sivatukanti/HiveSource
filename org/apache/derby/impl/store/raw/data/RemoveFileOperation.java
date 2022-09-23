// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import org.apache.derby.iapi.store.raw.Compensation;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.raw.xact.RawTransaction;
import org.apache.derby.iapi.services.io.LimitObjectInput;
import org.apache.derby.iapi.store.raw.log.LogInstant;
import org.apache.derby.iapi.store.raw.Transaction;
import org.apache.derby.iapi.util.ByteArray;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import org.apache.derby.io.StorageFile;
import org.apache.derby.iapi.store.raw.Undoable;

public class RemoveFileOperation implements Undoable
{
    private String name;
    private long generationId;
    private boolean removeAtOnce;
    private transient StorageFile fileToGo;
    
    public RemoveFileOperation() {
    }
    
    RemoveFileOperation(final String name, final long generationId, final boolean removeAtOnce) {
        this.name = name;
        this.generationId = generationId;
        this.removeAtOnce = removeAtOnce;
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        objectOutput.writeUTF(this.name);
        objectOutput.writeLong(this.generationId);
        objectOutput.writeBoolean(this.removeAtOnce);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        this.name = objectInput.readUTF();
        this.generationId = objectInput.readLong();
        this.removeAtOnce = objectInput.readBoolean();
    }
    
    public int getTypeFormatId() {
        return 291;
    }
    
    public ByteArray getPreparedLog() {
        return null;
    }
    
    public void releaseResource(final Transaction transaction) {
    }
    
    public int group() {
        return 1280;
    }
    
    public void doMe(final Transaction transaction, final LogInstant logInstant, final LimitObjectInput limitObjectInput) throws StandardException {
        if (this.fileToGo == null) {
            return;
        }
        ((BaseDataFileFactory)((RawTransaction)transaction).getDataFactory()).fileToRemove(this.fileToGo, true);
    }
    
    public boolean needsRedo(final Transaction transaction) throws StandardException {
        if (!this.removeAtOnce) {
            return false;
        }
        this.fileToGo = ((RawTransaction)transaction).getDataFactory().getFileHandler().getAsFile(this.name, this.generationId);
        return this.fileToGo != null && this.fileToGo.exists();
    }
    
    public Compensation generateUndo(final Transaction transaction, final LimitObjectInput limitObjectInput) throws StandardException, IOException {
        if (this.fileToGo != null) {
            ((BaseDataFileFactory)((RawTransaction)transaction).getDataFactory()).fileToRemove(this.fileToGo, false);
        }
        return null;
    }
}
