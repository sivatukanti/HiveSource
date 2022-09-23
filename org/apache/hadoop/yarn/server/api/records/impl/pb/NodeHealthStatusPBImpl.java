// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.api.records.impl.pb;

import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.proto.YarnServerCommonProtos;
import org.apache.hadoop.yarn.server.api.records.NodeHealthStatus;

public class NodeHealthStatusPBImpl extends NodeHealthStatus
{
    private YarnServerCommonProtos.NodeHealthStatusProto.Builder builder;
    private boolean viaProto;
    private YarnServerCommonProtos.NodeHealthStatusProto proto;
    
    public NodeHealthStatusPBImpl() {
        this.viaProto = false;
        this.proto = YarnServerCommonProtos.NodeHealthStatusProto.getDefaultInstance();
        this.builder = YarnServerCommonProtos.NodeHealthStatusProto.newBuilder();
    }
    
    public NodeHealthStatusPBImpl(final YarnServerCommonProtos.NodeHealthStatusProto proto) {
        this.viaProto = false;
        this.proto = YarnServerCommonProtos.NodeHealthStatusProto.getDefaultInstance();
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnServerCommonProtos.NodeHealthStatusProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((NodeHealthStatusPBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    private void mergeLocalToProto() {
        if (this.viaProto) {
            this.maybeInitBuilder();
        }
        this.proto = this.builder.build();
        this.viaProto = true;
    }
    
    private void maybeInitBuilder() {
        if (this.viaProto || this.builder == null) {
            this.builder = YarnServerCommonProtos.NodeHealthStatusProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public boolean getIsNodeHealthy() {
        final YarnServerCommonProtos.NodeHealthStatusProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getIsNodeHealthy();
    }
    
    @Override
    public void setIsNodeHealthy(final boolean isNodeHealthy) {
        this.maybeInitBuilder();
        this.builder.setIsNodeHealthy(isNodeHealthy);
    }
    
    @Override
    public String getHealthReport() {
        final YarnServerCommonProtos.NodeHealthStatusProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasHealthReport()) {
            return null;
        }
        return p.getHealthReport();
    }
    
    @Override
    public void setHealthReport(final String healthReport) {
        this.maybeInitBuilder();
        if (healthReport == null) {
            this.builder.clearHealthReport();
            return;
        }
        this.builder.setHealthReport(healthReport);
    }
    
    @Override
    public long getLastHealthReportTime() {
        final YarnServerCommonProtos.NodeHealthStatusProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getLastHealthReportTime();
    }
    
    @Override
    public void setLastHealthReportTime(final long lastHealthReport) {
        this.maybeInitBuilder();
        this.builder.setLastHealthReportTime(lastHealthReport);
    }
}
