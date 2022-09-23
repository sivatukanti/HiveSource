// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records.impl.pb;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.yarn.proto.YarnProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.records.ResourceBlacklistRequest;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class ResourceBlacklistRequestPBImpl extends ResourceBlacklistRequest
{
    YarnProtos.ResourceBlacklistRequestProto proto;
    YarnProtos.ResourceBlacklistRequestProto.Builder builder;
    boolean viaProto;
    List<String> blacklistAdditions;
    List<String> blacklistRemovals;
    
    public ResourceBlacklistRequestPBImpl() {
        this.proto = null;
        this.builder = null;
        this.viaProto = false;
        this.blacklistAdditions = null;
        this.blacklistRemovals = null;
        this.builder = YarnProtos.ResourceBlacklistRequestProto.newBuilder();
    }
    
    public ResourceBlacklistRequestPBImpl(final YarnProtos.ResourceBlacklistRequestProto proto) {
        this.proto = null;
        this.builder = null;
        this.viaProto = false;
        this.blacklistAdditions = null;
        this.blacklistRemovals = null;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnProtos.ResourceBlacklistRequestProto getProto() {
        this.mergeLocalToProto();
        this.proto = (this.viaProto ? this.proto : this.builder.build());
        this.viaProto = true;
        return this.proto;
    }
    
    private void maybeInitBuilder() {
        if (this.viaProto || this.builder == null) {
            this.builder = YarnProtos.ResourceBlacklistRequestProto.newBuilder(this.proto);
        }
        this.viaProto = false;
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
        if (this.blacklistAdditions != null) {
            this.addBlacklistAdditionsToProto();
        }
        if (this.blacklistRemovals != null) {
            this.addBlacklistRemovalsToProto();
        }
    }
    
    private void addBlacklistAdditionsToProto() {
        this.maybeInitBuilder();
        this.builder.clearBlacklistAdditions();
        if (this.blacklistAdditions == null) {
            return;
        }
        this.builder.addAllBlacklistAdditions(this.blacklistAdditions);
    }
    
    private void addBlacklistRemovalsToProto() {
        this.maybeInitBuilder();
        this.builder.clearBlacklistRemovals();
        if (this.blacklistRemovals == null) {
            return;
        }
        this.builder.addAllBlacklistRemovals(this.blacklistRemovals);
    }
    
    private void initBlacklistAdditions() {
        if (this.blacklistAdditions != null) {
            return;
        }
        final YarnProtos.ResourceBlacklistRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<String> list = p.getBlacklistAdditionsList();
        (this.blacklistAdditions = new ArrayList<String>()).addAll(list);
    }
    
    private void initBlacklistRemovals() {
        if (this.blacklistRemovals != null) {
            return;
        }
        final YarnProtos.ResourceBlacklistRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<String> list = p.getBlacklistRemovalsList();
        (this.blacklistRemovals = new ArrayList<String>()).addAll(list);
    }
    
    @Override
    public List<String> getBlacklistAdditions() {
        this.initBlacklistAdditions();
        return this.blacklistAdditions;
    }
    
    @Override
    public void setBlacklistAdditions(final List<String> resourceNames) {
        if (resourceNames == null || resourceNames.isEmpty()) {
            if (this.blacklistAdditions != null) {
                this.blacklistAdditions.clear();
            }
            return;
        }
        this.initBlacklistAdditions();
        this.blacklistAdditions.clear();
        this.blacklistAdditions.addAll(resourceNames);
    }
    
    @Override
    public List<String> getBlacklistRemovals() {
        this.initBlacklistRemovals();
        return this.blacklistRemovals;
    }
    
    @Override
    public void setBlacklistRemovals(final List<String> resourceNames) {
        if (resourceNames == null || resourceNames.isEmpty()) {
            if (this.blacklistRemovals != null) {
                this.blacklistRemovals.clear();
            }
            return;
        }
        this.initBlacklistRemovals();
        this.blacklistRemovals.clear();
        this.blacklistRemovals.addAll(resourceNames);
    }
    
    @Override
    public int hashCode() {
        return this.getProto().hashCode();
    }
    
    @Override
    public boolean equals(final Object other) {
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((ResourceBlacklistRequestPBImpl)this.getClass().cast(other)).getProto());
    }
}
