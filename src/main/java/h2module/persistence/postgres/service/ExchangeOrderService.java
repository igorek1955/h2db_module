package h2module.persistence.postgres.service;


import h2module.persistence.postgres.model.ExchangeOrder;
import h2module.persistence.postgres.repository.ExchangeOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ExchangeOrderService {

    @Autowired
    ExchangeOrderRepository exchangeOrderRepository;

    public void save(ExchangeOrder order) {
        log.info("saving to db order : " + order);
        exchangeOrderRepository.save(order);
    }

    public void deleteById(long id) {
        exchangeOrderRepository.deleteById(id);
    }

    public ExchangeOrder findById(long id) {
        Optional<ExchangeOrder> orderOptional = exchangeOrderRepository.findById(id);
        return orderOptional.orElseGet(ExchangeOrder::new);
    }

    public List<ExchangeOrder> findAll() {
        List<ExchangeOrder> orderList = new ArrayList<>();
        exchangeOrderRepository.findAll().forEach(orderList::add);
        return orderList;
    }

    public boolean deleteAll() {
        try {
            exchangeOrderRepository.deleteAll();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public int getRepoCount() {
        return (int) exchangeOrderRepository.count();
    }
}
