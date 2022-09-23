// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.client.request;

import org.apache.kerby.kerberos.kerb.type.base.EncryptionKey;
import org.apache.kerby.KOptions;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.client.KrbContext;

public class ArmoredTgsRequest extends TgsRequest
{
    private final ArmoredRequest armoredRequest;
    
    public ArmoredTgsRequest(final KrbContext context) {
        super(context);
        this.armoredRequest = new ArmoredRequest(this);
    }
    
    @Override
    public void process() throws KrbException {
        super.process();
        this.armoredRequest.process();
    }
    
    @Override
    protected void preauth() throws KrbException {
        this.armoredRequest.preauth();
        super.preauth();
    }
    
    @Override
    public KOptions getPreauthOptions() {
        return this.armoredRequest.getPreauthOptions();
    }
    
    @Override
    public EncryptionKey getClientKey() throws KrbException {
        return this.armoredRequest.getClientKey();
    }
    
    @Override
    public EncryptionKey getSessionKey() {
        return this.armoredRequest.getArmorCacheKey();
    }
}
