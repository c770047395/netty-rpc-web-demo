package com.cp.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(String name) {
        log.info(name);
        return "hello, " + name;
    }
}