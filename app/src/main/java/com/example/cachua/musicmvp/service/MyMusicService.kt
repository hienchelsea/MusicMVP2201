package com.example.cachua.musicmvp.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.widget.RemoteViews
import com.example.cachua.musicmvp.R
import com.example.cachua.musicmvp.data.model.MusicModel
import com.example.cachua.musicmvp.ui.music.MusicActivity
import com.example.cachua.musicmvp.utils.Constant


class MyMusicService : Service() {
    private val CHANNEL_ID = "com.example.cachua.musickotlin"
    private var mStatusService: Int = 0
    private var mPosition: Int = 0
    private var mIbinder = playMusicBinder()
    private var mMediaPlayer: MediaPlayer = MediaPlayer()
    private lateinit var mMusic: MusicModel
    private lateinit var mArrayListMusic: ArrayList<MusicModel>
    private lateinit var mCallBack: CallBack

    interface CallBack {
        fun onUpTime(music: MusicModel, progress: Int)
    }

    fun setCallBack(callBack: CallBack) {
        this.mCallBack = callBack
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action.equals(Constant.PLAY_ACTION)) {
            if (mMediaPlayer.isPlaying) {
                pauseSong()
                mCallBack.onUpTime(mMusic, mMediaPlayer.currentPosition)
            } else {
                playSong()
                mCallBack.onUpTime(mMusic, mMediaPlayer.currentPosition)
            }
        } else {
            if (intent?.action.equals(Constant.NEXT_ACTION)) {
                nextSong()
            } else {
                if (intent?.action.equals(Constant.PREV_ACTION)) {
                    previousSong()
                }
            }
        }
        return START_NOT_STICKY
    }


    override fun onBind(intent: Intent): IBinder {
        return mIbinder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        mMediaPlayer.stop()
        super.onDestroy()
    }


    inner class playMusicBinder : Binder() {
        fun service(): MyMusicService {
            return this@MyMusicService
        }
    }


    fun isPlaying(): Int {
        return mStatusService
    }

    fun onProgress(i: Int) {
        mMediaPlayer.seekTo(i * mMusic.duration / 100)
    }


    fun selectSong(mContext: Context, mArrayListMusic: ArrayList<MusicModel>, position: Int) {
        this.mArrayListMusic = mArrayListMusic
        mPosition = position
        mMediaPlayer.reset()
        mMediaPlayer.setDataSource(mContext, Uri.parse(mArrayListMusic[position].data))
        mMediaPlayer.prepare()
        mMusic = mArrayListMusic[position]

        if (mStatusService == 0) {
            mMediaPlayer.start()
        }
        upDateMusicTime(mMusic, 0, mPosition)
    }

    fun nextSong() {
        mPosition = if (mPosition < mArrayListMusic.size - 1) {
            mPosition + 1
        } else {
            0
        }
        mMediaPlayer.reset()
        mMediaPlayer.setDataSource(this, Uri.parse(mArrayListMusic[mPosition].data))
        mMediaPlayer.prepare()
        mMusic = mArrayListMusic[mPosition]
        if (mStatusService == 0) {
            mMediaPlayer.start()
        }
        upDateMusicTime(mMusic, 0, mPosition)
    }

    fun previousSong() {
        mPosition = if (mPosition <= 0) {
            mArrayListMusic.size - 1
        } else {
            mPosition - 1
        }
        mMediaPlayer.reset()
        mMediaPlayer.setDataSource(this, Uri.parse(mArrayListMusic[mPosition].data))
        mMediaPlayer.prepare()
        mMusic = mArrayListMusic[mPosition]
        if (mStatusService == 0) {
            mMediaPlayer.start()
        }
        upDateMusicTime(mMusic, 0, mPosition)

    }

    fun playSong() {
        mMediaPlayer.start()
        mStatusService = 0
        upDateMusicTime(mMusic, mMediaPlayer.currentPosition, mPosition)
        pushNotification(mMusic)
    }

    fun pauseSong() {
        mMediaPlayer.pause()
        mStatusService = 1
        pushNotification(mMusic)
    }

    fun upDateMusicTime(music: MusicModel, progress: Int, position: Int) {
        Handler().postDelayed({
            if (progress == 0) {
                pushNotification(music)
            }
            if (progress == 0 && mStatusService == 1) {
                mCallBack.onUpTime(music, mMediaPlayer.currentPosition)
            }
            if(mStatusService == 0 && !mMediaPlayer.isPlaying){
                nextSong()
            }
            if (mStatusService == 0 && position == mPosition) {
                mCallBack.onUpTime(music, mMediaPlayer.currentPosition)
                upDateMusicTime(music, mMediaPlayer.currentPosition, position)
            }
        }, 1000)
    }


    fun pushNotification(music: MusicModel) {
        var intentNotification = Intent(this, MusicActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_NEW_TASK
        }
        var pendingIntent = PendingIntent.getActivity(this, 0, intentNotification, PendingIntent.FLAG_UPDATE_CURRENT)
        var notificationLayout = RemoteViews(packageName, R.layout.notification_layout)
        notificationLayout.setTextViewText(R.id.textTitlePlay, music.title)
        notificationLayout.setTextViewText(R.id.textArtistPlay, music.artist)
        notificationLayout.setImageViewResource(R.id.imageAvatarPlay, R.drawable.icon_music_player)
        if (mMediaPlayer.isPlaying) {
            notificationLayout.setImageViewResource(R.id.imagePlay, R.drawable.icon_pause_two)
        } else {
            notificationLayout.setImageViewResource(R.id.imagePlay, R.drawable.icon_play_two)
        }

        var playIntentNotification = Intent(this, MyMusicService::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_NEW_TASK
        }
        playIntentNotification.action = Constant.PLAY_ACTION
        var playPendingIntent = PendingIntent.getService(this, 0, playIntentNotification, 0)

        var nextIntentNotification = Intent(this, MyMusicService::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_NEW_TASK
        }
        nextIntentNotification.action = Constant.NEXT_ACTION
        var nextPendingIntent = PendingIntent.getService(this, 0, nextIntentNotification, 0)


        var preIntentNotification = Intent(this, MyMusicService::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_NEW_TASK
        }
        preIntentNotification.action = Constant.PREV_ACTION
        var prePendingIntent = PendingIntent.getService(this, 0, preIntentNotification, 0)

        notificationLayout.setOnClickPendingIntent(R.id.imagePlay, playPendingIntent)
        notificationLayout.setOnClickPendingIntent(R.id.imagePlayNext, nextPendingIntent)
        notificationLayout.setOnClickPendingIntent(R.id.imagePlayBack, prePendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(CHANNEL_ID, "Hien", importance)
            notificationChannel.description = "Oc"
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)

        }

        var mNotification:Notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.icon_music_player)
                .setCustomBigContentView(notificationLayout)
                .setContentIntent(pendingIntent)
                .setTicker(music.title)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                .build()

        startForeground(1, mNotification)
    }


}
