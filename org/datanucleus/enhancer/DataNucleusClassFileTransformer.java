// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer;

import java.lang.instrument.IllegalClassFormatException;
import org.datanucleus.util.StringUtils;
import java.security.ProtectionDomain;
import java.lang.instrument.Instrumentation;
import java.util.Map;
import org.datanucleus.util.CommandLine;
import java.lang.instrument.ClassFileTransformer;

public class DataNucleusClassFileTransformer implements ClassFileTransformer
{
    protected RuntimeEnhancer enhancer;
    private CommandLine cmd;
    
    public DataNucleusClassFileTransformer() {
        this(null, null);
    }
    
    public DataNucleusClassFileTransformer(final String arguments) {
        this(arguments, null);
    }
    
    public DataNucleusClassFileTransformer(final String arguments, final Map contextProps) {
        (this.cmd = new CommandLine()).addOption("api", "api", "api", "api");
        this.cmd.addOption("generatePK", "generatePK", "<generate-pk>", "Generate PK class where needed?");
        this.cmd.addOption("generateConstructor", "generateConstructor", "<generate-constructor>", "Generate default constructor where needed?");
        this.cmd.addOption("detachListener", "detachListener", "<detach-listener>", "Use Detach Listener?");
        if (arguments != null) {
            this.cmd.parse(arguments.split("[\\s,=]+"));
        }
        this.enhancer = new RuntimeEnhancer((this.cmd.getOptionArg("api") != null) ? this.cmd.getOptionArg("api") : null, contextProps);
        if (this.cmd.hasOption("generateConstructor")) {
            final String val = this.cmd.getOptionArg("generateConstructor");
            if (val.equalsIgnoreCase("false")) {
                this.enhancer.unsetClassEnhancerOption("generate-default-constructor");
            }
        }
        if (this.cmd.hasOption("generatePK")) {
            final String val = this.cmd.getOptionArg("generatePK");
            if (val.equalsIgnoreCase("false")) {
                this.enhancer.unsetClassEnhancerOption("generate-primary-key");
            }
        }
        if (this.cmd.hasOption("detachListener")) {
            final String val = this.cmd.getOptionArg("detachListener");
            if (val.equalsIgnoreCase("true")) {
                this.enhancer.setClassEnhancerOption("generate-detach-listener");
            }
        }
    }
    
    public static void premain(final String agentArguments, final Instrumentation instrumentation) {
        instrumentation.addTransformer(new DataNucleusClassFileTransformer(agentArguments));
    }
    
    @Override
    public byte[] transform(final ClassLoader loader, final String className, final Class classBeingRedefined, final ProtectionDomain protectionDomain, final byte[] classfileBuffer) throws IllegalClassFormatException {
        final String name = StringUtils.replaceAll(className, "/", ".");
        if (name.startsWith("java.")) {
            return null;
        }
        if (name.startsWith("javax.")) {
            return null;
        }
        if (name.startsWith("org.datanucleus.") && !name.startsWith("org.datanucleus.samples") && !name.startsWith("org.datanucleus.test")) {
            return null;
        }
        if (this.cmd.getDefaultArgs() != null && this.cmd.getDefaultArgs().length > 0) {
            final String[] classes = this.cmd.getDefaultArgs();
            for (int i = 0; i < classes.length; ++i) {
                if (name.startsWith(classes[i])) {
                    return this.enhancer.enhance(name, classfileBuffer, loader);
                }
            }
            return null;
        }
        return this.enhancer.enhance(name, classfileBuffer, loader);
    }
}
