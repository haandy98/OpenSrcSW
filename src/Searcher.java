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
import java.io.*;
import java.util.*;

public class Searcher {

    // 참조할 문서의 개수
    int size_of_data = 5;

    // 출력할 문서의 개수
    int number_of_result_to_show = 3;

    public void Searcher(String recv_path, String query) throws IOException, ParserConfigurationException, SAXException, ClassNotFoundException {

        // 해쉬맵의 밸류를 파싱
        FileInputStream fileInputStream = new FileInputStream(recv_path);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        Object object = objectInputStream.readObject();
        objectInputStream.close();

        HashMap hashMap = (HashMap) object;
        Iterator<String> it = hashMap.keySet().iterator();

        List<List<String>> index = new ArrayList<>();
        while (it.hasNext()) {
            String read_key = it.next();
            String read_value = (String) hashMap.get(read_key);

            List<String> index_word = new ArrayList<>();
            index_word.add(read_key);

            String[] id_value = read_value.split(" ");
            Collections.addAll(index_word, id_value);

            index.add(index_word);
        }

        // 쿼리문과 해쉬맵 전달
//        double[] calcSim = CalcSim(query, index);

        KeywordExtractor ke = new KeywordExtractor();
        KeywordList kl = ke.extractKeyword(query, true);

        List<List<String>> query_index = new ArrayList<>();

        // 사용자 쿼리문 분석
        for (int i = 0; i < kl.size(); i++) {
            List<String> query_word = new ArrayList<>();
            query_word.add(kl.get(i).getString());
//            query_word.add("" + kl.get(i).getCnt());
            query_word.add("1");

            query_index.add(query_word);
        }

        // 쿼리 단어 가중치만 분리
        double[] query_w = new double[query_index.size()];
        for (int i = 0; i < query_index.size(); i++) {
            query_w[i] = Double.parseDouble(query_index.get(i).get(1));
        }

        // 해쉬맵에서 쿼리 단어들 정보만 골라내기
        List<List<String>> index_match = new ArrayList<>();
        for (List<String> queryIndex : query_index) {
            for (List<String> strings : index) {
                if (queryIndex.get(0).equals(strings.get(0))) {
                    index_match.add(strings);
                }
            }
        }

        // 가중치를 정리할 배열 표를 생성후 0으로 초기화
        double[][] index_w = new double[size_of_data][query_w.length];
        for (int i = 0; i < size_of_data; i++) {
            for (int j = 0; j < query_w.length; j++) {
                index_w[i][j] = 0.0;
            }
        }

        // 가중치를 계산한 후 가중치 값을 배열 표에 저장
        for (int i = 0; i < index_match.size(); i++) {
            for (int j = 0; j < (index_match.get(i).size() / 2); j++) {
                index_w[Integer.parseInt(index_match.get(i).get((2 * j) + 1))][i] = Double.parseDouble(index_match.get(i).get((2 * j) + 2));
            }
        }

        // 문서 아이디 별 가중치 배열 생성
        double[] Qid = new double[size_of_data];
        for (int i = 0; i < size_of_data; i++) {
            double Sim = CalcSim(index_w[i], query_w);
            Qid[i] = Sim;
        }

        // 가중치 크기순으로 재배열
        double tmp1;
        int tmp2;
        int[] doc_id = {0, 1, 2, 3, 4};
        for (int i = 0; i < Qid.length; i++) {
            for (int j = i + 1; j < Qid.length; j++) {
                if (Qid[j] > Qid[i]) {
                    tmp1 = Qid[j];
                    Qid[j] = Qid[i];
                    Qid[i] = tmp1;

                    tmp2 = doc_id[j];
                    doc_id[j] = doc_id[i];
                    doc_id[i] = tmp2;
                }
            }
        }

        // collection.xml로 부터 title 가져오기
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        Document document = documentBuilder.parse("collection.xml");

        Element root = document.getDocumentElement();
        NodeList element_doc = root.getElementsByTagName("doc");

        List<String> index_title = new ArrayList<>();

        for (int i = 0; i < element_doc.getLength(); i++) {

            Element element_doc_id = (Element) element_doc.item(i);
            NodeList element_title = element_doc_id.getElementsByTagName("title");

            Element titleElement = (Element) element_title.item(0);
            Text titleText = (Text) titleElement.getFirstChild();

            String strTitle = titleText.getData();

            index_title.add(strTitle);
        }

        // 가중치가 높은 상위 3개 문서 출력
        double Qid_match = 0;
        for (int i = 0; i < Qid.length; i++) {
            if (Qid[i] > 0) Qid_match = +Qid[i];
        }
        if (Qid_match == 0) {
            System.out.println("검색 결과가 없습니다.");
        } else {
            for (int i = 0; i < number_of_result_to_show; i++) {
                if (Qid[i] > 0) System.out.println("" + (i + 1) + ". " + index_title.get(doc_id[i]));
            }
        }
    }

    double CalcSim(double[] index_w, double[] query_w) {

        double Qid_w = InnerProduct(index_w, query_w);

        double s_index_w = 0;
        for (int i = 0; i < query_w.length; i++) {
            s_index_w += Math.pow(index_w[i], 2);
        }
        s_index_w = Math.sqrt(s_index_w);

        double s_query_w = 0;
        for (int i = 0; i < query_w.length; i++) {
            s_query_w += Math.pow(query_w[i], 2);
        }
        s_query_w = Math.sqrt(s_query_w);

        double Sim = Qid_w / (s_index_w * s_query_w);

        return Math.round(Sim * 100) / 100.0;
    }

    double InnerProduct(double[] index_w, double[] query_w) {
        double Qid_w = 0;
        for (int i = 0; i < query_w.length; i++) {
            Qid_w += (index_w[i] * query_w[i]);
        }

        return Qid_w;
    }
}
