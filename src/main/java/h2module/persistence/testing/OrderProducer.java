package h2module.persistence.testing;

import h2module.persistence.postgres.model.ExchangeOrder;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * Produces ExchangeOrder for testing
 *
 * Created by igor on 01 Feb, 2021
 */
@Service
public class OrderProducer {
    public ExchangeOrder getLimitBitOrderWithAllFieldsForTesting(){
        ExchangeOrder exchangeOrder = new ExchangeOrder();
        exchangeOrder.setExchangeOrderId(String.valueOf(new Random().nextInt(100000000)));
        exchangeOrder.setInternalOrderId(String.valueOf(new Random().nextInt(100000000)));
        exchangeOrder.setOrderType("MARKET");
        exchangeOrder.setCurrency("USD");
        exchangeOrder.setAction("BUY");
        return exchangeOrder;
    }
}
