// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.uri.rules.automata;

import java.util.Set;
import java.util.HashSet;
import java.util.Stack;
import com.sun.jersey.server.impl.uri.PathPattern;
import java.util.ArrayList;
import java.util.Iterator;
import com.sun.jersey.spi.uri.rules.UriMatchResultContext;
import com.sun.jersey.server.impl.uri.rules.PatternRulePair;
import java.util.List;
import com.sun.jersey.spi.uri.rules.UriRules;

public class AutomataMatchingUriTemplateRules<R> implements UriRules<R>
{
    private final TrieNode<R> automata;
    
    public AutomataMatchingUriTemplateRules(final List<PatternRulePair<R>> rules) {
        this.automata = this.initTrie(rules);
    }
    
    @Override
    public Iterator<R> match(final CharSequence path, final UriMatchResultContext resultContext) {
        final List<String> capturingGroupValues = new ArrayList<String>();
        final TrieNode<R> node = this.find(path, capturingGroupValues);
        if (node != null) {
            return node.getValue();
        }
        return new TrieNodeValue.EmptyIterator<R>();
    }
    
    private TrieNode<R> initTrie(final List<PatternRulePair<R>> rules) {
        final TrieNode<R> a = new TrieNode<R>();
        for (final PatternRulePair<R> prp : rules) {
            if (!(prp.p instanceof PathPattern)) {
                throw new IllegalArgumentException("The automata matching algorithm currently only worksfor UriPattern instance that are instances of PathPattern");
            }
            final PathPattern p = (PathPattern)prp.p;
            a.add(p.getTemplate().getTemplate(), prp.r, prp.p);
        }
        a.pack();
        return a;
    }
    
    private TrieNode<R> find(final CharSequence uri, final List<String> templateValues) {
        final int length = uri.length();
        final Stack<SearchState<R>> stack = new Stack<SearchState<R>>();
        final Stack<TrieNode<R>> candidates = new Stack<TrieNode<R>>();
        final Set<TrieArc<R>> visitedArcs = new HashSet<TrieArc<R>>();
        TrieNode<R> node = this.automata;
        TrieArc<R> nextArc = node.getFirstArc();
        int i = 0;
        while (true) {
            if (i >= length) {
                if (node.hasValue()) {
                    break;
                }
                SearchState<R> state;
                for (nextArc = null; !stack.isEmpty() && nextArc == null; nextArc = state.arc.next, node = state.node, i = state.i) {
                    state = stack.pop();
                }
                if (nextArc != null) {
                    while (visitedArcs.contains(nextArc)) {
                        nextArc = nextArc.next;
                    }
                    if (nextArc != null) {
                        visitedArcs.add(nextArc);
                    }
                }
                if (nextArc == null) {
                    break;
                }
                continue;
            }
            else if (nextArc == null && node.isWildcard()) {
                int p = 0;
                TrieArc<R> exitArc = null;
                while (i + p < length && (exitArc = node.matchExitArc(uri, i + p)) == null) {
                    ++p;
                }
                if (exitArc != null) {
                    nextArc = exitArc;
                }
                i += p;
            }
            else {
                if (nextArc == null && !node.isWildcard()) {
                    break;
                }
                if (nextArc.next != null && node.isWildcard()) {
                    stack.push(new SearchState<R>(node, nextArc, i));
                }
                if (node.hasValue()) {
                    candidates.push(node);
                }
                if (node.isWildcard() && nextArc.match(uri, i) > 0) {
                    i += nextArc.length();
                    node = nextArc.target;
                    nextArc = node.getFirstArc();
                }
                else if (node.isWildcard() && nextArc.match(uri, i) == 0) {
                    nextArc = nextArc.next;
                    if (nextArc != null) {
                        continue;
                    }
                    ++i;
                }
                else if (!node.isWildcard() && nextArc.match(uri, i) > 0) {
                    i += nextArc.length();
                    node = nextArc.target;
                    nextArc = node.getFirstArc();
                }
                else {
                    if (node.isWildcard() || nextArc.match(uri, i) != 0) {
                        continue;
                    }
                    nextArc = nextArc.next;
                }
            }
        }
        if (node.hasValue() && node.getPattern().match(uri, templateValues)) {
            return node;
        }
        while (!candidates.isEmpty()) {
            final TrieNode<R> s = candidates.pop();
            if (s.getPattern().match(uri, templateValues)) {
                return s;
            }
        }
        templateValues.clear();
        return null;
    }
    
    private static final class SearchState<E>
    {
        final TrieNode<E> node;
        final TrieArc<E> arc;
        final int i;
        
        public SearchState(final TrieNode<E> node, final TrieArc<E> arc, final int i) {
            this.node = node;
            this.arc = arc;
            this.i = i;
        }
    }
}
