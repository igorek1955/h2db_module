package h2module.persistence.h2.file_storage;

import java.util.Map;

public interface IDataFileStorage<T> {
    boolean save(T order);
    boolean load(String key);
    boolean delete(String key);
    Map<String, T> loadAll();
    boolean deleteAll();
}
