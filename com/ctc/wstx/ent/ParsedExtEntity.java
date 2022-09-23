// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.ent;

import javax.xml.stream.XMLStreamException;
import com.ctc.wstx.io.DefaultInputResolver;
import com.ctc.wstx.api.ReaderConfig;
import javax.xml.stream.XMLResolver;
import com.ctc.wstx.io.WstxInputSource;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import javax.xml.stream.Location;

public class ParsedExtEntity extends ExtEntity
{
    public ParsedExtEntity(final Location loc, final String name, final URL ctxt, final String pubId, final String sysId) {
        super(loc, name, ctxt, pubId, sysId);
    }
    
    @Override
    public String getNotationName() {
        return null;
    }
    
    @Override
    public void writeEnc(final Writer w) throws IOException {
        w.write("<!ENTITY ");
        w.write(this.mName);
        final String pubId = this.getPublicId();
        if (pubId != null) {
            w.write("PUBLIC \"");
            w.write(pubId);
            w.write("\" ");
        }
        else {
            w.write("SYSTEM ");
        }
        w.write(34);
        w.write(this.getSystemId());
        w.write("\">");
    }
    
    @Override
    public boolean isParsed() {
        return true;
    }
    
    @Override
    public WstxInputSource expand(final WstxInputSource parent, final XMLResolver res, final ReaderConfig cfg, int xmlVersion) throws IOException, XMLStreamException {
        if (xmlVersion == 0) {
            xmlVersion = 256;
        }
        return DefaultInputResolver.resolveEntity(parent, this.mContext, this.mName, this.getPublicId(), this.getSystemId(), res, cfg, xmlVersion);
    }
}
