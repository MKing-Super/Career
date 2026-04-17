package per.mk.springai.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    /**
     * AI 助手页面
     *
     * @return 视图名称
     */
    @GetMapping({"/", "/ai", "/ai/"})
    public String index() {
        return "index";
    }

    /**
     * 天气助手页面
     *
     * @return 视图名称
     */
    @GetMapping({"/weather", "/weather/"})
    public String weather() {
        return "weather";
    }
}
