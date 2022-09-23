// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.jute;

import org.xml.sax.Attributes;
import javax.xml.parsers.SAXParser;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;

class XmlInputArchive implements InputArchive
{
    private ArrayList<Value> valList;
    private int vLen;
    private int vIdx;
    
    private Value next() throws IOException {
        if (this.vIdx < this.vLen) {
            final Value v = this.valList.get(this.vIdx);
            this.valList.set(this.vIdx, null);
            ++this.vIdx;
            return v;
        }
        throw new IOException("Error in deserialization.");
    }
    
    static XmlInputArchive getArchive(final InputStream strm) throws ParserConfigurationException, SAXException, IOException {
        return new XmlInputArchive(strm);
    }
    
    public XmlInputArchive(final InputStream in) throws ParserConfigurationException, SAXException, IOException {
        this.valList = new ArrayList<Value>();
        final DefaultHandler handler = new XMLParser((ArrayList)this.valList);
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        final SAXParser parser = factory.newSAXParser();
        parser.parse(in, handler);
        this.vLen = this.valList.size();
        this.vIdx = 0;
    }
    
    @Override
    public byte readByte(final String tag) throws IOException {
        final Value v = this.next();
        if (!"ex:i1".equals(v.getType())) {
            throw new IOException("Error deserializing " + tag + ".");
        }
        return Byte.parseByte(v.getValue());
    }
    
    @Override
    public boolean readBool(final String tag) throws IOException {
        final Value v = this.next();
        if (!"boolean".equals(v.getType())) {
            throw new IOException("Error deserializing " + tag + ".");
        }
        return "1".equals(v.getValue());
    }
    
    @Override
    public int readInt(final String tag) throws IOException {
        final Value v = this.next();
        if (!"i4".equals(v.getType()) && !"int".equals(v.getType())) {
            throw new IOException("Error deserializing " + tag + ".");
        }
        return Integer.parseInt(v.getValue());
    }
    
    @Override
    public long readLong(final String tag) throws IOException {
        final Value v = this.next();
        if (!"ex:i8".equals(v.getType())) {
            throw new IOException("Error deserializing " + tag + ".");
        }
        return Long.parseLong(v.getValue());
    }
    
    @Override
    public float readFloat(final String tag) throws IOException {
        final Value v = this.next();
        if (!"ex:float".equals(v.getType())) {
            throw new IOException("Error deserializing " + tag + ".");
        }
        return Float.parseFloat(v.getValue());
    }
    
    @Override
    public double readDouble(final String tag) throws IOException {
        final Value v = this.next();
        if (!"double".equals(v.getType())) {
            throw new IOException("Error deserializing " + tag + ".");
        }
        return Double.parseDouble(v.getValue());
    }
    
    @Override
    public String readString(final String tag) throws IOException {
        final Value v = this.next();
        if (!"string".equals(v.getType())) {
            throw new IOException("Error deserializing " + tag + ".");
        }
        return Utils.fromXMLString(v.getValue());
    }
    
    @Override
    public byte[] readBuffer(final String tag) throws IOException {
        final Value v = this.next();
        if (!"string".equals(v.getType())) {
            throw new IOException("Error deserializing " + tag + ".");
        }
        return Utils.fromXMLBuffer(v.getValue());
    }
    
    @Override
    public void readRecord(final Record r, final String tag) throws IOException {
        r.deserialize(this, tag);
    }
    
    @Override
    public void startRecord(final String tag) throws IOException {
        final Value v = this.next();
        if (!"struct".equals(v.getType())) {
            throw new IOException("Error deserializing " + tag + ".");
        }
    }
    
    @Override
    public void endRecord(final String tag) throws IOException {
        final Value v = this.next();
        if (!"/struct".equals(v.getType())) {
            throw new IOException("Error deserializing " + tag + ".");
        }
    }
    
    @Override
    public Index startVector(final String tag) throws IOException {
        final Value v = this.next();
        if (!"array".equals(v.getType())) {
            throw new IOException("Error deserializing " + tag + ".");
        }
        return new XmlIndex();
    }
    
    @Override
    public void endVector(final String tag) throws IOException {
    }
    
    @Override
    public Index startMap(final String tag) throws IOException {
        return this.startVector(tag);
    }
    
    @Override
    public void endMap(final String tag) throws IOException {
        this.endVector(tag);
    }
    
    private static class Value
    {
        private String type;
        private StringBuffer sb;
        
        public Value(final String t) {
            this.type = t;
            this.sb = new StringBuffer();
        }
        
        public void addChars(final char[] buf, final int offset, final int len) {
            this.sb.append(buf, offset, len);
        }
        
        public String getValue() {
            return this.sb.toString();
        }
        
        public String getType() {
            return this.type;
        }
    }
    
    private static class XMLParser extends DefaultHandler
    {
        private boolean charsValid;
        private ArrayList<Value> valList;
        
        private XMLParser(final ArrayList<Value> vlist) {
            this.charsValid = false;
            this.valList = vlist;
        }
        
        @Override
        public void startDocument() throws SAXException {
        }
        
        @Override
        public void endDocument() throws SAXException {
        }
        
        @Override
        public void startElement(final String ns, final String sname, final String qname, final Attributes attrs) throws SAXException {
            this.charsValid = false;
            if ("boolean".equals(qname) || "i4".equals(qname) || "int".equals(qname) || "string".equals(qname) || "double".equals(qname) || "ex:i1".equals(qname) || "ex:i8".equals(qname) || "ex:float".equals(qname)) {
                this.charsValid = true;
                this.valList.add(new Value(qname));
            }
            else if ("struct".equals(qname) || "array".equals(qname)) {
                this.valList.add(new Value(qname));
            }
        }
        
        @Override
        public void endElement(final String ns, final String sname, final String qname) throws SAXException {
            this.charsValid = false;
            if ("struct".equals(qname) || "array".equals(qname)) {
                this.valList.add(new Value("/" + qname));
            }
        }
        
        @Override
        public void characters(final char[] buf, final int offset, final int len) throws SAXException {
            if (this.charsValid) {
                final Value v = this.valList.get(this.valList.size() - 1);
                v.addChars(buf, offset, len);
            }
        }
    }
    
    private class XmlIndex implements Index
    {
        @Override
        public boolean done() {
            final Value v = XmlInputArchive.this.valList.get(XmlInputArchive.this.vIdx);
            if ("/array".equals(v.getType())) {
                XmlInputArchive.this.valList.set(XmlInputArchive.this.vIdx, null);
                XmlInputArchive.this.vIdx++;
                return true;
            }
            return false;
        }
        
        @Override
        public void incr() {
        }
    }
}
