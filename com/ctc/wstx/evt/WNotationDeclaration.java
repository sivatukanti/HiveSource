// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.evt;

import javax.xml.stream.Location;
import java.net.URL;
import org.codehaus.stax2.ri.evt.NotationDeclarationEventImpl;

public class WNotationDeclaration extends NotationDeclarationEventImpl
{
    final URL _baseURL;
    
    public WNotationDeclaration(final Location loc, final String name, final String pubId, final String sysId, final URL baseURL) {
        super(loc, name, pubId, sysId);
        this._baseURL = baseURL;
    }
    
    @Override
    public String getBaseURI() {
        if (this._baseURL == null) {
            return super.getBaseURI();
        }
        return this._baseURL.toExternalForm();
    }
}
