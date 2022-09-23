// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records.impl.pb;

import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.proto.YarnProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.records.URL;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class URLPBImpl extends URL
{
    YarnProtos.URLProto proto;
    YarnProtos.URLProto.Builder builder;
    boolean viaProto;
    
    public URLPBImpl() {
        this.proto = YarnProtos.URLProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = YarnProtos.URLProto.newBuilder();
    }
    
    public URLPBImpl(final YarnProtos.URLProto proto) {
        this.proto = YarnProtos.URLProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnProtos.URLProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((URLPBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    private void maybeInitBuilder() {
        if (this.viaProto || this.builder == null) {
            this.builder = YarnProtos.URLProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public String getFile() {
        final YarnProtos.URLProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasFile()) {
            return null;
        }
        return p.getFile();
    }
    
    @Override
    public void setFile(final String file) {
        this.maybeInitBuilder();
        if (file == null) {
            this.builder.clearFile();
            return;
        }
        this.builder.setFile(file);
    }
    
    @Override
    public String getScheme() {
        final YarnProtos.URLProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasScheme()) {
            return null;
        }
        return p.getScheme();
    }
    
    @Override
    public void setScheme(final String scheme) {
        this.maybeInitBuilder();
        if (scheme == null) {
            this.builder.clearScheme();
            return;
        }
        this.builder.setScheme(scheme);
    }
    
    @Override
    public String getUserInfo() {
        final YarnProtos.URLProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasUserInfo()) {
            return null;
        }
        return p.getUserInfo();
    }
    
    @Override
    public void setUserInfo(final String userInfo) {
        this.maybeInitBuilder();
        if (userInfo == null) {
            this.builder.clearUserInfo();
            return;
        }
        this.builder.setUserInfo(userInfo);
    }
    
    @Override
    public String getHost() {
        final YarnProtos.URLProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasHost()) {
            return null;
        }
        return p.getHost();
    }
    
    @Override
    public void setHost(final String host) {
        this.maybeInitBuilder();
        if (host == null) {
            this.builder.clearHost();
            return;
        }
        this.builder.setHost(host);
    }
    
    @Override
    public int getPort() {
        final YarnProtos.URLProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getPort();
    }
    
    @Override
    public void setPort(final int port) {
        this.maybeInitBuilder();
        this.builder.setPort(port);
    }
}
