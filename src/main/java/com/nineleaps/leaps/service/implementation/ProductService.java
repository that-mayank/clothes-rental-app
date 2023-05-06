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
import com.nineleaps.leaps.service.ProductServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${ngrok_url}")
    private String ngrokUrl;

    public ProductDto getDtoFromProduct(Product product) {
        return new ProductDto(product);
    }

    @Override
    public List<ProductDto> filterProducts(String size, String brand, Long subcategoryId, double minPrice, double maxPrice) {
        List<Product> productList = productRepository.findBySubCategoriesId(subcategoryId);
        List<Product> result = new ArrayList<>();
        //price is filtered //size and brand is filtered
        for (Product product : productList) {
            if ((product.getPrice() >= minPrice && product.getPrice() <= maxPrice) && product.getSize() == size && product.getBrand() == brand) {
                result.add(product);
            }
        }
        List<ProductDto> resultDtos = new ArrayList<>();
        for (Product product : result) {
            ProductDto productDto = getDtoFromProduct(product);
            resultDtos.add(productDto);
        }
        return resultDtos;
    }

    private Product getProductFromDto(ProductDto productDto, List<SubCategory> subCategories, List<Category> categories, User user) {
        return new Product(productDto, subCategories, categories, user);
    }

    @Override
    public void addProduct(ProductDto productDto, List<SubCategory> subCategories, List<Category> categories, User user) {
        Product product = getProductFromDto(productDto, subCategories, categories, user);
        productRepository.save(product);
        //setting image url
        setImageUrl(product, productDto);
    }

    public List<ProductDto> listProducts(int pageNumber, int pageSize, User user) {
        //Pagination start
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Product> allProducts = productRepository.findAll(pageable);
        List<Product> products = allProducts.getContent();
        //removing owner products from his own borrowers flow
        List<Product> results = new ArrayList<>();
        for (Product product : products) {
            if (!product.getUser().equals(user)) {
                results.add(product);
            }
        }
        //Pagination end
        List<ProductDto> productDtos = new ArrayList<>();
        for (Product product : results) {
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
        product.setId(productId);
        //setting image url
        setImageUrl(product, productDto);
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
        for (String imageUrl : productDto.getImageUrl()) {
            ProductUrl productUrl = new ProductUrl();
            productUrl.setProduct(product);
            //remove ngrok link from imageUrl and return substring without ngrok link
            productUrl.setUrl(removeNgrokLink(imageUrl));
            productUrls.add(productUrl);
        }
        product.setImageURL(productUrls);
        productRepository.save(product);
    }

    private String removeNgrokLink(String imageUrl) {
        int size = ngrokUrl.length();
        return imageUrl.substring(size);
    }
}

