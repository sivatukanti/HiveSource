// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.Writable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.io.OutputStream;
import org.apache.hadoop.hive.serde2.typeinfo.HiveDecimalUtils;
import org.apache.hadoop.hive.common.type.HiveDecimal;
import java.nio.charset.CharacterCodingException;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.hive.serde2.typeinfo.DecimalTypeInfo;
import org.apache.commons.logging.Log;
import org.apache.hadoop.hive.serde2.io.HiveDecimalWritable;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive.LazyHiveDecimalObjectInspector;

public class LazyHiveDecimal extends LazyPrimitive<LazyHiveDecimalObjectInspector, HiveDecimalWritable>
{
    private static final Log LOG;
    private final int precision;
    private final int scale;
    private static final byte[] nullBytes;
    
    public LazyHiveDecimal(final LazyHiveDecimalObjectInspector oi) {
        super(oi);
        final DecimalTypeInfo typeInfo = (DecimalTypeInfo)oi.getTypeInfo();
        if (typeInfo == null) {
            throw new RuntimeException("Decimal type used without type params");
        }
        this.precision = typeInfo.precision();
        this.scale = typeInfo.scale();
        this.data = (T)new HiveDecimalWritable();
    }
    
    public LazyHiveDecimal(final LazyHiveDecimal copy) {
        super(copy);
        this.precision = copy.precision;
        this.scale = copy.scale;
        this.data = (T)new HiveDecimalWritable((HiveDecimalWritable)copy.data);
    }
    
    @Override
    public void init(final ByteArrayRef bytes, final int start, final int length) {
        String byteData = null;
        try {
            byteData = Text.decode(bytes.getData(), start, length);
        }
        catch (CharacterCodingException e) {
            this.isNull = true;
            LazyHiveDecimal.LOG.debug("Data not in the HiveDecimal data type range so converted to null.", e);
            return;
        }
        HiveDecimal dec = HiveDecimal.create(byteData);
        dec = this.enforcePrecisionScale(dec);
        if (dec != null) {
            ((HiveDecimalWritable)this.data).set(dec);
            this.isNull = false;
        }
        else {
            LazyHiveDecimal.LOG.debug("Data not in the HiveDecimal data type range so converted to null. Given data is :" + byteData);
            this.isNull = true;
        }
    }
    
    private HiveDecimal enforcePrecisionScale(final HiveDecimal dec) {
        return HiveDecimalUtils.enforcePrecisionScale(dec, this.precision, this.scale);
    }
    
    @Override
    public HiveDecimalWritable getWritableObject() {
        return (HiveDecimalWritable)this.data;
    }
    
    public static void writeUTF8(final OutputStream outputStream, final HiveDecimal hiveDecimal) throws IOException {
        if (hiveDecimal == null) {
            outputStream.write(LazyHiveDecimal.nullBytes);
        }
        else {
            final ByteBuffer b = Text.encode(hiveDecimal.toString());
            outputStream.write(b.array(), 0, b.limit());
        }
    }
    
    static {
        LOG = LogFactory.getLog(LazyHiveDecimal.class);
        nullBytes = new byte[] { 0, 0, 0, 0 };
    }
}
