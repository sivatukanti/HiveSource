// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime.debug;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import org.antlr.runtime.tree.TreeAdaptor;
import org.antlr.runtime.BaseRecognizer;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.ServerSocket;

public class DebugEventSocketProxy extends BlankDebugEventListener
{
    public static final int DEFAULT_DEBUGGER_PORT = 49100;
    protected int port;
    protected ServerSocket serverSocket;
    protected Socket socket;
    protected String grammarFileName;
    protected PrintWriter out;
    protected BufferedReader in;
    protected BaseRecognizer recognizer;
    protected TreeAdaptor adaptor;
    
    public DebugEventSocketProxy(final BaseRecognizer recognizer, final TreeAdaptor adaptor) {
        this(recognizer, 49100, adaptor);
    }
    
    public DebugEventSocketProxy(final BaseRecognizer recognizer, final int port, final TreeAdaptor adaptor) {
        this.port = 49100;
        this.grammarFileName = recognizer.getGrammarFileName();
        this.adaptor = adaptor;
        this.port = port;
    }
    
    public void handshake() throws IOException {
        if (this.serverSocket == null) {
            this.serverSocket = new ServerSocket(this.port);
            (this.socket = this.serverSocket.accept()).setTcpNoDelay(true);
            final OutputStream os = this.socket.getOutputStream();
            final OutputStreamWriter osw = new OutputStreamWriter(os, "UTF8");
            this.out = new PrintWriter(new BufferedWriter(osw));
            final InputStream is = this.socket.getInputStream();
            final InputStreamReader isr = new InputStreamReader(is, "UTF8");
            this.in = new BufferedReader(isr);
            this.out.println("ANTLR 2");
            this.out.println("grammar \"" + this.grammarFileName);
            this.out.flush();
            this.ack();
        }
    }
    
    public void commence() {
    }
    
    public void terminate() {
        this.transmit("terminate");
        this.out.close();
        try {
            this.socket.close();
        }
        catch (IOException ioe) {
            ioe.printStackTrace(System.err);
        }
    }
    
    protected void ack() {
        try {
            this.in.readLine();
        }
        catch (IOException ioe) {
            ioe.printStackTrace(System.err);
        }
    }
    
    protected void transmit(final String event) {
        this.out.println(event);
        this.out.flush();
        this.ack();
    }
    
    public void enterRule(final String grammarFileName, final String ruleName) {
        this.transmit("enterRule\t" + grammarFileName + "\t" + ruleName);
    }
    
    public void enterAlt(final int alt) {
        this.transmit("enterAlt\t" + alt);
    }
    
    public void exitRule(final String grammarFileName, final String ruleName) {
        this.transmit("exitRule\t" + grammarFileName + "\t" + ruleName);
    }
    
    public void enterSubRule(final int decisionNumber) {
        this.transmit("enterSubRule\t" + decisionNumber);
    }
    
    public void exitSubRule(final int decisionNumber) {
        this.transmit("exitSubRule\t" + decisionNumber);
    }
    
    public void enterDecision(final int decisionNumber, final boolean couldBacktrack) {
        this.transmit("enterDecision\t" + decisionNumber + "\t" + couldBacktrack);
    }
    
    public void exitDecision(final int decisionNumber) {
        this.transmit("exitDecision\t" + decisionNumber);
    }
    
    public void consumeToken(final Token t) {
        final String buf = this.serializeToken(t);
        this.transmit("consumeToken\t" + buf);
    }
    
    public void consumeHiddenToken(final Token t) {
        final String buf = this.serializeToken(t);
        this.transmit("consumeHiddenToken\t" + buf);
    }
    
    public void LT(final int i, final Token t) {
        if (t != null) {
            this.transmit("LT\t" + i + "\t" + this.serializeToken(t));
        }
    }
    
    public void mark(final int i) {
        this.transmit("mark\t" + i);
    }
    
    public void rewind(final int i) {
        this.transmit("rewind\t" + i);
    }
    
    public void rewind() {
        this.transmit("rewind");
    }
    
    public void beginBacktrack(final int level) {
        this.transmit("beginBacktrack\t" + level);
    }
    
    public void endBacktrack(final int level, final boolean successful) {
        this.transmit("endBacktrack\t" + level + "\t" + (successful ? 1 : 0));
    }
    
    public void location(final int line, final int pos) {
        this.transmit("location\t" + line + "\t" + pos);
    }
    
    public void recognitionException(final RecognitionException e) {
        final StringBuffer buf = new StringBuffer(50);
        buf.append("exception\t");
        buf.append(e.getClass().getName());
        buf.append("\t");
        buf.append(e.index);
        buf.append("\t");
        buf.append(e.line);
        buf.append("\t");
        buf.append(e.charPositionInLine);
        this.transmit(buf.toString());
    }
    
    public void beginResync() {
        this.transmit("beginResync");
    }
    
    public void endResync() {
        this.transmit("endResync");
    }
    
    public void semanticPredicate(final boolean result, final String predicate) {
        final StringBuffer buf = new StringBuffer(50);
        buf.append("semanticPredicate\t");
        buf.append(result);
        this.serializeText(buf, predicate);
        this.transmit(buf.toString());
    }
    
    public void consumeNode(final Object t) {
        final StringBuffer buf = new StringBuffer(50);
        buf.append("consumeNode");
        this.serializeNode(buf, t);
        this.transmit(buf.toString());
    }
    
    public void LT(final int i, final Object t) {
        final int ID = this.adaptor.getUniqueID(t);
        final String text = this.adaptor.getText(t);
        final int type = this.adaptor.getType(t);
        final StringBuffer buf = new StringBuffer(50);
        buf.append("LN\t");
        buf.append(i);
        this.serializeNode(buf, t);
        this.transmit(buf.toString());
    }
    
    protected void serializeNode(final StringBuffer buf, final Object t) {
        final int ID = this.adaptor.getUniqueID(t);
        final String text = this.adaptor.getText(t);
        final int type = this.adaptor.getType(t);
        buf.append("\t");
        buf.append(ID);
        buf.append("\t");
        buf.append(type);
        final Token token = this.adaptor.getToken(t);
        int line = -1;
        int pos = -1;
        if (token != null) {
            line = token.getLine();
            pos = token.getCharPositionInLine();
        }
        buf.append("\t");
        buf.append(line);
        buf.append("\t");
        buf.append(pos);
        final int tokenIndex = this.adaptor.getTokenStartIndex(t);
        buf.append("\t");
        buf.append(tokenIndex);
        this.serializeText(buf, text);
    }
    
    public void nilNode(final Object t) {
        final int ID = this.adaptor.getUniqueID(t);
        this.transmit("nilNode\t" + ID);
    }
    
    public void errorNode(final Object t) {
        final int ID = this.adaptor.getUniqueID(t);
        final String text = t.toString();
        final StringBuffer buf = new StringBuffer(50);
        buf.append("errorNode\t");
        buf.append(ID);
        buf.append("\t");
        buf.append(0);
        this.serializeText(buf, text);
        this.transmit(buf.toString());
    }
    
    public void createNode(final Object t) {
        final int ID = this.adaptor.getUniqueID(t);
        final String text = this.adaptor.getText(t);
        final int type = this.adaptor.getType(t);
        final StringBuffer buf = new StringBuffer(50);
        buf.append("createNodeFromTokenElements\t");
        buf.append(ID);
        buf.append("\t");
        buf.append(type);
        this.serializeText(buf, text);
        this.transmit(buf.toString());
    }
    
    public void createNode(final Object node, final Token token) {
        final int ID = this.adaptor.getUniqueID(node);
        final int tokenIndex = token.getTokenIndex();
        this.transmit("createNode\t" + ID + "\t" + tokenIndex);
    }
    
    public void becomeRoot(final Object newRoot, final Object oldRoot) {
        final int newRootID = this.adaptor.getUniqueID(newRoot);
        final int oldRootID = this.adaptor.getUniqueID(oldRoot);
        this.transmit("becomeRoot\t" + newRootID + "\t" + oldRootID);
    }
    
    public void addChild(final Object root, final Object child) {
        final int rootID = this.adaptor.getUniqueID(root);
        final int childID = this.adaptor.getUniqueID(child);
        this.transmit("addChild\t" + rootID + "\t" + childID);
    }
    
    public void setTokenBoundaries(final Object t, final int tokenStartIndex, final int tokenStopIndex) {
        final int ID = this.adaptor.getUniqueID(t);
        this.transmit("setTokenBoundaries\t" + ID + "\t" + tokenStartIndex + "\t" + tokenStopIndex);
    }
    
    public void setTreeAdaptor(final TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    
    public TreeAdaptor getTreeAdaptor() {
        return this.adaptor;
    }
    
    protected String serializeToken(final Token t) {
        final StringBuffer buf = new StringBuffer(50);
        buf.append(t.getTokenIndex());
        buf.append('\t');
        buf.append(t.getType());
        buf.append('\t');
        buf.append(t.getChannel());
        buf.append('\t');
        buf.append(t.getLine());
        buf.append('\t');
        buf.append(t.getCharPositionInLine());
        this.serializeText(buf, t.getText());
        return buf.toString();
    }
    
    protected void serializeText(final StringBuffer buf, String text) {
        buf.append("\t\"");
        if (text == null) {
            text = "";
        }
        text = this.escapeNewlines(text);
        buf.append(text);
    }
    
    protected String escapeNewlines(String txt) {
        txt = txt.replaceAll("%", "%25");
        txt = txt.replaceAll("\n", "%0A");
        txt = txt.replaceAll("\r", "%0D");
        return txt;
    }
}
