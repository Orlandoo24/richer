package com.astrosea.richer;

import com.astrosea.richer.mapper.RewardBaseMapper;
import com.astrosea.richer.mapper.RichRewardLogMapper;
import com.astrosea.richer.mapper.TaxAllNftDoMapper;
import com.astrosea.richer.service.RichService;
import com.astrosea.richer.vo.dto.HolderDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@Slf4j

@SpringBootTest
class RicherApplicationTests {

    @Autowired
    RewardBaseMapper rewardBaseMapper;

    @Autowired
    RichService richService;

    @Autowired
    RichRewardLogMapper logMapper;

    @Autowired
    TaxAllNftDoMapper nftMapper;




    @Test
    void contextLoads() {

        List<HolderDto> nftCountByRarity = nftMapper.getNftCountByRarity();

        System.out.println(nftCountByRarity);

    }

    @Test
    void fillTest() {



    }








}
