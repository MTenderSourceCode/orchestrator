package com.procurement.orchestrator.kafka;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.procurement.orchestrator.databinding.LocalDateTimeDeserializer;
import com.procurement.orchestrator.databinding.LocalDateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
public class Task {

    @JsonProperty("action")
    private ActionType action;

    @JsonProperty("ocid")
    private String ocid;

    @JsonProperty("phase")
    private String phase;

    @JsonProperty("launchTime")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime launchTime;

    @JsonProperty("metaData")
    private String metaData;

    @JsonCreator
    public Task(@JsonProperty("action") final ActionType action,
                @JsonProperty("ocid") final String ocid,
                @JsonProperty("phase") final String phase,
                @JsonProperty("launchTime")
                @JsonDeserialize(using = LocalDateTimeDeserializer.class) final LocalDateTime launchTime,
                @JsonProperty("metaData") final String metaData) {
        this.action = action;
        this.ocid = ocid;
        this.phase = phase;
        this.launchTime = launchTime;
        this.metaData = metaData;
    }

    public String getIdentifier(){
        return this.ocid+this.phase;
    }

    @Override
    public String toString() {
        return "{" +
                "action:" + action +
                ", ocid:'" + ocid + '\'' +
                ", phase:'" + phase + '\'' +
                ", launchTime:" + launchTime +
                ", metaData:'" + metaData + '\'' +
                '}';
    }

    public enum ActionType {

        SCHEDULE("schedule"),
        CANCEL("cancel"),
        REPLACE("replace");

        private static final Map<String, ActionType> CONSTANTS = new HashMap<>();

        static {
            for (final ActionType c : values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private final String value;

        ActionType(final String value) {
            this.value = value;
        }

        @JsonCreator
        public static ActionType fromValue(final String value) {
            final ActionType constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
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
}


