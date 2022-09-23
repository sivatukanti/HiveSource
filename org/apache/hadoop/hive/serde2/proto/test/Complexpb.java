// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.proto.test;

import java.util.Collections;
import java.util.List;

public final class Complexpb
{
    private Complexpb() {
    }
    
    public static final class IntString
    {
        public static final int MYINT_FIELD_NUMBER = 1;
        private final boolean hasMyint;
        private int myint_;
        public static final int MYSTRING_FIELD_NUMBER = 2;
        private final boolean hasMyString;
        private String myString_;
        public static final int UNDERSCORE_INT_FIELD_NUMBER = 3;
        private final boolean hasUnderscoreInt;
        private int underscoreInt_;
        
        public IntString(final int myInt, final String myString, final int underscoreInt) {
            this.myint_ = 0;
            this.myString_ = "";
            this.underscoreInt_ = 0;
            this.myint_ = myInt;
            this.hasMyint = true;
            this.myString_ = myString;
            this.hasMyString = true;
            this.underscoreInt_ = underscoreInt;
            this.hasUnderscoreInt = true;
        }
        
        public boolean hasMyint() {
            return this.hasMyint;
        }
        
        public int getMyint() {
            return this.myint_;
        }
        
        public boolean hasMyString() {
            return this.hasMyString;
        }
        
        public String getMyString() {
            return this.myString_;
        }
        
        public boolean hasUnderscoreInt() {
            return this.hasUnderscoreInt;
        }
        
        public int getUnderscoreInt() {
            return this.underscoreInt_;
        }
        
        public final boolean isInitialized() {
            return true;
        }
    }
    
    public static final class Complex
    {
        public static final int AINT_FIELD_NUMBER = 1;
        private final boolean hasAint;
        private int aint_;
        public static final int ASTRING_FIELD_NUMBER = 2;
        private final boolean hasAString;
        private String aString_;
        public static final int LINT_FIELD_NUMBER = 3;
        private List<Integer> lint_;
        public static final int LSTRING_FIELD_NUMBER = 4;
        private List<String> lString_;
        public static final int LINTSTRING_FIELD_NUMBER = 5;
        private List<IntString> lintString_;
        
        public Complex(final int aint, final String aString, final List<Integer> lint, final List<String> lString, final List<IntString> lintString) {
            this.aint_ = 0;
            this.aString_ = "";
            this.lint_ = Collections.emptyList();
            this.lString_ = Collections.emptyList();
            this.lintString_ = Collections.emptyList();
            this.aint_ = aint;
            this.hasAint = true;
            this.aString_ = aString;
            this.hasAString = true;
            this.lint_ = lint;
            this.lString_ = lString;
            this.lintString_ = lintString;
        }
        
        public boolean hasAint() {
            return this.hasAint;
        }
        
        public int getAint() {
            return this.aint_;
        }
        
        public boolean hasAString() {
            return this.hasAString;
        }
        
        public String getAString() {
            return this.aString_;
        }
        
        public List<Integer> getLintList() {
            return this.lint_;
        }
        
        public int getLintCount() {
            return this.lint_.size();
        }
        
        public int getLint(final int index) {
            return this.lint_.get(index);
        }
        
        public List<String> getLStringList() {
            return this.lString_;
        }
        
        public int getLStringCount() {
            return this.lString_.size();
        }
        
        public String getLString(final int index) {
            return this.lString_.get(index);
        }
        
        public List<IntString> getLintStringList() {
            return this.lintString_;
        }
        
        public int getLintStringCount() {
            return this.lintString_.size();
        }
        
        public IntString getLintString(final int index) {
            return this.lintString_.get(index);
        }
        
        private void initFields() {
        }
        
        public final boolean isInitialized() {
            return true;
        }
    }
}
