package fr.simplex_software.fifty_shades_of_rest.orders.repository;

import io.quarkus.hibernate.orm.panache.*;
import jakarta.enterprise.context.*;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.jpa.*;

import java.util.*;

@ApplicationScoped
public class CustomerRepository implements PanacheRepository<Customer>
{
  public Customer save(Customer customer)
  {
    persist(customer);
    return customer;
  }

  public List<Customer> findByLastName(String lastName)
  {
    return list("lastName", lastName);
  }

  public Optional<Customer> findByEmail(String email)
  {
    return find("email", email).firstResultOptional();
  }

  public List<Customer> listCustomersWithOrders()
  {
    return list("select distinct c from Customer c left join fetch c.orders where c.id is not null order by c.lastName");
  }

  public List<Customer> listCustomersByLastName(String lastName)
  {
    return list("lastName", lastName);
  }

  public int updateById(Long id, Customer customer)
  {
    return update("firstName = ?1 lastName = ?2 where id = ?3",
      customer.getFirstName(), customer.getLastName(), id);
  }
}
