// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive;

import org.apache.commons.lang.builder.HashCodeBuilder;
import java.util.Arrays;
import org.apache.commons.lang.ObjectUtils;
import org.apache.hadoop.io.Text;
import java.util.List;

public class LazyObjectInspectorParametersImpl implements LazyObjectInspectorParameters
{
    protected boolean escaped;
    protected byte escapeChar;
    protected boolean extendedBooleanLiteral;
    protected List<String> timestampFormats;
    protected byte[] separators;
    protected Text nullSequence;
    protected boolean lastColumnTakesRest;
    
    public LazyObjectInspectorParametersImpl() {
        this.escaped = false;
        this.extendedBooleanLiteral = false;
        this.timestampFormats = null;
    }
    
    public LazyObjectInspectorParametersImpl(final boolean escaped, final byte escapeChar, final boolean extendedBooleanLiteral, final List<String> timestampFormats, final byte[] separators, final Text nullSequence) {
        this.escaped = escaped;
        this.escapeChar = escapeChar;
        this.extendedBooleanLiteral = extendedBooleanLiteral;
        this.timestampFormats = timestampFormats;
        this.separators = separators;
        this.nullSequence = nullSequence;
        this.lastColumnTakesRest = false;
    }
    
    public LazyObjectInspectorParametersImpl(final boolean escaped, final byte escapeChar, final boolean extendedBooleanLiteral, final List<String> timestampFormats, final byte[] separators, final Text nullSequence, final boolean lastColumnTakesRest) {
        this.escaped = escaped;
        this.escapeChar = escapeChar;
        this.extendedBooleanLiteral = extendedBooleanLiteral;
        this.timestampFormats = timestampFormats;
        this.separators = separators;
        this.nullSequence = nullSequence;
        this.lastColumnTakesRest = lastColumnTakesRest;
    }
    
    public LazyObjectInspectorParametersImpl(final LazyObjectInspectorParameters lazyParams) {
        this.escaped = lazyParams.isEscaped();
        this.escapeChar = lazyParams.getEscapeChar();
        this.extendedBooleanLiteral = lazyParams.isExtendedBooleanLiteral();
        this.timestampFormats = lazyParams.getTimestampFormats();
        this.separators = lazyParams.getSeparators();
        this.nullSequence = lazyParams.getNullSequence();
        this.lastColumnTakesRest = lazyParams.isLastColumnTakesRest();
    }
    
    @Override
    public boolean isEscaped() {
        return this.escaped;
    }
    
    @Override
    public byte getEscapeChar() {
        return this.escapeChar;
    }
    
    @Override
    public boolean isExtendedBooleanLiteral() {
        return this.extendedBooleanLiteral;
    }
    
    @Override
    public List<String> getTimestampFormats() {
        return this.timestampFormats;
    }
    
    @Override
    public byte[] getSeparators() {
        return this.separators;
    }
    
    @Override
    public Text getNullSequence() {
        return this.nullSequence;
    }
    
    @Override
    public boolean isLastColumnTakesRest() {
        return this.lastColumnTakesRest;
    }
    
    protected boolean equals(final LazyObjectInspectorParametersImpl other) {
        return this.escaped == other.escaped && this.escapeChar == other.escapeChar && this.extendedBooleanLiteral == other.extendedBooleanLiteral && this.lastColumnTakesRest == other.lastColumnTakesRest && ObjectUtils.equals(this.nullSequence, other.nullSequence) && Arrays.equals(this.separators, other.separators) && ObjectUtils.equals(this.timestampFormats, other.timestampFormats);
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this == obj || (obj instanceof LazyObjectInspectorParametersImpl && this.equals((LazyObjectInspectorParametersImpl)obj));
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.escaped).append(this.escapeChar).append(this.extendedBooleanLiteral).append(this.timestampFormats).append(this.lastColumnTakesRest).append(this.nullSequence).append(this.separators).toHashCode();
    }
}
