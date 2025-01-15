package dev.akorovai.backend.product;

import dev.akorovai.backend.color.response.ColorResponse;
import dev.akorovai.backend.product.request.ProductRequest;
import dev.akorovai.backend.product.response.ProductResponse;
import dev.akorovai.backend.product.response.ProductWithSizeAvailabilityResponse;
import dev.akorovai.backend.security.ResponseRecord;
import dev.akorovai.backend.type.Type;
import dev.akorovai.backend.type.response.TypeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    // this one
    @PostMapping
    public ResponseEntity<ResponseRecord> addProduct(@RequestBody ProductRequest productRequest) {
        ProductResponse response = productService.addProduct(productRequest);
        ResponseRecord responseRecord = ResponseRecord.builder()
                                                .code(HttpStatus.CREATED.value())
                                                .message(response)
                                                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(responseRecord);
    }
    // this one
    @PutMapping("/{productId}")
    public ResponseEntity<ResponseRecord> modifyProduct(
            @PathVariable long productId,
            @RequestBody ProductRequest productRequest) {
        ProductResponse response = productService.modifyProductById(productRequest, productId);
        ResponseRecord responseRecord = ResponseRecord.builder()
                                                .code(HttpStatus.OK.value())
                                                .message(response)
                                                .build();
        return ResponseEntity.ok(responseRecord);
    }
    // this one
    @DeleteMapping("/{productId}")
    public ResponseEntity<ResponseRecord> deleteProduct(@PathVariable long productId) {
        productService.deleteProduct(productId);
        ResponseRecord responseRecord = ResponseRecord.builder()
                                                .code(HttpStatus.NO_CONTENT.value())
                                                .message("Product deleted successfully")
                                                .build();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(responseRecord);
    }
    // this one
    @PostMapping("/{productId}/discount")
    public ResponseEntity<ResponseRecord> addDiscount(
            @PathVariable long productId,
            @RequestParam int discount) {
        productService.addDiscount(discount, productId);
        ResponseRecord responseRecord = ResponseRecord.builder()
                                                .code(HttpStatus.OK.value())
                                                .message("Discount applied successfully")
                                                .build();
        return ResponseEntity.ok(responseRecord);
    }

    @GetMapping("/discounted")
    public ResponseEntity<ResponseRecord> getRandomDiscountedProducts() {
        List<ProductResponse> responses = productService.getRandomDiscountedProducts();
        ResponseRecord responseRecord = ResponseRecord.builder()
                                                .code(HttpStatus.OK.value())
                                                .message(responses)
                                                .build();
        return ResponseEntity.ok(responseRecord);
    }

    @GetMapping("/{productId}/similar")
    public ResponseEntity<ResponseRecord> getSimilarProducts(@PathVariable long productId) {
        List<ProductResponse> responses = productService.getSimilarProducts(productId);
        ResponseRecord responseRecord = ResponseRecord.builder()
                                                .code(HttpStatus.OK.value())
                                                .message(responses)
                                                .build();
        return ResponseEntity.ok(responseRecord);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<ResponseRecord> getProductsByType(@PathVariable String type) {
        List<ProductResponse> responses = productService.getProductsByType(type);
        ResponseRecord responseRecord = ResponseRecord.builder()
                                                .code(HttpStatus.OK.value())
                                                .message(responses)
                                                .build();
        return ResponseEntity.ok(responseRecord);
    }



    @GetMapping("/gender/{gender}")
    public ResponseEntity<ResponseRecord> getProductsByGender(@PathVariable Gender gender) {
        List<ProductResponse> responses = productService.getProductsByGender(gender);
        ResponseRecord responseRecord = ResponseRecord.builder()
                                                .code(HttpStatus.OK.value())
                                                .message(responses)
                                                .build();
        return ResponseEntity.ok(responseRecord);
    }

    @GetMapping("/new")
    public ResponseEntity<ResponseRecord> getNewProducts() {
        List<ProductResponse> responses = productService.getNewProducts();
        ResponseRecord responseRecord = ResponseRecord.builder()
                                                .code(HttpStatus.OK.value())
                                                .message(responses)
                                                .build();
        return ResponseEntity.ok(responseRecord);
    }

    @GetMapping("/filter")
    public ResponseEntity<ResponseRecord> getAllProductsWithFilters(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String size,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {
        TypeResponse typeResponse = type != null ? new TypeResponse(1L, type) : null;
        Gender genderEnum = gender != null ? Gender.valueOf(gender.toUpperCase()) : null;
        ColorResponse colorResponse = color != null ? new ColorResponse(1L, color, "123456") : null;
        Size sizeEnum = size != null ? Size.valueOf(size.toUpperCase()) : null;

        List<ProductResponse> responses = productService.getAllProductsWithFilters(
                typeResponse, genderEnum, colorResponse, sizeEnum, minPrice, maxPrice);

        ResponseRecord responseRecord = ResponseRecord.builder()
                                                .code(HttpStatus.OK.value())
                                                .message(responses)
                                                .build();

        return ResponseEntity.ok(responseRecord);
    }

    @GetMapping("/{productId}/size-availability")
    public ResponseEntity<ResponseRecord> getProductWithSizeAvailability(@PathVariable long productId) {
        ProductWithSizeAvailabilityResponse response = productService.getProductWithSizeAvailability(productId);
        ResponseRecord responseRecord = ResponseRecord.builder()
                                                .code(HttpStatus.OK.value())
                                                .message(response)
                                                .build();
        return ResponseEntity.ok(responseRecord);
    }

    @GetMapping("/size-availability")
    public ResponseEntity<ResponseRecord> getProductsWithSizeAvailability() {
        List<ProductWithSizeAvailabilityResponse> responses = productService.getProductsWithSizeAvailability();
        ResponseRecord responseRecord = ResponseRecord.builder()
                                                .code(HttpStatus.OK.value())
                                                .message(responses)
                                                .build();
        return ResponseEntity.ok(responseRecord);
    }
}