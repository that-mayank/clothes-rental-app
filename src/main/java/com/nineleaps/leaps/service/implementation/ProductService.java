package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.dto.product.ProductDto;
import com.nineleaps.leaps.exceptions.CustomException;
import com.nineleaps.leaps.exceptions.ProductNotExistException;
import com.nineleaps.leaps.model.products.Product;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.model.categories.SubCategory;
import com.nineleaps.leaps.model.products.ProductUrl;
import com.nineleaps.leaps.repository.ProductRepository;
import com.nineleaps.leaps.repository.ProductUrlRepository;
import com.nineleaps.leaps.service.ProductServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService implements ProductServiceInterface {
    private final ProductRepository productRepository;
    private final ProductUrlRepository productUrlRepository;

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
        //setting image url
        setImageUrl(product, productDto);
    }

    public List<ProductDto> listProducts(int pageNumber, int pageSize) {
        //Pagination start
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Product> allProducts = productRepository.findAll(pageable);
        List<Product> products = allProducts.getContent();
        //Pagination end
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
            //setting image url
            setImageUrl(product, productDto);
        }
    }

    @Override
    public Optional<Product> readProduct(Long productId) {
        return productRepository.findById(productId);
    }

    @Override
    public List<ProductDto> listProductsById(Long subcategoryId) {
        if (subcategoryId == 27) {
            return getProductsByPriceRange(0, 2000);
        } else if (subcategoryId == 28) {
            return getProductsByPriceRange(2000, 5000);
        } else if (subcategoryId == 29) {
            return getProductsByPriceRange(5000, 10000);
        } else if (subcategoryId == 30) {
            return getProductsByPriceRange(10000, Long.MAX_VALUE);
        } else {
            List<Product> products = productRepository.findBySubCategoriesId(subcategoryId);
            List<ProductDto> productDtos = new ArrayList<>();
            for (Product product : products) {
                ProductDto productDto = getDtoFromProduct(product);
                productDtos.add(productDto);
            }
            return productDtos;
        }
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

    @Override
    public List<ProductDto> getProductsByPriceRange(double minPrice, double maxPrice) {
        List<Product> body = productRepository.findProductByPriceRange(minPrice, maxPrice);
        List<ProductDto> productDtos = new ArrayList<>();
        for (Product product : body) {
            ProductDto productDto = getDtoFromProduct(product);
            productDtos.add(productDto);
        }
        return productDtos;
    }

    @Override
    public List<ProductDto> searchProducts(String query) {
        List<Product> products = productRepository.searchProducts(query);
        List<ProductDto> productDtos = new ArrayList<>();
        for (Product product : products) {
            ProductDto productDto = getDtoFromProduct(product);
            productDtos.add(productDto);
        }
        return productDtos;
    }

    private void setImageUrl(Product product, ProductDto productDto) {
        //setting image url
        List<ProductUrl> productUrls = new ArrayList<>();
        for (String imageurl : productDto.getImageURL()) {
            ProductUrl productUrl = new ProductUrl();
            productUrl.setProduct(product);
            productUrl.setUrl(imageurl);
            productUrls.add(productUrl);
        }
        product.setImageURL(productUrls);
        productRepository.save(product);
    }

}
