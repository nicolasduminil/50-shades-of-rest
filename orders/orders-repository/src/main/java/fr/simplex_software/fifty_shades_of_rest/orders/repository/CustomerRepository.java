package fr.simplex_software.fifty_shades_of_rest.orders.repository;

import io.quarkus.hibernate.orm.panache.*;
import jakarta.enterprise.context.*;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.jpa.*;

@ApplicationScoped
public class CustomerRepository implements PanacheRepository<Customer>
{
  public Customer save(Customer customer)
  {
    persist(customer);
    return customer;
  }

  public int updateById(Long id, Customer customer)
  {
    return update("firstName = ?1 lastName = ?2 where id = ?3",
      customer.getFirstName(), customer.getLastName(), id);
  }
}
