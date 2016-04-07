package com.vdlm.web.activity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.vdlm.service.activity.ActivityService;
import com.vdlm.service.activity.ActivityVO;
import com.vdlm.web.BaseController;

@Controller
public class ActivityController extends BaseController {
    
    @Autowired
    ActivityService activityService;
    
    @RequestMapping("/activity/{id}")
    public String view(@PathVariable("id") String id, Model model) {
        ActivityVO activity = activityService.loadVO(id);
        if (activity == null) {
            return "activity/404";
        }
        model.addAttribute("activity", activity);
        return "activity/view";
    }

}
