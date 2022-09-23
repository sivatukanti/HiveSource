// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.resources;

import java.io.IOException;
import java.io.OutputStream;

public interface Appendable
{
    OutputStream getAppendOutputStream() throws IOException;
}
