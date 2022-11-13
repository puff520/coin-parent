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


    /**
     * 获取币种历史K线
     *
     * @param symbol
     * @return
     */

    @RequestMapping("historyKline")
    public List<Candlestick> historyKline(String symbol, int size, CandlestickIntervalEnum intervalEnum) {
        List<Candlestick> list = marketClient
                .getCandlestick(CandlestickRequest.builder()
                        .symbol(symbol)
                        .interval(intervalEnum)
                        .size(size)
                        .build());
        return list;
    }

    /**
     * 获取最近成交记录
     *
     * @param symbol
     * @return
     */
    @RequestMapping("marketTrade")
    public List<MarketTrade> marketTrade(String symbol) {
        List<MarketTrade> marketTradeList = marketClient
                .getMarketTrade(MarketTradeRequest
                        .builder()
                        .symbol(symbol)
                        .build());
        return marketTradeList;
    }

    /**
     * 聚合行情
     */
    @RequestMapping("/detailMerged")
    public MarketDetailMerged detailMerged(String symbol) {
        MarketDetailMerged marketDetailMerged = marketClient
                .getMarketDetailMerged(MarketDetailMergedRequest
                        .builder()
                        .symbol(symbol)
                        .build());
        return marketDetailMerged;
    }

    /**
     * 所有交易对的最新
     */
    @RequestMapping("/marketTickers")
    public List<MarketTicker> marketTickers() {
        List<MarketTicker> tickerList = marketClient.getTickers();
        return tickerList;
    }


    /**
     * 市场深度数据
     */
    @RequestMapping("/marketDepth")
    public MarketDepth marketDepth(String symbol, DepthSizeEnum sizeEnum, DepthStepEnum stepEnum) {
        MarketDepth marketDepth = marketClient
                .getMarketDepth(MarketDepthRequest.builder()
                        .symbol(symbol)
                        .depth(sizeEnum)
                        .step(stepEnum)
                        .build());
        return marketDepth;
    }

    /**
     * 获得近期交易记录
     *
     * @param symbol
     * @return
     */
    @RequestMapping("historyTrade")
    public List<MarketTrade> historyTrade(String symbol) {
        List<MarketTrade> marketHistoryTradeList = marketClient
                .getMarketHistoryTrade(MarketHistoryTradeRequest
                        .builder()
                        .symbol(symbol)
                        .build());
        return marketHistoryTradeList;
    }

    /**
     * 最近24小时行情数据
     */
    @RequestMapping("marketDetail")
    public MarketDetail marketDetail(String symbol) {
        MarketDetail marketDetail = marketClient
                .getMarketDetail(MarketDetailRequest
                        .builder()
                        .symbol(symbol)
                        .build());
        return marketDetail;
    }


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
