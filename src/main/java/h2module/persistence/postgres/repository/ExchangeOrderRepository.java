package h2module.persistence.postgres.repository;


import h2module.persistence.postgres.model.ExchangeOrder;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExchangeOrderRepository extends CrudRepository<ExchangeOrder, Long> {
}
