// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords.impl.pb;

import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.proto.YarnServiceProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.protocolrecords.MoveApplicationAcrossQueuesResponse;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class MoveApplicationAcrossQueuesResponsePBImpl extends MoveApplicationAcrossQueuesResponse
{
    YarnServiceProtos.MoveApplicationAcrossQueuesResponseProto proto;
    YarnServiceProtos.MoveApplicationAcrossQueuesResponseProto.Builder builder;
    boolean viaProto;
    
    public MoveApplicationAcrossQueuesResponsePBImpl() {
        this.proto = YarnServiceProtos.MoveApplicationAcrossQueuesResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = YarnServiceProtos.MoveApplicationAcrossQueuesResponseProto.newBuilder();
    }
    
    public MoveApplicationAcrossQueuesResponsePBImpl(final YarnServiceProtos.MoveApplicationAcrossQueuesResponseProto proto) {
        this.proto = YarnServiceProtos.MoveApplicationAcrossQueuesResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnServiceProtos.MoveApplicationAcrossQueuesResponseProto getProto() {
        this.proto = (this.viaProto ? this.proto : this.builder.build());
        this.viaProto = true;
        return this.proto;
    }
    
    @Override
    public int hashCode() {
        return this.getProto().hashCode();
    }
    
    @Override
    public boolean equals(final Object other) {
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((MoveApplicationAcrossQueuesResponsePBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
}
