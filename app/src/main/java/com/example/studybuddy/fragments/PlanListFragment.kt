package com.example.studybuddy.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studybuddy.adapters.PlanAdapter
import com.example.studybuddy.viewmodels.PlanViewModel
import com.example.studybuddy.R
import com.example.studybuddy.data.Plan
import com.example.studybuddy.databinding.FragmentPlanListBinding

class PlanListFragment : Fragment() {

    private var _binding: FragmentPlanListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: PlanAdapter
    private val planViewModel: PlanViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlanListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PlanAdapter(
            mutableListOf(),
            onPlanClick = { plan ->
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, CreatePlanFragment.newInstanceForEdit(plan))
                    .addToBackStack(null)
                    .commit()
            },
            onDeleteClick = { plan ->
                showDeleteConfirmationDialog(plan)
            }
        )

        binding.planRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.planRecyclerView.adapter = adapter

        planViewModel.plans.observe(viewLifecycleOwner) { plans ->
            adapter.updatePlans(plans)
        }

        binding.createPlanButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, CreatePlanFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun showDeleteConfirmationDialog(plan: Plan) {
        AlertDialog.Builder(requireContext())
            .setTitle("Избриши план")
            .setMessage("Дали сте сигурни дека сакате да го избришете планот за ${plan.subject}?")
            .setPositiveButton("Избриши") { _, _ ->
                planViewModel.deletePlan(plan)
                Toast.makeText(requireContext(), "Планот е избришан", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Откажи", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}