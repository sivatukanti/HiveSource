// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.validation;

import org.datanucleus.state.ObjectProvider;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.FieldPersistenceModifier;
import java.lang.annotation.ElementType;
import javax.validation.Path;
import org.datanucleus.ExecutionContext;
import javax.validation.TraversableResolver;

class PersistenceTraversalResolver implements TraversableResolver
{
    ExecutionContext ec;
    
    PersistenceTraversalResolver(final ExecutionContext ec) {
        this.ec = ec;
    }
    
    public boolean isCascadable(final Object traversableObject, final Path.Node traversableProperty, final Class<?> rootBeanType, final Path pathToTraversableObject, final ElementType elementType) {
        return false;
    }
    
    public boolean isReachable(final Object traversableObject, final Path.Node traversableProperty, final Class<?> rootBeanType, final Path pathToTraversableObject, final ElementType elementType) {
        final AbstractClassMetaData acmd = this.ec.getMetaDataManager().getMetaDataForClass(traversableObject.getClass(), this.ec.getClassLoaderResolver());
        if (acmd == null) {
            return false;
        }
        final AbstractMemberMetaData mmd = acmd.getMetaDataForMember(traversableProperty.getName());
        if (mmd.getPersistenceModifier() == FieldPersistenceModifier.NONE) {
            return true;
        }
        final ObjectProvider op = this.ec.findObjectProvider(traversableObject);
        return op.isFieldLoaded(mmd.getAbsoluteFieldNumber());
    }
}
