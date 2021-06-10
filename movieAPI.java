import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

public class movieAPI {
    public static void main(String[] args) throws IOException, ParseException {
        String clientId = "TEZTuPFX4NRW02uqbSrX"; //애플리케이션 클라이언트 아이디값"
        String clientSecret = "JxLebS3jZx"; //애플리케이션 클라이언트 시크릿값"

        Scanner scan = new Scanner(System.in);
        String input;
        System.out.print("검색어를 입력하세요: ");
        input = scan.nextLine();
        String text = URLEncoder.encode(input, "UTF-8");
        scan.close();

        String apiURL = "https://openapi.naver.com/v1/search/movie.json?query=" + text;

        URL url = new URL(apiURL);
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("X-Naver-Client-Id", clientId);
        con.setRequestProperty("X-Naver-Client-Secret", clientSecret);

        int responseCode = con.getResponseCode();
        BufferedReader br;
        if(responseCode == 200) {
            br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        } else {
            br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
        }
        String inputLine;
        StringBuffer response = new StringBuffer();
        while((inputLine = br.readLine()) != null) {
            response.append(inputLine);
        }
        br.close();

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(String.valueOf(response));
        JSONArray infoArray = (JSONArray) jsonObject.get("items");

        for (int i = 0; i < infoArray.size(); i++) {
            System.out.println("=item_"+i+" ===========================================");
            JSONObject itemObject = (JSONObject) infoArray.get(i);
            System.out.println("title:\t\t" + itemObject.get("title"));
            System.out.println("subtitle:\t" + itemObject.get("subtitle"));
            System.out.println("director:\t" + itemObject.get("director"));
            System.out.println("actor:\t\t" + itemObject.get("actor"));
            System.out.println("userRating:\t" + itemObject.get("userRating") + "\n");
        }
    }
}
