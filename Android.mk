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
    web3j-crypto \
    web3j-abi \
    web3j-rlp \
    web3j-tuples \
    web3j-utils \
    guava-18 \
    slf4j-jdk14 \
    spongycastle-core \
    spongycastle-prov \
    protobuf-java \
    lambda-scrypt

LOCAL_STATIC_ANDROID_LIBRARIES := \
    android-support-v7-preference

LOCAL_CERTIFICATE := platform
LOCAL_PRIVILEGED_MODULE := true

LOCAL_PROGUARD_FLAG_FILES := proguard.flags

include frameworks/opt/setupwizard/library/common-gingerbread.mk
include frameworks/base/packages/SettingsLib/common.mk
include $(BUILD_PACKAGE)

