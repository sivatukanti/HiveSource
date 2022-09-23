// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.metadata;

import java.lang.reflect.Method;
import org.datanucleus.metadata.ContainerMetaData;
import javax.jdo.annotations.Discriminator;
import org.datanucleus.metadata.MapMetaData;
import java.util.Map;
import org.datanucleus.metadata.ArrayMetaData;
import org.datanucleus.metadata.CollectionMetaData;
import java.util.Collection;
import org.datanucleus.metadata.OrderMetaData;
import org.datanucleus.metadata.ValueMetaData;
import org.datanucleus.metadata.KeyMetaData;
import org.datanucleus.metadata.EmbeddedMetaData;
import javax.jdo.annotations.Embedded;
import org.datanucleus.metadata.ElementMetaData;
import org.datanucleus.metadata.FieldMetaData;
import org.datanucleus.metadata.PropertyMetaData;
import org.datanucleus.metadata.FieldPersistenceModifier;
import javax.jdo.annotations.PersistenceModifier;
import javax.jdo.annotations.NullValue;
import org.datanucleus.metadata.annotations.Member;
import org.datanucleus.metadata.QueryMetaData;
import javax.jdo.annotations.Query;
import java.util.Iterator;
import org.datanucleus.metadata.AbstractMemberMetaData;
import java.util.HashMap;
import org.datanucleus.metadata.ExtensionMetaData;
import org.datanucleus.metadata.ColumnMetaData;
import org.datanucleus.metadata.ForeignKeyMetaData;
import javax.jdo.annotations.ForeignKey;
import org.datanucleus.metadata.UniqueMetaData;
import javax.jdo.annotations.Unique;
import org.datanucleus.metadata.IndexMetaData;
import java.util.HashSet;
import javax.jdo.annotations.Index;
import org.datanucleus.metadata.SequenceMetaData;
import org.datanucleus.metadata.InvalidClassMetaDataException;
import javax.jdo.annotations.SequenceStrategy;
import org.datanucleus.metadata.FetchGroupMemberMetaData;
import javax.jdo.annotations.FetchGroup;
import org.datanucleus.metadata.FetchGroupMetaData;
import org.datanucleus.metadata.FetchPlanMetaData;
import javax.jdo.annotations.FetchPlan;
import org.datanucleus.metadata.DiscriminatorMetaData;
import javax.jdo.annotations.DiscriminatorStrategy;
import org.datanucleus.metadata.InheritanceMetaData;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.ForeignKeyAction;
import org.datanucleus.metadata.MetaDataUtils;
import org.datanucleus.metadata.JoinMetaData;
import javax.jdo.annotations.Join;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.metadata.PrimaryKeyMetaData;
import org.datanucleus.metadata.IdentityStrategy;
import org.datanucleus.metadata.IdentityMetaData;
import org.datanucleus.util.StringUtils;
import javax.jdo.annotations.IdGeneratorStrategy;
import org.datanucleus.metadata.IndexedValue;
import org.datanucleus.metadata.VersionMetaData;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.VersionStrategy;
import javax.jdo.annotations.Persistent;
import org.datanucleus.metadata.MetaData;
import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdentityType;
import org.datanucleus.metadata.ClassPersistenceModifier;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.metadata.annotations.AnnotationObject;
import org.datanucleus.metadata.PackageMetaData;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.metadata.annotations.AbstractAnnotationReader;

public class JDOAnnotationReader extends AbstractAnnotationReader
{
    public JDOAnnotationReader(final MetaDataManager mgr) {
        super(mgr);
        this.setSupportedAnnotationPackages(new String[] { "javax.jdo", "org.datanucleus" });
    }
    
    @Override
    protected AbstractClassMetaData processClassAnnotations(final PackageMetaData pmd, final Class cls, final AnnotationObject[] annotations, final ClassLoaderResolver clr) {
        AbstractClassMetaData cmd = null;
        if (annotations != null && annotations.length > 0) {
            final AnnotationObject pcAnnotation = this.isClassPersistenceCapable(cls);
            if (pcAnnotation != null) {
                if (cls.isInterface()) {
                    cmd = pmd.newInterfaceMetadata(ClassUtils.getClassNameForClass(cls));
                }
                else {
                    cmd = pmd.newClassMetadata(ClassUtils.getClassNameForClass(cls));
                }
                cmd.setPersistenceModifier(ClassPersistenceModifier.PERSISTENCE_CAPABLE);
                final HashMap<String, Object> annotationValues = pcAnnotation.getNameValueMap();
                cmd.setTable(annotationValues.get("table"));
                cmd.setCatalog(annotationValues.get("catalog"));
                cmd.setSchema(annotationValues.get("schema"));
                final String detachableStr = annotationValues.get("detachable");
                if (this.mgr.getNucleusContext().getPersistenceConfiguration().getBooleanProperty("datanucleus.metadata.alwaysDetachable")) {
                    cmd.setDetachable(true);
                }
                else {
                    cmd.setDetachable(detachableStr);
                }
                cmd.setRequiresExtent(annotationValues.get("requiresExtent"));
                String idClassName = null;
                final Class idClass = annotationValues.get("objectIdClass");
                if (idClass != null && idClass != Void.TYPE) {
                    idClassName = idClass.getName();
                }
                cmd.setObjectIdClass(idClassName);
                cmd.setPersistenceModifier(ClassPersistenceModifier.PERSISTENCE_CAPABLE);
                cmd.setEmbeddedOnly(annotationValues.get("embeddedOnly"));
                final IdentityType idTypeVal = annotationValues.get("identityType");
                final String identityType = JDOAnnotationUtils.getIdentityTypeString(idTypeVal);
                cmd.setIdentityType(org.datanucleus.metadata.IdentityType.getIdentityType(identityType));
                cmd.setCacheable(annotationValues.get("cacheable"));
                final String serializeRead = annotationValues.get("serializeRead");
                if (serializeRead != null) {
                    cmd.setSerializeRead(serializeRead.equals("true"));
                }
                JDOAnnotationUtils.addExtensionsToMetaData(cmd, annotationValues.get("extensions"));
                final Persistent[] members = annotationValues.get("members");
                if (members != null) {
                    for (int j = 0; j < members.length; ++j) {
                        String memberName = members[j].name();
                        if (memberName.indexOf(46) > 0) {
                            memberName = memberName.substring(memberName.lastIndexOf(46) + 1);
                        }
                        final boolean isField = this.isMemberOfClassAField(cls, memberName);
                        final AbstractMemberMetaData fmd = this.getFieldMetaDataForPersistent(cmd, members[j], isField);
                        cmd.addMember(fmd);
                    }
                }
            }
            else if (this.isClassPersistenceAware(cls)) {
                cmd = pmd.newClassMetadata(ClassUtils.getClassNameForClass(cls));
                cmd.setPersistenceModifier(ClassPersistenceModifier.PERSISTENCE_AWARE);
            }
            else {
                if (!this.doesClassHaveNamedQueries(cls)) {
                    return null;
                }
                cmd = pmd.newClassMetadata(ClassUtils.getClassNameForClass(cls));
                cmd.setPersistenceModifier(ClassPersistenceModifier.NON_PERSISTENT);
            }
            this.processNamedQueries(cmd, cls, annotations);
            if (cmd.getPersistenceModifier() != ClassPersistenceModifier.PERSISTENCE_CAPABLE) {
                return cmd;
            }
            InheritanceMetaData inhmd = null;
            DiscriminatorMetaData dismd = null;
            IdentityMetaData idmd = null;
            PrimaryKeyMetaData pkmd = null;
            VersionMetaData vermd = null;
            JoinMetaData[] joins = null;
            FetchPlanMetaData[] fetchPlans = null;
            FetchGroupMetaData[] fetchGroups = null;
            SequenceMetaData seqmd = null;
            String cacheable = null;
            boolean embeddedOnly = false;
            ColumnMetaData[] unmappedColumns = null;
            HashSet<IndexMetaData> indices = null;
            HashSet<UniqueMetaData> uniqueKeys = null;
            HashSet<ForeignKeyMetaData> fks = null;
            HashSet<ExtensionMetaData> extensions = null;
            for (int i = 0; i < annotations.length; ++i) {
                final HashMap<String, Object> annotationValues2 = annotations[i].getNameValueMap();
                final String annName = annotations[i].getName();
                if (annName.equals(JDOAnnotationUtils.EMBEDDED_ONLY)) {
                    embeddedOnly = true;
                }
                else if (annName.equals(JDOAnnotationUtils.VERSION)) {
                    final VersionStrategy versionStrategy = annotationValues2.get("strategy");
                    final String strategy = JDOAnnotationUtils.getVersionStrategyString(versionStrategy);
                    final String indexed = annotationValues2.get("indexed");
                    final String column = annotationValues2.get("column");
                    final Column[] columns = annotationValues2.get("columns");
                    vermd = new VersionMetaData();
                    vermd.setStrategy(strategy);
                    vermd.setColumnName(column);
                    vermd.setIndexed(IndexedValue.getIndexedValue(indexed));
                    if (columns != null && columns.length > 0) {
                        final ColumnMetaData colmd = JDOAnnotationUtils.getColumnMetaDataForColumnAnnotation(columns[0]);
                        vermd.setColumnMetaData(colmd);
                    }
                    JDOAnnotationUtils.addExtensionsToMetaData(vermd, annotationValues2.get("extensions"));
                }
                else if (annName.equals(JDOAnnotationUtils.DATASTORE_IDENTITY)) {
                    String strategy2 = JDOAnnotationUtils.getIdentityStrategyString(annotationValues2.get("strategy"));
                    final String customStrategy = annotationValues2.get("customStrategy");
                    if (!StringUtils.isWhitespace(customStrategy)) {
                        strategy2 = customStrategy;
                    }
                    final String sequence = annotationValues2.get("sequence");
                    final String column = annotationValues2.get("column");
                    final Column[] columns = annotationValues2.get("columns");
                    idmd = new IdentityMetaData();
                    idmd.setColumnName(column);
                    idmd.setValueStrategy(IdentityStrategy.getIdentityStrategy(strategy2));
                    idmd.setSequence(sequence);
                    if (columns != null && columns.length > 0) {
                        final ColumnMetaData colmd = JDOAnnotationUtils.getColumnMetaDataForColumnAnnotation(columns[0]);
                        idmd.setColumnMetaData(colmd);
                    }
                    JDOAnnotationUtils.addExtensionsToMetaData(idmd, annotationValues2.get("extensions"));
                }
                else if (annName.equals(JDOAnnotationUtils.PRIMARY_KEY)) {
                    final String pkName = annotationValues2.get("name");
                    final String pkColumn = annotationValues2.get("column");
                    final Column[] columns2 = annotationValues2.get("columns");
                    pkmd = new PrimaryKeyMetaData();
                    pkmd.setName(pkName);
                    pkmd.setColumnName(pkColumn);
                    if (columns2 != null && columns2.length > 0) {
                        for (int k = 0; k < columns2.length; ++k) {
                            pkmd.addColumn(JDOAnnotationUtils.getColumnMetaDataForColumnAnnotation(columns2[k]));
                        }
                    }
                }
                else if (annName.equals(JDOAnnotationUtils.JOINS)) {
                    if (joins != null) {
                        NucleusLogger.METADATA.warn(JDOAnnotationReader.LOCALISER.msg("044210", cmd.getFullClassName()));
                    }
                    final Join[] js = annotationValues2.get("value");
                    if (js != null && js.length > 0) {
                        joins = new JoinMetaData[js.length];
                        for (int l = 0; l < js.length; ++l) {
                            (joins[l] = new JoinMetaData()).setTable(js[l].table());
                            joins[l].setColumnName(js[l].column());
                            joins[l].setIndexed(IndexedValue.getIndexedValue(js[l].indexed()));
                            joins[l].setOuter(MetaDataUtils.getBooleanForString(js[l].outer(), false));
                            joins[l].setUnique(js[l].unique());
                            joins[l].setDeleteAction(JDOAnnotationUtils.getForeignKeyActionString(js[l].deleteAction()));
                        }
                    }
                }
                else if (annName.equals(JDOAnnotationUtils.JOIN)) {
                    if (joins != null) {
                        NucleusLogger.METADATA.warn(JDOAnnotationReader.LOCALISER.msg("044210", cmd.getFullClassName()));
                    }
                    joins = new JoinMetaData[] { new JoinMetaData() };
                    joins[0].setTable(annotationValues2.get("table"));
                    joins[0].setColumnName(annotationValues2.get("column"));
                    joins[0].setIndexed(IndexedValue.getIndexedValue(annotationValues2.get("indexed")));
                    joins[0].setOuter(MetaDataUtils.getBooleanForString(annotationValues2.get("outer"), false));
                    joins[0].setUnique(annotationValues2.get("unique"));
                    joins[0].setDeleteAction(annotationValues2.get("deleteAction").toString());
                    JDOAnnotationUtils.addExtensionsToMetaData(joins[0], annotationValues2.get("extensions"));
                }
                else if (annName.equals(JDOAnnotationUtils.INHERITANCE)) {
                    String strategy2 = JDOAnnotationUtils.getInheritanceStrategyString(annotationValues2.get("strategy"));
                    final String customStrategy = annotationValues2.get("customStrategy");
                    if (!StringUtils.isWhitespace(customStrategy)) {
                        strategy2 = customStrategy;
                    }
                    inhmd = new InheritanceMetaData();
                    inhmd.setStrategy(strategy2);
                }
                else if (annName.equals(JDOAnnotationUtils.DISCRIMINATOR)) {
                    final DiscriminatorStrategy discriminatorStrategy = annotationValues2.get("strategy");
                    final String strategy = JDOAnnotationUtils.getDiscriminatorStrategyString(discriminatorStrategy);
                    final String column2 = annotationValues2.get("column");
                    final String indexed2 = annotationValues2.get("indexed");
                    final String value = annotationValues2.get("value");
                    final Column[] columns3 = annotationValues2.get("columns");
                    dismd = new DiscriminatorMetaData();
                    dismd.setColumnName(column2);
                    dismd.setValue(value);
                    dismd.setStrategy(strategy);
                    dismd.setIndexed(indexed2);
                    if (columns3 != null && columns3.length > 0) {
                        final ColumnMetaData colmd2 = JDOAnnotationUtils.getColumnMetaDataForColumnAnnotation(columns3[0]);
                        dismd.setColumnMetaData(colmd2);
                    }
                }
                else if (annName.equals(JDOAnnotationUtils.FETCHPLANS)) {
                    if (fetchPlans != null) {
                        NucleusLogger.METADATA.warn(JDOAnnotationReader.LOCALISER.msg("044207", cmd.getFullClassName()));
                    }
                    final FetchPlan[] plans = annotationValues2.get("value");
                    fetchPlans = new FetchPlanMetaData[plans.length];
                    for (int l = 0; l < plans.length; ++l) {
                        (fetchPlans[l] = new FetchPlanMetaData(plans[l].name())).setMaxFetchDepth(plans[l].maxFetchDepth());
                        fetchPlans[l].setFetchSize(plans[l].fetchSize());
                        for (int numGroups = plans[l].fetchGroups().length, m = 0; m < numGroups; ++m) {
                            final FetchGroupMetaData fgmd = new FetchGroupMetaData(plans[l].fetchGroups()[m]);
                            fetchPlans[l].addFetchGroup(fgmd);
                        }
                    }
                }
                else if (annName.equals(JDOAnnotationUtils.FETCHPLAN)) {
                    if (fetchPlans != null) {
                        NucleusLogger.METADATA.warn(JDOAnnotationReader.LOCALISER.msg("044207", cmd.getFullClassName()));
                    }
                    fetchPlans = new FetchPlanMetaData[] { null };
                    final int maxFetchDepth = annotationValues2.get("maxFetchDepth");
                    final int fetchSize = annotationValues2.get("fetchSize");
                    (fetchPlans[0] = new FetchPlanMetaData(annotationValues2.get("name"))).setMaxFetchDepth(maxFetchDepth);
                    fetchPlans[0].setFetchSize(fetchSize);
                }
                else if (annName.equals(JDOAnnotationUtils.FETCHGROUPS)) {
                    if (fetchGroups != null) {
                        NucleusLogger.METADATA.warn(JDOAnnotationReader.LOCALISER.msg("044208", cmd.getFullClassName()));
                    }
                    final FetchGroup[] groups = annotationValues2.get("value");
                    fetchGroups = new FetchGroupMetaData[groups.length];
                    for (int l = 0; l < groups.length; ++l) {
                        fetchGroups[l] = new FetchGroupMetaData(groups[l].name());
                        if (!StringUtils.isWhitespace(groups[l].postLoad())) {
                            fetchGroups[l].setPostLoad(Boolean.valueOf(groups[l].postLoad()));
                        }
                        for (int numFields = groups[l].members().length, m = 0; m < numFields; ++m) {
                            final FetchGroupMemberMetaData fgmmd = new FetchGroupMemberMetaData(fetchGroups[l], groups[l].members()[m].name());
                            fgmmd.setRecursionDepth(groups[l].members()[m].recursionDepth());
                            fetchGroups[l].addMember(fgmmd);
                        }
                        for (int numGroups2 = groups[l].fetchGroups().length, k2 = 0; k2 < numGroups2; ++k2) {
                            final FetchGroupMetaData subgrp = new FetchGroupMetaData(groups[l].fetchGroups()[k2]);
                            fetchGroups[l].addFetchGroup(subgrp);
                        }
                    }
                }
                else if (annName.equals(JDOAnnotationUtils.FETCHGROUP)) {
                    if (fetchGroups != null) {
                        NucleusLogger.METADATA.warn(JDOAnnotationReader.LOCALISER.msg("044208", cmd.getFullClassName()));
                    }
                    fetchGroups = new FetchGroupMetaData[] { new FetchGroupMetaData(annotationValues2.get("name")) };
                    final String postLoadStr = annotationValues2.get("postLoad");
                    if (!StringUtils.isWhitespace(postLoadStr)) {
                        fetchGroups[0].setPostLoad(Boolean.valueOf(postLoadStr));
                    }
                    final Persistent[] fields = annotationValues2.get("members");
                    if (fields != null) {
                        for (int j2 = 0; j2 < fields.length; ++j2) {
                            final FetchGroupMemberMetaData fgmmd2 = new FetchGroupMemberMetaData(fetchGroups[0], fields[j2].name());
                            fgmmd2.setRecursionDepth(fields[j2].recursionDepth());
                            fetchGroups[0].addMember(fgmmd2);
                        }
                    }
                }
                else if (annName.equals(JDOAnnotationUtils.SEQUENCE)) {
                    final String seqName = annotationValues2.get("name");
                    final String seqStrategy = JDOAnnotationUtils.getSequenceStrategyString(annotationValues2.get("strategy"));
                    final String seqSeq = annotationValues2.get("datastoreSequence");
                    final Class seqFactory = annotationValues2.get("factoryClass");
                    String seqFactoryClassName = null;
                    if (seqFactory != null && seqFactory != Void.TYPE) {
                        seqFactoryClassName = seqFactory.getName();
                    }
                    final Integer seqSize = annotationValues2.get("allocationSize");
                    final Integer seqStart = annotationValues2.get("initialValue");
                    if (StringUtils.isWhitespace(seqName)) {
                        throw new InvalidClassMetaDataException(JDOAnnotationReader.LOCALISER, "044155", cmd.getFullClassName());
                    }
                    seqmd = new SequenceMetaData(seqName, seqStrategy);
                    seqmd.setFactoryClass(seqFactoryClassName);
                    seqmd.setDatastoreSequence(seqSeq);
                    if (seqSize != null) {
                        seqmd.setAllocationSize(seqSize);
                    }
                    if (seqStart != null) {
                        seqmd.setInitialValue(seqStart);
                    }
                    JDOAnnotationUtils.addExtensionsToMetaData(seqmd, annotationValues2.get("extensions"));
                }
                else if (annName.equals(JDOAnnotationUtils.INDICES)) {
                    final Index[] values = annotationValues2.get("value");
                    if (values != null && values.length > 0) {
                        indices = new HashSet<IndexMetaData>(values.length);
                        for (int l = 0; l < values.length; ++l) {
                            final IndexMetaData idxmd = JDOAnnotationUtils.getIndexMetaData(values[l].name(), values[l].table(), "" + values[l].unique(), values[l].members(), values[l].columns());
                            if (idxmd.getNumberOfColumns() == 0 && idxmd.getNumberOfMembers() == 0) {
                                NucleusLogger.METADATA.warn(JDOAnnotationReader.LOCALISER.msg("044204", cls.getName()));
                            }
                            else {
                                indices.add(idxmd);
                            }
                        }
                    }
                }
                else if (annName.equals(JDOAnnotationUtils.INDEX)) {
                    final String name = annotationValues2.get("name");
                    final String table = annotationValues2.get("table");
                    final String unique = annotationValues2.get("unique");
                    final String[] members2 = annotationValues2.get("members");
                    final Column[] columns = annotationValues2.get("columns");
                    final IndexMetaData idxmd2 = JDOAnnotationUtils.getIndexMetaData(name, table, unique, members2, columns);
                    if (idxmd2.getNumberOfColumns() == 0 && idxmd2.getNumberOfMembers() == 0) {
                        NucleusLogger.METADATA.warn(JDOAnnotationReader.LOCALISER.msg("044204", cls.getName()));
                    }
                    else {
                        indices = new HashSet<IndexMetaData>(1);
                        indices.add(idxmd2);
                    }
                }
                else if (annName.equals(JDOAnnotationUtils.UNIQUES)) {
                    final Unique[] values2 = annotationValues2.get("value");
                    if (values2 != null && values2.length > 0) {
                        uniqueKeys = new HashSet<UniqueMetaData>(values2.length);
                        for (int l = 0; l < values2.length; ++l) {
                            final UniqueMetaData unimd = JDOAnnotationUtils.getUniqueMetaData(values2[l].name(), values2[l].table(), "" + values2[l].deferred(), values2[l].members(), values2[l].columns());
                            if (unimd.getNumberOfColumns() == 0 && unimd.getNumberOfMembers() == 0) {
                                NucleusLogger.METADATA.warn(JDOAnnotationReader.LOCALISER.msg("044205", cls.getName()));
                            }
                            else {
                                uniqueKeys.add(unimd);
                            }
                        }
                    }
                }
                else if (annName.equals(JDOAnnotationUtils.UNIQUE)) {
                    final String name = annotationValues2.get("name");
                    final String table = annotationValues2.get("table");
                    final String deferred = annotationValues2.get("deferred");
                    final String[] members2 = annotationValues2.get("members");
                    final Column[] columns = annotationValues2.get("columns");
                    final UniqueMetaData unimd2 = JDOAnnotationUtils.getUniqueMetaData(name, table, deferred, members2, columns);
                    if (unimd2.getNumberOfColumns() == 0 && unimd2.getNumberOfMembers() == 0) {
                        NucleusLogger.METADATA.warn(JDOAnnotationReader.LOCALISER.msg("044205", cls.getName()));
                    }
                    else {
                        uniqueKeys = new HashSet<UniqueMetaData>(1);
                        uniqueKeys.add(unimd2);
                    }
                }
                else if (annName.equals(JDOAnnotationUtils.FOREIGNKEYS)) {
                    final ForeignKey[] values3 = annotationValues2.get("value");
                    if (values3 != null && values3.length > 0) {
                        fks = new HashSet<ForeignKeyMetaData>(values3.length);
                        for (int l = 0; l < values3.length; ++l) {
                            final String deleteAction = JDOAnnotationUtils.getForeignKeyActionString(values3[l].deleteAction());
                            final String updateAction = JDOAnnotationUtils.getForeignKeyActionString(values3[l].updateAction());
                            final ForeignKeyMetaData fkmd = JDOAnnotationUtils.getFKMetaData(values3[l].name(), values3[l].table(), values3[l].unique(), "" + values3[l].deferred(), deleteAction, updateAction, values3[l].members(), values3[l].columns());
                            if (fkmd.getNumberOfColumns() == 0 && fkmd.getNumberOfMembers() == 0) {
                                NucleusLogger.METADATA.warn(JDOAnnotationReader.LOCALISER.msg("044206", cls.getName()));
                            }
                            else {
                                fks.add(fkmd);
                            }
                        }
                    }
                }
                else if (annName.equals(JDOAnnotationUtils.FOREIGNKEY)) {
                    final String name = annotationValues2.get("name");
                    final String table = annotationValues2.get("table");
                    final String unique = annotationValues2.get("unique");
                    final String deferred2 = annotationValues2.get("deferred");
                    final String deleteAction2 = JDOAnnotationUtils.getForeignKeyActionString(annotationValues2.get("deleteAction"));
                    final String updateAction2 = JDOAnnotationUtils.getForeignKeyActionString(annotationValues2.get("updateAction"));
                    final String[] members3 = annotationValues2.get("members");
                    final Column[] columns4 = annotationValues2.get("columns");
                    final ForeignKeyMetaData fkmd2 = JDOAnnotationUtils.getFKMetaData(name, table, unique, deferred2, deleteAction2, updateAction2, members3, columns4);
                    if (fkmd2.getNumberOfColumns() == 0 && fkmd2.getNumberOfMembers() == 0) {
                        NucleusLogger.METADATA.warn(JDOAnnotationReader.LOCALISER.msg("044206", cls.getName()));
                    }
                    else {
                        fks = new HashSet<ForeignKeyMetaData>(1);
                        fks.add(fkmd2);
                    }
                }
                else if (annName.equals(JDOAnnotationUtils.COLUMNS)) {
                    final Column[] cols = annotationValues2.get("value");
                    if (cols != null && cols.length > 0) {
                        unmappedColumns = new ColumnMetaData[cols.length];
                        for (int l = 0; l < cols.length; ++l) {
                            JDOAnnotationUtils.addExtensionsToMetaData(unmappedColumns[l] = JDOAnnotationUtils.getColumnMetaDataForColumnAnnotation(cols[l]), cols[l].extensions());
                        }
                    }
                }
                else if (annName.equals("javax.jdo.annotations.Cacheable")) {
                    final String cache = annotationValues2.get("value");
                    if (cache != null) {
                        cacheable = cache;
                    }
                }
                else if (annName.equals(JDOAnnotationUtils.EXTENSIONS)) {
                    final Extension[] values4 = annotationValues2.get("value");
                    if (values4 != null && values4.length > 0) {
                        extensions = new HashSet<ExtensionMetaData>(values4.length);
                        for (int l = 0; l < values4.length; ++l) {
                            final ExtensionMetaData extmd = new ExtensionMetaData(values4[l].vendorName(), values4[l].key().toString(), values4[l].value().toString());
                            extensions.add(extmd);
                        }
                    }
                }
                else if (annName.equals(JDOAnnotationUtils.EXTENSION)) {
                    final ExtensionMetaData extmd2 = new ExtensionMetaData(annotationValues2.get("vendorName"), annotationValues2.get("key"), annotationValues2.get("value"));
                    extensions = new HashSet<ExtensionMetaData>(1);
                    extensions.add(extmd2);
                }
                else if (!annName.equals(JDOAnnotationUtils.PERSISTENCE_CAPABLE) && !annName.equals(JDOAnnotationUtils.PERSISTENCE_AWARE) && !annName.equals(JDOAnnotationUtils.QUERIES) && !annName.equals(JDOAnnotationUtils.QUERY)) {
                    NucleusLogger.METADATA.debug(JDOAnnotationReader.LOCALISER.msg("044203", cls.getName(), annotations[i].getName()));
                }
            }
            NucleusLogger.METADATA.debug(JDOAnnotationReader.LOCALISER.msg("044200", cls.getName(), "JDO"));
            if (embeddedOnly) {
                cmd.setEmbeddedOnly(true);
            }
            if (idmd != null) {
                idmd.setParent(cmd);
                cmd.setIdentityMetaData(idmd);
            }
            if (pkmd != null) {
                pkmd.setParent(cmd);
                cmd.setPrimaryKeyMetaData(pkmd);
            }
            if (vermd != null) {
                vermd.setParent(cmd);
                cmd.setVersionMetaData(vermd);
            }
            if (inhmd != null) {
                if (dismd != null) {
                    inhmd.setDiscriminatorMetaData(dismd);
                }
                inhmd.setParent(cmd);
                cmd.setInheritanceMetaData(inhmd);
            }
            else if (dismd != null) {
                inhmd = new InheritanceMetaData();
                inhmd.setDiscriminatorMetaData(dismd);
                cmd.setInheritanceMetaData(inhmd);
            }
            if (joins != null && joins.length > 0) {
                for (int i = 0; i < joins.length; ++i) {
                    cmd.addJoin(joins[i]);
                }
            }
            if (fetchGroups != null && fetchGroups.length > 0) {
                for (int i = 0; i < fetchGroups.length; ++i) {
                    fetchGroups[i].setParent(cmd);
                    cmd.addFetchGroup(fetchGroups[i]);
                }
            }
            if (seqmd != null) {
                cmd.getPackageMetaData().addSequence(seqmd);
            }
            if (indices != null) {
                for (final IndexMetaData idxmd3 : indices) {
                    idxmd3.setParent(cmd);
                    cmd.addIndex(idxmd3);
                }
            }
            if (uniqueKeys != null) {
                for (final UniqueMetaData unimd3 : uniqueKeys) {
                    unimd3.setParent(cmd);
                    cmd.addUniqueConstraint(unimd3);
                }
            }
            if (fks != null) {
                for (final ForeignKeyMetaData fkmd3 : fks) {
                    fkmd3.setParent(cmd);
                    cmd.addForeignKey(fkmd3);
                }
            }
            if (unmappedColumns != null) {
                for (int i = 0; i < unmappedColumns.length; ++i) {
                    final ColumnMetaData colmd3 = unmappedColumns[i];
                    colmd3.setParent(cmd);
                    cmd.addUnmappedColumn(colmd3);
                }
            }
            if (cacheable != null && cacheable.equalsIgnoreCase("false")) {
                cmd.setCacheable(false);
            }
            if (extensions != null) {
                for (final ExtensionMetaData extmd3 : extensions) {
                    cmd.addExtension(extmd3.getVendorName(), extmd3.getKey(), extmd3.getValue());
                }
            }
        }
        return cmd;
    }
    
    protected void processNamedQueries(final AbstractClassMetaData cmd, final Class cls, final AnnotationObject[] annotations) {
        QueryMetaData[] queries = null;
        for (int i = 0; i < annotations.length; ++i) {
            final HashMap<String, Object> annotationValues = annotations[i].getNameValueMap();
            final String annName = annotations[i].getName();
            if (annName.equals(JDOAnnotationUtils.QUERIES)) {
                if (queries != null) {
                    NucleusLogger.METADATA.warn(JDOAnnotationReader.LOCALISER.msg("044209", cmd.getFullClassName()));
                }
                final Query[] qs = annotationValues.get("value");
                queries = new QueryMetaData[qs.length];
                for (int j = 0; j < queries.length; ++j) {
                    final String lang = JDOAnnotationUtils.getQueryLanguageName(qs[j].language());
                    final String resultClassName = (qs[j].resultClass() != null && qs[j].resultClass() != Void.TYPE) ? qs[j].resultClass().getName() : null;
                    if (StringUtils.isWhitespace(qs[j].name())) {
                        throw new InvalidClassMetaDataException(JDOAnnotationReader.LOCALISER, "044154", cmd.getFullClassName());
                    }
                    (queries[j] = new QueryMetaData(qs[j].name())).setScope(cls.getName());
                    queries[j].setLanguage(lang);
                    queries[j].setUnmodifiable(qs[j].unmodifiable());
                    queries[j].setResultClass(resultClassName);
                    queries[j].setUnique(qs[j].unique());
                    queries[j].setFetchPlanName(qs[j].fetchPlan());
                    queries[j].setQuery(qs[j].value());
                    JDOAnnotationUtils.addExtensionsToMetaData(queries[j], qs[j].extensions());
                }
            }
            else if (annName.equals(JDOAnnotationUtils.QUERY)) {
                if (queries != null) {
                    NucleusLogger.METADATA.warn(JDOAnnotationReader.LOCALISER.msg("044209", cmd.getFullClassName()));
                }
                queries = new QueryMetaData[] { null };
                final String unmodifiable = "" + annotationValues.get("unmodifiable");
                final Class resultClassValue = annotationValues.get("resultClass");
                final String resultClassName2 = (resultClassValue != null && resultClassValue != Void.TYPE) ? resultClassValue.getName() : null;
                final String lang2 = JDOAnnotationUtils.getQueryLanguageName(annotationValues.get("language"));
                if (StringUtils.isWhitespace(annotationValues.get("name"))) {
                    throw new InvalidClassMetaDataException(JDOAnnotationReader.LOCALISER, "044154", cmd.getFullClassName());
                }
                (queries[0] = new QueryMetaData(annotationValues.get("name"))).setScope(cls.getName());
                queries[0].setLanguage(lang2);
                queries[0].setUnmodifiable(unmodifiable);
                queries[0].setResultClass(resultClassName2);
                queries[0].setUnique(annotationValues.get("unique"));
                queries[0].setFetchPlanName(annotationValues.get("fetchPlan"));
                queries[0].setQuery(annotationValues.get("value"));
                JDOAnnotationUtils.addExtensionsToMetaData(queries[0], annotationValues.get("extensions"));
            }
        }
        if (queries != null && queries.length > 0) {
            for (int i = 0; i < queries.length; ++i) {
                queries[i].setParent(cmd);
                cmd.addQuery(queries[i]);
            }
        }
    }
    
    @Override
    protected AbstractMemberMetaData processMemberAnnotations(final AbstractClassMetaData cmd, final Member member, final AnnotationObject[] annotations, final boolean propertyAccessor) {
        AbstractMemberMetaData mmd = null;
        if (annotations != null && annotations.length > 0) {
            boolean primaryKey = false;
            boolean serialised = false;
            boolean nonPersistentField = false;
            boolean transactionalField = false;
            String cacheable = null;
            Class[] elementTypes = null;
            String embeddedElement = null;
            String serializedElement = null;
            String dependentElement = null;
            Class keyType = null;
            String embeddedKey = null;
            String serializedKey = null;
            String dependentKey = null;
            Class valueType = null;
            String embeddedValue = null;
            String serializedValue = null;
            String dependentValue = null;
            String embeddedOwnerField = null;
            String embeddedNullIndicatorColumn = null;
            String embeddedNullIndicatorValue = null;
            Persistent[] embeddedMembers = null;
            Persistent[] embeddedElementMembers = null;
            Persistent[] embeddedKeyMembers = null;
            Persistent[] embeddedValueMembers = null;
            ColumnMetaData[] colmds = null;
            JoinMetaData joinmd = null;
            ElementMetaData elemmd = null;
            KeyMetaData keymd = null;
            ValueMetaData valuemd = null;
            OrderMetaData ordermd = null;
            IndexMetaData idxmd = null;
            UniqueMetaData unimd = null;
            ForeignKeyMetaData fkmd = null;
            HashSet<ExtensionMetaData> extensions = null;
            for (int i = 0; i < annotations.length; ++i) {
                final String annName = annotations[i].getName();
                final HashMap<String, Object> annotationValues = annotations[i].getNameValueMap();
                if (annName.equals(JDOAnnotationUtils.PERSISTENT)) {
                    final String pkStr = "" + annotationValues.get("primaryKey");
                    Boolean pk = null;
                    if (!StringUtils.isWhitespace(pkStr)) {
                        pk = Boolean.valueOf(pkStr);
                    }
                    final String dfgStr = annotationValues.get("defaultFetchGroup");
                    Boolean dfg = null;
                    if (!StringUtils.isWhitespace(dfgStr)) {
                        dfg = Boolean.valueOf(dfgStr);
                    }
                    final String nullValue = JDOAnnotationUtils.getNullValueString(annotationValues.get("nullValue"));
                    final String embStr = annotationValues.get("embedded");
                    Boolean embedded = null;
                    if (!StringUtils.isWhitespace(embStr)) {
                        embedded = Boolean.valueOf(embStr);
                    }
                    final String serStr = annotationValues.get("serialized");
                    Boolean serialized = null;
                    if (!StringUtils.isWhitespace(serStr)) {
                        serialized = Boolean.valueOf(serStr);
                    }
                    final String depStr = annotationValues.get("dependent");
                    Boolean dependent = null;
                    if (!StringUtils.isWhitespace(depStr)) {
                        dependent = Boolean.valueOf(depStr);
                    }
                    String valueStrategy = JDOAnnotationUtils.getIdentityStrategyString(annotationValues.get("valueStrategy"));
                    final String customValueStrategy = annotationValues.get("customValueStrategy");
                    if (!StringUtils.isWhitespace(customValueStrategy)) {
                        valueStrategy = customValueStrategy;
                    }
                    FieldPersistenceModifier modifier = JDOAnnotationUtils.getFieldPersistenceModifier(annotationValues.get("persistenceModifier"));
                    if (modifier == null) {
                        modifier = FieldPersistenceModifier.PERSISTENT;
                    }
                    final String sequence = annotationValues.get("sequence");
                    final String mappedBy = annotationValues.get("mappedBy");
                    final String table = annotationValues.get("table");
                    final String column = annotationValues.get("column");
                    final String loadFetchGroup = annotationValues.get("loadFetchGroup");
                    String fieldTypeName = null;
                    final int recursionDepth = annotationValues.get("recursionDepth");
                    cacheable = annotationValues.get("cacheable");
                    final Class[] fieldTypes = annotationValues.get("types");
                    if (fieldTypes != null && fieldTypes.length > 0) {
                        final StringBuilder typeStr = new StringBuilder();
                        for (int j = 0; j < fieldTypes.length; ++j) {
                            if (typeStr.length() > 0) {
                                typeStr.append(',');
                            }
                            if (fieldTypes[j] != null && fieldTypes[j] != Void.TYPE) {
                                typeStr.append(fieldTypes[j].getName());
                            }
                        }
                        fieldTypeName = typeStr.toString();
                    }
                    dependentElement = annotationValues.get("dependentElement");
                    serializedElement = annotationValues.get("serializedElement");
                    embeddedElement = annotationValues.get("embeddedElement");
                    dependentKey = annotationValues.get("dependentKey");
                    serializedKey = annotationValues.get("serializedKey");
                    embeddedKey = annotationValues.get("embeddedKey");
                    dependentValue = annotationValues.get("dependentValue");
                    serializedValue = annotationValues.get("serializedValue");
                    embeddedValue = annotationValues.get("embeddedValue");
                    if (member.isProperty()) {
                        mmd = new PropertyMetaData(cmd, member.getName());
                    }
                    else {
                        mmd = new FieldMetaData(cmd, member.getName());
                    }
                    if (modifier != null) {
                        mmd.setPersistenceModifier(modifier);
                    }
                    if (dfg != null) {
                        mmd.setDefaultFetchGroup(dfg);
                    }
                    if (pk != null) {
                        mmd.setPrimaryKey(pk);
                    }
                    if (embedded != null) {
                        mmd.setEmbedded(embedded);
                    }
                    if (serialized != null) {
                        mmd.setSerialised(serialized);
                    }
                    if (dependent != null) {
                        mmd.setDependent(dependent);
                    }
                    mmd.setNullValue(org.datanucleus.metadata.NullValue.getNullValue(nullValue));
                    mmd.setMappedBy(mappedBy);
                    mmd.setColumn(column);
                    mmd.setTable(table);
                    mmd.setRecursionDepth(recursionDepth);
                    mmd.setLoadFetchGroup(loadFetchGroup);
                    mmd.setValueStrategy(valueStrategy);
                    mmd.setSequence(sequence);
                    mmd.setFieldTypes(fieldTypeName);
                    final Column[] columns = annotationValues.get("columns");
                    if (columns != null && columns.length > 0) {
                        for (int j = 0; j < columns.length; ++j) {
                            mmd.addColumn(JDOAnnotationUtils.getColumnMetaDataForColumnAnnotation(columns[j]));
                        }
                    }
                    JDOAnnotationUtils.addExtensionsToMetaData(mmd, annotationValues.get("extensions"));
                }
                else if (annName.equals(JDOAnnotationUtils.PRIMARY_KEY)) {
                    primaryKey = true;
                    if (cmd.getIdentityType() == org.datanucleus.metadata.IdentityType.DATASTORE) {
                        cmd.setIdentityType(org.datanucleus.metadata.IdentityType.APPLICATION);
                    }
                }
                else if (annName.equals(JDOAnnotationUtils.SERIALIZED)) {
                    serialised = true;
                }
                else if (annName.equals(JDOAnnotationUtils.NOTPERSISTENT)) {
                    nonPersistentField = true;
                }
                else if (annName.equals(JDOAnnotationUtils.TRANSACTIONAL)) {
                    transactionalField = true;
                }
                else if (annName.equals(JDOAnnotationUtils.COLUMNS)) {
                    final Column[] cols = annotationValues.get("value");
                    if (cols != null && cols.length > 0) {
                        colmds = new ColumnMetaData[cols.length];
                        for (int k = 0; k < cols.length; ++k) {
                            JDOAnnotationUtils.addExtensionsToMetaData(colmds[k] = JDOAnnotationUtils.getColumnMetaDataForColumnAnnotation(cols[k]), cols[k].extensions());
                        }
                    }
                }
                else if (annName.equals(JDOAnnotationUtils.COLUMN)) {
                    colmds = new ColumnMetaData[] { JDOAnnotationUtils.getColumnMetaDataForAnnotations(annotationValues) };
                    JDOAnnotationUtils.addExtensionsToMetaData(colmds[0], annotationValues.get("extensions"));
                }
                else if (annName.equals(JDOAnnotationUtils.JOIN)) {
                    final String joinColumn = annotationValues.get("column");
                    final String joinOuter = annotationValues.get("outer");
                    final String deleteAction = JDOAnnotationUtils.getForeignKeyActionString(annotationValues.get("deleteAction"));
                    final String pkName = annotationValues.get("primaryKey");
                    final String fkName = annotationValues.get("foreignKey");
                    final String generateFK = annotationValues.get("generateForeignKey");
                    String indexed = annotationValues.get("indexed");
                    final String indexName = annotationValues.get("index");
                    String unique = annotationValues.get("unique");
                    final String uniqueName = annotationValues.get("uniqueKey");
                    final String generatePK = annotationValues.get("generatePrimaryKey");
                    if (!StringUtils.isWhitespace(uniqueName)) {
                        unique = "true";
                    }
                    if (!StringUtils.isWhitespace(indexName)) {
                        indexed = "true";
                    }
                    final Column[] joinColumns = annotationValues.get("columns");
                    joinmd = new JoinMetaData();
                    joinmd.setColumnName(joinColumn);
                    joinmd.setOuter(MetaDataUtils.getBooleanForString(joinOuter, false));
                    joinmd.setIndexed(IndexedValue.getIndexedValue(indexed));
                    joinmd.setUnique(unique);
                    joinmd.setDeleteAction(deleteAction);
                    if (!StringUtils.isWhitespace(pkName)) {
                        final PrimaryKeyMetaData pkmd = new PrimaryKeyMetaData();
                        pkmd.setName(pkName);
                        joinmd.setPrimaryKeyMetaData(pkmd);
                    }
                    else if (generatePK != null && generatePK.equalsIgnoreCase("true")) {
                        joinmd.setPrimaryKeyMetaData(new PrimaryKeyMetaData());
                    }
                    if (!StringUtils.isWhitespace(fkName)) {
                        ForeignKeyMetaData joinFkmd = joinmd.getForeignKeyMetaData();
                        if (joinFkmd == null) {
                            joinFkmd = new ForeignKeyMetaData();
                            joinFkmd.setName(fkName);
                            joinmd.setForeignKeyMetaData(joinFkmd);
                        }
                        else {
                            joinFkmd.setName(fkName);
                        }
                    }
                    else if (generateFK != null && generateFK.equalsIgnoreCase("true")) {
                        joinmd.setForeignKeyMetaData(new ForeignKeyMetaData());
                    }
                    if (!StringUtils.isWhitespace(indexName)) {
                        IndexMetaData joinIdxmd = joinmd.getIndexMetaData();
                        if (joinIdxmd == null) {
                            joinIdxmd = new IndexMetaData();
                            joinmd.setIndexMetaData(joinIdxmd);
                        }
                        joinIdxmd.setName(indexName);
                    }
                    if (!StringUtils.isWhitespace(uniqueName)) {
                        UniqueMetaData joinUnimd = joinmd.getUniqueMetaData();
                        if (joinUnimd == null) {
                            joinUnimd = new UniqueMetaData();
                            joinmd.setUniqueMetaData(joinUnimd);
                        }
                        joinUnimd.setName(uniqueName);
                    }
                    if (joinColumns != null && joinColumns.length > 0) {
                        for (int l = 0; l < joinColumns.length; ++l) {
                            joinmd.addColumn(JDOAnnotationUtils.getColumnMetaDataForColumnAnnotation(joinColumns[l]));
                        }
                    }
                    JDOAnnotationUtils.addExtensionsToMetaData(joinmd, annotationValues.get("extensions"));
                }
                else if (annName.equals(JDOAnnotationUtils.ELEMENT)) {
                    elementTypes = annotationValues.get("types");
                    embeddedElement = annotationValues.get("embedded");
                    serializedElement = annotationValues.get("serialized");
                    dependentElement = annotationValues.get("dependent");
                    final String elementColumn = annotationValues.get("column");
                    final String elementDeleteAction = JDOAnnotationUtils.getForeignKeyActionString(annotationValues.get("deleteAction"));
                    final String elementUpdateAction = JDOAnnotationUtils.getForeignKeyActionString(annotationValues.get("updateAction"));
                    final String elementMappedBy = annotationValues.get("mappedBy");
                    final Column[] elementColumns = annotationValues.get("columns");
                    final String fkName2 = annotationValues.get("foreignKey");
                    final String generateFK2 = annotationValues.get("generateForeignKey");
                    String indexed2 = annotationValues.get("indexed");
                    final String indexName2 = annotationValues.get("index");
                    String unique2 = annotationValues.get("unique");
                    final String uniqueName2 = annotationValues.get("uniqueKey");
                    if (!StringUtils.isWhitespace(uniqueName2)) {
                        unique2 = "true";
                    }
                    if (!StringUtils.isWhitespace(indexName2)) {
                        indexed2 = "true";
                    }
                    elemmd = new ElementMetaData();
                    elemmd.setColumnName(elementColumn);
                    elemmd.setDeleteAction(elementDeleteAction);
                    elemmd.setUpdateAction(elementUpdateAction);
                    elemmd.setIndexed(IndexedValue.getIndexedValue(indexed2));
                    elemmd.setUnique(MetaDataUtils.getBooleanForString(unique2, false));
                    elemmd.setMappedBy(elementMappedBy);
                    if (!StringUtils.isWhitespace(fkName2)) {
                        ForeignKeyMetaData elemFkmd = elemmd.getForeignKeyMetaData();
                        if (elemFkmd == null) {
                            elemFkmd = new ForeignKeyMetaData();
                            elemFkmd.setName(fkName2);
                            elemmd.setForeignKeyMetaData(elemFkmd);
                        }
                        else {
                            elemFkmd.setName(fkName2);
                        }
                    }
                    else if (generateFK2 != null && generateFK2.equalsIgnoreCase("true")) {
                        elemmd.setForeignKeyMetaData(new ForeignKeyMetaData());
                    }
                    if (!StringUtils.isWhitespace(indexName2)) {
                        IndexMetaData elemIdxmd = elemmd.getIndexMetaData();
                        if (elemIdxmd == null) {
                            elemIdxmd = new IndexMetaData();
                            elemmd.setIndexMetaData(elemIdxmd);
                        }
                        elemIdxmd.setName(indexName2);
                    }
                    if (!StringUtils.isWhitespace(uniqueName2)) {
                        UniqueMetaData elemUnimd = elemmd.getUniqueMetaData();
                        if (elemUnimd == null) {
                            elemUnimd = new UniqueMetaData();
                            elemmd.setUniqueMetaData(elemUnimd);
                        }
                        elemUnimd.setName(uniqueName2);
                    }
                    if (elementColumns != null && elementColumns.length > 0) {
                        for (int m = 0; m < elementColumns.length; ++m) {
                            elemmd.addColumn(JDOAnnotationUtils.getColumnMetaDataForColumnAnnotation(elementColumns[m]));
                        }
                    }
                    JDOAnnotationUtils.addExtensionsToMetaData(elemmd, annotationValues.get("extensions"));
                    final Embedded[] embeddedMappings = annotationValues.get("embeddedMapping");
                    if (embeddedMappings != null && embeddedMappings.length > 0) {
                        final EmbeddedMetaData embmd = new EmbeddedMetaData();
                        embmd.setOwnerMember(embeddedMappings[0].ownerMember());
                        embmd.setNullIndicatorColumn(embeddedMappings[0].nullIndicatorColumn());
                        embmd.setNullIndicatorValue(embeddedMappings[0].nullIndicatorValue());
                        try {
                            final Discriminator disc = embeddedMappings[0].discriminatorColumnName();
                            if (disc != null) {
                                final DiscriminatorMetaData dismd = embmd.newDiscriminatorMetadata();
                                dismd.setColumnName(disc.column());
                                dismd.setStrategy(JDOAnnotationUtils.getDiscriminatorStrategyString(disc.strategy()));
                            }
                        }
                        catch (Throwable t) {}
                        elemmd.setEmbeddedMetaData(embmd);
                        embeddedElementMembers = embeddedMappings[0].members();
                    }
                }
                else if (annName.equals(JDOAnnotationUtils.KEY)) {
                    final Class[] keyTypes = annotationValues.get("types");
                    if (keyTypes != null && keyTypes.length > 0) {
                        keyType = keyTypes[0];
                    }
                    embeddedKey = annotationValues.get("embedded");
                    serializedKey = annotationValues.get("serialized");
                    dependentKey = annotationValues.get("dependent");
                    final String keyColumn = annotationValues.get("column");
                    final String keyDeleteAction = JDOAnnotationUtils.getForeignKeyActionString(annotationValues.get("deleteAction"));
                    final String keyUpdateAction = JDOAnnotationUtils.getForeignKeyActionString(annotationValues.get("updateAction"));
                    final String keyMappedBy = annotationValues.get("mappedBy");
                    final Column[] keyColumns = annotationValues.get("columns");
                    final String fkName3 = annotationValues.get("foreignKey");
                    final String generateFK3 = annotationValues.get("generateForeignKey");
                    String indexed3 = annotationValues.get("indexed");
                    final String indexName3 = annotationValues.get("index");
                    String unique3 = annotationValues.get("unique");
                    final String uniqueName3 = annotationValues.get("uniqueKey");
                    if (!StringUtils.isWhitespace(uniqueName3)) {
                        unique3 = "true";
                    }
                    if (!StringUtils.isWhitespace(indexName3)) {
                        indexed3 = "true";
                    }
                    keymd = new KeyMetaData();
                    keymd.setColumnName(keyColumn);
                    keymd.setDeleteAction(keyDeleteAction);
                    keymd.setUpdateAction(keyUpdateAction);
                    keymd.setIndexed(IndexedValue.getIndexedValue(indexed3));
                    keymd.setUnique(MetaDataUtils.getBooleanForString(unique3, false));
                    keymd.setMappedBy(keyMappedBy);
                    if (!StringUtils.isWhitespace(fkName3)) {
                        ForeignKeyMetaData keyFkmd = keymd.getForeignKeyMetaData();
                        if (keyFkmd == null) {
                            keyFkmd = new ForeignKeyMetaData();
                            keyFkmd.setName(fkName3);
                            keymd.setForeignKeyMetaData(keyFkmd);
                        }
                        else {
                            keyFkmd.setName(fkName3);
                        }
                    }
                    else if (generateFK3 != null && generateFK3.equalsIgnoreCase("true")) {
                        keymd.setForeignKeyMetaData(new ForeignKeyMetaData());
                    }
                    if (!StringUtils.isWhitespace(indexName3)) {
                        IndexMetaData keyIdxmd = keymd.getIndexMetaData();
                        if (keyIdxmd == null) {
                            keyIdxmd = new IndexMetaData();
                            keymd.setIndexMetaData(keyIdxmd);
                        }
                        keyIdxmd.setName(indexName3);
                    }
                    if (!StringUtils.isWhitespace(uniqueName3)) {
                        UniqueMetaData keyUnimd = keymd.getUniqueMetaData();
                        if (keyUnimd == null) {
                            keyUnimd = new UniqueMetaData();
                            keymd.setUniqueMetaData(keyUnimd);
                        }
                        keyUnimd.setName(uniqueName3);
                    }
                    if (keyColumns != null && keyColumns.length > 0) {
                        for (int l = 0; l < keyColumns.length; ++l) {
                            keymd.addColumn(JDOAnnotationUtils.getColumnMetaDataForColumnAnnotation(keyColumns[l]));
                        }
                    }
                    JDOAnnotationUtils.addExtensionsToMetaData(keymd, annotationValues.get("extensions"));
                    final Embedded[] embeddedMappings2 = annotationValues.get("embeddedMapping");
                    if (embeddedMappings2 != null && embeddedMappings2.length > 0) {
                        final EmbeddedMetaData embmd2 = new EmbeddedMetaData();
                        embmd2.setOwnerMember(embeddedMappings2[0].ownerMember());
                        embmd2.setNullIndicatorColumn(embeddedMappings2[0].nullIndicatorColumn());
                        embmd2.setNullIndicatorValue(embeddedMappings2[0].nullIndicatorValue());
                        keymd.setEmbeddedMetaData(embmd2);
                        embeddedKeyMembers = embeddedMappings2[0].members();
                    }
                }
                else if (annName.equals(JDOAnnotationUtils.VALUE)) {
                    final Class[] valueTypes = annotationValues.get("types");
                    if (valueTypes != null && valueTypes.length > 0) {
                        valueType = valueTypes[0];
                    }
                    embeddedValue = annotationValues.get("embedded");
                    serializedValue = annotationValues.get("serialized");
                    dependentValue = annotationValues.get("dependent");
                    final String valueColumn = annotationValues.get("column");
                    final String valueDeleteAction = JDOAnnotationUtils.getForeignKeyActionString(annotationValues.get("deleteAction"));
                    final String valueUpdateAction = JDOAnnotationUtils.getForeignKeyActionString(annotationValues.get("updateAction"));
                    final String valueMappedBy = annotationValues.get("mappedBy");
                    final Column[] valueColumns = annotationValues.get("columns");
                    final String fkName3 = annotationValues.get("foreignKey");
                    final String generateFK3 = annotationValues.get("generateForeignKey");
                    String indexed3 = annotationValues.get("indexed");
                    final String indexName3 = annotationValues.get("index");
                    String unique3 = annotationValues.get("unique");
                    final String uniqueName3 = annotationValues.get("uniqueKey");
                    if (!StringUtils.isWhitespace(uniqueName3)) {
                        unique3 = "true";
                    }
                    if (!StringUtils.isWhitespace(indexName3)) {
                        indexed3 = "true";
                    }
                    valuemd = new ValueMetaData();
                    valuemd.setColumnName(valueColumn);
                    valuemd.setDeleteAction(valueDeleteAction);
                    valuemd.setUpdateAction(valueUpdateAction);
                    valuemd.setIndexed(IndexedValue.getIndexedValue(indexed3));
                    valuemd.setUnique(MetaDataUtils.getBooleanForString(unique3, false));
                    valuemd.setMappedBy(valueMappedBy);
                    if (!StringUtils.isWhitespace(fkName3)) {
                        ForeignKeyMetaData valueFkmd = valuemd.getForeignKeyMetaData();
                        if (valueFkmd == null) {
                            valueFkmd = new ForeignKeyMetaData();
                            valueFkmd.setName(fkName3);
                            valuemd.setForeignKeyMetaData(valueFkmd);
                        }
                        else {
                            valueFkmd.setName(fkName3);
                        }
                    }
                    else if (generateFK3 != null && generateFK3.equalsIgnoreCase("true")) {
                        valuemd.setForeignKeyMetaData(new ForeignKeyMetaData());
                    }
                    if (!StringUtils.isWhitespace(indexName3)) {
                        IndexMetaData valueIdxmd = valuemd.getIndexMetaData();
                        if (valueIdxmd == null) {
                            valueIdxmd = new IndexMetaData();
                            valuemd.setIndexMetaData(valueIdxmd);
                        }
                        valueIdxmd.setName(indexName3);
                    }
                    if (!StringUtils.isWhitespace(uniqueName3)) {
                        UniqueMetaData valueUnimd = valuemd.getUniqueMetaData();
                        if (valueUnimd == null) {
                            valueUnimd = new UniqueMetaData();
                            valuemd.setUniqueMetaData(valueUnimd);
                        }
                        valueUnimd.setName(uniqueName3);
                    }
                    if (valueColumns != null && valueColumns.length > 0) {
                        for (int l = 0; l < valueColumns.length; ++l) {
                            valuemd.addColumn(JDOAnnotationUtils.getColumnMetaDataForColumnAnnotation(valueColumns[l]));
                        }
                    }
                    JDOAnnotationUtils.addExtensionsToMetaData(valuemd, annotationValues.get("extensions"));
                    final Embedded[] embeddedMappings2 = annotationValues.get("embeddedMapping");
                    if (embeddedMappings2 != null && embeddedMappings2.length > 0) {
                        final EmbeddedMetaData embmd2 = new EmbeddedMetaData();
                        embmd2.setOwnerMember(embeddedMappings2[0].ownerMember());
                        embmd2.setNullIndicatorColumn(embeddedMappings2[0].nullIndicatorColumn());
                        embmd2.setNullIndicatorValue(embeddedMappings2[0].nullIndicatorValue());
                        valuemd.setEmbeddedMetaData(embmd2);
                        embeddedValueMembers = embeddedMappings2[0].members();
                    }
                }
                else if (annName.equals(JDOAnnotationUtils.ORDER)) {
                    final String orderColumn = annotationValues.get("column");
                    final String orderMappedBy = annotationValues.get("mappedBy");
                    final Column[] orderColumns = annotationValues.get("columns");
                    ordermd = new OrderMetaData();
                    ordermd.setColumnName(orderColumn);
                    ordermd.setMappedBy(orderMappedBy);
                    if (orderColumns != null && orderColumns.length > 0) {
                        for (int j2 = 0; j2 < orderColumns.length; ++j2) {
                            ordermd.addColumn(JDOAnnotationUtils.getColumnMetaDataForColumnAnnotation(orderColumns[j2]));
                        }
                    }
                    JDOAnnotationUtils.addExtensionsToMetaData(ordermd, annotationValues.get("extensions"));
                }
                else if (annName.equals(JDOAnnotationUtils.EMBEDDED)) {
                    embeddedOwnerField = annotationValues.get("ownerMember");
                    embeddedNullIndicatorColumn = annotationValues.get("nullIndicatorColumn");
                    embeddedNullIndicatorValue = annotationValues.get("nullIndicatorValue");
                    embeddedMembers = annotationValues.get("members");
                }
                else if (annName.equals(JDOAnnotationUtils.INDEX)) {
                    final String name = annotationValues.get("name");
                    final String table2 = annotationValues.get("table");
                    final String unique4 = annotationValues.get("unique");
                    final String[] members = annotationValues.get("members");
                    final Column[] columns2 = annotationValues.get("columns");
                    idxmd = JDOAnnotationUtils.getIndexMetaData(name, table2, unique4, members, columns2);
                }
                else if (annName.equals(JDOAnnotationUtils.UNIQUE)) {
                    final String name = annotationValues.get("name");
                    final String table2 = annotationValues.get("table");
                    final String deferred = annotationValues.get("deferred");
                    final String[] members = annotationValues.get("members");
                    final Column[] columns2 = annotationValues.get("columns");
                    unimd = JDOAnnotationUtils.getUniqueMetaData(name, table2, deferred, members, columns2);
                }
                else if (annName.equals(JDOAnnotationUtils.FOREIGNKEY)) {
                    final String name = annotationValues.get("name");
                    final String table2 = annotationValues.get("table");
                    final String unique4 = annotationValues.get("unique");
                    final String deferred2 = annotationValues.get("deferred");
                    final String deleteAction2 = JDOAnnotationUtils.getForeignKeyActionString(annotationValues.get("deleteAction"));
                    final String updateAction = JDOAnnotationUtils.getForeignKeyActionString(annotationValues.get("updateAction"));
                    final String[] members2 = annotationValues.get("members");
                    final Column[] columns3 = annotationValues.get("columns");
                    fkmd = JDOAnnotationUtils.getFKMetaData(name, table2, unique4, deferred2, deleteAction2, updateAction, members2, columns3);
                }
                else if (annName.equals("javax.jdo.annotations.Cacheable")) {
                    final String cache = annotationValues.get("value");
                    if (cache != null) {
                        cacheable = cache;
                    }
                }
                else if (annName.equals(JDOAnnotationUtils.EXTENSIONS)) {
                    final Extension[] values = annotationValues.get("value");
                    if (values != null && values.length > 0) {
                        extensions = new HashSet<ExtensionMetaData>(values.length);
                        for (int k = 0; k < values.length; ++k) {
                            final ExtensionMetaData extmd = new ExtensionMetaData(values[k].vendorName(), values[k].key().toString(), values[k].value().toString());
                            extensions.add(extmd);
                        }
                    }
                }
                else if (annName.equals(JDOAnnotationUtils.EXTENSION)) {
                    final ExtensionMetaData extmd2 = new ExtensionMetaData(annotationValues.get("vendorName"), annotationValues.get("key"), annotationValues.get("value"));
                    extensions = new HashSet<ExtensionMetaData>(1);
                    extensions.add(extmd2);
                }
                else {
                    NucleusLogger.METADATA.debug(JDOAnnotationReader.LOCALISER.msg("044211", cmd.getFullClassName(), member.getName(), annotations[i].getName()));
                }
            }
            if (mmd == null && (transactionalField || nonPersistentField || primaryKey || colmds != null || serialised || embeddedOwnerField != null || embeddedNullIndicatorColumn != null || embeddedNullIndicatorValue != null || embeddedMembers != null || elemmd != null || keymd != null || valuemd != null || ordermd != null || idxmd != null || unimd != null || fkmd != null || joinmd != null || extensions != null)) {
                if (member.isProperty()) {
                    mmd = new PropertyMetaData(cmd, member.getName());
                }
                else {
                    mmd = new FieldMetaData(cmd, member.getName());
                }
                if (primaryKey) {
                    mmd.setPersistenceModifier(FieldPersistenceModifier.PERSISTENT);
                    mmd.setPrimaryKey(primaryKey);
                }
                if (serialised) {
                    mmd.setPersistenceModifier(FieldPersistenceModifier.PERSISTENT);
                }
            }
            if (mmd != null) {
                cmd.addMember(mmd);
                if (primaryKey) {
                    mmd.setPrimaryKey(true);
                }
                if (serialised) {
                    mmd.setSerialised(true);
                }
                if (nonPersistentField) {
                    mmd.setNotPersistent();
                }
                if (transactionalField) {
                    mmd.setTransactional();
                }
                if (embeddedOwnerField != null || embeddedNullIndicatorColumn != null || embeddedNullIndicatorValue != null || embeddedMembers != null) {
                    final EmbeddedMetaData embmd3 = new EmbeddedMetaData();
                    embmd3.setOwnerMember(embeddedOwnerField);
                    embmd3.setNullIndicatorColumn(embeddedNullIndicatorColumn);
                    embmd3.setNullIndicatorValue(embeddedNullIndicatorValue);
                    mmd.setEmbeddedMetaData(embmd3);
                    if (embeddedMembers != null && embeddedMembers.length > 0) {
                        for (int j3 = 0; j3 < embeddedMembers.length; ++j3) {
                            String memberName = embeddedMembers[j3].name();
                            if (memberName.indexOf(46) > 0) {
                                memberName = memberName.substring(memberName.lastIndexOf(46) + 1);
                            }
                            final AbstractMemberMetaData embfmd = this.getFieldMetaDataForPersistent(embmd3, embeddedMembers[j3], this.isMemberOfClassAField(member.getType(), memberName));
                            embmd3.addMember(embfmd);
                        }
                    }
                }
                ContainerMetaData contmd = null;
                if (Collection.class.isAssignableFrom(member.getType())) {
                    Class collectionElementType = null;
                    final StringBuilder elementTypeStr = new StringBuilder();
                    if (elementTypes != null && elementTypes.length > 0 && elementTypes[0] != Void.TYPE) {
                        for (int j4 = 0; j4 < elementTypes.length; ++j4) {
                            if (elementTypeStr.length() > 0) {
                                elementTypeStr.append(',');
                            }
                            elementTypeStr.append(elementTypes[j4].getName());
                        }
                        collectionElementType = elementTypes[0];
                    }
                    else {
                        collectionElementType = ClassUtils.getCollectionElementType(member.getType(), member.getGenericType());
                    }
                    contmd = new CollectionMetaData();
                    final CollectionMetaData collmd = (CollectionMetaData)contmd;
                    collmd.setElementType(elementTypeStr.toString());
                    if (!StringUtils.isWhitespace(embeddedElement)) {
                        collmd.setEmbeddedElement(Boolean.valueOf(embeddedElement));
                    }
                    if (!StringUtils.isWhitespace(serializedElement)) {
                        collmd.setSerializedElement(Boolean.valueOf(serializedElement));
                    }
                    if (!StringUtils.isWhitespace(dependentElement)) {
                        collmd.setDependentElement(Boolean.valueOf(dependentElement));
                    }
                    if ((embeddedElementMembers != null || "true".equalsIgnoreCase(embeddedElement)) && elemmd == null) {
                        elemmd = new ElementMetaData();
                        mmd.setElementMetaData(elemmd);
                    }
                    if ("true".equalsIgnoreCase(embeddedElement) && elemmd.getEmbeddedMetaData() == null) {
                        final EmbeddedMetaData embmd4 = new EmbeddedMetaData();
                        elemmd.setEmbeddedMetaData(embmd4);
                    }
                    if (embeddedElementMembers != null) {
                        final EmbeddedMetaData embmd4 = elemmd.getEmbeddedMetaData();
                        for (int j5 = 0; j5 < embeddedElementMembers.length; ++j5) {
                            String memberName2 = embeddedElementMembers[j5].name();
                            if (memberName2.indexOf(46) > 0) {
                                memberName2 = memberName2.substring(memberName2.lastIndexOf(46) + 1);
                            }
                            final AbstractMemberMetaData embfmd2 = this.getFieldMetaDataForPersistent(embmd4, embeddedElementMembers[j5], this.isMemberOfClassAField(collectionElementType, memberName2));
                            embmd4.addMember(embfmd2);
                        }
                    }
                }
                else if (member.getType().isArray()) {
                    final StringBuilder elementTypeStr2 = new StringBuilder();
                    if (elementTypes != null && elementTypes.length > 0 && elementTypes[0] != Void.TYPE) {
                        for (int j6 = 0; j6 < elementTypes.length; ++j6) {
                            if (elementTypeStr2.length() > 0) {
                                elementTypeStr2.append(',');
                            }
                            elementTypeStr2.append(elementTypes[j6].getName());
                        }
                    }
                    else {
                        elementTypeStr2.append(member.getType().getComponentType().getName());
                    }
                    contmd = new ArrayMetaData();
                    final ArrayMetaData arrmd = (ArrayMetaData)contmd;
                    arrmd.setElementType(elementTypeStr2.toString());
                    if (!StringUtils.isWhitespace(embeddedElement)) {
                        arrmd.setEmbeddedElement(Boolean.valueOf(embeddedElement));
                    }
                    if (!StringUtils.isWhitespace(serializedElement)) {
                        arrmd.setSerializedElement(Boolean.valueOf(serializedElement));
                    }
                    if (!StringUtils.isWhitespace(dependentElement)) {
                        arrmd.setDependentElement(Boolean.valueOf(dependentElement));
                    }
                }
                else if (Map.class.isAssignableFrom(member.getType())) {
                    Class mapKeyType = null;
                    if (keyType != null && keyType != Void.TYPE) {
                        mapKeyType = keyType;
                    }
                    else {
                        mapKeyType = ClassUtils.getMapKeyType(member.getType(), member.getGenericType());
                    }
                    Class mapValueType = null;
                    if (valueType != null && valueType != Void.TYPE) {
                        mapValueType = valueType;
                    }
                    else {
                        mapValueType = ClassUtils.getMapValueType(member.getType(), member.getGenericType());
                    }
                    contmd = new MapMetaData();
                    final MapMetaData mapmd = (MapMetaData)contmd;
                    mapmd.setKeyType((mapKeyType != null) ? mapKeyType.getName() : null);
                    if (!StringUtils.isWhitespace(embeddedKey)) {
                        mapmd.setEmbeddedKey(Boolean.valueOf(embeddedKey));
                    }
                    if (!StringUtils.isWhitespace(serializedKey)) {
                        mapmd.setSerializedKey(Boolean.valueOf(serializedKey));
                    }
                    if (!StringUtils.isWhitespace(dependentKey)) {
                        mapmd.setDependentKey(Boolean.valueOf(dependentKey));
                    }
                    mapmd.setValueType((mapValueType != null) ? mapValueType.getName() : null);
                    if (!StringUtils.isWhitespace(embeddedValue)) {
                        mapmd.setEmbeddedValue(Boolean.valueOf(embeddedValue));
                    }
                    if (!StringUtils.isWhitespace(serializedValue)) {
                        mapmd.setSerializedValue(Boolean.valueOf(serializedValue));
                    }
                    if (!StringUtils.isWhitespace(dependentValue)) {
                        mapmd.setDependentValue(Boolean.valueOf(dependentValue));
                    }
                    if ((embeddedKeyMembers != null || "true".equalsIgnoreCase(embeddedKey)) && keymd == null) {
                        keymd = new KeyMetaData();
                        mmd.setKeyMetaData(keymd);
                    }
                    if ("true".equalsIgnoreCase(embeddedKey) && keymd.getEmbeddedMetaData() == null) {
                        final EmbeddedMetaData embmd4 = new EmbeddedMetaData();
                        keymd.setEmbeddedMetaData(embmd4);
                    }
                    if (embeddedKeyMembers != null) {
                        final EmbeddedMetaData embmd4 = keymd.getEmbeddedMetaData();
                        for (int j5 = 0; j5 < embeddedKeyMembers.length; ++j5) {
                            String memberName2 = embeddedKeyMembers[j5].name();
                            if (memberName2.indexOf(46) > 0) {
                                memberName2 = memberName2.substring(memberName2.lastIndexOf(46) + 1);
                            }
                            final AbstractMemberMetaData embfmd2 = this.getFieldMetaDataForPersistent(embmd4, embeddedKeyMembers[j5], this.isMemberOfClassAField(mapKeyType, memberName2));
                            embmd4.addMember(embfmd2);
                        }
                    }
                    if ((embeddedKeyMembers != null || "true".equalsIgnoreCase(embeddedKey)) && valuemd == null) {
                        valuemd = new ValueMetaData();
                        mmd.setValueMetaData(valuemd);
                    }
                    if ("true".equalsIgnoreCase(embeddedValue) && valuemd.getEmbeddedMetaData() == null) {
                        final EmbeddedMetaData embmd4 = new EmbeddedMetaData();
                        valuemd.setEmbeddedMetaData(embmd4);
                    }
                    if (embeddedValueMembers != null) {
                        final EmbeddedMetaData embmd4 = valuemd.getEmbeddedMetaData();
                        for (int j5 = 0; j5 < embeddedValueMembers.length; ++j5) {
                            String memberName2 = embeddedValueMembers[j5].name();
                            if (memberName2.indexOf(46) > 0) {
                                memberName2 = memberName2.substring(memberName2.lastIndexOf(46) + 1);
                            }
                            final AbstractMemberMetaData embfmd2 = this.getFieldMetaDataForPersistent(embmd4, embeddedValueMembers[j5], this.isMemberOfClassAField(mapValueType, memberName2));
                            embmd4.addMember(embfmd2);
                        }
                    }
                }
                if (contmd != null) {
                    mmd.setContainer(contmd);
                    if (elemmd != null) {
                        elemmd.setParent(mmd);
                        mmd.setElementMetaData(elemmd);
                        if (elemmd.getMappedBy() != null && mmd.getMappedBy() == null) {
                            mmd.setMappedBy(elemmd.getMappedBy());
                        }
                    }
                    if (keymd != null) {
                        keymd.setParent(mmd);
                        mmd.setKeyMetaData(keymd);
                    }
                    if (valuemd != null) {
                        valuemd.setParent(mmd);
                        mmd.setValueMetaData(valuemd);
                    }
                    if (ordermd != null) {
                        ordermd.setParent(mmd);
                        mmd.setOrderMetaData(ordermd);
                    }
                }
                if (joinmd != null) {
                    mmd.setJoinMetaData(joinmd);
                }
                if (colmds != null) {
                    for (int i2 = 0; i2 < colmds.length; ++i2) {
                        mmd.addColumn(colmds[i2]);
                    }
                }
                if (idxmd != null) {
                    mmd.setIndexMetaData(idxmd);
                }
                if (unimd != null) {
                    mmd.setUniqueMetaData(unimd);
                }
                if (fkmd != null) {
                    mmd.setForeignKeyMetaData(fkmd);
                }
                if (cacheable != null && cacheable.equalsIgnoreCase("false")) {
                    mmd.setCacheable(false);
                }
                if (extensions != null) {
                    for (final ExtensionMetaData extmd3 : extensions) {
                        mmd.addExtension(extmd3.getVendorName(), extmd3.getKey(), extmd3.getValue());
                    }
                }
            }
        }
        return mmd;
    }
    
    @Override
    protected void processMethodAnnotations(final AbstractClassMetaData cmd, final Method method) {
    }
    
    private AbstractMemberMetaData getFieldMetaDataForPersistent(final MetaData parent, final Persistent member, final boolean isField) {
        final FieldPersistenceModifier modifier = JDOAnnotationUtils.getFieldPersistenceModifier(member.persistenceModifier());
        final String nullValue = JDOAnnotationUtils.getNullValueString(member.nullValue());
        final String valueStrategy = JDOAnnotationUtils.getIdentityStrategyString(member.valueStrategy());
        String fieldTypeName = null;
        final Class[] fieldTypes = member.types();
        if (fieldTypes != null && fieldTypes.length > 0) {
            final StringBuilder typeStr = new StringBuilder();
            for (int j = 0; j < fieldTypes.length; ++j) {
                if (typeStr.length() > 0) {
                    typeStr.append(',');
                }
                if (fieldTypes[j] != null && fieldTypes[j] != Void.TYPE) {
                    typeStr.append(fieldTypes[j].getName());
                }
            }
            fieldTypeName = typeStr.toString();
        }
        AbstractMemberMetaData fmd = null;
        if (isField) {
            fmd = new FieldMetaData(parent, member.name());
        }
        else {
            fmd = new PropertyMetaData(parent, member.name());
        }
        if (modifier != null) {
            fmd.setPersistenceModifier(modifier);
        }
        if (!StringUtils.isWhitespace(member.defaultFetchGroup())) {
            fmd.setDefaultFetchGroup(Boolean.valueOf(member.defaultFetchGroup()));
        }
        if (!StringUtils.isWhitespace(member.primaryKey())) {
            fmd.setPrimaryKey(Boolean.valueOf(member.primaryKey()));
        }
        if (!StringUtils.isWhitespace(member.embedded())) {
            fmd.setEmbedded(Boolean.valueOf(member.embedded()));
        }
        if (!StringUtils.isWhitespace(member.serialized())) {
            fmd.setSerialised(Boolean.valueOf(member.serialized()));
        }
        if (!StringUtils.isWhitespace(member.dependent())) {
            fmd.setDependent(Boolean.valueOf(member.dependent()));
        }
        fmd.setNullValue(org.datanucleus.metadata.NullValue.getNullValue(nullValue));
        fmd.setMappedBy(member.mappedBy());
        fmd.setColumn(member.column());
        fmd.setTable(member.table());
        fmd.setLoadFetchGroup(member.loadFetchGroup());
        fmd.setValueStrategy(valueStrategy);
        fmd.setSequence(member.sequence());
        fmd.setFieldTypes(fieldTypeName);
        final Column[] columns = member.columns();
        if (columns != null && columns.length > 0) {
            for (int i = 0; i < columns.length; ++i) {
                fmd.addColumn(JDOAnnotationUtils.getColumnMetaDataForColumnAnnotation(columns[i]));
            }
        }
        return fmd;
    }
    
    private boolean isMemberOfClassAField(final Class cls, final String memberName) {
        try {
            cls.getDeclaredField(memberName);
        }
        catch (NoSuchFieldException nsfe) {
            return false;
        }
        return true;
    }
    
    protected AnnotationObject isClassPersistenceCapable(final Class cls) {
        final AnnotationObject[] annotations = this.getClassAnnotationsForClass(cls);
        for (int i = 0; i < annotations.length; ++i) {
            final String annClassName = annotations[i].getName();
            if (annClassName.equals(JDOAnnotationUtils.PERSISTENCE_CAPABLE)) {
                return annotations[i];
            }
        }
        return null;
    }
    
    protected boolean isClassPersistenceAware(final Class cls) {
        final AnnotationObject[] annotations = this.getClassAnnotationsForClass(cls);
        for (int i = 0; i < annotations.length; ++i) {
            final String annName = annotations[i].getName();
            if (annName.equals(JDOAnnotationUtils.PERSISTENCE_AWARE)) {
                return true;
            }
        }
        return false;
    }
    
    protected boolean doesClassHaveNamedQueries(final Class cls) {
        final AnnotationObject[] annotations = this.getClassAnnotationsForClass(cls);
        for (int i = 0; i < annotations.length; ++i) {
            final String annClassName = annotations[i].getName();
            if (annClassName.equals(JDOAnnotationUtils.QUERIES) || annClassName.equals(JDOAnnotationUtils.QUERY)) {
                return true;
            }
        }
        return false;
    }
}
