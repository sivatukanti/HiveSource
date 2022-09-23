// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import java.nio.file.FileVisitResult;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.SimpleFileVisitor;
import java.io.File;
import java.nio.file.PathMatcher;
import org.eclipse.jetty.util.log.Log;
import java.util.Locale;
import java.nio.file.LinkOption;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.ClosedWatchServiceException;
import java.util.Collection;
import java.util.HashSet;
import java.lang.reflect.Field;
import java.nio.file.FileSystems;
import java.util.Collections;
import java.io.IOException;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.HashMap;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.EventListener;
import java.util.List;
import java.nio.file.WatchKey;
import java.util.Map;
import java.nio.file.WatchService;
import java.nio.file.WatchEvent;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.component.AbstractLifeCycle;

public class PathWatcher extends AbstractLifeCycle implements Runnable
{
    private static final boolean IS_WINDOWS;
    private static final Logger LOG;
    private static final Logger NOISY_LOG;
    private static final WatchEvent.Kind<?>[] WATCH_EVENT_KINDS;
    private WatchService watchService;
    private WatchEvent.Modifier[] watchModifiers;
    private boolean nativeWatchService;
    private Map<WatchKey, Config> keys;
    private List<EventListener> listeners;
    private List<Config> configs;
    private long updateQuietTimeDuration;
    private TimeUnit updateQuietTimeUnit;
    private Thread thread;
    private boolean _notifyExistingOnStart;
    private Map<Path, PathPendingEvents> pendingEvents;
    
    protected static <T> WatchEvent<T> cast(final WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }
    
    public PathWatcher() {
        this.keys = new HashMap<WatchKey, Config>();
        this.listeners = new CopyOnWriteArrayList<EventListener>();
        this.configs = new ArrayList<Config>();
        this.updateQuietTimeDuration = 1000L;
        this.updateQuietTimeUnit = TimeUnit.MILLISECONDS;
        this._notifyExistingOnStart = true;
        this.pendingEvents = new LinkedHashMap<Path, PathPendingEvents>();
    }
    
    public void watch(final Path file) {
        Path abs = file;
        if (!abs.isAbsolute()) {
            abs = file.toAbsolutePath();
        }
        Config config = null;
        final Path parent = abs.getParent();
        for (final Config c : this.configs) {
            if (c.getPath().equals(parent)) {
                config = c;
                break;
            }
        }
        if (config == null) {
            config = new Config(abs.getParent());
            config.addIncludeGlobRelative("");
            config.addIncludeGlobRelative(file.getFileName().toString());
            this.watch(config);
        }
        else {
            config.addIncludeGlobRelative(file.getFileName().toString());
        }
    }
    
    public void watch(final Config config) {
        this.configs.add(config);
    }
    
    protected void prepareConfig(final Config baseDir) throws IOException {
        if (PathWatcher.LOG.isDebugEnabled()) {
            PathWatcher.LOG.debug("Watching directory {}", baseDir);
        }
        Files.walkFileTree(baseDir.getPath(), new DepthLimitedFileVisitor(this, baseDir));
    }
    
    public void addListener(final EventListener listener) {
        this.listeners.add(listener);
    }
    
    private void appendConfigId(final StringBuilder s) {
        final List<Path> dirs = new ArrayList<Path>();
        for (final Config config : this.keys.values()) {
            dirs.add(config.dir);
        }
        Collections.sort(dirs);
        s.append("[");
        if (dirs.size() > 0) {
            s.append(dirs.get(0));
            if (dirs.size() > 1) {
                s.append(" (+").append(dirs.size() - 1).append(")");
            }
        }
        else {
            s.append("<null>");
        }
        s.append("]");
    }
    
    @Override
    protected void doStart() throws Exception {
        this.createWatchService();
        this.setUpdateQuietTime(this.getUpdateQuietTimeMillis(), TimeUnit.MILLISECONDS);
        for (final Config c : this.configs) {
            this.prepareConfig(c);
        }
        final StringBuilder threadId = new StringBuilder();
        threadId.append("PathWatcher-Thread");
        this.appendConfigId(threadId);
        (this.thread = new Thread(this, threadId.toString())).setDaemon(true);
        this.thread.start();
        super.doStart();
    }
    
    @Override
    protected void doStop() throws Exception {
        if (this.watchService != null) {
            this.watchService.close();
        }
        this.watchService = null;
        this.thread = null;
        this.keys.clear();
        this.pendingEvents.clear();
        super.doStop();
    }
    
    public void reset() {
        if (!this.isStopped()) {
            throw new IllegalStateException("PathWatcher must be stopped before reset.");
        }
        this.configs.clear();
        this.listeners.clear();
    }
    
    private void createWatchService() throws IOException {
        this.watchService = FileSystems.getDefault().newWatchService();
        WatchEvent.Modifier[] modifiers = null;
        boolean nativeService = true;
        try {
            final ClassLoader cl = Thread.currentThread().getContextClassLoader();
            final Class<?> pollingWatchServiceClass = Class.forName("sun.nio.fs.PollingWatchService", false, cl);
            if (pollingWatchServiceClass.isAssignableFrom(this.watchService.getClass())) {
                nativeService = false;
                PathWatcher.LOG.info("Using Non-Native Java {}", pollingWatchServiceClass.getName());
                final Class<?> c = Class.forName("com.sun.nio.file.SensitivityWatchEventModifier");
                final Field f = c.getField("HIGH");
                modifiers = new WatchEvent.Modifier[] { (WatchEvent.Modifier)f.get(c) };
            }
        }
        catch (Throwable t) {
            PathWatcher.LOG.ignore(t);
        }
        this.watchModifiers = modifiers;
        this.nativeWatchService = nativeService;
    }
    
    protected boolean isNotifiable() {
        return this.isStarted() || (!this.isStarted() && this.isNotifyExistingOnStart());
    }
    
    public Iterator<EventListener> getListeners() {
        return this.listeners.iterator();
    }
    
    public long getUpdateQuietTimeMillis() {
        return TimeUnit.MILLISECONDS.convert(this.updateQuietTimeDuration, this.updateQuietTimeUnit);
    }
    
    protected void notifyOnPathWatchEvents(final List<PathWatchEvent> events) {
        if (events == null || events.isEmpty()) {
            return;
        }
        for (final EventListener listener : this.listeners) {
            if (listener instanceof EventListListener) {
                try {
                    ((EventListListener)listener).onPathWatchEvents(events);
                }
                catch (Throwable t) {
                    PathWatcher.LOG.warn(t);
                }
            }
            else {
                final Listener l = (Listener)listener;
                for (final PathWatchEvent event : events) {
                    try {
                        l.onPathWatchEvent(event);
                    }
                    catch (Throwable t2) {
                        PathWatcher.LOG.warn(t2);
                    }
                }
            }
        }
    }
    
    protected void register(final Path dir, final Config root) throws IOException {
        PathWatcher.LOG.debug("Registering watch on {}", dir);
        if (this.watchModifiers != null) {
            final WatchKey key = dir.register(this.watchService, PathWatcher.WATCH_EVENT_KINDS, this.watchModifiers);
            this.keys.put(key, root.asSubConfig(dir));
        }
        else {
            final WatchKey key = dir.register(this.watchService, PathWatcher.WATCH_EVENT_KINDS);
            this.keys.put(key, root.asSubConfig(dir));
        }
    }
    
    public boolean removeListener(final Listener listener) {
        return this.listeners.remove(listener);
    }
    
    @Override
    public void run() {
        final List<PathWatchEvent> notifiableEvents = new ArrayList<PathWatchEvent>();
        if (PathWatcher.LOG.isDebugEnabled()) {
            PathWatcher.LOG.debug("Starting java.nio file watching with {}", this.watchService);
        }
        while (this.watchService != null && this.thread == Thread.currentThread()) {
            WatchKey key = null;
            try {
                if (this.pendingEvents.isEmpty()) {
                    if (PathWatcher.NOISY_LOG.isDebugEnabled()) {
                        PathWatcher.NOISY_LOG.debug("Waiting for take()", new Object[0]);
                    }
                    key = this.watchService.take();
                }
                else {
                    if (PathWatcher.NOISY_LOG.isDebugEnabled()) {
                        PathWatcher.NOISY_LOG.debug("Waiting for poll({}, {})", this.updateQuietTimeDuration, this.updateQuietTimeUnit);
                    }
                    key = this.watchService.poll(this.updateQuietTimeDuration, this.updateQuietTimeUnit);
                    if (key == null) {
                        final long now = System.currentTimeMillis();
                        for (final Path path : new HashSet<Path>(this.pendingEvents.keySet())) {
                            final PathPendingEvents pending = this.pendingEvents.get(path);
                            if (pending.isQuiet(now, this.updateQuietTimeDuration, this.updateQuietTimeUnit)) {
                                for (final PathWatchEvent p : pending.getEvents()) {
                                    notifiableEvents.add(p);
                                }
                                this.pendingEvents.remove(path);
                            }
                        }
                    }
                }
            }
            catch (ClosedWatchServiceException e3) {
                return;
            }
            catch (InterruptedException e) {
                if (this.isRunning()) {
                    PathWatcher.LOG.warn(e);
                }
                else {
                    PathWatcher.LOG.ignore(e);
                }
                return;
            }
            if (key != null) {
                final Config config = this.keys.get(key);
                if (config == null) {
                    if (PathWatcher.LOG.isDebugEnabled()) {
                        PathWatcher.LOG.debug("WatchKey not recognized: {}", key);
                        continue;
                    }
                    continue;
                }
                else {
                    for (final WatchEvent<?> event : key.pollEvents()) {
                        final WatchEvent.Kind<Path> kind = (WatchEvent.Kind<Path>)event.kind();
                        final WatchEvent<Path> ev = cast(event);
                        final Path name = ev.context();
                        final Path child = config.dir.resolve(name);
                        if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                            if (Files.isDirectory(child, LinkOption.NOFOLLOW_LINKS)) {
                                try {
                                    this.prepareConfig(config.asSubConfig(child));
                                }
                                catch (IOException e2) {
                                    PathWatcher.LOG.warn(e2);
                                }
                            }
                            else {
                                if (!config.matches(child)) {
                                    continue;
                                }
                                this.addToPendingList(child, new PathWatchEvent(child, ev));
                            }
                        }
                        else {
                            if (!config.matches(child)) {
                                continue;
                            }
                            this.addToPendingList(child, new PathWatchEvent(child, ev));
                        }
                    }
                }
            }
            this.notifyOnPathWatchEvents(notifiableEvents);
            notifiableEvents.clear();
            if (key != null && !key.reset()) {
                this.keys.remove(key);
                if (this.keys.isEmpty()) {
                    return;
                }
                continue;
            }
        }
    }
    
    public void addToPendingList(final Path path, final PathWatchEvent event) {
        final PathPendingEvents pending = this.pendingEvents.get(path);
        if (pending == null) {
            this.pendingEvents.put(path, new PathPendingEvents(path, event));
        }
        else {
            pending.addEvent(event);
        }
    }
    
    public void setNotifyExistingOnStart(final boolean notify) {
        this._notifyExistingOnStart = notify;
    }
    
    public boolean isNotifyExistingOnStart() {
        return this._notifyExistingOnStart;
    }
    
    public void setUpdateQuietTime(final long duration, final TimeUnit unit) {
        final long desiredMillis = unit.toMillis(duration);
        if (this.watchService != null && !this.nativeWatchService && desiredMillis < 5000L) {
            PathWatcher.LOG.warn("Quiet Time is too low for non-native WatchService [{}]: {} < 5000 ms (defaulting to 5000 ms)", this.watchService.getClass().getName(), desiredMillis);
            this.updateQuietTimeDuration = 5000L;
            this.updateQuietTimeUnit = TimeUnit.MILLISECONDS;
            return;
        }
        if (PathWatcher.IS_WINDOWS && desiredMillis < 1000L) {
            PathWatcher.LOG.warn("Quiet Time is too low for Microsoft Windows: {} < 1000 ms (defaulting to 1000 ms)", desiredMillis);
            this.updateQuietTimeDuration = 1000L;
            this.updateQuietTimeUnit = TimeUnit.MILLISECONDS;
            return;
        }
        this.updateQuietTimeDuration = duration;
        this.updateQuietTimeUnit = unit;
    }
    
    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder(this.getClass().getName());
        this.appendConfigId(s);
        return s.toString();
    }
    
    static {
        final String os = System.getProperty("os.name");
        if (os == null) {
            IS_WINDOWS = false;
        }
        else {
            final String osl = os.toLowerCase(Locale.ENGLISH);
            IS_WINDOWS = osl.contains("windows");
        }
        LOG = Log.getLogger(PathWatcher.class);
        NOISY_LOG = Log.getLogger(PathWatcher.class.getName() + ".Noisy");
        WATCH_EVENT_KINDS = new WatchEvent.Kind[] { StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY };
    }
    
    public static class Config
    {
        public static final int UNLIMITED_DEPTH = -9999;
        private static final String PATTERN_SEP;
        protected final Path dir;
        protected int recurseDepth;
        protected List<PathMatcher> includes;
        protected List<PathMatcher> excludes;
        protected boolean excludeHidden;
        
        public Config(final Path path) {
            this.recurseDepth = 0;
            this.excludeHidden = false;
            this.dir = path;
            this.includes = new ArrayList<PathMatcher>();
            this.excludes = new ArrayList<PathMatcher>();
        }
        
        public void addExclude(final PathMatcher matcher) {
            this.excludes.add(matcher);
        }
        
        public void addExclude(final String syntaxAndPattern) {
            if (PathWatcher.LOG.isDebugEnabled()) {
                PathWatcher.LOG.debug("Adding exclude: [{}]", syntaxAndPattern);
            }
            this.addExclude(this.dir.getFileSystem().getPathMatcher(syntaxAndPattern));
        }
        
        public void addExcludeGlobRelative(final String pattern) {
            this.addExclude(this.toGlobPattern(this.dir, pattern));
        }
        
        public void addExcludeHidden() {
            if (!this.excludeHidden) {
                if (PathWatcher.LOG.isDebugEnabled()) {
                    PathWatcher.LOG.debug("Adding hidden files and directories to exclusions", new Object[0]);
                }
                this.excludeHidden = true;
                this.addExclude("regex:^.*" + Config.PATTERN_SEP + "\\..*$");
                this.addExclude("regex:^.*" + Config.PATTERN_SEP + "\\..*" + Config.PATTERN_SEP + ".*$");
            }
        }
        
        public void addExcludes(final List<String> syntaxAndPatterns) {
            for (final String syntaxAndPattern : syntaxAndPatterns) {
                this.addExclude(syntaxAndPattern);
            }
        }
        
        public void addInclude(final PathMatcher matcher) {
            this.includes.add(matcher);
        }
        
        public void addInclude(final String syntaxAndPattern) {
            if (PathWatcher.LOG.isDebugEnabled()) {
                PathWatcher.LOG.debug("Adding include: [{}]", syntaxAndPattern);
            }
            this.addInclude(this.dir.getFileSystem().getPathMatcher(syntaxAndPattern));
        }
        
        public void addIncludeGlobRelative(final String pattern) {
            this.addInclude(this.toGlobPattern(this.dir, pattern));
        }
        
        public void addIncludes(final List<String> syntaxAndPatterns) {
            for (final String syntaxAndPattern : syntaxAndPatterns) {
                this.addInclude(syntaxAndPattern);
            }
        }
        
        public Config asSubConfig(final Path dir) {
            final Config subconfig = new Config(dir);
            subconfig.includes = this.includes;
            subconfig.excludes = this.excludes;
            if (dir == this.dir) {
                subconfig.recurseDepth = this.recurseDepth;
            }
            else if (this.recurseDepth == -9999) {
                subconfig.recurseDepth = -9999;
            }
            else {
                subconfig.recurseDepth = this.recurseDepth - (dir.getNameCount() - this.dir.getNameCount());
            }
            return subconfig;
        }
        
        public int getRecurseDepth() {
            return this.recurseDepth;
        }
        
        public boolean isRecurseDepthUnlimited() {
            return this.recurseDepth == -9999;
        }
        
        public Path getPath() {
            return this.dir;
        }
        
        private boolean hasMatch(final Path path, final List<PathMatcher> matchers) {
            for (final PathMatcher matcher : matchers) {
                if (matcher.matches(path)) {
                    return true;
                }
            }
            return false;
        }
        
        public boolean isExcluded(final Path dir) throws IOException {
            if (this.excludeHidden && Files.isHidden(dir)) {
                if (PathWatcher.NOISY_LOG.isDebugEnabled()) {
                    PathWatcher.NOISY_LOG.debug("isExcluded [Hidden] on {}", dir);
                }
                return true;
            }
            if (this.excludes.isEmpty()) {
                return false;
            }
            final boolean matched = this.hasMatch(dir, this.excludes);
            if (PathWatcher.NOISY_LOG.isDebugEnabled()) {
                PathWatcher.NOISY_LOG.debug("isExcluded [{}] on {}", matched, dir);
            }
            return matched;
        }
        
        public boolean isIncluded(final Path dir) {
            if (this.includes.isEmpty()) {
                if (PathWatcher.NOISY_LOG.isDebugEnabled()) {
                    PathWatcher.NOISY_LOG.debug("isIncluded [All] on {}", dir);
                }
                return true;
            }
            final boolean matched = this.hasMatch(dir, this.includes);
            if (PathWatcher.NOISY_LOG.isDebugEnabled()) {
                PathWatcher.NOISY_LOG.debug("isIncluded [{}] on {}", matched, dir);
            }
            return matched;
        }
        
        public boolean matches(final Path path) {
            try {
                return !this.isExcluded(path) && this.isIncluded(path);
            }
            catch (IOException e) {
                PathWatcher.LOG.warn("Unable to match path: " + path, e);
                return false;
            }
        }
        
        public void setRecurseDepth(final int depth) {
            this.recurseDepth = depth;
        }
        
        public boolean shouldRecurseDirectory(final Path child) {
            if (!child.startsWith(this.dir)) {
                return false;
            }
            if (this.isRecurseDepthUnlimited()) {
                return true;
            }
            final int childDepth = this.dir.relativize(child).getNameCount();
            return childDepth <= this.recurseDepth;
        }
        
        private String toGlobPattern(final Path path, final String subPattern) {
            final StringBuilder s = new StringBuilder();
            s.append("glob:");
            boolean needDelim = false;
            final Path root = path.getRoot();
            if (root != null) {
                if (PathWatcher.NOISY_LOG.isDebugEnabled()) {
                    PathWatcher.NOISY_LOG.debug("Path: {} -> Root: {}", path, root);
                }
                for (final char c : root.toString().toCharArray()) {
                    if (c == '\\') {
                        s.append(Config.PATTERN_SEP);
                    }
                    else {
                        s.append(c);
                    }
                }
            }
            else {
                needDelim = true;
            }
            for (final Path segment : path) {
                if (needDelim) {
                    s.append(Config.PATTERN_SEP);
                }
                s.append(segment);
                needDelim = true;
            }
            if (subPattern != null && subPattern.length() > 0) {
                if (needDelim) {
                    s.append(Config.PATTERN_SEP);
                }
                for (final char c : subPattern.toCharArray()) {
                    if (c == '/') {
                        s.append(Config.PATTERN_SEP);
                    }
                    else {
                        s.append(c);
                    }
                }
            }
            return s.toString();
        }
        
        @Override
        public String toString() {
            final StringBuilder s = new StringBuilder();
            s.append(this.dir);
            if (this.recurseDepth > 0) {
                s.append(" [depth=").append(this.recurseDepth).append("]");
            }
            return s.toString();
        }
        
        static {
            String sep = File.separator;
            if (File.separatorChar == '\\') {
                sep = "\\\\";
            }
            PATTERN_SEP = sep;
        }
    }
    
    public static class DepthLimitedFileVisitor extends SimpleFileVisitor<Path>
    {
        private Config base;
        private PathWatcher watcher;
        
        public DepthLimitedFileVisitor(final PathWatcher watcher, final Config base) {
            this.base = base;
            this.watcher = watcher;
        }
        
        @Override
        public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
            if (!this.base.isExcluded(dir)) {
                if (this.base.isIncluded(dir) && this.watcher.isNotifiable()) {
                    final PathWatchEvent event = new PathWatchEvent(dir, PathWatchEventType.ADDED);
                    if (PathWatcher.LOG.isDebugEnabled()) {
                        PathWatcher.LOG.debug("Pending {}", event);
                    }
                    this.watcher.addToPendingList(dir, event);
                }
                if ((this.base.getPath().equals(dir) && (this.base.isRecurseDepthUnlimited() || this.base.getRecurseDepth() >= 0)) || this.base.shouldRecurseDirectory(dir)) {
                    this.watcher.register(dir, this.base);
                }
            }
            if ((this.base.getPath().equals(dir) && (this.base.isRecurseDepthUnlimited() || this.base.getRecurseDepth() >= 0)) || this.base.shouldRecurseDirectory(dir)) {
                return FileVisitResult.CONTINUE;
            }
            return FileVisitResult.SKIP_SUBTREE;
        }
        
        @Override
        public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
            if (this.base.matches(file) && this.watcher.isNotifiable()) {
                final PathWatchEvent event = new PathWatchEvent(file, PathWatchEventType.ADDED);
                if (PathWatcher.LOG.isDebugEnabled()) {
                    PathWatcher.LOG.debug("Pending {}", event);
                }
                this.watcher.addToPendingList(file, event);
            }
            return FileVisitResult.CONTINUE;
        }
    }
    
    public static class PathWatchEvent
    {
        private final Path path;
        private final PathWatchEventType type;
        private int count;
        
        public PathWatchEvent(final Path path, final PathWatchEventType type) {
            this.count = 0;
            this.path = path;
            this.count = 1;
            this.type = type;
        }
        
        public PathWatchEvent(final Path path, final WatchEvent<Path> event) {
            this.count = 0;
            this.path = path;
            this.count = event.count();
            if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                this.type = PathWatchEventType.ADDED;
            }
            else if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                this.type = PathWatchEventType.DELETED;
            }
            else if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                this.type = PathWatchEventType.MODIFIED;
            }
            else {
                this.type = PathWatchEventType.UNKNOWN;
            }
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            final PathWatchEvent other = (PathWatchEvent)obj;
            if (this.path == null) {
                if (other.path != null) {
                    return false;
                }
            }
            else if (!this.path.equals(other.path)) {
                return false;
            }
            return this.type == other.type;
        }
        
        public Path getPath() {
            return this.path;
        }
        
        public PathWatchEventType getType() {
            return this.type;
        }
        
        public void incrementCount(final int num) {
            this.count += num;
        }
        
        public int getCount() {
            return this.count;
        }
        
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = 31 * result + ((this.path == null) ? 0 : this.path.hashCode());
            result = 31 * result + ((this.type == null) ? 0 : this.type.hashCode());
            return result;
        }
        
        @Override
        public String toString() {
            return String.format("PathWatchEvent[%s|%s]", this.type, this.path);
        }
    }
    
    public static class PathPendingEvents
    {
        private Path _path;
        private List<PathWatchEvent> _events;
        private long _timestamp;
        private long _lastFileSize;
        
        public PathPendingEvents(final Path path) {
            this._lastFileSize = -1L;
            this._path = path;
        }
        
        public PathPendingEvents(final Path path, final PathWatchEvent event) {
            this(path);
            this.addEvent(event);
        }
        
        public void addEvent(final PathWatchEvent event) {
            final long now = System.currentTimeMillis();
            this._timestamp = now;
            if (this._events == null) {
                (this._events = new ArrayList<PathWatchEvent>()).add(event);
            }
            else {
                PathWatchEvent existingType = null;
                for (final PathWatchEvent e : this._events) {
                    if (e.getType() == event.getType()) {
                        existingType = e;
                        break;
                    }
                }
                if (existingType == null) {
                    this._events.add(event);
                }
                else {
                    existingType.incrementCount(event.getCount());
                }
            }
        }
        
        public List<PathWatchEvent> getEvents() {
            return this._events;
        }
        
        public long getTimestamp() {
            return this._timestamp;
        }
        
        public boolean isQuiet(final long now, final long expiredDuration, final TimeUnit expiredUnit) {
            final long pastdue = this._timestamp + expiredUnit.toMillis(expiredDuration);
            this._timestamp = now;
            final long fileSize = this._path.toFile().length();
            final boolean fileSizeChanged = this._lastFileSize != fileSize;
            this._lastFileSize = fileSize;
            return now > pastdue && !fileSizeChanged;
        }
    }
    
    public enum PathWatchEventType
    {
        ADDED, 
        DELETED, 
        MODIFIED, 
        UNKNOWN;
    }
    
    public interface EventListListener extends EventListener
    {
        void onPathWatchEvents(final List<PathWatchEvent> p0);
    }
    
    public interface Listener extends EventListener
    {
        void onPathWatchEvent(final PathWatchEvent p0);
    }
}
