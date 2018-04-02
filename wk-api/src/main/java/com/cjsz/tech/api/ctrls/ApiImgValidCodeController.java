package com.cjsz.tech.api.ctrls;

import com.cjsz.tech.util.ValidCodeUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 图形验证
 * Created by shiaihua on 17/1/5.
 */
@Controller
public class ApiImgValidCodeController {


    @RequestMapping(value = "/validcode", method = { RequestMethod.GET })
    public void validecode(HttpServletRequest request, HttpServletResponse resp, Map<String, Object> model)  {
        try{
            ValidCodeUtil.getValidCode(request, resp);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

}
