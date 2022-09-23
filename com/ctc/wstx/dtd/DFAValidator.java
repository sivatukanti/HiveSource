// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.dtd;

import java.util.TreeSet;
import java.util.Collection;
import com.ctc.wstx.util.StringUtil;
import com.ctc.wstx.util.PrefixedName;

public final class DFAValidator extends StructValidator
{
    DFAState mState;
    
    public DFAValidator(final DFAState initialState) {
        this.mState = initialState;
    }
    
    @Override
    public StructValidator newInstance() {
        return new DFAValidator(this.mState);
    }
    
    @Override
    public String tryToValidate(final PrefixedName elemName) {
        final DFAState next = this.mState.findNext(elemName);
        if (next != null) {
            this.mState = next;
            return null;
        }
        final TreeSet<PrefixedName> names = this.mState.getNextNames();
        if (names.size() == 0) {
            return "Expected $END";
        }
        if (this.mState.isAcceptingState()) {
            return "Expected <" + StringUtil.concatEntries(names, ">, <", null) + "> or $END";
        }
        return "Expected <" + StringUtil.concatEntries(names, ">, <", "> or <") + ">";
    }
    
    @Override
    public String fullyValid() {
        if (this.mState.isAcceptingState()) {
            return null;
        }
        final TreeSet<PrefixedName> names = this.mState.getNextNames();
        return "Expected <" + StringUtil.concatEntries(names, ">, <", "> or <") + ">";
    }
}
