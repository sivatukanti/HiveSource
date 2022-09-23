// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.spi;

import java.util.EventListener;

public interface RegisterClassListener extends EventListener
{
    void registerClass(final RegisterClassEvent p0);
}
