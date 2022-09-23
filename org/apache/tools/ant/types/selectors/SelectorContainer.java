// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.selectors;

import org.apache.tools.ant.types.selectors.modifiedselector.ModifiedSelector;
import java.util.Enumeration;
import org.apache.tools.ant.Project;

public interface SelectorContainer
{
    boolean hasSelectors();
    
    int selectorCount();
    
    FileSelector[] getSelectors(final Project p0);
    
    Enumeration<FileSelector> selectorElements();
    
    void appendSelector(final FileSelector p0);
    
    void addSelector(final SelectSelector p0);
    
    void addAnd(final AndSelector p0);
    
    void addOr(final OrSelector p0);
    
    void addNot(final NotSelector p0);
    
    void addNone(final NoneSelector p0);
    
    void addMajority(final MajoritySelector p0);
    
    void addDate(final DateSelector p0);
    
    void addSize(final SizeSelector p0);
    
    void addFilename(final FilenameSelector p0);
    
    void addCustom(final ExtendSelector p0);
    
    void addContains(final ContainsSelector p0);
    
    void addPresent(final PresentSelector p0);
    
    void addDepth(final DepthSelector p0);
    
    void addDepend(final DependSelector p0);
    
    void addContainsRegexp(final ContainsRegexpSelector p0);
    
    void addType(final TypeSelector p0);
    
    void addDifferent(final DifferentSelector p0);
    
    void addModified(final ModifiedSelector p0);
    
    void add(final FileSelector p0);
}
