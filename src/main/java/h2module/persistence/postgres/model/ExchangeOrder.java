package h2module.persistence.postgres.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.msgpack.annotation.Message;

import javax.persistence.*;


@Slf4j
@Data
@Entity
@Table(name = "exchange_order_test")
@Message
public class ExchangeOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="internal_order_id")
    private String internalOrderId; //Our own id
    @Column(name = "exchange_order_id")
    private String exchangeOrderId; //Exchange assigned id
    @Column
    private String symbol; //tradePair like BTCUSD
    @Column
    private String currency; // Currency BTCUSD -> USD
    @Column
    private String action; // SELL or BUY
    @Column(name="order_type")
    private String orderType; //LIMIT or MARKET
    @Column
    private String status; //Order status - submitted, cancelled, etc.
    @Column(name="total_quantity")
    private double totalQuantity; //Order size
    @Column(name="lmt_price")
    private double lmtPrice; //Order price
    @Column
    private double filled; //Current filled size
    @Column
    private double fees = 0; //Order fees@
    @Column
    private String message;
}
