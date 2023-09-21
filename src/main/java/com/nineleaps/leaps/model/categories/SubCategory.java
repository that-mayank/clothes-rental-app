package com.nineleaps.leaps.model.categories;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nineleaps.leaps.dto.category.SubCategoryDto;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.product.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "subcategories")
@Getter
@Setter
@NoArgsConstructor
public class SubCategory {


    // Audit Columns
    @Column(name = "created_at")
    private LocalDateTime subCategoryCreatedAt;

    @Column(name = "updated_at")
    private LocalDateTime subCategoryUpdatedAt;

    @Column(name = "created_by")
    private Long subCategoryCreatedBy;

    @Column(name = "updated_by")
    private Long subCategoryUpdatedBy;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    Category category;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "subcategory_name")
    private String subcategoryName;
    @Column(name = "image_url")
    private String imageUrl;
    private String description;
    @ManyToMany(mappedBy = "subCategories")
    @JsonIgnore
    private List<Product> products;

    public SubCategory(SubCategoryDto subCategoryDto, Category category) {
        this.subcategoryName = subCategoryDto.getSubcategoryName();
        this.imageUrl = subCategoryDto.getImageURL();
        this.description = subCategoryDto.getDescription();
        this.category = category;
    }

    public void setAuditColumnsCreate(User user) {
        this.subCategoryCreatedAt = user.getCreatedAt();
        this.subCategoryCreatedBy = user.getCreatedBy();
    }

    public void setAuditColumnsUpdate(Long userId){
        this.subCategoryUpdatedAt = LocalDateTime.now();
        this.subCategoryUpdatedBy = userId;
    }
}
