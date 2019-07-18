package com.example.cachua.musicmvp.data.repository

import android.util.Log
import com.example.cachua.musicmvp.data.model.MusicModel
import com.example.cachua.musicmvp.data.model.OnDataLoadedCallback
import com.example.cachua.musicmvp.data.source.ContentResolverData
import com.example.cachua.musicmvp.data.source.MusicDatasource
import com.example.cachua.musicmvp.data.source.MusicLocalDatasource
import java.net.MalformedURLException

class MusicRepository : MusicDatasource.Local {
    private var mMusicLocalDatasoure:MusicLocalDatasource
    constructor(mMusicLocalDatasoure:MusicLocalDatasource){
        this.mMusicLocalDatasoure=mMusicLocalDatasoure
    }


    override fun getAllMusic(onDataLoadedCallback: OnDataLoadedCallback<ArrayList<MusicModel>>) {
        try {
            mMusicLocalDatasoure.getAllMusic(onDataLoadedCallback)
        }
        catch (e: MalformedURLException){
            e.printStackTrace()
            onDataLoadedCallback.onDataNotAvailable(e)
        }

    }

}