// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive;

import java.sql.Timestamp;
import org.apache.hadoop.hive.serde2.lazy.LazyTimestamp;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;
import org.apache.hive.common.util.TimestampParser;
import java.util.List;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.TimestampObjectInspector;
import org.apache.hadoop.hive.serde2.io.TimestampWritable;

public class LazyTimestampObjectInspector extends AbstractPrimitiveLazyObjectInspector<TimestampWritable> implements TimestampObjectInspector
{
    protected List<String> timestampFormats;
    protected TimestampParser timestampParser;
    
    LazyTimestampObjectInspector() {
        super(TypeInfoFactory.timestampTypeInfo);
        this.timestampFormats = null;
        this.timestampParser = null;
        this.timestampParser = new TimestampParser();
    }
    
    LazyTimestampObjectInspector(final List<String> tsFormats) {
        super(TypeInfoFactory.timestampTypeInfo);
        this.timestampFormats = null;
        this.timestampParser = null;
        this.timestampFormats = tsFormats;
        this.timestampParser = new TimestampParser(tsFormats);
    }
    
    @Override
    public Object copyObject(final Object o) {
        return (o == null) ? null : new LazyTimestamp((LazyTimestamp)o);
    }
    
    @Override
    public Timestamp getPrimitiveJavaObject(final Object o) {
        return (o == null) ? null : ((LazyTimestamp)o).getWritableObject().getTimestamp();
    }
    
    public List<String> getTimestampFormats() {
        return this.timestampFormats;
    }
    
    public TimestampParser getTimestampParser() {
        return this.timestampParser;
    }
}
