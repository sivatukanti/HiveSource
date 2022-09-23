// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.metadata;

import org.xml.sax.SAXException;
import org.datanucleus.metadata.CollectionMetaData;
import org.datanucleus.metadata.ArrayMetaData;
import org.datanucleus.metadata.MapMetaData;
import org.datanucleus.metadata.SequenceMetaData;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.metadata.ForeignKeyAction;
import org.datanucleus.metadata.FetchPlanMetaData;
import org.datanucleus.metadata.ValueMetaData;
import org.datanucleus.metadata.KeyMetaData;
import org.datanucleus.metadata.ElementMetaData;
import org.datanucleus.metadata.VersionMetaData;
import org.datanucleus.metadata.DiscriminatorMetaData;
import org.datanucleus.metadata.OrderMetaData;
import org.datanucleus.metadata.AbstractElementMetaData;
import org.datanucleus.metadata.ColumnMetaData;
import org.datanucleus.metadata.MetaDataUtils;
import org.datanucleus.metadata.UniqueMetaData;
import org.datanucleus.metadata.IndexMetaData;
import org.datanucleus.metadata.ForeignKeyMetaData;
import org.datanucleus.metadata.InheritanceMetaData;
import org.datanucleus.metadata.IdentityStrategy;
import org.datanucleus.metadata.IdentityMetaData;
import org.datanucleus.metadata.FetchGroupMemberMetaData;
import org.datanucleus.metadata.FetchGroupMetaData;
import org.datanucleus.metadata.EmbeddedMetaData;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.ImplementsMetaData;
import org.datanucleus.metadata.JoinMetaData;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.PrimaryKeyMetaData;
import org.datanucleus.metadata.MetadataFileType;
import org.datanucleus.metadata.QueryMetaData;
import org.datanucleus.metadata.PropertyMetaData;
import org.datanucleus.metadata.IndexedValue;
import org.datanucleus.metadata.NullValue;
import org.datanucleus.metadata.FieldPersistenceModifier;
import org.datanucleus.metadata.FieldMetaData;
import org.datanucleus.metadata.MetaData;
import org.datanucleus.metadata.InterfaceMetaData;
import org.datanucleus.metadata.IdentityType;
import org.datanucleus.metadata.ClassPersistenceModifier;
import org.datanucleus.metadata.InvalidClassMetaDataException;
import org.datanucleus.util.StringUtils;
import org.datanucleus.metadata.ClassMetaData;
import org.xml.sax.Attributes;
import org.datanucleus.metadata.PackageMetaData;
import org.datanucleus.metadata.FileMetaData;
import org.xml.sax.EntityResolver;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.metadata.xml.AbstractMetaDataHandler;

public class JDOMetaDataHandler extends AbstractMetaDataHandler
{
    public JDOMetaDataHandler(final MetaDataManager mgr, final String filename, final EntityResolver resolver) {
        super(mgr, filename, resolver);
        this.metadata = new FileMetaData();
        ((FileMetaData)this.metadata).setFilename(filename);
        ((FileMetaData)this.metadata).setMetaDataManager(mgr);
        this.pushStack(this.metadata);
    }
    
    protected ClassMetaData newClassObject(final PackageMetaData pmd, final Attributes attrs) {
        final String name = this.getAttr(attrs, "name");
        if (StringUtils.isWhitespace(name)) {
            throw new InvalidClassMetaDataException(JDOMetaDataHandler.LOCALISER, "044061", pmd.getName());
        }
        final ClassMetaData cmd = new ClassMetaData(pmd, name);
        cmd.setTable(this.getAttr(attrs, "table"));
        cmd.setCatalog(this.getAttr(attrs, "catalog"));
        cmd.setSchema(this.getAttr(attrs, "schema"));
        cmd.setRequiresExtent(this.getAttr(attrs, "requires-extent"));
        final String detachableStr = this.getAttr(attrs, "detachable");
        if (this.mgr.getNucleusContext().getPersistenceConfiguration().getBooleanProperty("datanucleus.metadata.alwaysDetachable")) {
            cmd.setDetachable(true);
        }
        else {
            cmd.setDetachable(detachableStr);
        }
        cmd.setObjectIdClass(this.getAttr(attrs, "objectid-class"));
        cmd.setEmbeddedOnly(this.getAttr(attrs, "embedded-only"));
        cmd.setPersistenceModifier(ClassPersistenceModifier.getClassPersistenceModifier(this.getAttr(attrs, "persistence-modifier")));
        cmd.setIdentityType(IdentityType.getIdentityType(this.getAttr(attrs, "identity-type")));
        cmd.setPersistenceCapableSuperclass(this.getAttr(attrs, "persistence-capable-superclass"));
        final String cacheableAttr = this.getAttr(attrs, "cacheable");
        if (cacheableAttr != null) {
            cmd.setCacheable(!cacheableAttr.equalsIgnoreCase("false"));
        }
        final String serializeReadAttr = this.getAttr(attrs, "serialize-read");
        if (serializeReadAttr != null) {
            cmd.setSerializeRead(serializeReadAttr.equalsIgnoreCase("true"));
        }
        return cmd;
    }
    
    protected InterfaceMetaData newInterfaceObject(final PackageMetaData pmd, final Attributes attrs) {
        final String name = this.getAttr(attrs, "name");
        if (StringUtils.isWhitespace(name)) {
            throw new InvalidClassMetaDataException(JDOMetaDataHandler.LOCALISER, "044061", pmd.getName());
        }
        final InterfaceMetaData imd = new InterfaceMetaData(pmd, name);
        imd.setTable(this.getAttr(attrs, "table"));
        imd.setCatalog(this.getAttr(attrs, "catalog"));
        imd.setSchema(this.getAttr(attrs, "schema"));
        final String detachableStr = this.getAttr(attrs, "detachable");
        if (this.mgr.getNucleusContext().getPersistenceConfiguration().getBooleanProperty("datanucleus.metadata.alwaysDetachable")) {
            imd.setDetachable(true);
        }
        else {
            imd.setDetachable(detachableStr);
        }
        imd.setRequiresExtent(this.getAttr(attrs, "requires-extent"));
        imd.setObjectIdClass(this.getAttr(attrs, "objectid-class"));
        imd.setEmbeddedOnly(this.getAttr(attrs, "embedded-only"));
        imd.setIdentityType(IdentityType.getIdentityType(this.getAttr(attrs, "identity-type")));
        imd.setPersistenceModifier(ClassPersistenceModifier.PERSISTENCE_CAPABLE);
        final String cacheableAttr = this.getAttr(attrs, "cacheable");
        if (cacheableAttr != null) {
            imd.setCacheable(!cacheableAttr.equalsIgnoreCase("false"));
        }
        return imd;
    }
    
    protected FieldMetaData newFieldObject(final MetaData md, final Attributes attrs) {
        final FieldMetaData fmd = new FieldMetaData(md, this.getAttr(attrs, "name"));
        final String modStr = this.getAttr(attrs, "persistence-modifier");
        final FieldPersistenceModifier modifier = FieldPersistenceModifier.getFieldPersistenceModifier(modStr);
        if (modifier != null) {
            fmd.setPersistenceModifier(modifier);
        }
        fmd.setDeleteAction(this.getAttr(attrs, "delete-action"));
        final String pkStr = this.getAttr(attrs, "primary-key");
        if (!StringUtils.isWhitespace(pkStr)) {
            fmd.setPrimaryKey(Boolean.valueOf(pkStr));
        }
        final String dfgStr = this.getAttr(attrs, "default-fetch-group");
        if (!StringUtils.isWhitespace(dfgStr)) {
            fmd.setDefaultFetchGroup(Boolean.valueOf(dfgStr));
        }
        final String embStr = this.getAttr(attrs, "embedded");
        if (!StringUtils.isWhitespace(embStr)) {
            fmd.setEmbedded(Boolean.valueOf(embStr));
        }
        final String serStr = this.getAttr(attrs, "serialized");
        if (!StringUtils.isWhitespace(serStr)) {
            fmd.setSerialised(Boolean.valueOf(serStr));
        }
        final String depStr = this.getAttr(attrs, "dependent");
        if (!StringUtils.isWhitespace(depStr)) {
            fmd.setDependent(Boolean.valueOf(depStr));
        }
        fmd.setNullValue(NullValue.getNullValue(this.getAttr(attrs, "null-value")));
        fmd.setMappedBy(this.getAttr(attrs, "mapped-by"));
        fmd.setColumn(this.getAttr(attrs, "column"));
        fmd.setIndexed(IndexedValue.getIndexedValue(this.getAttr(attrs, "indexed")));
        fmd.setUnique(this.getAttr(attrs, "unique"));
        fmd.setTable(this.getAttr(attrs, "table"));
        fmd.setLoadFetchGroup(this.getAttr(attrs, "load-fetch-group"));
        fmd.setRecursionDepth(this.getAttr(attrs, "recursion-depth"));
        fmd.setValueStrategy(this.getAttr(attrs, "value-strategy"));
        fmd.setSequence(this.getAttr(attrs, "sequence"));
        fmd.setFieldTypes(this.getAttr(attrs, "field-type"));
        final String cacheableAttr = this.getAttr(attrs, "cacheable");
        if (cacheableAttr != null) {
            fmd.setCacheable(!cacheableAttr.equalsIgnoreCase("false"));
        }
        return fmd;
    }
    
    protected PropertyMetaData newPropertyObject(final MetaData md, final Attributes attrs) {
        final PropertyMetaData pmd = new PropertyMetaData(md, this.getAttr(attrs, "name"));
        final String modStr = this.getAttr(attrs, "persistence-modifier");
        final FieldPersistenceModifier modifier = FieldPersistenceModifier.getFieldPersistenceModifier(modStr);
        if (modifier != null) {
            pmd.setPersistenceModifier(modifier);
        }
        pmd.setDeleteAction(this.getAttr(attrs, "delete-action"));
        final String pkStr = this.getAttr(attrs, "primary-key");
        if (!StringUtils.isWhitespace(pkStr)) {
            pmd.setPrimaryKey(Boolean.valueOf(pkStr));
        }
        final String dfgStr = this.getAttr(attrs, "default-fetch-group");
        if (!StringUtils.isWhitespace(dfgStr)) {
            pmd.setDefaultFetchGroup(Boolean.valueOf(dfgStr));
        }
        final String embStr = this.getAttr(attrs, "embedded");
        if (!StringUtils.isWhitespace(embStr)) {
            pmd.setEmbedded(Boolean.valueOf(embStr));
        }
        final String serStr = this.getAttr(attrs, "serialized");
        if (!StringUtils.isWhitespace(serStr)) {
            pmd.setSerialised(Boolean.valueOf(serStr));
        }
        final String depStr = this.getAttr(attrs, "dependent");
        if (!StringUtils.isWhitespace(depStr)) {
            pmd.setDependent(Boolean.valueOf(depStr));
        }
        pmd.setNullValue(NullValue.getNullValue(this.getAttr(attrs, "null-value")));
        pmd.setMappedBy(this.getAttr(attrs, "mapped-by"));
        pmd.setColumn(this.getAttr(attrs, "column"));
        pmd.setIndexed(IndexedValue.getIndexedValue(this.getAttr(attrs, "indexed")));
        pmd.setUnique(this.getAttr(attrs, "unique"));
        pmd.setTable(this.getAttr(attrs, "table"));
        pmd.setLoadFetchGroup(this.getAttr(attrs, "load-fetch-group"));
        pmd.setRecursionDepth(this.getAttr(attrs, "recursion-depth"));
        pmd.setValueStrategy(this.getAttr(attrs, "value-strategy"));
        pmd.setSequence(this.getAttr(attrs, "sequence"));
        pmd.setFieldTypes(this.getAttr(attrs, "field-type"));
        pmd.setFieldName(this.getAttr(attrs, "field-name"));
        final String cacheableAttr = this.getAttr(attrs, "cacheable");
        if (cacheableAttr != null) {
            pmd.setCacheable(!cacheableAttr.equalsIgnoreCase("false"));
        }
        return pmd;
    }
    
    @Override
    public void startElement(final String uri, String localName, final String qName, final Attributes attrs) throws SAXException {
        if (this.charactersBuffer.length() > 0) {
            final String currentString = this.getString().trim();
            if (this.getStack() instanceof QueryMetaData) {
                ((QueryMetaData)this.getStack()).setQuery(currentString.trim());
            }
        }
        if (localName.length() < 1) {
            localName = qName;
        }
        try {
            if (localName.equals("jdo")) {
                final FileMetaData filemd = (FileMetaData)this.getStack();
                filemd.setType(MetadataFileType.JDO_FILE);
                filemd.setCatalog(this.getAttr(attrs, "catalog"));
                filemd.setSchema(this.getAttr(attrs, "schema"));
            }
            else if (localName.equals("orm")) {
                final FileMetaData filemd = (FileMetaData)this.getStack();
                filemd.setType(MetadataFileType.JDO_ORM_FILE);
                filemd.setCatalog(this.getAttr(attrs, "catalog"));
                filemd.setSchema(this.getAttr(attrs, "schema"));
            }
            else if (localName.equals("jdoquery")) {
                final FileMetaData filemd = (FileMetaData)this.getStack();
                filemd.setType(MetadataFileType.JDO_QUERY_FILE);
            }
            else if (localName.equals("fetch-plan")) {
                final FileMetaData filemd = (FileMetaData)this.metadata;
                final FetchPlanMetaData fpmd = filemd.newFetchPlanMetadata(this.getAttr(attrs, "name"));
                fpmd.setMaxFetchDepth(this.getAttr(attrs, "max-fetch-depth"));
                fpmd.setFetchSize(this.getAttr(attrs, "fetch-size"));
                this.pushStack(fpmd);
            }
            else if (localName.equals("package")) {
                final FileMetaData filemd = (FileMetaData)this.getStack();
                final PackageMetaData pmd = filemd.newPackageMetadata(this.getAttr(attrs, "name"));
                pmd.setCatalog(this.getAttr(attrs, "catalog"));
                pmd.setSchema(this.getAttr(attrs, "schema"));
                this.pushStack(pmd);
            }
            else if (localName.equals("class")) {
                final PackageMetaData pmd2 = (PackageMetaData)this.getStack();
                final ClassMetaData cmd = this.newClassObject(pmd2, attrs);
                pmd2.addClass(cmd);
                this.pushStack(cmd);
            }
            else if (localName.equals("interface")) {
                final PackageMetaData pmd2 = (PackageMetaData)this.getStack();
                final InterfaceMetaData imd = this.newInterfaceObject(pmd2, attrs);
                pmd2.addInterface(imd);
                this.pushStack(imd);
            }
            else if (localName.equals("primary-key")) {
                final MetaData md = this.getStack();
                final PrimaryKeyMetaData pkmd = new PrimaryKeyMetaData();
                pkmd.setName(this.getAttr(attrs, "name"));
                pkmd.setColumnName(this.getAttr(attrs, "column"));
                if (md instanceof AbstractClassMetaData) {
                    ((AbstractClassMetaData)md).setPrimaryKeyMetaData(pkmd);
                }
                else if (md instanceof JoinMetaData) {
                    ((JoinMetaData)md).setPrimaryKeyMetaData(pkmd);
                }
                this.pushStack(pkmd);
            }
            else if (localName.equals("implements")) {
                final ClassMetaData cmd2 = (ClassMetaData)this.getStack();
                final ImplementsMetaData imd2 = new ImplementsMetaData(this.getAttr(attrs, "name"));
                cmd2.addImplements(imd2);
                this.pushStack(imd2);
            }
            else if (localName.equals("property")) {
                final MetaData parent = this.getStack();
                if (parent instanceof AbstractClassMetaData) {
                    final AbstractClassMetaData acmd = (AbstractClassMetaData)parent;
                    final PropertyMetaData propmd = this.newPropertyObject(acmd, attrs);
                    acmd.addMember(propmd);
                    this.pushStack(propmd);
                }
                else if (parent instanceof EmbeddedMetaData) {
                    final EmbeddedMetaData emd = (EmbeddedMetaData)parent;
                    final PropertyMetaData propmd = this.newPropertyObject(emd, attrs);
                    emd.addMember(propmd);
                    this.pushStack(propmd);
                }
                else if (parent instanceof ImplementsMetaData) {
                    final ImplementsMetaData implmd = (ImplementsMetaData)parent;
                    final PropertyMetaData propmd = this.newPropertyObject(implmd, attrs);
                    implmd.addProperty(propmd);
                    this.pushStack(propmd);
                }
                else if (parent instanceof FetchGroupMetaData) {
                    final FetchGroupMetaData fgmd = (FetchGroupMetaData)parent;
                    final FetchGroupMemberMetaData fgmmd = new FetchGroupMemberMetaData(fgmd, this.getAttr(attrs, "name"));
                    fgmmd.setRecursionDepth(this.getAttr(attrs, "recursion-depth"));
                    fgmmd.setProperty();
                    fgmd.addMember(fgmmd);
                    this.pushStack(fgmmd);
                }
            }
            else if (localName.equals("datastore-identity")) {
                final AbstractClassMetaData acmd2 = (AbstractClassMetaData)this.getStack();
                final IdentityMetaData idmd = new IdentityMetaData();
                idmd.setColumnName(this.getAttr(attrs, "column"));
                idmd.setValueStrategy(IdentityStrategy.getIdentityStrategy(this.getAttr(attrs, "strategy")));
                idmd.setSequence(this.getAttr(attrs, "sequence"));
                acmd2.setIdentityMetaData(idmd);
                this.pushStack(idmd);
            }
            else if (localName.equals("inheritance")) {
                final MetaData parent = this.getStack();
                final AbstractClassMetaData acmd = (AbstractClassMetaData)parent;
                final InheritanceMetaData inhmd = new InheritanceMetaData();
                inhmd.setStrategy(this.getAttr(attrs, "strategy"));
                acmd.setInheritanceMetaData(inhmd);
                this.pushStack(inhmd);
            }
            else if (localName.equals("discriminator")) {
                final MetaData md = this.getStack();
                if (md instanceof InheritanceMetaData) {
                    final InheritanceMetaData inhmd2 = (InheritanceMetaData)md;
                    final DiscriminatorMetaData dismd = inhmd2.newDiscriminatorMetadata();
                    dismd.setColumnName(this.getAttr(attrs, "column"));
                    dismd.setValue(this.getAttr(attrs, "value"));
                    dismd.setStrategy(this.getAttr(attrs, "strategy"));
                    dismd.setIndexed(this.getAttr(attrs, "indexed"));
                    this.pushStack(dismd);
                }
                else if (md instanceof EmbeddedMetaData) {
                    final EmbeddedMetaData embmd = (EmbeddedMetaData)md;
                    final DiscriminatorMetaData dismd = embmd.newDiscriminatorMetadata();
                    dismd.setColumnName(this.getAttr(attrs, "column"));
                    dismd.setValue(this.getAttr(attrs, "value"));
                    dismd.setStrategy(this.getAttr(attrs, "strategy"));
                    dismd.setIndexed(this.getAttr(attrs, "indexed"));
                    this.pushStack(dismd);
                }
            }
            else if (localName.equals("query")) {
                final MetaData emd2 = this.getStack();
                if (emd2 instanceof ClassMetaData) {
                    final ClassMetaData cmd = (ClassMetaData)emd2;
                    final String name = this.getAttr(attrs, "name");
                    if (StringUtils.isWhitespace(name)) {
                        throw new InvalidClassMetaDataException(JDOMetaDataHandler.LOCALISER, "044154", cmd.getFullClassName());
                    }
                    final QueryMetaData qmd = new QueryMetaData(name);
                    qmd.setScope(cmd.getFullClassName());
                    qmd.setLanguage(this.getAttr(attrs, "language"));
                    qmd.setUnmodifiable(this.getAttr(attrs, "unmodifiable"));
                    qmd.setResultClass(this.getAttr(attrs, "result-class"));
                    qmd.setUnique(this.getAttr(attrs, "unique"));
                    qmd.setFetchPlanName(this.getAttr(attrs, "fetch-plan"));
                    cmd.addQuery(qmd);
                    this.pushStack(qmd);
                }
                else if (emd2 instanceof InterfaceMetaData) {
                    final InterfaceMetaData imd = (InterfaceMetaData)emd2;
                    final String name = this.getAttr(attrs, "name");
                    if (StringUtils.isWhitespace(name)) {
                        throw new InvalidClassMetaDataException(JDOMetaDataHandler.LOCALISER, "044154", imd.getFullClassName());
                    }
                    final QueryMetaData qmd = new QueryMetaData(name);
                    qmd.setScope(imd.getFullClassName());
                    qmd.setLanguage(this.getAttr(attrs, "language"));
                    qmd.setUnmodifiable(this.getAttr(attrs, "unmodifiable"));
                    qmd.setResultClass(this.getAttr(attrs, "result-class"));
                    qmd.setUnique(this.getAttr(attrs, "unique"));
                    qmd.setFetchPlanName(this.getAttr(attrs, "fetch-plan"));
                    imd.addQuery(qmd);
                    this.pushStack(qmd);
                }
                else if (emd2 instanceof FileMetaData) {
                    final FileMetaData filemd2 = (FileMetaData)emd2;
                    final QueryMetaData qmd2 = filemd2.newQueryMetadata(this.getAttr(attrs, "name"));
                    qmd2.setLanguage(this.getAttr(attrs, "language"));
                    qmd2.setUnmodifiable(this.getAttr(attrs, "unmodifiable"));
                    qmd2.setResultClass(this.getAttr(attrs, "result-class"));
                    qmd2.setUnique(this.getAttr(attrs, "unique"));
                    qmd2.setFetchPlanName(this.getAttr(attrs, "fetch-plan"));
                    this.pushStack(qmd2);
                }
            }
            else if (localName.equals("sequence")) {
                final PackageMetaData pmd2 = (PackageMetaData)this.getStack();
                final SequenceMetaData seqmd = pmd2.newSequenceMetadata(this.getAttr(attrs, "name"), this.getAttr(attrs, "strategy"));
                seqmd.setFactoryClass(this.getAttr(attrs, "factory-class"));
                seqmd.setDatastoreSequence(this.getAttr(attrs, "datastore-sequence"));
                final String seqSize = this.getAttr(attrs, "allocation-size");
                if (seqSize != null) {
                    seqmd.setAllocationSize(seqSize);
                }
                final String seqStart = this.getAttr(attrs, "initial-value");
                if (seqStart != null) {
                    seqmd.setInitialValue(seqStart);
                }
                this.pushStack(seqmd);
            }
            else if (localName.equals("field")) {
                final MetaData md = this.getStack();
                if (md instanceof FetchGroupMetaData) {
                    final FetchGroupMetaData fgmd = (FetchGroupMetaData)md;
                    final FetchGroupMemberMetaData fgmmd = new FetchGroupMemberMetaData(md, this.getAttr(attrs, "name"));
                    fgmmd.setRecursionDepth(this.getAttr(attrs, "recursion-depth"));
                    fgmd.addMember(fgmmd);
                    this.pushStack(fgmmd);
                    return;
                }
                final FieldMetaData fmd = this.newFieldObject(md, attrs);
                if (md instanceof ClassMetaData) {
                    final ClassMetaData cmd3 = (ClassMetaData)md;
                    cmd3.addMember(fmd);
                }
                else if (md instanceof EmbeddedMetaData) {
                    final EmbeddedMetaData emd3 = (EmbeddedMetaData)md;
                    emd3.addMember(fmd);
                }
                else if (md instanceof ForeignKeyMetaData) {
                    final ForeignKeyMetaData fkmd = (ForeignKeyMetaData)md;
                    fkmd.addMember(fmd.getName());
                }
                else if (md instanceof IndexMetaData) {
                    final IndexMetaData imd3 = (IndexMetaData)md;
                    imd3.addMember(fmd.getName());
                }
                else if (md instanceof UniqueMetaData) {
                    final UniqueMetaData umd = (UniqueMetaData)md;
                    umd.addMember(fmd.getName());
                }
                this.pushStack(fmd);
            }
            else if (localName.equals("join")) {
                final MetaData parent = this.getStack();
                final String tableName = this.getAttr(attrs, "table");
                final String columnName = this.getAttr(attrs, "column");
                final String outer = this.getAttr(attrs, "outer");
                final IndexedValue indexed = IndexedValue.getIndexedValue(this.getAttr(attrs, "indexed"));
                final String unique = this.getAttr(attrs, "unique");
                final String deleteAction = this.getAttr(attrs, "delete-action");
                JoinMetaData joinmd = null;
                if (parent instanceof AbstractMemberMetaData) {
                    final AbstractMemberMetaData fmd2 = (AbstractMemberMetaData)parent;
                    joinmd = fmd2.newJoinMetadata();
                }
                else if (parent instanceof AbstractClassMetaData) {
                    final AbstractClassMetaData cmd4 = (AbstractClassMetaData)parent;
                    joinmd = new JoinMetaData();
                    cmd4.addJoin(joinmd);
                }
                else if (parent instanceof InheritanceMetaData) {
                    final InheritanceMetaData inhmd3 = (InheritanceMetaData)parent;
                    joinmd = inhmd3.newJoinMetadata();
                }
                joinmd.setTable(tableName);
                joinmd.setColumnName(columnName);
                joinmd.setOuter(MetaDataUtils.getBooleanForString(outer, false));
                joinmd.setIndexed(indexed);
                joinmd.setUnique(unique);
                joinmd.setDeleteAction(deleteAction);
                this.pushStack(joinmd);
            }
            else if (localName.equals("map")) {
                final AbstractMemberMetaData fmd3 = (AbstractMemberMetaData)this.getStack();
                final MapMetaData mapmd = fmd3.newMapMetaData();
                mapmd.setKeyType(this.getAttr(attrs, "key-type"));
                final String embKeyStr = this.getAttr(attrs, "embedded-key");
                if (!StringUtils.isWhitespace(embKeyStr)) {
                    mapmd.setEmbeddedKey(Boolean.valueOf(embKeyStr));
                }
                final String serKeyStr = this.getAttr(attrs, "serialized-key");
                if (!StringUtils.isWhitespace(serKeyStr)) {
                    mapmd.setSerializedKey(Boolean.valueOf(serKeyStr));
                }
                final String depKeyStr = this.getAttr(attrs, "dependent-key");
                if (!StringUtils.isWhitespace(depKeyStr)) {
                    mapmd.setDependentKey(Boolean.valueOf(depKeyStr));
                }
                mapmd.setValueType(this.getAttr(attrs, "value-type"));
                final String embValStr = this.getAttr(attrs, "embedded-value");
                if (!StringUtils.isWhitespace(embValStr)) {
                    mapmd.setEmbeddedValue(Boolean.valueOf(embValStr));
                }
                final String serValStr = this.getAttr(attrs, "serialized-value");
                if (!StringUtils.isWhitespace(serValStr)) {
                    mapmd.setSerializedValue(Boolean.valueOf(serValStr));
                }
                final String depValStr = this.getAttr(attrs, "dependent-value");
                if (!StringUtils.isWhitespace(depValStr)) {
                    mapmd.setDependentValue(Boolean.valueOf(depValStr));
                }
                this.pushStack(mapmd);
            }
            else if (localName.equals("array")) {
                final AbstractMemberMetaData fmd3 = (AbstractMemberMetaData)this.getStack();
                final ArrayMetaData arrmd = fmd3.newArrayMetaData();
                arrmd.setElementType(this.getAttr(attrs, "element-type"));
                final String embElemStr = this.getAttr(attrs, "embedded-element");
                if (!StringUtils.isWhitespace(embElemStr)) {
                    arrmd.setEmbeddedElement(Boolean.valueOf(embElemStr));
                }
                final String serElemStr = this.getAttr(attrs, "serialized-element");
                if (!StringUtils.isWhitespace(serElemStr)) {
                    arrmd.setSerializedElement(Boolean.valueOf(serElemStr));
                }
                final String depElemStr = this.getAttr(attrs, "dependent-element");
                if (!StringUtils.isWhitespace(depElemStr)) {
                    arrmd.setDependentElement(Boolean.valueOf(depElemStr));
                }
                this.pushStack(arrmd);
            }
            else if (localName.equals("collection")) {
                final AbstractMemberMetaData fmd3 = (AbstractMemberMetaData)this.getStack();
                final CollectionMetaData collmd = fmd3.newCollectionMetaData();
                collmd.setElementType(this.getAttr(attrs, "element-type"));
                final String embElemStr = this.getAttr(attrs, "embedded-element");
                if (!StringUtils.isWhitespace(embElemStr)) {
                    collmd.setEmbeddedElement(Boolean.valueOf(embElemStr));
                }
                final String serElemStr = this.getAttr(attrs, "serialized-element");
                if (!StringUtils.isWhitespace(serElemStr)) {
                    collmd.setSerializedElement(Boolean.valueOf(serElemStr));
                }
                final String depElemStr = this.getAttr(attrs, "dependent-element");
                if (!StringUtils.isWhitespace(depElemStr)) {
                    collmd.setDependentElement(Boolean.valueOf(depElemStr));
                }
                this.pushStack(collmd);
            }
            else if (localName.equals("column")) {
                final MetaData md = this.getStack();
                final ColumnMetaData colmd = new ColumnMetaData();
                colmd.setName(this.getAttr(attrs, "name"));
                colmd.setTarget(this.getAttr(attrs, "target"));
                colmd.setTargetMember(this.getAttr(attrs, "target-field"));
                colmd.setJdbcType(this.getAttr(attrs, "jdbc-type"));
                colmd.setSqlType(this.getAttr(attrs, "sql-type"));
                colmd.setLength(this.getAttr(attrs, "length"));
                colmd.setScale(this.getAttr(attrs, "scale"));
                colmd.setAllowsNull(this.getAttr(attrs, "allows-null"));
                colmd.setDefaultValue(this.getAttr(attrs, "default-value"));
                colmd.setInsertValue(this.getAttr(attrs, "insert-value"));
                final String pos = this.getAttr(attrs, "position");
                if (pos != null) {
                    colmd.setPosition(pos);
                }
                if (md instanceof AbstractMemberMetaData) {
                    final AbstractMemberMetaData fmd4 = (AbstractMemberMetaData)md;
                    fmd4.addColumn(colmd);
                }
                else if (md instanceof AbstractElementMetaData) {
                    final AbstractElementMetaData elemd = (AbstractElementMetaData)md;
                    elemd.addColumn(colmd);
                }
                else if (md instanceof JoinMetaData) {
                    final JoinMetaData jnmd = (JoinMetaData)md;
                    jnmd.addColumn(colmd);
                }
                else if (md instanceof IdentityMetaData) {
                    final IdentityMetaData idmd2 = (IdentityMetaData)md;
                    idmd2.setColumnMetaData(colmd);
                }
                else if (md instanceof ForeignKeyMetaData) {
                    final ForeignKeyMetaData fkmd2 = (ForeignKeyMetaData)md;
                    fkmd2.addColumn(colmd);
                }
                else if (md instanceof IndexMetaData) {
                    final IndexMetaData idxmd = (IndexMetaData)md;
                    idxmd.addColumn(colmd);
                }
                else if (md instanceof UniqueMetaData) {
                    final UniqueMetaData unimd = (UniqueMetaData)md;
                    unimd.addColumn(colmd);
                }
                else if (md instanceof OrderMetaData) {
                    final OrderMetaData ormd = (OrderMetaData)md;
                    ormd.addColumn(colmd);
                }
                else if (md instanceof DiscriminatorMetaData) {
                    final DiscriminatorMetaData dismd2 = (DiscriminatorMetaData)md;
                    dismd2.setColumnMetaData(colmd);
                }
                else if (md instanceof VersionMetaData) {
                    final VersionMetaData vermd = (VersionMetaData)md;
                    vermd.setColumnMetaData(colmd);
                }
                else if (md instanceof AbstractClassMetaData) {
                    final AbstractClassMetaData cmd5 = (AbstractClassMetaData)md;
                    cmd5.addUnmappedColumn(colmd);
                }
                else if (md instanceof PrimaryKeyMetaData) {
                    final PrimaryKeyMetaData pkmd2 = (PrimaryKeyMetaData)md;
                    pkmd2.addColumn(colmd);
                }
                this.pushStack(colmd);
            }
            else if (localName.equals("element")) {
                final AbstractMemberMetaData fmd3 = (AbstractMemberMetaData)this.getStack();
                final ElementMetaData elemmd = new ElementMetaData();
                elemmd.setColumnName(this.getAttr(attrs, "column"));
                elemmd.setDeleteAction(this.getAttr(attrs, "delete-action"));
                elemmd.setUpdateAction(this.getAttr(attrs, "update-action"));
                elemmd.setIndexed(IndexedValue.getIndexedValue(this.getAttr(attrs, "indexed")));
                elemmd.setUnique(MetaDataUtils.getBooleanForString(this.getAttr(attrs, "unique"), false));
                final String mappedBy = this.getAttr(attrs, "mapped-by");
                elemmd.setMappedBy(mappedBy);
                if (!StringUtils.isWhitespace(mappedBy) && fmd3.getMappedBy() == null) {
                    fmd3.setMappedBy(mappedBy);
                }
                fmd3.setElementMetaData(elemmd);
                this.pushStack(elemmd);
            }
            else if (localName.equals("key")) {
                final AbstractMemberMetaData fmd3 = (AbstractMemberMetaData)this.getStack();
                final KeyMetaData keymd = new KeyMetaData();
                keymd.setColumnName(this.getAttr(attrs, "column"));
                keymd.setDeleteAction(this.getAttr(attrs, "delete-action"));
                keymd.setUpdateAction(this.getAttr(attrs, "update-action"));
                keymd.setIndexed(IndexedValue.getIndexedValue(this.getAttr(attrs, "indexed")));
                keymd.setUnique(MetaDataUtils.getBooleanForString(this.getAttr(attrs, "unique"), false));
                keymd.setMappedBy(this.getAttr(attrs, "mapped-by"));
                fmd3.setKeyMetaData(keymd);
                this.pushStack(keymd);
            }
            else if (localName.equals("value")) {
                final AbstractMemberMetaData fmd3 = (AbstractMemberMetaData)this.getStack();
                final ValueMetaData valuemd = new ValueMetaData();
                valuemd.setColumnName(this.getAttr(attrs, "column"));
                valuemd.setDeleteAction(this.getAttr(attrs, "delete-action"));
                valuemd.setUpdateAction(this.getAttr(attrs, "update-action"));
                valuemd.setIndexed(IndexedValue.getIndexedValue(this.getAttr(attrs, "indexed")));
                valuemd.setUnique(MetaDataUtils.getBooleanForString(this.getAttr(attrs, "unique"), false));
                valuemd.setMappedBy(this.getAttr(attrs, "mapped-by"));
                fmd3.setValueMetaData(valuemd);
                this.pushStack(valuemd);
            }
            else if (localName.equals("fetch-group")) {
                final MetaData md = this.getStack();
                final FetchGroupMetaData fgmd = new FetchGroupMetaData(this.getAttr(attrs, "name"));
                final String postLoadStr = this.getAttr(attrs, "post-load");
                if (!StringUtils.isWhitespace(postLoadStr)) {
                    fgmd.setPostLoad(Boolean.valueOf(postLoadStr));
                }
                if (md instanceof FetchGroupMetaData) {
                    final FetchGroupMetaData fgmdParent = (FetchGroupMetaData)md;
                    fgmdParent.addFetchGroup(fgmd);
                }
                else if (md instanceof AbstractClassMetaData) {
                    final AbstractClassMetaData cmd5 = (AbstractClassMetaData)md;
                    cmd5.addFetchGroup(fgmd);
                }
                else if (md instanceof FetchPlanMetaData) {
                    final FetchPlanMetaData fpmd2 = (FetchPlanMetaData)md;
                    fpmd2.addFetchGroup(fgmd);
                }
                this.pushStack(fgmd);
            }
            else if (localName.equals("extension")) {
                final MetaData md = this.getStack();
                md.addExtension(this.getAttr(attrs, "vendor-name"), this.getAttr(attrs, "key"), this.getAttr(attrs, "value"));
            }
            else if (localName.equals("version")) {
                final AbstractClassMetaData cmd6 = (AbstractClassMetaData)this.getStack();
                final VersionMetaData vermd2 = cmd6.newVersionMetadata();
                vermd2.setStrategy(this.getAttr(attrs, "strategy")).setColumnName(this.getAttr(attrs, "column"));
                vermd2.setIndexed(IndexedValue.getIndexedValue(this.getAttr(attrs, "indexed")));
                this.pushStack(vermd2);
            }
            else if (localName.equals("index")) {
                final MetaData md = this.getStack();
                final IndexMetaData idxmd2 = new IndexMetaData();
                idxmd2.setName(this.getAttr(attrs, "name"));
                idxmd2.setTable(this.getAttr(attrs, "table"));
                final String uniStr = this.getAttr(attrs, "unique");
                if (!StringUtils.isWhitespace(uniStr)) {
                    idxmd2.setUnique(Boolean.valueOf(uniStr));
                }
                if (md instanceof AbstractClassMetaData) {
                    final AbstractClassMetaData cmd5 = (AbstractClassMetaData)md;
                    cmd5.addIndex(idxmd2);
                }
                else if (md instanceof AbstractMemberMetaData) {
                    final AbstractMemberMetaData fmd4 = (AbstractMemberMetaData)md;
                    fmd4.setIndexMetaData(idxmd2);
                }
                else if (md instanceof JoinMetaData) {
                    final JoinMetaData jmd = (JoinMetaData)md;
                    jmd.setIndexMetaData(idxmd2);
                }
                else if (md instanceof AbstractElementMetaData) {
                    final AbstractElementMetaData elmd = (AbstractElementMetaData)md;
                    elmd.setIndexMetaData(idxmd2);
                }
                else if (md instanceof OrderMetaData) {
                    final OrderMetaData omd = (OrderMetaData)md;
                    omd.setIndexMetaData(idxmd2);
                }
                else if (md instanceof VersionMetaData) {
                    final VersionMetaData vermd = (VersionMetaData)md;
                    vermd.setIndexMetaData(idxmd2);
                }
                else if (md instanceof DiscriminatorMetaData) {
                    final DiscriminatorMetaData dismd2 = (DiscriminatorMetaData)md;
                    dismd2.setIndexMetaData(idxmd2);
                }
                this.pushStack(idxmd2);
            }
            else if (localName.equals("unique")) {
                final MetaData md = this.getStack();
                final UniqueMetaData unimd2 = new UniqueMetaData();
                unimd2.setName(this.getAttr(attrs, "name"));
                unimd2.setTable(this.getAttr(attrs, "table"));
                final String defStr = this.getAttr(attrs, "deferred");
                if (!StringUtils.isWhitespace(defStr)) {
                    unimd2.setDeferred(Boolean.valueOf(defStr));
                }
                if (md instanceof AbstractClassMetaData) {
                    final AbstractClassMetaData cmd5 = (AbstractClassMetaData)md;
                    cmd5.addUniqueConstraint(unimd2);
                }
                else if (md instanceof AbstractMemberMetaData) {
                    final AbstractMemberMetaData fmd4 = (AbstractMemberMetaData)md;
                    fmd4.setUniqueMetaData(unimd2);
                }
                else if (md instanceof JoinMetaData) {
                    final JoinMetaData jmd = (JoinMetaData)md;
                    jmd.setUniqueMetaData(unimd2);
                }
                else if (md instanceof AbstractElementMetaData) {
                    final AbstractElementMetaData elmd = (AbstractElementMetaData)md;
                    elmd.setUniqueMetaData(unimd2);
                }
                this.pushStack(unimd2);
            }
            else if (localName.equals("foreign-key")) {
                final MetaData md = this.getStack();
                final ForeignKeyMetaData fkmd3 = new ForeignKeyMetaData();
                fkmd3.setName(this.getAttr(attrs, "name"));
                fkmd3.setTable(this.getAttr(attrs, "table"));
                fkmd3.setUnique(this.getAttr(attrs, "unique"));
                fkmd3.setDeferred(this.getAttr(attrs, "deferred"));
                fkmd3.setDeleteAction(ForeignKeyAction.getForeignKeyAction(this.getAttr(attrs, "delete-action")));
                fkmd3.setUpdateAction(ForeignKeyAction.getForeignKeyAction(this.getAttr(attrs, "update-action")));
                if (md instanceof AbstractClassMetaData) {
                    final AbstractClassMetaData cmd7 = (AbstractClassMetaData)md;
                    cmd7.addForeignKey(fkmd3);
                }
                else if (md instanceof AbstractMemberMetaData) {
                    final AbstractMemberMetaData fmd5 = (AbstractMemberMetaData)md;
                    fmd5.setForeignKeyMetaData(fkmd3);
                }
                else if (md instanceof JoinMetaData) {
                    final JoinMetaData jmd2 = (JoinMetaData)md;
                    jmd2.setForeignKeyMetaData(fkmd3);
                }
                else if (md instanceof AbstractElementMetaData) {
                    final AbstractElementMetaData elmd2 = (AbstractElementMetaData)md;
                    elmd2.setForeignKeyMetaData(fkmd3);
                }
                this.pushStack(fkmd3);
            }
            else if (localName.equals("order")) {
                final OrderMetaData ordmd = new OrderMetaData();
                ordmd.setIndexed(IndexedValue.getIndexedValue(this.getAttr(attrs, "indexed")));
                ordmd.setColumnName(this.getAttr(attrs, "column"));
                ordmd.setMappedBy(this.getAttr(attrs, "mapped-by"));
                final AbstractMemberMetaData fmd6 = (AbstractMemberMetaData)this.getStack();
                fmd6.setOrderMetaData(ordmd);
                this.pushStack(ordmd);
            }
            else {
                if (!localName.equals("embedded")) {
                    final String message = JDOMetaDataHandler.LOCALISER.msg("044037", qName);
                    NucleusLogger.METADATA.error(message);
                    throw new RuntimeException(message);
                }
                final MetaData md = this.getStack();
                final EmbeddedMetaData embmd = new EmbeddedMetaData();
                embmd.setOwnerMember(this.getAttr(attrs, "owner-field"));
                embmd.setNullIndicatorColumn(this.getAttr(attrs, "null-indicator-column"));
                embmd.setNullIndicatorValue(this.getAttr(attrs, "null-indicator-value"));
                if (md instanceof AbstractMemberMetaData) {
                    final AbstractMemberMetaData fmd5 = (AbstractMemberMetaData)md;
                    fmd5.setEmbeddedMetaData(embmd);
                }
                else if (md instanceof KeyMetaData) {
                    final KeyMetaData kmd = (KeyMetaData)md;
                    kmd.setEmbeddedMetaData(embmd);
                }
                else if (md instanceof ValueMetaData) {
                    final ValueMetaData vmd = (ValueMetaData)md;
                    vmd.setEmbeddedMetaData(embmd);
                }
                else if (md instanceof ElementMetaData) {
                    final ElementMetaData elmd3 = (ElementMetaData)md;
                    elmd3.setEmbeddedMetaData(embmd);
                }
                this.pushStack(embmd);
            }
        }
        catch (RuntimeException ex) {
            NucleusLogger.METADATA.error(JDOMetaDataHandler.LOCALISER.msg("044042", qName, this.getStack(), uri), ex);
            throw ex;
        }
    }
    
    @Override
    public void endElement(final String uri, String localName, final String qName) throws SAXException {
        if (localName.length() < 1) {
            localName = qName;
        }
        final String currentString = this.getString().trim();
        if (currentString.length() > 0) {
            final MetaData md = this.getStack();
            if (localName.equals("query")) {
                ((QueryMetaData)md).setQuery(currentString);
            }
        }
        if (localName.equals("package") || localName.equals("fetch-plan") || localName.equals("class") || localName.equals("interface") || localName.equals("implements") || localName.equals("property") || localName.equals("datastore-identity") || localName.equals("inheritance") || localName.equals("primary-key") || localName.equals("version") || localName.equals("unmapped") || localName.equals("query") || localName.equals("sequence") || localName.equals("field") || localName.equals("map") || localName.equals("element") || localName.equals("embedded") || localName.equals("key") || localName.equals("value") || localName.equals("array") || localName.equals("collection") || localName.equals("join") || localName.equals("index") || localName.equals("unique") || localName.equals("foreign-key") || localName.equals("order") || localName.equals("fetch-group") || localName.equals("column") || localName.equals("discriminator")) {
            this.popStack();
        }
    }
}
