// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.io;

import java.util.Set;
import java.util.List;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import java.util.ArrayList;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import org.apache.derby.iapi.services.info.JVMInfo;
import org.apache.derby.iapi.services.property.PropertyUtil;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.OutputStream;
import org.apache.derby.io.WritableStorageFactory;
import java.io.InputStream;
import org.apache.derby.io.StorageFile;
import org.apache.derby.io.StorageFactory;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public abstract class FileUtil
{
    private static final int BUFFER_SIZE = 16384;
    private static final Object region;
    private static boolean initialized;
    private static Method setWrite;
    private static Method setRead;
    private static Method setExec;
    private static Class fileClz;
    private static Class filesClz;
    private static Class pathClz;
    private static Class pathsClz;
    private static Class aclEntryClz;
    private static Class aclFileAttributeViewClz;
    private static Class posixFileAttributeViewClz;
    private static Class userPrincipalClz;
    private static Class linkOptionArrayClz;
    private static Class linkOptionClz;
    private static Class stringArrayClz;
    private static Class aclEntryBuilderClz;
    private static Class aclEntryTypeClz;
    private static Class fileStoreClz;
    private static Class aclEntryPermissionClz;
    private static Method get;
    private static Method getFileAttributeView;
    private static Method supportsFileAttributeView;
    private static Method getFileStore;
    private static Method getOwner;
    private static Method getAcl;
    private static Method setAcl;
    private static Method principal;
    private static Method getName;
    private static Method build;
    private static Method newBuilder;
    private static Method setPrincipal;
    private static Method setType;
    private static Method values;
    private static Method setPermissions;
    private static Field allow;
    
    public static boolean removeDirectory(final File parent) {
        if (parent == null) {
            return false;
        }
        if (!parent.exists()) {
            return true;
        }
        if (!parent.isDirectory()) {
            return false;
        }
        final String[] list = parent.list();
        if (list != null) {
            for (int i = 0; i < list.length; ++i) {
                final File file = new File(parent, list[i]);
                if (file.isDirectory()) {
                    if (!removeDirectory(file)) {
                        return false;
                    }
                }
                else if (!file.delete()) {
                    return false;
                }
            }
        }
        return parent.delete();
    }
    
    public static boolean removeDirectory(final String pathname) {
        return removeDirectory(new File(pathname));
    }
    
    public static boolean copyDirectory(final File file, final File file2) {
        return copyDirectory(file, file2, null, null);
    }
    
    public static boolean copyDirectory(final String pathname, final String pathname2) {
        return copyDirectory(new File(pathname), new File(pathname2));
    }
    
    public static boolean copyDirectory(final File parent, final File file, byte[] array, final String[] array2) {
        if (parent == null) {
            return false;
        }
        if (!parent.exists()) {
            return true;
        }
        if (!parent.isDirectory()) {
            return false;
        }
        if (file.exists()) {
            return false;
        }
        if (!file.mkdirs()) {
            return false;
        }
        limitAccessToOwner(file);
        final String[] list = parent.list();
        if (list != null) {
            if (array == null) {
                array = new byte[16384];
            }
        Label_0183:
            for (int i = 0; i < list.length; ++i) {
                final String child = list[i];
                if (array2 != null) {
                    for (int j = 0; j < array2.length; ++j) {
                        if (child.equals(array2[j])) {
                            continue Label_0183;
                        }
                    }
                }
                final File file2 = new File(parent, child);
                if (file2.isDirectory()) {
                    if (!copyDirectory(file2, new File(file, child), array, array2)) {
                        return false;
                    }
                }
                else if (!copyFile(file2, new File(file, child), array)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public static boolean copyFile(final File file, final File file2, byte[] b) {
        if (b == null) {
            b = new byte[16384];
        }
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            fileOutputStream = new FileOutputStream(file2);
            limitAccessToOwner(file2);
            for (int i = fileInputStream.read(b); i != -1; i = fileInputStream.read(b)) {
                fileOutputStream.write(b, 0, i);
            }
            fileInputStream.close();
            fileInputStream = null;
            fileOutputStream.getFD().sync();
            fileOutputStream.close();
            fileOutputStream = null;
        }
        catch (IOException ex) {
            return false;
        }
        finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                }
                catch (IOException ex2) {}
            }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                }
                catch (IOException ex3) {}
            }
        }
        return true;
    }
    
    public static boolean copyDirectory(final StorageFactory storageFactory, final StorageFile storageFile, final File file, byte[] array, final String[] array2, final boolean b) {
        if (storageFile == null) {
            return false;
        }
        if (!storageFile.exists()) {
            return true;
        }
        if (!storageFile.isDirectory()) {
            return false;
        }
        if (file.exists()) {
            return false;
        }
        if (!file.mkdirs()) {
            return false;
        }
        limitAccessToOwner(file);
        final String[] list = storageFile.list();
        if (list != null) {
            if (array == null) {
                array = new byte[16384];
            }
        Label_0203:
            for (int i = 0; i < list.length; ++i) {
                final String s = list[i];
                if (array2 != null) {
                    for (int j = 0; j < array2.length; ++j) {
                        if (s.equals(array2[j])) {
                            continue Label_0203;
                        }
                    }
                }
                final StorageFile storageFile2 = storageFactory.newStorageFile(storageFile, s);
                if (storageFile2.isDirectory()) {
                    if (b && !copyDirectory(storageFactory, storageFile2, new File(file, s), array, array2, b)) {
                        return false;
                    }
                }
                else if (!copyFile(storageFactory, storageFile2, new File(file, s), array)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public static boolean copyFile(final StorageFactory storageFactory, final StorageFile storageFile, final File file) {
        return copyFile(storageFactory, storageFile, file, null);
    }
    
    public static boolean copyFile(final StorageFactory storageFactory, final StorageFile storageFile, final File file, byte[] b) {
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            inputStream = storageFile.getInputStream();
            fileOutputStream = new FileOutputStream(file);
            limitAccessToOwner(file);
            if (b == null) {
                b = new byte[16384];
            }
            for (int i = inputStream.read(b); i != -1; i = inputStream.read(b)) {
                fileOutputStream.write(b, 0, i);
            }
            inputStream.close();
            inputStream = null;
            fileOutputStream.getFD().sync();
            fileOutputStream.close();
            fileOutputStream = null;
        }
        catch (IOException ex) {
            return false;
        }
        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                }
                catch (IOException ex2) {}
            }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                }
                catch (IOException ex3) {}
            }
        }
        return true;
    }
    
    public static boolean copyDirectory(final WritableStorageFactory writableStorageFactory, final File file, final StorageFile storageFile) {
        return copyDirectory(writableStorageFactory, file, storageFile, null, null);
    }
    
    public static boolean copyDirectory(final WritableStorageFactory writableStorageFactory, final File parent, final StorageFile storageFile, byte[] array, final String[] array2) {
        if (parent == null) {
            return false;
        }
        if (!parent.exists()) {
            return true;
        }
        if (!parent.isDirectory()) {
            return false;
        }
        if (storageFile.exists()) {
            return false;
        }
        if (!storageFile.mkdirs()) {
            return false;
        }
        storageFile.limitAccessToOwner();
        final String[] list = parent.list();
        if (list != null) {
            if (array == null) {
                array = new byte[16384];
            }
        Label_0193:
            for (int i = 0; i < list.length; ++i) {
                final String child = list[i];
                if (array2 != null) {
                    for (int j = 0; j < array2.length; ++j) {
                        if (child.equals(array2[j])) {
                            continue Label_0193;
                        }
                    }
                }
                final File file = new File(parent, child);
                if (file.isDirectory()) {
                    if (!copyDirectory(writableStorageFactory, file, writableStorageFactory.newStorageFile(storageFile, child), array, array2)) {
                        return false;
                    }
                }
                else if (!copyFile(writableStorageFactory, file, writableStorageFactory.newStorageFile(storageFile, child), array)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public static boolean copyFile(final WritableStorageFactory writableStorageFactory, final File file, final StorageFile storageFile) {
        return copyFile(writableStorageFactory, file, storageFile, null);
    }
    
    public static boolean copyFile(final WritableStorageFactory writableStorageFactory, final File file, final StorageFile storageFile, byte[] b) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(file);
            outputStream = storageFile.getOutputStream();
            if (b == null) {
                b = new byte[16384];
            }
            for (int i = inputStream.read(b); i != -1; i = inputStream.read(b)) {
                outputStream.write(b, 0, i);
            }
            inputStream.close();
            inputStream = null;
            writableStorageFactory.sync(outputStream, false);
            outputStream.close();
            outputStream = null;
        }
        catch (IOException ex) {
            return false;
        }
        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                }
                catch (IOException ex2) {}
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                }
                catch (IOException ex3) {}
            }
        }
        return true;
    }
    
    public static boolean copyFile(final WritableStorageFactory writableStorageFactory, final StorageFile storageFile, final StorageFile storageFile2) {
        return copyFile(writableStorageFactory, storageFile, storageFile2, null);
    }
    
    public static boolean copyFile(final WritableStorageFactory writableStorageFactory, final StorageFile storageFile, final StorageFile storageFile2, byte[] b) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = storageFile.getInputStream();
            outputStream = storageFile2.getOutputStream();
            if (b == null) {
                b = new byte[16384];
            }
            for (int i = inputStream.read(b); i != -1; i = inputStream.read(b)) {
                outputStream.write(b, 0, i);
            }
            inputStream.close();
            inputStream = null;
            writableStorageFactory.sync(outputStream, false);
            outputStream.close();
            outputStream = null;
        }
        catch (IOException ex) {
            return false;
        }
        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                }
                catch (IOException ex2) {}
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                }
                catch (IOException ex3) {}
            }
        }
        return true;
    }
    
    public static File getAbsoluteFile(final File parent, final String s) {
        final File file = new File(s);
        if (file.isAbsolute()) {
            return file;
        }
        if (parent == null) {
            return null;
        }
        return new File(parent, s);
    }
    
    public static File newFile(final File parent, final String s) {
        if (parent == null) {
            return new File(s);
        }
        return new File(parent, s);
    }
    
    public static String stripProtocolFromFileName(final String spec) {
        String file = spec;
        try {
            file = new URL(spec).getFile();
        }
        catch (MalformedURLException ex) {}
        return file;
    }
    
    public static void limitAccessToOwner(final File file) {
        final String systemProperty = PropertyUtil.getSystemProperty("derby.storage.useDefaultFilePermissions");
        if (systemProperty != null) {
            if (Boolean.valueOf(systemProperty.trim())) {
                return;
            }
        }
        else if (JVMInfo.JDK_ID < 8 || !PropertyUtil.getSystemBoolean("derby.__serverStartedFromCmdLine", false)) {
            return;
        }
        synchronized (FileUtil.region) {
            if (!FileUtil.initialized) {
                FileUtil.initialized = true;
                try {
                    FileUtil.setWrite = FileUtil.fileClz.getMethod("setWritable", Boolean.TYPE, Boolean.TYPE);
                    FileUtil.setRead = FileUtil.fileClz.getMethod("setReadable", Boolean.TYPE, Boolean.TYPE);
                    FileUtil.setExec = FileUtil.fileClz.getMethod("setExecutable", Boolean.TYPE, Boolean.TYPE);
                }
                catch (NoSuchMethodException ex2) {}
                try {
                    FileUtil.filesClz = Class.forName("java.nio.file.Files");
                    FileUtil.pathClz = Class.forName("java.nio.file.Path");
                    FileUtil.pathsClz = Class.forName("java.nio.file.Paths");
                    FileUtil.aclEntryClz = Class.forName("java.nio.file.attribute.AclEntry");
                    FileUtil.aclFileAttributeViewClz = Class.forName("java.nio.file.attribute.AclFileAttributeView");
                    FileUtil.posixFileAttributeViewClz = Class.forName("java.nio.file.attribute.PosixFileAttributeView");
                    FileUtil.userPrincipalClz = Class.forName("java.nio.file.attribute.UserPrincipal");
                    FileUtil.linkOptionArrayClz = Class.forName("[Ljava.nio.file.LinkOption;");
                    FileUtil.linkOptionClz = Class.forName("java.nio.file.LinkOption");
                    FileUtil.stringArrayClz = Class.forName("[Ljava.lang.String;");
                    FileUtil.aclEntryBuilderClz = Class.forName("java.nio.file.attribute.AclEntry$Builder");
                    FileUtil.aclEntryTypeClz = Class.forName("java.nio.file.attribute.AclEntryType");
                    FileUtil.fileStoreClz = Class.forName("java.nio.file.FileStore");
                    FileUtil.aclEntryPermissionClz = Class.forName("java.nio.file.attribute.AclEntryPermission");
                    FileUtil.get = FileUtil.pathsClz.getMethod("get", String.class, FileUtil.stringArrayClz);
                    FileUtil.getFileAttributeView = FileUtil.filesClz.getMethod("getFileAttributeView", FileUtil.pathClz, Class.class, FileUtil.linkOptionArrayClz);
                    FileUtil.supportsFileAttributeView = FileUtil.fileStoreClz.getMethod("supportsFileAttributeView", Class.class);
                    FileUtil.getFileStore = FileUtil.filesClz.getMethod("getFileStore", FileUtil.pathClz);
                    FileUtil.getOwner = FileUtil.filesClz.getMethod("getOwner", FileUtil.pathClz, FileUtil.linkOptionArrayClz);
                    FileUtil.getAcl = FileUtil.aclFileAttributeViewClz.getMethod("getAcl", (Class[])new Class[0]);
                    FileUtil.setAcl = FileUtil.aclFileAttributeViewClz.getMethod("setAcl", List.class);
                    FileUtil.principal = FileUtil.aclEntryClz.getMethod("principal", (Class[])new Class[0]);
                    FileUtil.getName = FileUtil.userPrincipalClz.getMethod("getName", (Class[])new Class[0]);
                    FileUtil.build = FileUtil.aclEntryBuilderClz.getMethod("build", (Class[])new Class[0]);
                    FileUtil.newBuilder = FileUtil.aclEntryClz.getMethod("newBuilder", (Class[])new Class[0]);
                    FileUtil.setPrincipal = FileUtil.aclEntryBuilderClz.getMethod("setPrincipal", FileUtil.userPrincipalClz);
                    FileUtil.setType = FileUtil.aclEntryBuilderClz.getMethod("setType", FileUtil.aclEntryTypeClz);
                    FileUtil.values = FileUtil.aclEntryPermissionClz.getMethod("values", (Class[])null);
                    FileUtil.setPermissions = FileUtil.aclEntryBuilderClz.getMethod("setPermissions", Set.class);
                    FileUtil.allow = FileUtil.aclEntryTypeClz.getField("ALLOW");
                }
                catch (NoSuchMethodException ex3) {}
                catch (ClassNotFoundException ex4) {}
                catch (NoSuchFieldException ex5) {}
            }
        }
        if (FileUtil.setWrite == null) {
            return;
        }
        if (limitAccessToOwnerViaACLs(file)) {
            return;
        }
        try {
            assertTrue(FileUtil.setWrite.invoke(file, Boolean.FALSE, Boolean.FALSE));
            assertTrue(FileUtil.setWrite.invoke(file, Boolean.TRUE, Boolean.TRUE));
            assertTrue(FileUtil.setRead.invoke(file, Boolean.FALSE, Boolean.FALSE));
            assertTrue(FileUtil.setRead.invoke(file, Boolean.TRUE, Boolean.TRUE));
            if (file.isDirectory()) {
                assertTrue(FileUtil.setExec.invoke(file, Boolean.FALSE, Boolean.FALSE));
                assertTrue(FileUtil.setExec.invoke(file, Boolean.TRUE, Boolean.TRUE));
            }
        }
        catch (InvocationTargetException ex) {
            throw (SecurityException)ex.getCause();
        }
        catch (IllegalAccessException ex6) {}
    }
    
    private static void assertTrue(final Object o) {
    }
    
    private static boolean limitAccessToOwnerViaACLs(final File file) {
        if (FileUtil.filesClz == null) {
            return false;
        }
        try {
            final Object invoke = FileUtil.get.invoke(null, file.getPath(), new String[0]);
            if (!(boolean)FileUtil.supportsFileAttributeView.invoke(FileUtil.getFileStore.invoke(null, invoke), FileUtil.aclFileAttributeViewClz)) {
                return false;
            }
            final Object invoke2 = FileUtil.getFileAttributeView.invoke(null, invoke, FileUtil.aclFileAttributeViewClz, Array.newInstance(FileUtil.linkOptionClz, 0));
            if (invoke2 == null) {
                return false;
            }
            if (FileUtil.getFileAttributeView.invoke(null, invoke, FileUtil.posixFileAttributeViewClz, Array.newInstance(FileUtil.linkOptionClz, 0)) != null) {
                return false;
            }
            final Object invoke3 = FileUtil.getOwner.invoke(null, invoke, Array.newInstance(FileUtil.linkOptionClz, 0));
            final ArrayList<Object> list = new ArrayList<Object>();
            list.add(FileUtil.build.invoke(FileUtil.setPermissions.invoke(FileUtil.setType.invoke(FileUtil.setPrincipal.invoke(FileUtil.newBuilder.invoke(null, (Object[])null), invoke3), FileUtil.allow.get(FileUtil.aclEntryTypeClz)), new HashSet(Arrays.asList((Object[])FileUtil.values.invoke(null, (Object[])null)))), (Object[])null));
            FileUtil.setAcl.invoke(invoke2, list);
        }
        catch (IllegalAccessException ex2) {}
        catch (IllegalArgumentException ex3) {}
        catch (InvocationTargetException ex) {
            throw (RuntimeException)ex.getCause();
        }
        return true;
    }
    
    static {
        region = new Object();
        FileUtil.initialized = false;
        FileUtil.setWrite = null;
        FileUtil.setRead = null;
        FileUtil.setExec = null;
        FileUtil.fileClz = File.class;
    }
}
