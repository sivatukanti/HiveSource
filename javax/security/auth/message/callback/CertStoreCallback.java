// 
// Decompiled by Procyon v0.5.36
// 

package javax.security.auth.message.callback;

import java.security.cert.CertStore;
import javax.security.auth.callback.Callback;

public class CertStoreCallback implements Callback
{
    private CertStore certStore;
    
    public CertStore getCertStore() {
        return this.certStore;
    }
    
    public void setCertStore(final CertStore certStore) {
        this.certStore = certStore;
    }
}
