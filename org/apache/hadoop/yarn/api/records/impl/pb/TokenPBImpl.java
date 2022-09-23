// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records.impl.pb;

import com.google.protobuf.ByteString;
import java.nio.ByteBuffer;
import org.apache.hadoop.security.proto.SecurityProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.records.Token;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class TokenPBImpl extends Token
{
    private SecurityProtos.TokenProto proto;
    private SecurityProtos.TokenProto.Builder builder;
    private boolean viaProto;
    private ByteBuffer identifier;
    private ByteBuffer password;
    
    public TokenPBImpl() {
        this.proto = SecurityProtos.TokenProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = SecurityProtos.TokenProto.newBuilder();
    }
    
    public TokenPBImpl(final SecurityProtos.TokenProto proto) {
        this.proto = SecurityProtos.TokenProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public synchronized SecurityProtos.TokenProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((TokenPBImpl)this.getClass().cast(other)).getProto());
    }
    
    protected final ByteBuffer convertFromProtoFormat(final ByteString byteString) {
        return ProtoUtils.convertFromProtoFormat(byteString);
    }
    
    protected final ByteString convertToProtoFormat(final ByteBuffer byteBuffer) {
        return ProtoUtils.convertToProtoFormat(byteBuffer);
    }
    
    private synchronized void mergeLocalToBuilder() {
        if (this.identifier != null) {
            this.builder.setIdentifier(this.convertToProtoFormat(this.identifier));
        }
        if (this.password != null) {
            this.builder.setPassword(this.convertToProtoFormat(this.password));
        }
    }
    
    private synchronized void mergeLocalToProto() {
        if (this.viaProto) {
            this.maybeInitBuilder();
        }
        this.mergeLocalToBuilder();
        this.proto = this.builder.build();
        this.viaProto = true;
    }
    
    private synchronized void maybeInitBuilder() {
        if (this.viaProto || this.builder == null) {
            this.builder = SecurityProtos.TokenProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public synchronized ByteBuffer getIdentifier() {
        final SecurityProtos.TokenProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.identifier != null) {
            return this.identifier;
        }
        if (!p.hasIdentifier()) {
            return null;
        }
        return this.identifier = this.convertFromProtoFormat(p.getIdentifier());
    }
    
    @Override
    public synchronized void setIdentifier(final ByteBuffer identifier) {
        this.maybeInitBuilder();
        if (identifier == null) {
            this.builder.clearIdentifier();
        }
        this.identifier = identifier;
    }
    
    @Override
    public synchronized ByteBuffer getPassword() {
        final SecurityProtos.TokenProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.password != null) {
            return this.password;
        }
        if (!p.hasPassword()) {
            return null;
        }
        return this.password = this.convertFromProtoFormat(p.getPassword());
    }
    
    @Override
    public synchronized void setPassword(final ByteBuffer password) {
        this.maybeInitBuilder();
        if (password == null) {
            this.builder.clearPassword();
        }
        this.password = password;
    }
    
    @Override
    public synchronized String getKind() {
        final SecurityProtos.TokenProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasKind()) {
            return null;
        }
        return p.getKind();
    }
    
    @Override
    public synchronized void setKind(final String kind) {
        this.maybeInitBuilder();
        if (kind == null) {
            this.builder.clearKind();
            return;
        }
        this.builder.setKind(kind);
    }
    
    @Override
    public synchronized String getService() {
        final SecurityProtos.TokenProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasService()) {
            return null;
        }
        return p.getService();
    }
    
    @Override
    public synchronized void setService(final String service) {
        this.maybeInitBuilder();
        if (service == null) {
            this.builder.clearService();
            return;
        }
        this.builder.setService(service);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Token { ");
        sb.append("kind: ").append(this.getKind()).append(", ");
        sb.append("service: ").append(this.getService()).append(" }");
        return sb.toString();
    }
}
