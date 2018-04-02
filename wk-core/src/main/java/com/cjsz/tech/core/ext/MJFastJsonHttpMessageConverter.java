package com.cjsz.tech.core.ext;

/**
 * Created by shiaihua on 17/6/26.
 */
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.cjsz.tech.utils.JsonResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * fastjson消息转换器
 * Created by caiya on 15/12/23.
 */
public class MJFastJsonHttpMessageConverter extends FastJsonHttpMessageConverter {
    public static final Charset UTF8 = Charset.forName("UTF-8");
    private Charset charset;
    private SerializerFeature[] features;

    public MJFastJsonHttpMessageConverter() {
        super();
        this.charset = UTF8;
        this.features = new SerializerFeature[0];
    }

    @Override
    protected void writeInternal(Object obj, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        // obj就是controller中注解为@ResponseBody的方法返回值对象
        ServletRequestAttributes ra= (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request =  ra.getRequest();

        String callback = request.getParameter("callback");
        if(StringUtils.isNotEmpty(callback )  )  {

            JsonResult jsonpObject = (JsonResult)obj;
            OutputStream out = outputMessage.getBody();
            ObjectMapper mapper = new ObjectMapper();
            String text = mapper.writeValueAsString(jsonpObject);
//            String text = JSON.toJSONString(jsonpObject, this.features);
            String jsonpText = new StringBuilder(callback).append("(").append(text).append(")").toString();
            byte[] bytes = jsonpText.getBytes(this.charset);
            out.write(bytes);
        }else{
            super.writeInternal(obj, outputMessage);
        }
    }
}
