package jpa.jpa2Study.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {

    @GetMapping("/hello")
    //@ResponseBody 없을 시 hello.html을 찾아서 data에 hello!!!를 넣어준다.
    public String hello(Model model){
        model.addAttribute("data", "hello!!!");
        return "hello";
    }
}
