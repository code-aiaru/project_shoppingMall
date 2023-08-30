package org.project.dev.product.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.project.dev.product.dto.ProductDTO;
import org.project.dev.product.entity.ProductEntity;
import org.project.dev.product.repository.ProductRepository;
import org.project.dev.product.repository.ProductSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    /*
    Todo
     1. code.aiaru@gmail.com
     2. CRUD 처리를 위한 로직들이 모인 곳입니다.
     3. x
     4. x
     */


    private final ProductRepository productRepository;
    private final ProductPaginationService productPaginationService;


    // WRITE (CREATE)
    @Transactional
    public ProductEntity productWriteDetail(ProductDTO productDTO){
        productDTO.setProductHits(0); // productHits 초기화
        ProductEntity productEntity = ProductEntity.toEntity(productDTO);
        return productRepository.save(productEntity);
    }


    // LIST (READ)
    public ProductListResponse getProductList(int page, Pageable pageable, String searchType, String searchKeyword) {
        Pageable adjustedPageable = PageRequest.of(page - 1, pageable.getPageSize(), pageable.getSort());
        Page<ProductDTO> productList;

        if (searchKeyword == null || searchType == null) {
            productList = productPaginationService.productNoSearchList(adjustedPageable);
        } else {
            productList = productPaginationService.productSearchList(searchType, searchKeyword, adjustedPageable);
        }

        int nowPage = productList.getPageable().getPageNumber() + 1;
        int totalPage = productList.getTotalPages();
        int startPage = productPaginationService.calculateStartPage(nowPage, totalPage);
        int endPage = productPaginationService.calculateEndPage(nowPage, totalPage);

        return new ProductListResponse(productList, nowPage, startPage, endPage, totalPage, searchType, searchKeyword);
    }

    public List<ProductDTO> productCursorBasedList(Long lastId, int limit) {
        return null;
    }

    @Data
    @AllArgsConstructor
    public class ProductListResponse {
        private Page<ProductDTO> productList;
        private int nowPage;
        private int startPage;
        private int endPage;
        private int totalPage;
        private String searchType;
        private String searchKeyword;
    }



    // DETAIL (SELECT) & UPDATE (UPDATE) & UPDATE PROCESS (UPDATE)
    @Transactional(readOnly = true)
    public ProductDTO productViewDetail(Long id) {
        // 게시물의 ID를 이용하여 해당 게시물을 데이터베이스에서 찾음
        Optional<ProductEntity> optionalProductEntity = productRepository.findById(id);
        // 만약 해당 ID에 해당하는 게시물이 존재한다면,
        if(optionalProductEntity.isPresent()){
            // Optional 객체에서 실제 ProductEntity 객체를 가져옴
            ProductEntity productEntity = optionalProductEntity.get();
            // 가져온 ProductEntity 객체를 ProductDTO 객체로 변환
            ProductDTO productDTO = ProductDTO.toDTO(productEntity);
            // 변환된 ProductDTO 객체를 반환
            return productDTO;
        }else{
            // 해당 ID에 해당하는 게시물이 존재하지 않을 경우 null 반환
            return null;
        }
    }

    // UPDATE PROCESS (UPDATE)
    @Transactional
    public ProductDTO productUpdateDetail(ProductDTO productDTO) {
        ProductEntity productEntity = ProductEntity.toEntity(productDTO);
        productRepository.save(productEntity);
        return productViewDetail(productDTO.getId());
    }

    // DELETE (DELETE)
    // 이름은 delete이지만, 실제 로직은 update.
    // productDisplay 값을 false로 바꿔, 사용자에게 보이지 않도록 처리합니다.
    @Transactional
    public void delete(Long id) {
        productRepository.softDelete(id);
    }


}