package pro.gravit.simplecabinet.web.utils;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pro.gravit.simplecabinet.web.model.shop.GroupProduct;
import pro.gravit.simplecabinet.web.model.shop.ItemProduct;
import pro.gravit.simplecabinet.web.repository.shop.GroupProductRepository;
import pro.gravit.simplecabinet.web.repository.shop.ItemProductRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ExpiredProductScheduler {
    private final GroupProductRepository groupProductRepository;
    private final ItemProductRepository itemProductRepository;

    public ExpiredProductScheduler(GroupProductRepository groupProductRepository, ItemProductRepository itemProductRepository) {
        this.groupProductRepository = groupProductRepository;
        this.itemProductRepository = itemProductRepository;
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void expireGroupProducts() {
        LocalDateTime now = LocalDateTime.now();
        List<GroupProduct> expiredGroups = groupProductRepository.findAllByEndDateBeforeAndAvailableTrue(now);
        for (GroupProduct product : expiredGroups) {
            product.setAvailable(false);
            groupProductRepository.save(product);
        }
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void expireItemProducts() {
        LocalDateTime now = LocalDateTime.now();
        List<ItemProduct> expiredItems = itemProductRepository.findAllByEndDateBeforeAndAvailableTrue(now);
        for (ItemProduct product : expiredItems) {
            product.setAvailable(false);
            itemProductRepository.save(product);
        }
    }
}

