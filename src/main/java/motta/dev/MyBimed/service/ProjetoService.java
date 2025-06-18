package motta.dev.MyBimed.service;

import lombok.RequiredArgsConstructor;
import motta.dev.MyBimed.exception.ResourceAlreadyExistsException;
import motta.dev.MyBimed.exception.ResourceIsEmptyException;
import motta.dev.MyBimed.exception.ResourceNotFoundException;
import motta.dev.MyBimed.model.ProjetoModel;
import motta.dev.MyBimed.repository.ProjetoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjetoService {

    private final ProjetoRepository projetoRepository;

    public ProjetoModel createProjeto(ProjetoModel projeto) {
        if (projetoRepository.existsByTituloIgnoreCase(projeto.getTitulo())) {
            throw new ResourceAlreadyExistsException("Já existe um projeto com este título");
        }

        return projetoRepository.save(projeto);
    }

    public List<ProjetoModel> getAllProjetos() {
        List<ProjetoModel> projetos = projetoRepository.findAll();
        if (projetos.isEmpty()) {
            throw new ResourceIsEmptyException("Não existe nenhum projeto criado no momento!");
        }
        return projetos;
    }

    public ProjetoModel getProjetoById(UUID id) {
        return projetoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Este projeto não existe"));
    }

    public ProjetoModel updateProjeto(UUID id, ProjetoModel projetoUpdate) {
        return projetoRepository.findById(id)
                .map(projeto -> {
                    projeto.setTitulo(projetoUpdate.getTitulo());
                    projeto.setDataEntrega(projetoUpdate.getDataEntrega());
                    projeto.setDescricao(projetoUpdate.getDescricao());
                    projeto.setEquipeResponsavel(projetoUpdate.getEquipeResponsavel());
                    projeto.setCliente(projetoUpdate.getCliente());

                    return projetoRepository.save(projeto);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Não encontramos um projeto com este id"));
    }

    public void deleteProjeto(UUID id) {
        if (!projetoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Não encontramos um projeto com este id");
        }
        projetoRepository.deleteById(id);
    }
}