// 
// Decompiled by Procyon v0.5.36
// 

package javax.security.auth.message.callback;

import java.security.KeyStore;
import javax.security.auth.callback.Callback;

public class TrustStoreCallback implements Callback
{
    private KeyStore trustStore;
    
    public KeyStore getTrustStore() {
        return this.trustStore;
    }
    
    public void setTrustStore(final KeyStore trustStore) {
        this.trustStore = trustStore;
    }
}
