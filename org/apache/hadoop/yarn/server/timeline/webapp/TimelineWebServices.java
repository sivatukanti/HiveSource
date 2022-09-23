// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.timeline.webapp;

import javax.xml.bind.annotation.XmlElement;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.logging.LogFactory;
import java.util.List;
import java.util.ArrayList;
import org.apache.hadoop.yarn.server.timeline.TimelineReader;
import java.util.EnumSet;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import org.apache.hadoop.yarn.server.timeline.GenericObjectMapper;
import org.apache.hadoop.yarn.server.timeline.NameValuePair;
import java.util.TreeSet;
import java.util.SortedSet;
import org.apache.hadoop.yarn.api.records.timeline.TimelineDomains;
import javax.ws.rs.PUT;
import java.io.IOException;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.api.records.timeline.TimelineDomain;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.webapp.ForbiddenException;
import org.apache.hadoop.yarn.api.records.timeline.TimelinePutResponse;
import org.apache.hadoop.yarn.api.records.timeline.TimelineEvents;
import org.apache.hadoop.yarn.webapp.NotFoundException;
import org.apache.hadoop.yarn.server.timeline.EntityIdentifier;
import org.apache.hadoop.yarn.api.records.timeline.TimelineEntity;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.apache.hadoop.yarn.webapp.BadRequestException;
import org.apache.hadoop.yarn.api.records.timeline.TimelineEntities;
import javax.ws.rs.QueryParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import javax.servlet.http.HttpServletRequest;
import com.google.inject.Inject;
import org.apache.hadoop.yarn.server.timeline.TimelineDataManager;
import org.apache.commons.logging.Log;
import javax.ws.rs.Path;
import com.google.inject.Singleton;

@Singleton
@Path("/ws/v1/timeline")
public class TimelineWebServices
{
    private static final Log LOG;
    private TimelineDataManager timelineDataManager;
    
    @Inject
    public TimelineWebServices(final TimelineDataManager timelineDataManager) {
        this.timelineDataManager = timelineDataManager;
    }
    
    @GET
    @Produces({ "application/json" })
    public AboutInfo about(@Context final HttpServletRequest req, @Context final HttpServletResponse res) {
        this.init(res);
        return new AboutInfo("Timeline API");
    }
    
    @GET
    @Path("/{entityType}")
    @Produces({ "application/json" })
    public TimelineEntities getEntities(@Context final HttpServletRequest req, @Context final HttpServletResponse res, @PathParam("entityType") final String entityType, @QueryParam("primaryFilter") final String primaryFilter, @QueryParam("secondaryFilter") final String secondaryFilter, @QueryParam("windowStart") final String windowStart, @QueryParam("windowEnd") final String windowEnd, @QueryParam("fromId") final String fromId, @QueryParam("fromTs") final String fromTs, @QueryParam("limit") final String limit, @QueryParam("fields") final String fields) {
        this.init(res);
        try {
            return this.timelineDataManager.getEntities(parseStr(entityType), parsePairStr(primaryFilter, ":"), parsePairsStr(secondaryFilter, ",", ":"), parseLongStr(windowStart), parseLongStr(windowEnd), parseStr(fromId), parseLongStr(fromTs), parseLongStr(limit), parseFieldsStr(fields, ","), getUser(req));
        }
        catch (NumberFormatException e2) {
            throw new BadRequestException("windowStart, windowEnd or limit is not a numeric value.");
        }
        catch (IllegalArgumentException e3) {
            throw new BadRequestException("requested invalid field.");
        }
        catch (Exception e) {
            TimelineWebServices.LOG.error("Error getting entities", e);
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GET
    @Path("/{entityType}/{entityId}")
    @Produces({ "application/json" })
    public TimelineEntity getEntity(@Context final HttpServletRequest req, @Context final HttpServletResponse res, @PathParam("entityType") final String entityType, @PathParam("entityId") final String entityId, @QueryParam("fields") final String fields) {
        this.init(res);
        TimelineEntity entity = null;
        try {
            entity = this.timelineDataManager.getEntity(parseStr(entityType), parseStr(entityId), parseFieldsStr(fields, ","), getUser(req));
        }
        catch (IllegalArgumentException e2) {
            throw new BadRequestException("requested invalid field.");
        }
        catch (Exception e) {
            TimelineWebServices.LOG.error("Error getting entity", e);
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        }
        if (entity == null) {
            throw new NotFoundException("Timeline entity " + new EntityIdentifier(parseStr(entityId), parseStr(entityType)) + " is not found");
        }
        return entity;
    }
    
    @GET
    @Path("/{entityType}/events")
    @Produces({ "application/json" })
    public TimelineEvents getEvents(@Context final HttpServletRequest req, @Context final HttpServletResponse res, @PathParam("entityType") final String entityType, @QueryParam("entityId") final String entityId, @QueryParam("eventType") final String eventType, @QueryParam("windowStart") final String windowStart, @QueryParam("windowEnd") final String windowEnd, @QueryParam("limit") final String limit) {
        this.init(res);
        try {
            return this.timelineDataManager.getEvents(parseStr(entityType), parseArrayStr(entityId, ","), parseArrayStr(eventType, ","), parseLongStr(windowStart), parseLongStr(windowEnd), parseLongStr(limit), getUser(req));
        }
        catch (NumberFormatException e2) {
            throw new BadRequestException("windowStart, windowEnd or limit is not a numeric value.");
        }
        catch (Exception e) {
            TimelineWebServices.LOG.error("Error getting entity timelines", e);
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
    
    @POST
    @Consumes({ "application/json" })
    public TimelinePutResponse postEntities(@Context final HttpServletRequest req, @Context final HttpServletResponse res, final TimelineEntities entities) {
        this.init(res);
        final UserGroupInformation callerUGI = getUser(req);
        if (callerUGI == null) {
            final String msg = "The owner of the posted timeline entities is not set";
            TimelineWebServices.LOG.error(msg);
            throw new ForbiddenException(msg);
        }
        try {
            return this.timelineDataManager.postEntities(entities, callerUGI);
        }
        catch (Exception e) {
            TimelineWebServices.LOG.error("Error putting entities", e);
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PUT
    @Path("/domain")
    @Consumes({ "application/json" })
    public TimelinePutResponse putDomain(@Context final HttpServletRequest req, @Context final HttpServletResponse res, final TimelineDomain domain) {
        this.init(res);
        final UserGroupInformation callerUGI = getUser(req);
        if (callerUGI == null) {
            final String msg = "The owner of the posted timeline domain is not set";
            TimelineWebServices.LOG.error(msg);
            throw new ForbiddenException(msg);
        }
        domain.setOwner(callerUGI.getShortUserName());
        try {
            this.timelineDataManager.putDomain(domain, callerUGI);
        }
        catch (YarnException e) {
            TimelineWebServices.LOG.error(e.getMessage(), e);
            throw new ForbiddenException(e);
        }
        catch (IOException e2) {
            TimelineWebServices.LOG.error("Error putting domain", e2);
            throw new WebApplicationException(e2, Response.Status.INTERNAL_SERVER_ERROR);
        }
        return new TimelinePutResponse();
    }
    
    @GET
    @Path("/domain/{domainId}")
    @Produces({ "application/json" })
    public TimelineDomain getDomain(@Context final HttpServletRequest req, @Context final HttpServletResponse res, @PathParam("domainId") String domainId) {
        this.init(res);
        domainId = parseStr(domainId);
        if (domainId == null || domainId.length() == 0) {
            throw new BadRequestException("Domain ID is not specified.");
        }
        TimelineDomain domain = null;
        try {
            domain = this.timelineDataManager.getDomain(parseStr(domainId), getUser(req));
        }
        catch (Exception e) {
            TimelineWebServices.LOG.error("Error getting domain", e);
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        }
        if (domain == null) {
            throw new NotFoundException("Timeline domain [" + domainId + "] is not found");
        }
        return domain;
    }
    
    @GET
    @Path("/domain")
    @Produces({ "application/json" })
    public TimelineDomains getDomains(@Context final HttpServletRequest req, @Context final HttpServletResponse res, @QueryParam("owner") String owner) {
        this.init(res);
        owner = parseStr(owner);
        final UserGroupInformation callerUGI = getUser(req);
        if (owner == null || owner.length() == 0) {
            if (callerUGI == null) {
                throw new BadRequestException("Domain owner is not specified.");
            }
            owner = callerUGI.getShortUserName();
        }
        try {
            return this.timelineDataManager.getDomains(owner, callerUGI);
        }
        catch (Exception e) {
            TimelineWebServices.LOG.error("Error getting domains", e);
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
    
    private void init(final HttpServletResponse response) {
        response.setContentType(null);
    }
    
    private static UserGroupInformation getUser(final HttpServletRequest req) {
        final String remoteUser = req.getRemoteUser();
        UserGroupInformation callerUGI = null;
        if (remoteUser != null) {
            callerUGI = UserGroupInformation.createRemoteUser(remoteUser);
        }
        return callerUGI;
    }
    
    private static SortedSet<String> parseArrayStr(final String str, final String delimiter) {
        if (str == null) {
            return null;
        }
        final SortedSet<String> strSet = new TreeSet<String>();
        final String[] arr$;
        final String[] strs = arr$ = str.split(delimiter);
        for (final String aStr : arr$) {
            strSet.add(aStr.trim());
        }
        return strSet;
    }
    
    private static NameValuePair parsePairStr(final String str, final String delimiter) {
        if (str == null) {
            return null;
        }
        final String[] strs = str.split(delimiter, 2);
        try {
            return new NameValuePair(strs[0].trim(), GenericObjectMapper.OBJECT_READER.readValue(strs[1].trim()));
        }
        catch (Exception e) {
            return new NameValuePair(strs[0].trim(), strs[1].trim());
        }
    }
    
    private static Collection<NameValuePair> parsePairsStr(final String str, final String aDelimiter, final String pDelimiter) {
        if (str == null) {
            return null;
        }
        final String[] strs = str.split(aDelimiter);
        final Set<NameValuePair> pairs = new HashSet<NameValuePair>();
        for (final String aStr : strs) {
            pairs.add(parsePairStr(aStr, pDelimiter));
        }
        return pairs;
    }
    
    private static EnumSet<TimelineReader.Field> parseFieldsStr(final String str, final String delimiter) {
        if (str == null) {
            return null;
        }
        final String[] strs = str.split(delimiter);
        final List<TimelineReader.Field> fieldList = new ArrayList<TimelineReader.Field>();
        for (String s : strs) {
            s = s.trim().toUpperCase();
            if (s.equals("EVENTS")) {
                fieldList.add(TimelineReader.Field.EVENTS);
            }
            else if (s.equals("LASTEVENTONLY")) {
                fieldList.add(TimelineReader.Field.LAST_EVENT_ONLY);
            }
            else if (s.equals("RELATEDENTITIES")) {
                fieldList.add(TimelineReader.Field.RELATED_ENTITIES);
            }
            else if (s.equals("PRIMARYFILTERS")) {
                fieldList.add(TimelineReader.Field.PRIMARY_FILTERS);
            }
            else {
                if (!s.equals("OTHERINFO")) {
                    throw new IllegalArgumentException("Requested nonexistent field " + s);
                }
                fieldList.add(TimelineReader.Field.OTHER_INFO);
            }
        }
        if (fieldList.size() == 0) {
            return null;
        }
        final TimelineReader.Field f1 = fieldList.remove(fieldList.size() - 1);
        if (fieldList.size() == 0) {
            return EnumSet.of(f1);
        }
        return EnumSet.of(f1, (TimelineReader.Field[])fieldList.toArray((E[])new TimelineReader.Field[fieldList.size()]));
    }
    
    private static Long parseLongStr(final String str) {
        return (str == null) ? null : Long.valueOf(Long.parseLong(str.trim()));
    }
    
    private static String parseStr(final String str) {
        return (str == null) ? null : str.trim();
    }
    
    static {
        LOG = LogFactory.getLog(TimelineWebServices.class);
    }
    
    @XmlRootElement(name = "about")
    @XmlAccessorType(XmlAccessType.NONE)
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public static class AboutInfo
    {
        private String about;
        
        public AboutInfo() {
        }
        
        public AboutInfo(final String about) {
            this.about = about;
        }
        
        @XmlElement(name = "About")
        public String getAbout() {
            return this.about;
        }
        
        public void setAbout(final String about) {
            this.about = about;
        }
    }
}
