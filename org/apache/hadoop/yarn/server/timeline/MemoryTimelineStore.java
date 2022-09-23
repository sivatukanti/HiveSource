// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.timeline;

import java.util.HashSet;
import org.apache.hadoop.yarn.api.records.timeline.TimelinePutResponse;
import java.util.Comparator;
import org.apache.hadoop.yarn.api.records.timeline.TimelineDomains;
import java.io.IOException;
import org.apache.hadoop.yarn.api.records.timeline.TimelineEvent;
import org.apache.hadoop.yarn.api.records.timeline.TimelineEvents;
import java.util.SortedSet;
import java.util.List;
import java.util.Iterator;
import java.util.Collections;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.TreeSet;
import org.apache.hadoop.yarn.api.records.timeline.TimelineEntities;
import java.util.EnumSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import org.apache.hadoop.yarn.api.records.timeline.TimelineDomain;
import org.apache.hadoop.yarn.api.records.timeline.TimelineEntity;
import java.util.Map;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.service.AbstractService;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class MemoryTimelineStore extends AbstractService implements TimelineStore
{
    private Map<EntityIdentifier, TimelineEntity> entities;
    private Map<EntityIdentifier, Long> entityInsertTimes;
    private Map<String, TimelineDomain> domainsById;
    private Map<String, Set<TimelineDomain>> domainsByOwner;
    
    public MemoryTimelineStore() {
        super(MemoryTimelineStore.class.getName());
        this.entities = new HashMap<EntityIdentifier, TimelineEntity>();
        this.entityInsertTimes = new HashMap<EntityIdentifier, Long>();
        this.domainsById = new HashMap<String, TimelineDomain>();
        this.domainsByOwner = new HashMap<String, Set<TimelineDomain>>();
    }
    
    @Override
    public synchronized TimelineEntities getEntities(final String entityType, Long limit, Long windowStart, Long windowEnd, final String fromId, final Long fromTs, final NameValuePair primaryFilter, final Collection<NameValuePair> secondaryFilters, EnumSet<TimelineReader.Field> fields) {
        if (limit == null) {
            limit = 100L;
        }
        if (windowStart == null) {
            windowStart = Long.MIN_VALUE;
        }
        if (windowEnd == null) {
            windowEnd = Long.MAX_VALUE;
        }
        if (fields == null) {
            fields = EnumSet.allOf(TimelineReader.Field.class);
        }
        Iterator<TimelineEntity> entityIterator = null;
        if (fromId != null) {
            final TimelineEntity firstEntity = this.entities.get(new EntityIdentifier(fromId, entityType));
            if (firstEntity == null) {
                return new TimelineEntities();
            }
            entityIterator = new TreeSet<TimelineEntity>(this.entities.values()).tailSet(firstEntity, true).iterator();
        }
        if (entityIterator == null) {
            entityIterator = new PriorityQueue<TimelineEntity>(this.entities.values()).iterator();
        }
        final List<TimelineEntity> entitiesSelected = new ArrayList<TimelineEntity>();
        while (entityIterator.hasNext()) {
            final TimelineEntity entity = entityIterator.next();
            if (entitiesSelected.size() >= limit) {
                break;
            }
            if (!entity.getEntityType().equals(entityType)) {
                continue;
            }
            if (entity.getStartTime() <= windowStart) {
                continue;
            }
            if (entity.getStartTime() > windowEnd) {
                continue;
            }
            if (fromTs != null && this.entityInsertTimes.get(new EntityIdentifier(entity.getEntityId(), entity.getEntityType())) > fromTs) {
                continue;
            }
            if (primaryFilter != null && !matchPrimaryFilter(entity.getPrimaryFilters(), primaryFilter)) {
                continue;
            }
            if (secondaryFilters != null) {
                boolean flag = true;
                for (final NameValuePair secondaryFilter : secondaryFilters) {
                    if (secondaryFilter != null && !matchPrimaryFilter(entity.getPrimaryFilters(), secondaryFilter) && !matchFilter(entity.getOtherInfo(), secondaryFilter)) {
                        flag = false;
                        break;
                    }
                }
                if (!flag) {
                    continue;
                }
            }
            entitiesSelected.add(entity);
        }
        final List<TimelineEntity> entitiesToReturn = new ArrayList<TimelineEntity>();
        for (final TimelineEntity entitySelected : entitiesSelected) {
            entitiesToReturn.add(maskFields(entitySelected, fields));
        }
        Collections.sort(entitiesToReturn);
        final TimelineEntities entitiesWrapper = new TimelineEntities();
        entitiesWrapper.setEntities(entitiesToReturn);
        return entitiesWrapper;
    }
    
    @Override
    public synchronized TimelineEntity getEntity(final String entityId, final String entityType, EnumSet<TimelineReader.Field> fieldsToRetrieve) {
        if (fieldsToRetrieve == null) {
            fieldsToRetrieve = EnumSet.allOf(TimelineReader.Field.class);
        }
        final TimelineEntity entity = this.entities.get(new EntityIdentifier(entityId, entityType));
        if (entity == null) {
            return null;
        }
        return maskFields(entity, fieldsToRetrieve);
    }
    
    @Override
    public synchronized TimelineEvents getEntityTimelines(final String entityType, final SortedSet<String> entityIds, Long limit, Long windowStart, Long windowEnd, final Set<String> eventTypes) {
        final TimelineEvents allEvents = new TimelineEvents();
        if (entityIds == null) {
            return allEvents;
        }
        if (limit == null) {
            limit = 100L;
        }
        if (windowStart == null) {
            windowStart = Long.MIN_VALUE;
        }
        if (windowEnd == null) {
            windowEnd = Long.MAX_VALUE;
        }
        for (final String entityId : entityIds) {
            final EntityIdentifier entityID = new EntityIdentifier(entityId, entityType);
            final TimelineEntity entity = this.entities.get(entityID);
            if (entity == null) {
                continue;
            }
            final TimelineEvents.EventsOfOneEntity events = new TimelineEvents.EventsOfOneEntity();
            events.setEntityId(entityId);
            events.setEntityType(entityType);
            for (final TimelineEvent event : entity.getEvents()) {
                if (events.getEvents().size() >= limit) {
                    break;
                }
                if (event.getTimestamp() <= windowStart) {
                    continue;
                }
                if (event.getTimestamp() > windowEnd) {
                    continue;
                }
                if (eventTypes != null && !eventTypes.contains(event.getEventType())) {
                    continue;
                }
                events.addEvent(event);
            }
            allEvents.addEvent(events);
        }
        return allEvents;
    }
    
    @Override
    public TimelineDomain getDomain(final String domainId) throws IOException {
        final TimelineDomain domain = this.domainsById.get(domainId);
        if (domain == null) {
            return null;
        }
        return createTimelineDomain(domain.getId(), domain.getDescription(), domain.getOwner(), domain.getReaders(), domain.getWriters(), domain.getCreatedTime(), domain.getModifiedTime());
    }
    
    @Override
    public TimelineDomains getDomains(final String owner) throws IOException {
        final List<TimelineDomain> domains = new ArrayList<TimelineDomain>();
        final Set<TimelineDomain> domainsOfOneOwner = this.domainsByOwner.get(owner);
        if (domainsOfOneOwner == null) {
            return new TimelineDomains();
        }
        for (final TimelineDomain domain : this.domainsByOwner.get(owner)) {
            final TimelineDomain domainToReturn = createTimelineDomain(domain.getId(), domain.getDescription(), domain.getOwner(), domain.getReaders(), domain.getWriters(), domain.getCreatedTime(), domain.getModifiedTime());
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
    
    @Override
    public synchronized TimelinePutResponse put(final TimelineEntities data) {
        final TimelinePutResponse response = new TimelinePutResponse();
        for (final TimelineEntity entity : data.getEntities()) {
            final EntityIdentifier entityId = new EntityIdentifier(entity.getEntityId(), entity.getEntityType());
            TimelineEntity existingEntity = this.entities.get(entityId);
            if (existingEntity == null) {
                existingEntity = new TimelineEntity();
                existingEntity.setEntityId(entity.getEntityId());
                existingEntity.setEntityType(entity.getEntityType());
                existingEntity.setStartTime(entity.getStartTime());
                if (entity.getDomainId() == null || entity.getDomainId().length() == 0) {
                    final TimelinePutResponse.TimelinePutError error = new TimelinePutResponse.TimelinePutError();
                    error.setEntityId(entityId.getId());
                    error.setEntityType(entityId.getType());
                    error.setErrorCode(5);
                    response.addError(error);
                    continue;
                }
                existingEntity.setDomainId(entity.getDomainId());
                this.entities.put(entityId, existingEntity);
                this.entityInsertTimes.put(entityId, System.currentTimeMillis());
            }
            if (entity.getEvents() != null) {
                if (existingEntity.getEvents() == null) {
                    existingEntity.setEvents(entity.getEvents());
                }
                else {
                    existingEntity.addEvents(entity.getEvents());
                }
                Collections.sort(existingEntity.getEvents());
            }
            if (existingEntity.getStartTime() == null) {
                if (existingEntity.getEvents() == null || existingEntity.getEvents().isEmpty()) {
                    final TimelinePutResponse.TimelinePutError error = new TimelinePutResponse.TimelinePutError();
                    error.setEntityId(entityId.getId());
                    error.setEntityType(entityId.getType());
                    error.setErrorCode(1);
                    response.addError(error);
                    this.entities.remove(entityId);
                    this.entityInsertTimes.remove(entityId);
                    continue;
                }
                Long min = Long.MAX_VALUE;
                for (final TimelineEvent e : entity.getEvents()) {
                    if (min > e.getTimestamp()) {
                        min = e.getTimestamp();
                    }
                }
                existingEntity.setStartTime(min);
            }
            if (entity.getPrimaryFilters() != null) {
                if (existingEntity.getPrimaryFilters() == null) {
                    existingEntity.setPrimaryFilters(new HashMap<String, Set<Object>>());
                }
                for (final Map.Entry<String, Set<Object>> pf : entity.getPrimaryFilters().entrySet()) {
                    for (final Object pfo : pf.getValue()) {
                        existingEntity.addPrimaryFilter(pf.getKey(), maybeConvert(pfo));
                    }
                }
            }
            if (entity.getOtherInfo() != null) {
                if (existingEntity.getOtherInfo() == null) {
                    existingEntity.setOtherInfo(new HashMap<String, Object>());
                }
                for (final Map.Entry<String, Object> info : entity.getOtherInfo().entrySet()) {
                    existingEntity.addOtherInfo(info.getKey(), maybeConvert(info.getValue()));
                }
            }
            if (entity.getRelatedEntities() == null) {
                continue;
            }
            for (final Map.Entry<String, Set<String>> partRelatedEntities : entity.getRelatedEntities().entrySet()) {
                if (partRelatedEntities == null) {
                    continue;
                }
                for (final String idStr : partRelatedEntities.getValue()) {
                    final EntityIdentifier relatedEntityId = new EntityIdentifier(idStr, partRelatedEntities.getKey());
                    TimelineEntity relatedEntity = this.entities.get(relatedEntityId);
                    if (relatedEntity != null) {
                        if (relatedEntity.getDomainId().equals(existingEntity.getDomainId())) {
                            relatedEntity.addRelatedEntity(existingEntity.getEntityType(), existingEntity.getEntityId());
                        }
                        else {
                            final TimelinePutResponse.TimelinePutError error2 = new TimelinePutResponse.TimelinePutError();
                            error2.setEntityType(existingEntity.getEntityType());
                            error2.setEntityId(existingEntity.getEntityId());
                            error2.setErrorCode(6);
                            response.addError(error2);
                        }
                    }
                    else {
                        relatedEntity = new TimelineEntity();
                        relatedEntity.setEntityId(relatedEntityId.getId());
                        relatedEntity.setEntityType(relatedEntityId.getType());
                        relatedEntity.setStartTime(existingEntity.getStartTime());
                        relatedEntity.addRelatedEntity(existingEntity.getEntityType(), existingEntity.getEntityId());
                        relatedEntity.setDomainId(existingEntity.getDomainId());
                        this.entities.put(relatedEntityId, relatedEntity);
                        this.entityInsertTimes.put(relatedEntityId, System.currentTimeMillis());
                    }
                }
            }
        }
        return response;
    }
    
    @Override
    public void put(final TimelineDomain domain) throws IOException {
        final TimelineDomain domainToReplace = this.domainsById.get(domain.getId());
        final long currentTimestamp = System.currentTimeMillis();
        final TimelineDomain domainToStore = createTimelineDomain(domain.getId(), domain.getDescription(), domain.getOwner(), domain.getReaders(), domain.getWriters(), (domainToReplace == null) ? currentTimestamp : domainToReplace.getCreatedTime(), currentTimestamp);
        this.domainsById.put(domainToStore.getId(), domainToStore);
        Set<TimelineDomain> domainsByOneOwner = this.domainsByOwner.get(domainToStore.getOwner());
        if (domainsByOneOwner == null) {
            domainsByOneOwner = new HashSet<TimelineDomain>();
            this.domainsByOwner.put(domainToStore.getOwner(), domainsByOneOwner);
        }
        if (domainToReplace != null) {
            domainsByOneOwner.remove(domainToReplace);
        }
        domainsByOneOwner.add(domainToStore);
    }
    
    private static TimelineDomain createTimelineDomain(final String id, final String description, final String owner, final String readers, final String writers, final Long createdTime, final Long modifiedTime) {
        final TimelineDomain domainToStore = new TimelineDomain();
        domainToStore.setId(id);
        domainToStore.setDescription(description);
        domainToStore.setOwner(owner);
        domainToStore.setReaders(readers);
        domainToStore.setWriters(writers);
        domainToStore.setCreatedTime(createdTime);
        domainToStore.setModifiedTime(modifiedTime);
        return domainToStore;
    }
    
    private static TimelineEntity maskFields(final TimelineEntity entity, final EnumSet<TimelineReader.Field> fields) {
        final TimelineEntity entityToReturn = new TimelineEntity();
        entityToReturn.setEntityId(entity.getEntityId());
        entityToReturn.setEntityType(entity.getEntityType());
        entityToReturn.setStartTime(entity.getStartTime());
        entityToReturn.setDomainId(entity.getDomainId());
        if (fields.contains(TimelineReader.Field.EVENTS)) {
            entityToReturn.addEvents(entity.getEvents());
        }
        else if (fields.contains(TimelineReader.Field.LAST_EVENT_ONLY)) {
            entityToReturn.addEvent(entity.getEvents().get(0));
        }
        else {
            entityToReturn.setEvents(null);
        }
        if (fields.contains(TimelineReader.Field.RELATED_ENTITIES)) {
            entityToReturn.addRelatedEntities(entity.getRelatedEntities());
        }
        else {
            entityToReturn.setRelatedEntities(null);
        }
        if (fields.contains(TimelineReader.Field.PRIMARY_FILTERS)) {
            entityToReturn.addPrimaryFilters(entity.getPrimaryFilters());
        }
        else {
            entityToReturn.setPrimaryFilters(null);
        }
        if (fields.contains(TimelineReader.Field.OTHER_INFO)) {
            entityToReturn.addOtherInfo(entity.getOtherInfo());
        }
        else {
            entityToReturn.setOtherInfo(null);
        }
        return entityToReturn;
    }
    
    private static boolean matchFilter(final Map<String, Object> tags, final NameValuePair filter) {
        final Object value = tags.get(filter.getName());
        return value != null && value.equals(filter.getValue());
    }
    
    private static boolean matchPrimaryFilter(final Map<String, Set<Object>> tags, final NameValuePair filter) {
        final Set<Object> value = tags.get(filter.getName());
        return value != null && value.contains(filter.getValue());
    }
    
    private static Object maybeConvert(final Object o) {
        if (o instanceof Long) {
            final Long l = (Long)o;
            if (l >= -2147483648L && l <= 2147483647L) {
                return l.intValue();
            }
        }
        return o;
    }
}
