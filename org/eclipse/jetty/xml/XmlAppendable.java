// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.xml;

import java.util.Iterator;
import java.io.Closeable;
import org.eclipse.jetty.util.StringUtil;
import java.util.Map;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.util.Stack;

public class XmlAppendable
{
    private final String SPACES = "                                                                 ";
    private final Appendable _out;
    private final int _indent;
    private final Stack<String> _tags;
    private String _space;
    
    public XmlAppendable(final OutputStream out, final String encoding) throws IOException {
        this(new OutputStreamWriter(out, encoding), encoding);
    }
    
    public XmlAppendable(final Appendable out) throws IOException {
        this(out, 2);
    }
    
    public XmlAppendable(final Appendable out, final String encoding) throws IOException {
        this(out, 2, encoding);
    }
    
    public XmlAppendable(final Appendable out, final int indent) throws IOException {
        this(out, indent, "utf-8");
    }
    
    public XmlAppendable(final Appendable out, final int indent, final String encoding) throws IOException {
        this._tags = new Stack<String>();
        this._space = "";
        this._out = out;
        this._indent = indent;
        this._out.append("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>\n");
    }
    
    public XmlAppendable openTag(final String tag, final Map<String, String> attributes) throws IOException {
        this._out.append(this._space).append('<').append(tag);
        this.attributes(attributes);
        this._out.append(">\n");
        this._space += "                                                                 ".substring(0, this._indent);
        this._tags.push(tag);
        return this;
    }
    
    public XmlAppendable openTag(final String tag) throws IOException {
        this._out.append(this._space).append('<').append(tag).append(">\n");
        this._space += "                                                                 ".substring(0, this._indent);
        this._tags.push(tag);
        return this;
    }
    
    public XmlAppendable content(final String s) throws IOException {
        if (s != null) {
            this._out.append(StringUtil.sanitizeXmlString(s));
        }
        return this;
    }
    
    public XmlAppendable cdata(final String s) throws IOException {
        this._out.append("<![CDATA[").append(s).append("]]>");
        return this;
    }
    
    public XmlAppendable tag(final String tag) throws IOException {
        this._out.append(this._space).append('<').append(tag).append("/>\n");
        return this;
    }
    
    public XmlAppendable tag(final String tag, final Map<String, String> attributes) throws IOException {
        this._out.append(this._space).append('<').append(tag);
        this.attributes(attributes);
        this._out.append("/>\n");
        return this;
    }
    
    public XmlAppendable tag(final String tag, final String content) throws IOException {
        this._out.append(this._space).append('<').append(tag).append('>');
        this.content(content);
        this._out.append("</").append(tag).append(">\n");
        return this;
    }
    
    public XmlAppendable tagCDATA(final String tag, final String data) throws IOException {
        this._out.append(this._space).append('<').append(tag).append('>');
        this.cdata(data);
        this._out.append("</").append(tag).append(">\n");
        return this;
    }
    
    public XmlAppendable tag(final String tag, final Map<String, String> attributes, final String content) throws IOException {
        this._out.append(this._space).append('<').append(tag);
        this.attributes(attributes);
        this._out.append('>');
        this.content(content);
        this._out.append("</").append(tag).append(">\n");
        return this;
    }
    
    public XmlAppendable closeTag() throws IOException {
        if (this._tags.isEmpty()) {
            throw new IllegalStateException("Tags closed");
        }
        final String tag = this._tags.pop();
        this._space = this._space.substring(0, this._space.length() - this._indent);
        this._out.append(this._space).append("</").append(tag).append(">\n");
        if (this._tags.isEmpty() && this._out instanceof Closeable) {
            ((Closeable)this._out).close();
        }
        return this;
    }
    
    private void attributes(final Map<String, String> attributes) throws IOException {
        for (final String k : attributes.keySet()) {
            final String v = attributes.get(k);
            this._out.append(' ').append(k).append("=\"");
            this.content(v);
            this._out.append('\"');
        }
    }
    
    public void literal(final String xml) throws IOException {
        this._out.append(xml);
    }
}
