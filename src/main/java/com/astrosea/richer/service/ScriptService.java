package com.astrosea.richer.service;

import com.astrosea.richer.param.ClaimParam;

import com.astrosea.richer.response.Response;

public interface ScriptService {

    Response claim(ClaimParam param);

}
