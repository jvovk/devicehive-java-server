package com.devicehive.model.updates;


import com.devicehive.json.strategies.JsonPolicyDef;
import com.devicehive.model.DeviceCommand;
import com.devicehive.model.HiveEntity;
import com.devicehive.model.JsonStringWrapper;
import com.devicehive.model.NullableWrapper;

import java.sql.Timestamp;

import static com.devicehive.json.strategies.JsonPolicyDef.Policy.COMMAND_UPDATE_FROM_DEVICE;

public class DeviceCommandUpdate implements HiveEntity {
    @JsonPolicyDef(COMMAND_UPDATE_FROM_DEVICE)
    private Long id;

    @JsonPolicyDef(COMMAND_UPDATE_FROM_DEVICE)
    private NullableWrapper<Timestamp> timestamp;

    @JsonPolicyDef(COMMAND_UPDATE_FROM_DEVICE)
    private NullableWrapper<String> command;

    @JsonPolicyDef(COMMAND_UPDATE_FROM_DEVICE)
    private NullableWrapper<JsonStringWrapper> parameters;

    @JsonPolicyDef(COMMAND_UPDATE_FROM_DEVICE)
    private NullableWrapper<Integer> lifetime;

    @JsonPolicyDef(COMMAND_UPDATE_FROM_DEVICE)
    private NullableWrapper<Integer> flags;

    @JsonPolicyDef(COMMAND_UPDATE_FROM_DEVICE)
    private NullableWrapper<JsonStringWrapper> result;

    @JsonPolicyDef(COMMAND_UPDATE_FROM_DEVICE)
    private NullableWrapper<String> status;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NullableWrapper<Timestamp> getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(NullableWrapper<Timestamp> timestamp) {
        this.timestamp = timestamp;
    }

    public NullableWrapper<String> getCommand() {
        return command;
    }

    public void setCommand(NullableWrapper<String> command) {
        this.command = command;
    }

    public NullableWrapper<JsonStringWrapper> getParameters() {
        return parameters;
    }

    public void setParameters(NullableWrapper<JsonStringWrapper> parameters) {
        this.parameters = parameters;
    }

    public NullableWrapper<Integer> getLifetime() {
        return lifetime;
    }

    public void setLifetime(NullableWrapper<Integer> lifetime) {
        this.lifetime = lifetime;
    }

    public NullableWrapper<Integer> getFlags() {
        return flags;
    }

    public void setFlags(NullableWrapper<Integer> flags) {
        this.flags = flags;
    }

    public NullableWrapper<JsonStringWrapper> getResult() {
        return result;
    }

    public void setResult(NullableWrapper<JsonStringWrapper> result) {
        this.result = result;
    }

    public NullableWrapper<String> getStatus() {
        return status;
    }

    public void setStatus(NullableWrapper<String> status) {
        this.status = status;
    }

    public DeviceCommand convertTo() {
        DeviceCommand deviceCommand = new DeviceCommand();
        deviceCommand.setId(id);
        if (timestamp != null) {
            deviceCommand.setTimestamp(timestamp.getValue());
        }
        if (command != null) {
            deviceCommand.setCommand(command.getValue());
        }
        if (parameters != null) {
            deviceCommand.setParameters(parameters.getValue());
        }
        if (lifetime != null) {
            deviceCommand.setLifetime(lifetime.getValue());
        }
        if (flags != null) {
            deviceCommand.setFlags(flags.getValue());
        }
        if (status != null) {
            deviceCommand.setStatus(status.getValue());
        }
        if (result != null) {
            deviceCommand.setResult(result.getValue());
        }
        return deviceCommand;
    }
}