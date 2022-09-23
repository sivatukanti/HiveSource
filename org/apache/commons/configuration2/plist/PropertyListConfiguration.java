// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.plist;

import java.util.TimeZone;
import java.util.HashMap;
import java.util.Calendar;
import java.util.Date;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.configuration2.MapConfiguration;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.configuration2.Configuration;
import java.util.Map;
import java.util.Collection;
import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.configuration2.tree.NodeHandler;
import java.io.PrintWriter;
import java.io.Writer;
import org.apache.commons.configuration2.ex.ConfigurationException;
import java.io.Reader;
import org.apache.commons.configuration2.tree.NodeModel;
import org.apache.commons.configuration2.tree.InMemoryNodeModel;
import org.apache.commons.configuration2.tree.ImmutableNode;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.BaseHierarchicalConfiguration;

public class PropertyListConfiguration extends BaseHierarchicalConfiguration implements FileBasedConfiguration
{
    private static final DateComponentParser DATE_SEPARATOR_PARSER;
    private static final DateComponentParser TIME_SEPARATOR_PARSER;
    private static final DateComponentParser BLANK_SEPARATOR_PARSER;
    private static final DateComponentParser[] DATE_PARSERS;
    private static final String TIME_ZONE_PREFIX = "GMT";
    private static final int MILLIS_PER_MINUTE = 60000;
    private static final int MINUTES_PER_HOUR = 60;
    private static final int INDENT_SIZE = 4;
    private static final int TIME_ZONE_LENGTH = 5;
    private static final char PAD_CHAR = '0';
    
    public PropertyListConfiguration() {
    }
    
    public PropertyListConfiguration(final HierarchicalConfiguration<ImmutableNode> c) {
        super(c);
    }
    
    PropertyListConfiguration(final ImmutableNode root) {
        super(new InMemoryNodeModel(root));
    }
    
    @Override
    protected void setPropertyInternal(final String key, final Object value) {
        if (value instanceof byte[]) {
            this.setDetailEvents(false);
            try {
                this.clearProperty(key);
                this.addPropertyDirect(key, value);
            }
            finally {
                this.setDetailEvents(true);
            }
        }
        else {
            super.setPropertyInternal(key, value);
        }
    }
    
    @Override
    protected void addPropertyInternal(final String key, final Object value) {
        if (value instanceof byte[]) {
            this.addPropertyDirect(key, value);
        }
        else {
            super.addPropertyInternal(key, value);
        }
    }
    
    @Override
    public void read(final Reader in) throws ConfigurationException {
        final PropertyListParser parser = new PropertyListParser(in);
        try {
            final PropertyListConfiguration config = parser.parse();
            this.getModel().setRootNode(config.getNodeModel().getNodeHandler().getRootNode());
        }
        catch (ParseException e) {
            throw new ConfigurationException(e);
        }
    }
    
    @Override
    public void write(final Writer out) throws ConfigurationException {
        final PrintWriter writer = new PrintWriter(out);
        final NodeHandler<ImmutableNode> handler = this.getModel().getNodeHandler();
        this.printNode(writer, 0, handler.getRootNode(), handler);
        writer.flush();
    }
    
    private void printNode(final PrintWriter out, final int indentLevel, final ImmutableNode node, final NodeHandler<ImmutableNode> handler) {
        final String padding = StringUtils.repeat(" ", indentLevel * 4);
        if (node.getNodeName() != null) {
            out.print(padding + this.quoteString(node.getNodeName()) + " = ");
        }
        final List<ImmutableNode> children = new ArrayList<ImmutableNode>(node.getChildren());
        if (!children.isEmpty()) {
            if (indentLevel > 0) {
                out.println();
            }
            out.println(padding + "{");
            final Iterator<ImmutableNode> it = children.iterator();
            while (it.hasNext()) {
                final ImmutableNode child = it.next();
                this.printNode(out, indentLevel + 1, child, handler);
                final Object value = child.getValue();
                if (value != null && !(value instanceof Map) && !(value instanceof Configuration)) {
                    out.println(";");
                }
                if (it.hasNext() && (value == null || value instanceof List)) {
                    out.println();
                }
            }
            out.print(padding + "}");
            if (handler.getParent(node) != null) {
                out.println();
            }
        }
        else if (node.getValue() == null) {
            out.println();
            out.print(padding + "{ };");
            if (handler.getParent(node) != null) {
                out.println();
            }
        }
        else {
            final Object value2 = node.getValue();
            this.printValue(out, indentLevel, value2);
        }
    }
    
    private void printValue(final PrintWriter out, final int indentLevel, final Object value) {
        final String padding = StringUtils.repeat(" ", indentLevel * 4);
        if (value instanceof List) {
            out.print("( ");
            final Iterator<?> it = ((List)value).iterator();
            while (it.hasNext()) {
                this.printValue(out, indentLevel + 1, it.next());
                if (it.hasNext()) {
                    out.print(", ");
                }
            }
            out.print(" )");
        }
        else if (value instanceof PropertyListConfiguration) {
            final NodeHandler<ImmutableNode> handler = ((PropertyListConfiguration)value).getModel().getNodeHandler();
            this.printNode(out, indentLevel, handler.getRootNode(), handler);
        }
        else if (value instanceof Configuration) {
            out.println();
            out.println(padding + "{");
            final Configuration config = (Configuration)value;
            final Iterator<String> it2 = config.getKeys();
            while (it2.hasNext()) {
                final String key = it2.next();
                final ImmutableNode node = new ImmutableNode.Builder().name(key).value(config.getProperty(key)).create();
                final InMemoryNodeModel tempModel = new InMemoryNodeModel(node);
                this.printNode(out, indentLevel + 1, node, tempModel.getNodeHandler());
                out.println(";");
            }
            out.println(padding + "}");
        }
        else if (value instanceof Map) {
            final Map<String, Object> map = transformMap((Map<?, ?>)value);
            this.printValue(out, indentLevel, new MapConfiguration(map));
        }
        else if (value instanceof byte[]) {
            out.print("<" + new String(Hex.encodeHex((byte[])value)) + ">");
        }
        else if (value instanceof Date) {
            out.print(formatDate((Date)value));
        }
        else if (value != null) {
            out.print(this.quoteString(String.valueOf(value)));
        }
    }
    
    String quoteString(String s) {
        if (s == null) {
            return null;
        }
        if (s.indexOf(32) != -1 || s.indexOf(9) != -1 || s.indexOf(13) != -1 || s.indexOf(10) != -1 || s.indexOf(34) != -1 || s.indexOf(40) != -1 || s.indexOf(41) != -1 || s.indexOf(123) != -1 || s.indexOf(125) != -1 || s.indexOf(61) != -1 || s.indexOf(44) != -1 || s.indexOf(59) != -1) {
            s = s.replaceAll("\"", "\\\\\\\"");
            s = "\"" + s + "\"";
        }
        return s;
    }
    
    static Date parseDate(final String s) throws ParseException {
        final Calendar cal = Calendar.getInstance();
        cal.clear();
        int index = 0;
        for (final DateComponentParser parser : PropertyListConfiguration.DATE_PARSERS) {
            index += parser.parseComponent(s, index, cal);
        }
        return cal.getTime();
    }
    
    static String formatDate(final Calendar cal) {
        final StringBuilder buf = new StringBuilder();
        for (final DateComponentParser element : PropertyListConfiguration.DATE_PARSERS) {
            element.formatComponent(buf, cal);
        }
        return buf.toString();
    }
    
    static String formatDate(final Date date) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return formatDate(cal);
    }
    
    private static Map<String, Object> transformMap(final Map<?, ?> src) {
        final Map<String, Object> dest = new HashMap<String, Object>();
        for (final Map.Entry<?, ?> e : src.entrySet()) {
            if (e.getKey() instanceof String) {
                dest.put((String)e.getKey(), e.getValue());
            }
        }
        return dest;
    }
    
    static {
        DATE_SEPARATOR_PARSER = new DateSeparatorParser("-");
        TIME_SEPARATOR_PARSER = new DateSeparatorParser(":");
        BLANK_SEPARATOR_PARSER = new DateSeparatorParser(" ");
        DATE_PARSERS = new DateComponentParser[] { new DateSeparatorParser("<*D"), new DateFieldParser(1, 4), PropertyListConfiguration.DATE_SEPARATOR_PARSER, new DateFieldParser(2, 2, 1), PropertyListConfiguration.DATE_SEPARATOR_PARSER, new DateFieldParser(5, 2), PropertyListConfiguration.BLANK_SEPARATOR_PARSER, new DateFieldParser(11, 2), PropertyListConfiguration.TIME_SEPARATOR_PARSER, new DateFieldParser(12, 2), PropertyListConfiguration.TIME_SEPARATOR_PARSER, new DateFieldParser(13, 2), PropertyListConfiguration.BLANK_SEPARATOR_PARSER, new DateTimeZoneParser(), new DateSeparatorParser(">") };
    }
    
    private abstract static class DateComponentParser
    {
        public abstract int parseComponent(final String p0, final int p1, final Calendar p2) throws ParseException;
        
        public abstract void formatComponent(final StringBuilder p0, final Calendar p1);
        
        protected void checkLength(final String s, final int index, final int length) throws ParseException {
            final int len = (s == null) ? 0 : s.length();
            if (index + length > len) {
                throw new ParseException("Input string too short: " + s + ", index: " + index);
            }
        }
        
        protected void padNum(final StringBuilder buf, final int num, final int length) {
            buf.append(StringUtils.leftPad(String.valueOf(num), length, '0'));
        }
    }
    
    private static class DateFieldParser extends DateComponentParser
    {
        private final int calendarField;
        private final int length;
        private final int offset;
        
        public DateFieldParser(final int calFld, final int len) {
            this(calFld, len, 0);
        }
        
        public DateFieldParser(final int calFld, final int len, final int ofs) {
            this.calendarField = calFld;
            this.length = len;
            this.offset = ofs;
        }
        
        @Override
        public void formatComponent(final StringBuilder buf, final Calendar cal) {
            this.padNum(buf, cal.get(this.calendarField) + this.offset, this.length);
        }
        
        @Override
        public int parseComponent(final String s, final int index, final Calendar cal) throws ParseException {
            this.checkLength(s, index, this.length);
            try {
                cal.set(this.calendarField, Integer.parseInt(s.substring(index, index + this.length)) - this.offset);
                return this.length;
            }
            catch (NumberFormatException nfex) {
                throw new ParseException("Invalid number: " + s + ", index " + index);
            }
        }
    }
    
    private static class DateSeparatorParser extends DateComponentParser
    {
        private final String separator;
        
        public DateSeparatorParser(final String sep) {
            this.separator = sep;
        }
        
        @Override
        public void formatComponent(final StringBuilder buf, final Calendar cal) {
            buf.append(this.separator);
        }
        
        @Override
        public int parseComponent(final String s, final int index, final Calendar cal) throws ParseException {
            this.checkLength(s, index, this.separator.length());
            if (!s.startsWith(this.separator, index)) {
                throw new ParseException("Invalid input: " + s + ", index " + index + ", expected " + this.separator);
            }
            return this.separator.length();
        }
    }
    
    private static class DateTimeZoneParser extends DateComponentParser
    {
        @Override
        public void formatComponent(final StringBuilder buf, final Calendar cal) {
            final TimeZone tz = cal.getTimeZone();
            int ofs = tz.getRawOffset() / 60000;
            if (ofs < 0) {
                buf.append('-');
                ofs = -ofs;
            }
            else {
                buf.append('+');
            }
            final int hour = ofs / 60;
            final int min = ofs % 60;
            this.padNum(buf, hour, 2);
            this.padNum(buf, min, 2);
        }
        
        @Override
        public int parseComponent(final String s, final int index, final Calendar cal) throws ParseException {
            this.checkLength(s, index, 5);
            final TimeZone tz = TimeZone.getTimeZone("GMT" + s.substring(index, index + 5));
            cal.setTimeZone(tz);
            return 5;
        }
    }
}
