// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.compress;

import org.slf4j.LoggerFactory;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.ReflectionUtils;
import java.util.TreeMap;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import java.util.Iterator;
import org.apache.hadoop.util.StringUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.ServiceLoader;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class CompressionCodecFactory
{
    public static final Logger LOG;
    private static final ServiceLoader<CompressionCodec> CODEC_PROVIDERS;
    private SortedMap<String, CompressionCodec> codecs;
    private Map<String, CompressionCodec> codecsByName;
    private HashMap<String, CompressionCodec> codecsByClassName;
    
    private void addCodec(final CompressionCodec codec) {
        final String suffix = codec.getDefaultExtension();
        this.codecs.put(new StringBuilder(suffix).reverse().toString(), codec);
        this.codecsByClassName.put(codec.getClass().getCanonicalName(), codec);
        String codecName = codec.getClass().getSimpleName();
        this.codecsByName.put(StringUtils.toLowerCase(codecName), codec);
        if (codecName.endsWith("Codec")) {
            codecName = codecName.substring(0, codecName.length() - "Codec".length());
            this.codecsByName.put(StringUtils.toLowerCase(codecName), codec);
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        final Iterator<Map.Entry<String, CompressionCodec>> itr = this.codecs.entrySet().iterator();
        buf.append("{ ");
        if (itr.hasNext()) {
            Map.Entry<String, CompressionCodec> entry = itr.next();
            buf.append(entry.getKey());
            buf.append(": ");
            buf.append(entry.getValue().getClass().getName());
            while (itr.hasNext()) {
                entry = itr.next();
                buf.append(", ");
                buf.append(entry.getKey());
                buf.append(": ");
                buf.append(entry.getValue().getClass().getName());
            }
        }
        buf.append(" }");
        return buf.toString();
    }
    
    public static List<Class<? extends CompressionCodec>> getCodecClasses(final Configuration conf) {
        final List<Class<? extends CompressionCodec>> result = new ArrayList<Class<? extends CompressionCodec>>();
        synchronized (CompressionCodecFactory.CODEC_PROVIDERS) {
            for (final CompressionCodec codec : CompressionCodecFactory.CODEC_PROVIDERS) {
                result.add(codec.getClass());
            }
        }
        final String codecsString = conf.get("io.compression.codecs");
        if (codecsString != null) {
            final StringTokenizer codecSplit = new StringTokenizer(codecsString, ",");
            while (codecSplit.hasMoreElements()) {
                final String codecSubstring = codecSplit.nextToken().trim();
                if (codecSubstring.length() != 0) {
                    try {
                        final Class<?> cls = conf.getClassByName(codecSubstring);
                        if (!CompressionCodec.class.isAssignableFrom(cls)) {
                            throw new IllegalArgumentException("Class " + codecSubstring + " is not a CompressionCodec");
                        }
                        result.add(cls.asSubclass(CompressionCodec.class));
                    }
                    catch (ClassNotFoundException ex) {
                        throw new IllegalArgumentException("Compression codec " + codecSubstring + " not found.", ex);
                    }
                }
            }
        }
        return result;
    }
    
    public static void setCodecClasses(final Configuration conf, final List<Class> classes) {
        final StringBuilder buf = new StringBuilder();
        final Iterator<Class> itr = (Iterator<Class>)classes.iterator();
        if (itr.hasNext()) {
            final Class cls = itr.next();
            buf.append(cls.getName());
            while (itr.hasNext()) {
                buf.append(',');
                buf.append(itr.next().getName());
            }
        }
        conf.set("io.compression.codecs", buf.toString());
    }
    
    public CompressionCodecFactory(final Configuration conf) {
        this.codecs = null;
        this.codecsByName = null;
        this.codecsByClassName = null;
        this.codecs = new TreeMap<String, CompressionCodec>();
        this.codecsByClassName = new HashMap<String, CompressionCodec>();
        this.codecsByName = new HashMap<String, CompressionCodec>();
        final List<Class<? extends CompressionCodec>> codecClasses = getCodecClasses(conf);
        if (codecClasses == null || codecClasses.isEmpty()) {
            this.addCodec(new GzipCodec());
            this.addCodec(new DefaultCodec());
        }
        else {
            for (final Class<? extends CompressionCodec> codecClass : codecClasses) {
                this.addCodec(ReflectionUtils.newInstance(codecClass, conf));
            }
        }
    }
    
    public CompressionCodec getCodec(final Path file) {
        CompressionCodec result = null;
        if (this.codecs != null) {
            final String filename = file.getName();
            final String reversedFilename = new StringBuilder(filename).reverse().toString();
            final SortedMap<String, CompressionCodec> subMap = this.codecs.headMap(reversedFilename);
            if (!subMap.isEmpty()) {
                final String potentialSuffix = subMap.lastKey();
                if (reversedFilename.startsWith(potentialSuffix)) {
                    result = this.codecs.get(potentialSuffix);
                }
            }
        }
        return result;
    }
    
    public CompressionCodec getCodecByClassName(final String classname) {
        if (this.codecsByClassName == null) {
            return null;
        }
        return this.codecsByClassName.get(classname);
    }
    
    public CompressionCodec getCodecByName(final String codecName) {
        if (this.codecsByClassName == null) {
            return null;
        }
        CompressionCodec codec = this.getCodecByClassName(codecName);
        if (codec == null) {
            codec = this.codecsByName.get(StringUtils.toLowerCase(codecName));
        }
        return codec;
    }
    
    public Class<? extends CompressionCodec> getCodecClassByName(final String codecName) {
        final CompressionCodec codec = this.getCodecByName(codecName);
        if (codec == null) {
            return null;
        }
        return codec.getClass();
    }
    
    public static String removeSuffix(final String filename, final String suffix) {
        if (filename.endsWith(suffix)) {
            return filename.substring(0, filename.length() - suffix.length());
        }
        return filename;
    }
    
    public static void main(final String[] args) throws Exception {
        final Configuration conf = new Configuration();
        final CompressionCodecFactory factory = new CompressionCodecFactory(conf);
        boolean encode = false;
        for (int i = 0; i < args.length; ++i) {
            if ("-in".equals(args[i])) {
                encode = true;
            }
            else if ("-out".equals(args[i])) {
                encode = false;
            }
            else {
                final CompressionCodec codec = factory.getCodec(new Path(args[i]));
                if (codec == null) {
                    System.out.println("Codec for " + args[i] + " not found.");
                }
                else if (encode) {
                    CompressionOutputStream out = null;
                    InputStream in = null;
                    try {
                        out = codec.createOutputStream(new FileOutputStream(args[i]));
                        final byte[] buffer = new byte[100];
                        final String inFilename = removeSuffix(args[i], codec.getDefaultExtension());
                        in = new FileInputStream(inFilename);
                        for (int len = in.read(buffer); len > 0; len = in.read(buffer)) {
                            out.write(buffer, 0, len);
                        }
                    }
                    finally {
                        if (out != null) {
                            out.close();
                        }
                        if (in != null) {
                            in.close();
                        }
                    }
                }
                else {
                    CompressionInputStream in2 = null;
                    try {
                        in2 = codec.createInputStream(new FileInputStream(args[i]));
                        final byte[] buffer2 = new byte[100];
                        for (int len2 = in2.read(buffer2); len2 > 0; len2 = in2.read(buffer2)) {
                            System.out.write(buffer2, 0, len2);
                        }
                    }
                    finally {
                        if (in2 != null) {
                            in2.close();
                        }
                    }
                }
            }
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(CompressionCodecFactory.class.getName());
        CODEC_PROVIDERS = ServiceLoader.load(CompressionCodec.class);
    }
}
