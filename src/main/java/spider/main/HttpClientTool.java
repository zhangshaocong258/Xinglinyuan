package spider.main;

import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BestMatchSpecFactory;
import org.apache.http.impl.cookie.BrowserCompatSpecFactory;

/**
 * Created by zsc on 2016/11/23.
 *
 * 全局CloseableHttpClient，HttpClientContext，启动时初始化
 */
public class HttpClientTool {
    private static class HttpClientToolHolder {
        private static HttpClientTool httpClientTool = new HttpClientTool();
    }

    public static HttpClientTool getInstance() {
        return HttpClientToolHolder.httpClientTool;
    }

    private CloseableHttpClient closeableHttpClient;
    private HttpClientContext httpClientContext;

    private HttpClientTool() {
        //初始化CloseableHttpClient和HttpClientContext
        setCloseableHttpClient();
        setHttpClientContext();
    }

    private void setCloseableHttpClient() {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();//连接池，不设置会非常慢，艹！！！
        cm.setMaxTotal(20);
        cm.setDefaultMaxPerRoute(20);//设置全部路由的默认大小
//        HttpHost localhost = new HttpHost("locahost", 80);
//        cm.setMaxPerRoute(new HttpRoute(localhost), 50);//设置单个路由大小，更改localhost

        //建造者模式，设置超时，多线程设为10s，长一点！！！设置cookie
        RequestConfig globalConfig = RequestConfig.custom().
                setSocketTimeout(60000).
                setConnectTimeout(60000).
                setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY).build();
        this.closeableHttpClient = HttpClients.custom().
                setDefaultRequestConfig(globalConfig).setConnectionManager(cm).build();
    }

    private void setHttpClientContext() {
        this.httpClientContext = HttpClientContext.create();
        //设置cookie
        Registry<CookieSpecProvider> registry = RegistryBuilder.
                <CookieSpecProvider> create().
                register(CookieSpecs.BEST_MATCH, new BestMatchSpecFactory()).
                register(CookieSpecs.BROWSER_COMPATIBILITY,
                        new BrowserCompatSpecFactory()).build();
        this.httpClientContext.setCookieSpecRegistry(registry);
    }

    public CloseableHttpClient getCloseableHttpClient() {
        return closeableHttpClient;
    }

    public HttpClientContext getHttpClientContext() {
        return httpClientContext;
    }
}
