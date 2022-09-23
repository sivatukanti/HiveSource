// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2;

import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;
import org.apache.commons.configuration2.event.Event;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import java.util.Iterator;
import java.io.Writer;
import java.io.IOException;
import org.apache.commons.configuration2.ex.ConfigurationException;
import java.io.Reader;
import java.util.Set;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Map;
import org.apache.commons.configuration2.event.ConfigurationEvent;
import org.apache.commons.configuration2.event.EventListener;

public class PropertiesConfigurationLayout implements EventListener<ConfigurationEvent>
{
    private static final String CR = "\n";
    private static final String COMMENT_PREFIX = "# ";
    private final Map<String, PropertyLayoutData> layoutData;
    private String headerComment;
    private String footerComment;
    private String globalSeparator;
    private String lineSeparator;
    private final AtomicInteger loadCounter;
    private boolean forceSingleLine;
    
    public PropertiesConfigurationLayout() {
        this(null);
    }
    
    public PropertiesConfigurationLayout(final PropertiesConfigurationLayout c) {
        this.loadCounter = new AtomicInteger();
        this.layoutData = new LinkedHashMap<String, PropertyLayoutData>();
        if (c != null) {
            this.copyFrom(c);
        }
    }
    
    public String getCanonicalComment(final String key, final boolean commentChar) {
        return constructCanonicalComment(this.getComment(key), commentChar);
    }
    
    public String getComment(final String key) {
        return this.fetchLayoutData(key).getComment();
    }
    
    public void setComment(final String key, final String comment) {
        this.fetchLayoutData(key).setComment(comment);
    }
    
    public int getBlancLinesBefore(final String key) {
        return this.fetchLayoutData(key).getBlancLines();
    }
    
    public void setBlancLinesBefore(final String key, final int number) {
        this.fetchLayoutData(key).setBlancLines(number);
    }
    
    public String getCanonicalHeaderComment(final boolean commentChar) {
        return constructCanonicalComment(this.getHeaderComment(), commentChar);
    }
    
    public String getHeaderComment() {
        return this.headerComment;
    }
    
    public void setHeaderComment(final String comment) {
        this.headerComment = comment;
    }
    
    public String getCanonicalFooterCooment(final boolean commentChar) {
        return constructCanonicalComment(this.getFooterComment(), commentChar);
    }
    
    public String getFooterComment() {
        return this.footerComment;
    }
    
    public void setFooterComment(final String footerComment) {
        this.footerComment = footerComment;
    }
    
    public boolean isSingleLine(final String key) {
        return this.fetchLayoutData(key).isSingleLine();
    }
    
    public void setSingleLine(final String key, final boolean f) {
        this.fetchLayoutData(key).setSingleLine(f);
    }
    
    public boolean isForceSingleLine() {
        return this.forceSingleLine;
    }
    
    public void setForceSingleLine(final boolean f) {
        this.forceSingleLine = f;
    }
    
    public String getSeparator(final String key) {
        return this.fetchLayoutData(key).getSeparator();
    }
    
    public void setSeparator(final String key, final String sep) {
        this.fetchLayoutData(key).setSeparator(sep);
    }
    
    public String getGlobalSeparator() {
        return this.globalSeparator;
    }
    
    public void setGlobalSeparator(final String globalSeparator) {
        this.globalSeparator = globalSeparator;
    }
    
    public String getLineSeparator() {
        return this.lineSeparator;
    }
    
    public void setLineSeparator(final String lineSeparator) {
        this.lineSeparator = lineSeparator;
    }
    
    public Set<String> getKeys() {
        return this.layoutData.keySet();
    }
    
    public void load(final PropertiesConfiguration config, final Reader in) throws ConfigurationException {
        this.loadCounter.incrementAndGet();
        final PropertiesConfiguration.PropertiesReader reader = config.getIOFactory().createPropertiesReader(in);
        try {
            while (reader.nextProperty()) {
                if (config.propertyLoaded(reader.getPropertyName(), reader.getPropertyValue())) {
                    final boolean contained = this.layoutData.containsKey(reader.getPropertyName());
                    int blancLines;
                    int idx;
                    for (blancLines = 0, idx = this.checkHeaderComment(reader.getCommentLines()); idx < reader.getCommentLines().size() && reader.getCommentLines().get(idx).length() < 1; ++idx, ++blancLines) {}
                    final String comment = this.extractComment(reader.getCommentLines(), idx, reader.getCommentLines().size() - 1);
                    final PropertyLayoutData data = this.fetchLayoutData(reader.getPropertyName());
                    if (contained) {
                        data.addComment(comment);
                        data.setSingleLine(false);
                    }
                    else {
                        data.setComment(comment);
                        data.setBlancLines(blancLines);
                        data.setSeparator(reader.getPropertySeparator());
                    }
                }
            }
            this.setFooterComment(this.extractComment(reader.getCommentLines(), 0, reader.getCommentLines().size() - 1));
        }
        catch (IOException ioex) {
            throw new ConfigurationException(ioex);
        }
        finally {
            this.loadCounter.decrementAndGet();
        }
    }
    
    public void save(final PropertiesConfiguration config, final Writer out) throws ConfigurationException {
        try {
            final PropertiesConfiguration.PropertiesWriter writer = config.getIOFactory().createPropertiesWriter(out, config.getListDelimiterHandler());
            writer.setGlobalSeparator(this.getGlobalSeparator());
            if (this.getLineSeparator() != null) {
                writer.setLineSeparator(this.getLineSeparator());
            }
            if (this.headerComment != null) {
                writeComment(writer, this.getCanonicalHeaderComment(true));
                writer.writeln(null);
            }
            for (final String key : this.getKeys()) {
                if (config.containsKeyInternal(key)) {
                    for (int i = 0; i < this.getBlancLinesBefore(key); ++i) {
                        writer.writeln(null);
                    }
                    writeComment(writer, this.getCanonicalComment(key, true));
                    final boolean singleLine = this.isForceSingleLine() || this.isSingleLine(key);
                    writer.setCurrentSeparator(this.getSeparator(key));
                    writer.writeProperty(key, config.getPropertyInternal(key), singleLine);
                }
            }
            writeComment(writer, this.getCanonicalFooterCooment(true));
            writer.flush();
        }
        catch (IOException ioex) {
            throw new ConfigurationException(ioex);
        }
    }
    
    @Override
    public void onEvent(final ConfigurationEvent event) {
        if (!event.isBeforeUpdate() && this.loadCounter.get() == 0) {
            if (ConfigurationEvent.ADD_PROPERTY.equals(event.getEventType())) {
                final boolean contained = this.layoutData.containsKey(event.getPropertyName());
                final PropertyLayoutData data = this.fetchLayoutData(event.getPropertyName());
                data.setSingleLine(!contained);
            }
            else if (ConfigurationEvent.CLEAR_PROPERTY.equals(event.getEventType())) {
                this.layoutData.remove(event.getPropertyName());
            }
            else if (ConfigurationEvent.CLEAR.equals(event.getEventType())) {
                this.clear();
            }
            else if (ConfigurationEvent.SET_PROPERTY.equals(event.getEventType())) {
                this.fetchLayoutData(event.getPropertyName());
            }
        }
    }
    
    private PropertyLayoutData fetchLayoutData(final String key) {
        if (key == null) {
            throw new IllegalArgumentException("Property key must not be null!");
        }
        PropertyLayoutData data = this.layoutData.get(key);
        if (data == null) {
            data = new PropertyLayoutData();
            data.setSingleLine(true);
            this.layoutData.put(key, data);
        }
        return data;
    }
    
    private void clear() {
        this.layoutData.clear();
        this.setHeaderComment(null);
        this.setFooterComment(null);
    }
    
    static boolean isCommentLine(final String line) {
        return PropertiesConfiguration.isCommentLine(line);
    }
    
    static String trimComment(final String s, final boolean comment) {
        final StringBuilder buf = new StringBuilder(s.length());
        int lastPos = 0;
        int pos;
        do {
            pos = s.indexOf("\n", lastPos);
            if (pos >= 0) {
                final String line = s.substring(lastPos, pos);
                buf.append(stripCommentChar(line, comment)).append("\n");
                lastPos = pos + "\n".length();
            }
        } while (pos >= 0);
        if (lastPos < s.length()) {
            buf.append(stripCommentChar(s.substring(lastPos), comment));
        }
        return buf.toString();
    }
    
    static String stripCommentChar(final String s, final boolean comment) {
        if (StringUtils.isBlank(s) || isCommentLine(s) == comment) {
            return s;
        }
        if (!comment) {
            int pos;
            for (pos = 0; "#!".indexOf(s.charAt(pos)) < 0; ++pos) {}
            ++pos;
            while (pos < s.length() && Character.isWhitespace(s.charAt(pos))) {
                ++pos;
            }
            return (pos < s.length()) ? s.substring(pos) : "";
        }
        return "# " + s;
    }
    
    private String extractComment(final List<String> commentLines, final int from, final int to) {
        if (to < from) {
            return null;
        }
        final StringBuilder buf = new StringBuilder(commentLines.get(from));
        for (int i = from + 1; i <= to; ++i) {
            buf.append("\n");
            buf.append(commentLines.get(i));
        }
        return buf.toString();
    }
    
    private int checkHeaderComment(final List<String> commentLines) {
        if (this.loadCounter.get() == 1 && this.layoutData.isEmpty()) {
            int index;
            for (index = commentLines.size() - 1; index >= 0 && commentLines.get(index).length() > 0; --index) {}
            if (this.getHeaderComment() == null) {
                this.setHeaderComment(this.extractComment(commentLines, 0, index - 1));
            }
            return index + 1;
        }
        return 0;
    }
    
    private void copyFrom(final PropertiesConfigurationLayout c) {
        for (final String key : c.getKeys()) {
            final PropertyLayoutData data = c.layoutData.get(key);
            this.layoutData.put(key, data.clone());
        }
        this.setHeaderComment(c.getHeaderComment());
        this.setFooterComment(c.getFooterComment());
    }
    
    private static void writeComment(final PropertiesConfiguration.PropertiesWriter writer, final String comment) throws IOException {
        if (comment != null) {
            writer.writeln(StringUtils.replace(comment, "\n", writer.getLineSeparator()));
        }
    }
    
    private static String constructCanonicalComment(final String comment, final boolean commentChar) {
        return (comment == null) ? null : trimComment(comment, commentChar);
    }
    
    static class PropertyLayoutData implements Cloneable
    {
        private StringBuffer comment;
        private String separator;
        private int blancLines;
        private boolean singleLine;
        
        public PropertyLayoutData() {
            this.singleLine = true;
            this.separator = " = ";
        }
        
        public int getBlancLines() {
            return this.blancLines;
        }
        
        public void setBlancLines(final int blancLines) {
            this.blancLines = blancLines;
        }
        
        public boolean isSingleLine() {
            return this.singleLine;
        }
        
        public void setSingleLine(final boolean singleLine) {
            this.singleLine = singleLine;
        }
        
        public void addComment(final String s) {
            if (s != null) {
                if (this.comment == null) {
                    this.comment = new StringBuffer(s);
                }
                else {
                    this.comment.append("\n").append(s);
                }
            }
        }
        
        public void setComment(final String s) {
            if (s == null) {
                this.comment = null;
            }
            else {
                this.comment = new StringBuffer(s);
            }
        }
        
        public String getComment() {
            return (this.comment == null) ? null : this.comment.toString();
        }
        
        public String getSeparator() {
            return this.separator;
        }
        
        public void setSeparator(final String separator) {
            this.separator = separator;
        }
        
        public PropertyLayoutData clone() {
            try {
                final PropertyLayoutData copy = (PropertyLayoutData)super.clone();
                if (this.comment != null) {
                    copy.comment = new StringBuffer(this.getComment());
                }
                return copy;
            }
            catch (CloneNotSupportedException cnex) {
                throw new ConfigurationRuntimeException(cnex);
            }
        }
    }
}
