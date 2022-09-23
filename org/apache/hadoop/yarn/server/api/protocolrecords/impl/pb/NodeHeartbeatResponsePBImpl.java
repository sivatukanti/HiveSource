// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb;

import com.google.protobuf.Message;
import org.apache.hadoop.yarn.server.api.records.impl.pb.MasterKeyPBImpl;
import org.apache.hadoop.yarn.proto.YarnServerCommonProtos;
import org.apache.hadoop.yarn.api.records.impl.pb.ApplicationIdPBImpl;
import org.apache.hadoop.yarn.api.records.impl.pb.ContainerIdPBImpl;
import java.util.HashMap;
import java.util.Collection;
import org.apache.hadoop.yarn.proto.YarnProtos;
import java.util.ArrayList;
import org.apache.hadoop.yarn.server.api.records.NodeAction;
import java.util.Iterator;
import org.apache.hadoop.yarn.api.records.impl.pb.ProtoUtils;
import org.apache.hadoop.yarn.server.api.records.MasterKey;
import java.nio.ByteBuffer;
import java.util.Map;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ContainerId;
import java.util.List;
import org.apache.hadoop.yarn.server.api.protocolrecords.NodeHeartbeatResponse;
import org.apache.hadoop.yarn.proto.YarnServerCommonServiceProtos;
import org.apache.hadoop.yarn.api.records.impl.pb.ProtoBase;

public class NodeHeartbeatResponsePBImpl extends ProtoBase<YarnServerCommonServiceProtos.NodeHeartbeatResponseProto> implements NodeHeartbeatResponse
{
    YarnServerCommonServiceProtos.NodeHeartbeatResponseProto proto;
    YarnServerCommonServiceProtos.NodeHeartbeatResponseProto.Builder builder;
    boolean viaProto;
    private List<ContainerId> containersToCleanup;
    private List<ContainerId> containersToBeRemovedFromNM;
    private List<ApplicationId> applicationsToCleanup;
    private Map<ApplicationId, ByteBuffer> systemCredentials;
    private MasterKey containerTokenMasterKey;
    private MasterKey nmTokenMasterKey;
    
    public NodeHeartbeatResponsePBImpl() {
        this.proto = YarnServerCommonServiceProtos.NodeHeartbeatResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.containersToCleanup = null;
        this.containersToBeRemovedFromNM = null;
        this.applicationsToCleanup = null;
        this.systemCredentials = null;
        this.containerTokenMasterKey = null;
        this.nmTokenMasterKey = null;
        this.builder = YarnServerCommonServiceProtos.NodeHeartbeatResponseProto.newBuilder();
    }
    
    public NodeHeartbeatResponsePBImpl(final YarnServerCommonServiceProtos.NodeHeartbeatResponseProto proto) {
        this.proto = YarnServerCommonServiceProtos.NodeHeartbeatResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.containersToCleanup = null;
        this.containersToBeRemovedFromNM = null;
        this.applicationsToCleanup = null;
        this.systemCredentials = null;
        this.containerTokenMasterKey = null;
        this.nmTokenMasterKey = null;
        this.proto = proto;
        this.viaProto = true;
    }
    
    @Override
    public YarnServerCommonServiceProtos.NodeHeartbeatResponseProto getProto() {
        this.mergeLocalToProto();
        this.proto = (this.viaProto ? this.proto : this.builder.build());
        this.viaProto = true;
        return this.proto;
    }
    
    private void mergeLocalToBuilder() {
        if (this.containersToCleanup != null) {
            this.addContainersToCleanupToProto();
        }
        if (this.applicationsToCleanup != null) {
            this.addApplicationsToCleanupToProto();
        }
        if (this.containersToBeRemovedFromNM != null) {
            this.addContainersToBeRemovedFromNMToProto();
        }
        if (this.containerTokenMasterKey != null) {
            this.builder.setContainerTokenMasterKey(this.convertToProtoFormat(this.containerTokenMasterKey));
        }
        if (this.nmTokenMasterKey != null) {
            this.builder.setNmTokenMasterKey(this.convertToProtoFormat(this.nmTokenMasterKey));
        }
        if (this.systemCredentials != null) {
            this.addSystemCredentialsToProto();
        }
    }
    
    private void addSystemCredentialsToProto() {
        this.maybeInitBuilder();
        this.builder.clearSystemCredentialsForApps();
        for (final Map.Entry<ApplicationId, ByteBuffer> entry : this.systemCredentials.entrySet()) {
            this.builder.addSystemCredentialsForApps(YarnServerCommonServiceProtos.SystemCredentialsForAppsProto.newBuilder().setAppId(this.convertToProtoFormat(entry.getKey())).setCredentialsForApp(ProtoUtils.convertToProtoFormat(entry.getValue())));
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
            this.builder = YarnServerCommonServiceProtos.NodeHeartbeatResponseProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public int getResponseId() {
        final YarnServerCommonServiceProtos.NodeHeartbeatResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getResponseId();
    }
    
    @Override
    public void setResponseId(final int responseId) {
        this.maybeInitBuilder();
        this.builder.setResponseId(responseId);
    }
    
    @Override
    public MasterKey getContainerTokenMasterKey() {
        final YarnServerCommonServiceProtos.NodeHeartbeatResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.containerTokenMasterKey != null) {
            return this.containerTokenMasterKey;
        }
        if (!p.hasContainerTokenMasterKey()) {
            return null;
        }
        return this.containerTokenMasterKey = this.convertFromProtoFormat(p.getContainerTokenMasterKey());
    }
    
    @Override
    public void setContainerTokenMasterKey(final MasterKey masterKey) {
        this.maybeInitBuilder();
        if (masterKey == null) {
            this.builder.clearContainerTokenMasterKey();
        }
        this.containerTokenMasterKey = masterKey;
    }
    
    @Override
    public MasterKey getNMTokenMasterKey() {
        final YarnServerCommonServiceProtos.NodeHeartbeatResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.nmTokenMasterKey != null) {
            return this.nmTokenMasterKey;
        }
        if (!p.hasNmTokenMasterKey()) {
            return null;
        }
        return this.nmTokenMasterKey = this.convertFromProtoFormat(p.getNmTokenMasterKey());
    }
    
    @Override
    public void setNMTokenMasterKey(final MasterKey masterKey) {
        this.maybeInitBuilder();
        if (masterKey == null) {
            this.builder.clearNmTokenMasterKey();
        }
        this.nmTokenMasterKey = masterKey;
    }
    
    @Override
    public NodeAction getNodeAction() {
        final YarnServerCommonServiceProtos.NodeHeartbeatResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasNodeAction()) {
            return null;
        }
        return this.convertFromProtoFormat(p.getNodeAction());
    }
    
    @Override
    public void setNodeAction(final NodeAction nodeAction) {
        this.maybeInitBuilder();
        if (nodeAction == null) {
            this.builder.clearNodeAction();
            return;
        }
        this.builder.setNodeAction(this.convertToProtoFormat(nodeAction));
    }
    
    @Override
    public String getDiagnosticsMessage() {
        final YarnServerCommonServiceProtos.NodeHeartbeatResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasDiagnosticsMessage()) {
            return null;
        }
        return p.getDiagnosticsMessage();
    }
    
    @Override
    public void setDiagnosticsMessage(final String diagnosticsMessage) {
        this.maybeInitBuilder();
        if (diagnosticsMessage == null) {
            this.builder.clearDiagnosticsMessage();
            return;
        }
        this.builder.setDiagnosticsMessage(diagnosticsMessage);
    }
    
    @Override
    public List<ContainerId> getContainersToCleanup() {
        this.initContainersToCleanup();
        return this.containersToCleanup;
    }
    
    @Override
    public List<ContainerId> getContainersToBeRemovedFromNM() {
        this.initContainersToBeRemovedFromNM();
        return this.containersToBeRemovedFromNM;
    }
    
    private void initContainersToCleanup() {
        if (this.containersToCleanup != null) {
            return;
        }
        final YarnServerCommonServiceProtos.NodeHeartbeatResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<YarnProtos.ContainerIdProto> list = p.getContainersToCleanupList();
        this.containersToCleanup = new ArrayList<ContainerId>();
        for (final YarnProtos.ContainerIdProto c : list) {
            this.containersToCleanup.add(this.convertFromProtoFormat(c));
        }
    }
    
    private void initContainersToBeRemovedFromNM() {
        if (this.containersToBeRemovedFromNM != null) {
            return;
        }
        final YarnServerCommonServiceProtos.NodeHeartbeatResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<YarnProtos.ContainerIdProto> list = p.getContainersToBeRemovedFromNmList();
        this.containersToBeRemovedFromNM = new ArrayList<ContainerId>();
        for (final YarnProtos.ContainerIdProto c : list) {
            this.containersToBeRemovedFromNM.add(this.convertFromProtoFormat(c));
        }
    }
    
    @Override
    public void addAllContainersToCleanup(final List<ContainerId> containersToCleanup) {
        if (containersToCleanup == null) {
            return;
        }
        this.initContainersToCleanup();
        this.containersToCleanup.addAll(containersToCleanup);
    }
    
    @Override
    public void addContainersToBeRemovedFromNM(final List<ContainerId> containers) {
        if (containers == null) {
            return;
        }
        this.initContainersToBeRemovedFromNM();
        this.containersToBeRemovedFromNM.addAll(containers);
    }
    
    private void addContainersToCleanupToProto() {
        this.maybeInitBuilder();
        this.builder.clearContainersToCleanup();
        if (this.containersToCleanup == null) {
            return;
        }
        final Iterable<YarnProtos.ContainerIdProto> iterable = new Iterable<YarnProtos.ContainerIdProto>() {
            @Override
            public Iterator<YarnProtos.ContainerIdProto> iterator() {
                return new Iterator<YarnProtos.ContainerIdProto>() {
                    Iterator<ContainerId> iter = NodeHeartbeatResponsePBImpl.this.containersToCleanup.iterator();
                    
                    @Override
                    public boolean hasNext() {
                        return this.iter.hasNext();
                    }
                    
                    @Override
                    public YarnProtos.ContainerIdProto next() {
                        return NodeHeartbeatResponsePBImpl.this.convertToProtoFormat(this.iter.next());
                    }
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
        this.builder.addAllContainersToCleanup(iterable);
    }
    
    private void addContainersToBeRemovedFromNMToProto() {
        this.maybeInitBuilder();
        this.builder.clearContainersToBeRemovedFromNm();
        if (this.containersToBeRemovedFromNM == null) {
            return;
        }
        final Iterable<YarnProtos.ContainerIdProto> iterable = new Iterable<YarnProtos.ContainerIdProto>() {
            @Override
            public Iterator<YarnProtos.ContainerIdProto> iterator() {
                return new Iterator<YarnProtos.ContainerIdProto>() {
                    Iterator<ContainerId> iter = NodeHeartbeatResponsePBImpl.this.containersToBeRemovedFromNM.iterator();
                    
                    @Override
                    public boolean hasNext() {
                        return this.iter.hasNext();
                    }
                    
                    @Override
                    public YarnProtos.ContainerIdProto next() {
                        return NodeHeartbeatResponsePBImpl.this.convertToProtoFormat(this.iter.next());
                    }
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
        this.builder.addAllContainersToBeRemovedFromNm(iterable);
    }
    
    @Override
    public List<ApplicationId> getApplicationsToCleanup() {
        this.initApplicationsToCleanup();
        return this.applicationsToCleanup;
    }
    
    private void initApplicationsToCleanup() {
        if (this.applicationsToCleanup != null) {
            return;
        }
        final YarnServerCommonServiceProtos.NodeHeartbeatResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<YarnProtos.ApplicationIdProto> list = p.getApplicationsToCleanupList();
        this.applicationsToCleanup = new ArrayList<ApplicationId>();
        for (final YarnProtos.ApplicationIdProto c : list) {
            this.applicationsToCleanup.add(this.convertFromProtoFormat(c));
        }
    }
    
    @Override
    public void addAllApplicationsToCleanup(final List<ApplicationId> applicationsToCleanup) {
        if (applicationsToCleanup == null) {
            return;
        }
        this.initApplicationsToCleanup();
        this.applicationsToCleanup.addAll(applicationsToCleanup);
    }
    
    private void addApplicationsToCleanupToProto() {
        this.maybeInitBuilder();
        this.builder.clearApplicationsToCleanup();
        if (this.applicationsToCleanup == null) {
            return;
        }
        final Iterable<YarnProtos.ApplicationIdProto> iterable = new Iterable<YarnProtos.ApplicationIdProto>() {
            @Override
            public Iterator<YarnProtos.ApplicationIdProto> iterator() {
                return new Iterator<YarnProtos.ApplicationIdProto>() {
                    Iterator<ApplicationId> iter = NodeHeartbeatResponsePBImpl.this.applicationsToCleanup.iterator();
                    
                    @Override
                    public boolean hasNext() {
                        return this.iter.hasNext();
                    }
                    
                    @Override
                    public YarnProtos.ApplicationIdProto next() {
                        return NodeHeartbeatResponsePBImpl.this.convertToProtoFormat(this.iter.next());
                    }
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
        this.builder.addAllApplicationsToCleanup(iterable);
    }
    
    @Override
    public Map<ApplicationId, ByteBuffer> getSystemCredentialsForApps() {
        if (this.systemCredentials != null) {
            return this.systemCredentials;
        }
        this.initSystemCredentials();
        return this.systemCredentials;
    }
    
    private void initSystemCredentials() {
        final YarnServerCommonServiceProtos.NodeHeartbeatResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<YarnServerCommonServiceProtos.SystemCredentialsForAppsProto> list = p.getSystemCredentialsForAppsList();
        this.systemCredentials = new HashMap<ApplicationId, ByteBuffer>();
        for (final YarnServerCommonServiceProtos.SystemCredentialsForAppsProto c : list) {
            final ApplicationId appId = this.convertFromProtoFormat(c.getAppId());
            final ByteBuffer byteBuffer = ProtoUtils.convertFromProtoFormat(c.getCredentialsForApp());
            this.systemCredentials.put(appId, byteBuffer);
        }
    }
    
    @Override
    public void setSystemCredentialsForApps(final Map<ApplicationId, ByteBuffer> systemCredentials) {
        if (systemCredentials == null || systemCredentials.isEmpty()) {
            return;
        }
        this.maybeInitBuilder();
        (this.systemCredentials = new HashMap<ApplicationId, ByteBuffer>()).putAll(systemCredentials);
    }
    
    @Override
    public long getNextHeartBeatInterval() {
        final YarnServerCommonServiceProtos.NodeHeartbeatResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getNextHeartBeatInterval();
    }
    
    @Override
    public void setNextHeartBeatInterval(final long nextHeartBeatInterval) {
        this.maybeInitBuilder();
        this.builder.setNextHeartBeatInterval(nextHeartBeatInterval);
    }
    
    private ContainerIdPBImpl convertFromProtoFormat(final YarnProtos.ContainerIdProto p) {
        return new ContainerIdPBImpl(p);
    }
    
    private YarnProtos.ContainerIdProto convertToProtoFormat(final ContainerId t) {
        return ((ContainerIdPBImpl)t).getProto();
    }
    
    private ApplicationIdPBImpl convertFromProtoFormat(final YarnProtos.ApplicationIdProto p) {
        return new ApplicationIdPBImpl(p);
    }
    
    private YarnProtos.ApplicationIdProto convertToProtoFormat(final ApplicationId t) {
        return ((ApplicationIdPBImpl)t).getProto();
    }
    
    private NodeAction convertFromProtoFormat(final YarnServerCommonProtos.NodeActionProto p) {
        return NodeAction.valueOf(p.name());
    }
    
    private YarnServerCommonProtos.NodeActionProto convertToProtoFormat(final NodeAction t) {
        return YarnServerCommonProtos.NodeActionProto.valueOf(t.name());
    }
    
    private MasterKeyPBImpl convertFromProtoFormat(final YarnServerCommonProtos.MasterKeyProto p) {
        return new MasterKeyPBImpl(p);
    }
    
    private YarnServerCommonProtos.MasterKeyProto convertToProtoFormat(final MasterKey t) {
        return ((MasterKeyPBImpl)t).getProto();
    }
}
