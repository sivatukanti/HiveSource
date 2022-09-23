// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records.impl.pb;

import com.google.common.base.Preconditions;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.proto.YarnProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.records.ContainerId;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class ContainerIdPBImpl extends ContainerId
{
    YarnProtos.ContainerIdProto proto;
    YarnProtos.ContainerIdProto.Builder builder;
    private ApplicationAttemptId applicationAttemptId;
    
    public ContainerIdPBImpl() {
        this.proto = null;
        this.builder = null;
        this.applicationAttemptId = null;
        this.builder = YarnProtos.ContainerIdProto.newBuilder();
    }
    
    public ContainerIdPBImpl(final YarnProtos.ContainerIdProto proto) {
        this.proto = null;
        this.builder = null;
        this.applicationAttemptId = null;
        this.proto = proto;
        this.applicationAttemptId = this.convertFromProtoFormat(proto.getAppAttemptId());
    }
    
    public YarnProtos.ContainerIdProto getProto() {
        return this.proto;
    }
    
    @Deprecated
    @Override
    public int getId() {
        Preconditions.checkNotNull(this.proto);
        return (int)this.proto.getId();
    }
    
    @Override
    public long getContainerId() {
        Preconditions.checkNotNull(this.proto);
        return this.proto.getId();
    }
    
    @Override
    protected void setContainerId(final long id) {
        Preconditions.checkNotNull(this.builder);
        this.builder.setId(id);
    }
    
    @Override
    public ApplicationAttemptId getApplicationAttemptId() {
        return this.applicationAttemptId;
    }
    
    @Override
    protected void setApplicationAttemptId(final ApplicationAttemptId atId) {
        if (atId != null) {
            Preconditions.checkNotNull(this.builder);
            this.builder.setAppAttemptId(this.convertToProtoFormat(atId));
        }
        this.applicationAttemptId = atId;
    }
    
    private ApplicationAttemptIdPBImpl convertFromProtoFormat(final YarnProtos.ApplicationAttemptIdProto p) {
        return new ApplicationAttemptIdPBImpl(p);
    }
    
    private YarnProtos.ApplicationAttemptIdProto convertToProtoFormat(final ApplicationAttemptId t) {
        return ((ApplicationAttemptIdPBImpl)t).getProto();
    }
    
    @Override
    protected void build() {
        this.proto = this.builder.build();
        this.builder = null;
    }
}
