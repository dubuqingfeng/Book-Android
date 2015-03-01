/*
 * Copyright (C) 2010 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hookheart.bookbook.camera;

import android.graphics.Point;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

final class PreviewCallback implements Camera.PreviewCallback
{

    private static final String TAG = PreviewCallback.class.getSimpleName();

    /**
     * 相机配置管理
     */
    private final CameraConfigurationManager configManager;
    /**
     * 是否是一次性照相
     */
    private final boolean useOneShotPreviewCallback;
    /**
     * 解码的handler
     */
    private Handler previewHandler;
    /**
     * 解码信号
     */
    private int previewMessage;

    PreviewCallback(CameraConfigurationManager configManager,
            boolean useOneShotPreviewCallback)
    {
        this.configManager = configManager;
        this.useOneShotPreviewCallback = useOneShotPreviewCallback;
    }

    void setHandler(Handler previewHandler, int previewMessage)
    {
        this.previewHandler = previewHandler;
        this.previewMessage = previewMessage;
    }

    public void onPreviewFrame(byte[] data, Camera camera)
    {
        Point cameraResolution = configManager.getCameraResolution();
        if (!useOneShotPreviewCallback)
        {
            camera.setPreviewCallback(null);
        }
        if (previewHandler != null)
        {
            /**
             * 这个handler就是来自于DecodeThread中run（）中的handler
             */
            Message message = previewHandler.obtainMessage(previewMessage,
                    cameraResolution.x, cameraResolution.y, data);
            message.sendToTarget();
            previewHandler = null;
        }
        else
        {
            Log.d(TAG, "Got preview callback, but no handler for it");
        }
    }

}
