// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.dtd;

import java.util.Iterator;
import java.util.TreeSet;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import com.ctc.wstx.util.PrefixedName;
import java.util.HashMap;
import java.util.BitSet;

public final class DFAState
{
    final int mIndex;
    final boolean mAccepting;
    BitSet mTokenSet;
    HashMap<PrefixedName, DFAState> mNext;
    
    public DFAState(final int index, final BitSet tokenSet) {
        this.mNext = new HashMap<PrefixedName, DFAState>();
        this.mIndex = index;
        this.mAccepting = tokenSet.get(0);
        this.mTokenSet = tokenSet;
    }
    
    public static DFAState constructDFA(final ContentSpec rootSpec) {
        final ModelNode modelRoot = rootSpec.rewrite();
        final TokenModel eofToken = TokenModel.getNullToken();
        final ConcatModel dummyRoot = new ConcatModel(modelRoot, eofToken);
        final ArrayList<TokenModel> tokens = new ArrayList<TokenModel>();
        tokens.add(eofToken);
        dummyRoot.indexTokens(tokens);
        final int flen = tokens.size();
        final BitSet[] followPos = new BitSet[flen];
        final PrefixedName[] tokenNames = new PrefixedName[flen];
        for (int i = 0; i < flen; ++i) {
            followPos[i] = new BitSet(flen);
            tokenNames[i] = tokens.get(i).getName();
        }
        dummyRoot.calcFollowPos(followPos);
        final BitSet initial = new BitSet(flen);
        dummyRoot.addFirstPos(initial);
        final DFAState firstState = new DFAState(0, initial);
        final ArrayList<DFAState> stateList = new ArrayList<DFAState>();
        stateList.add(firstState);
        final HashMap<BitSet, DFAState> stateMap = new HashMap<BitSet, DFAState>();
        stateMap.put(initial, firstState);
        int j = 0;
        while (j < stateList.size()) {
            final DFAState curr = stateList.get(j++);
            curr.calcNext(tokenNames, followPos, stateList, stateMap);
        }
        return firstState;
    }
    
    public boolean isAcceptingState() {
        return this.mAccepting;
    }
    
    public int getIndex() {
        return this.mIndex;
    }
    
    public DFAState findNext(final PrefixedName elemName) {
        return this.mNext.get(elemName);
    }
    
    public TreeSet<PrefixedName> getNextNames() {
        final TreeSet<PrefixedName> names = new TreeSet<PrefixedName>();
        for (final PrefixedName n : this.mNext.keySet()) {
            names.add(n);
        }
        return names;
    }
    
    public void calcNext(final PrefixedName[] tokenNames, final BitSet[] tokenFPs, final List<DFAState> stateList, final Map<BitSet, DFAState> stateMap) {
        int first = -1;
        final BitSet tokenSet = (BitSet)this.mTokenSet.clone();
        this.mTokenSet = null;
        while ((first = tokenSet.nextSetBit(first + 1)) >= 0) {
            final PrefixedName tokenName = tokenNames[first];
            if (tokenName == null) {
                continue;
            }
            final BitSet nextGroup = (BitSet)tokenFPs[first].clone();
            int second = first;
            while ((second = tokenSet.nextSetBit(second + 1)) > 0) {
                if (tokenNames[second] == tokenName) {
                    tokenSet.clear(second);
                    nextGroup.or(tokenFPs[second]);
                }
            }
            DFAState next = stateMap.get(nextGroup);
            if (next == null) {
                next = new DFAState(stateList.size(), nextGroup);
                stateList.add(next);
                stateMap.put(nextGroup, next);
            }
            this.mNext.put(tokenName, next);
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("State #" + this.mIndex + ":\n");
        sb.append("  Accepting: " + this.mAccepting);
        sb.append("\n  Next states:\n");
        for (final Map.Entry<PrefixedName, DFAState> en : this.mNext.entrySet()) {
            sb.append(en.getKey());
            sb.append(" -> ");
            final DFAState next = en.getValue();
            sb.append(next.getIndex());
            sb.append("\n");
        }
        return sb.toString();
    }
}
