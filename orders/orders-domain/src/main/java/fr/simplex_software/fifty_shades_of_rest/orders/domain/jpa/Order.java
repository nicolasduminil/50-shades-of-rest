package fr.simplex_software.fifty_shades_of_rest.orders.domain.jpa;

import jakarta.persistence.*;

import java.math.*;

@Entity
@Table(name = "ORDERS")
public class Order
{
  @Id
  @SequenceGenerator(name="ORDERS_SEQ_GEN", sequenceName="ORDERS_SEQ", allocationSize=1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="ORDERS_SEQ_GEN")
  @Column(name = "ID", nullable = false, unique = true)
  private Long id;
  @Column(name = "ITEM", nullable = false, length = 40)
  private String item;
  @Column(name = "PRICE", nullable = false)
  private BigDecimal price;
  @ManyToOne
  @JoinColumn(name = "CUSTOMER_ID", nullable = false)
  private Customer customer;

  public Order()
  {
  }

  public Order(String item, BigDecimal price)
  {
    this.item = item;
    this.price = price;
  }

  public Order(String item, BigDecimal price, Customer customer)
  {
    this(item, price);
    this.customer = customer;
  }

  public Order(Long id, String item, BigDecimal price, Customer customer)
  {
    this (item, price, customer);
    this.id = id;
  }

  public Long getId()
  {
    return id;
  }

  public void setId(Long id)
  {
    this.id = id;
  }

  public String getItem()
  {
    return item;
  }

  public void setItem(String item)
  {
    this.item = item;
  }

  public BigDecimal getPrice()
  {
    return price;
  }

  public void setPrice(BigDecimal price)
  {
    this.price = price;
  }

  public Customer getCustomer()
  {
    return customer;
  }

  public void setCustomer(Customer customer)
  {
    this.customer = customer;
    if (customer != null && !customer.getOrders().contains(this))
      customer.addOrder(this);
  }
}
