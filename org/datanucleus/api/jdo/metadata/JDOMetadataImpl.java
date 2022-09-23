// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.metadata;

import org.datanucleus.metadata.InterfaceMetaData;
import javax.jdo.metadata.InterfaceMetadata;
import org.datanucleus.metadata.ClassMetaData;
import org.datanucleus.util.ClassUtils;
import javax.jdo.metadata.ClassMetadata;
import org.datanucleus.metadata.PackageMetaData;
import javax.jdo.metadata.PackageMetadata;
import org.datanucleus.metadata.QueryMetaData;
import javax.jdo.metadata.QueryMetadata;
import org.datanucleus.metadata.FetchPlanMetaData;
import javax.jdo.metadata.FetchPlanMetadata;
import org.datanucleus.metadata.MetadataFileType;
import org.datanucleus.metadata.MetaData;
import org.datanucleus.metadata.FileMetaData;
import javax.jdo.metadata.JDOMetadata;

public class JDOMetadataImpl extends AbstractMetadataImpl implements JDOMetadata
{
    public JDOMetadataImpl() {
        super(new FileMetaData());
        this.getInternal().setType(MetadataFileType.JDO_FILE);
    }
    
    public JDOMetadataImpl(final FileMetaData filemd) {
        super(filemd);
    }
    
    public FileMetaData getInternal() {
        return (FileMetaData)this.internalMD;
    }
    
    public FetchPlanMetadata[] getFetchPlans() {
        final FetchPlanMetaData[] baseFps = this.getInternal().getFetchPlans();
        if (baseFps == null) {
            return null;
        }
        final FetchPlanMetadataImpl[] fps = new FetchPlanMetadataImpl[baseFps.length];
        for (int i = 0; i < fps.length; ++i) {
            fps[i] = new FetchPlanMetadataImpl(baseFps[i]);
            fps[i].parent = this;
        }
        return fps;
    }
    
    public FetchPlanMetadata newFetchPlanMetadata(final String name) {
        final FetchPlanMetaData internalFpmd = this.getInternal().newFetchPlanMetadata(name);
        final FetchPlanMetadataImpl fpmd = new FetchPlanMetadataImpl(internalFpmd);
        fpmd.parent = this;
        return fpmd;
    }
    
    public int getNumberOfFetchPlans() {
        return this.getInternal().getNoOfFetchPlans();
    }
    
    public QueryMetadata[] getQueries() {
        final QueryMetaData[] baseQueries = this.getInternal().getQueries();
        if (baseQueries == null) {
            return null;
        }
        final QueryMetadataImpl[] queries = new QueryMetadataImpl[this.getInternal().getNoOfQueries()];
        for (int i = 0; i < queries.length; ++i) {
            queries[i] = new QueryMetadataImpl(baseQueries[i]);
            queries[i].parent = this;
        }
        return queries;
    }
    
    public int getNumberOfQueries() {
        return this.getInternal().getNoOfQueries();
    }
    
    public QueryMetadata newQueryMetadata(final String name) {
        final QueryMetaData internalQmd = this.getInternal().newQueryMetadata(name);
        final QueryMetadataImpl qmd = new QueryMetadataImpl(internalQmd);
        qmd.parent = this;
        return qmd;
    }
    
    public PackageMetadata[] getPackages() {
        final PackageMetadataImpl[] pmds = new PackageMetadataImpl[this.getInternal().getNoOfPackages()];
        for (int i = 0; i < pmds.length; ++i) {
            pmds[i] = new PackageMetadataImpl(this.getInternal().getPackage(i));
            pmds[i].parent = this;
        }
        return pmds;
    }
    
    public int getNumberOfPackages() {
        return this.getInternal().getNoOfPackages();
    }
    
    public PackageMetadata newPackageMetadata(final String name) {
        final PackageMetaData internalPmd = this.getInternal().newPackageMetadata(name);
        final PackageMetadataImpl pmd = new PackageMetadataImpl(internalPmd);
        pmd.parent = this;
        return pmd;
    }
    
    public PackageMetadata newPackageMetadata(final Package pkg) {
        final PackageMetaData internalPmd = this.getInternal().newPackageMetadata(pkg.getName());
        final PackageMetadataImpl pmd = new PackageMetadataImpl(internalPmd);
        pmd.parent = this;
        return pmd;
    }
    
    public ClassMetadata newClassMetadata(final Class cls) {
        final String packageName = ClassUtils.getPackageNameForClass(cls);
        final PackageMetaData internalPmd = this.getInternal().newPackageMetadata(packageName);
        final PackageMetadataImpl pmd = new PackageMetadataImpl(internalPmd);
        pmd.parent = this;
        final String className = ClassUtils.getClassNameForClass(cls);
        final ClassMetaData internalCmd = internalPmd.newClassMetadata(className);
        final ClassMetadataImpl cmd = new ClassMetadataImpl(internalCmd);
        cmd.parent = pmd;
        return cmd;
    }
    
    public InterfaceMetadata newInterfaceMetadata(final Class cls) {
        final String packageName = ClassUtils.getPackageNameForClass(cls);
        final PackageMetaData internalPmd = this.getInternal().newPackageMetadata(packageName);
        final PackageMetadataImpl pmd = new PackageMetadataImpl(internalPmd);
        pmd.parent = this;
        final String className = ClassUtils.getClassNameForClass(cls);
        final InterfaceMetaData internalImd = internalPmd.newInterfaceMetadata(className);
        final InterfaceMetadataImpl imd = new InterfaceMetadataImpl(internalImd);
        imd.parent = pmd;
        return imd;
    }
    
    public String getCatalog() {
        return this.getInternal().getCatalog();
    }
    
    public JDOMetadata setCatalog(final String cat) {
        this.getInternal().setCatalog(cat);
        return this;
    }
    
    public String getSchema() {
        return this.getInternal().getSchema();
    }
    
    public JDOMetadata setSchema(final String sch) {
        this.getInternal().setSchema(sch);
        return this;
    }
}
