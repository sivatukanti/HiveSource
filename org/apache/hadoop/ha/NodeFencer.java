// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ha;

import com.google.common.collect.ImmutableMap;
import org.slf4j.LoggerFactory;
import org.apache.hadoop.util.ReflectionUtils;
import java.util.regex.Matcher;
import com.google.common.collect.Lists;
import java.util.Iterator;
import org.apache.hadoop.conf.Configuration;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import java.util.regex.Pattern;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Evolving
public class NodeFencer
{
    private static final String CLASS_RE = "([a-zA-Z0-9\\.\\$]+)";
    private static final Pattern CLASS_WITH_ARGUMENT;
    private static final Pattern CLASS_WITHOUT_ARGUMENT;
    private static final Pattern HASH_COMMENT_RE;
    private static final Logger LOG;
    private static final Map<String, Class<? extends FenceMethod>> STANDARD_METHODS;
    private final List<FenceMethodWithArg> methods;
    
    NodeFencer(final Configuration conf, final String spec) throws BadFencingConfigurationException {
        this.methods = parseMethods(conf, spec);
    }
    
    public static NodeFencer create(final Configuration conf, final String confKey) throws BadFencingConfigurationException {
        final String confStr = conf.get(confKey);
        if (confStr == null) {
            return null;
        }
        return new NodeFencer(conf, confStr);
    }
    
    public boolean fence(final HAServiceTarget fromSvc) {
        NodeFencer.LOG.info("====== Beginning Service Fencing Process... ======");
        int i = 0;
        for (final FenceMethodWithArg method : this.methods) {
            NodeFencer.LOG.info("Trying method " + ++i + "/" + this.methods.size() + ": " + method);
            try {
                if (method.method.tryFence(fromSvc, method.arg)) {
                    NodeFencer.LOG.info("====== Fencing successful by method " + method + " ======");
                    return true;
                }
            }
            catch (BadFencingConfigurationException e) {
                NodeFencer.LOG.error("Fencing method " + method + " misconfigured", e);
                continue;
            }
            catch (Throwable t) {
                NodeFencer.LOG.error("Fencing method " + method + " failed with an unexpected error.", t);
                continue;
            }
            NodeFencer.LOG.warn("Fencing method " + method + " was unsuccessful.");
        }
        NodeFencer.LOG.error("Unable to fence service by any configured method.");
        return false;
    }
    
    private static List<FenceMethodWithArg> parseMethods(final Configuration conf, final String spec) throws BadFencingConfigurationException {
        final String[] lines = spec.split("\\s*\n\\s*");
        final List<FenceMethodWithArg> methods = (List<FenceMethodWithArg>)Lists.newArrayList();
        for (String line : lines) {
            line = NodeFencer.HASH_COMMENT_RE.matcher(line).replaceAll("");
            line = line.trim();
            if (!line.isEmpty()) {
                methods.add(parseMethod(conf, line));
            }
        }
        return methods;
    }
    
    private static FenceMethodWithArg parseMethod(final Configuration conf, final String line) throws BadFencingConfigurationException {
        Matcher m;
        if ((m = NodeFencer.CLASS_WITH_ARGUMENT.matcher(line)).matches()) {
            final String className = m.group(1);
            final String arg = m.group(2);
            return createFenceMethod(conf, className, arg);
        }
        if ((m = NodeFencer.CLASS_WITHOUT_ARGUMENT.matcher(line)).matches()) {
            final String className = m.group(1);
            return createFenceMethod(conf, className, null);
        }
        throw new BadFencingConfigurationException("Unable to parse line: '" + line + "'");
    }
    
    private static FenceMethodWithArg createFenceMethod(final Configuration conf, final String clazzName, final String arg) throws BadFencingConfigurationException {
        Class<?> clazz;
        try {
            clazz = NodeFencer.STANDARD_METHODS.get(clazzName);
            if (clazz == null) {
                clazz = Class.forName(clazzName);
            }
        }
        catch (Exception e) {
            throw new BadFencingConfigurationException("Could not find configured fencing method " + clazzName, e);
        }
        if (!FenceMethod.class.isAssignableFrom(clazz)) {
            throw new BadFencingConfigurationException("Class " + clazzName + " does not implement FenceMethod");
        }
        final FenceMethod method = ReflectionUtils.newInstance(clazz, conf);
        method.checkArgs(arg);
        return new FenceMethodWithArg(method, arg);
    }
    
    static {
        CLASS_WITH_ARGUMENT = Pattern.compile("([a-zA-Z0-9\\.\\$]+)\\((.+?)\\)");
        CLASS_WITHOUT_ARGUMENT = Pattern.compile("([a-zA-Z0-9\\.\\$]+)");
        HASH_COMMENT_RE = Pattern.compile("#.*$");
        LOG = LoggerFactory.getLogger(NodeFencer.class);
        STANDARD_METHODS = ImmutableMap.of("shell", ShellCommandFencer.class, "sshfence", (Class<ShellCommandFencer>)SshFenceByTcpPort.class, "powershell", (Class<ShellCommandFencer>)PowerShellFencer.class);
    }
    
    private static class FenceMethodWithArg
    {
        private final FenceMethod method;
        private final String arg;
        
        private FenceMethodWithArg(final FenceMethod method, final String arg) {
            this.method = method;
            this.arg = arg;
        }
        
        @Override
        public String toString() {
            return this.method.getClass().getCanonicalName() + "(" + this.arg + ")";
        }
    }
}
