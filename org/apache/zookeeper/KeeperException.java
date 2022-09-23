// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper;

import java.util.Iterator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import org.apache.yetus.audience.InterfaceAudience;

@InterfaceAudience.Public
public abstract class KeeperException extends Exception
{
    private List<OpResult> results;
    private Code code;
    private String path;
    
    public static KeeperException create(final Code code, final String path) {
        final KeeperException r = create(code);
        r.path = path;
        return r;
    }
    
    @Deprecated
    public static KeeperException create(final int code, final String path) {
        final KeeperException r = create(Code.get(code));
        r.path = path;
        return r;
    }
    
    @Deprecated
    public static KeeperException create(final int code) {
        return create(Code.get(code));
    }
    
    public static KeeperException create(final Code code) {
        switch (code) {
            case SYSTEMERROR: {
                return new SystemErrorException();
            }
            case RUNTIMEINCONSISTENCY: {
                return new RuntimeInconsistencyException();
            }
            case DATAINCONSISTENCY: {
                return new DataInconsistencyException();
            }
            case CONNECTIONLOSS: {
                return new ConnectionLossException();
            }
            case MARSHALLINGERROR: {
                return new MarshallingErrorException();
            }
            case UNIMPLEMENTED: {
                return new UnimplementedException();
            }
            case OPERATIONTIMEOUT: {
                return new OperationTimeoutException();
            }
            case BADARGUMENTS: {
                return new BadArgumentsException();
            }
            case APIERROR: {
                return new APIErrorException();
            }
            case NONODE: {
                return new NoNodeException();
            }
            case NOAUTH: {
                return new NoAuthException();
            }
            case BADVERSION: {
                return new BadVersionException();
            }
            case NOCHILDRENFOREPHEMERALS: {
                return new NoChildrenForEphemeralsException();
            }
            case NODEEXISTS: {
                return new NodeExistsException();
            }
            case INVALIDACL: {
                return new InvalidACLException();
            }
            case AUTHFAILED: {
                return new AuthFailedException();
            }
            case NOTEMPTY: {
                return new NotEmptyException();
            }
            case SESSIONEXPIRED: {
                return new SessionExpiredException();
            }
            case INVALIDCALLBACK: {
                return new InvalidCallbackException();
            }
            case SESSIONMOVED: {
                return new SessionMovedException();
            }
            case NOTREADONLY: {
                return new NotReadOnlyException();
            }
            default: {
                throw new IllegalArgumentException("Invalid exception code");
            }
        }
    }
    
    @Deprecated
    public void setCode(final int code) {
        this.code = Code.get(code);
    }
    
    static String getCodeMessage(final Code code) {
        switch (code) {
            case OK: {
                return "ok";
            }
            case SYSTEMERROR: {
                return "SystemError";
            }
            case RUNTIMEINCONSISTENCY: {
                return "RuntimeInconsistency";
            }
            case DATAINCONSISTENCY: {
                return "DataInconsistency";
            }
            case CONNECTIONLOSS: {
                return "ConnectionLoss";
            }
            case MARSHALLINGERROR: {
                return "MarshallingError";
            }
            case UNIMPLEMENTED: {
                return "Unimplemented";
            }
            case OPERATIONTIMEOUT: {
                return "OperationTimeout";
            }
            case BADARGUMENTS: {
                return "BadArguments";
            }
            case APIERROR: {
                return "APIError";
            }
            case NONODE: {
                return "NoNode";
            }
            case NOAUTH: {
                return "NoAuth";
            }
            case BADVERSION: {
                return "BadVersion";
            }
            case NOCHILDRENFOREPHEMERALS: {
                return "NoChildrenForEphemerals";
            }
            case NODEEXISTS: {
                return "NodeExists";
            }
            case INVALIDACL: {
                return "InvalidACL";
            }
            case AUTHFAILED: {
                return "AuthFailed";
            }
            case NOTEMPTY: {
                return "Directory not empty";
            }
            case SESSIONEXPIRED: {
                return "Session expired";
            }
            case INVALIDCALLBACK: {
                return "Invalid callback";
            }
            case SESSIONMOVED: {
                return "Session moved";
            }
            case NOTREADONLY: {
                return "Not a read-only call";
            }
            default: {
                return "Unknown error " + code;
            }
        }
    }
    
    public KeeperException(final Code code) {
        this.code = code;
    }
    
    KeeperException(final Code code, final String path) {
        this.code = code;
        this.path = path;
    }
    
    @Deprecated
    public int getCode() {
        return this.code.code;
    }
    
    public Code code() {
        return this.code;
    }
    
    public String getPath() {
        return this.path;
    }
    
    @Override
    public String getMessage() {
        if (this.path == null) {
            return "KeeperErrorCode = " + getCodeMessage(this.code);
        }
        return "KeeperErrorCode = " + getCodeMessage(this.code) + " for " + this.path;
    }
    
    void setMultiResults(final List<OpResult> results) {
        this.results = results;
    }
    
    public List<OpResult> getResults() {
        return (this.results != null) ? new ArrayList<OpResult>(this.results) : null;
    }
    
    @InterfaceAudience.Public
    public enum Code implements CodeDeprecated
    {
        OK(0), 
        SYSTEMERROR(-1), 
        RUNTIMEINCONSISTENCY(-2), 
        DATAINCONSISTENCY(-3), 
        CONNECTIONLOSS(-4), 
        MARSHALLINGERROR(-5), 
        UNIMPLEMENTED(-6), 
        OPERATIONTIMEOUT(-7), 
        BADARGUMENTS(-8), 
        APIERROR(-100), 
        NONODE(-101), 
        NOAUTH(-102), 
        BADVERSION(-103), 
        NOCHILDRENFOREPHEMERALS(-108), 
        NODEEXISTS(-110), 
        NOTEMPTY(-111), 
        SESSIONEXPIRED(-112), 
        INVALIDCALLBACK(-113), 
        INVALIDACL(-114), 
        AUTHFAILED(-115), 
        SESSIONMOVED(-118), 
        NOTREADONLY(-119);
        
        private static final Map<Integer, Code> lookup;
        private final int code;
        
        private Code(final int code) {
            this.code = code;
        }
        
        public int intValue() {
            return this.code;
        }
        
        public static Code get(final int code) {
            return Code.lookup.get(code);
        }
        
        static {
            lookup = new HashMap<Integer, Code>();
            for (final Code c : EnumSet.allOf(Code.class)) {
                Code.lookup.put(c.code, c);
            }
        }
    }
    
    @InterfaceAudience.Public
    public static class APIErrorException extends KeeperException
    {
        public APIErrorException() {
            super(Code.APIERROR);
        }
    }
    
    @InterfaceAudience.Public
    public static class AuthFailedException extends KeeperException
    {
        public AuthFailedException() {
            super(Code.AUTHFAILED);
        }
    }
    
    @InterfaceAudience.Public
    public static class BadArgumentsException extends KeeperException
    {
        public BadArgumentsException() {
            super(Code.BADARGUMENTS);
        }
        
        public BadArgumentsException(final String path) {
            super(Code.BADARGUMENTS, path);
        }
    }
    
    @InterfaceAudience.Public
    public static class BadVersionException extends KeeperException
    {
        public BadVersionException() {
            super(Code.BADVERSION);
        }
        
        public BadVersionException(final String path) {
            super(Code.BADVERSION, path);
        }
    }
    
    @InterfaceAudience.Public
    public static class ConnectionLossException extends KeeperException
    {
        public ConnectionLossException() {
            super(Code.CONNECTIONLOSS);
        }
    }
    
    @InterfaceAudience.Public
    public static class DataInconsistencyException extends KeeperException
    {
        public DataInconsistencyException() {
            super(Code.DATAINCONSISTENCY);
        }
    }
    
    @InterfaceAudience.Public
    public static class InvalidACLException extends KeeperException
    {
        public InvalidACLException() {
            super(Code.INVALIDACL);
        }
        
        public InvalidACLException(final String path) {
            super(Code.INVALIDACL, path);
        }
    }
    
    @InterfaceAudience.Public
    public static class InvalidCallbackException extends KeeperException
    {
        public InvalidCallbackException() {
            super(Code.INVALIDCALLBACK);
        }
    }
    
    @InterfaceAudience.Public
    public static class MarshallingErrorException extends KeeperException
    {
        public MarshallingErrorException() {
            super(Code.MARSHALLINGERROR);
        }
    }
    
    @InterfaceAudience.Public
    public static class NoAuthException extends KeeperException
    {
        public NoAuthException() {
            super(Code.NOAUTH);
        }
    }
    
    @InterfaceAudience.Public
    public static class NoChildrenForEphemeralsException extends KeeperException
    {
        public NoChildrenForEphemeralsException() {
            super(Code.NOCHILDRENFOREPHEMERALS);
        }
        
        public NoChildrenForEphemeralsException(final String path) {
            super(Code.NOCHILDRENFOREPHEMERALS, path);
        }
    }
    
    @InterfaceAudience.Public
    public static class NodeExistsException extends KeeperException
    {
        public NodeExistsException() {
            super(Code.NODEEXISTS);
        }
        
        public NodeExistsException(final String path) {
            super(Code.NODEEXISTS, path);
        }
    }
    
    @InterfaceAudience.Public
    public static class NoNodeException extends KeeperException
    {
        public NoNodeException() {
            super(Code.NONODE);
        }
        
        public NoNodeException(final String path) {
            super(Code.NONODE, path);
        }
    }
    
    @InterfaceAudience.Public
    public static class NotEmptyException extends KeeperException
    {
        public NotEmptyException() {
            super(Code.NOTEMPTY);
        }
        
        public NotEmptyException(final String path) {
            super(Code.NOTEMPTY, path);
        }
    }
    
    @InterfaceAudience.Public
    public static class OperationTimeoutException extends KeeperException
    {
        public OperationTimeoutException() {
            super(Code.OPERATIONTIMEOUT);
        }
    }
    
    @InterfaceAudience.Public
    public static class RuntimeInconsistencyException extends KeeperException
    {
        public RuntimeInconsistencyException() {
            super(Code.RUNTIMEINCONSISTENCY);
        }
    }
    
    @InterfaceAudience.Public
    public static class SessionExpiredException extends KeeperException
    {
        public SessionExpiredException() {
            super(Code.SESSIONEXPIRED);
        }
    }
    
    @InterfaceAudience.Public
    public static class SessionMovedException extends KeeperException
    {
        public SessionMovedException() {
            super(Code.SESSIONMOVED);
        }
    }
    
    @InterfaceAudience.Public
    public static class NotReadOnlyException extends KeeperException
    {
        public NotReadOnlyException() {
            super(Code.NOTREADONLY);
        }
    }
    
    @InterfaceAudience.Public
    public static class SystemErrorException extends KeeperException
    {
        public SystemErrorException() {
            super(Code.SYSTEMERROR);
        }
    }
    
    @InterfaceAudience.Public
    public static class UnimplementedException extends KeeperException
    {
        public UnimplementedException() {
            super(Code.UNIMPLEMENTED);
        }
    }
    
    @Deprecated
    @InterfaceAudience.Public
    public interface CodeDeprecated
    {
        @Deprecated
        public static final int Ok = 0;
        @Deprecated
        public static final int SystemError = -1;
        @Deprecated
        public static final int RuntimeInconsistency = -2;
        @Deprecated
        public static final int DataInconsistency = -3;
        @Deprecated
        public static final int ConnectionLoss = -4;
        @Deprecated
        public static final int MarshallingError = -5;
        @Deprecated
        public static final int Unimplemented = -6;
        @Deprecated
        public static final int OperationTimeout = -7;
        @Deprecated
        public static final int BadArguments = -8;
        @Deprecated
        public static final int APIError = -100;
        @Deprecated
        public static final int NoNode = -101;
        @Deprecated
        public static final int NoAuth = -102;
        @Deprecated
        public static final int BadVersion = -103;
        @Deprecated
        public static final int NoChildrenForEphemerals = -108;
        @Deprecated
        public static final int NodeExists = -110;
        @Deprecated
        public static final int NotEmpty = -111;
        @Deprecated
        public static final int SessionExpired = -112;
        @Deprecated
        public static final int InvalidCallback = -113;
        @Deprecated
        public static final int InvalidACL = -114;
        @Deprecated
        public static final int AuthFailed = -115;
    }
}
