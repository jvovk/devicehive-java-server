package com.devicehive.resource;

import com.devicehive.configuration.Constants;
import com.devicehive.json.strategies.JsonPolicyApply;
import com.devicehive.json.strategies.JsonPolicyDef;
import com.devicehive.model.DeviceCommand;
import com.devicehive.model.wrappers.DeviceCommandWrapper;
import io.swagger.annotations.*;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST controller for device commands: <i>/device/{deviceGuid}/command</i>. See <a
 * href="http://www.devicehive.com/restful#Reference/DeviceCommand">DeviceHive RESTful API: DeviceCommand</a> for
 * details.
 */
@Path("/device")
@Api(tags = {"DeviceCommand"}, consumes = "application/json")
public interface DeviceCommandResource {

    /**
     * Implementation of <a href="http://www.devicehive.com/restful#Reference/DeviceCommand/poll">DeviceHive RESTful
     * API: DeviceCommand: poll</a>
     *
     * @param deviceGuid Device unique identifier.
     * @param timestamp  Timestamp of the last received command (UTC). If not specified, the server's timestamp is taken
     *                   instead.
     * @param timeout    Waiting timeout in seconds (default: 30 seconds, maximum: 60 seconds). Specify 0 to disable
     *                   waiting.
     */
    @GET
    @Path("/{deviceGuid}/command/poll")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN', 'KEY') and hasPermission(null, 'GET_DEVICE_COMMAND')")
    @ApiOperation(value = "Polls the server to get commands.",
            notes = "This method returns all device commands that were created after specified timestamp.\n" +
                    "In the case when no commands were found, the method blocks until new command is received. If no commands are received within the waitTimeout period, the server returns an empty response. In this case, to continue polling, the client should repeat the call with the same timestamp value.",
            response = DeviceCommand.class,
            responseContainer = "List")
    void poll(
            @ApiParam(name = "deviceGuid", value = "Device GUID", required = true)
            @PathParam("deviceGuid")
            String deviceGuid,
            @ApiParam(name = "names", value = "Command names")
            @QueryParam("names")
            String namesString,
            @ApiParam(name = "timestamp", value = "Timestamp to start from")
            @QueryParam("timestamp")
            String timestamp,
            @ApiParam(name = "waitTimeout", value = "Wait timeout in seconds", defaultValue = Constants.DEFAULT_WAIT_TIMEOUT)
            @DefaultValue(Constants.DEFAULT_WAIT_TIMEOUT)
            @Min(0) @Max(Constants.MAX_WAIT_TIMEOUT)
            @QueryParam("waitTimeout")
            long timeout,
            @Suspended AsyncResponse asyncResponse);

    @GET
    @Path("/command/poll")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN', 'KEY') and hasPermission(null, 'GET_DEVICE_COMMAND')")
    @ApiOperation(value = "Polls the server to get commands.",
            notes = "This method returns all device commands that were created after specified timestamp.\n" +
                    "In the case when no commands were found, the method blocks until new command is received. If no commands are received within the waitTimeout period, the server returns an empty response. In this case, to continue polling, the client should repeat the call with the same timestamp value.",
            response = DeviceCommand.class,
            responseContainer = "List")
    void pollMany(
            @ApiParam(name = "deviceGuids", value = "List of device GUIDs")
            @QueryParam("deviceGuids")
            String deviceGuidsString,
            @ApiParam(name = "names", value = "Command names")
            @QueryParam("names")
            String namesString,
            @ApiParam(name = "timestamp", value = "Timestamp to start from")
            @QueryParam("timestamp")
            String timestamp,
            @ApiParam(name = "waitTimeout", value = "Wait timeout in seconds", defaultValue = Constants.DEFAULT_WAIT_TIMEOUT)
            @DefaultValue(Constants.DEFAULT_WAIT_TIMEOUT)
            @Min(0) @Max(Constants.MAX_WAIT_TIMEOUT)
            @QueryParam("waitTimeout")
            long timeout,
            @Suspended AsyncResponse asyncResponse);

    @GET
    @Path("/{deviceGuid}/command/{commandId}/poll")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN', 'KEY') and hasPermission(null, 'GET_DEVICE_COMMAND')")
    @ApiOperation(value = "Waits for a command to be processed.",
            notes = "Waits for a command to be processed.<br>" +
                    "<br>" +
                    "This method returns a command only if it has been processed by a device.<br>" +
                    "<br>" +
                    "In the case when command is not processed, the method blocks until device acknowledges command execution. If the command is not processed within the waitTimeout period, the server returns an empty response. In this case, to continue polling, the client should repeat the call.",
            response = DeviceCommand.class)
    @ApiResponses({
            @ApiResponse(code = 204, message = "Command was not processed during waitTimeout."),
            @ApiResponse(code = 400, message = "Command with commandId was not sent for device with deviceGuid or wrong input parameters."),
            @ApiResponse(code = 404, message = "Device or command was not found.")
    })
    void wait(
            @ApiParam(name = "deviceGuid", value = "Device GUID", required = true)
            @PathParam("deviceGuid")
            String deviceGuid,
            @ApiParam(name = "commandId", value = "Command Id", required = true)
            @PathParam("commandId")
            String commandId,
            @ApiParam(name = "waitTimeout", value = "Wait timeout in seconds (default: 30 seconds, maximum: 60 seconds). Specify 0 to disable waiting.", defaultValue = Constants.DEFAULT_WAIT_TIMEOUT)
            @DefaultValue(Constants.DEFAULT_WAIT_TIMEOUT)
            @Min(0) @Max(Constants.MAX_WAIT_TIMEOUT)
            @QueryParam("waitTimeout")
            long timeout,
            @Suspended AsyncResponse asyncResponse);

    @GET
    @Path("/{deviceGuid}/command")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN', 'KEY') and hasPermission(null, 'GET_DEVICE_COMMAND')")
    @ApiOperation(value = "Query commands.",
            notes = "Gets list of commands that has been received in specified time range.",
            response = DeviceCommand.class,
            responseContainer = "List")
    Response query(
            @ApiParam(name = "deviceGuid", value = "Device GUID", required = true)
            @PathParam("deviceGuid")
            String guid,
            @ApiParam(name = "start", value = "Start timestamp")
            @QueryParam("start")
            String startTs,
            @ApiParam(name = "end", value = "End timestamp")
            @QueryParam("end")
            String endTs,
            @ApiParam(name = "command", value = "Command name")
            @QueryParam("command")
            String command,
            @ApiParam(name = "status", value = "Command status")
            @QueryParam("status")
            String status,
            @ApiParam(name = "sortField", value = "Sort field")
            @QueryParam("sortField")
            @DefaultValue("timestamp")
            String sortField,
            @ApiParam(name = "sortOrder", value = "Sort order")
            @QueryParam("sortOrder")
            String sortOrderSt,
            @ApiParam(name = "take", value = "Limit param")
            @QueryParam("take")
            @DefaultValue(Constants.DEFAULT_TAKE_STR)
            Integer take,
            @ApiParam(name = "skip", value = "Skip param")
            @QueryParam("skip")
            Integer skip,
            @ApiParam(name = "gridInterval", value = "Grid interval")
            @QueryParam("gridInterval")
            Integer gridInterval);

    /**
     * Response contains following output: <p/> <code> { "id":    1 "timestamp":     "1970-01-01 00:00:00.0" "userId": 1
     * "command":   "command_name" "parameters":    {/ * JSON Object * /} "lifetime":  100 "flags":     1 "status":
     * "comand_status" "result":    { / * JSON Object* /} } </code>
     *
     * @param guid      String with Device GUID like "550e8400-e29b-41d4-a716-446655440000"
     * @param commandId command id
     */
    @GET
    @Path("/{deviceGuid}/command/{commandId}")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN', 'KEY') and hasPermission(null, 'GET_DEVICE_COMMAND')")
    @ApiOperation(value = "Get command ",
            notes = "Gets command by device GUID and command id",
            response = DeviceCommand.class)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "If device or command not found")
    })
    Response get(
            @ApiParam(name = "deviceGuid", value = "Device GUID", required = true)
            @PathParam("deviceGuid")
            String guid,
            @ApiParam(name = "commandId", value = "Command Id", required = true)
            @PathParam("commandId")
            String commandId);

    /**
     * <b>Creates new device command.</b> <p/> <i>Example request:</i> <code> { "command":   "command name",
     * "parameters":    {/ * Custom Json Object * /}, "lifetime": 0, "flags": 0 } </code> <p> Where, command  is Command
     * name, required parameters   Command parameters, a JSON object with an arbitrary structure. is not required
     * lifetime     Command lifetime, a number of seconds until this command expires. is not required flags    Command
     * flags, and optional value that could be supplied for device or related infrastructure. is not required\ </p> <p>
     * <i>Example response:</i> </p> <code> { "id": 1, "timestamp": "1970-01-01 00:00:00.0", "userId":    1 } </code>
     *
     * @param guid          device guid
     * @param deviceCommand device command resource
     */
    @POST
    @Path("/{deviceGuid}/command")
    @Consumes(MediaType.APPLICATION_JSON)
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN', 'KEY') and hasPermission(null, 'CREATE_DEVICE_COMMAND')")
    @ApiOperation(value = "Creates new device command.",
            notes = "Creates new device command, stores and returns command with generated id.",
            response = DeviceCommand.class)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "If device not found")
    })
    Response insert(
            @ApiParam(name = "deviceGuid", value = "Device GUID", required = true)
            @PathParam("deviceGuid")
            String guid,
            @ApiParam(value = "Command body", required = true, defaultValue = "{}")
            @JsonPolicyApply(JsonPolicyDef.Policy.COMMAND_FROM_CLIENT)
            DeviceCommandWrapper deviceCommand);

    /**
     * Implementation of <a href="http://www.devicehive.com/restful#Reference/DeviceCommand/update">DeviceHive RESTful
     * API: DeviceCommand: update</a> Updates an existing device command.
     *
     * @param guid      Device unique identifier.
     * @param commandId Device command identifier.
     * @param command   In the request body, supply a <a href="http://www.devicehive .com/restful#Reference/DeviceCommand">DeviceCommand</a>
     *                  resource. All fields are not required: flags - Command flags, and optional value that could be
     *                  supplied for device or related infrastructure. status - Command status, as reported by device or
     *                  related infrastructure. result - Command execution result, an optional value that could be
     *                  provided by device.
     * @return If successful, this method returns an empty response body.
     */
    @PUT
    @Path("/{deviceGuid}/command/{commandId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN', 'KEY') and hasPermission(null, 'UPDATE_DEVICE_COMMAND')")
    @ApiOperation(value = "Updates an existing device command.",
            notes = "Updates an existing device command.",
            response = DeviceCommand.class)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "If device or command not found")
    })
    Response update(
            @ApiParam(name = "deviceGuid", value = "Device GUID", required = true)
            @PathParam("deviceGuid")
            String guid,
            @ApiParam(name = "commandId", value = "Command Id", required = true)
            @PathParam("commandId")
            Long commandId,
            @ApiParam(value = "Command body", required = true, defaultValue = "{}")
            @JsonPolicyApply(JsonPolicyDef.Policy.REST_COMMAND_UPDATE_FROM_DEVICE)
            DeviceCommandWrapper command);
}
