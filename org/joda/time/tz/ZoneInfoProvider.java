// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.tz;

import java.io.DataInputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.TreeSet;
import java.util.Set;
import java.lang.ref.SoftReference;
import org.joda.time.DateTimeZone;
import java.io.IOException;
import java.util.Map;
import java.io.File;

public class ZoneInfoProvider implements Provider
{
    private final File iFileDir;
    private final String iResourcePath;
    private final ClassLoader iLoader;
    private final Map<String, Object> iZoneInfoMap;
    
    public ZoneInfoProvider(final File iFileDir) throws IOException {
        if (iFileDir == null) {
            throw new IllegalArgumentException("No file directory provided");
        }
        if (!iFileDir.exists()) {
            throw new IOException("File directory doesn't exist: " + iFileDir);
        }
        if (!iFileDir.isDirectory()) {
            throw new IOException("File doesn't refer to a directory: " + iFileDir);
        }
        this.iFileDir = iFileDir;
        this.iResourcePath = null;
        this.iLoader = null;
        this.iZoneInfoMap = loadZoneInfoMap(this.openResource("ZoneInfoMap"));
    }
    
    public ZoneInfoProvider(final String s) throws IOException {
        this(s, null, false);
    }
    
    public ZoneInfoProvider(final String s, final ClassLoader classLoader) throws IOException {
        this(s, classLoader, true);
    }
    
    private ZoneInfoProvider(String string, ClassLoader classLoader, final boolean b) throws IOException {
        if (string == null) {
            throw new IllegalArgumentException("No resource path provided");
        }
        if (!string.endsWith("/")) {
            string += '/';
        }
        this.iFileDir = null;
        this.iResourcePath = string;
        if (classLoader == null && !b) {
            classLoader = this.getClass().getClassLoader();
        }
        this.iLoader = classLoader;
        this.iZoneInfoMap = loadZoneInfoMap(this.openResource("ZoneInfoMap"));
    }
    
    public DateTimeZone getZone(final String s) {
        if (s == null) {
            return null;
        }
        final String value = this.iZoneInfoMap.get(s);
        if (value == null) {
            return null;
        }
        if (value instanceof SoftReference) {
            final DateTimeZone dateTimeZone = ((SoftReference<DateTimeZone>)value).get();
            if (dateTimeZone != null) {
                return dateTimeZone;
            }
            return this.loadZoneData(s);
        }
        else {
            if (s.equals(value)) {
                return this.loadZoneData(s);
            }
            return this.getZone(value);
        }
    }
    
    public Set<String> getAvailableIDs() {
        return new TreeSet<String>(this.iZoneInfoMap.keySet());
    }
    
    protected void uncaughtException(final Exception ex) {
        ex.printStackTrace();
    }
    
    private InputStream openResource(final String s) throws IOException {
        InputStream inputStream;
        if (this.iFileDir != null) {
            inputStream = new FileInputStream(new File(this.iFileDir, s));
        }
        else {
            final String concat = this.iResourcePath.concat(s);
            if (this.iLoader != null) {
                inputStream = this.iLoader.getResourceAsStream(concat);
            }
            else {
                inputStream = ClassLoader.getSystemResourceAsStream(concat);
            }
            if (inputStream == null) {
                throw new IOException(new StringBuilder(40).append("Resource not found: \"").append(concat).append("\" ClassLoader: ").append((this.iLoader != null) ? this.iLoader.toString() : "system").toString());
            }
        }
        return inputStream;
    }
    
    private DateTimeZone loadZoneData(final String s) {
        InputStream openResource = null;
        try {
            openResource = this.openResource(s);
            final DateTimeZone from = DateTimeZoneBuilder.readFrom(openResource, s);
            this.iZoneInfoMap.put(s, new SoftReference(from));
            return from;
        }
        catch (IOException ex) {
            this.uncaughtException(ex);
            this.iZoneInfoMap.remove(s);
            return null;
        }
        finally {
            try {
                if (openResource != null) {
                    openResource.close();
                }
            }
            catch (IOException ex2) {}
        }
    }
    
    private static Map<String, Object> loadZoneInfoMap(final InputStream in) throws IOException {
        final ConcurrentHashMap<String, SoftReference<DateTimeZone>> concurrentHashMap = new ConcurrentHashMap<String, SoftReference<DateTimeZone>>();
        final DataInputStream dataInputStream = new DataInputStream(in);
        try {
            readZoneInfoMap(dataInputStream, (Map<String, Object>)concurrentHashMap);
        }
        finally {
            try {
                dataInputStream.close();
            }
            catch (IOException ex) {}
        }
        concurrentHashMap.put("UTC", new SoftReference<DateTimeZone>(DateTimeZone.UTC));
        return (Map<String, Object>)concurrentHashMap;
    }
    
    private static void readZoneInfoMap(final DataInputStream dataInputStream, final Map<String, Object> map) throws IOException {
        final int unsignedShort = dataInputStream.readUnsignedShort();
        final String[] array = new String[unsignedShort];
        for (int i = 0; i < unsignedShort; ++i) {
            array[i] = dataInputStream.readUTF().intern();
        }
        for (int unsignedShort2 = dataInputStream.readUnsignedShort(), j = 0; j < unsignedShort2; ++j) {
            try {
                map.put(array[dataInputStream.readUnsignedShort()], array[dataInputStream.readUnsignedShort()]);
            }
            catch (ArrayIndexOutOfBoundsException ex) {
                throw new IOException("Corrupt zone info map");
            }
        }
    }
}
