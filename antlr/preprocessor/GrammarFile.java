// 
// Decompiled by Procyon v0.5.36
// 

package antlr.preprocessor;

import java.util.Enumeration;
import java.io.IOException;
import java.io.PrintWriter;
import antlr.Tool;
import antlr.collections.impl.IndexedVector;

public class GrammarFile
{
    protected String fileName;
    protected String headerAction;
    protected IndexedVector options;
    protected IndexedVector grammars;
    protected boolean expanded;
    protected Tool tool;
    
    public GrammarFile(final Tool tool, final String fileName) {
        this.headerAction = "";
        this.expanded = false;
        this.fileName = fileName;
        this.grammars = new IndexedVector();
        this.tool = tool;
    }
    
    public void addGrammar(final Grammar grammar) {
        this.grammars.appendElement(grammar.getName(), grammar);
    }
    
    public void generateExpandedFile() throws IOException {
        if (!this.expanded) {
            return;
        }
        final PrintWriter openOutputFile = this.tool.openOutputFile(this.nameForExpandedGrammarFile(this.getName()));
        openOutputFile.println(this.toString());
        openOutputFile.close();
    }
    
    public IndexedVector getGrammars() {
        return this.grammars;
    }
    
    public String getName() {
        return this.fileName;
    }
    
    public String nameForExpandedGrammarFile(final String s) {
        if (this.expanded) {
            return "expanded" + this.tool.fileMinusPath(s);
        }
        return s;
    }
    
    public void setExpanded(final boolean expanded) {
        this.expanded = expanded;
    }
    
    public void addHeaderAction(final String str) {
        this.headerAction = this.headerAction + str + System.getProperty("line.separator");
    }
    
    public void setOptions(final IndexedVector options) {
        this.options = options;
    }
    
    public String toString() {
        final String str = (this.headerAction == null) ? "" : this.headerAction;
        final String str2 = (this.options == null) ? "" : Hierarchy.optionsToString(this.options);
        final StringBuffer sb = new StringBuffer(10000);
        sb.append(str);
        sb.append(str2);
        final Enumeration elements = this.grammars.elements();
        while (elements.hasMoreElements()) {
            sb.append(elements.nextElement().toString());
        }
        return sb.toString();
    }
}
