// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.tree;

import java.util.List;

public interface ReferenceNodeHandler extends NodeHandler<ImmutableNode>
{
    Object getReference(final ImmutableNode p0);
    
    List<Object> removedReferences();
}
