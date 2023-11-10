package com.astrosea.richer.param;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class CreatGainParam {


    @NotNull(message = "fakeRewBase不能为空")
    private BigDecimal fakeRewBase;

    @NotNull(message = "realDecBase不能为空")
    private BigDecimal realDecBase;

}
