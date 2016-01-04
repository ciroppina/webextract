package it.ciroppina.testPurposes.loaders;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TXTFileLoader {
	
	public static String TEXT;
	public static final PrintStream console = System.out;
	
	static {
		File f = new File(System.getProperty("user.dir") + "/src/main/resources/194.txt");
		FileInputStream in = null;
		try {
			in = new FileInputStream(f);
		} catch (FileNotFoundException e) {
			f = new File(System.getProperty("user.dir") + "/applconf/194.txt");
			try {
				in = new FileInputStream(f);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
		}
		//CIRO: nessun test iniziale: updateTEXT(in);
	}

	public static void main(String[] args) {}
	
	public static void updateTEXT(InputStream aStream) {
		byte[] b;
		try {
			b = new byte[aStream.available()];
			aStream.read(b);
			aStream.close();
			TEXT = new String(b, StandardCharsets.UTF_8);
			/**
			 * MOST Important: before using in JavaScript functions
			 */
			TEXT = TEXT.replace("\r\n", "\r");
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	public static List<String> search(String entity) {
		List<String> offsets = new ArrayList<String>();
		if (entity.trim().length() < 1) {
		} else {
			Matcher matcher = Pattern.compile(
				"\\b*" + entity.trim().toUpperCase() 
				+ "\\b*|\\s*" + entity.trim().toUpperCase() +"\\s*")
				.matcher(TXTFileLoader.TEXT.toUpperCase());
			while (matcher.find()) {
				String k = matcher.group(0).trim();
				k = k.trim().toUpperCase().replace("\r\n", " ")
						.replace("  ", " ");

				offsets.add(matcher.start() + ":" + matcher.end());
			}
		}
		return offsets;
	}
}
