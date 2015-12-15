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
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Model;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

// The @Model stereotype is a convenience mechanism to make this a request-scoped bean that has an
// EL name
// Read more about the @Model stereotype in this FAQ:
// http://www.cdi-spec.org/faq/#accordion6
@Model
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
    private String current;
	
	public String getCurrent() {
		return current;
	}
	public void setCurrent(String current) {
		this.current = current;
	}
	
	@Produces
    @Named
    private String[] offsets;

	public String[] getOffsets() {
		return offsets;
	}
	public void setOffsets(String[] offsets) {
		this.offsets = offsets;
	}

	@Produces
    @Named
    private Map<String, Group> numbers;

	public Map<String, Group> getNumbers() {
		return numbers;
	}
	public void setNumbers(Map<String, Group> nums) {
		this.numbers = nums;
	}
	
	@Produces
    @Named
    private Map<String, Group> vehicles;

	public Map<String, Group> getVehicles() {
		return vehicles;
	}
	public void setVehicles(Map<String, Group> vs) {
		this.vehicles = vs;
	}
	
	@Produces
    @Named
    private Map<String, Group> people;

	public Map<String, Group> getPeople() {
		return people;
	}
	public void setPeople(Map<String, Group> people) {
		this.people = people;
	}
	
	@Produces
    @Named
    private javax.servlet.http.Part file; // +getter+setter
	public javax.servlet.http.Part getFile() {
		return file;
	}
	public void setFile(javax.servlet.http.Part file) {
		this.file = file;
	}
	
	@PostConstruct
    public void init() {
		setup();
    	this.text = TXTFileLoader.TEXT;
		this.current ="";
		this.people = new HashMap<String, Group>();
		this.vehicles = new HashMap<String, Group>();
		this.numbers = new HashMap<String, Group>();
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
    	init();
    	facesContext.addMessage("",
    	new FacesMessage(FacesMessage.SEVERITY_INFO,
			"Success! UPLOAD: got a text file",
			"UPLOAD: got a text file") );
    }

	public void receive() throws Exception {
		people = null;
		
		if (current.trim().length() < 1) {
			facesContext.addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Failure! MDM: nothing to search",
					"MDM: nothing to search"));
			return;
		}
		Object[] ooo = TXTFileLoader.search(current).toArray();
		offsets = Arrays.copyOf(ooo, ooo.length, String[].class);

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
	
	public void extractPeople() {
		offsets = null;
		//mock
		//this.people.put("De Filippis Gilda", ""+180L);
		//this.people.put("CARILLO Gianfranco", ""+79L);
		if (this.text == null || this.text.length() < 10) return;
		if (people == null || people.size() < 1) {
			Long frequency = 05L;
			FindEntity finder = new FindEntity(CAPITALIZED_WORDS, this.text);
			Map<String, Group> groups = finder.groupWithMinFrequencyOf(frequency);
			Map<String, Group> people = finder.people(groups);
			this.people = people;
		}
		return;
//		Iterator<String> iterator = people.keySet().iterator();
//		while (iterator.hasNext()) {
//			String k = iterator.next();
//			this.people.put(k, ""+people.get(k).getCount() );
//		}
	}

	public void extractNumbers() {
		offsets = null;

		if (this.text == null || this.text.length() < 10) return;
		if (numbers == null || numbers.size() < 1) {
			Long frequency = 01L;
			FindEntity finder = new FindEntity(PHONE_NUMBERS, this.text);
			Map<String, Group> groups = finder.groupWithMinFrequencyOf(frequency);
			Map<String, Group> nums = finder.numbers(groups);
			this.numbers = nums;
		}
		return;
	}
	
	public void extractVehicles() {
		offsets = null;

		if (this.text == null || this.text.length() < 10) return;
		if (vehicles == null || vehicles.size() < 1) {
			Long frequency = 01L;
			FindEntity finder = new FindEntity(VEHICLES, this.text);
			Map<String, Group> groups = finder.groupWithMinFrequencyOf(frequency);
			Map<String, Group> vs = finder.vehicles(groups);
			this.vehicles = vs;
		}
		return;
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
