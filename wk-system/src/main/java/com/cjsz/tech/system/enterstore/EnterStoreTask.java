package com.cjsz.tech.system.enterstore;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cjsz.tech.core.SpringContextUtil;
import com.cjsz.tech.system.beans.EnterStoreApp;
import com.cjsz.tech.system.beans.EnterStoreInfo;
import com.cjsz.tech.system.service.EnterStoreAppService;
import com.cjsz.tech.system.service.EnterStoreShopService;
import com.taobao.api.internal.toplink.logging.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;



/**
 * 每个机构中店面的KPI数据 JOB
 * Created by shiaihua on 16/7/1.
 */
public class EnterStoreTask {

    /**
     * 获取TOKEN的URL
     */
    private static String TOKEN_URL= "http://mrjapi.51jhh.net:82/oauth/token?client_id=mrj-api&grant_type=password";

    /**
     * 获取店面每小时KPI数据的URL
     */
    private static String KPI_HOUR_URL ="http://mrjapi.51jhh.net:82/api/shop/kpiforhour";


    /**
     *获取店面KPI数据
     */
    private static String KPIDATA_URL = "http://mrjapi.51jhh.net:82/api/shop/getKpiesData";


    public static EnterStoreTask self = null;


    static SimpleDateFormat DATE_SIMPLE=new SimpleDateFormat("yyyy-MM-dd");


    @Autowired
    private EnterStoreAppService enterStoreAppService;

    @Autowired
    private EnterStoreShopService enterStoreShopService;

    private static Map<String,TokenBean> tokens = new HashMap<String,TokenBean>();



    public static EnterStoreTask getInstance() {
        if (self == null) {
            self = new EnterStoreTask();
        }
        return self;
    }

    private EnterStoreTask() {
    }


    public  static Integer enterstoreNum(Long org_id,String shopid) {
        EnterStoreAppService enterStoreAppService = (EnterStoreAppService) SpringContextUtil.getBean("enterStoreAppService");
        EnterStoreShopService enterStoreShopService = (EnterStoreShopService)SpringContextUtil.getBean("enterStoreShopService");
        EnterStoreApp storeApp = enterStoreAppService.findByOrg(org_id);
        if(storeApp==null) {
            return 0;
        }


        EnterStoreInfo shopInfo = enterStoreShopService.findByOrgAndShop(org_id,shopid);
        if(shopInfo==null) {
            return 0;
        }

        TokenBean token = tokens.get(""+org_id);
        if(token==null) {
            String tokenurl = TOKEN_URL+"&username="+storeApp.getApp_id()+"&password="+storeApp.getApp_secret();
            JSONObject jsonObject = httpRequest(tokenurl, "GET", null);
            if(jsonObject!=null) {
                token = new TokenBean();
                token.setAccess_token(jsonObject.getString("access_token"));
                token.setExpires_in(jsonObject.getLong("expires_in"));
                token.setScope(jsonObject.getString("scope"));
                token.setRefresh_token(jsonObject.getString("refresh_token"));
                token.setToken_type(jsonObject.getString("token_type"));
            }
        }
        if(token==null) {
            return 0;
        }
//        EnterStoreInfo shopInfo = new EnterStoreInfo();
//        shopInfo.setOrg_id(18L);
//        shopInfo.setShop_id("6597");
//        shopInfo.setShop_name("九丘书馆");
        String datestr = DATE_SIMPLE.format(new Date());
        String requestUrl = getKPIDATA_FIVE_URL(shopInfo,token.getAccess_token(),datestr);

        JSONObject jsonObject = httpRequest(requestUrl, "GET", null);
        if (null != jsonObject) {
            Integer status = jsonObject.getInteger("status");
            if(status!=0) {
                return 0;
            }
            JSONObject content = jsonObject.getJSONObject("content");
            JSONArray accountesArray = content.getJSONArray("kpiShopAccountes");
            for(int i=0;i<accountesArray.size();i++) {
                JSONObject curAccountes = accountesArray.getJSONObject(i);
                Integer kpiInt = curAccountes.getInteger("kpiId");
                if(kpiInt!=1) {
                    continue;
                }
                JSONArray accountesDatas = curAccountes.getJSONArray("kpiShopAccountDatas");
                if(accountesDatas.size()>0) {
                    JSONObject datekpidatas = accountesDatas.getJSONObject(0);
                    JSONObject kpiDatasArray = datekpidatas.getJSONObject("dataMap");
                    Iterator iter = kpiDatasArray.values().iterator();
                    Integer kpiValue = 0;
                    while(iter.hasNext()) {
                        Integer curvalue = ((BigDecimal)iter.next()).intValue();
                        kpiValue+= curvalue;
                    }
                    return kpiValue;
                }
            }
        }
        return 0;

    }

    private static String collectTokenUrl(String appid,String secret) {
        String url = TOKEN_URL +"&username="+appid+"&password="+secret;
        return url;
    }

    private static String collectKpiHourUrl(EnterStoreApp app, EnterStoreInfo shopInfo, String token, String date) {
        String url = KPI_HOUR_URL +"&appId="+app.getApp_id()+"&access_token="+token+"&shopId="+shopInfo.getShop_id()+"&kpiId="+shopInfo.getShop_kpis()+"&dates="+date;
        return url;
    }

    public static String getKPIDATA_FIVE_URL(EnterStoreInfo shopInfo, String token, String date) {
        //?shopId=4384&dataType=2&beginDate=2015-12-14&endDate=2015-12-15&hasWay=true&access_token=0857e90d-0cd0-42a9-8906-6f05xxxx
        String url = KPIDATA_URL +"?access_token="+token+"&shopId="+shopInfo.getShop_id()+"&hasWay=true&dataType=1"+"&beginDate="+date+"&endDate="+date;
//        String datestr = DATE_SIMPLE.format(new Date());
//        url = url+"&beginDate="+datestr+"&endDate="+datestr;
        return url;
    }


    /**
     * 处理每个机构中店面的KPI数据
     * 当前为进店人数KPI数据
     */
    public void runTask() {
        //1 TODO 查APP信息
        List<EnterStoreApp>  appList = enterStoreAppService.findList();

        //2 TODO 查店面信息

        //3 TODO 获取和更新token


        //4 TODO 获取店面KPI信息


    }






    /**
     * 发起https请求并获取结果
     *
     * @param requestUrl
     *            请求地址
     * @param requestMethod
     *            请求方式（GET、POST）
     * @param outputStr
     *            提交的数据
     * @return JSONObject(通过JSONObject.get(key)的方式获取json对象的属性值)
     */
    public static JSONObject httpRequest(String requestUrl, String requestMethod, String outputStr) {
        JSONObject jsonObject = null;
        StringBuffer buffer = new StringBuffer();
        if (!requestUrl.contains("wgateid")) {
            try {
                // 创建SSLContext对象，并使用我们指定的信任管理器初始化
                TrustManager[] tm = { new MyX509TrustManager() };
                SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
                sslContext.init(null, tm, new java.security.SecureRandom());
                // 从上述SSLContext对象中得到SSLSocketFactory对象
                SSLSocketFactory ssf = sslContext.getSocketFactory();

                URL url = new URL(requestUrl);
                HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();

//                httpUrlConn.setSSLSocketFactory(ssf);

                httpUrlConn.setDoOutput(true);
                httpUrlConn.setDoInput(true);
                httpUrlConn.setUseCaches(false);
                // 设置请求方式（GET/POST）
                httpUrlConn.setRequestMethod(requestMethod);

                // if ("GET".equalsIgnoreCase(requestMethod))
                httpUrlConn.connect();

                // 当有数据需要提交时
                if (null != outputStr) {
                    OutputStream outputStream = httpUrlConn.getOutputStream();
                    // 注意编码格式，防止中文乱码
                    outputStream.write(outputStr.getBytes("UTF-8"));
                    outputStream.close();
                }

                // 将返回的输入流转换成字符串
                InputStream inputStream = httpUrlConn.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String str = null;
                while ((str = bufferedReader.readLine()) != null) {
                    buffer.append(str);
                }
                bufferedReader.close();
                inputStreamReader.close();
                // 释放资源
                inputStream.close();
                inputStream = null;
                httpUrlConn.disconnect();
                jsonObject = JSONObject.parseObject(buffer.toString());
                // jsonObject = JSONObject.fromObject(buffer.toString());
            } catch (ConnectException ce) {
                ce.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonObject;
        } else {
            try {
                // 创建SSLContext对象，并使用我们指定的信任管理器初始化

                URL url = new URL(requestUrl);
                HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();

                httpUrlConn.setDoOutput(true);
                httpUrlConn.setDoInput(true);
                httpUrlConn.setUseCaches(false);
                // 设置请求方式（GET/POST）
                httpUrlConn.setRequestMethod(requestMethod);

                // if ("GET".equalsIgnoreCase(requestMethod))
                httpUrlConn.connect();

                // 当有数据需要提交时
                if (null != outputStr) {
                    OutputStream outputStream = httpUrlConn.getOutputStream();
                    // 注意编码格式，防止中文乱码
                    outputStream.write(outputStr.getBytes("UTF-8"));
                    outputStream.close();
                }

                // 将返回的输入流转换成字符串
                InputStream inputStream = httpUrlConn.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String str = null;
                while ((str = bufferedReader.readLine()) != null) {
                    buffer.append(str);
                }
                bufferedReader.close();
                inputStreamReader.close();
                // 释放资源
                inputStream.close();
                inputStream = null;
                httpUrlConn.disconnect();
                jsonObject = JSONObject.parseObject(buffer.toString());
                // jsonObject = JSONObject.fromObject(buffer.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonObject;

        }
    }

}
