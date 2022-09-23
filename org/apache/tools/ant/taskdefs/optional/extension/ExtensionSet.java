// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.extension;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Stack;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.BuildException;
import java.util.List;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import java.util.ArrayList;
import org.apache.tools.ant.types.DataType;

public class ExtensionSet extends DataType
{
    private final ArrayList extensions;
    private final ArrayList extensionsFilesets;
    
    public ExtensionSet() {
        this.extensions = new ArrayList();
        this.extensionsFilesets = new ArrayList();
    }
    
    public void addExtension(final ExtensionAdapter extensionAdapter) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.setChecked(false);
        this.extensions.add(extensionAdapter);
    }
    
    public void addLibfileset(final LibFileSet fileSet) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.setChecked(false);
        this.extensionsFilesets.add(fileSet);
    }
    
    public void addFileset(final FileSet fileSet) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.setChecked(false);
        this.extensionsFilesets.add(fileSet);
    }
    
    public Extension[] toExtensions(final Project proj) throws BuildException {
        if (this.isReference()) {
            return ((ExtensionSet)this.getCheckedRef()).toExtensions(proj);
        }
        this.dieOnCircularReference();
        final ArrayList extensionsList = ExtensionUtil.toExtensions(this.extensions);
        ExtensionUtil.extractExtensions(proj, extensionsList, this.extensionsFilesets);
        return extensionsList.toArray(new Extension[extensionsList.size()]);
    }
    
    @Override
    public void setRefid(final Reference reference) throws BuildException {
        if (!this.extensions.isEmpty() || !this.extensionsFilesets.isEmpty()) {
            throw this.tooManyAttributes();
        }
        super.setRefid(reference);
    }
    
    @Override
    protected synchronized void dieOnCircularReference(final Stack stk, final Project p) throws BuildException {
        if (this.isChecked()) {
            return;
        }
        if (this.isReference()) {
            super.dieOnCircularReference(stk, p);
        }
        else {
            Iterator i = this.extensions.iterator();
            while (i.hasNext()) {
                DataType.pushAndInvokeCircularReferenceCheck(i.next(), stk, p);
            }
            i = this.extensionsFilesets.iterator();
            while (i.hasNext()) {
                DataType.pushAndInvokeCircularReferenceCheck(i.next(), stk, p);
            }
            this.setChecked(true);
        }
    }
    
    @Override
    public String toString() {
        return "ExtensionSet" + Arrays.asList(this.toExtensions(this.getProject()));
    }
}
