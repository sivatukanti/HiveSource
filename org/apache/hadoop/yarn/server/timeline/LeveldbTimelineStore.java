// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.timeline;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.locks.ReentrantLock;
import java.util.HashMap;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.yarn.api.records.timeline.TimelineDomains;
import org.apache.hadoop.yarn.api.records.timeline.TimelineDomain;
import org.iq80.leveldb.DBException;
import org.apache.hadoop.yarn.server.records.impl.pb.VersionPBImpl;
import org.apache.hadoop.yarn.proto.YarnServerCommonProtos;
import org.iq80.leveldb.ReadOptions;
import org.iq80.leveldb.WriteOptions;
import org.iq80.leveldb.WriteBatch;
import org.apache.hadoop.yarn.api.records.timeline.TimelinePutResponse;
import org.apache.hadoop.yarn.api.records.timeline.TimelineEntities;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.TreeMap;
import org.apache.hadoop.io.WritableComparator;
import java.util.Comparator;
import org.apache.hadoop.yarn.api.records.timeline.TimelineEvents;
import java.util.SortedSet;
import java.util.Set;
import org.apache.hadoop.yarn.api.records.timeline.TimelineEvent;
import java.util.List;
import org.iq80.leveldb.DBIterator;
import org.apache.hadoop.yarn.api.records.timeline.TimelineEntity;
import java.util.EnumSet;
import java.util.Collections;
import org.apache.commons.collections.map.LRUMap;
import java.io.File;
import org.apache.hadoop.io.IOUtils;
import java.io.Closeable;
import java.io.IOException;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.fusesource.leveldbjni.JniDBFactory;
import org.iq80.leveldb.Options;
import org.apache.hadoop.conf.Configuration;
import org.iq80.leveldb.DB;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.Map;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.yarn.server.records.Version;
import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.service.AbstractService;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class LeveldbTimelineStore extends AbstractService implements TimelineStore
{
    private static final Log LOG;
    @InterfaceAudience.Private
    @VisibleForTesting
    static final String FILENAME = "leveldb-timeline-store.ldb";
    private static final byte[] START_TIME_LOOKUP_PREFIX;
    private static final byte[] ENTITY_ENTRY_PREFIX;
    private static final byte[] INDEXED_ENTRY_PREFIX;
    private static final byte[] EVENTS_COLUMN;
    private static final byte[] PRIMARY_FILTERS_COLUMN;
    private static final byte[] OTHER_INFO_COLUMN;
    private static final byte[] RELATED_ENTITIES_COLUMN;
    private static final byte[] INVISIBLE_REVERSE_RELATED_ENTITIES_COLUMN;
    private static final byte[] DOMAIN_ID_COLUMN;
    private static final byte[] DOMAIN_ENTRY_PREFIX;
    private static final byte[] OWNER_LOOKUP_PREFIX;
    private static final byte[] DESCRIPTION_COLUMN;
    private static final byte[] OWNER_COLUMN;
    private static final byte[] READER_COLUMN;
    private static final byte[] WRITER_COLUMN;
    private static final byte[] TIMESTAMP_COLUMN;
    private static final byte[] EMPTY_BYTES;
    private static final String TIMELINE_STORE_VERSION_KEY = "timeline-store-version";
    private static final Version CURRENT_VERSION_INFO;
    @InterfaceAudience.Private
    @VisibleForTesting
    static final FsPermission LEVELDB_DIR_UMASK;
    private Map<EntityIdentifier, StartAndInsertTime> startTimeWriteCache;
    private Map<EntityIdentifier, Long> startTimeReadCache;
    private final LockMap<EntityIdentifier> writeLocks;
    private final ReentrantReadWriteLock deleteLock;
    private DB db;
    private Thread deletionThread;
    
    public LeveldbTimelineStore() {
        super(LeveldbTimelineStore.class.getName());
        this.writeLocks = new LockMap<EntityIdentifier>();
        this.deleteLock = new ReentrantReadWriteLock();
    }
    
    @Override
    protected void serviceInit(final Configuration conf) throws Exception {
        final Options options = new Options();
        options.createIfMissing(true);
        options.cacheSize(conf.getLong("yarn.timeline-service.leveldb-timeline-store.read-cache-size", 104857600L));
        final JniDBFactory factory = new JniDBFactory();
        final Path dbPath = new Path(conf.get("yarn.timeline-service.leveldb-timeline-store.path"), "leveldb-timeline-store.ldb");
        FileSystem localFS = null;
        try {
            localFS = FileSystem.getLocal(conf);
            if (!localFS.exists(dbPath)) {
                if (!localFS.mkdirs(dbPath)) {
                    throw new IOException("Couldn't create directory for leveldb timeline store " + dbPath);
                }
                localFS.setPermission(dbPath, LeveldbTimelineStore.LEVELDB_DIR_UMASK);
            }
        }
        finally {
            IOUtils.cleanup(LeveldbTimelineStore.LOG, localFS);
        }
        LeveldbTimelineStore.LOG.info("Using leveldb path " + dbPath);
        this.db = factory.open(new File(dbPath.toString()), options);
        this.checkVersion();
        this.startTimeWriteCache = Collections.synchronizedMap((Map<EntityIdentifier, StartAndInsertTime>)new LRUMap(getStartTimeWriteCacheSize(conf)));
        this.startTimeReadCache = Collections.synchronizedMap((Map<EntityIdentifier, Long>)new LRUMap(getStartTimeReadCacheSize(conf)));
        if (conf.getBoolean("yarn.timeline-service.ttl-enable", true)) {
            (this.deletionThread = new EntityDeletionThread(conf)).start();
        }
        super.serviceInit(conf);
    }
    
    @Override
    protected void serviceStop() throws Exception {
        if (this.deletionThread != null) {
            this.deletionThread.interrupt();
            LeveldbTimelineStore.LOG.info("Waiting for deletion thread to complete its current action");
            try {
                this.deletionThread.join();
            }
            catch (InterruptedException e) {
                LeveldbTimelineStore.LOG.warn("Interrupted while waiting for deletion thread to complete, closing db now", e);
            }
        }
        IOUtils.cleanup(LeveldbTimelineStore.LOG, this.db);
        super.serviceStop();
    }
    
    @Override
    public TimelineEntity getEntity(final String entityId, final String entityType, final EnumSet<TimelineReader.Field> fields) throws IOException {
        final Long revStartTime = this.getStartTimeLong(entityId, entityType);
        if (revStartTime == null) {
            return null;
        }
        final byte[] prefix = KeyBuilder.newInstance().add(LeveldbTimelineStore.ENTITY_ENTRY_PREFIX).add(entityType).add(GenericObjectMapper.writeReverseOrderedLong(revStartTime)).add(entityId).getBytesForLookup();
        DBIterator iterator = null;
        try {
            iterator = this.db.iterator();
            iterator.seek(prefix);
            return getEntity(entityId, entityType, revStartTime, fields, iterator, prefix, prefix.length);
        }
        finally {
            IOUtils.cleanup(LeveldbTimelineStore.LOG, iterator);
        }
    }
    
    private static TimelineEntity getEntity(final String entityId, final String entityType, final Long startTime, EnumSet<TimelineReader.Field> fields, final DBIterator iterator, final byte[] prefix, final int prefixlen) throws IOException {
        if (fields == null) {
            fields = EnumSet.allOf(TimelineReader.Field.class);
        }
        final TimelineEntity entity = new TimelineEntity();
        boolean events = false;
        boolean lastEvent = false;
        if (fields.contains(TimelineReader.Field.EVENTS)) {
            events = true;
        }
        else if (fields.contains(TimelineReader.Field.LAST_EVENT_ONLY)) {
            lastEvent = true;
        }
        else {
            entity.setEvents(null);
        }
        boolean relatedEntities = false;
        if (fields.contains(TimelineReader.Field.RELATED_ENTITIES)) {
            relatedEntities = true;
        }
        else {
            entity.setRelatedEntities(null);
        }
        boolean primaryFilters = false;
        if (fields.contains(TimelineReader.Field.PRIMARY_FILTERS)) {
            primaryFilters = true;
        }
        else {
            entity.setPrimaryFilters(null);
        }
        boolean otherInfo = false;
        if (fields.contains(TimelineReader.Field.OTHER_INFO)) {
            otherInfo = true;
        }
        else {
            entity.setOtherInfo(null);
        }
        while (iterator.hasNext()) {
            final byte[] key = iterator.peekNext().getKey();
            if (!prefixMatches(prefix, prefixlen, key)) {
                break;
            }
            if (key.length != prefixlen) {
                if (key[prefixlen] == LeveldbTimelineStore.PRIMARY_FILTERS_COLUMN[0]) {
                    if (primaryFilters) {
                        addPrimaryFilter(entity, key, prefixlen + LeveldbTimelineStore.PRIMARY_FILTERS_COLUMN.length);
                    }
                }
                else if (key[prefixlen] == LeveldbTimelineStore.OTHER_INFO_COLUMN[0]) {
                    if (otherInfo) {
                        entity.addOtherInfo(parseRemainingKey(key, prefixlen + LeveldbTimelineStore.OTHER_INFO_COLUMN.length), GenericObjectMapper.read(iterator.peekNext().getValue()));
                    }
                }
                else if (key[prefixlen] == LeveldbTimelineStore.RELATED_ENTITIES_COLUMN[0]) {
                    if (relatedEntities) {
                        addRelatedEntity(entity, key, prefixlen + LeveldbTimelineStore.RELATED_ENTITIES_COLUMN.length);
                    }
                }
                else if (key[prefixlen] == LeveldbTimelineStore.EVENTS_COLUMN[0]) {
                    if (events || (lastEvent && entity.getEvents().size() == 0)) {
                        final TimelineEvent event = getEntityEvent(null, key, prefixlen + LeveldbTimelineStore.EVENTS_COLUMN.length, iterator.peekNext().getValue());
                        if (event != null) {
                            entity.addEvent(event);
                        }
                    }
                }
                else if (key[prefixlen] == LeveldbTimelineStore.DOMAIN_ID_COLUMN[0]) {
                    final byte[] v = iterator.peekNext().getValue();
                    final String domainId = new String(v);
                    entity.setDomainId(domainId);
                }
                else if (key[prefixlen] != LeveldbTimelineStore.INVISIBLE_REVERSE_RELATED_ENTITIES_COLUMN[0]) {
                    LeveldbTimelineStore.LOG.warn(String.format("Found unexpected column for entity %s of type %s (0x%02x)", entityId, entityType, key[prefixlen]));
                }
            }
            iterator.next();
        }
        entity.setEntityId(entityId);
        entity.setEntityType(entityType);
        entity.setStartTime(startTime);
        return entity;
    }
    
    @Override
    public TimelineEvents getEntityTimelines(final String entityType, final SortedSet<String> entityIds, Long limit, final Long windowStart, Long windowEnd, final Set<String> eventType) throws IOException {
        final TimelineEvents events = new TimelineEvents();
        if (entityIds == null || entityIds.isEmpty()) {
            return events;
        }
        final Map<byte[], List<EntityIdentifier>> startTimeMap = new TreeMap<byte[], List<EntityIdentifier>>(new Comparator<byte[]>() {
            @Override
            public int compare(final byte[] o1, final byte[] o2) {
                return WritableComparator.compareBytes(o1, 0, o1.length, o2, 0, o2.length);
            }
        });
        DBIterator iterator = null;
        try {
            for (final String entityId : entityIds) {
                final byte[] startTime = this.getStartTime(entityId, entityType);
                if (startTime != null) {
                    List<EntityIdentifier> entities = startTimeMap.get(startTime);
                    if (entities == null) {
                        entities = new ArrayList<EntityIdentifier>();
                        startTimeMap.put(startTime, entities);
                    }
                    entities.add(new EntityIdentifier(entityId, entityType));
                }
            }
            for (final Map.Entry<byte[], List<EntityIdentifier>> entry : startTimeMap.entrySet()) {
                final byte[] revStartTime = entry.getKey();
                for (final EntityIdentifier entityIdentifier : entry.getValue()) {
                    final TimelineEvents.EventsOfOneEntity entity = new TimelineEvents.EventsOfOneEntity();
                    entity.setEntityId(entityIdentifier.getId());
                    entity.setEntityType(entityType);
                    events.addEvent(entity);
                    final KeyBuilder kb = KeyBuilder.newInstance().add(LeveldbTimelineStore.ENTITY_ENTRY_PREFIX).add(entityType).add(revStartTime).add(entityIdentifier.getId()).add(LeveldbTimelineStore.EVENTS_COLUMN);
                    final byte[] prefix = kb.getBytesForLookup();
                    if (windowEnd == null) {
                        windowEnd = Long.MAX_VALUE;
                    }
                    final byte[] revts = GenericObjectMapper.writeReverseOrderedLong(windowEnd);
                    kb.add(revts);
                    final byte[] first = kb.getBytesForLookup();
                    byte[] last = null;
                    if (windowStart != null) {
                        last = KeyBuilder.newInstance().add(prefix).add(GenericObjectMapper.writeReverseOrderedLong(windowStart)).getBytesForLookup();
                    }
                    if (limit == null) {
                        limit = 100L;
                    }
                    iterator = this.db.iterator();
                    iterator.seek(first);
                    while (entity.getEvents().size() < limit && iterator.hasNext()) {
                        final byte[] key = iterator.peekNext().getKey();
                        if (!prefixMatches(prefix, prefix.length, key)) {
                            break;
                        }
                        if (last != null && WritableComparator.compareBytes(key, 0, key.length, last, 0, last.length) > 0) {
                            break;
                        }
                        final TimelineEvent event = getEntityEvent(eventType, key, prefix.length, iterator.peekNext().getValue());
                        if (event != null) {
                            entity.addEvent(event);
                        }
                        iterator.next();
                    }
                }
            }
        }
        finally {
            IOUtils.cleanup(LeveldbTimelineStore.LOG, iterator);
        }
        return events;
    }
    
    private static boolean prefixMatches(final byte[] prefix, final int prefixlen, final byte[] b) {
        return b.length >= prefixlen && WritableComparator.compareBytes(prefix, 0, prefixlen, b, 0, prefixlen) == 0;
    }
    
    @Override
    public TimelineEntities getEntities(final String entityType, final Long limit, final Long windowStart, final Long windowEnd, final String fromId, final Long fromTs, final NameValuePair primaryFilter, final Collection<NameValuePair> secondaryFilters, final EnumSet<TimelineReader.Field> fields) throws IOException {
        if (primaryFilter == null) {
            return this.getEntityByTime(LeveldbTimelineStore.ENTITY_ENTRY_PREFIX, entityType, limit, windowStart, windowEnd, fromId, fromTs, secondaryFilters, fields);
        }
        final byte[] base = KeyBuilder.newInstance().add(LeveldbTimelineStore.INDEXED_ENTRY_PREFIX).add(primaryFilter.getName()).add(GenericObjectMapper.write(primaryFilter.getValue()), true).add(LeveldbTimelineStore.ENTITY_ENTRY_PREFIX).getBytesForLookup();
        return this.getEntityByTime(base, entityType, limit, windowStart, windowEnd, fromId, fromTs, secondaryFilters, fields);
    }
    
    private TimelineEntities getEntityByTime(final byte[] base, final String entityType, Long limit, final Long starttime, Long endtime, final String fromId, final Long fromTs, final Collection<NameValuePair> secondaryFilters, final EnumSet<TimelineReader.Field> fields) throws IOException {
        DBIterator iterator = null;
        try {
            final KeyBuilder kb = KeyBuilder.newInstance().add(base).add(entityType);
            final byte[] prefix = kb.getBytesForLookup();
            if (endtime == null) {
                endtime = Long.MAX_VALUE;
            }
            byte[] first = null;
            if (fromId != null) {
                final Long fromIdStartTime = this.getStartTimeLong(fromId, entityType);
                if (fromIdStartTime == null) {
                    return new TimelineEntities();
                }
                if (fromIdStartTime <= endtime) {
                    first = kb.add(GenericObjectMapper.writeReverseOrderedLong(fromIdStartTime)).add(fromId).getBytesForLookup();
                }
            }
            if (first == null) {
                first = kb.add(GenericObjectMapper.writeReverseOrderedLong(endtime)).getBytesForLookup();
            }
            byte[] last = null;
            if (starttime != null) {
                last = KeyBuilder.newInstance().add(base).add(entityType).add(GenericObjectMapper.writeReverseOrderedLong(starttime)).getBytesForLookup();
            }
            if (limit == null) {
                limit = 100L;
            }
            final TimelineEntities entities = new TimelineEntities();
            iterator = this.db.iterator();
            iterator.seek(first);
            while (entities.getEntities().size() < limit && iterator.hasNext()) {
                byte[] key = iterator.peekNext().getKey();
                if (!prefixMatches(prefix, prefix.length, key)) {
                    break;
                }
                if (last != null && WritableComparator.compareBytes(key, 0, key.length, last, 0, last.length) > 0) {
                    break;
                }
                final KeyParser kp = new KeyParser(key, prefix.length);
                final Long startTime = kp.getNextLong();
                final String entityId = kp.getNextString();
                if (fromTs != null) {
                    final long insertTime = GenericObjectMapper.readReverseOrderedLong(iterator.peekNext().getValue(), 0);
                    if (insertTime > fromTs) {
                        for (byte[] firstKey = key; iterator.hasNext() && prefixMatches(firstKey, kp.getOffset(), key); key = iterator.peekNext().getKey()) {
                            iterator.next();
                        }
                        continue;
                    }
                }
                final TimelineEntity entity = getEntity(entityId, entityType, startTime, fields, iterator, key, kp.getOffset());
                boolean filterPassed = true;
                if (secondaryFilters != null) {
                    for (final NameValuePair filter : secondaryFilters) {
                        final Object v = entity.getOtherInfo().get(filter.getName());
                        if (v == null) {
                            final Set<Object> vs = entity.getPrimaryFilters().get(filter.getName());
                            if (vs != null && !vs.contains(filter.getValue())) {
                                filterPassed = false;
                                break;
                            }
                            continue;
                        }
                        else {
                            if (!v.equals(filter.getValue())) {
                                filterPassed = false;
                                break;
                            }
                            continue;
                        }
                    }
                }
                if (!filterPassed) {
                    continue;
                }
                entities.addEntity(entity);
            }
            return entities;
        }
        finally {
            IOUtils.cleanup(LeveldbTimelineStore.LOG, iterator);
        }
    }
    
    private void put(final TimelineEntity entity, final TimelinePutResponse response, final boolean allowEmptyDomainId) {
        LockMap.CountingReentrantLock<EntityIdentifier> lock = this.writeLocks.getLock(new EntityIdentifier(entity.getEntityId(), entity.getEntityType()));
        lock.lock();
        WriteBatch writeBatch = null;
        final List<EntityIdentifier> relatedEntitiesWithoutStartTimes = new ArrayList<EntityIdentifier>();
        byte[] revStartTime = null;
        Map<String, Set<Object>> primaryFilters = null;
        try {
            writeBatch = this.db.createWriteBatch();
            final List<TimelineEvent> events = entity.getEvents();
            final StartAndInsertTime startAndInsertTime = this.getAndSetStartTime(entity.getEntityId(), entity.getEntityType(), entity.getStartTime(), events);
            if (startAndInsertTime == null) {
                final TimelinePutResponse.TimelinePutError error = new TimelinePutResponse.TimelinePutError();
                error.setEntityId(entity.getEntityId());
                error.setEntityType(entity.getEntityType());
                error.setErrorCode(1);
                response.addError(error);
                return;
            }
            revStartTime = GenericObjectMapper.writeReverseOrderedLong(startAndInsertTime.startTime);
            primaryFilters = entity.getPrimaryFilters();
            final byte[] markerKey = createEntityMarkerKey(entity.getEntityId(), entity.getEntityType(), revStartTime);
            final byte[] markerValue = GenericObjectMapper.writeReverseOrderedLong(startAndInsertTime.insertTime);
            writeBatch.put(markerKey, markerValue);
            writePrimaryFilterEntries(writeBatch, primaryFilters, markerKey, markerValue);
            if (events != null && !events.isEmpty()) {
                for (final TimelineEvent event : events) {
                    final byte[] revts = GenericObjectMapper.writeReverseOrderedLong(event.getTimestamp());
                    final byte[] key = createEntityEventKey(entity.getEntityId(), entity.getEntityType(), revStartTime, revts, event.getEventType());
                    final byte[] value = GenericObjectMapper.write(event.getEventInfo());
                    writeBatch.put(key, value);
                    writePrimaryFilterEntries(writeBatch, primaryFilters, key, value);
                }
            }
            final Map<String, Set<String>> relatedEntities = entity.getRelatedEntities();
            if (relatedEntities != null && !relatedEntities.isEmpty()) {
                for (final Map.Entry<String, Set<String>> relatedEntityList : relatedEntities.entrySet()) {
                    final String relatedEntityType = relatedEntityList.getKey();
                    for (final String relatedEntityId : relatedEntityList.getValue()) {
                        byte[] key2 = createReverseRelatedEntityKey(entity.getEntityId(), entity.getEntityType(), revStartTime, relatedEntityId, relatedEntityType);
                        writeBatch.put(key2, LeveldbTimelineStore.EMPTY_BYTES);
                        final byte[] relatedEntityStartTime = this.getStartTime(relatedEntityId, relatedEntityType);
                        if (relatedEntityStartTime == null) {
                            relatedEntitiesWithoutStartTimes.add(new EntityIdentifier(relatedEntityId, relatedEntityType));
                        }
                        else {
                            final byte[] domainIdBytes = this.db.get(createDomainIdKey(relatedEntityId, relatedEntityType, relatedEntityStartTime));
                            String domainId = null;
                            if (domainIdBytes == null) {
                                domainId = "DEFAULT";
                            }
                            else {
                                domainId = new String(domainIdBytes);
                            }
                            if (!domainId.equals(entity.getDomainId())) {
                                final TimelinePutResponse.TimelinePutError error2 = new TimelinePutResponse.TimelinePutError();
                                error2.setEntityId(entity.getEntityId());
                                error2.setEntityType(entity.getEntityType());
                                error2.setErrorCode(6);
                                response.addError(error2);
                            }
                            else {
                                key2 = createRelatedEntityKey(relatedEntityId, relatedEntityType, relatedEntityStartTime, entity.getEntityId(), entity.getEntityType());
                                writeBatch.put(key2, LeveldbTimelineStore.EMPTY_BYTES);
                            }
                        }
                    }
                }
            }
            if (primaryFilters != null && !primaryFilters.isEmpty()) {
                for (final Map.Entry<String, Set<Object>> primaryFilter : primaryFilters.entrySet()) {
                    for (final Object primaryFilterValue : primaryFilter.getValue()) {
                        final byte[] key3 = createPrimaryFilterKey(entity.getEntityId(), entity.getEntityType(), revStartTime, primaryFilter.getKey(), primaryFilterValue);
                        writeBatch.put(key3, LeveldbTimelineStore.EMPTY_BYTES);
                        writePrimaryFilterEntries(writeBatch, primaryFilters, key3, LeveldbTimelineStore.EMPTY_BYTES);
                    }
                }
            }
            final Map<String, Object> otherInfo = entity.getOtherInfo();
            if (otherInfo != null && !otherInfo.isEmpty()) {
                for (final Map.Entry<String, Object> i : otherInfo.entrySet()) {
                    final byte[] key4 = createOtherInfoKey(entity.getEntityId(), entity.getEntityType(), revStartTime, i.getKey());
                    final byte[] value2 = GenericObjectMapper.write(i.getValue());
                    writeBatch.put(key4, value2);
                    writePrimaryFilterEntries(writeBatch, primaryFilters, key4, value2);
                }
            }
            final byte[] key5 = createDomainIdKey(entity.getEntityId(), entity.getEntityType(), revStartTime);
            if (entity.getDomainId() == null || entity.getDomainId().length() == 0) {
                if (!allowEmptyDomainId) {
                    final TimelinePutResponse.TimelinePutError error3 = new TimelinePutResponse.TimelinePutError();
                    error3.setEntityId(entity.getEntityId());
                    error3.setEntityType(entity.getEntityType());
                    error3.setErrorCode(5);
                    response.addError(error3);
                    return;
                }
            }
            else {
                writeBatch.put(key5, entity.getDomainId().getBytes());
                writePrimaryFilterEntries(writeBatch, primaryFilters, key5, entity.getDomainId().getBytes());
            }
            this.db.write(writeBatch);
        }
        catch (IOException e) {
            LeveldbTimelineStore.LOG.error("Error putting entity " + entity.getEntityId() + " of type " + entity.getEntityType(), e);
            final TimelinePutResponse.TimelinePutError error4 = new TimelinePutResponse.TimelinePutError();
            error4.setEntityId(entity.getEntityId());
            error4.setEntityType(entity.getEntityType());
            error4.setErrorCode(2);
            response.addError(error4);
        }
        finally {
            lock.unlock();
            this.writeLocks.returnLock(lock);
            IOUtils.cleanup(LeveldbTimelineStore.LOG, writeBatch);
        }
        for (final EntityIdentifier relatedEntity : relatedEntitiesWithoutStartTimes) {
            lock = this.writeLocks.getLock(relatedEntity);
            lock.lock();
            try {
                final StartAndInsertTime relatedEntityStartAndInsertTime = this.getAndSetStartTime(relatedEntity.getId(), relatedEntity.getType(), GenericObjectMapper.readReverseOrderedLong(revStartTime, 0), null);
                if (relatedEntityStartAndInsertTime == null) {
                    throw new IOException("Error setting start time for related entity");
                }
                final byte[] relatedEntityStartTime2 = GenericObjectMapper.writeReverseOrderedLong(relatedEntityStartAndInsertTime.startTime);
                final byte[] key6 = createDomainIdKey(relatedEntity.getId(), relatedEntity.getType(), relatedEntityStartTime2);
                this.db.put(key6, entity.getDomainId().getBytes());
                this.db.put(createRelatedEntityKey(relatedEntity.getId(), relatedEntity.getType(), relatedEntityStartTime2, entity.getEntityId(), entity.getEntityType()), LeveldbTimelineStore.EMPTY_BYTES);
                this.db.put(createEntityMarkerKey(relatedEntity.getId(), relatedEntity.getType(), relatedEntityStartTime2), GenericObjectMapper.writeReverseOrderedLong(relatedEntityStartAndInsertTime.insertTime));
            }
            catch (IOException e2) {
                LeveldbTimelineStore.LOG.error("Error putting related entity " + relatedEntity.getId() + " of type " + relatedEntity.getType() + " for entity " + entity.getEntityId() + " of type " + entity.getEntityType(), e2);
                final TimelinePutResponse.TimelinePutError error5 = new TimelinePutResponse.TimelinePutError();
                error5.setEntityId(entity.getEntityId());
                error5.setEntityType(entity.getEntityType());
                error5.setErrorCode(2);
                response.addError(error5);
            }
            finally {
                lock.unlock();
                this.writeLocks.returnLock(lock);
            }
        }
    }
    
    private static void writePrimaryFilterEntries(final WriteBatch writeBatch, final Map<String, Set<Object>> primaryFilters, final byte[] key, final byte[] value) throws IOException {
        if (primaryFilters != null && !primaryFilters.isEmpty()) {
            for (final Map.Entry<String, Set<Object>> pf : primaryFilters.entrySet()) {
                for (final Object pfval : pf.getValue()) {
                    writeBatch.put(addPrimaryFilterToKey(pf.getKey(), pfval, key), value);
                }
            }
        }
    }
    
    @Override
    public TimelinePutResponse put(final TimelineEntities entities) {
        try {
            this.deleteLock.readLock().lock();
            final TimelinePutResponse response = new TimelinePutResponse();
            for (final TimelineEntity entity : entities.getEntities()) {
                this.put(entity, response, false);
            }
            return response;
        }
        finally {
            this.deleteLock.readLock().unlock();
        }
    }
    
    @InterfaceAudience.Private
    @VisibleForTesting
    public TimelinePutResponse putWithNoDomainId(final TimelineEntities entities) {
        try {
            this.deleteLock.readLock().lock();
            final TimelinePutResponse response = new TimelinePutResponse();
            for (final TimelineEntity entity : entities.getEntities()) {
                this.put(entity, response, true);
            }
            return response;
        }
        finally {
            this.deleteLock.readLock().unlock();
        }
    }
    
    private byte[] getStartTime(final String entityId, final String entityType) throws IOException {
        final Long l = this.getStartTimeLong(entityId, entityType);
        return (byte[])((l == null) ? null : GenericObjectMapper.writeReverseOrderedLong(l));
    }
    
    private Long getStartTimeLong(final String entityId, final String entityType) throws IOException {
        final EntityIdentifier entity = new EntityIdentifier(entityId, entityType);
        if (this.startTimeReadCache.containsKey(entity)) {
            return this.startTimeReadCache.get(entity);
        }
        final byte[] b = createStartTimeLookupKey(entity.getId(), entity.getType());
        final byte[] v = this.db.get(b);
        if (v == null) {
            return null;
        }
        final Long l = GenericObjectMapper.readReverseOrderedLong(v, 0);
        this.startTimeReadCache.put(entity, l);
        return l;
    }
    
    private StartAndInsertTime getAndSetStartTime(final String entityId, final String entityType, Long startTime, final List<TimelineEvent> events) throws IOException {
        final EntityIdentifier entity = new EntityIdentifier(entityId, entityType);
        if (startTime == null) {
            if (this.startTimeWriteCache.containsKey(entity)) {
                return this.startTimeWriteCache.get(entity);
            }
            if (events != null) {
                Long min = Long.MAX_VALUE;
                for (final TimelineEvent e : events) {
                    if (min > e.getTimestamp()) {
                        min = e.getTimestamp();
                    }
                }
                startTime = min;
            }
            return this.checkStartTimeInDb(entity, startTime);
        }
        else {
            if (this.startTimeWriteCache.containsKey(entity)) {
                return this.startTimeWriteCache.get(entity);
            }
            return this.checkStartTimeInDb(entity, startTime);
        }
    }
    
    private StartAndInsertTime checkStartTimeInDb(final EntityIdentifier entity, final Long suggestedStartTime) throws IOException {
        StartAndInsertTime startAndInsertTime = null;
        final byte[] b = createStartTimeLookupKey(entity.getId(), entity.getType());
        byte[] v = this.db.get(b);
        if (v == null) {
            if (suggestedStartTime == null) {
                return null;
            }
            startAndInsertTime = new StartAndInsertTime(suggestedStartTime, System.currentTimeMillis());
            v = new byte[16];
            GenericObjectMapper.writeReverseOrderedLong(suggestedStartTime, v, 0);
            GenericObjectMapper.writeReverseOrderedLong(startAndInsertTime.insertTime, v, 8);
            final WriteOptions writeOptions = new WriteOptions();
            writeOptions.sync(true);
            this.db.put(b, v, writeOptions);
        }
        else {
            startAndInsertTime = new StartAndInsertTime(GenericObjectMapper.readReverseOrderedLong(v, 0), GenericObjectMapper.readReverseOrderedLong(v, 8));
        }
        this.startTimeWriteCache.put(entity, startAndInsertTime);
        this.startTimeReadCache.put(entity, startAndInsertTime.startTime);
        return startAndInsertTime;
    }
    
    private static byte[] createStartTimeLookupKey(final String entityId, final String entityType) throws IOException {
        return KeyBuilder.newInstance().add(LeveldbTimelineStore.START_TIME_LOOKUP_PREFIX).add(entityType).add(entityId).getBytes();
    }
    
    private static byte[] createEntityMarkerKey(final String entityId, final String entityType, final byte[] revStartTime) throws IOException {
        return KeyBuilder.newInstance().add(LeveldbTimelineStore.ENTITY_ENTRY_PREFIX).add(entityType).add(revStartTime).add(entityId).getBytesForLookup();
    }
    
    private static byte[] addPrimaryFilterToKey(final String primaryFilterName, final Object primaryFilterValue, final byte[] key) throws IOException {
        return KeyBuilder.newInstance().add(LeveldbTimelineStore.INDEXED_ENTRY_PREFIX).add(primaryFilterName).add(GenericObjectMapper.write(primaryFilterValue), true).add(key).getBytes();
    }
    
    private static byte[] createEntityEventKey(final String entityId, final String entityType, final byte[] revStartTime, final byte[] revEventTimestamp, final String eventType) throws IOException {
        return KeyBuilder.newInstance().add(LeveldbTimelineStore.ENTITY_ENTRY_PREFIX).add(entityType).add(revStartTime).add(entityId).add(LeveldbTimelineStore.EVENTS_COLUMN).add(revEventTimestamp).add(eventType).getBytes();
    }
    
    private static TimelineEvent getEntityEvent(final Set<String> eventTypes, final byte[] key, final int offset, final byte[] value) throws IOException {
        final KeyParser kp = new KeyParser(key, offset);
        final long ts = kp.getNextLong();
        final String tstype = kp.getNextString();
        if (eventTypes == null || eventTypes.contains(tstype)) {
            final TimelineEvent event = new TimelineEvent();
            event.setTimestamp(ts);
            event.setEventType(tstype);
            final Object o = GenericObjectMapper.read(value);
            if (o == null) {
                event.setEventInfo(null);
            }
            else {
                if (!(o instanceof Map)) {
                    throw new IOException("Couldn't deserialize event info map");
                }
                final Map<String, Object> m = (Map<String, Object>)o;
                event.setEventInfo(m);
            }
            return event;
        }
        return null;
    }
    
    private static byte[] createPrimaryFilterKey(final String entityId, final String entityType, final byte[] revStartTime, final String name, final Object value) throws IOException {
        return KeyBuilder.newInstance().add(LeveldbTimelineStore.ENTITY_ENTRY_PREFIX).add(entityType).add(revStartTime).add(entityId).add(LeveldbTimelineStore.PRIMARY_FILTERS_COLUMN).add(name).add(GenericObjectMapper.write(value)).getBytes();
    }
    
    private static void addPrimaryFilter(final TimelineEntity entity, final byte[] key, final int offset) throws IOException {
        final KeyParser kp = new KeyParser(key, offset);
        final String name = kp.getNextString();
        final Object value = GenericObjectMapper.read(key, kp.getOffset());
        entity.addPrimaryFilter(name, value);
    }
    
    private static byte[] createOtherInfoKey(final String entityId, final String entityType, final byte[] revStartTime, final String name) throws IOException {
        return KeyBuilder.newInstance().add(LeveldbTimelineStore.ENTITY_ENTRY_PREFIX).add(entityType).add(revStartTime).add(entityId).add(LeveldbTimelineStore.OTHER_INFO_COLUMN).add(name).getBytes();
    }
    
    private static String parseRemainingKey(final byte[] b, final int offset) {
        return new String(b, offset, b.length - offset);
    }
    
    private static byte[] createRelatedEntityKey(final String entityId, final String entityType, final byte[] revStartTime, final String relatedEntityId, final String relatedEntityType) throws IOException {
        return KeyBuilder.newInstance().add(LeveldbTimelineStore.ENTITY_ENTRY_PREFIX).add(entityType).add(revStartTime).add(entityId).add(LeveldbTimelineStore.RELATED_ENTITIES_COLUMN).add(relatedEntityType).add(relatedEntityId).getBytes();
    }
    
    private static void addRelatedEntity(final TimelineEntity entity, final byte[] key, final int offset) throws IOException {
        final KeyParser kp = new KeyParser(key, offset);
        final String type = kp.getNextString();
        final String id = kp.getNextString();
        entity.addRelatedEntity(type, id);
    }
    
    private static byte[] createReverseRelatedEntityKey(final String entityId, final String entityType, final byte[] revStartTime, final String relatedEntityId, final String relatedEntityType) throws IOException {
        return KeyBuilder.newInstance().add(LeveldbTimelineStore.ENTITY_ENTRY_PREFIX).add(entityType).add(revStartTime).add(entityId).add(LeveldbTimelineStore.INVISIBLE_REVERSE_RELATED_ENTITIES_COLUMN).add(relatedEntityType).add(relatedEntityId).getBytes();
    }
    
    private static byte[] createDomainIdKey(final String entityId, final String entityType, final byte[] revStartTime) throws IOException {
        return KeyBuilder.newInstance().add(LeveldbTimelineStore.ENTITY_ENTRY_PREFIX).add(entityType).add(revStartTime).add(entityId).add(LeveldbTimelineStore.DOMAIN_ID_COLUMN).getBytes();
    }
    
    @VisibleForTesting
    void clearStartTimeCache() {
        this.startTimeWriteCache.clear();
        this.startTimeReadCache.clear();
    }
    
    @VisibleForTesting
    static int getStartTimeReadCacheSize(final Configuration conf) {
        return conf.getInt("yarn.timeline-service.leveldb-timeline-store.start-time-read-cache-size", 10000);
    }
    
    @VisibleForTesting
    static int getStartTimeWriteCacheSize(final Configuration conf) {
        return conf.getInt("yarn.timeline-service.leveldb-timeline-store.start-time-write-cache-size", 10000);
    }
    
    @VisibleForTesting
    List<String> getEntityTypes() throws IOException {
        DBIterator iterator = null;
        try {
            iterator = this.getDbIterator(false);
            final List<String> entityTypes = new ArrayList<String>();
            iterator.seek(LeveldbTimelineStore.ENTITY_ENTRY_PREFIX);
            while (iterator.hasNext()) {
                final byte[] key = iterator.peekNext().getKey();
                if (key[0] != LeveldbTimelineStore.ENTITY_ENTRY_PREFIX[0]) {
                    break;
                }
                final KeyParser kp = new KeyParser(key, LeveldbTimelineStore.ENTITY_ENTRY_PREFIX.length);
                final String entityType = kp.getNextString();
                entityTypes.add(entityType);
                final byte[] lookupKey = KeyBuilder.newInstance().add(LeveldbTimelineStore.ENTITY_ENTRY_PREFIX).add(entityType).getBytesForLookup();
                if (lookupKey[lookupKey.length - 1] != 0) {
                    throw new IOException("Found unexpected end byte in lookup key");
                }
                lookupKey[lookupKey.length - 1] = 1;
                iterator.seek(lookupKey);
            }
            return entityTypes;
        }
        finally {
            IOUtils.cleanup(LeveldbTimelineStore.LOG, iterator);
        }
    }
    
    private void deleteKeysWithPrefix(final WriteBatch writeBatch, final byte[] prefix, final DBIterator iterator) {
        iterator.seek(prefix);
        while (iterator.hasNext()) {
            final byte[] key = iterator.peekNext().getKey();
            if (!prefixMatches(prefix, prefix.length, key)) {
                break;
            }
            writeBatch.delete(key);
            iterator.next();
        }
    }
    
    @VisibleForTesting
    boolean deleteNextEntity(final String entityType, final byte[] reverseTimestamp, final DBIterator iterator, final DBIterator pfIterator, final boolean seeked) throws IOException {
        WriteBatch writeBatch = null;
        try {
            final KeyBuilder kb = KeyBuilder.newInstance().add(LeveldbTimelineStore.ENTITY_ENTRY_PREFIX).add(entityType);
            final byte[] typePrefix = kb.getBytesForLookup();
            kb.add(reverseTimestamp);
            if (!seeked) {
                iterator.seek(kb.getBytesForLookup());
            }
            if (!iterator.hasNext()) {
                return false;
            }
            final byte[] entityKey = iterator.peekNext().getKey();
            if (!prefixMatches(typePrefix, typePrefix.length, entityKey)) {
                return false;
            }
            KeyParser kp = new KeyParser(entityKey, typePrefix.length + 8);
            final String entityId = kp.getNextString();
            final int prefixlen = kp.getOffset();
            final byte[] deletePrefix = new byte[prefixlen];
            System.arraycopy(entityKey, 0, deletePrefix, 0, prefixlen);
            writeBatch = this.db.createWriteBatch();
            if (LeveldbTimelineStore.LOG.isDebugEnabled()) {
                LeveldbTimelineStore.LOG.debug("Deleting entity type:" + entityType + " id:" + entityId);
            }
            writeBatch.delete(createStartTimeLookupKey(entityId, entityType));
            final EntityIdentifier entityIdentifier = new EntityIdentifier(entityId, entityType);
            this.startTimeReadCache.remove(entityIdentifier);
            this.startTimeWriteCache.remove(entityIdentifier);
            while (iterator.hasNext()) {
                final byte[] key = iterator.peekNext().getKey();
                if (!prefixMatches(entityKey, prefixlen, key)) {
                    break;
                }
                writeBatch.delete(key);
                if (key.length != prefixlen) {
                    if (key[prefixlen] == LeveldbTimelineStore.PRIMARY_FILTERS_COLUMN[0]) {
                        kp = new KeyParser(key, prefixlen + LeveldbTimelineStore.PRIMARY_FILTERS_COLUMN.length);
                        final String name = kp.getNextString();
                        final Object value = GenericObjectMapper.read(key, kp.getOffset());
                        this.deleteKeysWithPrefix(writeBatch, addPrimaryFilterToKey(name, value, deletePrefix), pfIterator);
                        if (LeveldbTimelineStore.LOG.isDebugEnabled()) {
                            LeveldbTimelineStore.LOG.debug("Deleting entity type:" + entityType + " id:" + entityId + " primary filter entry " + name + " " + value);
                        }
                    }
                    else if (key[prefixlen] == LeveldbTimelineStore.RELATED_ENTITIES_COLUMN[0]) {
                        kp = new KeyParser(key, prefixlen + LeveldbTimelineStore.RELATED_ENTITIES_COLUMN.length);
                        final String type = kp.getNextString();
                        final String id = kp.getNextString();
                        final byte[] relatedEntityStartTime = this.getStartTime(id, type);
                        if (relatedEntityStartTime == null) {
                            LeveldbTimelineStore.LOG.warn("Found no start time for related entity " + id + " of type " + type + " while " + "deleting " + entityId + " of type " + entityType);
                        }
                        else {
                            writeBatch.delete(createReverseRelatedEntityKey(id, type, relatedEntityStartTime, entityId, entityType));
                            if (LeveldbTimelineStore.LOG.isDebugEnabled()) {
                                LeveldbTimelineStore.LOG.debug("Deleting entity type:" + entityType + " id:" + entityId + " from invisible reverse related entity " + "entry of type:" + type + " id:" + id);
                            }
                        }
                    }
                    else if (key[prefixlen] == LeveldbTimelineStore.INVISIBLE_REVERSE_RELATED_ENTITIES_COLUMN[0]) {
                        kp = new KeyParser(key, prefixlen + LeveldbTimelineStore.INVISIBLE_REVERSE_RELATED_ENTITIES_COLUMN.length);
                        final String type = kp.getNextString();
                        final String id = kp.getNextString();
                        final byte[] relatedEntityStartTime = this.getStartTime(id, type);
                        if (relatedEntityStartTime == null) {
                            LeveldbTimelineStore.LOG.warn("Found no start time for reverse related entity " + id + " of type " + type + " while " + "deleting " + entityId + " of type " + entityType);
                        }
                        else {
                            writeBatch.delete(createRelatedEntityKey(id, type, relatedEntityStartTime, entityId, entityType));
                            if (LeveldbTimelineStore.LOG.isDebugEnabled()) {
                                LeveldbTimelineStore.LOG.debug("Deleting entity type:" + entityType + " id:" + entityId + " from related entity entry of type:" + type + " id:" + id);
                            }
                        }
                    }
                }
                iterator.next();
            }
            final WriteOptions writeOptions = new WriteOptions();
            writeOptions.sync(true);
            this.db.write(writeBatch, writeOptions);
            return true;
        }
        finally {
            IOUtils.cleanup(LeveldbTimelineStore.LOG, writeBatch);
        }
    }
    
    @VisibleForTesting
    void discardOldEntities(final long timestamp) throws IOException, InterruptedException {
        final byte[] reverseTimestamp = GenericObjectMapper.writeReverseOrderedLong(timestamp);
        long totalCount = 0L;
        final long t1 = System.currentTimeMillis();
        try {
            final List<String> entityTypes = this.getEntityTypes();
            for (final String entityType : entityTypes) {
                DBIterator iterator = null;
                DBIterator pfIterator = null;
                long typeCount = 0L;
                try {
                    this.deleteLock.writeLock().lock();
                    iterator = this.getDbIterator(false);
                    pfIterator = this.getDbIterator(false);
                    if (this.deletionThread != null && this.deletionThread.isInterrupted()) {
                        throw new InterruptedException();
                    }
                    boolean seeked = false;
                    while (this.deleteNextEntity(entityType, reverseTimestamp, iterator, pfIterator, seeked)) {
                        ++typeCount;
                        ++totalCount;
                        seeked = true;
                        if (this.deletionThread != null && this.deletionThread.isInterrupted()) {
                            throw new InterruptedException();
                        }
                    }
                }
                catch (IOException e) {
                    LeveldbTimelineStore.LOG.error("Got IOException while deleting entities for type " + entityType + ", continuing to next type", e);
                }
                finally {
                    IOUtils.cleanup(LeveldbTimelineStore.LOG, iterator, pfIterator);
                    this.deleteLock.writeLock().unlock();
                    if (typeCount > 0L) {
                        LeveldbTimelineStore.LOG.info("Deleted " + typeCount + " entities of type " + entityType);
                    }
                }
            }
        }
        finally {
            final long t2 = System.currentTimeMillis();
            LeveldbTimelineStore.LOG.info("Discarded " + totalCount + " entities for timestamp " + timestamp + " and earlier in " + (t2 - t1) / 1000.0 + " seconds");
        }
    }
    
    @VisibleForTesting
    DBIterator getDbIterator(final boolean fillCache) {
        final ReadOptions readOptions = new ReadOptions();
        readOptions.fillCache(fillCache);
        return this.db.iterator(readOptions);
    }
    
    Version loadVersion() throws IOException {
        final byte[] data = this.db.get(JniDBFactory.bytes("timeline-store-version"));
        if (data == null || data.length == 0) {
            return Version.newInstance(1, 0);
        }
        final Version version = new VersionPBImpl(YarnServerCommonProtos.VersionProto.parseFrom(data));
        return version;
    }
    
    @VisibleForTesting
    void storeVersion(final Version state) throws IOException {
        this.dbStoreVersion(state);
    }
    
    private void dbStoreVersion(final Version state) throws IOException {
        final String key = "timeline-store-version";
        final byte[] data = ((VersionPBImpl)state).getProto().toByteArray();
        try {
            this.db.put(JniDBFactory.bytes(key), data);
        }
        catch (DBException e) {
            throw new IOException(e);
        }
    }
    
    Version getCurrentVersion() {
        return LeveldbTimelineStore.CURRENT_VERSION_INFO;
    }
    
    private void checkVersion() throws IOException {
        final Version loadedVersion = this.loadVersion();
        LeveldbTimelineStore.LOG.info("Loaded timeline store version info " + loadedVersion);
        if (loadedVersion.equals(this.getCurrentVersion())) {
            return;
        }
        if (loadedVersion.isCompatibleTo(this.getCurrentVersion())) {
            LeveldbTimelineStore.LOG.info("Storing timeline store version info " + this.getCurrentVersion());
            this.dbStoreVersion(LeveldbTimelineStore.CURRENT_VERSION_INFO);
            return;
        }
        final String incompatibleMessage = "Incompatible version for timeline store: expecting version " + this.getCurrentVersion() + ", but loading version " + loadedVersion;
        LeveldbTimelineStore.LOG.fatal(incompatibleMessage);
        throw new IOException(incompatibleMessage);
    }
    
    @Override
    public void put(final TimelineDomain domain) throws IOException {
        WriteBatch writeBatch = null;
        try {
            writeBatch = this.db.createWriteBatch();
            if (domain.getId() == null || domain.getId().length() == 0) {
                throw new IllegalArgumentException("Domain doesn't have an ID");
            }
            if (domain.getOwner() == null || domain.getOwner().length() == 0) {
                throw new IllegalArgumentException("Domain doesn't have an owner.");
            }
            byte[] domainEntryKey = createDomainEntryKey(domain.getId(), LeveldbTimelineStore.DESCRIPTION_COLUMN);
            byte[] ownerLookupEntryKey = createOwnerLookupKey(domain.getOwner(), domain.getId(), LeveldbTimelineStore.DESCRIPTION_COLUMN);
            if (domain.getDescription() != null) {
                writeBatch.put(domainEntryKey, domain.getDescription().getBytes());
                writeBatch.put(ownerLookupEntryKey, domain.getDescription().getBytes());
            }
            else {
                writeBatch.put(domainEntryKey, LeveldbTimelineStore.EMPTY_BYTES);
                writeBatch.put(ownerLookupEntryKey, LeveldbTimelineStore.EMPTY_BYTES);
            }
            domainEntryKey = createDomainEntryKey(domain.getId(), LeveldbTimelineStore.OWNER_COLUMN);
            ownerLookupEntryKey = createOwnerLookupKey(domain.getOwner(), domain.getId(), LeveldbTimelineStore.OWNER_COLUMN);
            writeBatch.put(domainEntryKey, domain.getOwner().getBytes());
            writeBatch.put(ownerLookupEntryKey, domain.getOwner().getBytes());
            domainEntryKey = createDomainEntryKey(domain.getId(), LeveldbTimelineStore.READER_COLUMN);
            ownerLookupEntryKey = createOwnerLookupKey(domain.getOwner(), domain.getId(), LeveldbTimelineStore.READER_COLUMN);
            if (domain.getReaders() != null && domain.getReaders().length() > 0) {
                writeBatch.put(domainEntryKey, domain.getReaders().getBytes());
                writeBatch.put(ownerLookupEntryKey, domain.getReaders().getBytes());
            }
            else {
                writeBatch.put(domainEntryKey, LeveldbTimelineStore.EMPTY_BYTES);
                writeBatch.put(ownerLookupEntryKey, LeveldbTimelineStore.EMPTY_BYTES);
            }
            domainEntryKey = createDomainEntryKey(domain.getId(), LeveldbTimelineStore.WRITER_COLUMN);
            ownerLookupEntryKey = createOwnerLookupKey(domain.getOwner(), domain.getId(), LeveldbTimelineStore.WRITER_COLUMN);
            if (domain.getWriters() != null && domain.getWriters().length() > 0) {
                writeBatch.put(domainEntryKey, domain.getWriters().getBytes());
                writeBatch.put(ownerLookupEntryKey, domain.getWriters().getBytes());
            }
            else {
                writeBatch.put(domainEntryKey, LeveldbTimelineStore.EMPTY_BYTES);
                writeBatch.put(ownerLookupEntryKey, LeveldbTimelineStore.EMPTY_BYTES);
            }
            domainEntryKey = createDomainEntryKey(domain.getId(), LeveldbTimelineStore.TIMESTAMP_COLUMN);
            ownerLookupEntryKey = createOwnerLookupKey(domain.getOwner(), domain.getId(), LeveldbTimelineStore.TIMESTAMP_COLUMN);
            final long currentTimestamp = System.currentTimeMillis();
            byte[] timestamps = this.db.get(domainEntryKey);
            if (timestamps == null) {
                timestamps = new byte[16];
                GenericObjectMapper.writeReverseOrderedLong(currentTimestamp, timestamps, 0);
                GenericObjectMapper.writeReverseOrderedLong(currentTimestamp, timestamps, 8);
            }
            else {
                GenericObjectMapper.writeReverseOrderedLong(currentTimestamp, timestamps, 8);
            }
            writeBatch.put(domainEntryKey, timestamps);
            writeBatch.put(ownerLookupEntryKey, timestamps);
            this.db.write(writeBatch);
        }
        finally {
            IOUtils.cleanup(LeveldbTimelineStore.LOG, writeBatch);
        }
    }
    
    private static byte[] createDomainEntryKey(final String domainId, final byte[] columnName) throws IOException {
        return KeyBuilder.newInstance().add(LeveldbTimelineStore.DOMAIN_ENTRY_PREFIX).add(domainId).add(columnName).getBytes();
    }
    
    private static byte[] createOwnerLookupKey(final String owner, final String domainId, final byte[] columnName) throws IOException {
        return KeyBuilder.newInstance().add(LeveldbTimelineStore.OWNER_LOOKUP_PREFIX).add(owner).add(domainId).add(columnName).getBytes();
    }
    
    @Override
    public TimelineDomain getDomain(final String domainId) throws IOException {
        DBIterator iterator = null;
        try {
            final byte[] prefix = KeyBuilder.newInstance().add(LeveldbTimelineStore.DOMAIN_ENTRY_PREFIX).add(domainId).getBytesForLookup();
            iterator = this.db.iterator();
            iterator.seek(prefix);
            return getTimelineDomain(iterator, domainId, prefix);
        }
        finally {
            IOUtils.cleanup(LeveldbTimelineStore.LOG, iterator);
        }
    }
    
    @Override
    public TimelineDomains getDomains(final String owner) throws IOException {
        DBIterator iterator = null;
        try {
            final byte[] prefix = KeyBuilder.newInstance().add(LeveldbTimelineStore.OWNER_LOOKUP_PREFIX).add(owner).getBytesForLookup();
            final List<TimelineDomain> domains = new ArrayList<TimelineDomain>();
            iterator = this.db.iterator();
            iterator.seek(prefix);
            while (iterator.hasNext()) {
                final byte[] key = iterator.peekNext().getKey();
                if (!prefixMatches(prefix, prefix.length, key)) {
                    break;
                }
                final KeyParser kp = new KeyParser(key, prefix.length);
                final String domainId = kp.getNextString();
                final byte[] prefixExt = KeyBuilder.newInstance().add(LeveldbTimelineStore.OWNER_LOOKUP_PREFIX).add(owner).add(domainId).getBytesForLookup();
                final TimelineDomain domainToReturn = getTimelineDomain(iterator, domainId, prefixExt);
                if (domainToReturn == null) {
                    continue;
                }
                domains.add(domainToReturn);
            }
            Collections.sort(domains, new Comparator<TimelineDomain>() {
                @Override
                public int compare(final TimelineDomain domain1, final TimelineDomain domain2) {
                    final int result = domain2.getCreatedTime().compareTo(domain1.getCreatedTime());
                    if (result == 0) {
                        return domain2.getModifiedTime().compareTo(domain1.getModifiedTime());
                    }
                    return result;
                }
            });
            final TimelineDomains domainsToReturn = new TimelineDomains();
            domainsToReturn.addDomains(domains);
            return domainsToReturn;
        }
        finally {
            IOUtils.cleanup(LeveldbTimelineStore.LOG, iterator);
        }
    }
    
    private static TimelineDomain getTimelineDomain(final DBIterator iterator, final String domainId, final byte[] prefix) throws IOException {
        final TimelineDomain domain = new TimelineDomain();
        domain.setId(domainId);
        boolean noRows = true;
        while (iterator.hasNext()) {
            final byte[] key = iterator.peekNext().getKey();
            if (!prefixMatches(prefix, prefix.length, key)) {
                break;
            }
            if (noRows) {
                noRows = false;
            }
            final byte[] value = iterator.peekNext().getValue();
            if (value != null && value.length > 0) {
                if (key[prefix.length] == LeveldbTimelineStore.DESCRIPTION_COLUMN[0]) {
                    domain.setDescription(new String(value));
                }
                else if (key[prefix.length] == LeveldbTimelineStore.OWNER_COLUMN[0]) {
                    domain.setOwner(new String(value));
                }
                else if (key[prefix.length] == LeveldbTimelineStore.READER_COLUMN[0]) {
                    domain.setReaders(new String(value));
                }
                else if (key[prefix.length] == LeveldbTimelineStore.WRITER_COLUMN[0]) {
                    domain.setWriters(new String(value));
                }
                else if (key[prefix.length] == LeveldbTimelineStore.TIMESTAMP_COLUMN[0]) {
                    domain.setCreatedTime(GenericObjectMapper.readReverseOrderedLong(value, 0));
                    domain.setModifiedTime(GenericObjectMapper.readReverseOrderedLong(value, 8));
                }
                else {
                    LeveldbTimelineStore.LOG.error("Unrecognized domain column: " + key[prefix.length]);
                }
            }
            iterator.next();
        }
        if (noRows) {
            return null;
        }
        return domain;
    }
    
    static {
        LOG = LogFactory.getLog(LeveldbTimelineStore.class);
        START_TIME_LOOKUP_PREFIX = "k".getBytes();
        ENTITY_ENTRY_PREFIX = "e".getBytes();
        INDEXED_ENTRY_PREFIX = "i".getBytes();
        EVENTS_COLUMN = "e".getBytes();
        PRIMARY_FILTERS_COLUMN = "f".getBytes();
        OTHER_INFO_COLUMN = "i".getBytes();
        RELATED_ENTITIES_COLUMN = "r".getBytes();
        INVISIBLE_REVERSE_RELATED_ENTITIES_COLUMN = "z".getBytes();
        DOMAIN_ID_COLUMN = "d".getBytes();
        DOMAIN_ENTRY_PREFIX = "d".getBytes();
        OWNER_LOOKUP_PREFIX = "o".getBytes();
        DESCRIPTION_COLUMN = "d".getBytes();
        OWNER_COLUMN = "o".getBytes();
        READER_COLUMN = "r".getBytes();
        WRITER_COLUMN = "w".getBytes();
        TIMESTAMP_COLUMN = "t".getBytes();
        EMPTY_BYTES = new byte[0];
        CURRENT_VERSION_INFO = Version.newInstance(1, 0);
        LEVELDB_DIR_UMASK = FsPermission.createImmutable((short)448);
    }
    
    private static class StartAndInsertTime
    {
        final long startTime;
        final long insertTime;
        
        public StartAndInsertTime(final long startTime, final long insertTime) {
            this.startTime = startTime;
            this.insertTime = insertTime;
        }
    }
    
    private class EntityDeletionThread extends Thread
    {
        private final long ttl;
        private final long ttlInterval;
        
        public EntityDeletionThread(final Configuration conf) {
            this.ttl = conf.getLong("yarn.timeline-service.ttl-ms", 604800000L);
            this.ttlInterval = conf.getLong("yarn.timeline-service.leveldb-timeline-store.ttl-interval-ms", 300000L);
            LeveldbTimelineStore.LOG.info("Starting deletion thread with ttl " + this.ttl + " and cycle " + "interval " + this.ttlInterval);
        }
        
        @Override
        public void run() {
            while (true) {
                final long timestamp = System.currentTimeMillis() - this.ttl;
                try {
                    LeveldbTimelineStore.this.discardOldEntities(timestamp);
                    Thread.sleep(this.ttlInterval);
                }
                catch (IOException e) {
                    LeveldbTimelineStore.LOG.error(e);
                }
                catch (InterruptedException e2) {
                    LeveldbTimelineStore.LOG.info("Deletion thread received interrupt, exiting");
                    break;
                }
            }
        }
    }
    
    private static class LockMap<K>
    {
        private Map<K, CountingReentrantLock<K>> locks;
        
        private LockMap() {
            this.locks = new HashMap<K, CountingReentrantLock<K>>();
        }
        
        synchronized CountingReentrantLock<K> getLock(final K key) {
            CountingReentrantLock<K> lock = this.locks.get(key);
            if (lock == null) {
                lock = new CountingReentrantLock<K>(key);
                this.locks.put(key, lock);
            }
            ((CountingReentrantLock<Object>)lock).count++;
            return lock;
        }
        
        synchronized void returnLock(final CountingReentrantLock<K> lock) {
            if (((CountingReentrantLock<Object>)lock).count == 0) {
                throw new IllegalStateException("Returned lock more times than it was retrieved");
            }
            ((CountingReentrantLock<Object>)lock).count--;
            if (((CountingReentrantLock<Object>)lock).count == 0) {
                this.locks.remove(((CountingReentrantLock<Object>)lock).key);
            }
        }
        
        private static class CountingReentrantLock<K> extends ReentrantLock
        {
            private static final long serialVersionUID = 1L;
            private int count;
            private K key;
            
            CountingReentrantLock(final K key) {
                this.count = 0;
                this.key = key;
            }
        }
    }
    
    private static class KeyBuilder
    {
        private static final int MAX_NUMBER_OF_KEY_ELEMENTS = 10;
        private byte[][] b;
        private boolean[] useSeparator;
        private int index;
        private int length;
        
        public KeyBuilder(final int size) {
            this.b = new byte[size][];
            this.useSeparator = new boolean[size];
            this.index = 0;
            this.length = 0;
        }
        
        public static KeyBuilder newInstance() {
            return new KeyBuilder(10);
        }
        
        public KeyBuilder add(final String s) {
            return this.add(s.getBytes(), true);
        }
        
        public KeyBuilder add(final byte[] t) {
            return this.add(t, false);
        }
        
        public KeyBuilder add(final byte[] t, final boolean sep) {
            this.b[this.index] = t;
            this.useSeparator[this.index] = sep;
            this.length += t.length;
            if (sep) {
                ++this.length;
            }
            ++this.index;
            return this;
        }
        
        public byte[] getBytes() throws IOException {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream(this.length);
            for (int i = 0; i < this.index; ++i) {
                baos.write(this.b[i]);
                if (i < this.index - 1 && this.useSeparator[i]) {
                    baos.write(0);
                }
            }
            return baos.toByteArray();
        }
        
        public byte[] getBytesForLookup() throws IOException {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream(this.length);
            for (int i = 0; i < this.index; ++i) {
                baos.write(this.b[i]);
                if (this.useSeparator[i]) {
                    baos.write(0);
                }
            }
            return baos.toByteArray();
        }
    }
    
    private static class KeyParser
    {
        private final byte[] b;
        private int offset;
        
        public KeyParser(final byte[] b, final int offset) {
            this.b = b;
            this.offset = offset;
        }
        
        public String getNextString() throws IOException {
            if (this.offset >= this.b.length) {
                throw new IOException("tried to read nonexistent string from byte array");
            }
            int i;
            for (i = 0; this.offset + i < this.b.length && this.b[this.offset + i] != 0; ++i) {}
            final String s = new String(this.b, this.offset, i);
            this.offset = this.offset + i + 1;
            return s;
        }
        
        public long getNextLong() throws IOException {
            if (this.offset + 8 >= this.b.length) {
                throw new IOException("byte array ran out when trying to read long");
            }
            final long l = GenericObjectMapper.readReverseOrderedLong(this.b, this.offset);
            this.offset += 8;
            return l;
        }
        
        public int getOffset() {
            return this.offset;
        }
    }
}
