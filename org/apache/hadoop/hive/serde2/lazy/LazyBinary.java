// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive.LazyBinaryObjectInspector;

public class LazyBinary extends LazyPrimitive<LazyBinaryObjectInspector, BytesWritable>
{
    private static final Log LOG;
    private static final boolean DEBUG_LOG_ENABLED;
    
    LazyBinary(final LazyBinaryObjectInspector oi) {
        super(oi);
        this.data = (T)new BytesWritable();
    }
    
    public LazyBinary(final LazyBinary other) {
        super(other);
        final BytesWritable incoming = ((LazyPrimitive<OI, BytesWritable>)other).getWritableObject();
        final byte[] bytes = new byte[incoming.getLength()];
        System.arraycopy(incoming.getBytes(), 0, bytes, 0, incoming.getLength());
        this.data = (T)new BytesWritable(bytes);
    }
    
    @Override
    public void init(final ByteArrayRef bytes, final int start, final int length) {
        super.init(bytes, start, length);
        final byte[] recv = new byte[length];
        System.arraycopy(bytes.getData(), start, recv, 0, length);
        byte[] decoded = decodeIfNeeded(recv);
        decoded = ((decoded.length > 0) ? decoded : recv);
        ((BytesWritable)this.data).set(decoded, 0, decoded.length);
    }
    
    public static byte[] decodeIfNeeded(final byte[] recv) {
        final boolean arrayByteBase64 = Base64.isArrayByteBase64(recv);
        if (LazyBinary.DEBUG_LOG_ENABLED && arrayByteBase64) {
            LazyBinary.LOG.debug("Data only contains Base64 alphabets only so try to decode the data.");
        }
        return arrayByteBase64 ? Base64.decodeBase64(recv) : recv;
    }
    
    static {
        LOG = LogFactory.getLog(LazyBinary.class);
        DEBUG_LOG_ENABLED = LazyBinary.LOG.isDebugEnabled();
    }
}
