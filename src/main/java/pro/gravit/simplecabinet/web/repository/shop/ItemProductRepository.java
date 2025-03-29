package pro.gravit.simplecabinet.web.repository.shop;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pro.gravit.simplecabinet.web.model.shop.ItemProduct;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemProductRepository extends JpaRepository<ItemProduct, Long> {

    Page<ItemProduct> findAllByAvailable(Pageable pageable, boolean available);

    @Modifying
    @Query("update ItemProduct gp set gp.count = gp.count - :quantity where gp.id = :id and gp.count >= :quantity")
    int decreaseCount(@Param("id") long id, @Param("quantity") long quantity);

    List<ItemProduct> findAllByEndDateBeforeAndAvailableTrue(LocalDateTime now);
}
