package cn.ztuo.bitrade.controller;


import cn.ztuo.bitrade.entity.CoinThumb;
import cn.ztuo.bitrade.entity.ExchangeCoin;
import cn.ztuo.bitrade.processor.CoinProcessor;
import cn.ztuo.bitrade.processor.CoinProcessorFactory;
import cn.ztuo.bitrade.service.ExchangeCoinService;
import cn.ztuo.bitrade.service.MarketService;
import com.alibaba.fastjson.JSON;
import com.huobi.api.request.account.SwapMarketHistoryKlineRequest;
import com.huobi.api.response.market.SwapMarketHistoryKlineResponse;
import com.huobi.api.service.market.MarketAPIServiceImpl;
import com.huobi.client.MarketClient;
import com.huobi.client.req.market.*;
import com.huobi.constant.HuobiOptions;
import com.huobi.constant.enums.DepthLevels;
import com.huobi.constant.enums.DepthSizeEnum;
import com.huobi.constant.enums.DepthStepEnum;
import com.huobi.model.market.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController("/v2")
public class Market2Controller {
    @Autowired
    private MarketService marketService;
    @Autowired
    private ExchangeCoinService coinService;
    @Autowired
    private CoinProcessorFactory coinProcessorFactory;

    MarketClient marketClient = MarketClient.create(new HuobiOptions());
    MarketAPIServiceImpl huobiAPIService = new MarketAPIServiceImpl();


    /**
     * 获取支持的交易币种
     *
     * @return
     */
    @RequestMapping("symbol")
    public List<ExchangeCoin> findAllSymbol() {
        List<ExchangeCoin> coins = coinService.findAllEnabled();
        return coins;
    }


    /**
     * 获取某交易对详情
     *
     * @param symbol
     * @return
     */
    @RequestMapping("symbol-info")
    public ExchangeCoin findSymbol(String symbol) {
        ExchangeCoin coin = coinService.findBySymbol(symbol);
        return coin;
    }

    /**
     * 获取币种缩略行情
     *
     * @return
     */
    @RequestMapping("symbol-thumb")
    public List<CoinThumb> findSymbolThumb() {
        List<ExchangeCoin> coins = coinService.findAllEnabled();
        List<CoinThumb> thumbs = new ArrayList<>();
        for (ExchangeCoin coin : coins) {
            CoinProcessor processor = coinProcessorFactory.getProcessor(coin.getSymbol());
            CoinThumb thumb = processor.getThumb();
            thumbs.add(thumb);
        }
        return thumbs;
    }


    /**
     * 聚合行情(Tickers)
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
     * 所有交易对的Tickers
     */
    @RequestMapping("/marketTickers")
    public List<MarketTicker> marketTickers() {
        List<MarketTicker> tickerList = marketClient.getTickers();
        return tickerList;
    }


    /**
     * 获取K线数据
     */
    @RequestMapping("/historyKline")
    public SwapMarketHistoryKlineResponse historyKline(String contractCode, String period, int size, int from, int to) {
        SwapMarketHistoryKlineRequest result = SwapMarketHistoryKlineRequest.builder()
                .contractCode(contractCode)
                .period(period)
                .size(size)
                .from(from)
                .to(to)
                .build();
        SwapMarketHistoryKlineResponse response = huobiAPIService.getSwapMarketHistoryKline(result);
        log.debug("获取K线数据：{}", JSON.toJSONString(response));
        return response;
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



}
