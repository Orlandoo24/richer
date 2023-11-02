package com.astrosea.richer.service;


import com.astrosea.richer.param.QueryCoinsParam;
import com.astrosea.richer.response.Response;
import com.astrosea.richer.vo.QueryCoinsVo;

public interface RichService {



    Response<QueryCoinsVo> query(QueryCoinsParam param);


}
