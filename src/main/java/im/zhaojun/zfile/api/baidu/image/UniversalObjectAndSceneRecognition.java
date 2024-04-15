package im.zhaojun.zfile.api.baidu.image;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import im.zhaojun.zfile.api.baidu.image.model.api.result.JsonResult;
import im.zhaojun.zfile.api.baidu.image.model.api.result.base.ResultItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import okhttp3.*;

import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Component
public class UniversalObjectAndSceneRecognition {
        @Value("${baidu.image.api.key}")
        private String API_KEY;
        @Value("${baidu.image.api.secret-key}")
        private String SECRET_KEY;

        static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();

        public JsonResult classify(String content) throws IOException{
            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            RequestBody body = RequestBody.create(mediaType,"image="+ URLEncoder.encode(content, "UTF-8"));
            Request request = new Request.Builder()
                    .url("https://aip.baidubce.com/rest/2.0/image-classify/v2/advanced_general?access_token=" + getAccessToken())
                    .method("POST", body)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .addHeader("Accept", "application/json")
                    .build();
            Response response = HTTP_CLIENT.newCall(request).execute();
            return parseJson(response.body().string());
        }


        public static JsonResult parseJson(String jsonString) {
            JSONObject jsonObject = JSON.parseObject(jsonString);
            System.out.println(jsonString);
            JsonResult result = new JsonResult();
            result.setResult_num(jsonObject.getInteger("result_num"));
            result.setLog_id(jsonObject.getString("log_id"));

            JSONArray resultArray = jsonObject.getJSONArray("result");
            List<ResultItem> resultItemList=new ArrayList<>();
            for (int i = 0; i < resultArray.size(); i++) {
                JSONObject resultObject = resultArray.getJSONObject(i);
                ResultItem item = new ResultItem();
                item.setScore(resultObject.getDouble("score"));
                item.setRoot(resultObject.getString("root"));
                item.setKeyword(resultObject.getString("keyword"));
                resultItemList.add(item);
            }
            result.setResult(resultItemList);
            return result;
        }
        /**
         * 从用户的AK，SK生成鉴权签名（Access Token）
         *
         * @return 鉴权签名（Access Token）
         * @throws IOException IO异常
         */
        private String getAccessToken() throws IOException {
            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            RequestBody body = RequestBody.create(mediaType, "grant_type=client_credentials&client_id=" + API_KEY
                    + "&client_secret=" + SECRET_KEY);
            Request request = new Request.Builder()
                    .url("https://aip.baidubce.com/oauth/2.0/token")
                    .method("POST", body)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();
            Response response = HTTP_CLIENT.newCall(request).execute();
            System.out.println(response);
            return new org.json.JSONObject(response.body().string()).getString("access_token");
        }

}
