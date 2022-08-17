package com.ferdinandsilva.tamaomali

import androidx.appcompat.app.AppCompatActivity
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Bundle
import android.os.CountDownTimer
import androidx.core.app.ActivityCompat
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import org.opencv.android.*
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.MatOfRect
import org.opencv.core.Size
import org.opencv.objdetect.CascadeClassifier
import org.opencv.objdetect.Objdetect
import org.w3c.dom.Text
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.random.Random

class MainActivity : AppCompatActivity(), CameraBridgeViewBase.CvCameraViewListener2 {
    private var cameraView: CameraBridgeViewBase? = null
    private var tempImage: Mat? = null
    private var cam: Mat? = null
    private var cascadeClassifier: CascadeClassifier? = null
    private var mediaPlayer: MediaPlayer? = null
    private var pool: SoundPool? = null
    private var right: Int = 0
    private var wrong: Int = 0
    private var pressButton: ImageView? = null
    private var menuLayout: ConstraintLayout? = null
    private var gameLayout: ConstraintLayout? = null
    private var rightCountNum: Int = 0
    private var wrongCountNum: Int = 0
    private var rightCount: TextView? = null
    private var wrongCount: TextView? = null
    private var question: TextView? = null
    private var initialFaceX: Int = 0
    private var lastMove: Int = 0
    private var fromMove: Int = 0
    private var currentQuestion: Int = 0
    private var isCorrect: Boolean = false
    private var tamaButton: ImageView? = null
    private var maliButton: ImageView? = null
    private var showQuestionCounter: Int = 0

    enum class GameState {
        NONE,
        GET_FACE,
        GET_QUESTION,
        SHOW_QUESTION,
        SHOW_ANSWER
    }

    private var currentGameState: GameState = GameState.NONE

    private val cameraLoaderCallBack = object : BaseLoaderCallback(this) {
        override fun onManagerConnected(status: Int) {
            when (status) {
                LoaderCallbackInterface.SUCCESS -> {

                    Log.i(TAG, "OpenCV loaded successfully")
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
                    cameraView!!.enableView()
                    cameraView!!.setCameraIndex(1)

                    var inputStream: InputStream = resources.openRawResource(R.raw.face)
                    var detectionDir: File = getDir("cascade", MODE_PRIVATE)
                    var detectionFile = File(detectionDir, "face.xml")
                    var outputStream: FileOutputStream = FileOutputStream(detectionFile)
                    val buffer = ByteArray(4096)
                    inputStream.use { input ->
                        outputStream.use { fileOut ->
                            while (true) {
                                val length = input.read(buffer)
                                if (length <= 0)
                                    break
                                fileOut.write(buffer, 0, length)
                            }
                            fileOut.flush()
                            fileOut.close()
                        }
                    }
                    inputStream.close()
                    cascadeClassifier = CascadeClassifier(detectionFile.absolutePath)

                    if (cascadeClassifier!!.empty()) {
                        Log.e(TAG, "Unable to load classifer...")
                        //Toast.makeText(applicationContext, "Unable to load classifier...", Toast.LENGTH_LONG).show()
                    } else {
                        Log.i(TAG, "Classifer loaded...")
                        //Toast.makeText(applicationContext, "Classifier loaded...", Toast.LENGTH_LONG).show()
                    }
                    //load background music
                    mediaPlayer = MediaPlayer.create(applicationContext, R.raw.bg)
                    mediaPlayer?.setScreenOnWhilePlaying(true)

                    pool = SoundPool(6, AudioManager.STREAM_MUSIC, 0)
                    right =  pool!!.load(applicationContext, R.raw.tama, 1)
                    wrong = pool!!.load(applicationContext, R.raw.mali, 1)
                    //pool!!.play(right, 1.0f, 1.0f, 1, 0, 1.0f) //to play sound
                    mediaPlayer?.start()
                }
                else -> {
                    super.onManagerConnected(status)
                }
            }
        }
    }

    fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "called onCreate")
        super.onCreate(savedInstanceState)

        // Permissions for Android 6+
        ActivityCompat.requestPermissions(
            this@MainActivity,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST
        )

        setContentView(R.layout.activity_main)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        cameraView = findViewById(R.id.camera)
        cameraView!!.visibility = SurfaceView.VISIBLE
        cameraView!!.setCvCameraViewListener(this)

        menuLayout = findViewById(R.id.menu_layout)
        gameLayout = findViewById(R.id.game_layout)
        gameLayout?.visibility = View.GONE

        rightCount = findViewById(R.id.right_count)
        wrongCount = findViewById(R.id.wrong_count)

        tamaButton = findViewById(R.id.tama_button)
        maliButton = findViewById(R.id.mali_button)

        question = findViewById(R.id.question)
        question?.text = ""

        pressButton = findViewById(R.id.press_button)
        pressButton?.setOnClickListener(View.OnClickListener { view ->
            menuLayout?.visibility = View.GONE
            gameLayout?.visibility = View.VISIBLE
            currentGameState = GameState.GET_FACE
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    cameraView!!.setCameraPermissionGranted()
                } else {
                    val message = "Camera permission was not granted"
                    Log.e(TAG, message)
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                }
            }
            else -> {
                Log.e(TAG, "Unexpected permission request")
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (cameraView != null)
            cameraView!!.disableView()
    }

    override fun onResume() {
        super.onResume()
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization")
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, cameraLoaderCallBack)
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!")
            cameraLoaderCallBack.onManagerConnected(LoaderCallbackInterface.SUCCESS)
        }
    }

    override fun onStop() {
        super.onStop()
        mediaPlayer?.stop()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (cameraView != null)
            cameraView!!.disableView()
    }

    override fun onCameraViewStarted(width: Int, height: Int) {
        cam = Mat()
        tempImage = Mat()
    }

    override fun onCameraViewStopped() {
    }

    private fun showAnswer() {
        currentGameState = GameState.SHOW_ANSWER
        fromMove = lastMove

        if (isCorrect) {
            rightCountNum += 1
            pool!!.play(right, 1.0f, 1.0f, 1, 0, 1.0f)
        } else {
            wrongCountNum += 1
            pool!!.play(wrong, 1.0f, 1.0f, 1, 0, 1.0f)
        }
    }

    override fun onCameraFrame(frame: CameraBridgeViewBase.CvCameraViewFrame): Mat {

        if (cam != null) {
            cam?.release()
        }

        if (tempImage != null) {
            tempImage?.release()
        }

        cam = frame.rgba()
        Core.flip(cam, tempImage, 1)
        cam = tempImage

        var matOfRect: MatOfRect = MatOfRect();

        cascadeClassifier?.detectMultiScale(cam, matOfRect, 1.1, 4, Objdetect.CASCADE_SCALE_IMAGE, Size(20.0, 20.0))

        if (currentGameState == GameState.GET_QUESTION) {
            //currentQuestion = Random(0).nextInt(0, Question.questions.size - 1)
            currentQuestion = ((Math.random() * (Question.questions.size - 1)) + 0).toInt()
            currentGameState = GameState.SHOW_QUESTION
        }

        for(mor in matOfRect.toArray()) {
            if (currentGameState == GameState.GET_FACE) {
                initialFaceX = mor.x
                currentGameState = GameState.GET_QUESTION
                break
            }

            if (currentGameState != GameState.GET_FACE && currentGameState != GameState.NONE) {

                if (mor.x < (initialFaceX - 300) && lastMove != 1) {
                    //left
                    lastMove = 1

                    if (currentGameState == GameState.SHOW_QUESTION) {
                        isCorrect = Question.questions[currentQuestion].a
                        showAnswer()
                    }
                    break
                }

                if (mor.x > (initialFaceX + 300) && lastMove != 2) {
                    //right
                    lastMove = 2

                    if (currentGameState == GameState.SHOW_QUESTION) {
                        isCorrect = !Question.questions[currentQuestion].a
                        showAnswer()
                    }
                    break
                }

                if (mor.x >= (initialFaceX - 95) && mor.x <= (initialFaceX + 95) && lastMove != 0) {
                    //back
                    lastMove = 0
                    break
                }
            }
        }

        runOnUiThread {

            if(currentGameState == GameState.SHOW_ANSWER) {

                if (showQuestionCounter == SHOW_QUESTION_COUNTER) {
                    currentGameState = GameState.GET_QUESTION
                    showQuestionCounter = 0
                }

                if (fromMove == 1) {
                    //left
                    if(isCorrect) {
                        tamaButton?.setImageResource(R.drawable.tama_green)
                    } else {
                        tamaButton?.setImageResource(R.drawable.tama_red)
                    }
                } else {
                    //right
                    if(isCorrect) {
                        maliButton?.setImageResource(R.drawable.mali_green)
                    } else {
                        maliButton?.setImageResource(R.drawable.mali_red)
                    }
                }
                showQuestionCounter += 1
            } else {
                tamaButton?.setImageResource(R.drawable.tama_orange)
                maliButton?.setImageResource(R.drawable.mali_orange)
            }

            if (currentGameState == GameState.SHOW_QUESTION || currentGameState == GameState.SHOW_ANSWER) {
                //show text
                question?.text = Question.questions[currentQuestion].q
            } else {
                //dont show
                question?.text = ""
            }

            rightCount?.text = "Tamang Sagot: ${rightCountNum}"
            wrongCount?.text = "Maling Sagot: ${wrongCountNum}"
        }

        return cam!!
    }

    companion object {

        private const val TAG = "MainActivity"
        private const val CAMERA_PERMISSION_REQUEST = 1
        private const val SHOW_QUESTION_COUNTER = 8
    }
}