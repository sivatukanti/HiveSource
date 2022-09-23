// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1.type;

import java.io.IOException;
import org.apache.kerby.asn1.UniversalTag;
import org.apache.kerby.asn1.Tag;

public abstract class Asn1Object
{
    private final Tag tag;
    
    public Asn1Object(final Tag tag) {
        this.tag = new Tag(tag);
    }
    
    public Asn1Object(final UniversalTag tag) {
        this.tag = new Tag(tag);
    }
    
    public Asn1Object(final int tag) {
        this.tag = new Tag(tag);
    }
    
    public Tag tag() {
        return this.tag;
    }
    
    public int tagFlags() {
        return this.tag().tagFlags();
    }
    
    public int tagNo() {
        return this.tag().tagNo();
    }
    
    public void usePrimitive(final boolean isPrimitive) {
        this.tag().usePrimitive(isPrimitive);
    }
    
    public boolean isPrimitive() {
        return this.tag().isPrimitive();
    }
    
    public boolean isUniversal() {
        return this.tag().isUniversal();
    }
    
    public boolean isAppSpecific() {
        return this.tag().isAppSpecific();
    }
    
    public boolean isContextSpecific() {
        return this.tag().isContextSpecific();
    }
    
    public boolean isTagSpecific() {
        return this.tag().isSpecific();
    }
    
    public boolean isEOC() {
        return this.tag().isEOC();
    }
    
    public boolean isNull() {
        return this.tag().isNull();
    }
    
    public boolean isSimple() {
        return Asn1Simple.isSimple(this.tag());
    }
    
    public boolean isCollection() {
        return Asn1Collection.isCollection(this.tag());
    }
    
    protected abstract int getHeaderLength() throws IOException;
    
    protected abstract int getBodyLength() throws IOException;
    
    protected String simpleInfo() {
        String simpleInfo = this.tag().typeStr();
        try {
            simpleInfo = simpleInfo + " [tag=" + this.tag() + ", len=" + this.getHeaderLength() + "+" + this.getBodyLength() + "] ";
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return simpleInfo;
    }
}
