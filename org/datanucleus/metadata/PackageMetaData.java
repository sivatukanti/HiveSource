// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

import java.util.HashSet;
import org.datanucleus.util.StringUtils;
import java.util.ArrayList;
import java.util.Iterator;
import org.datanucleus.ClassLoaderResolver;
import java.util.Collection;
import java.util.List;

public class PackageMetaData extends MetaData
{
    protected List<InterfaceMetaData> interfaces;
    protected List<ClassMetaData> classes;
    protected Collection<SequenceMetaData> sequences;
    protected Collection<TableGeneratorMetaData> tableGenerators;
    protected final String name;
    protected String catalog;
    protected String schema;
    
    PackageMetaData(final String name) {
        this.interfaces = null;
        this.classes = null;
        this.sequences = null;
        this.tableGenerators = null;
        this.name = ((name != null) ? name : "");
    }
    
    @Override
    public void initialise(final ClassLoaderResolver clr, final MetaDataManager mmgr) {
        if (this.catalog == null && ((FileMetaData)this.parent).getCatalog() != null) {
            this.catalog = ((FileMetaData)this.parent).getCatalog();
        }
        if (this.schema == null && ((FileMetaData)this.parent).getSchema() != null) {
            this.schema = ((FileMetaData)this.parent).getSchema();
        }
        super.initialise(clr, mmgr);
    }
    
    public FileMetaData getFileMetaData() {
        if (this.parent != null) {
            return (FileMetaData)this.parent;
        }
        return null;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getCatalog() {
        return this.catalog;
    }
    
    public String getSchema() {
        return this.schema;
    }
    
    public int getNoOfInterfaces() {
        return (this.interfaces != null) ? this.interfaces.size() : 0;
    }
    
    public InterfaceMetaData getInterface(final int i) {
        return this.interfaces.get(i);
    }
    
    public InterfaceMetaData getInterface(final String name) {
        for (final InterfaceMetaData imd : this.interfaces) {
            if (imd.getName().equals(name)) {
                return imd;
            }
        }
        return null;
    }
    
    public int getNoOfClasses() {
        return (this.classes != null) ? this.classes.size() : 0;
    }
    
    public ClassMetaData getClass(final int i) {
        return this.classes.get(i);
    }
    
    public ClassMetaData getClass(final String name) {
        for (final ClassMetaData cmd : this.classes) {
            if (cmd.getName().equals(name)) {
                return cmd;
            }
        }
        return null;
    }
    
    public int getNoOfSequences() {
        return (this.sequences != null) ? this.sequences.size() : 0;
    }
    
    public SequenceMetaData[] getSequences() {
        return (SequenceMetaData[])((this.sequences == null) ? null : ((SequenceMetaData[])this.sequences.toArray(new SequenceMetaData[this.sequences.size()])));
    }
    
    public SequenceMetaData getSequence(final String name) {
        for (final SequenceMetaData seqmd : this.sequences) {
            if (seqmd.getName().equals(name)) {
                return seqmd;
            }
        }
        return null;
    }
    
    public int getNoOfTableGenerators() {
        return (this.tableGenerators != null) ? this.tableGenerators.size() : 0;
    }
    
    public TableGeneratorMetaData[] getTableGenerators() {
        return (TableGeneratorMetaData[])((this.tableGenerators == null) ? null : ((TableGeneratorMetaData[])this.tableGenerators.toArray(new TableGeneratorMetaData[this.tableGenerators.size()])));
    }
    
    public TableGeneratorMetaData getTableGenerator(final String name) {
        for (final TableGeneratorMetaData tgmd : this.tableGenerators) {
            if (tgmd.getName().equals(name)) {
                return tgmd;
            }
        }
        return null;
    }
    
    public ClassMetaData addClass(final ClassMetaData cmd) {
        if (cmd == null) {
            return null;
        }
        if (this.classes == null) {
            this.classes = new ArrayList<ClassMetaData>();
        }
        else {
            for (final AbstractClassMetaData c : this.classes) {
                if (cmd.getName().equals(c.getName()) && c instanceof ClassMetaData) {
                    return (ClassMetaData)c;
                }
            }
        }
        this.classes.add(cmd);
        cmd.parent = this;
        return cmd;
    }
    
    public ClassMetaData newClassMetadata(final String className) {
        if (StringUtils.isWhitespace(className)) {
            throw new InvalidClassMetaDataException(PackageMetaData.LOCALISER, "044061", this.name);
        }
        final ClassMetaData cmd = new ClassMetaData(this, className);
        return this.addClass(cmd);
    }
    
    public InterfaceMetaData addInterface(final InterfaceMetaData imd) {
        if (imd == null) {
            return null;
        }
        if (this.interfaces == null) {
            this.interfaces = new ArrayList<InterfaceMetaData>();
        }
        else {
            for (final AbstractClassMetaData c : this.interfaces) {
                if (imd.getName().equals(c.getName()) && c instanceof InterfaceMetaData) {
                    return (InterfaceMetaData)c;
                }
            }
        }
        this.interfaces.add(imd);
        imd.parent = this;
        return imd;
    }
    
    public InterfaceMetaData newInterfaceMetadata(final String intfName) {
        final InterfaceMetaData imd = new InterfaceMetaData(this, intfName);
        return this.addInterface(imd);
    }
    
    public void addSequence(final SequenceMetaData seqmd) {
        if (seqmd == null) {
            return;
        }
        if (this.sequences == null) {
            this.sequences = new HashSet<SequenceMetaData>();
        }
        this.sequences.add(seqmd);
        seqmd.parent = this;
    }
    
    public SequenceMetaData newSequenceMetadata(final String seqName, final String seqStrategy) {
        final SequenceMetaData seqmd = new SequenceMetaData(seqName, seqStrategy);
        this.addSequence(seqmd);
        return seqmd;
    }
    
    public void addTableGenerator(final TableGeneratorMetaData tabmd) {
        if (tabmd == null) {
            return;
        }
        if (this.tableGenerators == null) {
            this.tableGenerators = new HashSet<TableGeneratorMetaData>();
        }
        this.tableGenerators.add(tabmd);
        tabmd.parent = this;
    }
    
    public TableGeneratorMetaData newTableGeneratorMetadata(final String name) {
        final TableGeneratorMetaData tgmd = new TableGeneratorMetaData(name);
        if (this.tableGenerators == null) {
            this.tableGenerators = new HashSet<TableGeneratorMetaData>();
        }
        this.tableGenerators.add(tgmd);
        tgmd.parent = this;
        return tgmd;
    }
    
    public PackageMetaData setCatalog(final String catalog) {
        this.catalog = (StringUtils.isWhitespace(catalog) ? null : catalog);
        return this;
    }
    
    public PackageMetaData setSchema(final String schema) {
        this.schema = (StringUtils.isWhitespace(schema) ? null : schema);
        return this;
    }
    
    @Override
    public String toString(final String prefix, final String indent) {
        final StringBuffer sb = new StringBuffer();
        sb.append(prefix).append("<package name=\"" + this.name + "\"");
        if (this.catalog != null) {
            sb.append(" catalog=\"" + this.catalog + "\"");
        }
        if (this.schema != null) {
            sb.append(" schema=\"" + this.schema + "\"");
        }
        sb.append(">\n");
        if (this.interfaces != null) {
            final Iterator int_iter = this.interfaces.iterator();
            while (int_iter.hasNext()) {
                sb.append(int_iter.next().toString(prefix + indent, indent));
            }
        }
        if (this.classes != null) {
            final Iterator cls_iter = this.classes.iterator();
            while (cls_iter.hasNext()) {
                sb.append(cls_iter.next().toString(prefix + indent, indent));
            }
        }
        if (this.sequences != null) {
            final Iterator seq_iter = this.sequences.iterator();
            while (seq_iter.hasNext()) {
                sb.append(seq_iter.next().toString(prefix + indent, indent));
            }
        }
        sb.append(super.toString(prefix + indent, indent));
        sb.append(prefix).append("</package>\n");
        return sb.toString();
    }
}
