// 
// Decompiled by Procyon v0.5.36
// 

package parquet.filter2.recordlevel;

import parquet.Preconditions;
import parquet.io.api.Binary;

public interface IncrementallyUpdatedFilterPredicate
{
    boolean accept(final Visitor p0);
    
    public abstract static class ValueInspector implements IncrementallyUpdatedFilterPredicate
    {
        private boolean result;
        private boolean isKnown;
        
        ValueInspector() {
            this.result = false;
            this.isKnown = false;
        }
        
        public void updateNull() {
            throw new UnsupportedOperationException();
        }
        
        public void update(final int value) {
            throw new UnsupportedOperationException();
        }
        
        public void update(final long value) {
            throw new UnsupportedOperationException();
        }
        
        public void update(final double value) {
            throw new UnsupportedOperationException();
        }
        
        public void update(final float value) {
            throw new UnsupportedOperationException();
        }
        
        public void update(final boolean value) {
            throw new UnsupportedOperationException();
        }
        
        public void update(final Binary value) {
            throw new UnsupportedOperationException();
        }
        
        public final void reset() {
            this.isKnown = false;
            this.result = false;
        }
        
        protected final void setResult(final boolean result) {
            if (this.isKnown) {
                throw new IllegalStateException("setResult() called on a ValueInspector whose result is already known! Did you forget to call reset()?");
            }
            this.result = result;
            this.isKnown = true;
        }
        
        public final boolean getResult() {
            if (!this.isKnown) {
                throw new IllegalStateException("getResult() called on a ValueInspector whose result is not yet known!");
            }
            return this.result;
        }
        
        public final boolean isKnown() {
            return this.isKnown;
        }
        
        @Override
        public boolean accept(final Visitor visitor) {
            return visitor.visit(this);
        }
    }
    
    public abstract static class BinaryLogical implements IncrementallyUpdatedFilterPredicate
    {
        private final IncrementallyUpdatedFilterPredicate left;
        private final IncrementallyUpdatedFilterPredicate right;
        
        BinaryLogical(final IncrementallyUpdatedFilterPredicate left, final IncrementallyUpdatedFilterPredicate right) {
            this.left = Preconditions.checkNotNull(left, "left");
            this.right = Preconditions.checkNotNull(right, "right");
        }
        
        public final IncrementallyUpdatedFilterPredicate getLeft() {
            return this.left;
        }
        
        public final IncrementallyUpdatedFilterPredicate getRight() {
            return this.right;
        }
    }
    
    public static final class Or extends BinaryLogical
    {
        Or(final IncrementallyUpdatedFilterPredicate left, final IncrementallyUpdatedFilterPredicate right) {
            super(left, right);
        }
        
        @Override
        public boolean accept(final Visitor visitor) {
            return visitor.visit(this);
        }
    }
    
    public static final class And extends BinaryLogical
    {
        And(final IncrementallyUpdatedFilterPredicate left, final IncrementallyUpdatedFilterPredicate right) {
            super(left, right);
        }
        
        @Override
        public boolean accept(final Visitor visitor) {
            return visitor.visit(this);
        }
    }
    
    public interface Visitor
    {
        boolean visit(final ValueInspector p0);
        
        boolean visit(final And p0);
        
        boolean visit(final Or p0);
    }
}
