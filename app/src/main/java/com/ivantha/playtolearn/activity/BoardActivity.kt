package com.ivantha.playtolearn.activity

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ivantha.playtolearn.R
import com.ivantha.playtolearn.adapter.TileRecyclerAdapter
import com.ivantha.playtolearn.common.FirebaseSaveHelper
import com.ivantha.playtolearn.common.Session
import com.ivantha.playtolearn.extensions.fadeIn
import com.ivantha.playtolearn.extensions.fadeOut
import com.ivantha.playtolearn.model.Board.Companion.COLUMN_COUNT
import com.ivantha.playtolearn.widget.ProblemDialog
import kotlinx.android.synthetic.main.activity_board.*

class BoardActivity : AppCompatActivity() {

    private val customHandler = Handler()

    private var startTime = 0L
    private var timeInMilliseconds = 0L
    private var pausedTime = 0L
    private var updatedTime = 0L

    private lateinit var successPlayer: MediaPlayer
    private lateinit var wrongPlayer: MediaPlayer
    private lateinit var reaction: ImageView

    private val updateTimerThread = object : Runnable {

        override fun run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime

            updatedTime = timeInMilliseconds

            var secs = (updatedTime / 1000).toInt()
            var mins = secs / 60
            secs %= 60
            val hrs = mins / 60
            mins %= 60

            timeStatusTextView!!.text = String.format("%02d:%02d:%02d", hrs, mins, secs)
            customHandler.postDelayed(this, 1000)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)

        reaction = findViewById(R.id.reaction)

        val gridLayoutManager = GridLayoutManager(this, COLUMN_COUNT, RecyclerView.VERTICAL, false)

        tileRecyclerView.layoutManager = gridLayoutManager
        tileRecyclerView.setHasFixedSize(true)

        val tileRecyclerAdapter = TileRecyclerAdapter(Session.saveFile!!.currentBoard, this::showQuestionDialog,
                this::updateGoldStatus, this::onMovement ,this::onGameOver)
        tileRecyclerView.adapter = tileRecyclerAdapter

        boardBackButton.setOnClickListener {
            finish()
        }

        boardMenuButton.setOnClickListener {
            finish()
            startActivity(Intent(this@BoardActivity, LevelsActivity::class.java))
        }

        boardRestartButton.setOnClickListener {
            FirebaseSaveHelper.newLevel(Session.currentUser!!.uid, Session.saveFile!!.currentLevel.id) {
                finish()
                startActivity(Intent(this@BoardActivity, BoardActivity::class.java))
            }
        }

        boardSettingsButton.setOnClickListener {
            startActivity(Intent(this@BoardActivity, SettingsActivity::class.java))
        }

        // Initialize sounds
        successPlayer = MediaPlayer.create(this, R.raw.jump)
        wrongPlayer = MediaPlayer.create(this, R.raw.click)

        // Start timer
        Session.saveFile!!.currentLevel.startTime = SystemClock.uptimeMillis()
        startTime = Session.saveFile!!.currentLevel.startTime
    }

    override fun onPause() {
        super.onPause()

        pausedTime = timeInMilliseconds
        customHandler.removeCallbacks(updateTimerThread)

        Session.saveFile!!.currentLevel.elapsedTime = pausedTime
    }

    override fun onResume() {
        super.onResume()
        val timeDiff =  SystemClock.uptimeMillis() - startTime - pausedTime
        startTime += timeDiff
        customHandler.postDelayed(updateTimerThread, 1000)
    }

    private fun updateGoldStatus() {
        goldStatusTextView.text = Session.saveFile!!.currentLevel.score.toString()
    }

    private fun showQuestionDialog(title: String, description: String) {
        val problemDialog = ProblemDialog(this@BoardActivity)
        problemDialog.setTitle(title)
        problemDialog.setDescription(description)
        problemDialog.show()
    }

    private fun onMovement(isSuccess: Boolean) {
        react(isSuccess)
        if (isSuccess) {
            successPlayer.start()
        } else {
            wrongPlayer.start()
        }
    }

    private fun react(isCorrect: Boolean) {
        if (isCorrect) {
            reaction.alpha = 1.0f
            reaction.setImageResource(R.drawable.smile)
            reaction.fadeIn(300) {
                reaction.fadeOut(500) {

                }
            }
        } else {
            reaction.alpha = 1.0f
            reaction.setImageResource(R.drawable.sad)
            reaction.fadeIn(300) {
                reaction.fadeOut(300) {

                }
            }
        }
    }

    private fun onGameOver() {
        finish()
        val intent = Intent(this, LevelCompleteActivity::class.java)
        intent.putExtra("level", Session.saveFile!!.currentLevel.id)
        intent.putExtra("score", Session.saveFile!!.currentLevel.score)
        intent.putExtra("time", Session.saveFile!!.currentLevel.elapsedTime)
        startActivity(intent)
    }
}
