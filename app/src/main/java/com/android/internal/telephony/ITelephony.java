package com.android.internal.telephony;

/**
 * Created by hp on 28-03-2017.
 */

public interface ITelephony {
    boolean endCall();

    void answerRingingCall();

    void silenceRinger();
}
