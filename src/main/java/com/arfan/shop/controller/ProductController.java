package com.arfan.shop.controller;

import com.arfan.shop.dto.ProductDto;
import com.arfan.shop.exception.AlreadyExistsException;
import com.arfan.shop.exception.ResourceNotFoundException;
import com.arfan.shop.model.Product;
import com.arfan.shop.request.AddProductRequest;
import com.arfan.shop.request.UpdateProductRequest;
import com.arfan.shop.response.ApiResponse;
import com.arfan.shop.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        List<ProductDto> convertedProducts = productService.getConvertedProducts(products);
        return ResponseEntity.ok(new ApiResponse("Success", convertedProducts));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getProductById(@PathVariable Long id) {
        try {
            Product product = productService.getProductById(id);
            ProductDto productDto = productService.convertToDto(product);
            return ResponseEntity.ok(new ApiResponse("Success", productDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addProduct(@RequestBody AddProductRequest request) {
        try {
            Product product = productService.addProduct(request);
            return ResponseEntity.ok(new ApiResponse("Add product success!", product));
        } catch (AlreadyExistsException e) {
            return ResponseEntity.status(CONFLICT).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/{id}/update")
    public ResponseEntity<ApiResponse> updateProduct(@RequestBody UpdateProductRequest request, @PathVariable Long id) {
        try {
            Product updatedProduct = productService.updateProduct(request, id);
            ProductDto productDto = productService.convertToDto(updatedProduct);
            return ResponseEntity.ok(new ApiResponse("Update success!", productDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProductById(id);
            return ResponseEntity.ok(new ApiResponse("Delete success", id));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/findBy")
    public ResponseEntity<ApiResponse> getProductBy(
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "brand", required = false) String brand,
            @RequestParam(name = "name", required = false) String name) {
        try {
            List<Product> products = new ArrayList<>();

            if (!isItBlank(category) && !isItBlank(brand)) {
                products = productService.getProductsByCategoryAndBrand(category, brand);
            } else if (!isItBlank(category)) {
                products = productService.getProductsByCategory(category);
            } else if (!isItBlank(brand) && !isItBlank(name)) {
                products = productService.getProductsByBrandAndName(brand, name);
            } else if (!isItBlank(brand)) {
                products = productService.getProductsByBrand(brand);
            } else if (!isItBlank(name)) {
                products = productService.getProductsByName(name);
            } else if (isItBlank(category) && isItBlank(brand) && isItBlank(name)) {
                return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(
                        "Bad Search",
                        null));

            }

            if (!products.isEmpty()) {
                List<ProductDto> convertedProducts = productService.getConvertedProducts(products);
                return ResponseEntity.ok(new ApiResponse("Success", convertedProducts));
            } else {
                return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(
                        "No Product Found with Name: " + name
                                + " or with Brand: " + brand
                                + " or with Category: " + category,
                        null));
            }
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }
    private boolean isItBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
}
