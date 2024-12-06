package fr.simplex_software.fifty_shades_of_rest.orders.service.mapping.tests.integration;

import com.github.database.rider.cdi.api.*;
import com.github.database.rider.core.api.configuration.*;
import com.github.database.rider.core.api.dataset.*;
import fr.simplex_software.fifty_shades_of_rest.orders.domain.dto.*;
import fr.simplex_software.fifty_shades_of_rest.orders.domain.jpa.*;
import fr.simplex_software.fifty_shades_of_rest.orders.service.*;
import fr.simplex_software.fifty_shades_of_rest.orders.service.exceptions.*;
import io.quarkus.test.junit.*;
import jakarta.inject.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

@QuarkusTest
@DBRider
@DBUnit(schema = "public", caseSensitiveTableNames = true, cacheConnection = false)
@DataSet(value = "orders.yml", cleanAfter = true)
public class CustomerServiceIT
{
  @Inject
  CustomerService customerService;

  @Test
  public void testGetCustomers()
  {
    List<CustomerDTO> customerDTOs = customerService.getCustomers();
    assertThat(customerDTOs).isNotEmpty();
    assertThat(customerDTOs).hasSize(2);
    assertThat(customerDTOs.getFirst().firstName()).isEqualTo("John");
  }

  @Test
  public void testGetCustomer()
  {
    CustomerDTO customerDTO = customerService.getCustomer(1L);
    assertThat(customerDTO).isNotNull();
    assertThat(customerDTO.firstName()).isEqualTo("John");
  }

  @Test
  public void testGetCustomerNotFound()
  {
    assertThatThrownBy(() -> customerService.getCustomer(3L))
      .isInstanceOf(CustomerNotFoundException.class);
  }

  @Test
  public void testGetCustomerByEmail()
  {
    CustomerDTO customerDTOs =
      customerService.getCustomerByEmail("john.doe@email.com");
    assertThat(customerDTOs).isNotNull();
    assertThat(customerDTOs.firstName()).isEqualTo("John");
  }

  @Test
  public void testGetCustomerByEmailNotFound()
  {
    assertThatThrownBy(() -> customerService.getCustomerByEmail("johnny.doe@email.com"))
      .isInstanceOf(CustomerNotFoundException.class);
  }

  @Test
  @DataSet(value = "orders2.yml", cleanAfter = true)
  public void testCreateCustomer()
  {
    CustomerDTO customerDTO = new CustomerDTO("Mike", "Doe",
      "mike.doe@email.com", "222-3456");
    CustomerDTO createdCustomerDTO = customerService.createCustomer(customerDTO);
    assertThat(createdCustomerDTO).isNotNull();
    assertThat(createdCustomerDTO.id()).isNotNull();
    assertThat(createdCustomerDTO.firstName()).isEqualTo("Mike");
    assertThat(createdCustomerDTO.lastName()).isEqualTo("Doe");
    assertThat(createdCustomerDTO.phone()).isEqualTo("222-3456");
  }

  @Test
  public void testUpdateCustomer()
  {
    CustomerDTO customerDTO =
      customerService.getCustomerByEmail("john.doe@email.com");
    customerDTO = new CustomerDTO(customerDTO.id(), "Mike", "Doe",
      "mike.doe@email.com", "222-3456");
    CustomerDTO updatedCustomerDTO = customerService.updateCustomer(customerDTO);
    assertThat(updatedCustomerDTO).isNotNull();
    assertThat(updatedCustomerDTO.id()).isNotNull();
    assertThat(updatedCustomerDTO.firstName()).isEqualTo("Mike");
   }

   @Test
   public void testUpdateCustomerNotFound()
   {
     CustomerDTO customerDTO = new CustomerDTO(3L, "Mike", "Doe",
       "XXXXXXXXXXXXXXXXXX", "222-3456");
     assertThatThrownBy(() -> customerService.updateCustomer(customerDTO))
       .isInstanceOf(CustomerNotFoundException.class);
   }

   @Test
   public void testDeleteCustomer()
   {
     CustomerDTO customerDTO = customerService.getCustomer(1L);
     customerService.deleteCustomer(customerDTO.id());
     assertThatThrownBy(() -> customerService.getCustomer(1L))
       .isInstanceOf(CustomerNotFoundException.class);
   }

   @Test
   public void testFindCustomerById()
   {
     Customer customer = customerService.findCustomerById(1L);
     assertThat(customer).isNotNull();
     assertThat(customer.getFirstName()).isEqualTo("John");
   }
}
