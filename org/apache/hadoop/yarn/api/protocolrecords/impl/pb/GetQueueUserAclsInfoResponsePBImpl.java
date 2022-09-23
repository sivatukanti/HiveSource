// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords.impl.pb;

import org.apache.hadoop.yarn.api.records.impl.pb.QueueUserACLInfoPBImpl;
import java.util.Iterator;
import org.apache.hadoop.yarn.proto.YarnProtos;
import java.util.ArrayList;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.proto.YarnServiceProtos;
import org.apache.hadoop.yarn.api.records.QueueUserACLInfo;
import java.util.List;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.protocolrecords.GetQueueUserAclsInfoResponse;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class GetQueueUserAclsInfoResponsePBImpl extends GetQueueUserAclsInfoResponse
{
    List<QueueUserACLInfo> queueUserAclsInfoList;
    YarnServiceProtos.GetQueueUserAclsInfoResponseProto proto;
    YarnServiceProtos.GetQueueUserAclsInfoResponseProto.Builder builder;
    boolean viaProto;
    
    public GetQueueUserAclsInfoResponsePBImpl() {
        this.proto = YarnServiceProtos.GetQueueUserAclsInfoResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = YarnServiceProtos.GetQueueUserAclsInfoResponseProto.newBuilder();
    }
    
    public GetQueueUserAclsInfoResponsePBImpl(final YarnServiceProtos.GetQueueUserAclsInfoResponseProto proto) {
        this.proto = YarnServiceProtos.GetQueueUserAclsInfoResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    @Override
    public List<QueueUserACLInfo> getUserAclsInfoList() {
        this.initLocalQueueUserAclsList();
        return this.queueUserAclsInfoList;
    }
    
    @Override
    public void setUserAclsInfoList(final List<QueueUserACLInfo> queueUserAclsList) {
        if (queueUserAclsList == null) {
            this.builder.clearQueueUserAcls();
        }
        this.queueUserAclsInfoList = queueUserAclsList;
    }
    
    public YarnServiceProtos.GetQueueUserAclsInfoResponseProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((GetQueueUserAclsInfoResponsePBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    private void mergeLocalToBuilder() {
        if (this.queueUserAclsInfoList != null) {
            this.addLocalQueueUserACLInfosToProto();
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
            this.builder = YarnServiceProtos.GetQueueUserAclsInfoResponseProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    private void initLocalQueueUserAclsList() {
        if (this.queueUserAclsInfoList != null) {
            return;
        }
        final YarnServiceProtos.GetQueueUserAclsInfoResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<YarnProtos.QueueUserACLInfoProto> list = p.getQueueUserAclsList();
        this.queueUserAclsInfoList = new ArrayList<QueueUserACLInfo>();
        for (final YarnProtos.QueueUserACLInfoProto a : list) {
            this.queueUserAclsInfoList.add(this.convertFromProtoFormat(a));
        }
    }
    
    private void addLocalQueueUserACLInfosToProto() {
        this.maybeInitBuilder();
        this.builder.clearQueueUserAcls();
        if (this.queueUserAclsInfoList == null) {
            return;
        }
        final Iterable<YarnProtos.QueueUserACLInfoProto> iterable = new Iterable<YarnProtos.QueueUserACLInfoProto>() {
            @Override
            public Iterator<YarnProtos.QueueUserACLInfoProto> iterator() {
                return new Iterator<YarnProtos.QueueUserACLInfoProto>() {
                    Iterator<QueueUserACLInfo> iter = GetQueueUserAclsInfoResponsePBImpl.this.queueUserAclsInfoList.iterator();
                    
                    @Override
                    public boolean hasNext() {
                        return this.iter.hasNext();
                    }
                    
                    @Override
                    public YarnProtos.QueueUserACLInfoProto next() {
                        return GetQueueUserAclsInfoResponsePBImpl.this.convertToProtoFormat(this.iter.next());
                    }
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
        this.builder.addAllQueueUserAcls(iterable);
    }
    
    private QueueUserACLInfoPBImpl convertFromProtoFormat(final YarnProtos.QueueUserACLInfoProto p) {
        return new QueueUserACLInfoPBImpl(p);
    }
    
    private YarnProtos.QueueUserACLInfoProto convertToProtoFormat(final QueueUserACLInfo t) {
        return ((QueueUserACLInfoPBImpl)t).getProto();
    }
}
