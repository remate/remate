package com.vdlm.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Handles favicon.ico requests assuring no <code>404 Not Found</code> error
 * is returned.
 * TODO 将来用静态资源
 */
@Controller class FaviconController {

    @RequestMapping("/favicon.ico")
    public String favicon(HttpServletRequest request) {
        if (request.getServerName() != null && request.getServerName().contains("xiangqu")) {
            return "forward:/resources/favicon.ico";
        } else {
            return "forward:/_resources/images/favicon.ico";
        }
    }
    
}