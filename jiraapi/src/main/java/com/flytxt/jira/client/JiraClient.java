package com.flytxt.jira.client;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.naming.AuthenticationException;

import com.flytxt.jira.client.exception.JiraException;
import com.flytxt.jira.main.JiraMain;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.core.util.Base64;
import com.sun.jersey.oauth.client.OAuthClientFilter;
import com.sun.jersey.oauth.signature.OAuthParameters;
import com.sun.jersey.oauth.signature.OAuthSecrets;
/**
 * 
 * @author shijuj
 *
 */
public class JiraClient {
	
	
	private static volatile JiraClient jiraClient = null ;
	private String auth = null;
	private String baseUrl;
	private boolean oauthBased;
	private String consumerKey;
	private String signatureMethod;
	private String privatekey;
	@SuppressWarnings("rawtypes")
	private Map searchFileds= null;
	private Client client =  Client.create();
	/**
	 * 
	 * @param baseUrl
	 * @param userId
	 * @param userSecret
	 */
	private JiraClient(String baseUrl,String userId,String userSecret){
		this.oauthBased = false;
		this.auth = new String(Base64.encode(userId+":"+userSecret));
		this.baseUrl = baseUrl==null?"https://flytxt.atlassian.net": baseUrl;
		this.searchFileds = getSearchFieldMap();
	}
	
	
	/**
	 * 
	 * @param BASE_URL
	 * @param oauthToken
	 * @param consumerKey
	 * @param signatureMethod
	 * @param privatekey
	 */
	private JiraClient(String BASE_URL,String oauthToken,String consumerKey,
								String signatureMethod,String privatekey){
		this.oauthBased = true;
		this.auth = oauthToken;
		this.baseUrl = BASE_URL==null?"https://flytxt.atlassian.net": BASE_URL;
		this.consumerKey = consumerKey;
		this.signatureMethod = signatureMethod;
		this.privatekey= privatekey;
		this.searchFileds = getSearchFieldMap();
	}
	
	
	
	/**
	 * Jira Client 
	 * @param BASE_URL
	 * @param userId
	 * @param userSecret
	 * @return
	 */
	public static JiraClient getInstance(String BASE_URL,String userId,String userSecret){
		if(null==jiraClient){
			synchronized (JiraClient.class) {
				if(null== jiraClient){
					jiraClient = new JiraClient(BASE_URL, userId, userSecret);
				}				
			}
		}
		return jiraClient;
	}
	
		
	/**
	 * 
	 * @param BASE_URL
	 * @param oauthToken
	 * @param consumerKey
	 * @param signatureMethod
	 * @param privatekey
	 * @return
	 */
	public static JiraClient getInstance(String BASE_URL,String oauthToken,
								String consumerKey,String signatureMethod,String privatekey){
		if(null==jiraClient){
			synchronized (JiraClient.class) {
				if(null== jiraClient){
					jiraClient = new JiraClient(BASE_URL, oauthToken,consumerKey,signatureMethod,privatekey);
				}				
			}
		}
		return jiraClient;
	}
	
	
	/**
	 * 
	 * @param searchParameters
	 * @param orderBy
	 * @param maxResult
	 * @return
	 */
	public String search(Map<String,String> searchParameters,
			String orderBy,int startAt,int maxResult,String [] fields) throws JiraException{
		
		final String url =  baseUrl +"/rest/api/2/search";
		StringBuilder builder = new StringBuilder(getSearchCriteria(searchParameters));
		
		builder.append(",\"startAt\":"+startAt); 
		
		if(-1!=maxResult ){
			builder.append(",\"maxResults\":"+maxResult); 
		}
		
		if(fields!=null){
			builder.append(getFileds(fields));
		}
		if(null!=orderBy){
			builder.append("+order+by+"+orderBy);
		}
		
		builder.append("}");
			
		System.out.println("Filter"+builder.toString());
		try {
			return invokePostMethod(auth, url, builder.toString());
			
		} catch (AuthenticationException | ClientHandlerException e) {
			throw new JiraException(e);
		}
		
	}
	
	/**
	 * 
	 * @param fields
	 * @return
	 */
	private String getFileds(String[] fields) {
		StringBuilder builder = new StringBuilder(",\"fields\":[");
		for(String field : fields){
			builder.append("\""+field + "\",");
		}
		builder.replace(builder.length()-1, builder.length(), "");
		builder.append("]");
		return builder.toString();
	}


	/**
	 * Build the jql Search criteria 
	 * @param searchParameters
	 * @return
	 */
	private String getSearchCriteria(Map<String, String> searchParameters) {
		StringBuilder builder = new StringBuilder();
		builder.append("{\"jql\":\"");		
		int parameterCount =0;
		for(Entry<String, String> parameter: searchParameters.entrySet()){			
			builder.append(getKey(parameter.getKey()).replaceAll("@", "\\\\\\\\u0040"))
				   .append(parameter.getValue().replaceAll("@", "\\\\\\\\u0040"));
			if (++parameterCount < searchParameters.size()){
				builder.append(" and ");
			}else{
				builder.append(" \"");
			}				 			
		}		
		return builder.toString();
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	private String getKey(String key) {
		if(searchFileds!=null && !searchFileds.isEmpty()){
			return searchFileds.containsKey(key)?(String)searchFileds.get(key):key;
		}
		return key;
	}


	/**
	 * 
	 * @param auth
	 * @param url
	 * @return
	 * @throws AuthenticationException
	 * @throws ClientHandlerException
	 */
	@SuppressWarnings("unused")
	private String invokeGetMethod(String auth, String url) throws AuthenticationException, ClientHandlerException {
		Client client = Client.create();
		WebResource webResource = client.resource(url);
		ClientResponse response = null;						
		
		if(oauthBased) {
			webResource.addFilter(getOauthFilter(client));
			response= webResource.type("application/json")
					.accept("application/json").get(ClientResponse.class);
		}else{
			response= webResource.header("Authorization", "Basic " + auth).type("application/json")
					.accept("application/json").get(ClientResponse.class);
		}
				
		int statusCode = response.getStatus();
		if (statusCode == 401) {
			throw new AuthenticationException("Invalid Username or Password");
		}
		return response.getEntity(String.class);
	}
	
	/**
	 * 
	 * @param auth
	 * @param url
	 * @param data
	 * @return
	 * @throws AuthenticationException
	 * @throws ClientHandlerException
	 */
	private String invokePostMethod(String auth, String url, String data) throws AuthenticationException, ClientHandlerException {
				
		WebResource webResource = client.resource(url);
		ClientResponse response = null;						
				
		if(oauthBased) {
			webResource.addFilter(getOauthFilter(client));
			response = webResource.type("application/json")
				.accept("application/json").post(ClientResponse.class, data);
		}else{
			response = webResource.header("Authorization", "Basic " + auth).type("application/json")
					.accept("application/json").post(ClientResponse.class, data);			
		}
		int statusCode = response.getStatus();
		if (statusCode == 401) {
			throw new AuthenticationException("Invalid Username or Password");
		}
		return response.getEntity(String.class);
		
	}
	

	/**
	 * For setting the OAuth Parameters
	 * @param client
	 * @return
	 */
	private ClientFilter getOauthFilter(Client client ) {
		OAuthSecrets secrets = new OAuthSecrets();
		secrets.consumerSecret(this.privatekey);
		OAuthParameters params = new OAuthParameters();
		params.consumerKey(consumerKey);
		params.setToken(auth);                
		params.setSignatureMethod( null!=signatureMethod ?signatureMethod: "RSA-SHA1");
		return new OAuthClientFilter(client.getProviders(), params, secrets);		
		
	}

	/**
	 * 
	 * @param auth
	 * @param url
	 * @param data
	 * @throws AuthenticationException
	 * @throws ClientHandlerException
	 */
	@SuppressWarnings("unused")
	private void invokePutMethod(String auth, String url, String data) throws AuthenticationException, ClientHandlerException {
		Client client = Client.create();
		WebResource webResource = client.resource(url);
		ClientResponse response = webResource.header("Authorization", "Basic " + auth).type("application/json")
				.accept("application/json").put(ClientResponse.class, data);
		int statusCode = response.getStatus();
		if (statusCode == 401) {
			throw new AuthenticationException("Invalid Username or Password");
		}
	}
	
	/**
	 * 
	 * @param auth
	 * @param url
	 * @throws AuthenticationException
	 * @throws ClientHandlerException
	 */
	@SuppressWarnings("unused")
	private void invokeDeleteMethod(String auth, String url) throws AuthenticationException, ClientHandlerException {
		Client client = Client.create();
		WebResource webResource = client.resource(url);
		long time =  System.currentTimeMillis();
		ClientResponse response = webResource.header("Authorization", "Basic " + auth).type("application/json")
				.accept("application/json").delete(ClientResponse.class);
		
		int statusCode = response.getStatus();
		if (statusCode == 401) {
			throw new AuthenticationException("Invalid Username or Password");
		}
	}
	
	/**
	 * Load the Search fields from the config file 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private Map getSearchFieldMap(){
		Properties properties = new Properties();
		try {
			ClassLoader classLoader = JiraMain.class.getClassLoader();
			File file = new File(classLoader.getResource("searchkeys.properties").getFile());
			InputStream inStream = new FileInputStream(file);
			properties.load(inStream);
			return  properties;
		} catch ( IOException e) {
			System.out.println("Search Field Map Not found : "+e.getMessage());
			return null;
		}
		
	}
}

