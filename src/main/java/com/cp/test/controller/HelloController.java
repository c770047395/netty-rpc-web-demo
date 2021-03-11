package com.cp.test.controller;

import com.cp.test.HelloService;
import com.netty.rpc.proxy.ProxyFactory;
import org.springframework.web.bind.annotation.*;

@RestController
public class HelloController {
    @GetMapping("/hello")
    public String hello(@RequestParam("name") String name) throws Exception {
        HelloService helloService = ProxyFactory.create(HelloService.class);
        return helloService.hello(name);
    }
}
