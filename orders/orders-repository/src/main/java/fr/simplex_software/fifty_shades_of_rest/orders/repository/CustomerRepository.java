package fr.simplex_software.fifty_shades_of_rest.orders.repository;

import io.quarkus.hibernate.orm.panache.*;
import jakarta.enterprise.context.*;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.jpa.*;

@ApplicationScoped
public class CustomerRepository implements PanacheRepository<Customer> {}
