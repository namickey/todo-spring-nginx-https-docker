package com.example.demo.web.aa01;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * メニュー画面コントローラ
 */
@Controller
public class AA0101Controller {

    /**
     * メニュー画面を表示する。
     * @return メニュー画面
     */
    @GetMapping("/")
    public String top() {
        return "AA0101/index";
    }
}
