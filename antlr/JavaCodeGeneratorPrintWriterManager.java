// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import java.util.Map;
import java.io.IOException;
import java.io.PrintWriter;

public interface JavaCodeGeneratorPrintWriterManager
{
    PrintWriter setupOutput(final Tool p0, final Grammar p1) throws IOException;
    
    PrintWriter setupOutput(final Tool p0, final String p1) throws IOException;
    
    void startMapping(final int p0);
    
    void startSingleSourceLineMapping(final int p0);
    
    void endMapping();
    
    void finishOutput() throws IOException;
    
    Map getSourceMaps();
}
