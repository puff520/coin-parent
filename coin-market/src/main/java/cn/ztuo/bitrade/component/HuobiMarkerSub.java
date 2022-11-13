package cn.ztuo.bitrade.component;

import cn.ztuo.bitrade.constant.NettyCommand;
import cn.ztuo.bitrade.consumer.ExchangeTradeConsumer;
import cn.ztuo.bitrade.entity.ExchangeOrder;
import cn.ztuo.bitrade.entity.ExchangeTrade;
import cn.ztuo.bitrade.entity.MarketDetail;
import cn.ztuo.bitrade.job.ExchangePushJob;
import cn.ztuo.bitrade.processor.CoinProcessor;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.huobi.client.MarketClient;
import com.huobi.client.req.market.SubMarketDetailRequest;
import com.huobi.constant.HuobiOptions;
import com.huobi.wss.event.MarketDetailSubResponse;
import com.huobi.wss.handle.WssMarketHandle;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class HuobiMarkerSub {

    private ExecutorService executor = new ThreadPoolExecutor(30, 100, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(1024), new ThreadPoolExecutor.AbortPolicy());
    MarketClient marketClient = MarketClient.create(new HuobiOptions());
    private String URL = "wss://api.hbdm.com/swap-ws";//合约站行情请求以及订阅地址
    WssMarketHandle wssMarketHandle = new WssMarketHandle(URL);

    @Autowired
    private ExchangePushJob pushJob;


    @PostConstruct
    private void subTopic() {
        executor.submit(new HuobiMarkerSub.HandleTradeThread());
    }

    public class HandleTradeThread implements Runnable {
        @Override
        public void run() {
            try {
                List<String> channels = Lists.newArrayList();
                channels.add("market.BTC-USD.detail");
                wssMarketHandle.sub(channels, response -> {
                    Long currentTimeMillis = System.currentTimeMillis();
                    MarketDetailSubResponse event = JSON.parseObject(response, MarketDetailSubResponse.class);
                    log.info("detailEvent的ts为：{},当前的时间戳为：{},时间间隔为：{}毫秒", event.getTs(), currentTimeMillis, currentTimeMillis - event.getTs());
                    pushJob.addMarketDetail("BTC-USD", new MarketDetail());
                });
                Thread.sleep(Integer.MAX_VALUE);
                //    pushJob.addTrades(symbol, trades);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
