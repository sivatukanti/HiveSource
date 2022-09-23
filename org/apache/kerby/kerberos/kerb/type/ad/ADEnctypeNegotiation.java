// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.ad;

import org.apache.kerby.asn1.Asn1Dumper;
import java.util.Iterator;
import java.util.List;
import java.io.IOException;
import org.apache.kerby.asn1.type.Asn1Integer;
import org.apache.kerby.kerberos.kerb.type.KrbSequenceOfType;

public class ADEnctypeNegotiation extends AuthorizationDataEntry
{
    private KrbSequenceOfType<Asn1Integer> myEnctypeNeg;
    
    public ADEnctypeNegotiation() {
        super(AuthorizationType.AD_ETYPE_NEGOTIATION);
        this.myEnctypeNeg = new KrbSequenceOfType<Asn1Integer>();
        this.myEnctypeNeg.outerEncodeable = this;
    }
    
    public ADEnctypeNegotiation(final byte[] encoded) throws IOException {
        this();
        this.myEnctypeNeg.decode(encoded);
    }
    
    public ADEnctypeNegotiation(final List<Asn1Integer> enctypeNeg) throws IOException {
        this();
        for (final Asn1Integer element : enctypeNeg) {
            this.myEnctypeNeg.add(element);
        }
    }
    
    public List<Asn1Integer> getEnctypeNegotiation() {
        return this.myEnctypeNeg.getElements();
    }
    
    public void add(final Asn1Integer element) {
        this.myEnctypeNeg.add(element);
    }
    
    public void clear() {
        this.myEnctypeNeg.clear();
    }
    
    @Override
    protected int encodingBodyLength() throws IOException {
        if (this.bodyLength == -1) {
            this.setAuthzData(this.myEnctypeNeg.encode());
            this.bodyLength = super.encodingBodyLength();
        }
        return this.bodyLength;
    }
    
    @Override
    public void dumpWith(final Asn1Dumper dumper, final int indents) {
        super.dumpWith(dumper, indents);
        dumper.newLine();
        this.myEnctypeNeg.dumpWith(dumper, indents + 8);
    }
}
