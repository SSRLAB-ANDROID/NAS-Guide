package by.ssrlab.domain.repository.ui

import by.ssrlab.data.obj.FolderObject
import by.ssrlab.domain.R

class UiDataProvider {

    fun getMainFolders(address1: Int, address2: Int, address3: Int): List<FolderObject> {
        return listOf(
            FolderObject(R.string.folder_history, R.drawable.png_folder_history, address1),
            FolderObject(R.string.folder_organizations, R.drawable.png_folder_orgs, address2),
            FolderObject(R.string.folder_inventions, R.drawable.png_folder_develops, address3)
        )
    }

    fun getHistoryFolders(address1: Int, address2: Int, address3: Int): List<FolderObject> {
        return listOf(
            FolderObject(R.string.folder_events, R.drawable.png_folder_events, address1),
            FolderObject(R.string.folder_persons, R.drawable.png_folder_persons, address2),
            FolderObject(R.string.folder_places, R.drawable.png_folder_places, address3)
        )
    }
}