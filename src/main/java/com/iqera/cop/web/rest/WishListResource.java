package com.iqera.cop.web.rest;

import com.iqera.cop.domain.WishList;
import com.iqera.cop.repository.WishListRepository;
import com.iqera.cop.service.WishListService;
import com.iqera.cop.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.iqera.cop.domain.WishList}.
 */
@RestController
@RequestMapping("/api/wish-lists")
public class WishListResource {

    private final Logger log = LoggerFactory.getLogger(WishListResource.class);

    private static final String ENTITY_NAME = "wishList";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final WishListService wishListService;

    private final WishListRepository wishListRepository;

    public WishListResource(WishListService wishListService, WishListRepository wishListRepository) {
        this.wishListService = wishListService;
        this.wishListRepository = wishListRepository;
    }

    /**
     * {@code POST  /wish-lists} : Create a new wishList.
     *
     * @param wishList the wishList to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new wishList, or with status {@code 400 (Bad Request)} if the wishList has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<WishList> createWishList(@Valid @RequestBody WishList wishList) throws URISyntaxException {
        log.debug("REST request to save WishList : {}", wishList);
        if (wishList.getId() != null) {
            throw new BadRequestAlertException("A new wishList cannot already have an ID", ENTITY_NAME, "idexists");
        }
        WishList result = wishListService.save(wishList);
        return ResponseEntity
            .created(new URI("/api/wish-lists/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /wish-lists/:id} : Updates an existing wishList.
     *
     * @param id the id of the wishList to save.
     * @param wishList the wishList to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated wishList,
     * or with status {@code 400 (Bad Request)} if the wishList is not valid,
     * or with status {@code 500 (Internal Server Error)} if the wishList couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<WishList> updateWishList(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody WishList wishList
    ) throws URISyntaxException {
        log.debug("REST request to update WishList : {}, {}", id, wishList);
        if (wishList.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, wishList.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!wishListRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        WishList result = wishListService.update(wishList);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, wishList.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /wish-lists/:id} : Partial updates given fields of an existing wishList, field will ignore if it is null
     *
     * @param id the id of the wishList to save.
     * @param wishList the wishList to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated wishList,
     * or with status {@code 400 (Bad Request)} if the wishList is not valid,
     * or with status {@code 404 (Not Found)} if the wishList is not found,
     * or with status {@code 500 (Internal Server Error)} if the wishList couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<WishList> partialUpdateWishList(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody WishList wishList
    ) throws URISyntaxException {
        log.debug("REST request to partial update WishList partially : {}, {}", id, wishList);
        if (wishList.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, wishList.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!wishListRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<WishList> result = wishListService.partialUpdate(wishList);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, wishList.getId().toString())
        );
    }

    /**
     * {@code GET  /wish-lists} : get all the wishLists.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of wishLists in body.
     */
    @GetMapping("")
    public ResponseEntity<List<WishList>> getAllWishLists(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false, defaultValue = "true") boolean eagerload
    ) {
        log.debug("REST request to get a page of WishLists");
        Page<WishList> page;
        if (eagerload) {
            page = wishListService.findAllWithEagerRelationships(pageable);
        } else {
            page = wishListService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /wish-lists/:id} : get the "id" wishList.
     *
     * @param id the id of the wishList to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the wishList, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<WishList> getWishList(@PathVariable Long id) {
        log.debug("REST request to get WishList : {}", id);
        Optional<WishList> wishList = wishListService.findOne(id);
        return ResponseUtil.wrapOrNotFound(wishList);
    }

    /**
     * {@code DELETE  /wish-lists/:id} : delete the "id" wishList.
     *
     * @param id the id of the wishList to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWishList(@PathVariable Long id) {
        log.debug("REST request to delete WishList : {}", id);
        wishListService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
