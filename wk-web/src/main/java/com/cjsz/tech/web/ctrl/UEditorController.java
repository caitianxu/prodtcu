package com.cjsz.tech.web.ctrl;

import com.baidu.ueditor.ActionEnter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by yunke on 16/3/15.
 * 富文本编辑器
 */
@Controller
public class UEditorController {


    @RequestMapping(value = "/static/admin/plugin/ueditor/jsp/controller.jsp", method = {RequestMethod.POST, RequestMethod.GET})
    public void config(HttpServletRequest request, HttpServletResponse response, String action) {
        String rootPath = request.getSession().getServletContext().getRealPath("/");

        try {
            String exec = new ActionEnter(request, rootPath).exec();
            PrintWriter writer = response.getWriter();
            writer.write(exec);
            writer.flush();
            writer.close();
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
