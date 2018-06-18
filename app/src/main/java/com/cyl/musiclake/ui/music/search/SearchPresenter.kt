package com.cyl.musiclake.ui.music.search

import com.cyl.musiclake.api.MusicApiServiceImpl
import com.cyl.musiclake.base.BasePresenter
import com.cyl.musiclake.data.db.DaoLitepal
import com.cyl.musiclake.data.db.Music
import com.cyl.musiclake.net.ApiManager
import com.cyl.musiclake.net.RequestCallBack
import javax.inject.Inject

/**
 * Created by yonglong on 2018/1/6.
 */

class SearchPresenter @Inject
constructor() : BasePresenter<SearchContract.View>(), SearchContract.Presenter {

    override fun search(key: String, type: SearchEngine.Filter, limit: Int, page: Int) {
        ApiManager.request(MusicApiServiceImpl
                .searchMusic(key, type, limit, page)
                .compose(mView.bindToLife()),
                object : RequestCallBack<List<Music>> {
                    override fun success(result: List<Music>) {
                        mView?.showSearchResult(result)
                        mView?.hideLoading()
                    }

                    override fun error(msg: String) {
                        mView?.hideLoading()
                    }
                })
    }

    override fun getSuggestions(query: String) {
        Thread {
            val data = DaoLitepal.getAllSearchInfo(query)
            mView?.showSearchSuggestion(data)
        }.start()
    }

    override fun saveQueryInfo(query: String) {
        DaoLitepal.addSearchInfo(query)
    }
}
