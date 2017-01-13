package common.messages;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
public class Manager {
	

	
		
		
		public static int hash(String node) throws NoSuchAlgorithmException,UnsupportedEncodingException
	    {
	    	MessageDigest md5 = MessageDigest.getInstance("MD5");
	    	byte[] checksum = md5.digest(node.getBytes("UTF-8"));
	    	return Math.abs((new BigInteger(1, checksum).intValue()));
	    }
	}


