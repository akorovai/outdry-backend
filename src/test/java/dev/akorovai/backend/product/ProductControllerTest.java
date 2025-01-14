package dev.akorovai.backend.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import dev.akorovai.backend.color.response.ColorResponse;
import dev.akorovai.backend.product.request.ProductRequest;
import dev.akorovai.backend.product.response.ProductResponse;
import dev.akorovai.backend.product.response.ProductWithSizeAvailabilityResponse;

import dev.akorovai.backend.type.TypeRepository;
import dev.akorovai.backend.type.response.TypeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

	@Mock
	private ProductService productService;

	@InjectMocks
	private ProductController productController;

	@Mock
	private TypeRepository typeRepository;
	private MockMvc mockMvc;
	private Faker faker;
	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
		faker = new Faker();
		objectMapper = new ObjectMapper();
	}

	@Nested
	class AddProductTests {
		@Test
		void addProduct_ShouldReturnCreatedResponse() throws Exception {
			// Arrange
			ProductRequest request = ProductRequest.builder()
					                         .name(faker.commerce().productName())
					                         .description(faker.lorem().sentence())
					                         .price(faker.number().randomDouble(2, 10, 100))
					                         .color(new ColorResponse(1L, "Red", 123456))
					                         .type(new TypeResponse(1L, "Clothing"))
					                         .gender(Gender.MEN)
					                         .size(Size.M)
					                         .amount(faker.number().randomDigit())
					                         .discount(faker.number().numberBetween(0, 50))
					                         .links(Collections.singletonList("https://example.com/image.jpg"))
					                         .build();

			ProductResponse productResponse = ProductResponse.builder()
					                                  .id(1L)
					                                  .name(request.getName())
					                                  .description(request.getDescription())
					                                  .price(request.getPrice())
					                                  .color(request.getColor())
					                                  .type(request.getType())
					                                  .gender(request.getGender())
					                                  .size(request.getSize())
					                                  .amount(request.getAmount())
					                                  .discount(request.getDiscount())
					                                  .links(request.getLinks())
					                                  .build();

			when(productService.addProduct(any(ProductRequest.class))).thenReturn(productResponse);

			// Act & Assert
			mockMvc.perform(post("/api/products")
					                .contentType(MediaType.APPLICATION_JSON)
					                .content(objectMapper.writeValueAsString(request)))
					.andDo(print()) // Print the response for debugging
					.andExpect(status().isCreated())
					.andExpect(jsonPath("$.code", is(HttpStatus.CREATED.value())))
					.andExpect(jsonPath("$.message.id", is(productResponse.getId().intValue())))
					.andExpect(jsonPath("$.message.name", is(productResponse.getName())))
					.andExpect(jsonPath("$.message.description", is(productResponse.getDescription())))
					.andExpect(jsonPath("$.message.price", is(productResponse.getPrice())))
					.andExpect(jsonPath("$.message.color.id", is(productResponse.getColor().getId().intValue())))
					.andExpect(jsonPath("$.message.color.name", is(productResponse.getColor().getName())))
					.andExpect(jsonPath("$.message.color.code", is(productResponse.getColor().getCode())))
					.andExpect(jsonPath("$.message.type.id", is(productResponse.getType().getId().intValue())))
					.andExpect(jsonPath("$.message.type.name", is(productResponse.getType().getName())))
					.andExpect(jsonPath("$.message.gender", is(productResponse.getGender().getDisplayName())))
					.andExpect(jsonPath("$.message.size", is(productResponse.getSize().getDisplayName())))
					.andExpect(jsonPath("$.message.amount", is(productResponse.getAmount())))
					.andExpect(jsonPath("$.message.discount", is(productResponse.getDiscount())))
					.andExpect(jsonPath("$.message.links", hasSize(productResponse.getLinks().size())));

			verify(productService, times(1)).addProduct(any(ProductRequest.class));
		}
	}

	@Nested
	class ModifyProductTests {
		@Test
		void modifyProduct_ShouldReturnOkResponse() throws Exception {
			// Arrange
			ProductRequest request = ProductRequest.builder()
					                         .name(faker.commerce().productName())
					                         .description(faker.lorem().sentence())
					                         .price(faker.number().randomDouble(2, 10, 100))
					                         .color(new ColorResponse(1L, "Red", 123456))
					                         .type(new TypeResponse(1L, "Clothing"))
					                         .gender(Gender.MEN)
					                         .size(Size.M)
					                         .amount(faker.number().randomDigit())
					                         .discount(faker.number().numberBetween(0, 50))
					                         .links(Collections.singletonList("https://example.com/image.jpg"))
					                         .build();

			ProductResponse response = ProductResponse.builder()
					                           .id(1L)
					                           .name(request.getName())
					                           .description(request.getDescription())
					                           .price(request.getPrice())
					                           .color(request.getColor())
					                           .type(request.getType())
					                           .gender(request.getGender())
					                           .size(request.getSize())
					                           .amount(request.getAmount())
					                           .discount(request.getDiscount())
					                           .links(request.getLinks())
					                           .build();

			when(productService.modifyProductById(any(ProductRequest.class), anyLong())).thenReturn(response);

			// Act & Assert
			mockMvc.perform(put("/api/products/1")
					                .contentType(MediaType.APPLICATION_JSON)
					                .content(objectMapper.writeValueAsString(request)))
					.andExpect(status().isOk()) // Expect 200 OK
					.andExpect(jsonPath("$.code", is(HttpStatus.OK.value())))
					.andExpect(jsonPath("$.message.id", is(response.getId().intValue())))
					.andExpect(jsonPath("$.message.name", is(response.getName())))
					.andExpect(jsonPath("$.message.description", is(response.getDescription())))
					.andExpect(jsonPath("$.message.price", is(response.getPrice())))
					.andExpect(jsonPath("$.message.color.id", is(response.getColor().getId().intValue())))
					.andExpect(jsonPath("$.message.color.name", is(response.getColor().getName())))
					.andExpect(jsonPath("$.message.color.code", is(response.getColor().getCode())))
					.andExpect(jsonPath("$.message.type.id", is(response.getType().getId().intValue())))
					.andExpect(jsonPath("$.message.type.name", is(response.getType().getName())))
					.andExpect(jsonPath("$.message.gender", is(response.getGender().getDisplayName())))
					.andExpect(jsonPath("$.message.size", is(response.getSize().getDisplayName())))
					.andExpect(jsonPath("$.message.amount", is(response.getAmount())))
					.andExpect(jsonPath("$.message.discount", is(response.getDiscount())))
					.andExpect(jsonPath("$.message.links", hasSize(response.getLinks().size())));

			verify(productService, times(1)).modifyProductById(any(ProductRequest.class), eq(1L));
		}
	}
	@Nested
	class DeleteProductTests {
		@Test
		void deleteProduct_ShouldReturnNoContentResponse() throws Exception {
			// Arrange
			doNothing().when(productService).deleteProduct(anyLong());

			// Act & Assert
			mockMvc.perform(delete("/api/products/1"))
					.andExpect(status().isNoContent())
					.andExpect(jsonPath("$.code", is(HttpStatus.NO_CONTENT.value())))
					.andExpect(jsonPath("$.message", is("Product deleted successfully")));

			verify(productService, times(1)).deleteProduct(1L);
		}
	}

	@Nested
	class DiscountTests {
		@Test
		void addDiscount_ShouldReturnOkResponse() throws Exception {
			// Arrange
			doNothing().when(productService).addDiscount(anyInt(), anyLong());

			// Act & Assert
			mockMvc.perform(post("/api/products/1/discount")
					                .param("discount", "10"))
					.andExpect(status().isOk()) // Expect 200 OK
					.andExpect(jsonPath("$.code", is(HttpStatus.OK.value())))
					.andExpect(jsonPath("$.message", is("Discount applied successfully")));

			verify(productService, times(1)).addDiscount(10, 1L);
		}
	}

	@Nested
	class GetRandomDiscountedProductsTests {
		@Test
		void getRandomDiscountedProducts_ShouldReturnOkResponse() throws Exception {
			// Arrange
			ProductResponse productResponse = ProductResponse.builder()
					                                  .id(1L)
					                                  .name(faker.commerce().productName())
					                                  .description(faker.lorem().sentence())
					                                  .price(faker.number().randomDouble(2, 10, 100))
					                                  .color(new ColorResponse(1L, "Red", 123456))
					                                  .type(new TypeResponse(1L, "Clothing"))
					                                  .gender(Gender.MEN) // Use the correct enum value
					                                  .size(Size.M)
					                                  .amount(faker.number().randomDigit())
					                                  .discount(faker.number().numberBetween(0, 50))
					                                  .links(Collections.singletonList("https://example.com/image.jpg"))
					                                  .build();

			List<ProductResponse> responses = Collections.singletonList(productResponse);
			when(productService.getRandomDiscountedProducts()).thenReturn(responses);

			// Act & Assert
			mockMvc.perform(get("/api/products/discounted"))
					.andExpect(status().isOk()) // Expect 200 OK
					.andExpect(jsonPath("$.code", is(HttpStatus.OK.value())))
					.andExpect(jsonPath("$.message", hasSize(1)))
					.andExpect(jsonPath("$.message[0].id", is(productResponse.getId().intValue())))
					.andExpect(jsonPath("$.message[0].name", is(productResponse.getName())))
					.andExpect(jsonPath("$.message[0].description", is(productResponse.getDescription())))
					.andExpect(jsonPath("$.message[0].price", is(productResponse.getPrice())))
					.andExpect(jsonPath("$.message[0].color.id", is(productResponse.getColor().getId().intValue())))
					.andExpect(jsonPath("$.message[0].color.name", is(productResponse.getColor().getName())))
					.andExpect(jsonPath("$.message[0].color.code", is(productResponse.getColor().getCode())))
					.andExpect(jsonPath("$.message[0].type.id", is(productResponse.getType().getId().intValue())))
					.andExpect(jsonPath("$.message[0].type.name", is(productResponse.getType().getName())))
					.andExpect(jsonPath("$.message[0].gender", is(productResponse.getGender().getDisplayName())))
					.andExpect(jsonPath("$.message[0].size", is(productResponse.getSize().getDisplayName())))
					.andExpect(jsonPath("$.message[0].amount", is(productResponse.getAmount())))
					.andExpect(jsonPath("$.message[0].discount", is(productResponse.getDiscount())))
					.andExpect(jsonPath("$.message[0].links", hasSize(productResponse.getLinks().size())));

			verify(productService, times(1)).getRandomDiscountedProducts();
		}
	}

	@Nested
	class GetSimilarProductsTests {
		@Test
		void getSimilarProducts_ShouldReturnOkResponse() throws Exception {
			// Arrange
			ProductResponse productResponse = ProductResponse.builder()
					                                  .id(1L)
					                                  .name(faker.commerce().productName())
					                                  .description(faker.lorem().sentence())
					                                  .price(faker.number().randomDouble(2, 10, 100))
					                                  .color(new ColorResponse(1L, "Red", 123456))
					                                  .type(new TypeResponse(1L, "Clothing"))
					                                  .gender(Gender.MEN) // Use the correct enum value
					                                  .size(Size.M)
					                                  .amount(faker.number().randomDigit())
					                                  .discount(faker.number().numberBetween(0, 50))
					                                  .links(Collections.singletonList("https://example.com/image.jpg"))
					                                  .build();

			List<ProductResponse> responses = Collections.singletonList(productResponse);
			when(productService.getSimilarProducts(anyLong())).thenReturn(responses);

			// Act & Assert
			mockMvc.perform(get("/api/products/1/similar"))
					.andExpect(status().isOk()) // Expect 200 OK
					.andExpect(jsonPath("$.code", is(HttpStatus.OK.value())))
					.andExpect(jsonPath("$.message", hasSize(1)))
					.andExpect(jsonPath("$.message[0].id", is(productResponse.getId().intValue())))
					.andExpect(jsonPath("$.message[0].name", is(productResponse.getName())))
					.andExpect(jsonPath("$.message[0].description", is(productResponse.getDescription())))
					.andExpect(jsonPath("$.message[0].price", is(productResponse.getPrice())))
					.andExpect(jsonPath("$.message[0].color.id", is(productResponse.getColor().getId().intValue())))
					.andExpect(jsonPath("$.message[0].color.name", is(productResponse.getColor().getName())))
					.andExpect(jsonPath("$.message[0].color.code", is(productResponse.getColor().getCode())))
					.andExpect(jsonPath("$.message[0].type.id", is(productResponse.getType().getId().intValue())))
					.andExpect(jsonPath("$.message[0].type.name", is(productResponse.getType().getName())))
					.andExpect(jsonPath("$.message[0].gender", is(productResponse.getGender().getDisplayName())))
					.andExpect(jsonPath("$.message[0].size", is(productResponse.getSize().getDisplayName())))
					.andExpect(jsonPath("$.message[0].amount", is(productResponse.getAmount())))
					.andExpect(jsonPath("$.message[0].discount", is(productResponse.getDiscount())))
					.andExpect(jsonPath("$.message[0].links", hasSize(productResponse.getLinks().size())));

			verify(productService, times(1)).getSimilarProducts(1L);
		}
	}
	@Nested
	class GetProductsByTypeTests {
		@Test
		void getProductsByType_ShouldReturnOkResponse() throws Exception {
			// Arrange
			String typeName = "CLOTHING"; // Use a consistent type name

			ProductResponse productResponse = ProductResponse.builder()
					                                  .id(1L)
					                                  .name(faker.commerce().productName())
					                                  .description(faker.lorem().sentence())
					                                  .price(faker.number().randomDouble(2, 10, 100))
					                                  .color(new ColorResponse(1L, "Red", 123456))
					                                  .type(new TypeResponse(1L, "CLOTHING")) // Use the same type name here
					                                  .gender(Gender.MEN)
					                                  .size(Size.M)
					                                  .amount(faker.number().randomDigit())
					                                  .discount(faker.number().numberBetween(0, 50))
					                                  .links(Collections.singletonList("https://example.com/image.jpg"))
					                                  .build();

			List<ProductResponse> responses = Collections.singletonList(productResponse);

			// Mock the ProductService to return the product list
			when(productService.getProductsByType(typeName)).thenReturn(responses);

			// Act & Assert
			mockMvc.perform(get("/api/products/type/{type}", typeName)) // Use the same type name here
					.andDo(print()) // Print the response for debugging
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.code", is(HttpStatus.OK.value())))
					.andExpect(jsonPath("$.message", hasSize(1)))
					.andExpect(jsonPath("$.message[0].id", is(productResponse.getId().intValue())))
					.andExpect(jsonPath("$.message[0].name", is(productResponse.getName())))
					.andExpect(jsonPath("$.message[0].description", is(productResponse.getDescription())))
					.andExpect(jsonPath("$.message[0].price", is(productResponse.getPrice())))
					.andExpect(jsonPath("$.message[0].color.id", is(productResponse.getColor().getId().intValue())))
					.andExpect(jsonPath("$.message[0].color.name", is(productResponse.getColor().getName())))
					.andExpect(jsonPath("$.message[0].color.code", is(productResponse.getColor().getCode())))
					.andExpect(jsonPath("$.message[0].type.id", is(productResponse.getType().getId().intValue())))
					.andExpect(jsonPath("$.message[0].type.name", is(productResponse.getType().getName())))
					.andExpect(jsonPath("$.message[0].gender", is(productResponse.getGender().getDisplayName())))
					.andExpect(jsonPath("$.message[0].size", is(productResponse.getSize().getDisplayName())))
					.andExpect(jsonPath("$.message[0].amount", is(productResponse.getAmount())))
					.andExpect(jsonPath("$.message[0].discount", is(productResponse.getDiscount())))
					.andExpect(jsonPath("$.message[0].links", hasSize(productResponse.getLinks().size())));

			// Verify interactions
			verify(productService, times(1)).getProductsByType(typeName);
		}
	}

	@Nested
	class GetProductsByGenderTests {
		@Test
		void getProductsByGender_ShouldReturnOkResponse() throws Exception {

			ProductResponse productResponse = ProductResponse.builder()
					                                  .id(1L)
					                                  .name(faker.commerce().productName())
					                                  .description(faker.lorem().sentence())
					                                  .price(faker.number().randomDouble(2, 10, 100))
					                                  .color(new ColorResponse(1L, "Red", 123456))
					                                  .type(new TypeResponse(1L, "Clothing"))
					                                  .gender(Gender.MEN)
					                                  .size(Size.M)
					                                  .amount(faker.number().randomDigit())
					                                  .discount(faker.number().numberBetween(0, 50))
					                                  .links(Collections.singletonList("https://example.com/image.jpg"))
					                                  .build();

			List<ProductResponse> responses = Collections.singletonList(productResponse);
			when(productService.getProductsByGender(any(Gender.class))).thenReturn(responses);


			mockMvc.perform(get("/api/products/gender/MEN"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.code", is(HttpStatus.OK.value())))
					.andExpect(jsonPath("$.message", hasSize(1)))
					.andExpect(jsonPath("$.message[0].id", is(productResponse.getId().intValue())))
					.andExpect(jsonPath("$.message[0].name", is(productResponse.getName())))
					.andExpect(jsonPath("$.message[0].description", is(productResponse.getDescription())))
					.andExpect(jsonPath("$.message[0].price", is(productResponse.getPrice())))
					.andExpect(jsonPath("$.message[0].color.id", is(productResponse.getColor().getId().intValue())))
					.andExpect(jsonPath("$.message[0].color.name", is(productResponse.getColor().getName())))
					.andExpect(jsonPath("$.message[0].color.code", is(productResponse.getColor().getCode())))
					.andExpect(jsonPath("$.message[0].type.id", is(productResponse.getType().getId().intValue())))
					.andExpect(jsonPath("$.message[0].type.name", is(productResponse.getType().getName())))
					.andExpect(jsonPath("$.message[0].gender", is(productResponse.getGender().getDisplayName())))
					.andExpect(jsonPath("$.message[0].size", is(productResponse.getSize().getDisplayName())))
					.andExpect(jsonPath("$.message[0].amount", is(productResponse.getAmount())))
					.andExpect(jsonPath("$.message[0].discount", is(productResponse.getDiscount())))
					.andExpect(jsonPath("$.message[0].links", hasSize(productResponse.getLinks().size())));

			verify(productService, times(1)).getProductsByGender(any(Gender.class));
		}
	}

	@Nested
	class GetNewProductsTests {
		@Test
		void getNewProducts_ShouldReturnOkResponse() throws Exception {
			// Arrange
			ProductResponse productResponse = ProductResponse.builder()
					                                  .id(1L)
					                                  .name(faker.commerce().productName())
					                                  .description(faker.lorem().sentence())
					                                  .price(faker.number().randomDouble(2, 10, 100))
					                                  .color(new ColorResponse(1L, "Red", 123456))
					                                  .type(new TypeResponse(1L, "Clothing"))
					                                  .gender(Gender.MEN)
					                                  .size(Size.M)
					                                  .amount(faker.number().randomDigit())
					                                  .discount(faker.number().numberBetween(0, 50))
					                                  .links(Collections.singletonList("https://example.com/image.jpg"))
					                                  .build();

			List<ProductResponse> responses = Collections.singletonList(productResponse);
			when(productService.getNewProducts()).thenReturn(responses);

			// Act & Assert
			mockMvc.perform(get("/api/products/new"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.code", is(HttpStatus.OK.value())))
					.andExpect(jsonPath("$.message", hasSize(1)))
					.andExpect(jsonPath("$.message[0].id", is(productResponse.getId().intValue())))
					.andExpect(jsonPath("$.message[0].name", is(productResponse.getName())))
					.andExpect(jsonPath("$.message[0].description", is(productResponse.getDescription())))
					.andExpect(jsonPath("$.message[0].price", is(productResponse.getPrice())))
					.andExpect(jsonPath("$.message[0].color.id", is(productResponse.getColor().getId().intValue())))
					.andExpect(jsonPath("$.message[0].color.name", is(productResponse.getColor().getName())))
					.andExpect(jsonPath("$.message[0].color.code", is(productResponse.getColor().getCode())))
					.andExpect(jsonPath("$.message[0].type.id", is(productResponse.getType().getId().intValue())))
					.andExpect(jsonPath("$.message[0].type.name", is(productResponse.getType().getName())))
					.andExpect(jsonPath("$.message[0].gender", is(productResponse.getGender().getDisplayName())))
					.andExpect(jsonPath("$.message[0].size", is(productResponse.getSize().getDisplayName())))
					.andExpect(jsonPath("$.message[0].amount", is(productResponse.getAmount())))
					.andExpect(jsonPath("$.message[0].discount", is(productResponse.getDiscount())))
					.andExpect(jsonPath("$.message[0].links", hasSize(productResponse.getLinks().size())));

			verify(productService, times(1)).getNewProducts();
		}
	}

	@Nested
	class GetAllProductsWithFiltersTests {
		@Test
		void getAllProductsWithFilters_ShouldReturnOkResponse() throws Exception {
			// Arrange
			ProductResponse productResponse = ProductResponse.builder()
					                                  .id(1L)
					                                  .name(faker.commerce().productName())
					                                  .description(faker.lorem().sentence())
					                                  .price(faker.number().randomDouble(2, 10, 100))
					                                  .color(new ColorResponse(1L, "Red", 123456))
					                                  .type(new TypeResponse(1L, "Clothing"))
					                                  .gender(Gender.MEN)
					                                  .size(Size.M)
					                                  .amount(faker.number().randomDigit())
					                                  .discount(faker.number().numberBetween(0, 50))
					                                  .links(Collections.singletonList("https://example.com/image.jpg"))
					                                  .build();

			List<ProductResponse> responses = Collections.singletonList(productResponse);
			when(productService.getAllProductsWithFilters(any(), any(), any(), any(), any(), any())).thenReturn(responses);

			// Act & Assert
			mockMvc.perform(get("/api/products/filter")
					                .param("type", "Clothing")
					                .param("gender", "MEN")
					                .param("color", "Red")
					                .param("size", "M")
					                .param("minPrice", "10")
					                .param("maxPrice", "100"))
					.andExpect(status().isOk()) // Expect 200 OK
					.andExpect(jsonPath("$.code", is(HttpStatus.OK.value())))
					.andExpect(jsonPath("$.message", hasSize(1)))
					.andExpect(jsonPath("$.message[0].id", is(productResponse.getId().intValue())))
					.andExpect(jsonPath("$.message[0].name", is(productResponse.getName())))
					.andExpect(jsonPath("$.message[0].description", is(productResponse.getDescription())))
					.andExpect(jsonPath("$.message[0].price", is(productResponse.getPrice())))
					.andExpect(jsonPath("$.message[0].color.id", is(productResponse.getColor().getId().intValue())))
					.andExpect(jsonPath("$.message[0].color.name", is(productResponse.getColor().getName())))
					.andExpect(jsonPath("$.message[0].color.code", is(productResponse.getColor().getCode())))
					.andExpect(jsonPath("$.message[0].type.id", is(productResponse.getType().getId().intValue())))
					.andExpect(jsonPath("$.message[0].type.name", is(productResponse.getType().getName())))
					.andExpect(jsonPath("$.message[0].gender", is(productResponse.getGender().getDisplayName()))) // Match displayName
					.andExpect(jsonPath("$.message[0].size", is(productResponse.getSize().getDisplayName()))) // Match displayName
					.andExpect(jsonPath("$.message[0].amount", is(productResponse.getAmount())))
					.andExpect(jsonPath("$.message[0].discount", is(productResponse.getDiscount())))
					.andExpect(jsonPath("$.message[0].links", hasSize(productResponse.getLinks().size())));

			verify(productService, times(1)).getAllProductsWithFilters(any(), any(), any(), any(), any(), any());
		}
	}
	@Nested
	class GetProductWithSizeAvailabilityTests {
		@Test
		void getProductWithSizeAvailability_ShouldReturnOkResponse() throws Exception {
			// Arrange
			ColorResponse colorResponse = new ColorResponse(1L, "Red", 123456);
			Set<Size> sizes = new HashSet<>(Arrays.asList(Size.S, Size.M, Size.L));

			ProductWithSizeAvailabilityResponse response = ProductWithSizeAvailabilityResponse.builder()
					                                               .id(1L)
					                                               .name(faker.commerce().productName())
					                                               .description(faker.lorem().sentence())
					                                               .price(faker.number().randomDouble(2, 10, 100))
					                                               .color(colorResponse)
					                                               .type(new TypeResponse(1L, "Clothing"))
					                                               .gender(Gender.MEN) // Use the enum value
					                                               .size(Size.M) // Use the enum value
					                                               .amount(faker.number().randomDigit())
					                                               .discount(faker.number().numberBetween(0, 50))
					                                               .links(Collections.singletonList("https://example.com/image.jpg"))
					                                               .sizeAvailabilityByColor(Map.of(colorResponse, sizes))
					                                               .build();

			when(productService.getProductWithSizeAvailability(anyLong())).thenReturn(response);

			// Act & Assert
			mockMvc.perform(get("/api/products/1/size-availability"))
					.andExpect(status().isOk()) // Expect 200 OK
					.andExpect(jsonPath("$.code", is(HttpStatus.OK.value())))
					.andExpect(jsonPath("$.message.id", is(response.getId().intValue())))
					.andExpect(jsonPath("$.message.name", is(response.getName())))
					.andExpect(jsonPath("$.message.description", is(response.getDescription())))
					.andExpect(jsonPath("$.message.price", is(response.getPrice())))
					.andExpect(jsonPath("$.message.color.id", is(response.getColor().getId().intValue())))
					.andExpect(jsonPath("$.message.color.name", is(response.getColor().getName())))
					.andExpect(jsonPath("$.message.color.code", is(response.getColor().getCode())))
					.andExpect(jsonPath("$.message.type.id", is(response.getType().getId().intValue())))
					.andExpect(jsonPath("$.message.type.name", is(response.getType().getName())))
					.andExpect(jsonPath("$.message.gender", is(response.getGender().getDisplayName()))) // Match displayName
					.andExpect(jsonPath("$.message.size", is(response.getSize().getDisplayName()))) // Match displayName
					.andExpect(jsonPath("$.message.amount", is(response.getAmount())))
					.andExpect(jsonPath("$.message.discount", is(response.getDiscount())))
					.andExpect(jsonPath("$.message.links", hasSize(response.getLinks().size())))
					.andExpect(jsonPath("$.message.sizeAvailabilityByColor", hasKey(colorResponse.getName())))
					.andExpect(jsonPath("$.message.sizeAvailabilityByColor['" + colorResponse.getName() + "']", hasSize(sizes.size())));

			verify(productService, times(1)).getProductWithSizeAvailability(1L);
		}
	}
}