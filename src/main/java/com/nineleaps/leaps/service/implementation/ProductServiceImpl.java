package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.dto.product.ProductDto;
import com.nineleaps.leaps.exceptions.CustomException;
import com.nineleaps.leaps.exceptions.ProductNotExistException;
import com.nineleaps.leaps.exceptions.QuantityOutOfBoundException;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.model.categories.SubCategory;
import com.nineleaps.leaps.model.product.Product;
import com.nineleaps.leaps.model.product.ProductUrl;
import com.nineleaps.leaps.repository.ProductRepository;
import com.nineleaps.leaps.service.CategoryServiceInterface;
import com.nineleaps.leaps.service.ProductServiceInterface;
import com.nineleaps.leaps.service.SubCategoryServiceInterface;
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
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.nineleaps.leaps.LeapsApplication.NGROK;
import static com.nineleaps.leaps.config.MessageStrings.*;

@Service
@AllArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductServiceInterface {
    private final ProductRepository productRepository;
    private final EntityManager entityManager;
    private final Helper helper;
    private final SubCategoryServiceInterface subCategoryService;
    private final CategoryServiceInterface categoryService;

    

    @Override
    public void addProduct(ProductDto productDto, HttpServletRequest request) {
        // JWT : Extracting user info from token
        User user = helper.getUser(request);

        // Retrieving Categories and Subcategories from DTO
        List<Category> categories = categoryService.getCategoriesFromIds(productDto.getCategoryIds());
        List<SubCategory> subCategories = subCategoryService.getSubCategoriesFromIds(productDto.getSubcategoryIds());
        Product product = getProductFromDto(productDto, subCategories, categories, user);
        productRepository.save(product);
        //setting image url
        setImageUrl(product, productDto);
    }

    @Override
    public List<ProductDto> listProducts(int pageNumber, int pageSize, HttpServletRequest request) {

        // JWT : Extracting user info from token
        User user = helper.getUser(request);

        Session session = getSession();
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Product> page = productRepository.findAllByUserNot(pageable, user);
        List<ProductDto> productDtos = new ArrayList<>();
        for (Product product : page.getContent()) {
            ProductDto productDto = getDtoFromProduct(product);
            productDtos.add(productDto);
        }
        disableSession(session);
        return productDtos;
    }

    @Override
    public void updateProduct(Long productId, ProductDto productDto, HttpServletRequest request) {
        // JWT : Extracting user info from token
        User user = helper.getUser(request);

        // Retrieving Categories and Subcategories from DTO
        List<Category> categories = categoryService.getCategoriesFromIds(productDto.getCategoryIds());
        List<SubCategory> subCategories = subCategoryService.getSubCategoriesFromIds(productDto.getSubcategoryIds());
        
        Product oldProduct = productRepository.findByUserIdAndId(user.getId(), productId);
        if (!Helper.notNull(oldProduct)) {
            throw new ProductNotExistException("Product does not belong to the user: " + user.getFirstName() + " " + user.getLastName());
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
    public List<ProductDto> listProductsById(Long subcategoryId, HttpServletRequest request) {
        Session session = getSession();
        // Guard Statement : Check if subcategory is valid
        Optional<SubCategory> optionalSubCategory = subCategoryService.readSubCategory(subcategoryId);
        if (optionalSubCategory.isEmpty()) {
            throw new CustomException("Subcategory not found!");
        }

        // JWT : Extracting user info from token
        User user = helper.getUser(request);

        List<Product> body = productRepository.findBySubCategoriesId(subcategoryId);
        List<ProductDto> productDtos = new ArrayList<>();
        for (Product product : body) {
            if (!product.getUser().equals(user)) {
                ProductDto productDto = getDtoFromProduct(product);
                productDtos.add(productDto);
            }
        }
        disableSession(session);
        return productDtos;

    }

    @Override
    public List<ProductDto> listProductsByCategoryId(Long categoryId, HttpServletRequest request) {
        Session session = getSession();
        //Guard Statement : Check if category id is valid
        Optional<Category> optionalCategory = categoryService.readCategory(categoryId);
        if (optionalCategory.isEmpty()) {
            throw new CustomException("Category not found!");
        }

        // JWT : Extracting user info from token
        User user = helper.getUser(request);

        List<Product> products = productRepository.findByCategoriesId(categoryId);
        List<ProductDto> productDtos = new ArrayList<>();
        for (Product product : products) {
            if (!product.getUser().equals(user)) {
                ProductDto productDto = getDtoFromProduct(product);
                productDtos.add(productDto);
            }
        }
        disableSession(session);
        return productDtos;
    }

    @Override
    public ProductDto listProductByid(Long productId) throws ProductNotExistException {
        Optional<Product> optionalProduct = productRepository.findById(productId);

        //check if product id is valid

        if (optionalProduct.isEmpty()) {
            throw new ProductNotExistException("Product is invalid: " + productId);
        }
        return getDtoFromProduct(optionalProduct.get());
    }

    @Override
    public Product getProductById(Long productId) throws ProductNotExistException {
        return productRepository
                .findById(productId)
                .orElseThrow(() -> new ProductNotExistException("Product is invalid: " + productId));
    }

    @Override
    public List<ProductDto> listProductsDesc(HttpServletRequest request) {
        // JWT : Extracting user info from token
        User user = helper.getUser(request);

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
    public List<ProductDto> listOwnerProducts(HttpServletRequest request) {
        // JWT : Extracting user info from token
        User user = helper.getUser(request);
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
        Session session = getSession();
        List<Product> body = productRepository.findProductByPriceRange(minPrice, maxPrice);
        List<ProductDto> productDtos = new ArrayList<>();
        for (Product product : body) {
            ProductDto productDto = getDtoFromProduct(product);
            productDtos.add(productDto);
        }
        disableSession(session);
        return productDtos;
    }

    @Override
    public List<ProductDto> searchProducts(String query, HttpServletRequest request) {
        Session session = getSession();
        // JWT : Extracting user info from token
        User user = helper.getUser(request);
        String[] stringList = query.split(" ");
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
        disableSession(session);
        return productDtos;
    }

    @Override
    public List<ProductDto> filterProducts(String size, Long subcategoryId, double minPrice, double maxPrice) {
        Session session = getSession();
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
        disableSession(session);
        return resultDtos;
    }

    @Override
    public void deleteProduct(Long productId, HttpServletRequest request) {
        // JWT : Extracting user info from token
        User user = helper.getUser(request);
        Product product = productRepository.findByUserIdAndId(user.getId(), productId);
        if (Optional.ofNullable(product).isEmpty()) {
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
    public void disableProduct(Long productId, int quantity, HttpServletRequest request) {
        // JWT : Extracting user info from token
        User user = helper.getUser(request);

        // Guard Statement : To check product belongs to current user
        Product product = productRepository.findByUserIdAndId(user.getId(), productId);
        if (Optional.ofNullable(product).isEmpty()) {
            throw new ProductNotExistException("Product does not belong to current user.");
        }
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
    public void enableProduct(Long productId, int quantity, HttpServletRequest request) {
        // JWT : Extracting user info from token
        User user = helper.getUser(request);

        // Guard Statement : To check product belongs to current user
        Product product = productRepository.findByUserIdAndId(user.getId(), productId);
        if (Optional.ofNullable(product).isEmpty()) {
            throw new ProductNotExistException("Product does not belong to current user.");
        }

        if (quantity > product.getDisabledQuantities()) {
            throw new QuantityOutOfBoundException("User cannot enable more quantities than disabled quantities");
        }
        product.setDisabledQuantities(product.getDisabledQuantities() - quantity);
        product.setAvailableQuantities(product.getAvailableQuantities() + quantity);
        product.setDisabled(false);
        productRepository.save(product);
    }

    private void setImageUrl(Product product, ProductDto productDto) {
        //setting image url
        List<ProductUrl> productUrls = new ArrayList<>();
        for (String imageUrl : productDto.getImageUrl()) {
            ProductUrl productUrl = new ProductUrl();
            productUrl.setProduct(product);
            //removing ngrok link
            String newImageUrl = ngrokLinkRemove(imageUrl);
            productUrl.setUrl(newImageUrl);
            productUrls.add(productUrl);
        }
        product.setImageURL(productUrls);
        productRepository.save(product);
    }

    private String ngrokLinkRemove(String imageUrl) {
        return Optional.of(imageUrl)
                .filter(url -> url.contains(NGROK))
                .map(url -> url.substring(NGROK.length()))
                .orElse(imageUrl);
    }

    public static ProductDto getDtoFromProduct(Product product) {
        return new ProductDto(product);
    }


    private static Product getProductFromDto(ProductDto productDto, List<SubCategory> subCategories, List<Category> categories, User user) {
        return new Product(productDto, subCategories, categories, user);
    }

    private Session getSession() {
        Session session = entityManager.unwrap(Session.class);
        Filter deletedProductFilter = session.enableFilter(DELETED_PRODUCT_FILTER);
        deletedProductFilter.setParameter(DELETED, false);
        Filter disabledProductFilter = session.enableFilter(DISABLED_PRODUCT_FILTER);
        disabledProductFilter.setParameter(DISABLED, false);
        return session;
    }

    private static void disableSession(Session session) {
        session.disableFilter(DELETED_PRODUCT_FILTER);
        session.disableFilter(DISABLED_PRODUCT_FILTER);
    }

}

