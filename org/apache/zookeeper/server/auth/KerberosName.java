// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.auth;

import org.apache.zookeeper.server.util.KerberosUtil;
import java.util.Iterator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.List;
import java.util.regex.Pattern;

public class KerberosName
{
    private final String serviceName;
    private final String hostName;
    private final String realm;
    private static final Pattern nameParser;
    private static Pattern parameterPattern;
    private static final Pattern ruleParser;
    private static final Pattern nonSimplePattern;
    private static List<Rule> rules;
    private static String defaultRealm;
    
    public KerberosName(final String name) {
        final Matcher match = KerberosName.nameParser.matcher(name);
        if (!match.matches()) {
            if (name.contains("@")) {
                throw new IllegalArgumentException("Malformed Kerberos name: " + name);
            }
            this.serviceName = name;
            this.hostName = null;
            this.realm = null;
        }
        else {
            this.serviceName = match.group(1);
            this.hostName = match.group(3);
            this.realm = match.group(4);
        }
    }
    
    public String getDefaultRealm() {
        return KerberosName.defaultRealm;
    }
    
    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        result.append(this.serviceName);
        if (this.hostName != null) {
            result.append('/');
            result.append(this.hostName);
        }
        if (this.realm != null) {
            result.append('@');
            result.append(this.realm);
        }
        return result.toString();
    }
    
    public String getServiceName() {
        return this.serviceName;
    }
    
    public String getHostName() {
        return this.hostName;
    }
    
    public String getRealm() {
        return this.realm;
    }
    
    static List<Rule> parseRules(final String rules) {
        final List<Rule> result = new ArrayList<Rule>();
        Matcher matcher;
        for (String remaining = rules.trim(); remaining.length() > 0; remaining = remaining.substring(matcher.end())) {
            matcher = KerberosName.ruleParser.matcher(remaining);
            if (!matcher.lookingAt()) {
                throw new IllegalArgumentException("Invalid rule: " + remaining);
            }
            if (matcher.group(2) != null) {
                result.add(new Rule());
            }
            else {
                result.add(new Rule(Integer.parseInt(matcher.group(4)), matcher.group(5), matcher.group(7), matcher.group(9), matcher.group(10), "g".equals(matcher.group(11))));
            }
        }
        return result;
    }
    
    public static void setConfiguration() throws IOException {
        final String ruleString = System.getProperty("zookeeper.security.auth_to_local", "DEFAULT");
        KerberosName.rules = parseRules(ruleString);
    }
    
    public String getShortName() throws IOException {
        String[] params;
        if (this.hostName == null) {
            if (this.realm == null) {
                return this.serviceName;
            }
            params = new String[] { this.realm, this.serviceName };
        }
        else {
            params = new String[] { this.realm, this.serviceName, this.hostName };
        }
        for (final Rule r : KerberosName.rules) {
            final String result = r.apply(params);
            if (result != null) {
                return result;
            }
        }
        throw new NoMatchingRule("No rules applied to " + this.toString());
    }
    
    static void printRules() throws IOException {
        int i = 0;
        for (final Rule r : KerberosName.rules) {
            System.out.println(++i + " " + r);
        }
    }
    
    public static void main(final String[] args) throws Exception {
        for (final String arg : args) {
            final KerberosName name = new KerberosName(arg);
            System.out.println("Name: " + name + " to " + name.getShortName());
        }
    }
    
    static {
        nameParser = Pattern.compile("([^/@]*)(/([^/@]*))?@([^/@]*)");
        KerberosName.parameterPattern = Pattern.compile("([^$]*)(\\$(\\d*))?");
        ruleParser = Pattern.compile("\\s*((DEFAULT)|(RULE:\\[(\\d*):([^\\]]*)](\\(([^)]*)\\))?(s/([^/]*)/([^/]*)/(g)?)?))");
        nonSimplePattern = Pattern.compile("[/@]");
        try {
            KerberosName.defaultRealm = KerberosUtil.getDefaultRealm();
        }
        catch (Exception ke) {
            if (System.getProperty("zookeeper.requireKerberosConfig") != null && System.getProperty("zookeeper.requireKerberosConfig").equals("true")) {
                throw new IllegalArgumentException("Can't get Kerberos configuration", ke);
            }
            KerberosName.defaultRealm = "";
        }
        try {
            setConfiguration();
        }
        catch (IOException e) {
            throw new IllegalArgumentException("Could not configure Kerberos principal name mapping.");
        }
    }
    
    private static class Rule
    {
        private final boolean isDefault;
        private final int numOfComponents;
        private final String format;
        private final Pattern match;
        private final Pattern fromPattern;
        private final String toPattern;
        private final boolean repeat;
        
        Rule() {
            this.isDefault = true;
            this.numOfComponents = 0;
            this.format = null;
            this.match = null;
            this.fromPattern = null;
            this.toPattern = null;
            this.repeat = false;
        }
        
        Rule(final int numOfComponents, final String format, final String match, final String fromPattern, final String toPattern, final boolean repeat) {
            this.isDefault = false;
            this.numOfComponents = numOfComponents;
            this.format = format;
            this.match = ((match == null) ? null : Pattern.compile(match));
            this.fromPattern = ((fromPattern == null) ? null : Pattern.compile(fromPattern));
            this.toPattern = toPattern;
            this.repeat = repeat;
        }
        
        @Override
        public String toString() {
            final StringBuilder buf = new StringBuilder();
            if (this.isDefault) {
                buf.append("DEFAULT");
            }
            else {
                buf.append("RULE:[");
                buf.append(this.numOfComponents);
                buf.append(':');
                buf.append(this.format);
                buf.append(']');
                if (this.match != null) {
                    buf.append('(');
                    buf.append(this.match);
                    buf.append(')');
                }
                if (this.fromPattern != null) {
                    buf.append("s/");
                    buf.append(this.fromPattern);
                    buf.append('/');
                    buf.append(this.toPattern);
                    buf.append('/');
                    if (this.repeat) {
                        buf.append('g');
                    }
                }
            }
            return buf.toString();
        }
        
        static String replaceParameters(final String format, final String[] params) throws BadFormatString {
            final Matcher match = KerberosName.parameterPattern.matcher(format);
            int start = 0;
            final StringBuilder result = new StringBuilder();
            while (start < format.length() && match.find(start)) {
                result.append(match.group(1));
                final String paramNum = match.group(3);
                if (paramNum != null) {
                    try {
                        final int num = Integer.parseInt(paramNum);
                        if (num < 0 || num > params.length) {
                            throw new BadFormatString("index " + num + " from " + format + " is outside of the valid range 0 to " + (params.length - 1));
                        }
                        result.append(params[num]);
                    }
                    catch (NumberFormatException nfe) {
                        throw new BadFormatString("bad format in username mapping in " + paramNum, nfe);
                    }
                }
                start = match.end();
            }
            return result.toString();
        }
        
        static String replaceSubstitution(final String base, final Pattern from, final String to, final boolean repeat) {
            final Matcher match = from.matcher(base);
            if (repeat) {
                return match.replaceAll(to);
            }
            return match.replaceFirst(to);
        }
        
        String apply(final String[] params) throws IOException {
            String result = null;
            if (this.isDefault) {
                if (KerberosName.defaultRealm.equals(params[0])) {
                    result = params[1];
                }
            }
            else if (params.length - 1 == this.numOfComponents) {
                final String base = replaceParameters(this.format, params);
                if (this.match == null || this.match.matcher(base).matches()) {
                    if (this.fromPattern == null) {
                        result = base;
                    }
                    else {
                        result = replaceSubstitution(base, this.fromPattern, this.toPattern, this.repeat);
                    }
                }
            }
            if (result != null && KerberosName.nonSimplePattern.matcher(result).find()) {
                throw new NoMatchingRule("Non-simple name " + result + " after auth_to_local rule " + this);
            }
            return result;
        }
    }
    
    public static class BadFormatString extends IOException
    {
        BadFormatString(final String msg) {
            super(msg);
        }
        
        BadFormatString(final String msg, final Throwable err) {
            super(msg, err);
        }
    }
    
    public static class NoMatchingRule extends IOException
    {
        NoMatchingRule(final String msg) {
            super(msg);
        }
    }
}
