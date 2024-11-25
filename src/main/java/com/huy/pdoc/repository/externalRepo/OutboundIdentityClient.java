package com.huy.pdoc.repository.externalRepo;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

import com.huy.pdoc.dto.request.ExchangeTokenRequest;
import com.huy.pdoc.dto.response.ExchangeTokenResponse;

import feign.QueryMap;

@FeignClient(name = "outboundIdentity", url = "https://oauth2.googleapis.com")
public interface OutboundIdentityClient {
    @PostMapping(value = "/token", produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ExchangeTokenResponse exchangeToken(@QueryMap ExchangeTokenRequest exchangeTokenRequest);
}