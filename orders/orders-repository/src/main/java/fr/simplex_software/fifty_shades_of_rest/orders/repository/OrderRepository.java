package fr.simplex_software.fifty_shades_of_rest.orders.repository;

import fr.simplex_software.fifty_shades_of_rest.orders.domain.jpa.*;
import io.quarkus.hibernate.orm.panache.*;
import jakarta.enterprise.context.*;

@ApplicationScoped
public class OrderRepository implements PanacheRepository<Order>{}
