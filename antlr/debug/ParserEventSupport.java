// 
// Decompiled by Procyon v0.5.36
// 

package antlr.debug;

import antlr.collections.impl.BitSet;
import java.util.Enumeration;
import java.util.Vector;
import java.util.Hashtable;

public class ParserEventSupport
{
    private Object source;
    private Hashtable doneListeners;
    private Vector matchListeners;
    private Vector messageListeners;
    private Vector tokenListeners;
    private Vector traceListeners;
    private Vector semPredListeners;
    private Vector synPredListeners;
    private Vector newLineListeners;
    private ParserMatchEvent matchEvent;
    private MessageEvent messageEvent;
    private ParserTokenEvent tokenEvent;
    private SemanticPredicateEvent semPredEvent;
    private SyntacticPredicateEvent synPredEvent;
    private TraceEvent traceEvent;
    private NewLineEvent newLineEvent;
    private ParserController controller;
    protected static final int CONSUME = 0;
    protected static final int ENTER_RULE = 1;
    protected static final int EXIT_RULE = 2;
    protected static final int LA = 3;
    protected static final int MATCH = 4;
    protected static final int MATCH_NOT = 5;
    protected static final int MISMATCH = 6;
    protected static final int MISMATCH_NOT = 7;
    protected static final int REPORT_ERROR = 8;
    protected static final int REPORT_WARNING = 9;
    protected static final int SEMPRED = 10;
    protected static final int SYNPRED_FAILED = 11;
    protected static final int SYNPRED_STARTED = 12;
    protected static final int SYNPRED_SUCCEEDED = 13;
    protected static final int NEW_LINE = 14;
    protected static final int DONE_PARSING = 15;
    private int ruleDepth;
    
    public ParserEventSupport(final Object source) {
        this.ruleDepth = 0;
        this.matchEvent = new ParserMatchEvent(source);
        this.messageEvent = new MessageEvent(source);
        this.tokenEvent = new ParserTokenEvent(source);
        this.traceEvent = new TraceEvent(source);
        this.semPredEvent = new SemanticPredicateEvent(source);
        this.synPredEvent = new SyntacticPredicateEvent(source);
        this.newLineEvent = new NewLineEvent(source);
        this.source = source;
    }
    
    public void addDoneListener(final ListenerBase listenerBase) {
        if (this.doneListeners == null) {
            this.doneListeners = new Hashtable();
        }
        final Integer n = this.doneListeners.get(listenerBase);
        int value;
        if (n != null) {
            value = n + 1;
        }
        else {
            value = 1;
        }
        this.doneListeners.put(listenerBase, new Integer(value));
    }
    
    public void addMessageListener(final MessageListener obj) {
        if (this.messageListeners == null) {
            this.messageListeners = new Vector();
        }
        this.messageListeners.addElement(obj);
        this.addDoneListener(obj);
    }
    
    public void addNewLineListener(final NewLineListener obj) {
        if (this.newLineListeners == null) {
            this.newLineListeners = new Vector();
        }
        this.newLineListeners.addElement(obj);
        this.addDoneListener(obj);
    }
    
    public void addParserListener(final ParserListener parserListener) {
        if (parserListener instanceof ParserController) {
            ((ParserController)parserListener).setParserEventSupport(this);
            this.controller = (ParserController)parserListener;
        }
        this.addParserMatchListener(parserListener);
        this.addParserTokenListener(parserListener);
        this.addMessageListener(parserListener);
        this.addTraceListener(parserListener);
        this.addSemanticPredicateListener(parserListener);
        this.addSyntacticPredicateListener(parserListener);
    }
    
    public void addParserMatchListener(final ParserMatchListener obj) {
        if (this.matchListeners == null) {
            this.matchListeners = new Vector();
        }
        this.matchListeners.addElement(obj);
        this.addDoneListener(obj);
    }
    
    public void addParserTokenListener(final ParserTokenListener obj) {
        if (this.tokenListeners == null) {
            this.tokenListeners = new Vector();
        }
        this.tokenListeners.addElement(obj);
        this.addDoneListener(obj);
    }
    
    public void addSemanticPredicateListener(final SemanticPredicateListener obj) {
        if (this.semPredListeners == null) {
            this.semPredListeners = new Vector();
        }
        this.semPredListeners.addElement(obj);
        this.addDoneListener(obj);
    }
    
    public void addSyntacticPredicateListener(final SyntacticPredicateListener obj) {
        if (this.synPredListeners == null) {
            this.synPredListeners = new Vector();
        }
        this.synPredListeners.addElement(obj);
        this.addDoneListener(obj);
    }
    
    public void addTraceListener(final TraceListener obj) {
        if (this.traceListeners == null) {
            this.traceListeners = new Vector();
        }
        this.traceListeners.addElement(obj);
        this.addDoneListener(obj);
    }
    
    public void fireConsume(final int n) {
        this.tokenEvent.setValues(ParserTokenEvent.CONSUME, 1, n);
        this.fireEvents(0, this.tokenListeners);
    }
    
    public void fireDoneParsing() {
        this.traceEvent.setValues(TraceEvent.DONE_PARSING, 0, 0, 0);
        Hashtable<ListenerBase, V> hashtable = null;
        synchronized (this) {
            if (this.doneListeners == null) {
                return;
            }
            hashtable = (Hashtable<ListenerBase, V>)this.doneListeners.clone();
        }
        if (hashtable != null) {
            final Enumeration<ListenerBase> keys = hashtable.keys();
            while (keys.hasMoreElements()) {
                this.fireEvent(15, keys.nextElement());
            }
        }
        if (this.controller != null) {
            this.controller.checkBreak();
        }
    }
    
    public void fireEnterRule(final int n, final int n2, final int n3) {
        ++this.ruleDepth;
        this.traceEvent.setValues(TraceEvent.ENTER, n, n2, n3);
        this.fireEvents(1, this.traceListeners);
    }
    
    public void fireEvent(final int i, final ListenerBase listenerBase) {
        switch (i) {
            case 0: {
                ((ParserTokenListener)listenerBase).parserConsume(this.tokenEvent);
                break;
            }
            case 3: {
                ((ParserTokenListener)listenerBase).parserLA(this.tokenEvent);
                break;
            }
            case 1: {
                ((TraceListener)listenerBase).enterRule(this.traceEvent);
                break;
            }
            case 2: {
                ((TraceListener)listenerBase).exitRule(this.traceEvent);
                break;
            }
            case 4: {
                ((ParserMatchListener)listenerBase).parserMatch(this.matchEvent);
                break;
            }
            case 5: {
                ((ParserMatchListener)listenerBase).parserMatchNot(this.matchEvent);
                break;
            }
            case 6: {
                ((ParserMatchListener)listenerBase).parserMismatch(this.matchEvent);
                break;
            }
            case 7: {
                ((ParserMatchListener)listenerBase).parserMismatchNot(this.matchEvent);
                break;
            }
            case 10: {
                ((SemanticPredicateListener)listenerBase).semanticPredicateEvaluated(this.semPredEvent);
                break;
            }
            case 12: {
                ((SyntacticPredicateListener)listenerBase).syntacticPredicateStarted(this.synPredEvent);
                break;
            }
            case 11: {
                ((SyntacticPredicateListener)listenerBase).syntacticPredicateFailed(this.synPredEvent);
                break;
            }
            case 13: {
                ((SyntacticPredicateListener)listenerBase).syntacticPredicateSucceeded(this.synPredEvent);
                break;
            }
            case 8: {
                ((MessageListener)listenerBase).reportError(this.messageEvent);
                break;
            }
            case 9: {
                ((MessageListener)listenerBase).reportWarning(this.messageEvent);
                break;
            }
            case 15: {
                listenerBase.doneParsing(this.traceEvent);
                break;
            }
            case 14: {
                ((NewLineListener)listenerBase).hitNewLine(this.newLineEvent);
                break;
            }
            default: {
                throw new IllegalArgumentException("bad type " + i + " for fireEvent()");
            }
        }
    }
    
    public void fireEvents(final int n, final Vector vector) {
        if (vector != null) {
            for (int i = 0; i < vector.size(); ++i) {
                this.fireEvent(n, vector.elementAt(i));
            }
        }
        if (this.controller != null) {
            this.controller.checkBreak();
        }
    }
    
    public void fireExitRule(final int n, final int n2, final int n3) {
        this.traceEvent.setValues(TraceEvent.EXIT, n, n2, n3);
        this.fireEvents(2, this.traceListeners);
        --this.ruleDepth;
        if (this.ruleDepth == 0) {
            this.fireDoneParsing();
        }
    }
    
    public void fireLA(final int n, final int n2) {
        this.tokenEvent.setValues(ParserTokenEvent.LA, n, n2);
        this.fireEvents(3, this.tokenListeners);
    }
    
    public void fireMatch(final char value, final int n) {
        this.matchEvent.setValues(ParserMatchEvent.CHAR, value, new Character(value), null, n, false, true);
        this.fireEvents(4, this.matchListeners);
    }
    
    public void fireMatch(final char c, final BitSet set, final int n) {
        this.matchEvent.setValues(ParserMatchEvent.CHAR_BITSET, c, set, null, n, false, true);
        this.fireEvents(4, this.matchListeners);
    }
    
    public void fireMatch(final char c, final String s, final int n) {
        this.matchEvent.setValues(ParserMatchEvent.CHAR_RANGE, c, s, null, n, false, true);
        this.fireEvents(4, this.matchListeners);
    }
    
    public void fireMatch(final int n, final BitSet set, final String s, final int n2) {
        this.matchEvent.setValues(ParserMatchEvent.BITSET, n, set, s, n2, false, true);
        this.fireEvents(4, this.matchListeners);
    }
    
    public void fireMatch(final int value, final String s, final int n) {
        this.matchEvent.setValues(ParserMatchEvent.TOKEN, value, new Integer(value), s, n, false, true);
        this.fireEvents(4, this.matchListeners);
    }
    
    public void fireMatch(final String s, final int n) {
        this.matchEvent.setValues(ParserMatchEvent.STRING, 0, s, null, n, false, true);
        this.fireEvents(4, this.matchListeners);
    }
    
    public void fireMatchNot(final char c, final char value, final int n) {
        this.matchEvent.setValues(ParserMatchEvent.CHAR, c, new Character(value), null, n, true, true);
        this.fireEvents(5, this.matchListeners);
    }
    
    public void fireMatchNot(final int n, final int value, final String s, final int n2) {
        this.matchEvent.setValues(ParserMatchEvent.TOKEN, n, new Integer(value), s, n2, true, true);
        this.fireEvents(5, this.matchListeners);
    }
    
    public void fireMismatch(final char c, final char value, final int n) {
        this.matchEvent.setValues(ParserMatchEvent.CHAR, c, new Character(value), null, n, false, false);
        this.fireEvents(6, this.matchListeners);
    }
    
    public void fireMismatch(final char c, final BitSet set, final int n) {
        this.matchEvent.setValues(ParserMatchEvent.CHAR_BITSET, c, set, null, n, false, true);
        this.fireEvents(6, this.matchListeners);
    }
    
    public void fireMismatch(final char c, final String s, final int n) {
        this.matchEvent.setValues(ParserMatchEvent.CHAR_RANGE, c, s, null, n, false, true);
        this.fireEvents(6, this.matchListeners);
    }
    
    public void fireMismatch(final int n, final int value, final String s, final int n2) {
        this.matchEvent.setValues(ParserMatchEvent.TOKEN, n, new Integer(value), s, n2, false, false);
        this.fireEvents(6, this.matchListeners);
    }
    
    public void fireMismatch(final int n, final BitSet set, final String s, final int n2) {
        this.matchEvent.setValues(ParserMatchEvent.BITSET, n, set, s, n2, false, true);
        this.fireEvents(6, this.matchListeners);
    }
    
    public void fireMismatch(final String s, final String s2, final int n) {
        this.matchEvent.setValues(ParserMatchEvent.STRING, 0, s2, s, n, false, true);
        this.fireEvents(6, this.matchListeners);
    }
    
    public void fireMismatchNot(final char c, final char value, final int n) {
        this.matchEvent.setValues(ParserMatchEvent.CHAR, c, new Character(value), null, n, true, true);
        this.fireEvents(7, this.matchListeners);
    }
    
    public void fireMismatchNot(final int n, final int value, final String s, final int n2) {
        this.matchEvent.setValues(ParserMatchEvent.TOKEN, n, new Integer(value), s, n2, true, true);
        this.fireEvents(7, this.matchListeners);
    }
    
    public void fireNewLine(final int values) {
        this.newLineEvent.setValues(values);
        this.fireEvents(14, this.newLineListeners);
    }
    
    public void fireReportError(final Exception ex) {
        this.messageEvent.setValues(MessageEvent.ERROR, ex.toString());
        this.fireEvents(8, this.messageListeners);
    }
    
    public void fireReportError(final String s) {
        this.messageEvent.setValues(MessageEvent.ERROR, s);
        this.fireEvents(8, this.messageListeners);
    }
    
    public void fireReportWarning(final String s) {
        this.messageEvent.setValues(MessageEvent.WARNING, s);
        this.fireEvents(9, this.messageListeners);
    }
    
    public boolean fireSemanticPredicateEvaluated(final int n, final int n2, final boolean b, final int n3) {
        this.semPredEvent.setValues(n, n2, b, n3);
        this.fireEvents(10, this.semPredListeners);
        return b;
    }
    
    public void fireSyntacticPredicateFailed(final int n) {
        this.synPredEvent.setValues(0, n);
        this.fireEvents(11, this.synPredListeners);
    }
    
    public void fireSyntacticPredicateStarted(final int n) {
        this.synPredEvent.setValues(0, n);
        this.fireEvents(12, this.synPredListeners);
    }
    
    public void fireSyntacticPredicateSucceeded(final int n) {
        this.synPredEvent.setValues(0, n);
        this.fireEvents(13, this.synPredListeners);
    }
    
    protected void refresh(final Vector vector) {
        final Vector vector2;
        synchronized (vector) {
            vector2 = (Vector)vector.clone();
        }
        if (vector2 != null) {
            for (int i = 0; i < vector2.size(); ++i) {
                vector2.elementAt(i).refresh();
            }
        }
    }
    
    public void refreshListeners() {
        this.refresh(this.matchListeners);
        this.refresh(this.messageListeners);
        this.refresh(this.tokenListeners);
        this.refresh(this.traceListeners);
        this.refresh(this.semPredListeners);
        this.refresh(this.synPredListeners);
    }
    
    public void removeDoneListener(final ListenerBase key) {
        if (this.doneListeners == null) {
            return;
        }
        final Integer n = this.doneListeners.get(key);
        int value = 0;
        if (n != null) {
            value = n - 1;
        }
        if (value == 0) {
            this.doneListeners.remove(key);
        }
        else {
            this.doneListeners.put(key, new Integer(value));
        }
    }
    
    public void removeMessageListener(final MessageListener obj) {
        if (this.messageListeners != null) {
            this.messageListeners.removeElement(obj);
        }
        this.removeDoneListener(obj);
    }
    
    public void removeNewLineListener(final NewLineListener obj) {
        if (this.newLineListeners != null) {
            this.newLineListeners.removeElement(obj);
        }
        this.removeDoneListener(obj);
    }
    
    public void removeParserListener(final ParserListener parserListener) {
        this.removeParserMatchListener(parserListener);
        this.removeMessageListener(parserListener);
        this.removeParserTokenListener(parserListener);
        this.removeTraceListener(parserListener);
        this.removeSemanticPredicateListener(parserListener);
        this.removeSyntacticPredicateListener(parserListener);
    }
    
    public void removeParserMatchListener(final ParserMatchListener obj) {
        if (this.matchListeners != null) {
            this.matchListeners.removeElement(obj);
        }
        this.removeDoneListener(obj);
    }
    
    public void removeParserTokenListener(final ParserTokenListener obj) {
        if (this.tokenListeners != null) {
            this.tokenListeners.removeElement(obj);
        }
        this.removeDoneListener(obj);
    }
    
    public void removeSemanticPredicateListener(final SemanticPredicateListener obj) {
        if (this.semPredListeners != null) {
            this.semPredListeners.removeElement(obj);
        }
        this.removeDoneListener(obj);
    }
    
    public void removeSyntacticPredicateListener(final SyntacticPredicateListener obj) {
        if (this.synPredListeners != null) {
            this.synPredListeners.removeElement(obj);
        }
        this.removeDoneListener(obj);
    }
    
    public void removeTraceListener(final TraceListener obj) {
        if (this.traceListeners != null) {
            this.traceListeners.removeElement(obj);
        }
        this.removeDoneListener(obj);
    }
}
