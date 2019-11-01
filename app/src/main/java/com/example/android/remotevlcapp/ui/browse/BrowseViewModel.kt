package com.example.android.remotevlcapp.ui.browse

import android.net.Uri
import androidx.lifecycle.*
import com.example.android.remotevlcapp.domain.browse.BrowseUseCase
import com.example.android.remotevlcapp.domain.browse.OpenFileUseCase
import com.example.android.remotevlcapp.event.Event
import com.example.android.remotevlcapp.event.Result
import com.example.android.remotevlcapp.model.FileInfo
import com.example.android.remotevlcapp.util.formatPathFromUri
import com.example.android.remotevlcapp.util.formatUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BrowseViewModel @Inject constructor(
    private val browseUseCase: BrowseUseCase,
    private val openFileUseCase: OpenFileUseCase
) : ViewModel() {

    private var currentPath = "file:///"
    private var directory = ""

    private val _swipeRefreshing = MutableLiveData<Event<Boolean>>()
    val swipeRefreshing: LiveData<Event<Boolean>> = _swipeRefreshing

    private val browseResponse = MutableLiveData<Result<List<FileInfo>>>()
    val browseUiData: LiveData<BrowseUiData> = browseResponse.switchMap {
        liveData {
            val path = formatPathFromUri(currentPath)
            directory = Uri.parse(currentPath).lastPathSegment ?: "/"
            val result = processBrowseResponse(it)
            _swipeRefreshing.postValue(Event(false))
            emit(BrowseUiData(result, path, directory))
        }
    }

    init {
        browse(currentPath)
    }

    fun onSwipeRefresh() {
        browse(currentPath)
    }

    fun browse(uri: String) {
        viewModelScope.launch(Dispatchers.IO) {
            browseResponse.postValue(Result.Loading)
            currentPath = formatUri(uri)
            browseResponse.postValue(browseUseCase(currentPath))
        }
    }

    fun openFile(uri: String) {
        viewModelScope.launch(Dispatchers.IO) { openFileUseCase(uri) }
    }

    private suspend fun processBrowseResponse(result: Result<List<FileInfo>>): Result<List<FileInfo>> {
        return if (result is Result.Success) {
            withContext(Dispatchers.Default) {
                Result.Success(putUpFolderFirst(result.data))
            }
        } else result
    }

    private fun putUpFolderFirst(files: List<FileInfo>): List<FileInfo> {
        val mutableFiles = files.toMutableList()
        mutableFiles.sortBy { it.type }
        var dir: FileInfo? = null
        for (file in mutableFiles) {
            if (file.type == "dir" && file.name == "..") {
                dir = file
                break
            }
        }
        val newFiles = mutableListOf<FileInfo>()
        if (dir != null) {
            mutableFiles.remove(dir)
            newFiles.add(dir)
            newFiles.addAll(mutableFiles)
        } else {
            if (directory != "/") {
                newFiles.add(FileInfo("dir", "", "..", "file:///", 0, 0))
            }
            newFiles.addAll(mutableFiles)
        }
        return newFiles
    }
}

data class BrowseUiData(
    val result: Result<List<FileInfo>>,
    val path: String,
    val directory: String
)
