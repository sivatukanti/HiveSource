// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.dtd;

import com.ctc.wstx.cfg.ErrorConsts;
import com.ctc.wstx.util.PrefixedName;

public class TokenContentSpec extends ContentSpec
{
    static final TokenContentSpec sDummy;
    final PrefixedName mElemName;
    
    public TokenContentSpec(final char arity, final PrefixedName elemName) {
        super(arity);
        this.mElemName = elemName;
    }
    
    public static TokenContentSpec construct(final char arity, final PrefixedName elemName) {
        return new TokenContentSpec(arity, elemName);
    }
    
    public static TokenContentSpec getDummySpec() {
        return TokenContentSpec.sDummy;
    }
    
    @Override
    public boolean isLeaf() {
        return this.mArity == ' ';
    }
    
    public PrefixedName getName() {
        return this.mElemName;
    }
    
    @Override
    public StructValidator getSimpleValidator() {
        return new Validator(this.mArity, this.mElemName);
    }
    
    @Override
    public ModelNode rewrite() {
        final TokenModel model = new TokenModel(this.mElemName);
        if (this.mArity == '*') {
            return new StarModel(model);
        }
        if (this.mArity == '?') {
            return new OptionalModel(model);
        }
        if (this.mArity == '+') {
            return new ConcatModel(model, new StarModel(new TokenModel(this.mElemName)));
        }
        return model;
    }
    
    @Override
    public String toString() {
        return (this.mArity == ' ') ? this.mElemName.toString() : (this.mElemName.toString() + this.mArity);
    }
    
    static {
        sDummy = new TokenContentSpec(' ', new PrefixedName("*", "*"));
    }
    
    static final class Validator extends StructValidator
    {
        final char mArity;
        final PrefixedName mElemName;
        int mCount;
        
        public Validator(final char arity, final PrefixedName elemName) {
            this.mCount = 0;
            this.mArity = arity;
            this.mElemName = elemName;
        }
        
        @Override
        public StructValidator newInstance() {
            return (this.mArity == '*') ? this : new Validator(this.mArity, this.mElemName);
        }
        
        @Override
        public String tryToValidate(final PrefixedName elemName) {
            if (!elemName.equals(this.mElemName)) {
                return "Expected element <" + this.mElemName + ">";
            }
            if (++this.mCount > 1 && (this.mArity == '?' || this.mArity == ' ')) {
                return "More than one instance of element <" + this.mElemName + ">";
            }
            return null;
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
                    return "Expected " + ((this.mArity == '+') ? "at least one" : "") + " element <" + this.mElemName + ">";
                }
                default: {
                    throw new IllegalStateException(ErrorConsts.ERR_INTERNAL);
                }
            }
        }
    }
}
