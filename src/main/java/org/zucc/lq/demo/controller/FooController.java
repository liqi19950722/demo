package org.zucc.lq.demo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
public class FooController {
    @Value(value ="${foo.value}")
    public String fooValue;
    @GetMapping(value = "/foo")
    public String foo() {
        return fooValue;
    }
}
