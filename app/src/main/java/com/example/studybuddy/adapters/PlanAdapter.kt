package com.example.studybuddy.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.studybuddy.data.Plan
import com.example.studybuddy.databinding.ItemPlanBinding

class PlanAdapter(
    private val planList: MutableList<Plan>,
    private val onPlanClick: (Plan) -> Unit,
    private val onDeleteClick: (Plan) -> Unit
) : RecyclerView.Adapter<PlanAdapter.PlanViewHolder>() {

    inner class PlanViewHolder(val binding: ItemPlanBinding) : RecyclerView.ViewHolder(binding.root) {
        init {

            binding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onPlanClick(planList[adapterPosition])
                }
            }


            binding.deleteButton.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onDeleteClick(planList[adapterPosition])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
        val binding = ItemPlanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlanViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
        val plan = planList[position]
        holder.binding.subjectText.text = plan.subject
        holder.binding.dateText.text = plan.date
        holder.binding.timeText.text = plan.time
        holder.binding.noteText.text = plan.note
    }

    override fun getItemCount() = planList.size

    fun updatePlans(newPlans: List<Plan>) {
        planList.clear()
        planList.addAll(newPlans)
        notifyDataSetChanged()
    }
}