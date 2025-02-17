package by.ssrlab.common_ui.common.ui.exhibit.fragments.exhibit

import by.ssrlab.common_ui.common.ui.MainActivity
import by.ssrlab.data.data.common.RepositoryData


object NavigationManager {

    lateinit var context: MainActivity
    var entitiesList: List<RepositoryData>? = null
    private var listSize = 1
    var currentPosition = 1

    private fun getPrevPosition(): Int {
        return if (currentPosition - 1 < 0) listSize - 1 else currentPosition - 1
    }

    private fun getNextPosition(): Int {
        return if (currentPosition + 1 >= listSize) 0 else currentPosition + 1
    }

    fun handleNavigate(activity: MainActivity) {
        context = activity
        entitiesList?.let {
            val currentItem = entitiesList!![currentPosition]
            activity.moveToExhibit(currentItem)
            listSize = entitiesList?.size!!
        }
    }

    fun handlePrevious() {
        val prevPosition = getPrevPosition()

        entitiesList?.let { context.moveToExhibit(entitiesList!![prevPosition]) }
        currentPosition = prevPosition
    }

    fun handleNext() {
        val nextPosition = getNextPosition()

        entitiesList?.let { context.moveToExhibit(entitiesList!![nextPosition]) }
        currentPosition = nextPosition
    }
}