package study.securitytest;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "home";
    }


    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/user")
    public @ResponseBody SecurityMessage user() {
        return SecurityMessage.builder().message("user page").authentication(SecurityContextHolder.getContext().getAuthentication()).build();
    }

    @Secured({"ROLE_ADMIN"})
    @GetMapping("/admin")
    public @ResponseBody SecurityMessage admin() {
        return SecurityMessage.builder().message("admin page").authentication(SecurityContextHolder.getContext().getAuthentication()).build();
    }





}
