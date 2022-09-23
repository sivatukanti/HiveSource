// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.api;

import com.ctc.wstx.util.ArgUtil;
import com.ctc.wstx.ent.IntEntity;
import com.ctc.wstx.util.DataUtil;
import org.codehaus.stax2.validation.DTDValidationSchema;
import com.ctc.wstx.dtd.DTDEventListener;
import java.util.Iterator;
import java.util.Collections;
import com.ctc.wstx.ent.EntityDecl;
import java.util.Map;
import com.ctc.wstx.io.BufferRecycler;
import java.lang.ref.SoftReference;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLReporter;
import java.net.URL;
import com.ctc.wstx.util.SymbolTable;
import java.util.HashMap;
import com.ctc.wstx.cfg.InputConfigFlags;

public final class ReaderConfig extends CommonConfig implements InputConfigFlags
{
    public static final int DEFAULT_MAX_ATTRIBUTES_PER_ELEMENT = 1000;
    public static final int DEFAULT_MAX_ATTRIBUTE_LENGTH = 524288;
    public static final int DEFAULT_MAX_ELEMENT_DEPTH = 1000;
    public static final int DEFAULT_MAX_ENTITY_DEPTH = 500;
    public static final int DEFAULT_MAX_ENTITY_COUNT = 100000;
    static final int PROP_COALESCE_TEXT = 1;
    static final int PROP_NAMESPACE_AWARE = 2;
    static final int PROP_REPLACE_ENTITY_REFS = 3;
    static final int PROP_SUPPORT_EXTERNAL_ENTITIES = 4;
    static final int PROP_VALIDATE_AGAINST_DTD = 5;
    static final int PROP_SUPPORT_DTD = 6;
    public static final int PROP_EVENT_ALLOCATOR = 7;
    static final int PROP_WARNING_REPORTER = 8;
    static final int PROP_XML_RESOLVER = 9;
    static final int PROP_INTERN_NS_URIS = 20;
    static final int PROP_INTERN_NAMES = 21;
    static final int PROP_REPORT_CDATA = 22;
    static final int PROP_REPORT_PROLOG_WS = 23;
    static final int PROP_PRESERVE_LOCATION = 24;
    static final int PROP_AUTO_CLOSE_INPUT = 25;
    static final int PROP_SUPPORT_XMLID = 26;
    static final int PROP_DTD_OVERRIDE = 27;
    static final int PROP_NORMALIZE_LFS = 40;
    static final int PROP_CACHE_DTDS = 42;
    static final int PROP_CACHE_DTDS_BY_PUBLIC_ID = 43;
    static final int PROP_LAZY_PARSING = 44;
    static final int PROP_SUPPORT_DTDPP = 45;
    static final int PROP_TREAT_CHAR_REFS_AS_ENTS = 46;
    static final int PROP_INPUT_BUFFER_LENGTH = 50;
    static final int PROP_MIN_TEXT_SEGMENT = 52;
    static final int PROP_CUSTOM_INTERNAL_ENTITIES = 53;
    static final int PROP_DTD_RESOLVER = 54;
    static final int PROP_ENTITY_RESOLVER = 55;
    static final int PROP_UNDECLARED_ENTITY_RESOLVER = 56;
    static final int PROP_BASE_URL = 57;
    static final int PROP_INPUT_PARSING_MODE = 58;
    static final int PROP_MAX_ATTRIBUTES_PER_ELEMENT = 60;
    static final int PROP_MAX_CHILDREN_PER_ELEMENT = 61;
    static final int PROP_MAX_ELEMENT_COUNT = 62;
    static final int PROP_MAX_ELEMENT_DEPTH = 63;
    static final int PROP_MAX_CHARACTERS = 64;
    static final int PROP_MAX_ATTRIBUTE_SIZE = 65;
    static final int PROP_MAX_TEXT_LENGTH = 66;
    static final int PROP_MAX_ENTITY_COUNT = 67;
    static final int PROP_MAX_ENTITY_DEPTH = 68;
    static final int MIN_INPUT_BUFFER_LENGTH = 8;
    static final int DTD_CACHE_SIZE_J2SE = 12;
    static final int DTD_CACHE_SIZE_J2ME = 5;
    static final int DEFAULT_SHORTEST_TEXT_SEGMENT = 64;
    static final int DEFAULT_FLAGS_FULL = 2973213;
    static final int DEFAULT_FLAGS_J2ME = 2973213;
    static final HashMap<String, Integer> sProperties;
    protected final boolean mIsJ2MESubset;
    protected final SymbolTable mSymbols;
    protected int mConfigFlags;
    protected int mConfigFlagMods;
    static final int PROP_INTERN_NAMES_EXPLICIT = 26;
    static final int PROP_INTERN_NS_URIS_EXPLICIT = 27;
    protected int mInputBufferLen;
    protected int mMinTextSegmentLen;
    protected int mMaxAttributesPerElement;
    protected int mMaxAttributeSize;
    protected int mMaxChildrenPerElement;
    protected int mMaxElementDepth;
    protected long mMaxElementCount;
    protected long mMaxCharacters;
    protected int mMaxTextLength;
    protected int mMaxEntityDepth;
    protected long mMaxEntityCount;
    protected URL mBaseURL;
    protected WstxInputProperties.ParsingMode mParsingMode;
    protected boolean mXml11;
    XMLReporter mReporter;
    XMLResolver mDtdResolver;
    XMLResolver mEntityResolver;
    Object[] mSpecialProperties;
    private static final int SPEC_PROC_COUNT = 4;
    private static final int SP_IX_CUSTOM_ENTITIES = 0;
    private static final int SP_IX_UNDECL_ENT_RESOLVER = 1;
    private static final int SP_IX_DTD_EVENT_LISTENER = 2;
    private static final int SP_IX_DTD_OVERRIDE = 3;
    static final ThreadLocal<SoftReference<BufferRecycler>> mRecyclerRef;
    BufferRecycler mCurrRecycler;
    
    private ReaderConfig(final ReaderConfig base, final boolean j2meSubset, final SymbolTable symbols, final int configFlags, final int configFlagMods, final int inputBufLen, final int minTextSegmentLen) {
        super(base);
        this.mMaxAttributesPerElement = 1000;
        this.mMaxAttributeSize = 524288;
        this.mMaxChildrenPerElement = Integer.MAX_VALUE;
        this.mMaxElementDepth = 1000;
        this.mMaxElementCount = Long.MAX_VALUE;
        this.mMaxCharacters = Long.MAX_VALUE;
        this.mMaxTextLength = Integer.MAX_VALUE;
        this.mMaxEntityDepth = 500;
        this.mMaxEntityCount = 100000L;
        this.mParsingMode = WstxInputProperties.PARSING_MODE_DOCUMENT;
        this.mXml11 = false;
        this.mDtdResolver = null;
        this.mEntityResolver = null;
        this.mSpecialProperties = null;
        this.mCurrRecycler = null;
        this.mIsJ2MESubset = j2meSubset;
        this.mSymbols = symbols;
        this.mConfigFlags = configFlags;
        this.mConfigFlagMods = configFlagMods;
        this.mInputBufferLen = inputBufLen;
        this.mMinTextSegmentLen = minTextSegmentLen;
        if (base != null) {
            this.mMaxAttributesPerElement = base.mMaxAttributesPerElement;
            this.mMaxAttributeSize = base.mMaxAttributeSize;
            this.mMaxChildrenPerElement = base.mMaxChildrenPerElement;
            this.mMaxElementCount = base.mMaxElementCount;
            this.mMaxElementDepth = base.mMaxElementDepth;
            this.mMaxCharacters = base.mMaxCharacters;
            this.mMaxTextLength = base.mMaxTextLength;
            this.mMaxEntityDepth = base.mMaxEntityDepth;
            this.mMaxEntityCount = base.mMaxEntityCount;
        }
        final SoftReference<BufferRecycler> ref = ReaderConfig.mRecyclerRef.get();
        if (ref != null) {
            this.mCurrRecycler = ref.get();
        }
    }
    
    public static ReaderConfig createJ2MEDefaults() {
        final ReaderConfig rc = new ReaderConfig(null, true, null, 2973213, 0, 2000, 64);
        return rc;
    }
    
    public static ReaderConfig createFullDefaults() {
        final ReaderConfig rc = new ReaderConfig(null, false, null, 2973213, 0, 4000, 64);
        return rc;
    }
    
    public ReaderConfig createNonShared(final SymbolTable sym) {
        final ReaderConfig rc = new ReaderConfig(this, this.mIsJ2MESubset, sym, this.mConfigFlags, this.mConfigFlagMods, this.mInputBufferLen, this.mMinTextSegmentLen);
        rc.mReporter = this.mReporter;
        rc.mDtdResolver = this.mDtdResolver;
        rc.mEntityResolver = this.mEntityResolver;
        rc.mBaseURL = this.mBaseURL;
        rc.mParsingMode = this.mParsingMode;
        rc.mMaxAttributesPerElement = this.mMaxAttributesPerElement;
        rc.mMaxAttributeSize = this.mMaxAttributeSize;
        rc.mMaxChildrenPerElement = this.mMaxChildrenPerElement;
        rc.mMaxElementCount = this.mMaxElementCount;
        rc.mMaxCharacters = this.mMaxCharacters;
        rc.mMaxTextLength = this.mMaxTextLength;
        rc.mMaxElementDepth = this.mMaxElementDepth;
        rc.mMaxEntityDepth = this.mMaxEntityDepth;
        rc.mMaxEntityCount = this.mMaxEntityCount;
        if (this.mSpecialProperties != null) {
            final int len = this.mSpecialProperties.length;
            final Object[] specProps = new Object[len];
            System.arraycopy(this.mSpecialProperties, 0, specProps, 0, len);
            rc.mSpecialProperties = specProps;
        }
        return rc;
    }
    
    public void resetState() {
        this.mXml11 = false;
    }
    
    @Override
    protected int findPropertyId(final String propName) {
        final Integer I = ReaderConfig.sProperties.get(propName);
        return (I == null) ? -1 : I;
    }
    
    public SymbolTable getSymbols() {
        return this.mSymbols;
    }
    
    public int getDtdCacheSize() {
        return this.mIsJ2MESubset ? 5 : 12;
    }
    
    public int getConfigFlags() {
        return this.mConfigFlags;
    }
    
    public boolean willCoalesceText() {
        return this._hasConfigFlag(2);
    }
    
    public boolean willSupportNamespaces() {
        return this._hasConfigFlag(1);
    }
    
    public boolean willReplaceEntityRefs() {
        return this._hasConfigFlag(4);
    }
    
    public boolean willSupportExternalEntities() {
        return this._hasConfigFlag(8);
    }
    
    public boolean willSupportDTDs() {
        return this._hasConfigFlag(16);
    }
    
    public boolean willValidateWithDTD() {
        return this._hasConfigFlag(32);
    }
    
    public boolean willReportCData() {
        return this._hasConfigFlag(512);
    }
    
    public boolean willParseLazily() {
        return this._hasConfigFlag(262144);
    }
    
    public boolean willInternNames() {
        return this._hasConfigFlag(1024);
    }
    
    public boolean willInternNsURIs() {
        return this._hasConfigFlag(2048);
    }
    
    public boolean willPreserveLocation() {
        return this._hasConfigFlag(4096);
    }
    
    public boolean willAutoCloseInput() {
        return this._hasConfigFlag(8192);
    }
    
    public boolean willReportPrologWhitespace() {
        return this._hasConfigFlag(256);
    }
    
    public boolean willCacheDTDs() {
        return this._hasConfigFlag(65536);
    }
    
    public boolean willCacheDTDsByPublicId() {
        return this._hasConfigFlag(131072);
    }
    
    public boolean willDoXmlIdTyping() {
        return this._hasConfigFlag(2097152);
    }
    
    public boolean willDoXmlIdUniqChecks() {
        return this._hasConfigFlag(4194304);
    }
    
    public boolean willSupportDTDPP() {
        return this._hasConfigFlag(524288);
    }
    
    public boolean willNormalizeLFs() {
        return this._hasConfigFlag(16384);
    }
    
    public boolean willTreatCharRefsAsEnts() {
        return this._hasConfigFlag(8388608);
    }
    
    public int getInputBufferLength() {
        return this.mInputBufferLen;
    }
    
    public int getShortestReportedTextSegment() {
        return this.mMinTextSegmentLen;
    }
    
    public int getMaxAttributesPerElement() {
        return this.mMaxAttributesPerElement;
    }
    
    public int getMaxAttributeSize() {
        return this.mMaxAttributeSize;
    }
    
    public int getMaxChildrenPerElement() {
        return this.mMaxChildrenPerElement;
    }
    
    public int getMaxElementDepth() {
        return this.mMaxElementDepth;
    }
    
    public long getMaxElementCount() {
        return this.mMaxElementCount;
    }
    
    public int getMaxEntityDepth() {
        return this.mMaxEntityDepth;
    }
    
    public long getMaxEntityCount() {
        return this.mMaxEntityCount;
    }
    
    public long getMaxCharacters() {
        return this.mMaxCharacters;
    }
    
    public long getMaxTextLength() {
        return this.mMaxTextLength;
    }
    
    public Map<String, EntityDecl> getCustomInternalEntities() {
        final Map<String, EntityDecl> custEnt = (Map<String, EntityDecl>)this._getSpecialProperty(0);
        if (custEnt == null) {
            return Collections.emptyMap();
        }
        final int len = custEnt.size();
        final HashMap<String, EntityDecl> m = new HashMap<String, EntityDecl>(len + (len >> 2), 0.81f);
        for (final Map.Entry<String, EntityDecl> me : custEnt.entrySet()) {
            m.put(me.getKey(), me.getValue());
        }
        return m;
    }
    
    public EntityDecl findCustomInternalEntity(final String id) {
        final Map<String, EntityDecl> custEnt = (Map<String, EntityDecl>)this._getSpecialProperty(0);
        if (custEnt == null) {
            return null;
        }
        return custEnt.get(id);
    }
    
    public XMLReporter getXMLReporter() {
        return this.mReporter;
    }
    
    public XMLResolver getXMLResolver() {
        return this.mEntityResolver;
    }
    
    public XMLResolver getDtdResolver() {
        return this.mDtdResolver;
    }
    
    public XMLResolver getEntityResolver() {
        return this.mEntityResolver;
    }
    
    public XMLResolver getUndeclaredEntityResolver() {
        return (XMLResolver)this._getSpecialProperty(1);
    }
    
    public URL getBaseURL() {
        return this.mBaseURL;
    }
    
    public WstxInputProperties.ParsingMode getInputParsingMode() {
        return this.mParsingMode;
    }
    
    public boolean inputParsingModeDocuments() {
        return this.mParsingMode == WstxInputProperties.PARSING_MODE_DOCUMENTS;
    }
    
    public boolean inputParsingModeFragment() {
        return this.mParsingMode == WstxInputProperties.PARSING_MODE_FRAGMENT;
    }
    
    public boolean isXml11() {
        return this.mXml11;
    }
    
    public DTDEventListener getDTDEventListener() {
        return (DTDEventListener)this._getSpecialProperty(2);
    }
    
    public DTDValidationSchema getDTDOverride() {
        return (DTDValidationSchema)this._getSpecialProperty(3);
    }
    
    public boolean hasInternNamesBeenEnabled() {
        return this._hasExplicitConfigFlag(1024);
    }
    
    public boolean hasInternNsURIsBeenEnabled() {
        return this._hasExplicitConfigFlag(2048);
    }
    
    public void setConfigFlag(final int flag) {
        this.mConfigFlags |= flag;
        this.mConfigFlagMods |= flag;
    }
    
    public void clearConfigFlag(final int flag) {
        this.mConfigFlags &= ~flag;
        this.mConfigFlagMods |= flag;
    }
    
    public void doCoalesceText(final boolean state) {
        this.setConfigFlag(2, state);
    }
    
    public void doSupportNamespaces(final boolean state) {
        this.setConfigFlag(1, state);
    }
    
    public void doReplaceEntityRefs(final boolean state) {
        this.setConfigFlag(4, state);
    }
    
    public void doSupportExternalEntities(final boolean state) {
        this.setConfigFlag(8, state);
    }
    
    public void doSupportDTDs(final boolean state) {
        this.setConfigFlag(16, state);
    }
    
    public void doValidateWithDTD(final boolean state) {
        this.setConfigFlag(32, state);
    }
    
    public void doInternNames(final boolean state) {
        this.setConfigFlag(1024, state);
    }
    
    public void doInternNsURIs(final boolean state) {
        this.setConfigFlag(2048, state);
    }
    
    public void doReportPrologWhitespace(final boolean state) {
        this.setConfigFlag(256, state);
    }
    
    public void doReportCData(final boolean state) {
        this.setConfigFlag(512, state);
    }
    
    public void doCacheDTDs(final boolean state) {
        this.setConfigFlag(65536, state);
    }
    
    public void doCacheDTDsByPublicId(final boolean state) {
        this.setConfigFlag(131072, state);
    }
    
    public void doParseLazily(final boolean state) {
        this.setConfigFlag(262144, state);
    }
    
    public void doXmlIdTyping(final boolean state) {
        this.setConfigFlag(2097152, state);
    }
    
    public void doXmlIdUniqChecks(final boolean state) {
        this.setConfigFlag(4194304, state);
    }
    
    public void doPreserveLocation(final boolean state) {
        this.setConfigFlag(4096, state);
    }
    
    public void doAutoCloseInput(final boolean state) {
        this.setConfigFlag(8192, state);
    }
    
    public void doSupportDTDPP(final boolean state) {
        this.setConfigFlag(524288, state);
    }
    
    public void doTreatCharRefsAsEnts(final boolean state) {
        this.setConfigFlag(8388608, state);
    }
    
    public void doNormalizeLFs(final boolean state) {
        this.setConfigFlag(16384, state);
    }
    
    public void setInputBufferLength(int value) {
        if (value < 8) {
            value = 8;
        }
        this.mInputBufferLen = value;
    }
    
    public void setShortestReportedTextSegment(final int value) {
        this.mMinTextSegmentLen = value;
    }
    
    public void setMaxAttributesPerElement(final int value) {
        this.mMaxAttributesPerElement = value;
    }
    
    public void setMaxAttributeSize(final int value) {
        this.mMaxAttributeSize = value;
    }
    
    public void setMaxChildrenPerElement(final int value) {
        this.mMaxChildrenPerElement = value;
    }
    
    public void setMaxElementDepth(final int value) {
        this.mMaxElementDepth = value;
    }
    
    public void setMaxElementCount(final long value) {
        this.mMaxElementCount = value;
    }
    
    public void setMaxCharacters(final long value) {
        this.mMaxCharacters = value;
    }
    
    public void setMaxTextLength(final int value) {
        this.mMaxTextLength = value;
    }
    
    public void setMaxEntityDepth(final int value) {
        this.mMaxEntityDepth = value;
    }
    
    public void setMaxEntityCount(final long value) {
        this.mMaxEntityCount = value;
    }
    
    public void setCustomInternalEntities(final Map<String, ?> m) {
        Map<String, EntityDecl> entMap;
        if (m == null || m.size() < 1) {
            entMap = Collections.emptyMap();
        }
        else {
            final int len = m.size();
            entMap = new HashMap<String, EntityDecl>(len + (len >> 1), 0.75f);
            for (final Map.Entry<String, ?> me : m.entrySet()) {
                final Object val = me.getValue();
                char[] ch;
                if (val == null) {
                    ch = DataUtil.getEmptyCharArray();
                }
                else if (val instanceof char[]) {
                    ch = (char[])val;
                }
                else {
                    final String str = val.toString();
                    ch = str.toCharArray();
                }
                final String name = me.getKey();
                entMap.put(name, IntEntity.create(name, ch));
            }
        }
        this._setSpecialProperty(0, entMap);
    }
    
    public void setXMLReporter(final XMLReporter r) {
        this.mReporter = r;
    }
    
    public void setXMLResolver(final XMLResolver r) {
        this.mEntityResolver = r;
        this.mDtdResolver = r;
    }
    
    public void setDtdResolver(final XMLResolver r) {
        this.mDtdResolver = r;
    }
    
    public void setEntityResolver(final XMLResolver r) {
        this.mEntityResolver = r;
    }
    
    public void setUndeclaredEntityResolver(final XMLResolver r) {
        this._setSpecialProperty(1, r);
    }
    
    public void setBaseURL(final URL baseURL) {
        this.mBaseURL = baseURL;
    }
    
    public void setInputParsingMode(final WstxInputProperties.ParsingMode mode) {
        this.mParsingMode = mode;
    }
    
    public void enableXml11(final boolean state) {
        this.mXml11 = state;
    }
    
    public void setDTDEventListener(final DTDEventListener l) {
        this._setSpecialProperty(2, l);
    }
    
    public void setDTDOverride(final DTDValidationSchema schema) {
        this._setSpecialProperty(3, schema);
    }
    
    public void configureForXmlConformance() {
        this.doSupportNamespaces(true);
        this.doSupportDTDs(true);
        this.doSupportExternalEntities(true);
        this.doReplaceEntityRefs(true);
        this.doXmlIdTyping(true);
        this.doXmlIdUniqChecks(true);
    }
    
    public void configureForConvenience() {
        this.doCoalesceText(true);
        this.doReplaceEntityRefs(true);
        this.doReportCData(false);
        this.doReportPrologWhitespace(false);
        this.doPreserveLocation(true);
        this.doParseLazily(false);
    }
    
    public void configureForSpeed() {
        this.doCoalesceText(false);
        this.doPreserveLocation(false);
        this.doReportPrologWhitespace(false);
        this.doInternNsURIs(true);
        this.doXmlIdUniqChecks(false);
        this.doCacheDTDs(true);
        this.doParseLazily(true);
        this.setShortestReportedTextSegment(16);
        this.setInputBufferLength(8000);
    }
    
    public void configureForLowMemUsage() {
        this.doCoalesceText(false);
        this.doPreserveLocation(false);
        this.doCacheDTDs(false);
        this.doParseLazily(true);
        this.doXmlIdUniqChecks(false);
        this.setShortestReportedTextSegment(64);
        this.setInputBufferLength(512);
    }
    
    public void configureForRoundTripping() {
        this.doCoalesceText(false);
        this.doReplaceEntityRefs(false);
        this.doReportCData(true);
        this.doReportPrologWhitespace(true);
        this.doTreatCharRefsAsEnts(true);
        this.doNormalizeLFs(false);
        this.setShortestReportedTextSegment(Integer.MAX_VALUE);
    }
    
    public char[] allocSmallCBuffer(final int minSize) {
        if (this.mCurrRecycler != null) {
            final char[] result = this.mCurrRecycler.getSmallCBuffer(minSize);
            if (result != null) {
                return result;
            }
        }
        return new char[minSize];
    }
    
    public void freeSmallCBuffer(final char[] buffer) {
        if (this.mCurrRecycler == null) {
            this.mCurrRecycler = this.createRecycler();
        }
        this.mCurrRecycler.returnSmallCBuffer(buffer);
    }
    
    public char[] allocMediumCBuffer(final int minSize) {
        if (this.mCurrRecycler != null) {
            final char[] result = this.mCurrRecycler.getMediumCBuffer(minSize);
            if (result != null) {
                return result;
            }
        }
        return new char[minSize];
    }
    
    public void freeMediumCBuffer(final char[] buffer) {
        if (this.mCurrRecycler == null) {
            this.mCurrRecycler = this.createRecycler();
        }
        this.mCurrRecycler.returnMediumCBuffer(buffer);
    }
    
    public char[] allocFullCBuffer(final int minSize) {
        if (this.mCurrRecycler != null) {
            final char[] result = this.mCurrRecycler.getFullCBuffer(minSize);
            if (result != null) {
                return result;
            }
        }
        return new char[minSize];
    }
    
    public void freeFullCBuffer(final char[] buffer) {
        if (this.mCurrRecycler == null) {
            this.mCurrRecycler = this.createRecycler();
        }
        this.mCurrRecycler.returnFullCBuffer(buffer);
    }
    
    public byte[] allocFullBBuffer(final int minSize) {
        if (this.mCurrRecycler != null) {
            final byte[] result = this.mCurrRecycler.getFullBBuffer(minSize);
            if (result != null) {
                return result;
            }
        }
        return new byte[minSize];
    }
    
    public void freeFullBBuffer(final byte[] buffer) {
        if (this.mCurrRecycler == null) {
            this.mCurrRecycler = this.createRecycler();
        }
        this.mCurrRecycler.returnFullBBuffer(buffer);
    }
    
    private BufferRecycler createRecycler() {
        final BufferRecycler recycler = new BufferRecycler();
        ReaderConfig.mRecyclerRef.set(new SoftReference<BufferRecycler>(recycler));
        return recycler;
    }
    
    private void setConfigFlag(final int flag, final boolean state) {
        if (state) {
            this.mConfigFlags |= flag;
        }
        else {
            this.mConfigFlags &= ~flag;
        }
        this.mConfigFlagMods |= flag;
    }
    
    public Object getProperty(final int id) {
        switch (id) {
            case 1: {
                return this.willCoalesceText() ? Boolean.TRUE : Boolean.FALSE;
            }
            case 2: {
                return this.willSupportNamespaces() ? Boolean.TRUE : Boolean.FALSE;
            }
            case 3: {
                return this.willReplaceEntityRefs() ? Boolean.TRUE : Boolean.FALSE;
            }
            case 4: {
                return this.willSupportExternalEntities() ? Boolean.TRUE : Boolean.FALSE;
            }
            case 5: {
                return this.willValidateWithDTD() ? Boolean.TRUE : Boolean.FALSE;
            }
            case 6: {
                return this.willSupportDTDs() ? Boolean.TRUE : Boolean.FALSE;
            }
            case 8: {
                return this.getXMLReporter();
            }
            case 9: {
                return this.getXMLResolver();
            }
            case 7: {
                return null;
            }
            case 23: {
                return this.willReportPrologWhitespace() ? Boolean.TRUE : Boolean.FALSE;
            }
            case 22: {
                return this.willReportCData() ? Boolean.TRUE : Boolean.FALSE;
            }
            case 21: {
                return this.willInternNames() ? Boolean.TRUE : Boolean.FALSE;
            }
            case 20: {
                return this.willInternNsURIs() ? Boolean.TRUE : Boolean.FALSE;
            }
            case 24: {
                return this.willPreserveLocation() ? Boolean.TRUE : Boolean.FALSE;
            }
            case 25: {
                return this.willAutoCloseInput() ? Boolean.TRUE : Boolean.FALSE;
            }
            case 27: {
                return this.getDTDOverride();
            }
            case 42: {
                return this.willCacheDTDs() ? Boolean.TRUE : Boolean.FALSE;
            }
            case 43: {
                return this.willCacheDTDsByPublicId() ? Boolean.TRUE : Boolean.FALSE;
            }
            case 44: {
                return this.willParseLazily() ? Boolean.TRUE : Boolean.FALSE;
            }
            case 26: {
                if (!this._hasConfigFlag(2097152)) {
                    return "disable";
                }
                return this._hasConfigFlag(4194304) ? "xmlidFull" : "xmlidTyping";
            }
            case 46: {
                return this.willTreatCharRefsAsEnts() ? Boolean.TRUE : Boolean.FALSE;
            }
            case 40: {
                return this.willNormalizeLFs() ? Boolean.TRUE : Boolean.FALSE;
            }
            case 50: {
                return DataUtil.Integer(this.getInputBufferLength());
            }
            case 60: {
                return DataUtil.Integer(this.getMaxAttributesPerElement());
            }
            case 65: {
                return DataUtil.Integer(this.getMaxAttributeSize());
            }
            case 61: {
                return DataUtil.Integer(this.getMaxChildrenPerElement());
            }
            case 63: {
                return DataUtil.Integer(this.getMaxElementDepth());
            }
            case 62: {
                return this.getMaxElementCount();
            }
            case 64: {
                return this.getMaxCharacters();
            }
            case 66: {
                return this.getMaxTextLength();
            }
            case 68: {
                return DataUtil.Integer(this.getMaxEntityDepth());
            }
            case 67: {
                return this.getMaxEntityCount();
            }
            case 52: {
                return DataUtil.Integer(this.getShortestReportedTextSegment());
            }
            case 53: {
                return this.getCustomInternalEntities();
            }
            case 54: {
                return this.getDtdResolver();
            }
            case 55: {
                return this.getEntityResolver();
            }
            case 56: {
                return this.getUndeclaredEntityResolver();
            }
            case 57: {
                return this.getBaseURL();
            }
            case 58: {
                return this.getInputParsingMode();
            }
            default: {
                throw new IllegalStateException("Internal error: no handler for property with internal id " + id + ".");
            }
        }
    }
    
    public boolean setProperty(final String propName, final int id, final Object value) {
        switch (id) {
            case 1: {
                this.doCoalesceText(ArgUtil.convertToBoolean(propName, value));
                break;
            }
            case 2: {
                this.doSupportNamespaces(ArgUtil.convertToBoolean(propName, value));
                break;
            }
            case 3: {
                this.doReplaceEntityRefs(ArgUtil.convertToBoolean(propName, value));
                break;
            }
            case 4: {
                this.doSupportExternalEntities(ArgUtil.convertToBoolean(propName, value));
                break;
            }
            case 6: {
                this.doSupportDTDs(ArgUtil.convertToBoolean(propName, value));
                break;
            }
            case 5: {
                this.doValidateWithDTD(ArgUtil.convertToBoolean(propName, value));
                break;
            }
            case 8: {
                this.setXMLReporter((XMLReporter)value);
                break;
            }
            case 9: {
                this.setXMLResolver((XMLResolver)value);
                break;
            }
            case 7: {
                return false;
            }
            case 20: {
                this.doInternNsURIs(ArgUtil.convertToBoolean(propName, value));
                break;
            }
            case 21: {
                this.doInternNames(ArgUtil.convertToBoolean(propName, value));
                break;
            }
            case 22: {
                this.doReportCData(ArgUtil.convertToBoolean(propName, value));
                break;
            }
            case 23: {
                this.doReportPrologWhitespace(ArgUtil.convertToBoolean(propName, value));
                break;
            }
            case 24: {
                this.doPreserveLocation(ArgUtil.convertToBoolean(propName, value));
                break;
            }
            case 25: {
                this.doAutoCloseInput(ArgUtil.convertToBoolean(propName, value));
                break;
            }
            case 26: {
                boolean typing;
                boolean uniq;
                if ("disable".equals(value)) {
                    uniq = (typing = false);
                }
                else if ("xmlidTyping".equals(value)) {
                    typing = true;
                    uniq = false;
                }
                else {
                    if (!"xmlidFull".equals(value)) {
                        throw new IllegalArgumentException("Illegal argument ('" + value + "') to set property " + "org.codehaus.stax2.supportXmlId" + " to: has to be one of '" + "disable" + "', '" + "xmlidTyping" + "' or '" + "xmlidFull" + "'");
                    }
                    uniq = (typing = true);
                }
                this.setConfigFlag(2097152, typing);
                this.setConfigFlag(4194304, uniq);
                break;
            }
            case 27: {
                this.setDTDOverride((DTDValidationSchema)value);
                break;
            }
            case 42: {
                this.doCacheDTDs(ArgUtil.convertToBoolean(propName, value));
                break;
            }
            case 43: {
                this.doCacheDTDsByPublicId(ArgUtil.convertToBoolean(propName, value));
                break;
            }
            case 44: {
                this.doParseLazily(ArgUtil.convertToBoolean(propName, value));
                break;
            }
            case 46: {
                this.doTreatCharRefsAsEnts(ArgUtil.convertToBoolean(propName, value));
                break;
            }
            case 40: {
                this.doNormalizeLFs(ArgUtil.convertToBoolean(propName, value));
                break;
            }
            case 50: {
                this.setInputBufferLength(ArgUtil.convertToInt(propName, value, 1));
                break;
            }
            case 60: {
                this.setMaxAttributesPerElement(ArgUtil.convertToInt(propName, value, 1));
                break;
            }
            case 65: {
                this.setMaxAttributeSize(ArgUtil.convertToInt(propName, value, 1));
                break;
            }
            case 61: {
                this.setMaxChildrenPerElement(ArgUtil.convertToInt(propName, value, 1));
                break;
            }
            case 63: {
                this.setMaxElementDepth(ArgUtil.convertToInt(propName, value, 1));
                break;
            }
            case 62: {
                this.setMaxElementCount(ArgUtil.convertToLong(propName, value, 1L));
                break;
            }
            case 64: {
                this.setMaxCharacters(ArgUtil.convertToLong(propName, value, 1L));
                break;
            }
            case 66: {
                this.setMaxTextLength(ArgUtil.convertToInt(propName, value, 1));
                break;
            }
            case 68: {
                this.setMaxEntityDepth(ArgUtil.convertToInt(propName, value, 1));
                break;
            }
            case 67: {
                this.setMaxEntityCount(ArgUtil.convertToLong(propName, value, 1L));
                break;
            }
            case 52: {
                this.setShortestReportedTextSegment(ArgUtil.convertToInt(propName, value, 1));
                break;
            }
            case 53: {
                final Map<String, ?> arg = (Map<String, ?>)value;
                this.setCustomInternalEntities(arg);
                break;
            }
            case 54: {
                this.setDtdResolver((XMLResolver)value);
                break;
            }
            case 55: {
                this.setEntityResolver((XMLResolver)value);
                break;
            }
            case 56: {
                this.setUndeclaredEntityResolver((XMLResolver)value);
                break;
            }
            case 57: {
                URL u;
                if (value == null) {
                    u = null;
                }
                else if (value instanceof URL) {
                    u = (URL)value;
                }
                else {
                    try {
                        u = new URL(value.toString());
                    }
                    catch (Exception ioe) {
                        throw new IllegalArgumentException(ioe.getMessage(), ioe);
                    }
                }
                this.setBaseURL(u);
                break;
            }
            case 58: {
                this.setInputParsingMode((WstxInputProperties.ParsingMode)value);
                break;
            }
            default: {
                throw new IllegalStateException("Internal error: no handler for property with internal id " + id + ".");
            }
        }
        return true;
    }
    
    protected boolean _hasConfigFlag(final int flag) {
        return (this.mConfigFlags & flag) != 0x0;
    }
    
    protected boolean _hasExplicitConfigFlag(final int flag) {
        return this._hasConfigFlag(flag) && (this.mConfigFlagMods & flag) != 0x0;
    }
    
    private final Object _getSpecialProperty(final int ix) {
        if (this.mSpecialProperties == null) {
            return null;
        }
        return this.mSpecialProperties[ix];
    }
    
    private final void _setSpecialProperty(final int ix, final Object value) {
        if (this.mSpecialProperties == null) {
            this.mSpecialProperties = new Object[4];
        }
        this.mSpecialProperties[ix] = value;
    }
    
    static {
        (sProperties = new HashMap<String, Integer>(64)).put("javax.xml.stream.isCoalescing", DataUtil.Integer(1));
        ReaderConfig.sProperties.put("javax.xml.stream.isNamespaceAware", DataUtil.Integer(2));
        ReaderConfig.sProperties.put("javax.xml.stream.isReplacingEntityReferences", DataUtil.Integer(3));
        ReaderConfig.sProperties.put("javax.xml.stream.isSupportingExternalEntities", DataUtil.Integer(4));
        ReaderConfig.sProperties.put("javax.xml.stream.isValidating", DataUtil.Integer(5));
        ReaderConfig.sProperties.put("javax.xml.stream.supportDTD", DataUtil.Integer(6));
        ReaderConfig.sProperties.put("javax.xml.stream.allocator", DataUtil.Integer(7));
        ReaderConfig.sProperties.put("javax.xml.stream.reporter", DataUtil.Integer(8));
        ReaderConfig.sProperties.put("javax.xml.stream.resolver", DataUtil.Integer(9));
        ReaderConfig.sProperties.put("org.codehaus.stax2.internNames", DataUtil.Integer(21));
        ReaderConfig.sProperties.put("org.codehaus.stax2.internNsUris", DataUtil.Integer(20));
        ReaderConfig.sProperties.put("http://java.sun.com/xml/stream/properties/report-cdata-event", DataUtil.Integer(22));
        ReaderConfig.sProperties.put("org.codehaus.stax2.reportPrologWhitespace", DataUtil.Integer(23));
        ReaderConfig.sProperties.put("org.codehaus.stax2.preserveLocation", DataUtil.Integer(24));
        ReaderConfig.sProperties.put("org.codehaus.stax2.closeInputSource", DataUtil.Integer(25));
        ReaderConfig.sProperties.put("org.codehaus.stax2.supportXmlId", DataUtil.Integer(26));
        ReaderConfig.sProperties.put("org.codehaus.stax2.propDtdOverride", DataUtil.Integer(27));
        ReaderConfig.sProperties.put("com.ctc.wstx.cacheDTDs", DataUtil.Integer(42));
        ReaderConfig.sProperties.put("com.ctc.wstx.cacheDTDsByPublicId", DataUtil.Integer(43));
        ReaderConfig.sProperties.put("com.ctc.wstx.lazyParsing", DataUtil.Integer(44));
        ReaderConfig.sProperties.put("com.ctc.wstx.supportDTDPP", DataUtil.Integer(45));
        ReaderConfig.sProperties.put("com.ctc.wstx.treatCharRefsAsEnts", DataUtil.Integer(46));
        ReaderConfig.sProperties.put("com.ctc.wstx.normalizeLFs", DataUtil.Integer(40));
        ReaderConfig.sProperties.put("com.ctc.wstx.inputBufferLength", DataUtil.Integer(50));
        ReaderConfig.sProperties.put("com.ctc.wstx.minTextSegment", DataUtil.Integer(52));
        ReaderConfig.sProperties.put("com.ctc.wstx.maxAttributesPerElement", DataUtil.Integer(60));
        ReaderConfig.sProperties.put("com.ctc.wstx.maxAttributeSize", DataUtil.Integer(65));
        ReaderConfig.sProperties.put("com.ctc.wstx.maxChildrenPerElement", DataUtil.Integer(61));
        ReaderConfig.sProperties.put("com.ctc.wstx.maxTextLength", DataUtil.Integer(66));
        ReaderConfig.sProperties.put("com.ctc.wstx.maxElementCount", DataUtil.Integer(62));
        ReaderConfig.sProperties.put("com.ctc.wstx.maxElementDepth", DataUtil.Integer(63));
        ReaderConfig.sProperties.put("com.ctc.wstx.maxEntityDepth", DataUtil.Integer(68));
        ReaderConfig.sProperties.put("com.ctc.wstx.maxEntityCount", DataUtil.Integer(67));
        ReaderConfig.sProperties.put("com.ctc.wstx.maxCharacters", DataUtil.Integer(64));
        final String key = "com.ctc.wstx.customInternalEntities";
        ReaderConfig.sProperties.put(key, 53);
        ReaderConfig.sProperties.put("com.ctc.wstx.dtdResolver", DataUtil.Integer(54));
        ReaderConfig.sProperties.put("com.ctc.wstx.entityResolver", DataUtil.Integer(55));
        ReaderConfig.sProperties.put("com.ctc.wstx.undeclaredEntityResolver", DataUtil.Integer(56));
        ReaderConfig.sProperties.put("com.ctc.wstx.baseURL", DataUtil.Integer(57));
        ReaderConfig.sProperties.put("com.ctc.wstx.fragmentMode", DataUtil.Integer(58));
        mRecyclerRef = new ThreadLocal<SoftReference<BufferRecycler>>();
    }
}
