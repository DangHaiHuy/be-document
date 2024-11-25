package com.huy.pdoc.repository.externalRepo;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.huy.pdoc.dto.response.OutboundUserResponse;

@FeignClient(name = "outboundUser", url = "https://www.googleapis.com")
public interface OutboundUserClient {
    @GetMapping(value = "/oauth2/v1/userinfo")
    OutboundUserResponse getUserInfo(@RequestParam("alt") String alt,
            @RequestParam("access_token") String accessToken);
}