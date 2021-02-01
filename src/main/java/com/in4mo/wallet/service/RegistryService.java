package com.in4mo.wallet.service;

import com.in4mo.wallet.exception.RegistryNotFoundException;
import com.in4mo.wallet.model.Registry;
import com.in4mo.wallet.model.request.RechargeRequest;
import com.in4mo.wallet.model.response.RegistryResponse;
import com.in4mo.wallet.repository.RegistryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class RegistryService {

    private final RegistryRepository registryRepository;

    public RegistryService(RegistryRepository registryRepository) {
        this.registryRepository = registryRepository;
    }

    public List<RegistryResponse> findByUserId(String userId) {
        return registryRepository
                .findByUserId(userId)
                .stream()
                .map(RegistryResponse::new)
                .collect(Collectors.toUnmodifiableList());
    }

    @Transactional
    public void recharge(String userId, String registryId, RechargeRequest rechargeRequest) {
        Registry registry = registryRepository.findByIdAndUserId(registryId, userId);

        if (Objects.isNull(registry)) {
            throw new RegistryNotFoundException("Registry 'not_existing' not found for user: '1'");
        }

        registry.setAmount(registry.getAmount() + rechargeRequest.getAmount());
    }
}
