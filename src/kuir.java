import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

public class kuir {

    public static void main(String[] args) throws IOException, TransformerException, ParserConfigurationException, SAXException, ClassNotFoundException {

        String path = null;
        makeCollection makeCollection = new makeCollection();
        makeKeyword makeKeyword = new makeKeyword();
        indexer indexer = new indexer();

        if (args[0].equals("-c")) {
            path = args[1];
            makeCollection.makeCollection(path);

        } else if (args[0].equals("-k")) {
            path = args[1];
            makeKeyword.makeKeyword(path);
        } else if (args[0].equals("-i")){
            path = args[1];
            indexer.indexer(path);
        } else {
            System.out.println("Wrong argument!");
        }

    }
}
