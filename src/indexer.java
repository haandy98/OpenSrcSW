import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.*;

public class indexer {
    @SuppressWarnings({ "rawtypes", "unchecked", "nls" })
    public void indexer(String recv_path) throws IOException, ParserConfigurationException, SAXException, ClassNotFoundException {

        FileOutputStream fileStream = new FileOutputStream("index.post");

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileStream);

        HashMap IndexMap = new HashMap();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        Document document = documentBuilder.parse(recv_path);

        Element root = document.getDocumentElement();
        NodeList element_doc = root.getElementsByTagName("doc");

        List<List<String>> index_pair = new ArrayList<>();

        for (int i = 0; i < element_doc.getLength(); i++) {

            Element element_doc_id = (Element)element_doc.item(i);
            NodeList element_body = element_doc_id.getElementsByTagName("body");

            Element bodyElement = (Element)element_body.item(0);
            Text bodyText = (Text)bodyElement.getFirstChild();

            String strBody = bodyText.getData();

            StringTokenizer pair = new StringTokenizer(strBody, "#");

            // [key, count, doc_id]
            while(pair.hasMoreTokens()) {
                String[] pair_keyCount = pair.nextToken().split(":");
                List<String> index_pair_keyCountId = new ArrayList<>();
                index_pair_keyCountId.add(pair_keyCount[0]);
                index_pair_keyCountId.add(pair_keyCount[1]);
                index_pair_keyCountId.add("" + i);
                index_pair.add(index_pair_keyCountId);
            }

        }
//        System.out.println(index_pair);

        for (int i = 0; i < index_pair.size(); i++) {

            String key = index_pair.get(i).get(0);
            StringBuilder value = new StringBuilder();
            List<List<String>> index = new ArrayList<>();

            for (List<String> strings : index_pair) {
                if (strings.get(0).equals(index_pair.get(i).get(0))) {
                    index.add(strings);
                }
            }

            double df = (double) index.size();

            for (List<String> strings : index) {
                double tf = Double.parseDouble(strings.get(1));

                double N_df = element_doc.getLength() / df;
                double w = tf * Math.log(N_df);

                value.append(strings.get(2));
                value.append(" ");
                value.append(String.format("%.3f", w));
                value.append(" ");
            }

            IndexMap.put(key, value.toString());
        }

        objectOutputStream.writeObject(IndexMap);
        objectOutputStream.close();

        //* HashMap 출력
        FileInputStream fileInputStream = new FileInputStream("index.post");
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        Object object = objectInputStream.readObject();
        objectInputStream.close();

//        System.out.println("읽어올 객체의 type -> " + object);

        HashMap hashMap = (HashMap) object;
        Iterator<String> it = hashMap.keySet().iterator();

        while(it.hasNext()) {
            String read_key = it.next();
            String read_value = (String)hashMap.get(read_key);
            System.out.println(read_key + " -> " + read_value);
        }
        //*/
    }
}
