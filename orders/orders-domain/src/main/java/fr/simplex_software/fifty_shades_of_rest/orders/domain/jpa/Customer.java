package fr.simplex_software.fifty_shades_of_rest.orders.domain.jpa;

import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import org.hibernate.annotations.*;

import java.util.*;
import java.util.ArrayList;

@Entity
@Table(name = "CUSTOMERS")
public class Customer
{
  @Id
  @SequenceGenerator(name="CUSTOMERS_SEQ_GEN", sequenceName="CUSTOMERS_SEQ", allocationSize=1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="CUSTOMERS_SEQ_GEN")
  @Column(name = "ID", nullable = false, unique = true)
  private Long id;
  @Column(name = "FIRST_NAME", nullable = false, length = 40)
  private String firstName;
  @Column(name = "LAST_NAME", nullable = false, length = 40)
  private String lastName;
  @Column(name = "EMAIL", nullable = false, length = 40, unique = true)
  private String email;
  @Column(name = "PHONE", nullable = false, length = 40, unique = true)
  private String phone;
  @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private List<Order> orders = new ArrayList<>();

  public Customer()
  {
  }

  public Customer(Long id, String firstName, String lastName, String email, String phone)
  {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.phone = phone;
    this.orders = new ArrayList<>();
  }

  public Customer(Long id, String firstName, String lastName, String email, String phone, List<Order> orders)
  {
    this(id, firstName, lastName, email, phone);
    this.orders = orders;
  }

  public Customer(String firstName, String lastName, String email, String phone)
  {
    this(null, firstName, lastName, email, phone);
  }

  public Customer(String firstName, String lastName, String email, String phone, List<Order> orders)
  {
    this(null, firstName, lastName, email, phone);
    this.orders = orders;
  }

  public Long getId()
  {
    return id;
  }

  public void setId(Long id)
  {
    this.id = id;
  }

  public String getFirstName()
  {
    return firstName;
  }

  public void setFirstName(String firstName)
  {
    this.firstName = firstName;
  }

  public String getLastName()
  {
    return lastName;
  }

  public void setLastName(String lastName)
  {
    this.lastName = lastName;
  }

  public String getEmail()
  {
    return email;
  }

  public void setEmail(String email)
  {
    this.email = email;
  }

  public String getPhone()
  {
    return phone;
  }

  public void setPhone(String phone)
  {
    this.phone = phone;
  }

  public List<Order> getOrders()
  {
    return orders;
  }

  public void setOrders(List<Order> orders)
  {
    this.orders = orders;
  }

  public void addOrder(Order order)
  {
    orders.add(order);
    order.setCustomer(this);
  }
}
