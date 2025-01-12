/**
 * 
 */
package no.hvl.dat152.rest.ws.service;

import java.util.List;

import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import no.hvl.dat152.rest.ws.exceptions.OrderNotFoundException;
import no.hvl.dat152.rest.ws.exceptions.UnauthorizedOrderActionException;
import no.hvl.dat152.rest.ws.model.Order;
import no.hvl.dat152.rest.ws.repository.OrderRepository;
import no.hvl.dat152.rest.ws.security.UserDetailsImpl;

/**
 * @author tdoy
 */
@Service
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;
	
	// TODO copy your solutions from previous tasks!
	public Order saveOrder(Order order) {

		order = orderRepository.save(order);

		return order;
	}

	/**public Order findOrder(Long id) throws OrderNotFoundException {

	 Order order = orderRepository.findById(id)
	 .orElseThrow(()-> new OrderNotFoundException("Order with id: "+id+" not found in the order list!"));

	 return order;
	 }
	 **/
	// TODO public void deleteOrder(Long id)
	public void deleteOrder(Long id) throws OrderNotFoundException {
		Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException("Order: " +id + " not found"));

		orderRepository.delete(order);
	}

	// TODO public List<Order> findAllOrders()
	//public List<Order> findAllOrders(){
	//List<Order> orders = orderRepository.findAll();

	//return orders;
	//}

	public Page<Order> findAllOrders(Pageable pageable){
		return orderRepository.findAll(pageable);

	}

	// TODO public List<Order> findByExpiryDate(LocalDate expiry, Pageable page)
	public Page<Order> findByExpiryDate(LocalDate expiry, Pageable pageable) {
		return orderRepository.findByExpiryBefore(expiry, pageable);
	}

	// TODO public Order updateOrder(Order order, Long id)
	public Order updateOrder(Order order, Long id) throws OrderNotFoundException{
		Order existingOrder = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException("order: " + id + " not found"));

		existingOrder.setIsbn(order.getIsbn());
		existingOrder.setExpiry(order.getExpiry());

		return orderRepository.save(existingOrder);
	}

	public Order findOrder(Long id) throws OrderNotFoundException, UnauthorizedOrderActionException {

		verifyPrincipalOfOrder(id);	// verify who is making this request - Only ADMIN or SUPER_ADMIN can access any order
		Order order = orderRepository.findById(id)
				.orElseThrow(()-> new OrderNotFoundException("Order with id: "+id+" not found in the order list!"));

		return order;
	}


	

	
	private boolean verifyPrincipalOfOrder(Long id) throws UnauthorizedOrderActionException {
		
		JwtAuthenticationToken oauthJwtToken = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
		UserDetailsImpl userPrincipal = (UserDetailsImpl) oauthJwtToken.getDetails();
		// verify if the user sending request is an ADMIN or SUPER_ADMIN
		for(GrantedAuthority authority : userPrincipal.getAuthorities()){
			if(authority.getAuthority().equals("ADMIN")) {
				return true;
			}
		}
		
		// otherwise, make sure that the user is the one who initially made the order
		String email = orderRepository.findEmailByOrderId(id);
		
		if(email.equals(userPrincipal.getEmail()))
			return true;
		
		throw new UnauthorizedOrderActionException("Unauthorized order action!");

	}

}
