package com.procurement.orchestrator.domain.dto.commnds;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.procurement.orchestrator.exception.EnumException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum ContractingCommandType {

    CREATE_CAN("createCAN"),
    CREATE_AC("createAC");

    private static final Map<String, ContractingCommandType> CONSTANTS = new HashMap<>();
    private final String value;

    static {
        for (final ContractingCommandType c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    ContractingCommandType(final String value) {
        this.value = value;
    }

    @JsonCreator
    public static ContractingCommandType fromValue(final String value) {
        final ContractingCommandType constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new EnumException(ContractingCommandType.class.getName(), value, Arrays.toString(values()));
        }
        return constant;
    }

    @Override
    public String toString() {
        return this.value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }
}
