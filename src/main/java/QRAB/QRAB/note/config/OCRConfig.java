package QRAB.QRAB.note.config;

import lombok.Getter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

@Getter
@Configuration
public class OCRConfig {
    @Value("${ocr.api.url}")
    private String apiUrl;

    @Value("${ocr.api.secret}")
    private String secretKey;

    public String extractTextFromImage(String imageUrl) throws IOException{
        URL url = new URL(apiUrl); //url 을 조기화
        System.out.println("imageUrl response: " + imageUrl);
        HttpURLConnection con = (HttpURLConnection)url.openConnection(); // api 연결을 열기
        con.setUseCaches(false); //캐시를 사용 x
        con.setDoInput(true); // 입력 허용
        con.setDoOutput(true); // 출력 허용
        con.setRequestMethod("POST");//요청 메서드
        con.setRequestProperty("Content-Type", "application/json; charset=utf-8"); //json형식
        con.setRequestProperty("X-OCR-SECRET", secretKey); //시크릿 키를 요청 헤더에 포함

        JSONObject json = new JSONObject(); // 요청 매개 변수를 담기 위한 JSON 객체 생성
        json.put("version", "V2"); // API 버전
        json.put("requestId", UUID.randomUUID().toString());// 고유한 요청 ID
        json.put("timestamp", System.currentTimeMillis()); // 현재 시간

        JSONObject image = new JSONObject();// 이미지 정보를 담기 위한 JSON 객체 생성
        int idx = 0;
        for(int i = 0; i < imageUrl.length(); i++){
            if(imageUrl.charAt(i) == '.'){
                idx = i;
            }
        }

        String imageFormat = imageUrl.substring(idx + 1);
        image.put("format", imageFormat);
        image.put("url", imageUrl);
        image.put("name", "demo");

        JSONArray images = new JSONArray(); // 이미지 객체를 배열에 추가
        images.put(image);
        json.put("images", images); //최종 json 객체에 이미지 배열 추가
        String postParams = json.toString(); // json -> 문자열

        DataOutputStream wr = new DataOutputStream(con.getOutputStream()); //데이터 전송을 위한 outputStream 생성
        wr.writeBytes(postParams); // Json 문자열 전송
        wr.flush(); //OutputStream 플러시
        wr.close();//OutputStream  닫기

        int responseCode = con.getResponseCode(); // API 응답 코드 받기
        BufferedReader br;
        if (responseCode == 200) { //정상 응답
            br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        } else {// 오류 응답
            br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
        }
        //응답 읽어오기 위한 StringBuffer 생성
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = br.readLine()) != null) { //응답 데이터를 한 줄씩 읽음
            response.append(inputLine); //StringBuffer 에 추가
        }
        br.close();

        System.out.println("Api response: " + response.toString());
        JSONObject jsonResponse = new JSONObject(response.toString()); //Json 응답을 파싱해 텍스트만 추출
        JSONArray imagesArray = jsonResponse.getJSONArray("images");
        StringBuilder extractedText = new StringBuilder();

        for(int i = 0; i < imagesArray.length(); i++) { //이미지 배열을 순회해 각 이미지의 필드 읽기
            JSONObject imageObj = imagesArray.getJSONObject(i);
            JSONArray fieldsArray = imageObj.getJSONArray("fields");

            for (int j = 0; j < fieldsArray.length(); j++) { //각 필드의 inferText 값을 출력
                JSONObject field = fieldsArray.getJSONObject(j);
                String inferText = field.getString("inferText");
                extractedText.append(inferText).append(" ");
                //System.out.println(inferText);
            }
        }
        return extractedText.toString();
    }
}
