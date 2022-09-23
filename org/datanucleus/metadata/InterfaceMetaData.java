// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

import org.datanucleus.exceptions.NucleusUserException;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import org.datanucleus.exceptions.ClassNotResolvedException;
import java.util.List;
import java.util.Collections;
import java.lang.reflect.Modifier;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.exceptions.NucleusException;
import java.util.Iterator;
import java.util.HashMap;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.ClassLoaderResolver;

public class InterfaceMetaData extends AbstractClassMetaData
{
    public InterfaceMetaData(final PackageMetaData parent, final String name) {
        super(parent, name);
    }
    
    @Override
    public synchronized void initialise(final ClassLoaderResolver clr, final MetaDataManager mmgr) {
        if (this.initialising || this.isInitialised()) {
            return;
        }
        this.checkPopulated();
        try {
            this.initialising = true;
            if (this.pcSuperclassMetaData != null && !this.pcSuperclassMetaData.isInitialised()) {
                this.pcSuperclassMetaData.initialise(clr, mmgr);
            }
            if (NucleusLogger.METADATA.isDebugEnabled()) {
                NucleusLogger.METADATA.debug(InterfaceMetaData.LOCALISER.msg("044076", this.fullName));
            }
            this.validateObjectIdClass(clr, mmgr);
            Iterator fields_iter = this.members.iterator();
            int no_of_managed_fields = 0;
            int no_of_overridden_fields = 0;
            while (fields_iter.hasNext()) {
                final AbstractMemberMetaData fmd = fields_iter.next();
                fmd.initialise(clr, mmgr);
                if (fmd.isFieldToBePersisted()) {
                    if (fmd.fieldBelongsToClass()) {
                        ++no_of_managed_fields;
                    }
                    else {
                        ++no_of_overridden_fields;
                    }
                }
            }
            this.managedMembers = new AbstractMemberMetaData[no_of_managed_fields];
            this.overriddenMembers = new AbstractMemberMetaData[no_of_overridden_fields];
            fields_iter = this.members.iterator();
            int field_id = 0;
            int overridden_field_id = 0;
            this.memberPositionsByName = new HashMap<String, Integer>();
            while (fields_iter.hasNext()) {
                final AbstractMemberMetaData fmd2 = fields_iter.next();
                if (fmd2.isFieldToBePersisted()) {
                    if (fmd2.fieldBelongsToClass()) {
                        fmd2.setFieldId(field_id);
                        this.managedMembers[field_id] = fmd2;
                        this.memberPositionsByName.put(fmd2.getName(), field_id);
                        ++field_id;
                    }
                    else {
                        this.overriddenMembers[overridden_field_id++] = fmd2;
                    }
                }
            }
            if (this.pcSuperclassMetaData != null) {
                if (!this.pcSuperclassMetaData.isInitialised()) {
                    this.pcSuperclassMetaData.initialise(clr, mmgr);
                }
                this.noOfInheritedManagedMembers = this.pcSuperclassMetaData.getNoOfInheritedManagedMembers() + this.pcSuperclassMetaData.getNoOfManagedMembers();
            }
            this.initialiseMemberPositionInformation(mmgr);
            this.joinMetaData = new JoinMetaData[this.joins.size()];
            for (int i = 0; i < this.joinMetaData.length; ++i) {
                (this.joinMetaData[i] = this.joins.get(i)).initialise(clr, mmgr);
            }
            this.indexMetaData = new IndexMetaData[this.indexes.size()];
            for (int i = 0; i < this.indexMetaData.length; ++i) {
                (this.indexMetaData[i] = this.indexes.get(i)).initialise(clr, mmgr);
            }
            this.foreignKeyMetaData = new ForeignKeyMetaData[this.foreignKeys.size()];
            for (int i = 0; i < this.foreignKeyMetaData.length; ++i) {
                (this.foreignKeyMetaData[i] = this.foreignKeys.get(i)).initialise(clr, mmgr);
            }
            this.uniqueMetaData = new UniqueMetaData[this.uniqueConstraints.size()];
            for (int i = 0; i < this.uniqueMetaData.length; ++i) {
                (this.uniqueMetaData[i] = this.uniqueConstraints.get(i)).initialise(clr, mmgr);
            }
            if (this.fetchGroups != null) {
                this.fetchGroupMetaDataByName = new HashMap<String, FetchGroupMetaData>();
                for (final FetchGroupMetaData fgmd : this.fetchGroups) {
                    fgmd.initialise(clr, mmgr);
                    this.fetchGroupMetaDataByName.put(fgmd.getName(), fgmd);
                }
            }
            if (this.identityType == IdentityType.DATASTORE && this.identityMetaData == null) {
                if (this.pcSuperclassMetaData != null) {
                    final IdentityMetaData superImd = this.pcSuperclassMetaData.getIdentityMetaData();
                    (this.identityMetaData = new IdentityMetaData()).setColumnName(superImd.getColumnName());
                    this.identityMetaData.setValueStrategy(superImd.getValueStrategy());
                    this.identityMetaData.setSequence(superImd.getSequence());
                }
                else {
                    this.identityMetaData = new IdentityMetaData();
                }
            }
            if (this.versionMetaData != null) {
                this.versionMetaData.initialise(clr, mmgr);
            }
            if (this.identityMetaData != null) {
                this.identityMetaData.initialise(clr, mmgr);
            }
            if (this.inheritanceMetaData != null) {
                this.inheritanceMetaData.initialise(clr, mmgr);
            }
            if (this.identityType == IdentityType.APPLICATION) {
                this.usesSingleFieldIdentityClass = mmgr.getApiAdapter().isSingleFieldIdentityClass(this.getObjectidClass());
            }
            this.joins.clear();
            this.joins = null;
            this.foreignKeys.clear();
            this.foreignKeys = null;
            this.indexes.clear();
            this.indexes = null;
            this.uniqueConstraints.clear();
            this.uniqueConstraints = null;
            this.setInitialised();
        }
        finally {
            this.initialising = false;
            mmgr.abstractClassMetaDataInitialised(this);
        }
    }
    
    @Override
    public synchronized void populate(final ClassLoaderResolver clr, final ClassLoader primary, final MetaDataManager mmgr) {
        if (this.isInitialised() || this.isPopulated()) {
            NucleusLogger.METADATA.error(InterfaceMetaData.LOCALISER.msg("044068", this.name));
            throw new NucleusException(InterfaceMetaData.LOCALISER.msg("044068", this.fullName)).setFatal();
        }
        if (this.populating) {
            return;
        }
        try {
            if (NucleusLogger.METADATA.isDebugEnabled()) {
                NucleusLogger.METADATA.debug(InterfaceMetaData.LOCALISER.msg("044075", this.fullName));
            }
            this.populating = true;
            final Class cls = this.loadClass(clr, primary, mmgr);
            if (!this.isMetaDataComplete()) {
                mmgr.addAnnotationsDataToClass(cls, this, clr);
            }
            mmgr.addORMDataToClass(cls, clr);
            if (ClassUtils.isInnerClass(this.fullName) && !Modifier.isStatic(cls.getModifiers()) && this.persistenceModifier == ClassPersistenceModifier.PERSISTENCE_CAPABLE) {
                throw new InvalidClassMetaDataException(InterfaceMetaData.LOCALISER, "044063", this.fullName);
            }
            this.determineSuperClassName(clr, cls, mmgr);
            this.inheritIdentity();
            this.determineIdentity();
            this.validateUserInputForIdentity();
            this.addMetaDataForMembersNotInMetaData(cls);
            this.validateUserInputForInheritanceMetaData(false);
            this.determineInheritanceMetaData(mmgr);
            this.applyDefaultDiscriminatorValueWhenNotSpecified(mmgr);
            if (this.objectidClass == null) {
                this.populatePropertyMetaData(clr, cls, true, primary, mmgr);
                this.determineObjectIdClass(mmgr);
                this.populatePropertyMetaData(clr, cls, false, primary, mmgr);
            }
            else {
                this.populatePropertyMetaData(clr, cls, true, primary, mmgr);
                this.populatePropertyMetaData(clr, cls, false, primary, mmgr);
                this.determineObjectIdClass(mmgr);
            }
            this.setPopulated();
        }
        catch (RuntimeException e) {
            NucleusLogger.METADATA.debug(e);
            throw e;
        }
        finally {
            this.populating = false;
        }
    }
    
    @Override
    protected AbstractMemberMetaData newDefaultedProperty(final String name) {
        return new PropertyMetaData(this, name);
    }
    
    protected void populatePropertyMetaData(final ClassLoaderResolver clr, final Class cls, final boolean pkFields, final ClassLoader primary, final MetaDataManager mmgr) {
        Collections.sort((List<Comparable>)this.members);
        for (final AbstractMemberMetaData fmd : this.members) {
            if (pkFields == fmd.isPrimaryKey()) {
                Class fieldCls = cls;
                if (!fmd.fieldBelongsToClass()) {
                    try {
                        fieldCls = clr.classForName(fmd.getClassName(), primary);
                    }
                    catch (ClassNotResolvedException cnre) {
                        final String fieldClassName = this.getPackageName() + "." + fmd.getClassName();
                        try {
                            fieldCls = clr.classForName(fieldClassName, primary);
                            fmd.setClassName(fieldClassName);
                        }
                        catch (ClassNotResolvedException cnre2) {
                            NucleusLogger.METADATA.error(InterfaceMetaData.LOCALISER.msg("044080", fieldClassName));
                            throw new InvalidClassMetaDataException(InterfaceMetaData.LOCALISER, "044080", this.fullName, fieldClassName);
                        }
                    }
                }
                Method cls_method = null;
                try {
                    cls_method = fieldCls.getDeclaredMethod(ClassUtils.getJavaBeanGetterName(fmd.getName(), true), (Class[])new Class[0]);
                }
                catch (Exception e) {
                    try {
                        cls_method = fieldCls.getDeclaredMethod(ClassUtils.getJavaBeanGetterName(fmd.getName(), false), (Class[])new Class[0]);
                    }
                    catch (Exception e2) {
                        throw new InvalidClassMetaDataException(InterfaceMetaData.LOCALISER, "044072", this.fullName, fmd.getFullFieldName());
                    }
                }
                fmd.populate(clr, null, cls_method, primary, mmgr);
            }
        }
    }
    
    protected void addMetaDataForMembersNotInMetaData(final Class cls) {
        Collections.sort((List<Comparable>)this.members);
        try {
            final Method[] clsMethods = cls.getDeclaredMethods();
            for (int i = 0; i < clsMethods.length; ++i) {
                if (clsMethods[i].getDeclaringClass().getName().equals(this.fullName) && (clsMethods[i].getName().startsWith("get") || clsMethods[i].getName().startsWith("is")) && !ClassUtils.isInnerClass(clsMethods[i].getName()) && !Modifier.isStatic(clsMethods[i].getModifiers())) {
                    final String memberName = ClassUtils.getFieldNameForJavaBeanGetter(clsMethods[i].getName());
                    if (Collections.binarySearch(this.members, memberName) < 0) {
                        final String setterName = ClassUtils.getJavaBeanSetterName(memberName);
                        for (int j = 0; j < clsMethods.length; ++j) {
                            if (clsMethods[j].getName().equals(setterName)) {
                                NucleusLogger.METADATA.debug(InterfaceMetaData.LOCALISER.msg("044060", this.fullName, memberName));
                                final AbstractMemberMetaData mmd = this.newDefaultedProperty(memberName);
                                this.members.add(mmd);
                                Collections.sort((List<Comparable>)this.members);
                                break;
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            NucleusLogger.METADATA.error(e.getMessage(), e);
            throw new NucleusUserException(e.getMessage());
        }
    }
    
    @Override
    public String toString(final String prefix, final String indent) {
        final StringBuffer sb = new StringBuffer();
        sb.append(prefix).append("<interface name=\"" + this.name + "\"\n");
        if (this.identityType != null) {
            sb.append(prefix).append("       identity-type=\"" + this.identityType + "\"\n");
        }
        if (this.objectidClass != null) {
            sb.append(prefix).append("       objectid-class=\"" + this.objectidClass + "\"\n");
        }
        if (!this.requiresExtent) {
            sb.append(prefix).append("       requires-extent=\"false\"");
        }
        if (this.embeddedOnly) {
            sb.append(prefix).append("       embedded-only=\"true\"\n");
        }
        if (this.detachable) {
            sb.append(prefix).append("       detachable=\"true\"\n");
        }
        if (this.table != null) {
            sb.append(prefix).append("       table=\"" + this.table + "\"\n");
        }
        sb.append(">\n");
        if (this.identityMetaData != null) {
            sb.append(this.identityMetaData.toString(prefix + indent, indent));
        }
        if (this.primaryKeyMetaData != null) {
            sb.append(this.primaryKeyMetaData.toString(prefix + indent, indent));
        }
        if (this.inheritanceMetaData != null) {
            sb.append(this.inheritanceMetaData.toString(prefix + indent, indent));
        }
        if (this.versionMetaData != null) {
            sb.append(this.versionMetaData.toString(prefix + indent, indent));
        }
        if (this.joins != null) {
            for (int i = 0; i < this.joins.size(); ++i) {
                final JoinMetaData jmd = this.joins.get(i);
                sb.append(jmd.toString(prefix + indent, indent));
            }
        }
        if (this.foreignKeys != null) {
            for (int i = 0; i < this.foreignKeys.size(); ++i) {
                final ForeignKeyMetaData fkmd = this.foreignKeys.get(i);
                sb.append(fkmd.toString(prefix + indent, indent));
            }
        }
        if (this.indexes != null) {
            for (int i = 0; i < this.indexes.size(); ++i) {
                final IndexMetaData imd = this.indexes.get(i);
                sb.append(imd.toString(prefix + indent, indent));
            }
        }
        if (this.uniqueConstraints != null) {
            for (int i = 0; i < this.uniqueConstraints.size(); ++i) {
                final UniqueMetaData unimd = this.uniqueConstraints.get(i);
                sb.append(unimd.toString(prefix + indent, indent));
            }
        }
        if (this.members != null) {
            for (int i = 0; i < this.members.size(); ++i) {
                final PropertyMetaData pmd = this.members.get(i);
                sb.append(pmd.toString(prefix + indent, indent));
            }
        }
        if (this.queries != null) {
            for (final QueryMetaData q : this.queries) {
                sb.append(q.toString(prefix + indent, indent));
            }
        }
        if (this.fetchGroups != null) {
            for (final FetchGroupMetaData fgmd : this.fetchGroups) {
                sb.append(fgmd.toString(prefix + indent, indent));
            }
        }
        sb.append(super.toString(prefix + indent, indent));
        sb.append(prefix + "</interface>\n");
        return sb.toString();
    }
}
