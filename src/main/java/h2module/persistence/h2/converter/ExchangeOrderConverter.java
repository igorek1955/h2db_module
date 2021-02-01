package h2module.persistence.h2.converter;

import h2module.persistence.h2.model.LocalStorageEntity;
import h2module.persistence.postgres.model.ExchangeOrder;
import lombok.extern.slf4j.Slf4j;
import org.msgpack.MessagePack;

/**
 * utility class for class conversion for use with H2 in-memory database and other databases.
 * h2 accepts any type of object if we mark it by @Message annotation and register it with MessagePack
 *
 */
@Slf4j
public class ExchangeOrderConverter {

    private static final MessagePack msgPack = new MessagePack();
    private static boolean registered = false;


    public static LocalStorageEntity convertToLocalEntity(ExchangeOrder order){
        LocalStorageEntity entity = new LocalStorageEntity();

        if(!registered){
            msgPack.register(ExchangeOrder.class);
            registered = true;
        }

        try{
            entity.setInternalOrderId(order.getInternalOrderId());
            entity.setObject(msgPack.write(order));
        } catch (Exception e){
            log.error("error occurred when converting to local entity " + e.getMessage());
        }
        return entity;
    }

    public static ExchangeOrder convertFromLocalEntity(LocalStorageEntity entity){
        ExchangeOrder order = new ExchangeOrder();

        if(!registered){
            msgPack.register(ExchangeOrder.class);
            registered = true;
        }

        try{
            order = msgPack.read(entity.getObject(), ExchangeOrder.class);
            order.setInternalOrderId(entity.getInternalOrderId());
        } catch (Exception e){
            log.error("error occurred when converting from local entity " + e.getMessage());
        }

        return order;
    }
}
