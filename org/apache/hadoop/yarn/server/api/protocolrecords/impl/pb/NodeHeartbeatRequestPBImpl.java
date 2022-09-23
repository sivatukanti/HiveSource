// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb;

import org.apache.hadoop.yarn.server.api.records.impl.pb.MasterKeyPBImpl;
import org.apache.hadoop.yarn.server.api.records.impl.pb.NodeStatusPBImpl;
import org.apache.hadoop.yarn.proto.YarnServerCommonProtos;
import org.apache.hadoop.yarn.server.api.records.MasterKey;
import org.apache.hadoop.yarn.server.api.records.NodeStatus;
import org.apache.hadoop.yarn.proto.YarnServerCommonServiceProtos;
import org.apache.hadoop.yarn.server.api.protocolrecords.NodeHeartbeatRequest;

public class NodeHeartbeatRequestPBImpl extends NodeHeartbeatRequest
{
    YarnServerCommonServiceProtos.NodeHeartbeatRequestProto proto;
    YarnServerCommonServiceProtos.NodeHeartbeatRequestProto.Builder builder;
    boolean viaProto;
    private NodeStatus nodeStatus;
    private MasterKey lastKnownContainerTokenMasterKey;
    private MasterKey lastKnownNMTokenMasterKey;
    
    public NodeHeartbeatRequestPBImpl() {
        this.proto = YarnServerCommonServiceProtos.NodeHeartbeatRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.nodeStatus = null;
        this.lastKnownContainerTokenMasterKey = null;
        this.lastKnownNMTokenMasterKey = null;
        this.builder = YarnServerCommonServiceProtos.NodeHeartbeatRequestProto.newBuilder();
    }
    
    public NodeHeartbeatRequestPBImpl(final YarnServerCommonServiceProtos.NodeHeartbeatRequestProto proto) {
        this.proto = YarnServerCommonServiceProtos.NodeHeartbeatRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.nodeStatus = null;
        this.lastKnownContainerTokenMasterKey = null;
        this.lastKnownNMTokenMasterKey = null;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnServerCommonServiceProtos.NodeHeartbeatRequestProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((NodeHeartbeatRequestPBImpl)this.getClass().cast(other)).getProto());
    }
    
    private void mergeLocalToBuilder() {
        if (this.nodeStatus != null) {
            this.builder.setNodeStatus(this.convertToProtoFormat(this.nodeStatus));
        }
        if (this.lastKnownContainerTokenMasterKey != null) {
            this.builder.setLastKnownContainerTokenMasterKey(this.convertToProtoFormat(this.lastKnownContainerTokenMasterKey));
        }
        if (this.lastKnownNMTokenMasterKey != null) {
            this.builder.setLastKnownNmTokenMasterKey(this.convertToProtoFormat(this.lastKnownNMTokenMasterKey));
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
            this.builder = YarnServerCommonServiceProtos.NodeHeartbeatRequestProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public NodeStatus getNodeStatus() {
        final YarnServerCommonServiceProtos.NodeHeartbeatRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.nodeStatus != null) {
            return this.nodeStatus;
        }
        if (!p.hasNodeStatus()) {
            return null;
        }
        return this.nodeStatus = this.convertFromProtoFormat(p.getNodeStatus());
    }
    
    @Override
    public void setNodeStatus(final NodeStatus nodeStatus) {
        this.maybeInitBuilder();
        if (nodeStatus == null) {
            this.builder.clearNodeStatus();
        }
        this.nodeStatus = nodeStatus;
    }
    
    @Override
    public MasterKey getLastKnownContainerTokenMasterKey() {
        final YarnServerCommonServiceProtos.NodeHeartbeatRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.lastKnownContainerTokenMasterKey != null) {
            return this.lastKnownContainerTokenMasterKey;
        }
        if (!p.hasLastKnownContainerTokenMasterKey()) {
            return null;
        }
        return this.lastKnownContainerTokenMasterKey = this.convertFromProtoFormat(p.getLastKnownContainerTokenMasterKey());
    }
    
    @Override
    public void setLastKnownContainerTokenMasterKey(final MasterKey masterKey) {
        this.maybeInitBuilder();
        if (masterKey == null) {
            this.builder.clearLastKnownContainerTokenMasterKey();
        }
        this.lastKnownContainerTokenMasterKey = masterKey;
    }
    
    @Override
    public MasterKey getLastKnownNMTokenMasterKey() {
        final YarnServerCommonServiceProtos.NodeHeartbeatRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.lastKnownNMTokenMasterKey != null) {
            return this.lastKnownNMTokenMasterKey;
        }
        if (!p.hasLastKnownNmTokenMasterKey()) {
            return null;
        }
        return this.lastKnownNMTokenMasterKey = this.convertFromProtoFormat(p.getLastKnownNmTokenMasterKey());
    }
    
    @Override
    public void setLastKnownNMTokenMasterKey(final MasterKey masterKey) {
        this.maybeInitBuilder();
        if (masterKey == null) {
            this.builder.clearLastKnownNmTokenMasterKey();
        }
        this.lastKnownNMTokenMasterKey = masterKey;
    }
    
    private NodeStatusPBImpl convertFromProtoFormat(final YarnServerCommonProtos.NodeStatusProto p) {
        return new NodeStatusPBImpl(p);
    }
    
    private YarnServerCommonProtos.NodeStatusProto convertToProtoFormat(final NodeStatus t) {
        return ((NodeStatusPBImpl)t).getProto();
    }
    
    private MasterKeyPBImpl convertFromProtoFormat(final YarnServerCommonProtos.MasterKeyProto p) {
        return new MasterKeyPBImpl(p);
    }
    
    private YarnServerCommonProtos.MasterKeyProto convertToProtoFormat(final MasterKey t) {
        return ((MasterKeyPBImpl)t).getProto();
    }
}
