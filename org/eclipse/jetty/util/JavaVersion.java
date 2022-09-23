// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaVersion
{
    public static final String JAVA_TARGET_PLATFORM = "org.eclipse.jetty.javaTargetPlatform";
    private static final Pattern PRE_JDK9;
    private static final Pattern JDK9;
    public static final JavaVersion VERSION;
    private final String version;
    private final int platform;
    private final int major;
    private final int minor;
    private final int micro;
    private final int update;
    private final String suffix;
    
    public static JavaVersion parse(final String version) {
        if (version.startsWith("1.")) {
            return parsePreJDK9(version);
        }
        return parseJDK9(version);
    }
    
    private static JavaVersion parsePreJDK9(final String version) {
        final Matcher matcher = JavaVersion.PRE_JDK9.matcher(version);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid Java version " + version);
        }
        final int major = 1;
        final int minor = Integer.parseInt(matcher.group(1));
        final String microGroup = matcher.group(3);
        final int micro = (microGroup == null || microGroup.isEmpty()) ? 0 : Integer.parseInt(microGroup);
        final String updateGroup = matcher.group(5);
        final int update = (updateGroup == null || updateGroup.isEmpty()) ? 0 : Integer.parseInt(updateGroup);
        final String suffix = matcher.group(6);
        return new JavaVersion(version, minor, major, minor, micro, update, suffix);
    }
    
    private static JavaVersion parseJDK9(final String version) {
        final Matcher matcher = JavaVersion.JDK9.matcher(version);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid Java version " + version);
        }
        final int major = Integer.parseInt(matcher.group(1));
        final String minorGroup = matcher.group(3);
        final int minor = (minorGroup == null || minorGroup.isEmpty()) ? 0 : Integer.parseInt(minorGroup);
        final String microGroup = matcher.group(5);
        final int micro = (microGroup == null || microGroup.isEmpty()) ? 0 : Integer.parseInt(microGroup);
        final String suffix = matcher.group(6);
        return new JavaVersion(version, major, major, minor, micro, 0, suffix);
    }
    
    private JavaVersion(final String version, final int platform, final int major, final int minor, final int micro, final int update, final String suffix) {
        this.version = version;
        this.platform = platform;
        this.major = major;
        this.minor = minor;
        this.micro = micro;
        this.update = update;
        this.suffix = suffix;
    }
    
    public String getVersion() {
        return this.version;
    }
    
    public int getPlatform() {
        return this.platform;
    }
    
    public int getMajor() {
        return this.major;
    }
    
    public int getMinor() {
        return this.minor;
    }
    
    public int getMicro() {
        return this.micro;
    }
    
    public int getUpdate() {
        return this.update;
    }
    
    public String getSuffix() {
        return this.suffix;
    }
    
    @Override
    public String toString() {
        return this.version;
    }
    
    static {
        PRE_JDK9 = Pattern.compile("1\\.(\\d)(\\.(\\d+)(_(\\d+))?)?(-.+)?");
        JDK9 = Pattern.compile("(\\d+)(\\.(\\d+))?(\\.(\\d+))?((-.+)?(\\+(\\d+)?(-.+)?)?)");
        VERSION = parse(System.getProperty("java.version"));
    }
}
