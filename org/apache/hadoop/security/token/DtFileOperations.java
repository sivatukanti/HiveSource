// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.token;

import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.ServiceLoader;
import java.util.Iterator;
import org.apache.hadoop.security.token.delegation.AbstractDelegationTokenIdentifier;
import org.apache.commons.lang3.StringUtils;
import java.io.PrintStream;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.fs.Path;
import java.io.File;
import java.util.Date;
import java.text.DateFormat;
import org.apache.hadoop.io.Text;
import org.slf4j.Logger;

public final class DtFileOperations
{
    private static final Logger LOG;
    public static final String FORMAT_PB = "protobuf";
    public static final String FORMAT_JAVA = "java";
    private static final String NA_STRING = "-NA-";
    private static final String PREFIX_HTTP = "http://";
    private static final String PREFIX_HTTPS = "https://";
    
    private DtFileOperations() {
    }
    
    private static String stripPrefix(final String u) {
        return u.replaceFirst("http://", "").replaceFirst("https://", "");
    }
    
    private static boolean matchAlias(final Token<?> token, final Text alias) {
        return alias == null || token.getService().equals(alias);
    }
    
    private static boolean matchService(final DtFetcher fetcher, final Text service, final String url) {
        final Text sName = fetcher.getServiceName();
        return (service == null && url.startsWith(sName.toString() + "://")) || (service != null && service.equals(sName));
    }
    
    private static String formatDate(final long date) {
        final DateFormat df = DateFormat.getDateTimeInstance(3, 3);
        return df.format(new Date(date));
    }
    
    private static Path fileToPath(final File f) {
        return new Path("file:" + f.getAbsolutePath());
    }
    
    public static void doFormattedWrite(final File f, final String format, final Credentials creds, final Configuration conf) throws IOException {
        Credentials.SerializedFormat credsFormat = Credentials.SerializedFormat.WRITABLE;
        if (format.equals("protobuf")) {
            credsFormat = Credentials.SerializedFormat.PROTOBUF;
        }
        creds.writeTokenStorageFile(fileToPath(f), conf, credsFormat);
    }
    
    public static void printTokenFile(final File tokenFile, final Text alias, final Configuration conf, final PrintStream out) throws IOException {
        out.println("File: " + tokenFile.getPath());
        final Credentials creds = Credentials.readTokenStorageFile(tokenFile, conf);
        printCredentials(creds, alias, out);
    }
    
    public static void printCredentials(final Credentials creds, final Text alias, final PrintStream out) throws IOException {
        boolean tokenHeader = true;
        final String fmt = "%-24s %-20s %-15s %-12s %s%n";
        for (final Token<?> token : creds.getAllTokens()) {
            if (matchAlias(token, alias)) {
                if (tokenHeader) {
                    out.printf(fmt, "Token kind", "Service", "Renewer", "Exp date", "URL enc token");
                    out.println(StringUtils.repeat("-", 80));
                    tokenHeader = false;
                }
                final AbstractDelegationTokenIdentifier id = (AbstractDelegationTokenIdentifier)token.decodeIdentifier();
                out.printf(fmt, token.getKind(), token.getService(), (id != null) ? id.getRenewer() : "-NA-", (id != null) ? formatDate(id.getMaxDate()) : "-NA-", token.encodeToUrlString());
            }
        }
    }
    
    public static void getTokenFile(final File tokenFile, final String fileFormat, final Text alias, final Text service, final String url, final String renewer, final Configuration conf) throws Exception {
        Token<?> token = null;
        final Credentials creds = tokenFile.exists() ? Credentials.readTokenStorageFile(tokenFile, conf) : new Credentials();
        final ServiceLoader<DtFetcher> loader = ServiceLoader.load(DtFetcher.class);
        for (final DtFetcher fetcher : loader) {
            if (matchService(fetcher, service, url)) {
                if (!fetcher.isTokenRequired()) {
                    final String message = "DtFetcher for service '" + service + "' does not require a token.  Check your configuration.  Note: security may be disabled or there may be two DtFetcher providers for the same service designation.";
                    DtFileOperations.LOG.error(message);
                    throw new IllegalArgumentException(message);
                }
                token = fetcher.addDelegationTokens(conf, creds, renewer, stripPrefix(url));
            }
        }
        if (alias != null) {
            if (token == null) {
                final String message2 = "DtFetcher for service '" + service + "' does not allow aliasing.  Cannot apply alias '" + alias + "'.  Drop alias flag to get token for this service.";
                DtFileOperations.LOG.error(message2);
                throw new IOException(message2);
            }
            final Token<?> aliasedToken = token.copyToken();
            aliasedToken.setService(alias);
            creds.addToken(alias, (Token<? extends TokenIdentifier>)aliasedToken);
            DtFileOperations.LOG.info("Add token with service " + alias);
        }
        doFormattedWrite(tokenFile, fileFormat, creds, conf);
    }
    
    public static void aliasTokenFile(final File tokenFile, final String fileFormat, final Text alias, final Text service, final Configuration conf) throws Exception {
        final Credentials newCreds = new Credentials();
        final Credentials creds = Credentials.readTokenStorageFile(tokenFile, conf);
        for (final Token<?> token : creds.getAllTokens()) {
            newCreds.addToken(token.getService(), (Token<? extends TokenIdentifier>)token);
            if (token.getService().equals(service)) {
                final Token<?> aliasedToken = token.copyToken();
                aliasedToken.setService(alias);
                newCreds.addToken(alias, (Token<? extends TokenIdentifier>)aliasedToken);
            }
        }
        doFormattedWrite(tokenFile, fileFormat, newCreds, conf);
    }
    
    public static void appendTokenFiles(final ArrayList<File> tokenFiles, final String fileFormat, final Configuration conf) throws IOException {
        final Credentials newCreds = new Credentials();
        File lastTokenFile = null;
        final Iterator<File> iterator = tokenFiles.iterator();
        while (iterator.hasNext()) {
            final File tokenFile = lastTokenFile = iterator.next();
            final Credentials creds = Credentials.readTokenStorageFile(tokenFile, conf);
            for (final Token<?> token : creds.getAllTokens()) {
                newCreds.addToken(token.getService(), (Token<? extends TokenIdentifier>)token);
            }
        }
        doFormattedWrite(lastTokenFile, fileFormat, newCreds, conf);
    }
    
    public static void removeTokenFromFile(final boolean cancel, final File tokenFile, final String fileFormat, final Text alias, final Configuration conf) throws IOException, InterruptedException {
        final Credentials newCreds = new Credentials();
        final Credentials creds = Credentials.readTokenStorageFile(tokenFile, conf);
        for (final Token<?> token : creds.getAllTokens()) {
            if (matchAlias(token, alias)) {
                if (!token.isManaged() || !cancel) {
                    continue;
                }
                token.cancel(conf);
                DtFileOperations.LOG.info("Canceled " + token.getKind() + ":" + token.getService());
            }
            else {
                newCreds.addToken(token.getService(), (Token<? extends TokenIdentifier>)token);
            }
        }
        doFormattedWrite(tokenFile, fileFormat, newCreds, conf);
    }
    
    public static void renewTokenFile(final File tokenFile, final String fileFormat, final Text alias, final Configuration conf) throws IOException, InterruptedException {
        final Credentials creds = Credentials.readTokenStorageFile(tokenFile, conf);
        for (final Token<?> token : creds.getAllTokens()) {
            if (token.isManaged() && matchAlias(token, alias)) {
                final long result = token.renew(conf);
                DtFileOperations.LOG.info("Renewed" + token.getKind() + ":" + token.getService() + " until " + formatDate(result));
            }
        }
        doFormattedWrite(tokenFile, fileFormat, creds, conf);
    }
    
    static {
        LOG = LoggerFactory.getLogger(DtFileOperations.class);
    }
}
