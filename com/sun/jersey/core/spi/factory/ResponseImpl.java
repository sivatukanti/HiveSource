// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.spi.factory;

import com.sun.jersey.core.header.OutBoundHeaders;
import java.lang.reflect.Type;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

public class ResponseImpl extends Response
{
    private final StatusType statusType;
    private final MultivaluedMap<String, Object> headers;
    private final Object entity;
    private final Type entityType;
    
    protected ResponseImpl(final StatusType statusType, final OutBoundHeaders headers, final Object entity, final Type entityType) {
        this.statusType = statusType;
        this.headers = headers;
        this.entity = entity;
        this.entityType = entityType;
    }
    
    protected ResponseImpl(final int status, final OutBoundHeaders headers, final Object entity, final Type entityType) {
        this.statusType = toStatusType(status);
        this.headers = headers;
        this.entity = entity;
        this.entityType = entityType;
    }
    
    public StatusType getStatusType() {
        return this.statusType;
    }
    
    public Type getEntityType() {
        return this.entityType;
    }
    
    @Override
    public int getStatus() {
        return this.statusType.getStatusCode();
    }
    
    @Override
    public MultivaluedMap<String, Object> getMetadata() {
        return this.headers;
    }
    
    @Override
    public Object getEntity() {
        return this.entity;
    }
    
    public static StatusType toStatusType(final int statusCode) {
        switch (statusCode) {
            case 200: {
                return Status.OK;
            }
            case 201: {
                return Status.CREATED;
            }
            case 202: {
                return Status.ACCEPTED;
            }
            case 204: {
                return Status.NO_CONTENT;
            }
            case 301: {
                return Status.MOVED_PERMANENTLY;
            }
            case 303: {
                return Status.SEE_OTHER;
            }
            case 304: {
                return Status.NOT_MODIFIED;
            }
            case 307: {
                return Status.TEMPORARY_REDIRECT;
            }
            case 400: {
                return Status.BAD_REQUEST;
            }
            case 401: {
                return Status.UNAUTHORIZED;
            }
            case 403: {
                return Status.FORBIDDEN;
            }
            case 404: {
                return Status.NOT_FOUND;
            }
            case 406: {
                return Status.NOT_ACCEPTABLE;
            }
            case 409: {
                return Status.CONFLICT;
            }
            case 410: {
                return Status.GONE;
            }
            case 412: {
                return Status.PRECONDITION_FAILED;
            }
            case 415: {
                return Status.UNSUPPORTED_MEDIA_TYPE;
            }
            case 500: {
                return Status.INTERNAL_SERVER_ERROR;
            }
            case 503: {
                return Status.SERVICE_UNAVAILABLE;
            }
            default: {
                return new StatusType() {
                    @Override
                    public int getStatusCode() {
                        return statusCode;
                    }
                    
                    @Override
                    public Status.Family getFamily() {
                        return ResponseImpl.toFamilyCode(statusCode);
                    }
                    
                    @Override
                    public String getReasonPhrase() {
                        return "";
                    }
                };
            }
        }
    }
    
    public static Status.Family toFamilyCode(final int statusCode) {
        switch (statusCode / 100) {
            case 1: {
                return Status.Family.INFORMATIONAL;
            }
            case 2: {
                return Status.Family.SUCCESSFUL;
            }
            case 3: {
                return Status.Family.REDIRECTION;
            }
            case 4: {
                return Status.Family.CLIENT_ERROR;
            }
            case 5: {
                return Status.Family.SERVER_ERROR;
            }
            default: {
                return Status.Family.OTHER;
            }
        }
    }
}
