// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop.api;

import parquet.io.api.RecordMaterializer;
import parquet.schema.MessageType;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;

public class DelegatingReadSupport<T> extends ReadSupport<T>
{
    private final ReadSupport<T> delegate;
    
    public DelegatingReadSupport(final ReadSupport<T> delegate) {
        this.delegate = delegate;
    }
    
    @Override
    public ReadContext init(final InitContext context) {
        return this.delegate.init(context);
    }
    
    @Override
    public RecordMaterializer<T> prepareForRead(final Configuration configuration, final Map<String, String> keyValueMetaData, final MessageType fileSchema, final ReadContext readContext) {
        return this.delegate.prepareForRead(configuration, keyValueMetaData, fileSchema, readContext);
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + "(" + this.delegate.toString() + ")";
    }
}
