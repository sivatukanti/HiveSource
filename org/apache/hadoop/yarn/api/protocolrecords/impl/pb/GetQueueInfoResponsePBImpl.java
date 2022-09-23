// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords.impl.pb;

import org.apache.hadoop.yarn.api.records.impl.pb.QueueInfoPBImpl;
import org.apache.hadoop.yarn.proto.YarnProtos;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.proto.YarnServiceProtos;
import org.apache.hadoop.yarn.api.records.QueueInfo;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.protocolrecords.GetQueueInfoResponse;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class GetQueueInfoResponsePBImpl extends GetQueueInfoResponse
{
    QueueInfo queueInfo;
    YarnServiceProtos.GetQueueInfoResponseProto proto;
    YarnServiceProtos.GetQueueInfoResponseProto.Builder builder;
    boolean viaProto;
    
    public GetQueueInfoResponsePBImpl() {
        this.proto = YarnServiceProtos.GetQueueInfoResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = YarnServiceProtos.GetQueueInfoResponseProto.newBuilder();
    }
    
    public GetQueueInfoResponsePBImpl(final YarnServiceProtos.GetQueueInfoResponseProto proto) {
        this.proto = YarnServiceProtos.GetQueueInfoResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnServiceProtos.GetQueueInfoResponseProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((GetQueueInfoResponsePBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    @Override
    public QueueInfo getQueueInfo() {
        if (this.queueInfo != null) {
            return this.queueInfo;
        }
        final YarnServiceProtos.GetQueueInfoResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasQueueInfo()) {
            return null;
        }
        return this.queueInfo = this.convertFromProtoFormat(p.getQueueInfo());
    }
    
    @Override
    public void setQueueInfo(final QueueInfo queueInfo) {
        this.maybeInitBuilder();
        if (queueInfo == null) {
            this.builder.clearQueueInfo();
        }
        this.queueInfo = queueInfo;
    }
    
    private void mergeLocalToBuilder() {
        if (this.queueInfo != null) {
            this.builder.setQueueInfo(this.convertToProtoFormat(this.queueInfo));
        }
    }
    
    private void mergeLocalToProto() {
        if (this.viaProto) {
            this.maybeInitBuilder();
        }
        this.mergeLocalToBuilder();
        this.proto = this.builder.build();
        this.viaProto = true;
    }
    
    private void maybeInitBuilder() {
        if (this.viaProto || this.builder == null) {
            this.builder = YarnServiceProtos.GetQueueInfoResponseProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    private QueueInfo convertFromProtoFormat(final YarnProtos.QueueInfoProto queueInfo) {
        return new QueueInfoPBImpl(queueInfo);
    }
    
    private YarnProtos.QueueInfoProto convertToProtoFormat(final QueueInfo queueInfo) {
        return ((QueueInfoPBImpl)queueInfo).getProto();
    }
}
