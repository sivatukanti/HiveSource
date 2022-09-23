// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.sw;

import java.text.MessageFormat;
import com.ctc.wstx.sr.AttributeCollector;
import com.ctc.wstx.sr.InputElementStack;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import java.io.Writer;
import javax.xml.stream.XMLReporter;
import com.ctc.wstx.exc.WstxValidationException;
import org.codehaus.stax2.validation.XMLValidationProblem;
import javax.xml.stream.Location;
import javax.xml.namespace.QName;
import com.ctc.wstx.io.WstxInputLocation;
import org.codehaus.stax2.XMLStreamLocation2;
import org.codehaus.stax2.validation.ValidatorPair;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.DTDInfo;
import javax.xml.stream.XMLStreamReader;
import com.ctc.wstx.sr.StreamReaderImpl;
import org.codehaus.stax2.XMLStreamReader2;
import com.ctc.wstx.util.StringUtil;
import com.ctc.wstx.util.DataUtil;
import com.ctc.wstx.cfg.ErrorConsts;
import javax.xml.namespace.NamespaceContext;
import java.io.IOException;
import com.ctc.wstx.exc.WstxIOException;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.validation.ValidationProblemHandler;
import org.codehaus.stax2.validation.XMLValidator;
import com.ctc.wstx.api.WriterConfig;
import com.ctc.wstx.cfg.OutputConfigFlags;
import org.codehaus.stax2.validation.ValidationContext;
import org.codehaus.stax2.ri.Stax2WriterImpl;

public abstract class BaseStreamWriter extends Stax2WriterImpl implements ValidationContext, OutputConfigFlags
{
    protected static final int STATE_PROLOG = 1;
    protected static final int STATE_TREE = 2;
    protected static final int STATE_EPILOG = 3;
    protected static final char CHAR_SPACE = ' ';
    protected static final int MIN_ARRAYCOPY = 12;
    protected static final int ATTR_MIN_ARRAYCOPY = 12;
    protected static final int DEFAULT_COPYBUFFER_LEN = 512;
    protected final XmlWriter mWriter;
    protected char[] mCopyBuffer;
    protected final WriterConfig mConfig;
    protected final boolean mCfgCDataAsText;
    protected final boolean mCfgCopyDefaultAttrs;
    protected final boolean mCfgAutomaticEmptyElems;
    protected boolean mCheckStructure;
    protected boolean mCheckAttrs;
    protected String mEncoding;
    protected XMLValidator mValidator;
    protected boolean mXml11;
    protected ValidationProblemHandler mVldProbHandler;
    protected int mState;
    protected boolean mAnyOutput;
    protected boolean mStartElementOpen;
    protected boolean mEmptyElement;
    protected int mVldContent;
    protected String mDtdRootElem;
    protected boolean mReturnNullForDefaultNamespace;
    
    protected BaseStreamWriter(final XmlWriter xw, final String enc, final WriterConfig cfg) {
        this.mCopyBuffer = null;
        this.mValidator = null;
        this.mXml11 = false;
        this.mVldProbHandler = null;
        this.mState = 1;
        this.mAnyOutput = false;
        this.mStartElementOpen = false;
        this.mEmptyElement = false;
        this.mVldContent = 4;
        this.mDtdRootElem = null;
        this.mWriter = xw;
        this.mEncoding = enc;
        this.mConfig = cfg;
        final int flags = cfg.getConfigFlags();
        this.mCheckStructure = ((flags & 0x100) != 0x0);
        this.mCheckAttrs = ((flags & 0x800) != 0x0);
        this.mCfgAutomaticEmptyElems = ((flags & 0x4) != 0x0);
        this.mCfgCDataAsText = ((flags & 0x8) != 0x0);
        this.mCfgCopyDefaultAttrs = ((flags & 0x10) != 0x0);
        this.mReturnNullForDefaultNamespace = this.mConfig.returnNullForDefaultNamespace();
    }
    
    @Override
    public void close() throws XMLStreamException {
        this._finishDocument(false);
    }
    
    @Override
    public void flush() throws XMLStreamException {
        try {
            this.mWriter.flush();
        }
        catch (IOException ie) {
            throw new WstxIOException(ie);
        }
    }
    
    @Override
    public abstract NamespaceContext getNamespaceContext();
    
    @Override
    public abstract String getPrefix(final String p0);
    
    @Override
    public Object getProperty(final String name) {
        if (name.equals("com.ctc.wstx.outputUnderlyingStream")) {
            return this.mWriter.getOutputStream();
        }
        if (name.equals("com.ctc.wstx.outputUnderlyingWriter")) {
            return this.mWriter.getWriter();
        }
        return this.mConfig.getProperty(name);
    }
    
    @Override
    public abstract void setDefaultNamespace(final String p0) throws XMLStreamException;
    
    @Override
    public abstract void setNamespaceContext(final NamespaceContext p0) throws XMLStreamException;
    
    @Override
    public abstract void setPrefix(final String p0, final String p1) throws XMLStreamException;
    
    @Override
    public abstract void writeAttribute(final String p0, final String p1) throws XMLStreamException;
    
    @Override
    public abstract void writeAttribute(final String p0, final String p1, final String p2) throws XMLStreamException;
    
    @Override
    public abstract void writeAttribute(final String p0, final String p1, final String p2, final String p3) throws XMLStreamException;
    
    @Override
    public void writeCData(final String data) throws XMLStreamException {
        if (this.mCfgCDataAsText) {
            this.writeCharacters(data);
            return;
        }
        this.mAnyOutput = true;
        if (this.mStartElementOpen) {
            this.closeStartElement(this.mEmptyElement);
        }
        this.verifyWriteCData();
        if (this.mVldContent == 3 && this.mValidator != null) {
            this.mValidator.validateText(data, false);
        }
        int ix;
        try {
            ix = this.mWriter.writeCData(data);
        }
        catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
        if (ix >= 0) {
            reportNwfContent(ErrorConsts.WERR_CDATA_CONTENT, DataUtil.Integer(ix));
        }
    }
    
    @Override
    public void writeCharacters(final char[] text, final int start, final int len) throws XMLStreamException {
        this.mAnyOutput = true;
        if (this.mStartElementOpen) {
            this.closeStartElement(this.mEmptyElement);
        }
        if (this.mCheckStructure && this.inPrologOrEpilog() && !StringUtil.isAllWhitespace(text, start, len)) {
            reportNwfStructure(ErrorConsts.WERR_PROLOG_NONWS_TEXT);
        }
        if (this.mVldContent <= 1) {
            if (this.mVldContent == 0) {
                this.reportInvalidContent(4);
            }
            else if (!StringUtil.isAllWhitespace(text, start, len)) {
                this.reportInvalidContent(4);
            }
        }
        else if (this.mVldContent == 3 && this.mValidator != null) {
            this.mValidator.validateText(text, start, start + len, false);
        }
        if (len > 0) {
            try {
                if (this.inPrologOrEpilog()) {
                    this.mWriter.writeRaw(text, start, len);
                }
                else {
                    this.mWriter.writeCharacters(text, start, len);
                }
            }
            catch (IOException ioe) {
                throw new WstxIOException(ioe);
            }
        }
    }
    
    @Override
    public void writeCharacters(final String text) throws XMLStreamException {
        this.mAnyOutput = true;
        if (this.mStartElementOpen) {
            this.closeStartElement(this.mEmptyElement);
        }
        if (this.mCheckStructure && this.inPrologOrEpilog() && !StringUtil.isAllWhitespace(text)) {
            reportNwfStructure(ErrorConsts.WERR_PROLOG_NONWS_TEXT);
        }
        if (this.mVldContent <= 1) {
            if (this.mVldContent == 0) {
                this.reportInvalidContent(4);
            }
            else if (!StringUtil.isAllWhitespace(text)) {
                this.reportInvalidContent(4);
            }
        }
        else if (this.mVldContent == 3 && this.mValidator != null) {
            this.mValidator.validateText(text, false);
        }
        if (this.inPrologOrEpilog()) {
            try {
                this.mWriter.writeRaw(text);
            }
            catch (IOException ioe) {
                throw new WstxIOException(ioe);
            }
            return;
        }
        int len = text.length();
        if (len >= 12) {
            final char[] buf = this.getCopyBuffer();
            int offset = 0;
            while (len > 0) {
                final int thisLen = (len > buf.length) ? buf.length : len;
                text.getChars(offset, offset + thisLen, buf, 0);
                try {
                    this.mWriter.writeCharacters(buf, 0, thisLen);
                }
                catch (IOException ioe2) {
                    throw new WstxIOException(ioe2);
                }
                offset += thisLen;
                len -= thisLen;
            }
        }
        else {
            try {
                this.mWriter.writeCharacters(text);
            }
            catch (IOException ioe3) {
                throw new WstxIOException(ioe3);
            }
        }
    }
    
    @Override
    public void writeComment(final String data) throws XMLStreamException {
        this.mAnyOutput = true;
        if (this.mStartElementOpen) {
            this.closeStartElement(this.mEmptyElement);
        }
        if (this.mVldContent == 0) {
            this.reportInvalidContent(5);
        }
        int ix;
        try {
            ix = this.mWriter.writeComment(data);
        }
        catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
        if (ix >= 0) {
            reportNwfContent(ErrorConsts.WERR_COMMENT_CONTENT, DataUtil.Integer(ix));
        }
    }
    
    @Override
    public abstract void writeDefaultNamespace(final String p0) throws XMLStreamException;
    
    @Override
    public void writeDTD(final String dtd) throws XMLStreamException {
        this.verifyWriteDTD();
        this.mDtdRootElem = "";
        try {
            this.mWriter.writeDTD(dtd);
        }
        catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }
    
    @Override
    public abstract void writeEmptyElement(final String p0) throws XMLStreamException;
    
    @Override
    public abstract void writeEmptyElement(final String p0, final String p1) throws XMLStreamException;
    
    @Override
    public abstract void writeEmptyElement(final String p0, final String p1, final String p2) throws XMLStreamException;
    
    @Override
    public void writeEndDocument() throws XMLStreamException {
        this._finishDocument(false);
    }
    
    @Override
    public abstract void writeEndElement() throws XMLStreamException;
    
    @Override
    public void writeEntityRef(final String name) throws XMLStreamException {
        this.mAnyOutput = true;
        if (this.mStartElementOpen) {
            this.closeStartElement(this.mEmptyElement);
        }
        if (this.mCheckStructure && this.inPrologOrEpilog()) {
            reportNwfStructure("Trying to output an entity reference outside main element tree (in prolog or epilog)");
        }
        if (this.mVldContent == 0) {
            this.reportInvalidContent(9);
        }
        try {
            this.mWriter.writeEntityReference(name);
        }
        catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }
    
    @Override
    public abstract void writeNamespace(final String p0, final String p1) throws XMLStreamException;
    
    @Override
    public void writeProcessingInstruction(final String target) throws XMLStreamException {
        this.writeProcessingInstruction(target, null);
    }
    
    @Override
    public void writeProcessingInstruction(final String target, final String data) throws XMLStreamException {
        this.mAnyOutput = true;
        if (this.mStartElementOpen) {
            this.closeStartElement(this.mEmptyElement);
        }
        if (this.mVldContent == 0) {
            this.reportInvalidContent(3);
        }
        int ix;
        try {
            ix = this.mWriter.writePI(target, data);
        }
        catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
        if (ix >= 0) {
            throw new XMLStreamException("Illegal input: processing instruction content has embedded '?>' in it (index " + ix + ")");
        }
    }
    
    @Override
    public void writeStartDocument() throws XMLStreamException {
        if (this.mEncoding == null) {
            this.mEncoding = "UTF-8";
        }
        this.writeStartDocument(this.mEncoding, "1.0");
    }
    
    @Override
    public void writeStartDocument(final String version) throws XMLStreamException {
        this.writeStartDocument(this.mEncoding, version);
    }
    
    @Override
    public void writeStartDocument(final String encoding, final String version) throws XMLStreamException {
        this.doWriteStartDocument(version, encoding, null);
    }
    
    protected void doWriteStartDocument(String version, final String encoding, final String standAlone) throws XMLStreamException {
        if (this.mCheckStructure && this.mAnyOutput) {
            reportNwfStructure("Can not output XML declaration, after other output has already been done.");
        }
        this.mAnyOutput = true;
        if (this.mConfig.willValidateContent() && version != null && version.length() > 0 && !version.equals("1.0") && !version.equals("1.1")) {
            reportNwfContent("Illegal version argument ('" + version + "'); should only use '" + "1.0" + "' or '" + "1.1" + "'");
        }
        if (version == null || version.length() == 0) {
            version = "1.0";
        }
        this.mXml11 = "1.1".equals(version);
        if (this.mXml11) {
            this.mWriter.enableXml11();
        }
        if (encoding != null && encoding.length() > 0 && (this.mEncoding == null || this.mEncoding.length() == 0)) {
            this.mEncoding = encoding;
        }
        try {
            this.mWriter.writeXmlDeclaration(version, encoding, standAlone);
        }
        catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }
    
    @Override
    public abstract void writeStartElement(final String p0) throws XMLStreamException;
    
    @Override
    public abstract void writeStartElement(final String p0, final String p1) throws XMLStreamException;
    
    @Override
    public abstract void writeStartElement(final String p0, final String p1, final String p2) throws XMLStreamException;
    
    @Override
    public void copyEventFromReader(final XMLStreamReader2 sr, final boolean preserveEventData) throws XMLStreamException {
        try {
            switch (sr.getEventType()) {
                case 7: {
                    final String version = sr.getVersion();
                    if (version != null) {
                        if (version.length() != 0) {
                            if (sr.standaloneSet()) {
                                this.writeStartDocument(sr.getVersion(), sr.getCharacterEncodingScheme(), sr.isStandalone());
                            }
                            else {
                                this.writeStartDocument(sr.getCharacterEncodingScheme(), sr.getVersion());
                            }
                        }
                    }
                    return;
                }
                case 8: {
                    this.writeEndDocument();
                    return;
                }
                case 1: {
                    if (sr instanceof StreamReaderImpl) {
                        final StreamReaderImpl impl = (StreamReaderImpl)sr;
                        this.copyStartElement(impl.getInputElementStack(), impl.getAttributeCollector());
                    }
                    else {
                        super.copyStartElement(sr);
                    }
                    return;
                }
                case 2: {
                    this.writeEndElement();
                    return;
                }
                case 6: {
                    this.mAnyOutput = true;
                    if (this.mStartElementOpen) {
                        this.closeStartElement(this.mEmptyElement);
                    }
                    sr.getText(this.wrapAsRawWriter(), preserveEventData);
                    return;
                }
                case 12: {
                    if (!this.mCfgCDataAsText) {
                        this.mAnyOutput = true;
                        if (this.mStartElementOpen) {
                            this.closeStartElement(this.mEmptyElement);
                        }
                        if (this.mCheckStructure && this.inPrologOrEpilog()) {
                            reportNwfStructure(ErrorConsts.WERR_PROLOG_CDATA);
                        }
                        this.mWriter.writeCDataStart();
                        sr.getText(this.wrapAsRawWriter(), preserveEventData);
                        this.mWriter.writeCDataEnd();
                        return;
                    }
                }
                case 4: {
                    this.mAnyOutput = true;
                    if (this.mStartElementOpen) {
                        this.closeStartElement(this.mEmptyElement);
                    }
                    sr.getText(this.wrapAsTextWriter(), preserveEventData);
                    return;
                }
                case 5: {
                    this.mAnyOutput = true;
                    if (this.mStartElementOpen) {
                        this.closeStartElement(this.mEmptyElement);
                    }
                    this.mWriter.writeCommentStart();
                    sr.getText(this.wrapAsRawWriter(), preserveEventData);
                    this.mWriter.writeCommentEnd();
                    return;
                }
                case 3: {
                    this.mWriter.writePIStart(sr.getPITarget(), true);
                    sr.getText(this.wrapAsRawWriter(), preserveEventData);
                    this.mWriter.writePIEnd();
                    return;
                }
                case 11: {
                    final DTDInfo info = sr.getDTDInfo();
                    if (info == null) {
                        throwOutputError("Current state DOCTYPE, but not DTDInfo Object returned -- reader doesn't support DTDs?");
                    }
                    this.writeDTD(info);
                    return;
                }
                case 9: {
                    this.writeEntityRef(sr.getLocalName());
                    return;
                }
            }
        }
        catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
        throw new XMLStreamException("Unrecognized event type (" + sr.getEventType() + "); not sure how to copy");
    }
    
    @Override
    public void closeCompletely() throws XMLStreamException {
        this._finishDocument(true);
    }
    
    @Override
    public boolean isPropertySupported(final String name) {
        return this.mConfig.isPropertySupported(name);
    }
    
    @Override
    public boolean setProperty(final String name, final Object value) {
        return this.mConfig.setProperty(name, value);
    }
    
    @Override
    public XMLValidator validateAgainst(final XMLValidationSchema schema) throws XMLStreamException {
        final XMLValidator vld = schema.createValidator(this);
        if (this.mValidator == null) {
            this.mCheckStructure = true;
            this.mCheckAttrs = true;
            this.mValidator = vld;
        }
        else {
            this.mValidator = new ValidatorPair(this.mValidator, vld);
        }
        return vld;
    }
    
    @Override
    public XMLValidator stopValidatingAgainst(final XMLValidationSchema schema) throws XMLStreamException {
        final XMLValidator[] results = new XMLValidator[2];
        XMLValidator found = null;
        if (ValidatorPair.removeValidator(this.mValidator, schema, results)) {
            found = results[0];
            this.mValidator = results[1];
            found.validationCompleted(false);
            if (this.mValidator == null) {
                this.resetValidationFlags();
            }
        }
        return found;
    }
    
    @Override
    public XMLValidator stopValidatingAgainst(final XMLValidator validator) throws XMLStreamException {
        final XMLValidator[] results = new XMLValidator[2];
        XMLValidator found = null;
        if (ValidatorPair.removeValidator(this.mValidator, validator, results)) {
            found = results[0];
            this.mValidator = results[1];
            found.validationCompleted(false);
            if (this.mValidator == null) {
                this.resetValidationFlags();
            }
        }
        return found;
    }
    
    @Override
    public ValidationProblemHandler setValidationProblemHandler(final ValidationProblemHandler h) {
        final ValidationProblemHandler oldH = this.mVldProbHandler;
        this.mVldProbHandler = h;
        return oldH;
    }
    
    private void resetValidationFlags() {
        final int flags = this.mConfig.getConfigFlags();
        this.mCheckStructure = ((flags & 0x100) != 0x0);
        this.mCheckAttrs = ((flags & 0x800) != 0x0);
    }
    
    @Override
    public XMLStreamLocation2 getLocation() {
        return new WstxInputLocation(null, null, (String)null, this.mWriter.getAbsOffset(), this.mWriter.getRow(), this.mWriter.getColumn());
    }
    
    @Override
    public String getEncoding() {
        return this.mEncoding;
    }
    
    @Override
    public void writeCData(final char[] cbuf, final int start, final int len) throws XMLStreamException {
        if (this.mCfgCDataAsText) {
            this.writeCharacters(cbuf, start, len);
            return;
        }
        this.mAnyOutput = true;
        if (this.mStartElementOpen) {
            this.closeStartElement(this.mEmptyElement);
        }
        this.verifyWriteCData();
        if (this.mVldContent == 3 && this.mValidator != null) {
            this.mValidator.validateText(cbuf, start, start + len, false);
        }
        int ix;
        try {
            ix = this.mWriter.writeCData(cbuf, start, len);
        }
        catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
        if (ix >= 0) {
            throwOutputError(ErrorConsts.WERR_CDATA_CONTENT, DataUtil.Integer(ix));
        }
    }
    
    public void writeDTD(final DTDInfo info) throws XMLStreamException {
        this.writeDTD(info.getDTDRootName(), info.getDTDSystemId(), info.getDTDPublicId(), info.getDTDInternalSubset());
    }
    
    @Override
    public void writeDTD(final String rootName, final String systemId, final String publicId, final String internalSubset) throws XMLStreamException {
        this.verifyWriteDTD();
        this.mDtdRootElem = rootName;
        try {
            this.mWriter.writeDTD(rootName, systemId, publicId, internalSubset);
        }
        catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }
    
    @Override
    public abstract void writeFullEndElement() throws XMLStreamException;
    
    @Override
    public void writeStartDocument(final String version, final String encoding, final boolean standAlone) throws XMLStreamException {
        this.doWriteStartDocument(version, encoding, standAlone ? "yes" : "no");
    }
    
    @Override
    public void writeRaw(final String text) throws XMLStreamException {
        this.mAnyOutput = true;
        if (this.mStartElementOpen) {
            this.closeStartElement(this.mEmptyElement);
        }
        try {
            this.mWriter.writeRaw(text, 0, text.length());
        }
        catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }
    
    @Override
    public void writeRaw(final String text, final int start, final int offset) throws XMLStreamException {
        this.mAnyOutput = true;
        if (this.mStartElementOpen) {
            this.closeStartElement(this.mEmptyElement);
        }
        try {
            this.mWriter.writeRaw(text, start, offset);
        }
        catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }
    
    @Override
    public void writeRaw(final char[] text, final int start, final int offset) throws XMLStreamException {
        this.mAnyOutput = true;
        if (this.mStartElementOpen) {
            this.closeStartElement(this.mEmptyElement);
        }
        try {
            this.mWriter.writeRaw(text, start, offset);
        }
        catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }
    
    @Override
    public void writeSpace(final String text) throws XMLStreamException {
        this.writeRaw(text);
    }
    
    @Override
    public void writeSpace(final char[] text, final int offset, final int length) throws XMLStreamException {
        this.writeRaw(text, offset, length);
    }
    
    @Override
    public String getXmlVersion() {
        return this.mXml11 ? "1.1" : "1.0";
    }
    
    @Override
    public abstract QName getCurrentElementName();
    
    @Override
    public abstract String getNamespaceURI(final String p0);
    
    @Override
    public String getBaseUri() {
        return null;
    }
    
    @Override
    public Location getValidationLocation() {
        return this.getLocation();
    }
    
    @Override
    public void reportProblem(final XMLValidationProblem prob) throws XMLStreamException {
        if (this.mVldProbHandler != null) {
            this.mVldProbHandler.reportProblem(prob);
            return;
        }
        if (prob.getSeverity() > 2) {
            throw WstxValidationException.create(prob);
        }
        final XMLReporter rep = this.mConfig.getProblemReporter();
        if (rep != null) {
            this.doReportProblem(rep, prob);
        }
        else if (prob.getSeverity() >= 2) {
            throw WstxValidationException.create(prob);
        }
    }
    
    @Override
    public int addDefaultAttribute(final String localName, final String uri, final String prefix, final String value) {
        return -1;
    }
    
    @Override
    public boolean isNotationDeclared(final String name) {
        return false;
    }
    
    @Override
    public boolean isUnparsedEntityDeclared(final String name) {
        return false;
    }
    
    @Override
    public int getAttributeCount() {
        return 0;
    }
    
    @Override
    public String getAttributeLocalName(final int index) {
        return null;
    }
    
    @Override
    public String getAttributeNamespace(final int index) {
        return null;
    }
    
    @Override
    public String getAttributePrefix(final int index) {
        return null;
    }
    
    @Override
    public String getAttributeValue(final int index) {
        return null;
    }
    
    @Override
    public String getAttributeValue(final String nsURI, final String localName) {
        return null;
    }
    
    @Override
    public String getAttributeType(final int index) {
        return "";
    }
    
    @Override
    public int findAttributeIndex(final String nsURI, final String localName) {
        return -1;
    }
    
    public final Writer wrapAsRawWriter() {
        return this.mWriter.wrapAsRawWriter();
    }
    
    public final Writer wrapAsTextWriter() {
        return this.mWriter.wrapAsTextWriter();
    }
    
    protected boolean isValidating() {
        return this.mValidator != null;
    }
    
    public abstract void writeStartElement(final StartElement p0) throws XMLStreamException;
    
    public abstract void writeEndElement(final QName p0) throws XMLStreamException;
    
    public void writeCharacters(final Characters ch) throws XMLStreamException {
        if (this.mStartElementOpen) {
            this.closeStartElement(this.mEmptyElement);
        }
        if (this.mCheckStructure && this.inPrologOrEpilog() && !ch.isIgnorableWhiteSpace() && !ch.isWhiteSpace()) {
            reportNwfStructure(ErrorConsts.WERR_PROLOG_NONWS_TEXT);
        }
        if (this.mVldContent <= 1) {
            if (this.mVldContent == 0) {
                this.reportInvalidContent(4);
            }
            else if (!ch.isIgnorableWhiteSpace() && !ch.isWhiteSpace()) {
                this.reportInvalidContent(4);
            }
        }
        else if (this.mVldContent == 3 && this.mValidator != null) {
            this.mValidator.validateText(ch.getData(), false);
        }
        try {
            this.mWriter.writeCharacters(ch.getData());
        }
        catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }
    
    protected abstract void closeStartElement(final boolean p0) throws XMLStreamException;
    
    protected final boolean inPrologOrEpilog() {
        return this.mState != 2;
    }
    
    private final void _finishDocument(final boolean forceRealClose) throws XMLStreamException {
        if (this.mState != 3) {
            if (this.mCheckStructure && this.mState == 1) {
                reportNwfStructure("Trying to write END_DOCUMENT when document has no root (ie. trying to output empty document).");
            }
            if (this.mStartElementOpen) {
                this.closeStartElement(this.mEmptyElement);
            }
            if (this.mState != 3 && this.mConfig.automaticEndElementsEnabled()) {
                do {
                    this.writeEndElement();
                } while (this.mState != 3);
            }
        }
        final char[] buf = this.mCopyBuffer;
        if (buf != null) {
            this.mCopyBuffer = null;
            this.mConfig.freeMediumCBuffer(buf);
        }
        try {
            this.mWriter.close(forceRealClose);
        }
        catch (IOException ie) {
            throw new WstxIOException(ie);
        }
    }
    
    public abstract void copyStartElement(final InputElementStack p0, final AttributeCollector p1) throws IOException, XMLStreamException;
    
    public abstract String validateQNamePrefix(final QName p0) throws XMLStreamException;
    
    protected final void verifyWriteCData() throws XMLStreamException {
        if (this.mCheckStructure && this.inPrologOrEpilog()) {
            reportNwfStructure(ErrorConsts.WERR_PROLOG_CDATA);
        }
        if (this.mVldContent <= 1) {
            this.reportInvalidContent(12);
        }
    }
    
    protected final void verifyWriteDTD() throws XMLStreamException {
        if (this.mCheckStructure) {
            if (this.mState != 1) {
                throw new XMLStreamException("Can not write DOCTYPE declaration (DTD) when not in prolog any more (state " + this.mState + "; start element(s) written)");
            }
            if (this.mDtdRootElem != null) {
                throw new XMLStreamException("Trying to write multiple DOCTYPE declarations");
            }
        }
    }
    
    protected void verifyRootElement(final String localName, final String prefix) throws XMLStreamException {
        if (this.isValidating() && this.mDtdRootElem != null && this.mDtdRootElem.length() > 0) {
            String wrongElem = null;
            if (!localName.equals(this.mDtdRootElem)) {
                final int lnLen = localName.length();
                final int oldLen = this.mDtdRootElem.length();
                if (oldLen <= lnLen || !this.mDtdRootElem.endsWith(localName) || this.mDtdRootElem.charAt(oldLen - lnLen - 1) != ':') {
                    if (prefix == null) {
                        wrongElem = localName;
                    }
                    else if (prefix.length() == 0) {
                        wrongElem = "[unknown]:" + localName;
                    }
                    else {
                        wrongElem = prefix + ":" + localName;
                    }
                }
            }
            if (wrongElem != null) {
                this.reportValidationProblem(ErrorConsts.ERR_VLD_WRONG_ROOT, wrongElem, this.mDtdRootElem);
            }
        }
        this.mState = 2;
    }
    
    protected static void throwOutputError(final String msg) throws XMLStreamException {
        throw new XMLStreamException(msg);
    }
    
    protected static void throwOutputError(final String format, final Object arg) throws XMLStreamException {
        final String msg = MessageFormat.format(format, arg);
        throwOutputError(msg);
    }
    
    protected static void reportIllegalMethod(final String msg) throws XMLStreamException {
        throwOutputError(msg);
    }
    
    protected static void reportNwfStructure(final String msg) throws XMLStreamException {
        throwOutputError(msg);
    }
    
    protected static void reportNwfStructure(final String msg, final Object arg) throws XMLStreamException {
        throwOutputError(msg, arg);
    }
    
    protected static void reportNwfContent(final String msg) throws XMLStreamException {
        throwOutputError(msg);
    }
    
    protected static void reportNwfContent(final String msg, final Object arg) throws XMLStreamException {
        throwOutputError(msg, arg);
    }
    
    protected static void reportNwfAttr(final String msg) throws XMLStreamException {
        throwOutputError(msg);
    }
    
    protected static void reportNwfAttr(final String msg, final Object arg) throws XMLStreamException {
        throwOutputError(msg, arg);
    }
    
    protected static void throwFromIOE(final IOException ioe) throws XMLStreamException {
        throw new WstxIOException(ioe);
    }
    
    protected static void reportIllegalArg(final String msg) throws IllegalArgumentException {
        throw new IllegalArgumentException(msg);
    }
    
    protected void reportInvalidContent(final int evtType) throws XMLStreamException {
        switch (this.mVldContent) {
            case 0: {
                this.reportValidationProblem(ErrorConsts.ERR_VLD_EMPTY, this.getTopElementDesc(), ErrorConsts.tokenTypeDesc(evtType));
                break;
            }
            case 1: {
                this.reportValidationProblem(ErrorConsts.ERR_VLD_NON_MIXED, this.getTopElementDesc());
                break;
            }
            case 3:
            case 4: {
                this.reportValidationProblem(ErrorConsts.ERR_VLD_ANY, this.getTopElementDesc(), ErrorConsts.tokenTypeDesc(evtType));
                break;
            }
            default: {
                this.reportValidationProblem("Internal error: trying to report invalid content for " + evtType);
                break;
            }
        }
    }
    
    public void reportValidationProblem(final String msg, final Location loc, final int severity) throws XMLStreamException {
        this.reportProblem(new XMLValidationProblem(loc, msg, severity));
    }
    
    public void reportValidationProblem(final String msg, final int severity) throws XMLStreamException {
        this.reportProblem(new XMLValidationProblem(this.getValidationLocation(), msg, severity));
    }
    
    public void reportValidationProblem(final String msg) throws XMLStreamException {
        this.reportProblem(new XMLValidationProblem(this.getValidationLocation(), msg, 2));
    }
    
    public void reportValidationProblem(final Location loc, final String msg) throws XMLStreamException {
        this.reportProblem(new XMLValidationProblem(loc, msg));
    }
    
    public void reportValidationProblem(final String format, final Object arg) throws XMLStreamException {
        final String msg = MessageFormat.format(format, arg);
        this.reportProblem(new XMLValidationProblem(this.getValidationLocation(), msg));
    }
    
    public void reportValidationProblem(final String format, final Object arg, final Object arg2) throws XMLStreamException {
        final String msg = MessageFormat.format(format, arg, arg2);
        this.reportProblem(new XMLValidationProblem(this.getValidationLocation(), msg));
    }
    
    protected void doReportProblem(final XMLReporter rep, final String probType, final String msg, Location loc) throws XMLStreamException {
        if (loc == null) {
            loc = this.getLocation();
        }
        this.doReportProblem(rep, new XMLValidationProblem(loc, msg, 2, probType));
    }
    
    protected void doReportProblem(final XMLReporter rep, final XMLValidationProblem prob) throws XMLStreamException {
        if (rep != null) {
            Location loc = prob.getLocation();
            if (loc == null) {
                loc = this.getLocation();
                prob.setLocation(loc);
            }
            if (prob.getType() == null) {
                prob.setType(ErrorConsts.WT_VALIDATION);
            }
            rep.report(prob.getMessage(), prob.getType(), prob, loc);
        }
    }
    
    protected abstract String getTopElementDesc();
    
    protected final char[] getCopyBuffer() {
        char[] buf = this.mCopyBuffer;
        if (buf == null) {
            buf = (this.mCopyBuffer = this.mConfig.allocMediumCBuffer(512));
        }
        return buf;
    }
    
    protected final char[] getCopyBuffer(final int minLen) {
        char[] buf = this.mCopyBuffer;
        if (buf == null || minLen > buf.length) {
            buf = (this.mCopyBuffer = this.mConfig.allocMediumCBuffer(Math.max(512, minLen)));
        }
        return buf;
    }
    
    @Override
    public String toString() {
        return "[StreamWriter: " + this.getClass() + ", underlying outputter: " + ((this.mWriter == null) ? "NULL" : (this.mWriter.toString() + "]"));
    }
}
