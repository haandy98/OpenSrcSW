import org.jsoup.Jsoup;
import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class makeKeyword {
    public void makeKeyword(String recv_path) throws ParserConfigurationException, IOException, SAXException, TransformerException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        Document document = documentBuilder.parse(recv_path);

        Element root = document.getDocumentElement();
        NodeList element_doc = root.getElementsByTagName("doc");

        List<String> index_doc_id = new ArrayList<>();
        List<String> index_title = new ArrayList<>();
        List<String> index_body = new ArrayList<>();


        for (int i = 0; i < element_doc.getLength(); i++) {

            Element element_doc_id = (Element)element_doc.item(i);
            NodeList element_title = element_doc_id.getElementsByTagName("title");
            NodeList element_body = element_doc_id.getElementsByTagName("body");

            Element titleElement = (Element)element_title.item(0);
            Element bodyElement = (Element)element_body.item(0);
            Text titleText = (Text)titleElement.getFirstChild();
            Text bodyText = (Text)bodyElement.getFirstChild();

            String strTitle = titleText.getData();
            String strBody = bodyText.getData();

            KeywordExtractor ke = new KeywordExtractor();
            KeywordList kl = ke.extractKeyword(strBody, true);

            StringBuilder bodyIndex = new StringBuilder();

            for (int j = 0; j < kl.size(); j++) {
                bodyIndex.append(kl.get(j).getString() + ":" + kl.get(j).getCnt());
                if (j < kl.size() - 1) bodyIndex.append("#");
            }

            index_doc_id.add("" + i);
            index_title.add(strTitle);
            index_body.add(bodyIndex.toString());
        }

        /**
         * XML 파일 형식으로 데이터 옮기기
         */
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        Document doc = docBuilder.newDocument();

        Element docs = doc.createElement("docs");
        doc.appendChild(docs);

        // 저장한 리스트를 XML 형식에 붙이기
        for (int i = 0; i < index_doc_id.size(); i++) {

            Element doc_id = doc.createElement("doc");
            docs.appendChild(doc_id);
            doc_id.setAttribute("id", index_doc_id.get(i));

            Element xml_title = doc.createElement("title");
            xml_title.appendChild(doc.createTextNode(index_title.get(i)));
            doc_id.appendChild(xml_title);

            Element xml_body = doc.createElement("body");
            xml_body.appendChild(doc.createTextNode(index_body.get(i)));
            doc_id.appendChild(xml_body);
        }

        /**
         * XML 파일 출력하기
         */
        TransformerFactory transformerFactory = TransformerFactory.newInstance();

        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new FileOutputStream(new File("index.xml")));

        transformer.transform(source, result);
    }
}
