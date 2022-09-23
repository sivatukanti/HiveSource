// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords.impl.pb;

import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.proto.YarnServiceProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.protocolrecords.GetClusterMetricsRequest;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class GetClusterMetricsRequestPBImpl extends GetClusterMetricsRequest
{
    YarnServiceProtos.GetClusterMetricsRequestProto proto;
    YarnServiceProtos.GetClusterMetricsRequestProto.Builder builder;
    boolean viaProto;
    
    public GetClusterMetricsRequestPBImpl() {
        this.proto = YarnServiceProtos.GetClusterMetricsRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = YarnServiceProtos.GetClusterMetricsRequestProto.newBuilder();
    }
    
    public GetClusterMetricsRequestPBImpl(final YarnServiceProtos.GetClusterMetricsRequestProto proto) {
        this.proto = YarnServiceProtos.GetClusterMetricsRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnServiceProtos.GetClusterMetricsRequestProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((GetClusterMetricsRequestPBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
}
