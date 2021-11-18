package com.example.player

import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.MediaController
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnPlay ->{
                audioPlay(posMusic)
            }
            R.id.btnNext ->{
                audioNext()
            }
            R.id.btnPause ->{
                audioStop()
            }
            R.id.btnPrev ->{
                audioPrev()
            }
        }
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        seekBar?.progress?.let { mediaPlayer.seekTo(it) }
    }

    val arrMusic = intArrayOf(R.raw.music1, R.raw.music2)
    val arrVideo = intArrayOf(R.raw.video1, R.raw.video2)
    val arrImage = intArrayOf(R.drawable.image1, R.drawable.image2)

    var posMusic = 0
    var posVideo = 0
    var handler = Handler()
    lateinit var mediaPlayer: MediaPlayer
    lateinit var mediaController: MediaController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mediaController = MediaController(this)
        mediaPlayer = MediaPlayer()
        seekSong.max = 100
        seekSong.progress = 0
        seekSong.setOnSeekBarChangeListener(this)
        btnNext.setOnClickListener(this)
        btnPause.setOnClickListener(this)
        btnPlay.setOnClickListener(this)
        btnPrev.setOnClickListener(this)
        mediaController.setPrevNextListeners(nextVideo, prevVideo)
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)
        videoSet(posVideo)

    }

    var nextVideo = View.OnClickListener { v:View ->
        if(posVideo < (arrVideo.size - 1))
            posVideo++
        else posVideo = 0
        videoSet(posVideo)
    }

    var prevVideo = View.OnClickListener { v:View ->
        if(posVideo > 0)
            posVideo--
        else posVideo = arrVideo.size - 1
        videoSet(posVideo)
    }

    fun videoSet(pos : Int){
        videoView.setVideoURI(Uri.parse("android.resource://"+packageName+"/"+arrVideo[pos]))
    }


    fun millisecondToString (ms : Int):String{
        var second = TimeUnit.MILLISECONDS.toSeconds(ms.toLong())
        var minute = TimeUnit.SECONDS.toMinutes(second)
        second %= 60
        return "$minute : $second"
    }


    fun audioPlay(pos : Int){
        mediaPlayer = MediaPlayer.create(this,arrMusic[pos])
        seekSong.max = mediaPlayer.duration
        txMxTm.setText(millisecondToString(seekSong.max))
        txCRTm.setText(millisecondToString(mediaPlayer.currentPosition))
        seekSong.progress = mediaPlayer.currentPosition
        imV.setImageResource(arrImage[pos])
        txSong.setText(arrMusic[pos])
        mediaPlayer.start()
        var updateSeekBar = UpdateSeekBar()
        handler.postDelayed(updateSeekBar,50)

    }

    fun audioNext(){
        if(mediaPlayer.isPlaying)mediaPlayer.stop()
        if(posMusic < (arrMusic.size - 1)){
            posMusic++
        }
        else{
            posMusic = 0
        }
        audioPlay(posMusic)
    }

    fun audioPrev(){
        if(mediaPlayer.isPlaying)mediaPlayer.stop()
        if(posMusic > 0){
            posMusic--
        }
        else{
            posMusic = arrMusic.size - 1
        }
        audioPlay(posMusic)
    }

    fun audioStop(){
        if(mediaPlayer.isPlaying) mediaPlayer.stop()
    }


    inner class UpdateSeekBar : Runnable{
        override fun run(){
            var currentTime = mediaPlayer.currentPosition
            txCRTm.setText(millisecondToString(currentTime))
            seekSong.progress = currentTime
            if(currentTime != mediaPlayer.duration) handler.postDelayed(this,50)
        }
    }
}