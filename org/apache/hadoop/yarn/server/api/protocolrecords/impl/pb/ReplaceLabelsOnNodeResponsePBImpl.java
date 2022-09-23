// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb;

import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.proto.YarnServerResourceManagerServiceProtos;
import org.apache.hadoop.yarn.server.api.protocolrecords.ReplaceLabelsOnNodeResponse;

public class ReplaceLabelsOnNodeResponsePBImpl extends ReplaceLabelsOnNodeResponse
{
    YarnServerResourceManagerServiceProtos.ReplaceLabelsOnNodeResponseProto proto;
    YarnServerResourceManagerServiceProtos.ReplaceLabelsOnNodeResponseProto.Builder builder;
    boolean viaProto;
    
    public ReplaceLabelsOnNodeResponsePBImpl() {
        this.proto = YarnServerResourceManagerServiceProtos.ReplaceLabelsOnNodeResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = YarnServerResourceManagerServiceProtos.ReplaceLabelsOnNodeResponseProto.newBuilder();
    }
    
    public ReplaceLabelsOnNodeResponsePBImpl(final YarnServerResourceManagerServiceProtos.ReplaceLabelsOnNodeResponseProto proto) {
        this.proto = YarnServerResourceManagerServiceProtos.ReplaceLabelsOnNodeResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnServerResourceManagerServiceProtos.ReplaceLabelsOnNodeResponseProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((ReplaceLabelsOnNodeResponsePBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
}
