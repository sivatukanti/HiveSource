// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records.impl.pb;

import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.proto.YarnProtos;
import org.apache.hadoop.yarn.api.records.LogAggregationContext;

public class LogAggregationContextPBImpl extends LogAggregationContext
{
    YarnProtos.LogAggregationContextProto proto;
    YarnProtos.LogAggregationContextProto.Builder builder;
    boolean viaProto;
    
    public LogAggregationContextPBImpl() {
        this.proto = YarnProtos.LogAggregationContextProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = YarnProtos.LogAggregationContextProto.newBuilder();
    }
    
    public LogAggregationContextPBImpl(final YarnProtos.LogAggregationContextProto proto) {
        this.proto = YarnProtos.LogAggregationContextProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnProtos.LogAggregationContextProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((LogAggregationContextPBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    private void mergeLocalToProto() {
        if (this.viaProto) {
            this.maybeInitBuilder();
        }
        this.proto = this.builder.build();
        this.viaProto = true;
    }
    
    private void maybeInitBuilder() {
        if (this.viaProto || this.builder == null) {
            this.builder = YarnProtos.LogAggregationContextProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public String getIncludePattern() {
        final YarnProtos.LogAggregationContextProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasIncludePattern()) {
            return null;
        }
        return p.getIncludePattern();
    }
    
    @Override
    public void setIncludePattern(final String includePattern) {
        this.maybeInitBuilder();
        if (includePattern == null) {
            this.builder.clearIncludePattern();
            return;
        }
        this.builder.setIncludePattern(includePattern);
    }
    
    @Override
    public String getExcludePattern() {
        final YarnProtos.LogAggregationContextProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasExcludePattern()) {
            return null;
        }
        return p.getExcludePattern();
    }
    
    @Override
    public void setExcludePattern(final String excludePattern) {
        this.maybeInitBuilder();
        if (excludePattern == null) {
            this.builder.clearExcludePattern();
            return;
        }
        this.builder.setExcludePattern(excludePattern);
    }
}
