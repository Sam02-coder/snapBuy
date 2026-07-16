package com.snapBuy.customer.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.snapBuy.customer.dto.request.AddressRequest;
import com.snapBuy.customer.dto.response.AddressResponse;
import com.snapBuy.customer.entity.Address;
import com.snapBuy.customer.mapper.AddressMapper;
import com.snapBuy.customer.repository.AddressRepository;
import com.snapBuy.customer.service.AddressService;
import com.snapBuy.exception.ForbiddenException;
import com.snapBuy.exception.ResourceNotFoundException;
import com.snapBuy.user.User;
import com.snapBuy.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AddressMapper addressMapper;

    @Override
    @Transactional
    public AddressResponse addAddress(Long customerId, AddressRequest request) {
        User user = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        boolean hasNoAddressesYet = addressRepository.findByUserId(customerId).isEmpty();
        boolean makeDefault = request.isDefault() || hasNoAddressesYet;

        if (makeDefault) {
            clearExistingDefault(customerId);
        }

        Address address = Address.builder()
                .user(user)
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .city(request.getCity())
                .state(request.getState())
                .postalCode(request.getPostalCode())
                .country(request.getCountry())
                .phone(request.getPhone())
                .isDefault(makeDefault)
                .build();

        return addressMapper.toResponse(addressRepository.save(address));
    }

    @Override
    @Transactional
    public AddressResponse updateAddress(Long customerId, Long addressId, AddressRequest request) {
        Address address = findOwnedAddress(customerId, addressId);

        if (request.isDefault() && !address.isDefault()) {
            clearExistingDefault(customerId);
        }

        address.setAddressLine1(request.getAddressLine1());
        address.setAddressLine2(request.getAddressLine2());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPostalCode(request.getPostalCode());
        address.setCountry(request.getCountry());
        address.setPhone(request.getPhone());
        address.setDefault(request.isDefault() || address.isDefault());

        return addressMapper.toResponse(addressRepository.save(address));
    }

    @Override
    @Transactional
    public void deleteAddress(Long customerId, Long addressId) {
        Address address = findOwnedAddress(customerId, addressId);
        boolean wasDefault = address.isDefault();
        addressRepository.delete(address);

        // Promote another address to default so checkout never has zero default addresses
        // as long as at least one address remains.
        if (wasDefault) {
            addressRepository.findByUserId(customerId).stream().findFirst().ifPresent(next -> {
                next.setDefault(true);
                addressRepository.save(next);
            });
        }
    }

    @Override
    public List<AddressResponse> listAddresses(Long customerId) {
        return addressRepository.findByUserId(customerId).stream().map(addressMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public AddressResponse setDefault(Long customerId, Long addressId) {
        Address address = findOwnedAddress(customerId, addressId);
        clearExistingDefault(customerId);
        address.setDefault(true);
        return addressMapper.toResponse(addressRepository.save(address));
    }

    private void clearExistingDefault(Long customerId) {
        addressRepository.findByUserIdAndIsDefaultTrue(customerId)
                .forEach(existing -> {
                    existing.setDefault(false);
                    addressRepository.save(existing);
                });
    }

    private Address findOwnedAddress(Long customerId, Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        if (!address.getUser().getId().equals(customerId)) {
            throw new ForbiddenException("You do not have access to this address");
        }
        return address;
    }
}