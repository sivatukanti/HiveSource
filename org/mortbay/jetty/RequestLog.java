// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty;

import org.mortbay.component.LifeCycle;

public interface RequestLog extends LifeCycle
{
    void log(final Request p0, final Response p1);
}
