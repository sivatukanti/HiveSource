// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.util;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.net.URLDecoder;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URI;
import java.io.IOException;
import java.net.MalformedURLException;
import java.io.File;
import java.net.URL;
import java.util.regex.Pattern;

public final class URLUtil
{
    private static final Pattern URI_WINDOWS_FILE_PATTERN;
    
    private URLUtil() {
    }
    
    public static URL urlFromSystemId(String sysId) throws IOException {
        try {
            sysId = cleanSystemId(sysId);
            final int ix = sysId.indexOf(58, 0);
            if (ix >= 3 && ix <= 8) {
                return new URL(sysId);
            }
            String absPath = new File(sysId).getAbsolutePath();
            final char sep = File.separatorChar;
            if (sep != '/') {
                absPath = absPath.replace(sep, '/');
            }
            if (absPath.length() > 0 && absPath.charAt(0) != '/') {
                absPath = "/" + absPath;
            }
            return new URL("file", "", absPath);
        }
        catch (MalformedURLException e) {
            throwIOException(e, sysId);
            return null;
        }
    }
    
    public static URI uriFromSystemId(final String sysId) throws IOException {
        try {
            if (sysId.indexOf(124, 0) > 0 && URLUtil.URI_WINDOWS_FILE_PATTERN.matcher(sysId).matches()) {
                return new URI(sysId.replace('|', ':'));
            }
            final int ix = sysId.indexOf(58, 0);
            if (ix >= 3 && ix <= 8) {
                return new URI(sysId);
            }
            String absPath = new File(sysId).getAbsolutePath();
            final char sep = File.separatorChar;
            if (sep != '/') {
                absPath = absPath.replace(sep, '/');
            }
            if (absPath.length() > 0 && absPath.charAt(0) != '/') {
                absPath = "/" + absPath;
            }
            return new URI("file", absPath, null);
        }
        catch (URISyntaxException e) {
            throwIOException(e, sysId);
            return null;
        }
    }
    
    public static URL urlFromSystemId(String sysId, final URL ctxt) throws IOException {
        if (ctxt == null) {
            return urlFromSystemId(sysId);
        }
        try {
            sysId = cleanSystemId(sysId);
            return new URL(ctxt, sysId);
        }
        catch (MalformedURLException e) {
            throwIOException(e, sysId);
            return null;
        }
    }
    
    public static URL urlFromCurrentDir() throws IOException {
        final File parent = new File("a").getAbsoluteFile().getParentFile();
        return toURL(parent);
    }
    
    public static InputStream inputStreamFromURL(final URL url) throws IOException {
        if ("file".equals(url.getProtocol())) {
            final String host = url.getHost();
            if (host == null || host.length() == 0) {
                String path = url.getPath();
                if (path.indexOf(37) >= 0) {
                    path = URLDecoder.decode(path, "UTF-8");
                }
                return new FileInputStream(path);
            }
        }
        return url.openStream();
    }
    
    public static OutputStream outputStreamFromURL(final URL url) throws IOException {
        if ("file".equals(url.getProtocol())) {
            final String host = url.getHost();
            if (host == null || host.length() == 0) {
                return new FileOutputStream(url.getPath());
            }
        }
        return url.openConnection().getOutputStream();
    }
    
    public static URL toURL(final File f) throws IOException {
        return f.toURI().toURL();
    }
    
    private static String cleanSystemId(final String sysId) {
        final int ix = sysId.indexOf(124);
        if (ix > 0 && URLUtil.URI_WINDOWS_FILE_PATTERN.matcher(sysId).matches()) {
            final StringBuilder sb = new StringBuilder(sysId);
            sb.setCharAt(ix, ':');
            return sb.toString();
        }
        return sysId;
    }
    
    private static void throwIOException(final Exception mex, final String sysId) throws IOException {
        final String msg = "[resolving systemId '" + sysId + "']: " + mex.toString();
        throw ExceptionUtil.constructIOException(msg, mex);
    }
    
    static {
        URI_WINDOWS_FILE_PATTERN = Pattern.compile("^file:///\\p{Alpha}|.*$");
    }
}
