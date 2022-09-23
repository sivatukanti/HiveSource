// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records.impl.pb;

import java.nio.ByteBuffer;
import com.google.protobuf.ByteString;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import com.google.protobuf.Message;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public abstract class ProtoBase<T extends Message>
{
    public abstract T getProto();
    
    @Override
    public int hashCode() {
        return this.getProto().hashCode();
    }
    
    @Override
    public boolean equals(final Object other) {
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((ProtoBase)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    protected final ByteBuffer convertFromProtoFormat(final ByteString byteString) {
        return ProtoUtils.convertFromProtoFormat(byteString);
    }
    
    protected final ByteString convertToProtoFormat(final ByteBuffer byteBuffer) {
        return ProtoUtils.convertToProtoFormat(byteBuffer);
    }
}
