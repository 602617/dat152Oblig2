package no.hvl.dat152.rest.ws.main.test;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import no.hvl.dat152.rest.ws.exceptions.OrderNotFoundException;
import no.hvl.dat152.rest.ws.model.Order;
import no.hvl.dat152.rest.ws.service.OrderService;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class TestOrder {
	
	@Autowired
	private OrderService orderService;
	
	private String API_ROOT = "http://localhost:8090/elibrary/api/v1";
	
	@DisplayName("JUnit test for filter by Expiry @GetMapping(/orders) endpoint")
	@Test
	public void getAllOrders_thenOK() {
		String expiry = LocalDate.now().plusWeeks(4).toString();
		Response response = RestAssured.given()
				.param("expiry", expiry)
				.param("page", 0)
				.param("size", 4)
				.get(API_ROOT+"/orders");

		System.out.println(response.getBody().asString());

		assertEquals(HttpStatus.OK.value(), response.getStatusCode());


		//assertTrue(response.jsonPath().getList("isbn").size() > 0);

		List<String> isbns = response.jsonPath().getList("isbn", String.class);

		// Ensure the list of ISBNs is not null and has items
		assertNotNull(isbns, "The list of ISBNs should not be null.");
		assertTrue(isbns.size() > 0, "The list of ISBNs should contain items.");
	}
	
	@DisplayName("JUnit test for Paging @GetMapping(/orders) endpoint")
	@Test
	public void getAllOrdersPaginate_thenOK() {
		
		Response response = RestAssured.given()
				.param("page", 0)
				.param("size", 2)
				.get(API_ROOT+"/orders");
		System.out.println(response.getBody().asString());

		assertEquals(HttpStatus.OK.value(), response.getStatusCode());
		assertTrue(response.jsonPath().getList("isbn").size() == 2);
	}
	
	@DisplayName("JUnit test for @GetMapping(/orders/{id}) endpoint")
	@Test
	public void getOrderById_thenOK() {

	    Response response = RestAssured.get(API_ROOT+"/orders/2");
	    
	    assertEquals(HttpStatus.OK.value(), response.getStatusCode());
	    assertEquals("2", response.jsonPath().get("id").toString());
	}
	
	@DisplayName("JUnit test for @PutMapping(/orders/{id}) endpoint")
	@Test
	@DirtiesContext
	public void updateOrder_thenOK() throws OrderNotFoundException {
	
		Order order = orderService.findOrder(2L);
		order.setExpiry(LocalDate.now().plusWeeks(4));
		
		String nexpiry = LocalDate.now().plusWeeks(4).toString();
		
		Response response = RestAssured.given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(order)
				.put(API_ROOT+"/orders/{id}", order.getId());
	    
	    assertEquals(HttpStatus.OK.value(), response.getStatusCode());
	    assertEquals(nexpiry, response.jsonPath().get("expiry"));
	}
	
	@DisplayName("JUnit test for @DeleteMapping(/orders/{id}) endpoint")
	@Test
	@DirtiesContext
	public void deleteOrderById_thenOK() {

	    Response response = RestAssured.delete(API_ROOT+"/orders/1");
	    
	    assertEquals(HttpStatus.OK.value(), response.getStatusCode());
	    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), RestAssured.get(API_ROOT+"/orders/1").getStatusCode());

	}
	
	@DisplayName("JUnit test for HATEOAS @GetMapping(/orders/{id}) endpoint")
	@Test
	public void getOrderById_HATEOAS_thenOK() {

	    Response response = RestAssured.get(API_ROOT+"/orders/2");
	    
	    assertTrue(response.jsonPath().get("_links").toString().contains("href"));
	}

}
