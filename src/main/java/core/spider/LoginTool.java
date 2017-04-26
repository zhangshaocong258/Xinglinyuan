package core.spider;

import core.util.Config;
import core.util.HttpClientUtil;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by zsc on 2016/11/23.
 */
public class LoginTool {
    //登录地址
    private static String EMAIL_LOGIN_URL = "http://tcm.sstp.cn/index.php?user-login";

    /**
     * @param account  账户
     * @param password 密码
     * @return
     */
    public boolean login(String account, String password) {
        CloseableHttpClient httpClient = HttpClientTool.getInstance().getCloseableHttpClient();
        HttpClientContext context = HttpClientTool.getInstance().getHttpClientContext();
        String loginState;
        HttpPost request;
        List<NameValuePair> formParams = new ArrayList<NameValuePair>();
        //通过邮箱登录
        request = new HttpPost(EMAIL_LOGIN_URL);
        formParams.add(new BasicNameValuePair("username", account));
        formParams.add(new BasicNameValuePair("password", password));
        formParams.add(new BasicNameValuePair("submit", "登录"));
        UrlEncodedFormEntity entity = null;
        try {
            entity = new UrlEncodedFormEntity(formParams, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        request.setEntity(entity);
        //无法根据loginState判断是否登录成功
        loginState = HttpClientUtil.getWebPage(httpClient, context, request, "utf-8", false);//登录
        System.out.println("loginState " + loginState);
        HttpClientUtil.serializeObject(context.getCookieStore(), Config.cookiePath);
        return true;
    }
    /**
     * 肉眼识别验证码
     * @param httpClient Http客户端
     * @param context Http上下文
     * @param url 验证码地址
     * @return
     */
    public String identify(CloseableHttpClient httpClient,HttpClientContext context, String url){
        String captchaPath = Config.captcha;
        String path = captchaPath.substring(0, captchaPath.lastIndexOf("/") + 1);
        String fileName = captchaPath.substring(captchaPath.lastIndexOf("/") + 1);
        HttpClientUtil.downloadFile(httpClient, context, url, path, fileName,true);
        System.out.println("请输入 " + captchaPath + " 下的验证码：");
        Scanner sc = new Scanner(System.in);
        String captcha = sc.nextLine();
        return captcha;
    }

    /**
     * 反序列化CookiesStore
     * @return
     */
    public boolean deserializeCookieStore(String path){
        try {
            CookieStore cookieStore = (CookieStore) HttpClientUtil.deserializeMyHttpClient(path);
            HttpClientTool.getInstance().getHttpClientContext().setCookieStore(cookieStore);
        } catch (Exception e){
            System.out.println("反序列化Cookie失败,没有找到Cookie文件");
            return false;
        }
        return true;
    }
}
