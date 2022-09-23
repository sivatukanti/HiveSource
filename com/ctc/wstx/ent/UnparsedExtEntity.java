// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.ent;

import com.ctc.wstx.api.ReaderConfig;
import javax.xml.stream.XMLResolver;
import com.ctc.wstx.io.WstxInputSource;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import javax.xml.stream.Location;

public class UnparsedExtEntity extends ExtEntity
{
    final String mNotationId;
    
    public UnparsedExtEntity(final Location loc, final String name, final URL ctxt, final String pubId, final String sysId, final String notationId) {
        super(loc, name, ctxt, pubId, sysId);
        this.mNotationId = notationId;
    }
    
    @Override
    public String getNotationName() {
        return this.mNotationId;
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
        w.write("\" NDATA ");
        w.write(this.mNotationId);
        w.write(62);
    }
    
    @Override
    public boolean isParsed() {
        return false;
    }
    
    @Override
    public WstxInputSource expand(final WstxInputSource parent, final XMLResolver res, final ReaderConfig cfg, final int xmlVersion) {
        throw new IllegalStateException("Internal error: createInputSource() called for unparsed (external) entity.");
    }
}
