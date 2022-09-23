// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb;

import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.proto.YarnServerResourceManagerServiceProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshQueuesRequest;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class RefreshQueuesRequestPBImpl extends RefreshQueuesRequest
{
    YarnServerResourceManagerServiceProtos.RefreshQueuesRequestProto proto;
    YarnServerResourceManagerServiceProtos.RefreshQueuesRequestProto.Builder builder;
    boolean viaProto;
    
    public RefreshQueuesRequestPBImpl() {
        this.proto = YarnServerResourceManagerServiceProtos.RefreshQueuesRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = YarnServerResourceManagerServiceProtos.RefreshQueuesRequestProto.newBuilder();
    }
    
    public RefreshQueuesRequestPBImpl(final YarnServerResourceManagerServiceProtos.RefreshQueuesRequestProto proto) {
        this.proto = YarnServerResourceManagerServiceProtos.RefreshQueuesRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnServerResourceManagerServiceProtos.RefreshQueuesRequestProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((RefreshQueuesRequestPBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
}
