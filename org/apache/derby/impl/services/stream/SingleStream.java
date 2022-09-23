// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.stream;

import java.security.AccessController;
import org.apache.derby.iapi.services.i18n.MessageService;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import org.apache.derby.iapi.services.io.FileUtil;
import java.io.FileOutputStream;
import org.apache.derby.iapi.services.monitor.Monitor;
import java.io.File;
import org.apache.derby.iapi.services.property.PropertyUtil;
import java.util.Properties;
import org.apache.derby.iapi.services.stream.PrintWriterGetHeader;
import org.apache.derby.iapi.services.stream.HeaderPrintWriter;
import java.security.PrivilegedAction;
import org.apache.derby.iapi.services.monitor.ModuleControl;
import org.apache.derby.iapi.services.stream.InfoStreams;

public final class SingleStream implements InfoStreams, ModuleControl, PrivilegedAction
{
    private HeaderPrintWriter theStream;
    private String PBfileName;
    private PrintWriterGetHeader PBheader;
    
    public void boot(final boolean b, final Properties properties) {
        this.theStream = this.makeStream();
    }
    
    public void stop() {
        ((BasicHeaderPrintWriter)this.theStream).complete();
    }
    
    public HeaderPrintWriter stream() {
        return this.theStream;
    }
    
    private HeaderPrintWriter makeStream() {
        final PrintWriterGetHeader header = this.makeHeader();
        HeaderPrintWriter headerPrintWriter = this.makeHPW(header);
        if (headerPrintWriter == null) {
            headerPrintWriter = this.createDefaultStream(header);
        }
        return headerPrintWriter;
    }
    
    private PrintWriterGetHeader makeHeader() {
        return new BasicGetLogHeader(true, true, null);
    }
    
    private HeaderPrintWriter makeHPW(final PrintWriterGetHeader printWriterGetHeader) {
        final String systemProperty = PropertyUtil.getSystemProperty("derby.stream.error.file");
        if (systemProperty != null) {
            return this.makeFileHPW(systemProperty, printWriterGetHeader);
        }
        final String systemProperty2 = PropertyUtil.getSystemProperty("derby.stream.error.method");
        if (systemProperty2 != null) {
            return this.makeMethodHPW(systemProperty2, printWriterGetHeader);
        }
        final String systemProperty3 = PropertyUtil.getSystemProperty("derby.stream.error.field");
        if (systemProperty3 != null) {
            return this.makeFieldHPW(systemProperty3, printWriterGetHeader);
        }
        return null;
    }
    
    private HeaderPrintWriter PBmakeFileHPW(final String s, final PrintWriterGetHeader printWriterGetHeader) {
        final boolean systemBoolean = PropertyUtil.getSystemBoolean("derby.infolog.append");
        File file = new File(s);
        if (!file.isAbsolute()) {
            final Object environment = Monitor.getMonitor().getEnvironment();
            if (environment instanceof File) {
                file = new File((File)environment, s);
            }
        }
        FileOutputStream out;
        try {
            if (file.exists() && systemBoolean) {
                out = new FileOutputStream(file.getPath(), true);
            }
            else {
                out = new FileOutputStream(file);
            }
            FileUtil.limitAccessToOwner(file);
        }
        catch (IOException ex) {
            return this.useDefaultStream(printWriterGetHeader, ex);
        }
        catch (SecurityException ex2) {
            return this.useDefaultStream(printWriterGetHeader, ex2);
        }
        return new BasicHeaderPrintWriter(new BufferedOutputStream(out), printWriterGetHeader, true, file.getPath());
    }
    
    private HeaderPrintWriter makeMethodHPW(final String s, final PrintWriterGetHeader printWriterGetHeader) {
        final int lastIndex = s.lastIndexOf(46);
        final String substring = s.substring(0, lastIndex);
        final String substring2 = s.substring(lastIndex + 1);
        Throwable targetException;
        try {
            final Class<?> forName = Class.forName(substring);
            try {
                final Method method = forName.getMethod(substring2, (Class[])new Class[0]);
                if (!Modifier.isStatic(method.getModifiers())) {
                    final HeaderPrintWriter useDefaultStream = this.useDefaultStream(printWriterGetHeader);
                    useDefaultStream.printlnWithHeader(method.toString() + " is not static");
                    return useDefaultStream;
                }
                try {
                    return this.makeValueHPW(method, method.invoke(null, new Object[0]), printWriterGetHeader, s);
                }
                catch (IllegalAccessException ex) {
                    targetException = ex;
                }
                catch (IllegalArgumentException ex2) {
                    targetException = ex2;
                }
                catch (InvocationTargetException ex3) {
                    targetException = ex3.getTargetException();
                }
            }
            catch (NoSuchMethodException ex4) {
                targetException = ex4;
            }
        }
        catch (ClassNotFoundException ex5) {
            targetException = ex5;
        }
        catch (SecurityException ex6) {
            targetException = ex6;
        }
        return this.useDefaultStream(printWriterGetHeader, targetException);
    }
    
    private HeaderPrintWriter makeFieldHPW(final String s, final PrintWriterGetHeader printWriterGetHeader) {
        final int lastIndex = s.lastIndexOf(46);
        final String substring = s.substring(0, lastIndex);
        final String substring2 = s.substring(lastIndex + 1, s.length());
        IllegalAccessException ex;
        try {
            final Class<?> forName = Class.forName(substring);
            try {
                final Field field = forName.getField(substring2);
                if (!Modifier.isStatic(field.getModifiers())) {
                    final HeaderPrintWriter useDefaultStream = this.useDefaultStream(printWriterGetHeader);
                    useDefaultStream.printlnWithHeader(field.toString() + " is not static");
                    return useDefaultStream;
                }
                try {
                    return this.makeValueHPW(field, field.get(null), printWriterGetHeader, s);
                }
                catch (IllegalAccessException ex2) {
                    ex = ex2;
                }
                catch (IllegalArgumentException ex3) {
                    ex = (IllegalAccessException)ex3;
                }
            }
            catch (NoSuchFieldException ex4) {
                ex = (IllegalAccessException)ex4;
            }
        }
        catch (ClassNotFoundException ex5) {
            ex = (IllegalAccessException)ex5;
        }
        catch (SecurityException ex6) {
            ex = (IllegalAccessException)ex6;
        }
        return this.useDefaultStream(printWriterGetHeader, ex);
    }
    
    private HeaderPrintWriter makeValueHPW(final Member member, final Object o, final PrintWriterGetHeader printWriterGetHeader, final String s) {
        if (o instanceof OutputStream) {
            return new BasicHeaderPrintWriter((OutputStream)o, printWriterGetHeader, false, s);
        }
        if (o instanceof Writer) {
            return new BasicHeaderPrintWriter((Writer)o, printWriterGetHeader, false, s);
        }
        final HeaderPrintWriter useDefaultStream = this.useDefaultStream(printWriterGetHeader);
        if (o == null) {
            useDefaultStream.printlnWithHeader(member.toString() + "=null");
        }
        else {
            useDefaultStream.printlnWithHeader(member.toString() + " instanceof " + o.getClass().getName());
        }
        return useDefaultStream;
    }
    
    private HeaderPrintWriter createDefaultStream(final PrintWriterGetHeader printWriterGetHeader) {
        return this.makeFileHPW("derby.log", printWriterGetHeader);
    }
    
    private HeaderPrintWriter useDefaultStream(final PrintWriterGetHeader printWriterGetHeader) {
        return new BasicHeaderPrintWriter(System.err, printWriterGetHeader, false, "System.err");
    }
    
    private HeaderPrintWriter useDefaultStream(final PrintWriterGetHeader printWriterGetHeader, Throwable t) {
        final HeaderPrintWriter useDefaultStream = this.useDefaultStream(printWriterGetHeader);
        while (t != null) {
            final Throwable cause = t.getCause();
            final String textMessage = MessageService.getTextMessage("N001");
            useDefaultStream.printlnWithHeader(t.toString() + ((cause != null) ? (" " + textMessage) : ""));
            t = cause;
        }
        return useDefaultStream;
    }
    
    private HeaderPrintWriter makeFileHPW(final String pBfileName, final PrintWriterGetHeader pBheader) {
        this.PBfileName = pBfileName;
        this.PBheader = pBheader;
        return AccessController.doPrivileged((PrivilegedAction<HeaderPrintWriter>)this);
    }
    
    public final Object run() {
        return this.PBmakeFileHPW(this.PBfileName, this.PBheader);
    }
}
