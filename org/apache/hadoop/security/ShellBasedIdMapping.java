// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security;

import java.util.Iterator;
import org.slf4j.LoggerFactory;
import java.util.regex.Matcher;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.io.FileInputStream;
import java.util.HashMap;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Map;
import org.apache.hadoop.util.Time;
import java.io.IOException;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.HashBiMap;
import org.apache.hadoop.conf.Configuration;
import com.google.common.collect.BiMap;
import java.util.regex.Pattern;
import java.io.File;
import org.slf4j.Logger;

public class ShellBasedIdMapping implements IdMappingServiceProvider
{
    private static final Logger LOG;
    private static final String OS;
    static final String GET_ALL_USERS_CMD = "getent passwd | cut -d: -f1,3";
    static final String GET_ALL_GROUPS_CMD = "getent group | cut -d: -f1,3";
    static final String MAC_GET_ALL_USERS_CMD = "dscl . -list /Users UniqueID";
    static final String MAC_GET_ALL_GROUPS_CMD = "dscl . -list /Groups PrimaryGroupID";
    private final File staticMappingFile;
    private StaticMapping staticMapping;
    private long lastModificationTimeStaticMap;
    private boolean constructFullMapAtInit;
    private static final Pattern EMPTY_LINE;
    private static final Pattern COMMENT_LINE;
    private static final Pattern MAPPING_LINE;
    private final long timeout;
    private BiMap<Integer, String> uidNameMap;
    private BiMap<Integer, String> gidNameMap;
    private long lastUpdateTime;
    private static final String DUPLICATE_NAME_ID_DEBUG_INFO = "NFS gateway could have problem starting with duplicate name or id on the host system.\nThis is because HDFS (non-kerberos cluster) uses name as the only way to identify a user or group.\nThe host system with duplicated user/group name or id might work fine most of the time by itself.\nHowever when NFS gateway talks to HDFS, HDFS accepts only user and group name.\nTherefore, same name means the same user or same group. To find the duplicated names/ids, one can do:\n<getent passwd | cut -d: -f1,3> and <getent group | cut -d: -f1,3> on Linux, BSD and Solaris systems,\n<dscl . -list /Users UniqueID> and <dscl . -list /Groups PrimaryGroupID> on MacOS.";
    
    @VisibleForTesting
    public ShellBasedIdMapping(final Configuration conf, final boolean constructFullMapAtInit) throws IOException {
        this.staticMapping = null;
        this.lastModificationTimeStaticMap = 0L;
        this.constructFullMapAtInit = false;
        this.uidNameMap = (BiMap<Integer, String>)HashBiMap.create();
        this.gidNameMap = (BiMap<Integer, String>)HashBiMap.create();
        this.lastUpdateTime = 0L;
        this.constructFullMapAtInit = constructFullMapAtInit;
        final long updateTime = conf.getLong("usergroupid.update.millis", 900000L);
        if (updateTime < 60000L) {
            ShellBasedIdMapping.LOG.info("User configured user account update time is less than 1 minute. Use 1 minute instead.");
            this.timeout = 60000L;
        }
        else {
            this.timeout = updateTime;
        }
        final String staticFilePath = conf.get("static.id.mapping.file", "/etc/nfs.map");
        this.staticMappingFile = new File(staticFilePath);
        this.updateStaticMapping();
        this.updateMaps();
    }
    
    public ShellBasedIdMapping(final Configuration conf) throws IOException {
        this(conf, false);
    }
    
    @VisibleForTesting
    public long getTimeout() {
        return this.timeout;
    }
    
    @VisibleForTesting
    public BiMap<Integer, String> getUidNameMap() {
        return this.uidNameMap;
    }
    
    @VisibleForTesting
    public BiMap<Integer, String> getGidNameMap() {
        return this.gidNameMap;
    }
    
    @VisibleForTesting
    public synchronized void clearNameMaps() {
        this.uidNameMap.clear();
        this.gidNameMap.clear();
        this.lastUpdateTime = Time.monotonicNow();
    }
    
    private synchronized boolean isExpired() {
        return Time.monotonicNow() - this.lastUpdateTime > this.timeout;
    }
    
    private void checkAndUpdateMaps() {
        if (this.isExpired()) {
            ShellBasedIdMapping.LOG.info("Update cache now");
            try {
                this.updateMaps();
            }
            catch (IOException e) {
                ShellBasedIdMapping.LOG.error("Can't update the maps. Will use the old ones, which can potentially cause problem.", e);
            }
        }
    }
    
    private static void reportDuplicateEntry(final String header, final Integer key, final String value, final Integer ekey, final String evalue) {
        ShellBasedIdMapping.LOG.warn("\n" + header + String.format("new entry (%d, %s), existing entry: (%d, %s).%n%s%n%s", key, value, ekey, evalue, "The new entry is to be ignored for the following reason.", "NFS gateway could have problem starting with duplicate name or id on the host system.\nThis is because HDFS (non-kerberos cluster) uses name as the only way to identify a user or group.\nThe host system with duplicated user/group name or id might work fine most of the time by itself.\nHowever when NFS gateway talks to HDFS, HDFS accepts only user and group name.\nTherefore, same name means the same user or same group. To find the duplicated names/ids, one can do:\n<getent passwd | cut -d: -f1,3> and <getent group | cut -d: -f1,3> on Linux, BSD and Solaris systems,\n<dscl . -list /Users UniqueID> and <dscl . -list /Groups PrimaryGroupID> on MacOS."));
    }
    
    private static Integer parseId(final String idStr) {
        final long longVal = Long.parseLong(idStr);
        return (int)longVal;
    }
    
    @VisibleForTesting
    public static boolean updateMapInternal(final BiMap<Integer, String> map, final String mapName, final String command, final String regex, final Map<Integer, Integer> staticMapping) throws IOException {
        boolean updated = false;
        BufferedReader br = null;
        try {
            final Process process = Runtime.getRuntime().exec(new String[] { "bash", "-c", command });
            br = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.defaultCharset()));
            String line = null;
            while ((line = br.readLine()) != null) {
                final String[] nameId = line.split(regex);
                if (nameId == null || nameId.length != 2) {
                    throw new IOException("Can't parse " + mapName + " list entry:" + line);
                }
                ShellBasedIdMapping.LOG.debug("add to " + mapName + "map:" + nameId[0] + " id:" + nameId[1]);
                final Integer key = staticMapping.get(parseId(nameId[1]));
                final String value = nameId[0];
                if (map.containsKey(key)) {
                    final String prevValue = map.get(key);
                    if (value.equals(prevValue)) {
                        continue;
                    }
                    reportDuplicateEntry("Got multiple names associated with the same id: ", key, value, key, prevValue);
                }
                else if (map.containsValue(value)) {
                    final Integer prevKey = map.inverse().get(value);
                    reportDuplicateEntry("Got multiple ids associated with the same name: ", key, value, prevKey, value);
                }
                else {
                    map.put(key, value);
                    updated = true;
                }
            }
            ShellBasedIdMapping.LOG.debug("Updated " + mapName + " map size: " + map.size());
        }
        catch (IOException e) {
            ShellBasedIdMapping.LOG.error("Can't update " + mapName + " map");
            throw e;
        }
        finally {
            if (br != null) {
                try {
                    br.close();
                }
                catch (IOException e2) {
                    ShellBasedIdMapping.LOG.error("Can't close BufferedReader of command result", e2);
                }
            }
        }
        return updated;
    }
    
    private boolean checkSupportedPlatform() {
        if (!ShellBasedIdMapping.OS.startsWith("Linux") && !ShellBasedIdMapping.OS.startsWith("Mac") && !ShellBasedIdMapping.OS.equals("SunOS") && !ShellBasedIdMapping.OS.contains("BSD")) {
            ShellBasedIdMapping.LOG.error("Platform is not supported:" + ShellBasedIdMapping.OS + ". Can't update user map and group map and 'nobody' will be used for any user and group.");
            return false;
        }
        return true;
    }
    
    private static boolean isInteger(final String s) {
        try {
            Integer.parseInt(s);
        }
        catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
    
    private synchronized void updateStaticMapping() throws IOException {
        final boolean init = this.staticMapping == null;
        if (this.staticMappingFile.exists()) {
            final long lmTime = this.staticMappingFile.lastModified();
            if (lmTime != this.lastModificationTimeStaticMap) {
                ShellBasedIdMapping.LOG.info(init ? "Using " : ("Reloading '" + this.staticMappingFile + "' for static UID/GID mapping..."));
                this.lastModificationTimeStaticMap = lmTime;
                this.staticMapping = parseStaticMap(this.staticMappingFile);
            }
        }
        else {
            if (init) {
                this.staticMapping = new StaticMapping(new HashMap<Integer, Integer>(), new HashMap<Integer, Integer>());
            }
            if (this.lastModificationTimeStaticMap != 0L || init) {
                ShellBasedIdMapping.LOG.info("Not doing static UID/GID mapping because '" + this.staticMappingFile + "' does not exist.");
            }
            this.lastModificationTimeStaticMap = 0L;
            this.staticMapping.clear();
        }
    }
    
    public synchronized void updateMaps() throws IOException {
        if (!this.checkSupportedPlatform()) {
            return;
        }
        if (this.constructFullMapAtInit) {
            this.loadFullMaps();
            this.constructFullMapAtInit = false;
        }
        else {
            this.updateStaticMapping();
            this.clearNameMaps();
        }
    }
    
    private synchronized void loadFullUserMap() throws IOException {
        final BiMap<Integer, String> uMap = (BiMap<Integer, String>)HashBiMap.create();
        if (ShellBasedIdMapping.OS.startsWith("Mac")) {
            updateMapInternal(uMap, "user", "dscl . -list /Users UniqueID", "\\s+", this.staticMapping.uidMapping);
        }
        else {
            updateMapInternal(uMap, "user", "getent passwd | cut -d: -f1,3", ":", this.staticMapping.uidMapping);
        }
        this.uidNameMap = uMap;
        this.lastUpdateTime = Time.monotonicNow();
    }
    
    private synchronized void loadFullGroupMap() throws IOException {
        final BiMap<Integer, String> gMap = (BiMap<Integer, String>)HashBiMap.create();
        if (ShellBasedIdMapping.OS.startsWith("Mac")) {
            updateMapInternal(gMap, "group", "dscl . -list /Groups PrimaryGroupID", "\\s+", this.staticMapping.gidMapping);
        }
        else {
            updateMapInternal(gMap, "group", "getent group | cut -d: -f1,3", ":", this.staticMapping.gidMapping);
        }
        this.gidNameMap = gMap;
        this.lastUpdateTime = Time.monotonicNow();
    }
    
    private synchronized void loadFullMaps() throws IOException {
        this.loadFullUserMap();
        this.loadFullGroupMap();
    }
    
    private String getName2IdCmdNIX(final String name, final boolean isGrp) {
        String cmd;
        if (isGrp) {
            cmd = "getent group " + name + " | cut -d: -f1,3";
        }
        else {
            cmd = "id -u " + name + " | awk '{print \"" + name + ":\"$1 }'";
        }
        return cmd;
    }
    
    private String getId2NameCmdNIX(final int id, final boolean isGrp) {
        String cmd = "getent ";
        cmd += (isGrp ? "group " : "passwd ");
        cmd = cmd + String.valueOf(id) + " | cut -d: -f1,3";
        return cmd;
    }
    
    private String getName2IdCmdMac(final String name, final boolean isGrp) {
        String cmd;
        if (isGrp) {
            cmd = "dscl . -read /Groups/" + name;
            cmd += " | grep PrimaryGroupID | awk '($1 == \"PrimaryGroupID:\") ";
            cmd = cmd + "{ print \"" + name + "  \" $2 }'";
        }
        else {
            cmd = "id -u " + name + " | awk '{print \"" + name + "  \"$1 }'";
        }
        return cmd;
    }
    
    private String getId2NameCmdMac(final int id, final boolean isGrp) {
        String cmd = "dscl . -search /";
        cmd += (isGrp ? "Groups PrimaryGroupID " : "Users UniqueID ");
        cmd += String.valueOf(id);
        cmd += " | sed 'N;s/\\n//g;N;s/\\n//g' | sed 's/";
        cmd += (isGrp ? "PrimaryGroupID" : "UniqueID");
        cmd += " = (//g' | sed 's/)//g' | sed 's/\\\"//g'";
        return cmd;
    }
    
    private synchronized void updateMapIncr(final String name, final boolean isGrp) throws IOException {
        if (!this.checkSupportedPlatform()) {
            return;
        }
        if (isInteger(name) && isGrp) {
            this.loadFullGroupMap();
            return;
        }
        boolean updated = false;
        this.updateStaticMapping();
        if (ShellBasedIdMapping.OS.startsWith("Linux") || ShellBasedIdMapping.OS.equals("SunOS") || ShellBasedIdMapping.OS.contains("BSD")) {
            if (isGrp) {
                updated = updateMapInternal(this.gidNameMap, "group", this.getName2IdCmdNIX(name, true), ":", this.staticMapping.gidMapping);
            }
            else {
                updated = updateMapInternal(this.uidNameMap, "user", this.getName2IdCmdNIX(name, false), ":", this.staticMapping.uidMapping);
            }
        }
        else if (isGrp) {
            updated = updateMapInternal(this.gidNameMap, "group", this.getName2IdCmdMac(name, true), "\\s+", this.staticMapping.gidMapping);
        }
        else {
            updated = updateMapInternal(this.uidNameMap, "user", this.getName2IdCmdMac(name, false), "\\s+", this.staticMapping.uidMapping);
        }
        if (updated) {
            this.lastUpdateTime = Time.monotonicNow();
        }
    }
    
    private synchronized void updateMapIncr(final int id, final boolean isGrp) throws IOException {
        if (!this.checkSupportedPlatform()) {
            return;
        }
        boolean updated = false;
        this.updateStaticMapping();
        if (ShellBasedIdMapping.OS.startsWith("Linux") || ShellBasedIdMapping.OS.equals("SunOS") || ShellBasedIdMapping.OS.contains("BSD")) {
            if (isGrp) {
                updated = updateMapInternal(this.gidNameMap, "group", this.getId2NameCmdNIX(id, true), ":", this.staticMapping.gidMapping);
            }
            else {
                updated = updateMapInternal(this.uidNameMap, "user", this.getId2NameCmdNIX(id, false), ":", this.staticMapping.uidMapping);
            }
        }
        else if (isGrp) {
            updated = updateMapInternal(this.gidNameMap, "group", this.getId2NameCmdMac(id, true), "\\s+", this.staticMapping.gidMapping);
        }
        else {
            updated = updateMapInternal(this.uidNameMap, "user", this.getId2NameCmdMac(id, false), "\\s+", this.staticMapping.uidMapping);
        }
        if (updated) {
            this.lastUpdateTime = Time.monotonicNow();
        }
    }
    
    static StaticMapping parseStaticMap(final File staticMapFile) throws IOException {
        final Map<Integer, Integer> uidMapping = new HashMap<Integer, Integer>();
        final Map<Integer, Integer> gidMapping = new HashMap<Integer, Integer>();
        final BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(staticMapFile), StandardCharsets.UTF_8));
        try {
            String line = null;
            while ((line = in.readLine()) != null) {
                if (!ShellBasedIdMapping.EMPTY_LINE.matcher(line).matches()) {
                    if (ShellBasedIdMapping.COMMENT_LINE.matcher(line).matches()) {
                        continue;
                    }
                    final Matcher lineMatcher = ShellBasedIdMapping.MAPPING_LINE.matcher(line);
                    if (!lineMatcher.matches()) {
                        ShellBasedIdMapping.LOG.warn("Could not parse line '" + line + "'. Lines should be of the form '[uid|gid] [remote id] [local id]'. Blank lines and everything following a '#' on a line will be ignored.");
                    }
                    else {
                        final String firstComponent = lineMatcher.group(1);
                        final Integer remoteId = parseId(lineMatcher.group(2));
                        final Integer localId = parseId(lineMatcher.group(3));
                        if (firstComponent.equals("uid")) {
                            uidMapping.put(localId, remoteId);
                        }
                        else {
                            gidMapping.put(localId, remoteId);
                        }
                    }
                }
            }
        }
        finally {
            in.close();
        }
        return new StaticMapping(uidMapping, gidMapping);
    }
    
    @Override
    public synchronized int getUid(final String user) throws IOException {
        this.checkAndUpdateMaps();
        Integer id = this.uidNameMap.inverse().get(user);
        if (id == null) {
            this.updateMapIncr(user, false);
            id = this.uidNameMap.inverse().get(user);
            if (id == null) {
                throw new IOException("User just deleted?:" + user);
            }
        }
        return id;
    }
    
    @Override
    public synchronized int getGid(final String group) throws IOException {
        this.checkAndUpdateMaps();
        Integer id = this.gidNameMap.inverse().get(group);
        if (id == null) {
            this.updateMapIncr(group, true);
            id = this.gidNameMap.inverse().get(group);
            if (id == null) {
                throw new IOException("No such group:" + group);
            }
        }
        return id;
    }
    
    @Override
    public synchronized String getUserName(final int uid, final String unknown) {
        this.checkAndUpdateMaps();
        String uname = this.uidNameMap.get(uid);
        if (uname == null) {
            try {
                this.updateMapIncr(uid, false);
            }
            catch (Exception ex) {}
            uname = this.uidNameMap.get(uid);
            if (uname == null) {
                ShellBasedIdMapping.LOG.warn("Can't find user name for uid " + uid + ". Use default user name " + unknown);
                uname = unknown;
            }
        }
        return uname;
    }
    
    @Override
    public synchronized String getGroupName(final int gid, final String unknown) {
        this.checkAndUpdateMaps();
        String gname = this.gidNameMap.get(gid);
        if (gname == null) {
            try {
                this.updateMapIncr(gid, true);
            }
            catch (Exception ex) {}
            gname = this.gidNameMap.get(gid);
            if (gname == null) {
                ShellBasedIdMapping.LOG.warn("Can't find group name for gid " + gid + ". Use default group name " + unknown);
                gname = unknown;
            }
        }
        return gname;
    }
    
    @Override
    public int getUidAllowingUnknown(final String user) {
        this.checkAndUpdateMaps();
        int uid;
        try {
            uid = this.getUid(user);
        }
        catch (IOException e) {
            uid = user.hashCode();
            ShellBasedIdMapping.LOG.info("Can't map user " + user + ". Use its string hashcode:" + uid);
        }
        return uid;
    }
    
    @Override
    public int getGidAllowingUnknown(final String group) {
        this.checkAndUpdateMaps();
        int gid;
        try {
            gid = this.getGid(group);
        }
        catch (IOException e) {
            gid = group.hashCode();
            ShellBasedIdMapping.LOG.info("Can't map group " + group + ". Use its string hashcode:" + gid);
        }
        return gid;
    }
    
    static {
        LOG = LoggerFactory.getLogger(ShellBasedIdMapping.class);
        OS = System.getProperty("os.name");
        EMPTY_LINE = Pattern.compile("^\\s*$");
        COMMENT_LINE = Pattern.compile("^\\s*#.*$");
        MAPPING_LINE = Pattern.compile("^(uid|gid)\\s+(\\d+)\\s+(0|-?[1-9]\\d*)\\s*(#.*)?$");
    }
    
    static final class PassThroughMap<K> extends HashMap<K, K>
    {
        public PassThroughMap() {
            this((Map)new HashMap());
        }
        
        public PassThroughMap(final Map<K, K> mapping) {
            for (final Map.Entry<K, K> entry : mapping.entrySet()) {
                super.put(entry.getKey(), entry.getValue());
            }
        }
        
        @Override
        public K get(final Object key) {
            if (super.containsKey(key)) {
                return super.get(key);
            }
            return (K)key;
        }
    }
    
    @VisibleForTesting
    static final class StaticMapping
    {
        final Map<Integer, Integer> uidMapping;
        final Map<Integer, Integer> gidMapping;
        
        public StaticMapping(final Map<Integer, Integer> uidMapping, final Map<Integer, Integer> gidMapping) {
            this.uidMapping = (Map<Integer, Integer>)new PassThroughMap((Map<Object, Object>)uidMapping);
            this.gidMapping = (Map<Integer, Integer>)new PassThroughMap((Map<Object, Object>)gidMapping);
        }
        
        public void clear() {
            this.uidMapping.clear();
            this.gidMapping.clear();
        }
        
        public boolean isNonEmpty() {
            return this.uidMapping.size() > 0 || this.gidMapping.size() > 0;
        }
    }
}
