// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.rolling;

import org.apache.log4j.pattern.IntegerPatternConverter;
import org.apache.log4j.pattern.DatePatternConverter;
import java.util.List;
import java.util.Map;
import org.apache.log4j.pattern.ExtrasPatternParser;
import java.util.ArrayList;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.pattern.ExtrasFormattingInfo;
import org.apache.log4j.pattern.PatternConverter;
import org.apache.log4j.spi.OptionHandler;

public abstract class RollingPolicyBase implements RollingPolicy, OptionHandler
{
    private static final String FNP_NOT_SET = "The FileNamePattern option must be set before using RollingPolicy. ";
    private static final String SEE_FNP_NOT_SET = "See also http://logging.apache.org/log4j/codes.html#tbr_fnp_not_set";
    private PatternConverter[] patternConverters;
    private ExtrasFormattingInfo[] patternFields;
    private String fileNamePatternStr;
    protected String activeFileName;
    
    public void activateOptions() {
        if (this.fileNamePatternStr != null) {
            this.parseFileNamePattern();
            return;
        }
        LogLog.warn("The FileNamePattern option must be set before using RollingPolicy. ");
        LogLog.warn("See also http://logging.apache.org/log4j/codes.html#tbr_fnp_not_set");
        throw new IllegalStateException("The FileNamePattern option must be set before using RollingPolicy. See also http://logging.apache.org/log4j/codes.html#tbr_fnp_not_set");
    }
    
    public void setFileNamePattern(final String fnp) {
        this.fileNamePatternStr = fnp;
    }
    
    public String getFileNamePattern() {
        return this.fileNamePatternStr;
    }
    
    public void setActiveFileName(final String afn) {
        this.activeFileName = afn;
    }
    
    public String getActiveFileName() {
        return this.activeFileName;
    }
    
    protected final void parseFileNamePattern() {
        final List converters = new ArrayList();
        final List fields = new ArrayList();
        ExtrasPatternParser.parse(this.fileNamePatternStr, converters, fields, null, ExtrasPatternParser.getFileNamePatternRules());
        this.patternConverters = new PatternConverter[converters.size()];
        this.patternConverters = converters.toArray(this.patternConverters);
        this.patternFields = new ExtrasFormattingInfo[converters.size()];
        this.patternFields = fields.toArray(this.patternFields);
    }
    
    protected final void formatFileName(final Object obj, final StringBuffer buf) {
        for (int i = 0; i < this.patternConverters.length; ++i) {
            final int fieldStart = buf.length();
            this.patternConverters[i].format(obj, buf);
            if (this.patternFields[i] != null) {
                this.patternFields[i].format(fieldStart, buf);
            }
        }
    }
    
    protected final PatternConverter getDatePatternConverter() {
        for (int i = 0; i < this.patternConverters.length; ++i) {
            if (this.patternConverters[i] instanceof DatePatternConverter) {
                return this.patternConverters[i];
            }
        }
        return null;
    }
    
    protected final PatternConverter getIntegerPatternConverter() {
        for (int i = 0; i < this.patternConverters.length; ++i) {
            if (this.patternConverters[i] instanceof IntegerPatternConverter) {
                return this.patternConverters[i];
            }
        }
        return null;
    }
}
