// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records.impl.pb;

import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.proto.YarnProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.records.YarnClusterMetrics;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class YarnClusterMetricsPBImpl extends YarnClusterMetrics
{
    YarnProtos.YarnClusterMetricsProto proto;
    YarnProtos.YarnClusterMetricsProto.Builder builder;
    boolean viaProto;
    
    public YarnClusterMetricsPBImpl() {
        this.proto = YarnProtos.YarnClusterMetricsProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = YarnProtos.YarnClusterMetricsProto.newBuilder();
    }
    
    public YarnClusterMetricsPBImpl(final YarnProtos.YarnClusterMetricsProto proto) {
        this.proto = YarnProtos.YarnClusterMetricsProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnProtos.YarnClusterMetricsProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((YarnClusterMetricsPBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    private void maybeInitBuilder() {
        if (this.viaProto || this.builder == null) {
            this.builder = YarnProtos.YarnClusterMetricsProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public int getNumNodeManagers() {
        final YarnProtos.YarnClusterMetricsProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getNumNodeManagers();
    }
    
    @Override
    public void setNumNodeManagers(final int numNodeManagers) {
        this.maybeInitBuilder();
        this.builder.setNumNodeManagers(numNodeManagers);
    }
}
