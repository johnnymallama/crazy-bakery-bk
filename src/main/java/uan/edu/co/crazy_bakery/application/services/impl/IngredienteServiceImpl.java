package uan.edu.co.crazy_bakery.application.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearIngredienteDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.IngredienteDTO;
i

import uan.edu.co.crazy_bakery.application.services.IngredienteService;
import uan.edu.co.crazy_bakery.infrastructure.repositories.IngredienteRepository;

@Service
public class IngredienteServiceImpl implements IngredienteService{

    private final IngredienteRepository in

    @Override
    public List<IngredienteDTO> getAllIngredientes() {
        return null;
    }

    @Override
    public IngredienteDTO getIngredienteById(String id) {
        return null;
    }

    @Override
    public IngredienteDTO createIngrediente(CrearIngredienteDTO crearIngredienteDTO) {
        return null;
    }

    @Override
    public IngredienteDTO updateIngrediente(String id, CrearIngredienteDTO crearIngredienteDTO) {
        return null;   
    }

    @Override
    public void deleteIngrediente(String id) {

    }

}
