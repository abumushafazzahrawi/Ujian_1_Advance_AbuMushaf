package com.example.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.database.EventEntity
import com.example.database.EventRoomDatabase
import com.example.indonesianevents.SettingPreference
import com.example.response.ListEventsItem
import com.example.retrofit.ApiConfig
import kotlinx.coroutines.launch

class MainViewModel(private val pref: SettingPreference) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _listEvent = MutableLiveData<List<ListEventsItem>>()
    val listEvent: LiveData<List<ListEventsItem>> = _listEvent

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _eventListEntity = MutableLiveData<List<EventEntity>>()
    val eventListEntity: LiveData<List<EventEntity>> = _eventListEntity

    private val _detailEvent = MutableLiveData<EventEntity?>()
    val detailEvent: LiveData<EventEntity?> = _detailEvent

    private val _favoriteEvents = MutableLiveData<List<EventEntity>>()
    val favoriteEvents: LiveData<List<EventEntity>> = _favoriteEvents

    fun getEventEntity(context: Context, type: String) {
        val db = EventRoomDatabase.getDatabase(context)
        viewModelScope.launch {
            val data = db.eventDao().getEventsByType(type)
            _eventListEntity.postValue(data)
        }
    }

    fun saveEventEntity(context: Context, list: List<ListEventsItem>, type: String) {
        val db = EventRoomDatabase.getDatabase(context)
        viewModelScope.launch {
            val existingFavorites = db.eventDao().getFavoriteEvents().map { it.id }.toSet()
            val entityList = list.map {
                EventEntity(
                    id = it.id,
                    name = it.name,
                    summary = it.summary,
                    description = it.description,
                    imageLogo = it.imageLogo,
                    mediaCover = it.mediaCover,
                    category = it.category,
                    ownerName = it.ownerName,
                    cityName = it.cityName,
                    quota = it.quota,
                    registrants = it.registrants,
                    beginTime = it.beginTime,
                    endTime = it.endTime,
                    link = it.link,
                    type = type,
                    isFavorite = existingFavorites.contains(it.id)
                )
            }
            db.eventDao().insertEvent(entityList)
        }
    }

    fun getDataEvent(context: Context) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = ApiConfig.getApiService().getUpComingEvents()
                if (response.isSuccessful) {
                    val result = response.body()
                    val data = result?.listEvents ?: emptyList()
                    _listEvent.value = data
                    saveEventEntity(context, data, "upcoming")
                    getEventEntity(context, "upcoming")
                } else {
                    _errorMessage.value = response.message()
                }
            } catch (e: Exception) {
                _errorMessage.value = "Koneksi bermasalah: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getDataFinishEvent(context: Context) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = ApiConfig.getApiService().getFinishedEvents()
                if (response.isSuccessful) {
                    val result = response.body()
                    val data = result?.listEvents ?: emptyList()
                    _listEvent.value = data
                    saveEventEntity(context, data, "finished")
                    getEventEntity(context, "finished")
                } else {
                    _errorMessage.value = response.message()
                }
            } catch (e: Exception) {
                _errorMessage.value = "Koneksi bermasalah: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchEvents(keyword: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = ApiConfig.getApiService().searchEvents(keyword = keyword)
                if (response.isSuccessful) {
                    val result = response.body()
                    val data = result?.listEvents ?: emptyList()
                    _listEvent.value = data
                } else {
                    _errorMessage.value = response.message()
                }
            } catch (e: Exception) {
                _errorMessage.value = "Search error: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getDetailEvent(context: Context, id: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            val db = EventRoomDatabase.getDatabase(context)
            val localData = db.eventDao().getEventById(id)
            if (localData != null) {
                _detailEvent.postValue(localData)
                _isLoading.value = false
            } else {
                try {
                    val response = ApiConfig.getApiService().getDetailEvent(id)
                    if (response.isSuccessful) {
                        val event = response.body()?.event
                        if (event != null) {
                            val entity = EventEntity(
                                id = event.id,
                                name = event.name,
                                summary = event.summary,
                                description = event.description,
                                imageLogo = event.imageLogo,
                                mediaCover = event.mediaCover,
                                category = event.category,
                                ownerName = event.ownerName,
                                cityName = event.cityName,
                                quota = event.quota,
                                registrants = event.registrants,
                                beginTime = event.beginTime,
                                endTime = event.endTime,
                                link = event.link,
                                type = "detail"
                            )
                            _detailEvent.postValue(entity)
                        }
                    }
                } catch (e: Exception) {
                    _errorMessage.value = e.localizedMessage
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }

    fun setFavoriteEvent(context: Context, event: EventEntity, favoriteState: Boolean) {
        viewModelScope.launch {
            val db = EventRoomDatabase.getDatabase(context)
            val updatedEvent = event.copy(isFavorite = favoriteState)
            db.eventDao().updateEvent(updatedEvent)
            _detailEvent.postValue(updatedEvent)
        }
    }

    fun getFavoriteEvents(context: Context) {
        viewModelScope.launch {
            val db = EventRoomDatabase.getDatabase(context)
            _favoriteEvents.postValue(db.eventDao().getFavoriteEvents())
        }
    }

    fun getThemeSettings(): LiveData<Boolean> {
        return pref.getThemeSetting().asLiveData()
    }

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            pref.saveThemeSetting(isDarkModeActive)
        }
    }

    fun getReminderSettings(): LiveData<Boolean> {
        return pref.getReminderSetting().asLiveData()
    }

    fun saveReminderSetting(isReminderActive: Boolean) {
        viewModelScope.launch {
            pref.saveReminderSetting(isReminderActive)
        }
    }
}
