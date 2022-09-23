// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.recovery.records.impl.pb;

import org.apache.hadoop.yarn.proto.YarnServerResourceManagerRecoveryProtos;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.records.Epoch;

public class EpochPBImpl extends Epoch
{
    YarnServerResourceManagerRecoveryProtos.EpochProto proto;
    YarnServerResourceManagerRecoveryProtos.EpochProto.Builder builder;
    boolean viaProto;
    
    public EpochPBImpl() {
        this.proto = YarnServerResourceManagerRecoveryProtos.EpochProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = YarnServerResourceManagerRecoveryProtos.EpochProto.newBuilder();
    }
    
    public EpochPBImpl(final YarnServerResourceManagerRecoveryProtos.EpochProto proto) {
        this.proto = YarnServerResourceManagerRecoveryProtos.EpochProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    @Override
    public YarnServerResourceManagerRecoveryProtos.EpochProto getProto() {
        this.proto = (this.viaProto ? this.proto : this.builder.build());
        this.viaProto = true;
        return this.proto;
    }
    
    private void maybeInitBuilder() {
        if (this.viaProto || this.builder == null) {
            this.builder = YarnServerResourceManagerRecoveryProtos.EpochProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public long getEpoch() {
        final YarnServerResourceManagerRecoveryProtos.EpochProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getEpoch();
    }
    
    @Override
    public void setEpoch(final long sequentialNumber) {
        this.maybeInitBuilder();
        this.builder.setEpoch(sequentialNumber);
    }
}
