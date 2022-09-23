// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security;

import org.slf4j.LoggerFactory;
import java.io.InputStream;
import java.net.URL;
import org.apache.commons.io.IOUtils;
import java.io.IOException;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.conf.Configuration;
import java.net.URISyntaxException;
import org.apache.hadoop.security.alias.JavaKeyStoreProvider;
import org.apache.hadoop.fs.Path;
import java.net.URI;
import org.slf4j.Logger;
import com.google.common.annotations.VisibleForTesting;

public final class ProviderUtils
{
    @VisibleForTesting
    public static final String NO_PASSWORD_WARN = "WARNING: You have accepted the use of the default provider password\nby not configuring a password in one of the two following locations:\n";
    @VisibleForTesting
    public static final String NO_PASSWORD_ERROR = "ERROR: The provider cannot find a password in the expected locations.\nPlease supply a password using one of the following two mechanisms:\n";
    @VisibleForTesting
    public static final String NO_PASSWORD_CONT = "Continuing with the default provider password.\n";
    @VisibleForTesting
    public static final String NO_PASSWORD_INSTRUCTIONS_DOC = "Please review the documentation regarding provider passwords in\nthe keystore passwords section of the Credential Provider API\n";
    private static final Logger LOG;
    
    private ProviderUtils() {
    }
    
    public static Path unnestUri(final URI nestedUri) {
        final StringBuilder result = new StringBuilder();
        final String authority = nestedUri.getAuthority();
        if (authority != null) {
            final String[] parts = nestedUri.getAuthority().split("@", 2);
            result.append(parts[0]);
            result.append("://");
            if (parts.length == 2) {
                result.append(parts[1]);
            }
        }
        result.append(nestedUri.getPath());
        if (nestedUri.getQuery() != null) {
            result.append("?");
            result.append(nestedUri.getQuery());
        }
        if (nestedUri.getFragment() != null) {
            result.append("#");
            result.append(nestedUri.getFragment());
        }
        return new Path(result.toString());
    }
    
    public static URI nestURIForLocalJavaKeyStoreProvider(final URI localFile) throws URISyntaxException {
        if (!"file".equals(localFile.getScheme())) {
            throw new IllegalArgumentException("passed URI had a scheme other than file.");
        }
        if (localFile.getAuthority() != null) {
            throw new IllegalArgumentException("passed URI must not have an authority component. For non-local keystores, please use " + JavaKeyStoreProvider.class.getName());
        }
        return new URI("localjceks", "//file" + localFile.getSchemeSpecificPart(), localFile.getFragment());
    }
    
    public static Configuration excludeIncompatibleCredentialProviders(final Configuration config, final Class<? extends FileSystem> fileSystemClass) throws IOException {
        final String providerPath = config.get("hadoop.security.credential.provider.path");
        if (providerPath == null) {
            return config;
        }
        final StringBuffer newProviderPath = new StringBuffer();
        final String[] providers = providerPath.split(",");
        Path path = null;
        for (final String provider : providers) {
            try {
                path = unnestUri(new URI(provider));
                Class<? extends FileSystem> clazz = null;
                try {
                    final String scheme = path.toUri().getScheme();
                    clazz = FileSystem.getFileSystemClass(scheme, config);
                }
                catch (IOException ioe) {
                    if (newProviderPath.length() > 0) {
                        newProviderPath.append(",");
                    }
                    newProviderPath.append(provider);
                }
                if (clazz != null) {
                    if (fileSystemClass.isAssignableFrom(clazz)) {
                        ProviderUtils.LOG.debug("Filesystem based provider excluded from provider path due to recursive dependency: " + provider);
                    }
                    else {
                        if (newProviderPath.length() > 0) {
                            newProviderPath.append(",");
                        }
                        newProviderPath.append(provider);
                    }
                }
            }
            catch (URISyntaxException e) {
                ProviderUtils.LOG.warn("Credential Provider URI is invalid." + provider);
            }
        }
        final String effectivePath = newProviderPath.toString();
        if (effectivePath.equals(providerPath)) {
            return config;
        }
        final Configuration conf = new Configuration(config);
        if (effectivePath.equals("")) {
            conf.unset("hadoop.security.credential.provider.path");
        }
        else {
            conf.set("hadoop.security.credential.provider.path", effectivePath);
        }
        return conf;
    }
    
    public static char[] locatePassword(final String envWithPass, final String fileWithPass) throws IOException {
        char[] pass = null;
        if (System.getenv().containsKey(envWithPass)) {
            pass = System.getenv(envWithPass).toCharArray();
        }
        if (pass == null && fileWithPass != null) {
            final ClassLoader cl = Thread.currentThread().getContextClassLoader();
            final URL pwdFile = cl.getResource(fileWithPass);
            if (pwdFile == null) {
                throw new IOException("Password file does not exist");
            }
            try (final InputStream is = pwdFile.openStream()) {
                pass = IOUtils.toString(is).trim().toCharArray();
            }
        }
        return pass;
    }
    
    private static String noPasswordInstruction(final String envKey, final String fileKey) {
        return "    * In the environment variable " + envKey + "\n    * In a file referred to by the configuration entry\n      " + fileKey + ".\n" + "Please review the documentation regarding provider passwords in\nthe keystore passwords section of the Credential Provider API\n";
    }
    
    public static String noPasswordWarning(final String envKey, final String fileKey) {
        return "WARNING: You have accepted the use of the default provider password\nby not configuring a password in one of the two following locations:\n" + noPasswordInstruction(envKey, fileKey) + "Continuing with the default provider password.\n";
    }
    
    public static String noPasswordError(final String envKey, final String fileKey) {
        return "ERROR: The provider cannot find a password in the expected locations.\nPlease supply a password using one of the following two mechanisms:\n" + noPasswordInstruction(envKey, fileKey);
    }
    
    static {
        LOG = LoggerFactory.getLogger(ProviderUtils.class);
    }
}
