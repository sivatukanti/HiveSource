// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.security;

import org.apache.hadoop.security.token.Token;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.security.UserGroupInformation;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;
import org.apache.hadoop.yarn.api.records.impl.pb.ApplicationAttemptIdPBImpl;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.proto.YarnSecurityTokenProtos;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.security.token.TokenIdentifier;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class AMRMTokenIdentifier extends TokenIdentifier
{
    public static final Text KIND_NAME;
    private YarnSecurityTokenProtos.AMRMTokenIdentifierProto proto;
    
    public AMRMTokenIdentifier() {
    }
    
    public AMRMTokenIdentifier(final ApplicationAttemptId appAttemptId, final int masterKeyId) {
        final YarnSecurityTokenProtos.AMRMTokenIdentifierProto.Builder builder = YarnSecurityTokenProtos.AMRMTokenIdentifierProto.newBuilder();
        if (appAttemptId != null) {
            builder.setAppAttemptId(((ApplicationAttemptIdPBImpl)appAttemptId).getProto());
        }
        builder.setKeyId(masterKeyId);
        this.proto = builder.build();
    }
    
    @InterfaceAudience.Private
    public ApplicationAttemptId getApplicationAttemptId() {
        if (!this.proto.hasAppAttemptId()) {
            return null;
        }
        return new ApplicationAttemptIdPBImpl(this.proto.getAppAttemptId());
    }
    
    @Override
    public void write(final DataOutput out) throws IOException {
        out.write(this.proto.toByteArray());
    }
    
    @Override
    public void readFields(final DataInput in) throws IOException {
        this.proto = YarnSecurityTokenProtos.AMRMTokenIdentifierProto.parseFrom((InputStream)in);
    }
    
    @Override
    public Text getKind() {
        return AMRMTokenIdentifier.KIND_NAME;
    }
    
    @Override
    public UserGroupInformation getUser() {
        String appAttemptId = null;
        if (this.proto.hasAppAttemptId()) {
            appAttemptId = new ApplicationAttemptIdPBImpl(this.proto.getAppAttemptId()).toString();
        }
        return UserGroupInformation.createRemoteUser(appAttemptId);
    }
    
    public int getKeyId() {
        return this.proto.getKeyId();
    }
    
    public YarnSecurityTokenProtos.AMRMTokenIdentifierProto getProto() {
        return this.proto;
    }
    
    @Override
    public int hashCode() {
        return this.getProto().hashCode();
    }
    
    @Override
    public boolean equals(final Object other) {
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((AMRMTokenIdentifier)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    static {
        KIND_NAME = new Text("YARN_AM_RM_TOKEN");
    }
    
    @InterfaceAudience.Private
    public static class Renewer extends Token.TrivialRenewer
    {
        @Override
        protected Text getKind() {
            return AMRMTokenIdentifier.KIND_NAME;
        }
    }
}
