package sg.lwx.work.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by michael on 2020/04/28.
 */
@RestController
public class HelloController {

    @GetMapping("/hello/welcome")
    public  String welcome() {
        return "Hello Word";
    }
}

