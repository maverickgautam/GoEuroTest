package com.kunal.goeuro.json.repsonse.parser.interfaces;

import java.io.IOException;

import lombok.NonNull;

import com.kunal.goeuro.json.repsonse.parser.exception.JsonQueryParserException;

/**
 * @author kunalgautam Interface defines a general mechanism from reading from and source and writing to a sink
 *  Source and sink can have different implementation.
 *  requirement of providing a CityName and getting back response is constant
 */


public interface JsonQueryParser {
    /**
     * Initialize the parser with source and sink properties Source is url of Rest API Sink is Path of file
     */
    void init() throws IOException;

    /**
     * release any resource aqcquired in the process
     * 
     * @throws Exception
     */
    void cleanup() throws IOException;

    /**
     * Given a city name get back the Json value
     * 
     * @param cityName
     * @return
     */
    Object getResponse(String cityName) throws JsonQueryParserException;

    /**
     * write the response to sink.
     * 
     * @return
     * @throws JsonQueryParserException
     */
    boolean sendTosink( Object obj) throws JsonQueryParserException;
}
