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
	
	
	FileInputStream input;
	
	//Initialize the parser with a file input for parsing 
	public XmlParser(FileInputStream input) {
		this.input = input;
	}

	/**
	 * @param articleTitle
	 *        the articleTitle tag you want to find in the xml file
	 * 
	 * @return a list of article titles
	 */
	public List<String> extractElementText(String articleTitle) {
		
		//SAXReader to read xml file
		SAXReader reader = new SAXReader();
		//use xPath to identify the node to find
		String xPathArticleTitle = "//"+articleTitle;
		
		try {
			//a document object will be created after reading the file
			Document document = reader.read(input);
			//create a list to store the titles
			List<String> titles = new ArrayList<String>();
			
			//selectNodes method will return a list of nodes that we want to find
			//we want to the article title tag 
			List<Node> articleList = document.selectNodes(xPathArticleTitle);
			//a iterator is created to loop through the whole list of article titles 
			Iterator<Node> iter = articleList.iterator();

			while(iter.hasNext()) {
				//get each articletitle node
				Node article = iter.next();
				//get the text of the articletitle node
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
			//create a document object 
			Document document = DocumentHelper.createDocument();
			//add the root element to the document object
			Element articleSet = document.addElement("PubmedArticleSet");
			
			//for each title in the title list
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
		XmlParser parser = new XmlParser(new FileInputStream("4020a1-datasets.xml"));
		List<String> titles = parser.extractElementText("ArticleTitle");
		parser.writeXml(titles);
		
	}

}
