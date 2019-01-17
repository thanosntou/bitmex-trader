package com.ntouzidis.cooperative.module.trade;

import com.ntouzidis.cooperative.module.bitmex.BitmexService;
import com.ntouzidis.cooperative.module.common.builder.DataDeleteOrderBuilder;
import com.ntouzidis.cooperative.module.common.builder.DataPostLeverage;
import com.ntouzidis.cooperative.module.common.builder.DataPostOrderBuilder;
import com.ntouzidis.cooperative.module.common.builder.SignalBuilder;
import com.ntouzidis.cooperative.module.user.entity.User;
import com.ntouzidis.cooperative.module.user.service.UserService;
import net.bull.javamelody.internal.model.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TradeService {

    Logger logger = LoggerFactory.getLogger(TradeService.class);

    private final BitmexService bitmexService;
    private final UserService userService;

    public TradeService(BitmexService bitmexService,
                        UserService userService) {
        this.bitmexService = bitmexService;
        this.userService = userService;
    }

    public void placeOrderAll(User trader, DataPostLeverage dataPostLeverage, DataPostOrderBuilder dataPostOrder) {
        List<User> enabledfollowers = getEnabledFollowers(trader);

        enabledfollowers.forEach(customer -> {
            bitmexService.post_Position_Leverage(customer, dataPostLeverage);

            bitmexService.post_Order_Order_WithFixeds(customer, dataPostOrder);
        });
    }

    public void createSignal(User trader, SignalBuilder sb) {
        List<User> enabledfollowers = getEnabledFollowers(trader);

        DataPostLeverage dataLeverage = new DataPostLeverage()
                .withSymbol(sb.getSymbol())
                .withLeverage(sb.getLeverage());

        enabledfollowers.forEach(customer -> {
            //            1. Set Leverage
            bitmexService.post_Position_Leverage(customer, dataLeverage);

            //            2. Market
            DataPostOrderBuilder marketDataOrder = new DataPostOrderBuilder()
                    .withOrderType("Market")
                    .withSymbol(sb.getSymbol())
                    .withSide(sb.getSide())
                    .withText("Bitcoin Syndicate");

            bitmexService.post_Order_Order_WithFixeds(customer, marketDataOrder);


            //            3. Stop Market
            if (sb.getStopLoss() != null) {
                DataPostOrderBuilder stopMarketDataOrder = new DataPostOrderBuilder()
                        .withOrderType("Stop")
                        .withSymbol(sb.getSymbol())
                        .withSide(sb.getSide().equals("Buy")?"Sell":"Buy")
                        .withExecInst("Close,LastPrice")
                        .withStopPrice(sb.getStopLoss())
                        .withText("Bitcoin Syndicate");

                bitmexService.post_Order_Order_WithFixeds(customer, stopMarketDataOrder);
            }

            //            4. Limit
            if (sb.getProfitTrigger() != null) {
                DataPostOrderBuilder limitDataOrder = new DataPostOrderBuilder()
                        .withOrderType("Limit")
                        .withSymbol(sb.getSymbol())
                        .withSide(sb.getSide().equals("Buy")?"Sell":"Buy")
                        .withPrice(sb.getProfitTrigger())
                        .withText("Bitcoin Syndicate");

                bitmexService.post_Order_Order_WithFixeds(customer, limitDataOrder);
            }
        });
    }

    public List<Map<String, Object>> getRandomActiveOrders(User trader) {
        List<Map<String, Object>> randomAllOrders;

        LinkedList<User> followers = new LinkedList<>(getEnabledFollowers(trader));

        for (User f: followers) {
            randomAllOrders = bitmexService.get_Order_Order(f);

            if (randomAllOrders != null)
                return randomAllOrders.stream()
                        .filter(i -> i.get("ordStatus").equals("New"))
                        .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    public List<Map<String, Object>> getRandomPositions(User trader) {
        List<Map<String, Object>> randomPositions;

        List<User> enabledfollowers = getEnabledFollowers(trader);

        for (User f: enabledfollowers) {
            randomPositions = bitmexService.get_Position(f);

            if (randomPositions != null)
                return randomPositions;
        }

        return Collections.emptyList();
    }

    public List<Map<String, Object>> getRandomTX(User trader) {
        List<Map<String, Object>> randomTX;

        List<User> enabledfollowers = getEnabledFollowers(trader);

        for (User f: enabledfollowers) {
            randomTX = bitmexService.get_Order_Order(f);

            if (!randomTX.isEmpty())
                return randomTX;
        }

        return Collections.emptyList();
    }

    void cancelOrder(User trader, DataDeleteOrderBuilder dataDeleteOrderBuilder) {
        List<User> enabledfollowers = getEnabledFollowers(trader);

        enabledfollowers.forEach(customer -> bitmexService.cancelOrder(customer, dataDeleteOrderBuilder));
    }

    public void cancelAllOrders(User trader, DataDeleteOrderBuilder dataDeleteOrderBuilder) {
        List<User> enabledfollowers = getEnabledFollowers(trader);

        enabledfollowers.forEach(customer -> bitmexService.cancelAllOrders(customer, dataDeleteOrderBuilder));
    }

    void positionAll(User trader, DataPostOrderBuilder dataPostOrderBuilder, int percentage) {
        List<User> enabledfollowers = getEnabledFollowers(trader);

        enabledfollowers.forEach(customer -> bitmexService.post_Order_Order_WithFixedsAndPercentage(customer, dataPostOrderBuilder, percentage));
    }

    void closeAllPosition(User trader, DataPostOrderBuilder dataPostOrderBuilder) {
        List<User> enabledfollowers = getEnabledFollowers(trader);

        enabledfollowers.forEach(customer -> bitmexService.post_Order_Order(customer, dataPostOrderBuilder));
    }

    Map<String, Long> calculateSumFixedQtys(List<User> followers) {
        Map<String, Long> sumFixedQtys = new HashMap<>();
        sumFixedQtys.put("XBTUSD", 0L);
        sumFixedQtys.put("XBTJPY", 0L);
        sumFixedQtys.put("ADAZ18", 0L);
        sumFixedQtys.put("BCHZ18", 0L);
        sumFixedQtys.put("EOSZ18", 0L);
        sumFixedQtys.put("ETHUSD", 0L);
        sumFixedQtys.put("LTCZ18", 0L);
        sumFixedQtys.put("TRXZ18", 0L);
        sumFixedQtys.put("XRPZ18", 0L);
        sumFixedQtys.put("XBTKRW", 0L);

        followers.forEach(f -> {
            sumFixedQtys.put("XBTUSD", sumFixedQtys.get("XBTUSD") + f.getFixedQtyXBTUSD());
            sumFixedQtys.put("XBTJPY", sumFixedQtys.get("XBTJPY") + f.getFixedQtyXBTJPY());
            sumFixedQtys.put("ADAZ18", sumFixedQtys.get("ADAZ18") + f.getFixedQtyADAZ18());
            sumFixedQtys.put("BCHZ18", sumFixedQtys.get("BCHZ18") + f.getFixedQtyBCHZ18());
            sumFixedQtys.put("EOSZ18", sumFixedQtys.get("EOSZ18") + f.getFixedQtyEOSZ18());
            sumFixedQtys.put("ETHUSD", sumFixedQtys.get("ETHUSD") + f.getFixedQtyETHUSD());
            sumFixedQtys.put("LTCZ18", sumFixedQtys.get("LTCZ18") + f.getFixedQtyLTCZ18());
            sumFixedQtys.put("TRXZ18", sumFixedQtys.get("TRXZ18") + f.getFixedQtyTRXZ18());
            sumFixedQtys.put("XRPZ18", sumFixedQtys.get("XRPZ18") + f.getFixedQtyXRPZ18());
            sumFixedQtys.put("XBTKRW", sumFixedQtys.get("XBTKRW") + f.getFixedQtyXBTKRW());
        });

        return sumFixedQtys;
    }

    Map<String, Double> calculateSumPositions(List<User> followers) {
        Map<String, Double> sumPositions = new HashMap<>();


        try {
            sumPositions.put("XBTUSD", (double) 0);
            sumPositions.put("XBTJPY", (double) 0);
            sumPositions.put("ADAZ18", (double) 0);
            sumPositions.put("BCHZ18", (double) 0);
            sumPositions.put("EOSZ18", (double) 0);
            sumPositions.put("ETHUSD", (double) 0);
            sumPositions.put("LTCZ18", (double) 0);
            sumPositions.put("TRXZ18", (double) 0);
            sumPositions.put("XRPZ18", (double) 0);
            sumPositions.put("XBTKRW", (double) 0);

            followers.forEach(f -> bitmexService.getAllSymbolPosition(f)
                    .stream()
                    .filter(Objects::nonNull)
                    .forEach(map -> {
                        String sym = map.get("symbol").toString();
                        sumPositions.put(sym, sumPositions.get(sym) + Double.parseDouble(map.get("currentQty").toString()));
                    })
            );

        } catch (Exception ex){
            logger.debug("Eskase h calculateSumPositions. Cause", ex.getCause());
            logger.debug("Eskase h calculateSumPositions, Stacktrace", ex.getStackTrace());
            logger.debug("Eskase h calculateSumPositions", ex);

        }



        return sumPositions;
    }

    private List<User> getEnabledFollowers(User trader) {
        return userService.getFollowers(trader)
                .stream()
                .filter(User::getEnabled)
                .collect(Collectors.toList());
    }

}
