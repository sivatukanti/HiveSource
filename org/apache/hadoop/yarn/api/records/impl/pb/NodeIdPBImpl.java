// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records.impl.pb;

import com.google.common.base.Preconditions;
import org.apache.hadoop.yarn.proto.YarnProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.records.NodeId;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class NodeIdPBImpl extends NodeId
{
    YarnProtos.NodeIdProto proto;
    YarnProtos.NodeIdProto.Builder builder;
    
    public NodeIdPBImpl() {
        this.proto = null;
        this.builder = null;
        this.builder = YarnProtos.NodeIdProto.newBuilder();
    }
    
    public NodeIdPBImpl(final YarnProtos.NodeIdProto proto) {
        this.proto = null;
        this.builder = null;
        this.proto = proto;
    }
    
    public YarnProtos.NodeIdProto getProto() {
        return this.proto;
    }
    
    @Override
    public String getHost() {
        Preconditions.checkNotNull(this.proto);
        return this.proto.getHost();
    }
    
    @Override
    protected void setHost(final String host) {
        Preconditions.checkNotNull(this.builder);
        this.builder.setHost(host);
    }
    
    @Override
    public int getPort() {
        Preconditions.checkNotNull(this.proto);
        return this.proto.getPort();
    }
    
    @Override
    protected void setPort(final int port) {
        Preconditions.checkNotNull(this.builder);
        this.builder.setPort(port);
    }
    
    @Override
    protected void build() {
        this.proto = this.builder.build();
        this.builder = null;
    }
}
