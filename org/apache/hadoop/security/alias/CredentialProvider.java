// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.alias;

import java.util.List;
import java.io.IOException;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public abstract class CredentialProvider
{
    public static final String CLEAR_TEXT_FALLBACK = "hadoop.security.credential.clear-text-fallback";
    
    public boolean isTransient() {
        return false;
    }
    
    public abstract void flush() throws IOException;
    
    public abstract CredentialEntry getCredentialEntry(final String p0) throws IOException;
    
    public abstract List<String> getAliases() throws IOException;
    
    public abstract CredentialEntry createCredentialEntry(final String p0, final char[] p1) throws IOException;
    
    public abstract void deleteCredentialEntry(final String p0) throws IOException;
    
    public boolean needsPassword() throws IOException {
        return false;
    }
    
    public String noPasswordWarning() {
        return null;
    }
    
    public String noPasswordError() {
        return null;
    }
    
    public static class CredentialEntry
    {
        private final String alias;
        private final char[] credential;
        
        protected CredentialEntry(final String alias, final char[] credential) {
            this.alias = alias;
            this.credential = credential;
        }
        
        public String getAlias() {
            return this.alias;
        }
        
        public char[] getCredential() {
            return this.credential;
        }
        
        @Override
        public String toString() {
            final StringBuilder buf = new StringBuilder();
            buf.append("alias(");
            buf.append(this.alias);
            buf.append(")=");
            if (this.credential == null) {
                buf.append("null");
            }
            else {
                for (final char c : this.credential) {
                    buf.append(c);
                }
            }
            return buf.toString();
        }
    }
}
