// 
// Decompiled by Procyon v0.5.36
// 

package antlr.preprocessor;

import java.io.File;
import java.util.Enumeration;
import java.io.IOException;
import java.io.FileNotFoundException;
import antlr.collections.impl.Vector;

public class Tool
{
    protected Hierarchy theHierarchy;
    protected String grammarFileName;
    protected String[] args;
    protected int nargs;
    protected Vector grammars;
    protected antlr.Tool antlrTool;
    
    public Tool(final antlr.Tool antlrTool, final String[] array) {
        this.antlrTool = antlrTool;
        this.processArguments(array);
    }
    
    public static void main(final String[] array) {
        final Tool tool = new Tool(new antlr.Tool(), array);
        tool.preprocess();
        final String[] preprocessedArgList = tool.preprocessedArgList();
        for (int i = 0; i < preprocessedArgList.length; ++i) {
            System.out.print(" " + preprocessedArgList[i]);
        }
        System.out.println();
    }
    
    public boolean preprocess() {
        if (this.grammarFileName == null) {
            this.antlrTool.toolError("no grammar file specified");
            return false;
        }
        if (this.grammars != null) {
            this.theHierarchy = new Hierarchy(this.antlrTool);
            final Enumeration elements = this.grammars.elements();
            while (elements.hasMoreElements()) {
                final String str = elements.nextElement();
                try {
                    this.theHierarchy.readGrammarFile(str);
                    continue;
                }
                catch (FileNotFoundException ex) {
                    this.antlrTool.toolError("file " + str + " not found");
                    return false;
                }
                break;
            }
        }
        if (!this.theHierarchy.verifyThatHierarchyIsComplete()) {
            return false;
        }
        this.theHierarchy.expandGrammarsInFile(this.grammarFileName);
        final GrammarFile file = this.theHierarchy.getFile(this.grammarFileName);
        final String nameForExpandedGrammarFile = file.nameForExpandedGrammarFile(this.grammarFileName);
        if (nameForExpandedGrammarFile.equals(this.grammarFileName)) {
            this.args[this.nargs++] = this.grammarFileName;
        }
        else {
            try {
                file.generateExpandedFile();
                this.args[this.nargs++] = this.antlrTool.getOutputDirectory() + System.getProperty("file.separator") + nameForExpandedGrammarFile;
            }
            catch (IOException ex2) {
                this.antlrTool.toolError("cannot write expanded grammar file " + nameForExpandedGrammarFile);
                return false;
            }
        }
        return true;
    }
    
    public String[] preprocessedArgList() {
        final String[] args = new String[this.nargs];
        System.arraycopy(this.args, 0, args, 0, this.nargs);
        return this.args = args;
    }
    
    private void processArguments(final String[] array) {
        this.nargs = 0;
        this.args = new String[array.length];
        for (int i = 0; i < array.length; ++i) {
            if (array[i].length() == 0) {
                this.antlrTool.warning("Zero length argument ignoring...");
            }
            else if (array[i].equals("-glib")) {
                if (File.separator.equals("\\") && array[i].indexOf(47) != -1) {
                    this.antlrTool.warning("-glib cannot deal with '/' on a PC: use '\\'; ignoring...");
                }
                else {
                    final antlr.Tool antlrTool = this.antlrTool;
                    this.grammars = antlr.Tool.parseSeparatedList(array[i + 1], ';');
                    ++i;
                }
            }
            else if (array[i].equals("-o")) {
                this.args[this.nargs++] = array[i];
                if (i + 1 >= array.length) {
                    this.antlrTool.error("missing output directory with -o option; ignoring");
                }
                else {
                    ++i;
                    this.args[this.nargs++] = array[i];
                    this.antlrTool.setOutputDirectory(array[i]);
                }
            }
            else if (array[i].charAt(0) == '-') {
                this.args[this.nargs++] = array[i];
            }
            else {
                this.grammarFileName = array[i];
                if (this.grammars == null) {
                    this.grammars = new Vector(10);
                }
                this.grammars.appendElement(this.grammarFileName);
                if (i + 1 < array.length) {
                    this.antlrTool.warning("grammar file must be last; ignoring other arguments...");
                    break;
                }
            }
        }
    }
}
