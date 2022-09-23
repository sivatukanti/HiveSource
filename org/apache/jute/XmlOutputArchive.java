// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.jute;

import java.util.TreeMap;
import java.util.List;
import java.io.OutputStream;
import java.io.IOException;
import java.util.Stack;
import java.io.PrintStream;

class XmlOutputArchive implements OutputArchive
{
    private PrintStream stream;
    private int indent;
    private Stack<String> compoundStack;
    
    private void putIndent() {
        final StringBuilder sb = new StringBuilder("");
        for (int idx = 0; idx < this.indent; ++idx) {
            sb.append("  ");
        }
        this.stream.print(sb.toString());
    }
    
    private void addIndent() {
        ++this.indent;
    }
    
    private void closeIndent() {
        --this.indent;
    }
    
    private void printBeginEnvelope(final String tag) {
        if (!this.compoundStack.empty()) {
            final String s = this.compoundStack.peek();
            if ("struct".equals(s)) {
                this.putIndent();
                this.stream.print("<member>\n");
                this.addIndent();
                this.putIndent();
                this.stream.print("<name>" + tag + "</name>\n");
                this.putIndent();
                this.stream.print("<value>");
            }
            else if ("vector".equals(s)) {
                this.stream.print("<value>");
            }
            else if ("map".equals(s)) {
                this.stream.print("<value>");
            }
        }
        else {
            this.stream.print("<value>");
        }
    }
    
    private void printEndEnvelope(final String tag) {
        if (!this.compoundStack.empty()) {
            final String s = this.compoundStack.peek();
            if ("struct".equals(s)) {
                this.stream.print("</value>\n");
                this.closeIndent();
                this.putIndent();
                this.stream.print("</member>\n");
            }
            else if ("vector".equals(s)) {
                this.stream.print("</value>\n");
            }
            else if ("map".equals(s)) {
                this.stream.print("</value>\n");
            }
        }
        else {
            this.stream.print("</value>\n");
        }
    }
    
    private void insideVector(final String tag) {
        this.printBeginEnvelope(tag);
        this.compoundStack.push("vector");
    }
    
    private void outsideVector(final String tag) throws IOException {
        final String s = this.compoundStack.pop();
        if (!"vector".equals(s)) {
            throw new IOException("Error serializing vector.");
        }
        this.printEndEnvelope(tag);
    }
    
    private void insideMap(final String tag) {
        this.printBeginEnvelope(tag);
        this.compoundStack.push("map");
    }
    
    private void outsideMap(final String tag) throws IOException {
        final String s = this.compoundStack.pop();
        if (!"map".equals(s)) {
            throw new IOException("Error serializing map.");
        }
        this.printEndEnvelope(tag);
    }
    
    private void insideRecord(final String tag) {
        this.printBeginEnvelope(tag);
        this.compoundStack.push("struct");
    }
    
    private void outsideRecord(final String tag) throws IOException {
        final String s = this.compoundStack.pop();
        if (!"struct".equals(s)) {
            throw new IOException("Error serializing record.");
        }
        this.printEndEnvelope(tag);
    }
    
    static XmlOutputArchive getArchive(final OutputStream strm) {
        return new XmlOutputArchive(strm);
    }
    
    public XmlOutputArchive(final OutputStream out) {
        this.indent = 0;
        this.stream = new PrintStream(out);
        this.compoundStack = new Stack<String>();
    }
    
    @Override
    public void writeByte(final byte b, final String tag) throws IOException {
        this.printBeginEnvelope(tag);
        this.stream.print("<ex:i1>");
        this.stream.print(Byte.toString(b));
        this.stream.print("</ex:i1>");
        this.printEndEnvelope(tag);
    }
    
    @Override
    public void writeBool(final boolean b, final String tag) throws IOException {
        this.printBeginEnvelope(tag);
        this.stream.print("<boolean>");
        this.stream.print(b ? "1" : "0");
        this.stream.print("</boolean>");
        this.printEndEnvelope(tag);
    }
    
    @Override
    public void writeInt(final int i, final String tag) throws IOException {
        this.printBeginEnvelope(tag);
        this.stream.print("<i4>");
        this.stream.print(Integer.toString(i));
        this.stream.print("</i4>");
        this.printEndEnvelope(tag);
    }
    
    @Override
    public void writeLong(final long l, final String tag) throws IOException {
        this.printBeginEnvelope(tag);
        this.stream.print("<ex:i8>");
        this.stream.print(Long.toString(l));
        this.stream.print("</ex:i8>");
        this.printEndEnvelope(tag);
    }
    
    @Override
    public void writeFloat(final float f, final String tag) throws IOException {
        this.printBeginEnvelope(tag);
        this.stream.print("<ex:float>");
        this.stream.print(Float.toString(f));
        this.stream.print("</ex:float>");
        this.printEndEnvelope(tag);
    }
    
    @Override
    public void writeDouble(final double d, final String tag) throws IOException {
        this.printBeginEnvelope(tag);
        this.stream.print("<double>");
        this.stream.print(Double.toString(d));
        this.stream.print("</double>");
        this.printEndEnvelope(tag);
    }
    
    @Override
    public void writeString(final String s, final String tag) throws IOException {
        this.printBeginEnvelope(tag);
        this.stream.print("<string>");
        this.stream.print(Utils.toXMLString(s));
        this.stream.print("</string>");
        this.printEndEnvelope(tag);
    }
    
    @Override
    public void writeBuffer(final byte[] buf, final String tag) throws IOException {
        this.printBeginEnvelope(tag);
        this.stream.print("<string>");
        this.stream.print(Utils.toXMLBuffer(buf));
        this.stream.print("</string>");
        this.printEndEnvelope(tag);
    }
    
    @Override
    public void writeRecord(final Record r, final String tag) throws IOException {
        r.serialize(this, tag);
    }
    
    @Override
    public void startRecord(final Record r, final String tag) throws IOException {
        this.insideRecord(tag);
        this.stream.print("<struct>\n");
        this.addIndent();
    }
    
    @Override
    public void endRecord(final Record r, final String tag) throws IOException {
        this.closeIndent();
        this.putIndent();
        this.stream.print("</struct>");
        this.outsideRecord(tag);
    }
    
    @Override
    public void startVector(final List v, final String tag) throws IOException {
        this.insideVector(tag);
        this.stream.print("<array>\n");
        this.addIndent();
    }
    
    @Override
    public void endVector(final List v, final String tag) throws IOException {
        this.closeIndent();
        this.putIndent();
        this.stream.print("</array>");
        this.outsideVector(tag);
    }
    
    @Override
    public void startMap(final TreeMap v, final String tag) throws IOException {
        this.insideMap(tag);
        this.stream.print("<array>\n");
        this.addIndent();
    }
    
    @Override
    public void endMap(final TreeMap v, final String tag) throws IOException {
        this.closeIndent();
        this.putIndent();
        this.stream.print("</array>");
        this.outsideMap(tag);
    }
}
