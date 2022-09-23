// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

import java.util.Set;
import java.util.List;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.datanucleus.api.ApiAdapter;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.exceptions.ClassNotResolvedException;
import java.util.Collection;
import org.datanucleus.util.StringUtils;
import org.datanucleus.ClassLoaderResolver;

public class CollectionMetaData extends ContainerMetaData
{
    protected ContainerComponent element;
    
    public CollectionMetaData(final CollectionMetaData collmd) {
        super(collmd);
        this.element = new ContainerComponent();
        this.element.embedded = collmd.element.embedded;
        this.element.serialized = collmd.element.serialized;
        this.element.dependent = collmd.element.dependent;
        this.element.type = collmd.element.type;
        this.element.classMetaData = collmd.element.classMetaData;
    }
    
    public CollectionMetaData() {
        this.element = new ContainerComponent();
    }
    
    @Override
    public void populate(final ClassLoaderResolver clr, final ClassLoader primary, final MetaDataManager mmgr) {
        final AbstractMemberMetaData mmd = (AbstractMemberMetaData)this.parent;
        if (!StringUtils.isWhitespace(this.element.type) && this.element.type.indexOf(44) > 0) {
            throw new InvalidMemberMetaDataException(CollectionMetaData.LOCALISER, "044131", mmd.getClassName(), mmd.getName());
        }
        this.element.populate(((AbstractMemberMetaData)this.parent).getAbstractClassMetaData().getPackageName(), clr, primary, mmgr);
        final Class field_type = this.getMemberMetaData().getType();
        if (!Collection.class.isAssignableFrom(field_type)) {
            throw new InvalidMemberMetaDataException(CollectionMetaData.LOCALISER, "044132", mmd.getClassName(), mmd.getName());
        }
        if (this.element.type == null) {
            throw new InvalidMemberMetaDataException(CollectionMetaData.LOCALISER, "044133", mmd.getClassName(), mmd.getName());
        }
        Class elementTypeClass = null;
        try {
            elementTypeClass = clr.classForName(this.element.type, primary);
        }
        catch (ClassNotResolvedException cnre) {
            throw new InvalidMemberMetaDataException(CollectionMetaData.LOCALISER, "044134", this.getMemberMetaData().getClassName(), this.getFieldName(), this.element.type);
        }
        if (!elementTypeClass.getName().equals(this.element.type)) {
            NucleusLogger.METADATA.info(CollectionMetaData.LOCALISER.msg("044135", this.getFieldName(), this.getMemberMetaData().getClassName(false), this.element.type, elementTypeClass.getName()));
            this.element.type = elementTypeClass.getName();
        }
        final ApiAdapter api = mmgr.getApiAdapter();
        if (this.element.embedded == null) {
            if (mmgr.getNucleusContext().getTypeManager().isDefaultEmbeddedType(elementTypeClass)) {
                this.element.embedded = Boolean.TRUE;
            }
            else if (api.isPersistable(elementTypeClass) || Object.class.isAssignableFrom(elementTypeClass) || elementTypeClass.isInterface()) {
                this.element.embedded = Boolean.FALSE;
            }
            else {
                this.element.embedded = Boolean.TRUE;
            }
        }
        if (Boolean.FALSE.equals(this.element.embedded) && !api.isPersistable(elementTypeClass) && !elementTypeClass.isInterface() && elementTypeClass != Object.class) {
            this.element.embedded = Boolean.TRUE;
        }
        final ElementMetaData elemmd = ((AbstractMemberMetaData)this.parent).getElementMetaData();
        if (elemmd != null && elemmd.getEmbeddedMetaData() != null) {
            this.element.embedded = Boolean.TRUE;
        }
        if (Boolean.TRUE.equals(this.element.dependent) && !api.isPersistable(elementTypeClass) && !elementTypeClass.isInterface() && elementTypeClass != Object.class) {
            this.element.dependent = Boolean.FALSE;
        }
        this.element.classMetaData = mmgr.getMetaDataForClassInternal(elementTypeClass, clr);
        super.populate(clr, primary, mmgr);
        this.setPopulated();
    }
    
    public String getElementType() {
        return this.element.type;
    }
    
    public boolean elementIsPersistent() {
        return this.element.classMetaData != null;
    }
    
    public AbstractClassMetaData getElementClassMetaData(final ClassLoaderResolver clr, final MetaDataManager mmgr) {
        if (this.element.classMetaData != null && !this.element.classMetaData.isInitialised()) {
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
                @Override
                public Object run() {
                    CollectionMetaData.this.element.classMetaData.initialise(clr, mmgr);
                    return null;
                }
            });
        }
        return this.element.classMetaData;
    }
    
    public boolean isEmbeddedElement() {
        return this.element.embedded != null && this.element.embedded;
    }
    
    public boolean isDependentElement() {
        return this.element.dependent != null && this.element.classMetaData != null && this.element.dependent;
    }
    
    public boolean isSerializedElement() {
        return this.element.serialized != null && this.element.serialized;
    }
    
    public CollectionMetaData setElementType(final String type) {
        this.element.setType(type);
        return this;
    }
    
    public CollectionMetaData setEmbeddedElement(final boolean embedded) {
        this.element.setEmbedded(embedded);
        return this;
    }
    
    public CollectionMetaData setSerializedElement(final boolean serialized) {
        this.element.setSerialized(serialized);
        return this;
    }
    
    public CollectionMetaData setDependentElement(final boolean dependent) {
        this.element.setDependent(dependent);
        return this;
    }
    
    void getReferencedClassMetaData(final List orderedCMDs, final Set referencedCMDs, final ClassLoaderResolver clr, final MetaDataManager mmgr) {
        final AbstractClassMetaData element_cmd = mmgr.getMetaDataForClass(this.element.type, clr);
        if (element_cmd != null) {
            element_cmd.getReferencedClassMetaData(orderedCMDs, referencedCMDs, clr, mmgr);
        }
    }
    
    @Override
    public String toString(final String prefix, final String indent) {
        final StringBuffer sb = new StringBuffer();
        sb.append(prefix).append("<collection element-type=\"").append(this.element.type).append("\"");
        if (this.element.embedded != null) {
            sb.append(" embedded-element=\"").append(this.element.embedded).append("\"");
        }
        if (this.element.dependent != null) {
            sb.append(" dependent-element=\"").append(this.element.dependent).append("\"");
        }
        if (this.element.serialized != null) {
            sb.append(" serialized-element=\"").append(this.element.serialized).append("\"");
        }
        sb.append(">\n");
        sb.append(super.toString(prefix + indent, indent));
        sb.append(prefix).append("</collection>\n");
        return sb.toString();
    }
}
