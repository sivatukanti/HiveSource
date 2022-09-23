// 
// Decompiled by Procyon v0.5.36
// 

package parquet.io;

import parquet.io.api.Binary;
import java.util.Arrays;
import parquet.schema.PrimitiveType;
import java.util.ArrayDeque;
import parquet.schema.MessageType;
import parquet.schema.Type;
import java.util.Deque;
import parquet.Log;
import parquet.io.api.RecordConsumer;

public class ValidatingRecordConsumer extends RecordConsumer
{
    private static final Log LOG;
    private static final boolean DEBUG;
    private final RecordConsumer delegate;
    private Deque<Type> types;
    private Deque<Integer> fields;
    private Deque<Integer> previousField;
    private Deque<Integer> fieldValueCount;
    
    public ValidatingRecordConsumer(final RecordConsumer delegate, final MessageType schema) {
        this.types = new ArrayDeque<Type>();
        this.fields = new ArrayDeque<Integer>();
        this.previousField = new ArrayDeque<Integer>();
        this.fieldValueCount = new ArrayDeque<Integer>();
        this.delegate = delegate;
        this.types.push(schema);
    }
    
    @Override
    public void startMessage() {
        this.previousField.push(-1);
        this.delegate.startMessage();
    }
    
    @Override
    public void endMessage() {
        this.delegate.endMessage();
        this.validateMissingFields(this.types.peek().asGroupType().getFieldCount());
        this.previousField.pop();
    }
    
    @Override
    public void startField(final String field, final int index) {
        if (index <= this.previousField.peek()) {
            throw new InvalidRecordException("fields must be added in order " + field + " index " + index + " is before previous field " + this.previousField.peek());
        }
        this.validateMissingFields(index);
        this.fields.push(index);
        this.fieldValueCount.push(0);
        this.delegate.startField(field, index);
    }
    
    private void validateMissingFields(final int index) {
        for (int i = this.previousField.peek() + 1; i < index; ++i) {
            final Type type = this.types.peek().asGroupType().getType(i);
            if (type.isRepetition(Type.Repetition.REQUIRED)) {
                throw new InvalidRecordException("required field is missing " + type);
            }
        }
    }
    
    @Override
    public void endField(final String field, final int index) {
        this.delegate.endField(field, index);
        this.fieldValueCount.pop();
        this.previousField.push(this.fields.pop());
    }
    
    @Override
    public void startGroup() {
        this.previousField.push(-1);
        this.types.push(this.types.peek().asGroupType().getType(this.fields.peek()));
        this.delegate.startGroup();
    }
    
    @Override
    public void endGroup() {
        this.delegate.endGroup();
        this.validateMissingFields(this.types.peek().asGroupType().getFieldCount());
        this.types.pop();
        this.previousField.pop();
    }
    
    private void validate(final PrimitiveType.PrimitiveTypeName p) {
        final Type currentType = this.types.peek().asGroupType().getType(this.fields.peek());
        final int c = this.fieldValueCount.pop() + 1;
        this.fieldValueCount.push(c);
        if (ValidatingRecordConsumer.DEBUG) {
            ValidatingRecordConsumer.LOG.debug("validate " + p + " for " + currentType.getName());
        }
        switch (currentType.getRepetition()) {
            case OPTIONAL:
            case REQUIRED: {
                if (c > 1) {
                    throw new InvalidRecordException("repeated value when the type is not repeated in " + currentType);
                }
                break;
            }
            case REPEATED: {
                break;
            }
            default: {
                throw new InvalidRecordException("unknown repetition " + currentType.getRepetition() + " in " + currentType);
            }
        }
        if (!currentType.isPrimitive() || currentType.asPrimitiveType().getPrimitiveTypeName() != p) {
            throw new InvalidRecordException("expected type " + p + " but got " + currentType);
        }
    }
    
    private void validate(final PrimitiveType.PrimitiveTypeName... ptypes) {
        final Type currentType = this.types.peek().asGroupType().getType(this.fields.peek());
        final int c = this.fieldValueCount.pop() + 1;
        this.fieldValueCount.push(c);
        if (ValidatingRecordConsumer.DEBUG) {
            ValidatingRecordConsumer.LOG.debug("validate " + Arrays.toString(ptypes) + " for " + currentType.getName());
        }
        switch (currentType.getRepetition()) {
            case OPTIONAL:
            case REQUIRED: {
                if (c > 1) {
                    throw new InvalidRecordException("repeated value when the type is not repeated in " + currentType);
                }
                break;
            }
            case REPEATED: {
                break;
            }
            default: {
                throw new InvalidRecordException("unknown repetition " + currentType.getRepetition() + " in " + currentType);
            }
        }
        if (!currentType.isPrimitive()) {
            throw new InvalidRecordException("expected type in " + Arrays.toString(ptypes) + " but got " + currentType);
        }
        for (final PrimitiveType.PrimitiveTypeName p : ptypes) {
            if (currentType.asPrimitiveType().getPrimitiveTypeName() == p) {
                return;
            }
        }
        throw new InvalidRecordException("expected type in " + Arrays.toString(ptypes) + " but got " + currentType);
    }
    
    @Override
    public void addInteger(final int value) {
        this.validate(PrimitiveType.PrimitiveTypeName.INT32);
        this.delegate.addInteger(value);
    }
    
    @Override
    public void addLong(final long value) {
        this.validate(PrimitiveType.PrimitiveTypeName.INT64);
        this.delegate.addLong(value);
    }
    
    @Override
    public void addBoolean(final boolean value) {
        this.validate(PrimitiveType.PrimitiveTypeName.BOOLEAN);
        this.delegate.addBoolean(value);
    }
    
    @Override
    public void addBinary(final Binary value) {
        this.validate(PrimitiveType.PrimitiveTypeName.BINARY, PrimitiveType.PrimitiveTypeName.INT96, PrimitiveType.PrimitiveTypeName.FIXED_LEN_BYTE_ARRAY);
        this.delegate.addBinary(value);
    }
    
    @Override
    public void addFloat(final float value) {
        this.validate(PrimitiveType.PrimitiveTypeName.FLOAT);
        this.delegate.addFloat(value);
    }
    
    @Override
    public void addDouble(final double value) {
        this.validate(PrimitiveType.PrimitiveTypeName.DOUBLE);
        this.delegate.addDouble(value);
    }
    
    static {
        LOG = Log.getLog(ValidatingRecordConsumer.class);
        DEBUG = Log.DEBUG;
    }
}
