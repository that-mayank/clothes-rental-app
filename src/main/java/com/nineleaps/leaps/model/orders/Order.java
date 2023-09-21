package com.nineleaps.leaps.model.orders;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nineleaps.leaps.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order {

    // Audit Columns
    @Column(name = "created_at")
    private LocalDateTime orderCreatedAt;

    @Column(name = "updated_at")
    private LocalDateTime orderUpdatedAt;

    @Column(name = "created_by")
    private Long orderCreatedBy;

    @Column(name = "updated_by")
    private Long orderUpdatedBy;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "create_date")
    private LocalDateTime createDate;
    @Column(name = "total_price")
    private double totalPrice;
    @Column(name = "session_id")
    private String sessionId;
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public void setAuditColumnsCreate(User user) {
        this.orderCreatedAt = user.getCreatedAt();
        this.orderCreatedBy = user.getCreatedBy();
    }

    public void setAuditColumnsUpdate(Long userId){
        this.orderUpdatedAt = LocalDateTime.now();
        this.orderUpdatedBy = userId;
    }

}
