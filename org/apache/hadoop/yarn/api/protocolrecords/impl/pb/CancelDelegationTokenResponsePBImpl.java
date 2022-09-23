// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords.impl.pb;

import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.security.proto.SecurityProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.protocolrecords.CancelDelegationTokenResponse;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class CancelDelegationTokenResponsePBImpl extends CancelDelegationTokenResponse
{
    SecurityProtos.CancelDelegationTokenResponseProto proto;
    
    public CancelDelegationTokenResponsePBImpl() {
        this.proto = SecurityProtos.CancelDelegationTokenResponseProto.getDefaultInstance();
    }
    
    public CancelDelegationTokenResponsePBImpl(final SecurityProtos.CancelDelegationTokenResponseProto proto) {
        this.proto = SecurityProtos.CancelDelegationTokenResponseProto.getDefaultInstance();
        this.proto = proto;
    }
    
    public SecurityProtos.CancelDelegationTokenResponseProto getProto() {
        return this.proto;
    }
    
    @Override
    public int hashCode() {
        return this.getProto().hashCode();
    }
    
    @Override
    public boolean equals(final Object other) {
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((CancelDelegationTokenResponsePBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
}
