// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords.impl.pb;

import org.apache.hadoop.yarn.api.records.impl.pb.YarnClusterMetricsPBImpl;
import org.apache.hadoop.yarn.proto.YarnProtos;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.api.records.YarnClusterMetrics;
import org.apache.hadoop.yarn.proto.YarnServiceProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.protocolrecords.GetClusterMetricsResponse;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class GetClusterMetricsResponsePBImpl extends GetClusterMetricsResponse
{
    YarnServiceProtos.GetClusterMetricsResponseProto proto;
    YarnServiceProtos.GetClusterMetricsResponseProto.Builder builder;
    boolean viaProto;
    private YarnClusterMetrics yarnClusterMetrics;
    
    public GetClusterMetricsResponsePBImpl() {
        this.proto = YarnServiceProtos.GetClusterMetricsResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.yarnClusterMetrics = null;
        this.builder = YarnServiceProtos.GetClusterMetricsResponseProto.newBuilder();
    }
    
    public GetClusterMetricsResponsePBImpl(final YarnServiceProtos.GetClusterMetricsResponseProto proto) {
        this.proto = YarnServiceProtos.GetClusterMetricsResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.yarnClusterMetrics = null;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnServiceProtos.GetClusterMetricsResponseProto getProto() {
        this.mergeLocalToProto();
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((GetClusterMetricsResponsePBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    private void mergeLocalToBuilder() {
        if (this.yarnClusterMetrics != null) {
            this.builder.setClusterMetrics(this.convertToProtoFormat(this.yarnClusterMetrics));
        }
    }
    
    private void mergeLocalToProto() {
        if (this.viaProto) {
            this.maybeInitBuilder();
        }
        this.mergeLocalToBuilder();
        this.proto = this.builder.build();
        this.viaProto = true;
    }
    
    private void maybeInitBuilder() {
        if (this.viaProto || this.builder == null) {
            this.builder = YarnServiceProtos.GetClusterMetricsResponseProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public YarnClusterMetrics getClusterMetrics() {
        final YarnServiceProtos.GetClusterMetricsResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.yarnClusterMetrics != null) {
            return this.yarnClusterMetrics;
        }
        if (!p.hasClusterMetrics()) {
            return null;
        }
        return this.yarnClusterMetrics = this.convertFromProtoFormat(p.getClusterMetrics());
    }
    
    @Override
    public void setClusterMetrics(final YarnClusterMetrics clusterMetrics) {
        this.maybeInitBuilder();
        if (clusterMetrics == null) {
            this.builder.clearClusterMetrics();
        }
        this.yarnClusterMetrics = clusterMetrics;
    }
    
    private YarnClusterMetricsPBImpl convertFromProtoFormat(final YarnProtos.YarnClusterMetricsProto p) {
        return new YarnClusterMetricsPBImpl(p);
    }
    
    private YarnProtos.YarnClusterMetricsProto convertToProtoFormat(final YarnClusterMetrics t) {
        return ((YarnClusterMetricsPBImpl)t).getProto();
    }
}
