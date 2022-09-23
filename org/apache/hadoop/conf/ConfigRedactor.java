// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.conf;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.fs.CommonConfigurationKeys;
import java.util.regex.Pattern;
import java.util.List;

public class ConfigRedactor
{
    private static final String REDACTED_TEXT = "<redacted>";
    private List<Pattern> compiledPatterns;
    
    public ConfigRedactor(final Configuration conf) {
        final String sensitiveRegexList = conf.get("hadoop.security.sensitive-config-keys", CommonConfigurationKeys.HADOOP_SECURITY_SENSITIVE_CONFIG_KEYS_DEFAULT);
        final List<String> sensitiveRegexes = Arrays.asList(StringUtils.getTrimmedStrings(sensitiveRegexList));
        this.compiledPatterns = new ArrayList<Pattern>();
        for (final String regex : sensitiveRegexes) {
            final Pattern p = Pattern.compile(regex);
            this.compiledPatterns.add(p);
        }
    }
    
    public String redact(final String key, final String value) {
        if (this.configIsSensitive(key)) {
            return "<redacted>";
        }
        return value;
    }
    
    private boolean configIsSensitive(final String key) {
        for (final Pattern regex : this.compiledPatterns) {
            if (regex.matcher(key).find()) {
                return true;
            }
        }
        return false;
    }
}
