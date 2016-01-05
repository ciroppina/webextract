package it.ciroppina.testPurposes.controller;

import it.ciroppina.testPurposes.loaders.TXTFileLoader;
import it.ciroppina.trovaEntita.FindEntity;
import it.ciroppina.trovaEntita.Group;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

// The @Model stereotype is a convenience mechanism to make this a request-scoped bean that has an
// EL name
// Read more about the @Model stereotype in this FAQ:
// http://www.cdi-spec.org/faq/#accordion6
//@Model
@SessionScoped
@Named("textController")
public class TXTFileController {

    @Inject
    private FacesContext facesContext;

    @Produces
    @Named
    private String text;
    public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}

	@Produces
    @Named
    private static String current;
	public String getCurrent() {
		return current;
	}
	public void setCurrent(String current) {
		TXTFileController.current = current;
	}
	
	@Produces
    @Named
    private static String[] offsets;
	public String[] getOffsets() {
		return offsets;
	}
	public void setOffsets(String[] offsets) {
		TXTFileController.offsets = offsets;
	}

	@Produces
    @Named
    private static Map<String, Group> numbers;
	public Map<String, Group> getNumbers() {
		return numbers;
	}
	public void setNumbers(Map<String, Group> nums) {
		TXTFileController.numbers = nums;
	}
	
	@Produces
    @Named
    private static Map<String, Group> vehicles;
	public Map<String, Group> getVehicles() {
		return vehicles;
	}
	public void setVehicles(Map<String, Group> vs) {
		TXTFileController.vehicles = vs;
	}
	
	@Produces
    @Named
    private static Map<String, Group> people;
	public Map<String, Group> getPeople() {
		return people;
	}
	public void setPeople(Map<String, Group> people) {
		TXTFileController.people = people;
	}
	
	@Produces
    @Named
    private static javax.servlet.http.Part file;
	public javax.servlet.http.Part getFile() {
		return file;
	}
	public void setFile(javax.servlet.http.Part file) {
		TXTFileController.file = file;
	}
	
	@PostConstruct
    public void init() {
		setup();
    	this.text = TXTFileLoader.TEXT;
		if (current == null) TXTFileController.current ="";
		if (people == null) TXTFileController.people = new HashMap<String, Group>();
		if (vehicles == null) TXTFileController.vehicles = new HashMap<String, Group>();
		if (numbers == null) TXTFileController.numbers = new HashMap<String, Group>();
    }
	
	public void reset() {
		current = "";
		numbers = new HashMap<String, Group>();
		people = new HashMap<String, Group>();
		vehicles = new HashMap<String, Group>();
	}
	
	private static String CAPITALIZED_WORDS = null;
	private static String PHONE_NUMBERS = null;
	private static String VEHICLES = null;
	private static void setup() {
		String whereIAm = System.getProperty("user.dir");
		String regexFile = whereIAm + "/applconf/regex.txt";
		//debug: console.println("File of regex(es): " + regexFile);

		Properties regex = new Properties();
		try {
			regex.load(new FileInputStream(new File(regexFile)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		CAPITALIZED_WORDS = regex.getProperty("REGEX_03");
		PHONE_NUMBERS = regex.getProperty("REGEX_04");
		VEHICLES = regex.getProperty("REGEX_05");
		//debug: console.println("RegEx under test: " + CAPITALIZED_WORDS);
	}
    
    public void upload() {
    	try {
    		InputStream input = file.getInputStream();
    		TXTFileLoader.updateTEXT(input);
    	}
        catch (Exception e) {
        	facesContext.addMessage("",
        	new FacesMessage(FacesMessage.SEVERITY_ERROR,
				"Failure! UPLOAD: got something wrong",
				"UPLOAD: got something wrong") );
        	return;
        }
    	reset();
    	setup();
    	facesContext.addMessage("",
    	new FacesMessage(FacesMessage.SEVERITY_INFO,
			"Success! UPLOAD: got a text file",
			"UPLOAD: got a text file") );
    }

	public void receive(String what) throws Exception {
		people = null;
		current = what;
		if (what.contains("\n")) current = what.substring(0, what.indexOf("\n"));
		if (what.contains("\r")) current = what.substring(0, what.indexOf("\r"));

		Object[] ooo = TXTFileLoader.search(what).toArray();
		setOffsets( Arrays.copyOf(ooo, ooo.length, String[].class) );

		try {
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Found " + offsets.length, "Successful");
			facesContext.addMessage(null, m);
		} catch (Exception e) {
			String errorMessage = this.getRootErrorMessage(e);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					errorMessage, "Unsuccessful");
			facesContext.addMessage(null, m);
		}
	}
	
	public void extractPeople(long hm) {
		offsets = null;
		reset();
		if (this.text == null || this.text.length() < 10) return;
		if (people == null || people.size() < 1) {
			Long frequency = hm;
			FindEntity finder = new FindEntity(CAPITALIZED_WORDS, this.text);
			Map<String, Group> groups = finder.groupWithMinFrequencyOf(frequency);
			Map<String, Group> people = finder.people(groups);
			TXTFileController.people = people;
		}
	}

	public void extractNumbers(long hm) {
		offsets = null;
		reset();
		if (this.text == null || this.text.length() < 10) return;
		if (numbers == null || numbers.size() < 1) {
			Long frequency = hm;
			FindEntity finder = new FindEntity(PHONE_NUMBERS, this.text);
			Map<String, Group> groups = finder.groupWithMinFrequencyOf(frequency);
			Map<String, Group> nums = finder.numbers(groups);
			TXTFileController.numbers = nums;
		}
	}
	
	public void extractVehicles(long hm) {
		offsets = null;
		reset();
		if (this.text == null || this.text.length() < 10) return;
		if (vehicles == null || vehicles.size() < 1) {
			Long frequency = hm;
			FindEntity finder = new FindEntity(VEHICLES, this.text);
			Map<String, Group> groups = finder.groupWithMinFrequencyOf(frequency);
			Map<String, Group> vs = finder.vehicles(groups);
			TXTFileController.vehicles = vs;
		}
	}
	
	/**
     * utility method for exception message interception
     * @param e
     * @return
     */
	private String getRootErrorMessage(Exception e) {
	    // Default to general error message that registration failed.
	    String errorMessage = "Registration failed. See server log for more information";
	    if (e == null) {
	        // This shouldn't happen, but return the default messages
	        return errorMessage;
	    }
	
	    // Start with the exception and recurse to find the root cause
	    Throwable t = e;
	    while (t != null) {
	        // Get the message from the Throwable class instance
	        errorMessage = t.getLocalizedMessage();
	        t = t.getCause();
	    }
	    // This is the root cause message
	    return errorMessage;
	}

}
