package com.br.codegroup.config;

import com.br.codegroup.domain.Member;
import com.br.codegroup.dto.MemberDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setSkipNullEnabled(true)
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);

        // Configuração personalizada para Member -> MemberDTO
        modelMapper.typeMap(Member.class, MemberDTO.class).addMappings(mapper -> {
            mapper.map(Member::getId, MemberDTO::setId);
            mapper.map(Member::getName, MemberDTO::setName);
            mapper.map(Member::getAssignment, MemberDTO::setAssignment);
            mapper.map(Member::getCreatedAt, MemberDTO::setCreatedAt);
            mapper.map(Member::getUpdatedAt, MemberDTO::setUpdatedAt);
        });

        return modelMapper;
    }
}