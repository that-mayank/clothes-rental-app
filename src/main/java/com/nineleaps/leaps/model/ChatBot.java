package com.nineleaps.leaps.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nineleaps.leaps.model.orders.Order;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Entity
@Table(name = "chat_bot_orders")
@Getter
@Setter
public class ChatBot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_related_question")
    private String question;
    @Column(name ="order_related_answer")
    private String answer;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;
}
