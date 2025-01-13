package dev.akorovai.backend.address_info;

import dev.akorovai.backend.address_info.response.AddressInfoResponse;
import dev.akorovai.backend.security.JwtService;
import dev.akorovai.backend.user.User;
import dev.akorovai.backend.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressInfoService {

    private final AddressInfoRepository addressInfoRepository;
    private final JwtService jwtService;
    private final AddressInfoMapper addressInfoMapper;


    public List<AddressInfoResponse> getAddressesForAuthenticatedUser() {
        User authenticatedUser = jwtService.getAuthenticatedUser();
        return addressInfoRepository.findByUser(authenticatedUser).stream()
                .map(addressInfoMapper::toResponse)
                .collect(Collectors.toList());
    }


    @Transactional
    public AddressInfoResponse addAddressForAuthenticatedUser(AddressInfoResponse addressInfoResponse) {
        User authenticatedUser = jwtService.getAuthenticatedUser();


        AddressInfo addressInfo = addressInfoMapper.toEntity(addressInfoResponse);
        addressInfo.setUser(authenticatedUser);


        AddressInfo savedAddressInfo = addressInfoRepository.save(addressInfo);

        return addressInfoMapper.toResponse(savedAddressInfo);
    }


    @Transactional
    public AddressInfoResponse editAddressForAuthenticatedUser(Long addressId, AddressInfoResponse addressInfoResponse) {
        User authenticatedUser = jwtService.getAuthenticatedUser();


        AddressInfo existingAddress = addressInfoRepository.findByIdAndUser(addressId, authenticatedUser)
                .orElseThrow(() -> new RuntimeException("Address not found or you do not have permission to edit it"));


        addressInfoMapper.updateEntityFromDTO(addressInfoResponse, existingAddress);


        AddressInfo updatedAddress = addressInfoRepository.save(existingAddress);


        return addressInfoMapper.toResponse(updatedAddress);
    }


    @Transactional
    public void deleteAddressForAuthenticatedUser(Long addressId) {
        User authenticatedUser = jwtService.getAuthenticatedUser();


        AddressInfo addressInfo = addressInfoRepository.findByIdAndUser(addressId, authenticatedUser)
                .orElseThrow(() -> new RuntimeException("Address not found or you do not have permission to delete it"));


        addressInfoRepository.delete(addressInfo);
    }
}