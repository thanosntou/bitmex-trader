package com.ntouzidis.cooperative.module.service;

import com.ntouzidis.cooperative.module.common.builder.DataDeleteOrderBuilder;
import com.ntouzidis.cooperative.module.common.builder.DataPostLeverage;
import com.ntouzidis.cooperative.module.common.builder.DataPostOrderBuilder;
import com.ntouzidis.cooperative.module.common.builder.SignalBuilder;
import com.ntouzidis.cooperative.module.common.enumeration.OrderType;
import com.ntouzidis.cooperative.module.common.enumeration.Side;
import com.ntouzidis.cooperative.module.common.enumeration.Symbol;
import com.ntouzidis.cooperative.module.user.entity.User;
import com.ntouzidis.cooperative.module.user.service.UserService;
import org.apache.http.concurrent.BasicFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
public class TradeService {

    Logger logger = LoggerFactory.getLogger(TradeService.class);

    private final BitmexService bitmexService;
    private final UserService userService;
    private final ExecutorService multiExecutor;

    public TradeService(
            BitmexService bitmexService,
            UserService userService,
            ExecutorService multiExecutor
    ) {
        this.bitmexService = bitmexService;
        this.userService = userService;
        this.multiExecutor = multiExecutor;
    }

    public void placeOrderAll(User trader, DataPostLeverage dataPostLeverage, DataPostOrderBuilder dataPostOrder) {
        List<User> enabledfollowers = userService.getEnabledFollowers(trader);

        String uniqueclOrdID1 = UUID.randomUUID().toString();

        String lastPrice = getSymbolLastPrice(dataPostOrder.getSymbol());

        Future<?> future = null;
        for (User follower: enabledfollowers) {
            future = multiExecutor.submit(() -> {
                try {
                    bitmexService.post_Position_Leverage(follower, dataPostLeverage);

                    if (OrderType.Stop.equals(dataPostOrder.getOrderType()) || OrderType.StopLimit.equals(dataPostOrder.getOrderType())) {
                        dataPostOrder.withOrderQty(String.valueOf(Math.abs((Integer)
                                bitmexService.getSymbolPosition(follower, dataPostLeverage.getSymbol()).get("execQty")))
                        );
                        if ("0".equals(dataPostOrder.getOrderQty())) {
                            dataPostOrder.withOrderQty(
                                    calculateFixedQtyForSymbol(
                                            follower, dataPostOrder.getSymbol(), dataPostLeverage.getLeverage(), lastPrice
                                    )
                            );
                        }
                    } else {
                        dataPostOrder.withOrderQty(
                                calculateFixedQtyForSymbol(
                                        follower, dataPostOrder.getSymbol(), dataPostLeverage.getLeverage(), lastPrice
                                )
                        );
                    }
                    bitmexService.post_Order_Order_WithFixeds(follower, dataPostOrder.withClOrdId(uniqueclOrdID1));

                } catch (Exception e) {
                    logger.error("Order failed for follower: " + follower.getUsername());
                }
            });
        }
        Optional.ofNullable(future).ifPresent(fut -> {
            try {
                fut.get();
                if (fut.isDone()) {
                    System.out.println("Bravooooooo");
                }

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    public void createSignal(User trader, SignalBuilder sb) {
        List<User> enabledfollowers = userService.getEnabledFollowers(trader);

        // unique id's to mark the following orders
        String uniqueclOrdID1 = UUID.randomUUID().toString();
        String uniqueclOrdID2 = UUID.randomUUID().toString();
        String uniqueclOrdID3 = UUID.randomUUID().toString();

        DataPostLeverage dataLeverage = new DataPostLeverage()
                .withSymbol(sb.getSymbol())
                .withLeverage(sb.getLeverage());

        enabledfollowers.forEach(customer -> {
            try {
                // 1. Set Leverage
                bitmexService.post_Position_Leverage(customer, dataLeverage);

                // 2. Market
                DataPostOrderBuilder marketDataOrder = new DataPostOrderBuilder()
                        .withClOrdId(uniqueclOrdID1)
                        .withOrderType(OrderType.Market)
                        .withSymbol(sb.getSymbol())
                        .withSide(sb.getSide());

                bitmexService.post_Order_Order_WithFixeds(customer, marketDataOrder);

                // 3. Stop Market
                if (sb.getStopLoss() != null) {
                    DataPostOrderBuilder stopMarketDataOrder = new DataPostOrderBuilder()
                            .withClOrdId(uniqueclOrdID2)
                            .withOrderType(OrderType.Stop)
                            .withSymbol(sb.getSymbol())
                            .withSide(sb.getSide().equals(Side.Buy) ? Side.Sell : Side.Buy)
                            .withExecInst("Close,LastPrice")
                            .withStopPrice(sb.getStopLoss());

                    bitmexService.post_Order_Order_WithFixeds(customer, stopMarketDataOrder);
                }

                // 4. Limit
                if (sb.getProfitTrigger() != null) {
                    DataPostOrderBuilder limitDataOrder = new DataPostOrderBuilder()
                            .withClOrdId(uniqueclOrdID3)
                            .withOrderType(OrderType.Limit)
                            .withSymbol(sb.getSymbol())
                            .withSide(sb.getSide().equals(Side.Buy) ? Side.Sell : Side.Buy)
                            .withPrice(sb.getProfitTrigger());

                    bitmexService.post_Order_Order_WithFixeds(customer, limitDataOrder);
                }
            } catch (Exception e) {
                logger.error("Order failed for follower: " + customer.getUsername());
            }
        });
    }

    public List<Map<String, Object>> getRandomActiveOrders(User trader) {
        return getRandomActiveOrdersOf(userService.getGuideFollower(trader));
    }

    public List<Map<String, Object>> getRandomActiveOrdersOf(User user) {
        return bitmexService.get_Order_Order(user)
                .stream()
                .filter(order -> Arrays.stream(Symbol.values())
                        .map(Symbol::getValue)
                        .collect(Collectors.toList())
                        .contains(order.get("symbol").toString()))
                .filter(i -> i.get("ordStatus").equals("New"))
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getRandomPositions(User trader) {
        return getRandomPositionsOf(userService.getGuideFollower(trader));
    }

    public List<Map<String, Object>> getRandomPositionsOf(User user) {
        return bitmexService.get_Position(user)
                .stream()
                .filter(pos -> Arrays.stream(Symbol.values())
                        .map(Symbol::getValue)
                        .collect(Collectors.toList())
                        .contains(pos.get("symbol").toString()))
                .filter(pos -> pos.get("avgEntryPrice") != null)
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getRandomTX(User trader) {
        List<Map<String, Object>> randomTX;

        List<User> enabledfollowers = userService.getEnabledFollowers(trader);

        for (User f: enabledfollowers) {
            randomTX = bitmexService.get_Order_Order(f);

            if (!randomTX.isEmpty())
                return randomTX;
        }

        return Collections.emptyList();
    }

    public void cancelOrder(User trader, DataDeleteOrderBuilder dataDeleteOrderBuilder) {
        List<User> enabledfollowers = userService.getEnabledFollowers(trader);

        enabledfollowers.forEach(customer -> bitmexService.cancelOrder(customer, dataDeleteOrderBuilder));
    }

    public void cancelAllOrders(User trader, DataDeleteOrderBuilder dataDeleteOrderBuilder) {
        List<User> enabledfollowers = userService.getEnabledFollowers(trader);

        enabledfollowers.forEach(customer -> bitmexService.cancelAllOrders(customer, dataDeleteOrderBuilder));
    }

    public void postOrder2(User trader, DataPostOrderBuilder dataPostOrderBuilder, int percentage) {
        List<User> enabledfollowers = userService.getEnabledFollowers(trader);

        enabledfollowers.forEach(customer -> bitmexService.post_Order_Order_WithFixedsAndPercentage(customer, dataPostOrderBuilder, percentage));
    }

    public void closeAllPosition(User trader, DataPostOrderBuilder dataPostOrderBuilder) {
        List<User> enabledfollowers = userService.getEnabledFollowers(trader);

        enabledfollowers.forEach(customer -> bitmexService.post_Order_Order(customer, dataPostOrderBuilder));
    }

    Map<String, Double> calculateSumFixedQtys(List<User> followers) {
        Map<String, Double> sumFixedQtys = new HashMap<>();
        Arrays.stream(Symbol.values()).forEach(symbol -> sumFixedQtys.put(symbol.getValue(), (double) 0));

        followers.forEach(f -> {
            sumFixedQtys.put(Symbol.XBTUSD.getValue(), sumFixedQtys.get(Symbol.XBTUSD.getValue()) + f.getFixedQtyXBTUSD());
            sumFixedQtys.put(Symbol.ETHUSD.getValue(), sumFixedQtys.get(Symbol.ETHUSD.getValue()) + f.getFixedQtyXBTJPY());
            sumFixedQtys.put(Symbol.ADAXXX.getValue(), sumFixedQtys.get(Symbol.ADAXXX.getValue()) + f.getFixedQtyADAZ18());
            sumFixedQtys.put(Symbol.BCHXXX.getValue(), sumFixedQtys.get(Symbol.BCHXXX.getValue()) + f.getFixedQtyBCHZ18());
            sumFixedQtys.put(Symbol.EOSXXX.getValue(), sumFixedQtys.get(Symbol.EOSXXX.getValue()) + f.getFixedQtyEOSZ18());
            sumFixedQtys.put(Symbol.ETHXXX.getValue(), sumFixedQtys.get(Symbol.ETHXXX.getValue()) + f.getFixedQtyETHUSD());
            sumFixedQtys.put(Symbol.LTCXXX.getValue(), sumFixedQtys.get(Symbol.LTCXXX.getValue()) + f.getFixedQtyLTCZ18());
            sumFixedQtys.put(Symbol.TRXXXX.getValue(), sumFixedQtys.get(Symbol.TRXXXX.getValue()) + f.getFixedQtyTRXZ18());
            sumFixedQtys.put(Symbol.XRPXXX.getValue(), sumFixedQtys.get(Symbol.XRPXXX.getValue()) + f.getFixedQtyXRPZ18());
        });

        return sumFixedQtys;
    }

    Map<String, Double> calculateSumPositions(List<User> followers) {
        Map<String, Double> sumPositions = new HashMap<>();
        Arrays.stream(Symbol.values()).forEach(symbol -> sumPositions.put(symbol.getValue(), (double) 0));

        try {
            followers.forEach(f -> bitmexService.getAllSymbolPosition(f)
                    .stream()
                    .filter(Objects::nonNull)
                    .forEach(map -> {
                        String sym = map.get("symbol").toString();
                        sumPositions.put(sym, sumPositions.get(sym) + Double.parseDouble(map.get("currentQty").toString()));
                    })
            );
        } catch (Exception ex){
            logger.debug("Eskase h calculateSumPositions", ex);
        }
        return sumPositions;
    }

    private String getSymbolLastPrice(Symbol symbol) {
        return bitmexService.getInstrumentLastPrice(
                userService.findCustomer("pelaths").orElseThrow(() ->
                        new RuntimeException("Customer not found")
                ),
                Symbol.valueOf(symbol.getValue())
        );
    }

    private String calculateFixedQtyForSymbol(User user, Symbol symbol, String leverage, String lastPrice) {
        if (symbol.equals(Symbol.XBTUSD))
            return calculateOrderQty(user, user.getFixedQtyXBTUSD(), leverage, lastPrice);
        if (symbol.equals(Symbol.ETHUSD))
            return calculateOrderQtyETHUSD(user, user.getFixedQtyETHUSD(), leverage);
        if (symbol.equals(Symbol.ADAXXX))
            return calculateOrderQty(user, user.getFixedQtyADAZ18(), leverage, lastPrice);
        if (symbol.equals(Symbol.BCHXXX))
            return calculateOrderQty(user, user.getFixedQtyBCHZ18(), leverage, lastPrice);
        if (symbol.equals(Symbol.EOSXXX))
            return calculateOrderQty(user, user.getFixedQtyEOSZ18(), leverage, lastPrice);
        if (symbol.equals(Symbol.ETHXXX))
            return calculateOrderQty(user, user.getFixedQtyXBTJPY(), leverage, lastPrice);
        //TODO fix these ethh19
        if (symbol.equals(Symbol.LTCXXX))
            return calculateOrderQty(user, user.getFixedQtyLTCZ18(), leverage, lastPrice);
        if (symbol.equals(Symbol.TRXXXX))
            return calculateOrderQty(user, user.getFixedQtyTRXZ18(), leverage, lastPrice);
        if (symbol.equals(Symbol.XRPXXX))
            return calculateOrderQty(user, user.getFixedQtyXRPZ18(), leverage, lastPrice);

        throw new RuntimeException("Fixed qty user calculation failed");
    }

    private String calculateOrderQty(User user, double fixedQty, String lev, String lastPrice) {
        return String.valueOf(Math.round(
                xbtAmount(user, fixedQty) * leverage(lev) * lastPrice(lastPrice)
        ));
    }

    private String calculateOrderQtyETHUSD(User user, double fixedQty, String lev) {
        return String.valueOf(Math.round(
                        ((fixedQty / 100 * Double.parseDouble(bitmexService.get_User_Margin(user).get("walletBalance").toString()) / 100000000)
                          * leverage(lev)
                        )
                        / (Double.parseDouble(bitmexService.getInstrumentLastPrice(user, Symbol.ETHUSD)) * 0.000001)
        ));
    }

    private Double lastPrice2(User user, Symbol symbol) {
        return Double.parseDouble(bitmexService.getInstrumentLastPrice(user, symbol)) * 0.000001;
    }

    private Double leverage(String leverage) {
        return Double.parseDouble(leverage);
    }

    private Double xbtAmount(User user, double fixedQty) {
        return fixedQty / 100 * Double.parseDouble(bitmexService.get_User_Margin(user).get("walletBalance").toString()) / 100000000;
    }

    private Double lastPrice(String leverage) {
        return Double.parseDouble(leverage);
    }

}