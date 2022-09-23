// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.dtd;

import com.ctc.wstx.util.DataUtil;
import com.ctc.wstx.util.ExceptionUtil;
import org.codehaus.stax2.validation.XMLValidationProblem;
import java.text.MessageFormat;
import com.ctc.wstx.util.ElementIdMap;
import javax.xml.stream.Location;
import java.util.Iterator;
import com.ctc.wstx.sr.InputElementStack;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.validation.XMLValidationSchema;
import java.util.Collections;
import java.util.Map;
import org.codehaus.stax2.validation.ValidationContext;
import com.ctc.wstx.ent.EntityDecl;
import com.ctc.wstx.util.PrefixedName;
import java.util.HashMap;
import com.ctc.wstx.sr.NsDefaultProvider;
import org.codehaus.stax2.validation.XMLValidator;

public abstract class DTDValidatorBase extends XMLValidator implements NsDefaultProvider
{
    protected static final HashMap<PrefixedName, DTDAttribute> NO_ATTRS;
    static final int DEFAULT_STACK_SIZE = 16;
    static final int EXP_MAX_ATTRS = 16;
    protected static final HashMap<String, EntityDecl> EMPTY_MAP;
    final boolean mHasNsDefaults;
    final DTDSubset mSchema;
    final ValidationContext mContext;
    final Map<PrefixedName, DTDElement> mElemSpecs;
    final Map<String, EntityDecl> mGeneralEntities;
    protected boolean mNormAttrs;
    protected DTDElement mCurrElem;
    protected DTDElement[] mElems;
    protected int mElemCount;
    protected HashMap<PrefixedName, DTDAttribute> mCurrAttrDefs;
    protected DTDAttribute[] mAttrSpecs;
    protected int mAttrCount;
    protected int mIdAttrIndex;
    protected final transient PrefixedName mTmpKey;
    char[] mTmpAttrValueBuffer;
    
    public DTDValidatorBase(final DTDSubset schema, final ValidationContext ctxt, final boolean hasNsDefaults, final Map<PrefixedName, DTDElement> elemSpecs, final Map<String, EntityDecl> genEntities) {
        this.mCurrElem = null;
        this.mElems = null;
        this.mElemCount = 0;
        this.mCurrAttrDefs = null;
        this.mAttrSpecs = new DTDAttribute[16];
        this.mAttrCount = 0;
        this.mIdAttrIndex = -1;
        this.mTmpKey = new PrefixedName(null, null);
        this.mTmpAttrValueBuffer = null;
        this.mSchema = schema;
        this.mContext = ctxt;
        this.mHasNsDefaults = hasNsDefaults;
        if (elemSpecs == null || elemSpecs.size() == 0) {
            this.mElemSpecs = Collections.emptyMap();
        }
        else {
            this.mElemSpecs = elemSpecs;
        }
        this.mGeneralEntities = genEntities;
        this.mNormAttrs = true;
        this.mElems = new DTDElement[16];
    }
    
    public void setAttrValueNormalization(final boolean state) {
        this.mNormAttrs = state;
    }
    
    public abstract boolean reallyValidating();
    
    @Override
    public final XMLValidationSchema getSchema() {
        return this.mSchema;
    }
    
    @Override
    public abstract void validateElementStart(final String p0, final String p1, final String p2) throws XMLStreamException;
    
    @Override
    public abstract String validateAttribute(final String p0, final String p1, final String p2, final String p3) throws XMLStreamException;
    
    @Override
    public abstract String validateAttribute(final String p0, final String p1, final String p2, final char[] p3, final int p4, final int p5) throws XMLStreamException;
    
    @Override
    public abstract int validateElementAndAttributes() throws XMLStreamException;
    
    @Override
    public abstract int validateElementEnd(final String p0, final String p1, final String p2) throws XMLStreamException;
    
    @Override
    public void validateText(final String text, final boolean lastTextSegment) throws XMLStreamException {
    }
    
    @Override
    public void validateText(final char[] cbuf, final int textStart, final int textEnd, final boolean lastTextSegment) throws XMLStreamException {
    }
    
    @Override
    public abstract void validationCompleted(final boolean p0) throws XMLStreamException;
    
    @Override
    public String getAttributeType(final int index) {
        final DTDAttribute attr = this.mAttrSpecs[index];
        return (attr == null) ? "CDATA" : attr.getValueTypeString();
    }
    
    @Override
    public int getIdAttrIndex() {
        int ix = this.mIdAttrIndex;
        if (ix == -2) {
            ix = -1;
            if (this.mCurrElem != null) {
                final DTDAttribute idAttr = this.mCurrElem.getIdAttribute();
                if (idAttr != null) {
                    final DTDAttribute[] attrs = this.mAttrSpecs;
                    for (int i = 0, len = attrs.length; i < len; ++i) {
                        if (attrs[i] == idAttr) {
                            ix = i;
                            break;
                        }
                    }
                }
            }
            this.mIdAttrIndex = ix;
        }
        return ix;
    }
    
    @Override
    public int getNotationAttrIndex() {
        for (int i = 0, len = this.mAttrCount; i < len; ++i) {
            if (this.mAttrSpecs[i].typeIsNotation()) {
                return i;
            }
        }
        return -1;
    }
    
    @Override
    public boolean mayHaveNsDefaults(final String elemPrefix, final String elemLN) {
        this.mTmpKey.reset(elemPrefix, elemLN);
        final DTDElement elem = this.mElemSpecs.get(this.mTmpKey);
        this.mCurrElem = elem;
        return elem != null && elem.hasNsDefaults();
    }
    
    @Override
    public void checkNsDefaults(final InputElementStack nsStack) throws XMLStreamException {
        final HashMap<String, DTDAttribute> m = this.mCurrElem.getNsDefaults();
        if (m != null) {
            for (final Map.Entry<String, DTDAttribute> me : m.entrySet()) {
                final String prefix = me.getKey();
                if (!nsStack.isPrefixLocallyDeclared(prefix)) {
                    final DTDAttribute attr = me.getValue();
                    final String uri = attr.getDefaultValue(this.mContext, this);
                    nsStack.addNsBinding(prefix, uri);
                }
            }
        }
    }
    
    PrefixedName getElemName() {
        final DTDElement elem = this.mElems[this.mElemCount - 1];
        return elem.getName();
    }
    
    Location getLocation() {
        return this.mContext.getValidationLocation();
    }
    
    protected abstract ElementIdMap getIdMap();
    
    Map<String, EntityDecl> getEntityMap() {
        return this.mGeneralEntities;
    }
    
    char[] getTempAttrValueBuffer(final int neededLength) {
        if (this.mTmpAttrValueBuffer == null || this.mTmpAttrValueBuffer.length < neededLength) {
            final int size = (neededLength < 100) ? 100 : neededLength;
            this.mTmpAttrValueBuffer = new char[size];
        }
        return this.mTmpAttrValueBuffer;
    }
    
    public boolean hasNsDefaults() {
        return this.mHasNsDefaults;
    }
    
    void reportValidationProblem(final String msg) throws XMLStreamException {
        this.doReportValidationProblem(msg, null);
    }
    
    void reportValidationProblem(final String msg, final Location loc) throws XMLStreamException {
        this.doReportValidationProblem(msg, loc);
    }
    
    void reportValidationProblem(final String format, final Object arg) throws XMLStreamException {
        this.doReportValidationProblem(MessageFormat.format(format, arg), null);
    }
    
    void reportValidationProblem(final String format, final Object arg1, final Object arg2) throws XMLStreamException {
        this.doReportValidationProblem(MessageFormat.format(format, arg1, arg2), null);
    }
    
    protected void doReportValidationProblem(final String msg, Location loc) throws XMLStreamException {
        if (loc == null) {
            loc = this.getLocation();
        }
        final XMLValidationProblem prob = new XMLValidationProblem(loc, msg, 2);
        prob.setReporter(this);
        this.mContext.reportProblem(prob);
    }
    
    protected void doAddDefaultValue(final DTDAttribute attr) throws XMLStreamException {
        final String def = attr.getDefaultValue(this.mContext, this);
        if (def == null) {
            ExceptionUtil.throwInternal("null default attribute value");
        }
        final PrefixedName an = attr.getName();
        final String prefix = an.getPrefix();
        String uri = "";
        if (prefix != null && prefix.length() > 0) {
            uri = this.mContext.getNamespaceURI(prefix);
            if (uri == null || uri.length() == 0) {
                this.reportValidationProblem("Unbound namespace prefix \"{0}\" for default attribute \"{1}\"", prefix, attr);
                uri = "";
            }
        }
        final int defIx = this.mContext.addDefaultAttribute(an.getLocalName(), uri, prefix, def);
        if (defIx >= 0) {
            while (defIx >= this.mAttrSpecs.length) {
                this.mAttrSpecs = (DTDAttribute[])DataUtil.growArrayBy50Pct(this.mAttrSpecs);
            }
            while (this.mAttrCount < defIx) {
                this.mAttrSpecs[this.mAttrCount++] = null;
            }
            this.mAttrSpecs[defIx] = attr;
            this.mAttrCount = defIx + 1;
        }
    }
    
    static {
        NO_ATTRS = new HashMap<PrefixedName, DTDAttribute>();
        EMPTY_MAP = new HashMap<String, EntityDecl>();
    }
}
