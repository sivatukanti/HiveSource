// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records.impl.pb;

import org.apache.hadoop.yarn.api.records.LocalResourceVisibility;
import org.apache.hadoop.yarn.api.records.LocalResourceType;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.api.records.URL;
import org.apache.hadoop.yarn.proto.YarnProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.records.LocalResource;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class LocalResourcePBImpl extends LocalResource
{
    YarnProtos.LocalResourceProto proto;
    YarnProtos.LocalResourceProto.Builder builder;
    boolean viaProto;
    private URL url;
    
    public LocalResourcePBImpl() {
        this.proto = YarnProtos.LocalResourceProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.url = null;
        this.builder = YarnProtos.LocalResourceProto.newBuilder();
    }
    
    public LocalResourcePBImpl(final YarnProtos.LocalResourceProto proto) {
        this.proto = YarnProtos.LocalResourceProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.url = null;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public synchronized YarnProtos.LocalResourceProto getProto() {
        this.mergeLocalToBuilder();
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((LocalResourcePBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    private synchronized void mergeLocalToBuilder() {
        YarnProtos.LocalResourceProtoOrBuilder l = this.viaProto ? this.proto : this.builder;
        if (this.url != null && !l.getResource().equals(((URLPBImpl)this.url).getProto())) {
            this.maybeInitBuilder();
            l = this.builder;
            this.builder.setResource(this.convertToProtoFormat(this.url));
        }
    }
    
    private synchronized void maybeInitBuilder() {
        if (this.viaProto || this.builder == null) {
            this.builder = YarnProtos.LocalResourceProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public synchronized long getSize() {
        final YarnProtos.LocalResourceProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getSize();
    }
    
    @Override
    public synchronized void setSize(final long size) {
        this.maybeInitBuilder();
        this.builder.setSize(size);
    }
    
    @Override
    public synchronized long getTimestamp() {
        final YarnProtos.LocalResourceProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getTimestamp();
    }
    
    @Override
    public synchronized void setTimestamp(final long timestamp) {
        this.maybeInitBuilder();
        this.builder.setTimestamp(timestamp);
    }
    
    @Override
    public synchronized LocalResourceType getType() {
        final YarnProtos.LocalResourceProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasType()) {
            return null;
        }
        return this.convertFromProtoFormat(p.getType());
    }
    
    @Override
    public synchronized void setType(final LocalResourceType type) {
        this.maybeInitBuilder();
        if (type == null) {
            this.builder.clearType();
            return;
        }
        this.builder.setType(this.convertToProtoFormat(type));
    }
    
    @Override
    public synchronized URL getResource() {
        final YarnProtos.LocalResourceProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.url != null) {
            return this.url;
        }
        if (!p.hasResource()) {
            return null;
        }
        return this.url = this.convertFromProtoFormat(p.getResource());
    }
    
    @Override
    public synchronized void setResource(final URL resource) {
        this.maybeInitBuilder();
        if (resource == null) {
            this.builder.clearResource();
        }
        this.url = resource;
    }
    
    @Override
    public synchronized LocalResourceVisibility getVisibility() {
        final YarnProtos.LocalResourceProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasVisibility()) {
            return null;
        }
        return this.convertFromProtoFormat(p.getVisibility());
    }
    
    @Override
    public synchronized void setVisibility(final LocalResourceVisibility visibility) {
        this.maybeInitBuilder();
        if (visibility == null) {
            this.builder.clearVisibility();
            return;
        }
        this.builder.setVisibility(this.convertToProtoFormat(visibility));
    }
    
    @Override
    public synchronized String getPattern() {
        final YarnProtos.LocalResourceProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasPattern()) {
            return null;
        }
        return p.getPattern();
    }
    
    @Override
    public synchronized void setPattern(final String pattern) {
        this.maybeInitBuilder();
        if (pattern == null) {
            this.builder.clearPattern();
            return;
        }
        this.builder.setPattern(pattern);
    }
    
    private YarnProtos.LocalResourceTypeProto convertToProtoFormat(final LocalResourceType e) {
        return ProtoUtils.convertToProtoFormat(e);
    }
    
    private LocalResourceType convertFromProtoFormat(final YarnProtos.LocalResourceTypeProto e) {
        return ProtoUtils.convertFromProtoFormat(e);
    }
    
    private URLPBImpl convertFromProtoFormat(final YarnProtos.URLProto p) {
        return new URLPBImpl(p);
    }
    
    private YarnProtos.URLProto convertToProtoFormat(final URL t) {
        return ((URLPBImpl)t).getProto();
    }
    
    private YarnProtos.LocalResourceVisibilityProto convertToProtoFormat(final LocalResourceVisibility e) {
        return ProtoUtils.convertToProtoFormat(e);
    }
    
    private LocalResourceVisibility convertFromProtoFormat(final YarnProtos.LocalResourceVisibilityProto e) {
        return ProtoUtils.convertFromProtoFormat(e);
    }
}
