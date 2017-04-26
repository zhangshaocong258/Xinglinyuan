package core.util;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.*;

/**
 * Created by zsc on 2016/11/23.
 * 1.获取页面string，判断是否登录成功
 * 2.序列化cookie
 * 3.反序列化cookie
 * 4.下载验证码图片
 */
public class HttpClientUtil {
    /**
     *
     * @param httpClient HttpClient客户端
     * @param context 上下文
     * @param request 请求
     * @param encoding 字符编码
     * @param isPrintConsole 是否打印到控制台
     * @return 网页内容
     */
    public static String getWebPage(CloseableHttpClient httpClient
            , HttpClientContext context
            , HttpRequestBase request
            , String encoding
            , boolean isPrintConsole){
        CloseableHttpResponse response = null;
//        String web = null;//得到实体内容，使用EntityUtils得到json
        try {
            response = httpClient.execute(request,context);
//            web = EntityUtils.toString(response.getEntity());
//            System.out.println("web " + web);
        } catch (HttpHostConnectException e){
            e.printStackTrace();
            System.out.println("HttpHostConnectException");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IOException");
        }
        System.out.println("status---" + response.getStatusLine().getStatusCode());
        //头文件
//        HeaderIterator iterator = response.headerIterator();
//        while (iterator.hasNext()) {
//            System.out.println("\t" + iterator.next());
//        }
        BufferedReader rd = null;
        StringBuilder webPage = null;
        try {
            rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent(),encoding));
            String line = "";
            webPage = new StringBuilder();
            while((line = rd.readLine()) != null) {
                webPage.append(line);
                if(isPrintConsole){//是否打印
                    System.out.println(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        request.releaseConnection();
        return webPage.toString();//或者返回web
    }
    /**
     * 序列化对象
     * @param object
     * @throws Exception
     */
    public static void serializeObject(Object object,String filePath){
        OutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(object);
            System.out.println("序列化成功");
            oos.flush();
            fos.close();
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 反序列化对象
     * @param path
     * @throws Exception
     */
    public static Object deserializeMyHttpClient(String path) throws NullPointerException, FileNotFoundException {
//		InputStream fis = HttpClientUtil.class.getResourceAsStream(name);
        File file = new File(path);
        InputStream fis = new FileInputStream(file);
        ObjectInputStream ois = null;
        Object object = null;
        try {
            ois = new ObjectInputStream(fis);
            object = ois.readObject();
            fis.close();
            ois.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 下载图片
     * @param fileURL 文件地址
     * @param path 保存路径
     * @param saveFileName 文件名，包括后缀名
     * @param isReplaceFile 若存在文件时，是否还需要下载文件
     */
    public static void downloadFile(CloseableHttpClient httpClient
            , HttpClientContext context
            , String fileURL
            , String path
            , String saveFileName
            , Boolean isReplaceFile){
        try{
            HttpGet request = new HttpGet(fileURL);
            CloseableHttpResponse response = httpClient.execute(request,context);
            System.out.println("status:" + response.getStatusLine().getStatusCode());
            File file =new File(path);
            //如果文件夹不存在则创建
            if  (!file .exists()  && !file .isDirectory()){
                //logger.info("//不存在");
                file.mkdirs();
            } else{
                System.out.println("//目录存在");
            }
            file = new File(path + saveFileName);
            if(!file.exists() || isReplaceFile){
                //如果文件不存在，则下载
                try {
                    OutputStream os = new FileOutputStream(file);
                    InputStream is = response.getEntity().getContent();
                    byte[] buff = new byte[(int) response.getEntity().getContentLength()];
                    while(true) {
                        int readed = is.read(buff);
                        if(readed == -1) {
                            break;
                        }
                        byte[] temp = new byte[readed];
                        System.arraycopy(buff, 0, temp, 0, readed);
                        os.write(temp);
                        System.out.println("文件下载中....");
                    }
                    is.close();
                    os.close();
                    System.out.println(fileURL + "--文件成功下载至" + path + saveFileName);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else{
                System.out.println(path);
                System.out.println("该文件存在！");
            }
            request.releaseConnection();
        } catch(IllegalArgumentException e){
            System.out.println("连接超时...");

        } catch(Exception e1){
            e1.printStackTrace();
        }
    }
}
