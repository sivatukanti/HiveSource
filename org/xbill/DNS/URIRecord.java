// 
// Decompiled by Procyon v0.5.36
// 

package org.xbill.DNS;

import java.io.IOException;

public class URIRecord extends Record
{
    private static final long serialVersionUID = 7955422413971804232L;
    private int priority;
    private int weight;
    private byte[] target;
    
    URIRecord() {
        this.target = new byte[0];
    }
    
    Record getObject() {
        return new URIRecord();
    }
    
    public URIRecord(final Name name, final int dclass, final long ttl, final int priority, final int weight, final String target) {
        super(name, 256, dclass, ttl);
        this.priority = Record.checkU16("priority", priority);
        this.weight = Record.checkU16("weight", weight);
        try {
            this.target = Record.byteArrayFromString(target);
        }
        catch (TextParseException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
    
    void rrFromWire(final DNSInput in) throws IOException {
        this.priority = in.readU16();
        this.weight = in.readU16();
        this.target = in.readCountedString();
    }
    
    void rdataFromString(final Tokenizer st, final Name origin) throws IOException {
        this.priority = st.getUInt16();
        this.weight = st.getUInt16();
        try {
            this.target = Record.byteArrayFromString(st.getString());
        }
        catch (TextParseException e) {
            throw st.exception(e.getMessage());
        }
    }
    
    String rrToString() {
        final StringBuffer sb = new StringBuffer();
        sb.append(this.priority + " ");
        sb.append(this.weight + " ");
        sb.append(Record.byteArrayToString(this.target, true));
        return sb.toString();
    }
    
    public int getPriority() {
        return this.priority;
    }
    
    public int getWeight() {
        return this.weight;
    }
    
    public String getTarget() {
        return Record.byteArrayToString(this.target, false);
    }
    
    void rrToWire(final DNSOutput out, final Compression c, final boolean canonical) {
        out.writeU16(this.priority);
        out.writeU16(this.weight);
        out.writeCountedString(this.target);
    }
}
