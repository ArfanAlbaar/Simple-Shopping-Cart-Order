package com.arfan.shop.service.product;

import com.arfan.shop.dto.ImageDto;
import com.arfan.shop.dto.ProductDto;
import com.arfan.shop.exception.AlreadyExistsException;
import com.arfan.shop.exception.ResourceNotFoundException;
import com.arfan.shop.model.*;
import com.arfan.shop.repository.*;
import com.arfan.shop.request.AddProductRequest;
import com.arfan.shop.request.UpdateProductRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@RequiredArgsConstructor
@Service
public class ProductServiceImplement implements ProductService {
    private final ProductRepository productRepository;

    private final CategoryRepository categoryRepository;

    private final ModelMapper modelMapper;

    private final ImageRepository imageRepository;

    private final CartItemRepository cartItemRepository;

    private final CartRepository cartRepository;

    @Override
    @Transactional
    public Product addProduct(AddProductRequest request) {

        if(productExists(request.getName(), request.getBrand())){
            throw new AlreadyExistsException(request.getBrand() + " " + request.getName() + " already exists, you may want to update this product instead!");
        }

        Category category = Optional.ofNullable(categoryRepository.findByName(request.getCategory().getName()))
                .orElseGet(() -> {
                    Category newCategory = new Category(request.getCategory().getName());
                    return categoryRepository.save(newCategory);
                });

        request.setCategory(category);
        return productRepository.save(createProduct(request, category));
    }

    private Product createProduct(AddProductRequest request, Category category) {
        return new Product(
                request.getName(),
                request.getBrand(),
                request.getPrice(),
                request.getInventory(),
                request.getDescription(),
                category
        );
    }

    private boolean productExists(String name, String brand) {
        return productRepository.existsByNameAndBrand(name, brand);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Product Not Found"));
    }

    @Override
    @Transactional
    public void deleteProductById(Long id) {
        productRepository.findById(id)
                .ifPresentOrElse(productRepository::delete,
                        () -> {throw  new ResourceNotFoundException("Product Not Found");});
    }

    @Override
    @Transactional
    public Product updateProduct(UpdateProductRequest request, Long productId) {
        Product updatedProduct = productRepository.findById(productId)
                .map(existingProduct -> updateExistingProduct(existingProduct, request))
                .map(productRepository::save)
                .orElseThrow(() -> new ResourceNotFoundException("Product Not Found"));

        // Update unit price in all associated CartItems
        updateCartItemsUnitPriceAndCartTotal(updatedProduct);

        return updatedProduct;
    }

    private Product updateExistingProduct(Product existingProduct, UpdateProductRequest request){
        existingProduct.setName(request.getName());
        existingProduct.setBrand(request.getBrand());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setInventory(request.getInventory());
        existingProduct.setDescription(request.getDescription());

        Category category = categoryRepository.findByName(request.getCategory().getName());
        existingProduct.setCategory(category);
        return existingProduct;
    }

    // Helper method to update Cart Items Unit Price
    private void updateCartItemsUnitPriceAndCartTotal(Product product) {
        List<CartItem> cartItems = cartItemRepository.findByProductId(product.getId());
        Set<Cart> cartsToUpdate = new HashSet<>();
        for (CartItem cartItem : cartItems) {
            cartItem.setUnitPrice(); // Set to new product price
            cartItem.setTotalPrice(); // Update total price based on quantity and unit price
            cartItemRepository.save(cartItem);

            cartsToUpdate.add(cartItem.getCart());
        }

        // Update total amount for each Cart containing this Product
        for (Cart cart : cartsToUpdate) {
            updateCartTotalAmount(cart);
            cartRepository.save(cart);
        }
    }

    // Helper method to calculate and update Cart's total amount
    private void updateCartTotalAmount(Cart cart) {
        BigDecimal totalAmount = cart.getCartItems()
                .stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalAmount(totalAmount);
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategoryNameContaining(category);
    }

    @Override
    public List<Product> getProductsByBrand(String brand) {
        return productRepository.findByBrandContaining(brand);
    }

    @Override
    public List<Product> getProductsByCategoryAndBrand(String category, String brand) {
        return productRepository.findByCategoryNameAndBrand(category, brand);
    }

    @Override
    public List<Product> getProductsByName(String name) {
        return productRepository.findByNameContaining(name);
    }

    @Override
    public List<Product> getProductsByBrandAndName(String brand, String name) {
        return productRepository.findByBrandAndName(brand, name);
    }

    @Override
    public Long countProductsByBrandAndName(String brand, String name) {
        return productRepository.countByBrandAndName(brand, name);
    }

    @Override
    public List<ProductDto> getConvertedProducts(List<Product> products) {
        return products.stream().map(this::convertToDto).toList();
    }

    @Override
    public ProductDto convertToDto(Product product) {
        ProductDto productDto = modelMapper.map(product, ProductDto.class);
        List<Image> images = imageRepository.findByProductId(product.getId());

        List<ImageDto> imageDtos = images.stream()
                .map(image -> modelMapper.map(image, ImageDto.class))
                .toList();

        productDto.setImages(imageDtos);
        return productDto;

    }
}
