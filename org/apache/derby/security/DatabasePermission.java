// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.security;

import java.util.HashSet;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.apache.derby.iapi.util.StringUtil;
import java.util.Locale;
import java.io.IOException;
import java.util.Set;
import java.security.Permission;

public final class DatabasePermission extends Permission
{
    public static final String URL_PROTOCOL_DIRECTORY = "directory:";
    public static final String URL_PATH_INCLUSIVE_STRING = "<<ALL FILES>>";
    public static final char URL_PATH_INCLUSIVE_CHAR = 'I';
    public static final char URL_PATH_SEPARATOR_CHAR = '/';
    public static final char URL_PATH_RELATIVE_CHAR = '.';
    public static final char URL_PATH_WILDCARD_CHAR = '*';
    public static final char URL_PATH_RECURSIVE_CHAR = '-';
    public static final String URL_PATH_SEPARATOR_STRING;
    public static final String URL_PATH_RELATIVE_STRING;
    public static final String URL_PATH_RELATIVE_PREFIX;
    public static final String URL_PATH_WILDCARD_STRING;
    public static final String URL_PATH_WILDCARD_SUFFIX;
    public static final String URL_PATH_RECURSIVE_STRING;
    public static final String URL_PATH_RECURSIVE_SUFFIX;
    public static final String CREATE = "create";
    protected static final Set LEGAL_ACTIONS;
    private final String url;
    private transient String path;
    private transient String parentPath;
    private char pathType;
    
    public DatabasePermission(final String s, final String s2) throws IOException {
        super(s);
        this.initActions(s2);
        this.initLocation(s);
        this.url = s;
    }
    
    protected void initActions(String lowerCase) {
        if (lowerCase == null) {
            throw new NullPointerException("actions can't be null");
        }
        if (lowerCase.length() == 0) {
            throw new IllegalArgumentException("actions can't be empty");
        }
        lowerCase = lowerCase.toLowerCase(Locale.ENGLISH);
        final String[] split = StringUtil.split(lowerCase, ',');
        for (int i = 0; i < split.length; ++i) {
            final String trim = split[i].trim();
            if (!DatabasePermission.LEGAL_ACTIONS.contains(trim)) {
                throw new IllegalArgumentException("Illegal action '" + trim + "'");
            }
        }
    }
    
    protected void initLocation(final String str) throws IOException {
        if (str == null) {
            throw new NullPointerException("URL can't be null");
        }
        if (str.length() == 0) {
            throw new IllegalArgumentException("URL can't be empty");
        }
        if (!str.startsWith("directory:")) {
            throw new IllegalArgumentException("Unsupported protocol in URL '" + str + "'");
        }
        String str2 = str.substring("directory:".length());
        if (str2.equals("<<ALL FILES>>")) {
            this.pathType = 'I';
        }
        else if (str2.equals(DatabasePermission.URL_PATH_RECURSIVE_STRING)) {
            this.pathType = '-';
            str2 = DatabasePermission.URL_PATH_RELATIVE_PREFIX;
        }
        else if (str2.equals(DatabasePermission.URL_PATH_WILDCARD_STRING)) {
            this.pathType = '*';
            str2 = DatabasePermission.URL_PATH_RELATIVE_PREFIX;
        }
        else if (str2.endsWith(DatabasePermission.URL_PATH_RECURSIVE_SUFFIX)) {
            this.pathType = '-';
            str2 = str2.substring(0, str2.length() - 1);
        }
        else if (str2.endsWith(DatabasePermission.URL_PATH_WILDCARD_SUFFIX)) {
            this.pathType = '*';
            str2 = str2.substring(0, str2.length() - 1);
        }
        else {
            this.pathType = '/';
        }
        if (this.pathType == 'I') {
            this.path = "<<ALL FILES>>";
        }
        else {
            if (str2.startsWith(DatabasePermission.URL_PATH_RELATIVE_PREFIX)) {
                str2 = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction() {
                    public Object run() {
                        return System.getProperty("user.dir");
                    }
                }) + DatabasePermission.URL_PATH_SEPARATOR_STRING + str2;
            }
            final String s = str2;
            File file;
            try {
                file = AccessController.doPrivileged((PrivilegedExceptionAction<File>)new PrivilegedExceptionAction() {
                    public Object run() throws IOException {
                        return new File(s).getCanonicalFile();
                    }
                });
            }
            catch (PrivilegedActionException ex) {
                throw (IOException)ex.getCause();
            }
            this.path = file.getPath();
            this.parentPath = ((this.pathType != '/') ? this.path : file.getParent());
        }
    }
    
    public boolean implies(final Permission permission) {
        if (!(permission instanceof DatabasePermission)) {
            return false;
        }
        final DatabasePermission databasePermission = (DatabasePermission)permission;
        if (this.pathType == 'I') {
            return true;
        }
        if (databasePermission.pathType == 'I') {
            return false;
        }
        if (this.pathType == '-') {
            return databasePermission.parentPath != null && databasePermission.parentPath.startsWith(this.path);
        }
        if (databasePermission.pathType == '-') {
            return false;
        }
        if (this.pathType == '*') {
            return this.path.equals(databasePermission.parentPath);
        }
        return databasePermission.pathType != '*' && this.path.equals(databasePermission.path);
    }
    
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof DatabasePermission)) {
            return false;
        }
        final DatabasePermission databasePermission = (DatabasePermission)o;
        return this.pathType == databasePermission.pathType && this.path.equals(databasePermission.path);
    }
    
    public int hashCode() {
        return this.path.hashCode() ^ this.pathType;
    }
    
    public String getActions() {
        return "create";
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.initLocation(this.url);
    }
    
    static {
        URL_PATH_SEPARATOR_STRING = String.valueOf('/');
        URL_PATH_RELATIVE_STRING = String.valueOf('.');
        URL_PATH_RELATIVE_PREFIX = DatabasePermission.URL_PATH_RELATIVE_STRING + '/';
        URL_PATH_WILDCARD_STRING = String.valueOf('*');
        URL_PATH_WILDCARD_SUFFIX = DatabasePermission.URL_PATH_SEPARATOR_STRING + '*';
        URL_PATH_RECURSIVE_STRING = String.valueOf('-');
        URL_PATH_RECURSIVE_SUFFIX = DatabasePermission.URL_PATH_SEPARATOR_STRING + '-';
        (LEGAL_ACTIONS = new HashSet()).add("create");
    }
}
