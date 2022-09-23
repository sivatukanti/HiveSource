// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.ad;

import org.apache.kerby.kerberos.kerb.type.KrbSequenceOfType;
import org.apache.kerby.asn1.Asn1Dumper;
import org.apache.kerby.asn1.type.Asn1Utf8String;
import java.util.List;
import java.io.IOException;

public class ADAuthenticationIndicator extends AuthorizationDataEntry
{
    private AuthIndicator myAuthIndicator;
    
    public ADAuthenticationIndicator() {
        super(AuthorizationType.AD_AUTHENTICAION_INDICATOR);
        this.myAuthIndicator = new AuthIndicator();
        this.myAuthIndicator.outerEncodeable = this;
    }
    
    public ADAuthenticationIndicator(final byte[] encoded) throws IOException {
        this();
        this.myAuthIndicator.decode(encoded);
    }
    
    public List<Asn1Utf8String> getAuthIndicators() {
        return this.myAuthIndicator.getElements();
    }
    
    public void add(final Asn1Utf8String indicator) {
        this.myAuthIndicator.add(indicator);
        this.resetBodyLength();
    }
    
    public void clear() {
        this.myAuthIndicator.clear();
        this.resetBodyLength();
    }
    
    @Override
    protected int encodingBodyLength() throws IOException {
        if (this.bodyLength == -1) {
            this.setAuthzData(this.myAuthIndicator.encode());
            this.bodyLength = super.encodingBodyLength();
        }
        return this.bodyLength;
    }
    
    @Override
    public void dumpWith(final Asn1Dumper dumper, final int indents) {
        super.dumpWith(dumper, indents);
        dumper.newLine();
        this.myAuthIndicator.dumpWith(dumper, indents + 8);
    }
    
    private static class AuthIndicator extends KrbSequenceOfType<Asn1Utf8String>
    {
    }
}
