package cn.leancoding.cotrip.model.plan;


import cn.leancoding.cotrip.service.TimeSpanDTO;
import org.modelmapper.ModelMapper;

public class TimeSpanConverter {
    static ModelMapper modelMapper = new ModelMapper();
    public static TimeSpanDTO toDTO(TimeSpan entity) {
        return modelMapper.map(entity, TimeSpanDTO.class);
    }
    public static TimeSpan toEntity(TimeSpanDTO dto) {
        return modelMapper.map(dto, TimeSpan.class);
    }
}
