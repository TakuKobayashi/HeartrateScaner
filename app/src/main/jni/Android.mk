LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := heartrate_scaner
LOCAL_SRC_FILES := heartrate_scaner.cpp
LOCAL_C_INCLUDES += $(LOCAL_PATH)
LOCAL_LDLIBS := -llog
LOCAL_CFLAGS := -std=gnu++11

LOCAL_SHARED_LIBRARIES := liblog

include $(BUILD_SHARED_LIBRARY)
