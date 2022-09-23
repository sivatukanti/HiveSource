// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import java.io.PrintWriter;

public class CSharpNameSpace extends NameSpace
{
    public CSharpNameSpace(final String s) {
        super(s);
    }
    
    void emitDeclarations(final PrintWriter printWriter) {
        printWriter.println("namespace " + this.getName());
        printWriter.println("{");
    }
    
    void emitClosures(final PrintWriter printWriter) {
        printWriter.println("}");
    }
}
