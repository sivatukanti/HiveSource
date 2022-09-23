// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.columnar;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hive.serde2.lazy.LazyFactory;
import org.apache.hadoop.hive.serde2.lazy.LazyObjectBase;
import org.apache.hadoop.hive.serde2.lazy.LazyUtils;
import org.apache.hadoop.hive.serde2.lazy.ByteArrayRef;
import java.util.List;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.io.Text;
import org.apache.commons.logging.Log;

public class ColumnarStruct extends ColumnarStructBase
{
    private static final Log LOG;
    Text nullSequence;
    int lengthNullSequence;
    
    public ColumnarStruct(final ObjectInspector oi, final List<Integer> notSkippedColumnIDs, final Text nullSequence) {
        super(oi, notSkippedColumnIDs);
        if (nullSequence != null) {
            this.nullSequence = nullSequence;
            this.lengthNullSequence = nullSequence.getLength();
        }
    }
    
    @Override
    protected int getLength(final ObjectInspector objectInspector, final ByteArrayRef cachedByteArrayRef, final int start, final int fieldLen) {
        if (fieldLen == this.lengthNullSequence) {
            final byte[] data = cachedByteArrayRef.getData();
            if (LazyUtils.compare(data, start, fieldLen, this.nullSequence.getBytes(), 0, this.lengthNullSequence) == 0) {
                return -1;
            }
        }
        return fieldLen;
    }
    
    @Override
    protected LazyObjectBase createLazyObjectBase(final ObjectInspector objectInspector) {
        return LazyFactory.createLazyObject(objectInspector);
    }
    
    static {
        LOG = LogFactory.getLog(ColumnarStruct.class);
    }
}
