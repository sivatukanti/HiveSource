// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records;

import java.nio.ByteBuffer;
import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class Token
{
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public static Token newInstance(final byte[] identifier, final String kind, final byte[] password, final String service) {
        final Token token = Records.newRecord(Token.class);
        token.setIdentifier(ByteBuffer.wrap(identifier));
        token.setKind(kind);
        token.setPassword(ByteBuffer.wrap(password));
        token.setService(service);
        return token;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract ByteBuffer getIdentifier();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setIdentifier(final ByteBuffer p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract ByteBuffer getPassword();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setPassword(final ByteBuffer p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract String getKind();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setKind(final String p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract String getService();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setService(final String p0);
}
