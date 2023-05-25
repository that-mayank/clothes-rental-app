package com.nineleaps.leaps.service;

import com.nineleaps.leaps.dto.product.ProductDto;
import com.nineleaps.leaps.exceptions.ProductNotExistException;
import com.nineleaps.leaps.model.Product;
import com.nineleaps.leaps.model.ProductUrl;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.categories.Category;
import com.nineleaps.leaps.model.categories.SubCategory;
import com.nineleaps.leaps.repository.CategoryRepository;
import com.nineleaps.leaps.repository.ProductRepository;
import com.nineleaps.leaps.repository.SubCategoryRepository;
import com.nineleaps.leaps.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.regex.Pattern;

import static com.nineleaps.leaps.LeapsProductionApplication.ngrok_url;

@Service
@RequiredArgsConstructor
public class ProductService implements ProductServiceInterface {
    private final ProductRepository productRepository;
    private final SubCategoryServiceInterface subCategoryService;
    private final EntityManager entityManager;
    private final SubCategoryRepository subCategoryRepository;
    private final CategoryRepository categoryRepository;


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
        Filter filter = session.enableFilter("deletedProductFilter");
        filter.setParameter("isDeleted", false);
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Product> allProducts = productRepository.findAll(pageable);
        List<Product> products = allProducts.getContent();
        session.disableFilter("deletedProductFilter");
        List<Product> results = new ArrayList<>();
        for (Product product : products) {
            if (!product.getUser().equals(user)) {
                results.add(product);
            }
        }
        List<ProductDto> productDtos = new ArrayList<>();
        for (Product product : results) {
            ProductDto productDto = getDtoFromProduct(product);
            productDtos.add(productDto);
        }
        return productDtos;
    }

    @Override
    public List<String> listSuggestions(String searchInput, User user) {
        List<Product> allProducts = productRepository.findAll();
        List<Product> results = new ArrayList<>();
        for (Product product : allProducts) {
            if (!product.getUser().equals(user)) {
                results.add(product);
            }
        }
        Set<String> searchSuggestions = new HashSet<>();
        for (Product product : results) {
            String productName = product.getName();
            String productBrand = product.getBrand();
            String productCategoryName = null;
            List<Category> productCategories = product.getCategories();
            for (Category category : productCategories) {
                productCategoryName = category.getCategoryName();
            }
            String suggestion = productName + " in " + productBrand + " for " + productCategoryName;
            String regex = "\\b" + Pattern.quote(searchInput) + "\\b"; // Exact word match
            if (suggestion.toLowerCase().matches("(?i).*" + regex + ".*")) {
                searchSuggestions.add(suggestion);
            }
        }
        return new ArrayList<>(searchSuggestions);
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
        if (subcategoryId == 28) {
            return getProductsByPriceRange(0, 2000);
        } else if (subcategoryId == 29) {
            return getProductsByPriceRange(2001, 5000);
        } else if (subcategoryId == 30) {
            return getProductsByPriceRange(5001, 10000);
        } else if (subcategoryId == 31) {
            return getProductsByPriceRange(10000, Long.MAX_VALUE);
        } else {
            Session session = entityManager.unwrap(Session.class);
            Filter filter = session.enableFilter("deletedProductFilter");
            filter.setParameter("isDeleted", false);
            List<Product> body = productRepository.findBySubCategoriesId(subcategoryId);
            session.disableFilter("deletedProductFilter");
            List<ProductDto> productDtos = new ArrayList<>();
            for (Product product : body) {
                if (!product.getUser().equals(user)) {
                    ProductDto productDto = getDtoFromProduct(product);
                    productDtos.add(productDto);
                }
            }
            return productDtos;
        }
    }

    @Override
    public List<ProductDto> listProductsByCategoryId(Long categoryId, User user) {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter("deletedProductFilter");
        filter.setParameter("isDeleted", false);
        List<Product> products = productRepository.findByCategoriesId(categoryId);
        session.disableFilter("deletedProductFilter");
        List<ProductDto> productDtos = new ArrayList<>();
        for (Product product : products) {
            if (!product.getUser().equals(user)) {
                ProductDto productDto = getDtoFromProduct(product);
                productDtos.add(productDto);
            }
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
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter("deletedProductFilter");
        filter.setParameter("isDeleted", false);
        List<Product> products = productRepository.findAllByUser(user, Sort.by(Sort.Direction.DESC, "id"));
        session.disableFilter("deletedProductFilter");
        List<ProductDto> productDtos = new ArrayList<>();
        for (Product product : products) {
            ProductDto productDto = getDtoFromProduct(product);
            productDtos.add(productDto);
        }
        return productDtos;
    }

    @Override
    public List<ProductDto> listOwnerProducts(User user) {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter("deletedProductFilter");
        filter.setParameter("isDeleted", false);
        List<Product> products = productRepository.findAllByUser(user);
        List<ProductDto> productDtos = new ArrayList<>();
        session.disableFilter("deletedProductFilter");

        for (Product product : products) {
            ProductDto productDto = getDtoFromProduct(product);
            productDtos.add(productDto);
        }
        return productDtos;
    }

    @Override
    public List<ProductDto> getProductsByPriceRange(double minPrice, double maxPrice) {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter("deletedProductFilter");
        filter.setParameter("isDeleted", false);
        List<Product> body = productRepository.findProductByPriceRange(minPrice, maxPrice);
        session.disableFilter("deletedProductFilter");
        List<ProductDto> productDtos = new ArrayList<>();
        for (Product product : body) {
            ProductDto productDto = getDtoFromProduct(product);
            productDtos.add(productDto);
        }
        return productDtos;
    }

//    @Override
//    public List<ProductDto> searchProducts(String query, User user) {
//        Session session = entityManager.unwrap(Session.class);
//        Filter filter = session.enableFilter("deletedProductFilter");
//        filter.setParameter("isDeleted", false);
//        String stringArray[] = query.split(" ");
//        List<String> stringList = Arrays.asList(stringArray);
//
//        List<ProductDto> productDtos = new ArrayList<>();
//
//        for (String keyword : stringList) {
//            List<Product> products = productRepository.searchProducts(keyword);
//            for (Product product : products) {
//                if (!product.getUser().equals(user)) {
//                    ProductDto productDto = getDtoFromProduct(product);
//                    productDtos.add(productDto);
//                }
//            }
//        }
//        session.disableFilter("deletedProductFilter");
//        return productDtos;
//    }

    @Override
    public List<ProductDto> filterProducts(String size, Long subcategoryId, double minPrice, double maxPrice) {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter("deletedProductFilter");
        filter.setParameter("isDeleted", false);
        List<Product> productList = productRepository.findBySubCategoriesId(subcategoryId);
        session.disableFilter("deletedProductFilter");

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
        return resultDtos;
    }

    @Override
    public void deleteProduct(Long productId, Long userId) {
        Product product = productRepository.findByUserIdAndId(userId, productId);
        if (!Helper.notNull(product)) {
            throw new ProductNotExistException("Product does not belong to current user.");
        }
        productRepository.deleteById(productId);
    }

    @Override
    public Product getProduct(Long productId, Long userId) {
        return productRepository.findByUserIdAndId(userId, productId);

    }

    @Override
    public void disableProduct(Product product) {
        product.setDisabled(true);
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
        if (imageurl.contains(ngrok_url)) {
            int size = ngrok_url.length();
            return imageurl.substring(size);
        }
        return imageurl;
    }

    @Override
    public List<ProductDto> searchProducts(String query, User user) {
        List<String> brands = new ArrayList<>(), names = new ArrayList<>(), colors = new ArrayList<>(), materials = new ArrayList<>();
        Map<String, Set<String>> spliterators = boyerMooreAlgorithm(query.toLowerCase());
        System.out.println(spliterators);
        for (Map.Entry<String, Set<String>> entry : spliterators.entrySet()) {
            if (entry.getKey().equals("brand")) {
                for (String brand : entry.getValue()) {
                    brands.add(brand);
                }
            } else if (entry.getKey().equals("name")) {
                for (String name : entry.getValue()) {
                    names.add(name);
                }
            } else if (entry.getKey().equals("color")) {
                for (String color : entry.getValue()) {
                    colors.add(color);
                }
            } else if (entry.getKey().equals("material")) {
                for (String material : entry.getValue()) {
                    materials.add(material);
                }
            }
        }
        List<Product> products = new ArrayList<>();
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter("deletedProductFilter");
        filter.setParameter("isDeleted", false);
//        for (String brandQuery : brands) {
//            for (String nameQuery : names) {
//                for (String colorQuery : colors) {
//                    for (String materialQuery : materials) {
//                        System.out.println("brandQuery" + " = " + brandQuery + " " + "nameQuery" + " = " + nameQuery + " " + " colorQuery " + " = " + colorQuery + " " + "materialQuery" + " = " + materialQuery);
//                        products.addAll(productRepository.searchProducts(brandQuery, nameQuery, colorQuery, materialQuery));
//                    }
//                }
//            }
//        }
        if (brands.isEmpty()) {
            if (colors.isEmpty()) {
                if (materials.isEmpty()) {
                    // Only names are available
                    for (String nameQuery : names) {
                        products.addAll(productRepository.searchProducts(null, nameQuery, null, null));
                    }
                } else {
                    // Only names and materials are available
                    for (String nameQuery : names) {
                        for (String materialQuery : materials) {
                            products.addAll(productRepository.searchProducts(null, nameQuery, null, materialQuery));
                        }
                    }
                }
            } else if (materials.isEmpty()) {
                // Only names and colors are available
                for (String nameQuery : names) {
                    for (String colorQuery : colors) {
                        products.addAll(productRepository.searchProducts(null, nameQuery, colorQuery, null));
                    }
                }
            } else {
                // Names, colors, and materials are available
                for (String nameQuery : names) {
                    for (String colorQuery : colors) {
                        for (String materialQuery : materials) {
                            products.addAll(productRepository.searchProducts(null, nameQuery, colorQuery, materialQuery));
                        }
                    }
                }
            }
        } else if (colors.isEmpty()) {
            if (materials.isEmpty()) {
                // Only brands are available
                for (String brandQuery : brands) {
                    products.addAll(productRepository.searchProducts(brandQuery, null, null, null));
                }
            } else {
                // Brands and materials are available
                for (String brandQuery : brands) {
                    for (String materialQuery : materials) {
                        products.addAll(productRepository.searchProducts(brandQuery, null, null, materialQuery));
                    }
                }
            }
        } else if (materials.isEmpty()) {
            // Brands and colors are available
            for (String brandQuery : brands) {
                for (String colorQuery : colors) {
                    products.addAll(productRepository.searchProducts(brandQuery, null, colorQuery, null));
                }
            }
        } else {
            // All arrays are available
            for (String brandQuery : brands) {
                for (String nameQuery : names) {
                    for (String colorQuery : colors) {
                        for (String materialQuery : materials) {
                            products.addAll(productRepository.searchProducts(brandQuery, nameQuery, colorQuery, materialQuery));
                        }
                    }
                }
            }
        }
        session.disableFilter("deletedProductFilter");
        List<ProductDto> productDtos = new ArrayList<>();
        for (Product product : products) {
            ProductDto productDto = getDtoFromProduct(product);
            productDtos.add(productDto);
        }
        return productDtos;
    }

    //
    // Implementation of Boyer Moore Algorithm
    private Map<String, Set<String>> boyerMooreAlgorithm(String query) {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter("deletedProductFilter");
        filter.setParameter("isDeleted", false);
        Map<String, List<String>> keyValues = new HashMap<>();
        List<String> subcategory = new ArrayList<>();
        List<String> category = new ArrayList<>();
        List<String> name = new ArrayList<>();
        List<String> color = new ArrayList<>();
        List<String> brand = new ArrayList<>();
        List<String> material = new ArrayList<>();
        //add subcategories from here
        for (SubCategory subCategory : subCategoryRepository.findAll()) {
            subcategory.add(subCategory.getSubcategoryName().toLowerCase());
        }

        for (Category categoryItr : categoryRepository.findAll()) {
            category.add(categoryItr.getCategoryName().toLowerCase());
        }
        keyValues.put("category", category);
        keyValues.put("subcategory", subcategory);
        //add product name, color, brand, and material
        for (Product product : productRepository.findAll()) {
            name.add(product.getName().toLowerCase());
            color.add(product.getColor().toLowerCase());
            brand.add(product.getBrand().toLowerCase());
            material.add(product.getMaterial().toLowerCase());
        }
        keyValues.put("name", name);
        keyValues.put("color", color);
        keyValues.put("brand", brand);
        keyValues.put("material", material);
        session.disableFilter("deletedProductFilter");

        System.out.println("name " + name);
        System.out.println("---------------------------------------------------");
        System.out.println("color " + color);
        System.out.println("---------------------------------------------------");
        System.out.println("brand " + brand);
        System.out.println("---------------------------------------------------");
        System.out.println("material " + material);
        System.out.println("---------------------------------------------------");
        //send values to boyerMooreMainMethod
        return searchWords(query.trim(), keyValues);
    }

    private int[] createBadCharacterTable(char[] pattern) {
        int tableSize = 256;
        int[] table = new int[tableSize];
        for (int i = 0; i < tableSize; i++) {
            table[i] = pattern.length;
        }
        for (int i = 0; i < pattern.length - 1; i++) {
            table[pattern[i]] = pattern.length - 1 - i;
        }
        return table;
    }

    private int[] createGoodSuffixTable(char[] pattern) {
        int patternLength = pattern.length;
        int[] table = new int[patternLength];
        int lastPrefixPosition = patternLength;
        for (int i = patternLength - 1; i >= 0; i--) {
            if (isPrefix(pattern, i + 1)) {
                lastPrefixPosition = i + 1;
            }
            table[patternLength - 1 - i] = lastPrefixPosition - i + patternLength - 1;
        }
        for (int i = 0; i < patternLength - 1; i++) {
            int suffixLength = suffixLength(pattern, i);
            table[suffixLength] = patternLength - 1 - i + suffixLength;
        }
        return table;
    }

    private boolean isPrefix(char[] pattern, int position) {
        int patternLength = pattern.length;
        for (int i = position, j = 0; i < patternLength; i++, j++) {
            if (pattern[i] != pattern[j]) {
                return false;
            }
        }
        return true;
    }

    private int suffixLength(char[] pattern, int position) {
        int patternLength = pattern.length;
        int length = 0;
        for (int i = position, j = patternLength - 1; i >= 0 && pattern[i] == pattern[j]; i--, j--) {
            length += 1;
        }
        return length;
    }

    public List<String> findMatchingBrandNames(String input, List<String> brandNames) {
        List<String> matchingBrandNames = new ArrayList<>();
        String[] words = input.split("\\s+"); // Split input into individual words
        for (String brandName : brandNames) {
            for (String word : words) {
                if (brandName.toLowerCase().contains(word.toLowerCase())) {
                    matchingBrandNames.add(brandName);
                }
            }
        }
        return matchingBrandNames;
    }

    public Map<String, Set<String>> searchWords(String text, Map<String, List<String>> keyValues) {
        String[] words = text.split("\\s+"); // Split input text into individual words
        Map<String, Set<String>> foundWords = new HashMap<>();
        // Search for matching brand names separately
        List<String> brandNames = keyValues.get("brand");
        List<String> matchingBrandNamesList = findMatchingBrandNames(text, brandNames);
        Set<String> matchingBrandNames = new HashSet<>(matchingBrandNamesList);
        if (!matchingBrandNames.isEmpty()) {
            foundWords.put("brand", matchingBrandNames);
        }
        // Search for matching words in other categories
        for (Map.Entry<String, List<String>> entry : keyValues.entrySet()) {
            String key = entry.getKey();
            if (key.equals("brand")) {
                continue; // Skip the "brandname" category
            }
            List<String> wordList = entry.getValue();
            Set<String> matchingWords = new HashSet<>();
            for (String word : words) {
                for (String listWord : wordList) {
                    if (listWord.toLowerCase().contains(word.toLowerCase())) {
                        matchingWords.add(listWord);
                        break; // Break out of the inner loop once a match is found
                    }
                }
            }
            if (!matchingWords.isEmpty()) {
                foundWords.put(key, matchingWords);
            }
        }
        return foundWords;
    }
}

