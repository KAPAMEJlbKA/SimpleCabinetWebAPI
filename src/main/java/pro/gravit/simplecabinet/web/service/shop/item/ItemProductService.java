package pro.gravit.simplecabinet.web.service.shop.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.gravit.simplecabinet.web.exception.BalanceException;
import pro.gravit.simplecabinet.web.exception.InvalidParametersException;
import pro.gravit.simplecabinet.web.model.shop.*;
import pro.gravit.simplecabinet.web.model.user.User;
import pro.gravit.simplecabinet.web.repository.shop.ItemOrderRepository;
import pro.gravit.simplecabinet.web.repository.shop.ItemProductRepository;
import pro.gravit.simplecabinet.web.service.shop.ShopService;
import pro.gravit.simplecabinet.web.service.shop.item.delivery.ItemDeliveryService;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ItemProductService {
    @Autowired
    private ItemProductRepository repository;
    @Autowired
    private ItemOrderRepository orderRepository;
    @Autowired
    private ShopService shopService;
    @Autowired
    private ItemDeliveryService deliveryService;

    public <S extends ItemProduct> S save(S entity) {
        return repository.save(entity);
    }

    public Optional<ItemProduct> findById(Long aLong) {
        return repository.findById(aLong);
    }

    public Page<ItemProduct> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<ItemProduct> findAllAvailable(Pageable pageable) {
        return repository.findAllByAvailable(pageable, true);
    }

    @Transactional
    public ItemOrder createItemOrder(ItemProduct product, long quantity, User user) {
            LocalDateTime now = LocalDateTime.now();

            if (product.getEndDate() != null && product.getEndDate().isBefore(now)) {
                throw new InvalidParametersException("Product expired", 3);
            }

            if (product.getCount() > 0) {
                int updated = repository.decreaseCount(product.getId(), quantity);
                if (updated == 0) {
                    throw new InvalidParametersException("Not enough product available", 4);
                }
            }

        var order = new ItemOrder();
        shopService.fillBasicOrderProperties(order, quantity, user);
        order.setProduct(product);
        shopService.makeTransaction(order, product);
        orderRepository.save(order);
        return order;
    }

    public void delivery(ItemOrder order) {
        shopService.fillProcessDeliveryOrderProperties(order);
        orderRepository.save(order);
        deliveryService.delivery(order);
    }
}
