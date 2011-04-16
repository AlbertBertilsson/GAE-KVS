package nu.albert.gaekvs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import com.google.appengine.repackaged.com.google.common.util.Base64;

public class HTTPHelp {
	
	public static int get(String url, StringBuilder sb) throws Exception {

        URL surl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) surl.openConnection();
        connection.setDoInput(true);
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestMethod("GET");

        sb.setLength(0);
        int rc = connection.getResponseCode();
        if (rc == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));

            int i;
            while ((i = reader.read()) != -1) {
                sb.append((char)i);
            }
            reader.close();
        }
        
        return rc;
	}

	public static int get(String url, StringBuilder sb, String user, String password) throws Exception {

        URL surl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) surl.openConnection();
        connection.setDoInput(true);
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        String cred = user + ":" + password;
        connection.setRequestProperty  ("Authorization", "Basic " + Base64.encode(cred.getBytes()));
        connection.setRequestMethod("GET");

        sb.setLength(0);
        int rc = connection.getResponseCode();
        if (rc == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));

            int i;
            while ((i = reader.read()) != -1) {
                sb.append((char)i);
            }
            reader.close();
        }
        
        return rc;
	}

	public static int delete(String url, String user, String password) throws Exception {

        URL surl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) surl.openConnection();
        connection.setDoInput(true);
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        String cred = user + ":" + password;
        connection.setRequestProperty  ("Authorization", "Basic " + Base64.encode(cred.getBytes()));
        connection.setRequestMethod("DELETE");

        int rc = connection.getResponseCode();
        return rc;
	}

	public static int delete(String url) throws Exception {

        URL surl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) surl.openConnection();
        connection.setDoInput(true);
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestMethod("DELETE");

        int rc = connection.getResponseCode();
        return rc;
	}

	
	public static int put(String url, String s) throws Exception {

        URL surl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) surl.openConnection();
        connection.setInstanceFollowRedirects(false);
        connection.setDoOutput(true);
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestMethod("PUT");

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        for (char c : s.toCharArray()) {
        	writer.append(c);
        }
        writer.close();
        
        connection.getInputStream();
        
        return connection.getResponseCode();
	}

	
	public static int put(String url, String user, String password, String s) throws Exception {

        URL surl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) surl.openConnection();
        connection.setInstanceFollowRedirects(false);
        connection.setDoOutput(true);
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        String cred = user + ":" + password;
        connection.setRequestProperty  ("Authorization", "Basic " + Base64.encode(cred.getBytes()));
        connection.setRequestMethod("PUT");

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        for (char c : s.toCharArray()) {
        	writer.append(c);
        }
        writer.close();
        
        connection.getInputStream();
        
        return connection.getResponseCode();
	}
}
