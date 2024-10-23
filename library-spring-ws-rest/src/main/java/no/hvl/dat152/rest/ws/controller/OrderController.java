/**
 * 
 */
package no.hvl.dat152.rest.ws.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import no.hvl.dat152.rest.ws.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.hvl.dat152.rest.ws.exceptions.OrderNotFoundException;
import no.hvl.dat152.rest.ws.exceptions.UserNotFoundException;
import no.hvl.dat152.rest.ws.model.Order;
import no.hvl.dat152.rest.ws.service.OrderService;

/**
 * @author tdoy
 */
@RestController
@RequestMapping("/elibrary/api/v1")
public class OrderController {

    @Autowired
    private OrderService orderService;

	// TODO - getAllBorrowOrders (@Mappings, URI=/orders, and method) + filter by expiry and paginate
    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAllBorrowOrders(@RequestParam(required = false) LocalDate expiry,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "3") int size) {

        Pageable pageable = PageRequest.of(page,size);
        List<Order> orders;


        if (expiry != null) {
            //return new ResponseEntity<>(orderService.findByExpiryDate(expiry,pageable),HttpStatus.OK);
            orders = orderService.findByExpiryDate(expiry, pageable);
        }else {
            //return new ResponseEntity<>(orderService.findAllOrders(),HttpStatus.OK);
            orders = orderService.findAllOrders(pageable);
        }

        return new ResponseEntity<>(orders, HttpStatus.OK);
    }
	
	// TODO - getBorrowOrder (@Mappings, URI=/orders/{id}, and method)
    @GetMapping("/orders/{id}")
    public ResponseEntity<EntityModel<Order>> getBorrowOrder(@PathVariable("id")Long id) throws OrderNotFoundException{

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
    public ResponseEntity<Order> deleteBookOrder(@PathVariable("id")Long id) throws OrderNotFoundException{
        Order order = orderService.findOrder(id);

        if (order == null){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        orderService.deleteOrder(order.getId());
        return new ResponseEntity<>(order, HttpStatus.OK);
    }


	
}
