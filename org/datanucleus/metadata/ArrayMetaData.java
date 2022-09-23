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
import org.datanucleus.ClassNameConstants;
import org.datanucleus.util.StringUtils;
import org.datanucleus.ClassLoaderResolver;

public class ArrayMetaData extends ContainerMetaData
{
    protected ContainerComponent element;
    protected boolean mayContainPersistableElements;
    
    public ArrayMetaData(final ArrayMetaData arrmd) {
        super(arrmd);
        this.element = new ContainerComponent();
        this.element.embedded = arrmd.element.embedded;
        this.element.serialized = arrmd.element.serialized;
        this.element.dependent = arrmd.element.dependent;
        this.element.type = arrmd.element.type;
        this.element.classMetaData = arrmd.element.classMetaData;
    }
    
    public ArrayMetaData() {
        this.element = new ContainerComponent();
    }
    
    @Override
    public void populate(final ClassLoaderResolver clr, final ClassLoader primary, final MetaDataManager mmgr) {
        final AbstractMemberMetaData mmd = (AbstractMemberMetaData)this.parent;
        if (!StringUtils.isWhitespace(this.element.type) && this.element.type.indexOf(44) > 0) {
            throw new InvalidMemberMetaDataException(ArrayMetaData.LOCALISER, "044140", mmd.getClassName(), mmd.getName());
        }
        final ApiAdapter api = mmgr.getApiAdapter();
        this.element.populate(((AbstractMemberMetaData)this.parent).getAbstractClassMetaData().getPackageName(), clr, primary, mmgr);
        final Class field_type = this.getMemberMetaData().getType();
        if (!field_type.isArray()) {
            throw new InvalidMemberMetaDataException(ArrayMetaData.LOCALISER, "044141", mmd.getClassName(), this.getFieldName());
        }
        if (this.element.embedded == null) {
            final Class component_type = field_type.getComponentType();
            if (mmgr.getNucleusContext().getTypeManager().isDefaultEmbeddedType(component_type)) {
                this.element.embedded = Boolean.TRUE;
            }
            else if (api.isPersistable(component_type) || Object.class.isAssignableFrom(component_type) || component_type.isInterface()) {
                this.element.embedded = Boolean.FALSE;
            }
            else {
                this.element.embedded = Boolean.TRUE;
            }
        }
        if (Boolean.FALSE.equals(this.element.embedded)) {
            final Class component_type = field_type.getComponentType();
            if (!api.isPersistable(component_type) && !component_type.isInterface() && component_type != Object.class) {
                this.element.embedded = Boolean.TRUE;
            }
        }
        if (!mmgr.isEnhancing() && !this.getMemberMetaData().isSerialized() && this.getMemberMetaData().getJoinMetaData() == null && !api.isPersistable(this.getMemberMetaData().getType().getComponentType()) && mmgr.supportsORM()) {
            final String arrayComponentType = this.getMemberMetaData().getType().getComponentType().getName();
            if (!arrayComponentType.equals(ClassNameConstants.BOOLEAN) && !arrayComponentType.equals(ClassNameConstants.BYTE) && !arrayComponentType.equals(ClassNameConstants.CHAR) && !arrayComponentType.equals(ClassNameConstants.DOUBLE) && !arrayComponentType.equals(ClassNameConstants.FLOAT) && !arrayComponentType.equals(ClassNameConstants.INT) && !arrayComponentType.equals(ClassNameConstants.LONG) && !arrayComponentType.equals(ClassNameConstants.SHORT) && !arrayComponentType.equals(ClassNameConstants.JAVA_LANG_BOOLEAN) && !arrayComponentType.equals(ClassNameConstants.JAVA_LANG_BYTE) && !arrayComponentType.equals(ClassNameConstants.JAVA_LANG_CHARACTER) && !arrayComponentType.equals(ClassNameConstants.JAVA_LANG_DOUBLE) && !arrayComponentType.equals(ClassNameConstants.JAVA_LANG_FLOAT) && !arrayComponentType.equals(ClassNameConstants.JAVA_LANG_INTEGER) && !arrayComponentType.equals(ClassNameConstants.JAVA_LANG_LONG) && !arrayComponentType.equals(ClassNameConstants.JAVA_LANG_SHORT) && !arrayComponentType.equals(ClassNameConstants.JAVA_MATH_BIGDECIMAL) && !arrayComponentType.equals(ClassNameConstants.JAVA_MATH_BIGINTEGER)) {
                final String msg = ArrayMetaData.LOCALISER.msg("044142", mmd.getClassName(), this.getFieldName(), this.getMemberMetaData().getType().getComponentType().getName());
                NucleusLogger.METADATA.warn(msg);
            }
        }
        if (this.element.type != null) {
            final Class elementCls = clr.classForName(this.element.type, primary);
            if (api.isPersistable(elementCls)) {
                this.mayContainPersistableElements = true;
            }
            this.element.classMetaData = mmgr.getMetaDataForClassInternal(elementCls, clr);
        }
        else {
            this.element.type = field_type.getComponentType().getName();
            this.element.classMetaData = mmgr.getMetaDataForClassInternal(field_type.getComponentType(), clr);
        }
        if (this.element.classMetaData != null) {
            this.mayContainPersistableElements = true;
        }
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
                    ArrayMetaData.this.element.classMetaData.initialise(clr, mmgr);
                    return null;
                }
            });
        }
        return this.element.classMetaData;
    }
    
    public boolean mayContainPersistableElements() {
        return this.mayContainPersistableElements;
    }
    
    public boolean isEmbeddedElement() {
        return this.element.embedded != null && this.element.embedded;
    }
    
    public boolean isSerializedElement() {
        return this.element.serialized != null && this.element.serialized;
    }
    
    public boolean isDependentElement() {
        return this.element.dependent != null && this.element.dependent;
    }
    
    public ArrayMetaData setElementType(final String type) {
        if (StringUtils.isWhitespace(type)) {
            this.element.type = null;
        }
        else {
            this.element.setType(type);
        }
        return this;
    }
    
    public ArrayMetaData setEmbeddedElement(final boolean embedded) {
        this.element.setEmbedded(embedded);
        return this;
    }
    
    public ArrayMetaData setSerializedElement(final boolean serialized) {
        this.element.setSerialized(serialized);
        return this;
    }
    
    public ArrayMetaData setDependentElement(final boolean dependent) {
        this.element.setDependent(dependent);
        return this;
    }
    
    void getReferencedClassMetaData(final List orderedCMDs, final Set referencedCMDs, final ClassLoaderResolver clr, final MetaDataManager mmgr) {
        final AbstractClassMetaData element_cmd = mmgr.getMetaDataForClass(this.getMemberMetaData().getType().getComponentType(), clr);
        if (element_cmd != null) {
            element_cmd.getReferencedClassMetaData(orderedCMDs, referencedCMDs, clr, mmgr);
        }
    }
    
    @Override
    public String toString(final String prefix, final String indent) {
        final StringBuffer sb = new StringBuffer();
        sb.append(prefix).append("<array");
        if (this.element.type != null) {
            sb.append(" element-type=\"").append(this.element.type).append("\"");
        }
        if (this.element.embedded != null) {
            sb.append(" embedded-element=\"").append(this.element.embedded).append("\"");
        }
        if (this.element.serialized != null) {
            sb.append(" serialized-element=\"").append(this.element.serialized).append("\"");
        }
        if (this.element.dependent != null) {
            sb.append(" dependent-element=\"").append(this.element.dependent).append("\"");
        }
        if (this.getNoOfExtensions() > 0) {
            sb.append(">\n");
            sb.append(super.toString(prefix + indent, indent));
            sb.append(prefix).append("</array>\n");
        }
        else {
            sb.append(prefix).append("/>\n");
        }
        return sb.toString();
    }
}
