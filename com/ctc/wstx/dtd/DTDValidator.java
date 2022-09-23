// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.dtd;

import com.ctc.wstx.util.ElementId;
import java.util.List;
import com.ctc.wstx.util.StringUtil;
import org.codehaus.stax2.validation.XMLValidator;
import javax.xml.stream.XMLStreamException;
import com.ctc.wstx.cfg.ErrorConsts;
import com.ctc.wstx.util.DataUtil;
import com.ctc.wstx.ent.EntityDecl;
import com.ctc.wstx.util.PrefixedName;
import java.util.Map;
import org.codehaus.stax2.validation.ValidationContext;
import java.util.BitSet;
import com.ctc.wstx.util.ElementIdMap;

public class DTDValidator extends DTDValidatorBase
{
    protected boolean mReportDuplicateErrors;
    protected ElementIdMap mIdMap;
    protected StructValidator[] mValidators;
    protected BitSet mCurrSpecialAttrs;
    boolean mCurrHasAnyFixed;
    BitSet mTmpSpecialAttrs;
    
    public DTDValidator(final DTDSubset schema, final ValidationContext ctxt, final boolean hasNsDefaults, final Map<PrefixedName, DTDElement> elemSpecs, final Map<String, EntityDecl> genEntities) {
        super(schema, ctxt, hasNsDefaults, elemSpecs, genEntities);
        this.mReportDuplicateErrors = false;
        this.mIdMap = null;
        this.mValidators = null;
        this.mCurrSpecialAttrs = null;
        this.mCurrHasAnyFixed = false;
        this.mValidators = new StructValidator[16];
    }
    
    @Override
    public final boolean reallyValidating() {
        return true;
    }
    
    @Override
    public void validateElementStart(final String localName, final String uri, final String prefix) throws XMLStreamException {
        this.mTmpKey.reset(prefix, localName);
        final DTDElement elem = this.mElemSpecs.get(this.mTmpKey);
        final int elemCount = this.mElemCount++;
        if (elemCount >= this.mElems.length) {
            this.mElems = (DTDElement[])DataUtil.growArrayBy50Pct(this.mElems);
            this.mValidators = (StructValidator[])DataUtil.growArrayBy50Pct(this.mValidators);
        }
        this.mElems[elemCount] = (this.mCurrElem = elem);
        if (elem == null || !elem.isDefined()) {
            this.reportValidationProblem(ErrorConsts.ERR_VLD_UNKNOWN_ELEM, this.mTmpKey.toString());
        }
        final StructValidator pv = (elemCount > 0) ? this.mValidators[elemCount - 1] : null;
        if (pv != null && elem != null) {
            String msg = pv.tryToValidate(elem.getName());
            if (msg != null) {
                final int ix = msg.indexOf("$END");
                final String pname = this.mElems[elemCount - 1].toString();
                if (ix >= 0) {
                    msg = msg.substring(0, ix) + "</" + pname + ">" + msg.substring(ix + 4);
                }
                this.reportValidationProblem("Validation error, encountered element <" + elem.getName() + "> as a child of <" + pname + ">: " + msg);
            }
        }
        this.mAttrCount = 0;
        this.mIdAttrIndex = -2;
        if (elem == null) {
            this.mValidators[elemCount] = null;
            this.mCurrAttrDefs = DTDValidator.NO_ATTRS;
            this.mCurrHasAnyFixed = false;
            this.mCurrSpecialAttrs = null;
        }
        else {
            this.mValidators[elemCount] = elem.getValidator();
            this.mCurrAttrDefs = elem.getAttributes();
            if (this.mCurrAttrDefs == null) {
                this.mCurrAttrDefs = DTDValidator.NO_ATTRS;
            }
            this.mCurrHasAnyFixed = elem.hasFixedAttrs();
            final int specCount = elem.getSpecialCount();
            if (specCount == 0) {
                this.mCurrSpecialAttrs = null;
            }
            else {
                BitSet bs = this.mTmpSpecialAttrs;
                if (bs == null) {
                    bs = (this.mTmpSpecialAttrs = new BitSet(specCount));
                }
                else {
                    bs.clear();
                }
                this.mCurrSpecialAttrs = bs;
            }
        }
    }
    
    @Override
    public String validateAttribute(final String localName, final String uri, final String prefix, final String value) throws XMLStreamException {
        final DTDAttribute attr = this.mCurrAttrDefs.get(this.mTmpKey.reset(prefix, localName));
        if (attr == null) {
            if (this.mCurrElem != null) {
                this.reportValidationProblem(ErrorConsts.ERR_VLD_UNKNOWN_ATTR, this.mCurrElem.toString(), this.mTmpKey.toString());
            }
            return value;
        }
        final int index = this.mAttrCount++;
        if (index >= this.mAttrSpecs.length) {
            this.mAttrSpecs = (DTDAttribute[])DataUtil.growArrayBy50Pct(this.mAttrSpecs);
        }
        this.mAttrSpecs[index] = attr;
        if (this.mCurrSpecialAttrs != null) {
            final int specIndex = attr.getSpecialIndex();
            if (specIndex >= 0) {
                this.mCurrSpecialAttrs.set(specIndex);
            }
        }
        final String result = attr.validate(this, value, this.mNormAttrs);
        if (this.mCurrHasAnyFixed && attr.isFixed()) {
            final String act = (result == null) ? value : result;
            final String exp = attr.getDefaultValue(this.mContext, this);
            if (!act.equals(exp)) {
                this.reportValidationProblem("Value of attribute \"" + attr + "\" (element <" + this.mCurrElem + ">) not \"" + exp + "\" as expected, but \"" + act + "\"");
            }
        }
        return result;
    }
    
    @Override
    public String validateAttribute(final String localName, final String uri, final String prefix, final char[] valueChars, final int valueStart, final int valueEnd) throws XMLStreamException {
        final DTDAttribute attr = this.mCurrAttrDefs.get(this.mTmpKey.reset(prefix, localName));
        if (attr == null) {
            if (this.mCurrElem != null) {
                this.reportValidationProblem(ErrorConsts.ERR_VLD_UNKNOWN_ATTR, this.mCurrElem.toString(), this.mTmpKey.toString());
            }
            return new String(valueChars, valueStart, valueEnd);
        }
        final int index = this.mAttrCount++;
        if (index >= this.mAttrSpecs.length) {
            this.mAttrSpecs = (DTDAttribute[])DataUtil.growArrayBy50Pct(this.mAttrSpecs);
        }
        this.mAttrSpecs[index] = attr;
        if (this.mCurrSpecialAttrs != null) {
            final int specIndex = attr.getSpecialIndex();
            if (specIndex >= 0) {
                this.mCurrSpecialAttrs.set(specIndex);
            }
        }
        final String result = attr.validate(this, valueChars, valueStart, valueEnd, this.mNormAttrs);
        if (this.mCurrHasAnyFixed && attr.isFixed()) {
            final String exp = attr.getDefaultValue(this.mContext, this);
            boolean match;
            if (result == null) {
                match = StringUtil.matches(exp, valueChars, valueStart, valueEnd - valueStart);
            }
            else {
                match = exp.equals(result);
            }
            if (!match) {
                final String act = (result == null) ? new String(valueChars, valueStart, valueEnd) : result;
                this.reportValidationProblem("Value of #FIXED attribute \"" + attr + "\" (element <" + this.mCurrElem + ">) not \"" + exp + "\" as expected, but \"" + act + "\"");
            }
        }
        return result;
    }
    
    @Override
    public int validateElementAndAttributes() throws XMLStreamException {
        final DTDElement elem = this.mCurrElem;
        if (elem == null) {
            return 4;
        }
        if (this.mCurrSpecialAttrs != null) {
            final BitSet specBits = this.mCurrSpecialAttrs;
            for (int specCount = elem.getSpecialCount(), ix = specBits.nextClearBit(0); ix < specCount; ix = specBits.nextClearBit(ix + 1)) {
                final List<DTDAttribute> specAttrs = elem.getSpecialAttrs();
                final DTDAttribute attr = specAttrs.get(ix);
                if (attr.isRequired()) {
                    this.reportValidationProblem("Required attribute \"{0}\" missing from element <{1}>", attr, elem);
                }
                else {
                    this.doAddDefaultValue(attr);
                }
            }
        }
        return elem.getAllowedContent();
    }
    
    @Override
    public int validateElementEnd(final String localName, final String uri, final String prefix) throws XMLStreamException {
        final int ix = this.mElemCount - 1;
        if (ix < 0) {
            return 1;
        }
        this.mElemCount = ix;
        final DTDElement closingElem = this.mElems[ix];
        this.mElems[ix] = null;
        final StructValidator v = this.mValidators[ix];
        this.mValidators[ix] = null;
        if (v != null) {
            final String msg = v.fullyValid();
            if (msg != null) {
                this.reportValidationProblem("Validation error, element </" + closingElem + ">: " + msg);
            }
        }
        if (ix < 1) {
            return 1;
        }
        return this.mElems[ix - 1].getAllowedContent();
    }
    
    @Override
    public void validationCompleted(final boolean eod) throws XMLStreamException {
        this.checkIdRefs();
    }
    
    @Override
    protected ElementIdMap getIdMap() {
        if (this.mIdMap == null) {
            this.mIdMap = new ElementIdMap();
        }
        return this.mIdMap;
    }
    
    protected void checkIdRefs() throws XMLStreamException {
        if (this.mIdMap != null) {
            final ElementId ref = this.mIdMap.getFirstUndefined();
            if (ref != null) {
                this.reportValidationProblem("Undefined id '" + ref.getId() + "': referenced from element <" + ref.getElemName() + ">, attribute '" + ref.getAttrName() + "'", ref.getLocation());
            }
        }
    }
}
