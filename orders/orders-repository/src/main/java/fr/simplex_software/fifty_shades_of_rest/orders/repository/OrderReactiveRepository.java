package fr.simplex_software.fifty_shades_of_rest.orders.repository;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.jpa.*;
import io.quarkus.hibernate.reactive.panache.*;
import io.smallrye.mutiny.*;
import jakarta.enterprise.context.*;

import java.util.*;

@ApplicationScoped
public class OrderReactiveRepository implements PanacheRepositoryBase<Order, Long>
{
  public Uni<Order> save(Order order)
  {
    return persist(order).chain(() -> Uni.createFrom().item(order));
  }

  public Uni<Order> createOrder(Order order)
  {
    return persist(order)
      .map(ignored -> order);
  }


  public Uni<List<Order>> listOrdersWithOrders()
  {
    return list("""
      select distinct c from Order c
      left join fetch c.orders 
      where c.id is not null 
      order by c.lastName""");
  }

  public Uni<Integer> updateById(Long id, Order order)
  {
    return update("firstName = ?1, lastName = ?2 where id = ?3",
      order.getItem(), order.getPrice(), id);
  }

  public Uni<List<Order>> findByCustomerId(Long customerId)
  {
    return find("customer.id", customerId).list();
  }

  public Uni<Optional<Order>> findByIdOptional(Long id)
  {
    return findById(id)
      .map(Optional::ofNullable);
  }
}
