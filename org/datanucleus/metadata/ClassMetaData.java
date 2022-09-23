// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

import java.util.ArrayList;
import java.util.HashMap;
import org.datanucleus.exceptions.ClassNotResolvedException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Iterator;
import org.datanucleus.util.ClassUtils;
import java.lang.reflect.Modifier;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.ClassLoaderResolver;
import java.util.List;

public class ClassMetaData extends AbstractClassMetaData
{
    protected List<ImplementsMetaData> implementations;
    protected ImplementsMetaData[] implementsMetaData;
    protected boolean isAbstract;
    
    public ClassMetaData(final PackageMetaData parent, final String name) {
        super(parent, name);
        this.implementations = null;
    }
    
    public ClassMetaData(final InterfaceMetaData imd, final String implClassName, final boolean copyFields) {
        super(imd, implClassName, copyFields);
        this.implementations = null;
    }
    
    public ClassMetaData(final ClassMetaData cmd, final String implClassName) {
        super(cmd, implClassName);
        this.implementations = null;
    }
    
    @Override
    public synchronized void populate(final ClassLoaderResolver clr, final ClassLoader primary, final MetaDataManager mmgr) {
        if (this.isInitialised() || this.isPopulated()) {
            NucleusLogger.METADATA.error(ClassMetaData.LOCALISER.msg("044068", this.name));
            throw new NucleusException(ClassMetaData.LOCALISER.msg("044068", this.fullName)).setFatal();
        }
        if (this.populating) {
            return;
        }
        try {
            if (NucleusLogger.METADATA.isDebugEnabled()) {
                NucleusLogger.METADATA.debug(ClassMetaData.LOCALISER.msg("044075", this.fullName));
            }
            this.populating = true;
            final Class cls = this.loadClass(clr, primary, mmgr);
            this.isAbstract = Modifier.isAbstract(cls.getModifiers());
            if (!this.isMetaDataComplete()) {
                mmgr.addAnnotationsDataToClass(cls, this, clr);
            }
            mmgr.addORMDataToClass(cls, clr);
            if (ClassUtils.isInnerClass(this.fullName) && !Modifier.isStatic(cls.getModifiers()) && this.persistenceModifier == ClassPersistenceModifier.PERSISTENCE_CAPABLE) {
                throw new InvalidClassMetaDataException(ClassMetaData.LOCALISER, "044063", this.fullName);
            }
            if (this.entityName == null) {
                this.entityName = this.name;
            }
            this.determineSuperClassName(clr, cls, mmgr);
            this.inheritIdentity();
            this.determineIdentity();
            this.validateUserInputForIdentity();
            this.addMetaDataForMembersNotInMetaData(cls, mmgr);
            this.validateUserInputForInheritanceMetaData(this.isAbstract());
            this.determineInheritanceMetaData(mmgr);
            this.applyDefaultDiscriminatorValueWhenNotSpecified(mmgr);
            if (this.objectidClass == null) {
                this.populateMemberMetaData(clr, cls, true, primary, mmgr);
                this.determineObjectIdClass(mmgr);
                this.populateMemberMetaData(clr, cls, false, primary, mmgr);
            }
            else {
                this.populateMemberMetaData(clr, cls, true, primary, mmgr);
                this.populateMemberMetaData(clr, cls, false, primary, mmgr);
                this.determineObjectIdClass(mmgr);
            }
            this.validateUnmappedColumns();
            if (this.implementations != null) {
                for (int i = 0; i < this.implementations.size(); ++i) {
                    this.implementations.get(i).populate(clr, primary, mmgr);
                }
            }
            if (this.persistentInterfaceImplNeedingTableFromSuperclass) {
                final AbstractClassMetaData acmd = this.getMetaDataForSuperinterfaceManagingTable(cls, clr, mmgr);
                if (acmd != null) {
                    this.table = acmd.table;
                    this.schema = acmd.schema;
                    this.catalog = acmd.catalog;
                }
                this.persistentInterfaceImplNeedingTableFromSuperclass = false;
            }
            else if (this.persistentInterfaceImplNeedingTableFromSubclass) {
                this.persistentInterfaceImplNeedingTableFromSubclass = false;
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
    
    private AbstractClassMetaData getMetaDataForSuperinterfaceManagingTable(final Class cls, final ClassLoaderResolver clr, final MetaDataManager mmgr) {
        for (final Class<?> superintf : ClassUtils.getSuperinterfaces(cls)) {
            final AbstractClassMetaData acmd = mmgr.getMetaDataForInterface(superintf, clr);
            if (acmd != null && acmd.getInheritanceMetaData() != null) {
                if (acmd.getInheritanceMetaData().getStrategy() == InheritanceStrategy.NEW_TABLE) {
                    return acmd;
                }
                if (acmd.getInheritanceMetaData().getStrategy() == InheritanceStrategy.SUPERCLASS_TABLE) {
                    return this.getMetaDataForSuperinterfaceManagingTable(superintf, clr, mmgr);
                }
                continue;
            }
        }
        return null;
    }
    
    protected void addMetaDataForMembersNotInMetaData(final Class cls, final MetaDataManager mmgr) {
        final String api = mmgr.getNucleusContext().getApiName();
        Collections.sort((List<Comparable>)this.members);
        try {
            boolean hasProperties = false;
            for (int i = 0; i < this.members.size(); ++i) {
                if (this.members.get(i) instanceof PropertyMetaData) {
                    hasProperties = true;
                    break;
                }
            }
            if (hasProperties && api.equalsIgnoreCase("JPA")) {
                final Method[] clsMethods = cls.getDeclaredMethods();
                for (int j = 0; j < clsMethods.length; ++j) {
                    if (clsMethods[j].getDeclaringClass().getName().equals(this.fullName) && ClassUtils.isJavaBeanGetterMethod(clsMethods[j]) && !ClassUtils.isInnerClass(clsMethods[j].getName())) {
                        final String propertyName = ClassUtils.getFieldNameForJavaBeanGetter(clsMethods[j].getName());
                        if (Collections.binarySearch(this.members, propertyName) < 0) {
                            NucleusLogger.METADATA.debug(ClassMetaData.LOCALISER.msg("044060", this.fullName, propertyName));
                            final AbstractMemberMetaData mmd = new PropertyMetaData(this, propertyName);
                            this.members.add(mmd);
                            Collections.sort((List<Comparable>)this.members);
                        }
                    }
                }
            }
            final Field[] clsFields = cls.getDeclaredFields();
            for (int j = 0; j < clsFields.length; ++j) {
                if (clsFields[j].getDeclaringClass().getName().equals(this.fullName) && !clsFields[j].getName().startsWith("jdo") && !ClassUtils.isInnerClass(clsFields[j].getName()) && !Modifier.isStatic(clsFields[j].getModifiers()) && Collections.binarySearch(this.members, clsFields[j].getName()) < 0) {
                    if (hasProperties && api.equalsIgnoreCase("JPA")) {
                        final AbstractMemberMetaData mmd2 = new FieldMetaData(this, clsFields[j].getName());
                        mmd2.setNotPersistent();
                        this.members.add(mmd2);
                        Collections.sort((List<Comparable>)this.members);
                    }
                    else {
                        NucleusLogger.METADATA.debug(ClassMetaData.LOCALISER.msg("044060", this.fullName, clsFields[j].getName()));
                        final AbstractMemberMetaData mmd2 = new FieldMetaData(this, clsFields[j].getName());
                        this.members.add(mmd2);
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
    
    protected void populateMemberMetaData(final ClassLoaderResolver clr, final Class cls, final boolean pkMembers, final ClassLoader primary, final MetaDataManager mmgr) {
        Collections.sort((List<Comparable>)this.members);
        for (final AbstractMemberMetaData mmd : this.members) {
            if (pkMembers == mmd.isPrimaryKey()) {
                Class fieldCls = cls;
                if (mmd.className != null && mmd.className.equals("#UNKNOWN")) {
                    if (this.pcSuperclassMetaData != null) {
                        final AbstractMemberMetaData superFmd = this.pcSuperclassMetaData.getMetaDataForMember(mmd.getName());
                        if (superFmd != null) {
                            if (superFmd.className != null) {
                                mmd.className = superFmd.className;
                            }
                            else {
                                mmd.className = superFmd.getClassName();
                            }
                        }
                    }
                    else {
                        mmd.className = null;
                    }
                }
                if (!mmd.fieldBelongsToClass()) {
                    try {
                        fieldCls = clr.classForName(mmd.getClassName());
                    }
                    catch (ClassNotResolvedException cnre) {
                        final String fieldClassName = this.getPackageName() + "." + mmd.getClassName();
                        try {
                            fieldCls = clr.classForName(fieldClassName);
                            mmd.setClassName(fieldClassName);
                        }
                        catch (ClassNotResolvedException cnre2) {
                            NucleusLogger.METADATA.error(ClassMetaData.LOCALISER.msg("044080", fieldClassName));
                            throw new InvalidClassMetaDataException(ClassMetaData.LOCALISER, "044080", this.fullName, fieldClassName);
                        }
                    }
                }
                boolean populated = false;
                if (mmd instanceof PropertyMetaData) {
                    Method getMethod = null;
                    try {
                        getMethod = fieldCls.getDeclaredMethod(ClassUtils.getJavaBeanGetterName(mmd.getName(), false), (Class[])new Class[0]);
                    }
                    catch (Exception e) {
                        try {
                            getMethod = fieldCls.getDeclaredMethod(ClassUtils.getJavaBeanGetterName(mmd.getName(), true), (Class[])new Class[0]);
                        }
                        catch (Exception ex) {}
                    }
                    if (getMethod == null && mmd.getPersistenceModifier() != FieldPersistenceModifier.NONE) {
                        throw new InvalidClassMetaDataException(ClassMetaData.LOCALISER, "044073", this.fullName, mmd.getName());
                    }
                    Method setMethod = null;
                    try {
                        final String setterName = ClassUtils.getJavaBeanSetterName(mmd.getName());
                        final Method[] methods = fieldCls.getDeclaredMethods();
                        for (int i = 0; i < methods.length; ++i) {
                            if (methods[i].getName().equals(setterName) && methods[i].getParameterTypes() != null && methods[i].getParameterTypes().length == 1) {
                                setMethod = methods[i];
                            }
                        }
                    }
                    catch (Exception ex2) {}
                    if (setMethod == null && mmd.getPersistenceModifier() != FieldPersistenceModifier.NONE) {
                        throw new InvalidClassMetaDataException(ClassMetaData.LOCALISER, "044074", this.fullName, mmd.getName());
                    }
                    if (getMethod != null) {
                        mmd.populate(clr, null, getMethod, primary, mmgr);
                        populated = true;
                    }
                }
                if (!populated) {
                    Field cls_field = null;
                    try {
                        cls_field = fieldCls.getDeclaredField(mmd.getName());
                    }
                    catch (Exception ex3) {}
                    if (cls_field != null) {
                        mmd.populate(clr, cls_field, null, primary, mmgr);
                        populated = true;
                    }
                }
                if (!populated) {
                    throw new InvalidClassMetaDataException(ClassMetaData.LOCALISER, "044071", this.fullName, mmd.getFullFieldName());
                }
                continue;
            }
        }
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
                NucleusLogger.METADATA.debug(ClassMetaData.LOCALISER.msg("044076", this.fullName));
            }
            this.validateObjectIdClass(clr, mmgr);
            Iterator membersIter = this.members.iterator();
            int numManaged = 0;
            int numOverridden = 0;
            while (membersIter.hasNext()) {
                final AbstractMemberMetaData mmd = membersIter.next();
                mmd.initialise(clr, mmgr);
                if (mmd.isFieldToBePersisted()) {
                    if (mmd.fieldBelongsToClass()) {
                        ++numManaged;
                    }
                    else {
                        ++numOverridden;
                    }
                }
            }
            this.managedMembers = new AbstractMemberMetaData[numManaged];
            this.overriddenMembers = new AbstractMemberMetaData[numOverridden];
            membersIter = this.members.iterator();
            int field_id = 0;
            int overridden_field_id = 0;
            this.memberPositionsByName = new HashMap<String, Integer>();
            while (membersIter.hasNext()) {
                final AbstractMemberMetaData mmd2 = membersIter.next();
                if (mmd2.isFieldToBePersisted()) {
                    if (mmd2.fieldBelongsToClass()) {
                        mmd2.setFieldId(field_id);
                        this.managedMembers[field_id] = mmd2;
                        this.memberPositionsByName.put(mmd2.getName(), field_id);
                        ++field_id;
                    }
                    else {
                        this.overriddenMembers[overridden_field_id++] = mmd2;
                        if (this.pcSuperclassMetaData == null) {
                            throw new InvalidClassMetaDataException(ClassMetaData.LOCALISER, "044162", this.fullName, mmd2.getFullFieldName());
                        }
                        final AbstractMemberMetaData superFmd = this.pcSuperclassMetaData.getMemberBeingOverridden(mmd2.getName());
                        if (superFmd == null || !superFmd.isPrimaryKey()) {
                            continue;
                        }
                        mmd2.setPrimaryKey(true);
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
            if (this.implementations != null) {
                this.implementsMetaData = new ImplementsMetaData[this.implementations.size()];
                for (int i = 0; i < this.implementations.size(); ++i) {
                    (this.implementsMetaData[i] = this.implementations.get(i)).initialise(clr, mmgr);
                }
                this.implementations.clear();
                this.implementations = null;
            }
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
                    this.identityMetaData.parent = this;
                }
                else {
                    this.identityMetaData = new IdentityMetaData();
                    this.identityMetaData.parent = this;
                }
            }
            if (this.primaryKeyMetaData != null) {
                this.primaryKeyMetaData.initialise(clr, mmgr);
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
    
    public boolean isAbstract() {
        return this.isAbstract;
    }
    
    @Override
    protected AbstractMemberMetaData newDefaultedProperty(final String name) {
        return new FieldMetaData(this, name);
    }
    
    public final ImplementsMetaData[] getImplementsMetaData() {
        return this.implementsMetaData;
    }
    
    public void addImplements(final ImplementsMetaData implmd) {
        if (implmd == null) {
            return;
        }
        if (this.isInitialised()) {
            throw new RuntimeException("Already initialised");
        }
        if (this.implementations == null) {
            this.implementations = new ArrayList<ImplementsMetaData>();
        }
        this.implementations.add(implmd);
        implmd.parent = this;
    }
    
    @Override
    public String toString(final String prefix, final String indent) {
        final StringBuffer sb = new StringBuffer();
        sb.append(prefix).append("<class name=\"" + this.name + "\"\n");
        if (this.identityType != null) {
            sb.append(prefix).append("       identity-type=\"" + this.identityType + "\"\n");
        }
        if (this.objectidClass != null) {
            sb.append(prefix).append("       objectid-class=\"" + this.objectidClass + "\"\n");
        }
        if (!this.requiresExtent) {
            sb.append(prefix).append("       requires-extent=\"" + this.requiresExtent + "\"\n");
        }
        if (this.embeddedOnly) {
            sb.append(prefix).append("       embedded-only=\"" + this.embeddedOnly + "\"\n");
        }
        if (this.persistenceModifier != null) {
            sb.append(prefix).append("       persistence-modifier=\"" + this.persistenceModifier + "\"\n");
        }
        if (this.catalog != null) {
            sb.append(prefix).append("       catalog=\"" + this.catalog + "\"\n");
        }
        if (this.schema != null) {
            sb.append(prefix).append("       schema=\"" + this.schema + "\"\n");
        }
        if (this.table != null) {
            sb.append(prefix).append("       table=\"" + this.table + "\"\n");
        }
        if (this.detachable) {
            sb.append(prefix).append("       detachable=\"" + this.detachable + "\"\n");
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
        if (this.joinMetaData != null) {
            for (int i = 0; i < this.joinMetaData.length; ++i) {
                sb.append(this.joinMetaData[i].toString(prefix + indent, indent));
            }
        }
        if (this.foreignKeyMetaData != null) {
            for (int i = 0; i < this.foreignKeyMetaData.length; ++i) {
                sb.append(this.foreignKeyMetaData[i].toString(prefix + indent, indent));
            }
        }
        if (this.indexMetaData != null) {
            for (int i = 0; i < this.indexMetaData.length; ++i) {
                sb.append(this.indexMetaData[i].toString(prefix + indent, indent));
            }
        }
        if (this.uniqueMetaData != null) {
            for (int i = 0; i < this.uniqueMetaData.length; ++i) {
                sb.append(this.uniqueMetaData[i].toString(prefix + indent, indent));
            }
        }
        if (this.managedMembers != null) {
            for (int i = 0; i < this.managedMembers.length; ++i) {
                sb.append(this.managedMembers[i].toString(prefix + indent, indent));
            }
        }
        else if (this.members != null && this.members.size() > 0) {
            for (final AbstractMemberMetaData mmd : this.members) {
                sb.append(mmd.toString(prefix + indent, indent));
            }
        }
        if (this.unmappedColumns != null) {
            for (int i = 0; i < this.unmappedColumns.size(); ++i) {
                final ColumnMetaData col = this.unmappedColumns.get(i);
                sb.append(col.toString(prefix + indent, indent));
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
        sb.append(prefix + "</class>\n");
        return sb.toString();
    }
}
