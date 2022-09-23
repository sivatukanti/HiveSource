// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy;

import org.apache.commons.logging.LogFactory;
import java.nio.charset.CharacterCodingException;
import org.apache.hadoop.io.Text;
import org.apache.commons.logging.Log;
import org.apache.hadoop.hive.serde2.io.DoubleWritable;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive.LazyDoubleObjectInspector;

public class LazyDouble extends LazyPrimitive<LazyDoubleObjectInspector, DoubleWritable>
{
    private static final Log LOG;
    
    public LazyDouble(final LazyDoubleObjectInspector oi) {
        super(oi);
        this.data = (T)new DoubleWritable();
    }
    
    public LazyDouble(final LazyDouble copy) {
        super(copy);
        this.data = (T)new DoubleWritable(((DoubleWritable)copy.data).get());
    }
    
    @Override
    public void init(final ByteArrayRef bytes, final int start, final int length) {
        String byteData = null;
        try {
            byteData = Text.decode(bytes.getData(), start, length);
            ((DoubleWritable)this.data).set(Double.parseDouble(byteData));
            this.isNull = false;
        }
        catch (NumberFormatException e) {
            this.isNull = true;
            LazyDouble.LOG.debug("Data not in the Double data type range so converted to null. Given data is :" + byteData, e);
        }
        catch (CharacterCodingException e2) {
            this.isNull = true;
            LazyDouble.LOG.debug("Data not in the Double data type range so converted to null.", e2);
        }
    }
    
    static {
        LOG = LogFactory.getLog(LazyDouble.class);
    }
}
