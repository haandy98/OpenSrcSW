import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class genSnippet {
    public static void main(String[] args) throws IOException, ClassNotFoundException {

        String path = null;


        if (args[0].equals("-f")) {
            path = args[1];
            String query = null;
            if (args[2].equals("-q")) {
                query = args[3];
            }
            function(path, query);

        }
    }

    public static void function(String recv_path, String query) throws IOException {

        File dir = new File(recv_path);
        Path path = Paths.get(String.valueOf(dir));
        String content = Files.readString(path);

        String[] sentence = content.split("/n");
        String[] word0 = sentence[0].split(" ");
        String[] word1 = sentence[1].split(" ");
        String[] word2 = sentence[2].split(" ");
        String[] word3 = sentence[3].split(" ");
        String[] word4 = sentence[4].split(" ");

        int count[] = new int[5];

        for (int i = 0; i < word0.length; i++) {
            if(query.equals(word0[i])){
                count[0]++;
            }
        }
        for (int i = 0; i < word1.length; i++) {
            if(query.equals(word1[i])){
                count[1]++;
            }
        }
        for (int i = 0; i < word2.length; i++) {
            if(query.equals(word2[i])){
                count[2]++;
            }
        }
        for (int i = 0; i < word3.length; i++) {
            if(query.equals(word3[i])){
                count[3]++;
            }
        }
        for (int i = 0; i < word4.length; i++) {
            if(query.equals(word4[i])){
                count[4]++;
            }
        }

        int tmp1;
        int tmp2;
        int[] doc_id = {0, 1, 2, 3, 4};
        for (int i = 0; i < count.length; i++) {
            for (int j = i + 1; j < count.length; j++) {
                if (count[j] > count[i]) {
                    tmp1 = count[j];
                    count[j] = count[i];
                    count[i] = tmp1;

                    tmp2 = doc_id[j];
                    doc_id[j] = doc_id[i];
                    doc_id[i] = tmp2;
                }
            }
        }

        System.out.println(sentence[doc_id[0]]);
    }

}
