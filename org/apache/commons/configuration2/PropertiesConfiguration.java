// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2;

import org.apache.commons.lang3.text.translate.AggregateTranslator;
import org.apache.commons.lang3.text.translate.UnicodeEscaper;
import org.apache.commons.lang3.text.translate.EntityArrays;
import org.apache.commons.lang3.text.translate.LookupTranslator;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.configuration2.convert.ListDelimiterHandler;
import org.apache.commons.configuration2.convert.ValueTransformer;
import org.apache.commons.lang3.text.translate.CharSequenceTranslator;
import java.io.FilterWriter;
import java.util.regex.Matcher;
import org.apache.commons.lang3.StringEscapeUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.io.LineNumberReader;
import org.apache.commons.configuration2.io.FileLocatorUtils;
import java.net.URL;
import org.apache.commons.configuration2.io.FileBased;
import org.apache.commons.configuration2.io.FileHandler;
import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;
import java.util.Iterator;
import java.util.Collection;
import org.apache.commons.lang3.StringUtils;
import java.io.Writer;
import java.io.IOException;
import org.apache.commons.configuration2.ex.ConfigurationException;
import java.io.Reader;
import org.apache.commons.configuration2.event.EventListener;
import org.apache.commons.configuration2.event.ConfigurationEvent;
import org.apache.commons.configuration2.io.FileLocator;
import org.apache.commons.configuration2.io.FileLocatorAware;

public class PropertiesConfiguration extends BaseConfiguration implements FileBasedConfiguration, FileLocatorAware
{
    public static final String DEFAULT_ENCODING = "ISO-8859-1";
    static final String COMMENT_CHARS = "#!";
    static final String DEFAULT_SEPARATOR = " = ";
    private static final IOFactory DEFAULT_IO_FACTORY;
    private static final String UNESCAPE_CHARACTERS = ":#=!\\'\"";
    private static String include;
    private static final char[] SEPARATORS;
    private static final char[] WHITE_SPACE;
    private static final String LINE_SEPARATOR;
    private static final int HEX_RADIX = 16;
    private static final int UNICODE_LEN = 4;
    private PropertiesConfigurationLayout layout;
    private IOFactory ioFactory;
    private FileLocator locator;
    private boolean includesAllowed;
    
    public PropertiesConfiguration() {
        this.includesAllowed = true;
        this.installLayout(this.createLayout());
    }
    
    public static String getInclude() {
        return PropertiesConfiguration.include;
    }
    
    public static void setInclude(final String inc) {
        PropertiesConfiguration.include = inc;
    }
    
    public void setIncludesAllowed(final boolean includesAllowed) {
        this.includesAllowed = includesAllowed;
    }
    
    public boolean isIncludesAllowed() {
        return this.includesAllowed;
    }
    
    public String getHeader() {
        this.beginRead(false);
        try {
            return this.getLayout().getHeaderComment();
        }
        finally {
            this.endRead();
        }
    }
    
    public void setHeader(final String header) {
        this.beginWrite(false);
        try {
            this.getLayout().setHeaderComment(header);
        }
        finally {
            this.endWrite();
        }
    }
    
    public String getFooter() {
        this.beginRead(false);
        try {
            return this.getLayout().getFooterComment();
        }
        finally {
            this.endRead();
        }
    }
    
    public void setFooter(final String footer) {
        this.beginWrite(false);
        try {
            this.getLayout().setFooterComment(footer);
        }
        finally {
            this.endWrite();
        }
    }
    
    public PropertiesConfigurationLayout getLayout() {
        return this.layout;
    }
    
    public void setLayout(final PropertiesConfigurationLayout layout) {
        this.installLayout(layout);
    }
    
    private void installLayout(final PropertiesConfigurationLayout layout) {
        if (this.layout != null) {
            this.removeEventListener(ConfigurationEvent.ANY, this.layout);
        }
        if (layout == null) {
            this.layout = this.createLayout();
        }
        else {
            this.layout = layout;
        }
        this.addEventListener(ConfigurationEvent.ANY, this.layout);
    }
    
    private PropertiesConfigurationLayout createLayout() {
        return new PropertiesConfigurationLayout();
    }
    
    public IOFactory getIOFactory() {
        return (this.ioFactory != null) ? this.ioFactory : PropertiesConfiguration.DEFAULT_IO_FACTORY;
    }
    
    public void setIOFactory(final IOFactory ioFactory) {
        if (ioFactory == null) {
            throw new IllegalArgumentException("IOFactory must not be null!");
        }
        this.ioFactory = ioFactory;
    }
    
    @Override
    public void initFileLocator(final FileLocator locator) {
        this.locator = locator;
    }
    
    @Override
    public void read(final Reader in) throws ConfigurationException, IOException {
        this.getLayout().load(this, in);
    }
    
    @Override
    public void write(final Writer out) throws ConfigurationException, IOException {
        this.getLayout().save(this, out);
    }
    
    @Override
    public Object clone() {
        final PropertiesConfiguration copy = (PropertiesConfiguration)super.clone();
        if (this.layout != null) {
            copy.setLayout(new PropertiesConfigurationLayout(this.layout));
        }
        return copy;
    }
    
    boolean propertyLoaded(final String key, final String value) throws ConfigurationException {
        boolean result;
        if (StringUtils.isNotEmpty(getInclude()) && key.equalsIgnoreCase(getInclude())) {
            if (this.isIncludesAllowed()) {
                final Collection<String> files = this.getListDelimiterHandler().split(value, true);
                for (final String f : files) {
                    this.loadIncludeFile(this.interpolate(f));
                }
            }
            result = false;
        }
        else {
            this.addPropertyInternal(key, value);
            result = true;
        }
        return result;
    }
    
    static boolean isCommentLine(final String line) {
        final String s = line.trim();
        return s.length() < 1 || "#!".indexOf(s.charAt(0)) >= 0;
    }
    
    private static int countTrailingBS(final String line) {
        int bsCount = 0;
        for (int idx = line.length() - 1; idx >= 0 && line.charAt(idx) == '\\'; --idx) {
            ++bsCount;
        }
        return bsCount;
    }
    
    protected static String unescapeJava(final String str) {
        if (str == null) {
            return null;
        }
        final int sz = str.length();
        final StringBuilder out = new StringBuilder(sz);
        final StringBuilder unicode = new StringBuilder(4);
        boolean hadSlash = false;
        boolean inUnicode = false;
        for (int i = 0; i < sz; ++i) {
            final char ch = str.charAt(i);
            if (inUnicode) {
                unicode.append(ch);
                if (unicode.length() != 4) {
                    continue;
                }
                try {
                    final int value = Integer.parseInt(unicode.toString(), 16);
                    out.append((char)value);
                    unicode.setLength(0);
                    inUnicode = false;
                    hadSlash = false;
                    continue;
                }
                catch (NumberFormatException nfe) {
                    throw new ConfigurationRuntimeException("Unable to parse unicode value: " + (Object)unicode, nfe);
                }
            }
            if (hadSlash) {
                hadSlash = false;
                if (ch == 'r') {
                    out.append('\r');
                }
                else if (ch == 'f') {
                    out.append('\f');
                }
                else if (ch == 't') {
                    out.append('\t');
                }
                else if (ch == 'n') {
                    out.append('\n');
                }
                else if (ch == 'b') {
                    out.append('\b');
                }
                else if (ch == 'u') {
                    inUnicode = true;
                }
                else if (needsUnescape(ch)) {
                    out.append(ch);
                }
                else {
                    out.append('\\');
                    out.append(ch);
                }
            }
            else if (ch == '\\') {
                hadSlash = true;
            }
            else {
                out.append(ch);
            }
        }
        if (hadSlash) {
            out.append('\\');
        }
        return out.toString();
    }
    
    private static boolean needsUnescape(final char ch) {
        return ":#=!\\'\"".indexOf(ch) >= 0;
    }
    
    private void loadIncludeFile(final String fileName) throws ConfigurationException {
        if (this.locator == null) {
            throw new ConfigurationException("Load operation not properly initialized! Do not call read(InputStream) directly, but use a FileHandler to load a configuration.");
        }
        URL url = this.locateIncludeFile(this.locator.getBasePath(), fileName);
        if (url == null) {
            final URL baseURL = this.locator.getSourceURL();
            if (baseURL != null) {
                url = this.locateIncludeFile(baseURL.toString(), fileName);
            }
        }
        if (url == null) {
            throw new ConfigurationException("Cannot resolve include file " + fileName);
        }
        final FileHandler fh = new FileHandler(this);
        fh.setFileLocator(this.locator);
        fh.load(url);
    }
    
    private URL locateIncludeFile(final String basePath, final String fileName) {
        final FileLocator includeLocator = FileLocatorUtils.fileLocator(this.locator).sourceURL(null).basePath(basePath).fileName(fileName).create();
        return FileLocatorUtils.locate(includeLocator);
    }
    
    static {
        DEFAULT_IO_FACTORY = new DefaultIOFactory();
        PropertiesConfiguration.include = "include";
        SEPARATORS = new char[] { '=', ':' };
        WHITE_SPACE = new char[] { ' ', '\t', '\f' };
        LINE_SEPARATOR = System.getProperty("line.separator");
    }
    
    public static class PropertiesReader extends LineNumberReader
    {
        private static final Pattern PROPERTY_PATTERN;
        private static final int IDX_KEY = 1;
        private static final int IDX_VALUE = 5;
        private static final int IDX_SEPARATOR = 3;
        private final List<String> commentLines;
        private String propertyName;
        private String propertyValue;
        private String propertySeparator;
        
        public PropertiesReader(final Reader reader) {
            super(reader);
            this.propertySeparator = " = ";
            this.commentLines = new ArrayList<String>();
        }
        
        public String readProperty() throws IOException {
            this.commentLines.clear();
            final StringBuilder buffer = new StringBuilder();
            while (true) {
                String line = this.readLine();
                if (line == null) {
                    return null;
                }
                if (PropertiesConfiguration.isCommentLine(line)) {
                    this.commentLines.add(line);
                }
                else {
                    line = line.trim();
                    if (!checkCombineLines(line)) {
                        buffer.append(line);
                        return buffer.toString();
                    }
                    line = line.substring(0, line.length() - 1);
                    buffer.append(line);
                }
            }
        }
        
        public boolean nextProperty() throws IOException {
            final String line = this.readProperty();
            if (line == null) {
                return false;
            }
            this.parseProperty(line);
            return true;
        }
        
        public List<String> getCommentLines() {
            return this.commentLines;
        }
        
        public String getPropertyName() {
            return this.propertyName;
        }
        
        public String getPropertyValue() {
            return this.propertyValue;
        }
        
        public String getPropertySeparator() {
            return this.propertySeparator;
        }
        
        protected void parseProperty(final String line) {
            final String[] property = doParseProperty(line);
            this.initPropertyName(property[0]);
            this.initPropertyValue(property[1]);
            this.initPropertySeparator(property[2]);
        }
        
        protected void initPropertyName(final String name) {
            this.propertyName = StringEscapeUtils.unescapeJava(name);
        }
        
        protected void initPropertyValue(final String value) {
            this.propertyValue = PropertiesConfiguration.unescapeJava(value);
        }
        
        protected void initPropertySeparator(final String value) {
            this.propertySeparator = value;
        }
        
        private static boolean checkCombineLines(final String line) {
            return countTrailingBS(line) % 2 != 0;
        }
        
        private static String[] doParseProperty(final String line) {
            final Matcher matcher = PropertiesReader.PROPERTY_PATTERN.matcher(line);
            final String[] result = { "", "", "" };
            if (matcher.matches()) {
                result[0] = matcher.group(1).trim();
                result[1] = matcher.group(5).trim();
                result[2] = matcher.group(3);
            }
            return result;
        }
        
        static {
            PROPERTY_PATTERN = Pattern.compile("(([\\S&&[^\\\\" + new String(PropertiesConfiguration.SEPARATORS) + "]]|\\\\.)*)(\\s*(\\s+|[" + new String(PropertiesConfiguration.SEPARATORS) + "])\\s*)?(.*)");
        }
    }
    
    public static class PropertiesWriter extends FilterWriter
    {
        private static final CharSequenceTranslator ESCAPE_PROPERTIES;
        private static final ValueTransformer TRANSFORMER;
        private final ListDelimiterHandler delimiterHandler;
        private String currentSeparator;
        private String globalSeparator;
        private String lineSeparator;
        
        public PropertiesWriter(final Writer writer, final ListDelimiterHandler delHandler) {
            super(writer);
            this.delimiterHandler = delHandler;
        }
        
        public ListDelimiterHandler getDelimiterHandler() {
            return this.delimiterHandler;
        }
        
        public String getCurrentSeparator() {
            return this.currentSeparator;
        }
        
        public void setCurrentSeparator(final String currentSeparator) {
            this.currentSeparator = currentSeparator;
        }
        
        public String getGlobalSeparator() {
            return this.globalSeparator;
        }
        
        public void setGlobalSeparator(final String globalSeparator) {
            this.globalSeparator = globalSeparator;
        }
        
        public String getLineSeparator() {
            return (this.lineSeparator != null) ? this.lineSeparator : PropertiesConfiguration.LINE_SEPARATOR;
        }
        
        public void setLineSeparator(final String lineSeparator) {
            this.lineSeparator = lineSeparator;
        }
        
        public void writeProperty(final String key, final Object value) throws IOException {
            this.writeProperty(key, value, false);
        }
        
        public void writeProperty(final String key, final List<?> values) throws IOException {
            for (int i = 0; i < values.size(); ++i) {
                this.writeProperty(key, values.get(i));
            }
        }
        
        public void writeProperty(final String key, final Object value, final boolean forceSingleLine) throws IOException {
            String v;
            if (value instanceof List) {
                v = null;
                final List<?> values = (List<?>)value;
                if (forceSingleLine) {
                    try {
                        v = String.valueOf(this.getDelimiterHandler().escapeList(values, PropertiesWriter.TRANSFORMER));
                    }
                    catch (UnsupportedOperationException ex) {}
                }
                if (v == null) {
                    this.writeProperty(key, values);
                    return;
                }
            }
            else {
                v = String.valueOf(this.getDelimiterHandler().escape(value, PropertiesWriter.TRANSFORMER));
            }
            this.write(this.escapeKey(key));
            this.write(this.fetchSeparator(key, value));
            this.write(v);
            this.writeln(null);
        }
        
        public void writeComment(final String comment) throws IOException {
            this.writeln("# " + comment);
        }
        
        protected String escapeKey(final String key) {
            final StringBuilder newkey = new StringBuilder();
            for (int i = 0; i < key.length(); ++i) {
                final char c = key.charAt(i);
                if (ArrayUtils.contains(PropertiesConfiguration.SEPARATORS, c) || ArrayUtils.contains(PropertiesConfiguration.WHITE_SPACE, c)) {
                    newkey.append('\\');
                    newkey.append(c);
                }
                else {
                    newkey.append(c);
                }
            }
            return newkey.toString();
        }
        
        public void writeln(final String s) throws IOException {
            if (s != null) {
                this.write(s);
            }
            this.write(this.getLineSeparator());
        }
        
        protected String fetchSeparator(final String key, final Object value) {
            return (this.getGlobalSeparator() != null) ? this.getGlobalSeparator() : StringUtils.defaultString(this.getCurrentSeparator());
        }
        
        static {
            ESCAPE_PROPERTIES = new AggregateTranslator(new CharSequenceTranslator[] { new LookupTranslator((CharSequence[][])new String[][] { { "\\", "\\\\" } }), new LookupTranslator((CharSequence[][])EntityArrays.JAVA_CTRL_CHARS_ESCAPE()), UnicodeEscaper.outsideOf(32, 127) });
            TRANSFORMER = new ValueTransformer() {
                @Override
                public Object transformValue(final Object value) {
                    final String strVal = String.valueOf(value);
                    return PropertiesWriter.ESCAPE_PROPERTIES.translate(strVal);
                }
            };
        }
    }
    
    public static class DefaultIOFactory implements IOFactory
    {
        @Override
        public PropertiesReader createPropertiesReader(final Reader in) {
            return new PropertiesReader(in);
        }
        
        @Override
        public PropertiesWriter createPropertiesWriter(final Writer out, final ListDelimiterHandler handler) {
            return new PropertiesWriter(out, handler);
        }
    }
    
    public interface IOFactory
    {
        PropertiesReader createPropertiesReader(final Reader p0);
        
        PropertiesWriter createPropertiesWriter(final Writer p0, final ListDelimiterHandler p1);
    }
}
