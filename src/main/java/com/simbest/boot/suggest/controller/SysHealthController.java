package com.simbest.boot.suggest.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.simbest.boot.suggest.model.JsonResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/sys/health")
public class SysHealthController {

    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/anonymous/heart", method = { RequestMethod.HEAD })
    public JsonResponse healthHeart() {
        return JsonResponse.defaultSuccessResponse();
    }

}
