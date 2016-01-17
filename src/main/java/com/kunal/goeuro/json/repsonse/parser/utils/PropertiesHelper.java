package com.kunal.goeuro.json.repsonse.parser.utils;

import java.util.Properties;

public class PropertiesHelper {

    private Properties props = null;

    public PropertiesHelper(Properties props) {
        this.props = props;
    }

    public String getString(String name, String defaultValue) {
        return props.getProperty(name, defaultValue);
    }

    public int getInteger(String name, Integer defaultValue) {
        return Integer.parseInt(props.getProperty(name, defaultValue.toString()));
    }

}


