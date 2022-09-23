// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

import org.datanucleus.util.StringUtils;
import java.util.Iterator;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.datanucleus.util.ClassUtils;
import java.util.Collections;
import org.datanucleus.exceptions.ClassNotResolvedException;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.ClassLoaderResolver;
import java.util.ArrayList;
import java.util.List;

public class EmbeddedMetaData extends MetaData
{
    protected String ownerMember;
    protected String nullIndicatorColumn;
    protected String nullIndicatorValue;
    protected DiscriminatorMetaData discriminatorMetaData;
    protected final List members;
    protected AbstractMemberMetaData[] fieldMetaData;
    
    public EmbeddedMetaData(final EmbeddedMetaData embmd) {
        super(null, embmd);
        this.members = new ArrayList();
        this.ownerMember = embmd.ownerMember;
        this.nullIndicatorColumn = embmd.nullIndicatorColumn;
        this.nullIndicatorValue = embmd.nullIndicatorValue;
        for (int i = 0; i < embmd.members.size(); ++i) {
            if (embmd.members.get(i) instanceof FieldMetaData) {
                this.addMember(new FieldMetaData(this, embmd.members.get(i)));
            }
            else {
                this.addMember(new PropertyMetaData(this, embmd.members.get(i)));
            }
        }
    }
    
    public EmbeddedMetaData() {
        this.members = new ArrayList();
    }
    
    public void populate(final ClassLoaderResolver clr, final ClassLoader primary, final MetaDataManager mmgr) {
        final MetaData md = this.getParent();
        AbstractMemberMetaData apmd = null;
        AbstractClassMetaData embCmd = null;
        String embeddedType = null;
        if (md instanceof AbstractMemberMetaData) {
            apmd = (AbstractMemberMetaData)md;
            embeddedType = apmd.getTypeName();
            embCmd = mmgr.getMetaDataForClassInternal(apmd.getType(), clr);
            if (embCmd == null && apmd.getFieldTypes() != null && apmd.getFieldTypes().length == 1) {
                embCmd = mmgr.getMetaDataForClassInternal(clr.classForName(apmd.getFieldTypes()[0]), clr);
            }
            if (embCmd == null) {
                NucleusLogger.METADATA.error(EmbeddedMetaData.LOCALISER.msg("044121", apmd.getFullFieldName(), apmd.getTypeName()));
                throw new InvalidMemberMetaDataException(EmbeddedMetaData.LOCALISER, "044121", apmd.getClassName(), apmd.getName(), apmd.getTypeName());
            }
        }
        else if (md instanceof ElementMetaData) {
            final ElementMetaData elemmd = (ElementMetaData)md;
            apmd = (AbstractMemberMetaData)elemmd.getParent();
            embeddedType = apmd.getCollection().getElementType();
            try {
                final Class cls = clr.classForName(embeddedType, primary);
                embCmd = mmgr.getMetaDataForClassInternal(cls, clr);
            }
            catch (ClassNotResolvedException ex) {}
            if (embCmd == null) {
                NucleusLogger.METADATA.error(EmbeddedMetaData.LOCALISER.msg("044122", apmd.getFullFieldName(), embeddedType));
                throw new InvalidMemberMetaDataException(EmbeddedMetaData.LOCALISER, "044122", apmd.getClassName(), apmd.getName(), embeddedType);
            }
        }
        else if (md instanceof KeyMetaData) {
            final KeyMetaData keymd = (KeyMetaData)md;
            apmd = (AbstractMemberMetaData)keymd.getParent();
            embeddedType = apmd.getMap().getKeyType();
            try {
                final Class cls = clr.classForName(embeddedType, primary);
                embCmd = mmgr.getMetaDataForClassInternal(cls, clr);
            }
            catch (ClassNotResolvedException ex2) {}
            if (embCmd == null) {
                NucleusLogger.METADATA.error(EmbeddedMetaData.LOCALISER.msg("044123", apmd.getFullFieldName(), embeddedType));
                throw new InvalidMemberMetaDataException(EmbeddedMetaData.LOCALISER, "044123", apmd.getClassName(), apmd.getName(), embeddedType);
            }
        }
        else if (md instanceof ValueMetaData) {
            final ValueMetaData valuemd = (ValueMetaData)md;
            apmd = (AbstractMemberMetaData)valuemd.getParent();
            embeddedType = apmd.getMap().getValueType();
            try {
                final Class cls = clr.classForName(embeddedType, primary);
                embCmd = mmgr.getMetaDataForClassInternal(cls, clr);
            }
            catch (ClassNotResolvedException ex3) {}
            if (embCmd == null) {
                NucleusLogger.METADATA.error(EmbeddedMetaData.LOCALISER.msg("044124", apmd.getFullFieldName(), embeddedType));
                throw new InvalidMemberMetaDataException(EmbeddedMetaData.LOCALISER, "044124", apmd.getClassName(), apmd.getName(), embeddedType);
            }
        }
        for (final Object fld : this.members) {
            if (embCmd instanceof InterfaceMetaData && fld instanceof FieldMetaData) {
                throw new InvalidMemberMetaDataException(EmbeddedMetaData.LOCALISER, "044129", apmd.getClassName(), apmd.getName(), ((AbstractMemberMetaData)fld).getName());
            }
        }
        Class embeddedClass = null;
        Collections.sort((List<Comparable>)this.members);
        try {
            embeddedClass = clr.classForName(embeddedType, primary);
            final Field[] cls_fields = embeddedClass.getDeclaredFields();
            for (int i = 0; i < cls_fields.length; ++i) {
                if (cls_fields[i].getDeclaringClass().getName().equals(embeddedType) && !cls_fields[i].getName().startsWith("jdo") && !ClassUtils.isInnerClass(cls_fields[i].getName()) && !Modifier.isStatic(cls_fields[i].getModifiers()) && Collections.binarySearch(this.members, cls_fields[i].getName()) < 0) {
                    final AbstractMemberMetaData embMmd = embCmd.getMetaDataForMember(cls_fields[i].getName());
                    FieldMetaData omittedFmd = null;
                    if (embMmd != null) {
                        FieldPersistenceModifier fieldModifier = embMmd.getPersistenceModifier();
                        if (fieldModifier == FieldPersistenceModifier.DEFAULT) {
                            fieldModifier = embMmd.getDefaultFieldPersistenceModifier(cls_fields[i].getType(), cls_fields[i].getModifiers(), mmgr.isFieldTypePersistable(cls_fields[i].getType()), mmgr);
                        }
                        if (fieldModifier == FieldPersistenceModifier.PERSISTENT) {
                            omittedFmd = new FieldMetaData(this, embMmd);
                            omittedFmd.setPrimaryKey(false);
                        }
                    }
                    else {
                        omittedFmd = new FieldMetaData(this, cls_fields[i].getName());
                    }
                    if (omittedFmd != null) {
                        NucleusLogger.METADATA.debug(EmbeddedMetaData.LOCALISER.msg("044125", apmd.getClassName(), cls_fields[i].getName(), embeddedType));
                        this.members.add(omittedFmd);
                        Collections.sort((List<Comparable>)this.members);
                    }
                }
            }
        }
        catch (Exception e) {
            NucleusLogger.METADATA.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
        if (embCmd instanceof InterfaceMetaData) {
            try {
                final Method[] clsMethods = embeddedClass.getDeclaredMethods();
                for (int i = 0; i < clsMethods.length; ++i) {
                    if (clsMethods[i].getDeclaringClass().getName().equals(embeddedType) && (clsMethods[i].getName().startsWith("get") || clsMethods[i].getName().startsWith("is")) && !ClassUtils.isInnerClass(clsMethods[i].getName())) {
                        final String fieldName = ClassUtils.getFieldNameForJavaBeanGetter(clsMethods[i].getName());
                        if (Collections.binarySearch(this.members, fieldName) < 0) {
                            NucleusLogger.METADATA.debug(EmbeddedMetaData.LOCALISER.msg("044060", apmd.getClassName(), fieldName));
                            final PropertyMetaData pmd = new PropertyMetaData(this, fieldName);
                            this.members.add(pmd);
                            Collections.sort((List<Comparable>)this.members);
                        }
                    }
                }
            }
            catch (Exception e) {
                NucleusLogger.METADATA.error(e.getMessage(), e);
                throw new RuntimeException(e.getMessage());
            }
        }
        Collections.sort((List<Comparable>)this.members);
        final Iterator memberIter = this.members.iterator();
        while (memberIter.hasNext()) {
            Class embFmdClass = embeddedClass;
            final AbstractMemberMetaData fieldFmd = memberIter.next();
            if (!fieldFmd.fieldBelongsToClass()) {
                try {
                    embFmdClass = clr.classForName(fieldFmd.getClassName(true));
                }
                catch (ClassNotResolvedException cnre) {
                    final String fieldClsName = embeddedClass.getPackage().getName() + "." + fieldFmd.getClassName(true);
                    fieldFmd.setClassName(fieldClsName);
                    embFmdClass = clr.classForName(fieldClsName);
                }
            }
            if (fieldFmd instanceof FieldMetaData) {
                Field cls_field = null;
                try {
                    cls_field = embFmdClass.getDeclaredField(fieldFmd.getName());
                }
                catch (Exception e2) {
                    throw new InvalidMemberMetaDataException(EmbeddedMetaData.LOCALISER, "044071", embFmdClass.getName(), fieldFmd.getName());
                }
                fieldFmd.populate(clr, cls_field, null, primary, mmgr);
            }
            else {
                Method cls_method = null;
                try {
                    cls_method = embFmdClass.getDeclaredMethod(ClassUtils.getJavaBeanGetterName(fieldFmd.getName(), true), (Class[])new Class[0]);
                }
                catch (Exception e2) {
                    try {
                        cls_method = embFmdClass.getDeclaredMethod(ClassUtils.getJavaBeanGetterName(fieldFmd.getName(), false), (Class[])new Class[0]);
                    }
                    catch (Exception e3) {
                        throw new InvalidMemberMetaDataException(EmbeddedMetaData.LOCALISER, "044071", embFmdClass.getName(), fieldFmd.getName());
                    }
                }
                fieldFmd.populate(clr, null, cls_method, primary, mmgr);
            }
        }
    }
    
    @Override
    public void initialise(final ClassLoaderResolver clr, final MetaDataManager mmgr) {
        this.fieldMetaData = new AbstractMemberMetaData[this.members.size()];
        for (int i = 0; i < this.fieldMetaData.length; ++i) {
            (this.fieldMetaData[i] = this.members.get(i)).initialise(clr, mmgr);
        }
        if (this.discriminatorMetaData != null) {
            this.discriminatorMetaData.initialise(clr, mmgr);
        }
        this.setInitialised();
    }
    
    public final AbstractMemberMetaData[] getMemberMetaData() {
        return this.fieldMetaData;
    }
    
    public final String getOwnerMember() {
        return this.ownerMember;
    }
    
    public EmbeddedMetaData setOwnerMember(final String ownerMember) {
        this.ownerMember = (StringUtils.isWhitespace(ownerMember) ? null : ownerMember);
        return this;
    }
    
    public final String getNullIndicatorColumn() {
        return this.nullIndicatorColumn;
    }
    
    public EmbeddedMetaData setNullIndicatorColumn(final String column) {
        this.nullIndicatorColumn = (StringUtils.isWhitespace(column) ? null : column);
        return this;
    }
    
    public final String getNullIndicatorValue() {
        return this.nullIndicatorValue;
    }
    
    public EmbeddedMetaData setNullIndicatorValue(final String value) {
        this.nullIndicatorValue = (StringUtils.isWhitespace(value) ? null : value);
        return this;
    }
    
    public final DiscriminatorMetaData getDiscriminatorMetaData() {
        return this.discriminatorMetaData;
    }
    
    public EmbeddedMetaData setDiscriminatorMetaData(final DiscriminatorMetaData dismd) {
        this.discriminatorMetaData = dismd;
        return (EmbeddedMetaData)(this.discriminatorMetaData.parent = this);
    }
    
    public DiscriminatorMetaData newDiscriminatorMetadata() {
        final DiscriminatorMetaData dismd = new DiscriminatorMetaData();
        this.setDiscriminatorMetaData(dismd);
        return dismd;
    }
    
    public void addMember(final AbstractMemberMetaData mmd) {
        if (mmd == null) {
            return;
        }
        if (this.isInitialised()) {
            throw new InvalidMemberMetaDataException(EmbeddedMetaData.LOCALISER, "044108", mmd.getClassName(), mmd.getName());
        }
        for (final AbstractMemberMetaData md : this.members) {
            if (mmd.getName().equals(md.getName())) {
                throw new InvalidMemberMetaDataException(EmbeddedMetaData.LOCALISER, "044112", mmd.getClassName(), mmd.getName());
            }
        }
        this.members.add(mmd);
        mmd.parent = this;
    }
    
    public FieldMetaData newFieldMetaData(final String name) {
        final FieldMetaData fmd = new FieldMetaData(this, name);
        this.addMember(fmd);
        return fmd;
    }
    
    public PropertyMetaData newPropertyMetaData(final String name) {
        final PropertyMetaData pmd = new PropertyMetaData(this, name);
        this.addMember(pmd);
        return pmd;
    }
    
    @Override
    public String toString(final String prefix, final String indent) {
        final StringBuffer sb = new StringBuffer();
        sb.append(prefix).append("<embedded");
        if (this.ownerMember != null) {
            sb.append(" owner-field=\"" + this.ownerMember + "\"");
        }
        if (this.nullIndicatorColumn != null) {
            sb.append(" null-indicator-column=\"" + this.nullIndicatorColumn + "\"");
        }
        if (this.nullIndicatorValue != null) {
            sb.append(" null-indicator-value=\"" + this.nullIndicatorValue + "\"");
        }
        sb.append(">\n");
        if (this.discriminatorMetaData != null) {
            sb.append(this.discriminatorMetaData.toString(prefix + indent, indent));
        }
        for (int i = 0; i < this.members.size(); ++i) {
            final AbstractMemberMetaData f = this.members.get(i);
            sb.append(f.toString(prefix + indent, indent));
        }
        sb.append(super.toString(prefix + indent, indent));
        sb.append(prefix + "</embedded>\n");
        return sb.toString();
    }
}
