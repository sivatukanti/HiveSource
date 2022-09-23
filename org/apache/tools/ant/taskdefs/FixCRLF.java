// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.types.EnumeratedAttribute;
import java.util.NoSuchElementException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.Enumeration;
import org.apache.tools.ant.types.FilterSetCollection;
import org.apache.tools.ant.DirectoryScanner;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import java.io.Reader;
import org.apache.tools.ant.types.FilterChain;
import java.util.Vector;
import org.apache.tools.ant.filters.FixCrLfFilter;
import java.io.File;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.filters.ChainableReader;

public class FixCRLF extends MatchingTask implements ChainableReader
{
    private static final String FIXCRLF_ERROR = "<fixcrlf> error: ";
    public static final String ERROR_FILE_AND_SRCDIR = "<fixcrlf> error: srcdir and file are mutually exclusive";
    private static final FileUtils FILE_UTILS;
    private boolean preserveLastModified;
    private File srcDir;
    private File destDir;
    private File file;
    private FixCrLfFilter filter;
    private Vector<FilterChain> fcv;
    private String encoding;
    private String outputEncoding;
    
    public FixCRLF() {
        this.preserveLastModified = false;
        this.destDir = null;
        this.filter = new FixCrLfFilter();
        this.fcv = null;
        this.encoding = null;
        this.outputEncoding = null;
    }
    
    public final Reader chain(final Reader rdr) {
        return this.filter.chain(rdr);
    }
    
    public void setSrcdir(final File srcDir) {
        this.srcDir = srcDir;
    }
    
    public void setDestdir(final File destDir) {
        this.destDir = destDir;
    }
    
    public void setJavafiles(final boolean javafiles) {
        this.filter.setJavafiles(javafiles);
    }
    
    public void setFile(final File file) {
        this.file = file;
    }
    
    public void setEol(final CrLf attr) {
        this.filter.setEol(FixCrLfFilter.CrLf.newInstance(attr.getValue()));
    }
    
    @Deprecated
    public void setCr(final AddAsisRemove attr) {
        this.log("DEPRECATED: The cr attribute has been deprecated,", 1);
        this.log("Please use the eol attribute instead", 1);
        final String option = attr.getValue();
        final CrLf c = new CrLf();
        if (option.equals("remove")) {
            c.setValue("lf");
        }
        else if (option.equals("asis")) {
            c.setValue("asis");
        }
        else {
            c.setValue("crlf");
        }
        this.setEol(c);
    }
    
    public void setTab(final AddAsisRemove attr) {
        this.filter.setTab(FixCrLfFilter.AddAsisRemove.newInstance(attr.getValue()));
    }
    
    public void setTablength(final int tlength) throws BuildException {
        try {
            this.filter.setTablength(tlength);
        }
        catch (IOException e) {
            throw new BuildException(e.getMessage(), e);
        }
    }
    
    public void setEof(final AddAsisRemove attr) {
        this.filter.setEof(FixCrLfFilter.AddAsisRemove.newInstance(attr.getValue()));
    }
    
    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }
    
    public void setOutputEncoding(final String outputEncoding) {
        this.outputEncoding = outputEncoding;
    }
    
    public void setFixlast(final boolean fixlast) {
        this.filter.setFixlast(fixlast);
    }
    
    public void setPreserveLastModified(final boolean preserve) {
        this.preserveLastModified = preserve;
    }
    
    @Override
    public void execute() throws BuildException {
        this.validate();
        final String enc = (this.encoding == null) ? "default" : this.encoding;
        this.log("options: eol=" + this.filter.getEol().getValue() + " tab=" + this.filter.getTab().getValue() + " eof=" + this.filter.getEof().getValue() + " tablength=" + this.filter.getTablength() + " encoding=" + enc + " outputencoding=" + ((this.outputEncoding == null) ? enc : this.outputEncoding), 3);
        final DirectoryScanner ds = super.getDirectoryScanner(this.srcDir);
        final String[] files = ds.getIncludedFiles();
        for (int i = 0; i < files.length; ++i) {
            this.processFile(files[i]);
        }
    }
    
    private void validate() throws BuildException {
        if (this.file != null) {
            if (this.srcDir != null) {
                throw new BuildException("<fixcrlf> error: srcdir and file are mutually exclusive");
            }
            this.fileset.setFile(this.file);
            this.srcDir = this.file.getParentFile();
        }
        if (this.srcDir == null) {
            throw new BuildException("<fixcrlf> error: srcdir attribute must be set!");
        }
        if (!this.srcDir.exists()) {
            throw new BuildException("<fixcrlf> error: srcdir does not exist: '" + this.srcDir + "'");
        }
        if (!this.srcDir.isDirectory()) {
            throw new BuildException("<fixcrlf> error: srcdir is not a directory: '" + this.srcDir + "'");
        }
        if (this.destDir != null) {
            if (!this.destDir.exists()) {
                throw new BuildException("<fixcrlf> error: destdir does not exist: '" + this.destDir + "'");
            }
            if (!this.destDir.isDirectory()) {
                throw new BuildException("<fixcrlf> error: destdir is not a directory: '" + this.destDir + "'");
            }
        }
    }
    
    private void processFile(final String file) throws BuildException {
        final File srcFile = new File(this.srcDir, file);
        final long lastModified = srcFile.lastModified();
        final File destD = (this.destDir == null) ? this.srcDir : this.destDir;
        if (this.fcv == null) {
            final FilterChain fc = new FilterChain();
            fc.add(this.filter);
            (this.fcv = new Vector<FilterChain>(1)).add(fc);
        }
        final File tmpFile = FixCRLF.FILE_UTILS.createTempFile("fixcrlf", "", null, true, false);
        try {
            FixCRLF.FILE_UTILS.copyFile(srcFile, tmpFile, null, this.fcv, false, false, this.encoding, (this.outputEncoding == null) ? this.encoding : this.outputEncoding, this.getProject());
            final File destFile = new File(destD, file);
            boolean destIsWrong = true;
            if (destFile.exists()) {
                this.log("destFile " + destFile + " exists", 4);
                destIsWrong = !FixCRLF.FILE_UTILS.contentEquals(destFile, tmpFile);
                this.log(destFile + (destIsWrong ? " is being written" : " is not written, as the contents are identical"), 4);
            }
            if (destIsWrong) {
                FixCRLF.FILE_UTILS.rename(tmpFile, destFile);
                if (this.preserveLastModified) {
                    this.log("preserved lastModified for " + destFile, 4);
                    FixCRLF.FILE_UTILS.setFileLastModified(destFile, lastModified);
                }
            }
        }
        catch (IOException e) {
            throw new BuildException("error running fixcrlf on file " + srcFile, e);
        }
        finally {
            if (tmpFile != null && tmpFile.exists()) {
                FixCRLF.FILE_UTILS.tryHardToDelete(tmpFile);
            }
        }
    }
    
    static {
        FILE_UTILS = FileUtils.getFileUtils();
    }
    
    protected class OneLiner implements Enumeration<Object>
    {
        private static final int UNDEF = -1;
        private static final int NOTJAVA = 0;
        private static final int LOOKING = 1;
        private static final int INBUFLEN = 8192;
        private static final int LINEBUFLEN = 200;
        private static final char CTRLZ = '\u001a';
        private int state;
        private StringBuffer eolStr;
        private StringBuffer eofStr;
        private BufferedReader reader;
        private StringBuffer line;
        private boolean reachedEof;
        private File srcFile;
        
        public OneLiner(final File srcFile) throws BuildException {
            this.state = (FixCRLF.this.filter.getJavafiles() ? 1 : 0);
            this.eolStr = new StringBuffer(200);
            this.eofStr = new StringBuffer();
            this.line = new StringBuffer();
            this.reachedEof = false;
            this.srcFile = srcFile;
            try {
                InputStreamReader in;
                if (FixCRLF.this.encoding == null) {
                    in = new FileReader(srcFile);
                }
                else {
                    final FileInputStream in2;
                    in = new InputStreamReader(in2, FixCRLF.this.encoding);
                    in2 = new FileInputStream(srcFile);
                }
                this.reader = new BufferedReader(in, 8192);
                this.nextLine();
            }
            catch (IOException e) {
                throw new BuildException(srcFile + ": " + e.getMessage(), e, FixCRLF.this.getLocation());
            }
        }
        
        protected void nextLine() throws BuildException {
            int ch = -1;
            int eolcount = 0;
            this.eolStr = new StringBuffer();
            this.line = new StringBuffer();
            try {
                for (ch = this.reader.read(); ch != -1 && ch != 13 && ch != 10; ch = this.reader.read()) {
                    this.line.append((char)ch);
                }
                if (ch == -1 && this.line.length() == 0) {
                    this.reachedEof = true;
                    return;
                }
                Label_0270: {
                    switch ((char)ch) {
                        case '\r': {
                            ++eolcount;
                            this.eolStr.append('\r');
                            this.reader.mark(2);
                            ch = this.reader.read();
                            switch (ch) {
                                case 13: {
                                    ch = this.reader.read();
                                    if ((char)ch == '\n') {
                                        eolcount += 2;
                                        this.eolStr.append("\r\n");
                                        break Label_0270;
                                    }
                                    this.reader.reset();
                                    break Label_0270;
                                }
                                case 10: {
                                    ++eolcount;
                                    this.eolStr.append('\n');
                                    break Label_0270;
                                }
                                case -1: {
                                    break Label_0270;
                                }
                                default: {
                                    this.reader.reset();
                                    break Label_0270;
                                }
                            }
                            break;
                        }
                        case '\n': {
                            ++eolcount;
                            this.eolStr.append('\n');
                            break;
                        }
                    }
                }
                if (eolcount == 0) {
                    int i = this.line.length();
                    while (--i >= 0 && this.line.charAt(i) == '\u001a') {}
                    if (i < this.line.length() - 1) {
                        this.eofStr.append(this.line.toString().substring(i + 1));
                        if (i < 0) {
                            this.line.setLength(0);
                            this.reachedEof = true;
                        }
                        else {
                            this.line.setLength(i + 1);
                        }
                    }
                }
            }
            catch (IOException e) {
                throw new BuildException(this.srcFile + ": " + e.getMessage(), e, FixCRLF.this.getLocation());
            }
        }
        
        public String getEofStr() {
            return this.eofStr.substring(0);
        }
        
        public int getState() {
            return this.state;
        }
        
        public void setState(final int state) {
            this.state = state;
        }
        
        public boolean hasMoreElements() {
            return !this.reachedEof;
        }
        
        public Object nextElement() throws NoSuchElementException {
            if (!this.hasMoreElements()) {
                throw new NoSuchElementException("OneLiner");
            }
            final BufferLine tmpLine = new BufferLine(this.line.toString(), this.eolStr.substring(0));
            this.nextLine();
            return tmpLine;
        }
        
        public void close() throws IOException {
            if (this.reader != null) {
                this.reader.close();
            }
        }
        
        class BufferLine
        {
            private int next;
            private int column;
            private int lookahead;
            private String line;
            private String eolStr;
            
            public BufferLine(final String line, final String eolStr) throws BuildException {
                this.next = 0;
                this.column = 0;
                this.lookahead = -1;
                this.next = 0;
                this.column = 0;
                this.line = line;
                this.eolStr = eolStr;
            }
            
            public int getNext() {
                return this.next;
            }
            
            public void setNext(final int next) {
                this.next = next;
            }
            
            public int getLookahead() {
                return this.lookahead;
            }
            
            public void setLookahead(final int lookahead) {
                this.lookahead = lookahead;
            }
            
            public char getChar(final int i) {
                return this.line.charAt(i);
            }
            
            public char getNextChar() {
                return this.getChar(this.next);
            }
            
            public char getNextCharInc() {
                return this.getChar(this.next++);
            }
            
            public int getColumn() {
                return this.column;
            }
            
            public void setColumn(final int col) {
                this.column = col;
            }
            
            public int incColumn() {
                return this.column++;
            }
            
            public int length() {
                return this.line.length();
            }
            
            public int getEolLength() {
                return this.eolStr.length();
            }
            
            public String getLineString() {
                return this.line;
            }
            
            public String getEol() {
                return this.eolStr;
            }
            
            public String substring(final int begin) {
                return this.line.substring(begin);
            }
            
            public String substring(final int begin, final int end) {
                return this.line.substring(begin, end);
            }
            
            public void setState(final int state) {
                OneLiner.this.setState(state);
            }
            
            public int getState() {
                return OneLiner.this.getState();
            }
        }
    }
    
    public static class AddAsisRemove extends EnumeratedAttribute
    {
        @Override
        public String[] getValues() {
            return new String[] { "add", "asis", "remove" };
        }
    }
    
    public static class CrLf extends EnumeratedAttribute
    {
        @Override
        public String[] getValues() {
            return new String[] { "asis", "cr", "lf", "crlf", "mac", "unix", "dos" };
        }
    }
}
