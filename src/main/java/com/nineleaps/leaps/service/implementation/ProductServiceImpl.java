package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.dto.product.ProductDto;
import com.nineleaps.leaps.exceptions.ProductNotExistException;
import com.nineleaps.leaps.exceptions.QuantityOutOfBoundException;
import com.nineleaps.leaps.model.product.Product;
import com.nineleaps.leaps.model.product.ProductUrl;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.model.categories.SubCategory;
import com.nineleaps.leaps.repository.ProductRepository;
import com.nineleaps.leaps.service.ProductServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import lombok.AllArgsConstructor;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.*;

import static com.nineleaps.leaps.LeapsApplication.NGROK;
import static com.nineleaps.leaps.config.MessageStrings.*;

@Service
@AllArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductServiceInterface {
    private final ProductRepository productRepository;
    private final EntityManager entityManager;

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

    @Override
    public List<ProductDto> listProducts(int pageNumber, int pageSize, User user) {
        Session session = entityManager.unwrap(Session.class);
        Filter deletedProductFilter = session.enableFilter(DELETED_PRODUCT_FILTER);
        deletedProductFilter.setParameter(DELETED, false);
        Filter disabledProductFilter = session.enableFilter(DISABLED_PRODUCT_FILTER);
        disabledProductFilter.setParameter(DISABLED, false);
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Product> page = productRepository.findAllByUserNot(pageable, user);
        List<ProductDto> productDtos = new ArrayList<>();
        for (Product product : page.getContent()) {
            ProductDto productDto = getDtoFromProduct(product);
            productDtos.add(productDto);
        }
        session.disableFilter(DELETED_PRODUCT_FILTER);
        session.disableFilter(DISABLED_PRODUCT_FILTER);
        return productDtos;
    }

    @Override
    public void updateProduct(Long productId, ProductDto productDto, List<SubCategory> subCategories, List<Category> categories, User user) {
        Product oldProduct = productRepository.findByUserIdAndId(user.getId(), productId);
        if (!Helper.notNull(oldProduct)) {
            throw new ProductNotExistException("Product does not belong to the user: " + user.getFirstName() + " " + user.getLastName());
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
    public List<ProductDto> listProductsById(Long subcategoryId, User user) {
        Session session = entityManager.unwrap(Session.class);
        Filter deletedProductFilter = session.enableFilter(DELETED_PRODUCT_FILTER);
        Filter disabledProductFilter = session.enableFilter(DISABLED_PRODUCT_FILTER);
        deletedProductFilter.setParameter(DELETED, false);
        disabledProductFilter.setParameter(DISABLED, false);
        if (subcategoryId == 19) {
            return getProductsByPriceRange(0, 2000);
        } else if (subcategoryId == 20) {
            return getProductsByPriceRange(2001, 5000);
        } else if (subcategoryId == 21) {
            return getProductsByPriceRange(5001, 10000);
        } else if (subcategoryId == 22) {
            return getProductsByPriceRange(10000, Long.MAX_VALUE);
        } else {
            List<Product> body = productRepository.findBySubCategoriesId(subcategoryId);
            List<ProductDto> productDtos = new ArrayList<>();
            for (Product product : body) {
                if (!product.getUser().equals(user)) {
                    ProductDto productDto = getDtoFromProduct(product);
                    productDtos.add(productDto);
                }
            }
            session.disableFilter(DISABLED_PRODUCT_FILTER);
            session.disableFilter(DELETED_PRODUCT_FILTER);
            return productDtos;
        }
    }

    @Override
    public List<ProductDto> listProductsByCategoryId(Long categoryId, User user) {
        Session session = entityManager.unwrap(Session.class);
        Filter deletedProductFilter = session.enableFilter(DELETED_PRODUCT_FILTER);
        Filter disabledProductFilter = session.enableFilter(DISABLED_PRODUCT_FILTER);
        deletedProductFilter.setParameter(DELETED, false);
        disabledProductFilter.setParameter(DISABLED, false);
        List<Product> products = productRepository.findByCategoriesId(categoryId);
        List<ProductDto> productDtos = new ArrayList<>();
        for (Product product : products) {
            if (!product.getUser().equals(user)) {
                ProductDto productDto = getDtoFromProduct(product);
                productDtos.add(productDto);
            }
        }
        session.disableFilter(DISABLED_PRODUCT_FILTER);
        session.disableFilter(DELETED_PRODUCT_FILTER);
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
        Session session = entityManager.unwrap(Session.class);
        Filter deletedProductFilter = session.enableFilter(DELETED_PRODUCT_FILTER);
        deletedProductFilter.setParameter(DELETED, false);
        List<Product> products = productRepository.findAllByUser(user, Sort.by(Sort.Direction.DESC, "id"));
        List<ProductDto> productDtos = new ArrayList<>();
        for (Product product : products) {
            ProductDto productDto = getDtoFromProduct(product);
            productDtos.add(productDto);
        }
        session.disableFilter(DELETED_PRODUCT_FILTER);
        return productDtos;
    }

    @Override
    public List<ProductDto> listOwnerProducts(User user) {
        Session session = entityManager.unwrap(Session.class);
        Filter deletedProductFilter = session.enableFilter(DELETED_PRODUCT_FILTER);
        deletedProductFilter.setParameter(DELETED, false);
        List<Product> products = productRepository.findAllByUser(user);
        List<ProductDto> productDtos = new ArrayList<>();

        for (Product product : products) {
            ProductDto productDto = getDtoFromProduct(product);
            productDtos.add(productDto);
        }
        session.disableFilter(DELETED_PRODUCT_FILTER);
        return productDtos;
    }

    @Override
    public List<ProductDto> getProductsByPriceRange(double minPrice, double maxPrice) {
        Session session = entityManager.unwrap(Session.class);
        Filter deletedProductFilter = session.enableFilter(DELETED_PRODUCT_FILTER);
        Filter disabledProductFilter = session.enableFilter(DISABLED_PRODUCT_FILTER);
        deletedProductFilter.setParameter(DELETED, false);
        disabledProductFilter.setParameter(DISABLED, false);
        List<Product> body = productRepository.findProductByPriceRange(minPrice, maxPrice);
        List<ProductDto> productDtos = new ArrayList<>();
        for (Product product : body) {
            ProductDto productDto = getDtoFromProduct(product);
            productDtos.add(productDto);
        }
        session.disableFilter(DISABLED_PRODUCT_FILTER);
        session.disableFilter(DELETED_PRODUCT_FILTER);
        return productDtos;
    }

    @Override
    public List<ProductDto> searchProducts(String query, User user) {
        Session session = entityManager.unwrap(Session.class);
        Filter deletedProductFilter = session.enableFilter(DELETED_PRODUCT_FILTER);
        Filter disabledProductFilter = session.enableFilter(DISABLED_PRODUCT_FILTER);
        deletedProductFilter.setParameter(DELETED, false);
        disabledProductFilter.setParameter(DISABLED, false);
        String[] stringArray = query.split(" ");
        String[] stringList = stringArray;

        List<ProductDto> productDtos = new ArrayList<>();

        for (String keyword : stringList) {
            List<Product> products = productRepository.searchProducts(keyword);
            for (Product product : products) {
                if (!product.getUser().equals(user)) {
                    ProductDto productDto = getDtoFromProduct(product);
                    productDtos.add(productDto);
                }
            }
        }
        session.disableFilter(DISABLED_PRODUCT_FILTER);
        session.disableFilter(DELETED_PRODUCT_FILTER);
        return productDtos;
    }

    @Override
    public List<ProductDto> filterProducts(String size, Long subcategoryId, double minPrice, double maxPrice) {
        Session session = entityManager.unwrap(Session.class);
        Filter deletedProductFilter = session.enableFilter(DELETED_PRODUCT_FILTER);
        Filter disabledProductFilter = session.enableFilter(DISABLED_PRODUCT_FILTER);
        deletedProductFilter.setParameter(DELETED, false);
        disabledProductFilter.setParameter(DISABLED, false);
        List<Product> productList = productRepository.findBySubCategoriesId(subcategoryId);

        List<Product> result = new ArrayList<>();
        //price is filtered
        for (Product product : productList) {
            if ((product.getPrice() >= minPrice && product.getPrice() <= maxPrice) && product.getSize().equals(size)) {
                //size is filtered, brand is left because we need to add brand to frontend
                result.add(product);
            }
        }
        List<ProductDto> resultDtos = new ArrayList<>();
        for (Product product : result) {
            ProductDto productDto = getDtoFromProduct(product);
            resultDtos.add(productDto);
        }
        session.disableFilter(DISABLED_PRODUCT_FILTER);
        session.disableFilter(DELETED_PRODUCT_FILTER);
        return resultDtos;
    }

    @Override
    public void deleteProduct(Long productId, Long userId) {
        Product product = productRepository.findByUserIdAndId(userId, productId);
        if (!Helper.notNull(product)) {
            throw new ProductNotExistException("Product does not belong to current user.");
        }
        product.setDeleted(true);
        productRepository.save(product);
    }

    @Override
    public Product getProduct(Long productId, Long userId) {
        return productRepository.findByUserIdAndId(userId, productId);
    }

    @Override
    public void disableProduct(Product product, int quantity) {
        if (quantity > product.getAvailableQuantities()) {
            throw new QuantityOutOfBoundException("User cannot disable more quantities than available quantities");
        }
        product.setDisabledQuantities(product.getDisabledQuantities() + quantity);
        product.setAvailableQuantities(product.getAvailableQuantities() - quantity);
        if (product.getAvailableQuantities() == 0) {
            product.setDisabled(true);
        }
        productRepository.save(product);
    }

    @Override
    public void enableProduct(Product product, int quantity) {
        if (quantity > product.getDisabledQuantities()) {
            throw new QuantityOutOfBoundException("User cannot enable more quantities than disabled quantities");
        }
        product.setDisabledQuantities(product.getDisabledQuantities() - quantity);
        product.setAvailableQuantities(product.getAvailableQuantities() + quantity);
        if (product.isDisabled()) {
            product.setDisabled(false);
        }
        productRepository.save(product);
    }

    private void setImageUrl(Product product, ProductDto productDto) {
        //setting image url
        List<ProductUrl> productUrls = new ArrayList<>();
        for (String imageurl : productDto.getImageUrl()) {
            ProductUrl productUrl = new ProductUrl();
            productUrl.setProduct(product);
            //removing ngrok link
            String newImageurl = ngrokLinkRemove(imageurl);
            productUrl.setUrl(newImageurl);
            productUrls.add(productUrl);
        }
        product.setImageURL(productUrls);
        productRepository.save(product);
    }

    private String ngrokLinkRemove(String imageurl) {
        if (imageurl.contains(NGROK)) {
            int size = NGROK.length();
            return imageurl.substring(size);
        }
        return imageurl;
    }

}

