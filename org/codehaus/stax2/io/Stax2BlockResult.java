// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.io;

import java.io.OutputStream;
import java.io.IOException;
import java.io.Writer;

public abstract class Stax2BlockResult extends Stax2Result
{
    protected Stax2BlockResult() {
    }
    
    @Override
    public abstract Writer constructWriter() throws IOException;
    
    @Override
    public abstract OutputStream constructOutputStream() throws IOException;
}
