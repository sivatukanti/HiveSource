// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.jsonFormatVisitors;

public enum JsonValueFormat
{
    DATE_TIME {
        @Override
        public String toString() {
            return "date-time";
        }
    }, 
    DATE {
        @Override
        public String toString() {
            return "date";
        }
    }, 
    TIME {
        @Override
        public String toString() {
            return "time";
        }
    }, 
    UTC_MILLISEC {
        @Override
        public String toString() {
            return "utc-millisec";
        }
    }, 
    REGEX {
        @Override
        public String toString() {
            return "regex";
        }
    }, 
    COLOR {
        @Override
        public String toString() {
            return "color";
        }
    }, 
    STYLE {
        @Override
        public String toString() {
            return "style";
        }
    }, 
    PHONE {
        @Override
        public String toString() {
            return "phone";
        }
    }, 
    URI {
        @Override
        public String toString() {
            return "uri";
        }
    }, 
    EMAIL {
        @Override
        public String toString() {
            return "email";
        }
    }, 
    IP_ADDRESS {
        @Override
        public String toString() {
            return "ip-address";
        }
    }, 
    IPV6 {
        @Override
        public String toString() {
            return "ipv6";
        }
    }, 
    HOST_NAME {
        @Override
        public String toString() {
            return "host-name";
        }
    };
}
