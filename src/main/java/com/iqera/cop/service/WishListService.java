package com.iqera.cop.service;

import com.iqera.cop.domain.WishList;
import com.iqera.cop.repository.WishListRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.iqera.cop.domain.WishList}.
 */
@Service
@Transactional
public class WishListService {

    private final Logger log = LoggerFactory.getLogger(WishListService.class);

    private final WishListRepository wishListRepository;

    public WishListService(WishListRepository wishListRepository) {
        this.wishListRepository = wishListRepository;
    }

    /**
     * Save a wishList.
     *
     * @param wishList the entity to save.
     * @return the persisted entity.
     */
    public WishList save(WishList wishList) {
        log.debug("Request to save WishList : {}", wishList);
        return wishListRepository.save(wishList);
    }

    /**
     * Update a wishList.
     *
     * @param wishList the entity to save.
     * @return the persisted entity.
     */
    public WishList update(WishList wishList) {
        log.debug("Request to update WishList : {}", wishList);
        return wishListRepository.save(wishList);
    }

    /**
     * Partially update a wishList.
     *
     * @param wishList the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<WishList> partialUpdate(WishList wishList) {
        log.debug("Request to partially update WishList : {}", wishList);

        return wishListRepository
            .findById(wishList.getId())
            .map(existingWishList -> {
                if (wishList.getNumberOfElement() != null) {
                    existingWishList.setNumberOfElement(wishList.getNumberOfElement());
                }

                return existingWishList;
            })
            .map(wishListRepository::save);
    }

    /**
     * Get all the wishLists.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<WishList> findAll(Pageable pageable) {
        log.debug("Request to get all WishLists");
        return wishListRepository.findAll(pageable);
    }

    /**
     * Get all the wishLists with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<WishList> findAllWithEagerRelationships(Pageable pageable) {
        return wishListRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one wishList by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<WishList> findOne(Long id) {
        log.debug("Request to get WishList : {}", id);
        return wishListRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the wishList by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete WishList : {}", id);
        wishListRepository.deleteById(id);
    }
}
