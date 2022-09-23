// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.plugin;

import java.util.HashMap;
import java.util.Map;
import org.datanucleus.exceptions.NucleusUserException;
import java.math.BigInteger;
import java.text.StringCharacterIterator;
import java.text.CharacterIterator;
import org.datanucleus.ClassConstants;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Text;
import javax.xml.parsers.ParserConfigurationException;
import java.io.InputStream;
import java.io.Reader;
import org.xml.sax.InputSource;
import java.io.InputStreamReader;
import javax.xml.parsers.DocumentBuilder;
import java.util.StringTokenizer;
import org.w3c.dom.NodeList;
import org.datanucleus.ClassLoaderResolver;
import org.w3c.dom.Element;
import java.util.ArrayList;
import java.util.Collections;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.util.NucleusLogger;
import java.util.List;
import java.net.URL;
import java.util.jar.Manifest;
import javax.xml.parsers.DocumentBuilderFactory;
import org.datanucleus.util.Localiser;

class PluginParser
{
    protected static final Localiser LOCALISER;
    static DocumentBuilderFactory dbFactory;
    
    public static Bundle parseManifest(final Manifest mf, final URL fileUrl) {
        Bundle bundle = null;
        try {
            final String symbolicName = getBundleSymbolicName(mf, null);
            final String bundleVersion = getBundleVersion(mf, null);
            final String bundleName = getBundleName(mf, null);
            final String bundleVendor = getBundleVendor(mf, null);
            bundle = new Bundle(symbolicName, bundleName, bundleVendor, bundleVersion, fileUrl);
            bundle.setRequireBundle(getRequireBundle(mf));
        }
        catch (NucleusException ne) {
            NucleusLogger.GENERAL.warn("Plugin at URL=" + fileUrl + " failed to parse so is being ignored", ne);
            return null;
        }
        return bundle;
    }
    
    private static List<Bundle.BundleDescription> getRequireBundle(final Manifest mf) {
        final String str = mf.getMainAttributes().getValue("Require-Bundle");
        if (str == null || str.length() < 1) {
            return (List<Bundle.BundleDescription>)Collections.EMPTY_LIST;
        }
        final Parser p = new Parser(str);
        final List<Bundle.BundleDescription> requiredBundle = new ArrayList<Bundle.BundleDescription>();
        String bundleSymbolicName = p.parseSymbolicName();
        while (bundleSymbolicName != null) {
            final Bundle.BundleDescription bd = new Bundle.BundleDescription();
            bd.setBundleSymbolicName(bundleSymbolicName);
            bd.setParameters(p.parseParameters());
            bundleSymbolicName = p.parseSymbolicName();
            requiredBundle.add(bd);
        }
        return requiredBundle;
    }
    
    private static List<ExtensionPoint> parseExtensionPoints(final Element rootElement, final Bundle plugin, final ClassLoaderResolver clr) {
        final List<ExtensionPoint> extensionPoints = new ArrayList<ExtensionPoint>();
        try {
            final NodeList elements = rootElement.getElementsByTagName("extension-point");
            for (int i = 0; i < elements.getLength(); ++i) {
                final Element element = (Element)elements.item(i);
                final String id = element.getAttribute("id").trim();
                final String name = element.getAttribute("name");
                final String schema = element.getAttribute("schema");
                extensionPoints.add(new ExtensionPoint(id, name, clr.getResource(schema, null), plugin));
            }
        }
        catch (NucleusException ex) {
            throw ex;
        }
        return extensionPoints;
    }
    
    private static List<Extension> parseExtensions(final Element rootElement, final Bundle plugin, final ClassLoaderResolver clr) {
        final List<Extension> extensions = new ArrayList<Extension>();
        try {
            final NodeList elements = rootElement.getElementsByTagName("extension");
            for (int i = 0; i < elements.getLength(); ++i) {
                final Element element = (Element)elements.item(i);
                final Extension ex = new Extension(element.getAttribute("point"), plugin);
                final NodeList elms = element.getChildNodes();
                extensions.add(ex);
                for (int e = 0; e < elms.getLength(); ++e) {
                    if (elms.item(e) instanceof Element) {
                        ex.addConfigurationElement(parseConfigurationElement(ex, (Element)elms.item(e), null));
                    }
                }
            }
        }
        catch (NucleusException ex2) {
            throw ex2;
        }
        return extensions;
    }
    
    private static String getBundleSymbolicName(final Manifest mf, final String defaultValue) {
        if (mf == null) {
            return defaultValue;
        }
        final String name = mf.getMainAttributes().getValue("Bundle-SymbolicName");
        if (name == null) {
            return defaultValue;
        }
        final StringTokenizer token = new StringTokenizer(name, ";");
        return token.nextToken().trim();
    }
    
    private static String getBundleName(final Manifest mf, final String defaultValue) {
        if (mf == null) {
            return defaultValue;
        }
        final String name = mf.getMainAttributes().getValue("Bundle-Name");
        if (name == null) {
            return defaultValue;
        }
        return name;
    }
    
    private static String getBundleVendor(final Manifest mf, final String defaultValue) {
        if (mf == null) {
            return defaultValue;
        }
        final String vendor = mf.getMainAttributes().getValue("Bundle-Vendor");
        if (vendor == null) {
            return defaultValue;
        }
        return vendor;
    }
    
    private static String getBundleVersion(final Manifest mf, final String defaultValue) {
        if (mf == null) {
            return defaultValue;
        }
        final String version = mf.getMainAttributes().getValue("Bundle-Version");
        if (version == null) {
            return defaultValue;
        }
        return version;
    }
    
    public static List[] parsePluginElements(final DocumentBuilder db, final PluginRegistry mgr, final URL fileUrl, final Bundle plugin, final ClassLoaderResolver clr) {
        List extensionPoints = Collections.EMPTY_LIST;
        List extensions = Collections.EMPTY_LIST;
        InputStream is = null;
        try {
            is = fileUrl.openStream();
            final Element rootElement = db.parse(new InputSource(new InputStreamReader(is))).getDocumentElement();
            if (NucleusLogger.GENERAL.isDebugEnabled()) {
                NucleusLogger.GENERAL.debug(PluginParser.LOCALISER.msg("024003", fileUrl.toString()));
            }
            extensionPoints = parseExtensionPoints(rootElement, plugin, clr);
            if (NucleusLogger.GENERAL.isDebugEnabled()) {
                NucleusLogger.GENERAL.debug(PluginParser.LOCALISER.msg("024004", fileUrl.toString()));
            }
            extensions = parseExtensions(rootElement, plugin, clr);
        }
        catch (NucleusException ex) {
            throw ex;
        }
        catch (Exception e) {
            NucleusLogger.GENERAL.error(PluginParser.LOCALISER.msg("024000", fileUrl.getFile()));
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (Exception ex2) {}
            }
        }
        return new List[] { extensionPoints, extensions };
    }
    
    public static DocumentBuilder getDocumentBuilder() {
        try {
            if (PluginParser.dbFactory == null) {
                PluginParser.dbFactory = DocumentBuilderFactory.newInstance();
            }
            return PluginParser.dbFactory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e1) {
            throw new NucleusException(PluginParser.LOCALISER.msg("024016", e1.getMessage()));
        }
    }
    
    public static ConfigurationElement parseConfigurationElement(final Extension ex, final Element element, final ConfigurationElement parent) {
        final ConfigurationElement confElm = new ConfigurationElement(ex, element.getNodeName(), parent);
        final NamedNodeMap attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); ++i) {
            final Node attribute = attributes.item(i);
            confElm.putAttribute(attribute.getNodeName(), attribute.getNodeValue());
        }
        final NodeList elements = element.getChildNodes();
        for (int j = 0; j < elements.getLength(); ++j) {
            if (elements.item(j) instanceof Element) {
                final Element elm = (Element)elements.item(j);
                final ConfigurationElement child = parseConfigurationElement(ex, elm, confElm);
                confElm.addConfigurationElement(child);
            }
            else if (elements.item(j) instanceof Text) {
                confElm.setText(elements.item(j).getNodeValue());
            }
        }
        return confElm;
    }
    
    public static Bundle.BundleVersionRange parseVersionRange(final String interval) {
        final Parser p = new Parser(interval);
        final Bundle.BundleVersionRange versionRange = new Bundle.BundleVersionRange();
        if (p.parseChar('[')) {
            versionRange.floor_inclusive = true;
        }
        else if (p.parseChar('(')) {
            versionRange.floor_inclusive = false;
        }
        versionRange.floor = new Bundle.BundleVersion();
        versionRange.floor.major = p.parseIntegerLiteral().intValue();
        if (p.parseChar('.')) {
            versionRange.floor.minor = p.parseIntegerLiteral().intValue();
        }
        if (p.parseChar('.')) {
            versionRange.floor.micro = p.parseIntegerLiteral().intValue();
        }
        if (p.parseChar('.')) {
            versionRange.floor.qualifier = p.parseIdentifier();
        }
        if (p.parseChar(',')) {
            versionRange.ceiling = new Bundle.BundleVersion();
            versionRange.ceiling.major = p.parseIntegerLiteral().intValue();
            if (p.parseChar('.')) {
                versionRange.ceiling.minor = p.parseIntegerLiteral().intValue();
            }
            if (p.parseChar('.')) {
                versionRange.ceiling.micro = p.parseIntegerLiteral().intValue();
            }
            if (p.parseChar('.')) {
                versionRange.ceiling.qualifier = p.parseIdentifier();
            }
            if (p.parseChar(']')) {
                versionRange.ceiling_inclusive = true;
            }
            else if (p.parseChar(')')) {
                versionRange.ceiling_inclusive = false;
            }
        }
        return versionRange;
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
        PluginParser.dbFactory = null;
    }
    
    public static class Parser
    {
        private final String input;
        protected final CharacterIterator ci;
        
        public Parser(final String input) {
            this.input = input;
            this.ci = new StringCharacterIterator(input);
        }
        
        public String getInput() {
            return this.input;
        }
        
        public int getIndex() {
            return this.ci.getIndex();
        }
        
        public int skipWS() {
            final int startIdx = this.ci.getIndex();
            for (char c = this.ci.current(); Character.isWhitespace(c) || c == '\t' || c == '\f' || c == '\n' || c == '\r' || c == '\t' || c == '\f' || c == ' ' || c == '\t' || c == '\n' || c == '\f' || c == '\r' || c == ' '; c = this.ci.next()) {}
            return startIdx;
        }
        
        public boolean parseEOS() {
            this.skipWS();
            return this.ci.current() == '\uffff';
        }
        
        public boolean parseChar(final char c) {
            this.skipWS();
            if (this.ci.current() == c) {
                this.ci.next();
                return true;
            }
            return false;
        }
        
        public boolean parseChar(final char c, final char unlessFollowedBy) {
            final int savedIdx = this.skipWS();
            if (this.ci.current() == c && this.ci.next() != unlessFollowedBy) {
                return true;
            }
            this.ci.setIndex(savedIdx);
            return false;
        }
        
        public BigInteger parseIntegerLiteral() {
            final int savedIdx = this.skipWS();
            final StringBuffer digits = new StringBuffer();
            char c = this.ci.current();
            int radix;
            if (c == '0') {
                c = this.ci.next();
                if (c == 'x' || c == 'X') {
                    radix = 16;
                    for (c = this.ci.next(); this.isHexDigit(c); c = this.ci.next()) {
                        digits.append(c);
                    }
                }
                else if (this.isOctDigit(c)) {
                    radix = 8;
                    do {
                        digits.append(c);
                        c = this.ci.next();
                    } while (this.isOctDigit(c));
                }
                else {
                    radix = 10;
                    digits.append('0');
                }
            }
            else {
                radix = 10;
                while (this.isDecDigit(c)) {
                    digits.append(c);
                    c = this.ci.next();
                }
            }
            if (digits.length() == 0) {
                this.ci.setIndex(savedIdx);
                return null;
            }
            if (c == 'l' || c == 'L') {
                this.ci.next();
            }
            return new BigInteger(digits.toString(), radix);
        }
        
        public boolean parseString(final String s) {
            final int savedIdx = this.skipWS();
            final int len = s.length();
            char c = this.ci.current();
            for (int i = 0; i < len; ++i) {
                if (c != s.charAt(i)) {
                    this.ci.setIndex(savedIdx);
                    return false;
                }
                c = this.ci.next();
            }
            return true;
        }
        
        public boolean parseStringIgnoreCase(final String s) {
            final String lowerCasedString = s.toLowerCase();
            final int savedIdx = this.skipWS();
            final int len = lowerCasedString.length();
            char c = this.ci.current();
            for (int i = 0; i < len; ++i) {
                if (Character.toLowerCase(c) != lowerCasedString.charAt(i)) {
                    this.ci.setIndex(savedIdx);
                    return false;
                }
                c = this.ci.next();
            }
            return true;
        }
        
        public String parseIdentifier() {
            this.skipWS();
            char c = this.ci.current();
            if (!Character.isJavaIdentifierStart(c)) {
                return null;
            }
            final StringBuffer id = new StringBuffer();
            id.append(c);
            while (Character.isJavaIdentifierPart(c = this.ci.next()) || c == '-') {
                id.append(c);
            }
            return id.toString();
        }
        
        public String parseInterval() {
            this.skipWS();
            char c = this.ci.current();
            final StringBuffer id = new StringBuffer();
            while ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '.' || c == '_' || c == '-' || c == '[' || c == ']' || c == '(' || c == ')') {
                id.append(c);
                c = this.ci.next();
            }
            return id.toString();
        }
        
        public String parseName() {
            final int savedIdx = this.skipWS();
            String id;
            if ((id = this.parseIdentifier()) == null) {
                return null;
            }
            final StringBuffer qn = new StringBuffer(id);
            while (this.parseChar('.')) {
                if ((id = this.parseIdentifier()) == null) {
                    this.ci.setIndex(savedIdx);
                    return null;
                }
                qn.append('.').append(id);
            }
            return qn.toString();
        }
        
        private final boolean isDecDigit(final char c) {
            return c >= '0' && c <= '9';
        }
        
        private final boolean isOctDigit(final char c) {
            return c >= '0' && c <= '7';
        }
        
        private final boolean isHexDigit(final char c) {
            return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
        }
        
        public boolean nextIsSingleQuote() {
            this.skipWS();
            return this.ci.current() == '\'';
        }
        
        public boolean nextIsDot() {
            return this.ci.current() == '.';
        }
        
        public boolean nextIsComma() {
            return this.ci.current() == ',';
        }
        
        public boolean nextIsSemiColon() {
            return this.ci.current() == ';';
        }
        
        public String parseStringLiteral() {
            this.skipWS();
            final char quote = this.ci.current();
            if (quote != '\"' && quote != '\'') {
                return null;
            }
            final StringBuffer lit = new StringBuffer();
            char c;
            while ((c = this.ci.next()) != quote) {
                if (c == '\uffff') {
                    throw new NucleusUserException("Invalid string literal: " + this.input);
                }
                if (c == '\\') {
                    c = this.parseEscapedCharacter();
                }
                lit.append(c);
            }
            this.ci.next();
            return lit.toString();
        }
        
        private char parseEscapedCharacter() {
            char c;
            if (this.isOctDigit(c = this.ci.next())) {
                int i = c - '0';
                if (this.isOctDigit(c = this.ci.next())) {
                    i = i * 8 + (c - '0');
                    if (this.isOctDigit(c = this.ci.next())) {
                        i = i * 8 + (c - '0');
                    }
                    else {
                        this.ci.previous();
                    }
                }
                else {
                    this.ci.previous();
                }
                if (i > 255) {
                    throw new NucleusUserException("Invalid character escape: '\\" + Integer.toOctalString(i) + "'");
                }
                return (char)i;
            }
            else {
                switch (c) {
                    case 'b': {
                        return '\b';
                    }
                    case 't': {
                        return '\t';
                    }
                    case 'n': {
                        return '\n';
                    }
                    case 'f': {
                        return '\f';
                    }
                    case 'r': {
                        return '\r';
                    }
                    case '\"': {
                        return '\"';
                    }
                    case '\'': {
                        return '\'';
                    }
                    case '\\': {
                        return '\\';
                    }
                    default: {
                        throw new NucleusUserException("Invalid character escape: '\\" + c + "'");
                    }
                }
            }
        }
        
        public String remaining() {
            final StringBuffer sb = new StringBuffer();
            for (char c = this.ci.current(); c != '\uffff'; c = this.ci.next()) {
                sb.append(c);
            }
            return sb.toString();
        }
        
        @Override
        public String toString() {
            return this.input;
        }
        
        public Map parseParameters() {
            this.skipWS();
            final Map paramaters = new HashMap();
            while (this.nextIsSemiColon()) {
                this.parseChar(';');
                this.skipWS();
                final String name = this.parseName();
                this.skipWS();
                if (!this.parseString(":=") && !this.parseString("=")) {
                    throw new NucleusUserException("Expected := or = symbols but found \"" + this.remaining() + "\" at position " + this.getIndex() + " of text \"" + this.input + "\"");
                }
                String argument = this.parseStringLiteral();
                if (argument == null) {
                    argument = this.parseIdentifier();
                }
                if (argument == null) {
                    argument = this.parseInterval();
                }
                paramaters.put(name, argument);
                this.skipWS();
            }
            return paramaters;
        }
        
        public String parseSymbolicName() {
            if (this.nextIsComma()) {
                this.parseChar(',');
            }
            final String name = this.parseName();
            if (name == null && !this.parseEOS()) {
                throw new NucleusUserException("Invalid characters found \"" + this.remaining() + "\" at position " + this.getIndex() + " of text \"" + this.input + "\"");
            }
            return name;
        }
    }
}
