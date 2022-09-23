// 
// Decompiled by Procyon v0.5.36
// 

package parquet.io;

import parquet.io.api.Binary;
import parquet.column.ColumnReadStore;
import parquet.io.api.RecordMaterializer;
import parquet.io.api.RecordConsumer;
import parquet.Log;

public abstract class BaseRecordReader<T> extends RecordReader<T>
{
    private static final Log LOG;
    public RecordConsumer recordConsumer;
    public RecordMaterializer<T> recordMaterializer;
    public ColumnReadStore columnStore;
    RecordReaderImplementation.State[] caseLookup;
    private String endField;
    private int endIndex;
    
    @Override
    public T read() {
        this.readOneRecord();
        return this.recordMaterializer.getCurrentRecord();
    }
    
    protected abstract void readOneRecord();
    
    protected void currentLevel(final int currentLevel) {
        if (Log.DEBUG) {
            BaseRecordReader.LOG.debug("currentLevel: " + currentLevel);
        }
    }
    
    protected void log(final String message) {
        if (Log.DEBUG) {
            BaseRecordReader.LOG.debug("bc: " + message);
        }
    }
    
    protected final int getCaseId(final int state, final int currentLevel, final int d, final int nextR) {
        return this.caseLookup[state].getCase(currentLevel, d, nextR).getID();
    }
    
    protected final void startMessage() {
        this.endField = null;
        if (Log.DEBUG) {
            BaseRecordReader.LOG.debug("startMessage()");
        }
        this.recordConsumer.startMessage();
    }
    
    protected final void startGroup(final String field, final int index) {
        this.startField(field, index);
        if (Log.DEBUG) {
            BaseRecordReader.LOG.debug("startGroup()");
        }
        this.recordConsumer.startGroup();
    }
    
    private void startField(final String field, final int index) {
        if (Log.DEBUG) {
            BaseRecordReader.LOG.debug("startField(" + field + "," + index + ")");
        }
        if (this.endField != null && index == this.endIndex) {
            this.endField = null;
        }
        else {
            if (this.endField != null) {
                this.recordConsumer.endField(this.endField, this.endIndex);
                this.endField = null;
            }
            this.recordConsumer.startField(field, index);
        }
    }
    
    protected final void addPrimitiveINT64(final String field, final int index, final long value) {
        this.startField(field, index);
        if (Log.DEBUG) {
            BaseRecordReader.LOG.debug("addLong(" + value + ")");
        }
        this.recordConsumer.addLong(value);
        this.endField(field, index);
    }
    
    private void endField(final String field, final int index) {
        if (Log.DEBUG) {
            BaseRecordReader.LOG.debug("endField(" + field + "," + index + ")");
        }
        if (this.endField != null) {
            this.recordConsumer.endField(this.endField, this.endIndex);
        }
        this.endField = field;
        this.endIndex = index;
    }
    
    protected final void addPrimitiveBINARY(final String field, final int index, final Binary value) {
        this.startField(field, index);
        if (Log.DEBUG) {
            BaseRecordReader.LOG.debug("addBinary(" + value + ")");
        }
        this.recordConsumer.addBinary(value);
        this.endField(field, index);
    }
    
    protected final void addPrimitiveINT32(final String field, final int index, final int value) {
        this.startField(field, index);
        if (Log.DEBUG) {
            BaseRecordReader.LOG.debug("addInteger(" + value + ")");
        }
        this.recordConsumer.addInteger(value);
        this.endField(field, index);
    }
    
    protected final void endGroup(final String field, final int index) {
        if (this.endField != null) {
            this.recordConsumer.endField(this.endField, this.endIndex);
            this.endField = null;
        }
        if (Log.DEBUG) {
            BaseRecordReader.LOG.debug("endGroup()");
        }
        this.recordConsumer.endGroup();
        this.endField(field, index);
    }
    
    protected final void endMessage() {
        if (this.endField != null) {
            this.recordConsumer.endField(this.endField, this.endIndex);
            this.endField = null;
        }
        if (Log.DEBUG) {
            BaseRecordReader.LOG.debug("endMessage()");
        }
        this.recordConsumer.endMessage();
    }
    
    protected void error(final String message) {
        throw new ParquetDecodingException(message);
    }
    
    static {
        LOG = Log.getLog(BaseRecordReader.class);
    }
}
