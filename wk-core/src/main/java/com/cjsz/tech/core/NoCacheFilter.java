package com.cjsz.tech.core;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebFilter(filterName = "noCacheFilter",urlPatterns = "/*")
public class NoCacheFilter implements Filter {
        @Override
        public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws ServletException {
            HttpServletResponse response = (HttpServletResponse) res;
            HttpServletRequest request = (HttpServletRequest) req;
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Allow","GET,POST,PUT,DELETE,OPTIONS");
            response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");
            response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
            response.setHeader("Access-Control-Max-Age", "3600");
            if (request.getMethod()!="OPTIONS") {
                try {
                    chain.doFilter(req, res);
                }catch(Exception e) {
                    e.printStackTrace();
                }
            } else {
            }
        }

        @Override
        public void init(FilterConfig filterConfig) {}

        @Override
        public void destroy() {}

    }