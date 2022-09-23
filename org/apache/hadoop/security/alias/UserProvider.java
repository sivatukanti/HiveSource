// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.alias;

import org.apache.hadoop.conf.Configuration;
import java.net.URI;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.nio.charset.StandardCharsets;
import org.apache.hadoop.io.Text;
import java.io.IOException;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class UserProvider extends CredentialProvider
{
    public static final String SCHEME_NAME = "user";
    private final UserGroupInformation user;
    private final Credentials credentials;
    
    private UserProvider() throws IOException {
        this.user = UserGroupInformation.getCurrentUser();
        this.credentials = this.user.getCredentials();
    }
    
    @Override
    public boolean isTransient() {
        return true;
    }
    
    @Override
    public synchronized CredentialEntry getCredentialEntry(final String alias) {
        final byte[] bytes = this.credentials.getSecretKey(new Text(alias));
        if (bytes == null) {
            return null;
        }
        return new CredentialEntry(alias, new String(bytes, StandardCharsets.UTF_8).toCharArray());
    }
    
    @Override
    public synchronized CredentialEntry createCredentialEntry(final String name, final char[] credential) throws IOException {
        final Text nameT = new Text(name);
        if (this.credentials.getSecretKey(nameT) != null) {
            throw new IOException("Credential " + name + " already exists in " + this);
        }
        this.credentials.addSecretKey(new Text(name), new String(credential).getBytes("UTF-8"));
        return new CredentialEntry(name, credential);
    }
    
    @Override
    public synchronized void deleteCredentialEntry(final String name) throws IOException {
        final byte[] cred = this.credentials.getSecretKey(new Text(name));
        if (cred != null) {
            this.credentials.removeSecretKey(new Text(name));
            return;
        }
        throw new IOException("Credential " + name + " does not exist in " + this);
    }
    
    @Override
    public String toString() {
        return "user:///";
    }
    
    @Override
    public synchronized void flush() {
        this.user.addCredentials(this.credentials);
    }
    
    @Override
    public synchronized List<String> getAliases() throws IOException {
        final List<String> list = new ArrayList<String>();
        final List<Text> aliases = this.credentials.getAllSecretKeys();
        for (final Text key : aliases) {
            list.add(key.toString());
        }
        return list;
    }
    
    public static class Factory extends CredentialProviderFactory
    {
        @Override
        public CredentialProvider createProvider(final URI providerName, final Configuration conf) throws IOException {
            if ("user".equals(providerName.getScheme())) {
                return new UserProvider(null);
            }
            return null;
        }
    }
}
