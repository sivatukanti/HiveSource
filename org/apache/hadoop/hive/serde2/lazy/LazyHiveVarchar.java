// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy;

import org.apache.commons.logging.LogFactory;
import java.nio.charset.CharacterCodingException;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.hive.serde2.typeinfo.VarcharTypeInfo;
import org.apache.commons.logging.Log;
import org.apache.hadoop.hive.serde2.io.HiveVarcharWritable;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive.LazyHiveVarcharObjectInspector;

public class LazyHiveVarchar extends LazyPrimitive<LazyHiveVarcharObjectInspector, HiveVarcharWritable>
{
    private static final Log LOG;
    protected int maxLength;
    
    public LazyHiveVarchar(final LazyHiveVarcharObjectInspector oi) {
        super(oi);
        this.maxLength = -1;
        this.maxLength = ((VarcharTypeInfo)oi.getTypeInfo()).getLength();
        this.data = (T)new HiveVarcharWritable();
    }
    
    public LazyHiveVarchar(final LazyHiveVarchar copy) {
        super(copy);
        this.maxLength = -1;
        this.maxLength = copy.maxLength;
        this.data = (T)new HiveVarcharWritable((HiveVarcharWritable)copy.data);
    }
    
    public void setValue(final LazyHiveVarchar copy) {
        ((HiveVarcharWritable)this.data).set((HiveVarcharWritable)copy.data, this.maxLength);
    }
    
    @Override
    public void init(final ByteArrayRef bytes, final int start, final int length) {
        if (((LazyHiveVarcharObjectInspector)this.oi).isEscaped()) {
            final Text textData = ((HiveVarcharWritable)this.data).getTextValue();
            LazyUtils.copyAndEscapeStringDataToText(bytes.getData(), start, length, ((LazyHiveVarcharObjectInspector)this.oi).getEscapeChar(), textData);
            ((HiveVarcharWritable)this.data).set(textData.toString(), this.maxLength);
            this.isNull = false;
        }
        else {
            try {
                String byteData = null;
                byteData = Text.decode(bytes.getData(), start, length);
                ((HiveVarcharWritable)this.data).set(byteData, this.maxLength);
                this.isNull = false;
            }
            catch (CharacterCodingException e) {
                this.isNull = true;
                LazyHiveVarchar.LOG.debug("Data not in the HiveVarchar data type range so converted to null.", e);
            }
        }
    }
    
    static {
        LOG = LogFactory.getLog(LazyHiveVarchar.class);
    }
}
