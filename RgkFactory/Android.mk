LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_STATIC_JAVA_LIBRARIES := libfactory android-support-v4

LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_PACKAGE_NAME := RgkFactory

LOCAL_CERTIFICATE := platform

LOCAL_JNI_SHARED_LIBRARIES := libsensor_calibration_jni
LOCAL_JAVA_LIBRARIES := mediatek-framework

LOCAL_REQUIRED_MODULES := libsensor_calibration_jni

include $(BUILD_PACKAGE)

include $(CLEAR_VARS)

LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := libfactory:libs/zxing.jar
include $(BUILD_MULTI_PREBUILT)

# Use the folloing include to make our test apk.
include $(LOCAL_PATH)/jni/sensor/Android.mk
include $(call all-makefiles-under,$(LOCAL_PATH))

