// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.authentication.util;

import java.util.Locale;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.regex.Matcher;
import com.google.common.annotations.VisibleForTesting;
import java.util.List;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Evolving
public class KerberosName
{
    private static final Logger LOG;
    public static final String MECHANISM_HADOOP = "hadoop";
    public static final String MECHANISM_MIT = "mit";
    public static final String DEFAULT_MECHANISM = "hadoop";
    private final String serviceName;
    private final String hostName;
    private final String realm;
    private static final Pattern nameParser;
    private static Pattern parameterPattern;
    private static final Pattern ruleParser;
    private static final Pattern nonSimplePattern;
    private static List<Rule> rules;
    private static String ruleMechanism;
    private static String defaultRealm;
    
    @VisibleForTesting
    public static void resetDefaultRealm() {
        try {
            KerberosName.defaultRealm = KerberosUtil.getDefaultRealm();
        }
        catch (Exception ke) {
            KerberosName.LOG.debug("resetting default realm failed, current default realm will still be used.", ke);
        }
    }
    
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
            this.realm = match.group(5);
        }
    }
    
    public static synchronized String getDefaultRealm() {
        if (KerberosName.defaultRealm == null) {
            try {
                KerberosName.defaultRealm = KerberosUtil.getDefaultRealm();
            }
            catch (Exception ke) {
                KerberosName.LOG.debug("Kerberos krb5 configuration not found, setting default realm to empty");
                KerberosName.defaultRealm = "";
            }
        }
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
                result.add(new Rule(Integer.parseInt(matcher.group(4)), matcher.group(5), matcher.group(7), matcher.group(9), matcher.group(10), "g".equals(matcher.group(11)), "L".equals(matcher.group(12))));
            }
        }
        return result;
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
        String ruleMechanism = KerberosName.ruleMechanism;
        if (ruleMechanism == null && KerberosName.rules != null) {
            KerberosName.LOG.warn("auth_to_local rule mechanism not set.Using default of hadoop");
            ruleMechanism = "hadoop";
        }
        for (final Rule r : KerberosName.rules) {
            final String result = r.apply(params, ruleMechanism);
            if (result != null) {
                return result;
            }
        }
        if (ruleMechanism.equalsIgnoreCase("hadoop")) {
            throw new NoMatchingRule("No rules applied to " + this.toString());
        }
        return this.toString();
    }
    
    public static String getRules() {
        String ruleString = null;
        if (KerberosName.rules != null) {
            final StringBuilder sb = new StringBuilder();
            for (final Rule rule : KerberosName.rules) {
                sb.append(rule.toString()).append("\n");
            }
            ruleString = sb.toString().trim();
        }
        return ruleString;
    }
    
    public static boolean hasRulesBeenSet() {
        return KerberosName.rules != null;
    }
    
    public static boolean hasRuleMechanismBeenSet() {
        return KerberosName.ruleMechanism != null;
    }
    
    public static void setRules(final String ruleString) {
        KerberosName.rules = ((ruleString != null) ? parseRules(ruleString) : null);
    }
    
    public static void setRuleMechanism(final String ruleMech) {
        if (ruleMech != null && !ruleMech.equalsIgnoreCase("hadoop") && !ruleMech.equalsIgnoreCase("mit")) {
            throw new IllegalArgumentException("Invalid rule mechanism: " + ruleMech);
        }
        KerberosName.ruleMechanism = ruleMech;
    }
    
    public static String getRuleMechanism() {
        return KerberosName.ruleMechanism;
    }
    
    static void printRules() throws IOException {
        int i = 0;
        for (final Rule r : KerberosName.rules) {
            System.out.println(++i + " " + r);
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(KerberosName.class);
        nameParser = Pattern.compile("([^/@]+)(/([^/@]+))?(@([^/@]+))?");
        KerberosName.parameterPattern = Pattern.compile("([^$]*)(\\$(\\d*))?");
        ruleParser = Pattern.compile("\\s*((DEFAULT)|(RULE:\\[(\\d*):([^\\]]*)](\\(([^)]*)\\))?(s/([^/]*)/([^/]*)/(g)?)?))/?(L)?");
        nonSimplePattern = Pattern.compile("[/@]");
        KerberosName.ruleMechanism = null;
        KerberosName.defaultRealm = null;
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
        private final boolean toLowerCase;
        
        Rule() {
            this.isDefault = true;
            this.numOfComponents = 0;
            this.format = null;
            this.match = null;
            this.fromPattern = null;
            this.toPattern = null;
            this.repeat = false;
            this.toLowerCase = false;
        }
        
        Rule(final int numOfComponents, final String format, final String match, final String fromPattern, final String toPattern, final boolean repeat, final boolean toLowerCase) {
            this.isDefault = false;
            this.numOfComponents = numOfComponents;
            this.format = format;
            this.match = ((match == null) ? null : Pattern.compile(match));
            this.fromPattern = ((fromPattern == null) ? null : Pattern.compile(fromPattern));
            this.toPattern = toPattern;
            this.repeat = repeat;
            this.toLowerCase = toLowerCase;
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
                if (this.toLowerCase) {
                    buf.append("/L");
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
        
        String apply(final String[] params, final String ruleMechanism) throws IOException {
            String result = null;
            if (this.isDefault) {
                if (KerberosName.getDefaultRealm().equals(params[0])) {
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
            if (result != null && KerberosName.nonSimplePattern.matcher(result).find() && ruleMechanism.equalsIgnoreCase("hadoop")) {
                throw new NoMatchingRule("Non-simple name " + result + " after auth_to_local rule " + this);
            }
            if (this.toLowerCase && result != null) {
                result = result.toLowerCase(Locale.ENGLISH);
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
