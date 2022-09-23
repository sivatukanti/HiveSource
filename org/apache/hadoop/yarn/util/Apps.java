// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.util;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.util.StringInterner;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.hadoop.util.Shell;
import java.util.Map;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import java.util.Iterator;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class Apps
{
    public static final String APP = "application";
    public static final String ID = "ID";
    
    public static ApplicationId toAppID(final String aid) {
        final Iterator<String> it = StringHelper._split(aid).iterator();
        return toAppID("application", aid, it);
    }
    
    public static ApplicationId toAppID(final String prefix, final String s, final Iterator<String> it) {
        if (!it.hasNext() || !it.next().equals(prefix)) {
            throwParseException(StringHelper.sjoin(prefix, "ID"), s);
        }
        shouldHaveNext(prefix, s, it);
        final ApplicationId appId = ApplicationId.newInstance(Long.parseLong(it.next()), Integer.parseInt(it.next()));
        return appId;
    }
    
    public static void shouldHaveNext(final String prefix, final String s, final Iterator<String> it) {
        if (!it.hasNext()) {
            throwParseException(StringHelper.sjoin(prefix, "ID"), s);
        }
    }
    
    public static void throwParseException(final String name, final String s) {
        throw new YarnRuntimeException(StringHelper.join("Error parsing ", name, ": ", s));
    }
    
    public static void setEnvFromInputString(final Map<String, String> env, final String envString, final String classPathSeparator) {
        if (envString != null && envString.length() > 0) {
            final String[] childEnvs = envString.split(",");
            final Pattern p = Pattern.compile(Shell.getEnvironmentVariableRegex());
            for (final String cEnv : childEnvs) {
                final String[] parts = cEnv.split("=");
                final Matcher m = p.matcher(parts[1]);
                final StringBuffer sb = new StringBuffer();
                while (m.find()) {
                    final String var = m.group(1);
                    String replace = env.get(var);
                    if (replace == null) {
                        replace = System.getenv(var);
                    }
                    if (replace == null) {
                        replace = "";
                    }
                    m.appendReplacement(sb, Matcher.quoteReplacement(replace));
                }
                m.appendTail(sb);
                addToEnvironment(env, parts[0], sb.toString(), classPathSeparator);
            }
        }
    }
    
    @Deprecated
    public static void setEnvFromInputString(final Map<String, String> env, final String envString) {
        setEnvFromInputString(env, envString, File.pathSeparator);
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public static void addToEnvironment(final Map<String, String> environment, final String variable, final String value, final String classPathSeparator) {
        String val = environment.get(variable);
        if (val == null) {
            val = value;
        }
        else {
            val = val + classPathSeparator + value;
        }
        environment.put(StringInterner.weakIntern(variable), StringInterner.weakIntern(val));
    }
    
    @Deprecated
    public static void addToEnvironment(final Map<String, String> environment, final String variable, final String value) {
        addToEnvironment(environment, variable, value, File.pathSeparator);
    }
    
    public static String crossPlatformify(final String var) {
        return "{{" + var + "}}";
    }
}
