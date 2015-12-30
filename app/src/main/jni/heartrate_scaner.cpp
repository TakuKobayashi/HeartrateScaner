#include <jni.h>
#include <string.h>
#include <android/log.h>

using namespace std;

extern "C" {
JNIEXPORT jintArray JNICALL Java_kobayashi_taku_com_heartratescan_Util_convert(JNIEnv *env,
                                                                                      jobject obj,
                                                                                      jbyteArray yuv420sp,
                                                                                      jint width,
                                                                                      jint height,
                                                                                      jobject heartrate) {


    jbyte *yuv420 = env->GetByteArrayElements(yuv420sp, 0);

    jclass heartrateClass = env->GetObjectClass(heartrate);

    int redLightCounter = 0;
    int lightFieldCount = 0;

    int frameSize = width * height;
    jintArray r = env->NewIntArray(frameSize);
//    jintArray grayArray = env->NewIntArray(frameSize);
    jint *narr = env->GetIntArrayElements(r, 0);
//    jint *nGrayArr = env->GetIntArrayElements(grayArray, 0);
    for (int j = 0, yp = 0; j < height; ++j) {
        int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
        for (int i = 0; i < width; ++i, ++yp) {
            int y = (0xff & ((int) yuv420[yp])) - 16;
            if (y < 0) y = 0;
            if ((i & 1) == 0) {
                v = (0xff & yuv420[uvp++]) - 128;
                u = (0xff & yuv420[uvp++]) - 128;
            }

            int y1192 = 1192 * y;
            int r = (y1192 + 1634 * v);
            int g = (y1192 - 833 * v - 400 * u);
            int b = (y1192 + 2066 * u);

            if (r < 0) r = 0; else if (r > 262143) r = 262143;
            if (g < 0) g = 0; else if (g > 262143) g = 262143;
            if (b < 0) b = 0; else if (b > 262143) b = 262143;

            int rgb = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);

            int gray = (int) (0.298912 * b + 0.586611 * g + 0.114478 * r);
            if (r > 128) {
                ++redLightCounter;
            }
            if (gray > 128) {
                ++lightFieldCount;
            }

            //nGrayArr[yp] = (alpha << 24) | (gray << 16) | (gray << 8) | gray;

            narr[yp] = rgb;
        }
    }

    jmethodID redLightCountId = env->GetMethodID(heartrateClass, "setRedLightCount", "(I)V");
    env->CallVoidMethod(heartrate, redLightCountId, redLightCounter);

    jmethodID lightFieldId = env->GetMethodID(heartrateClass, "setLightFieldCount", "(I)V");
    env->CallVoidMethod(heartrate, lightFieldId, lightFieldCount);

    env->ReleaseByteArrayElements(yuv420sp, yuv420, 0);
    env->ReleaseIntArrayElements(r, narr, 0);
    return r;
}
}