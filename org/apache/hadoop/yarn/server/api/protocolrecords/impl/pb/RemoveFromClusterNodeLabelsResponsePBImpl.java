// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb;

import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.proto.YarnServerResourceManagerServiceProtos;
import org.apache.hadoop.yarn.server.api.protocolrecords.RemoveFromClusterNodeLabelsResponse;

public class RemoveFromClusterNodeLabelsResponsePBImpl extends RemoveFromClusterNodeLabelsResponse
{
    YarnServerResourceManagerServiceProtos.RemoveFromClusterNodeLabelsResponseProto proto;
    YarnServerResourceManagerServiceProtos.RemoveFromClusterNodeLabelsResponseProto.Builder builder;
    boolean viaProto;
    
    public RemoveFromClusterNodeLabelsResponsePBImpl() {
        this.proto = YarnServerResourceManagerServiceProtos.RemoveFromClusterNodeLabelsResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = YarnServerResourceManagerServiceProtos.RemoveFromClusterNodeLabelsResponseProto.newBuilder();
    }
    
    public RemoveFromClusterNodeLabelsResponsePBImpl(final YarnServerResourceManagerServiceProtos.RemoveFromClusterNodeLabelsResponseProto proto) {
        this.proto = YarnServerResourceManagerServiceProtos.RemoveFromClusterNodeLabelsResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnServerResourceManagerServiceProtos.RemoveFromClusterNodeLabelsResponseProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((RemoveFromClusterNodeLabelsResponsePBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
}
