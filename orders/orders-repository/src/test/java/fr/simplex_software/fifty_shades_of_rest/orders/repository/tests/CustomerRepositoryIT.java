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
  @DataSet(cleanBefore = true)
  @ExpectedDataSet(value = "orders.yml",ignoreCols = "id, CUSTOMER_ID")
  public void testFindAll()
  {
    customerRepository.persist(getCustomer());
    List<Customer> customers = customerRepository.findAll().stream().toList();
    assertThat(customers).isNotNull();
    assertThat(customers).hasSize(1);
  }

  private Customer getCustomer()
  {
    Order order = new Order(1L, "myItem1", new BigDecimal("100.25"), null);
    Customer customer = new Customer( "John", "Doe",
      "john.doe@email.com", "555-1234", List.of(order));
    order.setCustomer(customer);
    return customer;
  }
}
