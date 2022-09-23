// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import java.lang.reflect.Constructor;
import java.io.InputStream;
import java.net.HttpURLConnection;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.LinkedHashMap;
import javax.servlet.http.HttpServletResponse;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class HttpExceptionUtils
{
    public static final String ERROR_JSON = "RemoteException";
    public static final String ERROR_EXCEPTION_JSON = "exception";
    public static final String ERROR_CLASSNAME_JSON = "javaClassName";
    public static final String ERROR_MESSAGE_JSON = "message";
    private static final String APPLICATION_JSON_MIME = "application/json";
    private static final String ENTER;
    
    public static void createServletExceptionResponse(final HttpServletResponse response, final int status, final Throwable ex) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        final Map<String, Object> json = new LinkedHashMap<String, Object>();
        json.put("message", getOneLineMessage(ex));
        json.put("exception", ex.getClass().getSimpleName());
        json.put("javaClassName", ex.getClass().getName());
        final Map<String, Object> jsonResponse = new LinkedHashMap<String, Object>();
        jsonResponse.put("RemoteException", json);
        final Writer writer = response.getWriter();
        JsonSerialization.writer().writeValue(writer, jsonResponse);
        writer.flush();
    }
    
    public static Response createJerseyExceptionResponse(final Response.Status status, final Throwable ex) {
        final Map<String, Object> json = new LinkedHashMap<String, Object>();
        json.put("message", getOneLineMessage(ex));
        json.put("exception", ex.getClass().getSimpleName());
        json.put("javaClassName", ex.getClass().getName());
        final Map<String, Object> response = new LinkedHashMap<String, Object>();
        response.put("RemoteException", json);
        return Response.status(status).type("application/json").entity(response).build();
    }
    
    private static String getOneLineMessage(final Throwable exception) {
        String message = exception.getMessage();
        if (message != null) {
            final int i = message.indexOf(HttpExceptionUtils.ENTER);
            if (i > -1) {
                message = message.substring(0, i);
            }
        }
        return message;
    }
    
    private static void throwEx(final Throwable ex) {
        throwException(ex);
    }
    
    private static <E extends Throwable> void throwException(final Throwable ex) throws E, Throwable {
        throw ex;
    }
    
    public static void validateResponse(final HttpURLConnection conn, final int expectedStatus) throws IOException {
        if (conn.getResponseCode() != expectedStatus) {
            InputStream es = null;
            Exception toThrow;
            try {
                es = conn.getErrorStream();
                Map json = JsonSerialization.mapReader().readValue(es);
                json = json.get("RemoteException");
                final String exClass = json.get("javaClassName");
                final String exMsg = json.get("message");
                if (exClass != null) {
                    try {
                        final ClassLoader cl = HttpExceptionUtils.class.getClassLoader();
                        final Class klass = cl.loadClass(exClass);
                        final Constructor constr = klass.getConstructor(String.class);
                        toThrow = constr.newInstance(exMsg);
                    }
                    catch (Exception ex) {
                        toThrow = new IOException(String.format("HTTP status [%d], exception [%s], message [%s] ", conn.getResponseCode(), exClass, exMsg));
                    }
                }
                else {
                    final String msg = (exMsg != null) ? exMsg : conn.getResponseMessage();
                    toThrow = new IOException(String.format("HTTP status [%d], message [%s]", conn.getResponseCode(), msg));
                }
            }
            catch (Exception ex2) {
                toThrow = new IOException(String.format("HTTP status [%d], message [%s]", conn.getResponseCode(), conn.getResponseMessage()));
            }
            finally {
                if (es != null) {
                    try {
                        es.close();
                    }
                    catch (IOException ex3) {}
                }
            }
            throwEx(toThrow);
        }
    }
    
    static {
        ENTER = System.getProperty("line.separator");
    }
}
