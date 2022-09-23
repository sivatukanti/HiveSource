// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records.impl.pb;

import java.util.HashSet;
import java.util.Iterator;
import java.util.ArrayList;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.api.records.PreemptionResourceRequest;
import java.util.List;
import org.apache.hadoop.yarn.api.records.PreemptionContainer;
import java.util.Set;
import org.apache.hadoop.yarn.proto.YarnProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.records.PreemptionContract;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class PreemptionContractPBImpl extends PreemptionContract
{
    YarnProtos.PreemptionContractProto proto;
    YarnProtos.PreemptionContractProto.Builder builder;
    boolean viaProto;
    private Set<PreemptionContainer> containers;
    private List<PreemptionResourceRequest> resources;
    
    public PreemptionContractPBImpl() {
        this.proto = YarnProtos.PreemptionContractProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = YarnProtos.PreemptionContractProto.newBuilder();
    }
    
    public PreemptionContractPBImpl(final YarnProtos.PreemptionContractProto proto) {
        this.proto = YarnProtos.PreemptionContractProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public synchronized YarnProtos.PreemptionContractProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((PreemptionContractPBImpl)this.getClass().cast(other)).getProto());
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
        if (this.resources != null) {
            this.addResourcesToProto();
        }
        if (this.containers != null) {
            this.addContainersToProto();
        }
    }
    
    private void maybeInitBuilder() {
        if (this.viaProto || this.builder == null) {
            this.builder = YarnProtos.PreemptionContractProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public synchronized Set<PreemptionContainer> getContainers() {
        this.initPreemptionContainers();
        return this.containers;
    }
    
    @Override
    public synchronized void setContainers(final Set<PreemptionContainer> containers) {
        if (null == containers) {
            this.builder.clearContainer();
        }
        this.containers = containers;
    }
    
    @Override
    public synchronized List<PreemptionResourceRequest> getResourceRequest() {
        this.initPreemptionResourceRequests();
        return this.resources;
    }
    
    @Override
    public synchronized void setResourceRequest(final List<PreemptionResourceRequest> req) {
        if (null == this.resources) {
            this.builder.clearResource();
        }
        this.resources = req;
    }
    
    private void initPreemptionResourceRequests() {
        if (this.resources != null) {
            return;
        }
        final YarnProtos.PreemptionContractProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<YarnProtos.PreemptionResourceRequestProto> list = p.getResourceList();
        this.resources = new ArrayList<PreemptionResourceRequest>();
        for (final YarnProtos.PreemptionResourceRequestProto rr : list) {
            this.resources.add(this.convertFromProtoFormat(rr));
        }
    }
    
    private void addResourcesToProto() {
        this.maybeInitBuilder();
        this.builder.clearResource();
        if (null == this.resources) {
            return;
        }
        final Iterable<YarnProtos.PreemptionResourceRequestProto> iterable = new Iterable<YarnProtos.PreemptionResourceRequestProto>() {
            @Override
            public Iterator<YarnProtos.PreemptionResourceRequestProto> iterator() {
                return new Iterator<YarnProtos.PreemptionResourceRequestProto>() {
                    Iterator<PreemptionResourceRequest> iter = PreemptionContractPBImpl.this.resources.iterator();
                    
                    @Override
                    public boolean hasNext() {
                        return this.iter.hasNext();
                    }
                    
                    @Override
                    public YarnProtos.PreemptionResourceRequestProto next() {
                        return PreemptionContractPBImpl.this.convertToProtoFormat(this.iter.next());
                    }
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
        this.builder.addAllResource(iterable);
    }
    
    private void initPreemptionContainers() {
        if (this.containers != null) {
            return;
        }
        final YarnProtos.PreemptionContractProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<YarnProtos.PreemptionContainerProto> list = p.getContainerList();
        this.containers = new HashSet<PreemptionContainer>();
        for (final YarnProtos.PreemptionContainerProto c : list) {
            this.containers.add(this.convertFromProtoFormat(c));
        }
    }
    
    private void addContainersToProto() {
        this.maybeInitBuilder();
        this.builder.clearContainer();
        if (null == this.containers) {
            return;
        }
        final Iterable<YarnProtos.PreemptionContainerProto> iterable = new Iterable<YarnProtos.PreemptionContainerProto>() {
            @Override
            public Iterator<YarnProtos.PreemptionContainerProto> iterator() {
                return new Iterator<YarnProtos.PreemptionContainerProto>() {
                    Iterator<PreemptionContainer> iter = PreemptionContractPBImpl.this.containers.iterator();
                    
                    @Override
                    public boolean hasNext() {
                        return this.iter.hasNext();
                    }
                    
                    @Override
                    public YarnProtos.PreemptionContainerProto next() {
                        return PreemptionContractPBImpl.this.convertToProtoFormat(this.iter.next());
                    }
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
        this.builder.addAllContainer(iterable);
    }
    
    private PreemptionContainerPBImpl convertFromProtoFormat(final YarnProtos.PreemptionContainerProto p) {
        return new PreemptionContainerPBImpl(p);
    }
    
    private YarnProtos.PreemptionContainerProto convertToProtoFormat(final PreemptionContainer t) {
        return ((PreemptionContainerPBImpl)t).getProto();
    }
    
    private PreemptionResourceRequestPBImpl convertFromProtoFormat(final YarnProtos.PreemptionResourceRequestProto p) {
        return new PreemptionResourceRequestPBImpl(p);
    }
    
    private YarnProtos.PreemptionResourceRequestProto convertToProtoFormat(final PreemptionResourceRequest t) {
        return ((PreemptionResourceRequestPBImpl)t).getProto();
    }
}
