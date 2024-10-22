/**
 * 
 */
package no.hvl.dat152.rest.ws.controller;

import java.util.List;
import java.util.Set;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import no.hvl.dat152.rest.ws.exceptions.OrderNotFoundException;
import no.hvl.dat152.rest.ws.exceptions.UserNotFoundException;
import no.hvl.dat152.rest.ws.model.Order;
import no.hvl.dat152.rest.ws.model.User;
import no.hvl.dat152.rest.ws.service.UserService;

/**
 * @author tdoy
 */
@RestController
@RequestMapping("/elibrary/api/v1")
public class UserController {

	@Autowired
	private UserService userService;
	
	@GetMapping("/users")
	public ResponseEntity<Object> getUsers(){
		
		List<User> users = userService.findAllUsers();
		
		if(users.isEmpty())
			
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		else
			return new ResponseEntity<>(users, HttpStatus.OK);
	}
	
	@GetMapping(value = "/users/{id}")
	public ResponseEntity<Object> getUser(@PathVariable("id") Long id) throws UserNotFoundException, OrderNotFoundException{
		
		User user = userService.findUser(id);
		
		return new ResponseEntity<>(user, HttpStatus.OK);	
		
	}
	
	// TODO - createUser (@Mappings, URI=/users, and method)
	@PostMapping("/users")
	public ResponseEntity<User> createUser(@RequestBody User user) {
		User createdUser = userService.saveUser(user);
		return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
	}

	// TODO - updateUser (@Mappings, URI, and method)
	@PutMapping("/users/{id}")
	public ResponseEntity<User> updateUser(@RequestBody User user, @PathVariable("id") Long id) throws UserNotFoundException{
		User updatedUser = userService.updateUser(user, id);
		return new ResponseEntity<>(updatedUser, HttpStatus.OK);
	}
	
	// TODO - deleteUser (@Mappings, URI, and method)
		@DeleteMapping("/users/{id}")
		public ResponseEntity<User> deleteUser(@PathVariable("id")Long id) throws UserNotFoundException {
		User user = userService.findUser(id);

		userService.deleteUser(user.getUserid());

		return new ResponseEntity<>(user, HttpStatus.OK);
		}


	//min
	// TODO - getUserOrders (@Mappings, URI=/users/{id}/orders, and method)
	@GetMapping("/users/{id}/orders")
	public ResponseEntity<Object> getUserOrders(@PathVariable("id")Long id) throws UserNotFoundException{
		Set<Order> orders = userService.getUserOrders(id);

		if(orders == null || orders.isEmpty()){
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(orders, HttpStatus.OK);
	}
	
	// TODO - getUserOrder (@Mappings, URI=/users/{uid}/orders/{oid}, and method)
	@GetMapping("/users/{uid}/orders/{oid}")
	public ResponseEntity<Order> getUserOrder(@PathVariable("uid")Long userid, @PathVariable("oid") Long orderid) throws UserNotFoundException, OrderNotFoundException {
		Order order = userService.getUserOrder(userid,orderid);

		if(order == null){
			//throw new OrderNotFoundException("order not found for id: " + orderid);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}

		return new ResponseEntity<>(order, HttpStatus.OK);

	}

	// TODO - deleteUserOrder (@Mappings, URI, and method)
		@DeleteMapping("/users/{uid}/orders/{oid}")
		public ResponseEntity<Void> deleteUserOrder(@PathVariable("uid")Long userid, @PathVariable("oid")Long orderid) throws UserNotFoundException, OrderNotFoundException{

			userService.deleteOrderForUser(userid, orderid);

			return new ResponseEntity<>(HttpStatus.OK);

		}
	
	// TODO - createUserOrder (@Mappings, URI, and method) + HATEOAS links
		@PostMapping("/users/{id}/orders")

		public ResponseEntity<Object> createUserOrder(@PathVariable("id") Long id,
													 @RequestBody Order order) throws UserNotFoundException, OrderNotFoundException {

			User user = userService.createOrdersForUser(id, order);


			return new ResponseEntity<>(user, HttpStatus.CREATED);
		}

		public void addLinks(Set<Order> orders) throws OrderNotFoundException{

		for (Order order : orders){
			Link rordersLink = linkTo(methodOn(OrderController.class).deleteBookOrder(order.getId())).withRel("Update_Return_or_Cancel");
			order.add(rordersLink);
		}
		}
	
}
