// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy;

import org.apache.commons.logging.LogFactory;
import java.nio.charset.CharacterCodingException;
import org.apache.hadoop.io.Text;
import org.apache.commons.logging.Log;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;

public abstract class LazyPrimitive<OI extends ObjectInspector, T extends Writable> extends LazyObject<OI>
{
    private static final Log LOG;
    protected T data;
    
    protected LazyPrimitive(final OI oi) {
        super(oi);
    }
    
    protected LazyPrimitive(final LazyPrimitive<OI, T> copy) {
        super(copy.oi);
        this.isNull = copy.isNull;
    }
    
    public T getWritableObject() {
        return (T)(this.isNull ? null : this.data);
    }
    
    @Override
    public String toString() {
        return this.isNull ? null : this.data.toString();
    }
    
    @Override
    public int hashCode() {
        return this.isNull ? 0 : this.data.hashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof LazyPrimitive && (this.data == obj || (this.data != null && obj != null && this.data.equals(((LazyPrimitive)obj).getWritableObject())));
    }
    
    public void logExceptionMessage(final ByteArrayRef bytes, final int start, final int length, final String dataType) {
        try {
            if (LazyPrimitive.LOG.isDebugEnabled()) {
                final String byteData = Text.decode(bytes.getData(), start, length);
                LazyPrimitive.LOG.debug("Data not in the " + dataType + " data type range so converted to null. Given data is :" + byteData, new Exception("For debugging purposes"));
            }
        }
        catch (CharacterCodingException e1) {
            LazyPrimitive.LOG.debug("Data not in the " + dataType + " data type range so converted to null.", e1);
        }
    }
    
    static {
        LOG = LogFactory.getLog(LazyPrimitive.class);
    }
}
