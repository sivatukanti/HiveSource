// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy;

import org.apache.commons.logging.LogFactory;
import java.nio.charset.CharacterCodingException;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.hive.serde2.typeinfo.CharTypeInfo;
import org.apache.commons.logging.Log;
import org.apache.hadoop.hive.serde2.io.HiveCharWritable;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive.LazyHiveCharObjectInspector;

public class LazyHiveChar extends LazyPrimitive<LazyHiveCharObjectInspector, HiveCharWritable>
{
    private static final Log LOG;
    protected int maxLength;
    
    public LazyHiveChar(final LazyHiveCharObjectInspector oi) {
        super(oi);
        this.maxLength = -1;
        this.maxLength = ((CharTypeInfo)oi.getTypeInfo()).getLength();
        this.data = (T)new HiveCharWritable();
    }
    
    public LazyHiveChar(final LazyHiveChar copy) {
        super(copy);
        this.maxLength = -1;
        this.maxLength = copy.maxLength;
        this.data = (T)new HiveCharWritable((HiveCharWritable)copy.data);
    }
    
    public void setValue(final LazyHiveChar copy) {
        ((HiveCharWritable)this.data).set((HiveCharWritable)copy.data, this.maxLength);
    }
    
    @Override
    public void init(final ByteArrayRef bytes, final int start, final int length) {
        if (((LazyHiveCharObjectInspector)this.oi).isEscaped()) {
            final Text textData = ((HiveCharWritable)this.data).getTextValue();
            LazyUtils.copyAndEscapeStringDataToText(bytes.getData(), start, length, ((LazyHiveCharObjectInspector)this.oi).getEscapeChar(), textData);
            ((HiveCharWritable)this.data).set(textData.toString(), this.maxLength);
            this.isNull = false;
        }
        else {
            String byteData = null;
            try {
                byteData = Text.decode(bytes.getData(), start, length);
                ((HiveCharWritable)this.data).set(byteData, this.maxLength);
                this.isNull = false;
            }
            catch (CharacterCodingException e) {
                this.isNull = true;
                LazyHiveChar.LOG.debug("Data not in the HiveChar data type range so converted to null.", e);
            }
        }
    }
    
    static {
        LOG = LogFactory.getLog(LazyHiveChar.class);
    }
}
