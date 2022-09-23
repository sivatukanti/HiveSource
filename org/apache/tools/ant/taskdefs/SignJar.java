// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.taskdefs.condition.IsSigned;
import java.io.IOException;
import org.apache.tools.ant.types.resources.FileResource;
import java.util.Iterator;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.ResourceUtils;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.util.IdentityMapper;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.FileNameMapper;
import java.io.File;
import org.apache.tools.ant.util.FileUtils;

public class SignJar extends AbstractJarSignerTask
{
    private static final FileUtils FILE_UTILS;
    protected String sigfile;
    protected File signedjar;
    protected boolean internalsf;
    protected boolean sectionsonly;
    private boolean preserveLastModified;
    protected boolean lazy;
    protected File destDir;
    private FileNameMapper mapper;
    protected String tsaurl;
    protected String tsacert;
    private boolean force;
    private String sigAlg;
    private String digestAlg;
    public static final String ERROR_TODIR_AND_SIGNEDJAR = "'destdir' and 'signedjar' cannot both be set";
    public static final String ERROR_TOO_MANY_MAPPERS = "Too many mappers";
    public static final String ERROR_SIGNEDJAR_AND_PATHS = "You cannot specify the signed JAR when using paths or filesets";
    public static final String ERROR_BAD_MAP = "Cannot map source file to anything sensible: ";
    public static final String ERROR_MAPPER_WITHOUT_DEST = "The destDir attribute is required if a mapper is set";
    public static final String ERROR_NO_ALIAS = "alias attribute must be set";
    public static final String ERROR_NO_STOREPASS = "storepass attribute must be set";
    
    public SignJar() {
        this.force = false;
    }
    
    public void setSigfile(final String sigfile) {
        this.sigfile = sigfile;
    }
    
    public void setSignedjar(final File signedjar) {
        this.signedjar = signedjar;
    }
    
    public void setInternalsf(final boolean internalsf) {
        this.internalsf = internalsf;
    }
    
    public void setSectionsonly(final boolean sectionsonly) {
        this.sectionsonly = sectionsonly;
    }
    
    public void setLazy(final boolean lazy) {
        this.lazy = lazy;
    }
    
    public void setDestDir(final File destDir) {
        this.destDir = destDir;
    }
    
    public void add(final FileNameMapper newMapper) {
        if (this.mapper != null) {
            throw new BuildException("Too many mappers");
        }
        this.mapper = newMapper;
    }
    
    public FileNameMapper getMapper() {
        return this.mapper;
    }
    
    public String getTsaurl() {
        return this.tsaurl;
    }
    
    public void setTsaurl(final String tsaurl) {
        this.tsaurl = tsaurl;
    }
    
    public String getTsacert() {
        return this.tsacert;
    }
    
    public void setTsacert(final String tsacert) {
        this.tsacert = tsacert;
    }
    
    public void setForce(final boolean b) {
        this.force = b;
    }
    
    public boolean isForce() {
        return this.force;
    }
    
    public void setSigAlg(final String sigAlg) {
        this.sigAlg = sigAlg;
    }
    
    public String getSigAlg() {
        return this.sigAlg;
    }
    
    public void setDigestAlg(final String digestAlg) {
        this.digestAlg = digestAlg;
    }
    
    public String getDigestAlg() {
        return this.digestAlg;
    }
    
    @Override
    public void execute() throws BuildException {
        final boolean hasJar = this.jar != null;
        final boolean hasSignedJar = this.signedjar != null;
        final boolean hasDestDir = this.destDir != null;
        final boolean hasMapper = this.mapper != null;
        if (!hasJar && !this.hasResources()) {
            throw new BuildException("jar must be set through jar attribute or nested filesets");
        }
        if (null == this.alias) {
            throw new BuildException("alias attribute must be set");
        }
        if (null == this.storepass) {
            throw new BuildException("storepass attribute must be set");
        }
        if (hasDestDir && hasSignedJar) {
            throw new BuildException("'destdir' and 'signedjar' cannot both be set");
        }
        if (this.hasResources() && hasSignedJar) {
            throw new BuildException("You cannot specify the signed JAR when using paths or filesets");
        }
        if (!hasDestDir && hasMapper) {
            throw new BuildException("The destDir attribute is required if a mapper is set");
        }
        this.beginExecution();
        try {
            if (hasJar && hasSignedJar) {
                this.signOneJar(this.jar, this.signedjar);
                return;
            }
            final Path sources = this.createUnifiedSourcePath();
            FileNameMapper destMapper;
            if (hasMapper) {
                destMapper = this.mapper;
            }
            else {
                destMapper = new IdentityMapper();
            }
            for (final Resource r : sources) {
                final FileResource fr = ResourceUtils.asFileResource(r.as(FileProvider.class));
                final File toDir = hasDestDir ? this.destDir : fr.getBaseDir();
                final String[] destFilenames = destMapper.mapFileName(fr.getName());
                if (destFilenames == null || destFilenames.length != 1) {
                    throw new BuildException("Cannot map source file to anything sensible: " + fr.getFile());
                }
                final File destFile = new File(toDir, destFilenames[0]);
                this.signOneJar(fr.getFile(), destFile);
            }
        }
        finally {
            this.endExecution();
        }
    }
    
    private void signOneJar(final File jarSource, final File jarTarget) throws BuildException {
        File targetFile = jarTarget;
        if (targetFile == null) {
            targetFile = jarSource;
        }
        if (this.isUpToDate(jarSource, targetFile)) {
            return;
        }
        final long lastModified = jarSource.lastModified();
        final ExecTask cmd = this.createJarSigner();
        this.setCommonOptions(cmd);
        this.bindToKeystore(cmd);
        if (null != this.sigfile) {
            this.addValue(cmd, "-sigfile");
            final String value = this.sigfile;
            this.addValue(cmd, value);
        }
        try {
            if (!SignJar.FILE_UTILS.areSame(jarSource, targetFile)) {
                this.addValue(cmd, "-signedjar");
                this.addValue(cmd, targetFile.getPath());
            }
        }
        catch (IOException ioex) {
            throw new BuildException(ioex);
        }
        if (this.internalsf) {
            this.addValue(cmd, "-internalsf");
        }
        if (this.sectionsonly) {
            this.addValue(cmd, "-sectionsonly");
        }
        if (this.sigAlg != null) {
            this.addValue(cmd, "-sigalg");
            this.addValue(cmd, this.sigAlg);
        }
        if (this.digestAlg != null) {
            this.addValue(cmd, "-digestalg");
            this.addValue(cmd, this.digestAlg);
        }
        this.addTimestampAuthorityCommands(cmd);
        this.addValue(cmd, jarSource.getPath());
        this.addValue(cmd, this.alias);
        this.log("Signing JAR: " + jarSource.getAbsolutePath() + " to " + targetFile.getAbsolutePath() + " as " + this.alias);
        cmd.execute();
        if (this.preserveLastModified) {
            SignJar.FILE_UTILS.setFileLastModified(targetFile, lastModified);
        }
    }
    
    private void addTimestampAuthorityCommands(final ExecTask cmd) {
        if (this.tsaurl != null) {
            this.addValue(cmd, "-tsa");
            this.addValue(cmd, this.tsaurl);
        }
        if (this.tsacert != null) {
            this.addValue(cmd, "-tsacert");
            this.addValue(cmd, this.tsacert);
        }
    }
    
    protected boolean isUpToDate(final File jarFile, final File signedjarFile) {
        if (this.isForce() || null == jarFile || !jarFile.exists()) {
            return false;
        }
        File destFile = signedjarFile;
        if (destFile == null) {
            destFile = jarFile;
        }
        if (jarFile.equals(destFile)) {
            return this.lazy && this.isSigned(jarFile);
        }
        return SignJar.FILE_UTILS.isUpToDate(jarFile, destFile);
    }
    
    protected boolean isSigned(final File file) {
        try {
            return IsSigned.isSigned(file, (this.sigfile == null) ? this.alias : this.sigfile);
        }
        catch (IOException e) {
            this.log(e.toString(), 3);
            return false;
        }
    }
    
    public void setPreserveLastModified(final boolean preserveLastModified) {
        this.preserveLastModified = preserveLastModified;
    }
    
    static {
        FILE_UTILS = FileUtils.getFileUtils();
    }
}
