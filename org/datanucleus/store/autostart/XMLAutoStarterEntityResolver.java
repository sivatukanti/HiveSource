// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.autostart;

import org.datanucleus.util.AbstractXMLEntityResolver;

public class XMLAutoStarterEntityResolver extends AbstractXMLEntityResolver
{
    public static final String PUBLIC_ID_KEY = "-//DataNucleus//DTD DataNucleus AutoStarter Metadata 1.0//EN";
    
    public XMLAutoStarterEntityResolver() {
        this.publicIdEntities.put("-//DataNucleus//DTD DataNucleus AutoStarter Metadata 1.0//EN", "/org/datanucleus/datanucleus_autostart_1_0.dtd");
        this.systemIdEntities.put("-//DataNucleus//DTD DataNucleus AutoStarter Metadata 1.0//EN", "/org/datanucleus/datanucleus_autostart_1_0.dtd");
        this.systemIdEntities.put("file:/org/datanucleus/datanucleus_autostart_1_0.dtd", "/org/datanucleus/datanucleus_autostart_1_0.dtd");
    }
}
