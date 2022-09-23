// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.log;

import java.io.InputStream;
import org.apache.derby.iapi.store.raw.Loggable;
import java.io.IOException;
import org.apache.derby.iapi.store.raw.xact.TransactionId;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.access.DatabaseInstant;
import org.apache.derby.iapi.services.io.ArrayInputStream;
import org.apache.derby.iapi.store.raw.log.LogFactory;
import org.apache.derby.iapi.store.raw.ScanHandle;

public class FlushedScanHandle implements ScanHandle
{
    LogFactory lf;
    StreamLogScan fs;
    LogRecord lr;
    boolean readOptionalData;
    int groupsIWant;
    ArrayInputStream rawInput;
    
    FlushedScanHandle(final LogToFile lf, final DatabaseInstant databaseInstant, final int groupsIWant) throws StandardException {
        this.lr = null;
        this.readOptionalData = false;
        this.rawInput = new ArrayInputStream(new byte[4096]);
        this.lf = lf;
        this.fs = new FlushedScan(lf, ((LogCounter)databaseInstant).getValueAsLong());
        this.groupsIWant = groupsIWant;
    }
    
    public boolean next() throws StandardException {
        this.readOptionalData = false;
        this.lr = null;
        try {
            this.lr = this.fs.getNextRecord(this.rawInput, null, this.groupsIWant);
            return this.lr != null;
        }
        catch (IOException ex) {
            ex.printStackTrace();
            this.fs.close();
            this.fs = null;
            throw this.lf.markCorrupt(StandardException.newException("XSLA2.D", ex));
        }
    }
    
    public int getGroup() throws StandardException {
        return this.lr.group();
    }
    
    public Loggable getLoggable() throws StandardException {
        try {
            return this.lr.getLoggable();
        }
        catch (IOException ex) {
            ex.printStackTrace();
            this.fs.close();
            this.fs = null;
            throw this.lf.markCorrupt(StandardException.newException("XSLA2.D", ex));
        }
        catch (ClassNotFoundException ex2) {
            this.fs.close();
            this.fs = null;
            throw this.lf.markCorrupt(StandardException.newException("XSLA3.D", ex2));
        }
    }
    
    public InputStream getOptionalData() throws StandardException {
        if (this.lr == null) {
            return null;
        }
        try {
            final int int1 = this.rawInput.readInt();
            this.readOptionalData = true;
            this.rawInput.setLimit(int1);
            return this.rawInput;
        }
        catch (IOException ex) {
            this.fs.close();
            this.fs = null;
            throw this.lf.markCorrupt(StandardException.newException("XSLA2.D", ex));
        }
    }
    
    public DatabaseInstant getInstant() throws StandardException {
        return this.fs.getLogInstant();
    }
    
    public Object getTransactionId() throws StandardException {
        try {
            return this.lr.getTransactionId();
        }
        catch (IOException ex) {
            ex.printStackTrace();
            this.fs.close();
            this.fs = null;
            throw this.lf.markCorrupt(StandardException.newException("XSLA2.D", ex));
        }
        catch (ClassNotFoundException ex2) {
            this.fs.close();
            this.fs = null;
            throw this.lf.markCorrupt(StandardException.newException("XSLA3.D", ex2));
        }
    }
    
    public void close() {
        if (this.fs != null) {
            this.fs.close();
        }
        this.fs = null;
    }
}
