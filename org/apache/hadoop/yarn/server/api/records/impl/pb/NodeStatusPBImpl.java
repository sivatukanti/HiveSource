// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.api.records.impl.pb;

import org.apache.hadoop.yarn.api.records.impl.pb.ApplicationIdPBImpl;
import org.apache.hadoop.yarn.api.records.impl.pb.ContainerStatusPBImpl;
import org.apache.hadoop.yarn.api.records.impl.pb.NodeIdPBImpl;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.hadoop.yarn.proto.YarnProtos;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.server.api.records.NodeHealthStatus;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import java.util.List;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.proto.YarnServerCommonProtos;
import org.apache.hadoop.yarn.server.api.records.NodeStatus;

public class NodeStatusPBImpl extends NodeStatus
{
    YarnServerCommonProtos.NodeStatusProto proto;
    YarnServerCommonProtos.NodeStatusProto.Builder builder;
    boolean viaProto;
    private NodeId nodeId;
    private List<ContainerStatus> containers;
    private NodeHealthStatus nodeHealthStatus;
    private List<ApplicationId> keepAliveApplications;
    
    public NodeStatusPBImpl() {
        this.proto = YarnServerCommonProtos.NodeStatusProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.nodeId = null;
        this.containers = null;
        this.nodeHealthStatus = null;
        this.keepAliveApplications = null;
        this.builder = YarnServerCommonProtos.NodeStatusProto.newBuilder();
    }
    
    public NodeStatusPBImpl(final YarnServerCommonProtos.NodeStatusProto proto) {
        this.proto = YarnServerCommonProtos.NodeStatusProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.nodeId = null;
        this.containers = null;
        this.nodeHealthStatus = null;
        this.keepAliveApplications = null;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public synchronized YarnServerCommonProtos.NodeStatusProto getProto() {
        this.mergeLocalToProto();
        this.proto = (this.viaProto ? this.proto : this.builder.build());
        this.viaProto = true;
        return this.proto;
    }
    
    private synchronized void mergeLocalToBuilder() {
        if (this.nodeId != null) {
            this.builder.setNodeId(this.convertToProtoFormat(this.nodeId));
        }
        if (this.containers != null) {
            this.addContainersToProto();
        }
        if (this.nodeHealthStatus != null) {
            this.builder.setNodeHealthStatus(this.convertToProtoFormat(this.nodeHealthStatus));
        }
        if (this.keepAliveApplications != null) {
            this.addKeepAliveApplicationsToProto();
        }
    }
    
    private synchronized void mergeLocalToProto() {
        if (this.viaProto) {
            this.maybeInitBuilder();
        }
        this.mergeLocalToBuilder();
        this.proto = this.builder.build();
        this.viaProto = true;
    }
    
    private synchronized void maybeInitBuilder() {
        if (this.viaProto || this.builder == null) {
            this.builder = YarnServerCommonProtos.NodeStatusProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    private synchronized void addContainersToProto() {
        this.maybeInitBuilder();
        this.builder.clearContainersStatuses();
        if (this.containers == null) {
            return;
        }
        final Iterable<YarnProtos.ContainerStatusProto> iterable = new Iterable<YarnProtos.ContainerStatusProto>() {
            @Override
            public Iterator<YarnProtos.ContainerStatusProto> iterator() {
                return new Iterator<YarnProtos.ContainerStatusProto>() {
                    Iterator<ContainerStatus> iter = NodeStatusPBImpl.this.containers.iterator();
                    
                    @Override
                    public boolean hasNext() {
                        return this.iter.hasNext();
                    }
                    
                    @Override
                    public YarnProtos.ContainerStatusProto next() {
                        return NodeStatusPBImpl.this.convertToProtoFormat(this.iter.next());
                    }
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
        this.builder.addAllContainersStatuses(iterable);
    }
    
    private synchronized void addKeepAliveApplicationsToProto() {
        this.maybeInitBuilder();
        this.builder.clearKeepAliveApplications();
        if (this.keepAliveApplications == null) {
            return;
        }
        final Iterable<YarnProtos.ApplicationIdProto> iterable = new Iterable<YarnProtos.ApplicationIdProto>() {
            @Override
            public Iterator<YarnProtos.ApplicationIdProto> iterator() {
                return new Iterator<YarnProtos.ApplicationIdProto>() {
                    Iterator<ApplicationId> iter = NodeStatusPBImpl.this.keepAliveApplications.iterator();
                    
                    @Override
                    public boolean hasNext() {
                        return this.iter.hasNext();
                    }
                    
                    @Override
                    public YarnProtos.ApplicationIdProto next() {
                        return NodeStatusPBImpl.this.convertToProtoFormat(this.iter.next());
                    }
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
        this.builder.addAllKeepAliveApplications(iterable);
    }
    
    @Override
    public int hashCode() {
        return this.getProto().hashCode();
    }
    
    @Override
    public boolean equals(final Object other) {
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((NodeStatusPBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public synchronized int getResponseId() {
        final YarnServerCommonProtos.NodeStatusProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getResponseId();
    }
    
    @Override
    public synchronized void setResponseId(final int responseId) {
        this.maybeInitBuilder();
        this.builder.setResponseId(responseId);
    }
    
    @Override
    public synchronized NodeId getNodeId() {
        final YarnServerCommonProtos.NodeStatusProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.nodeId != null) {
            return this.nodeId;
        }
        if (!p.hasNodeId()) {
            return null;
        }
        return this.nodeId = this.convertFromProtoFormat(p.getNodeId());
    }
    
    @Override
    public synchronized void setNodeId(final NodeId nodeId) {
        this.maybeInitBuilder();
        if (nodeId == null) {
            this.builder.clearNodeId();
        }
        this.nodeId = nodeId;
    }
    
    @Override
    public synchronized List<ContainerStatus> getContainersStatuses() {
        this.initContainers();
        return this.containers;
    }
    
    @Override
    public synchronized void setContainersStatuses(final List<ContainerStatus> containers) {
        if (containers == null) {
            this.builder.clearContainersStatuses();
        }
        this.containers = containers;
    }
    
    @Override
    public synchronized List<ApplicationId> getKeepAliveApplications() {
        this.initKeepAliveApplications();
        return this.keepAliveApplications;
    }
    
    @Override
    public synchronized void setKeepAliveApplications(final List<ApplicationId> appIds) {
        if (appIds == null) {
            this.builder.clearKeepAliveApplications();
        }
        this.keepAliveApplications = appIds;
    }
    
    private synchronized void initContainers() {
        if (this.containers != null) {
            return;
        }
        final YarnServerCommonProtos.NodeStatusProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<YarnProtos.ContainerStatusProto> list = p.getContainersStatusesList();
        this.containers = new ArrayList<ContainerStatus>();
        for (final YarnProtos.ContainerStatusProto c : list) {
            this.containers.add(this.convertFromProtoFormat(c));
        }
    }
    
    private synchronized void initKeepAliveApplications() {
        if (this.keepAliveApplications != null) {
            return;
        }
        final YarnServerCommonProtos.NodeStatusProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<YarnProtos.ApplicationIdProto> list = p.getKeepAliveApplicationsList();
        this.keepAliveApplications = new ArrayList<ApplicationId>();
        for (final YarnProtos.ApplicationIdProto c : list) {
            this.keepAliveApplications.add(this.convertFromProtoFormat(c));
        }
    }
    
    @Override
    public synchronized NodeHealthStatus getNodeHealthStatus() {
        final YarnServerCommonProtos.NodeStatusProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.nodeHealthStatus != null) {
            return this.nodeHealthStatus;
        }
        if (!p.hasNodeHealthStatus()) {
            return null;
        }
        return this.nodeHealthStatus = this.convertFromProtoFormat(p.getNodeHealthStatus());
    }
    
    @Override
    public synchronized void setNodeHealthStatus(final NodeHealthStatus healthStatus) {
        this.maybeInitBuilder();
        if (healthStatus == null) {
            this.builder.clearNodeHealthStatus();
        }
        this.nodeHealthStatus = healthStatus;
    }
    
    private YarnProtos.NodeIdProto convertToProtoFormat(final NodeId nodeId) {
        return ((NodeIdPBImpl)nodeId).getProto();
    }
    
    private NodeId convertFromProtoFormat(final YarnProtos.NodeIdProto proto) {
        return new NodeIdPBImpl(proto);
    }
    
    private YarnServerCommonProtos.NodeHealthStatusProto convertToProtoFormat(final NodeHealthStatus healthStatus) {
        return ((NodeHealthStatusPBImpl)healthStatus).getProto();
    }
    
    private NodeHealthStatus convertFromProtoFormat(final YarnServerCommonProtos.NodeHealthStatusProto proto) {
        return new NodeHealthStatusPBImpl(proto);
    }
    
    private ContainerStatusPBImpl convertFromProtoFormat(final YarnProtos.ContainerStatusProto c) {
        return new ContainerStatusPBImpl(c);
    }
    
    private YarnProtos.ContainerStatusProto convertToProtoFormat(final ContainerStatus c) {
        return ((ContainerStatusPBImpl)c).getProto();
    }
    
    private ApplicationIdPBImpl convertFromProtoFormat(final YarnProtos.ApplicationIdProto c) {
        return new ApplicationIdPBImpl(c);
    }
    
    private YarnProtos.ApplicationIdProto convertToProtoFormat(final ApplicationId c) {
        return ((ApplicationIdPBImpl)c).getProto();
    }
}
