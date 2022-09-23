// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.resources.selectors;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import java.util.Stack;
import java.util.Iterator;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.comparators.ResourceComparator;
import org.apache.tools.ant.types.resources.Union;
import org.apache.tools.ant.types.Comparison;
import org.apache.tools.ant.types.Quantifier;
import org.apache.tools.ant.types.resources.comparators.DelegatedResourceComparator;
import org.apache.tools.ant.types.DataType;

public class Compare extends DataType implements ResourceSelector
{
    private static final String ONE_CONTROL_MESSAGE = " the <control> element should be specified exactly once.";
    private DelegatedResourceComparator comp;
    private Quantifier against;
    private Comparison when;
    private Union control;
    
    public Compare() {
        this.comp = new DelegatedResourceComparator();
        this.against = Quantifier.ALL;
        this.when = Comparison.EQUAL;
    }
    
    public synchronized void add(final ResourceComparator c) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.comp.add(c);
        this.setChecked(false);
    }
    
    public synchronized void setAgainst(final Quantifier against) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        this.against = against;
    }
    
    public synchronized void setWhen(final Comparison when) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        this.when = when;
    }
    
    public synchronized ResourceCollection createControl() {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        if (this.control != null) {
            throw this.oneControl();
        }
        this.control = new Union();
        this.setChecked(false);
        return this.control;
    }
    
    public synchronized boolean isSelected(final Resource r) {
        if (this.isReference()) {
            return ((ResourceSelector)this.getCheckedRef()).isSelected(r);
        }
        if (this.control == null) {
            throw this.oneControl();
        }
        this.dieOnCircularReference();
        int t = 0;
        int f = 0;
        for (final Resource res : this.control) {
            if (this.when.evaluate(this.comp.compare(r, res))) {
                ++t;
            }
            else {
                ++f;
            }
        }
        return this.against.evaluate(t, f);
    }
    
    @Override
    protected synchronized void dieOnCircularReference(final Stack<Object> stk, final Project p) throws BuildException {
        if (this.isChecked()) {
            return;
        }
        if (this.isReference()) {
            super.dieOnCircularReference(stk, p);
        }
        else {
            if (this.control != null) {
                DataType.pushAndInvokeCircularReferenceCheck(this.control, stk, p);
            }
            DataType.pushAndInvokeCircularReferenceCheck(this.comp, stk, p);
            this.setChecked(true);
        }
    }
    
    private BuildException oneControl() {
        return new BuildException(super.toString() + " the <control> element should be specified exactly once.");
    }
}
