package com.kunal.goeuro.json.repsonse.parser.constants;


public class JsonQueryParserConstants {

    public static final int NUM_HTTP_RETRY_SESSION_HANDLER = 3;
    
    public static final String PROPERTY_FILE_NAME = "queryparser.properties";
    public static final String  URL_KEYWORD = "url";
    public static final String  FILEPATH = "filePath";
       
    public static String DEFAULT_URL = "http://api.goeuro.com/api/v2/position/suggest/en/";
    public static String DEFAULT_OUTPUT_FILE= "/tmp/goeuro/query/parser/output.txt";
   
    public static String RECORD_DELIM = "\n";
    public static String FIELD_DELIM = ",";
   
   
}
