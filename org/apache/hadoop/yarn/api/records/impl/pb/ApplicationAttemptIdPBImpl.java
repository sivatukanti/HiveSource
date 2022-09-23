// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records.impl.pb;

import com.google.common.base.Preconditions;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.proto.YarnProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class ApplicationAttemptIdPBImpl extends ApplicationAttemptId
{
    YarnProtos.ApplicationAttemptIdProto proto;
    YarnProtos.ApplicationAttemptIdProto.Builder builder;
    private ApplicationId applicationId;
    
    public ApplicationAttemptIdPBImpl() {
        this.proto = null;
        this.builder = null;
        this.applicationId = null;
        this.builder = YarnProtos.ApplicationAttemptIdProto.newBuilder();
    }
    
    public ApplicationAttemptIdPBImpl(final YarnProtos.ApplicationAttemptIdProto proto) {
        this.proto = null;
        this.builder = null;
        this.applicationId = null;
        this.proto = proto;
        this.applicationId = this.convertFromProtoFormat(proto.getApplicationId());
    }
    
    public YarnProtos.ApplicationAttemptIdProto getProto() {
        return this.proto;
    }
    
    @Override
    public int getAttemptId() {
        Preconditions.checkNotNull(this.proto);
        return this.proto.getAttemptId();
    }
    
    @Override
    protected void setAttemptId(final int attemptId) {
        Preconditions.checkNotNull(this.builder);
        this.builder.setAttemptId(attemptId);
    }
    
    @Override
    public ApplicationId getApplicationId() {
        return this.applicationId;
    }
    
    public void setApplicationId(final ApplicationId appId) {
        if (appId != null) {
            Preconditions.checkNotNull(this.builder);
            this.builder.setApplicationId(this.convertToProtoFormat(appId));
        }
        this.applicationId = appId;
    }
    
    private ApplicationIdPBImpl convertFromProtoFormat(final YarnProtos.ApplicationIdProto p) {
        return new ApplicationIdPBImpl(p);
    }
    
    private YarnProtos.ApplicationIdProto convertToProtoFormat(final ApplicationId t) {
        return ((ApplicationIdPBImpl)t).getProto();
    }
    
    @Override
    protected void build() {
        this.proto = this.builder.build();
        this.builder = null;
    }
}
