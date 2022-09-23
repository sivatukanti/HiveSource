// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.security;

import org.apache.commons.logging.LogFactory;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.security.UserGroupInformation;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;
import org.apache.hadoop.yarn.api.records.impl.pb.NodeIdPBImpl;
import org.apache.hadoop.yarn.api.records.impl.pb.ApplicationAttemptIdPBImpl;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.proto.YarnSecurityTokenProtos;
import org.apache.hadoop.io.Text;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.security.token.TokenIdentifier;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class NMTokenIdentifier extends TokenIdentifier
{
    private static Log LOG;
    public static final Text KIND;
    private YarnSecurityTokenProtos.NMTokenIdentifierProto proto;
    
    public NMTokenIdentifier(final ApplicationAttemptId appAttemptId, final NodeId nodeId, final String applicationSubmitter, final int masterKeyId) {
        final YarnSecurityTokenProtos.NMTokenIdentifierProto.Builder builder = YarnSecurityTokenProtos.NMTokenIdentifierProto.newBuilder();
        if (appAttemptId != null) {
            builder.setAppAttemptId(((ApplicationAttemptIdPBImpl)appAttemptId).getProto());
        }
        if (nodeId != null) {
            builder.setNodeId(((NodeIdPBImpl)nodeId).getProto());
        }
        builder.setAppSubmitter(applicationSubmitter);
        builder.setKeyId(masterKeyId);
        this.proto = builder.build();
    }
    
    public NMTokenIdentifier() {
    }
    
    public ApplicationAttemptId getApplicationAttemptId() {
        if (!this.proto.hasAppAttemptId()) {
            return null;
        }
        return new ApplicationAttemptIdPBImpl(this.proto.getAppAttemptId());
    }
    
    public NodeId getNodeId() {
        if (!this.proto.hasNodeId()) {
            return null;
        }
        return new NodeIdPBImpl(this.proto.getNodeId());
    }
    
    public String getApplicationSubmitter() {
        return this.proto.getAppSubmitter();
    }
    
    public int getKeyId() {
        return this.proto.getKeyId();
    }
    
    @Override
    public void write(final DataOutput out) throws IOException {
        NMTokenIdentifier.LOG.debug("Writing NMTokenIdentifier to RPC layer: " + this);
        out.write(this.proto.toByteArray());
    }
    
    @Override
    public void readFields(final DataInput in) throws IOException {
        this.proto = YarnSecurityTokenProtos.NMTokenIdentifierProto.parseFrom((InputStream)in);
    }
    
    @Override
    public Text getKind() {
        return NMTokenIdentifier.KIND;
    }
    
    @Override
    public UserGroupInformation getUser() {
        String appAttemptId = null;
        if (this.proto.hasAppAttemptId()) {
            appAttemptId = new ApplicationAttemptIdPBImpl(this.proto.getAppAttemptId()).toString();
        }
        return UserGroupInformation.createRemoteUser(appAttemptId);
    }
    
    public YarnSecurityTokenProtos.NMTokenIdentifierProto getProto() {
        return this.proto;
    }
    
    @Override
    public int hashCode() {
        return this.getProto().hashCode();
    }
    
    @Override
    public boolean equals(final Object other) {
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((NMTokenIdentifier)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    static {
        NMTokenIdentifier.LOG = LogFactory.getLog(NMTokenIdentifier.class);
        KIND = new Text("NMToken");
    }
}
