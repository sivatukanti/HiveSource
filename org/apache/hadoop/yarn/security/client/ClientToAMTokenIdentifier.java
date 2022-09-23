// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.security.client;

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
public class ClientToAMTokenIdentifier extends TokenIdentifier
{
    public static final Text KIND_NAME;
    private YarnSecurityTokenProtos.ClientToAMTokenIdentifierProto proto;
    
    public ClientToAMTokenIdentifier() {
    }
    
    public ClientToAMTokenIdentifier(final ApplicationAttemptId id, final String client) {
        final YarnSecurityTokenProtos.ClientToAMTokenIdentifierProto.Builder builder = YarnSecurityTokenProtos.ClientToAMTokenIdentifierProto.newBuilder();
        if (id != null) {
            builder.setAppAttemptId(((ApplicationAttemptIdPBImpl)id).getProto());
        }
        if (client != null) {
            builder.setClientName(client);
        }
        this.proto = builder.build();
    }
    
    public ApplicationAttemptId getApplicationAttemptID() {
        if (!this.proto.hasAppAttemptId()) {
            return null;
        }
        return new ApplicationAttemptIdPBImpl(this.proto.getAppAttemptId());
    }
    
    public String getClientName() {
        return this.proto.getClientName();
    }
    
    public YarnSecurityTokenProtos.ClientToAMTokenIdentifierProto getProto() {
        return this.proto;
    }
    
    @Override
    public void write(final DataOutput out) throws IOException {
        out.write(this.proto.toByteArray());
    }
    
    @Override
    public void readFields(final DataInput in) throws IOException {
        this.proto = YarnSecurityTokenProtos.ClientToAMTokenIdentifierProto.parseFrom((InputStream)in);
    }
    
    @Override
    public Text getKind() {
        return ClientToAMTokenIdentifier.KIND_NAME;
    }
    
    @Override
    public UserGroupInformation getUser() {
        final String clientName = this.getClientName();
        if (clientName == null) {
            return null;
        }
        return UserGroupInformation.createRemoteUser(clientName);
    }
    
    @Override
    public int hashCode() {
        return this.getProto().hashCode();
    }
    
    @Override
    public boolean equals(final Object other) {
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((ClientToAMTokenIdentifier)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    static {
        KIND_NAME = new Text("YARN_CLIENT_TOKEN");
    }
}
