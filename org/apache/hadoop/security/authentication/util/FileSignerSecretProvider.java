// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.authentication.util;

import java.io.Reader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.io.InputStream;
import java.io.InputStreamReader;
import com.google.common.base.Charsets;
import java.io.FileInputStream;
import javax.servlet.ServletContext;
import java.util.Properties;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.classification.InterfaceStability;

@InterfaceStability.Unstable
@InterfaceAudience.Private
public class FileSignerSecretProvider extends SignerSecretProvider
{
    private byte[] secret;
    private byte[][] secrets;
    
    @Override
    public void init(final Properties config, final ServletContext servletContext, final long tokenValidity) throws Exception {
        final String signatureSecretFile = config.getProperty("signature.secret.file", null);
        Reader reader = null;
        if (signatureSecretFile != null) {
            try {
                final StringBuilder sb = new StringBuilder();
                reader = new InputStreamReader(new FileInputStream(signatureSecretFile), Charsets.UTF_8);
                for (int c = reader.read(); c > -1; c = reader.read()) {
                    sb.append((char)c);
                }
                this.secret = sb.toString().getBytes(Charset.forName("UTF-8"));
            }
            catch (IOException ex) {
                throw new RuntimeException("Could not read signature secret file: " + signatureSecretFile);
            }
            finally {
                if (reader != null) {
                    try {
                        reader.close();
                    }
                    catch (IOException ex2) {}
                }
            }
        }
        this.secrets = new byte[][] { this.secret };
    }
    
    @Override
    public byte[] getCurrentSecret() {
        return this.secret;
    }
    
    @Override
    public byte[][] getAllSecrets() {
        return this.secrets;
    }
}
