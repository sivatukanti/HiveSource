// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.util;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.PushbackReader;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.util.Iterator;
import java.io.PrintStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Properties;

public class LayoutPreservingProperties extends Properties
{
    private String LS;
    private ArrayList logicalLines;
    private HashMap keyedPairLines;
    private boolean removeComments;
    
    public LayoutPreservingProperties() {
        this.LS = StringUtils.LINE_SEP;
        this.logicalLines = new ArrayList();
        this.keyedPairLines = new HashMap();
    }
    
    public LayoutPreservingProperties(final Properties defaults) {
        super(defaults);
        this.LS = StringUtils.LINE_SEP;
        this.logicalLines = new ArrayList();
        this.keyedPairLines = new HashMap();
    }
    
    public boolean isRemoveComments() {
        return this.removeComments;
    }
    
    public void setRemoveComments(final boolean val) {
        this.removeComments = val;
    }
    
    @Override
    public void load(final InputStream inStream) throws IOException {
        final String s = this.readLines(inStream);
        final byte[] ba = s.getBytes("ISO-8859-1");
        final ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        super.load(bais);
    }
    
    @Override
    public Object put(final Object key, final Object value) throws NullPointerException {
        final Object obj = super.put(key, value);
        this.innerSetProperty(key.toString(), value.toString());
        return obj;
    }
    
    @Override
    public Object setProperty(final String key, final String value) throws NullPointerException {
        final Object obj = super.setProperty(key, value);
        this.innerSetProperty(key, value);
        return obj;
    }
    
    private void innerSetProperty(String key, String value) {
        value = this.escapeValue(value);
        if (this.keyedPairLines.containsKey(key)) {
            final Integer i = this.keyedPairLines.get(key);
            final Pair p = this.logicalLines.get(i);
            p.setValue(value);
        }
        else {
            key = this.escapeName(key);
            final Pair p2 = new Pair(key, value);
            p2.setNew(true);
            this.keyedPairLines.put(key, new Integer(this.logicalLines.size()));
            this.logicalLines.add(p2);
        }
    }
    
    @Override
    public void clear() {
        super.clear();
        this.keyedPairLines.clear();
        this.logicalLines.clear();
    }
    
    @Override
    public Object remove(final Object key) {
        final Object obj = super.remove(key);
        final Integer i = this.keyedPairLines.remove(key);
        if (null != i) {
            if (this.removeComments) {
                this.removeCommentsEndingAt(i);
            }
            this.logicalLines.set(i, null);
        }
        return obj;
    }
    
    @Override
    public Object clone() {
        final LayoutPreservingProperties dolly = (LayoutPreservingProperties)super.clone();
        dolly.keyedPairLines = (HashMap)this.keyedPairLines.clone();
        dolly.logicalLines = (ArrayList)this.logicalLines.clone();
        for (int size = dolly.logicalLines.size(), j = 0; j < size; ++j) {
            final LogicalLine line = dolly.logicalLines.get(j);
            if (line instanceof Pair) {
                final Pair p = (Pair)line;
                dolly.logicalLines.set(j, p.clone());
            }
        }
        return dolly;
    }
    
    public void listLines(final PrintStream out) {
        out.println("-- logical lines --");
        for (final LogicalLine line : this.logicalLines) {
            if (line instanceof Blank) {
                out.println("blank:   \"" + line + "\"");
            }
            else if (line instanceof Comment) {
                out.println("comment: \"" + line + "\"");
            }
            else {
                if (!(line instanceof Pair)) {
                    continue;
                }
                out.println("pair:    \"" + line + "\"");
            }
        }
    }
    
    public void saveAs(final File dest) throws IOException {
        final FileOutputStream fos = new FileOutputStream(dest);
        this.store(fos, null);
        fos.close();
    }
    
    @Override
    public void store(final OutputStream out, final String header) throws IOException {
        final OutputStreamWriter osw = new OutputStreamWriter(out, "ISO-8859-1");
        int skipLines = 0;
        final int totalLines = this.logicalLines.size();
        if (header != null) {
            osw.write("#" + header + this.LS);
            if (totalLines > 0 && this.logicalLines.get(0) instanceof Comment && header.equals(this.logicalLines.get(0).toString().substring(1))) {
                skipLines = 1;
            }
        }
        if (totalLines > skipLines && this.logicalLines.get(skipLines) instanceof Comment) {
            try {
                DateUtils.parseDateFromHeader(this.logicalLines.get(skipLines).toString().substring(1));
                ++skipLines;
            }
            catch (ParseException ex) {}
        }
        osw.write("#" + DateUtils.getDateForHeader() + this.LS);
        boolean writtenSep = false;
        for (final LogicalLine line : this.logicalLines.subList(skipLines, totalLines)) {
            if (line instanceof Pair) {
                if (((Pair)line).isNew() && !writtenSep) {
                    osw.write(this.LS);
                    writtenSep = true;
                }
                osw.write(line.toString() + this.LS);
            }
            else {
                if (line == null) {
                    continue;
                }
                osw.write(line.toString() + this.LS);
            }
        }
        osw.close();
    }
    
    private String readLines(final InputStream is) throws IOException {
        final InputStreamReader isr = new InputStreamReader(is, "ISO-8859-1");
        final PushbackReader pbr = new PushbackReader(isr, 1);
        if (this.logicalLines.size() > 0) {
            this.logicalLines.add(new Blank());
        }
        String s = this.readFirstLine(pbr);
        final BufferedReader br = new BufferedReader(pbr);
        boolean continuation = false;
        boolean comment = false;
        final StringBuffer fileBuffer = new StringBuffer();
        final StringBuffer logicalLineBuffer = new StringBuffer();
        while (s != null) {
            fileBuffer.append(s).append(this.LS);
            if (continuation) {
                s = "\n" + s;
            }
            else {
                comment = s.matches("^( |\t|\f)*(#|!).*");
            }
            if (!comment) {
                continuation = this.requiresContinuation(s);
            }
            logicalLineBuffer.append(s);
            if (!continuation) {
                LogicalLine line = null;
                if (comment) {
                    line = new Comment(logicalLineBuffer.toString());
                }
                else if (logicalLineBuffer.toString().trim().length() == 0) {
                    line = new Blank();
                }
                else {
                    line = new Pair(logicalLineBuffer.toString());
                    final String key = this.unescape(((Pair)line).getName());
                    if (this.keyedPairLines.containsKey(key)) {
                        this.remove(key);
                    }
                    this.keyedPairLines.put(key, new Integer(this.logicalLines.size()));
                }
                this.logicalLines.add(line);
                logicalLineBuffer.setLength(0);
            }
            s = br.readLine();
        }
        return fileBuffer.toString();
    }
    
    private String readFirstLine(final PushbackReader r) throws IOException {
        final StringBuffer sb = new StringBuffer(80);
        int ch = r.read();
        boolean hasCR = false;
        this.LS = StringUtils.LINE_SEP;
        while (ch >= 0) {
            if (hasCR && ch != 10) {
                r.unread(ch);
                break;
            }
            if (ch == 13) {
                this.LS = "\r";
                hasCR = true;
            }
            else {
                if (ch == 10) {
                    this.LS = (hasCR ? "\r\n" : "\n");
                    break;
                }
                sb.append((char)ch);
            }
            ch = r.read();
        }
        return sb.toString();
    }
    
    private boolean requiresContinuation(final String s) {
        char[] ca;
        int i;
        for (ca = s.toCharArray(), i = ca.length - 1; i > 0 && ca[i] == '\\'; --i) {}
        final int tb = ca.length - i - 1;
        return tb % 2 == 1;
    }
    
    private String unescape(final String s) {
        final char[] ch = new char[s.length() + 1];
        s.getChars(0, s.length(), ch, 0);
        ch[s.length()] = '\n';
        final StringBuffer buffy = new StringBuffer(s.length());
        for (int i = 0; i < ch.length; ++i) {
            char c = ch[i];
            if (c == '\n') {
                break;
            }
            if (c == '\\') {
                c = ch[++i];
                if (c == 'n') {
                    buffy.append('\n');
                }
                else if (c == 'r') {
                    buffy.append('\r');
                }
                else if (c == 'f') {
                    buffy.append('\f');
                }
                else if (c == 't') {
                    buffy.append('\t');
                }
                else if (c == 'u') {
                    c = this.unescapeUnicode(ch, i + 1);
                    i += 4;
                    buffy.append(c);
                }
                else {
                    buffy.append(c);
                }
            }
            else {
                buffy.append(c);
            }
        }
        return buffy.toString();
    }
    
    private char unescapeUnicode(final char[] ch, final int i) {
        final String s = new String(ch, i, 4);
        return (char)Integer.parseInt(s, 16);
    }
    
    private String escapeValue(final String s) {
        return this.escape(s, false);
    }
    
    private String escapeName(final String s) {
        return this.escape(s, true);
    }
    
    private String escape(final String s, final boolean escapeAllSpaces) {
        if (s == null) {
            return null;
        }
        final char[] ch = new char[s.length()];
        s.getChars(0, s.length(), ch, 0);
        final String forEscaping = "\t\f\r\n\\:=#!";
        final String escaped = "tfrn\\:=#!";
        final StringBuffer buffy = new StringBuffer(s.length());
        boolean leadingSpace = true;
        for (int i = 0; i < ch.length; ++i) {
            final char c = ch[i];
            if (c == ' ') {
                if (escapeAllSpaces || leadingSpace) {
                    buffy.append("\\");
                }
            }
            else {
                leadingSpace = false;
            }
            final int p = forEscaping.indexOf(c);
            if (p != -1) {
                buffy.append("\\").append(escaped.substring(p, p + 1));
            }
            else if (c < ' ' || c > '~') {
                buffy.append(this.escapeUnicode(c));
            }
            else {
                buffy.append(c);
            }
        }
        return buffy.toString();
    }
    
    private String escapeUnicode(final char ch) {
        return "\\" + (Object)UnicodeUtil.EscapeUnicode(ch);
    }
    
    private void removeCommentsEndingAt(int pos) {
        int end;
        for (end = --pos; pos > 0 && this.logicalLines.get(pos) instanceof Blank; --pos) {}
        if (!(this.logicalLines.get(pos) instanceof Comment)) {
            return;
        }
        while (pos >= 0 && this.logicalLines.get(pos) instanceof Comment) {
            --pos;
        }
        ++pos;
        while (pos <= end) {
            this.logicalLines.set(pos, null);
            ++pos;
        }
    }
    
    private abstract static class LogicalLine
    {
        private String text;
        
        public LogicalLine(final String text) {
            this.text = text;
        }
        
        public void setText(final String text) {
            this.text = text;
        }
        
        @Override
        public String toString() {
            return this.text;
        }
    }
    
    private static class Blank extends LogicalLine
    {
        public Blank() {
            super("");
        }
    }
    
    private class Comment extends LogicalLine
    {
        public Comment(final String text) {
            super(text);
        }
    }
    
    private static class Pair extends LogicalLine implements Cloneable
    {
        private String name;
        private String value;
        private boolean added;
        
        public Pair(final String text) {
            super(text);
            this.parsePair(text);
        }
        
        public Pair(final String name, final String value) {
            this(name + "=" + value);
        }
        
        public String getName() {
            return this.name;
        }
        
        public String getValue() {
            return this.value;
        }
        
        public void setValue(final String value) {
            this.value = value;
            this.setText(this.name + "=" + value);
        }
        
        public boolean isNew() {
            return this.added;
        }
        
        public void setNew(final boolean val) {
            this.added = val;
        }
        
        public Object clone() {
            Object dolly = null;
            try {
                dolly = super.clone();
            }
            catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            return dolly;
        }
        
        private void parsePair(final String text) {
            final int pos = this.findFirstSeparator(text);
            if (pos == -1) {
                this.name = text;
                this.value = null;
            }
            else {
                this.name = text.substring(0, pos);
                this.value = text.substring(pos + 1, text.length());
            }
            this.name = this.stripStart(this.name, " \t\f");
        }
        
        private String stripStart(final String s, final String chars) {
            if (s == null) {
                return null;
            }
            int i;
            for (i = 0; i < s.length() && chars.indexOf(s.charAt(i)) != -1; ++i) {}
            if (i == s.length()) {
                return "";
            }
            return s.substring(i);
        }
        
        private int findFirstSeparator(String s) {
            s = s.replaceAll("\\\\\\\\", "__");
            s = s.replaceAll("\\\\=", "__");
            s = s.replaceAll("\\\\:", "__");
            s = s.replaceAll("\\\\ ", "__");
            s = s.replaceAll("\\\\t", "__");
            return this.indexOfAny(s, " :=\t");
        }
        
        private int indexOfAny(final String s, final String chars) {
            if (s == null || chars == null) {
                return -1;
            }
            int p = s.length() + 1;
            for (int i = 0; i < chars.length(); ++i) {
                final int x = s.indexOf(chars.charAt(i));
                if (x != -1 && x < p) {
                    p = x;
                }
            }
            if (p == s.length() + 1) {
                return -1;
            }
            return p;
        }
    }
}
