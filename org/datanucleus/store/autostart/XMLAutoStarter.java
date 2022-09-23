// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.autostart;

import javax.xml.transform.Transformer;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import java.io.OutputStream;
import javax.xml.transform.stream.StreamResult;
import java.io.FileOutputStream;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.TransformerFactory;
import java.util.Iterator;
import java.util.Map;
import org.datanucleus.store.exceptions.DatastoreInitialisationException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.datanucleus.store.StoreData;
import java.util.Collection;
import java.net.MalformedURLException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Node;
import org.datanucleus.util.NucleusLogger;
import java.io.Reader;
import org.xml.sax.InputSource;
import java.io.InputStreamReader;
import org.xml.sax.EntityResolver;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.HashSet;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.StoreManager;
import java.util.Set;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import java.net.URL;

public class XMLAutoStarter extends AbstractAutoStartMechanism
{
    protected final URL fileUrl;
    protected Document doc;
    protected Element rootElement;
    String version;
    Set<String> autoStartClasses;
    
    public XMLAutoStarter(final StoreManager storeMgr, final ClassLoaderResolver clr) throws MalformedURLException {
        this.version = null;
        this.autoStartClasses = new HashSet<String>();
        this.fileUrl = new URL("file:" + storeMgr.getStringProperty("datanucleus.autoStartMechanismXmlFile"));
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            final DocumentBuilder db = factory.newDocumentBuilder();
            try {
                db.setEntityResolver(new XMLAutoStarterEntityResolver());
                this.rootElement = db.parse(new InputSource(new InputStreamReader(this.fileUrl.openStream()))).getDocumentElement();
                this.doc = this.rootElement.getOwnerDocument();
            }
            catch (Exception e2) {
                NucleusLogger.PERSISTENCE.info(XMLAutoStarter.LOCALISER.msg("034201", this.fileUrl.getFile()));
                this.doc = db.newDocument();
                this.rootElement = this.doc.createElement("datanucleus_autostart");
                this.doc.appendChild(this.rootElement);
                this.writeToFile();
            }
        }
        catch (ParserConfigurationException e1) {
            NucleusLogger.PERSISTENCE.error(XMLAutoStarter.LOCALISER.msg("034202", this.fileUrl.getFile(), e1.getMessage()));
        }
        this.version = storeMgr.getNucleusContext().getPluginManager().getVersionForBundle("org.datanucleus");
    }
    
    @Override
    public Collection getAllClassData() throws DatastoreInitialisationException {
        final Collection classes = new HashSet();
        final NodeList classElements = this.rootElement.getElementsByTagName("class");
        for (int i = 0; i < classElements.getLength(); ++i) {
            final Element element = (Element)classElements.item(i);
            final StoreData data = new StoreData(element.getAttribute("name"), element.getAttribute("type").equals("FCO") ? 1 : 2);
            this.autoStartClasses.add(data.getName());
            final NamedNodeMap attributeMap = element.getAttributes();
            for (int j = 0; j < attributeMap.getLength(); ++j) {
                final Node attr = attributeMap.item(j);
                final String attrName = attr.getNodeName();
                final String attrValue = attr.getNodeValue();
                if (!attrName.equals("name") && !attrName.equals("type")) {
                    data.addProperty(attrName, attrValue);
                }
            }
            classes.add(data);
        }
        return classes;
    }
    
    @Override
    public boolean isOpen() {
        return true;
    }
    
    @Override
    public void close() {
        this.writeToFile();
        super.close();
    }
    
    @Override
    public void addClass(final StoreData data) {
        if (this.autoStartClasses.contains(data.getName())) {
            return;
        }
        final Element classElement = this.doc.createElement("class");
        classElement.setAttribute("name", data.getName());
        classElement.setAttribute("type", data.isFCO() ? "FCO" : "SCO");
        classElement.setAttribute("version", this.version);
        final Map dataProps = data.getProperties();
        for (final Map.Entry entry : dataProps.entrySet()) {
            final String key = entry.getKey();
            final Object val = entry.getValue();
            if (val instanceof String) {
                classElement.setAttribute(key, (String)val);
            }
        }
        this.rootElement.appendChild(classElement);
    }
    
    @Override
    public void deleteClass(final String className) {
        this.autoStartClasses.remove(className);
        final NodeList classElements = this.rootElement.getElementsByTagName("class");
        for (int i = 0; i < classElements.getLength(); ++i) {
            final Element element = (Element)classElements.item(i);
            final String attr = element.getAttribute("name");
            if (attr != null && attr.equals(className)) {
                this.rootElement.removeChild(element);
            }
        }
    }
    
    @Override
    public void deleteAllClasses() {
        this.autoStartClasses.clear();
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            final DocumentBuilder db = factory.newDocumentBuilder();
            this.doc = db.newDocument();
            this.rootElement = this.doc.createElement("datanucleus_autostart");
            this.doc.appendChild(this.rootElement);
        }
        catch (ParserConfigurationException e) {
            NucleusLogger.PERSISTENCE.error(XMLAutoStarter.LOCALISER.msg("034203", this.fileUrl.getFile(), e.getMessage()));
        }
    }
    
    @Override
    public String getStorageDescription() {
        return XMLAutoStarter.LOCALISER.msg("034200");
    }
    
    private synchronized void writeToFile() {
        try {
            final TransformerFactory tf = TransformerFactory.newInstance();
            final Transformer m = tf.newTransformer();
            final DOMSource source = new DOMSource(this.doc);
            final FileOutputStream os = new FileOutputStream(this.fileUrl.getFile());
            final StreamResult result = new StreamResult(os);
            m.setOutputProperty("indent", "yes");
            m.setOutputProperty("doctype-public", "-//DataNucleus//DTD DataNucleus AutoStarter Metadata 1.0//EN");
            m.transform(source, result);
            os.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            NucleusLogger.PERSISTENCE.error(XMLAutoStarter.LOCALISER.msg("034203", this.fileUrl.getFile(), e.getMessage()));
        }
    }
}
