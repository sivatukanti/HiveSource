// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords.impl.pb;

import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.proto.YarnServiceProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.protocolrecords.GetQueueUserAclsInfoRequest;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class GetQueueUserAclsInfoRequestPBImpl extends GetQueueUserAclsInfoRequest
{
    YarnServiceProtos.GetQueueUserAclsInfoRequestProto proto;
    YarnServiceProtos.GetQueueUserAclsInfoRequestProto.Builder builder;
    boolean viaProto;
    
    public GetQueueUserAclsInfoRequestPBImpl() {
        this.proto = YarnServiceProtos.GetQueueUserAclsInfoRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = YarnServiceProtos.GetQueueUserAclsInfoRequestProto.newBuilder();
    }
    
    public GetQueueUserAclsInfoRequestPBImpl(final YarnServiceProtos.GetQueueUserAclsInfoRequestProto proto) {
        this.proto = YarnServiceProtos.GetQueueUserAclsInfoRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnServiceProtos.GetQueueUserAclsInfoRequestProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((GetQueueUserAclsInfoRequestPBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
}
