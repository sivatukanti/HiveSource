// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords.impl.pb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import org.apache.hadoop.yarn.api.records.impl.pb.SerializedExceptionPBImpl;
import org.apache.hadoop.yarn.api.records.impl.pb.ContainerIdPBImpl;
import org.apache.hadoop.yarn.proto.YarnProtos;
import org.apache.hadoop.yarn.api.records.impl.pb.ProtoUtils;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.api.records.SerializedException;
import org.apache.hadoop.yarn.api.records.ContainerId;
import java.util.List;
import java.nio.ByteBuffer;
import java.util.Map;
import org.apache.hadoop.yarn.proto.YarnServiceProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.protocolrecords.StartContainersResponse;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class StartContainersResponsePBImpl extends StartContainersResponse
{
    YarnServiceProtos.StartContainersResponseProto proto;
    YarnServiceProtos.StartContainersResponseProto.Builder builder;
    boolean viaProto;
    private Map<String, ByteBuffer> servicesMetaData;
    private List<ContainerId> succeededContainers;
    private Map<ContainerId, SerializedException> failedContainers;
    
    public StartContainersResponsePBImpl() {
        this.proto = YarnServiceProtos.StartContainersResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.servicesMetaData = null;
        this.succeededContainers = null;
        this.failedContainers = null;
        this.builder = YarnServiceProtos.StartContainersResponseProto.newBuilder();
    }
    
    public StartContainersResponsePBImpl(final YarnServiceProtos.StartContainersResponseProto proto) {
        this.proto = YarnServiceProtos.StartContainersResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.servicesMetaData = null;
        this.succeededContainers = null;
        this.failedContainers = null;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnServiceProtos.StartContainersResponseProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((StartContainersResponsePBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    private void mergeLocalToBuilder() {
        if (this.servicesMetaData != null) {
            this.addServicesMetaDataToProto();
        }
        if (this.succeededContainers != null) {
            this.addSucceededContainersToProto();
        }
        if (this.failedContainers != null) {
            this.addFailedContainersToProto();
        }
    }
    
    protected final ByteBuffer convertFromProtoFormat(final ByteString byteString) {
        return ProtoUtils.convertFromProtoFormat(byteString);
    }
    
    protected final ByteString convertToProtoFormat(final ByteBuffer byteBuffer) {
        return ProtoUtils.convertToProtoFormat(byteBuffer);
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
            this.builder = YarnServiceProtos.StartContainersResponseProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public Map<String, ByteBuffer> getAllServicesMetaData() {
        this.initServicesMetaData();
        return this.servicesMetaData;
    }
    
    @Override
    public void setAllServicesMetaData(final Map<String, ByteBuffer> servicesMetaData) {
        if (servicesMetaData == null) {
            return;
        }
        this.initServicesMetaData();
        this.servicesMetaData.clear();
        this.servicesMetaData.putAll(servicesMetaData);
    }
    
    private void initServicesMetaData() {
        if (this.servicesMetaData != null) {
            return;
        }
        final YarnServiceProtos.StartContainersResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<YarnProtos.StringBytesMapProto> list = p.getServicesMetaDataList();
        this.servicesMetaData = new HashMap<String, ByteBuffer>();
        for (final YarnProtos.StringBytesMapProto c : list) {
            this.servicesMetaData.put(c.getKey(), this.convertFromProtoFormat(c.getValue()));
        }
    }
    
    private void addServicesMetaDataToProto() {
        this.maybeInitBuilder();
        this.builder.clearServicesMetaData();
        if (this.servicesMetaData == null) {
            return;
        }
        final Iterable<YarnProtos.StringBytesMapProto> iterable = new Iterable<YarnProtos.StringBytesMapProto>() {
            @Override
            public Iterator<YarnProtos.StringBytesMapProto> iterator() {
                return new Iterator<YarnProtos.StringBytesMapProto>() {
                    Iterator<String> keyIter = StartContainersResponsePBImpl.this.servicesMetaData.keySet().iterator();
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                    
                    @Override
                    public YarnProtos.StringBytesMapProto next() {
                        final String key = this.keyIter.next();
                        return YarnProtos.StringBytesMapProto.newBuilder().setKey(key).setValue(StartContainersResponsePBImpl.this.convertToProtoFormat(StartContainersResponsePBImpl.this.servicesMetaData.get(key))).build();
                    }
                    
                    @Override
                    public boolean hasNext() {
                        return this.keyIter.hasNext();
                    }
                };
            }
        };
        this.builder.addAllServicesMetaData(iterable);
    }
    
    private void addFailedContainersToProto() {
        this.maybeInitBuilder();
        this.builder.clearFailedRequests();
        if (this.failedContainers == null) {
            return;
        }
        final List<YarnServiceProtos.ContainerExceptionMapProto> protoList = new ArrayList<YarnServiceProtos.ContainerExceptionMapProto>();
        for (final Map.Entry<ContainerId, SerializedException> entry : this.failedContainers.entrySet()) {
            protoList.add(YarnServiceProtos.ContainerExceptionMapProto.newBuilder().setContainerId(this.convertToProtoFormat(entry.getKey())).setException(this.convertToProtoFormat(entry.getValue())).build());
        }
        this.builder.addAllFailedRequests(protoList);
    }
    
    private void addSucceededContainersToProto() {
        this.maybeInitBuilder();
        this.builder.clearSucceededRequests();
        if (this.succeededContainers == null) {
            return;
        }
        final Iterable<YarnProtos.ContainerIdProto> iterable = new Iterable<YarnProtos.ContainerIdProto>() {
            @Override
            public Iterator<YarnProtos.ContainerIdProto> iterator() {
                return new Iterator<YarnProtos.ContainerIdProto>() {
                    Iterator<ContainerId> iter = StartContainersResponsePBImpl.this.succeededContainers.iterator();
                    
                    @Override
                    public boolean hasNext() {
                        return this.iter.hasNext();
                    }
                    
                    @Override
                    public YarnProtos.ContainerIdProto next() {
                        return StartContainersResponsePBImpl.this.convertToProtoFormat(this.iter.next());
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
    
    private void initSucceededContainers() {
        if (this.succeededContainers != null) {
            return;
        }
        final YarnServiceProtos.StartContainersResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<YarnProtos.ContainerIdProto> list = p.getSucceededRequestsList();
        this.succeededContainers = new ArrayList<ContainerId>();
        for (final YarnProtos.ContainerIdProto c : list) {
            this.succeededContainers.add(this.convertFromProtoFormat(c));
        }
    }
    
    @Override
    public List<ContainerId> getSuccessfullyStartedContainers() {
        this.initSucceededContainers();
        return this.succeededContainers;
    }
    
    @Override
    public void setSuccessfullyStartedContainers(final List<ContainerId> succeededContainers) {
        this.maybeInitBuilder();
        if (succeededContainers == null) {
            this.builder.clearSucceededRequests();
        }
        this.succeededContainers = succeededContainers;
    }
    
    private void initFailedContainers() {
        if (this.failedContainers != null) {
            return;
        }
        final YarnServiceProtos.StartContainersResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<YarnServiceProtos.ContainerExceptionMapProto> protoList = p.getFailedRequestsList();
        this.failedContainers = new HashMap<ContainerId, SerializedException>();
        for (final YarnServiceProtos.ContainerExceptionMapProto ce : protoList) {
            this.failedContainers.put(this.convertFromProtoFormat(ce.getContainerId()), this.convertFromProtoFormat(ce.getException()));
        }
    }
    
    @Override
    public Map<ContainerId, SerializedException> getFailedRequests() {
        this.initFailedContainers();
        return this.failedContainers;
    }
    
    @Override
    public void setFailedRequests(final Map<ContainerId, SerializedException> failedContainers) {
        this.maybeInitBuilder();
        if (failedContainers == null) {
            this.builder.clearFailedRequests();
        }
        this.failedContainers = failedContainers;
    }
}
