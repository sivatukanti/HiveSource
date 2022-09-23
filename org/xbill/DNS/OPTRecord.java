// 
// Decompiled by Procyon v0.5.36
// 

package org.xbill.DNS;

import java.util.Collections;
import java.util.Iterator;
import java.io.IOException;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

public class OPTRecord extends Record
{
    private static final long serialVersionUID = -6254521894809367938L;
    private List options;
    
    OPTRecord() {
    }
    
    Record getObject() {
        return new OPTRecord();
    }
    
    public OPTRecord(final int payloadSize, final int xrcode, final int version, final int flags, final List options) {
        super(Name.root, 41, payloadSize, 0L);
        Record.checkU16("payloadSize", payloadSize);
        Record.checkU8("xrcode", xrcode);
        Record.checkU8("version", version);
        Record.checkU16("flags", flags);
        this.ttl = ((long)xrcode << 24) + ((long)version << 16) + flags;
        if (options != null) {
            this.options = new ArrayList(options);
        }
    }
    
    public OPTRecord(final int payloadSize, final int xrcode, final int version, final int flags) {
        this(payloadSize, xrcode, version, flags, null);
    }
    
    public OPTRecord(final int payloadSize, final int xrcode, final int version) {
        this(payloadSize, xrcode, version, 0, null);
    }
    
    void rrFromWire(final DNSInput in) throws IOException {
        if (in.remaining() > 0) {
            this.options = new ArrayList();
        }
        while (in.remaining() > 0) {
            final EDNSOption option = EDNSOption.fromWire(in);
            this.options.add(option);
        }
    }
    
    void rdataFromString(final Tokenizer st, final Name origin) throws IOException {
        throw st.exception("no text format defined for OPT");
    }
    
    String rrToString() {
        final StringBuffer sb = new StringBuffer();
        if (this.options != null) {
            sb.append(this.options);
            sb.append(" ");
        }
        sb.append(" ; payload ");
        sb.append(this.getPayloadSize());
        sb.append(", xrcode ");
        sb.append(this.getExtendedRcode());
        sb.append(", version ");
        sb.append(this.getVersion());
        sb.append(", flags ");
        sb.append(this.getFlags());
        return sb.toString();
    }
    
    public int getPayloadSize() {
        return this.dclass;
    }
    
    public int getExtendedRcode() {
        return (int)(this.ttl >>> 24);
    }
    
    public int getVersion() {
        return (int)(this.ttl >>> 16 & 0xFFL);
    }
    
    public int getFlags() {
        return (int)(this.ttl & 0xFFFFL);
    }
    
    void rrToWire(final DNSOutput out, final Compression c, final boolean canonical) {
        if (this.options == null) {
            return;
        }
        for (final EDNSOption option : this.options) {
            option.toWire(out);
        }
    }
    
    public List getOptions() {
        if (this.options == null) {
            return Collections.EMPTY_LIST;
        }
        return Collections.unmodifiableList((List<?>)this.options);
    }
    
    public List getOptions(final int code) {
        if (this.options == null) {
            return Collections.EMPTY_LIST;
        }
        List list = Collections.EMPTY_LIST;
        for (final EDNSOption opt : this.options) {
            if (opt.getCode() == code) {
                if (list == Collections.EMPTY_LIST) {
                    list = new ArrayList();
                }
                list.add(opt);
            }
        }
        return list;
    }
    
    public boolean equals(final Object arg) {
        return super.equals(arg) && this.ttl == ((OPTRecord)arg).ttl;
    }
}
