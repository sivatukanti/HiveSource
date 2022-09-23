// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb;

import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.proto.YarnServerResourceManagerServiceProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshAdminAclsResponse;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class RefreshAdminAclsResponsePBImpl extends RefreshAdminAclsResponse
{
    YarnServerResourceManagerServiceProtos.RefreshAdminAclsResponseProto proto;
    YarnServerResourceManagerServiceProtos.RefreshAdminAclsResponseProto.Builder builder;
    boolean viaProto;
    
    public RefreshAdminAclsResponsePBImpl() {
        this.proto = YarnServerResourceManagerServiceProtos.RefreshAdminAclsResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = YarnServerResourceManagerServiceProtos.RefreshAdminAclsResponseProto.newBuilder();
    }
    
    public RefreshAdminAclsResponsePBImpl(final YarnServerResourceManagerServiceProtos.RefreshAdminAclsResponseProto proto) {
        this.proto = YarnServerResourceManagerServiceProtos.RefreshAdminAclsResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnServerResourceManagerServiceProtos.RefreshAdminAclsResponseProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((RefreshAdminAclsResponsePBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
}
