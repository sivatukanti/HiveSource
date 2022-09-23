// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb;

import java.util.Collection;
import java.util.HashSet;
import org.apache.hadoop.yarn.proto.YarnServerResourceManagerServiceProtos;
import java.util.Set;
import org.apache.hadoop.yarn.server.api.protocolrecords.RemoveFromClusterNodeLabelsRequest;

public class RemoveFromClusterNodeLabelsRequestPBImpl extends RemoveFromClusterNodeLabelsRequest
{
    Set<String> labels;
    YarnServerResourceManagerServiceProtos.RemoveFromClusterNodeLabelsRequestProto proto;
    YarnServerResourceManagerServiceProtos.RemoveFromClusterNodeLabelsRequestProto.Builder builder;
    boolean viaProto;
    
    public RemoveFromClusterNodeLabelsRequestPBImpl() {
        this.proto = YarnServerResourceManagerServiceProtos.RemoveFromClusterNodeLabelsRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = YarnServerResourceManagerServiceProtos.RemoveFromClusterNodeLabelsRequestProto.newBuilder();
    }
    
    public RemoveFromClusterNodeLabelsRequestPBImpl(final YarnServerResourceManagerServiceProtos.RemoveFromClusterNodeLabelsRequestProto proto) {
        this.proto = YarnServerResourceManagerServiceProtos.RemoveFromClusterNodeLabelsRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    private void maybeInitBuilder() {
        if (this.viaProto || this.builder == null) {
            this.builder = YarnServerResourceManagerServiceProtos.RemoveFromClusterNodeLabelsRequestProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    private void mergeLocalToBuilder() {
        if (this.labels != null && !this.labels.isEmpty()) {
            this.builder.clearNodeLabels();
            this.builder.addAllNodeLabels(this.labels);
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
    
    public YarnServerResourceManagerServiceProtos.RemoveFromClusterNodeLabelsRequestProto getProto() {
        this.mergeLocalToProto();
        this.proto = (this.viaProto ? this.proto : this.builder.build());
        this.viaProto = true;
        return this.proto;
    }
    
    private void initNodeLabels() {
        if (this.labels != null) {
            return;
        }
        final YarnServerResourceManagerServiceProtos.RemoveFromClusterNodeLabelsRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        (this.labels = new HashSet<String>()).addAll(p.getNodeLabelsList());
    }
    
    @Override
    public void setNodeLabels(final Set<String> labels) {
        this.maybeInitBuilder();
        if (labels == null || labels.isEmpty()) {
            this.builder.clearNodeLabels();
        }
        this.labels = labels;
    }
    
    @Override
    public Set<String> getNodeLabels() {
        this.initNodeLabels();
        return this.labels;
    }
    
    @Override
    public int hashCode() {
        assert false : "hashCode not designed";
        return 0;
    }
    
    @Override
    public boolean equals(final Object other) {
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((RemoveFromClusterNodeLabelsRequestPBImpl)this.getClass().cast(other)).getProto());
    }
}
