// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.evt;

import java.util.Collection;
import java.util.ArrayList;
import javax.xml.stream.Location;
import javax.xml.stream.events.NotationDeclaration;
import javax.xml.stream.events.EntityDeclaration;
import java.util.List;
import com.ctc.wstx.dtd.DTDSubset;
import org.codehaus.stax2.ri.evt.DTDEventImpl;

public class WDTD extends DTDEventImpl
{
    final DTDSubset mSubset;
    List<EntityDeclaration> mEntities;
    List<NotationDeclaration> mNotations;
    
    public WDTD(final Location loc, final String rootName, final String sysId, final String pubId, final String intSubset, final DTDSubset dtdSubset) {
        super(loc, rootName, sysId, pubId, intSubset, dtdSubset);
        this.mEntities = null;
        this.mNotations = null;
        this.mSubset = dtdSubset;
    }
    
    public WDTD(final Location loc, final String rootName, final String sysId, final String pubId, final String intSubset) {
        this(loc, rootName, sysId, pubId, intSubset, null);
    }
    
    public WDTD(final Location loc, final String rootName, final String intSubset) {
        this(loc, rootName, null, null, intSubset, null);
    }
    
    public WDTD(final Location loc, final String fullText) {
        super(loc, fullText);
        this.mEntities = null;
        this.mNotations = null;
        this.mSubset = null;
    }
    
    @Override
    public List<EntityDeclaration> getEntities() {
        if (this.mEntities == null && this.mSubset != null) {
            this.mEntities = new ArrayList<EntityDeclaration>(this.mSubset.getGeneralEntityList());
        }
        return this.mEntities;
    }
    
    @Override
    public List<NotationDeclaration> getNotations() {
        if (this.mNotations == null && this.mSubset != null) {
            this.mNotations = new ArrayList<NotationDeclaration>(this.mSubset.getNotationList());
        }
        return this.mNotations;
    }
}
