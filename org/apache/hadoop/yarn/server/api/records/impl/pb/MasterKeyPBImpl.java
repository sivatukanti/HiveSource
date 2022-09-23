// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.api.records.impl.pb;

import com.google.protobuf.Message;
import java.nio.ByteBuffer;
import org.apache.hadoop.yarn.server.api.records.MasterKey;
import org.apache.hadoop.yarn.proto.YarnServerCommonProtos;
import org.apache.hadoop.yarn.api.records.impl.pb.ProtoBase;

public class MasterKeyPBImpl extends ProtoBase<YarnServerCommonProtos.MasterKeyProto> implements MasterKey
{
    YarnServerCommonProtos.MasterKeyProto proto;
    YarnServerCommonProtos.MasterKeyProto.Builder builder;
    boolean viaProto;
    
    public MasterKeyPBImpl() {
        this.proto = YarnServerCommonProtos.MasterKeyProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = YarnServerCommonProtos.MasterKeyProto.newBuilder();
    }
    
    public MasterKeyPBImpl(final YarnServerCommonProtos.MasterKeyProto proto) {
        this.proto = YarnServerCommonProtos.MasterKeyProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    @Override
    public synchronized YarnServerCommonProtos.MasterKeyProto getProto() {
        this.proto = (this.viaProto ? this.proto : this.builder.build());
        this.viaProto = true;
        return this.proto;
    }
    
    private synchronized void maybeInitBuilder() {
        if (this.viaProto || this.builder == null) {
            this.builder = YarnServerCommonProtos.MasterKeyProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public synchronized int getKeyId() {
        final YarnServerCommonProtos.MasterKeyProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getKeyId();
    }
    
    @Override
    public synchronized void setKeyId(final int id) {
        this.maybeInitBuilder();
        this.builder.setKeyId(id);
    }
    
    @Override
    public synchronized ByteBuffer getBytes() {
        final YarnServerCommonProtos.MasterKeyProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return this.convertFromProtoFormat(p.getBytes());
    }
    
    @Override
    public synchronized void setBytes(final ByteBuffer bytes) {
        this.maybeInitBuilder();
        this.builder.setBytes(this.convertToProtoFormat(bytes));
    }
    
    @Override
    public int hashCode() {
        return this.getKeyId();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof MasterKey)) {
            return false;
        }
        final MasterKey other = (MasterKey)obj;
        return this.getKeyId() == other.getKeyId() && this.getBytes().equals(other.getBytes());
    }
}
