package sg.lwx.work.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author lianwenxiu
 */
@RestController
public class HelloController {

    @GetMapping("/hello/welcome")
    public  String welcome() {
        return "Hello Word";
    }
}

