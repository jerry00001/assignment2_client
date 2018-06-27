package introsde.rest.client;


import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.Transformer;

import com.cedarsoftware.util.io.JsonWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class Client  {
	
static{
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
        System.setProperty("current.date.time", dateFormat.format(new Date()));
    }

	private final static String USER_AGENT = "Mozilla/5.0";
	private static int first_person_id;
	private static int last_person_id;
	private static int total_person;
	private static NodeList activityTypeNodeList =null;
	private static int activity_id;
	private static String activity_type;


	public static void main(String[] args) throws Exception{
		SysStreamsLogger.bindSystemStreams();
		

		URL BaseURL=new URL("https://assignment2-server.herokuapp.com");
		
		System.out.println("Server URL:  " + BaseURL );
		
		//2 Accept:APPLICATION/XML
		System.out.println("\nSTEP 3.1 XML => Request #1 GET BASE_URL/person Accept: APPLICATION/XML");
		HttpURLConnection conn=getConnection(BaseURL.toString()+"/person");
		getCountXML(conn);
		
		//3.1 Accept:APPLICATION/JSON
		System.out.println("\nSTEP 3.1 JSON => Request #1 GET BASE_URL/person Accept: APPLICATION/JSON\n");
		conn=getConnection(BaseURL.toString()+"/person");
		getCountJSON(conn);
		
		//3.2 Accept:APPLICATION/XML
		System.out.println("\n\n\nSTEP 3.2 XML => Request #2 GET /person/"+first_person_id +" Accept: APPLICATION/XML\n");
		conn=getConnection(BaseURL.toString()+"/person/"+first_person_id);
		getPersonsXML(conn);
		
		//3.2 Accept:APPLICATION/JSON
		System.out.println("\nSTEP 3.2 JSON => Request #2 GET /person/"+first_person_id +" Accept: APPLICATION/JSON\n");
		conn=getConnection(BaseURL.toString()+"/person/"+first_person_id);
		System.out.println(JsonWriter.formatJson(getStringJSON(conn).toString()));
		
		//3.3 Accept:APPLICATION/XML Content-type:APPLICATION/XML
		System.out.println("\n\n\nSTEP 3.3 XML => Request #3 PUT /person/"+first_person_id +" Accept:APPLICATION/XML Content-type:APPLICATION/XML\n");
		conn=getConnection(BaseURL.toString()+"/person/"+first_person_id);
		conn.setRequestProperty("Accept", "application/xml");
		conn.setRequestProperty("Content-type", "application/xml");
		String urlParameters="<person> <firstname>PersonSixth</firstname> </person>";
		updatePerson(conn, urlParameters);
		System.out.println("\nRequest# 2 Get the updated Person GET /person/"+first_person_id +" Accept: APPLICATION/XML\n");
		conn=getConnection(BaseURL.toString()+"/person/"+first_person_id);
		getPersonsXML(conn);
		
		//3.3 Accept:APPLICATION/JSON Content-type:APPLICATION/JSON
		System.out.println("\nSTEP 3.3 JSON => Request #3 PUT /person/"+first_person_id +" Accept:APPLICATION/JSON Content-type:APPLICATION/JSON\n");
		conn=getConnection(BaseURL.toString()+"/person/"+first_person_id);
		conn.setRequestProperty("Accept", "application/json");
		conn.setRequestProperty("Content-type", "application/json");
		urlParameters="{\"firstname\": \"Firstman\"}";
		updatePerson(conn, urlParameters);
		System.out.println("\nRequest# 2 Get the updated Person GET /person/"+first_person_id +" Accept: APPLICATION/XML\n");
		conn=getConnection(BaseURL.toString()+"/person/"+first_person_id);
		getPersonsXML(conn);
		
		//3.4 XML
		System.out.println("\n\n\nSTEP 3.4 XML => Request #4 POST /person Accept:APPLICATION/XML Content-type:APPLICATION/XML\n");
		conn=getConnection(BaseURL.toString()+"/person");
		String parms="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			      +  "\n <person> "
			      +  "\n<firstname>PersonSixth</firstname>"  
			      +  "\n <lastname>xyzSixth</lastname>"  
			      +  "\n <birthdate>1989-04-29</birthdate>"  
			      +  " \n<activity> " 
			      +  "\n     <name>PingPong</name>"
			      +  "\n     <description>Playig ping pong</description>"
			      +  "\n     <place>SportsComplex</place>"
			      +  "\n    <startdate>2016-14-13T11:40:00.0</startdate>"
			      +  "\n </activity>"
			      +  "\n</person>";
		Element personElement = (Element) doPostPutXML(conn,parms,"POST").getElementsByTagName("person").item(0);
		int newId=Integer.parseInt( personElement.getAttribute("person_id"));
		
		//3.4 JSON
		System.out.println("\nSTEP 3.4 JSON => Request #4 POST /person Accept:APPLICATION/JSON Content-type:APPLICATION/JSON\n");
		conn=getConnection(BaseURL.toString()+"/person");
		parms="{\n" + 
				"        \"lastname\": \"michael1\",\n" + 
				"        \"firstname\": \"george9\",\n" + 
				"        \"birthdate\": \"1980-06-20\",\n" + 
				"        \"activity\": [\n" + 
				"            {\n" + 
				"                \"name\": \"Meeting\",\n" + 
				"                \"description\": \"business meeting\",\n" + 
				"                \"place\": \"Torino\",\n" + 
				"                \"type\": \"Persistence\",\n" + 
				"                \"startdate\": \"2018-01-15T09:00:00.0\"\n" + 
				"            }\n" + 
				"        ]\n" + 
				"    }";
		doPostPutJSON(conn,parms,"POST");
		
		//3.5
		System.out.println("\n\n\nStep#3.5 Request #5 DELETE /person/"+newId +" Accept:APPLICATION/XML Content-type:APPLICATION/XML\n");
		conn=getConnection(BaseURL.toString()+"/person/"+newId);
		deletePerson(conn);
		System.out.println("=> Request #2 GET /person/"+newId +" Accept: APPLICATION/XML if it return 404 result is OK\n");
		conn=getConnection(BaseURL.toString()+"/person/"+newId);
		if(getPersonsXML(conn)==404) 
			System.out.println("=> Result: OK ");
		
		//3.6 Accept:APPLICATION/XML
		System.out.println("\n\n\nStep 3.6 XML => Request #6 GET /activity_types Accept:APPLICATION/XML\n");
		conn=getConnection(BaseURL.toString()+"/activity_types");
		activityTypeNodeList=getListXML(conn,2,"activity_Type");
		
		//3.6 Accept:APPLICATION/JSON
		System.out.println("\n\n\nStep 3.6 JSON => Request #6 GET /activity_types Accept:APPLICATION/JSON\n");
		conn=getConnection(BaseURL.toString()+"/activity_types");
		getActivityTypesJSON(conn,2);
		
		//3.7 Accept:APPLICATION/XML
		System.out.println("\n\n\nStep# 3.7 XML => Request#7 (GET BASE_URL/person/{id}/{activity_type})"
				+ "\n=> check if person have at least one Activity\n");
		int flag =0;
		List<Integer> ids= new LinkedList<>();
		ids.add(first_person_id);
		ids.add(last_person_id);
		for(Integer id: ids) {
			for (int i = 0; i < activityTypeNodeList.getLength(); i++) {
				Node nNode = activityTypeNodeList.item(i);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					conn=getConnection(BaseURL.toString()+"/person/"+id+"/"+  nNode.getTextContent());
					NodeList list=getListXML(conn,1,"activity");
					if(list!=null) {
						flag=1;
						for (int j = 0; j < list.getLength(); j++) {
							Node n = list.item(j);
							if (n.getNodeType() == Node.ELEMENT_NODE) {
								Element act = (Element) n;
								activity_id=Integer.parseInt(act.getAttribute("activity_id"));
								activity_type=act.getElementsByTagName("type").item(0).getTextContent();
								
							}
						}
								
					}
				}
			}
			if(flag==1) {
				System.out.println("=> Result: OK => Person with ID:"+id+" have atleast one activity type\n");
				flag=0;
			}
			else {
				System.out.println("=> ERROR");
			}
		}
		
		//3.7 Accept:APPLICATION/JSON
		System.out.println("\n\n\nStep# 3.7 JSON => Request#7 (GET BASE_URL/person/{id}/{activity_type})"
				+ "\n=> Check if person have at least one Activity\n");
		flag =0;
		for(Integer id: ids) {
			for (int i = 0; i < activityTypeNodeList.getLength(); i++) {
				Node nNode = activityTypeNodeList.item(i);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					conn=getConnection(BaseURL.toString()+"/person/"+id+"/"+  nNode.getTextContent());
					if(getActivityTypesJSON(conn,1)!=null) {
						flag=1;
					}
				}
			}
			if(flag==1) {
				System.out.println("=> Person with ID:"+id+" have atleast one activity type\n");
				flag=0;
			}
			else {
				System.out.println("=> ERROR");
			}
		}
		
		//3.8 XML 
		System.out.println("\n\n\nStep 3.8 XML =>Request #8 GET /person/"+last_person_id+"/"+activity_type+"/"+activity_id+" Accept:APPLICATION/XML\n");
		conn=getConnection(BaseURL.toString()+"/person/"+last_person_id+"/"+activity_type+"/"+activity_id);
		getListXML(conn,1,"activity");
		
		//3.8 JSON
		System.out.println("\nStep 3.8 JSON =>Request #8 GET /person/"+last_person_id+"/"+activity_type+"/"+activity_id+" Accept:APPLICATION/JSON\n");
		conn=getConnection(BaseURL.toString()+"/person/"+last_person_id+"/"+activity_type+"/"+activity_id);
		getActivityTypesJSON(conn,0);
		
		
		
		
		//3.9 XML

		System.out.println("\n\nStep# 3.9 XML => Request #9 => POST /person/"
				+first_person_id
				+"/"+activityTypeNodeList.item(2).getTextContent()
				+" Accept:APPLICATION/XML Content-Type:APPLICATION/XML\n");
		conn=getConnection(BaseURL.toString()+"/person/"+first_person_id+"/"+activityTypeNodeList.item(2).getTextContent());
		parms=activityTypeNodeList.item(0).getTextContent();
		doPostPutXML(conn,parms,"POST");

		
		System.out.println("Step# 3.9  JSON => Request #9 => POST /person/"
				+first_person_id
				+"/"+activityTypeNodeList.item(0).getTextContent()
				+" Accept:APPLICATION/JSON Content-Type:APPLICATION/JSON");
		conn=getConnection(BaseURL.toString()+"/person/"+first_person_id+"/"+activityTypeNodeList.item(0).getTextContent());
		parms=activityTypeNodeList.item(2).getTextContent();
		doPostPutJSON(conn,parms,"POST");

		System.out.println("Step# 3.9  JSON => Request #7 => GET /person/"
				+first_person_id+"/"
				+activityTypeNodeList.item(2).getTextContent()
				+" Accept:APPLICATION/JSON");
		conn=getConnection(BaseURL.toString()+"/person/"+first_person_id+"/"+activityTypeNodeList.item(2).getTextContent());
		getActivityTypesJSON(conn,1);
		
		//3.10 XML
	
		System.out.println("\n\n\nStep# 3.10  XML => Request #10 => PUT /person/"
				+last_person_id+"/"
				+activity_type+"/"
				+activity_id
				+" Accept:APPLICATION/XML");
		conn=getConnection(BaseURL.toString()+"/person/"+last_person_id+"/"+activity_type+"/"+activity_id);
		parms="\n<activity> "
			      + 	"\n	<type>"+activityTypeNodeList.item(1).getTextContent() +"</type> "
			      +	"\n</activity>";
		doPostPutXML(conn,parms,"PUT");

				System.out.println("\nStep 3.10 XML =>Request #8 GET /person/"+last_person_id+"/"+activityTypeNodeList.item(1).getTextContent()+"/"+activity_id+" Accept:APPLICATION/XML\n");
				conn=getConnection(BaseURL.toString()+"/person/"+last_person_id+"/"+activityTypeNodeList.item(1).getTextContent()+"/"+activity_id);
				getListXML(conn,1,"activity");
		
		
		//3.10 JSON
	
		System.out.println("\n\nStep# 3.10 JSON => Request #10 => PUT /person/"
				+last_person_id+"/"
				+activityTypeNodeList.item(1).getTextContent()+"/"
				+activity_id
				+" Accept:APPLICATION/JSON");
		conn=getConnection(BaseURL.toString()+"/person/"+last_person_id+"/"+activityTypeNodeList.item(1).getTextContent()+"/"+activity_id);
		parms="{ " 
			      + "\n	\"type\":"+"\""+ activity_type +"\""
			      +	"\n}";
		doPostPutJSON(conn,parms,"PUT");
	
		System.out.println("\nStep 3.10 JSON =>Request #8 GET /person/"+last_person_id+"/"+activity_type+"/"+activity_id+" Accept:APPLICATION/JSON\n");
		conn=getConnection(BaseURL.toString()+"/person/"+last_person_id+"/"+activity_type+"/"+activity_id);
		getActivityTypesJSON(conn,0);
		
		
		//3.11 XML
		System.out.println("\n\n\nStep# 3.11 XML => Request #11 => GET /person/"
				+last_person_id+"/"
				+activity_type
				+"?before=2017-01-22T12:00:00.0&after=2018-02-28T09:00:00.0"
				+" Accept:APPLICATION/XML");
		conn=getConnection(BaseURL.toString()+"/person/"+last_person_id+"/"+activity_type+"?before=2017-01-22T12:00:00.0&after=2018-02-28T09:00:00.0");
		getListXML(conn,1,"activity");
		
		//3.11 JSON
		System.out.println("\nStep# 3.11 JSON => Request #11 => GET /person/"
				+last_person_id+"/"
				+activity_type
				+"?before=2017-01-22T12:00:00.0&after=2018-02-28T09:00:00.0"
				+" Accept:APPLICATION/JSON");
		conn=getConnection(BaseURL.toString()+"/person/"+last_person_id+"/"+activity_type+"?before=2017-01-22T12:00:00.0&after=2018-02-28T09:00:00.0");
		getActivityTypesJSON(conn,0);
	
	}
	


	private static JSONArray getActivityTypesJSON(HttpURLConnection conn, int min) throws IOException {
		JSONArray arr=null;
		String body=getStringJSON(conn);
		if(body==null)
			return null;
			
	
		if(min>=1) {
			arr = new JSONArray(body.toString());
			if(arr.length()>=min) {
    				System.out.println("\n=> Result: OK");
 				System.out.println("\n=> Http status: " + conn.getResponseCode()+"\n");
    			}else {
    				System.out.println("=> Result: ERROR");
    			}
		}
	
		else {
			System.out.println("\n=> Result: OK");
			System.out.println("\n=> Http status: " + conn.getResponseCode()+"\n");
		}	
		
		System.out.println(JsonWriter.formatJson(body.toString()));
		return arr;
	}
	
	private static NodeList getListXML(HttpURLConnection conn, int min, String tagName) throws IOException, TransformerException, Exception {
		Document doc= getDocXML(conn);
		if(doc == null)
			return null;
		NodeList list = doc.getElementsByTagName(tagName);
			
		//server is expected to return NodeList
        	if(list.getLength()>=min) {
        		System.out.println("\n=> Result: OK ");
        		System.out.println("\n=> Http status: " + conn.getResponseCode()+"\n");
        	}else {
        		System.out.println("\n=> Result: ERROR\n");
        	}
		printDocument(doc, System.out);
		return list;
	}


	private static void deletePerson(HttpURLConnection conn) throws IOException {
		conn.setRequestProperty("Accept", "application/xml");
		conn.setRequestProperty("Content-type", "application/xml");
		conn.setRequestMethod("DELETE");
		conn.setDoOutput( true );
		conn.setDoInput( true );
		conn.setRequestProperty("User-Agent", USER_AGENT);
		conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

    
		if (conn.getResponseCode() != 204) {
			throw new RuntimeException("Failed : HTTP error code : "
				+ conn.getResponseCode());
		}
		else {
			System.out.println("Http status code :" + conn.getResponseCode());
		}
	}
	

	private static void doPostPutJSON(HttpURLConnection conn, String parms, String input) throws IOException {
		
		conn.setRequestProperty("Accept", "application/json");
		conn.setRequestProperty("Content-type", "application/json");
		conn.setRequestMethod(input);
		conn.setDoOutput( true );
		conn.setDoInput( true );
		
		DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
		wr.writeBytes(parms);
		wr.flush();
		wr.close();

		int responseCode = conn.getResponseCode();
		if (responseCode != 200 && responseCode != 201) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
		}
		else {
			System.out.println("\n=> Result: OK ");
    			System.out.println("\n=> Http status: " + conn.getResponseCode());
    			System.out.println("\n=> Parameters : " + parms+"\n");
		}
			
		
	}

	private static Document doPostPutXML(HttpURLConnection conn, String parms, String input) throws Exception {
		Document doc = null;
		conn.setRequestProperty("Accept", "application/xml");
		conn.setRequestProperty("Content-type", "application/xml");
		conn.setRequestMethod(input);
		
		
		System.out.println("\n=> Parameters : " + parms+"\n");
		conn.setDoOutput( true );
		conn.setDoInput( true );
		
		DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
		wr.writeBytes(parms);
		wr.flush();
		wr.close();

		int responseCode = conn.getResponseCode();
		if (responseCode != 200 && responseCode != 201) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
		}else 
			if (input.equals("POST")) {
				System.out.println("\n=> Result: OK ");
				System.out.println("\n=> Http status: " + responseCode+"\n");
				InputStream stream= conn.getInputStream();
				doc = parseXML(stream);
				printDocument(doc, System.out);
			}
		
		return doc;
	}




	private static void updatePerson(HttpURLConnection conn, String parms) throws IOException {

		conn.setRequestMethod("PUT");
		conn.setDoOutput( true );
		conn.setDoInput( true );
		
		DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
		wr.writeBytes(parms);
		wr.flush();
		wr.close();

		int responseCode = conn.getResponseCode();
		if (responseCode != 201) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
		}else {
			System.out.println("\n=> Result: OK ");
			System.out.println("\n=> Http status: " + responseCode);
			System.out.println("\n=> POST parameters : " + parms+"\n");
		}
	}


	
	private static int getPersonsXML(HttpURLConnection conn) throws Exception {
		Document doc=getDocXML(conn);
		if(doc!=null)
			printDocument(doc, System.out);
		
		return conn.getResponseCode();
	}
	
	private static Document getDocXML(HttpURLConnection conn) throws IOException, Exception {
		Document doc=null;
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/xml");
		
		conn.setDoOutput(true);
		conn.setDoInput(true);
		
		if (conn.getResponseCode() != 200) {
			if(conn.getResponseCode()== 404)
				return null;
			throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
		}
		else {
			doc = parseXML(conn.getInputStream());
		}
		return doc;
	}


	//Step 3.1 Accept:APPLICATION/XML
	private static void getCountXML(HttpURLConnection conn) throws Exception {
			Document doc = getDocXML(conn);
			if(doc == null)
				return;
        		NodeList nodeList = doc.getElementsByTagName("person");
        		setIDsXML(nodeList);
			
        		total_person=nodeList.getLength();
			System.out.println("\nTotal number of people is: "+ total_person);
			System.out.println("\nFirst Persin ID: "+first_person_id);
			System.out.println("\nLast Person ID: "+last_person_id+"\n");
			
			printDocument(doc, System.out);
		
	}
	private static void setIDsXML(NodeList nodeList) {
		int flag=0;
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node nNode = nodeList.item(i);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element personElement = (Element) nNode;
				
				if(flag==0) {
					first_person_id=Integer.parseInt(personElement.getAttribute("person_id"));
					flag=1;
				}
				last_person_id= Integer.parseInt(personElement.getAttribute("person_id"));
				
			}
         }
		
	}

	//Step 3.1 Accept:APPLICATION/JSON
	private static void getCountJSON(HttpURLConnection conn) throws Exception,JSONException {
		int flag =0;
		String body=getStringJSON(conn);
		JSONArray arr = new JSONArray(body.toString());
		System.out.println(JsonWriter.formatJson(body.toString()));
		
		for (int i = 0; i < arr.length(); i++) {
		    JSONObject person = (JSONObject) arr.get(i);
		    if(flag==0) {
				first_person_id=Integer.parseInt(person.get("person_id").toString());
				flag=1;
			}
			last_person_id=Integer.parseInt(person.get("person_id").toString());
		}
		total_person=arr.length();
		System.out.println("\nTotal number of people is: "+ total_person);
		System.out.println("\nFirst Persin ID: "+first_person_id);
		System.out.println("\nLast Person ID: "+last_person_id);
	}
	
	
	
	private static String getStringJSON(HttpURLConnection conn) throws JSONException, IOException {
		String body=null;
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");
		conn.setDoOutput(true);
		conn.setDoInput(true);
		
		if (conn.getResponseCode() != 200) {
			if(conn.getResponseCode()== 404)
				return null;
			throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
		}
		else {
			InputStream stream= conn.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader((stream)));
			body= br.readLine();
		}
		conn.disconnect();
		return body;
	}
	

	
	private static Document parseXML(InputStream stream)throws Exception{
		DocumentBuilderFactory objDocumentBuilderFactory = null;
		DocumentBuilder objDocumentBuilder = null;
		Document doc = null;
		        
		try{
			objDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
		    	objDocumentBuilder = objDocumentBuilderFactory.newDocumentBuilder();
		    	doc = objDocumentBuilder.parse(stream);
		}
		catch(Exception ex){
		    throw ex;
		}       
		
		return doc;
	}
	

	private static HttpURLConnection getConnection(String string) throws IOException {
		URL url= new URL(string);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		return conn;
	}
	
	public static void printDocument(Document doc, OutputStream out) throws IOException, TransformerException {
	    TransformerFactory tf = TransformerFactory.newInstance();
	    Transformer transformer = tf.newTransformer();
	    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
	    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
	    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

	    transformer.transform(new DOMSource(doc), 
	         new StreamResult(new OutputStreamWriter(out, "UTF-8")));
	}
	
}
