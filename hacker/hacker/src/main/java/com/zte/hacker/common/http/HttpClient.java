package com.zte.hacker.common.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

public class HttpClient {

	public static String request(String url) throws IOException {
		
		PrintWriter out = null;  
        BufferedReader in = null;  
        String result = "";  

        try{
        	URL u = new URL(url);
    		URLConnection urlConnection = u.openConnection();
    		urlConnection.setConnectTimeout(3000);
    		urlConnection.setReadTimeout(3000);
    		
    		urlConnection.setRequestProperty("accept", "*/*");  
    		urlConnection.setRequestProperty("connection", "Keep-Alive");  
    		urlConnection.setDoOutput(true);  
            urlConnection.setDoInput(true); 
            
            out = new PrintWriter(urlConnection.getOutputStream());  
            out.flush();  
            in = new BufferedReader(  
                    new InputStreamReader(urlConnection.getInputStream()));  
            String line;  
            while ((line = in.readLine()) != null) {  
                result += line;  
            } 
        }
        finally {
        	try {  
                if (out != null) {  
                    out.close();  
                }  
                if (in != null) {  
                    in.close();  
                }  
            } catch (IOException ex) {  
                ex.printStackTrace();  
            }  
		}
		
        return result;
	}

}
