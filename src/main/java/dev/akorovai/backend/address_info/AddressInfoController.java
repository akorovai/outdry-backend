package dev.akorovai.backend.address_info;

import dev.akorovai.backend.address_info.response.AddressInfoResponse;
import dev.akorovai.backend.security.ResponseRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressInfoController {

    private final AddressInfoService addressInfoService;

    @GetMapping
    public ResponseEntity<ResponseRecord> getAddressesForAuthenticatedUser() {
        List<AddressInfoResponse> addresses = addressInfoService.getAddressesForAuthenticatedUser();


        ResponseRecord response = ResponseRecord.builder().code(HttpStatus.OK.value()).message(addresses).build();

        return ResponseEntity.ok(response);
    }


    @PostMapping
    public ResponseEntity<ResponseRecord> addAddressForAuthenticatedUser(@RequestBody AddressInfoResponse addressInfoResponse) {
        AddressInfoResponse savedAddress = addressInfoService.addAddressForAuthenticatedUser(addressInfoResponse);


        ResponseRecord response = ResponseRecord.builder().code(HttpStatus.OK.value()).message(savedAddress).build();

        return ResponseEntity.ok(response);
    }


    @PutMapping("/{addressId}")
    public ResponseEntity<ResponseRecord> editAddressForAuthenticatedUser(@PathVariable Long addressId, @RequestBody AddressInfoResponse addressInfoResponse) {
        AddressInfoResponse updatedAddress = addressInfoService.editAddressForAuthenticatedUser(addressId, addressInfoResponse);


        ResponseRecord response = ResponseRecord.builder().code(HttpStatus.OK.value()).message(updatedAddress).build();

        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{addressId}")
    public ResponseEntity<ResponseRecord> deleteAddressForAuthenticatedUser(@PathVariable Long addressId) {
        addressInfoService.deleteAddressForAuthenticatedUser(addressId);


        ResponseRecord response = ResponseRecord.builder().code(HttpStatus.NO_CONTENT.value()).message("Address deleted successfully").build();

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
}