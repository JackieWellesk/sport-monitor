package geist.hegel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaForwardController {

    @GetMapping({"/front", "/front/"})
    public String front() {
        return "forward:/front/index.html";
    }

    @GetMapping({"/front/{path:[^\\.]*}", "/front/**/{path:[^\\.]*}"})
    public String frontRoutes() {
        return "forward:/front/index.html";
    }

    @GetMapping({"/admin", "/admin/"})
    public String admin() {
        return "forward:/admin/index.html";
    }

    @GetMapping({"/admin/{path:[^\\.]*}", "/admin/**/{path:[^\\.]*}"})
    public String adminRoutes() {
        return "forward:/admin/index.html";
    }
}