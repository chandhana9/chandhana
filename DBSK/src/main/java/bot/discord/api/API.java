package bot.discord.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;

import javax.naming.directory.SearchResult;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpRequest;
import org.w3c.dom.Document;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;

public class API {

	public String requestAPI(String URL) throws IOException {
		String body = "";
		URL url = new URL(URL);
		URLConnection con = url.openConnection();
		InputStream in = con.getInputStream();
		String encoding = con.getContentEncoding();
		encoding = encoding == null ? "UTF-8" : encoding;
		body = IOUtils.toString(in, encoding);
		return body;
	}
	
	public String encode(String arg0) throws UnsupportedEncodingException {
		return URLEncoder.encode(arg0,"UTF-8");
	}
	
	public String getWeather(String city, String country, int reportNum) throws Exception{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(new URL("http://api.openweathermap.org/data/2.5/forecast?q=burnaby&mode=xml&units=metric&appid=191b7c0599a63f6262a1cf6807772d70").openStream());
		
		if(reportNum > (doc.getElementsByTagName("time").getLength() - 1) || reportNum < 0) {
			return ("Forecast report number must be within 0-" + (doc.getElementsByTagName("time").getLength() - 1));
		}
		
		if(country.equals("")) {
			doc = db.parse(new URL("http://api.openweathermap.org/data/2.5/forecast?q="+city+"&mode=xml&units=metric&appid=191b7c0599a63f6262a1cf6807772d70").openStream());
		}else {
			doc = db.parse(new URL("http://api.openweathermap.org/data/2.5/forecast?q="+city+","+country+"&mode=xml&units=metric&appid=191b7c0599a63f6262a1cf6807772d70").openStream());
		}
		String location = doc.getElementsByTagName("name").item(0).getTextContent();
		String iso3166 = doc.getElementsByTagName("country").item(0).getTextContent();
		
		String timeInit = doc.getElementsByTagName("time").item(reportNum).getAttributes().getNamedItem("from").getTextContent();
		String timeFinal = doc.getElementsByTagName("time").item(reportNum).getAttributes().getNamedItem("to").getTextContent();
		String time = " from " + timeInit + " to " + timeFinal;
		
		String weather = doc.getElementsByTagName("symbol").item(reportNum).getAttributes().getNamedItem("name").getTextContent();
		
		String windDirection = doc.getElementsByTagName("windDirection").item(reportNum).getAttributes().getNamedItem("name").getTextContent();
		double windSpeed = Double.parseDouble(doc.getElementsByTagName("windSpeed").item(reportNum).getAttributes().getNamedItem("mps").getTextContent()) * 1.6;
		
		double temp = Double.parseDouble(doc.getElementsByTagName("temperature").item(reportNum).getAttributes().getNamedItem("value").getTextContent());
		
		String humidity = doc.getElementsByTagName("humidity").item(reportNum).getAttributes().getNamedItem("value").getTextContent();
		
		String forecast = "Here is the weather report for " + location  + "," + iso3166 + time + ":"
				+ "\nWeather: " + weather.substring(0,1).toUpperCase() + weather.substring(1, weather.length())
						+ "\nThe wind is blowing " + windDirection + " at " + ("" + windSpeed).substring(0, 5) + " kilometers per hour, or " + ("" + (windSpeed/1.6)).substring(0,Math.min(5, ("" + (windSpeed/1.6)).length())) + " miles per hour."
								+ "\nThe temperature outside is " + temp + "°C or " + ((temp * 1.8) + 32) + "°F"
										+ "\nThe humidity is at " + humidity + "% today.";
		
		return forecast;
	}
	
	public Document requestXMLAPI(String URL) throws Exception{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(new URL(URL).openStream());

		return doc;
	}
	
	public static String searchYoutube(String keyword, String APIKey) throws Exception{
		HttpTransport transport = new ApacheHttpTransport();
		GsonFactory gf = new GsonFactory();
		YouTube youtube = new YouTube.Builder(transport, gf, new HttpRequestInitializer() {
            public void initialize(HttpRequest request) throws IOException {
            }

			@Override
			public void initialize(com.google.api.client.http.HttpRequest arg0) throws IOException {
				// TODO Auto-generated method stub
				
			}
        }).setApplicationName("youtube-cmdline-search-sample").build();
		
		String searchWord = keyword;
		
		YouTube.Search.List search = youtube.search().list("id,snippet");
        search.setKey(APIKey);
        search.setQ(searchWord);
        
        search.setType("video");
        search.setMaxResults(1L);
        
        SearchListResponse searchResponse = search.execute();
        List<com.google.api.services.youtube.model.SearchResult> searchResultList = searchResponse.getItems();
        if(searchResultList != null) {
        	com.google.api.services.youtube.model.SearchResult singleVideo = searchResultList.get(0);
            ResourceId rId = singleVideo.getId();
            if (rId.getKind().equals("youtube#video")) {
                return "https://www.youtube.com/watch?v=" + rId.getVideoId();
            }
        }
        return "No videos found.";
        
	}
}
