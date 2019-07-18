package com.example.cachua.musicmvp.data.source

import com.example.cachua.musicmvp.data.model.MusicModel
import com.example.cachua.musicmvp.data.model.OnDataLoadedCallback


class MusicLocalDatasource : MusicDatasource.Local{
    private var mContentResolverData:ContentResolverData

    constructor( mContentResolverData:ContentResolverData){
        this.mContentResolverData=mContentResolverData
    }

    override fun getAllMusic(onDataLoadedCallback: OnDataLoadedCallback<ArrayList<MusicModel>>) {
        ReadExternalMusicTask(mContentResolverData, onDataLoadedCallback).execute()
    }


}