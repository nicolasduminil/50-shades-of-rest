package fr.simplex_software.fifty_shades_of_rest.orders.repository.tests;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.jpa.*;
import fr.simplex_software.fifty_shades_of_rest.orders.repository.*;
import io.quarkus.test.*;
import io.quarkus.test.junit.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

@QuarkusTest
public class TestCustomerRepository
{
  @InjectMock
  CustomerRepository customerRepository;

  @Test
  public void testCustomerRepository()
  {
    assertThat(customerRepository).isNotNull();
    Mockito.when(customerRepository.count()).thenReturn(23L);
    assertThat(customerRepository.count()).isEqualTo(23L);
    Mockito.when(customerRepository.findAll()).thenReturn();
  }

  @Test
  void testCustomerRepositoryFindAll() {
    // Prepare test data
    List<Customer> expectedCustomers = Arrays.asList(
      new Customer("1", "John Doe"),
      new Customer("2", "Jane Smith")
    );

    // Mock the repository behavior
    Mockito.when(customerRepository.findAll()).thenReturn(expectedCustomers);

    // Execute the operation
    List<Customer> actualCustomers = customerRepository.findAll();

    // Verify the results
    assertNotNull(actualCustomers);
    assertEquals(expectedCustomers.size(), actualCustomers.size());
    assertEquals(expectedCustomers, actualCustomers);

    // Verify the method was called
    Mockito.verify(customerRepository).findAll();
  }
}
}
