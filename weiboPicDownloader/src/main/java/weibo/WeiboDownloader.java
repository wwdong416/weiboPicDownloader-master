package weibo;

import org.apache.http.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WeiboDownloader {

    /**
     * UA
     */
    static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36";

    /**
     * 路径
     */
    public static String savePath = "D:\\jsoupweibo";

    public static void main(String[] args) throws ParseException, IOException, InterruptedException {

        List<String> configs = getFileData("D://" + "jsoupweibo/config.txt");
        if (configs.isEmpty()) {
            File file = new File("D://" + "jsoupweibo/config.txt");
            file.mkdirs();
            System.out.println("配置信息有误,请到D://jsoupweibo/config.txt下修改配置。");
            return;
        }
        String[] urls = configs.get(0).split("/", 2);
        String url = urls[0];
        String containerId = "";
        int num = configs.size();
        System.out.println(num+"0000000000");
        //  String namelist = "刘强东/摩拜胡玉兰/0";
        //    String[] namelist1 = namelist.split("/");
        String findname = "";

        while (!findname.contains("0")) {
            for (int q = 0; q < configs.size(); q++) {

                savePath = "D://" + "jsoupweibo/" + configs.get(q);
                findname = configs.get(q);
                System.out.println(savePath);
                boolean isFirst=true;
                    if(new File(savePath).exists()){
                        isFirst=false;
                    }
                    String isnull = "";

                    while (isnull != "0") {
                        if(!isFirst){
                            System.out.println(findname+" 已经存在 ");
                            break;
                        }
                    url = urls[0];
                    System.out.println("爬虫对象：" + findname);
                    // System.out.println(namelist1[j]);

                    //  System.out.println(findname);
                    containerId = WeiboUtils.nicknameToContainerId(findname);
                    System.out.println("微博ID：" + containerId);

                    List<String> imgUrls = null;
                    try {
                        imgUrls = WeiboUtils.getAllImgURL(containerId);
                    } catch (Exception e1) {
                        System.out.println("解析出现异常， 请稍候再试！");
                        return;
                    }
                    System.out.println("分析完毕");
                    System.out.println("图片数量: " + imgUrls.size());

                    if (!savePath.endsWith("/") && !savePath.endsWith("\\")) {
                        if (savePath.contains("/"))
                            savePath = savePath + "/";
                        else
                            savePath = savePath + "\\";
                    }

                    if (!new File(savePath).exists()) {
                        try {
                            new File(savePath).mkdirs();
                            System.out.println("成功创建  " + savePath );
                        } catch (Exception e) {
                            System.out.println("无法创建目录,请手动创建");
                        }
                    }
                    CountDownLatch downLatch = new CountDownLatch(imgUrls.size());
                    ExecutorService executor = Executors.newFixedThreadPool(4);
                    for (int i = 0; i < imgUrls.size(); i++) {
                        executor.submit(new ImageDownloadTask(downLatch, i, imgUrls.get(i)));
                    }
                    downLatch.await();
                    System.out.println("图片下载完成, 路径是 " + savePath);
                        isnull = "0";
                    executor.shutdown();
                }

            }
        }

    }
   private static List<String> getFileData(String s) {
        List<String> configs = new ArrayList<String>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(s), "gb2312"));
            String str = null;
            while ((str = reader.readLine()) != null) {
                configs.add(str);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return configs;
    }
}
