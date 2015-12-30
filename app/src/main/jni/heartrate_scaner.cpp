#include <jni.h>
#include <string.h>
#include <android/log.h>

using namespace std;

extern "C" {
JNIEXPORT jintArray JNICALL Java_kobayashi_taku_com_heartratescan_Util_convert(JNIEnv *env,
                                                                                      jobject obj,
                                                                                      jintArray src,
                                                                                      jint width,
                                                                                      jint height) {
    jint *arr = env->GetIntArrayElements(src, 0);
    int totalPixel = width * height;
    jintArray r = env->NewIntArray(totalPixel);
    jint *narr = env->GetIntArrayElements(r, 0);
    for (int i = 0; i < totalPixel; i++) {
        int alpha = (arr[i] & 0xFF000000) >> 24;
        int red = (arr[i] & 0x00FF0000) >> 16;
        int green = (arr[i] & 0x0000FF00) >> 8;
        int blue = (arr[i] & 0x000000FF);
        //ここに計算処理を色々と書く。
        narr[i] = (alpha << 24) | (blue << 16) | (red << 8) | green;
    }
    env->ReleaseIntArrayElements(src, arr, 0);
    env->ReleaseIntArrayElements(r, narr, 0);
    return r;
}
}