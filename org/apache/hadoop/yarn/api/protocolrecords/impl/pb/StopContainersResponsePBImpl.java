// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords.impl.pb;

import org.apache.hadoop.yarn.api.records.impl.pb.SerializedExceptionPBImpl;
import org.apache.hadoop.yarn.api.records.impl.pb.ContainerIdPBImpl;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.hadoop.yarn.proto.YarnProtos;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.api.records.SerializedException;
import java.util.Map;
import org.apache.hadoop.yarn.api.records.ContainerId;
import java.util.List;
import org.apache.hadoop.yarn.proto.YarnServiceProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.protocolrecords.StopContainersResponse;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class StopContainersResponsePBImpl extends StopContainersResponse
{
    YarnServiceProtos.StopContainersResponseProto proto;
    YarnServiceProtos.StopContainersResponseProto.Builder builder;
    boolean viaProto;
    private List<ContainerId> succeededRequests;
    private Map<ContainerId, SerializedException> failedRequests;
    
    public StopContainersResponsePBImpl() {
        this.proto = YarnServiceProtos.StopContainersResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.succeededRequests = null;
        this.failedRequests = null;
        this.builder = YarnServiceProtos.StopContainersResponseProto.newBuilder();
    }
    
    public StopContainersResponsePBImpl(final YarnServiceProtos.StopContainersResponseProto proto) {
        this.proto = YarnServiceProtos.StopContainersResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.succeededRequests = null;
        this.failedRequests = null;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnServiceProtos.StopContainersResponseProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((StopContainersResponsePBImpl)this.getClass().cast(other)).getProto());
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
    
    private void maybeInitBuilder() {
        if (this.viaProto || this.builder == null) {
            this.builder = YarnServiceProtos.StopContainersResponseProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    private void mergeLocalToBuilder() {
        if (this.succeededRequests != null) {
            this.addSucceededRequestsToProto();
        }
        if (this.failedRequests != null) {
            this.addFailedRequestsToProto();
        }
    }
    
    private void addSucceededRequestsToProto() {
        this.maybeInitBuilder();
        this.builder.clearSucceededRequests();
        if (this.succeededRequests == null) {
            return;
        }
        final Iterable<YarnProtos.ContainerIdProto> iterable = new Iterable<YarnProtos.ContainerIdProto>() {
            @Override
            public Iterator<YarnProtos.ContainerIdProto> iterator() {
                return new Iterator<YarnProtos.ContainerIdProto>() {
                    Iterator<ContainerId> iter = StopContainersResponsePBImpl.this.succeededRequests.iterator();
                    
                    @Override
                    public boolean hasNext() {
                        return this.iter.hasNext();
                    }
                    
                    @Override
                    public YarnProtos.ContainerIdProto next() {
                        return StopContainersResponsePBImpl.this.convertToProtoFormat(this.iter.next());
                    }
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
        this.builder.addAllSucceededRequests(iterable);
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
    
    private void initSucceededRequests() {
        if (this.succeededRequests != null) {
            return;
        }
        final YarnServiceProtos.StopContainersResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<YarnProtos.ContainerIdProto> list = p.getSucceededRequestsList();
        this.succeededRequests = new ArrayList<ContainerId>();
        for (final YarnProtos.ContainerIdProto c : list) {
            this.succeededRequests.add(this.convertFromProtoFormat(c));
        }
    }
    
    private void initFailedRequests() {
        if (this.failedRequests != null) {
            return;
        }
        final YarnServiceProtos.StopContainersResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<YarnServiceProtos.ContainerExceptionMapProto> protoList = p.getFailedRequestsList();
        this.failedRequests = new HashMap<ContainerId, SerializedException>();
        for (final YarnServiceProtos.ContainerExceptionMapProto ce : protoList) {
            this.failedRequests.put(this.convertFromProtoFormat(ce.getContainerId()), this.convertFromProtoFormat(ce.getException()));
        }
    }
    
    @Override
    public List<ContainerId> getSuccessfullyStoppedContainers() {
        this.initSucceededRequests();
        return this.succeededRequests;
    }
    
    @Override
    public void setSuccessfullyStoppedContainers(final List<ContainerId> succeededRequests) {
        this.maybeInitBuilder();
        if (succeededRequests == null) {
            this.builder.clearSucceededRequests();
        }
        this.succeededRequests = succeededRequests;
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
