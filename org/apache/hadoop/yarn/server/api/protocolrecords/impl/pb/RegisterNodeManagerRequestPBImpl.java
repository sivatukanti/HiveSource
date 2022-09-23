// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb;

import org.apache.hadoop.yarn.api.records.impl.pb.ResourcePBImpl;
import org.apache.hadoop.yarn.api.records.impl.pb.NodeIdPBImpl;
import org.apache.hadoop.yarn.api.records.impl.pb.ApplicationIdPBImpl;
import java.util.Collection;
import org.apache.hadoop.yarn.proto.YarnProtos;
import java.util.Iterator;
import java.util.ArrayList;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.server.api.protocolrecords.NMContainerStatus;
import java.util.List;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.proto.YarnServerCommonServiceProtos;
import org.apache.hadoop.yarn.server.api.protocolrecords.RegisterNodeManagerRequest;

public class RegisterNodeManagerRequestPBImpl extends RegisterNodeManagerRequest
{
    YarnServerCommonServiceProtos.RegisterNodeManagerRequestProto proto;
    YarnServerCommonServiceProtos.RegisterNodeManagerRequestProto.Builder builder;
    boolean viaProto;
    private Resource resource;
    private NodeId nodeId;
    private List<NMContainerStatus> containerStatuses;
    private List<ApplicationId> runningApplications;
    
    public RegisterNodeManagerRequestPBImpl() {
        this.proto = YarnServerCommonServiceProtos.RegisterNodeManagerRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.resource = null;
        this.nodeId = null;
        this.containerStatuses = null;
        this.runningApplications = null;
        this.builder = YarnServerCommonServiceProtos.RegisterNodeManagerRequestProto.newBuilder();
    }
    
    public RegisterNodeManagerRequestPBImpl(final YarnServerCommonServiceProtos.RegisterNodeManagerRequestProto proto) {
        this.proto = YarnServerCommonServiceProtos.RegisterNodeManagerRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.resource = null;
        this.nodeId = null;
        this.containerStatuses = null;
        this.runningApplications = null;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnServerCommonServiceProtos.RegisterNodeManagerRequestProto getProto() {
        this.mergeLocalToProto();
        this.proto = (this.viaProto ? this.proto : this.builder.build());
        this.viaProto = true;
        return this.proto;
    }
    
    private void mergeLocalToBuilder() {
        if (this.containerStatuses != null) {
            this.addNMContainerStatusesToProto();
        }
        if (this.runningApplications != null) {
            this.addRunningApplicationsToProto();
        }
        if (this.resource != null) {
            this.builder.setResource(this.convertToProtoFormat(this.resource));
        }
        if (this.nodeId != null) {
            this.builder.setNodeId(this.convertToProtoFormat(this.nodeId));
        }
    }
    
    private synchronized void addNMContainerStatusesToProto() {
        this.maybeInitBuilder();
        this.builder.clearContainerStatuses();
        final List<YarnServerCommonServiceProtos.NMContainerStatusProto> list = new ArrayList<YarnServerCommonServiceProtos.NMContainerStatusProto>();
        for (final NMContainerStatus status : this.containerStatuses) {
            list.add(this.convertToProtoFormat(status));
        }
        this.builder.addAllContainerStatuses(list);
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
            this.builder = YarnServerCommonServiceProtos.RegisterNodeManagerRequestProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public Resource getResource() {
        final YarnServerCommonServiceProtos.RegisterNodeManagerRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.resource != null) {
            return this.resource;
        }
        if (!p.hasResource()) {
            return null;
        }
        return this.resource = this.convertFromProtoFormat(p.getResource());
    }
    
    @Override
    public void setResource(final Resource resource) {
        this.maybeInitBuilder();
        if (resource == null) {
            this.builder.clearResource();
        }
        this.resource = resource;
    }
    
    @Override
    public NodeId getNodeId() {
        final YarnServerCommonServiceProtos.RegisterNodeManagerRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.nodeId != null) {
            return this.nodeId;
        }
        if (!p.hasNodeId()) {
            return null;
        }
        return this.nodeId = this.convertFromProtoFormat(p.getNodeId());
    }
    
    @Override
    public void setNodeId(final NodeId nodeId) {
        this.maybeInitBuilder();
        if (nodeId == null) {
            this.builder.clearNodeId();
        }
        this.nodeId = nodeId;
    }
    
    @Override
    public int getHttpPort() {
        final YarnServerCommonServiceProtos.RegisterNodeManagerRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasHttpPort()) {
            return 0;
        }
        return p.getHttpPort();
    }
    
    @Override
    public void setHttpPort(final int httpPort) {
        this.maybeInitBuilder();
        this.builder.setHttpPort(httpPort);
    }
    
    @Override
    public List<ApplicationId> getRunningApplications() {
        this.initRunningApplications();
        return this.runningApplications;
    }
    
    private void initRunningApplications() {
        if (this.runningApplications != null) {
            return;
        }
        final YarnServerCommonServiceProtos.RegisterNodeManagerRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<YarnProtos.ApplicationIdProto> list = p.getRunningApplicationsList();
        this.runningApplications = new ArrayList<ApplicationId>();
        for (final YarnProtos.ApplicationIdProto c : list) {
            this.runningApplications.add(this.convertFromProtoFormat(c));
        }
    }
    
    @Override
    public void setRunningApplications(final List<ApplicationId> apps) {
        if (apps == null) {
            return;
        }
        this.initRunningApplications();
        this.runningApplications.addAll(apps);
    }
    
    private void addRunningApplicationsToProto() {
        this.maybeInitBuilder();
        this.builder.clearRunningApplications();
        if (this.runningApplications == null) {
            return;
        }
        final Iterable<YarnProtos.ApplicationIdProto> it = new Iterable<YarnProtos.ApplicationIdProto>() {
            @Override
            public Iterator<YarnProtos.ApplicationIdProto> iterator() {
                return new Iterator<YarnProtos.ApplicationIdProto>() {
                    Iterator<ApplicationId> iter = RegisterNodeManagerRequestPBImpl.this.runningApplications.iterator();
                    
                    @Override
                    public boolean hasNext() {
                        return this.iter.hasNext();
                    }
                    
                    @Override
                    public YarnProtos.ApplicationIdProto next() {
                        return RegisterNodeManagerRequestPBImpl.this.convertToProtoFormat(this.iter.next());
                    }
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
        this.builder.addAllRunningApplications(it);
    }
    
    @Override
    public List<NMContainerStatus> getNMContainerStatuses() {
        this.initContainerRecoveryReports();
        return this.containerStatuses;
    }
    
    private void initContainerRecoveryReports() {
        if (this.containerStatuses != null) {
            return;
        }
        final YarnServerCommonServiceProtos.RegisterNodeManagerRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<YarnServerCommonServiceProtos.NMContainerStatusProto> list = p.getContainerStatusesList();
        this.containerStatuses = new ArrayList<NMContainerStatus>();
        for (final YarnServerCommonServiceProtos.NMContainerStatusProto c : list) {
            this.containerStatuses.add(this.convertFromProtoFormat(c));
        }
    }
    
    @Override
    public void setContainerStatuses(final List<NMContainerStatus> containerReports) {
        if (containerReports == null) {
            return;
        }
        this.initContainerRecoveryReports();
        this.containerStatuses.addAll(containerReports);
    }
    
    @Override
    public int hashCode() {
        return this.getProto().hashCode();
    }
    
    @Override
    public boolean equals(final Object other) {
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((RegisterNodeManagerRequestPBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String getNMVersion() {
        final YarnServerCommonServiceProtos.RegisterNodeManagerRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasNmVersion()) {
            return "";
        }
        return p.getNmVersion();
    }
    
    @Override
    public void setNMVersion(final String version) {
        this.maybeInitBuilder();
        this.builder.setNmVersion(version);
    }
    
    private ApplicationIdPBImpl convertFromProtoFormat(final YarnProtos.ApplicationIdProto p) {
        return new ApplicationIdPBImpl(p);
    }
    
    private YarnProtos.ApplicationIdProto convertToProtoFormat(final ApplicationId t) {
        return ((ApplicationIdPBImpl)t).getProto();
    }
    
    private NodeIdPBImpl convertFromProtoFormat(final YarnProtos.NodeIdProto p) {
        return new NodeIdPBImpl(p);
    }
    
    private YarnProtos.NodeIdProto convertToProtoFormat(final NodeId t) {
        return ((NodeIdPBImpl)t).getProto();
    }
    
    private ResourcePBImpl convertFromProtoFormat(final YarnProtos.ResourceProto p) {
        return new ResourcePBImpl(p);
    }
    
    private YarnProtos.ResourceProto convertToProtoFormat(final Resource t) {
        return ((ResourcePBImpl)t).getProto();
    }
    
    private NMContainerStatusPBImpl convertFromProtoFormat(final YarnServerCommonServiceProtos.NMContainerStatusProto c) {
        return new NMContainerStatusPBImpl(c);
    }
    
    private YarnServerCommonServiceProtos.NMContainerStatusProto convertToProtoFormat(final NMContainerStatus c) {
        return ((NMContainerStatusPBImpl)c).getProto();
    }
}
