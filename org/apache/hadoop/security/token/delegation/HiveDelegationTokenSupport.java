// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.token.delegation;

import java.io.DataInput;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.DataOutput;
import org.apache.hadoop.io.WritableUtils;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;

public final class HiveDelegationTokenSupport
{
    private HiveDelegationTokenSupport() {
    }
    
    public static byte[] encodeDelegationTokenInformation(final AbstractDelegationTokenSecretManager.DelegationTokenInformation token) {
        try {
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            final DataOutputStream out = new DataOutputStream(bos);
            WritableUtils.writeVInt(out, token.password.length);
            out.write(token.password);
            out.writeLong(token.renewDate);
            out.flush();
            return bos.toByteArray();
        }
        catch (IOException ex) {
            throw new RuntimeException("Failed to encode token.", ex);
        }
    }
    
    public static AbstractDelegationTokenSecretManager.DelegationTokenInformation decodeDelegationTokenInformation(final byte[] tokenBytes) throws IOException {
        final DataInputStream in = new DataInputStream(new ByteArrayInputStream(tokenBytes));
        final AbstractDelegationTokenSecretManager.DelegationTokenInformation token = new AbstractDelegationTokenSecretManager.DelegationTokenInformation(0L, null);
        final int len = WritableUtils.readVInt(in);
        in.readFully(token.password = new byte[len]);
        token.renewDate = in.readLong();
        return token;
    }
    
    public static void rollMasterKey(final AbstractDelegationTokenSecretManager<? extends AbstractDelegationTokenIdentifier> mgr) throws IOException {
        mgr.rollMasterKey();
    }
}
