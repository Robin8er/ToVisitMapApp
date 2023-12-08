package hu.ait.tovisitmapapp.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.ait.tovisitmapapp.data.ToVisitItem
import hu.ait.tovisitmapapp.data.ToVisitListDAO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ToVisitListViewModel @Inject constructor(
    private val toVisitListDAO: ToVisitListDAO
) : ViewModel() {


    fun getAllToVisitList(): Flow<List<ToVisitItem>> {
        return toVisitListDAO.getAllToVisitItems()
    }

    fun getToVisitItemsLike(name: String): Flow<List<ToVisitItem>> {
        return toVisitListDAO.getToVisitItemsLike(name)
    }

//    suspend fun getFoodItemsNum(): Int {
//        return toVisitListDAO.getFoodItemsNum()
//    }
//
//    suspend fun getElectronicsItemsNum(): Int {
//        return toVisitListDAO.getElectronicsItemsNum()
//    }
//
//    suspend fun getBookItemsNum(): Int {
//        return toVisitListDAO.getBookItemsNum()
//    }

    fun addToVisitItem(toVisitItem: ToVisitItem) {
        viewModelScope.launch {
            toVisitListDAO.insert(toVisitItem)
        }
    }


    fun removeToVisitItem(toVisitItem: ToVisitItem) {
        viewModelScope.launch {
            toVisitListDAO.delete(toVisitItem)
        }
    }

    fun editToVisitItem(editedToVisitItem: ToVisitItem) {
        viewModelScope.launch {
            toVisitListDAO.update(editedToVisitItem)
        }
    }

    fun changeToVisitState(toVisitItem: ToVisitItem, value: Boolean) {
        val newToVisitItem = toVisitItem.copy()
        newToVisitItem.haveVisited = value
        viewModelScope.launch {
            toVisitListDAO.update(newToVisitItem)
        }
    }

    fun clearAllToVisitItems() {
        viewModelScope.launch {
            toVisitListDAO.deleteAllToVisitItems()
        }
    }
}