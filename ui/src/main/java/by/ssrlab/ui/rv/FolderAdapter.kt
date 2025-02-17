package by.ssrlab.ui.rv

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import by.ssrlab.data.obj.FolderObject
import by.ssrlab.common_ui.databinding.RvFolderItemBinding


class FolderAdapter(
    private val entitiesList: List<FolderObject>,
    private val pngLoadAction: (ImageView, Int) -> Unit,
    private val navigateAction: (Int) -> Unit
): RecyclerView.Adapter<FolderAdapter.FolderHolder>() {

    inner class FolderHolder(val binding: RvFolderItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RvFolderItemBinding.inflate(
            inflater,
            parent,
            false
        )

        return FolderHolder(binding)
    }

    override fun onBindViewHolder(holder: FolderHolder, position: Int) {
        holder.binding.apply {
            rvFolderMainTitle.text = root.context.getString(entitiesList[position].title)
            pngLoadAction(rvFolderMainPng, entitiesList[position].imageResource)
            rvFolderMainRipple.setOnClickListener {
                navigateAction(entitiesList[position].address)
            }
        }
    }

    override fun getItemCount() = entitiesList.size
}