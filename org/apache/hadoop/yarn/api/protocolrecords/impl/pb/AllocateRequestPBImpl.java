// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords.impl.pb;

import org.apache.hadoop.yarn.api.records.impl.pb.ResourceBlacklistRequestPBImpl;
import org.apache.hadoop.yarn.api.records.impl.pb.ContainerIdPBImpl;
import org.apache.hadoop.yarn.api.records.impl.pb.ContainerResourceIncreaseRequestPBImpl;
import org.apache.hadoop.yarn.api.records.impl.pb.ResourceRequestPBImpl;
import java.util.Iterator;
import org.apache.hadoop.yarn.proto.YarnProtos;
import java.util.ArrayList;
import java.util.Collection;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.api.records.ResourceBlacklistRequest;
import org.apache.hadoop.yarn.api.records.ContainerResourceIncreaseRequest;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ResourceRequest;
import java.util.List;
import org.apache.hadoop.yarn.proto.YarnServiceProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateRequest;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class AllocateRequestPBImpl extends AllocateRequest
{
    YarnServiceProtos.AllocateRequestProto proto;
    YarnServiceProtos.AllocateRequestProto.Builder builder;
    boolean viaProto;
    private List<ResourceRequest> ask;
    private List<ContainerId> release;
    private List<ContainerResourceIncreaseRequest> increaseRequests;
    private ResourceBlacklistRequest blacklistRequest;
    
    public AllocateRequestPBImpl() {
        this.proto = YarnServiceProtos.AllocateRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.ask = null;
        this.release = null;
        this.increaseRequests = null;
        this.blacklistRequest = null;
        this.builder = YarnServiceProtos.AllocateRequestProto.newBuilder();
    }
    
    public AllocateRequestPBImpl(final YarnServiceProtos.AllocateRequestProto proto) {
        this.proto = YarnServiceProtos.AllocateRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.ask = null;
        this.release = null;
        this.increaseRequests = null;
        this.blacklistRequest = null;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnServiceProtos.AllocateRequestProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((AllocateRequestPBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    private void mergeLocalToBuilder() {
        if (this.ask != null) {
            this.addAsksToProto();
        }
        if (this.release != null) {
            this.addReleasesToProto();
        }
        if (this.increaseRequests != null) {
            this.addIncreaseRequestsToProto();
        }
        if (this.blacklistRequest != null) {
            this.builder.setBlacklistRequest(this.convertToProtoFormat(this.blacklistRequest));
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
            this.builder = YarnServiceProtos.AllocateRequestProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public int getResponseId() {
        final YarnServiceProtos.AllocateRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getResponseId();
    }
    
    @Override
    public void setResponseId(final int id) {
        this.maybeInitBuilder();
        this.builder.setResponseId(id);
    }
    
    @Override
    public float getProgress() {
        final YarnServiceProtos.AllocateRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getProgress();
    }
    
    @Override
    public void setProgress(final float progress) {
        this.maybeInitBuilder();
        this.builder.setProgress(progress);
    }
    
    @Override
    public List<ResourceRequest> getAskList() {
        this.initAsks();
        return this.ask;
    }
    
    @Override
    public void setAskList(final List<ResourceRequest> resourceRequests) {
        if (resourceRequests == null) {
            return;
        }
        this.initAsks();
        this.ask.clear();
        this.ask.addAll(resourceRequests);
    }
    
    @Override
    public List<ContainerResourceIncreaseRequest> getIncreaseRequests() {
        this.initIncreaseRequests();
        return this.increaseRequests;
    }
    
    @Override
    public void setIncreaseRequests(final List<ContainerResourceIncreaseRequest> increaseRequests) {
        if (increaseRequests == null) {
            return;
        }
        this.initIncreaseRequests();
        this.increaseRequests.clear();
        this.increaseRequests.addAll(increaseRequests);
    }
    
    @Override
    public ResourceBlacklistRequest getResourceBlacklistRequest() {
        final YarnServiceProtos.AllocateRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.blacklistRequest != null) {
            return this.blacklistRequest;
        }
        if (!p.hasBlacklistRequest()) {
            return null;
        }
        return this.blacklistRequest = this.convertFromProtoFormat(p.getBlacklistRequest());
    }
    
    @Override
    public void setResourceBlacklistRequest(final ResourceBlacklistRequest blacklistRequest) {
        this.maybeInitBuilder();
        if (blacklistRequest == null) {
            this.builder.clearBlacklistRequest();
        }
        this.blacklistRequest = blacklistRequest;
    }
    
    private void initAsks() {
        if (this.ask != null) {
            return;
        }
        final YarnServiceProtos.AllocateRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<YarnProtos.ResourceRequestProto> list = p.getAskList();
        this.ask = new ArrayList<ResourceRequest>();
        for (final YarnProtos.ResourceRequestProto c : list) {
            this.ask.add(this.convertFromProtoFormat(c));
        }
    }
    
    private void addAsksToProto() {
        this.maybeInitBuilder();
        this.builder.clearAsk();
        if (this.ask == null) {
            return;
        }
        final Iterable<YarnProtos.ResourceRequestProto> iterable = new Iterable<YarnProtos.ResourceRequestProto>() {
            @Override
            public Iterator<YarnProtos.ResourceRequestProto> iterator() {
                return new Iterator<YarnProtos.ResourceRequestProto>() {
                    Iterator<ResourceRequest> iter = AllocateRequestPBImpl.this.ask.iterator();
                    
                    @Override
                    public boolean hasNext() {
                        return this.iter.hasNext();
                    }
                    
                    @Override
                    public YarnProtos.ResourceRequestProto next() {
                        return AllocateRequestPBImpl.this.convertToProtoFormat(this.iter.next());
                    }
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
        this.builder.addAllAsk(iterable);
    }
    
    private void initIncreaseRequests() {
        if (this.increaseRequests != null) {
            return;
        }
        final YarnServiceProtos.AllocateRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<YarnProtos.ContainerResourceIncreaseRequestProto> list = p.getIncreaseRequestList();
        this.increaseRequests = new ArrayList<ContainerResourceIncreaseRequest>();
        for (final YarnProtos.ContainerResourceIncreaseRequestProto c : list) {
            this.increaseRequests.add(this.convertFromProtoFormat(c));
        }
    }
    
    private void addIncreaseRequestsToProto() {
        this.maybeInitBuilder();
        this.builder.clearIncreaseRequest();
        if (this.increaseRequests == null) {
            return;
        }
        final Iterable<YarnProtos.ContainerResourceIncreaseRequestProto> iterable = new Iterable<YarnProtos.ContainerResourceIncreaseRequestProto>() {
            @Override
            public Iterator<YarnProtos.ContainerResourceIncreaseRequestProto> iterator() {
                return new Iterator<YarnProtos.ContainerResourceIncreaseRequestProto>() {
                    Iterator<ContainerResourceIncreaseRequest> iter = AllocateRequestPBImpl.this.increaseRequests.iterator();
                    
                    @Override
                    public boolean hasNext() {
                        return this.iter.hasNext();
                    }
                    
                    @Override
                    public YarnProtos.ContainerResourceIncreaseRequestProto next() {
                        return AllocateRequestPBImpl.this.convertToProtoFormat(this.iter.next());
                    }
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
        this.builder.addAllIncreaseRequest(iterable);
    }
    
    @Override
    public List<ContainerId> getReleaseList() {
        this.initReleases();
        return this.release;
    }
    
    @Override
    public void setReleaseList(final List<ContainerId> releaseContainers) {
        if (releaseContainers == null) {
            return;
        }
        this.initReleases();
        this.release.clear();
        this.release.addAll(releaseContainers);
    }
    
    private void initReleases() {
        if (this.release != null) {
            return;
        }
        final YarnServiceProtos.AllocateRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<YarnProtos.ContainerIdProto> list = p.getReleaseList();
        this.release = new ArrayList<ContainerId>();
        for (final YarnProtos.ContainerIdProto c : list) {
            this.release.add(this.convertFromProtoFormat(c));
        }
    }
    
    private void addReleasesToProto() {
        this.maybeInitBuilder();
        this.builder.clearRelease();
        if (this.release == null) {
            return;
        }
        final Iterable<YarnProtos.ContainerIdProto> iterable = new Iterable<YarnProtos.ContainerIdProto>() {
            @Override
            public Iterator<YarnProtos.ContainerIdProto> iterator() {
                return new Iterator<YarnProtos.ContainerIdProto>() {
                    Iterator<ContainerId> iter = AllocateRequestPBImpl.this.release.iterator();
                    
                    @Override
                    public boolean hasNext() {
                        return this.iter.hasNext();
                    }
                    
                    @Override
                    public YarnProtos.ContainerIdProto next() {
                        return AllocateRequestPBImpl.this.convertToProtoFormat(this.iter.next());
                    }
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
        this.builder.addAllRelease(iterable);
    }
    
    private ResourceRequestPBImpl convertFromProtoFormat(final YarnProtos.ResourceRequestProto p) {
        return new ResourceRequestPBImpl(p);
    }
    
    private YarnProtos.ResourceRequestProto convertToProtoFormat(final ResourceRequest t) {
        return ((ResourceRequestPBImpl)t).getProto();
    }
    
    private ContainerResourceIncreaseRequestPBImpl convertFromProtoFormat(final YarnProtos.ContainerResourceIncreaseRequestProto p) {
        return new ContainerResourceIncreaseRequestPBImpl(p);
    }
    
    private YarnProtos.ContainerResourceIncreaseRequestProto convertToProtoFormat(final ContainerResourceIncreaseRequest t) {
        return ((ContainerResourceIncreaseRequestPBImpl)t).getProto();
    }
    
    private ContainerIdPBImpl convertFromProtoFormat(final YarnProtos.ContainerIdProto p) {
        return new ContainerIdPBImpl(p);
    }
    
    private YarnProtos.ContainerIdProto convertToProtoFormat(final ContainerId t) {
        return ((ContainerIdPBImpl)t).getProto();
    }
    
    private ResourceBlacklistRequestPBImpl convertFromProtoFormat(final YarnProtos.ResourceBlacklistRequestProto p) {
        return new ResourceBlacklistRequestPBImpl(p);
    }
    
    private YarnProtos.ResourceBlacklistRequestProto convertToProtoFormat(final ResourceBlacklistRequest t) {
        return ((ResourceBlacklistRequestPBImpl)t).getProto();
    }
}
