package com.example.cachua.musicmvp.ui.music

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.cachua.musicmvp.R
import com.example.cachua.musicmvp.data.model.MusicModel
import com.example.cachua.musicmvp.data.repository.MusicRepository
import com.example.cachua.musicmvp.data.source.ContentResolverData
import com.example.cachua.musicmvp.data.source.MusicLocalDatasource
import com.example.cachua.musicmvp.service.MyMusicService
import com.example.cachua.musicmvp.ui.music.Adapater.MusicAdapter
import com.example.cachua.musicmvp.utils.Constant
import kotlinx.android.synthetic.main.activity_music.*
import java.text.SimpleDateFormat
import java.util.*

class MusicActivity : AppCompatActivity(), MusicContract.View, MyMusicService.CallBack, View.OnClickListener, MusicAdapter.CallBack {

    private lateinit var mMusicPresenter: MusicPresenter
    private lateinit var mMusicRepository: MusicRepository
    private lateinit var mMusicLocalDataSource: MusicLocalDatasource
    private lateinit var mMusicAdapter: MusicAdapter
    private lateinit var simpleDateFormat: SimpleDateFormat
    private lateinit var mMyMusicService: MyMusicService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music)
        initView()
    }

    private fun initView() {
        initData()
        initEvent()
    }

    private fun initData() {
        val mContentProvider = ContentResolverData(this)
        mMusicLocalDataSource = MusicLocalDatasource(mContentProvider)
        mMusicRepository = MusicRepository(mMusicLocalDataSource)
        mMusicPresenter = MusicPresenter(mMusicRepository, this)
        simpleDateFormat = SimpleDateFormat("mm:ss", Locale.US)
        
        var intent = Intent(this, MyMusicService::class.java)
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE)
        
        var mArrayMusic= ArrayList<MusicModel>()
        mMusicAdapter = MusicAdapter(applicationContext, mArrayMusic)
        recyclerMusic.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        recyclerMusic.setHasFixedSize(true)
        recyclerMusic.adapter = mMusicAdapter
    }

    private fun initEvent() {
        imagePlay.setOnClickListener(this)
        imagePlayNext.setOnClickListener(this)
        imagePlayBack.setOnClickListener(this)
        seekBarSong.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                mMyMusicService.onProgress(seekBarSong.progress)
            }

        })
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.imagePlay -> {
                if (mMyMusicService.isPlaying() == 0) {
                    imagePlay.setImageResource(R.drawable.icon_play_two)
                    mMyMusicService.pauseSong()
                } else {
                    imagePlay.setImageResource(R.drawable.icon_pause_two)
                    mMyMusicService.playSong()
                }
            }
            R.id.imagePlayNext -> {
                mMyMusicService.nextSong()
            }
            R.id.imagePlayBack -> {
                mMyMusicService.previousSong()
            }
        }
    }


    override fun displayListMusic(arrayListMuisc: ArrayList<MusicModel>) {
        mMusicAdapter.upDateAdapter(applicationContext,arrayListMuisc)
        mMusicAdapter.setCallBack(this@MusicActivity)

    }

    override fun onError() {
        Toast.makeText(this,"Loi doc du lieu",Toast.LENGTH_LONG).show()
    }

    override fun loadMusic(music: ArrayList<MusicModel>, i: Int) {
        constraintPlay.visibility = View.VISIBLE
        Log.e("...", "${music[i]}")
        textTitlePlay.text = music[i].title
        textArtistPlay.text = music[i].artist
        if (mMyMusicService.isPlaying() == 1) {
            imagePlay.setImageResource(R.drawable.icon_play_two)
        } else {
            imagePlay.setImageResource(R.drawable.icon_pause_two)
        }
        Glide
                .with(applicationContext)
                .load(music[i].uri)
                .placeholder(R.drawable.icon_music_player).into(imageAvatarPlay)
        textSongTotalDurationLabel.text = simpleDateFormat.format(music[i].duration.toLong())

        mMyMusicService.selectSong(this, music, i)
    }

    override fun onUpTime(music: MusicModel, progress: Int) {
        textTitlePlay.text = music.title
        textArtistPlay.text = music.artist
        Glide
                .with(applicationContext)
                .load(music.uri)
                .placeholder(R.drawable.icon_music_player).into(imageAvatarPlay)
        textSongTotalDurationLabel.text = simpleDateFormat.format(music.duration.toLong())
        textSongCurrentDurationLabel.text = simpleDateFormat.format(progress.toLong()).toString()
        var timeSong = (progress * 100 / music.duration)
        seekBarSong.progress = timeSong
    }

    private val mServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as MyMusicService.playMusicBinder
            mMyMusicService = binder.service()
            mMyMusicService.setCallBack(this@MusicActivity)
        }

        override fun onServiceDisconnected(name: ComponentName) {

        }
    }

    private fun isRequestPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    Constant.MY_PERMISSIONS_REQUEST_WRITE)
        }
        else{
            mMusicPresenter.loadDisPlayListMusic()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Constant.MY_PERMISSIONS_REQUEST_WRITE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    mMusicPresenter.loadDisPlayListMusic()
                }
            }
        }
    }



    override fun onResume() {
        super.onResume()
        isRequestPermissions()

    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

}
