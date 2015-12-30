package kobayashi.taku.com.heartratescan;

import android.app.Activity;
import android.graphics.Color;
import android.hardware.Camera;

import java.util.ArrayList;

public class HeartRateScanCamera {
    private Activity mActivity;
    private Camera mCamera;
    private Camera.Size mPreviewSize;
    private HeartScanCallback mCallback;

    private HeartrateField mHeartrate;

    public HeartRateScanCamera(Activity act){
        mActivity = act;
        mHeartrate = new HeartrateField();
    }

    public void setScanCallback(HeartScanCallback callback){
        mCallback = callback;
    }

    public void scanStart(){
        mCamera = Camera.open();
        Camera.Parameters cp = mCamera.getParameters();

        mCamera.setDisplayOrientation(Util.getCameraDisplayOrientation(mActivity, 0));
        mHeartrate.setImageSize(cp.getPreviewSize().width, cp.getPreviewSize().height);
        mPreviewSize = cp.getPreviewSize();
        cp.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        mCamera.setParameters(cp);

        mCamera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                int[] rgb = Util.decodeYUV420SP(data, mPreviewSize.width, mPreviewSize.height, mHeartrate);
                //指が当たっていない
                if(!mHeartrate.checkBeat()){
                    mHeartrate.reset();
                    return;
                }
                long span = mHeartrate.getSpan();
                if(mHeartrate.beat()){
                    if(mCallback != null) mCallback.onBeat(mHeartrate.getBpm(), span);
                }
            }
        });
        mCamera.startPreview();
    }

    public void releaseCamera() {
        if (mCamera != null){
            Camera.Parameters cp = mCamera.getParameters();
            cp.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(cp);
            mCamera.cancelAutoFocus();
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
        mHeartrate.reset();
    }

    public interface HeartScanCallback{
        public void onBeat(int bpm, long beatSpan);
    }

    /*
    public interface DecodePixelListener{
        public void onDecode(int pixel, int wIndex, int hIndex);
    }
    */
}
