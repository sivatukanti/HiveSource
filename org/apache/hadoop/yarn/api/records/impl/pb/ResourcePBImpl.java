// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records.impl.pb;

import org.apache.hadoop.yarn.proto.YarnProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.records.Resource;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class ResourcePBImpl extends Resource
{
    YarnProtos.ResourceProto proto;
    YarnProtos.ResourceProto.Builder builder;
    boolean viaProto;
    
    public ResourcePBImpl() {
        this.proto = YarnProtos.ResourceProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = YarnProtos.ResourceProto.newBuilder();
    }
    
    public ResourcePBImpl(final YarnProtos.ResourceProto proto) {
        this.proto = YarnProtos.ResourceProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnProtos.ResourceProto getProto() {
        this.proto = (this.viaProto ? this.proto : this.builder.build());
        this.viaProto = true;
        return this.proto;
    }
    
    private void maybeInitBuilder() {
        if (this.viaProto || this.builder == null) {
            this.builder = YarnProtos.ResourceProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public int getMemory() {
        final YarnProtos.ResourceProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getMemory();
    }
    
    @Override
    public void setMemory(final int memory) {
        this.maybeInitBuilder();
        this.builder.setMemory(memory);
    }
    
    @Override
    public int getVirtualCores() {
        final YarnProtos.ResourceProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getVirtualCores();
    }
    
    @Override
    public void setVirtualCores(final int vCores) {
        this.maybeInitBuilder();
        this.builder.setVirtualCores(vCores);
    }
    
    @Override
    public int compareTo(final Resource other) {
        int diff = this.getMemory() - other.getMemory();
        if (diff == 0) {
            diff = this.getVirtualCores() - other.getVirtualCores();
        }
        return diff;
    }
}
