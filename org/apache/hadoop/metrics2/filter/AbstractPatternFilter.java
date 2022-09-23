// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.filter;

import java.util.Iterator;
import org.apache.hadoop.metrics2.MetricsTag;
import com.google.re2j.Matcher;
import org.apache.hadoop.metrics2.MetricsException;
import org.apache.commons.configuration2.SubsetConfiguration;
import com.google.common.collect.Maps;
import java.util.Map;
import com.google.re2j.Pattern;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.metrics2.MetricsFilter;

@InterfaceAudience.Private
public abstract class AbstractPatternFilter extends MetricsFilter
{
    protected static final String INCLUDE_KEY = "include";
    protected static final String EXCLUDE_KEY = "exclude";
    protected static final String INCLUDE_TAGS_KEY = "include.tags";
    protected static final String EXCLUDE_TAGS_KEY = "exclude.tags";
    private Pattern includePattern;
    private Pattern excludePattern;
    private final Map<String, Pattern> includeTagPatterns;
    private final Map<String, Pattern> excludeTagPatterns;
    private final Pattern tagPattern;
    
    AbstractPatternFilter() {
        this.tagPattern = Pattern.compile("^(\\w+):(.*)");
        this.includeTagPatterns = (Map<String, Pattern>)Maps.newHashMap();
        this.excludeTagPatterns = (Map<String, Pattern>)Maps.newHashMap();
    }
    
    @Override
    public void init(final SubsetConfiguration conf) {
        String patternString = conf.getString("include");
        if (patternString != null && !patternString.isEmpty()) {
            this.setIncludePattern(this.compile(patternString));
        }
        patternString = conf.getString("exclude");
        if (patternString != null && !patternString.isEmpty()) {
            this.setExcludePattern(this.compile(patternString));
        }
        String[] patternStrings = conf.getStringArray("include.tags");
        if (patternStrings != null && patternStrings.length != 0) {
            for (final String pstr : patternStrings) {
                final Matcher matcher = this.tagPattern.matcher(pstr);
                if (!matcher.matches()) {
                    throw new MetricsException("Illegal tag pattern: " + pstr);
                }
                this.setIncludeTagPattern(matcher.group(1), this.compile(matcher.group(2)));
            }
        }
        patternStrings = conf.getStringArray("exclude.tags");
        if (patternStrings != null && patternStrings.length != 0) {
            for (final String pstr : patternStrings) {
                final Matcher matcher = this.tagPattern.matcher(pstr);
                if (!matcher.matches()) {
                    throw new MetricsException("Illegal tag pattern: " + pstr);
                }
                this.setExcludeTagPattern(matcher.group(1), this.compile(matcher.group(2)));
            }
        }
    }
    
    void setIncludePattern(final Pattern includePattern) {
        this.includePattern = includePattern;
    }
    
    void setExcludePattern(final Pattern excludePattern) {
        this.excludePattern = excludePattern;
    }
    
    void setIncludeTagPattern(final String name, final Pattern pattern) {
        this.includeTagPatterns.put(name, pattern);
    }
    
    void setExcludeTagPattern(final String name, final Pattern pattern) {
        this.excludeTagPatterns.put(name, pattern);
    }
    
    @Override
    public boolean accepts(final MetricsTag tag) {
        final Pattern ipat = this.includeTagPatterns.get(tag.name());
        if (ipat != null && ipat.matcher(tag.value()).matches()) {
            return true;
        }
        final Pattern epat = this.excludeTagPatterns.get(tag.name());
        return (epat == null || !epat.matcher(tag.value()).matches()) && (this.includeTagPatterns.isEmpty() || !this.excludeTagPatterns.isEmpty());
    }
    
    @Override
    public boolean accepts(final Iterable<MetricsTag> tags) {
        for (final MetricsTag t : tags) {
            final Pattern pat = this.includeTagPatterns.get(t.name());
            if (pat != null && pat.matcher(t.value()).matches()) {
                return true;
            }
        }
        for (final MetricsTag t : tags) {
            final Pattern pat = this.excludeTagPatterns.get(t.name());
            if (pat != null && pat.matcher(t.value()).matches()) {
                return false;
            }
        }
        return this.includeTagPatterns.isEmpty() || !this.excludeTagPatterns.isEmpty();
    }
    
    @Override
    public boolean accepts(final String name) {
        return (this.includePattern != null && this.includePattern.matcher(name).matches()) || ((this.excludePattern == null || !this.excludePattern.matcher(name).matches()) && (this.includePattern == null || this.excludePattern != null));
    }
    
    protected abstract Pattern compile(final String p0);
}
