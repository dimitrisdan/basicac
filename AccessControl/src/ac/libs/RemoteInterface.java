package ac.libs;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;


public interface RemoteInterface extends Remote{
	
	public List<String> buildMenuRBAC(String username) throws RemoteException;
	
	public List<String> buildMenuACL(String username) throws RemoteException;
	
	public Boolean validateUser(String username, String password) throws RemoteException, NoSuchAlgorithmException, InvalidKeySpecException;
	
	public String getAuthorizationName(String username)throws RemoteException;
	
}
