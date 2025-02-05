package fr.simplex_software.fifty_shades_of_rest.orders.domain.tests;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.jpa.*;
import fr.simplex_software.fifty_shades_of_rest.orders.domain.jpa.Order;
import io.quarkus.test.junit.*;
import jakarta.inject.*;
import jakarta.persistence.*;
import jakarta.transaction.*;
import org.junit.jupiter.api.*;

import java.math.*;

import static org.assertj.core.api.Assertions.*;

@QuarkusTest
@Transactional
public class JpaHibernateIT
{
  @Inject
  EntityManager em;

  @Test
  public void testCustomer()
  {
    Customer customer = new Customer("John", "Doe",
      "john.doe@email.com", "222-786453");
    em.persist(customer);
    em.flush();
    em.clear();
    Customer found = em.find(Customer.class, customer.getId());
    assertThat(found).isNotNull();
    assertThat(found.getId()).isNotNull();
    assertThat(customer.getFirstName()).isEqualTo(found.getFirstName());
    assertThat(customer.getLastName()).isEqualTo(found.getLastName());
  }

  @Test
  public void testOrder()
  {
    Customer customer = new Customer("Jane", "Doe",
      "jane.doe@email.com", "222-786460");
    Order order = new Order("miItem1", new BigDecimal("210.76"), customer);
    customer.addOrder(order);
    em.persist(customer);
    em.flush();
    em.clear();
    Order found = em.find(Order.class, order.getId());
    assertThat(found).isNotNull();
    assertThat(found.getId()).isNotNull();
    assertThat(found.getItem()).isEqualTo(order.getItem());
    assertThat(found.getPrice()).isEqualTo(order.getPrice());
    assertThat(found.getCustomer()).isNotNull();
    Customer foundCustomer = found.getCustomer();
    assertThat(foundCustomer).isNotNull();
    assertThat(foundCustomer.getId()).isNotNull();
    assertThat(foundCustomer.getFirstName()).isEqualTo(customer.getFirstName());
  }
}
