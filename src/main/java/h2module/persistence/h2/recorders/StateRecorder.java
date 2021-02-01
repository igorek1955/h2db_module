package h2module.persistence.h2.recorders;

import h2module.persistence.h2.converter.ObjectConverter;
import h2module.persistence.h2.file_storage.DataFileStorage;
import h2module.persistence.h2.model.LocalStorageEntity;
import h2module.persistence.postgres.model.ExchangeOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PreDestroy;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * class records all changes to ExchangeOrders and persists them to H2 in-memory database
 * Class is accessible only via static commitChange method. Works somewhat similar to github
 * First we commit - then we push to the server
 * Pushes occur every 10 milliseconds
 */
@Slf4j
@Configuration
@EnableScheduling
public class StateRecorder {

    //linked list - because last state of order matters the most
    private static ConcurrentLinkedDeque<LocalStorageEntity> updatedOrders = new ConcurrentLinkedDeque<>();
    public static long start = 0;

    @Autowired
    DataFileStorage fileStorage;

    @Autowired
    ObjectConverter objectConverter;

    /**
     * saving updated orders to h2 database every 10 millis , initial delay - delay operations from first startup
     */
    @Scheduled(fixedDelay = 10)
    private void pushToDb() {
        if (start == 0) {
            log.info("start initiated");
            start = System.currentTimeMillis();
        }

        if (updatedOrders.size() > 0) {
            LocalStorageEntity order = updatedOrders.getFirst();
            try {
                fileStorage.save(order);
                updatedOrders.remove(order);
//                log.warn("pushed changes to db.. remaining orders: " + updatedOrders.size());
            } catch (Exception e) {
                log.warn(e.getMessage());
            }
        } else {
//            System.err.println("finished writing to db in " + ((System.currentTimeMillis() - start) / 1000) + " secs");
        }
    }

    private void pushAllToDb() {
        updatedOrders.forEach(order -> {
            fileStorage.save(order);
            updatedOrders.remove(order);
        });
        log.warn("pushed remaining orders to db..");
    }

    /**
     * committing order changes to updatedOrders list on every order change (status, qty-filled etc.)
     *
     * @param order
     */
    public void commitChange(ExchangeOrder order) {
//        log.warn("committing to db order : " + order.getInternalOrderId() + " with status " + order.getStatus());
        log.warn("committing to db order: " + order.toString());
        updatedOrders.addLast(objectConverter.convertToLocalEntity(order, order.getInternalOrderId()));
    }


    @PreDestroy
    private void pushToDbOnExit() {
        pushAllToDb();
    }
}
