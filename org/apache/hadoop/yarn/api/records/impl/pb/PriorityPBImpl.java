// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records.impl.pb;

import org.apache.hadoop.yarn.proto.YarnProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.records.Priority;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class PriorityPBImpl extends Priority
{
    YarnProtos.PriorityProto proto;
    YarnProtos.PriorityProto.Builder builder;
    boolean viaProto;
    
    public PriorityPBImpl() {
        this.proto = YarnProtos.PriorityProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = YarnProtos.PriorityProto.newBuilder();
    }
    
    public PriorityPBImpl(final YarnProtos.PriorityProto proto) {
        this.proto = YarnProtos.PriorityProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnProtos.PriorityProto getProto() {
        this.proto = (this.viaProto ? this.proto : this.builder.build());
        this.viaProto = true;
        return this.proto;
    }
    
    private void maybeInitBuilder() {
        if (this.viaProto || this.builder == null) {
            this.builder = YarnProtos.PriorityProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public int getPriority() {
        final YarnProtos.PriorityProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getPriority();
    }
    
    @Override
    public void setPriority(final int priority) {
        this.maybeInitBuilder();
        this.builder.setPriority(priority);
    }
    
    @Override
    public String toString() {
        return Integer.valueOf(this.getPriority()).toString();
    }
}
