// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import java.util.StringTokenizer;
import antlr.collections.impl.Vector;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.InputStreamReader;
import antlr.collections.impl.BitSet;
import java.io.Reader;

public class Tool
{
    public static String version;
    ToolErrorHandler errorHandler;
    protected boolean hasError;
    boolean genDiagnostics;
    boolean genDocBook;
    boolean genHTML;
    protected String outputDir;
    protected String grammarFile;
    transient Reader f;
    protected String literalsPrefix;
    protected boolean upperCaseMangledLiterals;
    protected NameSpace nameSpace;
    protected String namespaceAntlr;
    protected String namespaceStd;
    protected boolean genHashLines;
    protected boolean noConstructors;
    private BitSet cmdLineArgValid;
    
    public Tool() {
        this.hasError = false;
        this.genDiagnostics = false;
        this.genDocBook = false;
        this.genHTML = false;
        this.outputDir = ".";
        this.f = new InputStreamReader(System.in);
        this.literalsPrefix = "LITERAL_";
        this.upperCaseMangledLiterals = false;
        this.nameSpace = null;
        this.namespaceAntlr = null;
        this.namespaceStd = null;
        this.genHashLines = true;
        this.noConstructors = false;
        this.cmdLineArgValid = new BitSet();
        this.errorHandler = new DefaultToolErrorHandler(this);
    }
    
    public String getGrammarFile() {
        return this.grammarFile;
    }
    
    public boolean hasError() {
        return this.hasError;
    }
    
    public NameSpace getNameSpace() {
        return this.nameSpace;
    }
    
    public String getNamespaceStd() {
        return this.namespaceStd;
    }
    
    public String getNamespaceAntlr() {
        return this.namespaceAntlr;
    }
    
    public boolean getGenHashLines() {
        return this.genHashLines;
    }
    
    public String getLiteralsPrefix() {
        return this.literalsPrefix;
    }
    
    public boolean getUpperCaseMangledLiterals() {
        return this.upperCaseMangledLiterals;
    }
    
    public void setFileLineFormatter(final FileLineFormatter formatter) {
        FileLineFormatter.setFormatter(formatter);
    }
    
    protected void checkForInvalidArguments(final String[] array, final BitSet set) {
        for (int i = 0; i < array.length; ++i) {
            if (!set.member(i)) {
                this.warning("invalid command-line argument: " + array[i] + "; ignored");
            }
        }
    }
    
    public void copyFile(final String str, final String str2) throws IOException {
        final File file = new File(str);
        final File file2 = new File(str2);
        Reader reader = null;
        Writer writer = null;
        try {
            if (!file.exists() || !file.isFile()) {
                throw new FileCopyException("FileCopy: no such source file: " + str);
            }
            if (!file.canRead()) {
                throw new FileCopyException("FileCopy: source file is unreadable: " + str);
            }
            if (file2.exists()) {
                if (!file2.isFile()) {
                    throw new FileCopyException("FileCopy: destination is not a file: " + str2);
                }
                final DataInputStream dataInputStream = new DataInputStream(System.in);
                if (!file2.canWrite()) {
                    throw new FileCopyException("FileCopy: destination file is unwriteable: " + str2);
                }
            }
            else {
                final File parent = this.parent(file2);
                if (!parent.exists()) {
                    throw new FileCopyException("FileCopy: destination directory doesn't exist: " + str2);
                }
                if (!parent.canWrite()) {
                    throw new FileCopyException("FileCopy: destination directory is unwriteable: " + str2);
                }
            }
            reader = new BufferedReader(new FileReader(file));
            writer = new BufferedWriter(new FileWriter(file2));
            final char[] array = new char[1024];
            while (true) {
                final int read = reader.read(array, 0, 1024);
                if (read == -1) {
                    break;
                }
                writer.write(array, 0, read);
            }
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException ex) {}
            }
            if (writer != null) {
                try {
                    writer.close();
                }
                catch (IOException ex2) {}
            }
        }
    }
    
    public void doEverythingWrapper(final String[] array) {
        System.exit(this.doEverything(array));
    }
    
    public int doEverything(final String[] array) {
        final antlr.preprocessor.Tool tool = new antlr.preprocessor.Tool(this, array);
        final boolean preprocess = tool.preprocess();
        final String[] preprocessedArgList = tool.preprocessedArgList();
        this.processArguments(preprocessedArgList);
        if (!preprocess) {
            return 1;
        }
        this.f = this.getGrammarReader();
        final TokenBuffer tokenBuffer = new TokenBuffer(new ANTLRLexer(this.f));
        final LLkAnalyzer analyzer = new LLkAnalyzer(this);
        final MakeGrammar behavior = new MakeGrammar(this, array, analyzer);
        try {
            final ANTLRParser antlrParser = new ANTLRParser(tokenBuffer, behavior, this);
            antlrParser.setFilename(this.grammarFile);
            antlrParser.grammar();
            if (this.hasError()) {
                this.fatalError("Exiting due to errors.");
            }
            this.checkForInvalidArguments(preprocessedArgList, this.cmdLineArgValid);
            final String string = "antlr." + this.getLanguage(behavior) + "CodeGenerator";
            try {
                final CodeGenerator codeGenerator = (CodeGenerator)Utils.createInstanceOf(string);
                codeGenerator.setBehavior(behavior);
                codeGenerator.setAnalyzer(analyzer);
                codeGenerator.setTool(this);
                codeGenerator.gen();
            }
            catch (ClassNotFoundException ex3) {
                this.panic("Cannot instantiate code-generator: " + string);
            }
            catch (InstantiationException ex4) {
                this.panic("Cannot instantiate code-generator: " + string);
            }
            catch (IllegalArgumentException ex5) {
                this.panic("Cannot instantiate code-generator: " + string);
            }
            catch (IllegalAccessException ex6) {
                this.panic("code-generator class '" + string + "' is not accessible");
            }
        }
        catch (RecognitionException ex) {
            this.fatalError("Unhandled parser error: " + ex.getMessage());
        }
        catch (TokenStreamException ex2) {
            this.fatalError("TokenStreamException: " + ex2.getMessage());
        }
        return 0;
    }
    
    public void error(final String str) {
        this.hasError = true;
        System.err.println("error: " + str);
    }
    
    public void error(final String str, final String s, final int n, final int n2) {
        this.hasError = true;
        System.err.println(FileLineFormatter.getFormatter().getFormatString(s, n, n2) + str);
    }
    
    public String fileMinusPath(final String s) {
        final int lastIndex = s.lastIndexOf(System.getProperty("file.separator"));
        if (lastIndex == -1) {
            return s;
        }
        return s.substring(lastIndex + 1);
    }
    
    public String getLanguage(final MakeGrammar makeGrammar) {
        if (this.genDiagnostics) {
            return "Diagnostic";
        }
        if (this.genHTML) {
            return "HTML";
        }
        if (this.genDocBook) {
            return "DocBook";
        }
        return makeGrammar.language;
    }
    
    public String getOutputDirectory() {
        return this.outputDir;
    }
    
    private static void help() {
        System.err.println("usage: java antlr.Tool [args] file.g");
        System.err.println("  -o outputDir       specify output directory where all output generated.");
        System.err.println("  -glib superGrammar specify location of supergrammar file.");
        System.err.println("  -debug             launch the ParseView debugger upon parser invocation.");
        System.err.println("  -html              generate a html file from your grammar.");
        System.err.println("  -docbook           generate a docbook sgml file from your grammar.");
        System.err.println("  -diagnostic        generate a textfile with diagnostics.");
        System.err.println("  -trace             have all rules call traceIn/traceOut.");
        System.err.println("  -traceLexer        have lexer rules call traceIn/traceOut.");
        System.err.println("  -traceParser       have parser rules call traceIn/traceOut.");
        System.err.println("  -traceTreeParser   have tree parser rules call traceIn/traceOut.");
        System.err.println("  -h|-help|--help    this message");
    }
    
    public static void main(final String[] array) {
        System.err.println("ANTLR Parser Generator   Version 2.7.7 (20060906)   1989-2005");
        Tool.version = "2.7.7 (20060906)";
        try {
            boolean b = false;
            if (array.length == 0) {
                b = true;
            }
            else {
                for (int i = 0; i < array.length; ++i) {
                    if (array[i].equals("-h") || array[i].equals("-help") || array[i].equals("--help")) {
                        b = true;
                        break;
                    }
                }
            }
            if (b) {
                help();
            }
            else {
                new Tool().doEverything(array);
            }
        }
        catch (Exception ex) {
            System.err.println(System.getProperty("line.separator") + System.getProperty("line.separator"));
            System.err.println("#$%%*&@# internal error: " + ex.toString());
            System.err.println("[complain to nearest government official");
            System.err.println(" or send hate-mail to parrt@antlr.org;");
            System.err.println(" please send stack trace with report.]" + System.getProperty("line.separator"));
            ex.printStackTrace();
        }
    }
    
    public PrintWriter openOutputFile(final String str) throws IOException {
        if (this.outputDir != ".") {
            final File file = new File(this.outputDir);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
        return new PrintWriter(new PreservingFileWriter(this.outputDir + System.getProperty("file.separator") + str));
    }
    
    public Reader getGrammarReader() {
        Reader reader = null;
        try {
            if (this.grammarFile != null) {
                reader = new BufferedReader(new FileReader(this.grammarFile));
            }
        }
        catch (IOException ex) {
            this.fatalError("cannot open grammar file " + this.grammarFile);
        }
        return reader;
    }
    
    public void reportException(final Exception ex, final String str) {
        System.err.println((str == null) ? ex.getMessage() : (str + ": " + ex.getMessage()));
    }
    
    public void reportProgress(final String x) {
        System.out.println(x);
    }
    
    public void fatalError(final String x) {
        System.err.println(x);
        Utils.error(x);
    }
    
    public void panic() {
        this.fatalError("panic");
    }
    
    public void panic(final String str) {
        this.fatalError("panic: " + str);
    }
    
    public File parent(final File file) {
        final String parent = file.getParent();
        if (parent != null) {
            return new File(parent);
        }
        if (file.isAbsolute()) {
            return new File(File.separator);
        }
        return new File(System.getProperty("user.dir"));
    }
    
    public static Vector parseSeparatedList(final String str, final char c) {
        final StringTokenizer stringTokenizer = new StringTokenizer(str, String.valueOf(c));
        final Vector vector = new Vector(10);
        while (stringTokenizer.hasMoreTokens()) {
            vector.appendElement(stringTokenizer.nextToken());
        }
        if (vector.size() == 0) {
            return null;
        }
        return vector;
    }
    
    public String pathToFile(final String s) {
        final int lastIndex = s.lastIndexOf(System.getProperty("file.separator"));
        if (lastIndex == -1) {
            return "." + System.getProperty("file.separator");
        }
        return s.substring(0, lastIndex + 1);
    }
    
    protected void processArguments(final String[] array) {
        for (int i = 0; i < array.length; ++i) {
            if (array[i].equals("-diagnostic")) {
                this.genDiagnostics = true;
                this.genHTML = false;
                this.setArgOK(i);
            }
            else if (array[i].equals("-o")) {
                this.setArgOK(i);
                if (i + 1 >= array.length) {
                    this.error("missing output directory with -o option; ignoring");
                }
                else {
                    ++i;
                    this.setOutputDirectory(array[i]);
                    this.setArgOK(i);
                }
            }
            else if (array[i].equals("-html")) {
                this.genHTML = true;
                this.genDiagnostics = false;
                this.setArgOK(i);
            }
            else if (array[i].equals("-docbook")) {
                this.genDocBook = true;
                this.genDiagnostics = false;
                this.setArgOK(i);
            }
            else if (array[i].charAt(0) != '-') {
                this.grammarFile = array[i];
                this.setArgOK(i);
            }
        }
    }
    
    public void setArgOK(final int n) {
        this.cmdLineArgValid.add(n);
    }
    
    public void setOutputDirectory(final String outputDir) {
        this.outputDir = outputDir;
    }
    
    public void toolError(final String str) {
        System.err.println("error: " + str);
    }
    
    public void warning(final String str) {
        System.err.println("warning: " + str);
    }
    
    public void warning(final String str, final String s, final int n, final int n2) {
        System.err.println(FileLineFormatter.getFormatter().getFormatString(s, n, n2) + "warning:" + str);
    }
    
    public void warning(final String[] array, final String s, final int n, final int n2) {
        if (array == null || array.length == 0) {
            this.panic("bad multi-line message to Tool.warning");
        }
        System.err.println(FileLineFormatter.getFormatter().getFormatString(s, n, n2) + "warning:" + array[0]);
        for (int i = 1; i < array.length; ++i) {
            System.err.println(FileLineFormatter.getFormatter().getFormatString(s, n, n2) + "    " + array[i]);
        }
    }
    
    public void setNameSpace(final String s) {
        if (null == this.nameSpace) {
            this.nameSpace = new NameSpace(StringUtils.stripFrontBack(s, "\"", "\""));
        }
    }
    
    static {
        Tool.version = "";
    }
}
