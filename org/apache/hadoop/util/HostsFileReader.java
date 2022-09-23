// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import org.slf4j.LoggerFactory;
import java.util.Collection;
import java.util.HashMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Map;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.io.FileInputStream;
import java.io.File;
import java.util.Set;
import java.io.InputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Unstable
public class HostsFileReader
{
    private static final Logger LOG;
    private final AtomicReference<HostDetails> current;
    
    public HostsFileReader(final String inFile, final String exFile) throws IOException {
        final HostDetails hostDetails = new HostDetails(inFile, Collections.emptySet(), exFile, Collections.emptyMap());
        this.current = new AtomicReference<HostDetails>(hostDetails);
        this.refresh(inFile, exFile);
    }
    
    @InterfaceAudience.Private
    public HostsFileReader(final String includesFile, final InputStream inFileInputStream, final String excludesFile, final InputStream exFileInputStream) throws IOException {
        final HostDetails hostDetails = new HostDetails(includesFile, Collections.emptySet(), excludesFile, Collections.emptyMap());
        this.current = new AtomicReference<HostDetails>(hostDetails);
        this.refresh(inFileInputStream, exFileInputStream);
    }
    
    public static void readFileToSet(final String type, final String filename, final Set<String> set) throws IOException {
        final File file = new File(filename);
        final FileInputStream fis = new FileInputStream(file);
        readFileToSetWithFileInputStream(type, filename, fis, set);
    }
    
    @InterfaceAudience.Private
    public static void readFileToSetWithFileInputStream(final String type, final String filename, final InputStream fileInputStream, final Set<String> set) throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(fileInputStream, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                final String[] nodes = line.split("[ \t\n\f\r]+");
                if (nodes != null) {
                    for (int i = 0; i < nodes.length; ++i) {
                        nodes[i] = nodes[i].trim();
                        if (nodes[i].startsWith("#")) {
                            break;
                        }
                        if (!nodes[i].isEmpty()) {
                            HostsFileReader.LOG.info("Adding a node \"" + nodes[i] + "\" to the list of " + type + " hosts from " + filename);
                            set.add(nodes[i]);
                        }
                    }
                }
            }
        }
        finally {
            if (reader != null) {
                reader.close();
            }
            fileInputStream.close();
        }
    }
    
    public void refresh() throws IOException {
        final HostDetails hostDetails = this.current.get();
        this.refresh(hostDetails.includesFile, hostDetails.excludesFile);
    }
    
    public static void readFileToMap(final String type, final String filename, final Map<String, Integer> map) throws IOException {
        final File file = new File(filename);
        final FileInputStream fis = new FileInputStream(file);
        readFileToMapWithFileInputStream(type, filename, fis, map);
    }
    
    public static void readFileToMapWithFileInputStream(final String type, final String filename, final InputStream inputStream, final Map<String, Integer> map) throws IOException {
        final boolean xmlInput = filename.toLowerCase().endsWith(".xml");
        if (xmlInput) {
            readXmlFileToMapWithFileInputStream(type, filename, inputStream, map);
        }
        else {
            final HashSet<String> nodes = new HashSet<String>();
            readFileToSetWithFileInputStream(type, filename, inputStream, nodes);
            for (final String node : nodes) {
                map.put(node, null);
            }
        }
    }
    
    public static void readXmlFileToMapWithFileInputStream(final String type, final String filename, final InputStream fileInputStream, final Map<String, Integer> map) throws IOException {
        final DocumentBuilderFactory builder = DocumentBuilderFactory.newInstance();
        try {
            final DocumentBuilder db = builder.newDocumentBuilder();
            final Document dom = db.parse(fileInputStream);
            final Element doc = dom.getDocumentElement();
            final NodeList nodes = doc.getElementsByTagName("host");
            for (int i = 0; i < nodes.getLength(); ++i) {
                final Node node = nodes.item(i);
                if (node.getNodeType() == 1) {
                    final Element e = (Element)node;
                    final String v = readFirstTagValue(e, "name");
                    final String[] hosts = StringUtils.getTrimmedStrings(v);
                    final String str = readFirstTagValue(e, "timeout");
                    final Integer timeout = (str == null) ? null : Integer.valueOf(Integer.parseInt(str));
                    for (final String host : hosts) {
                        map.put(host, timeout);
                        HostsFileReader.LOG.info("Adding a node \"" + host + "\" to the list of " + type + " hosts from " + filename);
                    }
                }
            }
        }
        catch (IOException ex) {}
        catch (SAXException ex2) {}
        catch (ParserConfigurationException e2) {
            HostsFileReader.LOG.error("error parsing " + filename, e2);
            throw new RuntimeException(e2);
        }
        finally {
            fileInputStream.close();
        }
    }
    
    static String readFirstTagValue(final Element e, final String tag) {
        final NodeList nodes = e.getElementsByTagName(tag);
        return (nodes.getLength() == 0) ? null : nodes.item(0).getTextContent();
    }
    
    public void refresh(final String includesFile, final String excludesFile) throws IOException {
        HostsFileReader.LOG.info("Refreshing hosts (include/exclude) list");
        final HostDetails oldDetails = this.current.get();
        Set<String> newIncludes = oldDetails.includes;
        Map<String, Integer> newExcludes = oldDetails.excludes;
        if (includesFile != null && !includesFile.isEmpty()) {
            newIncludes = new HashSet<String>();
            readFileToSet("included", includesFile, newIncludes);
            newIncludes = Collections.unmodifiableSet((Set<? extends String>)newIncludes);
        }
        if (excludesFile != null && !excludesFile.isEmpty()) {
            newExcludes = new HashMap<String, Integer>();
            readFileToMap("excluded", excludesFile, newExcludes);
            newExcludes = Collections.unmodifiableMap((Map<? extends String, ? extends Integer>)newExcludes);
        }
        final HostDetails newDetails = new HostDetails(includesFile, newIncludes, excludesFile, newExcludes);
        this.current.set(newDetails);
    }
    
    @InterfaceAudience.Private
    public void refresh(final InputStream inFileInputStream, final InputStream exFileInputStream) throws IOException {
        HostsFileReader.LOG.info("Refreshing hosts (include/exclude) list");
        final HostDetails oldDetails = this.current.get();
        Set<String> newIncludes = oldDetails.includes;
        Map<String, Integer> newExcludes = oldDetails.excludes;
        if (inFileInputStream != null) {
            newIncludes = new HashSet<String>();
            readFileToSetWithFileInputStream("included", oldDetails.includesFile, inFileInputStream, newIncludes);
            newIncludes = Collections.unmodifiableSet((Set<? extends String>)newIncludes);
        }
        if (exFileInputStream != null) {
            newExcludes = new HashMap<String, Integer>();
            readFileToMapWithFileInputStream("excluded", oldDetails.excludesFile, exFileInputStream, newExcludes);
            newExcludes = Collections.unmodifiableMap((Map<? extends String, ? extends Integer>)newExcludes);
        }
        final HostDetails newDetails = new HostDetails(oldDetails.includesFile, newIncludes, oldDetails.excludesFile, newExcludes);
        this.current.set(newDetails);
    }
    
    public Set<String> getHosts() {
        final HostDetails hostDetails = this.current.get();
        return hostDetails.getIncludedHosts();
    }
    
    public Set<String> getExcludedHosts() {
        final HostDetails hostDetails = this.current.get();
        return hostDetails.getExcludedHosts();
    }
    
    @Deprecated
    public void getHostDetails(final Set<String> includes, final Set<String> excludes) {
        final HostDetails hostDetails = this.current.get();
        includes.addAll(hostDetails.getIncludedHosts());
        excludes.addAll(hostDetails.getExcludedHosts());
    }
    
    @Deprecated
    public void getHostDetails(final Set<String> includeHosts, final Map<String, Integer> excludeHosts) {
        final HostDetails hostDetails = this.current.get();
        includeHosts.addAll(hostDetails.getIncludedHosts());
        excludeHosts.putAll(hostDetails.getExcludedMap());
    }
    
    public HostDetails getHostDetails() {
        return this.current.get();
    }
    
    public void setIncludesFile(final String includesFile) {
        HostsFileReader.LOG.info("Setting the includes file to " + includesFile);
        final HostDetails oldDetails = this.current.get();
        final HostDetails newDetails = new HostDetails(includesFile, oldDetails.includes, oldDetails.excludesFile, oldDetails.excludes);
        this.current.set(newDetails);
    }
    
    public void setExcludesFile(final String excludesFile) {
        HostsFileReader.LOG.info("Setting the excludes file to " + excludesFile);
        final HostDetails oldDetails = this.current.get();
        final HostDetails newDetails = new HostDetails(oldDetails.includesFile, oldDetails.includes, excludesFile, oldDetails.excludes);
        this.current.set(newDetails);
    }
    
    public void updateFileNames(final String includesFile, final String excludesFile) {
        HostsFileReader.LOG.info("Setting the includes file to " + includesFile);
        HostsFileReader.LOG.info("Setting the excludes file to " + excludesFile);
        final HostDetails oldDetails = this.current.get();
        final HostDetails newDetails = new HostDetails(includesFile, oldDetails.includes, excludesFile, oldDetails.excludes);
        this.current.set(newDetails);
    }
    
    static {
        LOG = LoggerFactory.getLogger(HostsFileReader.class);
    }
    
    public static class HostDetails
    {
        private final String includesFile;
        private final Set<String> includes;
        private final String excludesFile;
        private final Map<String, Integer> excludes;
        
        HostDetails(final String includesFile, final Set<String> includes, final String excludesFile, final Map<String, Integer> excludes) {
            this.includesFile = includesFile;
            this.includes = includes;
            this.excludesFile = excludesFile;
            this.excludes = excludes;
        }
        
        public String getIncludesFile() {
            return this.includesFile;
        }
        
        public Set<String> getIncludedHosts() {
            return this.includes;
        }
        
        public String getExcludesFile() {
            return this.excludesFile;
        }
        
        public Set<String> getExcludedHosts() {
            return this.excludes.keySet();
        }
        
        public Map<String, Integer> getExcludedMap() {
            return this.excludes;
        }
    }
}
