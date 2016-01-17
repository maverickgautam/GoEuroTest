package com.kunal.goeuro.json.repsonse.parser.impls;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;

import lombok.Data;
import lombok.Getter;
import lombok.NonNull;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.kunal.goeuro.json.repsonse.parser.constants.JsonQueryParserConstants;
import com.kunal.goeuro.json.repsonse.parser.exception.JsonQueryParserException;
import com.kunal.goeuro.json.repsonse.parser.interfaces.JsonQueryParser;
import com.kunal.goeuro.json.repsonse.parser.utils.PropertiesHelper;



@Data
class GeoPosition {
    private Double latitude;
    private Double longitude;

    @Override
    public String toString() {
        return latitude + "," + longitude;
    }
}


@Data
class Location {
    private Integer _id;
    private String key;
    private String name;
    private String fullName;
    private String iata_airport_code;
    private String type;
    private String country;
    private GeoPosition geo_position;
    private Integer locationId;
    private Boolean inEurope;
    private String countryCode;
    private Boolean coreCountry;
    private String distance;
}



public class JsonQueryParserImpl implements JsonQueryParser {

    private final HttpClient client = getHttpClient();
    @Getter
    private String filePath;
    private String url;
    private File outPutfileHandler;


    public JsonQueryParserImpl() throws IOException {
        init();
    }

    /**
     * We use HttpClient for establising connection to the REST API dpending on ones need
     * RETRY/KEEPALIVE/CONNECTIONREUSE can be setup
     * 
     * @return HttpClient
     */
    private HttpClient getHttpClient() {
        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();
        manager.setMaxTotal(JsonQueryParserConstants.NUM_HTTP_RETRY_SESSION_HANDLER);
        manager.setDefaultMaxPerRoute(JsonQueryParserConstants.NUM_HTTP_RETRY_SESSION_HANDLER);
        DefaultHttpRequestRetryHandler retryHandler =
                new DefaultHttpRequestRetryHandler(JsonQueryParserConstants.NUM_HTTP_RETRY_SESSION_HANDLER, true);
        HttpClient client =
                HttpClientBuilder.create().setConnectionManager(manager).setRetryHandler(retryHandler).build();
        return client;
    }

    /*
     * used for loading files in the classpath
     */
    private InputStream getFilePathInBuildPath(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        return (classLoader.getResourceAsStream(fileName));
    }

 
    /**
     * Initializes all resources
     * Read props from property File 
     * sets input REST URL and output file Path 
     */
    @Override
    public void init() throws IOException {
        Properties prop = new Properties();
        InputStream input = getFilePathInBuildPath(JsonQueryParserConstants.PROPERTY_FILE_NAME);
        prop.load(input);
        PropertiesHelper helper = new PropertiesHelper(prop);
        url = helper.getString(JsonQueryParserConstants.URL_KEYWORD, JsonQueryParserConstants.DEFAULT_URL);
        filePath = helper.getString(JsonQueryParserConstants.FILEPATH, JsonQueryParserConstants.DEFAULT_OUTPUT_FILE);
        //If output path exist delete it 
        outPutfileHandler = new File(filePath);
        if (outPutfileHandler.exists()) {
            outPutfileHandler.delete();
        }
    }


    @Override
    public void cleanup() throws IOException {

    }


    /*
     * The respons object from The restAPi needs to be parsed here 
     * This is custom logic for parsing object
     */
    protected String customResponeParser(@NonNull Object obj) {
        StringBuilder content = new StringBuilder();
        Gson gson = new Gson();
        JSONArray js = (JSONArray) obj;
        if (js.isEmpty()) {
            return content.toString();
        }

        Iterator<?> keys = js.iterator();
        while (keys.hasNext()) {
            Object obj1 = keys.next();
            Location location = gson.fromJson(obj1.toString(), Location.class);
            content.append(location.get_id()).append(JsonQueryParserConstants.FIELD_DELIM).append(location.getName())
                    .append(JsonQueryParserConstants.FIELD_DELIM).append(location.getType())
                    .append(JsonQueryParserConstants.FIELD_DELIM).append(location.getGeo_position().toString())
                    .append(JsonQueryParserConstants.RECORD_DELIM);
        }
        return content.toString();
    }


    /*
     * For given citName fetch the Json Response from the RestEndpoint
     * (non-Javadoc)
     * @see com.kunal.goeuro.json.repsonse.parser.interfaces.JsonQueryParser#getResponse(java.lang.String)
     */

    @Override
    public Object getResponse(@NonNull String cityName) throws JsonQueryParserException {

        HttpGet get = new HttpGet(url + cityName);
        get.addHeader("accept", "application/json");

        HttpResponse response = null;
        String json = null;

        try {
            response = client.execute(get);
        } catch (IOException e) {
            throw new JsonQueryParserException(JsonQueryParserException.ErrorCode.RESPONSE_FETCH_ERROR,
                    "Exception caught while getting http response  ", e);
        }
         
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new JsonQueryParserException(JsonQueryParserException.ErrorCode.HTTP_STATUS_CODE_ERROR,
                    "Exception caught while getting http response  ");
        }

        try {
            json = EntityUtils.toString(response.getEntity());
            JSONParser parse = new JSONParser();
            return parse.parse(json);
        } catch (ParseException | IOException e) {
            throw new JsonQueryParserException(JsonQueryParserException.ErrorCode.JSON_PARSING_ERROR,
                    "Exception while parsing Json ", e);
        }
    }


    /**
     * write the data into the file specified via properties file . 
     */

    @Override
    public boolean sendTosink(@NonNull Object obj) throws JsonQueryParserException {
        try {
            if (StringUtils.isBlank((String) obj)) {
                return true;
            }
            FileUtils.writeStringToFile(outPutfileHandler, (String) obj, "UTF-8");
        } catch (IOException e) {
            throw new JsonQueryParserException(JsonQueryParserException.ErrorCode.FILE_WRITING_EXCEPTION,
                    "Exception while writing to file", e);
        }
        return true;
    }

    public void run(@NonNull String cityName) throws JsonQueryParserException {
        Object obj = getResponse(cityName);
        if (obj == null) {
            return;
        }
        sendTosink(customResponeParser(obj));
    }


    public static void main(String[] args) throws Exception {
        JsonQueryParserImpl queryParser = new JsonQueryParserImpl();
        System.out.println("Output file is present at " + queryParser.getFilePath());
        queryParser.run(args[0]);
    }
}
