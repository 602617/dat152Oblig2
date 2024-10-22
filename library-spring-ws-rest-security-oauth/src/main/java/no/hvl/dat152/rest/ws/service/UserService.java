/**
 * 
 */
package no.hvl.dat152.rest.ws.service;


import java.util.List;
import java.util.Set;

import no.hvl.dat152.rest.ws.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.hvl.dat152.rest.ws.exceptions.OrderNotFoundException;
import no.hvl.dat152.rest.ws.exceptions.UserNotFoundException;
import no.hvl.dat152.rest.ws.model.Order;
import no.hvl.dat152.rest.ws.model.User;
import no.hvl.dat152.rest.ws.repository.UserRepository;

/**
 * @author tdoy
 */
@Service
public class UserService {

	// TODO copy your solutions from previous tasks!
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;


    public List<User> findAllUsers(){

        List<User> allUsers = (List<User>) userRepository.findAll();

        return allUsers;
    }

    public User findUser(Long userid) throws UserNotFoundException {

        User user = userRepository.findById(userid)
                .orElseThrow(()-> new UserNotFoundException("User with id: "+userid+" not found"));

        return user;
    }


    // TODO public User saveUser(User user)
    public User saveUser(User user){
        return userRepository.save(user);
    }

    // TODO public void deleteUser(Long id) throws UserNotFoundException
    public void deleteUser(Long id) throws UserNotFoundException {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("user with id:" + id + " not found"));

        userRepository.delete(user);

    }

    // TODO public User updateUser(User user, Long id)
    public  User updateUser(User user, Long id) throws UserNotFoundException {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("user with id: " + id + " not found"));

        existingUser.setFirstname(user.getFirstname());
        existingUser.setLastname(user.getLastname());

        return userRepository.save(existingUser);
    }

    // TODO public Set<Order> getUserOrders(Long userid)
    public Set<Order> getUserOrders(Long userid) throws UserNotFoundException{
        User existingUser = userRepository.findById(userid)
                .orElseThrow(() -> new UserNotFoundException("user: " + userid + " not found"));

        Set<Order> userOrders = existingUser.getOrders();

        return userOrders;
    }

    // TODO public Order getUserOrder(Long userid, Long oid)
    public Order getUserOrder(Long userid, Long oid) throws UserNotFoundException, OrderNotFoundException{
        User user = userRepository.findById(userid).orElseThrow(() -> new UserNotFoundException("user: " + userid + " not found"));

        for(Order order : user.getOrders()) {
            if(order.getId().equals(oid)){
                return order;
            }
        }

        throw new OrderNotFoundException("order id: " + oid + " not found for user " + userid);
    }

    // TODO public void deleteOrderForUser(Long userid, Long oid)
    public void deleteOrderForUser(Long userid, Long oid) throws UserNotFoundException, OrderNotFoundException{
        User user = userRepository.findById(userid).orElseThrow(() -> new UserNotFoundException("user: " + userid+ " not found"));

        Order order = orderRepository.findById(oid).orElseThrow(()-> new OrderNotFoundException("order: " + oid + " not found for user: " + userid));
        orderRepository.delete(order);
    }

    // TODO public User createOrdersForUser(Long userid, Order order)
    public Order createOrdersForUser(Long userid, Order order) throws UserNotFoundException {
        User user = findUser(userid);
        user.addOrder(order);

        saveUser(user);
        return order;
    }
}
