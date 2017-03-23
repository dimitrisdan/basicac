package ac.services;
import java.util.ArrayList;
import java.util.List;

public class PrintServiceACL  {

	private List<String> printerQueue = new ArrayList<String>();
	private String quality;
	private String paper;
	private String color;
	private Boolean start;
	
	public PrintServiceACL(){
		printerQueue.add("Security in Computing 4th Edition.pdf");
		quality = "best";
		paper = "A4";
		color = "B&W";
		start = true;
	}
	
	public String print(String username,String filename, String printer) throws Exception{
		if (AuthenticationService.checkACL(username,"print"))
			return print(filename, printer);
		else
			throw new Exception("Access Denied");
	}
	private String print(String filename, String printer){
		printerQueue.add(filename);
		return "-> Print file "+filename+"...";
	}

	public String queue(String username) throws Exception{
		if (AuthenticationService.checkACL(username,"queue"))
			return queue();
		else
			throw new Exception("Access Denied");
	}
	private String queue(){
		return printerQueue.toString();
	}

	public Boolean topQueue(String username, int job) throws Exception{
		if (AuthenticationService.checkACL(username,"topqueue"))
			return topQueue(job);
		else
			throw new Exception("Access Denied");
	}
	private Boolean topQueue(int job){
		String serverMessage = "REQUEST topQueue()...";
		try{
			String item = printerQueue.get(job);
			printerQueue.remove(job);
			printerQueue.add(0,item);
			System.out.println(serverMessage.concat("DONE"));
		    return true;
		}catch(Exception e){
			System.out.println(serverMessage.concat("FAILED"));
			e.printStackTrace();
			return false;
		}
	}
	
	public Boolean start(String username) throws Exception{
		if (AuthenticationService.checkACL(username,"start"))
			return start();
		else
			throw new Exception("Access Denied");
	}
	private Boolean start(){
		if (start==true)
			return false;
		else
			start = true;
		return true;
	}

	public Boolean stop(String username)throws Exception{
		if (AuthenticationService.checkACL(username,"stop"))
			return stop();
		else
			throw new Exception("Access Denied");
	}
	public Boolean stop(){
		if (start == true)
			start = false;
		else
			return false;
		return true;
	}
	
	public Boolean restart(String username)throws Exception{
		if (AuthenticationService.checkACL(username,"restart"))
			return restart();
		else
			throw new Exception("Access Denied");
	}
	private Boolean restart(){
		if (start == true)
			start = false;
		else
			return false;
		start = true;
		return true;
	}

	public String status(String username)throws Exception{
		if (AuthenticationService.checkACL(username,"status"))
			return status();
		else
			throw new Exception("Access Denied");
	}
	private String status() {
		String clientMessage;
		try{
			if ( printerQueue == null && start == false) 
				clientMessage = "Printer queue is null...Please start the service";
			else if ( ( printerQueue != null ) && 
					( printerQueue.size() < 30 ) &&
					( start == true )) 
				clientMessage = "Printer is available...";
			else 
				clientMessage = "Printer is offline";
			return clientMessage;
		}catch(Exception e){
			clientMessage = "Disconnected...Try again";
			e.printStackTrace();
			return clientMessage;
		}
	}
	
	public String readConfig(String username, String parameter)throws Exception{
		if (AuthenticationService.checkACL(username,"readconfig"))
			return readConfig(parameter);
		else
			throw new Exception("Access Denied");
	}
	private String readConfig(String parameter) {
		if ( parameter.equals("quality"))	 return quality;
		else if ( parameter.equals("paper")) return paper;
		else if ( parameter.equals("color")) return color;
		else return "Invalid parameter";
	}
	
	public Boolean setConfig(String username, String parameter, String value)throws Exception{
		if (AuthenticationService.checkACL(username,"setconfig"))
			return setConfig(parameter,value);
		else
			throw new Exception("Access Denied");
	}
	private Boolean setConfig(String parameter, String value){
		if ( parameter.equals("quality"))	 quality = value;
		else if ( parameter.equals("paper")) paper = value;
		else if ( parameter.equals("color")) color = value;
		else return false;
		return true;
	}
}