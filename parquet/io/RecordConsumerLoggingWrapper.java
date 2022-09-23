// 
// Decompiled by Procyon v0.5.36
// 

package parquet.io;

import java.util.Arrays;
import parquet.io.api.Binary;
import parquet.Log;
import parquet.io.api.RecordConsumer;

public class RecordConsumerLoggingWrapper extends RecordConsumer
{
    private static final Log logger;
    private static final boolean DEBUG;
    private final RecordConsumer delegate;
    int indent;
    
    public RecordConsumerLoggingWrapper(final RecordConsumer delegate) {
        this.indent = 0;
        this.delegate = delegate;
    }
    
    @Override
    public void startField(final String field, final int index) {
        if (RecordConsumerLoggingWrapper.DEBUG) {
            this.logOpen(field);
        }
        this.delegate.startField(field, index);
    }
    
    private void logOpen(final String field) {
        this.log("<" + field + ">");
    }
    
    private String indent() {
        final StringBuilder result = new StringBuilder();
        for (int i = 0; i < this.indent; ++i) {
            result.append("  ");
        }
        return result.toString();
    }
    
    private void log(final Object value) {
        RecordConsumerLoggingWrapper.logger.debug(this.indent() + value);
    }
    
    @Override
    public void startGroup() {
        if (RecordConsumerLoggingWrapper.DEBUG) {
            ++this.indent;
        }
        if (RecordConsumerLoggingWrapper.DEBUG) {
            this.log("<!-- start group -->");
        }
        this.delegate.startGroup();
    }
    
    @Override
    public void addInteger(final int value) {
        if (RecordConsumerLoggingWrapper.DEBUG) {
            this.log(value);
        }
        this.delegate.addInteger(value);
    }
    
    @Override
    public void addLong(final long value) {
        if (RecordConsumerLoggingWrapper.DEBUG) {
            this.log(value);
        }
        this.delegate.addLong(value);
    }
    
    @Override
    public void addBoolean(final boolean value) {
        if (RecordConsumerLoggingWrapper.DEBUG) {
            this.log(value);
        }
        this.delegate.addBoolean(value);
    }
    
    @Override
    public void addBinary(final Binary value) {
        if (RecordConsumerLoggingWrapper.DEBUG) {
            this.log(Arrays.toString(value.getBytes()));
        }
        this.delegate.addBinary(value);
    }
    
    @Override
    public void addFloat(final float value) {
        if (RecordConsumerLoggingWrapper.DEBUG) {
            this.log(value);
        }
        this.delegate.addFloat(value);
    }
    
    @Override
    public void addDouble(final double value) {
        if (RecordConsumerLoggingWrapper.DEBUG) {
            this.log(value);
        }
        this.delegate.addDouble(value);
    }
    
    @Override
    public void endGroup() {
        if (RecordConsumerLoggingWrapper.DEBUG) {
            this.log("<!-- end group -->");
        }
        if (RecordConsumerLoggingWrapper.DEBUG) {
            --this.indent;
        }
        this.delegate.endGroup();
    }
    
    @Override
    public void endField(final String field, final int index) {
        if (RecordConsumerLoggingWrapper.DEBUG) {
            this.logClose(field);
        }
        this.delegate.endField(field, index);
    }
    
    private void logClose(final String field) {
        this.log("</" + field + ">");
    }
    
    @Override
    public void startMessage() {
        if (RecordConsumerLoggingWrapper.DEBUG) {
            this.log("<!-- start message -->");
        }
        this.delegate.startMessage();
    }
    
    @Override
    public void endMessage() {
        this.delegate.endMessage();
        if (RecordConsumerLoggingWrapper.DEBUG) {
            this.log("<!-- end message -->");
        }
    }
    
    static {
        logger = Log.getLog(RecordConsumerLoggingWrapper.class);
        DEBUG = Log.DEBUG;
    }
}
