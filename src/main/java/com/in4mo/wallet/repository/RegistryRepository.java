package com.in4mo.wallet.repository;

import com.in4mo.wallet.model.Registry;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

@Component
public interface RegistryRepository extends CrudRepository<Registry, Integer> {
}
