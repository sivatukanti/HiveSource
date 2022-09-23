// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.io;

import org.apache.commons.configuration2.sync.NoOpSynchronizer;
import org.apache.commons.configuration2.sync.Synchronizer;
import org.apache.commons.logging.LogFactory;
import java.util.Iterator;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.InputStreamReader;
import java.io.IOException;
import org.apache.commons.configuration2.sync.LockMode;
import java.io.Closeable;
import java.io.Writer;
import java.io.OutputStream;
import java.io.Reader;
import java.io.InputStream;
import java.net.MalformedURLException;
import org.apache.commons.configuration2.ex.ConfigurationException;
import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.configuration2.sync.SynchronizerSupport;

public class FileHandler
{
    private static final String FILE_SCHEME = "file:";
    private static final String FILE_SCHEME_SLASH = "file://";
    private static final SynchronizerSupport DUMMY_SYNC_SUPPORT;
    private final FileBased content;
    private final AtomicReference<FileLocator> fileLocator;
    private final List<FileHandlerListener> listeners;
    
    public FileHandler() {
        this(null);
    }
    
    public FileHandler(final FileBased obj) {
        this(obj, emptyFileLocator());
    }
    
    public FileHandler(final FileBased obj, final FileHandler c) {
        this(obj, checkSourceHandler(c).getFileLocator());
    }
    
    private FileHandler(final FileBased obj, final FileLocator locator) {
        this.listeners = new CopyOnWriteArrayList<FileHandlerListener>();
        this.content = obj;
        this.fileLocator = new AtomicReference<FileLocator>(locator);
    }
    
    public static FileHandler fromMap(final Map<String, ?> map) {
        return new FileHandler(null, FileLocatorUtils.fromMap(map));
    }
    
    public final FileBased getContent() {
        return this.content;
    }
    
    public void addFileHandlerListener(final FileHandlerListener l) {
        if (l == null) {
            throw new IllegalArgumentException("Listener must not be null!");
        }
        this.listeners.add(l);
    }
    
    public void removeFileHandlerListener(final FileHandlerListener l) {
        this.listeners.remove(l);
    }
    
    public String getFileName() {
        final FileLocator locator = this.getFileLocator();
        if (locator.getFileName() != null) {
            return locator.getFileName();
        }
        if (locator.getSourceURL() != null) {
            return FileLocatorUtils.getFileName(locator.getSourceURL());
        }
        return null;
    }
    
    public void setFileName(final String fileName) {
        final String name = normalizeFileURL(fileName);
        new Updater() {
            @Override
            protected void updateBuilder(final FileLocator.FileLocatorBuilder builder) {
                builder.fileName(name);
                builder.sourceURL(null);
            }
        }.update();
    }
    
    public String getBasePath() {
        final FileLocator locator = this.getFileLocator();
        if (locator.getBasePath() != null) {
            return locator.getBasePath();
        }
        if (locator.getSourceURL() != null) {
            return FileLocatorUtils.getBasePath(locator.getSourceURL());
        }
        return null;
    }
    
    public void setBasePath(final String basePath) {
        final String path = normalizeFileURL(basePath);
        new Updater() {
            @Override
            protected void updateBuilder(final FileLocator.FileLocatorBuilder builder) {
                builder.basePath(path);
                builder.sourceURL(null);
            }
        }.update();
    }
    
    public File getFile() {
        return createFile(this.getFileLocator());
    }
    
    public void setFile(final File file) {
        final String fileName = file.getName();
        final String basePath = (file.getParentFile() != null) ? file.getParentFile().getAbsolutePath() : null;
        new Updater() {
            @Override
            protected void updateBuilder(final FileLocator.FileLocatorBuilder builder) {
                builder.fileName(fileName).basePath(basePath).sourceURL(null);
            }
        }.update();
    }
    
    public String getPath() {
        final FileLocator locator = this.getFileLocator();
        final File file = createFile(locator);
        return FileLocatorUtils.obtainFileSystem(locator).getPath(file, locator.getSourceURL(), locator.getBasePath(), locator.getFileName());
    }
    
    public void setPath(final String path) {
        this.setFile(new File(path));
    }
    
    public URL getURL() {
        final FileLocator locator = this.getFileLocator();
        return (locator.getSourceURL() != null) ? locator.getSourceURL() : FileLocatorUtils.locate(locator);
    }
    
    public void setURL(final URL url) {
        new Updater() {
            @Override
            protected void updateBuilder(final FileLocator.FileLocatorBuilder builder) {
                builder.sourceURL(url);
                builder.basePath(null).fileName(null);
            }
        }.update();
    }
    
    public FileLocator getFileLocator() {
        return this.fileLocator.get();
    }
    
    public void setFileLocator(final FileLocator locator) {
        if (locator == null) {
            throw new IllegalArgumentException("FileLocator must not be null!");
        }
        this.fileLocator.set(locator);
        this.fireLocationChangedEvent();
    }
    
    public boolean isLocationDefined() {
        return FileLocatorUtils.isLocationDefined(this.getFileLocator());
    }
    
    public void clearLocation() {
        new Updater() {
            @Override
            protected void updateBuilder(final FileLocator.FileLocatorBuilder builder) {
                builder.basePath(null).fileName(null).sourceURL(null);
            }
        }.update();
    }
    
    public String getEncoding() {
        return this.getFileLocator().getEncoding();
    }
    
    public void setEncoding(final String encoding) {
        new Updater() {
            @Override
            protected void updateBuilder(final FileLocator.FileLocatorBuilder builder) {
                builder.encoding(encoding);
            }
        }.update();
    }
    
    public FileSystem getFileSystem() {
        return FileLocatorUtils.obtainFileSystem(this.getFileLocator());
    }
    
    public void setFileSystem(final FileSystem fileSystem) {
        new Updater() {
            @Override
            protected void updateBuilder(final FileLocator.FileLocatorBuilder builder) {
                builder.fileSystem(fileSystem);
            }
        }.update();
    }
    
    public void resetFileSystem() {
        this.setFileSystem(null);
    }
    
    public FileLocationStrategy getLocationStrategy() {
        return FileLocatorUtils.obtainLocationStrategy(this.getFileLocator());
    }
    
    public void setLocationStrategy(final FileLocationStrategy strategy) {
        new Updater() {
            @Override
            protected void updateBuilder(final FileLocator.FileLocatorBuilder builder) {
                builder.locationStrategy(strategy);
            }
        }.update();
    }
    
    public boolean locate() {
        boolean done;
        boolean result;
        do {
            final FileLocator locator = this.getFileLocator();
            FileLocator fullLocator = FileLocatorUtils.fullyInitializedLocator(locator);
            if (fullLocator == null) {
                result = false;
                fullLocator = locator;
            }
            else {
                result = (fullLocator != locator || FileLocatorUtils.isFullyInitialized(locator));
            }
            done = this.fileLocator.compareAndSet(locator, fullLocator);
        } while (!done);
        return result;
    }
    
    public void load() throws ConfigurationException {
        this.load(this.checkContentAndGetLocator());
    }
    
    public void load(final String fileName) throws ConfigurationException {
        this.load(fileName, this.checkContentAndGetLocator());
    }
    
    public void load(final File file) throws ConfigurationException {
        URL url;
        try {
            url = FileLocatorUtils.toURL(file);
        }
        catch (MalformedURLException e1) {
            throw new ConfigurationException("Cannot create URL from file " + file);
        }
        this.load(url);
    }
    
    public void load(final URL url) throws ConfigurationException {
        this.load(url, this.checkContentAndGetLocator());
    }
    
    public void load(final InputStream in) throws ConfigurationException {
        this.load(in, this.checkContentAndGetLocator());
    }
    
    public void load(final InputStream in, final String encoding) throws ConfigurationException {
        this.loadFromStream(in, encoding, null);
    }
    
    public void load(final Reader in) throws ConfigurationException {
        this.checkContent();
        this.injectNullFileLocator();
        this.loadFromReader(in);
    }
    
    public void save() throws ConfigurationException {
        this.save(this.checkContentAndGetLocator());
    }
    
    public void save(final String fileName) throws ConfigurationException {
        this.save(fileName, this.checkContentAndGetLocator());
    }
    
    public void save(final URL url) throws ConfigurationException {
        this.save(url, this.checkContentAndGetLocator());
    }
    
    public void save(final File file) throws ConfigurationException {
        this.save(file, this.checkContentAndGetLocator());
    }
    
    public void save(final OutputStream out) throws ConfigurationException {
        this.save(out, this.checkContentAndGetLocator());
    }
    
    public void save(final OutputStream out, final String encoding) throws ConfigurationException {
        this.saveToStream(out, encoding, null);
    }
    
    public void save(final Writer out) throws ConfigurationException {
        this.checkContent();
        this.injectNullFileLocator();
        this.saveToWriter(out);
    }
    
    private FileLocator.FileLocatorBuilder prepareNullLocatorBuilder() {
        return FileLocatorUtils.fileLocator(this.getFileLocator()).sourceURL(null).basePath(null).fileName(null);
    }
    
    private void injectNullFileLocator() {
        if (this.getContent() instanceof FileLocatorAware) {
            final FileLocator locator = this.prepareNullLocatorBuilder().create();
            ((FileLocatorAware)this.getContent()).initFileLocator(locator);
        }
    }
    
    private void injectFileLocator(final URL url) {
        if (url == null) {
            this.injectNullFileLocator();
        }
        else if (this.getContent() instanceof FileLocatorAware) {
            final FileLocator locator = this.prepareNullLocatorBuilder().sourceURL(url).create();
            ((FileLocatorAware)this.getContent()).initFileLocator(locator);
        }
    }
    
    private SynchronizerSupport fetchSynchronizerSupport() {
        if (this.getContent() instanceof SynchronizerSupport) {
            return (SynchronizerSupport)this.getContent();
        }
        return FileHandler.DUMMY_SYNC_SUPPORT;
    }
    
    private void load(final FileLocator locator) throws ConfigurationException {
        final URL url = FileLocatorUtils.locateOrThrow(locator);
        this.load(url, locator);
    }
    
    private void load(final URL url, final FileLocator locator) throws ConfigurationException {
        InputStream in = null;
        try {
            in = FileLocatorUtils.obtainFileSystem(locator).getInputStream(url);
            this.loadFromStream(in, locator.getEncoding(), url);
        }
        catch (ConfigurationException e) {
            throw e;
        }
        catch (Exception e2) {
            throw new ConfigurationException("Unable to load the configuration from the URL " + url, e2);
        }
        finally {
            closeSilent(in);
        }
    }
    
    private void load(final String fileName, final FileLocator locator) throws ConfigurationException {
        final FileLocator locFileName = this.createLocatorWithFileName(fileName, locator);
        final URL url = FileLocatorUtils.locateOrThrow(locFileName);
        this.load(url, locator);
    }
    
    private void load(final InputStream in, final FileLocator locator) throws ConfigurationException {
        this.load(in, locator.getEncoding());
    }
    
    private void loadFromStream(final InputStream in, final String encoding, final URL url) throws ConfigurationException {
        this.checkContent();
        final SynchronizerSupport syncSupport = this.fetchSynchronizerSupport();
        syncSupport.lock(LockMode.WRITE);
        try {
            this.injectFileLocator(url);
            if (this.getContent() instanceof InputStreamSupport) {
                this.loadFromStreamDirectly(in);
            }
            else {
                this.loadFromTransformedStream(in, encoding);
            }
        }
        finally {
            syncSupport.unlock(LockMode.WRITE);
        }
    }
    
    private void loadFromStreamDirectly(final InputStream in) throws ConfigurationException {
        try {
            ((InputStreamSupport)this.getContent()).read(in);
        }
        catch (IOException e) {
            throw new ConfigurationException(e);
        }
    }
    
    private void loadFromTransformedStream(final InputStream in, final String encoding) throws ConfigurationException {
        Reader reader = null;
        if (encoding != null) {
            try {
                reader = new InputStreamReader(in, encoding);
            }
            catch (UnsupportedEncodingException e) {
                throw new ConfigurationException("The requested encoding is not supported, try the default encoding.", e);
            }
        }
        if (reader == null) {
            reader = new InputStreamReader(in);
        }
        this.loadFromReader(reader);
    }
    
    private void loadFromReader(final Reader in) throws ConfigurationException {
        this.fireLoadingEvent();
        try {
            this.getContent().read(in);
        }
        catch (IOException ioex) {
            throw new ConfigurationException(ioex);
        }
        finally {
            this.fireLoadedEvent();
        }
    }
    
    private void save(final FileLocator locator) throws ConfigurationException {
        if (!FileLocatorUtils.isLocationDefined(locator)) {
            throw new ConfigurationException("No file location has been set!");
        }
        if (locator.getSourceURL() != null) {
            this.save(locator.getSourceURL(), locator);
        }
        else {
            this.save(locator.getFileName(), locator);
        }
    }
    
    private void save(final String fileName, final FileLocator locator) throws ConfigurationException {
        URL url;
        try {
            url = FileLocatorUtils.obtainFileSystem(locator).getURL(locator.getBasePath(), fileName);
        }
        catch (MalformedURLException e) {
            throw new ConfigurationException(e);
        }
        if (url == null) {
            throw new ConfigurationException("Cannot locate configuration source " + fileName);
        }
        this.save(url, locator);
    }
    
    private void save(final URL url, final FileLocator locator) throws ConfigurationException {
        OutputStream out = null;
        try {
            out = FileLocatorUtils.obtainFileSystem(locator).getOutputStream(url);
            this.saveToStream(out, locator.getEncoding(), url);
            if (out instanceof VerifiableOutputStream) {
                try {
                    ((VerifiableOutputStream)out).verify();
                }
                catch (IOException e) {
                    throw new ConfigurationException(e);
                }
            }
        }
        finally {
            closeSilent(out);
        }
    }
    
    private void save(final File file, final FileLocator locator) throws ConfigurationException {
        OutputStream out = null;
        try {
            out = FileLocatorUtils.obtainFileSystem(locator).getOutputStream(file);
            this.saveToStream(out, locator.getEncoding(), file.toURI().toURL());
        }
        catch (MalformedURLException muex) {
            throw new ConfigurationException(muex);
        }
        finally {
            closeSilent(out);
        }
    }
    
    private void save(final OutputStream out, final FileLocator locator) throws ConfigurationException {
        this.save(out, locator.getEncoding());
    }
    
    private void saveToStream(final OutputStream out, final String encoding, final URL url) throws ConfigurationException {
        this.checkContent();
        final SynchronizerSupport syncSupport = this.fetchSynchronizerSupport();
        syncSupport.lock(LockMode.WRITE);
        try {
            this.injectFileLocator(url);
            Writer writer = null;
            if (encoding != null) {
                try {
                    writer = new OutputStreamWriter(out, encoding);
                }
                catch (UnsupportedEncodingException e) {
                    throw new ConfigurationException("The requested encoding is not supported, try the default encoding.", e);
                }
            }
            if (writer == null) {
                writer = new OutputStreamWriter(out);
            }
            this.saveToWriter(writer);
        }
        finally {
            syncSupport.unlock(LockMode.WRITE);
        }
    }
    
    private void saveToWriter(final Writer out) throws ConfigurationException {
        this.fireSavingEvent();
        try {
            this.getContent().write(out);
        }
        catch (IOException ioex) {
            throw new ConfigurationException(ioex);
        }
        finally {
            this.fireSavedEvent();
        }
    }
    
    private FileLocator createLocatorWithFileName(final String fileName, final FileLocator locator) {
        return FileLocatorUtils.fileLocator(locator).sourceURL(null).fileName(fileName).create();
    }
    
    private void checkContent() throws ConfigurationException {
        if (this.getContent() == null) {
            throw new ConfigurationException("No content available!");
        }
    }
    
    private FileLocator checkContentAndGetLocator() throws ConfigurationException {
        this.checkContent();
        return this.getFileLocator();
    }
    
    private void fireLoadingEvent() {
        for (final FileHandlerListener l : this.listeners) {
            l.loading(this);
        }
    }
    
    private void fireLoadedEvent() {
        for (final FileHandlerListener l : this.listeners) {
            l.loaded(this);
        }
    }
    
    private void fireSavingEvent() {
        for (final FileHandlerListener l : this.listeners) {
            l.saving(this);
        }
    }
    
    private void fireSavedEvent() {
        for (final FileHandlerListener l : this.listeners) {
            l.saved(this);
        }
    }
    
    private void fireLocationChangedEvent() {
        for (final FileHandlerListener l : this.listeners) {
            l.locationChanged(this);
        }
    }
    
    private static String normalizeFileURL(String fileName) {
        if (fileName != null && fileName.startsWith("file:") && !fileName.startsWith("file://")) {
            fileName = "file://" + fileName.substring("file:".length());
        }
        return fileName;
    }
    
    private static void closeSilent(final Closeable cl) {
        try {
            if (cl != null) {
                cl.close();
            }
        }
        catch (IOException e) {
            LogFactory.getLog(FileHandler.class).warn("Exception when closing " + cl, e);
        }
    }
    
    private static File createFile(final FileLocator loc) {
        if (loc.getFileName() == null && loc.getSourceURL() == null) {
            return null;
        }
        if (loc.getSourceURL() != null) {
            return FileLocatorUtils.fileFromURL(loc.getSourceURL());
        }
        return FileLocatorUtils.getFile(loc.getBasePath(), loc.getFileName());
    }
    
    private static FileLocator emptyFileLocator() {
        return FileLocatorUtils.fileLocator().create();
    }
    
    private static FileHandler checkSourceHandler(final FileHandler c) {
        if (c == null) {
            throw new IllegalArgumentException("FileHandler to assign must not be null!");
        }
        return c;
    }
    
    static {
        DUMMY_SYNC_SUPPORT = new SynchronizerSupport() {
            @Override
            public void unlock(final LockMode mode) {
            }
            
            @Override
            public void setSynchronizer(final Synchronizer sync) {
            }
            
            @Override
            public void lock(final LockMode mode) {
            }
            
            @Override
            public Synchronizer getSynchronizer() {
                return NoOpSynchronizer.INSTANCE;
            }
        };
    }
    
    private abstract class Updater
    {
        public void update() {
            boolean done;
            do {
                final FileLocator oldLocator = FileHandler.this.fileLocator.get();
                final FileLocator.FileLocatorBuilder builder = FileLocatorUtils.fileLocator(oldLocator);
                this.updateBuilder(builder);
                done = FileHandler.this.fileLocator.compareAndSet(oldLocator, builder.create());
            } while (!done);
            FileHandler.this.fireLocationChangedEvent();
        }
        
        protected abstract void updateBuilder(final FileLocator.FileLocatorBuilder p0);
    }
}
