package com.cjsz.tech.system.ctrl;

import com.cjsz.tech.system.enterstore.EnterStoreApi;
import com.cjsz.tech.system.enterstore.EnterStoreTask;
import com.cjsz.tech.system.utils.AppResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by shiaihua on 16/7/3.
 */
@Controller
public class FreeEnterStoreController {


    /**
     * APP在线阅读页面
     *
     * @return
     */
    @RequestMapping(value = "/enter", method = {RequestMethod.GET })
    public Object enterstore(HttpServletRequest request, Map<String, Object> model) {
        return "admin/enter";
    }


    /**
     * APP在线阅读页面
     *
     * @return
     */
    @RequestMapping(value = "/frontapi/enterstore/json", method = {RequestMethod.GET,RequestMethod.POST })
    @ResponseBody
    public Object enterstoredata(HttpServletRequest request, Map<String, Object> model,@RequestParam("o") Long o,@RequestParam("s") String s) {
        AppResult result = AppResult.getSuccess("操作成功!");
        Integer enternum = EnterStoreTask.enterstoreNum(o,s);
        EnterStoreApi enterStoreApi = EnterStoreApi.getAPI();
        enterStoreApi.setEnternum(enternum);
        result.setData(enterStoreApi);
        return result;
    }


}
