// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import java.util.Arrays;
import org.apache.tools.ant.util.LazyFileOutputStream;
import org.apache.tools.ant.util.LineOrientedOutputStreamRedirector;
import org.apache.tools.ant.util.OutputStreamFunneler;
import org.apache.tools.ant.util.KeepAliveOutputStream;
import java.io.ByteArrayInputStream;
import org.apache.tools.ant.util.ConcatFileInputStream;
import org.apache.tools.ant.BuildException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import org.apache.tools.ant.util.ReaderInputStream;
import org.apache.tools.ant.filters.util.ChainReaderHelper;
import java.io.InputStreamReader;
import org.apache.tools.ant.util.LeadPipeInputStream;
import org.apache.tools.ant.util.TeeOutputStream;
import java.io.IOException;
import org.apache.tools.ant.util.StringUtils;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.StringReader;
import java.io.ByteArrayOutputStream;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FilterChain;
import java.util.Vector;
import java.io.PrintStream;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.tools.ant.ProjectComponent;
import java.io.File;

public class Redirector
{
    private static final int STREAMPUMPER_WAIT_INTERVAL = 1000;
    private static final String DEFAULT_ENCODING;
    private File[] input;
    private File[] out;
    private File[] error;
    private boolean logError;
    private PropertyOutputStream baos;
    private PropertyOutputStream errorBaos;
    private String outputProperty;
    private String errorProperty;
    private String inputString;
    private boolean appendOut;
    private boolean appendErr;
    private boolean alwaysLogOut;
    private boolean alwaysLogErr;
    private boolean createEmptyFilesOut;
    private boolean createEmptyFilesErr;
    private ProjectComponent managingTask;
    private OutputStream outputStream;
    private OutputStream errorStream;
    private InputStream inputStream;
    private PrintStream outPrintStream;
    private PrintStream errorPrintStream;
    private Vector<FilterChain> outputFilterChains;
    private Vector<FilterChain> errorFilterChains;
    private Vector<FilterChain> inputFilterChains;
    private String outputEncoding;
    private String errorEncoding;
    private String inputEncoding;
    private boolean appendProperties;
    private final ThreadGroup threadGroup;
    private boolean logInputString;
    private Object inMutex;
    private Object outMutex;
    private Object errMutex;
    
    public Redirector(final Task managingTask) {
        this((ProjectComponent)managingTask);
    }
    
    public Redirector(final ProjectComponent managingTask) {
        this.logError = false;
        this.baos = null;
        this.errorBaos = null;
        this.appendOut = false;
        this.appendErr = false;
        this.alwaysLogOut = false;
        this.alwaysLogErr = false;
        this.createEmptyFilesOut = true;
        this.createEmptyFilesErr = true;
        this.outputStream = null;
        this.errorStream = null;
        this.inputStream = null;
        this.outPrintStream = null;
        this.errorPrintStream = null;
        this.outputEncoding = Redirector.DEFAULT_ENCODING;
        this.errorEncoding = Redirector.DEFAULT_ENCODING;
        this.inputEncoding = Redirector.DEFAULT_ENCODING;
        this.appendProperties = true;
        this.threadGroup = new ThreadGroup("redirector");
        this.logInputString = true;
        this.inMutex = new Object();
        this.outMutex = new Object();
        this.errMutex = new Object();
        this.managingTask = managingTask;
    }
    
    public void setInput(final File input) {
        this.setInput((File[])((input == null) ? null : new File[] { input }));
    }
    
    public void setInput(final File[] input) {
        synchronized (this.inMutex) {
            if (input == null) {
                this.input = null;
            }
            else {
                this.input = input.clone();
            }
        }
    }
    
    public void setInputString(final String inputString) {
        synchronized (this.inMutex) {
            this.inputString = inputString;
        }
    }
    
    public void setLogInputString(final boolean logInputString) {
        this.logInputString = logInputString;
    }
    
    void setInputStream(final InputStream inputStream) {
        synchronized (this.inMutex) {
            this.inputStream = inputStream;
        }
    }
    
    public void setOutput(final File out) {
        this.setOutput((File[])((out == null) ? null : new File[] { out }));
    }
    
    public void setOutput(final File[] out) {
        synchronized (this.outMutex) {
            if (out == null) {
                this.out = null;
            }
            else {
                this.out = out.clone();
            }
        }
    }
    
    public void setOutputEncoding(final String outputEncoding) {
        if (outputEncoding == null) {
            throw new IllegalArgumentException("outputEncoding must not be null");
        }
        synchronized (this.outMutex) {
            this.outputEncoding = outputEncoding;
        }
    }
    
    public void setErrorEncoding(final String errorEncoding) {
        if (errorEncoding == null) {
            throw new IllegalArgumentException("errorEncoding must not be null");
        }
        synchronized (this.errMutex) {
            this.errorEncoding = errorEncoding;
        }
    }
    
    public void setInputEncoding(final String inputEncoding) {
        if (inputEncoding == null) {
            throw new IllegalArgumentException("inputEncoding must not be null");
        }
        synchronized (this.inMutex) {
            this.inputEncoding = inputEncoding;
        }
    }
    
    public void setLogError(final boolean logError) {
        synchronized (this.errMutex) {
            this.logError = logError;
        }
    }
    
    public void setAppendProperties(final boolean appendProperties) {
        synchronized (this.outMutex) {
            this.appendProperties = appendProperties;
        }
    }
    
    public void setError(final File error) {
        this.setError((File[])((error == null) ? null : new File[] { error }));
    }
    
    public void setError(final File[] error) {
        synchronized (this.errMutex) {
            if (error == null) {
                this.error = null;
            }
            else {
                this.error = error.clone();
            }
        }
    }
    
    public void setOutputProperty(final String outputProperty) {
        if (outputProperty == null || !outputProperty.equals(this.outputProperty)) {
            synchronized (this.outMutex) {
                this.outputProperty = outputProperty;
                this.baos = null;
            }
        }
    }
    
    public void setAppend(final boolean append) {
        synchronized (this.outMutex) {
            this.appendOut = append;
        }
        synchronized (this.errMutex) {
            this.appendErr = append;
        }
    }
    
    public void setAlwaysLog(final boolean alwaysLog) {
        synchronized (this.outMutex) {
            this.alwaysLogOut = alwaysLog;
        }
        synchronized (this.errMutex) {
            this.alwaysLogErr = alwaysLog;
        }
    }
    
    public void setCreateEmptyFiles(final boolean createEmptyFiles) {
        synchronized (this.outMutex) {
            this.createEmptyFilesOut = createEmptyFiles;
        }
        synchronized (this.outMutex) {
            this.createEmptyFilesErr = createEmptyFiles;
        }
    }
    
    public void setErrorProperty(final String errorProperty) {
        synchronized (this.errMutex) {
            if (errorProperty == null || !errorProperty.equals(this.errorProperty)) {
                this.errorProperty = errorProperty;
                this.errorBaos = null;
            }
        }
    }
    
    public void setInputFilterChains(final Vector<FilterChain> inputFilterChains) {
        synchronized (this.inMutex) {
            this.inputFilterChains = inputFilterChains;
        }
    }
    
    public void setOutputFilterChains(final Vector<FilterChain> outputFilterChains) {
        synchronized (this.outMutex) {
            this.outputFilterChains = outputFilterChains;
        }
    }
    
    public void setErrorFilterChains(final Vector<FilterChain> errorFilterChains) {
        synchronized (this.errMutex) {
            this.errorFilterChains = errorFilterChains;
        }
    }
    
    private void setPropertyFromBAOS(final ByteArrayOutputStream baos, final String propertyName) throws IOException {
        final BufferedReader in = new BufferedReader(new StringReader(Execute.toString(baos)));
        String line = null;
        final StringBuffer val = new StringBuffer();
        while ((line = in.readLine()) != null) {
            if (val.length() != 0) {
                val.append(StringUtils.LINE_SEP);
            }
            val.append(line);
        }
        this.managingTask.getProject().setNewProperty(propertyName, val.toString());
    }
    
    public void createStreams() {
        synchronized (this.outMutex) {
            this.outStreams();
            if (this.alwaysLogOut || this.outputStream == null) {
                final OutputStream outputLog = new LogOutputStream(this.managingTask, 2);
                this.outputStream = ((this.outputStream == null) ? outputLog : new TeeOutputStream(outputLog, this.outputStream));
            }
            Label_0270: {
                if (this.outputFilterChains == null || this.outputFilterChains.size() <= 0) {
                    if (this.outputEncoding.equalsIgnoreCase(this.inputEncoding)) {
                        break Label_0270;
                    }
                }
                try {
                    final LeadPipeInputStream snk = new LeadPipeInputStream();
                    snk.setManagingComponent(this.managingTask);
                    InputStream outPumpIn = snk;
                    Reader reader = new InputStreamReader(outPumpIn, this.inputEncoding);
                    if (this.outputFilterChains != null && this.outputFilterChains.size() > 0) {
                        final ChainReaderHelper helper = new ChainReaderHelper();
                        helper.setProject(this.managingTask.getProject());
                        helper.setPrimaryReader(reader);
                        helper.setFilterChains(this.outputFilterChains);
                        reader = helper.getAssembledReader();
                    }
                    outPumpIn = new ReaderInputStream(reader, this.outputEncoding);
                    final Thread t = new Thread(this.threadGroup, new StreamPumper(outPumpIn, this.outputStream, true), "output pumper");
                    t.setPriority(10);
                    this.outputStream = new PipedOutputStream(snk);
                    t.start();
                }
                catch (IOException eyeOhEx) {
                    throw new BuildException("error setting up output stream", eyeOhEx);
                }
            }
        }
        synchronized (this.errMutex) {
            this.errorStreams();
            if (this.alwaysLogErr || this.errorStream == null) {
                final OutputStream errorLog = new LogOutputStream(this.managingTask, 1);
                this.errorStream = ((this.errorStream == null) ? errorLog : new TeeOutputStream(errorLog, this.errorStream));
            }
            Label_0552: {
                if (this.errorFilterChains == null || this.errorFilterChains.size() <= 0) {
                    if (this.errorEncoding.equalsIgnoreCase(this.inputEncoding)) {
                        break Label_0552;
                    }
                }
                try {
                    final LeadPipeInputStream snk = new LeadPipeInputStream();
                    snk.setManagingComponent(this.managingTask);
                    InputStream errPumpIn = snk;
                    Reader reader = new InputStreamReader(errPumpIn, this.inputEncoding);
                    if (this.errorFilterChains != null && this.errorFilterChains.size() > 0) {
                        final ChainReaderHelper helper = new ChainReaderHelper();
                        helper.setProject(this.managingTask.getProject());
                        helper.setPrimaryReader(reader);
                        helper.setFilterChains(this.errorFilterChains);
                        reader = helper.getAssembledReader();
                    }
                    errPumpIn = new ReaderInputStream(reader, this.errorEncoding);
                    final Thread t = new Thread(this.threadGroup, new StreamPumper(errPumpIn, this.errorStream, true), "error pumper");
                    t.setPriority(10);
                    this.errorStream = new PipedOutputStream(snk);
                    t.start();
                }
                catch (IOException eyeOhEx) {
                    throw new BuildException("error setting up error stream", eyeOhEx);
                }
            }
        }
        synchronized (this.inMutex) {
            if (this.input != null && this.input.length > 0) {
                this.managingTask.log("Redirecting input from file" + ((this.input.length == 1) ? "" : "s"), 3);
                try {
                    this.inputStream = new ConcatFileInputStream(this.input);
                }
                catch (IOException eyeOhEx) {
                    throw new BuildException(eyeOhEx);
                }
                ((ConcatFileInputStream)this.inputStream).setManagingComponent(this.managingTask);
            }
            else if (this.inputString != null) {
                final StringBuffer buf = new StringBuffer("Using input ");
                if (this.logInputString) {
                    buf.append('\"').append(this.inputString).append('\"');
                }
                else {
                    buf.append("string");
                }
                this.managingTask.log(buf.toString(), 3);
                this.inputStream = new ByteArrayInputStream(this.inputString.getBytes());
            }
            if (this.inputStream != null && this.inputFilterChains != null && this.inputFilterChains.size() > 0) {
                final ChainReaderHelper helper2 = new ChainReaderHelper();
                helper2.setProject(this.managingTask.getProject());
                try {
                    helper2.setPrimaryReader(new InputStreamReader(this.inputStream, this.inputEncoding));
                }
                catch (IOException eyeOhEx2) {
                    throw new BuildException("error setting up input stream", eyeOhEx2);
                }
                helper2.setFilterChains(this.inputFilterChains);
                this.inputStream = new ReaderInputStream(helper2.getAssembledReader(), this.inputEncoding);
            }
        }
    }
    
    private void outStreams() {
        if (this.out != null && this.out.length > 0) {
            final String logHead = new StringBuffer("Output ").append(this.appendOut ? "appended" : "redirected").append(" to ").toString();
            this.outputStream = this.foldFiles(this.out, logHead, 3, this.appendOut, this.createEmptyFilesOut);
        }
        if (this.outputProperty != null) {
            if (this.baos == null) {
                this.baos = new PropertyOutputStream(this.outputProperty);
                this.managingTask.log("Output redirected to property: " + this.outputProperty, 3);
            }
            final OutputStream keepAliveOutput = new KeepAliveOutputStream(this.baos);
            this.outputStream = ((this.outputStream == null) ? keepAliveOutput : new TeeOutputStream(this.outputStream, keepAliveOutput));
        }
        else {
            this.baos = null;
        }
    }
    
    private void errorStreams() {
        if (this.error != null && this.error.length > 0) {
            final String logHead = new StringBuffer("Error ").append(this.appendErr ? "appended" : "redirected").append(" to ").toString();
            this.errorStream = this.foldFiles(this.error, logHead, 3, this.appendErr, this.createEmptyFilesErr);
        }
        else if (!this.logError && this.outputStream != null && this.errorProperty == null) {
            final long funnelTimeout = 0L;
            final OutputStreamFunneler funneler = new OutputStreamFunneler(this.outputStream, funnelTimeout);
            try {
                this.outputStream = new LineOrientedOutputStreamRedirector(funneler.getFunnelInstance());
                this.errorStream = new LineOrientedOutputStreamRedirector(funneler.getFunnelInstance());
            }
            catch (IOException eyeOhEx) {
                throw new BuildException("error splitting output/error streams", eyeOhEx);
            }
        }
        if (this.errorProperty != null) {
            if (this.errorBaos == null) {
                this.errorBaos = new PropertyOutputStream(this.errorProperty);
                this.managingTask.log("Error redirected to property: " + this.errorProperty, 3);
            }
            final OutputStream keepAliveError = new KeepAliveOutputStream(this.errorBaos);
            this.errorStream = ((this.error == null || this.error.length == 0) ? keepAliveError : new TeeOutputStream(this.errorStream, keepAliveError));
        }
        else {
            this.errorBaos = null;
        }
    }
    
    public ExecuteStreamHandler createHandler() throws BuildException {
        this.createStreams();
        final boolean nonBlockingRead = this.input == null && this.inputString == null;
        return new PumpStreamHandler(this.getOutputStream(), this.getErrorStream(), this.getInputStream(), nonBlockingRead);
    }
    
    protected void handleOutput(final String output) {
        synchronized (this.outMutex) {
            if (this.outPrintStream == null) {
                this.outPrintStream = new PrintStream(this.outputStream);
            }
            this.outPrintStream.print(output);
        }
    }
    
    protected int handleInput(final byte[] buffer, final int offset, final int length) throws IOException {
        synchronized (this.inMutex) {
            if (this.inputStream == null) {
                return this.managingTask.getProject().defaultInput(buffer, offset, length);
            }
            return this.inputStream.read(buffer, offset, length);
        }
    }
    
    protected void handleFlush(final String output) {
        synchronized (this.outMutex) {
            if (this.outPrintStream == null) {
                this.outPrintStream = new PrintStream(this.outputStream);
            }
            this.outPrintStream.print(output);
            this.outPrintStream.flush();
        }
    }
    
    protected void handleErrorOutput(final String output) {
        synchronized (this.errMutex) {
            if (this.errorPrintStream == null) {
                this.errorPrintStream = new PrintStream(this.errorStream);
            }
            this.errorPrintStream.print(output);
        }
    }
    
    protected void handleErrorFlush(final String output) {
        synchronized (this.errMutex) {
            if (this.errorPrintStream == null) {
                this.errorPrintStream = new PrintStream(this.errorStream);
            }
            this.errorPrintStream.print(output);
            this.errorPrintStream.flush();
        }
    }
    
    public OutputStream getOutputStream() {
        synchronized (this.outMutex) {
            return this.outputStream;
        }
    }
    
    public OutputStream getErrorStream() {
        synchronized (this.errMutex) {
            return this.errorStream;
        }
    }
    
    public InputStream getInputStream() {
        synchronized (this.inMutex) {
            return this.inputStream;
        }
    }
    
    public void complete() throws IOException {
        System.out.flush();
        System.err.flush();
        synchronized (this.inMutex) {
            if (this.inputStream != null) {
                this.inputStream.close();
            }
        }
        synchronized (this.outMutex) {
            this.outputStream.flush();
            this.outputStream.close();
        }
        synchronized (this.errMutex) {
            this.errorStream.flush();
            this.errorStream.close();
        }
        synchronized (this) {
            while (this.threadGroup.activeCount() > 0) {
                try {
                    this.managingTask.log("waiting for " + this.threadGroup.activeCount() + " Threads:", 4);
                    final Thread[] thread = new Thread[this.threadGroup.activeCount()];
                    this.threadGroup.enumerate(thread);
                    for (int i = 0; i < thread.length && thread[i] != null; ++i) {
                        try {
                            this.managingTask.log(thread[i].toString(), 4);
                        }
                        catch (NullPointerException ex) {}
                    }
                    this.wait(1000L);
                }
                catch (InterruptedException eyeEx) {
                    final Thread[] thread2 = new Thread[this.threadGroup.activeCount()];
                    this.threadGroup.enumerate(thread2);
                    for (int j = 0; j < thread2.length && thread2[j] != null; ++j) {
                        thread2[j].interrupt();
                    }
                }
            }
        }
        this.setProperties();
        synchronized (this.inMutex) {
            this.inputStream = null;
        }
        synchronized (this.outMutex) {
            this.outputStream = null;
            this.outPrintStream = null;
        }
        synchronized (this.errMutex) {
            this.errorStream = null;
            this.errorPrintStream = null;
        }
    }
    
    public void setProperties() {
        synchronized (this.outMutex) {
            if (this.baos != null) {
                try {
                    this.baos.close();
                }
                catch (IOException ex) {}
            }
        }
        synchronized (this.errMutex) {
            if (this.errorBaos != null) {
                try {
                    this.errorBaos.close();
                }
                catch (IOException ex2) {}
            }
        }
    }
    
    private OutputStream foldFiles(final File[] file, final String logHead, final int loglevel, final boolean append, final boolean createEmptyFiles) {
        final OutputStream result = new LazyFileOutputStream(file[0], append, createEmptyFiles);
        this.managingTask.log(logHead + file[0], loglevel);
        final char[] c = new char[logHead.length()];
        Arrays.fill(c, ' ');
        final String indent = new String(c);
        for (int i = 1; i < file.length; ++i) {
            this.outputStream = new TeeOutputStream(this.outputStream, new LazyFileOutputStream(file[i], append, createEmptyFiles));
            this.managingTask.log(indent + file[i], loglevel);
        }
        return result;
    }
    
    static {
        DEFAULT_ENCODING = System.getProperty("file.encoding");
    }
    
    private class PropertyOutputStream extends ByteArrayOutputStream
    {
        private String property;
        private boolean closed;
        
        PropertyOutputStream(final String property) {
            this.closed = false;
            this.property = property;
        }
        
        @Override
        public void close() throws IOException {
            synchronized (Redirector.this.outMutex) {
                if (!this.closed && (!Redirector.this.appendOut || !Redirector.this.appendProperties)) {
                    Redirector.this.setPropertyFromBAOS(this, this.property);
                    this.closed = true;
                }
            }
        }
    }
}
