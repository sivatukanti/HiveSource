// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords.impl.pb;

import org.apache.hadoop.yarn.api.records.impl.pb.SerializedExceptionPBImpl;
import org.apache.hadoop.yarn.api.records.impl.pb.ContainerIdPBImpl;
import org.apache.hadoop.yarn.api.records.impl.pb.ContainerStatusPBImpl;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.hadoop.yarn.proto.YarnProtos;
import java.util.ArrayList;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.api.records.SerializedException;
import org.apache.hadoop.yarn.api.records.ContainerId;
import java.util.Map;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import java.util.List;
import org.apache.hadoop.yarn.proto.YarnServiceProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainerStatusesResponse;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class GetContainerStatusesResponsePBImpl extends GetContainerStatusesResponse
{
    YarnServiceProtos.GetContainerStatusesResponseProto proto;
    YarnServiceProtos.GetContainerStatusesResponseProto.Builder builder;
    boolean viaProto;
    private List<ContainerStatus> containerStatuses;
    private Map<ContainerId, SerializedException> failedRequests;
    
    public GetContainerStatusesResponsePBImpl() {
        this.proto = YarnServiceProtos.GetContainerStatusesResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.containerStatuses = null;
        this.failedRequests = null;
        this.builder = YarnServiceProtos.GetContainerStatusesResponseProto.newBuilder();
    }
    
    public GetContainerStatusesResponsePBImpl(final YarnServiceProtos.GetContainerStatusesResponseProto proto) {
        this.proto = YarnServiceProtos.GetContainerStatusesResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.containerStatuses = null;
        this.failedRequests = null;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnServiceProtos.GetContainerStatusesResponseProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((GetContainerStatusesResponsePBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    private void mergeLocalToBuilder() {
        if (this.containerStatuses != null) {
            this.addLocalContainerStatusesToProto();
        }
        if (this.failedRequests != null) {
            this.addFailedRequestsToProto();
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
            this.builder = YarnServiceProtos.GetContainerStatusesResponseProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    private void addLocalContainerStatusesToProto() {
        this.maybeInitBuilder();
        this.builder.clearStatus();
        if (this.containerStatuses == null) {
            return;
        }
        final List<YarnProtos.ContainerStatusProto> protoList = new ArrayList<YarnProtos.ContainerStatusProto>();
        for (final ContainerStatus status : this.containerStatuses) {
            protoList.add(this.convertToProtoFormat(status));
        }
        this.builder.addAllStatus(protoList);
    }
    
    private void addFailedRequestsToProto() {
        this.maybeInitBuilder();
        this.builder.clearFailedRequests();
        if (this.failedRequests == null) {
            return;
        }
        final List<YarnServiceProtos.ContainerExceptionMapProto> protoList = new ArrayList<YarnServiceProtos.ContainerExceptionMapProto>();
        for (final Map.Entry<ContainerId, SerializedException> entry : this.failedRequests.entrySet()) {
            protoList.add(YarnServiceProtos.ContainerExceptionMapProto.newBuilder().setContainerId(this.convertToProtoFormat(entry.getKey())).setException(this.convertToProtoFormat(entry.getValue())).build());
        }
        this.builder.addAllFailedRequests(protoList);
    }
    
    private void initLocalContainerStatuses() {
        if (this.containerStatuses != null) {
            return;
        }
        final YarnServiceProtos.GetContainerStatusesResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<YarnProtos.ContainerStatusProto> statuses = p.getStatusList();
        this.containerStatuses = new ArrayList<ContainerStatus>();
        for (final YarnProtos.ContainerStatusProto status : statuses) {
            this.containerStatuses.add(this.convertFromProtoFormat(status));
        }
    }
    
    private void initFailedRequests() {
        if (this.failedRequests != null) {
            return;
        }
        final YarnServiceProtos.GetContainerStatusesResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<YarnServiceProtos.ContainerExceptionMapProto> protoList = p.getFailedRequestsList();
        this.failedRequests = new HashMap<ContainerId, SerializedException>();
        for (final YarnServiceProtos.ContainerExceptionMapProto ce : protoList) {
            this.failedRequests.put(this.convertFromProtoFormat(ce.getContainerId()), this.convertFromProtoFormat(ce.getException()));
        }
    }
    
    @Override
    public List<ContainerStatus> getContainerStatuses() {
        this.initLocalContainerStatuses();
        return this.containerStatuses;
    }
    
    @Override
    public void setContainerStatuses(final List<ContainerStatus> statuses) {
        this.maybeInitBuilder();
        if (statuses == null) {
            this.builder.clearStatus();
        }
        this.containerStatuses = statuses;
    }
    
    @Override
    public Map<ContainerId, SerializedException> getFailedRequests() {
        this.initFailedRequests();
        return this.failedRequests;
    }
    
    @Override
    public void setFailedRequests(final Map<ContainerId, SerializedException> failedRequests) {
        this.maybeInitBuilder();
        if (failedRequests == null) {
            this.builder.clearFailedRequests();
        }
        this.failedRequests = failedRequests;
    }
    
    private ContainerStatusPBImpl convertFromProtoFormat(final YarnProtos.ContainerStatusProto p) {
        return new ContainerStatusPBImpl(p);
    }
    
    private YarnProtos.ContainerStatusProto convertToProtoFormat(final ContainerStatus t) {
        return ((ContainerStatusPBImpl)t).getProto();
    }
    
    private ContainerIdPBImpl convertFromProtoFormat(final YarnProtos.ContainerIdProto p) {
        return new ContainerIdPBImpl(p);
    }
    
    private YarnProtos.ContainerIdProto convertToProtoFormat(final ContainerId t) {
        return ((ContainerIdPBImpl)t).getProto();
    }
    
    private SerializedExceptionPBImpl convertFromProtoFormat(final YarnProtos.SerializedExceptionProto p) {
        return new SerializedExceptionPBImpl(p);
    }
    
    private YarnProtos.SerializedExceptionProto convertToProtoFormat(final SerializedException t) {
        return ((SerializedExceptionPBImpl)t).getProto();
    }
}
