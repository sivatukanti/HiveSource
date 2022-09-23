// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.evt;

import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.Location;
import javax.xml.stream.events.EntityReference;
import org.codehaus.stax2.ri.evt.EntityReferenceEventImpl;

public class WEntityReference extends EntityReferenceEventImpl implements EntityReference
{
    final String mName;
    
    public WEntityReference(final Location loc, final EntityDeclaration decl) {
        super(loc, decl);
        this.mName = null;
    }
    
    public WEntityReference(final Location loc, final String name) {
        super(loc, (EntityDeclaration)null);
        this.mName = name;
    }
    
    @Override
    public String getName() {
        if (this.mName != null) {
            return this.mName;
        }
        return super.getName();
    }
}
