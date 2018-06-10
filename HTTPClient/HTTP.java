package com.ehpessoa.http;

import java.io.InputStream;

public interface HTTP {
	
	public void post() throws HTTPClientException;
	
	public void get() throws HTTPClientException;
	
	public void fileDownload() throws HTTPClientException;
	
	public void fileUpload() throws HTTPClientException;
	
	public String getHost();
	
	public void setHost(String host);
	
	public String getParameter();
	
	public void setParameter(String parameter);
	
	public InputStream getResponseAsStream();
	
	public void setResponseAsStream(InputStream responseAsStream);
	
	public String getResponseAsString();
	
	public Object getHttpMethod();
	
	public int getStatusCode();
	
	public void setStatusCode(int statusCode);
	
	public void setHttpMethod(Object httpMethod);
	
	public void setResponseAsString(String responseAsString);
	
	public String getCharset();
	
	public void setCharset(String charset);
	
	public String getOutputFile();
	
	public void setOutputFile(String outputFile);	

}
