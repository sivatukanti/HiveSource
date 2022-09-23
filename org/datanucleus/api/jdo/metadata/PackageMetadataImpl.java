// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.metadata;

import javax.jdo.metadata.Metadata;
import javax.jdo.annotations.SequenceStrategy;
import org.datanucleus.metadata.SequenceMetaData;
import javax.jdo.metadata.SequenceMetadata;
import org.datanucleus.metadata.InterfaceMetaData;
import javax.jdo.metadata.InterfaceMetadata;
import org.datanucleus.util.ClassUtils;
import javax.jdo.JDOUserException;
import org.datanucleus.metadata.ClassMetaData;
import javax.jdo.metadata.ClassMetadata;
import org.datanucleus.metadata.MetaData;
import org.datanucleus.metadata.PackageMetaData;
import javax.jdo.metadata.PackageMetadata;

public class PackageMetadataImpl extends AbstractMetadataImpl implements PackageMetadata
{
    public PackageMetadataImpl(final PackageMetaData pmd) {
        super(pmd);
    }
    
    public PackageMetaData getInternal() {
        return (PackageMetaData)this.internalMD;
    }
    
    public String getName() {
        return this.getInternal().getName();
    }
    
    public String getCatalog() {
        return this.getInternal().getCatalog();
    }
    
    public PackageMetadata setCatalog(final String cat) {
        this.getInternal().setCatalog(cat);
        return this;
    }
    
    public String getSchema() {
        return this.getInternal().getSchema();
    }
    
    public PackageMetadata setSchema(final String sch) {
        this.getInternal().setSchema(sch);
        return this;
    }
    
    public ClassMetadata[] getClasses() {
        final ClassMetadataImpl[] classes = new ClassMetadataImpl[this.getNumberOfClasses()];
        for (int i = 0; i < classes.length; ++i) {
            classes[i] = new ClassMetadataImpl(this.getInternal().getClass(i));
            classes[i].parent = this;
        }
        return classes;
    }
    
    public int getNumberOfClasses() {
        return this.getInternal().getNoOfClasses();
    }
    
    public ClassMetadata newClassMetadata(final String name) {
        final ClassMetaData internalCmd = this.getInternal().newClassMetadata(name);
        final ClassMetadataImpl cmd = new ClassMetadataImpl(internalCmd);
        cmd.parent = this;
        return cmd;
    }
    
    public ClassMetadata newClassMetadata(final Class cls) {
        if (cls.isInterface()) {
            throw new JDOUserException("Canot create new class metadata for " + cls.getName() + " since it is an interface!");
        }
        final ClassMetaData internalCmd = this.getInternal().newClassMetadata(ClassUtils.getClassNameForClass(cls));
        final ClassMetadataImpl cmd = new ClassMetadataImpl(internalCmd);
        cmd.parent = this;
        return cmd;
    }
    
    public InterfaceMetadata[] getInterfaces() {
        final InterfaceMetadataImpl[] interfaces = new InterfaceMetadataImpl[this.getNumberOfInterfaces()];
        for (int i = 0; i < interfaces.length; ++i) {
            interfaces[i] = new InterfaceMetadataImpl(this.getInternal().getInterface(i));
            interfaces[i].parent = this;
        }
        return interfaces;
    }
    
    public int getNumberOfInterfaces() {
        return this.getInternal().getNoOfInterfaces();
    }
    
    public InterfaceMetadata newInterfaceMetadata(final String name) {
        final InterfaceMetaData internalImd = this.getInternal().newInterfaceMetadata(name);
        final InterfaceMetadataImpl imd = new InterfaceMetadataImpl(internalImd);
        imd.parent = this;
        return imd;
    }
    
    public InterfaceMetadata newInterfaceMetadata(final Class cls) {
        if (!cls.isInterface()) {
            throw new JDOUserException("Canot create new interface metadata for " + cls.getName() + " since not interface!");
        }
        final InterfaceMetaData internalImd = this.getInternal().newInterfaceMetadata(ClassUtils.getClassNameForClass(cls));
        final InterfaceMetadataImpl imd = new InterfaceMetadataImpl(internalImd);
        imd.parent = this;
        return imd;
    }
    
    public SequenceMetadata[] getSequences() {
        final SequenceMetaData[] internalSeqmds = this.getInternal().getSequences();
        if (internalSeqmds == null) {
            return null;
        }
        final SequenceMetadataImpl[] seqmds = new SequenceMetadataImpl[internalSeqmds.length];
        for (int i = 0; i < seqmds.length; ++i) {
            seqmds[i] = new SequenceMetadataImpl(internalSeqmds[i]);
            seqmds[i].parent = this;
        }
        return seqmds;
    }
    
    public int getNumberOfSequences() {
        return this.getInternal().getNoOfSequences();
    }
    
    public SequenceMetadata newSequenceMetadata(final String name, final SequenceStrategy strategy) {
        String str = null;
        if (strategy == SequenceStrategy.CONTIGUOUS) {
            str = org.datanucleus.metadata.SequenceStrategy.CONTIGUOUS.toString();
        }
        else if (strategy == SequenceStrategy.NONCONTIGUOUS) {
            str = org.datanucleus.metadata.SequenceStrategy.NONCONTIGUOUS.toString();
        }
        else if (strategy == SequenceStrategy.NONTRANSACTIONAL) {
            str = org.datanucleus.metadata.SequenceStrategy.NONTRANSACTIONAL.toString();
        }
        final SequenceMetaData internalSeqmd = this.getInternal().newSequenceMetadata(name, str);
        final SequenceMetadataImpl seqmd = new SequenceMetadataImpl(internalSeqmd);
        seqmd.parent = this;
        return seqmd;
    }
    
    @Override
    public AbstractMetadataImpl getParent() {
        if (this.parent == null) {
            this.parent = new JDOMetadataImpl(((PackageMetaData)this.internalMD).getFileMetaData());
        }
        return super.getParent();
    }
}
