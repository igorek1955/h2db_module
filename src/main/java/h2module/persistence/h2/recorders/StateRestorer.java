package h2module.persistence.h2.recorders;

import h2module.persistence.h2.converter.ObjectConverter;
import h2module.persistence.h2.file_storage.DataFileStorage;
import h2module.persistence.h2.model.LocalStorageEntity;
import h2module.persistence.postgres.model.ExchangeOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * on application startup restores open orders from h2 database.
 * First it downloads all ExchangeOrders from h2 in-memory database file.
 * Then downloads all Open ExchangeOrders from exchange servers.
 * Then downloads all orders history.
 * Then compares orders from db and orders from server information, and commits all changes to database for further persistence
 * Launches automatically on startup (Bean PostConstruct)
 *
 */
@Slf4j
@Service
public class StateRestorer {

    @Autowired
    DataFileStorage dataStorage;

    @Autowired
    ObjectConverter objectConverter;


    private Map<String, ExchangeOrder> ordersFromDb = new HashMap<>();
    private List<ExchangeOrder> ordersFromServer = new ArrayList<>();


//    @PostConstruct
    private void initRestoration() {
        initOrderMapAllOrders();
        getOpenOrdersFromServer();
        updateTrackedOpenOrders();
        restoreOrdersState();
    }

    /**
     * loading orders from database
     */
    private void initOrderMapAllOrders() {
        Map<String, LocalStorageEntity> tempOrders = dataStorage.loadAll();
        tempOrders.forEach((k, v) -> ordersFromDb.put(k, (ExchangeOrder) objectConverter.convertFromLocalEntity(v)));
        System.err.println(ordersFromDb.size());
        ordersFromDb.forEach((k, v) -> System.out.println(v));
    }

    /**
     * on application startup sending open orders to the service for tracking
     */
    private void updateTrackedOpenOrders() {
        //checking orders statuses
    }

    /**
     * getting open orders from server
     */
    private void getOpenOrdersFromServer() {
        // getting open orders from rest endpoint
    }

    /**
     * updating orders in db with information from servers.
     * used when application goes online and to keep db up-to-date
     */
    private void restoreOrdersState() {
        // getting orders history from rest endpoint
    }
}
