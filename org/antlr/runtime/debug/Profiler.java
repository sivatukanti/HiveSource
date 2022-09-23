// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime.debug;

import java.util.Iterator;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.IntStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.antlr.runtime.misc.DoubleKeyMap;
import java.util.Stack;
import java.util.Set;
import org.antlr.runtime.Token;

public class Profiler extends BlankDebugEventListener
{
    public static final String DATA_SEP = "\t";
    public static final String newline;
    static boolean dump;
    public static final String Version = "3";
    public static final String RUNTIME_STATS_FILENAME = "runtime.stats";
    public DebugParser parser;
    protected int ruleLevel;
    protected Token lastRealTokenTouchedInDecision;
    protected Set<String> uniqueRules;
    protected Stack<String> currentGrammarFileName;
    protected Stack<String> currentRuleName;
    protected Stack<Integer> currentLine;
    protected Stack<Integer> currentPos;
    protected DoubleKeyMap<String, Integer, DecisionDescriptor> decisions;
    protected List<DecisionEvent> decisionEvents;
    protected Stack<DecisionEvent> decisionStack;
    protected int backtrackDepth;
    ProfileStats stats;
    
    public Profiler() {
        this.parser = null;
        this.ruleLevel = 0;
        this.uniqueRules = new HashSet<String>();
        this.currentGrammarFileName = new Stack<String>();
        this.currentRuleName = new Stack<String>();
        this.currentLine = new Stack<Integer>();
        this.currentPos = new Stack<Integer>();
        this.decisions = new DoubleKeyMap<String, Integer, DecisionDescriptor>();
        this.decisionEvents = new ArrayList<DecisionEvent>();
        this.decisionStack = new Stack<DecisionEvent>();
        this.stats = new ProfileStats();
    }
    
    public Profiler(final DebugParser parser) {
        this.parser = null;
        this.ruleLevel = 0;
        this.uniqueRules = new HashSet<String>();
        this.currentGrammarFileName = new Stack<String>();
        this.currentRuleName = new Stack<String>();
        this.currentLine = new Stack<Integer>();
        this.currentPos = new Stack<Integer>();
        this.decisions = new DoubleKeyMap<String, Integer, DecisionDescriptor>();
        this.decisionEvents = new ArrayList<DecisionEvent>();
        this.decisionStack = new Stack<DecisionEvent>();
        this.stats = new ProfileStats();
        this.parser = parser;
    }
    
    public void enterRule(final String grammarFileName, final String ruleName) {
        ++this.ruleLevel;
        final ProfileStats stats = this.stats;
        ++stats.numRuleInvocations;
        this.uniqueRules.add(grammarFileName + ":" + ruleName);
        this.stats.maxRuleInvocationDepth = Math.max(this.stats.maxRuleInvocationDepth, this.ruleLevel);
        this.currentGrammarFileName.push(grammarFileName);
        this.currentRuleName.push(ruleName);
    }
    
    public void exitRule(final String grammarFileName, final String ruleName) {
        --this.ruleLevel;
        this.currentGrammarFileName.pop();
        this.currentRuleName.pop();
    }
    
    public void examineRuleMemoization(final IntStream input, final int ruleIndex, final int stopIndex, final String ruleName) {
        if (Profiler.dump) {
            System.out.println("examine memo " + ruleName + " at " + input.index() + ": " + stopIndex);
        }
        if (stopIndex == -1) {
            final ProfileStats stats = this.stats;
            ++stats.numMemoizationCacheMisses;
            final ProfileStats stats2 = this.stats;
            ++stats2.numGuessingRuleInvocations;
            final DecisionEvent currentDecision = this.currentDecision();
            ++currentDecision.numMemoizationCacheMisses;
        }
        else {
            final ProfileStats stats3 = this.stats;
            ++stats3.numMemoizationCacheHits;
            final DecisionEvent currentDecision2 = this.currentDecision();
            ++currentDecision2.numMemoizationCacheHits;
        }
    }
    
    public void memoize(final IntStream input, final int ruleIndex, final int ruleStartIndex, final String ruleName) {
        if (Profiler.dump) {
            System.out.println("memoize " + ruleName);
        }
        final ProfileStats stats = this.stats;
        ++stats.numMemoizationCacheEntries;
    }
    
    public void location(final int line, final int pos) {
        this.currentLine.push(new Integer(line));
        this.currentPos.push(new Integer(pos));
    }
    
    public void enterDecision(final int decisionNumber, final boolean couldBacktrack) {
        this.lastRealTokenTouchedInDecision = null;
        final ProfileStats stats = this.stats;
        ++stats.numDecisionEvents;
        final int startingLookaheadIndex = this.parser.getTokenStream().index();
        final TokenStream input = this.parser.getTokenStream();
        if (Profiler.dump) {
            System.out.println("enterDecision canBacktrack=" + couldBacktrack + " " + decisionNumber + " backtrack depth " + this.backtrackDepth + " @ " + input.get(input.index()) + " rule " + this.locationDescription());
        }
        final String g = this.currentGrammarFileName.peek();
        DecisionDescriptor descriptor = this.decisions.get(g, new Integer(decisionNumber));
        if (descriptor == null) {
            descriptor = new DecisionDescriptor();
            this.decisions.put(g, new Integer(decisionNumber), descriptor);
            descriptor.decision = decisionNumber;
            descriptor.fileName = this.currentGrammarFileName.peek();
            descriptor.ruleName = this.currentRuleName.peek();
            descriptor.line = this.currentLine.peek();
            descriptor.pos = this.currentPos.peek();
            descriptor.couldBacktrack = couldBacktrack;
        }
        final DecisionDescriptor decisionDescriptor = descriptor;
        ++decisionDescriptor.n;
        final DecisionEvent d = new DecisionEvent();
        this.decisionStack.push(d);
        d.decision = descriptor;
        d.startTime = System.currentTimeMillis();
        d.startIndex = startingLookaheadIndex;
    }
    
    public void exitDecision(final int decisionNumber) {
        final DecisionEvent d = this.decisionStack.pop();
        d.stopTime = System.currentTimeMillis();
        final int lastTokenIndex = this.lastRealTokenTouchedInDecision.getTokenIndex();
        final int numHidden = this.getNumberOfHiddenTokens(d.startIndex, lastTokenIndex);
        final int depth = lastTokenIndex - d.startIndex - numHidden + 1;
        d.k = depth;
        d.decision.maxk = Math.max(d.decision.maxk, depth);
        if (Profiler.dump) {
            System.out.println("exitDecision " + decisionNumber + " in " + d.decision.ruleName + " lookahead " + d.k + " max token " + this.lastRealTokenTouchedInDecision);
        }
        this.decisionEvents.add(d);
    }
    
    public void consumeToken(final Token token) {
        if (Profiler.dump) {
            System.out.println("consume token " + token);
        }
        if (!this.inDecision()) {
            final ProfileStats stats = this.stats;
            ++stats.numTokens;
            return;
        }
        if (this.lastRealTokenTouchedInDecision == null || this.lastRealTokenTouchedInDecision.getTokenIndex() < token.getTokenIndex()) {
            this.lastRealTokenTouchedInDecision = token;
        }
        final DecisionEvent d = this.currentDecision();
        final int thisRefIndex = token.getTokenIndex();
        final int numHidden = this.getNumberOfHiddenTokens(d.startIndex, thisRefIndex);
        final int depth = thisRefIndex - d.startIndex - numHidden + 1;
        if (Profiler.dump) {
            System.out.println("consume " + thisRefIndex + " " + depth + " tokens ahead in " + d.decision.ruleName + "-" + d.decision.decision + " start index " + d.startIndex);
        }
    }
    
    public boolean inDecision() {
        return this.decisionStack.size() > 0;
    }
    
    public void consumeHiddenToken(final Token token) {
        if (!this.inDecision()) {
            final ProfileStats stats = this.stats;
            ++stats.numHiddenTokens;
        }
    }
    
    public void LT(final int i, final Token t) {
        if (this.inDecision() && i > 0) {
            final DecisionEvent d = this.currentDecision();
            if (Profiler.dump) {
                System.out.println("LT(" + i + ")=" + t + " index " + t.getTokenIndex() + " relative to " + d.decision.ruleName + "-" + d.decision.decision + " start index " + d.startIndex);
            }
            if (this.lastRealTokenTouchedInDecision == null || this.lastRealTokenTouchedInDecision.getTokenIndex() < t.getTokenIndex()) {
                this.lastRealTokenTouchedInDecision = t;
                if (Profiler.dump) {
                    System.out.println("set last token " + this.lastRealTokenTouchedInDecision);
                }
            }
        }
    }
    
    public void beginBacktrack(final int level) {
        if (Profiler.dump) {
            System.out.println("enter backtrack " + level);
        }
        ++this.backtrackDepth;
        final DecisionEvent e = this.currentDecision();
        if (e.decision.couldBacktrack) {
            final ProfileStats stats = this.stats;
            ++stats.numBacktrackOccurrences;
            final DecisionDescriptor decision = e.decision;
            ++decision.numBacktrackOccurrences;
            e.backtracks = true;
        }
    }
    
    public void endBacktrack(final int level, final boolean successful) {
        if (Profiler.dump) {
            System.out.println("exit backtrack " + level + ": " + successful);
        }
        --this.backtrackDepth;
    }
    
    public void mark(final int i) {
        if (Profiler.dump) {
            System.out.println("mark " + i);
        }
    }
    
    public void rewind(final int i) {
        if (Profiler.dump) {
            System.out.println("rewind " + i);
        }
    }
    
    public void rewind() {
        if (Profiler.dump) {
            System.out.println("rewind");
        }
    }
    
    protected DecisionEvent currentDecision() {
        return this.decisionStack.peek();
    }
    
    public void recognitionException(final RecognitionException e) {
        final ProfileStats stats = this.stats;
        ++stats.numReportedErrors;
    }
    
    public void semanticPredicate(final boolean result, final String predicate) {
        final ProfileStats stats = this.stats;
        ++stats.numSemanticPredicates;
        if (this.inDecision()) {
            final DecisionEvent d = this.currentDecision();
            d.evalSemPred = true;
            final DecisionDescriptor decision = d.decision;
            ++decision.numSemPredEvals;
            if (Profiler.dump) {
                System.out.println("eval " + predicate + " in " + d.decision.ruleName + "-" + d.decision.decision);
            }
        }
    }
    
    public void terminate() {
        for (final DecisionEvent e : this.decisionEvents) {
            final DecisionDescriptor decision = e.decision;
            decision.avgk += e.k;
            final ProfileStats stats = this.stats;
            stats.avgkPerDecisionEvent += e.k;
            if (e.backtracks) {
                final ProfileStats stats2 = this.stats;
                stats2.avgkPerBacktrackingDecisionEvent += e.k;
            }
        }
        this.stats.averageDecisionPercentBacktracks = 0.0f;
        for (final DecisionDescriptor d : this.decisions.values()) {
            final ProfileStats stats3 = this.stats;
            ++stats3.numDecisionsCovered;
            final DecisionDescriptor decisionDescriptor = d;
            decisionDescriptor.avgk /= d.n;
            if (d.couldBacktrack) {
                final ProfileStats stats4 = this.stats;
                ++stats4.numDecisionsThatPotentiallyBacktrack;
                final float percentBacktracks = d.numBacktrackOccurrences / (float)d.n;
                final ProfileStats stats5 = this.stats;
                stats5.averageDecisionPercentBacktracks += percentBacktracks;
            }
            if (d.numBacktrackOccurrences > 0) {
                final ProfileStats stats6 = this.stats;
                ++stats6.numDecisionsThatDoBacktrack;
            }
        }
        final ProfileStats stats7 = this.stats;
        stats7.averageDecisionPercentBacktracks /= this.stats.numDecisionsThatPotentiallyBacktrack;
        final ProfileStats stats8 = this.stats;
        stats8.averageDecisionPercentBacktracks *= 100.0f;
        final ProfileStats stats9 = this.stats;
        stats9.avgkPerDecisionEvent /= this.stats.numDecisionEvents;
        final ProfileStats stats10 = this.stats;
        stats10.avgkPerBacktrackingDecisionEvent /= this.stats.numBacktrackOccurrences;
        System.err.println(this.toString());
        System.err.println(this.getDecisionStatsDump());
    }
    
    public void setParser(final DebugParser parser) {
        this.parser = parser;
    }
    
    public String toNotifyString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("3");
        buf.append('\t');
        buf.append(this.parser.getClass().getName());
        return buf.toString();
    }
    
    public String toString() {
        return toString(this.getReport());
    }
    
    public ProfileStats getReport() {
        this.stats.Version = "3";
        this.stats.name = this.parser.getClass().getName();
        this.stats.numUniqueRulesInvoked = this.uniqueRules.size();
        return this.stats;
    }
    
    public DoubleKeyMap getDecisionStats() {
        return this.decisions;
    }
    
    public List getDecisionEvents() {
        return this.decisionEvents;
    }
    
    public static String toString(final ProfileStats stats) {
        final StringBuffer buf = new StringBuffer();
        buf.append("ANTLR Runtime Report; Profile Version ");
        buf.append(stats.Version);
        buf.append(Profiler.newline);
        buf.append("parser name ");
        buf.append(stats.name);
        buf.append(Profiler.newline);
        buf.append("Number of rule invocations ");
        buf.append(stats.numRuleInvocations);
        buf.append(Profiler.newline);
        buf.append("Number of unique rules visited ");
        buf.append(stats.numUniqueRulesInvoked);
        buf.append(Profiler.newline);
        buf.append("Number of decision events ");
        buf.append(stats.numDecisionEvents);
        buf.append(Profiler.newline);
        buf.append("Overall average k per decision event ");
        buf.append(stats.avgkPerDecisionEvent);
        buf.append(Profiler.newline);
        buf.append("Number of backtracking occurrences (can be multiple per decision) ");
        buf.append(stats.numBacktrackOccurrences);
        buf.append(Profiler.newline);
        buf.append("Overall average k per decision event that backtracks ");
        buf.append(stats.avgkPerBacktrackingDecisionEvent);
        buf.append(Profiler.newline);
        buf.append("Number of rule invocations while backtracking ");
        buf.append(stats.numGuessingRuleInvocations);
        buf.append(Profiler.newline);
        buf.append("num decisions that potentially backtrack ");
        buf.append(stats.numDecisionsThatPotentiallyBacktrack);
        buf.append(Profiler.newline);
        buf.append("num decisions that do backtrack ");
        buf.append(stats.numDecisionsThatDoBacktrack);
        buf.append(Profiler.newline);
        buf.append("num decisions that potentially backtrack but don't ");
        buf.append(stats.numDecisionsThatPotentiallyBacktrack - stats.numDecisionsThatDoBacktrack);
        buf.append(Profiler.newline);
        buf.append("average % of time a potentially backtracking decision backtracks ");
        buf.append(stats.averageDecisionPercentBacktracks);
        buf.append(Profiler.newline);
        buf.append("num unique decisions covered ");
        buf.append(stats.numDecisionsCovered);
        buf.append(Profiler.newline);
        buf.append("max rule invocation nesting depth ");
        buf.append(stats.maxRuleInvocationDepth);
        buf.append(Profiler.newline);
        buf.append("rule memoization cache size ");
        buf.append(stats.numMemoizationCacheEntries);
        buf.append(Profiler.newline);
        buf.append("number of rule memoization cache hits ");
        buf.append(stats.numMemoizationCacheHits);
        buf.append(Profiler.newline);
        buf.append("number of rule memoization cache misses ");
        buf.append(stats.numMemoizationCacheMisses);
        buf.append(Profiler.newline);
        buf.append("number of tokens ");
        buf.append(stats.numTokens);
        buf.append(Profiler.newline);
        buf.append("number of hidden tokens ");
        buf.append(stats.numHiddenTokens);
        buf.append(Profiler.newline);
        buf.append("number of char ");
        buf.append(stats.numCharsMatched);
        buf.append(Profiler.newline);
        buf.append("number of hidden char ");
        buf.append(stats.numHiddenCharsMatched);
        buf.append(Profiler.newline);
        buf.append("number of syntax errors ");
        buf.append(stats.numReportedErrors);
        buf.append(Profiler.newline);
        return buf.toString();
    }
    
    public String getDecisionStatsDump() {
        final StringBuffer buf = new StringBuffer();
        buf.append("location");
        buf.append("\t");
        buf.append("n");
        buf.append("\t");
        buf.append("avgk");
        buf.append("\t");
        buf.append("maxk");
        buf.append("\t");
        buf.append("synpred");
        buf.append("\t");
        buf.append("sempred");
        buf.append("\t");
        buf.append("canbacktrack");
        buf.append("\n");
        for (final String fileName : this.decisions.keySet()) {
            for (final int d : this.decisions.keySet(fileName)) {
                final DecisionDescriptor s = this.decisions.get(fileName, new Integer(d));
                buf.append(s.decision);
                buf.append("@");
                buf.append(this.locationDescription(s.fileName, s.ruleName, s.line, s.pos));
                buf.append("\t");
                buf.append(s.n);
                buf.append("\t");
                buf.append(String.format("%.2f", new Float(s.avgk)));
                buf.append("\t");
                buf.append(s.maxk);
                buf.append("\t");
                buf.append(s.numBacktrackOccurrences);
                buf.append("\t");
                buf.append(s.numSemPredEvals);
                buf.append("\t");
                buf.append(s.couldBacktrack ? "1" : "0");
                buf.append(Profiler.newline);
            }
        }
        return buf.toString();
    }
    
    protected int[] trim(int[] X, final int n) {
        if (n < X.length) {
            final int[] trimmed = new int[n];
            System.arraycopy(X, 0, trimmed, 0, n);
            X = trimmed;
        }
        return X;
    }
    
    protected int[] toArray(final List a) {
        final int[] x = new int[a.size()];
        for (int i = 0; i < a.size(); ++i) {
            final Integer I = a.get(i);
            x[i] = I;
        }
        return x;
    }
    
    public int getNumberOfHiddenTokens(final int i, final int j) {
        int n = 0;
        final TokenStream input = this.parser.getTokenStream();
        for (int ti = i; ti < input.size() && ti <= j; ++ti) {
            final Token t = input.get(ti);
            if (t.getChannel() != 0) {
                ++n;
            }
        }
        return n;
    }
    
    protected String locationDescription() {
        return this.locationDescription(this.currentGrammarFileName.peek(), this.currentRuleName.peek(), this.currentLine.peek(), this.currentPos.peek());
    }
    
    protected String locationDescription(final String file, final String rule, final int line, final int pos) {
        return file + ":" + line + ":" + pos + "(" + rule + ")";
    }
    
    static {
        newline = System.getProperty("line.separator");
        Profiler.dump = false;
    }
    
    public static class ProfileStats
    {
        public String Version;
        public String name;
        public int numRuleInvocations;
        public int numUniqueRulesInvoked;
        public int numDecisionEvents;
        public int numDecisionsCovered;
        public int numDecisionsThatPotentiallyBacktrack;
        public int numDecisionsThatDoBacktrack;
        public int maxRuleInvocationDepth;
        public float avgkPerDecisionEvent;
        public float avgkPerBacktrackingDecisionEvent;
        public float averageDecisionPercentBacktracks;
        public int numBacktrackOccurrences;
        public int numFixedDecisions;
        public int minDecisionMaxFixedLookaheads;
        public int maxDecisionMaxFixedLookaheads;
        public int avgDecisionMaxFixedLookaheads;
        public int stddevDecisionMaxFixedLookaheads;
        public int numCyclicDecisions;
        public int minDecisionMaxCyclicLookaheads;
        public int maxDecisionMaxCyclicLookaheads;
        public int avgDecisionMaxCyclicLookaheads;
        public int stddevDecisionMaxCyclicLookaheads;
        public int numSemanticPredicates;
        public int numTokens;
        public int numHiddenTokens;
        public int numCharsMatched;
        public int numHiddenCharsMatched;
        public int numReportedErrors;
        public int numMemoizationCacheHits;
        public int numMemoizationCacheMisses;
        public int numGuessingRuleInvocations;
        public int numMemoizationCacheEntries;
    }
    
    public static class DecisionDescriptor
    {
        public int decision;
        public String fileName;
        public String ruleName;
        public int line;
        public int pos;
        public boolean couldBacktrack;
        public int n;
        public float avgk;
        public int maxk;
        public int numBacktrackOccurrences;
        public int numSemPredEvals;
    }
    
    public static class DecisionEvent
    {
        public DecisionDescriptor decision;
        public int startIndex;
        public int k;
        public boolean backtracks;
        public boolean evalSemPred;
        public long startTime;
        public long stopTime;
        public int numMemoizationCacheHits;
        public int numMemoizationCacheMisses;
    }
}
