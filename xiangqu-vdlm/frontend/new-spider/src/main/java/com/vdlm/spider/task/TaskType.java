package com.vdlm.spider.task;

/**
 *
 * @author: chenxi
 */

public enum TaskType {

	DEFAULT,
	MANUALLY;
	
	public static TaskType fromName(String name) {
        for (final TaskType type : TaskType.values()) {
            if (type.name().equals(name)) {
                return type;
            }
        }
        return null;
    }
	
	public static TaskType fromOrdinal(int ordinal) {
        for (final TaskType type : TaskType.values()) {
            if (type.ordinal() == ordinal) {
                return type;
            }
        }
        return null;
    }
}
