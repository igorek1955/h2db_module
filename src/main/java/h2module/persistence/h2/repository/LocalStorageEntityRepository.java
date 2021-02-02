package h2module.persistence.h2.repository;


import h2module.persistence.h2.model.LocalStorageEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * spring data jpa repository
 */
@Repository
public interface LocalStorageEntityRepository extends CrudRepository<LocalStorageEntity, String> {

}
