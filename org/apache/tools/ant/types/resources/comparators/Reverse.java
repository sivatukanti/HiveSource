// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.resources.comparators;

import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.Project;
import java.util.Stack;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.BuildException;

public class Reverse extends ResourceComparator
{
    private static final String ONE_NESTED = "You must not nest more than one ResourceComparator for reversal.";
    private ResourceComparator nested;
    
    public Reverse() {
    }
    
    public Reverse(final ResourceComparator c) {
        this.add(c);
    }
    
    public void add(final ResourceComparator c) {
        if (this.nested != null) {
            throw new BuildException("You must not nest more than one ResourceComparator for reversal.");
        }
        this.nested = c;
        this.setChecked(false);
    }
    
    @Override
    protected int resourceCompare(final Resource foo, final Resource bar) {
        return -1 * ((this.nested == null) ? foo.compareTo(bar) : this.nested.compare(foo, bar));
    }
    
    @Override
    protected void dieOnCircularReference(final Stack<Object> stk, final Project p) throws BuildException {
        if (this.isChecked()) {
            return;
        }
        if (this.isReference()) {
            super.dieOnCircularReference(stk, p);
        }
        else {
            if (this.nested instanceof DataType) {
                DataType.pushAndInvokeCircularReferenceCheck(this.nested, stk, p);
            }
            this.setChecked(true);
        }
    }
}
