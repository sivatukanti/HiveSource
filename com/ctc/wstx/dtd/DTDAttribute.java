// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.dtd;

import java.util.Map;
import com.ctc.wstx.ent.EntityDecl;
import com.ctc.wstx.util.WordResolver;
import com.ctc.wstx.io.WstxInputData;
import com.ctc.wstx.util.StringUtil;
import com.ctc.wstx.sr.InputProblemReporter;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.validation.XMLValidator;
import org.codehaus.stax2.validation.ValidationContext;
import com.ctc.wstx.util.PrefixedName;

public abstract class DTDAttribute
{
    static final char CHAR_SPACE = ' ';
    public static final int TYPE_CDATA = 0;
    public static final int TYPE_ENUMERATED = 1;
    public static final int TYPE_ID = 2;
    public static final int TYPE_IDREF = 3;
    public static final int TYPE_IDREFS = 4;
    public static final int TYPE_ENTITY = 5;
    public static final int TYPE_ENTITIES = 6;
    public static final int TYPE_NOTATION = 7;
    public static final int TYPE_NMTOKEN = 8;
    public static final int TYPE_NMTOKENS = 9;
    static final String[] sTypes;
    protected final PrefixedName mName;
    protected final int mSpecialIndex;
    protected final DefaultAttrValue mDefValue;
    protected final boolean mCfgNsAware;
    protected final boolean mCfgXml11;
    
    public DTDAttribute(final PrefixedName name, final DefaultAttrValue defValue, final int specIndex, final boolean nsAware, final boolean xml11) {
        this.mName = name;
        this.mDefValue = defValue;
        this.mSpecialIndex = specIndex;
        this.mCfgNsAware = nsAware;
        this.mCfgXml11 = xml11;
    }
    
    public abstract DTDAttribute cloneWith(final int p0);
    
    public final PrefixedName getName() {
        return this.mName;
    }
    
    @Override
    public final String toString() {
        return this.mName.toString();
    }
    
    public final String getDefaultValue(final ValidationContext ctxt, final XMLValidator dtd) throws XMLStreamException {
        String val = this.mDefValue.getValueIfOk();
        if (val == null) {
            this.mDefValue.reportUndeclared(ctxt, dtd);
            val = this.mDefValue.getValue();
        }
        return val;
    }
    
    public final int getSpecialIndex() {
        return this.mSpecialIndex;
    }
    
    public final boolean needsValidation() {
        return this.getValueType() != 0;
    }
    
    public final boolean isFixed() {
        return this.mDefValue.isFixed();
    }
    
    public final boolean isRequired() {
        return this.mDefValue.isRequired();
    }
    
    public final boolean isSpecial() {
        return this.mDefValue.isSpecial();
    }
    
    public final boolean hasDefaultValue() {
        return this.mDefValue.hasDefaultValue();
    }
    
    public int getValueType() {
        return 0;
    }
    
    public String getValueTypeString() {
        return DTDAttribute.sTypes[this.getValueType()];
    }
    
    public boolean typeIsId() {
        return false;
    }
    
    public boolean typeIsNotation() {
        return false;
    }
    
    public abstract String validate(final DTDValidatorBase p0, final char[] p1, final int p2, final int p3, final boolean p4) throws XMLStreamException;
    
    public String validate(final DTDValidatorBase v, final String value, final boolean normalize) throws XMLStreamException {
        final int len = value.length();
        final char[] cbuf = v.getTempAttrValueBuffer(value.length());
        if (len > 0) {
            value.getChars(0, len, cbuf, 0);
        }
        return this.validate(v, cbuf, 0, len, normalize);
    }
    
    public abstract void validateDefault(final InputProblemReporter p0, final boolean p1) throws XMLStreamException;
    
    public String normalize(final DTDValidatorBase v, final char[] cbuf, final int start, final int end) {
        return StringUtil.normalizeSpaces(cbuf, start, end);
    }
    
    public void normalizeDefault() {
        final String val = this.mDefValue.getValue();
        if (val.length() > 0) {
            final char[] cbuf = val.toCharArray();
            final String str = StringUtil.normalizeSpaces(cbuf, 0, cbuf.length);
            if (str != null) {
                this.mDefValue.setValue(str);
            }
        }
    }
    
    protected String validateDefaultName(final InputProblemReporter rep, final boolean normalize) throws XMLStreamException {
        final String origDefValue = this.mDefValue.getValue();
        final String defValue = origDefValue.trim();
        if (defValue.length() == 0) {
            this.reportValidationProblem(rep, "Invalid default value '" + defValue + "'; empty String is not a valid name");
        }
        final int illegalIx = WstxInputData.findIllegalNameChar(defValue, this.mCfgNsAware, this.mCfgXml11);
        if (illegalIx >= 0) {
            if (illegalIx == 0) {
                this.reportValidationProblem(rep, "Invalid default value '" + defValue + "'; character " + WstxInputData.getCharDesc(defValue.charAt(0)) + ") not valid first character of a name");
            }
            else {
                this.reportValidationProblem(rep, "Invalid default value '" + defValue + "'; character #" + illegalIx + " (" + WstxInputData.getCharDesc(defValue.charAt(illegalIx)) + ") not valid name character");
            }
        }
        return normalize ? defValue : origDefValue;
    }
    
    protected String validateDefaultNames(final InputProblemReporter rep, final boolean normalize) throws XMLStreamException {
        final String defValue = this.mDefValue.getValue().trim();
        final int len = defValue.length();
        StringBuilder sb = null;
        int count = 0;
        int i;
    Label_0295:
        for (int start = 0; start < len; start = i + 1) {
            char c;
            for (c = defValue.charAt(start); WstxInputData.isSpaceChar(c); c = defValue.charAt(start)) {
                if (++start >= len) {
                    break Label_0295;
                }
            }
            for (i = start + 1; i < len && !WstxInputData.isSpaceChar(defValue.charAt(i)); ++i) {}
            final String token = defValue.substring(start, i);
            final int illegalIx = WstxInputData.findIllegalNameChar(token, this.mCfgNsAware, this.mCfgXml11);
            if (illegalIx >= 0) {
                if (illegalIx == 0) {
                    this.reportValidationProblem(rep, "Invalid default value '" + defValue + "'; character " + WstxInputData.getCharDesc(defValue.charAt(start)) + ") not valid first character of a name token");
                }
                else {
                    this.reportValidationProblem(rep, "Invalid default value '" + defValue + "'; character " + WstxInputData.getCharDesc(c) + ") not a valid name character");
                }
            }
            ++count;
            if (normalize) {
                if (sb == null) {
                    sb = new StringBuilder(i - start + 32);
                }
                else {
                    sb.append(' ');
                }
                sb.append(token);
            }
        }
        if (count == 0) {
            this.reportValidationProblem(rep, "Invalid default value '" + defValue + "'; empty String is not a valid name value");
        }
        return normalize ? sb.toString() : defValue;
    }
    
    protected String validateDefaultNmToken(final InputProblemReporter rep, final boolean normalize) throws XMLStreamException {
        final String origDefValue = this.mDefValue.getValue();
        final String defValue = origDefValue.trim();
        if (defValue.length() == 0) {
            this.reportValidationProblem(rep, "Invalid default value '" + defValue + "'; empty String is not a valid NMTOKEN");
        }
        final int illegalIx = WstxInputData.findIllegalNmtokenChar(defValue, this.mCfgNsAware, this.mCfgXml11);
        if (illegalIx >= 0) {
            this.reportValidationProblem(rep, "Invalid default value '" + defValue + "'; character #" + illegalIx + " (" + WstxInputData.getCharDesc(defValue.charAt(illegalIx)) + ") not valid NMTOKEN character");
        }
        return normalize ? defValue : origDefValue;
    }
    
    public String validateEnumValue(final char[] cbuf, int start, int end, final boolean normalize, final WordResolver res) {
        if (normalize) {
            while (start < end && cbuf[start] <= ' ') {
                ++start;
            }
            while (--end > start && cbuf[end] <= ' ') {}
            ++end;
        }
        if (start >= end) {
            return null;
        }
        return res.find(cbuf, start, end);
    }
    
    protected EntityDecl findEntityDecl(final DTDValidatorBase v, final char[] ch, final int start, final int len) throws XMLStreamException {
        final Map<String, EntityDecl> entMap = v.getEntityMap();
        final String id = new String(ch, start, len);
        final EntityDecl ent = entMap.get(id);
        if (ent == null) {
            this.reportValidationProblem(v, "Referenced entity '" + id + "' not defined");
        }
        else if (ent.isParsed()) {
            this.reportValidationProblem(v, "Referenced entity '" + id + "' is not an unparsed entity");
        }
        return ent;
    }
    
    protected void checkEntity(final InputProblemReporter rep, final String id, final EntityDecl ent) throws XMLStreamException {
        if (ent == null) {
            rep.reportValidationProblem("Referenced entity '" + id + "' not defined");
        }
        else if (ent.isParsed()) {
            rep.reportValidationProblem("Referenced entity '" + id + "' is not an unparsed entity");
        }
    }
    
    protected String reportInvalidChar(final DTDValidatorBase v, final char c, final String msg) throws XMLStreamException {
        this.reportValidationProblem(v, "Invalid character " + WstxInputData.getCharDesc(c) + ": " + msg);
        return null;
    }
    
    protected String reportValidationProblem(final DTDValidatorBase v, final String msg) throws XMLStreamException {
        v.reportValidationProblem("Attribute '" + this.mName + "': " + msg);
        return null;
    }
    
    protected String reportValidationProblem(final InputProblemReporter rep, final String msg) throws XMLStreamException {
        rep.reportValidationProblem("Attribute definition '" + this.mName + "': " + msg);
        return null;
    }
    
    static {
        sTypes = new String[] { "CDATA", "ENUMERATED", "ID", "IDREF", "IDREFS", "ENTITY", "ENTITIES", "NOTATION", "NMTOKEN", "NMTOKENS" };
    }
}
