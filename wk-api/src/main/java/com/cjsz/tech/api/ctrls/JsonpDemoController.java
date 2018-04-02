package com.cjsz.tech.api.ctrls;

import com.cjsz.tech.book.domain.BookRepo;
import com.cjsz.tech.utils.JsonResult;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by shiaihua on 17/6/23.
 */
@RestController
@RequestMapping("/jsonp")
public class JsonpDemoController {

    /**
     * 移动端注册
     *
     * @param request
     * @return
     */
//    @RequestMapping(value = "/testJsonp",produces = MediaType.APPLICATION_JSON_VALUE)
//    @JsonView
//    @ResponseBody
//    @RequestMapping(value = "/testJsonp",produces = MediaType.APPLICATION_JSON_VALUE)
    @RequestMapping(value = "/testJsonp", method = { RequestMethod.POST,RequestMethod.GET })
    @ResponseBody
    public Object testjsonp(HttpServletRequest request) {
        BookRepo book = new BookRepo();
        book.setBook_id(238923L);
        book.setBook_name("测试的阿");
        JsonResult jsonResult = (JsonResult) JsonResult.getSuccess("获取成功");
        jsonResult.setData(book);
        return jsonResult;
    }
}
