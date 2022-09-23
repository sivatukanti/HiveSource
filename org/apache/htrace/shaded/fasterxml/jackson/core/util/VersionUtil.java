// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.core.util;

import java.util.Properties;
import java.io.IOException;
import java.io.BufferedReader;
import org.apache.htrace.shaded.fasterxml.jackson.core.Versioned;
import java.io.InputStream;
import java.io.Closeable;
import java.io.UnsupportedEncodingException;
import java.io.Reader;
import java.io.InputStreamReader;
import org.apache.htrace.shaded.fasterxml.jackson.core.Version;
import java.util.regex.Pattern;

public class VersionUtil
{
    private static final Pattern V_SEP;
    private final Version _v;
    
    protected VersionUtil() {
        Version v = null;
        try {
            v = versionFor(this.getClass());
        }
        catch (Exception e) {
            System.err.println("ERROR: Failed to load Version information from " + this.getClass());
        }
        if (v == null) {
            v = Version.unknownVersion();
        }
        this._v = v;
    }
    
    public Version version() {
        return this._v;
    }
    
    public static Version versionFor(final Class<?> cls) {
        final Version packageVersion = packageVersionFor(cls);
        if (packageVersion != null) {
            return packageVersion;
        }
        final InputStream in = cls.getResourceAsStream("VERSION.txt");
        if (in == null) {
            return Version.unknownVersion();
        }
        try {
            final InputStreamReader reader = new InputStreamReader(in, "UTF-8");
            return doReadVersion(reader);
        }
        catch (UnsupportedEncodingException e) {
            return Version.unknownVersion();
        }
        finally {
            _close(in);
        }
    }
    
    public static Version packageVersionFor(final Class<?> cls) {
        try {
            final String versionInfoClassName = cls.getPackage().getName() + ".PackageVersion";
            final Class<?> vClass = Class.forName(versionInfoClassName, true, cls.getClassLoader());
            try {
                return ((Versioned)vClass.newInstance()).version();
            }
            catch (Exception e) {
                throw new IllegalArgumentException("Failed to get Versioned out of " + vClass);
            }
        }
        catch (Exception e2) {
            return null;
        }
    }
    
    private static Version doReadVersion(final Reader r) {
        String version = null;
        String group = null;
        String artifact = null;
        final BufferedReader br = new BufferedReader(r);
        try {
            version = br.readLine();
            if (version != null) {
                group = br.readLine();
                if (group != null) {
                    artifact = br.readLine();
                }
            }
        }
        catch (IOException ignored) {}
        finally {
            _close(br);
        }
        if (group != null) {
            group = group.trim();
        }
        if (artifact != null) {
            artifact = artifact.trim();
        }
        return parseVersion(version, group, artifact);
    }
    
    public static Version mavenVersionFor(final ClassLoader cl, final String groupId, final String artifactId) {
        final InputStream pomProperties = cl.getResourceAsStream("META-INF/maven/" + groupId.replaceAll("\\.", "/") + "/" + artifactId + "/pom.properties");
        if (pomProperties != null) {
            try {
                final Properties props = new Properties();
                props.load(pomProperties);
                final String versionStr = props.getProperty("version");
                final String pomPropertiesArtifactId = props.getProperty("artifactId");
                final String pomPropertiesGroupId = props.getProperty("groupId");
                return parseVersion(versionStr, pomPropertiesGroupId, pomPropertiesArtifactId);
            }
            catch (IOException e) {}
            finally {
                _close(pomProperties);
            }
        }
        return Version.unknownVersion();
    }
    
    public static Version parseVersion(String s, final String groupId, final String artifactId) {
        if (s != null && (s = s.trim()).length() > 0) {
            final String[] parts = VersionUtil.V_SEP.split(s);
            return new Version(parseVersionPart(parts[0]), (parts.length > 1) ? parseVersionPart(parts[1]) : 0, (parts.length > 2) ? parseVersionPart(parts[2]) : 0, (parts.length > 3) ? parts[3] : null, groupId, artifactId);
        }
        return null;
    }
    
    protected static int parseVersionPart(final String s) {
        int number = 0;
        for (int i = 0, len = s.length(); i < len; ++i) {
            final char c = s.charAt(i);
            if (c > '9') {
                break;
            }
            if (c < '0') {
                break;
            }
            number = number * 10 + (c - '0');
        }
        return number;
    }
    
    private static final void _close(final Closeable c) {
        try {
            c.close();
        }
        catch (IOException ex) {}
    }
    
    public static final void throwInternal() {
        throw new RuntimeException("Internal error: this code path should never get executed");
    }
    
    static {
        V_SEP = Pattern.compile("[-_./;:]");
    }
}
