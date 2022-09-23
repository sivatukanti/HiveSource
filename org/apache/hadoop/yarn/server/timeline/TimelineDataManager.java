// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.timeline;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.yarn.api.records.timeline.TimelineDomains;
import java.util.List;
import org.apache.hadoop.yarn.util.StringHelper;
import org.apache.hadoop.yarn.util.timeline.TimelineUtils;
import java.util.ArrayList;
import org.apache.hadoop.yarn.api.records.timeline.TimelinePutResponse;
import java.util.Set;
import org.apache.hadoop.yarn.api.records.timeline.TimelineEvents;
import java.util.SortedSet;
import java.io.IOException;
import java.util.Iterator;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.api.records.ApplicationAccessType;
import org.apache.hadoop.yarn.api.records.timeline.TimelineEntity;
import org.apache.hadoop.yarn.api.records.timeline.TimelineEntities;
import java.util.EnumSet;
import java.util.Collection;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.records.timeline.TimelineDomain;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.server.timeline.security.TimelineACLsManager;
import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.logging.Log;
import org.apache.hadoop.service.AbstractService;

public class TimelineDataManager extends AbstractService
{
    private static final Log LOG;
    @VisibleForTesting
    public static final String DEFAULT_DOMAIN_ID = "DEFAULT";
    private TimelineStore store;
    private TimelineACLsManager timelineACLsManager;
    
    public TimelineDataManager(final TimelineStore store, final TimelineACLsManager timelineACLsManager) {
        super(TimelineDataManager.class.getName());
        this.store = store;
        (this.timelineACLsManager = timelineACLsManager).setTimelineStore(store);
    }
    
    @Override
    protected void serviceInit(final Configuration conf) throws Exception {
        TimelineDomain domain = this.store.getDomain("DEFAULT");
        if (domain == null) {
            domain = new TimelineDomain();
            domain.setId("DEFAULT");
            domain.setDescription("System Default Domain");
            domain.setOwner(UserGroupInformation.getCurrentUser().getShortUserName());
            domain.setReaders("*");
            domain.setWriters("*");
            this.store.put(domain);
        }
        super.serviceInit(conf);
    }
    
    public TimelineEntities getEntities(final String entityType, final NameValuePair primaryFilter, final Collection<NameValuePair> secondaryFilter, final Long windowStart, final Long windowEnd, final String fromId, final Long fromTs, final Long limit, final EnumSet<TimelineReader.Field> fields, final UserGroupInformation callerUGI) throws YarnException, IOException {
        TimelineEntities entities = null;
        entities = this.store.getEntities(entityType, limit, windowStart, windowEnd, fromId, fromTs, primaryFilter, secondaryFilter, fields);
        if (entities != null) {
            final Iterator<TimelineEntity> entitiesItr = entities.getEntities().iterator();
            while (entitiesItr.hasNext()) {
                final TimelineEntity entity = entitiesItr.next();
                addDefaultDomainIdIfAbsent(entity);
                try {
                    if (this.timelineACLsManager.checkAccess(callerUGI, ApplicationAccessType.VIEW_APP, entity)) {
                        continue;
                    }
                    entitiesItr.remove();
                }
                catch (YarnException e) {
                    TimelineDataManager.LOG.error("Error when verifying access for user " + callerUGI + " on the events of the timeline entity " + new EntityIdentifier(entity.getEntityId(), entity.getEntityType()), e);
                    entitiesItr.remove();
                }
            }
        }
        if (entities == null) {
            return new TimelineEntities();
        }
        return entities;
    }
    
    public TimelineEntity getEntity(final String entityType, final String entityId, final EnumSet<TimelineReader.Field> fields, final UserGroupInformation callerUGI) throws YarnException, IOException {
        TimelineEntity entity = null;
        entity = this.store.getEntity(entityId, entityType, fields);
        if (entity != null) {
            addDefaultDomainIdIfAbsent(entity);
            if (!this.timelineACLsManager.checkAccess(callerUGI, ApplicationAccessType.VIEW_APP, entity)) {
                entity = null;
            }
        }
        return entity;
    }
    
    public TimelineEvents getEvents(final String entityType, final SortedSet<String> entityIds, final SortedSet<String> eventTypes, final Long windowStart, final Long windowEnd, final Long limit, final UserGroupInformation callerUGI) throws YarnException, IOException {
        TimelineEvents events = null;
        events = this.store.getEntityTimelines(entityType, entityIds, limit, windowStart, windowEnd, eventTypes);
        if (events != null) {
            final Iterator<TimelineEvents.EventsOfOneEntity> eventsItr = events.getAllEvents().iterator();
            while (eventsItr.hasNext()) {
                final TimelineEvents.EventsOfOneEntity eventsOfOneEntity = eventsItr.next();
                try {
                    final TimelineEntity entity = this.store.getEntity(eventsOfOneEntity.getEntityId(), eventsOfOneEntity.getEntityType(), EnumSet.of(TimelineReader.Field.PRIMARY_FILTERS));
                    addDefaultDomainIdIfAbsent(entity);
                    if (this.timelineACLsManager.checkAccess(callerUGI, ApplicationAccessType.VIEW_APP, entity)) {
                        continue;
                    }
                    eventsItr.remove();
                }
                catch (Exception e) {
                    TimelineDataManager.LOG.error("Error when verifying access for user " + callerUGI + " on the events of the timeline entity " + new EntityIdentifier(eventsOfOneEntity.getEntityId(), eventsOfOneEntity.getEntityType()), e);
                    eventsItr.remove();
                }
            }
        }
        if (events == null) {
            return new TimelineEvents();
        }
        return events;
    }
    
    public TimelinePutResponse postEntities(final TimelineEntities entities, final UserGroupInformation callerUGI) throws YarnException, IOException {
        if (entities == null) {
            return new TimelinePutResponse();
        }
        final List<EntityIdentifier> entityIDs = new ArrayList<EntityIdentifier>();
        final TimelineEntities entitiesToPut = new TimelineEntities();
        final List<TimelinePutResponse.TimelinePutError> errors = new ArrayList<TimelinePutResponse.TimelinePutError>();
        for (final TimelineEntity entity : entities.getEntities()) {
            final EntityIdentifier entityID = new EntityIdentifier(entity.getEntityId(), entity.getEntityType());
            if (entity.getDomainId() == null || entity.getDomainId().length() == 0) {
                entity.setDomainId("DEFAULT");
            }
            TimelineEntity existingEntity = null;
            try {
                existingEntity = this.store.getEntity(entityID.getId(), entityID.getType(), EnumSet.of(TimelineReader.Field.PRIMARY_FILTERS));
                if (existingEntity != null) {
                    addDefaultDomainIdIfAbsent(existingEntity);
                    if (!existingEntity.getDomainId().equals(entity.getDomainId())) {
                        throw new YarnException("The domain of the timeline entity " + entityID + " is not allowed to be changed.");
                    }
                }
                if (!this.timelineACLsManager.checkAccess(callerUGI, ApplicationAccessType.MODIFY_APP, entity)) {
                    throw new YarnException(callerUGI + " is not allowed to put the timeline entity " + entityID + " into the domain " + entity.getDomainId() + ".");
                }
            }
            catch (Exception e) {
                TimelineDataManager.LOG.error("Skip the timeline entity: " + entityID, e);
                final TimelinePutResponse.TimelinePutError error = new TimelinePutResponse.TimelinePutError();
                error.setEntityId(entityID.getId());
                error.setEntityType(entityID.getType());
                error.setErrorCode(4);
                errors.add(error);
                continue;
            }
            entityIDs.add(entityID);
            entitiesToPut.addEntity(entity);
            if (TimelineDataManager.LOG.isDebugEnabled()) {
                TimelineDataManager.LOG.debug("Storing the entity " + entityID + ", JSON-style content: " + TimelineUtils.dumpTimelineRecordtoJSON(entity));
            }
        }
        if (TimelineDataManager.LOG.isDebugEnabled()) {
            TimelineDataManager.LOG.debug("Storing entities: " + StringHelper.CSV_JOINER.join(entityIDs));
        }
        final TimelinePutResponse response = this.store.put(entitiesToPut);
        response.addErrors(errors);
        return response;
    }
    
    public void putDomain(final TimelineDomain domain, final UserGroupInformation callerUGI) throws YarnException, IOException {
        final TimelineDomain existingDomain = this.store.getDomain(domain.getId());
        if (existingDomain != null) {
            if (!this.timelineACLsManager.checkAccess(callerUGI, existingDomain)) {
                throw new YarnException(callerUGI.getShortUserName() + " is not allowed to override an existing domain " + existingDomain.getId());
            }
            domain.setOwner(existingDomain.getOwner());
        }
        this.store.put(domain);
        if (existingDomain != null) {
            this.timelineACLsManager.replaceIfExist(domain);
        }
    }
    
    public TimelineDomain getDomain(final String domainId, final UserGroupInformation callerUGI) throws YarnException, IOException {
        final TimelineDomain domain = this.store.getDomain(domainId);
        if (domain != null && this.timelineACLsManager.checkAccess(callerUGI, domain)) {
            return domain;
        }
        return null;
    }
    
    public TimelineDomains getDomains(final String owner, final UserGroupInformation callerUGI) throws YarnException, IOException {
        final TimelineDomains domains = this.store.getDomains(owner);
        boolean hasAccess = true;
        if (domains.getDomains().size() > 0) {
            hasAccess = this.timelineACLsManager.checkAccess(callerUGI, domains.getDomains().get(0));
        }
        if (hasAccess) {
            return domains;
        }
        return new TimelineDomains();
    }
    
    private static void addDefaultDomainIdIfAbsent(final TimelineEntity entity) {
        if (entity.getDomainId() == null) {
            entity.setDomainId("DEFAULT");
        }
    }
    
    static {
        LOG = LogFactory.getLog(TimelineDataManager.class);
    }
}
