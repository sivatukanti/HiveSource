// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1.parse;

import java.util.Iterator;
import org.apache.kerby.asn1.Asn1Dumper;
import java.util.ArrayList;
import java.nio.ByteBuffer;
import java.util.List;
import org.apache.kerby.asn1.Asn1Dumpable;

public class Asn1Container extends Asn1ParseResult implements Asn1Dumpable
{
    private List<Asn1ParseResult> children;
    
    public Asn1Container(final Asn1Header header, final int bodyStart, final ByteBuffer buffer) {
        super(header, bodyStart, buffer);
        this.children = new ArrayList<Asn1ParseResult>();
    }
    
    public List<Asn1ParseResult> getChildren() {
        return this.children;
    }
    
    public void addItem(final Asn1ParseResult value) {
        this.children.add(value);
    }
    
    public void clear() {
        this.children.clear();
    }
    
    @Override
    public void dumpWith(final Asn1Dumper dumper, final int indents) {
        dumper.indent(indents).append(this.toString());
        if (this.children.size() > 0) {
            dumper.newLine();
        }
        int i = this.children.size();
        for (final Asn1ParseResult aObj : this.children) {
            dumper.dumpParseResult(indents + 4, aObj);
            if (--i > 0) {
                dumper.newLine();
            }
        }
    }
    
    @Override
    public String toString() {
        String typeStr = this.tag().typeStr();
        typeStr = typeStr + " [tag=" + this.tag() + ", off=" + this.getOffset() + ", len=" + this.getHeaderLength() + "+" + this.getBodyLength() + (this.isDefinitiveLength() ? "" : "(undefined)") + "]";
        return typeStr;
    }
}
