package com.ntouzidis.cooperative.module.trade;

import com.ntouzidis.cooperative.module.bitmex.BitmexService;
import com.ntouzidis.cooperative.module.common.builder.DataPostLeverage;
import com.ntouzidis.cooperative.module.common.builder.DataPostOrderBuilder;
import com.ntouzidis.cooperative.module.common.builder.SignalBuilder;
import com.ntouzidis.cooperative.module.user.entity.User;
import com.ntouzidis.cooperative.module.user.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TradeService {

    private final BitmexService bitmexService;
    private final UserService userService;

    public TradeService(BitmexService bitmexService,
                        UserService userService) {
        this.bitmexService = bitmexService;
        this.userService = userService;
    }

    void createSignal(User trader, SignalBuilder sb) {
        List<User> followers = userService.getFollowers(trader);

        DataPostLeverage dataLeverageBuilder = new DataPostLeverage()
                .withSymbol(sb.getSymbol())
                .withLeverage(sb.getLeverage());

        followers.forEach(customer -> {
            DataPostOrderBuilder marketDataOrder = new DataPostOrderBuilder()
                    .withOrderType("Market")
                    .withSymbol(sb.getSymbol())
                    .withSide(sb.getSide())
                    .withOrderQty(customer.getFixedQtyADAZ18().toString())
                    .withText("Bitcoin Syndicate");

            //            1. Set Leverage
            bitmexService.post_Position_Leverage(customer, dataLeverageBuilder);

            //            2. Market
            bitmexService.post_Order_Order(customer, marketDataOrder);

            if (sb.getStopLoss() != null) {
                DataPostOrderBuilder stopMarketDataOrder = new DataPostOrderBuilder()
                        .withOrderType("Stop")
                        .withSymbol(sb.getSymbol())
                        .withSide(sb.getSide().equals("Buy")?"Sell":"Buy")
                        .withOrderQty(customer.getFixedQtyADAZ18().toString())
                        .withExecInst("Close,LastPrice")
                        .withStopPrice(sb.getStopLoss())
                        .withText("Bitcoin Syndicate");

                //            3. Stop Market
                bitmexService.post_Order_Order(customer, stopMarketDataOrder);
            }

            if (sb.getProfitTrigger() != null) {
                DataPostOrderBuilder limitDataOrder = new DataPostOrderBuilder()
                        .withOrderType("Limit")
                        .withSymbol(sb.getSymbol())
                        .withSide(sb.getSide().equals("Buy")?"Sell":"Buy")
                        .withOrderQty(customer.getFixedQtyADAZ18().toString())
                        .withPrice(sb.getProfitTrigger())
                        .withText("Bitcoin Syndicate");

                //            4. Limit
                bitmexService.post_Order_Order(customer, limitDataOrder);
            }
        });
    }

}
