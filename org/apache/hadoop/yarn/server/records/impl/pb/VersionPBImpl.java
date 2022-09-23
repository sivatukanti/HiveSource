// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.records.impl.pb;

import org.apache.hadoop.yarn.proto.YarnServerCommonProtos;
import org.apache.hadoop.yarn.server.records.Version;

public class VersionPBImpl extends Version
{
    YarnServerCommonProtos.VersionProto proto;
    YarnServerCommonProtos.VersionProto.Builder builder;
    boolean viaProto;
    
    public VersionPBImpl() {
        this.proto = YarnServerCommonProtos.VersionProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = YarnServerCommonProtos.VersionProto.newBuilder();
    }
    
    public VersionPBImpl(final YarnServerCommonProtos.VersionProto proto) {
        this.proto = YarnServerCommonProtos.VersionProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnServerCommonProtos.VersionProto getProto() {
        this.proto = (this.viaProto ? this.proto : this.builder.build());
        this.viaProto = true;
        return this.proto;
    }
    
    private void maybeInitBuilder() {
        if (this.viaProto || this.builder == null) {
            this.builder = YarnServerCommonProtos.VersionProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public int getMajorVersion() {
        final YarnServerCommonProtos.VersionProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getMajorVersion();
    }
    
    @Override
    public void setMajorVersion(final int major) {
        this.maybeInitBuilder();
        this.builder.setMajorVersion(major);
    }
    
    @Override
    public int getMinorVersion() {
        final YarnServerCommonProtos.VersionProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getMinorVersion();
    }
    
    @Override
    public void setMinorVersion(final int minor) {
        this.maybeInitBuilder();
        this.builder.setMinorVersion(minor);
    }
}
