#include <jni.h>
#include <string>
#include "api_key.h"

extern "C" JNIEXPORT jstring JNICALL
Java_com_criticalblue_currencyconverterdemo_MainActivity_getApiKey(
        JNIEnv *env,
        jobject /* this */) {

    // To add the API_KEY to the mobile app when is compiled you need to:
    //   * copy `api_key.h.example` to `api_key.h`
    //   * edit the file and replace this text `place-the-api-key-here` with your desired API_KEY
    std::string JNI_API_KEY = CURRENCY_CONVERTER_DEMO_API_KEY_H;

    return env->NewStringUTF(JNI_API_KEY.c_str());
}
