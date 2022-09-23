// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records.impl.pb;

import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.proto.YarnProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.records.PreemptionContainer;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class PreemptionContainerPBImpl extends PreemptionContainer
{
    YarnProtos.PreemptionContainerProto proto;
    YarnProtos.PreemptionContainerProto.Builder builder;
    boolean viaProto;
    private ContainerId id;
    
    public PreemptionContainerPBImpl() {
        this.proto = YarnProtos.PreemptionContainerProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = YarnProtos.PreemptionContainerProto.newBuilder();
    }
    
    public PreemptionContainerPBImpl(final YarnProtos.PreemptionContainerProto proto) {
        this.proto = YarnProtos.PreemptionContainerProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public synchronized YarnProtos.PreemptionContainerProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((PreemptionContainerPBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    private void mergeLocalToProto() {
        if (this.viaProto) {
            this.maybeInitBuilder();
        }
        this.mergeLocalToBuilder();
        this.proto = this.builder.build();
        this.viaProto = true;
    }
    
    private void mergeLocalToBuilder() {
        if (this.id != null) {
            this.builder.setId(this.convertToProtoFormat(this.id));
        }
    }
    
    private void maybeInitBuilder() {
        if (this.viaProto || this.builder == null) {
            this.builder = YarnProtos.PreemptionContainerProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public synchronized ContainerId getId() {
        final YarnProtos.PreemptionContainerProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.id != null) {
            return this.id;
        }
        if (!p.hasId()) {
            return null;
        }
        return this.id = this.convertFromProtoFormat(p.getId());
    }
    
    @Override
    public synchronized void setId(final ContainerId id) {
        this.maybeInitBuilder();
        if (null == id) {
            this.builder.clearId();
        }
        this.id = id;
    }
    
    private ContainerIdPBImpl convertFromProtoFormat(final YarnProtos.ContainerIdProto p) {
        return new ContainerIdPBImpl(p);
    }
    
    private YarnProtos.ContainerIdProto convertToProtoFormat(final ContainerId t) {
        return ((ContainerIdPBImpl)t).getProto();
    }
}
