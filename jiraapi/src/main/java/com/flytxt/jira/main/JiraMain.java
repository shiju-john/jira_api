package com.flytxt.jira.main;
 
import java.util.HashMap;
import java.util.Map;

import com.flytxt.jira.client.JiraApi;
import com.flytxt.jira.client.JiraImpl;
import com.flytxt.jira.client.exception.JiraException;

public class JiraMain {
	
	static String baseUrl = "https://flytxt.atlassian.net";
	static String privatekey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJc"
			+ "AgEAAoGBANs2reAaZEXiLhao"
		+ "K5JTE17WbMQS2MZuRJhAj2o/Dd3Ryi8X1beIgxIKVfNoOyPZX43NVJOWsHYCZQOw"
		+ "VNiDSTTTueqDACOmdK3fBIUW7iZvPMqL+TC5Tit4RNMvlDmD+FapglN+vIm1u0fA"
		+ "M2Z83hZo55aPIi3RCLUb/ZOa8UMpAgMBAAECgYANUOP9Af1nVVbxX2POLqe1khbn"
		+ "TjHG0/nsubc8f/oKqGy8sZj03iEAHoqcD4/QHKs2ROvHT1cGxBx4veRZhpgStaQP"
		+ "Dqe2u1xPF9cTR1QAsp8g++bh6QI5BGT+wrfEJSzbCDxsDQPHGeEstFAICatAGtTF"
		+ "T8BObAnybR1gEv8PEQJBAPJwWOFvFSInKTtiChxIVA2Gi81rPo2LKnOCl0v7++DC"
		+ "t56w0P1iOEJVXU18KsHbTQqSqoRfjdsY/m9UF3dbZwUCQQDnecDJEJ0kOlNvmcqg"
		+ "qpQofb73qpLxCtTdiBeCt4CE23OhVAgKJDrWWLIVz7YPCiHIgg4nl/qsaVl97Lx8"
		+ "IBzVAkAmQgBkosjs+M0S5+e8itVoxQCuy+u1Hm72h3cksIEQ+OlNC44PTj6eiSYO"
		+ "IjgFG3xO3NI3zXRvTMRqARUq6quZAkAfCuf8zvqUAjJwVAqlk1q9N2fl5P1B0DcN"
		+ "4pNsl1ln99pA93kAiy1M06ZGYI3E5JH1RuPJEYuvlY1H4vjAGLplAkEAheWuS00k"
		+ "XlO35HYvxLE4pB7s00j7zBdSINcNBuvBpApbDdf4geAoFaTvjnYD5g9wqo1oCEeC"
		+ "YEnJSpS1yhA/4w==";
	
	static String oauthToken = "alOEfokOqpwqWngGC1BB1ykxBlTH4tjF";	
	static String consumerKey = "googlepepper";
	static String signatureMethod ="RSA-SHA1";
	
	public static void main(String[] args) {	
	
		//for (int i=0;i<10;i++){
		Map<String,String> searchParameters = new  HashMap<>();		
		JiraApi api = JiraImpl.getInstance(baseUrl,oauthToken,consumerKey,signatureMethod,privatekey);
		//JiraClient client = JiraClient.getInstance(null, "arunkumar.thampi@flytxt.com", "aditianju@2012");
		//JiraClient client = JiraClient.getInstance(baseUrl,oauthToken,consumerKey,signatureMethod,privatekey);
		//searchParameters.put("assignee","=kartheek.gollanapalli@flytxt.com");
		//searchParameters.put("createdDate", ">-4w");		
		//searchParameters.put("key", "=WIN-7");
		//searchParameters.put("priority"," IN (High,low)");
		searchParameters.put("project", "='Neon X'");
		searchParameters.put("issueType", "=bug");
		searchParameters.put("status", "=open");
		try {			
		
			Map<String,Object> chartParameters = new HashMap<>();
			chartParameters.put("CHART_WIDTH",500);
			chartParameters.put("CHART_HEIGHT",300);
			chartParameters.put("CHART_IMAGE_PATH", "images");
			chartParameters.put("CHART_3D_ENABLED", false);
			chartParameters.put("CHART_TITLE","Neon X issue status");
			chartParameters.put("CHART_LEGEND_POSITION","RIGHT");
			chartParameters.put("CHART_IMAGE_EXTENSION","JPEG");
			// based on keys :  project ,issuetype ,assignee,priority,status,creator,reporter
			System.out.println(api.getChart("P","priority",searchParameters,100,chartParameters));
			
		} catch (JiraException e) {
			e.printStackTrace();
		}	
		}
	//}	

}
