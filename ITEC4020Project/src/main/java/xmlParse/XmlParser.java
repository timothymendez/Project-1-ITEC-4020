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

	public XmlParser(FileInputStream input) {
		this.input = input;
	}

	/**
	 * @param articleTitle
	 *         the articleTitle tag you want to find in the xml file
	 * 
	 * @return
	 */
	public List<String> extractElementText(String articleTitle) {

		SAXReader reader = new SAXReader();
		String xPathArticleTitle = "//"+articleTitle;
		
		try {
			Document document = reader.read(input);

			List<String> titles = new ArrayList<String>();

			List<Node> articleList = document.selectNodes(xPathArticleTitle);
			Iterator<Node> iter = articleList.iterator();

			while(iter.hasNext()) {
				Node article = iter.next();
				String title = article.getText();
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

		XMLWriter writer = null;

		try {

			Document document = DocumentHelper.createDocument();
			Element articleSet = document.addElement("PubmedArticleSet");

			for (String title : titles) {
				Element pubmedArticle = articleSet.addElement("PubmedArticle");
				Element articleTitle = pubmedArticle.addElement("ArticleTitle");
				articleTitle.setText(title);
			}

			FileOutputStream output = new FileOutputStream("titles.xml");
			writer = new XMLWriter(output, OutputFormat.createPrettyPrint());

			writer.write(document);
			System.out.println("Finished");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
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
