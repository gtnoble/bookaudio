package bookaudio;

import java.io.*;
import java.net.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

public class Audiobook {
	Document page;
	
	final String trackNodes = "li.track";
	ArrayList<String> trackUrls = new ArrayList<String>();
	
	final String titleNode = ".head > span";
	String title;
	
	final String authorNode = ".authors > a > span";
	String author;
	
	final String readersNode = "div.parameter-value[itemprop=\"readBy\"]";
	ArrayList<String> readers = new ArrayList<String>();
	
	
	
	public Audiobook(URL audiobookPageUrl) {
		try {
			page = Jsoup.connect(audiobookPageUrl.toString()).get();
		} catch (IOException e) {
			System.err.println("ERROR: could not retrieve web page.");
			e.printStackTrace();
		}
		
		author = page.select(authorNode).first().text();
		title = page.select(titleNode).first().text();
		
		Elements trackElements = page.select(trackNodes);
		
		for(Element trackElement : trackElements) {
			trackUrls.add("https:" + trackElement.attr("data-url"));
			
		}
		
		
	}
	
	public void download(Path baseDirectory, boolean overwrite) {
		int sequence = 1;
		
		for(String trackUrl : trackUrls) {
			Path directory = baseDirectory.resolve(author).resolve(title);
			String fileName = String.format("%s - %s - %d.mp3", author, title, sequence); //file name
			Path filePath = directory.resolve(fileName);
			sequence++;

			if (filePath.toFile().exists() && (! overwrite)) {
				System.out.println("File" + filePath + "exists, skipping.");
				continue;
			}
			
			System.out.println("Downloading: " + fileName);
			
			directory.toFile().mkdirs();
			
			URLConnection downloadConnection = null;
			try {
				downloadConnection = new URL(trackUrl).openConnection();
			} catch (MalformedURLException e1) {
				System.err.println("ERROR: malformed URL: " + trackUrl);
				e1.printStackTrace();
			} catch (IOException e1) {
				System.err.println("ERROR: could not connect to URL: " + trackUrl);
				e1.printStackTrace();
			}
			
			InputStream downloadStream = null;
			try {
				downloadStream = downloadConnection.getInputStream();
			} catch (IOException e) {
				System.err.println("ERROR: could not open download stream");
				e.printStackTrace();
			}
			
			try {
				Files.copy(downloadStream, filePath, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				System.err.println("ERROR: Could not open download target file: " + fileName);
				e.printStackTrace();
			}
			
		}
	}
	
	
}
