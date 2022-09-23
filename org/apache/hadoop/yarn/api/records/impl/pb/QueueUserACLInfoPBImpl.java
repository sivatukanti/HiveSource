// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records.impl.pb;

import java.util.Iterator;
import java.util.ArrayList;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.api.records.QueueACL;
import java.util.List;
import org.apache.hadoop.yarn.proto.YarnProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.records.QueueUserACLInfo;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class QueueUserACLInfoPBImpl extends QueueUserACLInfo
{
    YarnProtos.QueueUserACLInfoProto proto;
    YarnProtos.QueueUserACLInfoProto.Builder builder;
    boolean viaProto;
    List<QueueACL> userAclsList;
    
    public QueueUserACLInfoPBImpl() {
        this.proto = YarnProtos.QueueUserACLInfoProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = YarnProtos.QueueUserACLInfoProto.newBuilder();
    }
    
    public QueueUserACLInfoPBImpl(final YarnProtos.QueueUserACLInfoProto proto) {
        this.proto = YarnProtos.QueueUserACLInfoProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    @Override
    public String getQueueName() {
        final YarnProtos.QueueUserACLInfoProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.hasQueueName() ? p.getQueueName() : null;
    }
    
    @Override
    public List<QueueACL> getUserAcls() {
        this.initLocalQueueUserAclsList();
        return this.userAclsList;
    }
    
    @Override
    public void setQueueName(final String queueName) {
        this.maybeInitBuilder();
        if (queueName == null) {
            this.builder.clearQueueName();
            return;
        }
        this.builder.setQueueName(queueName);
    }
    
    @Override
    public void setUserAcls(final List<QueueACL> userAclsList) {
        if (userAclsList == null) {
            this.builder.clearUserAcls();
        }
        this.userAclsList = userAclsList;
    }
    
    public YarnProtos.QueueUserACLInfoProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((QueueUserACLInfoPBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    private void initLocalQueueUserAclsList() {
        if (this.userAclsList != null) {
            return;
        }
        final YarnProtos.QueueUserACLInfoProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<YarnProtos.QueueACLProto> list = p.getUserAclsList();
        this.userAclsList = new ArrayList<QueueACL>();
        for (final YarnProtos.QueueACLProto a : list) {
            this.userAclsList.add(this.convertFromProtoFormat(a));
        }
    }
    
    private void addQueueACLsToProto() {
        this.maybeInitBuilder();
        this.builder.clearUserAcls();
        if (this.userAclsList == null) {
            return;
        }
        final Iterable<YarnProtos.QueueACLProto> iterable = new Iterable<YarnProtos.QueueACLProto>() {
            @Override
            public Iterator<YarnProtos.QueueACLProto> iterator() {
                return new Iterator<YarnProtos.QueueACLProto>() {
                    Iterator<QueueACL> iter = QueueUserACLInfoPBImpl.this.userAclsList.iterator();
                    
                    @Override
                    public boolean hasNext() {
                        return this.iter.hasNext();
                    }
                    
                    @Override
                    public YarnProtos.QueueACLProto next() {
                        return QueueUserACLInfoPBImpl.this.convertToProtoFormat(this.iter.next());
                    }
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
        this.builder.addAllUserAcls(iterable);
    }
    
    private void maybeInitBuilder() {
        if (this.viaProto || this.builder == null) {
            this.builder = YarnProtos.QueueUserACLInfoProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    private void mergeLocalToBuilder() {
        if (this.userAclsList != null) {
            this.addQueueACLsToProto();
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
    
    private QueueACL convertFromProtoFormat(final YarnProtos.QueueACLProto q) {
        return ProtoUtils.convertFromProtoFormat(q);
    }
    
    private YarnProtos.QueueACLProto convertToProtoFormat(final QueueACL queueAcl) {
        return ProtoUtils.convertToProtoFormat(queueAcl);
    }
}
