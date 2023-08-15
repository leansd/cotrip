package cn.leancoding.cotrip.model.location;


import cn.leancoding.cotrip.service.LocationDTO;
import org.modelmapper.ModelMapper;

public class LocationConverter {
    static ModelMapper modelMapper = new ModelMapper();
    public static LocationDTO toDTO(Location location) {
        return modelMapper.map(location, LocationDTO.class);
    }
    public static Location toEntity(LocationDTO locationDTO) {
        return modelMapper.map(locationDTO, Location.class);
    }
}
