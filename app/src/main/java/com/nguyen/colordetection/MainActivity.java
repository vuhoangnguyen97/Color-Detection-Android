package com.nguyen.colordetection;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "colordetection::Activity";



    private MenuItem menuTipoCamara = null;
    private MenuItem menuBlancoYNegro = null;
    private MenuItem menuModoReconocimiento = null;
    private MenuItem menuSubmenuResoluciones = null;
    private boolean tipoCamara = true;
    private boolean modoGrises = false;
    private boolean modoReconocimiento = false;
    private int anchoCamara = 1280; 
    private int altoCamara = 720;

    private CameraBridgeViewBase camara;
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @SuppressLint("LongLogTag")
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    camara.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //Screen ON Permanente

        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
        setContentView(R.layout.activity_main);

        if (tipoCamara){
            camara = (CameraBridgeViewBase)findViewById(R.id.camara_nativa);
        }else{
            camara = (CameraBridgeViewBase)findViewById(R.id.camara_java);
        }

        camara.setVisibility(SurfaceView.VISIBLE);
        camara.setCvCameraViewListener(new CameraBridgeViewBase.CvCameraViewListener2() {
            @Override
            public void onCameraViewStarted(int width, int height) {

            }

            @Override
            public void onCameraViewStopped() {

            }

            @Override
            public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame frame) {
                if(modoGrises){
                    return frame.gray();
                }else{
                    Mat mat = frame.rgba();
                    // PIXEL CENTRAL
                    int alto = altoCamara / 2;	//camera.getHeight() / 2;
                    int ancho = anchoCamara / 2;	//camera.getWidth() / 2;

                   double[] color = mat.get(alto, ancho);

                    double[] colorInverso = { 255 - color[0], 255 - color[1], 255 - color[2], 255};

                    Core.line(mat, new Point(0, altoCamara), new Point(anchoCamara - 25, altoCamara), new Scalar(colorInverso[0], colorInverso[1], colorInverso[2]), 1, 1, 1); //Izquierda
                    Core.line(mat, new Point(anchoCamara + 25, altoCamara), new Point(anchoCamara + anchoCamara, altoCamara), new Scalar(colorInverso[0], colorInverso[1], colorInverso[2]), 1, 1, 1); //Derecha

                    Core.line(mat, new Point(anchoCamara, 0), new Point(anchoCamara, altoCamara - 25), new Scalar(colorInverso[0], colorInverso[1], colorInverso[2]), 1, 1, 1); //Top
                    Core.line(mat, new Point(anchoCamara, altoCamara + 25), new Point(anchoCamara, altoCamara + altoCamara), new Scalar(colorInverso[0], colorInverso[1], colorInverso[2]), 1, 1, 1); //Bottom

                    Core.circle(mat, new Point(ancho, alto), 3, new Scalar(colorInverso[0], colorInverso[1], colorInverso[2]), -1);

                    Core.circle(mat, new Point(ancho, alto), 50, new Scalar(colorInverso[0], colorInverso[1], colorInverso[2]), 1);
                    String texto = "RGB: " + color[0] + " " + color[1] + " " + color[2];
                    Core.putText(mat, texto, new Point(10, 50), 3, 1, new Scalar(255, 255, 255, 255), 2);
		           String nombreColor = getColorName(color[0], color[1], color[2]);
                    Core.putText(mat, nombreColor, new Point(ancho, 50), 3, 1, new Scalar(255, 255, 255, 255), 2);
		            Core.rectangle(mat, new Point( 10 , 80), new Point(anchoCamara - 10, 100), new Scalar(color[0], color[1], color[2], 255), -1); //Al pintar, usamos RGBA


                    return mat;
                }
            }
        });
    }
}
