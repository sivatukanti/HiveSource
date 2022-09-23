// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.util;

import org.apache.hadoop.security.SecurityUtil;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.security.token.TokenIdentifier;
import java.net.InetSocketAddress;
import org.apache.hadoop.yarn.api.records.Token;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.net.URISyntaxException;
import java.net.URI;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.yarn.api.records.URL;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class ConverterUtils
{
    public static final String APPLICATION_PREFIX = "application";
    public static final String CONTAINER_PREFIX = "container";
    public static final String APPLICATION_ATTEMPT_PREFIX = "appattempt";
    
    public static Path getPathFromYarnURL(final URL url) throws URISyntaxException {
        final String scheme = (url.getScheme() == null) ? "" : url.getScheme();
        String authority = "";
        if (url.getHost() != null) {
            authority = url.getHost();
            if (url.getUserInfo() != null) {
                authority = url.getUserInfo() + "@" + authority;
            }
            if (url.getPort() > 0) {
                authority = authority + ":" + url.getPort();
            }
        }
        return new Path(new URI(scheme, authority, url.getFile(), null, null).normalize());
    }
    
    public static Map<String, String> convertToString(final Map<CharSequence, CharSequence> env) {
        final Map<String, String> stringMap = new HashMap<String, String>();
        for (final Map.Entry<CharSequence, CharSequence> entry : env.entrySet()) {
            stringMap.put(entry.getKey().toString(), entry.getValue().toString());
        }
        return stringMap;
    }
    
    public static URL getYarnUrlFromPath(final Path path) {
        return getYarnUrlFromURI(path.toUri());
    }
    
    public static URL getYarnUrlFromURI(final URI uri) {
        final URL url = RecordFactoryProvider.getRecordFactory(null).newRecordInstance(URL.class);
        if (uri.getHost() != null) {
            url.setHost(uri.getHost());
        }
        if (uri.getUserInfo() != null) {
            url.setUserInfo(uri.getUserInfo());
        }
        url.setPort(uri.getPort());
        url.setScheme(uri.getScheme());
        url.setFile(uri.getPath());
        return url;
    }
    
    public static String toString(final ApplicationId appId) {
        return appId.toString();
    }
    
    public static ApplicationId toApplicationId(final RecordFactory recordFactory, final String appIdStr) {
        final Iterator<String> it = StringHelper._split(appIdStr).iterator();
        it.next();
        return toApplicationId(recordFactory, it);
    }
    
    private static ApplicationId toApplicationId(final RecordFactory recordFactory, final Iterator<String> it) {
        final ApplicationId appId = ApplicationId.newInstance(Long.parseLong(it.next()), Integer.parseInt(it.next()));
        return appId;
    }
    
    private static ApplicationAttemptId toApplicationAttemptId(final Iterator<String> it) throws NumberFormatException {
        final ApplicationId appId = ApplicationId.newInstance(Long.parseLong(it.next()), Integer.parseInt(it.next()));
        final ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(appId, Integer.parseInt(it.next()));
        return appAttemptId;
    }
    
    private static ApplicationId toApplicationId(final Iterator<String> it) throws NumberFormatException {
        final ApplicationId appId = ApplicationId.newInstance(Long.parseLong(it.next()), Integer.parseInt(it.next()));
        return appId;
    }
    
    public static String toString(final ContainerId cId) {
        return (cId == null) ? null : cId.toString();
    }
    
    public static NodeId toNodeIdWithDefaultPort(final String nodeIdStr) {
        if (nodeIdStr.indexOf(":") < 0) {
            return toNodeId(nodeIdStr + ":0");
        }
        return toNodeId(nodeIdStr);
    }
    
    public static NodeId toNodeId(final String nodeIdStr) {
        final String[] parts = nodeIdStr.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid NodeId [" + nodeIdStr + "]. Expected host:port");
        }
        try {
            final NodeId nodeId = NodeId.newInstance(parts[0].trim(), Integer.parseInt(parts[1]));
            return nodeId;
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid port: " + parts[1], e);
        }
    }
    
    public static ContainerId toContainerId(final String containerIdStr) {
        return ContainerId.fromString(containerIdStr);
    }
    
    public static ApplicationAttemptId toApplicationAttemptId(final String applicationAttmeptIdStr) {
        final Iterator<String> it = StringHelper._split(applicationAttmeptIdStr).iterator();
        if (!it.next().equals("appattempt")) {
            throw new IllegalArgumentException("Invalid AppAttemptId prefix: " + applicationAttmeptIdStr);
        }
        try {
            return toApplicationAttemptId(it);
        }
        catch (NumberFormatException n) {
            throw new IllegalArgumentException("Invalid AppAttemptId: " + applicationAttmeptIdStr, n);
        }
    }
    
    public static ApplicationId toApplicationId(final String appIdStr) {
        final Iterator<String> it = StringHelper._split(appIdStr).iterator();
        if (!it.next().equals("application")) {
            throw new IllegalArgumentException("Invalid ApplicationId prefix: " + appIdStr + ". The valid ApplicationId should start with prefix " + "application");
        }
        try {
            return toApplicationId(it);
        }
        catch (NumberFormatException n) {
            throw new IllegalArgumentException("Invalid AppAttemptId: " + appIdStr, n);
        }
    }
    
    public static <T extends TokenIdentifier> org.apache.hadoop.security.token.Token<T> convertFromYarn(final Token protoToken, final InetSocketAddress serviceAddr) {
        final org.apache.hadoop.security.token.Token<T> token = new org.apache.hadoop.security.token.Token<T>(protoToken.getIdentifier().array(), protoToken.getPassword().array(), new Text(protoToken.getKind()), new Text(protoToken.getService()));
        if (serviceAddr != null) {
            SecurityUtil.setTokenService(token, serviceAddr);
        }
        return token;
    }
    
    public static <T extends TokenIdentifier> org.apache.hadoop.security.token.Token<T> convertFromYarn(final Token protoToken, final Text service) {
        final org.apache.hadoop.security.token.Token<T> token = new org.apache.hadoop.security.token.Token<T>(protoToken.getIdentifier().array(), protoToken.getPassword().array(), new Text(protoToken.getKind()), new Text(protoToken.getService()));
        if (service != null) {
            token.setService(service);
        }
        return token;
    }
}
