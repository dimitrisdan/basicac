package ac.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import ac.libs.RemoteInterface;
import ac.libs.PasswordHash;
import ac.services.PrintServiceRBAC;

public class PrintClientRBAC {
	
	private static String readFromKeyboard() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String s = br.readLine();
        return s;
	}
	
	private static String readPassword(String username) throws IOException {
		try {
			String clientSalt = username+"datasecurity";
			String hashedPassword = PasswordHash.createHashFromClient(readFromKeyboard(),clientSalt);
			return hashedPassword;
		} catch (NoSuchAlgorithmException e) {
			System.out.println("NoSuchAlgorithmException while reading a password");
			return null;
		} catch (InvalidKeySpecException e) {
			System.out.println("InvalidKeySpecException while reading a password");
			return null;
		}	
	}

	public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException{
		
		String RMI_URL = "rmi://localhost:44444/print";
		String username;
		String welcome = "PrintClient\n--------------------\nPlease enter your credentials(max 3 attempts)";
		int attempts = 0;
		int remAttempts = 3;


		System.out.println("*******************************************************\n"
						 + "* Users:\n"
						 + "* alice,bob,cecila,david,erica,george\n"
						 + "* For passwords enter the same string e.g. alice->alice\n"
						 + "*******************************************************\n");
		
		try {
			RemoteInterface rmt = (RemoteInterface) Naming.lookup(RMI_URL);
			
			try {
				System.out.println(welcome);
				
				do{
					
					System.out.print("Username: ");
					username = readFromKeyboard();
					System.out.print("Password: ");
					
					if(rmt.validateUser(username,readPassword(username))){
						
						remAttempts = 3;
						attempts = 0;
						System.out.print("\nWelcome "+username+"!\nAuthorization: "+rmt.getAuthorizationName(username));
						List<String> menu = (rmt.buildMenuRBAC(username));
						Boolean exit = false;
						PrintServiceRBAC prnt = new PrintServiceRBAC();
						
						do{
							
							System.out.println("\nMENU\n--------------------");
							
							for(int i=0;i<menu.size();i++)
								System.out.println(menu.get(i));
							
							System.out.print("Option: ");
							int option = Integer.valueOf(readFromKeyboard());
							
							String parameter;
							
							try{
								
								switch (option) {
							    	
							    	case 0:
							    		exit = true;
							    		attempts = 0;
							    		break;
							    	
							    	case 1:  
							    		System.out.print("Print file:");
							    		String filename = readFromKeyboard();
							    		System.out.print("Printer:");
							    		String printer = readFromKeyboard();
							    		System.out.println("\n"+prnt.print(username, filename, printer));
						                break;
						            
						            case 2:  
						            	System.out.println("\nPrinter Queue:\n"
						            					  + prnt.queue(username)+"\n");
						                break;
						            
						            case 3:  
						            	System.out.println("Move to the top:");
							    		String job = readFromKeyboard();
						            	if(prnt.topQueue(username, Integer.valueOf(job)))
						            		System.out.println("\nPushed to the top of the queue\n");
						            	else
						            		System.out.println("\nFailed to push\n");
						                break;
						            
						            case 4:
						            	if(prnt.start(username))
						            		System.out.println("Service started...");
						            	else
						            		System.out.println("\nFailed to start\n");
						            	break;
						            
						            case 5:
						            	prnt.stop(username);
						            	System.out.println("Service stopped...");
						                break;
						            
						            case 6:
						            	prnt.restart(username);
						            	System.out.println("Service restarted...");
						                break;
						            
						            case 7:
						            	System.out.println("Status:");
						            	System.out.println(prnt.status(username));
						                break;
						            
						            case 8:
						            	System.out.println("Parameter(quality,paper,color):");
							    		parameter = readFromKeyboard();
							    		System.out.println(prnt.readConfig(username,parameter));
						                break;
						            
						            case 9:
						            	System.out.println("Parameter(quality,paper,color):");
						            	parameter = readFromKeyboard();
						            	System.out.println("Value:");
						            	String value = readFromKeyboard();
						            	prnt.setConfig(username,parameter, value);
						                break;
						            
						            case 10:
							    		exit = true;
							    		attempts = 3;
							    		break;
						            
						            default:
						            	System.out.println("Invalid option");
						                break;
						        }
							}catch(Exception e){
								System.out.println(e.getMessage());
							}
							
						}while(!exit);
					
					}else
						System.out.println("\nWrong username or password!!\n");
					attempts++;
					remAttempts--;
					
					if(remAttempts==0)
						System.out.println("Sorry :( Bye Bye");
					else
						System.out.println("--------------------\nPlease enter your credentials("+remAttempts+" attempt(s) remaining)");
				
				}while(attempts<3);
			
			} catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
				System.out.println("Exception while login a user");
			}
			
		} catch (MalformedURLException | RemoteException | NotBoundException e1) {
			System.out.println("RMIException");
		}
	}
}