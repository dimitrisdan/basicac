package ac.server;

import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import ac.services.AuthenticationService;

public class PrintServer {

	public static void main(String[] args) throws AccessException, RemoteException {
		Registry registry = null;
		try{
			registry = LocateRegistry.createRegistry(44444); //use any no. less than 55000
		}catch(RemoteException e){
			System.out.println("Error creating Registry...");
	    }
		System.out.println("** Print Server \n** All the Jedi members are valid to use...\n** Waiting for requests");
		
		registry.rebind("print", new AuthenticationService());
	}
	

}
