package com.example.cachua.musicmvp.data.source

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.example.cachua.musicmvp.data.model.MusicModel

class ContentResolverData {

    private var mContext: Context
    constructor(mContext: Context){
        this.mContext=mContext
    }

    fun getData():ArrayList<MusicModel>{
        var mArrayListMusic: ArrayList<MusicModel> = ArrayList()
        val projection = arrayOf(MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID
        )
        val cr =  mContext.contentResolver
        val cursor = cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection, null, null, "LOWER(" + MediaStore.Audio.Media.TITLE
                + ") ASC")
        var count = 0
        while (cursor.moveToNext()) {
            count++
            mArrayListMusic.add(MusicModel(cursor.getString(0).toInt(),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5).toInt(),
                    cursor.getString(6).toLong(),
                    loadImage(cursor.getString(6).toLong().toLong())
            ))
        }
        cursor.close()

        return mArrayListMusic
    }
    private fun loadImage(idAlbum: Long): Uri {
        val sArtworkUri = Uri
                .parse("content://media/external/audio/albumart")
        return ContentUris.withAppendedId(sArtworkUri, idAlbum)
    }

}