//package cn.ztuo.bitrade.controller;
//
//
//import com.huobi.client.MarketClient;
//import com.huobi.client.req.market.SubMarketDetailRequest;
//import com.huobi.client.req.market.SubMbpIncrementalUpdateRequest;
//import com.huobi.client.req.market.SubMbpRefreshUpdateRequest;
//import com.huobi.constant.HuobiOptions;
//import com.huobi.constant.enums.DepthLevels;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@Slf4j
//@RestController
//public class TestController {
//
//
//    MarketClient marketClient = MarketClient.create(new HuobiOptions());
//
//
//    @RequestMapping("subDetail")
//    public void subDetail(String symbol) {
//        marketClient.subMarketDetail(SubMarketDetailRequest
//                        .builder()
//                        .symbol(symbol).build()
//                , (marketDetailEvent) -> System.out.println(marketDetailEvent.toString()));
//    }
//
//
//    @RequestMapping("mbpLevels")
//    public void mbpLevels(String symbol, DepthLevels depthLevel) {
//        marketClient.subMbpRefreshUpdate(SubMbpRefreshUpdateRequest
//                .builder()
//                .symbols(symbol)
//                .levels(depthLevel).build(), event -> System.out.println(event.toString()));
//    }
//
//
//    @RequestMapping("mbpRefreshLevels")
//    public void mbpRefreshLevels(String symbol) {
//        marketClient.subMbpIncrementalUpdate(SubMbpIncrementalUpdateRequest
//                .builder().symbol(symbol).build(), event -> System.out.println(event.toString()));
//    }
//
//
//}
