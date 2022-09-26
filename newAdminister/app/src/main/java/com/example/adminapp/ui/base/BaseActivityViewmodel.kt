package com.example.adminapp.ui.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.adminapp.utils.SingleLiveEvent
import com.example.adminapp.utils.SnackbarMessageString
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit


open class BaseActivityViewModel(application: Application): AndroidViewModel(application) {

    protected val compositeDisposable = CompositeDisposable()

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

    private fun addDisposable(disposable: Disposable){
        compositeDisposable.add(disposable)
    }


    private val snackbarMessageString = SnackbarMessageString()

    private val _apiCallErrorEvent:SingleLiveEvent<String> = SingleLiveEvent()
    val apiCallErrorEvent: LiveData<String> get() = _apiCallErrorEvent

    open fun <T> apiCall(single: Single<T>, onSuccess: Consumer<in T>,
                         onError: Consumer<in Throwable> = Consumer {
                             _apiCallErrorEvent.postValue(it.message)
                             showSnackbar("오류가 발생했습니다. ${it.message}")
                         },
                         indicator : Boolean = false, timeout: Long = 10){
        addDisposable(single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .timeout(timeout, TimeUnit.SECONDS)
            .subscribe(onSuccess, onError))
    }

    fun apiCall(completable: Completable,
                onComplete: Action = Action{
                },
                onError: Consumer<Throwable> = Consumer {
                    _apiCallErrorEvent.postValue(it.message)
                    showSnackbar("오류가 발생했습니다. ${it.message}")
                },
                indicator: Boolean = false,
                timeout: Long = 5){
        addDisposable(completable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .timeout(timeout, TimeUnit.SECONDS)
            .subscribe(onComplete, onError))
    }

    fun showSnackbar(str: String){
        snackbarMessageString.postValue(str)
    }

}