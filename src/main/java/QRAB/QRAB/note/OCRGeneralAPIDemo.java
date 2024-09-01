package QRAB.QRAB.note;



import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;


public class OCRGeneralAPIDemo {

    public static void main(String[] args) {
        //api 엔드 포인트
        String apiURL = "https://3viphnb84f.apigw.ntruss.com/custom/v1/33919/1052997939660282774c9eec454329d7ff7beb617edb815dc04b9a37be6db7d9/general";
        //api 접근을 위한 secret key
        String secretKey = "eE1DZm5OTXB1eWZZTm9GZWppUU5sWWRUdGhqRGxhZmU=";

        try {
            URL url = new URL(apiURL); //url 을 조기화
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

            JSONObject image = new JSONObject();  // 이미지 정보를 담기 위한 JSON 객체 생성
            image.put("format", "jpg"); // 이미지 포맷
            image.put("url", "https://kr.object.ncloudstorage.com/ocr-ci-test/sample/1.jpg"); // image should be public, otherwise, should use data
            // FileInputStream inputStream = new FileInputStream("YOUR_IMAGE_FILE");
            // byte[] buffer = new byte[inputStream.available()];
            // inputStream.read(buffer);
            // inputStream.close();
            // image.put("data", buffer);
            image.put("name", "demo"); //이미지 이름 설정

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

            JSONObject jsonResponse = new JSONObject(response.toString()); //Json 응답을 파싱해 텍스트만 추출
            JSONArray imagesArray = jsonResponse.getJSONArray("images");

            for(int i = 0; i < imagesArray.length(); i++){ //이미지 배열을 순회해 각 이미지의 필드 읽기
                JSONObject imageObj = imagesArray.getJSONObject(i);
                JSONArray fieldsArray = imageObj.getJSONArray("fields");

                for(int j = 0; j < fieldsArray.length(); j++){ //각 필드의 inferText 값을 출력
                    JSONObject field = fieldsArray.getJSONObject(j);
                    String inferText  = field.getString("inferText");
                    System.out.println(inferText);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}