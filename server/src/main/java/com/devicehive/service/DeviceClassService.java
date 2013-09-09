package com.devicehive.service;

import com.devicehive.dao.DeviceClassDAO;
import com.devicehive.exceptions.HiveException;
import com.devicehive.model.DeviceClass;
import com.devicehive.model.Equipment;
import com.devicehive.model.NullableWrapper;
import com.devicehive.model.updates.DeviceClassUpdate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.validation.constraints.NotNull;
import java.util.*;

import static javax.ws.rs.core.Response.Status.*;

/**
 * @author Nikolay Loboda
 * @since 19.07.13
 */
@Stateless
public class DeviceClassService {

    @EJB
    private DeviceClassDAO deviceClassDAO;
    @EJB
    private EquipmentService equipmentService;

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public DeviceClass get(@NotNull long id) {
        return deviceClassDAO.get(id);
    }

    public boolean delete(@NotNull long id) {
        return deviceClassDAO.delete(id);
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public DeviceClass getWithEquipment(@NotNull long id) {
        return deviceClassDAO.getWithEquipment(id);
    }

    public DeviceClass createOrUpdateDeviceClass(NullableWrapper<DeviceClassUpdate> deviceClass,
                                                 Set<Equipment> newEquipmentSet, boolean useExistingEquipment) {
        DeviceClass stored;
        //use existing
        if (deviceClass == null) {
            return null;
        }
        //check is already done
        DeviceClass deviceClassFromMessage = deviceClass.getValue().convertTo();
        if (deviceClassFromMessage.getId() != null) {
            stored = deviceClassDAO.getDeviceClass(deviceClassFromMessage.getId());
        } else {
            stored = deviceClassDAO.getDeviceClassByNameAndVersion(deviceClassFromMessage.getName(),
                    deviceClassFromMessage.getVersion());
        }
        if (stored != null) {
            //update
            if (!stored.getPermanent()) {
                if (deviceClass.getValue().getData() != null) {
                    stored.setData(deviceClassFromMessage.getData());
                }
                if (deviceClass.getValue().getOfflineTimeout() != null) {
                    stored.setOfflineTimeout(deviceClassFromMessage.getOfflineTimeout());
                }
                if (deviceClass.getValue().getPermanent() != null) {
                    stored.setPermanent(deviceClassFromMessage.getPermanent());
                }
                if (!useExistingEquipment) {
                    replaceEquipment(newEquipmentSet, stored);
                }
            }
            return stored;
        } else {
            //create
            if (deviceClassFromMessage.getId() != null) {
                throw new HiveException("Invalid request");
            }
            deviceClassDAO.createDeviceClass(deviceClassFromMessage);
            if (!useExistingEquipment) {
                replaceEquipment(newEquipmentSet, deviceClassFromMessage);
            }
            return deviceClassFromMessage;
        }
    }

    public DeviceClass addDeviceClass(DeviceClass deviceClass) {
        if (deviceClass.getId() != null) {
            throw new HiveException("Invalid request. Id cannot be specified.", BAD_REQUEST.getStatusCode());
        }
        if (deviceClassDAO.getDeviceClassByNameAndVersion(deviceClass.getName(), deviceClass.getVersion()) != null) {
            throw new HiveException("DeviceClass cannot be created. Device class with such name and version already " +
                    "exists", FORBIDDEN.getStatusCode());
        }
        DeviceClass createdDeviceClass = deviceClassDAO.createDeviceClass(deviceClass);
        if (deviceClass.getEquipment() != null) {
            Set<Equipment> resultEquipment = createEquipment(createdDeviceClass, deviceClass.getEquipment());
            createdDeviceClass.setEquipment(resultEquipment);
        }
        return createdDeviceClass;
    }

    public void update(long id, DeviceClassUpdate update) {
        if (update == null) {
            return;
        }
        DeviceClass stored = deviceClassDAO.getDeviceClass(id);
        if (stored == null) {
            throw new HiveException("device with id : " + id + " does not exists");
        }
        if (update.getData() != null)
            stored.setData(update.getData().getValue());
        if (update.getEquipment() != null) {
            replaceEquipment(update.getEquipment().getValue(), stored);
            stored.setEquipment(update.getEquipment().getValue());
        }
        if (update.getName() != null) {
            stored.setName(update.getName().getValue());
        }
        if (update.getPermanent() != null) {
            stored.setPermanent(update.getPermanent().getValue());
        }
        if (update.getOfflineTimeout() != null) {
            stored.setOfflineTimeout(update.getOfflineTimeout().getValue());
        }
        if (update.getVersion() != null) {
            stored.setVersion(update.getVersion().getValue());
        }
        deviceClassDAO.updateDeviceClass(stored);
    }

//    public void updateEquipment(Set<Equipment> newEquipmentSet, DeviceClass deviceClass) {
//        List<Equipment> existingEquipments = equipmentService.getByDeviceClass(deviceClass);
//        if (!newEquipmentSet.isEmpty() && !existingEquipments.isEmpty()) {
//            equipmentService.delete(existingEquipments);
//        }
//        for (Equipment equipment : newEquipmentSet) {
//            equipment.setDeviceClass(deviceClass);
//            equipmentService.create(equipment);
//        }
//    }

    public void replaceEquipment(@NotNull Collection<Equipment> equipmentsToReplace,
                                 @NotNull DeviceClass deviceClass) {
        equipmentService.deleteByDeviceClass(deviceClass);
        Set<String> codes = new HashSet<>(equipmentsToReplace.size());
        for (Equipment newEquipment : equipmentsToReplace) {
            if (codes.contains(newEquipment.getCode())) {
                throw new HiveException("Duplicate equipment entry with code = " + newEquipment.getCode() + " for " +
                        "device class with id : " + deviceClass.getId());
            }
            codes.add(newEquipment.getCode());
            newEquipment.setDeviceClass(deviceClass);
            equipmentService.create(newEquipment);
        }
    }

    public Set<Equipment> createEquipment(@NotNull DeviceClass deviceClass, @NotNull Set<Equipment> equipments) {
        Set<String> existingCodesSet = new HashSet<>(equipments.size());

        for (Equipment equipment : equipments) {
            if (existingCodesSet.contains(equipment.getCode())) {
                throw new HiveException("Duplicate equipment entry with code = " + equipment.getCode() + " for " +
                        "device class with id : " + deviceClass.getId());
            }
            existingCodesSet.add(equipment.getCode());
            equipment.setDeviceClass(deviceClass);
            equipmentService.create(equipment);
        }
        return equipments;
    }

    @Deprecated
    public Equipment createEquipment(Long classId, Equipment equipment) {
        DeviceClass deviceClass = deviceClassDAO.get(classId);

        if (deviceClass == null) {
            throw new HiveException("No device class with id = " + classId + " found", NOT_FOUND.getStatusCode());
        }
        if (deviceClass.getPermanent()) {
            throw new HiveException("Unable to update equipment on permanent device class.",
                    NOT_FOUND.getStatusCode());
        }
        List<Equipment> equipments = equipmentService.getByDeviceClass(deviceClass);
        String newCode = equipment.getCode();
        if (equipments != null) {
            for (Equipment e : equipments) {
                if (newCode.equals(e.getCode())) {
                    throw new HiveException("Equipment with code = " + newCode + " and device class id = " + classId +
                            " already exists");
                }
            }
        }
        equipment.setDeviceClass(deviceClass);
        return equipmentService.create(equipment);
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<DeviceClass> getDeviceClassList(String name, String namePattern, String version, String sortField,
                                                Boolean sortOrderAsc, Integer take, Integer skip) {
        return deviceClassDAO.getDeviceClassList(name, namePattern, version, sortField, sortOrderAsc, take, skip);
    }

}