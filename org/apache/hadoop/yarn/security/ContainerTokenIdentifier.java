// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.security;

import org.apache.hadoop.security.token.Token;
import org.apache.commons.logging.LogFactory;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.security.UserGroupInformation;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;
import org.apache.hadoop.yarn.api.records.impl.pb.LogAggregationContextPBImpl;
import org.apache.hadoop.yarn.api.records.impl.pb.PriorityPBImpl;
import org.apache.hadoop.yarn.api.records.impl.pb.ResourcePBImpl;
import org.apache.hadoop.yarn.api.records.impl.pb.ContainerIdPBImpl;
import org.apache.hadoop.yarn.api.records.LogAggregationContext;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.proto.YarnSecurityTokenProtos;
import org.apache.hadoop.io.Text;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.security.token.TokenIdentifier;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class ContainerTokenIdentifier extends TokenIdentifier
{
    private static Log LOG;
    public static final Text KIND;
    private YarnSecurityTokenProtos.ContainerTokenIdentifierProto proto;
    
    public ContainerTokenIdentifier(final ContainerId containerID, final String hostName, final String appSubmitter, final Resource r, final long expiryTimeStamp, final int masterKeyId, final long rmIdentifier, final Priority priority, final long creationTime) {
        this(containerID, hostName, appSubmitter, r, expiryTimeStamp, masterKeyId, rmIdentifier, priority, creationTime, null);
    }
    
    public ContainerTokenIdentifier(final ContainerId containerID, final String hostName, final String appSubmitter, final Resource r, final long expiryTimeStamp, final int masterKeyId, final long rmIdentifier, final Priority priority, final long creationTime, final LogAggregationContext logAggregationContext) {
        final YarnSecurityTokenProtos.ContainerTokenIdentifierProto.Builder builder = YarnSecurityTokenProtos.ContainerTokenIdentifierProto.newBuilder();
        if (containerID != null) {
            builder.setContainerId(((ContainerIdPBImpl)containerID).getProto());
        }
        builder.setNmHostAddr(hostName);
        builder.setAppSubmitter(appSubmitter);
        if (r != null) {
            builder.setResource(((ResourcePBImpl)r).getProto());
        }
        builder.setExpiryTimeStamp(expiryTimeStamp);
        builder.setMasterKeyId(masterKeyId);
        builder.setRmIdentifier(rmIdentifier);
        if (priority != null) {
            builder.setPriority(((PriorityPBImpl)priority).getProto());
        }
        builder.setCreationTime(creationTime);
        if (logAggregationContext != null) {
            builder.setLogAggregationContext(((LogAggregationContextPBImpl)logAggregationContext).getProto());
        }
        this.proto = builder.build();
    }
    
    public ContainerTokenIdentifier() {
    }
    
    public ContainerId getContainerID() {
        if (!this.proto.hasContainerId()) {
            return null;
        }
        return new ContainerIdPBImpl(this.proto.getContainerId());
    }
    
    public String getApplicationSubmitter() {
        return this.proto.getAppSubmitter();
    }
    
    public String getNmHostAddress() {
        return this.proto.getNmHostAddr();
    }
    
    public Resource getResource() {
        if (!this.proto.hasResource()) {
            return null;
        }
        return new ResourcePBImpl(this.proto.getResource());
    }
    
    public long getExpiryTimeStamp() {
        return this.proto.getExpiryTimeStamp();
    }
    
    public int getMasterKeyId() {
        return this.proto.getMasterKeyId();
    }
    
    public Priority getPriority() {
        if (!this.proto.hasPriority()) {
            return null;
        }
        return new PriorityPBImpl(this.proto.getPriority());
    }
    
    public long getCreationTime() {
        return this.proto.getCreationTime();
    }
    
    public long getRMIdentifier() {
        return this.proto.getRmIdentifier();
    }
    
    public YarnSecurityTokenProtos.ContainerTokenIdentifierProto getProto() {
        return this.proto;
    }
    
    public LogAggregationContext getLogAggregationContext() {
        if (!this.proto.hasLogAggregationContext()) {
            return null;
        }
        return new LogAggregationContextPBImpl(this.proto.getLogAggregationContext());
    }
    
    @Override
    public void write(final DataOutput out) throws IOException {
        ContainerTokenIdentifier.LOG.debug("Writing ContainerTokenIdentifier to RPC layer: " + this);
        out.write(this.proto.toByteArray());
    }
    
    @Override
    public void readFields(final DataInput in) throws IOException {
        this.proto = YarnSecurityTokenProtos.ContainerTokenIdentifierProto.parseFrom((InputStream)in);
    }
    
    @Override
    public Text getKind() {
        return ContainerTokenIdentifier.KIND;
    }
    
    @Override
    public UserGroupInformation getUser() {
        String containerId = null;
        if (this.proto.hasContainerId()) {
            containerId = new ContainerIdPBImpl(this.proto.getContainerId()).toString();
        }
        return UserGroupInformation.createRemoteUser(containerId);
    }
    
    @Override
    public int hashCode() {
        return this.getProto().hashCode();
    }
    
    @Override
    public boolean equals(final Object other) {
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((ContainerTokenIdentifier)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    static {
        ContainerTokenIdentifier.LOG = LogFactory.getLog(ContainerTokenIdentifier.class);
        KIND = new Text("ContainerToken");
    }
    
    @InterfaceAudience.Private
    public static class Renewer extends Token.TrivialRenewer
    {
        @Override
        protected Text getKind() {
            return ContainerTokenIdentifier.KIND;
        }
    }
}
