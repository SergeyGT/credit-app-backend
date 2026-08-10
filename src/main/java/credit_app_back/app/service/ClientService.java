package credit_app_back.app.service;

import org.springframework.stereotype.Service;

import credit_app_back.app.mapper.ClientMapper;
import credit_app_back.app.repository.ClientRepository;
import credit_app_back.app.util.ClientValidator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientValidator clientValidator;
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    
}
