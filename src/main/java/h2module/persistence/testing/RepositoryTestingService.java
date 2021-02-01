package h2module.persistence.testing;


import h2module.persistence.h2.converter.ExchangeOrderConverter;
import h2module.persistence.h2.file_storage.DataFileStorage;
import h2module.persistence.h2.model.LocalStorageEntity;
import h2module.persistence.postgres.model.ExchangeOrder;
import h2module.persistence.postgres.service.ExchangeOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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


    @PostConstruct
    private void initTest(){
        testEverything();
    }

    private void testEverything() {
        try {
            if (!testh2Save(10)) System.err.println("testh2Save has failed");
            if (!testH2LoadAll()) System.err.println("testH2LoadAll has failed");
            if (!testPsqlSave()) System.err.println("postgresql save has failed");
            if (!testPsqlLoadAll()) System.err.println("postgresql load all has failed");
//            if(!TestPsqlDeleteAll()) System.err.println("postgresql delete all has failed");
//            if(!testH2DeleteAll()) System.err.println("h2 delete all has failed");
        } catch (Exception e){
            log.error(e.getMessage());
        }
    }

    private boolean testh2Save(int i) {
        testH2DeleteAll();
        try {
            List<LocalStorageEntity> orderList = new ArrayList<>(i);
            for (int j = 0; j < i; j++) {
                orderList.add(ExchangeOrderConverter.convertToLocalEntity(orderProducer.getLimitBitOrderWithAllFieldsForTesting()));
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
        try{
            postgresStorage.deleteAll();
            ordersFromH2Db.forEach((key, value) -> {
                postgresStorage.save(ExchangeOrderConverter.convertFromLocalEntity(value));
            });
            Thread.sleep(200);
        } catch (Exception e){

        }
        System.err.println("postgre count : " + postgresStorage.getRepoCount()+ " h2 count map : " + ordersFromH2Db.size());
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
