package com.ehpessoa.http;

import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.log4j.Logger;

public class HTTPSClient extends HTTPClient implements HTTP {
	
	private static Logger log = Logger.getLogger(HTTPSClient.class);
	
	static {  
		log.info("Start HTTPS Session");
		Protocol.registerProtocol("https", new Protocol("https", new EasySSLProtocolSocketFactory(), 443));  
	}  
	
    /**
     * 
     * @param host
     */
    public HTTPSClient(String host) {
    	
    	super(host);
    	
    }	
	    
    /**
     * 
     * @param host
     * @param parameter
     */
    public HTTPSClient(String host, String parameter) {
    	
    	super(host, parameter);
    	
    }
     
    
    /**
     * 
     * @param host
     * @param parameter 
     * @param proxyHost
     * @param proxyPort
     */
    public HTTPSClient(String host, String parameter, String proxyHost, int proxyPort) {

    	super(host, parameter, proxyHost, proxyPort);
    }
    

}
