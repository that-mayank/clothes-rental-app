package com.nineleaps.leaps.service;

import com.nineleaps.leaps.dto.product.ProductDto;
import com.nineleaps.leaps.exceptions.CustomException;
import com.nineleaps.leaps.exceptions.ProductNotExistException;
import com.nineleaps.leaps.model.Product;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.model.categories.SubCategory;
import com.nineleaps.leaps.repository.ProductRepository;
import com.nineleaps.leaps.utils.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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

    public static ProductDto getDtoFromProduct(Product product) {
        return new ProductDto(product);
    }


    private static Product getProductFromDto(ProductDto productDto, List<SubCategory> subCategories, List<Category> categories, User user) {
        return new Product(productDto, subCategories, categories, user);
    }

    @Override
    public void addProduct(ProductDto productDto, List<SubCategory> subCategories, List<Category> categories, User user) {
        Product product = getProductFromDto(productDto, subCategories, categories, user);
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
    public void updateProduct(Long productId, ProductDto productDto, List<SubCategory> subCategories, List<Category> categories, User user) throws CustomException {
        Product oldProduct = productRepository.findById(productId).get();
        if (!oldProduct.getUser().equals(user)) {
            throw new CustomException("Product does not belong to the user: " + user.getFirstName() + " " + user.getLastName());
        }
        Product product = getProductFromDto(productDto, subCategories, categories, user);
        if (Helper.notNull(product)) {
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

    @Override
    public List<ProductDto> listProductsByCategoryId(Long categoryId) {
        List<Product> products = productRepository.findByCategoriesId(categoryId);
        List<ProductDto> productDtos = new ArrayList<>();
        for (Product product : products) {
            ProductDto productDto = getDtoFromProduct(product);
            productDtos.add(productDto);
        }
        return productDtos;
    }

    @Override
    public ProductDto listProductByid(Long productId) throws ProductNotExistException {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (!optionalProduct.isPresent()) {
            throw new ProductNotExistException("Product is invalid: " + productId);
        }
        return getDtoFromProduct(optionalProduct.get());
    }

    @Override
    public Product getProductById(Long productId) throws ProductNotExistException {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isEmpty()) {
            throw new ProductNotExistException("Product is invalid: " + productId);
        }
        return optionalProduct.get();
    }

    @Override
    public List<ProductDto> listProductsDesc(User user) {
        List<Product> products = productRepository.findAllByUser(user, Sort.by(Sort.Direction.DESC, "id"));
        List<ProductDto> productDtos = new ArrayList<>();
        for (Product product : products) {
            ProductDto productDto = getDtoFromProduct(product);
            productDtos.add(productDto);
        }
        return productDtos;
    }

    @Override
    public List<ProductDto> listOwnerProducts(User user) {
        List<Product> products = productRepository.findAllByUser(user);
        List<ProductDto> productDtos = new ArrayList<>();
        for (Product product : products) {
            ProductDto productDto = getDtoFromProduct(product);
            productDtos.add(productDto);
        }
        return productDtos;
    }
}

