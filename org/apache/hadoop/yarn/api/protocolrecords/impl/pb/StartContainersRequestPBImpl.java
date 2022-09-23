// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords.impl.pb;

import java.util.Iterator;
import java.util.ArrayList;
import org.apache.hadoop.yarn.api.protocolrecords.StartContainerRequest;
import java.util.List;
import org.apache.hadoop.yarn.proto.YarnServiceProtos;
import org.apache.hadoop.yarn.api.protocolrecords.StartContainersRequest;

public class StartContainersRequestPBImpl extends StartContainersRequest
{
    YarnServiceProtos.StartContainersRequestProto proto;
    YarnServiceProtos.StartContainersRequestProto.Builder builder;
    boolean viaProto;
    private List<StartContainerRequest> requests;
    
    public StartContainersRequestPBImpl() {
        this.proto = YarnServiceProtos.StartContainersRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.requests = null;
        this.builder = YarnServiceProtos.StartContainersRequestProto.newBuilder();
    }
    
    public StartContainersRequestPBImpl(final YarnServiceProtos.StartContainersRequestProto proto) {
        this.proto = YarnServiceProtos.StartContainersRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.requests = null;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnServiceProtos.StartContainersRequestProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((StartContainersRequestPBImpl)this.getClass().cast(other)).getProto());
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
        if (this.requests != null) {
            this.addLocalRequestsToProto();
        }
    }
    
    private void maybeInitBuilder() {
        if (this.viaProto || this.builder == null) {
            this.builder = YarnServiceProtos.StartContainersRequestProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    private void addLocalRequestsToProto() {
        this.maybeInitBuilder();
        this.builder.clearStartContainerRequest();
        final List<YarnServiceProtos.StartContainerRequestProto> protoList = new ArrayList<YarnServiceProtos.StartContainerRequestProto>();
        for (final StartContainerRequest r : this.requests) {
            protoList.add(this.convertToProtoFormat(r));
        }
        this.builder.addAllStartContainerRequest(protoList);
    }
    
    private void initLocalRequests() {
        final YarnServiceProtos.StartContainersRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<YarnServiceProtos.StartContainerRequestProto> requestList = p.getStartContainerRequestList();
        this.requests = new ArrayList<StartContainerRequest>();
        for (final YarnServiceProtos.StartContainerRequestProto r : requestList) {
            this.requests.add(this.convertFromProtoFormat(r));
        }
    }
    
    @Override
    public void setStartContainerRequests(final List<StartContainerRequest> requests) {
        this.maybeInitBuilder();
        if (requests == null) {
            this.builder.clearStartContainerRequest();
        }
        this.requests = requests;
    }
    
    @Override
    public List<StartContainerRequest> getStartContainerRequests() {
        if (this.requests != null) {
            return this.requests;
        }
        this.initLocalRequests();
        return this.requests;
    }
    
    private StartContainerRequestPBImpl convertFromProtoFormat(final YarnServiceProtos.StartContainerRequestProto p) {
        return new StartContainerRequestPBImpl(p);
    }
    
    private YarnServiceProtos.StartContainerRequestProto convertToProtoFormat(final StartContainerRequest t) {
        return ((StartContainerRequestPBImpl)t).getProto();
    }
}
