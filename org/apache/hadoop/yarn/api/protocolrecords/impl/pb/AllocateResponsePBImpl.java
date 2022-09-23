// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords.impl.pb;

import org.apache.hadoop.yarn.api.records.impl.pb.TokenPBImpl;
import org.apache.hadoop.security.proto.SecurityProtos;
import org.apache.hadoop.yarn.api.records.impl.pb.NMTokenPBImpl;
import org.apache.hadoop.yarn.api.records.impl.pb.PreemptionMessagePBImpl;
import org.apache.hadoop.yarn.api.records.impl.pb.ResourcePBImpl;
import org.apache.hadoop.yarn.api.records.impl.pb.ContainerStatusPBImpl;
import org.apache.hadoop.yarn.api.records.impl.pb.ContainerPBImpl;
import org.apache.hadoop.yarn.api.records.impl.pb.NodeReportPBImpl;
import org.apache.hadoop.yarn.api.records.impl.pb.ContainerResourceDecreasePBImpl;
import org.apache.hadoop.yarn.api.records.impl.pb.ContainerResourceIncreasePBImpl;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import org.apache.hadoop.yarn.api.records.impl.pb.ProtoUtils;
import org.apache.hadoop.yarn.api.records.AMCommand;
import org.apache.hadoop.yarn.proto.YarnProtos;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.api.records.Token;
import org.apache.hadoop.yarn.api.records.PreemptionMessage;
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.api.records.ContainerResourceDecrease;
import org.apache.hadoop.yarn.api.records.ContainerResourceIncrease;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.api.records.NMToken;
import org.apache.hadoop.yarn.api.records.Container;
import java.util.List;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.proto.YarnServiceProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateResponse;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class AllocateResponsePBImpl extends AllocateResponse
{
    YarnServiceProtos.AllocateResponseProto proto;
    YarnServiceProtos.AllocateResponseProto.Builder builder;
    boolean viaProto;
    Resource limit;
    private List<Container> allocatedContainers;
    private List<NMToken> nmTokens;
    private List<ContainerStatus> completedContainersStatuses;
    private List<ContainerResourceIncrease> increasedContainers;
    private List<ContainerResourceDecrease> decreasedContainers;
    private List<NodeReport> updatedNodes;
    private PreemptionMessage preempt;
    private Token amrmToken;
    
    public AllocateResponsePBImpl() {
        this.proto = YarnServiceProtos.AllocateResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.allocatedContainers = null;
        this.nmTokens = null;
        this.completedContainersStatuses = null;
        this.increasedContainers = null;
        this.decreasedContainers = null;
        this.updatedNodes = null;
        this.amrmToken = null;
        this.builder = YarnServiceProtos.AllocateResponseProto.newBuilder();
    }
    
    public AllocateResponsePBImpl(final YarnServiceProtos.AllocateResponseProto proto) {
        this.proto = YarnServiceProtos.AllocateResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.allocatedContainers = null;
        this.nmTokens = null;
        this.completedContainersStatuses = null;
        this.increasedContainers = null;
        this.decreasedContainers = null;
        this.updatedNodes = null;
        this.amrmToken = null;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public synchronized YarnServiceProtos.AllocateResponseProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((AllocateResponsePBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    private synchronized void mergeLocalToBuilder() {
        if (this.allocatedContainers != null) {
            this.builder.clearAllocatedContainers();
            final Iterable<YarnProtos.ContainerProto> iterable = this.getContainerProtoIterable(this.allocatedContainers);
            this.builder.addAllAllocatedContainers(iterable);
        }
        if (this.nmTokens != null) {
            this.builder.clearNmTokens();
            final Iterable<YarnServiceProtos.NMTokenProto> iterable2 = this.getTokenProtoIterable(this.nmTokens);
            this.builder.addAllNmTokens(iterable2);
        }
        if (this.completedContainersStatuses != null) {
            this.builder.clearCompletedContainerStatuses();
            final Iterable<YarnProtos.ContainerStatusProto> iterable3 = this.getContainerStatusProtoIterable(this.completedContainersStatuses);
            this.builder.addAllCompletedContainerStatuses(iterable3);
        }
        if (this.updatedNodes != null) {
            this.builder.clearUpdatedNodes();
            final Iterable<YarnProtos.NodeReportProto> iterable4 = this.getNodeReportProtoIterable(this.updatedNodes);
            this.builder.addAllUpdatedNodes(iterable4);
        }
        if (this.limit != null) {
            this.builder.setLimit(this.convertToProtoFormat(this.limit));
        }
        if (this.preempt != null) {
            this.builder.setPreempt(this.convertToProtoFormat(this.preempt));
        }
        if (this.increasedContainers != null) {
            this.builder.clearIncreasedContainers();
            final Iterable<YarnProtos.ContainerResourceIncreaseProto> iterable5 = this.getIncreaseProtoIterable(this.increasedContainers);
            this.builder.addAllIncreasedContainers(iterable5);
        }
        if (this.decreasedContainers != null) {
            this.builder.clearDecreasedContainers();
            final Iterable<YarnProtos.ContainerResourceDecreaseProto> iterable6 = this.getChangeProtoIterable(this.decreasedContainers);
            this.builder.addAllDecreasedContainers(iterable6);
        }
        if (this.amrmToken != null) {
            this.builder.setAmRmToken(this.convertToProtoFormat(this.amrmToken));
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
            this.builder = YarnServiceProtos.AllocateResponseProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public synchronized AMCommand getAMCommand() {
        final YarnServiceProtos.AllocateResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasAMCommand()) {
            return null;
        }
        return ProtoUtils.convertFromProtoFormat(p.getAMCommand());
    }
    
    @Override
    public synchronized void setAMCommand(final AMCommand command) {
        this.maybeInitBuilder();
        if (command == null) {
            this.builder.clearAMCommand();
            return;
        }
        this.builder.setAMCommand(ProtoUtils.convertToProtoFormat(command));
    }
    
    @Override
    public synchronized int getResponseId() {
        final YarnServiceProtos.AllocateResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getResponseId();
    }
    
    @Override
    public synchronized void setResponseId(final int responseId) {
        this.maybeInitBuilder();
        this.builder.setResponseId(responseId);
    }
    
    @Override
    public synchronized Resource getAvailableResources() {
        if (this.limit != null) {
            return this.limit;
        }
        final YarnServiceProtos.AllocateResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasLimit()) {
            return null;
        }
        return this.limit = this.convertFromProtoFormat(p.getLimit());
    }
    
    @Override
    public synchronized void setAvailableResources(final Resource limit) {
        this.maybeInitBuilder();
        if (limit == null) {
            this.builder.clearLimit();
        }
        this.limit = limit;
    }
    
    @Override
    public synchronized List<NodeReport> getUpdatedNodes() {
        this.initLocalNewNodeReportList();
        return this.updatedNodes;
    }
    
    @Override
    public synchronized void setUpdatedNodes(final List<NodeReport> updatedNodes) {
        if (updatedNodes == null) {
            this.updatedNodes.clear();
            return;
        }
        (this.updatedNodes = new ArrayList<NodeReport>(updatedNodes.size())).addAll(updatedNodes);
    }
    
    @Override
    public synchronized List<Container> getAllocatedContainers() {
        this.initLocalNewContainerList();
        return this.allocatedContainers;
    }
    
    @Override
    public synchronized void setAllocatedContainers(final List<Container> containers) {
        if (containers == null) {
            return;
        }
        this.initLocalNewContainerList();
        this.allocatedContainers.addAll(containers);
    }
    
    @Override
    public synchronized List<ContainerStatus> getCompletedContainersStatuses() {
        this.initLocalFinishedContainerList();
        return this.completedContainersStatuses;
    }
    
    @Override
    public synchronized void setCompletedContainersStatuses(final List<ContainerStatus> containers) {
        if (containers == null) {
            return;
        }
        this.initLocalFinishedContainerList();
        this.completedContainersStatuses.addAll(containers);
    }
    
    @Override
    public synchronized void setNMTokens(final List<NMToken> nmTokens) {
        if (nmTokens == null || nmTokens.isEmpty()) {
            if (this.nmTokens != null) {
                this.nmTokens.clear();
            }
            this.builder.clearNmTokens();
            return;
        }
        this.initLocalNewNMTokenList();
        this.nmTokens.addAll(nmTokens);
    }
    
    @Override
    public synchronized List<NMToken> getNMTokens() {
        this.initLocalNewNMTokenList();
        return this.nmTokens;
    }
    
    @Override
    public synchronized int getNumClusterNodes() {
        final YarnServiceProtos.AllocateResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getNumClusterNodes();
    }
    
    @Override
    public synchronized void setNumClusterNodes(final int numNodes) {
        this.maybeInitBuilder();
        this.builder.setNumClusterNodes(numNodes);
    }
    
    @Override
    public synchronized PreemptionMessage getPreemptionMessage() {
        final YarnServiceProtos.AllocateResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.preempt != null) {
            return this.preempt;
        }
        if (!p.hasPreempt()) {
            return null;
        }
        return this.preempt = this.convertFromProtoFormat(p.getPreempt());
    }
    
    @Override
    public synchronized void setPreemptionMessage(final PreemptionMessage preempt) {
        this.maybeInitBuilder();
        if (null == preempt) {
            this.builder.clearPreempt();
        }
        this.preempt = preempt;
    }
    
    @Override
    public synchronized List<ContainerResourceIncrease> getIncreasedContainers() {
        this.initLocalIncreasedContainerList();
        return this.increasedContainers;
    }
    
    @Override
    public synchronized void setIncreasedContainers(final List<ContainerResourceIncrease> increasedContainers) {
        if (increasedContainers == null) {
            return;
        }
        this.initLocalIncreasedContainerList();
        this.increasedContainers.addAll(increasedContainers);
    }
    
    @Override
    public synchronized List<ContainerResourceDecrease> getDecreasedContainers() {
        this.initLocalDecreasedContainerList();
        return this.decreasedContainers;
    }
    
    @Override
    public synchronized void setDecreasedContainers(final List<ContainerResourceDecrease> decreasedContainers) {
        if (decreasedContainers == null) {
            return;
        }
        this.initLocalDecreasedContainerList();
        this.decreasedContainers.addAll(decreasedContainers);
    }
    
    @Override
    public synchronized Token getAMRMToken() {
        final YarnServiceProtos.AllocateResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.amrmToken != null) {
            return this.amrmToken;
        }
        if (!p.hasAmRmToken()) {
            return null;
        }
        return this.amrmToken = this.convertFromProtoFormat(p.getAmRmToken());
    }
    
    @Override
    public synchronized void setAMRMToken(final Token amRMToken) {
        this.maybeInitBuilder();
        if (amRMToken == null) {
            this.builder.clearAmRmToken();
        }
        this.amrmToken = amRMToken;
    }
    
    private synchronized void initLocalIncreasedContainerList() {
        if (this.increasedContainers != null) {
            return;
        }
        final YarnServiceProtos.AllocateResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<YarnProtos.ContainerResourceIncreaseProto> list = p.getIncreasedContainersList();
        this.increasedContainers = new ArrayList<ContainerResourceIncrease>();
        for (final YarnProtos.ContainerResourceIncreaseProto c : list) {
            this.increasedContainers.add(this.convertFromProtoFormat(c));
        }
    }
    
    private synchronized void initLocalDecreasedContainerList() {
        if (this.decreasedContainers != null) {
            return;
        }
        final YarnServiceProtos.AllocateResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<YarnProtos.ContainerResourceDecreaseProto> list = p.getDecreasedContainersList();
        this.decreasedContainers = new ArrayList<ContainerResourceDecrease>();
        for (final YarnProtos.ContainerResourceDecreaseProto c : list) {
            this.decreasedContainers.add(this.convertFromProtoFormat(c));
        }
    }
    
    private synchronized void initLocalNewNodeReportList() {
        if (this.updatedNodes != null) {
            return;
        }
        final YarnServiceProtos.AllocateResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<YarnProtos.NodeReportProto> list = p.getUpdatedNodesList();
        this.updatedNodes = new ArrayList<NodeReport>(list.size());
        for (final YarnProtos.NodeReportProto n : list) {
            this.updatedNodes.add(this.convertFromProtoFormat(n));
        }
    }
    
    private synchronized void initLocalNewContainerList() {
        if (this.allocatedContainers != null) {
            return;
        }
        final YarnServiceProtos.AllocateResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<YarnProtos.ContainerProto> list = p.getAllocatedContainersList();
        this.allocatedContainers = new ArrayList<Container>();
        for (final YarnProtos.ContainerProto c : list) {
            this.allocatedContainers.add(this.convertFromProtoFormat(c));
        }
    }
    
    private synchronized void initLocalNewNMTokenList() {
        if (this.nmTokens != null) {
            return;
        }
        final YarnServiceProtos.AllocateResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<YarnServiceProtos.NMTokenProto> list = p.getNmTokensList();
        this.nmTokens = new ArrayList<NMToken>();
        for (final YarnServiceProtos.NMTokenProto t : list) {
            this.nmTokens.add(this.convertFromProtoFormat(t));
        }
    }
    
    private synchronized Iterable<YarnProtos.ContainerResourceIncreaseProto> getIncreaseProtoIterable(final List<ContainerResourceIncrease> newContainersList) {
        this.maybeInitBuilder();
        return new Iterable<YarnProtos.ContainerResourceIncreaseProto>() {
            @Override
            public synchronized Iterator<YarnProtos.ContainerResourceIncreaseProto> iterator() {
                return new Iterator<YarnProtos.ContainerResourceIncreaseProto>() {
                    Iterator<ContainerResourceIncrease> iter = newContainersList.iterator();
                    
                    @Override
                    public synchronized boolean hasNext() {
                        return this.iter.hasNext();
                    }
                    
                    @Override
                    public synchronized YarnProtos.ContainerResourceIncreaseProto next() {
                        return AllocateResponsePBImpl.this.convertToProtoFormat(this.iter.next());
                    }
                    
                    @Override
                    public synchronized void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }
    
    private synchronized Iterable<YarnProtos.ContainerResourceDecreaseProto> getChangeProtoIterable(final List<ContainerResourceDecrease> newContainersList) {
        this.maybeInitBuilder();
        return new Iterable<YarnProtos.ContainerResourceDecreaseProto>() {
            @Override
            public synchronized Iterator<YarnProtos.ContainerResourceDecreaseProto> iterator() {
                return new Iterator<YarnProtos.ContainerResourceDecreaseProto>() {
                    Iterator<ContainerResourceDecrease> iter = newContainersList.iterator();
                    
                    @Override
                    public synchronized boolean hasNext() {
                        return this.iter.hasNext();
                    }
                    
                    @Override
                    public synchronized YarnProtos.ContainerResourceDecreaseProto next() {
                        return AllocateResponsePBImpl.this.convertToProtoFormat(this.iter.next());
                    }
                    
                    @Override
                    public synchronized void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }
    
    private synchronized Iterable<YarnProtos.ContainerProto> getContainerProtoIterable(final List<Container> newContainersList) {
        this.maybeInitBuilder();
        return new Iterable<YarnProtos.ContainerProto>() {
            @Override
            public synchronized Iterator<YarnProtos.ContainerProto> iterator() {
                return new Iterator<YarnProtos.ContainerProto>() {
                    Iterator<Container> iter = newContainersList.iterator();
                    
                    @Override
                    public synchronized boolean hasNext() {
                        return this.iter.hasNext();
                    }
                    
                    @Override
                    public synchronized YarnProtos.ContainerProto next() {
                        return AllocateResponsePBImpl.this.convertToProtoFormat(this.iter.next());
                    }
                    
                    @Override
                    public synchronized void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }
    
    private synchronized Iterable<YarnServiceProtos.NMTokenProto> getTokenProtoIterable(final List<NMToken> nmTokenList) {
        this.maybeInitBuilder();
        return new Iterable<YarnServiceProtos.NMTokenProto>() {
            @Override
            public synchronized Iterator<YarnServiceProtos.NMTokenProto> iterator() {
                return new Iterator<YarnServiceProtos.NMTokenProto>() {
                    Iterator<NMToken> iter = nmTokenList.iterator();
                    
                    @Override
                    public boolean hasNext() {
                        return this.iter.hasNext();
                    }
                    
                    @Override
                    public YarnServiceProtos.NMTokenProto next() {
                        return AllocateResponsePBImpl.this.convertToProtoFormat(this.iter.next());
                    }
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }
    
    private synchronized Iterable<YarnProtos.ContainerStatusProto> getContainerStatusProtoIterable(final List<ContainerStatus> newContainersList) {
        this.maybeInitBuilder();
        return new Iterable<YarnProtos.ContainerStatusProto>() {
            @Override
            public synchronized Iterator<YarnProtos.ContainerStatusProto> iterator() {
                return new Iterator<YarnProtos.ContainerStatusProto>() {
                    Iterator<ContainerStatus> iter = newContainersList.iterator();
                    
                    @Override
                    public synchronized boolean hasNext() {
                        return this.iter.hasNext();
                    }
                    
                    @Override
                    public synchronized YarnProtos.ContainerStatusProto next() {
                        return AllocateResponsePBImpl.this.convertToProtoFormat(this.iter.next());
                    }
                    
                    @Override
                    public synchronized void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }
    
    private synchronized Iterable<YarnProtos.NodeReportProto> getNodeReportProtoIterable(final List<NodeReport> newNodeReportsList) {
        this.maybeInitBuilder();
        return new Iterable<YarnProtos.NodeReportProto>() {
            @Override
            public synchronized Iterator<YarnProtos.NodeReportProto> iterator() {
                return new Iterator<YarnProtos.NodeReportProto>() {
                    Iterator<NodeReport> iter = newNodeReportsList.iterator();
                    
                    @Override
                    public synchronized boolean hasNext() {
                        return this.iter.hasNext();
                    }
                    
                    @Override
                    public synchronized YarnProtos.NodeReportProto next() {
                        return AllocateResponsePBImpl.this.convertToProtoFormat(this.iter.next());
                    }
                    
                    @Override
                    public synchronized void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }
    
    private synchronized void initLocalFinishedContainerList() {
        if (this.completedContainersStatuses != null) {
            return;
        }
        final YarnServiceProtos.AllocateResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<YarnProtos.ContainerStatusProto> list = p.getCompletedContainerStatusesList();
        this.completedContainersStatuses = new ArrayList<ContainerStatus>();
        for (final YarnProtos.ContainerStatusProto c : list) {
            this.completedContainersStatuses.add(this.convertFromProtoFormat(c));
        }
    }
    
    private synchronized ContainerResourceIncrease convertFromProtoFormat(final YarnProtos.ContainerResourceIncreaseProto p) {
        return new ContainerResourceIncreasePBImpl(p);
    }
    
    private synchronized YarnProtos.ContainerResourceIncreaseProto convertToProtoFormat(final ContainerResourceIncrease t) {
        return ((ContainerResourceIncreasePBImpl)t).getProto();
    }
    
    private synchronized ContainerResourceDecrease convertFromProtoFormat(final YarnProtos.ContainerResourceDecreaseProto p) {
        return new ContainerResourceDecreasePBImpl(p);
    }
    
    private synchronized YarnProtos.ContainerResourceDecreaseProto convertToProtoFormat(final ContainerResourceDecrease t) {
        return ((ContainerResourceDecreasePBImpl)t).getProto();
    }
    
    private synchronized NodeReportPBImpl convertFromProtoFormat(final YarnProtos.NodeReportProto p) {
        return new NodeReportPBImpl(p);
    }
    
    private synchronized YarnProtos.NodeReportProto convertToProtoFormat(final NodeReport t) {
        return ((NodeReportPBImpl)t).getProto();
    }
    
    private synchronized ContainerPBImpl convertFromProtoFormat(final YarnProtos.ContainerProto p) {
        return new ContainerPBImpl(p);
    }
    
    private synchronized YarnProtos.ContainerProto convertToProtoFormat(final Container t) {
        return ((ContainerPBImpl)t).getProto();
    }
    
    private synchronized ContainerStatusPBImpl convertFromProtoFormat(final YarnProtos.ContainerStatusProto p) {
        return new ContainerStatusPBImpl(p);
    }
    
    private synchronized YarnProtos.ContainerStatusProto convertToProtoFormat(final ContainerStatus t) {
        return ((ContainerStatusPBImpl)t).getProto();
    }
    
    private synchronized ResourcePBImpl convertFromProtoFormat(final YarnProtos.ResourceProto p) {
        return new ResourcePBImpl(p);
    }
    
    private synchronized YarnProtos.ResourceProto convertToProtoFormat(final Resource r) {
        return ((ResourcePBImpl)r).getProto();
    }
    
    private synchronized PreemptionMessagePBImpl convertFromProtoFormat(final YarnProtos.PreemptionMessageProto p) {
        return new PreemptionMessagePBImpl(p);
    }
    
    private synchronized YarnProtos.PreemptionMessageProto convertToProtoFormat(final PreemptionMessage r) {
        return ((PreemptionMessagePBImpl)r).getProto();
    }
    
    private synchronized YarnServiceProtos.NMTokenProto convertToProtoFormat(final NMToken token) {
        return ((NMTokenPBImpl)token).getProto();
    }
    
    private synchronized NMToken convertFromProtoFormat(final YarnServiceProtos.NMTokenProto proto) {
        return new NMTokenPBImpl(proto);
    }
    
    private TokenPBImpl convertFromProtoFormat(final SecurityProtos.TokenProto p) {
        return new TokenPBImpl(p);
    }
    
    private SecurityProtos.TokenProto convertToProtoFormat(final Token t) {
        return ((TokenPBImpl)t).getProto();
    }
}
