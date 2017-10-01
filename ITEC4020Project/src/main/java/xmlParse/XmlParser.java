package xmlParse;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class XmlParser {
	
	/*
	A FileInputStream obtains input bytes from a file in a file system. FileInputStream is meant for reading streams of raw bytes such as image data. For reading streams of characters
	*/
	
	FileInputStream input;
	
	//Initialize the parser with a file input for parsing. In this case it will be the 4020A1-Datasets
	public XmlParser(FileInputStream input) {
		this.input = input;
	}

	
	// this method allows us to target specific elements in the xml file and extract its contents. 
	// @param articleTitle = the tag you want to find in the xml file
	
	public List<String> extractElementText(String articleTitle) {
		
		//We've decided to use the SAX(Simple API for XML) to read our XML file as an alternative to the DOM because it is faster and doesn't take as much memory. 
		SAXReader reader = new SAXReader();
		
		//use xPath to identify the node to find. The use of "//" indicates xPaths syntax to search for all arguments that match the article title
		String xPathArticleTitle = "//"+articleTitle;
		
		try {
			//a document object will be created after reading the file
			Document document = reader.read(input);
			
			//create a list to store the titles
			List<String> titles = new ArrayList<String>();
			
			//We will create a list of nodes to store the mined articleTitle elements.
			//The selectNodes method will return a list of nodes that we want to find 
			List<Node> articleList = document.selectNodes(xPathArticleTitle);
			
			//An iterator is necessary to loop through the list of nodes we created above 
			Iterator<Node> iter = articleList.iterator();

			while(iter.hasNext()) {
				//fetch each ArticleTitle node
				Node article = iter.next();
				//get and save the text of the ArticleTitle node to the String title var
				String title = article.getText();
				//add the title to the title list
				titles.add(title);
			}
			return titles;

		} catch (DocumentException e) {
			e.printStackTrace();
			throw new RuntimeException("Parse Failed");
		}
	}
	
	

	/**
	 * @param titles
	 *        titles to be written into a file
	*/
	public void writeXml(List<String> titles) {
		//declare a XML writer to write xml file
		XMLWriter writer = null;

		try {
			//create a document object. This will be the document the writer writes to. 
			Document document = DocumentHelper.createDocument();
			//Begin by adding the root element to the document object
			Element articleSet = document.addElement("PubmedArticleSet");
			
			//A for-loop to add each title in the list to the document. 
			for (String title : titles) {
				//add a PubmedArticle tag and ArticleTitle tag
				Element pubmedArticle = articleSet.addElement("PubmedArticle");
				Element articleTitle = pubmedArticle.addElement("ArticleTitle");
				//set the text in the ArticleTitle tag to each article title we have
				articleTitle.setText(title);
			}
			
			//Initiate a fileoutputStream
			FileOutputStream output = new FileOutputStream("titles.xml");
			//Initiate a XMLWriter
			writer = new XMLWriter(output, OutputFormat.createPrettyPrint());
			//write a document to the file
			writer.write(document);
			System.out.println("Finished");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			//close the writer to release resource
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}
		}

	}
	
	public static void main(String[] args) throws FileNotFoundException {
		XmlParser parser = new XmlParser(new FileInputStream("C:\\Users\\sonmendez\\git\\Project-1-ITEC-4020\\ITEC4020Project\\src\\main\\java\\xmlParse\\4020a1-datasets"));
		List<String> titles = parser.extractElementText("ArticleTitle");
		parser.writeXml(titles);
	
}
}
