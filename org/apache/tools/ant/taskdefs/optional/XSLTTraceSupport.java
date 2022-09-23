// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional;

import org.apache.tools.ant.taskdefs.XSLTProcess;
import javax.xml.transform.Transformer;

public interface XSLTTraceSupport
{
    void configureTrace(final Transformer p0, final XSLTProcess.TraceConfiguration p1);
}
