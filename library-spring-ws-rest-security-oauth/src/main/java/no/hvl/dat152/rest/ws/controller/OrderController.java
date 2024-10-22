/**
 * 
 */
package no.hvl.dat152.rest.ws.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.hvl.dat152.rest.ws.exceptions.OrderNotFoundException;
import no.hvl.dat152.rest.ws.exceptions.UnauthorizedOrderActionException;
import no.hvl.dat152.rest.ws.exceptions.UserNotFoundException;
import no.hvl.dat152.rest.ws.model.Order;
import no.hvl.dat152.rest.ws.service.OrderService;

/**
 * @author tdoy
 */
@RestController
@RequestMapping("/elibrary/api/v1")
public class OrderController {

	// TODO authority annotation
    @Autowired
    private OrderService orderService;

    // TODO - getAllBorrowOrders (@Mappings, URI=/orders, and method) + filter by expiry and paginate
    @GetMapping("/orders")
    public ResponseEntity<Page<Order>> getAllBorrowOrders(@RequestParam(required = false) LocalDate expiry,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size) throws OrderNotFoundException {

        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders;
        if (expiry != null) {
            orders = orderService.findByExpiryDate(expiry, pageable);  // Filter by expiry date
        } else {
            orders = orderService.findAllOrders(pageable);  // No filtering, just pagination
        }

        // Check if orders are available
        if (orders.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        // Return the paginated list of orders
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    // TODO - getBorrowOrder (@Mappings, URI=/orders/{id}, and method)
    @GetMapping("/orders/{id}")
    public ResponseEntity<EntityModel<Order>> getBorrowOrder(@PathVariable("id")Long id) throws OrderNotFoundException, UnauthorizedOrderActionException {

        Order order = orderService.findOrder(id);

        if (order == null){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        Link selfLink = WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(OrderController.class).getBorrowOrder(id))
                .withSelfRel();
        EntityModel<Order> orderModel = EntityModel.of(order);
        orderModel.add(selfLink);
        return new ResponseEntity<>(orderModel, HttpStatus.OK);
    }

    // TODO - updateOrder (@Mappings, URI=/orders/{id}, and method)
    @PutMapping("/orders/{id}")
    public ResponseEntity<Order> updateOrder(@RequestBody Order order, @PathVariable("id")Long id) throws OrderNotFoundException{

        orderService.updateOrder(order, id);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    // TODO - deleteBookOrder (@Mappings, URI=/orders/{id}, and method)
    @DeleteMapping("/orders/{id}")
    public ResponseEntity<Order> deleteBookOrder(@PathVariable("id")Long id) throws OrderNotFoundException, UnauthorizedOrderActionException {
        Order order = orderService.findOrder(id);

        if (order == null){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        orderService.deleteOrder(order.getId());
        return new ResponseEntity<>(order, HttpStatus.OK);
    }
	
}
