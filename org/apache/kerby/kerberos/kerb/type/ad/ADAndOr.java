// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.ad;

import org.apache.kerby.asn1.Asn1Dumper;
import java.util.Iterator;
import java.util.List;
import java.io.IOException;
import org.apache.kerby.kerberos.kerb.type.KrbSequenceOfType;

public class ADAndOr extends AuthorizationDataEntry
{
    private KrbSequenceOfType<AndOr> myAndOr;
    
    public ADAndOr() {
        super(AuthorizationType.AD_AND_OR);
        this.myAndOr = new KrbSequenceOfType<AndOr>();
        this.myAndOr.outerEncodeable = this;
    }
    
    public ADAndOr(final byte[] encoded) throws IOException {
        this();
        this.myAndOr.decode(encoded);
    }
    
    public ADAndOr(final List<AndOr> elements) {
        this();
        for (final AndOr element : elements) {
            this.myAndOr.add(element);
        }
    }
    
    public List<AndOr> getAndOrs() throws IOException {
        return this.myAndOr.getElements();
    }
    
    public void add(final AndOr element) {
        this.myAndOr.add(element);
    }
    
    @Override
    protected int encodingBodyLength() throws IOException {
        if (this.bodyLength == -1) {
            this.setAuthzData(this.myAndOr.encode());
            this.bodyLength = super.encodingBodyLength();
        }
        return this.bodyLength;
    }
    
    @Override
    public void dumpWith(final Asn1Dumper dumper, final int indents) {
        super.dumpWith(dumper, indents);
        dumper.newLine();
        this.myAndOr.dumpWith(dumper, indents + 8);
    }
}
