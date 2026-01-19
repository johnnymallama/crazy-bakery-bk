package uan.edu.co.crazy_bakery.application.services.impl;

import org.springframework.stereotype.Service;
import uan.edu.co.crazy_bakery.application.services.TipoIngredienteService;
import uan.edu.co.crazy_bakery.domain.enums.TipoIngrediente;

import java.util.Arrays;
import java.util.List;

@Service
public class TipoIngredienteServiceImpl implements TipoIngredienteService {

    @Override
    public List<TipoIngrediente> getAllTiposIngrediente() {
        return Arrays.asList(TipoIngrediente.values());
    }
}
