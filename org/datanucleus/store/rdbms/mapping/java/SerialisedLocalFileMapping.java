// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import org.datanucleus.TransactionEventListener;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.util.NucleusLogger;
import java.io.File;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.store.rdbms.mapping.MappingCallbacks;

public class SerialisedLocalFileMapping extends JavaTypeMapping implements MappingCallbacks
{
    public static final String SERIALIZE_TO_FOLDER_EXTENSION = "serializeToFileLocation";
    String folderName;
    
    public SerialisedLocalFileMapping() {
        this.folderName = null;
    }
    
    @Override
    public void initialize(final AbstractMemberMetaData mmd, final Table table, final ClassLoaderResolver clr) {
        super.initialize(mmd, table, clr);
        this.folderName = mmd.getValueForExtension("serializeToFileLocation");
        final File folder = new File(this.folderName);
        if (!folder.exists()) {
            NucleusLogger.PERSISTENCE.debug("Creating folder for persistence data for field " + mmd.getFullFieldName() + " : folder=" + this.folderName);
            folder.mkdir();
        }
    }
    
    @Override
    public boolean includeInFetchStatement() {
        return false;
    }
    
    @Override
    public boolean includeInUpdateStatement() {
        return false;
    }
    
    @Override
    public boolean includeInInsertStatement() {
        return false;
    }
    
    @Override
    public Class getJavaType() {
        return this.mmd.getType();
    }
    
    @Override
    public void insertPostProcessing(final ObjectProvider op) {
    }
    
    @Override
    public void postInsert(final ObjectProvider op) {
        final Object val = op.provideField(this.mmd.getAbsoluteFieldNumber());
        this.serialiseFieldValue(op, val);
        if (op.getExecutionContext().getTransaction().isActive()) {
            op.getExecutionContext().getTransaction().addTransactionEventListener(new TransactionEventListener() {
                @Override
                public void transactionPreRollBack() {
                    final File fieldFile = new File(SerialisedLocalFileMapping.this.getFilenameForObjectProvider(op));
                    if (fieldFile.exists()) {
                        fieldFile.delete();
                    }
                }
                
                @Override
                public void transactionStarted() {
                }
                
                @Override
                public void transactionRolledBack() {
                }
                
                @Override
                public void transactionPreFlush() {
                }
                
                @Override
                public void transactionPreCommit() {
                }
                
                @Override
                public void transactionFlushed() {
                }
                
                @Override
                public void transactionEnded() {
                }
                
                @Override
                public void transactionCommitted() {
                }
            });
        }
    }
    
    @Override
    public void postFetch(final ObjectProvider op) {
        final Object value = this.deserialiseFieldValue(op);
        op.replaceField(this.mmd.getAbsoluteFieldNumber(), value);
    }
    
    @Override
    public void postUpdate(final ObjectProvider op) {
        final Object oldValue = this.deserialiseFieldValue(op);
        final Object val = op.provideField(this.mmd.getAbsoluteFieldNumber());
        this.serialiseFieldValue(op, val);
        if (op.getExecutionContext().getTransaction().isActive()) {
            op.getExecutionContext().getTransaction().addTransactionEventListener(new TransactionEventListener() {
                @Override
                public void transactionPreRollBack() {
                    SerialisedLocalFileMapping.this.serialiseFieldValue(op, oldValue);
                }
                
                @Override
                public void transactionStarted() {
                }
                
                @Override
                public void transactionRolledBack() {
                }
                
                @Override
                public void transactionPreFlush() {
                }
                
                @Override
                public void transactionPreCommit() {
                }
                
                @Override
                public void transactionFlushed() {
                }
                
                @Override
                public void transactionEnded() {
                }
                
                @Override
                public void transactionCommitted() {
                }
            });
        }
    }
    
    @Override
    public void preDelete(final ObjectProvider op) {
        final Object oldValue = op.provideField(this.mmd.getAbsoluteFieldNumber());
        final File fieldFile = new File(this.getFilenameForObjectProvider(op));
        if (fieldFile.exists()) {
            fieldFile.delete();
        }
        if (op.getExecutionContext().getTransaction().isActive()) {
            op.getExecutionContext().getTransaction().addTransactionEventListener(new TransactionEventListener() {
                @Override
                public void transactionPreRollBack() {
                    SerialisedLocalFileMapping.this.serialiseFieldValue(op, oldValue);
                }
                
                @Override
                public void transactionStarted() {
                }
                
                @Override
                public void transactionRolledBack() {
                }
                
                @Override
                public void transactionPreFlush() {
                }
                
                @Override
                public void transactionPreCommit() {
                }
                
                @Override
                public void transactionFlushed() {
                }
                
                @Override
                public void transactionEnded() {
                }
                
                @Override
                public void transactionCommitted() {
                }
            });
        }
    }
    
    protected String getFilenameForObjectProvider(final ObjectProvider op) {
        return this.folderName + System.getProperty("file.separator") + op.getInternalObjectId();
    }
    
    protected void serialiseFieldValue(final ObjectProvider op, final Object value) {
        try {
            final FileOutputStream fileOut = new FileOutputStream(this.getFilenameForObjectProvider(op));
            final ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(value);
            out.close();
            fileOut.close();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    protected Object deserialiseFieldValue(final ObjectProvider op) {
        Object value = null;
        try {
            final FileInputStream fileIn = new FileInputStream(this.getFilenameForObjectProvider(op));
            final ObjectInputStream in = new ObjectInputStream(fileIn);
            value = in.readObject();
            in.close();
            fileIn.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return value;
    }
}
