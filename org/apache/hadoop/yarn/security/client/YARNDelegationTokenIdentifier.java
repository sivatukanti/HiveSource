// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.security.client;

import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.DataInput;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.yarn.proto.YarnSecurityTokenProtos;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.security.token.delegation.AbstractDelegationTokenIdentifier;

@InterfaceAudience.Private
public abstract class YARNDelegationTokenIdentifier extends AbstractDelegationTokenIdentifier
{
    YarnSecurityTokenProtos.YARNDelegationTokenIdentifierProto.Builder builder;
    
    public YARNDelegationTokenIdentifier() {
        this.builder = YarnSecurityTokenProtos.YARNDelegationTokenIdentifierProto.newBuilder();
    }
    
    public YARNDelegationTokenIdentifier(final Text owner, final Text renewer, final Text realUser) {
        super(owner, renewer, realUser);
        this.builder = YarnSecurityTokenProtos.YARNDelegationTokenIdentifierProto.newBuilder();
    }
    
    public YARNDelegationTokenIdentifier(final YarnSecurityTokenProtos.YARNDelegationTokenIdentifierProto.Builder builder) {
        this.builder = YarnSecurityTokenProtos.YARNDelegationTokenIdentifierProto.newBuilder();
        this.builder = builder;
    }
    
    @Override
    public synchronized void readFields(final DataInput in) throws IOException {
        this.builder.mergeFrom((InputStream)in);
        if (this.builder.getOwner() != null) {
            this.setOwner(new Text(this.builder.getOwner()));
        }
        if (this.builder.getRenewer() != null) {
            this.setRenewer(new Text(this.builder.getRenewer()));
        }
        if (this.builder.getRealUser() != null) {
            this.setRealUser(new Text(this.builder.getRealUser()));
        }
        this.setIssueDate(this.builder.getIssueDate());
        this.setMaxDate(this.builder.getMaxDate());
        this.setSequenceNumber(this.builder.getSequenceNumber());
        this.setMasterKeyId(this.builder.getMasterKeyId());
    }
    
    @Override
    public synchronized void write(final DataOutput out) throws IOException {
        this.builder.setOwner(this.getOwner().toString());
        this.builder.setRenewer(this.getRenewer().toString());
        this.builder.setRealUser(this.getRealUser().toString());
        this.builder.setIssueDate(this.getIssueDate());
        this.builder.setMaxDate(this.getMaxDate());
        this.builder.setSequenceNumber(this.getSequenceNumber());
        this.builder.setMasterKeyId(this.getMasterKeyId());
        this.builder.build().writeTo((OutputStream)out);
    }
    
    public YarnSecurityTokenProtos.YARNDelegationTokenIdentifierProto getProto() {
        return this.builder.build();
    }
}
