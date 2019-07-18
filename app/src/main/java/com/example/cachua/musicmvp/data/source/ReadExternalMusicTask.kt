package com.example.cachua.musicmvp.data.source

import android.os.AsyncTask
import com.example.cachua.musicmvp.data.model.MusicModel
import com.example.cachua.musicmvp.data.model.OnDataLoadedCallback



class ReadExternalMusicTask :AsyncTask<Void, Void, ArrayList<MusicModel>>{
    private  var mContentResolverData: ContentResolverData
    private  var mCallback: OnDataLoadedCallback<ArrayList<MusicModel>>

    constructor(mContentResolverData: ContentResolverData, onDataLoadedCallback: OnDataLoadedCallback<ArrayList<MusicModel>>){
        this.mContentResolverData= mContentResolverData
        this.mCallback= onDataLoadedCallback
    }


    override fun doInBackground(vararg params: Void?): ArrayList<MusicModel> {
        return mContentResolverData.getData()
    }

    override fun onPostExecute(result: ArrayList<MusicModel>?) {
       if(result==null){
           mCallback.onDataNotAvailable(NullPointerException())
       }
        else{
           mCallback.onDataLoaded(result)
       }
    }

}