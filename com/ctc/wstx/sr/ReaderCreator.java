// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.sr;

import com.ctc.wstx.util.SymbolTable;
import com.ctc.wstx.dtd.DTDSubset;
import com.ctc.wstx.dtd.DTDId;

public interface ReaderCreator
{
    DTDSubset findCachedDTD(final DTDId p0);
    
    void updateSymbolTable(final SymbolTable p0);
    
    void addCachedDTD(final DTDId p0, final DTDSubset p1);
}
