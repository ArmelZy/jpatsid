package com.iqera.cop.repository;

import com.iqera.cop.domain.WishList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the WishList entity.
 */
@Repository
public interface WishListRepository extends JpaRepository<WishList, Long> {
    default Optional<WishList> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<WishList> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<WishList> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select wishList from WishList wishList left join fetch wishList.customerDetails",
        countQuery = "select count(wishList) from WishList wishList"
    )
    Page<WishList> findAllWithToOneRelationships(Pageable pageable);

    @Query("select wishList from WishList wishList left join fetch wishList.customerDetails")
    List<WishList> findAllWithToOneRelationships();

    @Query("select wishList from WishList wishList left join fetch wishList.customerDetails where wishList.id =:id")
    Optional<WishList> findOneWithToOneRelationships(@Param("id") Long id);
}
