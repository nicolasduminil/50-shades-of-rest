package fr.simplex_software.fifty_shades_of_rest.orders.repository.reactive;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.jpa.*;
import io.quarkus.hibernate.reactive.panache.*;
import io.smallrye.mutiny.*;
import jakarta.enterprise.context.*;

import java.util.*;

@ApplicationScoped
public class CustomerReactiveRepository implements PanacheRepository<Customer>
{
  public Uni<Customer> save(Customer customer)
  {
    return persist(customer).chain(() -> Uni.createFrom().item(customer));
  }

  public Uni<Customer> createCustomer(Customer customer)
  {
    return persist(customer)
      .map(ignored -> customer);
  }

  public Uni<List<Customer>> findByLastName(String lastName)
  {
    return list("lastName", lastName);
  }

  public Uni<Optional<Customer>> findByEmail(String email)
  {
    return find("email", email).firstResult().map(Optional::ofNullable);
  }

  public Uni<List<Customer>> listCustomersWithOrders()
  {
    return list("""
      select distinct c from Customer c
      left join fetch c.orders 
      where c.id is not null 
      order by c.lastName""");
  }

  public Uni<List<Customer>> listCustomersByLastName(String lastName)
  {
    return list("lastName", lastName);
  }

  public Uni<Integer> updateById(Long id, Customer customer)
  {
    return update("firstName = ?1, lastName = ?2 where id = ?3",
      customer.getFirstName(), customer.getLastName(), id);
  }

  public Uni<Long> deleteByLastName(String lastName)
  {
    return delete("lastName", lastName);
  }

  public Uni<Optional<Customer>> findByIdOptional(Long id)
  {
    return findById(id).map(Optional::ofNullable);
  }
}
