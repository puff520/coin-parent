package cn.ztuo.bitrade.controller;


import com.binance.client.RequestOptions;
import com.binance.client.SyncRequestClient;
import com.binance.client.impl.RestApiRequest;
import com.binance.client.impl.utils.JsonWrapperArray;
import com.binance.client.impl.utils.UrlParamsBuilder;
import com.binance.client.model.enums.CandlestickInterval;
import com.huobi.client.MarketClient;
import com.huobi.client.req.market.*;
import com.huobi.constant.HuobiOptions;
import com.huobi.constant.enums.CandlestickIntervalEnum;
import com.huobi.constant.enums.DepthLevels;
import com.huobi.constant.enums.DepthSizeEnum;
import com.huobi.constant.enums.DepthStepEnum;
import com.huobi.model.market.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@RestController
public class TestController {


    MarketClient marketClient = MarketClient.create(new HuobiOptions());


    @RequestMapping("subDetail")
    public void subDetail(String symbol) {
        marketClient.subMarketDetail(SubMarketDetailRequest
                        .builder()
                        .symbol(symbol).build()
                , (marketDetailEvent) -> System.out.println(marketDetailEvent.toString()));
    }


    @RequestMapping("mbpLevels")
    public void mbpLevels(String symbol, DepthLevels depthLevel) {
        marketClient.subMbpRefreshUpdate(SubMbpRefreshUpdateRequest
                .builder()
                .symbols(symbol)
                .levels(depthLevel).build(), event -> System.out.println(event.toString()));
    }


    @RequestMapping("mbpRefreshLevels")
    public void mbpRefreshLevels(String symbol) {
        marketClient.subMbpIncrementalUpdate(SubMbpIncrementalUpdateRequest
                .builder().symbol(symbol).build(), event -> System.out.println(event.toString()));
    }


    @RequestMapping("bnHistoryKline")
    public List<com.binance.client.model.market.Candlestick> bnHistoryKline(String symbol, CandlestickInterval interval, Long startTime,
                                                                            Long endTime, Integer limit) {
        RequestOptions options = new RequestOptions();
        SyncRequestClient syncRequestClient = SyncRequestClient.create("", "",
                options);
        List<com.binance.client.model.market.Candlestick> list = syncRequestClient.getCandlestick(symbol, interval, startTime, endTime, limit);
        return list;
    }


}
