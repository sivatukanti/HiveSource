// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security;

import org.slf4j.LoggerFactory;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import org.apache.hadoop.util.StringUtils;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "HDFS" })
@InterfaceStability.Evolving
public class RuleBasedLdapGroupsMapping extends LdapGroupsMapping
{
    public static final String CONVERSION_RULE_KEY = "hadoop.security.group.mapping.ldap.conversion.rule";
    private static final String CONVERSION_RULE_DEFAULT = "none";
    private static final Logger LOG;
    private Rule rule;
    
    @Override
    public synchronized void setConf(final Configuration conf) {
        super.setConf(conf);
        final String value = conf.get("hadoop.security.group.mapping.ldap.conversion.rule", "none");
        try {
            this.rule = Rule.valueOf(value.toUpperCase());
        }
        catch (IllegalArgumentException iae) {
            RuleBasedLdapGroupsMapping.LOG.warn("Invalid {} configured: '{}'. Using default value: '{}'", "hadoop.security.group.mapping.ldap.conversion.rule", value, "none");
        }
    }
    
    @Override
    public synchronized List<String> getGroups(final String user) {
        final List<String> groups = super.getGroups(user);
        switch (this.rule) {
            case TO_UPPER: {
                return groups.stream().map((Function<? super Object, ?>)StringUtils::toUpperCase).collect((Collector<? super Object, ?, List<String>>)Collectors.toList());
            }
            case TO_LOWER: {
                return groups.stream().map((Function<? super Object, ?>)StringUtils::toLowerCase).collect((Collector<? super Object, ?, List<String>>)Collectors.toList());
            }
            default: {
                return groups;
            }
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(RuleBasedLdapGroupsMapping.class);
    }
    
    private enum Rule
    {
        TO_UPPER, 
        TO_LOWER, 
        NONE;
    }
}
