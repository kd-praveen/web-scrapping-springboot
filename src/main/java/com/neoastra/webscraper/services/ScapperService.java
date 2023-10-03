package com.neoastra.webscraper.services;

import java.util.Set;

import com.neoastra.webscraper.dto.ResponseDTO;

public interface ScapperService {

    Set<ResponseDTO> getVehicleByModel(String vehicleModel);
    
}
