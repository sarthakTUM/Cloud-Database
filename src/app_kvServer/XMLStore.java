package app_kvServer;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javafx.util.Pair;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import common.messages.KVMessage.StatusType;
import common.messages.Manager;


public class XMLStore implements DataStoreWrapper{

	private static final String LOG = "LOG:XMLSTORE:";
	public static boolean PUT_STATUS;
	private static Node KVPair, rootelement = null; 
	private static NodeList KVPairs, KVPairChildren = null;
	private static StringBuilder returnBuilder;
	private static Document doc = null;
	private static DocumentBuilder docBuilder = null;
	private static String getresult = null;
	private  StatusType dbRequestStatus;

	@Override
	public List<Pair<String,String>> get(int startRange, int endRange) throws NoSuchAlgorithmException, UnsupportedEncodingException, DOMException
	{// pass  beginning and end hash range, and it gets all the values in that range in a list of key value pairs. can be used like list.get(index).getKey()/list.get(index).getValue()
		List<Pair<String,String>> list = new ArrayList<Pair<String,String>>();
		try {
			parseDatabase();
		} catch (SAXException | IOException | ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setStatus(StatusType.GET_ERROR);

		}
		if(rootelement==null){
			getresult = "Key does not exist!!";
		}
		else{
			boolean novalue = true;
			KVPairs = rootelement.getChildNodes();
			for(int i = 0; i < KVPairs.getLength(); i++)
			{

				KVPair = KVPairs.item(i);
				KVPairChildren = KVPair.getChildNodes();
				for (int y = 0; y < KVPairChildren.getLength()-1; y++) 
				{
					//if(key.equals(KVPairChildren.item(y).getTextContent()))
					String key =KVPairChildren.item(y).getTextContent();
					int hashedKey=Manager.hash(key);
					if((hashedKey>=startRange) && (hashedKey<=endRange))
					{

						String Value=KVPairChildren.item(y).getNextSibling().getTextContent();

						// found existing key
						list.add(new Pair(key,Value));
						novalue = false;
						setStatus(StatusType.GET_SUCCESS);






					}
				}

				if(novalue){
					getresult = "The key you are looking for does not exist, please try again!";
					setStatus(StatusType.GET_ERROR);
				}

			}


		}return list;
	}
	@Override
	public synchronized DatabaseResponse put(String key, String value) {
		// TODO Auto-generated method stub
		System.out.println(LOG + "processing PUT : " + key + ":" + value);
		boolean isPutSuccess = false;
		DatabaseResponse databaseResponse = null;
		try {
			parseDatabase();
		} catch (SAXException | IOException | ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setStatus(StatusType.PUT_ERROR);
			isPutSuccess = false;
		}

		if(rootelement==null){
			// DB does not exist; creating root element
			doc = docBuilder.newDocument();
			rootelement = doc.createElement("KVStore");
			doc.appendChild(rootelement);
			makeNewEntry(key, value);
			setStatus(StatusType.PUT_SUCCESS);
			isPutSuccess = true;
		}

		else{
			boolean makenewentry = true;
			KVPairs = rootelement.getChildNodes();

			for(int i = 0; i < KVPairs.getLength(); i++)
			{

				KVPair = KVPairs.item(i);
				KVPairChildren = KVPair.getChildNodes();
				for (int y = 0; y < KVPairChildren.getLength(); y++) 
				{
					if(key.equals(KVPairChildren.item(y).getTextContent()))
					{
						makenewentry = false;
						// modify existing key value
						KVPairChildren.item(y).getNextSibling().setTextContent(value);
						setStatus(StatusType.PUT_UPDATE);
						isPutSuccess = true;
					}

				}
			}

			if(makenewentry){
				makeNewEntry(key, value);
				setStatus(StatusType.PUT_SUCCESS);
				isPutSuccess = true;
			}
		}


		try {
			writeToPersistentStorage();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setStatus(StatusType.PUT_ERROR);
			isPutSuccess = false;
		}
		databaseResponse = new DatabaseResponse();
		databaseResponse.setReuqestType(DBCommand.PUT);
		if(isPutSuccess){
			System.out.println(LOG + "put success : " + key + ":" + value);
			databaseResponse.setResult(DBRequestResult.SUCCESS);
		}
		else{
			System.out.println(LOG + "put success : " + key + ":" + value);
			databaseResponse.setResult(DBRequestResult.FAIL);
		}
		databaseResponse.setMessage(null);
		databaseResponse.setStatus(getStatus());
		databaseResponse.setKey(key);
		databaseResponse.setValue(value);
		return databaseResponse;
	}


	@Override
	public synchronized DatabaseResponse get(String key) {
		// TODO Auto-generated method stub

		boolean getSuccess = false;
		DatabaseResponse databaseResponse = new DatabaseResponse();
		try {
			parseDatabase();
		} catch (SAXException | IOException | ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setStatus(StatusType.GET_ERROR);

		}
		if(rootelement==null){
			getresult = "Key does not exist!!";
			getSuccess = false;
		}
		else{
			boolean novalue = true;
			KVPairs = rootelement.getChildNodes();
			for(int i = 0; i < KVPairs.getLength(); i++)
			{

				KVPair = KVPairs.item(i);
				KVPairChildren = KVPair.getChildNodes();
				for (int y = 0; y < KVPairChildren.getLength(); y++) 
				{
					if(key.equals(KVPairChildren.item(y).getTextContent()))
					{
						// found existing key
						novalue = false;
						setStatus(StatusType.GET_SUCCESS);
						getresult = "KEY: " + key + " VALUE: "+ KVPairChildren.item(y).getNextSibling().getTextContent();
						databaseResponse.setValue(KVPairChildren.item(y).getNextSibling().getTextContent());
						getSuccess = true;
						break;
					}

				}
			}

			if(novalue){
				getresult = "The key you are looking for does not exist, please try again!";
				setStatus(StatusType.GET_ERROR);
				getSuccess = false;
			}

		}

		databaseResponse.setReuqestType(DBCommand.GET);
		if(getSuccess){
			databaseResponse.setResult(DBRequestResult.SUCCESS);
		}
		else{
			databaseResponse.setResult(DBRequestResult.FAIL);
		}
		databaseResponse.setMessage(getresult);
		databaseResponse.setStatus(getStatus());
		databaseResponse.setKey(key);


		return databaseResponse;
	}

	private static void parseDatabase() throws SAXException, IOException, ParserConfigurationException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		docBuilder = docFactory.newDocumentBuilder();
		doc = docBuilder.parse("KVStore\\Store.xml");
		rootelement = doc.getFirstChild();

	}

	private void makeNewEntry(String key, String value) {
		// create KVPair
		Element KVPair = doc.createElement("KVPair");
		rootelement.appendChild(KVPair);

		// Create and insert Key
		Element KVkey = doc.createElement("Key");
		KVkey.appendChild(doc.createTextNode(key));
		KVPair.appendChild(KVkey);

		// Create and insert Value
		Element KVvalue = doc.createElement("Value");
		KVvalue.appendChild(doc.createTextNode(value));
		KVPair.appendChild(KVvalue);
		System.out.println("created entry");

	}

	public void writeToPersistentStorage() throws TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File("KVStore\\Store.xml"));

		// Output to console for testing
		// result = new StreamResult(System.out);

		transformer.transform(source, result);

		System.out.println("Server Persistent Storage Updated!");



	}

	public StatusType getStatus() {
		return this.dbRequestStatus;
	}
	public void setStatus(StatusType dbRequestStatus) {
		this.dbRequestStatus = dbRequestStatus;
	}


}
