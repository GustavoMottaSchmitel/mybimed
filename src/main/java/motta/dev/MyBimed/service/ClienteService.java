package motta.dev.MyBimed.service;

import lombok.RequiredArgsConstructor;
import motta.dev.MyBimed.exception.ResourceAlreadyExistsException;
import motta.dev.MyBimed.exception.ResourceNotFoundException;
import motta.dev.MyBimed.model.ClienteModel;
import motta.dev.MyBimed.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteModel criarCliente(ClienteModel clienteModel) {
        if (clienteRepository.existsByEmail(clienteModel.getEmail())) {
            throw new ResourceAlreadyExistsException("Este email já esta cadastrado");
        }

        return clienteRepository.save(clienteModel);
    }

    public List<ClienteModel> getAllClientes() {
        return clienteRepository.findAll();
    }

    public ClienteModel getClienteById(UUID id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));
    }

    public ClienteModel updateCliente(UUID id, ClienteModel updateCliente) {
        return clienteRepository.findById(id)
                .map(cliente -> {
                    cliente.setNome(updateCliente.getNome());
                    cliente.setEspecialidade(updateCliente.getEspecialidade());
                    cliente.setTelefone(updateCliente.getTelefone());
                    cliente.setWhatsapp(updateCliente.getWhatsapp());

                    return clienteRepository.save(cliente);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));
    }

    public void deleteCliente(UUID id) {
        if (!clienteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cliente não encontrado");
        }
        clienteRepository.deleteById(id);
    }
}
