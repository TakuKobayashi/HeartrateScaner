package kobayashi.taku.com.heartratescan;

import android.util.Log;

import java.util.ArrayList;

public class HeartrateField {
  private static final String TAG = "HeartRateScan";

  private int frameWidth = 0;
  private int frameHeight = 0;

  //指を当てている間は赤がほとんどをしめるため、指が当たっているかどうかの判定に使う
  private int redLightCounter = 0;
  // 明るい部分を数える
  private int lightFieldCount = 0;
  // 指が当たっている時、直前の明るい部分を記録して比較するのに使う
  private int prevSampling = 0;
  // 明るい部分が前回との差分により増減していくことで、1ループで1ビートとカウントできそう
  private int beatCounter = 0;
  //前回とのビートの時でBPがなんとなく出そう
  private long prevBeatSpan = 0;
  private int sumLoopBeatCount;
  private ArrayList<Integer> bpmList = new ArrayList<Integer>();

  public void HeartrateField() {
    reset();
  }

  public void setImageSize(int width, int height) {
    frameWidth = width;
    frameHeight = height;
  }

  public void setRedLightCount(int count){
    Log.d(TAG, "redCount:" + count);
    redLightCounter = count;
  }

  public void setLightFieldCount(int count){
    Log.d(TAG, "lightCount:" + count);
    lightFieldCount = count;
  }

  public boolean checkBeat(){
    return redLightCounter > (frameWidth * frameHeight) * 0.9;
  }

  public void reset() {
    prevSampling = 0;
    beatCounter = 0;
    prevBeatSpan = System.currentTimeMillis();
    sumLoopBeatCount = 0;
    bpmList.clear();
  }

  public boolean beat() {
    Log.d(TAG, "w:" + frameWidth + " h:" + frameHeight + "wh:" + frameWidth * frameHeight + " r:" + redLightCounter + " l:" + lightFieldCount);
    Log.d(TAG, "b:" + beatCounter + " bs:" + prevBeatSpan + " s:" + sumLoopBeatCount);

    if (prevSampling == 0) {
      prevSampling = lightFieldCount;
      prevBeatSpan = System.currentTimeMillis();
    } else {
      if (prevSampling < lightFieldCount) {
        ++beatCounter;
        if (beatCounter > 1) beatCounter = 1;
      } else {
        --beatCounter;
        if (beatCounter < -1) beatCounter = -1;
      }
      prevSampling = lightFieldCount;
      if (beatCounter == 0) {
        ++sumLoopBeatCount;
        if (sumLoopBeatCount % 2 == 0) {
          long span = getSpan();
          int bpm = (int) ((float) 1 * 1000 * 60 / span);
          prevBeatSpan = System.currentTimeMillis();
          bpmList.add(bpm);
          return true;
        }
      }
    }
    return false;
  }

  public int getBpm() {
    int sum = 0;
    for(int i = 0;i < bpmList.size();++i) {
      sum += bpmList.get(i);
    }
    Log.d(TAG, "sum:" + sum + " size:" + bpmList.size());
    return sum / bpmList.size();
  }

  public long getSpan() {
    return System.currentTimeMillis() - prevBeatSpan;
  }
}
