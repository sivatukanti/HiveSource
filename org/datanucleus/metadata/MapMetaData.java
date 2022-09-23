// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

import java.util.Set;
import java.util.List;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.datanucleus.api.ApiAdapter;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.exceptions.ClassNotResolvedException;
import org.datanucleus.util.ClassUtils;
import java.util.Properties;
import java.util.Map;
import org.datanucleus.util.StringUtils;
import org.datanucleus.ClassLoaderResolver;

public class MapMetaData extends ContainerMetaData
{
    protected MapType mapType;
    protected ContainerComponent key;
    protected ContainerComponent value;
    
    public MapMetaData(final MapMetaData mapmd) {
        super(mapmd);
        this.key = new ContainerComponent();
        this.key.embedded = mapmd.key.embedded;
        this.key.serialized = mapmd.key.serialized;
        this.key.dependent = mapmd.key.dependent;
        this.key.type = mapmd.key.type;
        this.key.classMetaData = mapmd.key.classMetaData;
        this.value = new ContainerComponent();
        this.value.embedded = mapmd.value.embedded;
        this.value.serialized = mapmd.value.serialized;
        this.value.dependent = mapmd.value.dependent;
        this.value.type = mapmd.value.type;
        this.value.classMetaData = mapmd.value.classMetaData;
    }
    
    public MapMetaData() {
        this.key = new ContainerComponent();
        this.value = new ContainerComponent();
    }
    
    @Override
    public void populate(final ClassLoaderResolver clr, final ClassLoader primary, final MetaDataManager mmgr) {
        final AbstractMemberMetaData mmd = (AbstractMemberMetaData)this.parent;
        if (!StringUtils.isWhitespace(this.key.type) && this.key.type.indexOf(44) > 0) {
            throw new InvalidMemberMetaDataException(MapMetaData.LOCALISER, "044143", mmd.getClassName(), mmd.getName());
        }
        if (!StringUtils.isWhitespace(this.value.type) && this.value.type.indexOf(44) > 0) {
            throw new InvalidMemberMetaDataException(MapMetaData.LOCALISER, "044144", mmd.getClassName(), mmd.getName());
        }
        final ApiAdapter api = mmgr.getApiAdapter();
        this.key.populate(((AbstractMemberMetaData)this.parent).getAbstractClassMetaData().getPackageName(), clr, primary, mmgr);
        this.value.populate(((AbstractMemberMetaData)this.parent).getAbstractClassMetaData().getPackageName(), clr, primary, mmgr);
        final Class field_type = this.getMemberMetaData().getType();
        if (!Map.class.isAssignableFrom(field_type)) {
            throw new InvalidMemberMetaDataException(MapMetaData.LOCALISER, "044145", mmd.getClassName(), mmd.getName());
        }
        if (Properties.class.isAssignableFrom(field_type)) {
            if (this.key.type == null) {
                this.key.type = String.class.getName();
            }
            if (this.value.type == null) {
                this.value.type = String.class.getName();
            }
        }
        if (this.key.type == null) {
            throw new InvalidMemberMetaDataException(MapMetaData.LOCALISER, "044146", mmd.getClassName(), mmd.getName());
        }
        Class keyTypeClass = null;
        try {
            keyTypeClass = clr.classForName(this.key.type, primary);
        }
        catch (ClassNotResolvedException cnre) {
            try {
                keyTypeClass = clr.classForName(ClassUtils.getJavaLangClassForType(this.key.type), primary);
            }
            catch (ClassNotResolvedException cnre2) {
                throw new InvalidMemberMetaDataException(MapMetaData.LOCALISER, "044147", mmd.getClassName(), mmd.getName(), this.key.type);
            }
        }
        if (!keyTypeClass.getName().equals(this.key.type)) {
            NucleusLogger.METADATA.info(MapMetaData.LOCALISER.msg("044148", this.getFieldName(), this.getMemberMetaData().getClassName(false), this.key.type, keyTypeClass.getName()));
            this.key.type = keyTypeClass.getName();
        }
        if (this.key.embedded == null) {
            if (mmgr.getNucleusContext().getTypeManager().isDefaultEmbeddedType(keyTypeClass)) {
                this.key.embedded = Boolean.TRUE;
            }
            else if (api.isPersistable(keyTypeClass) || Object.class.isAssignableFrom(keyTypeClass) || keyTypeClass.isInterface()) {
                this.key.embedded = Boolean.FALSE;
            }
            else {
                this.key.embedded = Boolean.TRUE;
            }
        }
        if (Boolean.FALSE.equals(this.key.embedded) && !api.isPersistable(keyTypeClass) && !keyTypeClass.isInterface() && keyTypeClass != Object.class) {
            this.key.embedded = Boolean.TRUE;
        }
        final KeyMetaData keymd = ((AbstractMemberMetaData)this.parent).getKeyMetaData();
        if (keymd != null && keymd.getEmbeddedMetaData() != null) {
            this.key.embedded = Boolean.TRUE;
        }
        if (this.value.type == null) {
            throw new InvalidMemberMetaDataException(MapMetaData.LOCALISER, "044149", mmd.getClassName(), mmd.getName());
        }
        Class valueTypeClass = null;
        try {
            valueTypeClass = clr.classForName(this.value.type);
        }
        catch (ClassNotResolvedException cnre3) {
            try {
                valueTypeClass = clr.classForName(ClassUtils.getJavaLangClassForType(this.value.type));
            }
            catch (ClassNotResolvedException cnre4) {
                throw new InvalidMemberMetaDataException(MapMetaData.LOCALISER, "044150", mmd.getClassName(), mmd.getName(), this.value.type);
            }
        }
        if (!valueTypeClass.getName().equals(this.value.type)) {
            NucleusLogger.METADATA.info(MapMetaData.LOCALISER.msg("044151", this.getFieldName(), this.getMemberMetaData().getClassName(false), this.value.type, valueTypeClass.getName()));
            this.value.type = valueTypeClass.getName();
        }
        if (this.value.embedded == null) {
            if (mmgr.getNucleusContext().getTypeManager().isDefaultEmbeddedType(valueTypeClass)) {
                this.value.embedded = Boolean.TRUE;
            }
            else if (api.isPersistable(valueTypeClass) || Object.class.isAssignableFrom(valueTypeClass) || valueTypeClass.isInterface()) {
                this.value.embedded = Boolean.FALSE;
            }
            else {
                this.value.embedded = Boolean.TRUE;
            }
        }
        if (this.value.embedded == Boolean.FALSE && !api.isPersistable(valueTypeClass) && !valueTypeClass.isInterface() && valueTypeClass != Object.class) {
            this.value.embedded = Boolean.TRUE;
        }
        final ValueMetaData valuemd = ((AbstractMemberMetaData)this.parent).getValueMetaData();
        if (valuemd != null && valuemd.getEmbeddedMetaData() != null) {
            this.value.embedded = Boolean.TRUE;
        }
        this.key.classMetaData = mmgr.getMetaDataForClassInternal(keyTypeClass, clr);
        this.value.classMetaData = mmgr.getMetaDataForClassInternal(valueTypeClass, clr);
        if (keymd != null && keymd.mappedBy != null && keymd.mappedBy.equals("#PK")) {
            if (this.value.classMetaData.getNoOfPrimaryKeyMembers() != 1) {
                throw new NucleusUserException("DataNucleus does not support use of <map-key> with no name field when the value class has a composite primary key");
            }
            final int[] valuePkFieldNums = this.value.classMetaData.getPKMemberPositions();
            keymd.mappedBy = this.value.classMetaData.getMetaDataForManagedMemberAtAbsolutePosition(valuePkFieldNums[0]).name;
        }
        super.populate(clr, primary, mmgr);
        this.setPopulated();
    }
    
    public MapType getMapType() {
        if (this.mapType == null) {
            final AbstractMemberMetaData mmd = (AbstractMemberMetaData)this.parent;
            if (mmd.getJoinMetaData() != null) {
                this.mapType = MapType.MAP_TYPE_JOIN;
            }
            else if (mmd.getValueMetaData() != null && mmd.getValueMetaData().getMappedBy() != null) {
                this.mapType = MapType.MAP_TYPE_VALUE_IN_KEY;
            }
            else {
                this.mapType = MapType.MAP_TYPE_KEY_IN_VALUE;
            }
        }
        return this.mapType;
    }
    
    public String getKeyType() {
        return this.key.type;
    }
    
    public AbstractClassMetaData getKeyClassMetaData(final ClassLoaderResolver clr, final MetaDataManager mmgr) {
        if (this.key.classMetaData != null && !this.key.classMetaData.isInitialised()) {
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
                @Override
                public Object run() {
                    MapMetaData.this.key.classMetaData.initialise(clr, mmgr);
                    return null;
                }
            });
        }
        return this.key.classMetaData;
    }
    
    public boolean keyIsPersistent() {
        return this.key.classMetaData != null;
    }
    
    public String getValueType() {
        return this.value.type;
    }
    
    public AbstractClassMetaData getValueClassMetaData(final ClassLoaderResolver clr, final MetaDataManager mmgr) {
        if (this.value.classMetaData != null && !this.value.classMetaData.isInitialised()) {
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
                @Override
                public Object run() {
                    MapMetaData.this.value.classMetaData.initialise(clr, mmgr);
                    return null;
                }
            });
        }
        return this.value.classMetaData;
    }
    
    public boolean valueIsPersistent() {
        return this.value.classMetaData != null;
    }
    
    public boolean isEmbeddedKey() {
        return this.key.embedded != null && this.key.embedded;
    }
    
    public boolean isEmbeddedValue() {
        return this.value.embedded != null && this.value.embedded;
    }
    
    public boolean isSerializedKey() {
        return this.key.serialized != null && this.key.serialized;
    }
    
    public boolean isSerializedValue() {
        return this.value.serialized != null && this.value.serialized;
    }
    
    public boolean isDependentKey() {
        return this.key.dependent != null && this.key.classMetaData != null && this.key.dependent;
    }
    
    public boolean isDependentValue() {
        return this.value.dependent != null && this.value.classMetaData != null && this.value.dependent;
    }
    
    public MapMetaData setKeyType(final String type) {
        this.key.setType(type);
        return this;
    }
    
    public MapMetaData setEmbeddedKey(final boolean embedded) {
        this.key.setEmbedded(embedded);
        return this;
    }
    
    public MapMetaData setSerializedKey(final boolean serialized) {
        this.key.setSerialized(serialized);
        return this;
    }
    
    public MapMetaData setDependentKey(final boolean dependent) {
        this.key.setDependent(dependent);
        return this;
    }
    
    public MapMetaData setValueType(final String type) {
        this.value.setType(type);
        return this;
    }
    
    public MapMetaData setEmbeddedValue(final boolean embedded) {
        this.value.setEmbedded(embedded);
        return this;
    }
    
    public MapMetaData setSerializedValue(final boolean serialized) {
        this.value.setSerialized(serialized);
        return this;
    }
    
    public MapMetaData setDependentValue(final boolean dependent) {
        this.value.setDependent(dependent);
        return this;
    }
    
    void getReferencedClassMetaData(final List orderedCMDs, final Set referencedCMDs, final ClassLoaderResolver clr, final MetaDataManager mmgr) {
        final AbstractClassMetaData key_cmd = mmgr.getMetaDataForClass(this.key.type, clr);
        if (key_cmd != null) {
            key_cmd.getReferencedClassMetaData(orderedCMDs, referencedCMDs, clr, mmgr);
        }
        final AbstractClassMetaData value_cmd = mmgr.getMetaDataForClass(this.value.type, clr);
        if (value_cmd != null) {
            value_cmd.getReferencedClassMetaData(orderedCMDs, referencedCMDs, clr, mmgr);
        }
    }
    
    @Override
    public String toString(final String prefix, final String indent) {
        final StringBuffer sb = new StringBuffer();
        sb.append(prefix).append("<map key-type=\"").append(this.key.type).append("\" value-type=\"").append(this.value.type).append("\"");
        if (this.key.embedded != null) {
            sb.append(" embedded-key=\"").append(this.key.embedded).append("\"");
        }
        if (this.value.embedded != null) {
            sb.append(" embedded-value=\"").append(this.value.embedded).append("\"");
        }
        if (this.key.dependent != null) {
            sb.append(" dependent-key=\"").append(this.key.dependent).append("\"");
        }
        if (this.value.dependent != null) {
            sb.append(" dependent-value=\"").append(this.value.dependent).append("\"");
        }
        if (this.key.serialized != null) {
            sb.append(" serialized-key=\"").append(this.key.serialized).append("\"");
        }
        if (this.value.serialized != null) {
            sb.append(" serialized-value=\"").append(this.value.serialized).append("\"");
        }
        sb.append(">\n");
        sb.append(super.toString(prefix + indent, indent));
        sb.append(prefix).append("</map>\n");
        return sb.toString();
    }
    
    public enum MapType
    {
        MAP_TYPE_JOIN, 
        MAP_TYPE_KEY_IN_VALUE, 
        MAP_TYPE_VALUE_IN_KEY;
    }
}
