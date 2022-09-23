// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime.tree;

import java.util.regex.Matcher;
import org.antlr.runtime.MismatchedTreeNodeException;
import org.antlr.runtime.Token;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.BitSet;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.IntStream;
import org.antlr.runtime.RecognizerSharedState;
import java.util.regex.Pattern;
import org.antlr.runtime.BaseRecognizer;

public class TreeParser extends BaseRecognizer
{
    public static final int DOWN = 2;
    public static final int UP = 3;
    static String dotdot;
    static String doubleEtc;
    static Pattern dotdotPattern;
    static Pattern doubleEtcPattern;
    protected TreeNodeStream input;
    
    public TreeParser(final TreeNodeStream input) {
        this.setTreeNodeStream(input);
    }
    
    public TreeParser(final TreeNodeStream input, final RecognizerSharedState state) {
        super(state);
        this.setTreeNodeStream(input);
    }
    
    public void reset() {
        super.reset();
        if (this.input != null) {
            this.input.seek(0);
        }
    }
    
    public void setTreeNodeStream(final TreeNodeStream input) {
        this.input = input;
    }
    
    public TreeNodeStream getTreeNodeStream() {
        return this.input;
    }
    
    public String getSourceName() {
        return this.input.getSourceName();
    }
    
    protected Object getCurrentInputSymbol(final IntStream input) {
        return ((TreeNodeStream)input).LT(1);
    }
    
    protected Object getMissingSymbol(final IntStream input, final RecognitionException e, final int expectedTokenType, final BitSet follow) {
        final String tokenText = "<missing " + this.getTokenNames()[expectedTokenType] + ">";
        final TreeAdaptor adaptor = ((TreeNodeStream)e.input).getTreeAdaptor();
        return adaptor.create(new CommonToken(expectedTokenType, tokenText));
    }
    
    public void matchAny(final IntStream ignore) {
        this.state.errorRecovery = false;
        this.state.failed = false;
        Object look = this.input.LT(1);
        if (this.input.getTreeAdaptor().getChildCount(look) == 0) {
            this.input.consume();
            return;
        }
        int level = 0;
        int tokenType = this.input.getTreeAdaptor().getType(look);
        while (tokenType != -1 && (tokenType != 3 || level != 0)) {
            this.input.consume();
            look = this.input.LT(1);
            tokenType = this.input.getTreeAdaptor().getType(look);
            if (tokenType == 2) {
                ++level;
            }
            else {
                if (tokenType != 3) {
                    continue;
                }
                --level;
            }
        }
        this.input.consume();
    }
    
    protected Object recoverFromMismatchedToken(final IntStream input, final int ttype, final BitSet follow) throws RecognitionException {
        throw new MismatchedTreeNodeException(ttype, (TreeNodeStream)input);
    }
    
    public String getErrorHeader(final RecognitionException e) {
        return this.getGrammarFileName() + ": node from " + (e.approximateLineInfo ? "after " : "") + "line " + e.line + ":" + e.charPositionInLine;
    }
    
    public String getErrorMessage(final RecognitionException e, final String[] tokenNames) {
        if (this instanceof TreeParser) {
            final TreeAdaptor adaptor = ((TreeNodeStream)e.input).getTreeAdaptor();
            e.token = adaptor.getToken(e.node);
            if (e.token == null) {
                e.token = new CommonToken(adaptor.getType(e.node), adaptor.getText(e.node));
            }
        }
        return super.getErrorMessage(e, tokenNames);
    }
    
    public boolean inContext(final String context) {
        return inContext(this.input.getTreeAdaptor(), this.getTokenNames(), this.input.LT(1), context);
    }
    
    public static boolean inContext(final TreeAdaptor adaptor, final String[] tokenNames, Object t, String context) {
        final Matcher dotdotMatcher = TreeParser.dotdotPattern.matcher(context);
        final Matcher doubleEtcMatcher = TreeParser.doubleEtcPattern.matcher(context);
        if (dotdotMatcher.find()) {
            throw new IllegalArgumentException("invalid syntax: ..");
        }
        if (doubleEtcMatcher.find()) {
            throw new IllegalArgumentException("invalid syntax: ... ...");
        }
        context = context.replaceAll("\\.\\.\\.", " ... ");
        context = context.trim();
        final String[] nodes = context.split("\\s+");
        int ni;
        String goal;
        Object ancestor;
        String name;
        for (ni = nodes.length - 1, t = adaptor.getParent(t); ni >= 0 && t != null; --ni, t = adaptor.getParent(t)) {
            if (nodes[ni].equals("...")) {
                if (ni == 0) {
                    return true;
                }
                goal = nodes[ni - 1];
                ancestor = getAncestor(adaptor, tokenNames, t, goal);
                if (ancestor == null) {
                    return false;
                }
                t = ancestor;
                --ni;
            }
            name = tokenNames[adaptor.getType(t)];
            if (!name.equals(nodes[ni])) {
                return false;
            }
        }
        return t != null || ni < 0;
    }
    
    protected static Object getAncestor(final TreeAdaptor adaptor, final String[] tokenNames, Object t, final String goal) {
        while (t != null) {
            final String name = tokenNames[adaptor.getType(t)];
            if (name.equals(goal)) {
                return t;
            }
            t = adaptor.getParent(t);
        }
        return null;
    }
    
    public void traceIn(final String ruleName, final int ruleIndex) {
        super.traceIn(ruleName, ruleIndex, this.input.LT(1));
    }
    
    public void traceOut(final String ruleName, final int ruleIndex) {
        super.traceOut(ruleName, ruleIndex, this.input.LT(1));
    }
    
    static {
        TreeParser.dotdot = ".*[^.]\\.\\.[^.].*";
        TreeParser.doubleEtc = ".*\\.\\.\\.\\s+\\.\\.\\..*";
        TreeParser.dotdotPattern = Pattern.compile(TreeParser.dotdot);
        TreeParser.doubleEtcPattern = Pattern.compile(TreeParser.doubleEtc);
    }
}
