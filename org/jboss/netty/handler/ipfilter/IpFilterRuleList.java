// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.ipfilter;

import org.jboss.netty.logging.InternalLoggerFactory;
import java.net.UnknownHostException;
import org.jboss.netty.util.internal.StringUtil;
import org.jboss.netty.logging.InternalLogger;
import java.util.ArrayList;

public class IpFilterRuleList extends ArrayList<IpFilterRule>
{
    private static final long serialVersionUID = -6164162941749588780L;
    private static final InternalLogger logger;
    
    public IpFilterRuleList(final String rules) {
        this.parseRules(rules);
    }
    
    private void parseRules(final String rules) {
        final String[] arr$;
        final String[] ruless = arr$ = StringUtil.split(rules, ',');
        for (final String rule : arr$) {
            this.parseRule(rule.trim());
        }
    }
    
    private void parseRule(final String rule) {
        if (rule == null || rule.length() == 0) {
            return;
        }
        if (!rule.startsWith("+") && !rule.startsWith("-")) {
            if (IpFilterRuleList.logger.isErrorEnabled()) {
                IpFilterRuleList.logger.error("syntax error in ip filter rule:" + rule);
            }
            return;
        }
        final boolean allow = rule.startsWith("+");
        if (rule.charAt(1) == 'n' || rule.charAt(1) == 'i') {
            ((ArrayList<PatternRule>)this).add(new PatternRule(allow, rule.substring(1)));
        }
        else if (rule.charAt(1) == 'c') {
            try {
                ((ArrayList<IpSubnetFilterRule>)this).add(new IpSubnetFilterRule(allow, rule.substring(3)));
            }
            catch (UnknownHostException e) {
                if (IpFilterRuleList.logger.isErrorEnabled()) {
                    IpFilterRuleList.logger.error("error parsing ip filter " + rule, e);
                }
            }
        }
        else if (IpFilterRuleList.logger.isErrorEnabled()) {
            IpFilterRuleList.logger.error("syntax error in ip filter rule:" + rule);
        }
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(IpFilterRuleList.class);
    }
}
