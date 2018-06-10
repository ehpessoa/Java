package com.ehpessoa.http;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;

/**
 * 
 * @author Everaldo Pessoa
 *
 */
public class HTTPClient implements HTTP {
	
	private String host = null;
	private String parameter = null;	
	private String responseAsString = "";
	private String outputFile = "";
	private InputStream responseAsStream = null;
	private HttpClient client = null;
	private String charset = null;
	private Object httpMethod = null;	
	private int statusCode = -1;
	private static Logger log = Logger.getLogger(HTTPClient.class);
	
    /**
     * 
     * @param host
     */
    public HTTPClient(String host) {
    	
    	this(host, null, null, 0);
    	
    }	
	    
    /**
     * 
     * @param host
     * @param parameter
     */
    public HTTPClient(String host, String parameter) {
    	
    	this(host, parameter, null, 0);
    	
    }
     
    
    /**
     * 
     * @param host
     * @param parameter 
     * @param proxyHost
     * @param proxyPort
     */
    public HTTPClient(String host, String parameter, String proxyHost, int proxyPort) {

    	this.client = new HttpClient();
        this.host = host;
        this.parameter = parameter;
        
        if ( proxyHost != null &&  proxyPort > 0 ) {
            HostConfiguration hostConf = this.client.getHostConfiguration();
            hostConf.setProxy(proxyHost, proxyPort); 
            this.client.setHostConfiguration(hostConf);
        }

    }	
    
    /**
     * @throws HTTPClientException 
     * 
     *
     */
	public void post() throws HTTPClientException {

		try {
			log.info("Initialize POST Method");
			
			PostMethod method = new PostMethod(this.host);

			if ( this.charset != null ) {
				log.info("Defining the charset="+this.charset);
				HttpMethodParams methodParams = new HttpMethodParams();
				methodParams.setContentCharset(this.charset);
				method.setParams(methodParams);
			}				
			if ( this.parameter != null ) {
				log.info("Defining the parameters");
				Iterator it = getParameter(this.parameter).iterator();
				while ( it.hasNext() ) {				
					method.addParameter((NameValuePair)it.next());
				}
			}
			
			log.info("Execute the method POST, charset: " + method.getParams().getContentCharset());
			this.statusCode = this.client.executeMethod(method);
			log.info("Status = "+ this.statusCode);
			if (this.statusCode != -1) {
				log.info("Getting the response");
				this.responseAsString = method.getResponseBodyAsString();
				this.responseAsStream = method.getResponseBodyAsStream();
				method.releaseConnection();
			}
			
			this.httpMethod = method;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	
	/**
	 * 
	 * @throws HTTPClientException
	 */
	public void get() throws HTTPClientException {

		try {			
			log.info("Initialize GET Method");

			GetMethod method = new GetMethod(this.host);
			
			if ( this.charset != null ) {
				log.info("Defining the charset="+this.charset);
				HttpMethodParams methodParams = new HttpMethodParams();
				methodParams.setContentCharset(this.charset);
				method.setParams(methodParams);
			}				
			if (  this.parameter != null ) {
				log.info("Defining the parameters");
				method.setQueryString(this.parameter);
			}
			
			log.info("Execute the method GET, charset: " + method.getParams().getContentCharset());
			this.statusCode = this.client.executeMethod(method);
			log.info("Status = "+ this.statusCode);
			if (this.statusCode != -1) {
				log.info("Getting the response");
				this.responseAsString = method.getResponseBodyAsString();
				this.responseAsStream = method.getResponseBodyAsStream();			      
				method.releaseConnection();
			}
			
			this.httpMethod = method;
			
		} catch (Exception e) {
			throw new HTTPClientException(e);
		}
		
	}
	
	public void setCredentials(String user, String password) {
		
		//host = http://localhost:9080/securityAppTesteWeb/teste.jsp
		String t1 = host.substring(host.indexOf(":")+3,host.length());
		String t2 = t1.substring(0,t1.indexOf("/")); 
		int p = 80;
		String h = t2;
		if ( t2.indexOf(":") != -1 ) {
			h = t2.substring(0,t2.indexOf(":"));
			p = Integer.parseInt(t2.substring(t2.indexOf(":")+1,t2.length()));
		}
		this.client.getParams().setAuthenticationPreemptive(true);
        Credentials defaultcreds = new UsernamePasswordCredentials(user, password);         
        this.client.getState().setCredentials(new AuthScope(h, p), defaultcreds);			
		
	}
	
	public void fileDownload() throws HTTPClientException {
		
		try {			
			
	    	StringTokenizer st = new StringTokenizer(this.parameter,"&");	    	
	    	if ( st.hasMoreTokens() ) {   		
	    		String tok = st.nextToken();
	    		String key = tok.substring(0,tok.indexOf("="));
	    		String val = tok.substring(tok.indexOf("=")+1,tok.length());
	    		log.debug("key = " + key);
	    		log.debug("val = " + val);	    		
			
	    		HTTPClient http = new HTTPClient(this.host);
	    		this.outputFile = val;
	    		http.get();
				if ( http.getStatusCode() == 200 ) {
					BufferedInputStream bis = new BufferedInputStream(http.getResponseAsStream());
					BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(this.outputFile, false));
					int b = 0;
					while ((b = bis.read()) >= 0) {
						bos.write(b);
					}
					bis.close();
					bos.flush();
					bos.close();
				}
	    	}
	    	
		} catch (Exception e) {
			throw new HTTPClientException(e);
		}
		
	}
	
	public void fileUpload() throws HTTPClientException {
		
		try {
	        HttpClient client = new HttpClient();
	        MultipartPostMethod mPost = new MultipartPostMethod(this.host);
	        client.setConnectionTimeout(8000);
	        
	    	StringTokenizer st = new StringTokenizer(this.parameter,"&");	    	
	    	while ( st.hasMoreTokens() ) {   		
	    		String tok = st.nextToken();
	    		String key = tok.substring(0,tok.indexOf("="));
	    		String val = tok.substring(tok.indexOf("=")+1,tok.length());
	    		log.debug("key = " + key);
	    		log.debug("val = " + val);	    		
		        // Send any XML file as the body of the POST request
		        File f = new File(val);
		        log.debug("File Length = " + f.length());
		        mPost.addParameter(f.getName(), f);
	    	}
	        int statusCode1 = client.executeMethod(mPost);

	        log.debug("statusLine>>>" + mPost.getStatusLine());
	        mPost.releaseConnection();	
	        
		} catch (Exception e) {
			throw new HTTPClientException(e);
		}
	}
	
    /**
	 * @return the host
	 */
	public String getHost() {
		return this.host;
	}

	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return the parameter
	 */
	public String getParameter() {
		return this.parameter;
	}

	/**
	 * @param parameter the parameter to set
	 */
	public void setParameter(String parameter) {
		this.parameter = parameter;
	}



	/**
	 * @return the responseAsStream
	 */
	public InputStream getResponseAsStream() {
		return this.responseAsStream;
	}

	/**
	 * @param responseAsStream the responseAsStream to set
	 */
	public void setResponseAsStream(InputStream responseAsStream) {
		this.responseAsStream = responseAsStream;
	}

	/**
	 * @return the responseAsString
	 */
	public String getResponseAsString() {
		return this.responseAsString;		
	}

	/**
	 * @return the httpMethod
	 */
	public Object getHttpMethod() {
		return this.httpMethod;
	}

	/**
	 * @return the statusCode
	 */
	public int getStatusCode() {
		return this.statusCode;
	}

	/**
	 * @param statusCode the statusCode to set
	 */
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * @param httpMethod the httpMethod to set
	 */
	public void setHttpMethod(Object httpMethod) {
		this.httpMethod = httpMethod;
	}

	/**
	 * @param responseAsString the responseAsString to set
	 */
	public void setResponseAsString(String responseAsString) {
		this.responseAsString = responseAsString;
	}

	/**
	 * @return the charset
	 */
	public String getCharset() {
		return this.charset;
	}

	/**
	 * @param charset the charset to set
	 */
	public void setCharset(String charset) {
		this.charset = charset;
	}
	
		
	/**
	 * @return the outputFile
	 */
	public String getOutputFile() {
		return outputFile;
	}

	/**
	 * @param outputFile the outputFile to set
	 */
	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}
	
	/**
	 * 
	 * @param params
	 * @return
	 */
    private List getParameter(String params) {

    	List listParameter = new ArrayList(); 
    	StringTokenizer st = new StringTokenizer(params,"&");
    	while ( st.hasMoreTokens() ) {   		
    		String tok = st.nextToken();
    		String key = tok.substring(0,tok.indexOf("="));
    		String val = tok.substring(tok.indexOf("=")+1,tok.length());
    		log.debug("key = " + key);
    		log.debug("val = " + val);
    		NameValuePair nvp = new NameValuePair(key, val);
    		listParameter.add(nvp);
    	}
    	return listParameter;
    	              
    }
    
    
    public static void main(String[] args) {
    	
    	HTTPClient http = new HTTPClient("http://shareit.global.ehpessoa.com/sites/BAA/default.aspx");
    	try {
    		http.setCredentials("pessoaev", "lksZq2");
			http.get();
			log.info(http.getResponseAsString());
		} catch (HTTPClientException e) {
			e.printStackTrace();
		}
    	
    }
	
}