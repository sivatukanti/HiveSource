// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1.type;

import org.apache.kerby.asn1.Asn1Dumper;
import org.apache.kerby.asn1.Asn1Converter;
import org.apache.kerby.asn1.parse.Asn1ParseResult;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.util.Iterator;
import java.util.ArrayList;
import org.apache.kerby.asn1.Tag;
import org.apache.kerby.asn1.parse.Asn1Container;
import org.apache.kerby.asn1.Asn1Dumpable;
import java.util.List;

public class Asn1Constructed extends AbstractAsn1Type<List<Asn1Type>> implements Asn1Dumpable
{
    protected Asn1Container container;
    private boolean lazy;
    
    public Asn1Constructed(final Tag tag) {
        super(tag);
        this.lazy = false;
        ((AbstractAsn1Type<ArrayList<Asn1Type>>)this).setValue(new ArrayList<Asn1Type>());
        this.usePrimitive(false);
    }
    
    public Asn1Container getContainer() {
        return this.container;
    }
    
    public void setLazy(final boolean lazy) {
        this.lazy = lazy;
    }
    
    public boolean isLazy() {
        return this.lazy;
    }
    
    public void addItem(final Asn1Type value) {
        this.resetBodyLength();
        this.getValue().add(value);
        if (value instanceof Asn1Encodeable) {
            ((Asn1Encodeable)value).outerEncodeable = this;
        }
    }
    
    public void clear() {
        this.resetBodyLength();
        ((AbstractAsn1Type<List>)this).getValue().clear();
    }
    
    @Override
    protected int encodingBodyLength() throws IOException {
        final List<Asn1Type> valueItems = this.getValue();
        int allLen = 0;
        for (final Asn1Type item : valueItems) {
            if (item != null) {
                allLen += item.encodingLength();
            }
        }
        return allLen;
    }
    
    @Override
    protected void encodeBody(final ByteBuffer buffer) throws IOException {
        final List<Asn1Type> valueItems = this.getValue();
        for (final Asn1Type item : valueItems) {
            if (item != null) {
                item.encode(buffer);
            }
        }
    }
    
    @Override
    protected void decodeBody(final Asn1ParseResult parseResult) throws IOException {
        final Asn1Container container = (Asn1Container)parseResult;
        this.container = container;
        this.useDefinitiveLength(parseResult.isDefinitiveLength());
        if (!this.isLazy()) {
            this.decodeElements();
        }
    }
    
    protected void decodeElements() throws IOException {
        for (final Asn1ParseResult parsingItem : this.getContainer().getChildren()) {
            if (parsingItem.isEOC()) {
                continue;
            }
            final Asn1Type tmpValue = Asn1Converter.convert(parsingItem, this.lazy);
            this.addItem(tmpValue);
        }
    }
    
    @Override
    public void dumpWith(final Asn1Dumper dumper, final int indents) {
        final String typeStr = this.tag().typeStr() + " [" + "tag=" + this.tag() + ", len=" + this.getHeaderLength() + "+" + this.getBodyLength() + "] ";
        dumper.indent(indents).append(typeStr).newLine();
        final List<Asn1Type> items = this.getValue();
        int i = 0;
        for (final Asn1Type aObj : items) {
            dumper.dumpType(indents + 4, aObj);
            if (i++ != items.size() - 1) {
                dumper.newLine();
            }
        }
    }
}
