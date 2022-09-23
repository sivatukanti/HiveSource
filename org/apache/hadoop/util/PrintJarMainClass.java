// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import java.util.jar.Manifest;
import java.util.jar.JarFile;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class PrintJarMainClass
{
    public static void main(final String[] args) {
        try (final JarFile jar_file = new JarFile(args[0])) {
            final Manifest manifest = jar_file.getManifest();
            if (manifest != null) {
                final String value = manifest.getMainAttributes().getValue("Main-Class");
                if (value != null) {
                    System.out.println(value.replaceAll("/", "."));
                    return;
                }
            }
        }
        catch (Throwable t3) {}
        System.out.println("UNKNOWN");
        System.exit(1);
    }
}
