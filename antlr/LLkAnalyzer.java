// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import antlr.collections.impl.BitSet;

public class LLkAnalyzer implements LLkGrammarAnalyzer
{
    public boolean DEBUG_ANALYZER;
    private AlternativeBlock currentBlock;
    protected Tool tool;
    protected Grammar grammar;
    protected boolean lexicalAnalysis;
    CharFormatter charFormatter;
    
    public LLkAnalyzer(final Tool tool) {
        this.DEBUG_ANALYZER = false;
        this.tool = null;
        this.grammar = null;
        this.lexicalAnalysis = false;
        this.charFormatter = new JavaCharFormatter();
        this.tool = tool;
    }
    
    protected boolean altUsesWildcardDefault(final Alternative alternative) {
        final AlternativeElement head = alternative.head;
        return (head instanceof TreeElement && ((TreeElement)head).root instanceof WildcardElement) || (head instanceof WildcardElement && head.next instanceof BlockEndElement);
    }
    
    public boolean deterministic(final AlternativeBlock alternativeBlock) {
        if (this.DEBUG_ANALYZER) {
            System.out.println("deterministic(" + alternativeBlock + ")");
        }
        boolean b = true;
        final int size = alternativeBlock.alternatives.size();
        final AlternativeBlock currentBlock = this.currentBlock;
        this.currentBlock = alternativeBlock;
        if (!alternativeBlock.greedy && !(alternativeBlock instanceof OneOrMoreBlock) && !(alternativeBlock instanceof ZeroOrMoreBlock)) {
            this.tool.warning("Being nongreedy only makes sense for (...)+ and (...)*", this.grammar.getFilename(), alternativeBlock.getLine(), alternativeBlock.getColumn());
        }
        if (size == 1) {
            final AlternativeElement head = alternativeBlock.getAlternativeAt(0).head;
            this.currentBlock.alti = 0;
            alternativeBlock.getAlternativeAt(0).cache[1] = head.look(1);
            alternativeBlock.getAlternativeAt(0).lookaheadDepth = 1;
            this.currentBlock = currentBlock;
            return true;
        }
        for (int i = 0; i < size - 1; ++i) {
            this.currentBlock.alti = i;
            this.currentBlock.analysisAlt = i;
            this.currentBlock.altj = i + 1;
            for (int j = i + 1; j < size; ++j) {
                this.currentBlock.altj = j;
                if (this.DEBUG_ANALYZER) {
                    System.out.println("comparing " + i + " against alt " + j);
                }
                this.currentBlock.analysisAlt = j;
                int n = 1;
                final Lookahead[] array = new Lookahead[this.grammar.maxk + 1];
                boolean b2;
                do {
                    b2 = false;
                    if (this.DEBUG_ANALYZER) {
                        System.out.println("checking depth " + n + "<=" + this.grammar.maxk);
                    }
                    final Lookahead altLookahead = this.getAltLookahead(alternativeBlock, i, n);
                    final Lookahead altLookahead2 = this.getAltLookahead(alternativeBlock, j, n);
                    if (this.DEBUG_ANALYZER) {
                        System.out.println("p is " + altLookahead.toString(",", this.charFormatter, this.grammar));
                    }
                    if (this.DEBUG_ANALYZER) {
                        System.out.println("q is " + altLookahead2.toString(",", this.charFormatter, this.grammar));
                    }
                    array[n] = altLookahead.intersection(altLookahead2);
                    if (this.DEBUG_ANALYZER) {
                        System.out.println("intersection at depth " + n + " is " + array[n].toString());
                    }
                    if (!array[n].nil()) {
                        b2 = true;
                        ++n;
                    }
                } while (b2 && n <= this.grammar.maxk);
                final Alternative alternative = alternativeBlock.getAlternativeAt(i);
                final Alternative alternative2 = alternativeBlock.getAlternativeAt(j);
                if (b2) {
                    b = false;
                    alternative.lookaheadDepth = Integer.MAX_VALUE;
                    alternative2.lookaheadDepth = Integer.MAX_VALUE;
                    if (alternative.synPred != null) {
                        if (this.DEBUG_ANALYZER) {
                            System.out.println("alt " + i + " has a syn pred");
                        }
                    }
                    else if (alternative.semPred != null) {
                        if (this.DEBUG_ANALYZER) {
                            System.out.println("alt " + i + " has a sem pred");
                        }
                    }
                    else if (!this.altUsesWildcardDefault(alternative2)) {
                        if (!alternativeBlock.warnWhenFollowAmbig) {
                            if (alternative.head instanceof BlockEndElement) {
                                continue;
                            }
                            if (alternative2.head instanceof BlockEndElement) {
                                continue;
                            }
                        }
                        if (alternativeBlock.generateAmbigWarnings) {
                            if (alternativeBlock.greedySet && alternativeBlock.greedy) {
                                if (alternative.head instanceof BlockEndElement && !(alternative2.head instanceof BlockEndElement)) {
                                    continue;
                                }
                                if (alternative2.head instanceof BlockEndElement && !(alternative.head instanceof BlockEndElement)) {
                                    continue;
                                }
                            }
                            this.tool.errorHandler.warnAltAmbiguity(this.grammar, alternativeBlock, this.lexicalAnalysis, this.grammar.maxk, array, i, j);
                        }
                    }
                }
                else {
                    alternative.lookaheadDepth = Math.max(alternative.lookaheadDepth, n);
                    alternative2.lookaheadDepth = Math.max(alternative2.lookaheadDepth, n);
                }
            }
        }
        this.currentBlock = currentBlock;
        return b;
    }
    
    public boolean deterministic(final OneOrMoreBlock oneOrMoreBlock) {
        if (this.DEBUG_ANALYZER) {
            System.out.println("deterministic(...)+(" + oneOrMoreBlock + ")");
        }
        final AlternativeBlock currentBlock = this.currentBlock;
        this.currentBlock = oneOrMoreBlock;
        final boolean deterministic = this.deterministic((AlternativeBlock)oneOrMoreBlock);
        final boolean deterministicImpliedPath = this.deterministicImpliedPath(oneOrMoreBlock);
        this.currentBlock = currentBlock;
        return deterministicImpliedPath && deterministic;
    }
    
    public boolean deterministic(final ZeroOrMoreBlock zeroOrMoreBlock) {
        if (this.DEBUG_ANALYZER) {
            System.out.println("deterministic(...)*(" + zeroOrMoreBlock + ")");
        }
        final AlternativeBlock currentBlock = this.currentBlock;
        this.currentBlock = zeroOrMoreBlock;
        final boolean deterministic = this.deterministic((AlternativeBlock)zeroOrMoreBlock);
        final boolean deterministicImpliedPath = this.deterministicImpliedPath(zeroOrMoreBlock);
        this.currentBlock = currentBlock;
        return deterministicImpliedPath && deterministic;
    }
    
    public boolean deterministicImpliedPath(final BlockWithImpliedExitPath blockWithImpliedExitPath) {
        boolean b = true;
        final int size = blockWithImpliedExitPath.getAlternatives().size();
        this.currentBlock.altj = -1;
        if (this.DEBUG_ANALYZER) {
            System.out.println("deterministicImpliedPath");
        }
        for (int i = 0; i < size; ++i) {
            final Alternative alternative = blockWithImpliedExitPath.getAlternativeAt(i);
            if (alternative.head instanceof BlockEndElement) {
                this.tool.warning("empty alternative makes no sense in (...)* or (...)+", this.grammar.getFilename(), blockWithImpliedExitPath.getLine(), blockWithImpliedExitPath.getColumn());
            }
            int n = 1;
            final Lookahead[] array = new Lookahead[this.grammar.maxk + 1];
            boolean b2;
            do {
                b2 = false;
                if (this.DEBUG_ANALYZER) {
                    System.out.println("checking depth " + n + "<=" + this.grammar.maxk);
                }
                final Lookahead look = blockWithImpliedExitPath.next.look(n);
                blockWithImpliedExitPath.exitCache[n] = look;
                this.currentBlock.alti = i;
                final Lookahead altLookahead = this.getAltLookahead(blockWithImpliedExitPath, i, n);
                if (this.DEBUG_ANALYZER) {
                    System.out.println("follow is " + look.toString(",", this.charFormatter, this.grammar));
                }
                if (this.DEBUG_ANALYZER) {
                    System.out.println("p is " + altLookahead.toString(",", this.charFormatter, this.grammar));
                }
                array[n] = look.intersection(altLookahead);
                if (this.DEBUG_ANALYZER) {
                    System.out.println("intersection at depth " + n + " is " + array[n]);
                }
                if (!array[n].nil()) {
                    b2 = true;
                    ++n;
                }
            } while (b2 && n <= this.grammar.maxk);
            if (b2) {
                b = false;
                alternative.lookaheadDepth = Integer.MAX_VALUE;
                blockWithImpliedExitPath.exitLookaheadDepth = Integer.MAX_VALUE;
                final Alternative alternative2 = blockWithImpliedExitPath.getAlternativeAt(this.currentBlock.alti);
                if (blockWithImpliedExitPath.warnWhenFollowAmbig) {
                    if (blockWithImpliedExitPath.generateAmbigWarnings) {
                        if (blockWithImpliedExitPath.greedy && blockWithImpliedExitPath.greedySet && !(alternative2.head instanceof BlockEndElement)) {
                            if (this.DEBUG_ANALYZER) {
                                System.out.println("greedy loop");
                            }
                        }
                        else if (!blockWithImpliedExitPath.greedy && !(alternative2.head instanceof BlockEndElement)) {
                            if (this.DEBUG_ANALYZER) {
                                System.out.println("nongreedy loop");
                            }
                            if (!lookaheadEquivForApproxAndFullAnalysis(blockWithImpliedExitPath.exitCache, this.grammar.maxk)) {
                                this.tool.warning(new String[] { "nongreedy block may exit incorrectly due", "\tto limitations of linear approximate lookahead (first k-1 sets", "\tin lookahead not singleton)." }, this.grammar.getFilename(), blockWithImpliedExitPath.getLine(), blockWithImpliedExitPath.getColumn());
                            }
                        }
                        else {
                            this.tool.errorHandler.warnAltExitAmbiguity(this.grammar, blockWithImpliedExitPath, this.lexicalAnalysis, this.grammar.maxk, array, i);
                        }
                    }
                }
            }
            else {
                alternative.lookaheadDepth = Math.max(alternative.lookaheadDepth, n);
                blockWithImpliedExitPath.exitLookaheadDepth = Math.max(blockWithImpliedExitPath.exitLookaheadDepth, n);
            }
        }
        return b;
    }
    
    public Lookahead FOLLOW(final int i, final RuleEndElement ruleEndElement) {
        final RuleBlock ruleBlock = (RuleBlock)ruleEndElement.block;
        String s;
        if (this.lexicalAnalysis) {
            s = CodeGenerator.encodeLexerRuleName(ruleBlock.getRuleName());
        }
        else {
            s = ruleBlock.getRuleName();
        }
        if (this.DEBUG_ANALYZER) {
            System.out.println("FOLLOW(" + i + "," + s + ")");
        }
        if (ruleEndElement.lock[i]) {
            if (this.DEBUG_ANALYZER) {
                System.out.println("FOLLOW cycle to " + s);
            }
            return new Lookahead(s);
        }
        if (ruleEndElement.cache[i] == null) {
            ruleEndElement.lock[i] = true;
            final Lookahead lookahead = new Lookahead();
            final RuleSymbol ruleSymbol = (RuleSymbol)this.grammar.getSymbol(s);
            for (int j = 0; j < ruleSymbol.numReferences(); ++j) {
                final RuleRefElement reference = ruleSymbol.getReference(j);
                if (this.DEBUG_ANALYZER) {
                    System.out.println("next[" + s + "] is " + reference.next.toString());
                }
                final Lookahead look = reference.next.look(i);
                if (this.DEBUG_ANALYZER) {
                    System.out.println("FIRST of next[" + s + "] ptr is " + look.toString());
                }
                if (look.cycle != null && look.cycle.equals(s)) {
                    look.cycle = null;
                }
                lookahead.combineWith(look);
                if (this.DEBUG_ANALYZER) {
                    System.out.println("combined FOLLOW[" + s + "] is " + lookahead.toString());
                }
            }
            ruleEndElement.lock[i] = false;
            if (lookahead.fset.nil() && lookahead.cycle == null) {
                if (this.grammar instanceof TreeWalkerGrammar) {
                    lookahead.fset.add(3);
                }
                else if (this.grammar instanceof LexerGrammar) {
                    lookahead.setEpsilon();
                }
                else {
                    lookahead.fset.add(1);
                }
            }
            if (this.DEBUG_ANALYZER) {
                System.out.println("saving FOLLOW(" + i + ") for " + s + ": " + lookahead.toString(",", this.charFormatter, this.grammar));
            }
            ruleEndElement.cache[i] = (Lookahead)lookahead.clone();
            return lookahead;
        }
        if (this.DEBUG_ANALYZER) {
            System.out.println("cache entry FOLLOW(" + i + ") for " + s + ": " + ruleEndElement.cache[i].toString(",", this.charFormatter, this.grammar));
        }
        if (ruleEndElement.cache[i].cycle == null) {
            return (Lookahead)ruleEndElement.cache[i].clone();
        }
        final RuleEndElement endNode = ((RuleSymbol)this.grammar.getSymbol(ruleEndElement.cache[i].cycle)).getBlock().endNode;
        if (endNode.cache[i] == null) {
            return (Lookahead)ruleEndElement.cache[i].clone();
        }
        if (this.DEBUG_ANALYZER) {
            System.out.println("combining FOLLOW(" + i + ") for " + s + ": from " + ruleEndElement.cache[i].toString(",", this.charFormatter, this.grammar) + " with FOLLOW for " + ((RuleBlock)endNode.block).getRuleName() + ": " + endNode.cache[i].toString(",", this.charFormatter, this.grammar));
        }
        if (endNode.cache[i].cycle == null) {
            ruleEndElement.cache[i].combineWith(endNode.cache[i]);
            ruleEndElement.cache[i].cycle = null;
        }
        else {
            final Lookahead follow = this.FOLLOW(i, endNode);
            ruleEndElement.cache[i].combineWith(follow);
            ruleEndElement.cache[i].cycle = follow.cycle;
        }
        if (this.DEBUG_ANALYZER) {
            System.out.println("saving FOLLOW(" + i + ") for " + s + ": from " + ruleEndElement.cache[i].toString(",", this.charFormatter, this.grammar));
        }
        return (Lookahead)ruleEndElement.cache[i].clone();
    }
    
    private Lookahead getAltLookahead(final AlternativeBlock alternativeBlock, final int n, final int n2) {
        final Alternative alternative = alternativeBlock.getAlternativeAt(n);
        final AlternativeElement head = alternative.head;
        Lookahead look;
        if (alternative.cache[n2] == null) {
            look = head.look(n2);
            alternative.cache[n2] = look;
        }
        else {
            look = alternative.cache[n2];
        }
        return look;
    }
    
    public Lookahead look(final int i, final ActionElement obj) {
        if (this.DEBUG_ANALYZER) {
            System.out.println("lookAction(" + i + "," + obj + ")");
        }
        return obj.next.look(i);
    }
    
    public Lookahead look(final int i, final AlternativeBlock obj) {
        if (this.DEBUG_ANALYZER) {
            System.out.println("lookAltBlk(" + i + "," + obj + ")");
        }
        final AlternativeBlock currentBlock = this.currentBlock;
        this.currentBlock = obj;
        final Lookahead lookahead = new Lookahead();
        for (int j = 0; j < obj.alternatives.size(); ++j) {
            if (this.DEBUG_ANALYZER) {
                System.out.println("alt " + j + " of " + obj);
            }
            this.currentBlock.analysisAlt = j;
            final Alternative alternative = obj.getAlternativeAt(j);
            final AlternativeElement head = alternative.head;
            if (this.DEBUG_ANALYZER && alternative.head == alternative.tail) {
                System.out.println("alt " + j + " is empty");
            }
            lookahead.combineWith(head.look(i));
        }
        if (i == 1 && obj.not && this.subruleCanBeInverted(obj, this.lexicalAnalysis)) {
            if (this.lexicalAnalysis) {
                final BitSet fset = (BitSet)((LexerGrammar)this.grammar).charVocabulary.clone();
                final int[] array = lookahead.fset.toArray();
                for (int k = 0; k < array.length; ++k) {
                    fset.remove(array[k]);
                }
                lookahead.fset = fset;
            }
            else {
                lookahead.fset.notInPlace(4, this.grammar.tokenManager.maxTokenType());
            }
        }
        this.currentBlock = currentBlock;
        return lookahead;
    }
    
    public Lookahead look(final int i, final BlockEndElement blockEndElement) {
        if (this.DEBUG_ANALYZER) {
            System.out.println("lookBlockEnd(" + i + ", " + blockEndElement.block + "); lock is " + blockEndElement.lock[i]);
        }
        if (blockEndElement.lock[i]) {
            return new Lookahead();
        }
        Lookahead look;
        if (blockEndElement.block instanceof ZeroOrMoreBlock || blockEndElement.block instanceof OneOrMoreBlock) {
            blockEndElement.lock[i] = true;
            look = this.look(i, blockEndElement.block);
            blockEndElement.lock[i] = false;
        }
        else {
            look = new Lookahead();
        }
        if (blockEndElement.block instanceof TreeElement) {
            look.combineWith(Lookahead.of(3));
        }
        else if (blockEndElement.block instanceof SynPredBlock) {
            look.setEpsilon();
        }
        else {
            look.combineWith(blockEndElement.block.next.look(i));
        }
        return look;
    }
    
    public Lookahead look(final int i, final CharLiteralElement obj) {
        if (this.DEBUG_ANALYZER) {
            System.out.println("lookCharLiteral(" + i + "," + obj + ")");
        }
        if (i > 1) {
            return obj.next.look(i - 1);
        }
        if (!this.lexicalAnalysis) {
            this.tool.panic("Character literal reference found in parser");
            return Lookahead.of(obj.getType());
        }
        if (obj.not) {
            final BitSet set = (BitSet)((LexerGrammar)this.grammar).charVocabulary.clone();
            if (this.DEBUG_ANALYZER) {
                System.out.println("charVocab is " + set.toString());
            }
            this.removeCompetingPredictionSets(set, obj);
            if (this.DEBUG_ANALYZER) {
                System.out.println("charVocab after removal of prior alt lookahead " + set.toString());
            }
            set.clear(obj.getType());
            return new Lookahead(set);
        }
        return Lookahead.of(obj.getType());
    }
    
    public Lookahead look(final int i, final CharRangeElement obj) {
        if (this.DEBUG_ANALYZER) {
            System.out.println("lookCharRange(" + i + "," + obj + ")");
        }
        if (i > 1) {
            return obj.next.look(i - 1);
        }
        final BitSet of = BitSet.of(obj.begin);
        for (int j = obj.begin + '\u0001'; j <= obj.end; ++j) {
            of.add(j);
        }
        return new Lookahead(of);
    }
    
    public Lookahead look(final int i, final GrammarAtom obj) {
        if (this.DEBUG_ANALYZER) {
            System.out.println("look(" + i + "," + obj + "[" + obj.getType() + "])");
        }
        if (this.lexicalAnalysis) {
            this.tool.panic("token reference found in lexer");
        }
        if (i > 1) {
            return obj.next.look(i - 1);
        }
        final Lookahead of = Lookahead.of(obj.getType());
        if (obj.not) {
            of.fset.notInPlace(4, this.grammar.tokenManager.maxTokenType());
            this.removeCompetingPredictionSets(of.fset, obj);
        }
        return of;
    }
    
    public Lookahead look(final int i, final OneOrMoreBlock obj) {
        if (this.DEBUG_ANALYZER) {
            System.out.println("look+" + i + "," + obj + ")");
        }
        return this.look(i, (AlternativeBlock)obj);
    }
    
    public Lookahead look(final int i, final RuleBlock obj) {
        if (this.DEBUG_ANALYZER) {
            System.out.println("lookRuleBlk(" + i + "," + obj + ")");
        }
        return this.look(i, (AlternativeBlock)obj);
    }
    
    public Lookahead look(final int i, final RuleEndElement ruleEndElement) {
        if (this.DEBUG_ANALYZER) {
            System.out.println("lookRuleBlockEnd(" + i + "); noFOLLOW=" + ruleEndElement.noFOLLOW + "; lock is " + ruleEndElement.lock[i]);
        }
        if (ruleEndElement.noFOLLOW) {
            final Lookahead lookahead = new Lookahead();
            lookahead.setEpsilon();
            lookahead.epsilonDepth = BitSet.of(i);
            return lookahead;
        }
        return this.FOLLOW(i, ruleEndElement);
    }
    
    public Lookahead look(final int i, final RuleRefElement obj) {
        if (this.DEBUG_ANALYZER) {
            System.out.println("lookRuleRef(" + i + "," + obj + ")");
        }
        final RuleSymbol ruleSymbol = (RuleSymbol)this.grammar.getSymbol(obj.targetRule);
        if (ruleSymbol == null || !ruleSymbol.defined) {
            this.tool.error("no definition of rule " + obj.targetRule, this.grammar.getFilename(), obj.getLine(), obj.getColumn());
            return new Lookahead();
        }
        final RuleEndElement endNode = ruleSymbol.getBlock().endNode;
        final boolean noFOLLOW = endNode.noFOLLOW;
        endNode.noFOLLOW = true;
        final Lookahead look = this.look(i, obj.targetRule);
        if (this.DEBUG_ANALYZER) {
            System.out.println("back from rule ref to " + obj.targetRule);
        }
        endNode.noFOLLOW = noFOLLOW;
        if (look.cycle != null) {
            this.tool.error("infinite recursion to rule " + look.cycle + " from rule " + obj.enclosingRuleName, this.grammar.getFilename(), obj.getLine(), obj.getColumn());
        }
        if (look.containsEpsilon()) {
            if (this.DEBUG_ANALYZER) {
                System.out.println("rule ref to " + obj.targetRule + " has eps, depth: " + look.epsilonDepth);
            }
            look.resetEpsilon();
            final int[] array = look.epsilonDepth.toArray();
            look.epsilonDepth = null;
            for (int j = 0; j < array.length; ++j) {
                look.combineWith(obj.next.look(i - (i - array[j])));
            }
        }
        return look;
    }
    
    public Lookahead look(final int i, final StringLiteralElement obj) {
        if (this.DEBUG_ANALYZER) {
            System.out.println("lookStringLiteral(" + i + "," + obj + ")");
        }
        if (this.lexicalAnalysis) {
            if (i > obj.processedAtomText.length()) {
                return obj.next.look(i - obj.processedAtomText.length());
            }
            return Lookahead.of(obj.processedAtomText.charAt(i - 1));
        }
        else {
            if (i > 1) {
                return obj.next.look(i - 1);
            }
            final Lookahead of = Lookahead.of(obj.getType());
            if (obj.not) {
                of.fset.notInPlace(4, this.grammar.tokenManager.maxTokenType());
            }
            return of;
        }
    }
    
    public Lookahead look(final int i, final SynPredBlock obj) {
        if (this.DEBUG_ANALYZER) {
            System.out.println("look=>(" + i + "," + obj + ")");
        }
        return obj.next.look(i);
    }
    
    public Lookahead look(final int i, final TokenRangeElement obj) {
        if (this.DEBUG_ANALYZER) {
            System.out.println("lookTokenRange(" + i + "," + obj + ")");
        }
        if (i > 1) {
            return obj.next.look(i - 1);
        }
        final BitSet of = BitSet.of(obj.begin);
        for (int j = obj.begin + 1; j <= obj.end; ++j) {
            of.add(j);
        }
        return new Lookahead(of);
    }
    
    public Lookahead look(final int i, final TreeElement treeElement) {
        if (this.DEBUG_ANALYZER) {
            System.out.println("look(" + i + "," + treeElement.root + "[" + treeElement.root.getType() + "])");
        }
        if (i > 1) {
            return treeElement.next.look(i - 1);
        }
        Lookahead lookahead;
        if (treeElement.root instanceof WildcardElement) {
            lookahead = treeElement.root.look(1);
        }
        else {
            lookahead = Lookahead.of(treeElement.root.getType());
            if (treeElement.root.not) {
                lookahead.fset.notInPlace(4, this.grammar.tokenManager.maxTokenType());
            }
        }
        return lookahead;
    }
    
    public Lookahead look(final int n, final WildcardElement wildcardElement) {
        if (this.DEBUG_ANALYZER) {
            System.out.println("look(" + n + "," + wildcardElement + ")");
        }
        if (n > 1) {
            return wildcardElement.next.look(n - 1);
        }
        BitSet obj;
        if (this.lexicalAnalysis) {
            obj = (BitSet)((LexerGrammar)this.grammar).charVocabulary.clone();
        }
        else {
            obj = new BitSet(1);
            obj.notInPlace(4, this.grammar.tokenManager.maxTokenType());
            if (this.DEBUG_ANALYZER) {
                System.out.println("look(" + n + "," + wildcardElement + ") after not: " + obj);
            }
        }
        return new Lookahead(obj);
    }
    
    public Lookahead look(final int i, final ZeroOrMoreBlock obj) {
        if (this.DEBUG_ANALYZER) {
            System.out.println("look*(" + i + "," + obj + ")");
        }
        final Lookahead look = this.look(i, (AlternativeBlock)obj);
        look.combineWith(obj.next.look(i));
        return look;
    }
    
    public Lookahead look(final int i, final String str) {
        if (this.DEBUG_ANALYZER) {
            System.out.println("lookRuleName(" + i + "," + str + ")");
        }
        final RuleBlock block = ((RuleSymbol)this.grammar.getSymbol(str)).getBlock();
        if (block.lock[i]) {
            if (this.DEBUG_ANALYZER) {
                System.out.println("infinite recursion to rule " + block.getRuleName());
            }
            return new Lookahead(str);
        }
        if (block.cache[i] != null) {
            if (this.DEBUG_ANALYZER) {
                System.out.println("found depth " + i + " result in FIRST " + str + " cache: " + block.cache[i].toString(",", this.charFormatter, this.grammar));
            }
            return (Lookahead)block.cache[i].clone();
        }
        block.lock[i] = true;
        final Lookahead look = this.look(i, block);
        block.lock[i] = false;
        block.cache[i] = (Lookahead)look.clone();
        if (this.DEBUG_ANALYZER) {
            System.out.println("saving depth " + i + " result in FIRST " + str + " cache: " + block.cache[i].toString(",", this.charFormatter, this.grammar));
        }
        return look;
    }
    
    public static boolean lookaheadEquivForApproxAndFullAnalysis(final Lookahead[] array, final int n) {
        for (int i = 1; i <= n - 1; ++i) {
            if (array[i].fset.degree() > 1) {
                return false;
            }
        }
        return true;
    }
    
    private void removeCompetingPredictionSets(final BitSet set, final AlternativeElement alternativeElement) {
        final AlternativeElement head = this.currentBlock.getAlternativeAt(this.currentBlock.analysisAlt).head;
        if (head instanceof TreeElement) {
            if (((TreeElement)head).root != alternativeElement) {
                return;
            }
        }
        else if (alternativeElement != head) {
            return;
        }
        for (int i = 0; i < this.currentBlock.analysisAlt; ++i) {
            set.subtractInPlace(this.currentBlock.getAlternativeAt(i).head.look(1).fset);
        }
    }
    
    private void removeCompetingPredictionSetsFromWildcard(final Lookahead[] array, final AlternativeElement alternativeElement, final int n) {
        for (int i = 1; i <= n; ++i) {
            for (int j = 0; j < this.currentBlock.analysisAlt; ++j) {
                array[i].fset.subtractInPlace(this.currentBlock.getAlternativeAt(j).head.look(i).fset);
            }
        }
    }
    
    private void reset() {
        this.grammar = null;
        this.DEBUG_ANALYZER = false;
        this.currentBlock = null;
        this.lexicalAnalysis = false;
    }
    
    public void setGrammar(final Grammar grammar) {
        if (this.grammar != null) {
            this.reset();
        }
        this.grammar = grammar;
        this.lexicalAnalysis = (this.grammar instanceof LexerGrammar);
        this.DEBUG_ANALYZER = this.grammar.analyzerDebug;
    }
    
    public boolean subruleCanBeInverted(final AlternativeBlock alternativeBlock, final boolean b) {
        if (alternativeBlock instanceof ZeroOrMoreBlock || alternativeBlock instanceof OneOrMoreBlock || alternativeBlock instanceof SynPredBlock) {
            return false;
        }
        if (alternativeBlock.alternatives.size() == 0) {
            return false;
        }
        for (int i = 0; i < alternativeBlock.alternatives.size(); ++i) {
            final Alternative alternative = alternativeBlock.getAlternativeAt(i);
            if (alternative.synPred != null || alternative.semPred != null || alternative.exceptionSpec != null) {
                return false;
            }
            final AlternativeElement head = alternative.head;
            if ((!(head instanceof CharLiteralElement) && !(head instanceof TokenRefElement) && !(head instanceof CharRangeElement) && !(head instanceof TokenRangeElement) && (!(head instanceof StringLiteralElement) || b)) || !(head.next instanceof BlockEndElement) || head.getAutoGenType() != 1) {
                return false;
            }
        }
        return true;
    }
}
