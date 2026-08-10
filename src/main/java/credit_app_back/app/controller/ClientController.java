package credit_app_back.app.controller;

import credit_app_back.app.dto.ClientDto;
import credit_app_back.app.dto.FindClientsDto;
import credit_app_back.app.dto.PageResponseDto;
import credit_app_back.app.entity.Client;
import credit_app_back.app.mapper.ClientMapper;
import credit_app_back.app.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    private final ClientMapper clientMapper;

    @GetMapping
    public ResponseEntity<PageResponseDto<ClientDto>> getAllClients(
            @RequestParam(defaultValue = "0") int page  
    ) {
        log.debug("GET /api/clients, page: {}", page);
        PageResponseDto<ClientDto> response = clientService.getAllClients(page);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/find")
    public ResponseEntity<PageResponseDto<ClientDto>> findClients(
            @RequestParam(defaultValue = "0") int page,  
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String passport,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String middleName
    ) {
        log.debug("GET /api/clients/find, page: {}, phone: {}, passport: {}, firstName: {}, lastName: {}, middleName: {}",
                page, phone, passport, firstName, lastName, middleName);

        FindClientsDto filters = FindClientsDto.builder()
                .phone(phone)
                .passport(passport)
                .firstName(firstName)
                .lastName(lastName)
                .middleName(middleName)
                .build();

        PageResponseDto<ClientDto> response = clientService.findClients(page, filters);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientDto> getClientById(@PathVariable Long id) {
        log.debug("GET /api/clients/{}", id);
        Client client = clientService.getClientById(id);
        return ResponseEntity.ok(clientMapper.toDto(client));
    }

    @PostMapping
    public ResponseEntity<ClientDto> createClient(@Valid @RequestBody ClientDto dto) {
        log.debug("POST /api/clients, dto: {}", dto);
        Client client = clientService.createClient(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(clientMapper.toDto(client));
    }
}