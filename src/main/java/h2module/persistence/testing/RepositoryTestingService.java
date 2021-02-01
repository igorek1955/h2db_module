package h2module.persistence.testing;


import h2module.persistence.h2.converter.ObjectConverter;
import h2module.persistence.h2.file_storage.DataFileStorage;
import h2module.persistence.h2.model.LocalStorageEntity;
import h2module.persistence.postgres.model.ExchangeOrder;
import h2module.persistence.postgres.service.ExchangeOrderService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;


/**
 * class for testing CRUD operations of h2 in-memory database and postgresql.
 */
@Slf4j
@Service
public class RepositoryTestingService {

    private static Map<String, LocalStorageEntity> ordersFromH2Db = new HashMap<>();
    private static List<ExchangeOrder> ordersFromPostgres = new ArrayList<>();


    @Autowired
    DataFileStorage h2dataStorage;

    @Autowired
    ExchangeOrderService postgresStorage;

    @Autowired
    OrderProducer orderProducer;

    @Autowired
    ObjectConverter objectConverter;


    @PostConstruct
    private void initTest() {
//        testEverything();
//        initHeavyDBTest(10000, 10);
        testDb(30000);
    }

    private void testDb(int orders) {
        h2dataStorage.deleteAll();
        Set<LocalStorageEntity> orderSet = new HashSet<>(orders);
        for (int i = 0; i < orders; i++) {
            ExchangeOrder order = orderProducer.getLimitBitOrderWithAllFieldsForTesting();
            LocalStorageEntity entity = objectConverter.convertToLocalEntity(order, order.getInternalOrderId());
            if(entity.getId() == null) System.out.println("null");
            orderSet.add(entity);
        }
        ExchangeOrder orderSaved = (ExchangeOrder)  objectConverter.convertFromLocalEntity(orderSet.stream().findFirst().get());
        System.err.println("saved order : " + orderSaved);
        orderSet.forEach(order -> {
            if (!h2dataStorage.save(order)) {
                ExchangeOrder order1 = (ExchangeOrder) objectConverter.convertFromLocalEntity(order);
                log.error("order not saved: " + order1.toString());
            }
        });
        orderSet.forEach(order -> {
            if (!h2dataStorage.delete(order.getId())) {
                ExchangeOrder order1 = (ExchangeOrder) objectConverter.convertFromLocalEntity(order);
                log.error("order not deleted: " + order1.toString());
            }
        });
    }

    @SneakyThrows
    private void initHeavyDBTest(int orders, int loops) {
        h2dataStorage.deleteAll();
        for (int i = 0; i < loops; i++) {
            Set<LocalStorageEntity> orderSet = new HashSet<>(orders);
            for (int y = 0; y < orders; y++) {
                ExchangeOrder order = orderProducer.getLimitBitOrderWithAllFieldsForTesting();
                orderSet.add(objectConverter.convertToLocalEntity(order, order.getInternalOrderId()));
            }
            System.err.println("number of items in set to save : " + orderSet.size());
            long start = System.currentTimeMillis();
            orderSet.forEach(order -> h2dataStorage.save(order));
            System.err.println("finished saving orders to h2 db in " + ((System.currentTimeMillis() - start) / 1000) + " seconds");
            System.err.println("number of items in db : " + h2dataStorage.getRepoCount());
            System.err.println("commencing deletion....");
            long start2 = System.currentTimeMillis();
            orderSet.forEach(order -> {
                        if (!h2dataStorage.delete(order.getId())) {
                            ExchangeOrder order1 = (ExchangeOrder) objectConverter.convertFromLocalEntity(order);
                            log.error("order not deleted: " + order1.toString());
                        }
                    });
                    System.err.println("finished deleting orders from h2 db in " + ((System.currentTimeMillis() - start2) / 1000) + " seconds");
            System.err.println("number of items in db : " + h2dataStorage.getRepoCount());
        }
    }

    private void testEverything() {
        try {
            if (!testh2Save(10)) System.err.println("testh2Save has failed");
            if (!testH2LoadAll()) System.err.println("testH2LoadAll has failed");
            if (!testPsqlSave()) System.err.println("postgresql save has failed");
            if (!testPsqlLoadAll()) System.err.println("postgresql load all has failed");
            if (!TestPsqlDeleteAll()) System.err.println("postgresql delete all has failed");
            if (!testH2DeleteAll()) System.err.println("h2 delete all has failed");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private boolean testh2Save(int i) {
        testH2DeleteAll();
        try {
            List<LocalStorageEntity> orderList = new ArrayList<>(i);
            for (int j = 0; j < i; j++) {
                ExchangeOrder order = orderProducer.getLimitBitOrderWithAllFieldsForTesting();
                orderList.add(objectConverter.convertToLocalEntity(order, order.getInternalOrderId()));
            }
            orderList.forEach(order -> h2dataStorage.save(order));
            log.warn("all orders have been pushed to h2 database");

            return h2dataStorage.getRepoCount() == i;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    private boolean testH2DeleteAll() {
        return h2dataStorage.deleteAll();
    }

    private boolean TestPsqlDeleteAll() {
        return postgresStorage.deleteAll();
    }

    private boolean testPsqlLoadAll() {
        ordersFromPostgres.addAll(postgresStorage.findAll());
        System.err.println("postgre list count : " + ordersFromPostgres.size() + " orders on postgres server : " + postgresStorage.getRepoCount());
        return ordersFromPostgres.size() == postgresStorage.getRepoCount();
    }

    private boolean testPsqlSave() {
        try {
            postgresStorage.deleteAll();
            ordersFromH2Db.forEach((key, value) -> {
                ExchangeOrder order = (ExchangeOrder) objectConverter.convertFromLocalEntity(value);
                postgresStorage.save(order);
            });
            Thread.sleep(200);
        } catch (Exception e) {

        }
        System.err.println("postgre count : " + postgresStorage.getRepoCount() + " h2 count map : " + ordersFromH2Db.size());
        return postgresStorage.getRepoCount() == ordersFromH2Db.size();
    }


    private boolean testH2LoadAll() {
        try {
            long start = System.currentTimeMillis();
            ordersFromH2Db = h2dataStorage.loadAll();
            System.err.println("all loaded in " + (System.currentTimeMillis() - start) / 1000 + " secs");
            return ordersFromH2Db.size() == h2dataStorage.getRepoCount();
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }
}
