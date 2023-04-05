package com.nineleaps.leaps.service;

import com.nineleaps.leaps.dto.product.ProductDto;
import com.nineleaps.leaps.model.Product;
import com.nineleaps.leaps.model.categories.SubCategory;
import com.nineleaps.leaps.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService implements ProductServiceInterface {
    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public static Product getProductFromDto(ProductDto productDto, List<SubCategory> subCategories) {
        return new Product(productDto, subCategories);
    }

    public static ProductDto getDtoFromProduct(Product product) {
        return new ProductDto(product);
    }

    @Override
    public void addProduct(ProductDto productDto, List<SubCategory> subCategories) {
        Product product = getProductFromDto(productDto, subCategories);
        productRepository.save(product);
    }

    public List<ProductDto> listProducts() {
        List<Product> products = productRepository.findAll();
        List<ProductDto> productDtos = new ArrayList<>();
        for (Product product : products) {
            ProductDto productDto = getDtoFromProduct(product);
            productDtos.add(productDto);
        }
        return productDtos;
    }

    @Override
    public void updateProduct(Long productId, ProductDto productDto, List<SubCategory> subCategories) {
        Product product = getProductFromDto(productDto, subCategories);
        if (product != null) {
            product.setId(productId);
            productRepository.save(product);
        }
    }

    @Override
    public Optional<Product> readProduct(Long productId) {
        return productRepository.findById(productId);
    }

    @Override
    public List<Product> listProductsById(Long subcategoryId) {
        return productRepository.findBySubCategoriesId(subcategoryId);
    }
}

