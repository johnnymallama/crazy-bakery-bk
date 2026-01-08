package uan.edu.co.crazy_bakery.application.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearIngredienteTamanoDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.IngredienteTamanoDTO;
import uan.edu.co.crazy_bakery.application.mappers.IngredienteTamanoMapper;
import uan.edu.co.crazy_bakery.domain.model.IngredienteTamano;
import uan.edu.co.crazy_bakery.domain.model.Tamano;
import uan.edu.co.crazy_bakery.infrastructure.repositories.IngredienteTamanoRepository;
import uan.edu.co.crazy_bakery.infrastructure.repositories.TamanoRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IngredienteTamanoServiceImpl implements IngredienteTamanoService {

    private final IngredienteTamanoRepository ingredienteTamanoRepository;
    private final TamanoRepository tamanoRepository;
    private final IngredienteTamanoMapper ingredienteTamanoMapper = IngredienteTamanoMapper.INSTANCE;

    @Override
    public List<IngredienteTamanoDTO> consultarPorTamano(Long tamanoId) {
        return ingredienteTamanoRepository.findByTamanoIdAndEstadoTrue(tamanoId)
                .stream()
                .map(ingredienteTamanoMapper::ingredienteTamanoToIngredienteTamanoDTO)
                .collect(Collectors.toList());
    }

    @Override
    public IngredienteTamanoDTO crearRelacion(CrearIngredienteTamanoDTO crearIngredienteTamanoDTO) {
        Tamano tamano = tamanoRepository.findByIdAndEstadoTrue(crearIngredienteTamanoDTO.getTamanoId())
                .orElseThrow(() -> new RuntimeException("Tamaño no encontrado"));

        IngredienteTamano ingredienteTamano = new IngredienteTamano();
        ingredienteTamano.setTamano(tamano);
        ingredienteTamano.setTipoIngrediente(crearIngredienteTamanoDTO.getTipoIngrediente());
        ingredienteTamano.setGramos(crearIngredienteTamanoDTO.getGramos());

        IngredienteTamano saved = ingredienteTamanoRepository.save(ingredienteTamano);

        return ingredienteTamanoMapper.ingredienteTamanoToIngredienteTamanoDTO(saved);
    }

    @Override
    public boolean inactivarRelacion(Long id) {
        return ingredienteTamanoRepository.findById(id)
                .map(ingredienteTamano -> {
                    ingredienteTamano.setEstado(false);
                    ingredienteTamanoRepository.save(ingredienteTamano);
                    return true;
                })
                .orElse(false);
    }
}
