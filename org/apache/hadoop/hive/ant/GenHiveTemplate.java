// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.ant;

import org.apache.tools.ant.BuildException;
import org.w3c.dom.Text;
import javax.xml.transform.Transformer;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.TransformerFactory;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.shims.ShimLoader;
import org.w3c.dom.Node;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import java.net.URL;
import java.io.File;
import org.apache.tools.ant.Task;

public class GenHiveTemplate extends Task
{
    private String templateFile;
    
    public String getTemplateFile() {
        return this.templateFile;
    }
    
    public void setTemplateFile(final String templateFile) {
        this.templateFile = templateFile;
    }
    
    private void generate() throws Exception {
        final File current = new File(this.templateFile);
        if (current.exists()) {
            final ClassLoader loader = GenHiveTemplate.class.getClassLoader();
            final URL url = loader.getResource("org/apache/hadoop/hive/conf/HiveConf.class");
            if (url != null) {
                final File file = new File(url.getFile());
                if (file.exists() && file.lastModified() < current.lastModified()) {
                    return;
                }
            }
        }
        this.writeToFile(current, this.generateTemplate());
    }
    
    private Document generateTemplate() throws Exception {
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        final DocumentBuilder docBuilder = dbf.newDocumentBuilder();
        final Document doc = docBuilder.newDocument();
        doc.appendChild(doc.createProcessingInstruction("xml-stylesheet", "type=\"text/xsl\" href=\"configuration.xsl\""));
        doc.appendChild(doc.createComment("\n   Licensed to the Apache Software Foundation (ASF) under one or more\n   contributor license agreements.  See the NOTICE file distributed with\n   this work for additional information regarding copyright ownership.\n   The ASF licenses this file to You under the Apache License, Version 2.0\n   (the \"License\"); you may not use this file except in compliance with\n   the License.  You may obtain a copy of the License at\n\n       http://www.apache.org/licenses/LICENSE-2.0\n\n   Unless required by applicable law or agreed to in writing, software\n   distributed under the License is distributed on an \"AS IS\" BASIS,\n   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n   See the License for the specific language governing permissions and\n   limitations under the License.\n"));
        final Element root = doc.createElement("configuration");
        doc.appendChild(root);
        root.appendChild(doc.createComment(" WARNING!!! This file is auto generated for documentation purposes ONLY! "));
        root.appendChild(doc.createComment(" WARNING!!! Any changes you make to this file will be ignored by Hive.   "));
        root.appendChild(doc.createComment(" WARNING!!! You must make your changes in hive-site.xml instead.         "));
        root.appendChild(doc.createComment(" Hive Execution Parameters "));
        Thread.currentThread().setContextClassLoader(ShimLoader.class.getClassLoader());
        for (final HiveConf.ConfVars confVars : HiveConf.ConfVars.values()) {
            if (!confVars.isExcluded()) {
                final Element property = this.appendElement(root, "property", null);
                this.appendElement(property, "name", confVars.varname);
                this.appendElement(property, "value", confVars.getDefaultExpr());
                this.appendElement(property, "description", this.normalize(confVars.getDescription()));
            }
        }
        return doc;
    }
    
    private String normalize(final String description) {
        int index = description.indexOf(10);
        if (index < 0) {
            return description;
        }
        int prev = 0;
        final StringBuilder builder = new StringBuilder(description.length() << 1);
        while (index > 0) {
            builder.append("\n      ").append(description.substring(prev, index));
            index = description.indexOf(10, prev = index + 1);
        }
        if (prev < description.length()) {
            builder.append("\n      ").append(description.substring(prev));
        }
        builder.append("\n    ");
        return builder.toString();
    }
    
    private void writeToFile(final File template, final Document document) throws Exception {
        final Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty("indent", "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        final DOMSource source = new DOMSource(document);
        final StreamResult result = new StreamResult(template);
        transformer.transform(source, result);
    }
    
    private Element appendElement(final Element parent, final String name, final String text) {
        final Document document = parent.getOwnerDocument();
        final Element child = document.createElement(name);
        parent.appendChild(child);
        if (text != null) {
            final Text textNode = document.createTextNode(text);
            child.appendChild(textNode);
        }
        return child;
    }
    
    @Override
    public void execute() throws BuildException {
        try {
            this.generate();
        }
        catch (Exception e) {
            throw new BuildException(e);
        }
    }
    
    public static void main(final String[] args) throws Exception {
        final GenHiveTemplate gen = new GenHiveTemplate();
        gen.generate();
    }
}
