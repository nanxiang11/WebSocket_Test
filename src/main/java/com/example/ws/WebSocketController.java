package com.example.ws;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class WebSocketController {


    @RequestMapping("/")
    public String webSocket(Model model){
            model.addAttribute("Springuser", "测试");

            return "webSocket";

    }

}
