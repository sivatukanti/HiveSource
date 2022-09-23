// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.io;

import javax.xml.stream.Location;
import java.net.URL;
import java.io.Reader;
import com.ctc.wstx.api.ReaderConfig;

public final class InputSourceFactory
{
    public static ReaderSource constructEntitySource(final ReaderConfig cfg, final WstxInputSource parent, final String entityName, final InputBootstrapper bs, final String pubId, final SystemId sysId, final int xmlVersion, final Reader r) {
        final ReaderSource rs = new ReaderSource(cfg, parent, entityName, pubId, sysId, r, true);
        if (bs != null) {
            rs.setInputOffsets(bs.getInputTotal(), bs.getInputRow(), -bs.getInputColumn());
        }
        return rs;
    }
    
    public static BranchingReaderSource constructDocumentSource(final ReaderConfig cfg, final InputBootstrapper bs, final String pubId, SystemId sysId, final Reader r, final boolean realClose) {
        final URL url = cfg.getBaseURL();
        if (url != null) {
            sysId = SystemId.construct(url);
        }
        final BranchingReaderSource rs = new BranchingReaderSource(cfg, pubId, sysId, r, realClose);
        if (bs != null) {
            rs.setInputOffsets(bs.getInputTotal(), bs.getInputRow(), -bs.getInputColumn());
        }
        return rs;
    }
    
    public static WstxInputSource constructCharArraySource(final WstxInputSource parent, final String fromEntity, final char[] text, final int offset, final int len, final Location loc, final URL src) {
        final SystemId sysId = SystemId.construct(loc.getSystemId(), src);
        return new CharArraySource(parent, fromEntity, text, offset, len, loc, sysId);
    }
}
