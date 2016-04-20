package com.harora.ceeride;


import com.harora.ceeride.service.CostEstimates;
import com.harora.ceeride.service.LyftClient;

import org.junit.Test;

import retrofit2.Call;

/**
 * Created by harora on 4/7/16.
 */
public class TestSimpleTest {

    @Test
    public void getCost(){
        System.out.println("SAmple test running");
        LyftClient.LyftService service = LyftClient.getLyftService("m5ASmQsgURu7","ivJbJ9DSvRQ2X8kDKIZdazUbNW1Pd0e7");
        try {
            Call<CostEstimates> costs = service.getCosts(42.37,-71.098, 42.34,-71.08);
            retrofit2.Response<CostEstimates> response = costs.execute();
            System.out.println("SAmple test running " + response.body().getCostEstimates().size());


        } catch (Exception e){
            System.out.println(e);
        }

    }
}
