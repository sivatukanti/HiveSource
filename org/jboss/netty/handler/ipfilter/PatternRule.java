// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.ipfilter;

import org.jboss.netty.logging.InternalLoggerFactory;
import java.net.UnknownHostException;
import org.jboss.netty.util.internal.StringUtil;
import java.net.InetAddress;
import java.util.regex.Pattern;
import org.jboss.netty.logging.InternalLogger;

public class PatternRule implements IpFilterRule, Comparable<Object>
{
    private static final InternalLogger logger;
    private Pattern ipPattern;
    private Pattern namePattern;
    private boolean isAllowRule;
    private boolean localhost;
    private final String pattern;
    
    public PatternRule(final boolean allow, final String pattern) {
        this.isAllowRule = true;
        this.isAllowRule = allow;
        this.parse(this.pattern = pattern);
    }
    
    public String getPattern() {
        return this.pattern;
    }
    
    public boolean isAllowRule() {
        return this.isAllowRule;
    }
    
    public boolean isDenyRule() {
        return !this.isAllowRule;
    }
    
    public boolean contains(final InetAddress inetAddress) {
        return (this.localhost && isLocalhost(inetAddress)) || (this.ipPattern != null && this.ipPattern.matcher(inetAddress.getHostAddress()).matches()) || (this.namePattern != null && this.namePattern.matcher(inetAddress.getHostName()).matches());
    }
    
    private void parse(final String pattern) {
        if (pattern == null) {
            return;
        }
        final String[] acls = StringUtil.split(pattern, ',');
        String ip = "";
        String name = "";
        for (String c : acls) {
            c = c.trim();
            if ("n:localhost".equals(c)) {
                this.localhost = true;
            }
            else if (c.startsWith("n:")) {
                name = addRule(name, c.substring(2));
            }
            else if (c.startsWith("i:")) {
                ip = addRule(ip, c.substring(2));
            }
        }
        if (ip.length() != 0) {
            this.ipPattern = Pattern.compile(ip);
        }
        if (name.length() != 0) {
            this.namePattern = Pattern.compile(name);
        }
    }
    
    private static String addRule(String pattern, String rule) {
        if (rule == null || rule.length() == 0) {
            return pattern;
        }
        if (pattern.length() != 0) {
            pattern += "|";
        }
        rule = rule.replaceAll("\\.", "\\\\.");
        rule = rule.replaceAll("\\*", ".*");
        rule = rule.replaceAll("\\?", ".");
        pattern = pattern + '(' + rule + ')';
        return pattern;
    }
    
    private static boolean isLocalhost(final InetAddress address) {
        try {
            if (address.equals(InetAddress.getLocalHost())) {
                return true;
            }
        }
        catch (UnknownHostException e) {
            if (PatternRule.logger.isInfoEnabled()) {
                PatternRule.logger.info("error getting ip of localhost", e);
            }
        }
        try {
            final InetAddress[] arr$;
            final InetAddress[] addrs = arr$ = InetAddress.getAllByName("127.0.0.1");
            for (final InetAddress addr : arr$) {
                if (addr.equals(address)) {
                    return true;
                }
            }
        }
        catch (UnknownHostException e) {
            if (PatternRule.logger.isInfoEnabled()) {
                PatternRule.logger.info("error getting ip of localhost", e);
            }
        }
        return false;
    }
    
    public int compareTo(final Object o) {
        if (o == null) {
            return -1;
        }
        if (!(o instanceof PatternRule)) {
            return -1;
        }
        final PatternRule p = (PatternRule)o;
        if (p.isAllowRule() && !this.isAllowRule) {
            return -1;
        }
        if (this.pattern == null && p.pattern == null) {
            return 0;
        }
        if (this.pattern != null) {
            return this.pattern.compareTo(p.getPattern());
        }
        return -1;
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(PatternRule.class);
    }
}
