// 
// Decompiled by Procyon v0.5.36
// 

package org.xbill.DNS;

public class NSIDOption extends GenericEDNSOption
{
    private static final long serialVersionUID = 74739759292589056L;
    
    NSIDOption() {
        super(3);
    }
    
    public NSIDOption(final byte[] data) {
        super(3, data);
    }
}
