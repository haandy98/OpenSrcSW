import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SimpleIR {

    public static void main(String[] args) throws ParserConfigurationException, TransformerException, FileNotFoundException {

        /**
         * 디렉토리로 부터 모든 파일 읽어와 리스트에 저장하기
         */
        File dir = new File("C:/SW/SimpleIR/example");
        File files[] = dir.listFiles();
        List<String> examples = new ArrayList<>();;

        for (File file : files) {
            try {
                Path path = Paths.get(String.valueOf(file));

                String content = readFile(path.toString(), StandardCharsets.UTF_8);
                examples.add(content);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//        System.out.println(examples.get(0));


        /**
         * XML 파일 형식으로 데이터 옮기기
         */
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        org.w3c.dom.Document doc = docBuilder.newDocument();

        org.w3c.dom.Element docs = doc.createElement("docs");
        doc.appendChild(docs);

        // 저장한 리스트를 XML 형식에 붙이기
        for (int i = 0; i < examples.size(); i++) {
            Document document = Jsoup.parse(examples.get(i));

            String title = document.title();
//        System.out.println(title);
            Element body = document.body();
            String body_text = body.text();
//        System.out.println(body_text);

            org.w3c.dom.Element doc_id = doc.createElement("doc");
            docs.appendChild(doc_id);
            doc_id.setAttribute("id", ""+i);

            org.w3c.dom.Element xml_title = doc.createElement("title");
            xml_title.appendChild(doc.createTextNode(title));
            doc_id.appendChild(xml_title);

            org.w3c.dom.Element xml_body = doc.createElement("body");
            xml_body.appendChild(doc.createTextNode(body_text));
            doc_id.appendChild(xml_body);
        }

        /**
         * XML 파일 출력하기
         */
        TransformerFactory transformerFactory = TransformerFactory.newInstance();

        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new FileOutputStream(new File("result.xml")));

        transformer.transform(source, result);

    }

    /**
     * path에서 String으로 읽어오기
     */
    static String readFile(String path, Charset encoding)
            throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }



}
