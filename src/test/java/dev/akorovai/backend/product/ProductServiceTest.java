package dev.akorovai.backend.product;

import com.github.javafaker.Faker;
import dev.akorovai.backend.color.Color;
import dev.akorovai.backend.color.ColorMapper;
import dev.akorovai.backend.color.response.ColorResponse;
import dev.akorovai.backend.handler.product.ProductNotFoundException;
import dev.akorovai.backend.product.mapper.ProductMapper;
import dev.akorovai.backend.product.request.ProductRequest;
import dev.akorovai.backend.product.response.ProductResponse;
import dev.akorovai.backend.product.response.ProductWithSizeAvailabilityResponse;
import dev.akorovai.backend.type.Type;
import dev.akorovai.backend.type.TypeMapper;
import dev.akorovai.backend.type.TypeRepository;
import dev.akorovai.backend.type.response.TypeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

	@Mock
	private ProductRepository productRepository;

	@Mock
	private ProductMapper productMapper;

	@Mock
	private ColorMapper colorMapper;

	@Mock
	private TypeMapper typeMapper;

	@InjectMocks
	private ProductService productService;

	@Mock
	private TypeRepository typeRepository;

	private Faker faker;

	@BeforeEach
	void setUp() {
		faker = new Faker();
	}

	@Nested
	class AddProductTests {
		@Test
		void testAddProduct() {
			// Arrange
			ProductRequest productRequest = createProductRequest();
			Product product = createProduct();
			Product savedProduct = createProduct();
			ProductResponse productResponse = createProductResponse();

			when(productMapper.toProduct(productRequest)).thenReturn(product);
			when(productRepository.save(product)).thenReturn(savedProduct);
			when(productMapper.toProductResponse(savedProduct)).thenReturn(productResponse);

			// Act
			ProductResponse result = productService.addProduct(productRequest);

			// Assert
			assertThat(result).isEqualTo(productResponse);
			verify(productMapper).toProduct(productRequest);
			verify(productRepository).save(product);
			verify(productMapper).toProductResponse(savedProduct);
		}
	}

	@Nested
	class ModifyProductTests {
		@Test
		void testModifyProductById() {
			// Arrange
			long productId = faker.number().randomNumber();
			ProductRequest productRequest = createProductRequest();
			Product product = createProduct();
			Product updatedProduct = createProduct();
			ProductResponse productResponse = createProductResponse();

			when(productRepository.findById(productId)).thenReturn(Optional.of(product));
			when(productRepository.save(product)).thenReturn(updatedProduct);
			when(productMapper.toProductResponse(updatedProduct)).thenReturn(productResponse);

			// Act
			ProductResponse result = productService.modifyProductById(productRequest, productId);

			// Assert
			assertThat(result).isEqualTo(productResponse);
			verify(productRepository).findById(productId);
			verify(productMapper).updateProductFromRequest(productRequest, product);
			verify(productRepository).save(product);
			verify(productMapper).toProductResponse(updatedProduct);
		}

		@Test
		void testModifyProductById_ProductNotFound() {
			// Arrange
			long productId = faker.number().randomNumber();
			ProductRequest productRequest = createProductRequest();

			when(productRepository.findById(productId)).thenReturn(Optional.empty());

			// Act & Assert
			assertThatThrownBy(() -> productService.modifyProductById(productRequest, productId))
					.isInstanceOf(ProductNotFoundException.class)
					.hasMessage("Product not found with ID: " + productId);
			verify(productRepository).findById(productId);
			verifyNoMoreInteractions(productMapper, productRepository);
		}
	}

	@Nested
	class DeleteProductTests {
		@Test
		void testDeleteProduct() {
			// Arrange
			long productId = faker.number().randomNumber();

			when(productRepository.existsById(productId)).thenReturn(true);

			// Act
			productService.deleteProduct(productId);

			// Assert
			verify(productRepository).existsById(productId);
			verify(productRepository).deleteById(productId);
		}

		@Test
		void testDeleteProduct_ProductNotFound() {
			// Arrange
			long productId = faker.number().randomNumber();

			when(productRepository.existsById(productId)).thenReturn(false);

			// Act & Assert
			assertThatThrownBy(() -> productService.deleteProduct(productId))
					.isInstanceOf(ProductNotFoundException.class)
					.hasMessage("Product not found with ID: " + productId);
			verify(productRepository).existsById(productId);
			verifyNoMoreInteractions(productRepository);
		}
	}

	@Nested
	class DiscountTests {
		@Test
		void testAddDiscount() {
			// Arrange
			long productId = faker.number().randomNumber();
			int discount = faker.number().numberBetween(1, 100);
			Product product = createProduct();

			when(productRepository.findById(productId)).thenReturn(Optional.of(product));

			// Act
			productService.addDiscount(discount, productId);

			// Assert
			assertThat(product.getDiscount()).isEqualTo(discount);
			verify(productRepository).findById(productId);
			verify(productRepository).save(product);
		}

		@Test
		void testAddDiscount_ProductNotFound() {
			// Arrange
			long productId = faker.number().randomNumber();
			int discount = faker.number().numberBetween(1, 100);

			when(productRepository.findById(productId)).thenReturn(Optional.empty());

			// Act & Assert
			assertThatThrownBy(() -> productService.addDiscount(discount, productId))
					.isInstanceOf(ProductNotFoundException.class)
					.hasMessage("Product not found with ID: " + productId);
			verify(productRepository).findById(productId);
			verifyNoMoreInteractions(productRepository);
		}
	}

	@Nested
	class ProductRetrievalTests {
		@Test
		void testGetRandomDiscountedProducts() {
			// Arrange
			List<Product> discountedProducts = Arrays.asList(createProduct(), createProduct());
			List<ProductResponse> productResponses = Arrays.asList(createProductResponse(), createProductResponse());

			when(productRepository.findByDiscountGreaterThan(0)).thenReturn(discountedProducts);
			when(productMapper.toProductResponse(any(Product.class)))
					.thenReturn(productResponses.get(0), productResponses.get(1));

			// Act
			List<ProductResponse> result = productService.getRandomDiscountedProducts();

			// Assert
			assertThat(result).hasSize(2);
			assertThat(result).containsExactlyInAnyOrderElementsOf(productResponses);
			verify(productRepository).findByDiscountGreaterThan(0);
			verify(productMapper, times(2)).toProductResponse(any(Product.class));
		}

		@Test
		void testGetSimilarProducts() {
			// Arrange
			long productId = faker.number().randomNumber();
			Product product = createProduct();
			List<Product> similarProducts = Arrays.asList(createProduct(), createProduct());
			List<ProductResponse> productResponses = Arrays.asList(createProductResponse(), createProductResponse());

			when(productRepository.findById(productId)).thenReturn(Optional.of(product));
			when(productRepository.findByTypeAndGenderAndColor(any(Type.class), any(Gender.class), any(Color.class)))
					.thenReturn(similarProducts);
			when(productMapper.toProductResponse(any(Product.class)))
					.thenReturn(productResponses.get(0), productResponses.get(1));

			// Act
			List<ProductResponse> result = productService.getSimilarProducts(productId);

			// Assert
			assertThat(result).hasSize(2);
			assertThat(result).containsExactlyInAnyOrderElementsOf(productResponses);
			verify(productRepository).findById(productId);
			verify(productRepository).findByTypeAndGenderAndColor(any(Type.class), any(Gender.class), any(Color.class));
			verify(productMapper, times(2)).toProductResponse(any(Product.class));
		}

		@Test
		void testGetProductsByType() {

			String typeName = faker.commerce().department();

			Type type = Type.builder()
					            .id(1L)
					            .name(typeName)
					            .build();

			List<Product> products = Arrays.asList(createProduct(), createProduct());
			List<ProductResponse> productResponses = Arrays.asList(createProductResponse(), createProductResponse());


			when(typeRepository.findByName(typeName)).thenReturn(Optional.of(type));

			// Mock the productRepository to return the list of products
			when(productRepository.findByType(type)).thenReturn(products);

			// Mock the productMapper to convert Product to ProductResponse
			when(productMapper.toProductResponse(any(Product.class)))
					.thenReturn(productResponses.get(0), productResponses.get(1));

			// Act
			List<ProductResponse> result = productService.getProductsByType(typeName);

			// Assert
			assertThat(result).hasSize(2); // Verify the size of the result list
			assertThat(result).containsExactlyInAnyOrderElementsOf(productResponses); // Verify the contents of the result list

			// Verify interactions
			verify(typeRepository).findByName(typeName); // Verify typeRepository was called
			verify(productRepository).findByType(type); // Verify productRepository was called
			verify(productMapper, times(2)).toProductResponse(any(Product.class)); // Verify productMapper was called twice
		}

		@Test
		void testGetProductsByGender() {
			// Arrange
			Gender gender = Gender.MEN;
			List<Product> products = Arrays.asList(createProduct(), createProduct());
			List<ProductResponse> productResponses = Arrays.asList(createProductResponse(), createProductResponse());

			when(productRepository.findByGender(gender)).thenReturn(products);
			when(productMapper.toProductResponse(any(Product.class)))
					.thenReturn(productResponses.get(0), productResponses.get(1));

			// Act
			List<ProductResponse> result = productService.getProductsByGender(gender);

			// Assert
			assertThat(result).hasSize(2);
			assertThat(result).containsExactlyInAnyOrderElementsOf(productResponses);
			verify(productRepository).findByGender(gender);
			verify(productMapper, times(2)).toProductResponse(any(Product.class));
		}

		@Test
		void testGetNewProducts() {
			// Arrange
			LocalDateTime fixedCutoffDate = LocalDateTime.of(2024, 1, 1, 0, 0);
			LocalDateTime now = fixedCutoffDate.plusDays(1);
			LocalDateTime cutoffDate = now.minusDays(30);

			List<Product> newProducts = Arrays.asList(createProduct(), createProduct());
			List<ProductResponse> productResponses = Arrays.asList(createProductResponse(), createProductResponse());

			when(productRepository.findByCreatedDateAfter(cutoffDate)).thenReturn(newProducts);
			when(productMapper.toProductResponse(any(Product.class)))
					.thenReturn(productResponses.get(0), productResponses.get(1));

			try (MockedStatic<LocalDateTime> mockedLocalDateTime = mockStatic(LocalDateTime.class)) {
				mockedLocalDateTime.when(LocalDateTime::now).thenReturn(now);

				// Act
				List<ProductResponse> result = productService.getNewProducts();

				// Assert
				assertThat(result).hasSize(2);
				assertThat(result).containsExactlyInAnyOrderElementsOf(productResponses);
				verify(productRepository).findByCreatedDateAfter(cutoffDate);
				verify(productMapper, times(2)).toProductResponse(any(Product.class));
			}
		}

		@Test
		void testGetAllProductsWithFilters() {
			// Arrange
			TypeResponse typeResponse = TypeResponse.builder().name(faker.commerce().department()).build();
			Gender gender = Gender.MEN;
			ColorResponse colorResponse = ColorResponse.builder().name(faker.color().name()).build();
			Size size = Size.M;
			Double minPrice = 10.0;
			Double maxPrice = 100.0;

			Type type = Type.builder().name(typeResponse.getName()).build();
			Color color = Color.builder().name(colorResponse.getName()).build();

			Product product1 = createProduct();
			product1.setType(type);
			product1.setGender(gender);
			product1.setColor(color);
			product1.setSize(size);
			product1.setPrice(50.0);

			Product product2 = createProduct();
			product2.setType(type);
			product2.setGender(gender);
			product2.setColor(color);
			product2.setSize(size);
			product2.setPrice(75.0);

			List<Product> products = Arrays.asList(product1, product2);
			List<ProductResponse> productResponses = Arrays.asList(createProductResponse(), createProductResponse());

			when(typeMapper.toType(typeResponse)).thenReturn(type);
			when(colorMapper.toColor(colorResponse)).thenReturn(color);
			when(productRepository.findAll()).thenReturn(products);
			when(productMapper.toProductResponse(any(Product.class)))
					.thenReturn(productResponses.get(0), productResponses.get(1));

			// Act
			List<ProductResponse> result = productService.getAllProductsWithFilters(typeResponse, gender, colorResponse, size, minPrice, maxPrice);

			// Assert
			assertThat(result).hasSize(2);
			assertThat(result).containsExactlyInAnyOrderElementsOf(productResponses);
			verify(typeMapper).toType(typeResponse);
			verify(colorMapper).toColor(colorResponse);
			verify(productRepository).findAll();
			verify(productMapper, times(2)).toProductResponse(any(Product.class));
		}

		@Test
		void testGetProductWithSizeAvailability() {
			// Arrange
			long productId = faker.number().randomNumber();
			Product product = createProduct();
			List<Product> productsWithSameName = Arrays.asList(createProduct(), createProduct());
			ProductWithSizeAvailabilityResponse response = createProductWithSizeAvailabilityResponse();

			when(productRepository.findById(productId)).thenReturn(Optional.of(product));
			when(productRepository.findByName(product.getName())).thenReturn(productsWithSameName);
			when(productMapper.toProductWithSizeAvailabilityResponse(product)).thenReturn(response);

			// Act
			ProductWithSizeAvailabilityResponse result = productService.getProductWithSizeAvailability(productId);

			// Assert
			assertThat(result).isEqualTo(response);
			verify(productRepository).findById(productId);
			verify(productRepository).findByName(product.getName());
			verify(productMapper).toProductWithSizeAvailabilityResponse(product);
		}
	}

	// Helper methods for creating test data
	private ProductRequest createProductRequest() {
		return ProductRequest.builder()
				       .id(faker.number().randomNumber())
				       .name(faker.commerce().productName())
				       .description(faker.lorem().sentence())
				       .links(Arrays.asList(faker.internet().url(), faker.internet().url()))
				       .amount(faker.number().numberBetween(1, 100))
				       .discount(faker.number().numberBetween(0, 100))
				       .size(Size.M)
				       .type(TypeResponse.builder().name(faker.commerce().department()).build())
				       .color(ColorResponse.builder().name(faker.color().name()).build())
				       .price(faker.number().randomDouble(2, 10, 1000))
				       .gender(Gender.MEN)
				       .build();
	}

	private Product createProduct() {
		return Product.builder()
				       .id(faker.number().randomNumber())
				       .name(faker.commerce().productName())
				       .description(faker.lorem().sentence())
				       .price(faker.number().randomDouble(2, 10, 1000))
				       .color(Color.builder().name(faker.color().name()).build())
				       .type(Type.builder().name(faker.commerce().department()).build())
				       .links(Arrays.asList(faker.internet().url(), faker.internet().url()))
				       .gender(Gender.MEN)
				       .amount(faker.number().numberBetween(1, 100))
				       .discount(faker.number().numberBetween(0, 100))
				       .size(Size.M)
				       .build();
	}

	private ProductResponse createProductResponse() {
		return ProductResponse.builder()
				       .id(faker.number().randomNumber())
				       .name(faker.commerce().productName())
				       .description(faker.lorem().sentence())
				       .price(faker.number().randomDouble(2, 10, 1000))
				       .color(ColorResponse.builder().name(faker.color().name()).build())
				       .type(TypeResponse.builder().name(faker.commerce().department()).build())
				       .links(Arrays.asList(faker.internet().url(), faker.internet().url()))
				       .gender(Gender.MEN)
				       .amount(faker.number().numberBetween(1, 100))
				       .discount(faker.number().numberBetween(0, 100))
				       .size(Size.M)
				       .build();
	}

	private ProductWithSizeAvailabilityResponse createProductWithSizeAvailabilityResponse() {
		return ProductWithSizeAvailabilityResponse.builder()
				       .id(faker.number().randomNumber())
				       .name(faker.commerce().productName())
				       .description(faker.lorem().sentence())
				       .price(faker.number().randomDouble(2, 10, 1000))
				       .color(ColorResponse.builder().name(faker.color().name()).build())
				       .type(TypeResponse.builder().name(faker.commerce().department()).build())
				       .links(Arrays.asList(faker.internet().url(), faker.internet().url()))
				       .gender(Gender.MEN)
				       .amount(faker.number().numberBetween(1, 100))
				       .discount(faker.number().numberBetween(0, 100))
				       .size(Size.M)
				       .sizeAvailabilityByColor(new HashMap<>())
				       .build();
	}
	@Test
	void getProductsWithSizeAvailability_ShouldReturnListOfProductsWithSizeAvailability() {
		// Arrange
		Product product1 = Product.builder()
				                   .id(1L)
				                   .name("T-Shirt")
				                   .color(Color.builder().id(1L).name("Red").code("123456").build())
				                   .size(Size.M)
				                   .build();

		Product product2 = Product.builder()
				                   .id(2L)
				                   .name("T-Shirt")
				                   .color(Color.builder().id(1L).name("Red").code("123456").build())
				                   .size(Size.L)
				                   .build();

		Product product3 = Product.builder()
				                   .id(3L)
				                   .name("Jeans")
				                   .color(Color.builder().id(2L).name("Blue").code("654321").build())
				                   .size(Size.M)
				                   .build();

		List<Product> products = Arrays.asList(product1, product2, product3);


		when(productRepository.findAll()).thenReturn(products);

		ProductWithSizeAvailabilityResponse response1 = ProductWithSizeAvailabilityResponse.builder()
				                                                .id(1L)
				                                                .name("T-Shirt")
				                                                .color(new ColorResponse(1L, "Red", "123456"))
				                                                .size(Size.M)
				                                                .sizeAvailabilityByColor(Map.of(
						                                                new ColorResponse(1L, "Red", "123456"),
						                                                Set.of(Size.M, Size.L)
				                                                ))
				                                                .build();

		ProductWithSizeAvailabilityResponse response2 = ProductWithSizeAvailabilityResponse.builder()
				                                                .id(3L)
				                                                .name("Jeans")
				                                                .color(new ColorResponse(2L, "Blue", "654321"))
				                                                .size(Size.M)
				                                                .sizeAvailabilityByColor(Map.of(
						                                                new ColorResponse(2L, "Blue", "654321"),
						                                                Set.of(Size.M)
				                                                ))
				                                                .build();

		when(productMapper.toProductWithSizeAvailabilityResponse(any(Product.class)))
				.thenReturn(response1, response2);

		// Act
		List<ProductWithSizeAvailabilityResponse> result = productService.getProductsWithSizeAvailability();

		// Assert
		assertThat(result).hasSize(2);
		assertThat(result).containsExactlyInAnyOrder(response1, response2);

		// Verify interactions
		verify(productRepository, times(1)).findAll();
		verify(productMapper, times(2)).toProductWithSizeAvailabilityResponse(any(Product.class));
	}
}