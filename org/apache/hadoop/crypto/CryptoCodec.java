// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.crypto;

import org.slf4j.LoggerFactory;
import java.security.GeneralSecurityException;
import com.google.common.base.Splitter;
import org.apache.hadoop.fs.CommonConfigurationKeysPublic;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import org.apache.hadoop.util.PerformanceAdvisory;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.Closeable;
import org.apache.hadoop.conf.Configurable;

@InterfaceAudience.Private
@InterfaceStability.Evolving
public abstract class CryptoCodec implements Configurable, Closeable
{
    public static Logger LOG;
    
    public static CryptoCodec getInstance(final Configuration conf, final CipherSuite cipherSuite) {
        final List<Class<? extends CryptoCodec>> klasses = getCodecClasses(conf, cipherSuite);
        if (klasses == null) {
            return null;
        }
        CryptoCodec codec = null;
        for (final Class<? extends CryptoCodec> klass : klasses) {
            try {
                final CryptoCodec c = ReflectionUtils.newInstance(klass, conf);
                if (c.getCipherSuite().getName().equals(cipherSuite.getName())) {
                    if (codec != null) {
                        continue;
                    }
                    PerformanceAdvisory.LOG.debug("Using crypto codec {}.", klass.getName());
                    codec = c;
                }
                else {
                    PerformanceAdvisory.LOG.debug("Crypto codec {} doesn't meet the cipher suite {}.", klass.getName(), cipherSuite.getName());
                }
            }
            catch (Exception e) {
                PerformanceAdvisory.LOG.debug("Crypto codec {} is not available.", klass.getName());
            }
        }
        return codec;
    }
    
    public static CryptoCodec getInstance(final Configuration conf) {
        final String name = conf.get("hadoop.security.crypto.cipher.suite", "AES/CTR/NoPadding");
        return getInstance(conf, CipherSuite.convert(name));
    }
    
    private static List<Class<? extends CryptoCodec>> getCodecClasses(final Configuration conf, final CipherSuite cipherSuite) {
        final List<Class<? extends CryptoCodec>> result = (List<Class<? extends CryptoCodec>>)Lists.newArrayList();
        final String configName = "hadoop.security.crypto.codec.classes" + cipherSuite.getConfigSuffix();
        String codecString;
        if (configName.equals(CommonConfigurationKeysPublic.HADOOP_SECURITY_CRYPTO_CODEC_CLASSES_AES_CTR_NOPADDING_KEY)) {
            codecString = conf.get(configName, CommonConfigurationKeysPublic.HADOOP_SECURITY_CRYPTO_CODEC_CLASSES_AES_CTR_NOPADDING_DEFAULT);
        }
        else {
            codecString = conf.get(configName);
        }
        if (codecString == null) {
            PerformanceAdvisory.LOG.debug("No crypto codec classes with cipher suite configured.");
            return null;
        }
        for (final String c : Splitter.on(',').trimResults().omitEmptyStrings().split(codecString)) {
            try {
                final Class<?> cls = conf.getClassByName(c);
                result.add(cls.asSubclass(CryptoCodec.class));
            }
            catch (ClassCastException e) {
                PerformanceAdvisory.LOG.debug("Class {} is not a CryptoCodec.", c);
            }
            catch (ClassNotFoundException e2) {
                PerformanceAdvisory.LOG.debug("Crypto codec {} not found.", c);
            }
        }
        return result;
    }
    
    public abstract CipherSuite getCipherSuite();
    
    public abstract Encryptor createEncryptor() throws GeneralSecurityException;
    
    public abstract Decryptor createDecryptor() throws GeneralSecurityException;
    
    public abstract void calculateIV(final byte[] p0, final long p1, final byte[] p2);
    
    public abstract void generateSecureRandom(final byte[] p0);
    
    static {
        CryptoCodec.LOG = LoggerFactory.getLogger(CryptoCodec.class);
    }
}
