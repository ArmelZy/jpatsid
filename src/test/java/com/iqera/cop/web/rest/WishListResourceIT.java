package com.iqera.cop.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.iqera.cop.IntegrationTest;
import com.iqera.cop.domain.CustomerDetails;
import com.iqera.cop.domain.WishList;
import com.iqera.cop.repository.WishListRepository;
import com.iqera.cop.service.WishListService;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link WishListResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class WishListResourceIT {

    private static final Integer DEFAULT_NUMBER_OF_ELEMENT = 1;
    private static final Integer UPDATED_NUMBER_OF_ELEMENT = 2;

    private static final String ENTITY_API_URL = "/api/wish-lists";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private WishListRepository wishListRepository;

    @Mock
    private WishListRepository wishListRepositoryMock;

    @Mock
    private WishListService wishListServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restWishListMockMvc;

    private WishList wishList;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WishList createEntity(EntityManager em) {
        WishList wishList = new WishList().numberOfElement(DEFAULT_NUMBER_OF_ELEMENT);
        // Add required entity
        CustomerDetails customerDetails;
        if (TestUtil.findAll(em, CustomerDetails.class).isEmpty()) {
            customerDetails = CustomerDetailsResourceIT.createEntity(em);
            em.persist(customerDetails);
            em.flush();
        } else {
            customerDetails = TestUtil.findAll(em, CustomerDetails.class).get(0);
        }
        wishList.setCustomerDetails(customerDetails);
        return wishList;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WishList createUpdatedEntity(EntityManager em) {
        WishList wishList = new WishList().numberOfElement(UPDATED_NUMBER_OF_ELEMENT);
        // Add required entity
        CustomerDetails customerDetails;
        if (TestUtil.findAll(em, CustomerDetails.class).isEmpty()) {
            customerDetails = CustomerDetailsResourceIT.createUpdatedEntity(em);
            em.persist(customerDetails);
            em.flush();
        } else {
            customerDetails = TestUtil.findAll(em, CustomerDetails.class).get(0);
        }
        wishList.setCustomerDetails(customerDetails);
        return wishList;
    }

    @BeforeEach
    public void initTest() {
        wishList = createEntity(em);
    }

    @Test
    @Transactional
    void createWishList() throws Exception {
        int databaseSizeBeforeCreate = wishListRepository.findAll().size();
        // Create the WishList
        restWishListMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(wishList)))
            .andExpect(status().isCreated());

        // Validate the WishList in the database
        List<WishList> wishListList = wishListRepository.findAll();
        assertThat(wishListList).hasSize(databaseSizeBeforeCreate + 1);
        WishList testWishList = wishListList.get(wishListList.size() - 1);
        assertThat(testWishList.getNumberOfElement()).isEqualTo(DEFAULT_NUMBER_OF_ELEMENT);
    }

    @Test
    @Transactional
    void createWishListWithExistingId() throws Exception {
        // Create the WishList with an existing ID
        wishList.setId(1L);

        int databaseSizeBeforeCreate = wishListRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restWishListMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(wishList)))
            .andExpect(status().isBadRequest());

        // Validate the WishList in the database
        List<WishList> wishListList = wishListRepository.findAll();
        assertThat(wishListList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllWishLists() throws Exception {
        // Initialize the database
        wishListRepository.saveAndFlush(wishList);

        // Get all the wishListList
        restWishListMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(wishList.getId().intValue())))
            .andExpect(jsonPath("$.[*].numberOfElement").value(hasItem(DEFAULT_NUMBER_OF_ELEMENT)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllWishListsWithEagerRelationshipsIsEnabled() throws Exception {
        when(wishListServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restWishListMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(wishListServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllWishListsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(wishListServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restWishListMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(wishListRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getWishList() throws Exception {
        // Initialize the database
        wishListRepository.saveAndFlush(wishList);

        // Get the wishList
        restWishListMockMvc
            .perform(get(ENTITY_API_URL_ID, wishList.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(wishList.getId().intValue()))
            .andExpect(jsonPath("$.numberOfElement").value(DEFAULT_NUMBER_OF_ELEMENT));
    }

    @Test
    @Transactional
    void getNonExistingWishList() throws Exception {
        // Get the wishList
        restWishListMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingWishList() throws Exception {
        // Initialize the database
        wishListRepository.saveAndFlush(wishList);

        int databaseSizeBeforeUpdate = wishListRepository.findAll().size();

        // Update the wishList
        WishList updatedWishList = wishListRepository.findById(wishList.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedWishList are not directly saved in db
        em.detach(updatedWishList);
        updatedWishList.numberOfElement(UPDATED_NUMBER_OF_ELEMENT);

        restWishListMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedWishList.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedWishList))
            )
            .andExpect(status().isOk());

        // Validate the WishList in the database
        List<WishList> wishListList = wishListRepository.findAll();
        assertThat(wishListList).hasSize(databaseSizeBeforeUpdate);
        WishList testWishList = wishListList.get(wishListList.size() - 1);
        assertThat(testWishList.getNumberOfElement()).isEqualTo(UPDATED_NUMBER_OF_ELEMENT);
    }

    @Test
    @Transactional
    void putNonExistingWishList() throws Exception {
        int databaseSizeBeforeUpdate = wishListRepository.findAll().size();
        wishList.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWishListMockMvc
            .perform(
                put(ENTITY_API_URL_ID, wishList.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(wishList))
            )
            .andExpect(status().isBadRequest());

        // Validate the WishList in the database
        List<WishList> wishListList = wishListRepository.findAll();
        assertThat(wishListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchWishList() throws Exception {
        int databaseSizeBeforeUpdate = wishListRepository.findAll().size();
        wishList.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWishListMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(wishList))
            )
            .andExpect(status().isBadRequest());

        // Validate the WishList in the database
        List<WishList> wishListList = wishListRepository.findAll();
        assertThat(wishListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamWishList() throws Exception {
        int databaseSizeBeforeUpdate = wishListRepository.findAll().size();
        wishList.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWishListMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(wishList)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the WishList in the database
        List<WishList> wishListList = wishListRepository.findAll();
        assertThat(wishListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateWishListWithPatch() throws Exception {
        // Initialize the database
        wishListRepository.saveAndFlush(wishList);

        int databaseSizeBeforeUpdate = wishListRepository.findAll().size();

        // Update the wishList using partial update
        WishList partialUpdatedWishList = new WishList();
        partialUpdatedWishList.setId(wishList.getId());

        partialUpdatedWishList.numberOfElement(UPDATED_NUMBER_OF_ELEMENT);

        restWishListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWishList.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedWishList))
            )
            .andExpect(status().isOk());

        // Validate the WishList in the database
        List<WishList> wishListList = wishListRepository.findAll();
        assertThat(wishListList).hasSize(databaseSizeBeforeUpdate);
        WishList testWishList = wishListList.get(wishListList.size() - 1);
        assertThat(testWishList.getNumberOfElement()).isEqualTo(UPDATED_NUMBER_OF_ELEMENT);
    }

    @Test
    @Transactional
    void fullUpdateWishListWithPatch() throws Exception {
        // Initialize the database
        wishListRepository.saveAndFlush(wishList);

        int databaseSizeBeforeUpdate = wishListRepository.findAll().size();

        // Update the wishList using partial update
        WishList partialUpdatedWishList = new WishList();
        partialUpdatedWishList.setId(wishList.getId());

        partialUpdatedWishList.numberOfElement(UPDATED_NUMBER_OF_ELEMENT);

        restWishListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWishList.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedWishList))
            )
            .andExpect(status().isOk());

        // Validate the WishList in the database
        List<WishList> wishListList = wishListRepository.findAll();
        assertThat(wishListList).hasSize(databaseSizeBeforeUpdate);
        WishList testWishList = wishListList.get(wishListList.size() - 1);
        assertThat(testWishList.getNumberOfElement()).isEqualTo(UPDATED_NUMBER_OF_ELEMENT);
    }

    @Test
    @Transactional
    void patchNonExistingWishList() throws Exception {
        int databaseSizeBeforeUpdate = wishListRepository.findAll().size();
        wishList.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWishListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, wishList.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(wishList))
            )
            .andExpect(status().isBadRequest());

        // Validate the WishList in the database
        List<WishList> wishListList = wishListRepository.findAll();
        assertThat(wishListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchWishList() throws Exception {
        int databaseSizeBeforeUpdate = wishListRepository.findAll().size();
        wishList.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWishListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(wishList))
            )
            .andExpect(status().isBadRequest());

        // Validate the WishList in the database
        List<WishList> wishListList = wishListRepository.findAll();
        assertThat(wishListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamWishList() throws Exception {
        int databaseSizeBeforeUpdate = wishListRepository.findAll().size();
        wishList.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWishListMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(wishList)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the WishList in the database
        List<WishList> wishListList = wishListRepository.findAll();
        assertThat(wishListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteWishList() throws Exception {
        // Initialize the database
        wishListRepository.saveAndFlush(wishList);

        int databaseSizeBeforeDelete = wishListRepository.findAll().size();

        // Delete the wishList
        restWishListMockMvc
            .perform(delete(ENTITY_API_URL_ID, wishList.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<WishList> wishListList = wishListRepository.findAll();
        assertThat(wishListList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
