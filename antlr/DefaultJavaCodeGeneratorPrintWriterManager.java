// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import java.io.Writer;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.io.PrintWriter;

public class DefaultJavaCodeGeneratorPrintWriterManager implements JavaCodeGeneratorPrintWriterManager
{
    private Grammar grammar;
    private PrintWriterWithSMAP smapOutput;
    private PrintWriter currentOutput;
    private Tool tool;
    private Map sourceMaps;
    private String currentFileName;
    
    public DefaultJavaCodeGeneratorPrintWriterManager() {
        this.sourceMaps = new HashMap();
    }
    
    public PrintWriter setupOutput(final Tool tool, final Grammar grammar) throws IOException {
        return this.setupOutput(tool, grammar, null);
    }
    
    public PrintWriter setupOutput(final Tool tool, final String s) throws IOException {
        return this.setupOutput(tool, null, s);
    }
    
    public PrintWriter setupOutput(final Tool tool, final Grammar grammar, String className) throws IOException {
        this.tool = tool;
        this.grammar = grammar;
        if (className == null) {
            className = grammar.getClassName();
        }
        this.smapOutput = new PrintWriterWithSMAP(tool.openOutputFile(className + ".java"));
        this.currentFileName = className + ".java";
        return this.currentOutput = this.smapOutput;
    }
    
    public void startMapping(final int n) {
        this.smapOutput.startMapping(n);
    }
    
    public void startSingleSourceLineMapping(final int n) {
        this.smapOutput.startSingleSourceLineMapping(n);
    }
    
    public void endMapping() {
        this.smapOutput.endMapping();
    }
    
    public void finishOutput() throws IOException {
        this.currentOutput.close();
        if (this.grammar != null) {
            final PrintWriter openOutputFile = this.tool.openOutputFile(this.grammar.getClassName() + ".smap");
            String s = this.grammar.getFilename().replace('\\', '/');
            final int lastIndex = s.lastIndexOf(47);
            if (lastIndex != -1) {
                s = s.substring(lastIndex + 1);
            }
            this.smapOutput.dump(openOutputFile, this.grammar.getClassName(), s);
            this.sourceMaps.put(this.currentFileName, this.smapOutput.getSourceMap());
        }
        this.currentOutput = null;
    }
    
    public Map getSourceMaps() {
        return this.sourceMaps;
    }
    
    public int getCurrentOutputLine() {
        return this.smapOutput.getCurrentOutputLine();
    }
}
