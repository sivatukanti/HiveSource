// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords.impl.pb;

import org.apache.hadoop.yarn.api.records.impl.pb.ContainerIdPBImpl;
import java.util.Iterator;
import org.apache.hadoop.yarn.proto.YarnProtos;
import java.util.ArrayList;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.api.records.ContainerId;
import java.util.List;
import org.apache.hadoop.yarn.proto.YarnServiceProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.protocolrecords.StopContainersRequest;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class StopContainersRequestPBImpl extends StopContainersRequest
{
    YarnServiceProtos.StopContainersRequestProto proto;
    YarnServiceProtos.StopContainersRequestProto.Builder builder;
    boolean viaProto;
    private List<ContainerId> containerIds;
    
    public StopContainersRequestPBImpl() {
        this.proto = YarnServiceProtos.StopContainersRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.containerIds = null;
        this.builder = YarnServiceProtos.StopContainersRequestProto.newBuilder();
    }
    
    public StopContainersRequestPBImpl(final YarnServiceProtos.StopContainersRequestProto proto) {
        this.proto = YarnServiceProtos.StopContainersRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.containerIds = null;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnServiceProtos.StopContainersRequestProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((StopContainersRequestPBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    private void mergeLocalToBuilder() {
        if (this.containerIds != null) {
            this.addLocalContainerIdsToProto();
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
            this.builder = YarnServiceProtos.StopContainersRequestProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    private void addLocalContainerIdsToProto() {
        this.maybeInitBuilder();
        this.builder.clearContainerId();
        if (this.containerIds == null) {
            return;
        }
        final List<YarnProtos.ContainerIdProto> protoList = new ArrayList<YarnProtos.ContainerIdProto>();
        for (final ContainerId id : this.containerIds) {
            protoList.add(this.convertToProtoFormat(id));
        }
        this.builder.addAllContainerId(protoList);
    }
    
    private void initLocalContainerIds() {
        if (this.containerIds != null) {
            return;
        }
        final YarnServiceProtos.StopContainersRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<YarnProtos.ContainerIdProto> containerIds = p.getContainerIdList();
        this.containerIds = new ArrayList<ContainerId>();
        for (final YarnProtos.ContainerIdProto id : containerIds) {
            this.containerIds.add(this.convertFromProtoFormat(id));
        }
    }
    
    @Override
    public List<ContainerId> getContainerIds() {
        this.initLocalContainerIds();
        return this.containerIds;
    }
    
    @Override
    public void setContainerIds(final List<ContainerId> containerIds) {
        this.maybeInitBuilder();
        if (containerIds == null) {
            this.builder.clearContainerId();
        }
        this.containerIds = containerIds;
    }
    
    private ContainerIdPBImpl convertFromProtoFormat(final YarnProtos.ContainerIdProto p) {
        return new ContainerIdPBImpl(p);
    }
    
    private YarnProtos.ContainerIdProto convertToProtoFormat(final ContainerId t) {
        return ((ContainerIdPBImpl)t).getProto();
    }
}
