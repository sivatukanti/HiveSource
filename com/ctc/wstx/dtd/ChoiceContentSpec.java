// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.dtd;

import com.ctc.wstx.util.ExceptionUtil;
import com.ctc.wstx.util.PrefixedName;
import java.util.Collection;

public class ChoiceContentSpec extends ContentSpec
{
    final boolean mNsAware;
    final boolean mHasMixed;
    final ContentSpec[] mContentSpecs;
    
    private ChoiceContentSpec(final boolean nsAware, final char arity, final boolean mixed, final ContentSpec[] specs) {
        super(arity);
        this.mNsAware = nsAware;
        this.mHasMixed = mixed;
        this.mContentSpecs = specs;
    }
    
    private ChoiceContentSpec(final boolean nsAware, final char arity, final boolean mixed, final Collection<ContentSpec> specs) {
        super(arity);
        this.mNsAware = nsAware;
        this.mHasMixed = mixed;
        specs.toArray(this.mContentSpecs = new ContentSpec[specs.size()]);
    }
    
    public static ChoiceContentSpec constructChoice(final boolean nsAware, final char arity, final Collection<ContentSpec> specs) {
        return new ChoiceContentSpec(nsAware, arity, false, specs);
    }
    
    public static ChoiceContentSpec constructMixed(final boolean nsAware, final Collection<ContentSpec> specs) {
        return new ChoiceContentSpec(nsAware, '*', true, specs);
    }
    
    @Override
    public StructValidator getSimpleValidator() {
        final ContentSpec[] specs = this.mContentSpecs;
        final int len = specs.length;
        int i;
        if (this.mHasMixed) {
            i = len;
        }
        else {
            for (i = 0; i < len; ++i) {
                if (!specs[i].isLeaf()) {
                    break;
                }
            }
        }
        if (i == len) {
            final PrefixedNameSet keyset = namesetFromSpecs(this.mNsAware, specs);
            return new Validator(this.mArity, keyset);
        }
        return null;
    }
    
    @Override
    public ModelNode rewrite() {
        final ContentSpec[] specs = this.mContentSpecs;
        final int len = specs.length;
        final ModelNode[] models = new ModelNode[len];
        for (int i = 0; i < len; ++i) {
            models[i] = specs[i].rewrite();
        }
        final ChoiceModel model = new ChoiceModel(models);
        if (this.mArity == '*') {
            return new StarModel(model);
        }
        if (this.mArity == '?') {
            return new OptionalModel(model);
        }
        if (this.mArity == '+') {
            return new ConcatModel(model, new StarModel(model.cloneModel()));
        }
        return model;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        if (this.mHasMixed) {
            sb.append("(#PCDATA | ");
        }
        else {
            sb.append('(');
        }
        for (int i = 0; i < this.mContentSpecs.length; ++i) {
            if (i > 0) {
                sb.append(" | ");
            }
            sb.append(this.mContentSpecs[i].toString());
        }
        sb.append(')');
        if (this.mArity != ' ') {
            sb.append(this.mArity);
        }
        return sb.toString();
    }
    
    protected static PrefixedNameSet namesetFromSpecs(final boolean nsAware, final ContentSpec[] specs) {
        final int len = specs.length;
        final PrefixedName[] nameArray = new PrefixedName[len];
        for (int i = 0; i < len; ++i) {
            nameArray[i] = ((TokenContentSpec)specs[i]).getName();
        }
        if (len < 5) {
            return new SmallPrefixedNameSet(nsAware, nameArray);
        }
        return new LargePrefixedNameSet(nsAware, nameArray);
    }
    
    static final class Validator extends StructValidator
    {
        final char mArity;
        final PrefixedNameSet mNames;
        int mCount;
        
        public Validator(final char arity, final PrefixedNameSet names) {
            this.mCount = 0;
            this.mArity = arity;
            this.mNames = names;
        }
        
        @Override
        public StructValidator newInstance() {
            return (this.mArity == '*') ? this : new Validator(this.mArity, this.mNames);
        }
        
        @Override
        public String tryToValidate(final PrefixedName elemName) {
            if (!this.mNames.contains(elemName)) {
                if (this.mNames.hasMultiple()) {
                    return "Expected one of (" + this.mNames.toString(" | ") + ")";
                }
                return "Expected <" + this.mNames.toString("") + ">";
            }
            else {
                if (++this.mCount <= 1 || (this.mArity != '?' && this.mArity != ' ')) {
                    return null;
                }
                if (this.mNames.hasMultiple()) {
                    return "Expected $END (already had one of [" + this.mNames.toString(" | ") + "]";
                }
                return "Expected $END (already had one <" + this.mNames.toString("") + ">]";
            }
        }
        
        @Override
        public String fullyValid() {
            switch (this.mArity) {
                case '*':
                case '?': {
                    return null;
                }
                case ' ':
                case '+': {
                    if (this.mCount > 0) {
                        return null;
                    }
                    return "Expected " + ((this.mArity == '+') ? "at least" : "") + " one of elements (" + this.mNames + ")";
                }
                default: {
                    ExceptionUtil.throwGenericInternal();
                    return null;
                }
            }
        }
    }
}
