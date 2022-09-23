// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import java.util.Enumeration;
import java.io.PrintWriter;
import java.util.StringTokenizer;
import java.util.Vector;

public class NameSpace
{
    private Vector names;
    private String _name;
    
    public NameSpace(final String original) {
        this.names = new Vector();
        this._name = new String(original);
        this.parse(original);
    }
    
    public String getName() {
        return this._name;
    }
    
    protected void parse(final String str) {
        final StringTokenizer stringTokenizer = new StringTokenizer(str, "::");
        while (stringTokenizer.hasMoreTokens()) {
            this.names.addElement(stringTokenizer.nextToken());
        }
    }
    
    void emitDeclarations(final PrintWriter printWriter) {
        final Enumeration<String> elements = this.names.elements();
        while (elements.hasMoreElements()) {
            printWriter.println("ANTLR_BEGIN_NAMESPACE(" + elements.nextElement() + ")");
        }
    }
    
    void emitClosures(final PrintWriter printWriter) {
        for (int i = 0; i < this.names.size(); ++i) {
            printWriter.println("ANTLR_END_NAMESPACE");
        }
    }
}
