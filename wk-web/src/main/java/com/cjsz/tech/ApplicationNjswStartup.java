package com.cjsz.tech;

import com.cjsz.tech.core.SpringContextUtil;
import com.cjsz.tech.system.domain.Adv;
import com.cjsz.tech.system.domain.OrgExtend;
import com.cjsz.tech.system.service.OrgExtendService;
import com.cjsz.tech.system.utils.CacheUtil;
import com.cjsz.tech.web.service.NjswIndexEhcacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;


/**
 * 启动时加载
 * Created by shiaihua on 16/10/3.
 */

@Component
@ComponentScan(basePackages = "com.cjsz.tech")
@Order(value=2)
public class ApplicationNjswStartup implements CommandLineRunner {
	
	@Autowired
	NjswIndexEhcacheService njswIndexEhcacheService;
	@Autowired
	OrgExtendService orgExtendService;

    @Override
    public void run(String... args) throws Exception {

    }
}