// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.net.ftp.parser;

import org.apache.commons.net.ftp.Configurable;
import java.util.Locale;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFileEntryParser;
import java.util.regex.Pattern;

public class DefaultFTPFileEntryParserFactory implements FTPFileEntryParserFactory
{
    private static final String JAVA_IDENTIFIER = "\\p{javaJavaIdentifierStart}(\\p{javaJavaIdentifierPart})*";
    private static final String JAVA_QUALIFIED_NAME = "(\\p{javaJavaIdentifierStart}(\\p{javaJavaIdentifierPart})*\\.)+\\p{javaJavaIdentifierStart}(\\p{javaJavaIdentifierPart})*";
    private static final Pattern JAVA_QUALIFIED_NAME_PATTERN;
    
    @Override
    public FTPFileEntryParser createFileEntryParser(final String key) {
        if (key == null) {
            throw new ParserInitializationException("Parser key cannot be null");
        }
        return this.createFileEntryParser(key, null);
    }
    
    private FTPFileEntryParser createFileEntryParser(final String key, final FTPClientConfig config) {
        FTPFileEntryParser parser = null;
        if (DefaultFTPFileEntryParserFactory.JAVA_QUALIFIED_NAME_PATTERN.matcher(key).matches()) {
            try {
                final Class<?> parserClass = Class.forName(key);
                try {
                    parser = (FTPFileEntryParser)parserClass.newInstance();
                }
                catch (ClassCastException e) {
                    throw new ParserInitializationException(parserClass.getName() + " does not implement the interface " + "org.apache.commons.net.ftp.FTPFileEntryParser.", e);
                }
                catch (Exception e2) {
                    throw new ParserInitializationException("Error initializing parser", e2);
                }
                catch (ExceptionInInitializerError e3) {
                    throw new ParserInitializationException("Error initializing parser", e3);
                }
            }
            catch (ClassNotFoundException ex) {}
        }
        if (parser == null) {
            final String ukey = key.toUpperCase(Locale.ENGLISH);
            if (ukey.indexOf("UNIX_LTRIM") >= 0) {
                parser = new UnixFTPEntryParser(config, true);
            }
            else if (ukey.indexOf("UNIX") >= 0) {
                parser = new UnixFTPEntryParser(config, false);
            }
            else if (ukey.indexOf("VMS") >= 0) {
                parser = new VMSVersioningFTPEntryParser(config);
            }
            else if (ukey.indexOf("WINDOWS") >= 0) {
                parser = this.createNTFTPEntryParser(config);
            }
            else if (ukey.indexOf("OS/2") >= 0) {
                parser = new OS2FTPEntryParser(config);
            }
            else if (ukey.indexOf("OS/400") >= 0 || ukey.indexOf("AS/400") >= 0) {
                parser = this.createOS400FTPEntryParser(config);
            }
            else if (ukey.indexOf("MVS") >= 0) {
                parser = new MVSFTPEntryParser();
            }
            else if (ukey.indexOf("NETWARE") >= 0) {
                parser = new NetwareFTPEntryParser(config);
            }
            else if (ukey.indexOf("MACOS PETER") >= 0) {
                parser = new MacOsPeterFTPEntryParser(config);
            }
            else {
                if (ukey.indexOf("TYPE: L8") < 0) {
                    throw new ParserInitializationException("Unknown parser type: " + key);
                }
                parser = new UnixFTPEntryParser(config);
            }
        }
        if (parser instanceof Configurable) {
            ((Configurable)parser).configure(config);
        }
        return parser;
    }
    
    @Override
    public FTPFileEntryParser createFileEntryParser(final FTPClientConfig config) throws ParserInitializationException {
        final String key = config.getServerSystemKey();
        return this.createFileEntryParser(key, config);
    }
    
    public FTPFileEntryParser createUnixFTPEntryParser() {
        return new UnixFTPEntryParser();
    }
    
    public FTPFileEntryParser createVMSVersioningFTPEntryParser() {
        return new VMSVersioningFTPEntryParser();
    }
    
    public FTPFileEntryParser createNetwareFTPEntryParser() {
        return new NetwareFTPEntryParser();
    }
    
    public FTPFileEntryParser createNTFTPEntryParser() {
        return this.createNTFTPEntryParser(null);
    }
    
    private FTPFileEntryParser createNTFTPEntryParser(final FTPClientConfig config) {
        if (config != null && "WINDOWS".equals(config.getServerSystemKey())) {
            return new NTFTPEntryParser(config);
        }
        final FTPClientConfig config2 = (config != null) ? new FTPClientConfig(config) : null;
        return new CompositeFileEntryParser(new FTPFileEntryParser[] { new NTFTPEntryParser(config), new UnixFTPEntryParser(config2, config2 != null && "UNIX_LTRIM".equals(config2.getServerSystemKey())) });
    }
    
    public FTPFileEntryParser createOS2FTPEntryParser() {
        return new OS2FTPEntryParser();
    }
    
    public FTPFileEntryParser createOS400FTPEntryParser() {
        return this.createOS400FTPEntryParser(null);
    }
    
    private FTPFileEntryParser createOS400FTPEntryParser(final FTPClientConfig config) {
        if (config != null && "OS/400".equals(config.getServerSystemKey())) {
            return new OS400FTPEntryParser(config);
        }
        final FTPClientConfig config2 = (config != null) ? new FTPClientConfig(config) : null;
        return new CompositeFileEntryParser(new FTPFileEntryParser[] { new OS400FTPEntryParser(config), new UnixFTPEntryParser(config2, config2 != null && "UNIX_LTRIM".equals(config2.getServerSystemKey())) });
    }
    
    public FTPFileEntryParser createMVSEntryParser() {
        return new MVSFTPEntryParser();
    }
    
    static {
        JAVA_QUALIFIED_NAME_PATTERN = Pattern.compile("(\\p{javaJavaIdentifierStart}(\\p{javaJavaIdentifierPart})*\\.)+\\p{javaJavaIdentifierStart}(\\p{javaJavaIdentifierPart})*");
    }
}
