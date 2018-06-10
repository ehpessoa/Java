package com.ehpessoa.http;

import org.apache.log4j.Logger;

public class Executor {

	private static Logger log = Logger.getLogger(Executor.class);
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			
			HTTP http = null;
			if ( args.length == 2 ) {
				if ( args[1].toLowerCase().startsWith("http://") ) {
					http = new HTTPClient(args[1]);
				} else if ( args[1].toLowerCase().startsWith("https://") ) {
					http = new HTTPSClient(args[1]);
				}				
				if ( args[0].equalsIgnoreCase("GET") ) {
					http.get();
				} else if ( args[0].equalsIgnoreCase("POST") ) {
					http.post();
				}
				log.info(http.getResponseAsString());
				if ( http.getStatusCode() != 200 ) {
					log.error("HTTP ERROR " + http.getStatusCode());
					System.exit(1);
				}
			} else if ( args.length == 3 ) {
				if ( args[1].toLowerCase().startsWith("http://") ) {
					http = new HTTPClient(args[1],args[2]);
				} else if ( args[1].toLowerCase().startsWith("https://") ) {
					http = new HTTPSClient(args[1],args[2]);
				}
				if ( args[0].equalsIgnoreCase("DOWNLOAD") ) {
					http.fileDownload();
				} else if ( args[0].equalsIgnoreCase("UPLOAD") ) {
					http.fileUpload();
				} else {					
					if ( args[0].equalsIgnoreCase("GET") ) {						
						http.get();
					} else if ( args[0].equalsIgnoreCase("POST") ) {
						http.post();
					}					
					log.info(http.getResponseAsString());
				}
				log.info(http.getResponseAsString());
				if ( http.getStatusCode() != 200 ) {
					log.error("HTTP ERROR " + http.getStatusCode());
					System.exit(1);
				}
			} else {				
				log.info("Usage for HTTP:");
				log.info("   java -jar HTTPClient.jar <GET/POST/DOWNLOAD/UPLOAD> <HTTPHOST> [PARAMETERS] [PROXYHOST] [PROXYPORT]");
				log.info("Example 1:");
				log.info("   java -jar HTTPClient.jar GET http://www.br.ehpessoa.com/home/home.jsp");
				log.info("Example 2:");
				log.info("   java -jar HTTPClient.jar POST https://www.br.ehpessoa.com/login/login.jsp account=everaldo&password=123123");
				System.exit(1);
			}
			
			System.exit(0);
			
		} catch (Exception e) {
			log.error(e);
			System.exit(1);
		}
		
	}

}
