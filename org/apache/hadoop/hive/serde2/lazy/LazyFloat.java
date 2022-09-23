// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy;

import org.apache.commons.logging.LogFactory;
import java.nio.charset.CharacterCodingException;
import org.apache.hadoop.io.Text;
import org.apache.commons.logging.Log;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive.LazyFloatObjectInspector;

public class LazyFloat extends LazyPrimitive<LazyFloatObjectInspector, FloatWritable>
{
    private static final Log LOG;
    
    public LazyFloat(final LazyFloatObjectInspector oi) {
        super(oi);
        this.data = (T)new FloatWritable();
    }
    
    public LazyFloat(final LazyFloat copy) {
        super(copy);
        this.data = (T)new FloatWritable(((FloatWritable)copy.data).get());
    }
    
    @Override
    public void init(final ByteArrayRef bytes, final int start, final int length) {
        String byteData = null;
        try {
            byteData = Text.decode(bytes.getData(), start, length);
            ((FloatWritable)this.data).set(Float.parseFloat(byteData));
            this.isNull = false;
        }
        catch (NumberFormatException e) {
            this.isNull = true;
            LazyFloat.LOG.debug("Data not in the Float data type range so converted to null. Given data is :" + byteData, e);
        }
        catch (CharacterCodingException e2) {
            this.isNull = true;
            LazyFloat.LOG.debug("Data not in the Float data type range so converted to null.", e2);
        }
    }
    
    static {
        LOG = LogFactory.getLog(LazyFloat.class);
    }
}
