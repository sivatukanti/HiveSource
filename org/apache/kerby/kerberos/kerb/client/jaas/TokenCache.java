// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.client.jaas;

import org.slf4j.LoggerFactory;
import java.io.Writer;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.commons.io.output.FileWriterWithEncoding;
import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.io.File;
import org.slf4j.Logger;

public class TokenCache
{
    private static final Logger LOG;
    private static final String DEFAULT_TOKEN_CACHE_PATH = ".tokenauth";
    private static final String TOKEN_CACHE_FILE = ".tokenauth.token";
    
    public static String readToken(final String tokenCacheFile) {
        File cacheFile;
        if (tokenCacheFile != null && !tokenCacheFile.isEmpty()) {
            cacheFile = new File(tokenCacheFile);
            if (!cacheFile.exists()) {
                throw new RuntimeException("Invalid token cache specified: " + tokenCacheFile);
            }
        }
        else {
            cacheFile = getDefaultTokenCache();
            if (!cacheFile.exists()) {
                throw new RuntimeException("No token cache available by default");
            }
        }
        String token = null;
        try {
            final List<String> lines = Files.readAllLines(cacheFile.toPath(), StandardCharsets.UTF_8);
            if (lines != null && !lines.isEmpty()) {
                token = lines.get(0);
            }
        }
        catch (IOException ex) {
            TokenCache.LOG.error("Failed to read file: " + cacheFile.getName());
        }
        return token;
    }
    
    public static void writeToken(final String token, final String tokenCacheFile) {
        final File cacheFile = new File(tokenCacheFile);
        try {
            final Writer writer = new FileWriterWithEncoding(cacheFile, StandardCharsets.UTF_8);
            writer.write(token);
            writer.flush();
            writer.close();
            cacheFile.setReadable(false, false);
            cacheFile.setReadable(true, true);
            if (!cacheFile.setWritable(true, true)) {
                throw new KrbException("Cache file is not readable.");
            }
        }
        catch (IOException ioe) {
            if (cacheFile.delete()) {
                System.err.println("Cache file is deleted.");
            }
        }
        catch (KrbException e) {
            TokenCache.LOG.error("Failed to write token to cache File. " + e.toString());
        }
    }
    
    public static File getDefaultTokenCache() {
        final String homeDir = System.getProperty("user.home", ".tokenauth");
        return new File(homeDir, ".tokenauth.token");
    }
    
    static {
        LOG = LoggerFactory.getLogger(TokenCache.class);
    }
}
