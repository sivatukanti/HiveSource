// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2;

import com.google.common.base.Charsets;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import java.util.Properties;
import org.apache.hadoop.conf.Configuration;
import java.nio.charset.Charset;

public abstract class AbstractEncodingAwareSerDe extends AbstractSerDe
{
    protected Charset charset;
    
    @Deprecated
    @Override
    public void initialize(final Configuration conf, final Properties tbl) throws SerDeException {
        this.charset = Charset.forName(tbl.getProperty("serialization.encoding", "UTF-8"));
    }
    
    @Override
    public final Writable serialize(final Object obj, final ObjectInspector objInspector) throws SerDeException {
        Writable result = this.doSerialize(obj, objInspector);
        if (!this.charset.equals(Charsets.UTF_8)) {
            result = this.transformFromUTF8(result);
        }
        return result;
    }
    
    protected abstract Writable transformFromUTF8(final Writable p0);
    
    protected abstract Writable doSerialize(final Object p0, final ObjectInspector p1) throws SerDeException;
    
    @Override
    public final Object deserialize(Writable blob) throws SerDeException {
        if (!this.charset.equals(Charsets.UTF_8)) {
            blob = this.transformToUTF8(blob);
        }
        return this.doDeserialize(blob);
    }
    
    protected abstract Writable transformToUTF8(final Writable p0);
    
    protected abstract Object doDeserialize(final Writable p0) throws SerDeException;
}
