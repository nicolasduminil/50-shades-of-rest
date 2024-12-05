package fr.simplex_software.fifty_shades_of_rest.orders.service.mapping.tests.integration;

import com.github.database.rider.cdi.api.*;
import com.github.database.rider.core.api.configuration.*;
import com.github.database.rider.core.api.dataset.*;
import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import fr.simplex_software.fifty_shades_of_rest.orders.domain.jpa.*;
import fr.simplex_software.fifty_shades_of_rest.orders.service.*;
import fr.simplex_software.fifty_shades_of_rest.orders.service.mapping.*;
import io.quarkus.test.junit.*;
import jakarta.inject.*;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;

@QuarkusTest
@DBRider
@DBUnit(schema = "public", caseSensitiveTableNames = true, cacheConnection = false)
@DataSet(value = "orders.yml", cleanAfter = true)
public class CustomerMapperIT
{
  @Inject
  CustomerService customerService;

  @Test
  public void testToEntity()
  {
    CustomerDTO customerDTO = customerService.getCustomer(1L);
    Customer customer = CustomerMapper.INSTANCE.toEntity(customerDTO);
    assertThat(customer).isNotNull();
    assertThat(customer.getLastName()).isEqualTo(customerDTO.lastName());
    assertThat(customer.getId()).isEqualTo(customerDTO.id());
  }
}
