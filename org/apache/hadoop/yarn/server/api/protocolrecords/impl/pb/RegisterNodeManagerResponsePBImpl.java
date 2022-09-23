// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb;

import com.google.protobuf.Message;
import org.apache.hadoop.yarn.server.api.records.impl.pb.MasterKeyPBImpl;
import org.apache.hadoop.yarn.proto.YarnServerCommonProtos;
import org.apache.hadoop.yarn.server.api.records.NodeAction;
import org.apache.hadoop.yarn.server.api.records.MasterKey;
import org.apache.hadoop.yarn.server.api.protocolrecords.RegisterNodeManagerResponse;
import org.apache.hadoop.yarn.proto.YarnServerCommonServiceProtos;
import org.apache.hadoop.yarn.api.records.impl.pb.ProtoBase;

public class RegisterNodeManagerResponsePBImpl extends ProtoBase<YarnServerCommonServiceProtos.RegisterNodeManagerResponseProto> implements RegisterNodeManagerResponse
{
    YarnServerCommonServiceProtos.RegisterNodeManagerResponseProto proto;
    YarnServerCommonServiceProtos.RegisterNodeManagerResponseProto.Builder builder;
    boolean viaProto;
    private MasterKey containerTokenMasterKey;
    private MasterKey nmTokenMasterKey;
    private boolean rebuild;
    
    public RegisterNodeManagerResponsePBImpl() {
        this.proto = YarnServerCommonServiceProtos.RegisterNodeManagerResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.containerTokenMasterKey = null;
        this.nmTokenMasterKey = null;
        this.rebuild = false;
        this.builder = YarnServerCommonServiceProtos.RegisterNodeManagerResponseProto.newBuilder();
    }
    
    public RegisterNodeManagerResponsePBImpl(final YarnServerCommonServiceProtos.RegisterNodeManagerResponseProto proto) {
        this.proto = YarnServerCommonServiceProtos.RegisterNodeManagerResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.containerTokenMasterKey = null;
        this.nmTokenMasterKey = null;
        this.rebuild = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    @Override
    public YarnServerCommonServiceProtos.RegisterNodeManagerResponseProto getProto() {
        if (this.rebuild) {
            this.mergeLocalToProto();
        }
        this.proto = (this.viaProto ? this.proto : this.builder.build());
        this.viaProto = true;
        return this.proto;
    }
    
    private void mergeLocalToBuilder() {
        if (this.containerTokenMasterKey != null) {
            this.builder.setContainerTokenMasterKey(this.convertToProtoFormat(this.containerTokenMasterKey));
        }
        if (this.nmTokenMasterKey != null) {
            this.builder.setNmTokenMasterKey(this.convertToProtoFormat(this.nmTokenMasterKey));
        }
    }
    
    private void mergeLocalToProto() {
        if (this.viaProto) {
            this.maybeInitBuilder();
        }
        this.mergeLocalToBuilder();
        this.proto = this.builder.build();
        this.rebuild = false;
        this.viaProto = true;
    }
    
    private void maybeInitBuilder() {
        if (this.viaProto || this.builder == null) {
            this.builder = YarnServerCommonServiceProtos.RegisterNodeManagerResponseProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public MasterKey getContainerTokenMasterKey() {
        final YarnServerCommonServiceProtos.RegisterNodeManagerResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
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
        this.rebuild = true;
    }
    
    @Override
    public MasterKey getNMTokenMasterKey() {
        final YarnServerCommonServiceProtos.RegisterNodeManagerResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
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
        this.rebuild = true;
    }
    
    @Override
    public String getDiagnosticsMessage() {
        final YarnServerCommonServiceProtos.RegisterNodeManagerResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
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
    public String getRMVersion() {
        final YarnServerCommonServiceProtos.RegisterNodeManagerResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasRmVersion()) {
            return null;
        }
        return p.getRmVersion();
    }
    
    @Override
    public void setRMVersion(final String rmVersion) {
        this.maybeInitBuilder();
        if (rmVersion == null) {
            this.builder.clearRmIdentifier();
            return;
        }
        this.builder.setRmVersion(rmVersion);
    }
    
    @Override
    public NodeAction getNodeAction() {
        final YarnServerCommonServiceProtos.RegisterNodeManagerResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
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
        }
        else {
            this.builder.setNodeAction(this.convertToProtoFormat(nodeAction));
        }
        this.rebuild = true;
    }
    
    @Override
    public long getRMIdentifier() {
        final YarnServerCommonServiceProtos.RegisterNodeManagerResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getRmIdentifier();
    }
    
    @Override
    public void setRMIdentifier(final long rmIdentifier) {
        this.maybeInitBuilder();
        this.builder.setRmIdentifier(rmIdentifier);
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
