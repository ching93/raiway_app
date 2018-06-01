/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kiri.mavenproject1.entities;

import java.time.Duration;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 *
 * @author User
 */
@Converter(autoApply=true)
public class DurationAttributeConverter implements AttributeConverter<Duration, Long> {
    @Override
    public Long convertToDatabaseColumn(Duration attribute) {
        return attribute.toMinutes();
    }
    @Override
    public Duration convertToEntityAttribute(Long dbData) {
        return Duration.ofMinutes(dbData);
    }
}
