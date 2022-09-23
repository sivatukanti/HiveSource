// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.dtd;

import com.ctc.wstx.util.PrefixedName;
import java.util.Collection;

public class SeqContentSpec extends ContentSpec
{
    final boolean mNsAware;
    final ContentSpec[] mContentSpecs;
    
    public SeqContentSpec(final boolean nsAware, final char arity, final ContentSpec[] subSpecs) {
        super(arity);
        this.mNsAware = nsAware;
        this.mContentSpecs = subSpecs;
    }
    
    public static SeqContentSpec construct(final boolean nsAware, final char arity, final Collection<ContentSpec> subSpecs) {
        final ContentSpec[] specs = new ContentSpec[subSpecs.size()];
        subSpecs.toArray(specs);
        return new SeqContentSpec(nsAware, arity, specs);
    }
    
    @Override
    public StructValidator getSimpleValidator() {
        ContentSpec[] specs;
        int i;
        int len;
        for (specs = this.mContentSpecs, i = 0, len = specs.length; i < len && specs[i].isLeaf(); ++i) {}
        if (i == len) {
            final PrefixedName[] set = new PrefixedName[len];
            for (i = 0; i < len; ++i) {
                final TokenContentSpec ss = (TokenContentSpec)specs[i];
                set[i] = ss.getName();
            }
            return new Validator(this.mArity, set);
        }
        return null;
    }
    
    @Override
    public ModelNode rewrite() {
        final ModelNode model = this.rewrite(this.mContentSpecs, 0, this.mContentSpecs.length);
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
    
    private ModelNode rewrite(final ContentSpec[] specs, final int first, final int last) {
        final int count = last - first;
        if (count > 3) {
            final int mid = last + first + 1 >> 1;
            return new ConcatModel(this.rewrite(specs, first, mid), this.rewrite(specs, mid, last));
        }
        ConcatModel model = new ConcatModel(specs[first].rewrite(), specs[first + 1].rewrite());
        if (count == 3) {
            model = new ConcatModel(model, specs[first + 2].rewrite());
        }
        return model;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append('(');
        for (int i = 0; i < this.mContentSpecs.length; ++i) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(this.mContentSpecs[i].toString());
        }
        sb.append(')');
        if (this.mArity != ' ') {
            sb.append(this.mArity);
        }
        return sb.toString();
    }
    
    static final class Validator extends StructValidator
    {
        final char mArity;
        final PrefixedName[] mNames;
        int mRounds;
        int mStep;
        
        public Validator(final char arity, final PrefixedName[] names) {
            this.mRounds = 0;
            this.mStep = 0;
            this.mArity = arity;
            this.mNames = names;
        }
        
        @Override
        public StructValidator newInstance() {
            return new Validator(this.mArity, this.mNames);
        }
        
        @Override
        public String tryToValidate(final PrefixedName elemName) {
            if (this.mStep == 0 && this.mRounds == 1 && (this.mArity == '?' || this.mArity == ' ')) {
                return "was not expecting any more elements in the sequence (" + concatNames(this.mNames) + ")";
            }
            final PrefixedName next = this.mNames[this.mStep];
            if (!elemName.equals(next)) {
                return this.expElem(this.mStep);
            }
            if (++this.mStep == this.mNames.length) {
                ++this.mRounds;
                this.mStep = 0;
            }
            return null;
        }
        
        @Override
        public String fullyValid() {
            if (this.mStep != 0) {
                return this.expElem(this.mStep) + "; got end element";
            }
            switch (this.mArity) {
                case '*':
                case '?': {
                    return null;
                }
                case ' ':
                case '+': {
                    if (this.mRounds > 0) {
                        return null;
                    }
                    return "Expected sequence (" + concatNames(this.mNames) + "); got end element";
                }
                default: {
                    throw new IllegalStateException("Internal error");
                }
            }
        }
        
        private String expElem(final int step) {
            return "expected element <" + this.mNames[step] + "> in sequence (" + concatNames(this.mNames) + ")";
        }
        
        static final String concatNames(final PrefixedName[] names) {
            final StringBuilder sb = new StringBuilder();
            for (int i = 0, len = names.length; i < len; ++i) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(names[i].toString());
            }
            return sb.toString();
        }
    }
}
