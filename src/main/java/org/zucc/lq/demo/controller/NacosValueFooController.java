package org.zucc.lq.demo.controller;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NacosValueFooController {

    @NacosValue(value = "${foo.nacos.value}", autoRefreshed = true)
    public String fooNacosValue;

    @GetMapping(value = "/nacos/foo")
    public String foo() {
        return fooNacosValue;
    }
}
