// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.recovery.records.impl.pb;

import org.apache.hadoop.yarn.server.api.records.impl.pb.MasterKeyPBImpl;
import org.apache.hadoop.yarn.proto.YarnServerCommonProtos;
import org.apache.hadoop.yarn.server.api.records.MasterKey;
import org.apache.hadoop.yarn.proto.YarnServerResourceManagerRecoveryProtos;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.records.AMRMTokenSecretManagerState;

public class AMRMTokenSecretManagerStatePBImpl extends AMRMTokenSecretManagerState
{
    YarnServerResourceManagerRecoveryProtos.AMRMTokenSecretManagerStateProto proto;
    YarnServerResourceManagerRecoveryProtos.AMRMTokenSecretManagerStateProto.Builder builder;
    boolean viaProto;
    private MasterKey currentMasterKey;
    private MasterKey nextMasterKey;
    
    public AMRMTokenSecretManagerStatePBImpl() {
        this.proto = YarnServerResourceManagerRecoveryProtos.AMRMTokenSecretManagerStateProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.currentMasterKey = null;
        this.nextMasterKey = null;
        this.builder = YarnServerResourceManagerRecoveryProtos.AMRMTokenSecretManagerStateProto.newBuilder();
    }
    
    public AMRMTokenSecretManagerStatePBImpl(final YarnServerResourceManagerRecoveryProtos.AMRMTokenSecretManagerStateProto proto) {
        this.proto = YarnServerResourceManagerRecoveryProtos.AMRMTokenSecretManagerStateProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.currentMasterKey = null;
        this.nextMasterKey = null;
        this.proto = proto;
        this.viaProto = true;
    }
    
    @Override
    public YarnServerResourceManagerRecoveryProtos.AMRMTokenSecretManagerStateProto getProto() {
        this.mergeLocalToProto();
        this.proto = (this.viaProto ? this.proto : this.builder.build());
        this.viaProto = true;
        return this.proto;
    }
    
    private void mergeLocalToBuilder() {
        if (this.currentMasterKey != null) {
            this.builder.setCurrentMasterKey(this.convertToProtoFormat(this.currentMasterKey));
        }
        if (this.nextMasterKey != null) {
            this.builder.setNextMasterKey(this.convertToProtoFormat(this.nextMasterKey));
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
            this.builder = YarnServerResourceManagerRecoveryProtos.AMRMTokenSecretManagerStateProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public MasterKey getCurrentMasterKey() {
        final YarnServerResourceManagerRecoveryProtos.AMRMTokenSecretManagerStateProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.currentMasterKey != null) {
            return this.currentMasterKey;
        }
        if (!p.hasCurrentMasterKey()) {
            return null;
        }
        return this.currentMasterKey = this.convertFromProtoFormat(p.getCurrentMasterKey());
    }
    
    @Override
    public void setCurrentMasterKey(final MasterKey currentMasterKey) {
        this.maybeInitBuilder();
        if (currentMasterKey == null) {
            this.builder.clearCurrentMasterKey();
        }
        this.currentMasterKey = currentMasterKey;
    }
    
    @Override
    public MasterKey getNextMasterKey() {
        final YarnServerResourceManagerRecoveryProtos.AMRMTokenSecretManagerStateProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.nextMasterKey != null) {
            return this.nextMasterKey;
        }
        if (!p.hasNextMasterKey()) {
            return null;
        }
        return this.nextMasterKey = this.convertFromProtoFormat(p.getNextMasterKey());
    }
    
    @Override
    public void setNextMasterKey(final MasterKey nextMasterKey) {
        this.maybeInitBuilder();
        if (nextMasterKey == null) {
            this.builder.clearNextMasterKey();
        }
        this.nextMasterKey = nextMasterKey;
    }
    
    private YarnServerCommonProtos.MasterKeyProto convertToProtoFormat(final MasterKey t) {
        return ((MasterKeyPBImpl)t).getProto();
    }
    
    private MasterKeyPBImpl convertFromProtoFormat(final YarnServerCommonProtos.MasterKeyProto p) {
        return new MasterKeyPBImpl(p);
    }
}
