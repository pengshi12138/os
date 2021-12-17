package com.niuniukeaiyouhaochi.os.device;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dongye
 * @date 2021/10/4 18:23
 */
public class DeviceController {
	private static DeviceController deviceController;
	public static List<Device> deviceList = new ArrayList<Device>();

	private DeviceController() {
		deviceList.add(new Device("A"));
		deviceList.add(new Device("A"));
		deviceList.add(new Device("B"));
		deviceList.add(new Device("B"));
		deviceList.add(new Device("C"));
	}

	public static DeviceController getInstance() {
		if (deviceController == null) {
			deviceController = new DeviceController();
		}
		return deviceController;
	}


	public static Device getDevice(String type) {
		for(int i=0;i<deviceList.size();i++) {
			if(deviceList.get(i).getType().equals(type)&&!deviceList.get(i).isOccupy()) {
				return deviceList.get(i);
			}
		}
		return null;
	}

	public static void resetDevice(Device device) {
		device.setPID("null");
		device.setOccupy(false);
	}
}
