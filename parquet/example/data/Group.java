// 
// Decompiled by Procyon v0.5.36
// 

package parquet.example.data;

import parquet.io.api.RecordConsumer;
import parquet.io.api.Binary;
import parquet.example.data.simple.NanoTime;
import parquet.Log;

public abstract class Group extends GroupValueSource
{
    private static final Log logger;
    private static final boolean DEBUG;
    
    public void add(final String field, final int value) {
        this.add(this.getType().getFieldIndex(field), value);
    }
    
    public void add(final String field, final long value) {
        this.add(this.getType().getFieldIndex(field), value);
    }
    
    public void add(final String field, final float value) {
        this.add(this.getType().getFieldIndex(field), value);
    }
    
    public void add(final String field, final double value) {
        this.add(this.getType().getFieldIndex(field), value);
    }
    
    public void add(final String field, final String value) {
        this.add(this.getType().getFieldIndex(field), value);
    }
    
    public void add(final String field, final NanoTime value) {
        this.add(this.getType().getFieldIndex(field), value);
    }
    
    public void add(final String field, final boolean value) {
        this.add(this.getType().getFieldIndex(field), value);
    }
    
    public void add(final String field, final Binary value) {
        this.add(this.getType().getFieldIndex(field), value);
    }
    
    public void add(final String field, final Group value) {
        this.add(this.getType().getFieldIndex(field), value);
    }
    
    public Group addGroup(final String field) {
        if (Group.DEBUG) {
            Group.logger.debug("add group " + field + " to " + this.getType().getName());
        }
        return this.addGroup(this.getType().getFieldIndex(field));
    }
    
    @Override
    public Group getGroup(final String field, final int index) {
        return this.getGroup(this.getType().getFieldIndex(field), index);
    }
    
    public abstract void add(final int p0, final int p1);
    
    public abstract void add(final int p0, final long p1);
    
    public abstract void add(final int p0, final String p1);
    
    public abstract void add(final int p0, final boolean p1);
    
    public abstract void add(final int p0, final NanoTime p1);
    
    public abstract void add(final int p0, final Binary p1);
    
    public abstract void add(final int p0, final float p1);
    
    public abstract void add(final int p0, final double p1);
    
    public abstract void add(final int p0, final Group p1);
    
    public abstract Group addGroup(final int p0);
    
    @Override
    public abstract Group getGroup(final int p0, final int p1);
    
    public Group asGroup() {
        return this;
    }
    
    public Group append(final String fieldName, final int value) {
        this.add(fieldName, value);
        return this;
    }
    
    public Group append(final String fieldName, final float value) {
        this.add(fieldName, value);
        return this;
    }
    
    public Group append(final String fieldName, final double value) {
        this.add(fieldName, value);
        return this;
    }
    
    public Group append(final String fieldName, final long value) {
        this.add(fieldName, value);
        return this;
    }
    
    public Group append(final String fieldName, final NanoTime value) {
        this.add(fieldName, value);
        return this;
    }
    
    public Group append(final String fieldName, final String value) {
        this.add(fieldName, Binary.fromString(value));
        return this;
    }
    
    public Group append(final String fieldName, final boolean value) {
        this.add(fieldName, value);
        return this;
    }
    
    public Group append(final String fieldName, final Binary value) {
        this.add(fieldName, value);
        return this;
    }
    
    public abstract void writeValue(final int p0, final int p1, final RecordConsumer p2);
    
    static {
        logger = Log.getLog(Group.class);
        DEBUG = Log.DEBUG;
    }
}
