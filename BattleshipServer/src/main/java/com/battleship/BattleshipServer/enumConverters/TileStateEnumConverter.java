package com.battleship.BattleshipServer.enumConverters;

import com.battleship.BattleshipServer.model.tile.TileStateEnum;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class TileStateEnumConverter implements AttributeConverter<TileStateEnum, String> {

    @Override
    public String convertToDatabaseColumn(TileStateEnum attribute) {
        return attribute.getName();
    }

    @Override
    public TileStateEnum convertToEntityAttribute(String dbData) {
        return TileStateEnum.fromString(dbData);
    }
}
