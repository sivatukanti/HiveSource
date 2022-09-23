// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.recovery.records;

import java.io.ByteArrayInputStream;
import org.apache.hadoop.yarn.security.client.RMDelegationTokenIdentifier;
import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.DataInput;
import org.apache.hadoop.yarn.security.client.YARNDelegationTokenIdentifier;
import org.apache.hadoop.yarn.proto.YarnServerResourceManagerRecoveryProtos;

public class RMDelegationTokenIdentifierData
{
    YarnServerResourceManagerRecoveryProtos.RMDelegationTokenIdentifierDataProto.Builder builder;
    
    public RMDelegationTokenIdentifierData() {
        this.builder = YarnServerResourceManagerRecoveryProtos.RMDelegationTokenIdentifierDataProto.newBuilder();
    }
    
    public RMDelegationTokenIdentifierData(final YARNDelegationTokenIdentifier identifier, final long renewdate) {
        (this.builder = YarnServerResourceManagerRecoveryProtos.RMDelegationTokenIdentifierDataProto.newBuilder()).setTokenIdentifier(identifier.getProto());
        this.builder.setRenewDate(renewdate);
    }
    
    public void readFields(final DataInput in) throws IOException {
        this.builder.mergeFrom((InputStream)in);
    }
    
    public byte[] toByteArray() throws IOException {
        return this.builder.build().toByteArray();
    }
    
    public RMDelegationTokenIdentifier getTokenIdentifier() throws IOException {
        final ByteArrayInputStream in = new ByteArrayInputStream(this.builder.getTokenIdentifier().toByteArray());
        final RMDelegationTokenIdentifier identifer = new RMDelegationTokenIdentifier();
        identifer.readFields(new DataInputStream(in));
        return identifer;
    }
    
    public long getRenewDate() {
        return this.builder.getRenewDate();
    }
}
