// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.taskdefs.condition.Os;
import java.io.File;
import java.util.Iterator;
import org.apache.tools.ant.util.FileNameMapper;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.util.IdentityMapper;
import java.util.ArrayList;
import org.apache.tools.ant.types.resources.Union;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Mapper;
import java.util.Vector;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.resources.Resources;
import org.apache.tools.ant.Task;

public class PathConvert extends Task
{
    private static boolean onWindows;
    private Resources path;
    private Reference refid;
    private String targetOS;
    private boolean targetWindows;
    private boolean setonempty;
    private String property;
    private Vector prefixMap;
    private String pathSep;
    private String dirSep;
    private Mapper mapper;
    private boolean preserveDuplicates;
    
    public PathConvert() {
        this.path = null;
        this.refid = null;
        this.targetOS = null;
        this.targetWindows = false;
        this.setonempty = true;
        this.property = null;
        this.prefixMap = new Vector();
        this.pathSep = null;
        this.dirSep = null;
        this.mapper = null;
    }
    
    public Path createPath() {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        final Path result = new Path(this.getProject());
        this.add(result);
        return result;
    }
    
    public void add(final ResourceCollection rc) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.getPath().add(rc);
    }
    
    private synchronized Resources getPath() {
        if (this.path == null) {
            (this.path = new Resources(this.getProject())).setCache(true);
        }
        return this.path;
    }
    
    public MapEntry createMap() {
        final MapEntry entry = new MapEntry();
        this.prefixMap.addElement(entry);
        return entry;
    }
    
    @Deprecated
    public void setTargetos(final String target) {
        final TargetOs to = new TargetOs();
        to.setValue(target);
        this.setTargetos(to);
    }
    
    public void setTargetos(final TargetOs target) {
        this.targetOS = target.getValue();
        this.targetWindows = (!this.targetOS.equals("unix") && !this.targetOS.equals("tandem"));
    }
    
    public void setSetonempty(final boolean setonempty) {
        this.setonempty = setonempty;
    }
    
    public void setProperty(final String p) {
        this.property = p;
    }
    
    public void setRefid(final Reference r) {
        if (this.path != null) {
            throw this.noChildrenAllowed();
        }
        this.refid = r;
    }
    
    public void setPathSep(final String sep) {
        this.pathSep = sep;
    }
    
    public void setDirSep(final String sep) {
        this.dirSep = sep;
    }
    
    public void setPreserveDuplicates(final boolean preserveDuplicates) {
        this.preserveDuplicates = preserveDuplicates;
    }
    
    public boolean isPreserveDuplicates() {
        return this.preserveDuplicates;
    }
    
    public boolean isReference() {
        return this.refid != null;
    }
    
    @Override
    public void execute() throws BuildException {
        final Resources savedPath = this.path;
        final String savedPathSep = this.pathSep;
        final String savedDirSep = this.dirSep;
        try {
            if (this.isReference()) {
                final Object o = this.refid.getReferencedObject(this.getProject());
                if (!(o instanceof ResourceCollection)) {
                    throw new BuildException("refid '" + this.refid.getRefId() + "' does not refer to a resource collection.");
                }
                this.getPath().add((ResourceCollection)o);
            }
            this.validateSetup();
            final String fromDirSep = PathConvert.onWindows ? "\\" : "/";
            final StringBuffer rslt = new StringBuffer();
            final ResourceCollection resources = this.isPreserveDuplicates() ? this.path : new Union(this.path);
            final List ret = new ArrayList();
            final FileNameMapper mapperImpl = (this.mapper == null) ? new IdentityMapper() : this.mapper.getImplementation();
            for (final Resource r : resources) {
                final String[] mapped = mapperImpl.mapFileName(String.valueOf(r));
                for (int m = 0; mapped != null && m < mapped.length; ++m) {
                    ret.add(mapped[m]);
                }
            }
            boolean first = true;
            final Iterator mappedIter = ret.iterator();
            while (mappedIter.hasNext()) {
                final String elem = this.mapElement(mappedIter.next());
                if (!first) {
                    rslt.append(this.pathSep);
                }
                first = false;
                final StringTokenizer stDirectory = new StringTokenizer(elem, fromDirSep, true);
                while (stDirectory.hasMoreTokens()) {
                    final String token = stDirectory.nextToken();
                    rslt.append(fromDirSep.equals(token) ? this.dirSep : token);
                }
            }
            if (this.setonempty || rslt.length() > 0) {
                final String value = rslt.toString();
                if (this.property == null) {
                    this.log(value);
                }
                else {
                    this.log("Set property " + this.property + " = " + value, 3);
                    this.getProject().setNewProperty(this.property, value);
                }
            }
        }
        finally {
            this.path = savedPath;
            this.dirSep = savedDirSep;
            this.pathSep = savedPathSep;
        }
    }
    
    private String mapElement(String elem) {
        final int size = this.prefixMap.size();
        if (size != 0) {
            for (int i = 0; i < size; ++i) {
                final MapEntry entry = this.prefixMap.elementAt(i);
                final String newElem = entry.apply(elem);
                if (newElem != elem) {
                    elem = newElem;
                    break;
                }
            }
        }
        return elem;
    }
    
    public void addMapper(final Mapper mapper) {
        if (this.mapper != null) {
            throw new BuildException("Cannot define more than one mapper");
        }
        this.mapper = mapper;
    }
    
    public void add(final FileNameMapper fileNameMapper) {
        final Mapper m = new Mapper(this.getProject());
        m.add(fileNameMapper);
        this.addMapper(m);
    }
    
    private void validateSetup() throws BuildException {
        if (this.path == null) {
            throw new BuildException("You must specify a path to convert");
        }
        String dsep = File.separator;
        String psep = File.pathSeparator;
        if (this.targetOS != null) {
            psep = (this.targetWindows ? ";" : ":");
            dsep = (this.targetWindows ? "\\" : "/");
        }
        if (this.pathSep != null) {
            psep = this.pathSep;
        }
        if (this.dirSep != null) {
            dsep = this.dirSep;
        }
        this.pathSep = psep;
        this.dirSep = dsep;
    }
    
    private BuildException noChildrenAllowed() {
        return new BuildException("You must not specify nested elements when using the refid attribute.");
    }
    
    static {
        PathConvert.onWindows = Os.isFamily("dos");
    }
    
    public class MapEntry
    {
        private String from;
        private String to;
        
        public MapEntry() {
            this.from = null;
            this.to = null;
        }
        
        public void setFrom(final String from) {
            this.from = from;
        }
        
        public void setTo(final String to) {
            this.to = to;
        }
        
        public String apply(final String elem) {
            if (this.from == null || this.to == null) {
                throw new BuildException("Both 'from' and 'to' must be set in a map entry");
            }
            final String cmpElem = PathConvert.onWindows ? elem.toLowerCase().replace('\\', '/') : elem;
            final String cmpFrom = PathConvert.onWindows ? this.from.toLowerCase().replace('\\', '/') : this.from;
            return cmpElem.startsWith(cmpFrom) ? (this.to + elem.substring(this.from.length())) : elem;
        }
    }
    
    public static class TargetOs extends EnumeratedAttribute
    {
        @Override
        public String[] getValues() {
            return new String[] { "windows", "unix", "netware", "os/2", "tandem" };
        }
    }
}
