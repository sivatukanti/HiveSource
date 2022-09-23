// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.token.delegation;

import com.google.common.annotations.VisibleForTesting;
import java.io.DataOutput;
import org.apache.hadoop.io.WritableUtils;
import java.io.DataInput;
import java.io.IOException;
import org.apache.hadoop.security.HadoopKerberosName;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.security.token.TokenIdentifier;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public abstract class AbstractDelegationTokenIdentifier extends TokenIdentifier
{
    private static final byte VERSION = 0;
    private Text owner;
    private Text renewer;
    private Text realUser;
    private long issueDate;
    private long maxDate;
    private int sequenceNumber;
    private int masterKeyId;
    
    public AbstractDelegationTokenIdentifier() {
        this(new Text(), new Text(), new Text());
    }
    
    public AbstractDelegationTokenIdentifier(final Text owner, final Text renewer, final Text realUser) {
        this.masterKeyId = 0;
        this.setOwner(owner);
        this.setRenewer(renewer);
        this.setRealUser(realUser);
        this.issueDate = 0L;
        this.maxDate = 0L;
    }
    
    @Override
    public abstract Text getKind();
    
    @Override
    public UserGroupInformation getUser() {
        if (this.owner == null || this.owner.toString().isEmpty()) {
            return null;
        }
        UserGroupInformation ugi;
        UserGroupInformation realUgi;
        if (this.realUser == null || this.realUser.toString().isEmpty() || this.realUser.equals(this.owner)) {
            realUgi = (ugi = UserGroupInformation.createRemoteUser(this.owner.toString()));
        }
        else {
            realUgi = UserGroupInformation.createRemoteUser(this.realUser.toString());
            ugi = UserGroupInformation.createProxyUser(this.owner.toString(), realUgi);
        }
        realUgi.setAuthenticationMethod(UserGroupInformation.AuthenticationMethod.TOKEN);
        return ugi;
    }
    
    public Text getOwner() {
        return this.owner;
    }
    
    public void setOwner(final Text owner) {
        if (owner == null) {
            this.owner = new Text();
        }
        else {
            this.owner = owner;
        }
    }
    
    public Text getRenewer() {
        return this.renewer;
    }
    
    public void setRenewer(final Text renewer) {
        if (renewer == null) {
            this.renewer = new Text();
        }
        else {
            final HadoopKerberosName renewerKrbName = new HadoopKerberosName(renewer.toString());
            try {
                this.renewer = new Text(renewerKrbName.getShortName());
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    public Text getRealUser() {
        return this.realUser;
    }
    
    public void setRealUser(final Text realUser) {
        if (realUser == null) {
            this.realUser = new Text();
        }
        else {
            this.realUser = realUser;
        }
    }
    
    public void setIssueDate(final long issueDate) {
        this.issueDate = issueDate;
    }
    
    public long getIssueDate() {
        return this.issueDate;
    }
    
    public void setMaxDate(final long maxDate) {
        this.maxDate = maxDate;
    }
    
    public long getMaxDate() {
        return this.maxDate;
    }
    
    public void setSequenceNumber(final int seqNum) {
        this.sequenceNumber = seqNum;
    }
    
    public int getSequenceNumber() {
        return this.sequenceNumber;
    }
    
    public void setMasterKeyId(final int newId) {
        this.masterKeyId = newId;
    }
    
    public int getMasterKeyId() {
        return this.masterKeyId;
    }
    
    protected static boolean isEqual(final Object a, final Object b) {
        return (a == null) ? (b == null) : a.equals(b);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof AbstractDelegationTokenIdentifier) {
            final AbstractDelegationTokenIdentifier that = (AbstractDelegationTokenIdentifier)obj;
            return this.sequenceNumber == that.sequenceNumber && this.issueDate == that.issueDate && this.maxDate == that.maxDate && this.masterKeyId == that.masterKeyId && isEqual(this.owner, that.owner) && isEqual(this.renewer, that.renewer) && isEqual(this.realUser, that.realUser);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.sequenceNumber;
    }
    
    @Override
    public void readFields(final DataInput in) throws IOException {
        final byte version = in.readByte();
        if (version != 0) {
            throw new IOException("Unknown version of delegation token " + version);
        }
        this.owner.readFields(in, 1048576);
        this.renewer.readFields(in, 1048576);
        this.realUser.readFields(in, 1048576);
        this.issueDate = WritableUtils.readVLong(in);
        this.maxDate = WritableUtils.readVLong(in);
        this.sequenceNumber = WritableUtils.readVInt(in);
        this.masterKeyId = WritableUtils.readVInt(in);
    }
    
    @VisibleForTesting
    void writeImpl(final DataOutput out) throws IOException {
        out.writeByte(0);
        this.owner.write(out);
        this.renewer.write(out);
        this.realUser.write(out);
        WritableUtils.writeVLong(out, this.issueDate);
        WritableUtils.writeVLong(out, this.maxDate);
        WritableUtils.writeVInt(out, this.sequenceNumber);
        WritableUtils.writeVInt(out, this.masterKeyId);
    }
    
    @Override
    public void write(final DataOutput out) throws IOException {
        if (this.owner.getLength() > 1048576) {
            throw new IOException("owner is too long to be serialized!");
        }
        if (this.renewer.getLength() > 1048576) {
            throw new IOException("renewer is too long to be serialized!");
        }
        if (this.realUser.getLength() > 1048576) {
            throw new IOException("realuser is too long to be serialized!");
        }
        this.writeImpl(out);
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(this.getKind()).append(" owner=").append(this.owner).append(", renewer=").append(this.renewer).append(", realUser=").append(this.realUser).append(", issueDate=").append(this.issueDate).append(", maxDate=").append(this.maxDate).append(", sequenceNumber=").append(this.sequenceNumber).append(", masterKeyId=").append(this.masterKeyId);
        return buffer.toString();
    }
    
    public String toStringStable() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("owner=").append(this.owner).append(", renewer=").append(this.renewer).append(", realUser=").append(this.realUser).append(", issueDate=").append(this.issueDate).append(", maxDate=").append(this.maxDate).append(", sequenceNumber=").append(this.sequenceNumber).append(", masterKeyId=").append(this.masterKeyId);
        return buffer.toString();
    }
}
