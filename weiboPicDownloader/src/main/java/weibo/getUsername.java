package weibo;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class getUsername {
private static final String fileName = "D://jsoupweibo//";
    static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36";

    public static void main(String[] args) throws IOException {
        JsonElement isFinsh = null;
        List<String> names = new ArrayList<String>();
        String page = "0";//json数据的页数，超出页数json数据返回的是null
        System.out.println("------------请输入查询人物关键词------------");
        Scanner in = new Scanner(System.in);
        String keytag = in.nextLine();//关键字
       // String keytag = "CEO";
        while (page != null) {
            //json地址
            String url = "https://m.weibo.cn/api/container/getIndex?containerid=100103type%3D3%26q%3D" + keytag + "%26isv%3D2%26specfilter%3D1%26log_type%3D7&page=0&page=" + page;
            HttpClient httpClient = getHttpClient();
            HttpGet get = new HttpGet(url);
            get.setHeader("User-Agent", USER_AGENT);
            HttpResponse response = httpClient.execute(get);
            String ret = EntityUtils.toString(response.getEntity(), "utf-8");
            JsonObject root = new JsonParser().parse(ret).getAsJsonObject();
            JsonObject asJsonObject = root.getAsJsonObject("data");
            JsonArray array = asJsonObject.getAsJsonArray("cards");
            JsonObject cardlistInfo = asJsonObject.getAsJsonObject("cardlistInfo");
            isFinsh = cardlistInfo.get("page");
            //判断是否为最后一页
            if (isFinsh.isJsonNull()) {
                page = null;
            } else {
                page = cardlistInfo.get("page").getAsString();
                //打印当前json数据中page的值，页数
               System.out.println("++++++++page : "+page+"+++++++++" );
            }
            for (int i = 0; i < array.size(); i++) {
                JsonObject mblog = array.get(i).getAsJsonObject();
                if (mblog != null) {
                    JsonArray pics = mblog.getAsJsonArray("card_group");
                    if (pics != null) {
                        for (int j = 0; j < pics.size(); j++) {
                            JsonObject o = pics.get(j).getAsJsonObject();
                            JsonObject large = o.getAsJsonObject("user");
                            if (large != null) {
                                System.out.println(large.get("screen_name").getAsString());
                                names.add(large.get("screen_name").getAsString());
                                //saveText(large.get("screen_name").getAsString(),keytag);
                            }
                        }
                    }
                }
            }
        }
       in.close();
        saveText(names,keytag);

    }

    /**
     * 保存到本地
     * */
    private static void saveText(List<String> names, String pathname) throws IOException {
        //路径
        File outputFile = new File(fileName+pathname+".txt");
        //若不存在文件，新建
        if(!outputFile.exists()){
            outputFile.createNewFile();
        }
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outputFile)), true);
        for (int i = 0;i < names.size();i++){
            out.println(names.get(i));
        }
        out.close();
    }
    public static HttpClient getHttpClient() {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);
        return httpClient;
    }




}
