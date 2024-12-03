package fr.simplex_software.fifty_shades_of_rest.orders.repository;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.jpa.*;
import io.quarkus.hibernate.orm.panache.*;
import jakarta.enterprise.context.*;

import java.util.*;

@ApplicationScoped
public class OrderRepository implements PanacheRepository<Order>
{
  public Order save(Order order)
  {
    persist(order);
    return order;
  }

  public int updateById(Long id, Order order)
  {
    return update("item = ?1, price = ?2 where id = ?3", order.getItem(), order.getPrice(), id);
  }

  public List<Order> findByItem(String item)
  {
    return list("item", item);
  }

  public List<Order> listOrdersByItem(String item)
  {
    return list("item", item);
  }

  public List<Order> findByCustomer(Customer customer)
  {
    return list("customer", customer);
  }

  public void deleteByCustomer(Customer customer)
  {
    delete("customer", customer);
  }
}
