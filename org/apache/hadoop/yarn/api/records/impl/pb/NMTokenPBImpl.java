// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records.impl.pb;

import org.apache.hadoop.security.proto.SecurityProtos;
import org.apache.hadoop.yarn.proto.YarnProtos;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.Token;
import org.apache.hadoop.yarn.proto.YarnServiceProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.records.NMToken;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class NMTokenPBImpl extends NMToken
{
    YarnServiceProtos.NMTokenProto proto;
    YarnServiceProtos.NMTokenProto.Builder builder;
    boolean viaProto;
    private Token token;
    private NodeId nodeId;
    
    public NMTokenPBImpl() {
        this.proto = YarnServiceProtos.NMTokenProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.token = null;
        this.nodeId = null;
        this.builder = YarnServiceProtos.NMTokenProto.newBuilder();
    }
    
    public NMTokenPBImpl(final YarnServiceProtos.NMTokenProto proto) {
        this.proto = YarnServiceProtos.NMTokenProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.token = null;
        this.nodeId = null;
        this.proto = proto;
        this.viaProto = true;
    }
    
    @Override
    public synchronized NodeId getNodeId() {
        final YarnServiceProtos.NMTokenProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.nodeId != null) {
            return this.nodeId;
        }
        if (!p.hasNodeId()) {
            return null;
        }
        return this.nodeId = this.convertFromProtoFormat(p.getNodeId());
    }
    
    @Override
    public synchronized void setNodeId(final NodeId nodeId) {
        this.maybeInitBuilder();
        if (nodeId == null) {
            this.builder.clearNodeId();
        }
        this.nodeId = nodeId;
    }
    
    @Override
    public synchronized Token getToken() {
        final YarnServiceProtos.NMTokenProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.token != null) {
            return this.token;
        }
        if (!p.hasToken()) {
            return null;
        }
        return this.token = this.convertFromProtoFormat(p.getToken());
    }
    
    @Override
    public synchronized void setToken(final Token token) {
        this.maybeInitBuilder();
        if (token == null) {
            this.builder.clearToken();
        }
        this.token = token;
    }
    
    public synchronized YarnServiceProtos.NMTokenProto getProto() {
        this.mergeLocalToProto();
        this.proto = (this.viaProto ? this.proto : this.builder.build());
        this.viaProto = true;
        return this.proto;
    }
    
    private synchronized void mergeLocalToProto() {
        if (this.viaProto) {
            this.maybeInitBuilder();
        }
        this.mergeLocalToBuilder();
        this.proto = this.builder.build();
        this.viaProto = true;
    }
    
    private synchronized void mergeLocalToBuilder() {
        if (this.nodeId != null) {
            this.builder.setNodeId(this.convertToProtoFormat(this.nodeId));
        }
        if (this.token != null) {
            this.builder.setToken(this.convertToProtoFormat(this.token));
        }
    }
    
    private synchronized void maybeInitBuilder() {
        if (this.viaProto || this.builder == null) {
            this.builder = YarnServiceProtos.NMTokenProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    private synchronized NodeId convertFromProtoFormat(final YarnProtos.NodeIdProto p) {
        return new NodeIdPBImpl(p);
    }
    
    private synchronized YarnProtos.NodeIdProto convertToProtoFormat(final NodeId nodeId) {
        return ((NodeIdPBImpl)nodeId).getProto();
    }
    
    private synchronized SecurityProtos.TokenProto convertToProtoFormat(final Token token) {
        return ((TokenPBImpl)token).getProto();
    }
    
    private synchronized Token convertFromProtoFormat(final SecurityProtos.TokenProto proto) {
        return new TokenPBImpl(proto);
    }
}
