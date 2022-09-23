// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime.debug;

import org.antlr.runtime.tree.Tree;
import org.antlr.runtime.tree.BaseTree;
import org.antlr.runtime.CharStream;
import java.util.StringTokenizer;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.net.ConnectException;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class RemoteDebugEventSocketListener implements Runnable
{
    static final int MAX_EVENT_ELEMENTS = 8;
    DebugEventListener listener;
    String machine;
    int port;
    Socket channel;
    PrintWriter out;
    BufferedReader in;
    String event;
    public String version;
    public String grammarFileName;
    int previousTokenIndex;
    boolean tokenIndexesInvalid;
    
    public RemoteDebugEventSocketListener(final DebugEventListener listener, final String machine, final int port) throws IOException {
        this.channel = null;
        this.previousTokenIndex = -1;
        this.tokenIndexesInvalid = false;
        this.listener = listener;
        this.machine = machine;
        this.port = port;
        if (!this.openConnection()) {
            throw new ConnectException();
        }
    }
    
    protected void eventHandler() {
        try {
            this.handshake();
            this.event = this.in.readLine();
            while (this.event != null) {
                this.dispatch(this.event);
                this.ack();
                this.event = this.in.readLine();
            }
        }
        catch (Exception e) {
            System.err.println(e);
            e.printStackTrace(System.err);
        }
        finally {
            this.closeConnection();
        }
    }
    
    protected boolean openConnection() {
        boolean success = false;
        try {
            (this.channel = new Socket(this.machine, this.port)).setTcpNoDelay(true);
            final OutputStream os = this.channel.getOutputStream();
            final OutputStreamWriter osw = new OutputStreamWriter(os, "UTF8");
            this.out = new PrintWriter(new BufferedWriter(osw));
            final InputStream is = this.channel.getInputStream();
            final InputStreamReader isr = new InputStreamReader(is, "UTF8");
            this.in = new BufferedReader(isr);
            success = true;
        }
        catch (Exception e) {
            System.err.println(e);
        }
        return success;
    }
    
    protected void closeConnection() {
        try {
            this.in.close();
            this.in = null;
            this.out.close();
            this.out = null;
            this.channel.close();
            this.channel = null;
        }
        catch (Exception e) {
            System.err.println(e);
            e.printStackTrace(System.err);
            if (this.in != null) {
                try {
                    this.in.close();
                }
                catch (IOException ioe) {
                    System.err.println(ioe);
                }
            }
            if (this.out != null) {
                this.out.close();
            }
            if (this.channel != null) {
                try {
                    this.channel.close();
                }
                catch (IOException ioe) {
                    System.err.println(ioe);
                }
            }
        }
        finally {
            if (this.in != null) {
                try {
                    this.in.close();
                }
                catch (IOException ioe2) {
                    System.err.println(ioe2);
                }
            }
            if (this.out != null) {
                this.out.close();
            }
            if (this.channel != null) {
                try {
                    this.channel.close();
                }
                catch (IOException ioe2) {
                    System.err.println(ioe2);
                }
            }
        }
    }
    
    protected void handshake() throws IOException {
        final String antlrLine = this.in.readLine();
        final String[] antlrElements = this.getEventElements(antlrLine);
        this.version = antlrElements[1];
        final String grammarLine = this.in.readLine();
        final String[] grammarElements = this.getEventElements(grammarLine);
        this.grammarFileName = grammarElements[1];
        this.ack();
        this.listener.commence();
    }
    
    protected void ack() {
        this.out.println("ack");
        this.out.flush();
    }
    
    protected void dispatch(final String line) {
        final String[] elements = this.getEventElements(line);
        if (elements == null || elements[0] == null) {
            System.err.println("unknown debug event: " + line);
            return;
        }
        if (elements[0].equals("enterRule")) {
            this.listener.enterRule(elements[1], elements[2]);
        }
        else if (elements[0].equals("exitRule")) {
            this.listener.exitRule(elements[1], elements[2]);
        }
        else if (elements[0].equals("enterAlt")) {
            this.listener.enterAlt(Integer.parseInt(elements[1]));
        }
        else if (elements[0].equals("enterSubRule")) {
            this.listener.enterSubRule(Integer.parseInt(elements[1]));
        }
        else if (elements[0].equals("exitSubRule")) {
            this.listener.exitSubRule(Integer.parseInt(elements[1]));
        }
        else if (elements[0].equals("enterDecision")) {
            this.listener.enterDecision(Integer.parseInt(elements[1]), elements[2].equals("true"));
        }
        else if (elements[0].equals("exitDecision")) {
            this.listener.exitDecision(Integer.parseInt(elements[1]));
        }
        else if (elements[0].equals("location")) {
            this.listener.location(Integer.parseInt(elements[1]), Integer.parseInt(elements[2]));
        }
        else if (elements[0].equals("consumeToken")) {
            final ProxyToken t = this.deserializeToken(elements, 1);
            if (t.getTokenIndex() == this.previousTokenIndex) {
                this.tokenIndexesInvalid = true;
            }
            this.previousTokenIndex = t.getTokenIndex();
            this.listener.consumeToken(t);
        }
        else if (elements[0].equals("consumeHiddenToken")) {
            final ProxyToken t = this.deserializeToken(elements, 1);
            if (t.getTokenIndex() == this.previousTokenIndex) {
                this.tokenIndexesInvalid = true;
            }
            this.previousTokenIndex = t.getTokenIndex();
            this.listener.consumeHiddenToken(t);
        }
        else if (elements[0].equals("LT")) {
            final Token t2 = this.deserializeToken(elements, 2);
            this.listener.LT(Integer.parseInt(elements[1]), t2);
        }
        else if (elements[0].equals("mark")) {
            this.listener.mark(Integer.parseInt(elements[1]));
        }
        else if (elements[0].equals("rewind")) {
            if (elements[1] != null) {
                this.listener.rewind(Integer.parseInt(elements[1]));
            }
            else {
                this.listener.rewind();
            }
        }
        else if (elements[0].equals("beginBacktrack")) {
            this.listener.beginBacktrack(Integer.parseInt(elements[1]));
        }
        else if (elements[0].equals("endBacktrack")) {
            final int level = Integer.parseInt(elements[1]);
            final int successI = Integer.parseInt(elements[2]);
            this.listener.endBacktrack(level, successI == 1);
        }
        else if (elements[0].equals("exception")) {
            final String excName = elements[1];
            final String indexS = elements[2];
            final String lineS = elements[3];
            final String posS = elements[4];
            Class excClass = null;
            try {
                excClass = Class.forName(excName);
                final RecognitionException e = excClass.newInstance();
                e.index = Integer.parseInt(indexS);
                e.line = Integer.parseInt(lineS);
                e.charPositionInLine = Integer.parseInt(posS);
                this.listener.recognitionException(e);
            }
            catch (ClassNotFoundException cnfe) {
                System.err.println("can't find class " + cnfe);
                cnfe.printStackTrace(System.err);
            }
            catch (InstantiationException ie) {
                System.err.println("can't instantiate class " + ie);
                ie.printStackTrace(System.err);
            }
            catch (IllegalAccessException iae) {
                System.err.println("can't access class " + iae);
                iae.printStackTrace(System.err);
            }
        }
        else if (elements[0].equals("beginResync")) {
            this.listener.beginResync();
        }
        else if (elements[0].equals("endResync")) {
            this.listener.endResync();
        }
        else if (elements[0].equals("terminate")) {
            this.listener.terminate();
        }
        else if (elements[0].equals("semanticPredicate")) {
            final Boolean result = Boolean.valueOf(elements[1]);
            String predicateText = elements[2];
            predicateText = this.unEscapeNewlines(predicateText);
            this.listener.semanticPredicate(result, predicateText);
        }
        else if (elements[0].equals("consumeNode")) {
            final ProxyTree node = this.deserializeNode(elements, 1);
            this.listener.consumeNode(node);
        }
        else if (elements[0].equals("LN")) {
            final int i = Integer.parseInt(elements[1]);
            final ProxyTree node2 = this.deserializeNode(elements, 2);
            this.listener.LT(i, node2);
        }
        else if (elements[0].equals("createNodeFromTokenElements")) {
            final int ID = Integer.parseInt(elements[1]);
            final int type = Integer.parseInt(elements[2]);
            String text = elements[3];
            text = this.unEscapeNewlines(text);
            final ProxyTree node3 = new ProxyTree(ID, type, -1, -1, -1, text);
            this.listener.createNode(node3);
        }
        else if (elements[0].equals("createNode")) {
            final int ID = Integer.parseInt(elements[1]);
            final int tokenIndex = Integer.parseInt(elements[2]);
            final ProxyTree node4 = new ProxyTree(ID);
            final ProxyToken token = new ProxyToken(tokenIndex);
            this.listener.createNode(node4, token);
        }
        else if (elements[0].equals("nilNode")) {
            final int ID = Integer.parseInt(elements[1]);
            final ProxyTree node2 = new ProxyTree(ID);
            this.listener.nilNode(node2);
        }
        else if (elements[0].equals("errorNode")) {
            final int ID = Integer.parseInt(elements[1]);
            final int type = Integer.parseInt(elements[2]);
            String text = elements[3];
            text = this.unEscapeNewlines(text);
            final ProxyTree node3 = new ProxyTree(ID, type, -1, -1, -1, text);
            this.listener.errorNode(node3);
        }
        else if (elements[0].equals("becomeRoot")) {
            final int newRootID = Integer.parseInt(elements[1]);
            final int oldRootID = Integer.parseInt(elements[2]);
            final ProxyTree newRoot = new ProxyTree(newRootID);
            final ProxyTree oldRoot = new ProxyTree(oldRootID);
            this.listener.becomeRoot(newRoot, oldRoot);
        }
        else if (elements[0].equals("addChild")) {
            final int rootID = Integer.parseInt(elements[1]);
            final int childID = Integer.parseInt(elements[2]);
            final ProxyTree root = new ProxyTree(rootID);
            final ProxyTree child = new ProxyTree(childID);
            this.listener.addChild(root, child);
        }
        else if (elements[0].equals("setTokenBoundaries")) {
            final int ID = Integer.parseInt(elements[1]);
            final ProxyTree node2 = new ProxyTree(ID);
            this.listener.setTokenBoundaries(node2, Integer.parseInt(elements[2]), Integer.parseInt(elements[3]));
        }
        else {
            System.err.println("unknown debug event: " + line);
        }
    }
    
    protected ProxyTree deserializeNode(final String[] elements, final int offset) {
        final int ID = Integer.parseInt(elements[offset + 0]);
        final int type = Integer.parseInt(elements[offset + 1]);
        final int tokenLine = Integer.parseInt(elements[offset + 2]);
        final int charPositionInLine = Integer.parseInt(elements[offset + 3]);
        final int tokenIndex = Integer.parseInt(elements[offset + 4]);
        String text = elements[offset + 5];
        text = this.unEscapeNewlines(text);
        return new ProxyTree(ID, type, tokenLine, charPositionInLine, tokenIndex, text);
    }
    
    protected ProxyToken deserializeToken(final String[] elements, final int offset) {
        final String indexS = elements[offset + 0];
        final String typeS = elements[offset + 1];
        final String channelS = elements[offset + 2];
        final String lineS = elements[offset + 3];
        final String posS = elements[offset + 4];
        String text = elements[offset + 5];
        text = this.unEscapeNewlines(text);
        final int index = Integer.parseInt(indexS);
        final ProxyToken t = new ProxyToken(index, Integer.parseInt(typeS), Integer.parseInt(channelS), Integer.parseInt(lineS), Integer.parseInt(posS), text);
        return t;
    }
    
    public void start() {
        final Thread t = new Thread(this);
        t.start();
    }
    
    public void run() {
        this.eventHandler();
    }
    
    public String[] getEventElements(String event) {
        if (event == null) {
            return null;
        }
        final String[] elements = new String[8];
        String str = null;
        try {
            final int firstQuoteIndex = event.indexOf(34);
            if (firstQuoteIndex >= 0) {
                final String eventWithoutString = event.substring(0, firstQuoteIndex);
                str = event.substring(firstQuoteIndex + 1, event.length());
                event = eventWithoutString;
            }
            final StringTokenizer st = new StringTokenizer(event, "\t", false);
            int i = 0;
            while (st.hasMoreTokens()) {
                if (i >= 8) {
                    return elements;
                }
                elements[i] = st.nextToken();
                ++i;
            }
            if (str != null) {
                elements[i] = str;
            }
        }
        catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return elements;
    }
    
    protected String unEscapeNewlines(String txt) {
        txt = txt.replaceAll("%0A", "\n");
        txt = txt.replaceAll("%0D", "\r");
        txt = txt.replaceAll("%25", "%");
        return txt;
    }
    
    public boolean tokenIndexesAreInvalid() {
        return false;
    }
    
    public static class ProxyToken implements Token
    {
        int index;
        int type;
        int channel;
        int line;
        int charPos;
        String text;
        
        public ProxyToken(final int index) {
            this.index = index;
        }
        
        public ProxyToken(final int index, final int type, final int channel, final int line, final int charPos, final String text) {
            this.index = index;
            this.type = type;
            this.channel = channel;
            this.line = line;
            this.charPos = charPos;
            this.text = text;
        }
        
        public String getText() {
            return this.text;
        }
        
        public void setText(final String text) {
            this.text = text;
        }
        
        public int getType() {
            return this.type;
        }
        
        public void setType(final int ttype) {
            this.type = ttype;
        }
        
        public int getLine() {
            return this.line;
        }
        
        public void setLine(final int line) {
            this.line = line;
        }
        
        public int getCharPositionInLine() {
            return this.charPos;
        }
        
        public void setCharPositionInLine(final int pos) {
            this.charPos = pos;
        }
        
        public int getChannel() {
            return this.channel;
        }
        
        public void setChannel(final int channel) {
            this.channel = channel;
        }
        
        public int getTokenIndex() {
            return this.index;
        }
        
        public void setTokenIndex(final int index) {
            this.index = index;
        }
        
        public CharStream getInputStream() {
            return null;
        }
        
        public void setInputStream(final CharStream input) {
        }
        
        public String toString() {
            String channelStr = "";
            if (this.channel != 0) {
                channelStr = ",channel=" + this.channel;
            }
            return "[" + this.getText() + "/<" + this.type + ">" + channelStr + "," + this.line + ":" + this.getCharPositionInLine() + ",@" + this.index + "]";
        }
    }
    
    public static class ProxyTree extends BaseTree
    {
        public int ID;
        public int type;
        public int line;
        public int charPos;
        public int tokenIndex;
        public String text;
        
        public ProxyTree(final int ID, final int type, final int line, final int charPos, final int tokenIndex, final String text) {
            this.line = 0;
            this.charPos = -1;
            this.tokenIndex = -1;
            this.ID = ID;
            this.type = type;
            this.line = line;
            this.charPos = charPos;
            this.tokenIndex = tokenIndex;
            this.text = text;
        }
        
        public ProxyTree(final int ID) {
            this.line = 0;
            this.charPos = -1;
            this.tokenIndex = -1;
            this.ID = ID;
        }
        
        public int getTokenStartIndex() {
            return this.tokenIndex;
        }
        
        public void setTokenStartIndex(final int index) {
        }
        
        public int getTokenStopIndex() {
            return 0;
        }
        
        public void setTokenStopIndex(final int index) {
        }
        
        public Tree dupNode() {
            return null;
        }
        
        public int getType() {
            return this.type;
        }
        
        public String getText() {
            return this.text;
        }
        
        public String toString() {
            return "fix this";
        }
    }
}
