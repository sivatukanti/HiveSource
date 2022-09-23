// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records.impl.pb;

import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.api.records.PreemptionContract;
import org.apache.hadoop.yarn.api.records.StrictPreemptionContract;
import org.apache.hadoop.yarn.proto.YarnProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.records.PreemptionMessage;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class PreemptionMessagePBImpl extends PreemptionMessage
{
    YarnProtos.PreemptionMessageProto proto;
    YarnProtos.PreemptionMessageProto.Builder builder;
    boolean viaProto;
    private StrictPreemptionContract strict;
    private PreemptionContract contract;
    
    public PreemptionMessagePBImpl() {
        this.proto = YarnProtos.PreemptionMessageProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = YarnProtos.PreemptionMessageProto.newBuilder();
    }
    
    public PreemptionMessagePBImpl(final YarnProtos.PreemptionMessageProto proto) {
        this.proto = YarnProtos.PreemptionMessageProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public synchronized YarnProtos.PreemptionMessageProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((PreemptionMessagePBImpl)this.getClass().cast(other)).getProto());
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
        if (this.strict != null) {
            this.builder.setStrictContract(this.convertToProtoFormat(this.strict));
        }
        if (this.contract != null) {
            this.builder.setContract(this.convertToProtoFormat(this.contract));
        }
    }
    
    private void maybeInitBuilder() {
        if (this.viaProto || this.builder == null) {
            this.builder = YarnProtos.PreemptionMessageProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public synchronized StrictPreemptionContract getStrictContract() {
        final YarnProtos.PreemptionMessageProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.strict != null) {
            return this.strict;
        }
        if (!p.hasStrictContract()) {
            return null;
        }
        return this.strict = this.convertFromProtoFormat(p.getStrictContract());
    }
    
    @Override
    public synchronized void setStrictContract(final StrictPreemptionContract strict) {
        this.maybeInitBuilder();
        if (null == strict) {
            this.builder.clearStrictContract();
        }
        this.strict = strict;
    }
    
    @Override
    public synchronized PreemptionContract getContract() {
        final YarnProtos.PreemptionMessageProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.contract != null) {
            return this.contract;
        }
        if (!p.hasContract()) {
            return null;
        }
        return this.contract = this.convertFromProtoFormat(p.getContract());
    }
    
    @Override
    public synchronized void setContract(final PreemptionContract c) {
        this.maybeInitBuilder();
        if (null == c) {
            this.builder.clearContract();
        }
        this.contract = c;
    }
    
    private StrictPreemptionContractPBImpl convertFromProtoFormat(final YarnProtos.StrictPreemptionContractProto p) {
        return new StrictPreemptionContractPBImpl(p);
    }
    
    private YarnProtos.StrictPreemptionContractProto convertToProtoFormat(final StrictPreemptionContract t) {
        return ((StrictPreemptionContractPBImpl)t).getProto();
    }
    
    private PreemptionContractPBImpl convertFromProtoFormat(final YarnProtos.PreemptionContractProto p) {
        return new PreemptionContractPBImpl(p);
    }
    
    private YarnProtos.PreemptionContractProto convertToProtoFormat(final PreemptionContract t) {
        return ((PreemptionContractPBImpl)t).getProto();
    }
}
