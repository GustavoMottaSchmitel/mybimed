package motta.dev.MyBimed.service;

import lombok.RequiredArgsConstructor;
import motta.dev.MyBimed.exception.ResourceNotFoundException;
import motta.dev.MyBimed.model.AgendaModel;
import motta.dev.MyBimed.repository.AgendaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AgendaService {

    private final AgendaRepository agendaRepository;

    public List<AgendaModel> getALlAgendas() {
        return agendaRepository.findAll();
    }

    public AgendaModel getAgendaById(String id) {
        return agendaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agenda não encontrada"));
    }
}
