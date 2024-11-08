package com.arfan.shop.service.product;

import com.arfan.shop.dto.ProductDto;
import com.arfan.shop.model.Product;
import com.arfan.shop.request.AddProductRequest;
import com.arfan.shop.request.UpdateProductRequest;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ProductService {

    Product addProduct(AddProductRequest request);
    List<Product> getAllProducts();
    Product getProductById(Long id);
    void deleteProductById(Long id);
    Product updateProduct(UpdateProductRequest request, Long productId);
    List<Product> getProductsByCategory(String category);
    List<Product> getProductsByBrand(String brand);
    List<Product> getProductsByCategoryAndBrand(String category, String brand);
    List<Product> getProductsByName(String name);
    List<Product> getProductsByBrandAndName(String brand, String name);
    Long countProductsByBrandAndName(String brand, String name);

    List<ProductDto> getConvertedProducts(List<Product> products);

    ProductDto convertToDto(Product product);
}
