package h2module.persistence.h2.file_storage;

import h2module.persistence.h2.model.LocalStorageEntity;
import h2module.persistence.h2.repository.LocalStorageEntityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * implementation of IDataFileStorage for persisting ExchangeOrder's to h2 database for backup.
 * Uses Spring Data Jpa for database persistance
 */
@Slf4j
@Service
public class DataFileStorage implements IDataFileStorage<LocalStorageEntity> {

    LocalStorageEntityRepository localStorageEntityRepository;

    public DataFileStorage(LocalStorageEntityRepository localStorageEntityRepository) {
        this.localStorageEntityRepository = localStorageEntityRepository;
    }

    @Override
    public boolean save(LocalStorageEntity order) {
        try {
            localStorageEntityRepository.save(order);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    //loading
    @Override
    public boolean load(String internalId) {
        return false;
    }


    @Override
    public boolean delete(String internalId) {
        try {
            localStorageEntityRepository.deleteById(internalId);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    @Override
    public Map<String, LocalStorageEntity> loadAll() {
        Map<String, LocalStorageEntity> orderMap = new HashMap<>();
        try {
            localStorageEntityRepository.findAll().forEach(order -> {
                log.info(order.toString());
                orderMap.put(order.getInternalOrderId(), order);
            });
            log.info("successfully imported orders from in-memory database");
            log.info("openOrdersMap.size = " + orderMap.size());
            return orderMap;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return orderMap;
    }

    @Override
    public boolean deleteAll() {
        try {
            log.warn("h2 orders before deletion : " + localStorageEntityRepository.count());
            localStorageEntityRepository.deleteAll();
            log.warn("h2 orders after deletion : " + localStorageEntityRepository.count());
            log.warn("deleted all data from h2 repository ");
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    public int getRepoCount() {
        return (int) localStorageEntityRepository.count();
    }
}
