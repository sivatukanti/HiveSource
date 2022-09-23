// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb;

import java.util.Collection;
import java.util.HashSet;
import org.apache.hadoop.yarn.proto.YarnServerResourceManagerServiceProtos;
import java.util.Set;
import org.apache.hadoop.yarn.server.api.protocolrecords.AddToClusterNodeLabelsRequest;

public class AddToClusterNodeLabelsRequestPBImpl extends AddToClusterNodeLabelsRequest
{
    Set<String> labels;
    YarnServerResourceManagerServiceProtos.AddToClusterNodeLabelsRequestProto proto;
    YarnServerResourceManagerServiceProtos.AddToClusterNodeLabelsRequestProto.Builder builder;
    boolean viaProto;
    
    public AddToClusterNodeLabelsRequestPBImpl() {
        this.proto = YarnServerResourceManagerServiceProtos.AddToClusterNodeLabelsRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = YarnServerResourceManagerServiceProtos.AddToClusterNodeLabelsRequestProto.newBuilder();
    }
    
    public AddToClusterNodeLabelsRequestPBImpl(final YarnServerResourceManagerServiceProtos.AddToClusterNodeLabelsRequestProto proto) {
        this.proto = YarnServerResourceManagerServiceProtos.AddToClusterNodeLabelsRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    private void maybeInitBuilder() {
        if (this.viaProto || this.builder == null) {
            this.builder = YarnServerResourceManagerServiceProtos.AddToClusterNodeLabelsRequestProto.newBuilder(this.proto);
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
    
    public YarnServerResourceManagerServiceProtos.AddToClusterNodeLabelsRequestProto getProto() {
        this.mergeLocalToProto();
        this.proto = (this.viaProto ? this.proto : this.builder.build());
        this.viaProto = true;
        return this.proto;
    }
    
    private void initLabels() {
        if (this.labels != null) {
            return;
        }
        final YarnServerResourceManagerServiceProtos.AddToClusterNodeLabelsRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
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
        this.initLabels();
        return this.labels;
    }
    
    @Override
    public int hashCode() {
        assert false : "hashCode not designed";
        return 0;
    }
    
    @Override
    public boolean equals(final Object other) {
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((AddToClusterNodeLabelsRequestPBImpl)this.getClass().cast(other)).getProto());
    }
}
