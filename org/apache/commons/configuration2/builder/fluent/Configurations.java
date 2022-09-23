// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.builder.fluent;

import org.apache.commons.configuration2.CombinedConfiguration;
import org.apache.commons.configuration2.builder.BuilderParameters;
import org.apache.commons.configuration2.builder.combined.CombinedConfigurationBuilder;
import org.apache.commons.configuration2.INIConfiguration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import java.net.URL;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.FileBasedConfiguration;
import java.io.File;

public class Configurations
{
    private final Parameters parameters;
    
    public Configurations() {
        this(null);
    }
    
    public Configurations(final Parameters params) {
        this.parameters = ((params != null) ? params : new Parameters());
    }
    
    public Parameters getParameters() {
        return this.parameters;
    }
    
    public <T extends FileBasedConfiguration> FileBasedConfigurationBuilder<T> fileBasedBuilder(final Class<T> configClass, final File file) {
        return this.createFileBasedBuilder(configClass, this.fileParams(file));
    }
    
    public <T extends FileBasedConfiguration> FileBasedConfigurationBuilder<T> fileBasedBuilder(final Class<T> configClass, final URL url) {
        return this.createFileBasedBuilder(configClass, this.fileParams(url));
    }
    
    public <T extends FileBasedConfiguration> FileBasedConfigurationBuilder<T> fileBasedBuilder(final Class<T> configClass, final String path) {
        return this.createFileBasedBuilder(configClass, this.fileParams(path));
    }
    
    public <T extends FileBasedConfiguration> T fileBased(final Class<T> configClass, final File file) throws ConfigurationException {
        return this.fileBasedBuilder(configClass, file).getConfiguration();
    }
    
    public <T extends FileBasedConfiguration> T fileBased(final Class<T> configClass, final URL url) throws ConfigurationException {
        return this.fileBasedBuilder(configClass, url).getConfiguration();
    }
    
    public <T extends FileBasedConfiguration> T fileBased(final Class<T> configClass, final String path) throws ConfigurationException {
        return this.fileBasedBuilder(configClass, path).getConfiguration();
    }
    
    public FileBasedConfigurationBuilder<PropertiesConfiguration> propertiesBuilder(final File file) {
        return this.fileBasedBuilder(PropertiesConfiguration.class, file);
    }
    
    public FileBasedConfigurationBuilder<PropertiesConfiguration> propertiesBuilder(final URL url) {
        return this.fileBasedBuilder(PropertiesConfiguration.class, url);
    }
    
    public FileBasedConfigurationBuilder<PropertiesConfiguration> propertiesBuilder(final String path) {
        return this.fileBasedBuilder(PropertiesConfiguration.class, path);
    }
    
    public PropertiesConfiguration properties(final File file) throws ConfigurationException {
        return this.propertiesBuilder(file).getConfiguration();
    }
    
    public PropertiesConfiguration properties(final URL url) throws ConfigurationException {
        return this.propertiesBuilder(url).getConfiguration();
    }
    
    public PropertiesConfiguration properties(final String path) throws ConfigurationException {
        return this.propertiesBuilder(path).getConfiguration();
    }
    
    public FileBasedConfigurationBuilder<XMLConfiguration> xmlBuilder(final File file) {
        return this.fileBasedBuilder(XMLConfiguration.class, file);
    }
    
    public FileBasedConfigurationBuilder<XMLConfiguration> xmlBuilder(final URL url) {
        return this.fileBasedBuilder(XMLConfiguration.class, url);
    }
    
    public FileBasedConfigurationBuilder<XMLConfiguration> xmlBuilder(final String path) {
        return this.fileBasedBuilder(XMLConfiguration.class, path);
    }
    
    public XMLConfiguration xml(final File file) throws ConfigurationException {
        return this.xmlBuilder(file).getConfiguration();
    }
    
    public XMLConfiguration xml(final URL url) throws ConfigurationException {
        return this.xmlBuilder(url).getConfiguration();
    }
    
    public XMLConfiguration xml(final String path) throws ConfigurationException {
        return this.xmlBuilder(path).getConfiguration();
    }
    
    public FileBasedConfigurationBuilder<INIConfiguration> iniBuilder(final File file) {
        return this.fileBasedBuilder(INIConfiguration.class, file);
    }
    
    public FileBasedConfigurationBuilder<INIConfiguration> iniBuilder(final URL url) {
        return this.fileBasedBuilder(INIConfiguration.class, url);
    }
    
    public FileBasedConfigurationBuilder<INIConfiguration> iniBuilder(final String path) {
        return this.fileBasedBuilder(INIConfiguration.class, path);
    }
    
    public INIConfiguration ini(final File file) throws ConfigurationException {
        return this.iniBuilder(file).getConfiguration();
    }
    
    public INIConfiguration ini(final URL url) throws ConfigurationException {
        return this.iniBuilder(url).getConfiguration();
    }
    
    public INIConfiguration ini(final String path) throws ConfigurationException {
        return this.iniBuilder(path).getConfiguration();
    }
    
    public CombinedConfigurationBuilder combinedBuilder(final File file) {
        return new CombinedConfigurationBuilder().configure(this.fileParams(file));
    }
    
    public CombinedConfigurationBuilder combinedBuilder(final URL url) {
        return new CombinedConfigurationBuilder().configure(this.fileParams(url));
    }
    
    public CombinedConfigurationBuilder combinedBuilder(final String path) {
        return new CombinedConfigurationBuilder().configure(this.fileParams(path));
    }
    
    public CombinedConfiguration combined(final File file) throws ConfigurationException {
        return this.combinedBuilder(file).getConfiguration();
    }
    
    public CombinedConfiguration combined(final URL url) throws ConfigurationException {
        return this.combinedBuilder(url).getConfiguration();
    }
    
    public CombinedConfiguration combined(final String path) throws ConfigurationException {
        return this.combinedBuilder(path).getConfiguration();
    }
    
    private <T extends FileBasedConfiguration> FileBasedConfigurationBuilder<T> createFileBasedBuilder(final Class<T> configClass, final FileBasedBuilderParameters params) {
        return new FileBasedConfigurationBuilder<T>((Class<? extends T>)configClass).configure(params);
    }
    
    private FileBasedBuilderParameters fileParams() {
        return this.getParameters().fileBased();
    }
    
    private FileBasedBuilderParameters fileParams(final File file) {
        return this.fileParams().setFile(file);
    }
    
    private FileBasedBuilderParameters fileParams(final URL url) {
        return this.fileParams().setURL(url);
    }
    
    private FileBasedBuilderParameters fileParams(final String path) {
        return this.fileParams().setFileName(path);
    }
}
