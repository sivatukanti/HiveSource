// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.metadata;

import javax.jdo.metadata.Metadata;
import org.datanucleus.metadata.AbstractClassMetaData;
import java.lang.reflect.Field;
import org.datanucleus.metadata.FieldMetaData;
import javax.jdo.metadata.FieldMetadata;
import javax.jdo.metadata.ClassPersistenceModifier;
import org.datanucleus.metadata.MetaData;
import org.datanucleus.metadata.ClassMetaData;
import javax.jdo.metadata.ClassMetadata;

public class ClassMetadataImpl extends TypeMetadataImpl implements ClassMetadata
{
    public ClassMetadataImpl(final ClassMetaData internal) {
        super(internal);
    }
    
    @Override
    public ClassMetaData getInternal() {
        return (ClassMetaData)this.internalMD;
    }
    
    public ClassPersistenceModifier getPersistenceModifier() {
        final org.datanucleus.metadata.ClassPersistenceModifier mod = this.getInternal().getPersistenceModifier();
        if (mod == org.datanucleus.metadata.ClassPersistenceModifier.PERSISTENCE_CAPABLE) {
            return ClassPersistenceModifier.PERSISTENCE_CAPABLE;
        }
        if (mod == org.datanucleus.metadata.ClassPersistenceModifier.PERSISTENCE_AWARE) {
            return ClassPersistenceModifier.PERSISTENCE_AWARE;
        }
        return ClassPersistenceModifier.NON_PERSISTENT;
    }
    
    public ClassMetadata setPersistenceModifier(final ClassPersistenceModifier mod) {
        if (mod == ClassPersistenceModifier.PERSISTENCE_CAPABLE) {
            this.getInternal().setPersistenceModifier(org.datanucleus.metadata.ClassPersistenceModifier.PERSISTENCE_CAPABLE);
        }
        else if (mod == ClassPersistenceModifier.PERSISTENCE_AWARE) {
            this.getInternal().setPersistenceModifier(org.datanucleus.metadata.ClassPersistenceModifier.PERSISTENCE_AWARE);
        }
        else if (mod == ClassPersistenceModifier.NON_PERSISTENT) {
            this.getInternal().setPersistenceModifier(org.datanucleus.metadata.ClassPersistenceModifier.NON_PERSISTENT);
        }
        return this;
    }
    
    public FieldMetadata newFieldMetadata(final String name) {
        final FieldMetaData internalFmd = this.getInternal().newFieldMetadata(name);
        final FieldMetadataImpl fmd = new FieldMetadataImpl(internalFmd);
        fmd.parent = this;
        return fmd;
    }
    
    public FieldMetadata newFieldMetadata(final Field fld) {
        final FieldMetaData internalFmd = this.getInternal().newFieldMetadata(fld.getName());
        final FieldMetadataImpl fmd = new FieldMetadataImpl(internalFmd);
        fmd.parent = this;
        return fmd;
    }
    
    @Override
    public AbstractMetadataImpl getParent() {
        if (this.parent == null) {
            this.parent = new PackageMetadataImpl(((ClassMetaData)this.internalMD).getPackageMetaData());
        }
        return super.getParent();
    }
}
