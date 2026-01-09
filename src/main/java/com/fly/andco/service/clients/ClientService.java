package com.fly.andco.service.clients;

import com.fly.andco.model.clients.Client;
import com.fly.andco.repository.clients.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    public List<Client> getAll() {
        return clientRepository.findAll();
    }

    public Optional<Client> getById(Long id) {
        return clientRepository.findById(id);
    }

    public Optional<Client> getByEmail(String email) {
        return clientRepository.findByEmail(email);
    }

    public Client save(Client client) {
        return clientRepository.save(client);
    }

    public void delete(Long id) {
        clientRepository.deleteById(id);
    }
    
    /**
     * Trouve ou crée un client par email
     */
    public Client findOrCreate(String email, String nom, String prenom, String telephone) {
        Optional<Client> existing = clientRepository.findByEmail(email);
        if (existing.isPresent()) {
            return existing.get();
        }
        Client client = new Client(nom, prenom, email, telephone);
        return clientRepository.save(client);
    }
}
