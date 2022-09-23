// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api;

import org.apache.hadoop.util.Shell;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public interface ApplicationConstants
{
    public static final String APP_SUBMIT_TIME_ENV = "APP_SUBMIT_TIME_ENV";
    public static final String CONTAINER_TOKEN_FILE_ENV_NAME = "HADOOP_TOKEN_FILE_LOCATION";
    public static final String APPLICATION_WEB_PROXY_BASE_ENV = "APPLICATION_WEB_PROXY_BASE";
    public static final String LOG_DIR_EXPANSION_VAR = "<LOG_DIR>";
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public static final String CLASS_PATH_SEPARATOR = "<CPS>";
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public static final String PARAMETER_EXPANSION_LEFT = "{{";
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public static final String PARAMETER_EXPANSION_RIGHT = "}}";
    public static final String STDERR = "stderr";
    public static final String STDOUT = "stdout";
    public static final String MAX_APP_ATTEMPTS_ENV = "MAX_APP_ATTEMPTS";
    
    public enum Environment
    {
        USER("USER"), 
        LOGNAME("LOGNAME"), 
        HOME("HOME"), 
        PWD("PWD"), 
        PATH("PATH"), 
        SHELL("SHELL"), 
        JAVA_HOME("JAVA_HOME"), 
        CLASSPATH("CLASSPATH"), 
        APP_CLASSPATH("APP_CLASSPATH"), 
        LD_LIBRARY_PATH("LD_LIBRARY_PATH"), 
        HADOOP_CONF_DIR("HADOOP_CONF_DIR"), 
        HADOOP_COMMON_HOME("HADOOP_COMMON_HOME"), 
        HADOOP_HDFS_HOME("HADOOP_HDFS_HOME"), 
        MALLOC_ARENA_MAX("MALLOC_ARENA_MAX"), 
        HADOOP_YARN_HOME("HADOOP_YARN_HOME"), 
        CONTAINER_ID("CONTAINER_ID"), 
        NM_HOST("NM_HOST"), 
        NM_HTTP_PORT("NM_HTTP_PORT"), 
        NM_PORT("NM_PORT"), 
        LOCAL_DIRS("LOCAL_DIRS"), 
        LOG_DIRS("LOG_DIRS");
        
        private final String variable;
        
        private Environment(final String variable) {
            this.variable = variable;
        }
        
        public String key() {
            return this.variable;
        }
        
        @Override
        public String toString() {
            return this.variable;
        }
        
        public String $() {
            if (Shell.WINDOWS) {
                return "%" + this.variable + "%";
            }
            return "$" + this.variable;
        }
        
        @InterfaceAudience.Public
        @InterfaceStability.Unstable
        public String $$() {
            return "{{" + this.variable + "}}";
        }
    }
}
