// 
// Decompiled by Procyon v0.5.36
// 

package javax.xml.bind;

import java.security.BasicPermission;

public final class JAXBPermission extends BasicPermission
{
    private static final long serialVersionUID = 1L;
    
    public JAXBPermission(final String name) {
        super(name);
    }
}
