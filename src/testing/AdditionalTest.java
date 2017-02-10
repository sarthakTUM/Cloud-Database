package testing;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import org.junit.Test;

import app_kvEcs.ECSKVstore;
import app_kvServer.ServerKVStore;
import common.messages.Manager;
import junit.framework.TestCase;

public class AdditionalTest extends TestCase {
	
	// TODO add your test cases, at least 3
	
	@Test
	public void testPutAndGet() {
	ServerKVStore.put("key1", "value1");
	String response=ServerKVStore.get("key1");
		assertTrue(response=="value1");
	}
	public void testPutUpdate() {
		ServerKVStore.put("key1", "value1");
		String beforeOverwrite=ServerKVStore.get("key1");
		ServerKVStore.put("key1", "value2");
		String afterOverwrite=ServerKVStore.get("key1");
		
			assertTrue(beforeOverwrite!=afterOverwrite);
		}
	public void testPutDelete() {
		ServerKVStore.put("key1", "value1");
		ServerKVStore.put("key1");
		String Response=ServerKVStore.get("key1");
		
			assertTrue(Response!="value1");
		}
	public void testRangeDelete() throws NoSuchAlgorithmException, UnsupportedEncodingException {
		int hashOfKey1,hashOfKey2;
		int begRange,EndRange;
		hashOfKey1=Manager.hash("key1");
		hashOfKey2=Manager.hash("key2");
		if (hashOfKey1>hashOfKey2)
		{
		  begRange=hashOfKey2;
		  EndRange=hashOfKey1;
		}
		else
		{
		 begRange=hashOfKey1;
		 EndRange=hashOfKey2;
		}
		ServerKVStore.put(begRange,EndRange);
		

		
			assertTrue(Response!="value1");
		}
}
