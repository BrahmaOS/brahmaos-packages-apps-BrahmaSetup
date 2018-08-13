LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_DEX_PREOPT := false
LOCAL_PACKAGE_NAME := BrahmaSetup
LOCAL_MODULE_TAGS := optional
LOCAL_OVERRIDES_PACKAGES := Provision

LOCAL_SRC_FILES := $(call all-subdir-java-files)
LOCAL_RESOURCE_DIR := $(LOCAL_PATH)/res \
                      frameworks/support/design/res \
                      frameworks/support/v7/appcompat/res

LOCAL_AAPT_FLAGS := \
    --auto-add-overlay \
    --extra-packages android.support.design \
    --extra-packages android.support.v7.appcompat

LOCAL_STATIC_JAVA_LIBRARIES := \
    android-support-design \
    android-support-v7-appcompat \
    android-support-v4 \
    bitcoinj-core \
    web3j-core \
    guava-18 \
    slf4j-jdk14 \
    spongycastle-core \
    spongycastle-prov \
    protobuf-java \
    scrypt-jar

LOCAL_CERTIFICATE := platform
LOCAL_PRIVILEGED_MODULE := true

LOCAL_PROGUARD_FLAG_FILES := proguard.flags

include $(BUILD_PACKAGE)

include $(CLEAR_VARS)

LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := bitcoinj-core:libs/bitcoinj-core-0.14.7.jar \
                                        web3j-core:libs/web3j-core-android-2.2.1.jar \
                                        guava-18:libs/guava-18.0.jar \
                                        spongycastle-core:libs/spongycastle-core-1.54.0.0.jar \
                                        spongycastle-prov:libs/spongycastle-prov-1.54.0.0.jar \
                                        protobuf-java:libs/protobuf-java-2.6.1.jar \
                                        scrypt-jar:libs/lambdaworks-scrypt-1.4.0.jar
LOCAL_MODULE_TAGS := optional

include $(BUILD_MULTI_PREBUILT)

