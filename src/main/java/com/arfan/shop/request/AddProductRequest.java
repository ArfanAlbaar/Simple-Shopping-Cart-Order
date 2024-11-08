package com.arfan.shop.request;

import com.arfan.shop.model.Category;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class AddProductRequest {

    private String name;
    private String brand;
    private BigDecimal price;
    private int inventory;
    private String description;
    private Category category;

}
