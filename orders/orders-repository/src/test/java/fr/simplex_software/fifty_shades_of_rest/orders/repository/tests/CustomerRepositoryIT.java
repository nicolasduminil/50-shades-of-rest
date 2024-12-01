package fr.simplex_software.fifty_shades_of_rest.orders.repository.tests;

import com.github.database.rider.cdi.api.*;
import com.github.database.rider.core.api.configuration.*;
import com.github.database.rider.core.api.dataset.*;
import fr.simplex_software.fifty_shades_of_rest.orders.domain.jpa.*;
import fr.simplex_software.fifty_shades_of_rest.orders.domain.jpa.Order;
import fr.simplex_software.fifty_shades_of_rest.orders.repository.*;
import io.quarkus.test.junit.*;
import jakarta.inject.*;
import jakarta.transaction.*;
import org.junit.jupiter.api.*;

import java.math.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

@QuarkusTest
@DBRider
@DBUnit(schema = "public", caseSensitiveTableNames = true, cacheConnection = false)
public class CustomerRepositoryIT
{
  @Inject
  CustomerRepository customerRepository;

  @Test
  @Transactional
  @DataSet(value = "orders.yml", cleanBefore = true)
  public void testFindAll()
  {
    List<Customer> customers = customerRepository.findAll().stream().toList();
    assertThat(customers).isNotNull();
    assertThat(customers).hasSize(2);
    //assertThat(customers.getFirst().getOrders()).hasSize(2);
  }

  @Test
  @Transactional
  @DataSet(cleanBefore = true)
  @ExpectedDataSet(value = "expected-orders.yml")
  public void testPersist()
  {
    customerRepository.persist(getCustomer());
  }

  private Customer getCustomer()
  {
    Customer customer = new Customer( "John", "Doe",
      "john.doe@email.com", "555-1234");
    customer.addOrder(new Order( "myItem1", new BigDecimal("100.25"), customer));
    customer.addOrder(new Order( "myItem2", new BigDecimal("200.25"), customer));
    return customer;
  }
}
