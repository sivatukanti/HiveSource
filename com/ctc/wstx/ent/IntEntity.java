// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.ent;

import com.ctc.wstx.io.InputSourceFactory;
import com.ctc.wstx.api.ReaderConfig;
import javax.xml.stream.XMLResolver;
import com.ctc.wstx.io.WstxInputSource;
import com.ctc.wstx.io.TextEscaper;
import java.io.IOException;
import java.io.Writer;
import com.ctc.wstx.io.WstxInputLocation;
import java.net.URL;
import javax.xml.stream.Location;

public class IntEntity extends EntityDecl
{
    protected final Location mContentLocation;
    final char[] mRepl;
    String mReplText;
    
    public IntEntity(final Location loc, final String name, final URL ctxt, final char[] repl, final Location defLoc) {
        super(loc, name, ctxt);
        this.mReplText = null;
        this.mRepl = repl;
        this.mContentLocation = defLoc;
    }
    
    public static IntEntity create(final String id, final String repl) {
        return create(id, repl.toCharArray());
    }
    
    public static IntEntity create(final String id, final char[] val) {
        final WstxInputLocation loc = WstxInputLocation.getEmptyLocation();
        return new IntEntity(loc, id, null, val, loc);
    }
    
    @Override
    public String getNotationName() {
        return null;
    }
    
    @Override
    public String getPublicId() {
        return null;
    }
    
    @Override
    public String getReplacementText() {
        String repl = this.mReplText;
        if (repl == null) {
            repl = ((this.mRepl.length == 0) ? "" : new String(this.mRepl));
            this.mReplText = repl;
        }
        return this.mReplText;
    }
    
    @Override
    public int getReplacementText(final Writer w) throws IOException {
        w.write(this.mRepl);
        return this.mRepl.length;
    }
    
    @Override
    public String getSystemId() {
        return null;
    }
    
    @Override
    public void writeEnc(final Writer w) throws IOException {
        w.write("<!ENTITY ");
        w.write(this.mName);
        w.write(" \"");
        TextEscaper.outputDTDText(w, this.mRepl, 0, this.mRepl.length);
        w.write("\">");
    }
    
    @Override
    public char[] getReplacementChars() {
        return this.mRepl;
    }
    
    @Override
    public boolean isExternal() {
        return false;
    }
    
    @Override
    public boolean isParsed() {
        return true;
    }
    
    @Override
    public WstxInputSource expand(final WstxInputSource parent, final XMLResolver res, final ReaderConfig cfg, final int xmlVersion) {
        return InputSourceFactory.constructCharArraySource(parent, this.mName, this.mRepl, 0, this.mRepl.length, this.mContentLocation, null);
    }
}
