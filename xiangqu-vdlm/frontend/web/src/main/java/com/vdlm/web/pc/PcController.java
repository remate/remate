package com.vdlm.web.pc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.vdlm.web.BaseController;

@Controller
public class PcController extends BaseController {
        
        @RequestMapping(value = "/pc/index")
        public String index() {
            return "pc/index";
        }
}
