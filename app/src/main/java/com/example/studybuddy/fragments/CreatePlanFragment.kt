package com.example.studybuddy.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.studybuddy.viewmodels.PlanViewModel
import com.example.studybuddy.data.Plan
import com.example.studybuddy.databinding.FragmentCreatePlanBinding
import java.util.*

class CreatePlanFragment : Fragment() {

    private var _binding: FragmentCreatePlanBinding? = null
    private val binding get() = _binding!!

    private val planViewModel: PlanViewModel by activityViewModels()
    private val calendar = Calendar.getInstance()


    private var editingPlan: Plan? = null
    private var isEditMode = false

    companion object {
        private const val ARG_PLAN = "arg_plan"


        fun newInstanceForEdit(plan: Plan): CreatePlanFragment {
            val fragment = CreatePlanFragment()
            val bundle = Bundle()
            bundle.putSerializable(ARG_PLAN, plan)
            fragment.arguments = bundle
            return fragment
        }

        fun newInstance(): CreatePlanFragment {
            return CreatePlanFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let { bundle ->
            editingPlan = bundle.getSerializable(ARG_PLAN) as? Plan
            isEditMode = editingPlan != null
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePlanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkNotificationPermission()

        if (isEditMode && editingPlan != null) {
            populateFieldsForEdit(editingPlan!!)
            binding.saveButton.text = "Ажурирај"
        }

        binding.dateInput.setOnClickListener {
            DatePickerDialog(
                requireContext(), { _, year, month, day ->
                    binding.dateInput.setText(String.format("%02d/%02d/%04d", day, month + 1, year))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }


        binding.timeInput.setOnClickListener {
            TimePickerDialog(
                requireContext(), { _, hour, minute ->
                    binding.timeInput.setText(String.format("%02d:%02d", hour, minute))
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }


        binding.saveButton.setOnClickListener {
            savePlan()
        }

        binding.cancelButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun populateFieldsForEdit(plan: Plan) {
        binding.subjectInput.setText(plan.subject)
        binding.dateInput.setText(plan.date)
        binding.timeInput.setText(plan.time)
        binding.noteInput.setText(plan.note)
    }

    private fun savePlan() {
        val subject = binding.subjectInput.text.toString()
        val date = binding.dateInput.text.toString()
        val time = binding.timeInput.text.toString()
        val note = binding.noteInput.text.toString()

        if (!TextUtils.isEmpty(subject) && !TextUtils.isEmpty(date) && !TextUtils.isEmpty(time)) {

            if (isEditMode && editingPlan != null) {
                val updatedPlan = editingPlan!!.copy(
                    subject = subject,
                    date = date,
                    time = time,
                    note = note
                )
                planViewModel.updatePlan(updatedPlan)
                Toast.makeText(requireContext(), "Планот е ажуриран!", Toast.LENGTH_SHORT).show()
            } else {
                val newPlan = Plan(subject = subject, date = date, time = time, note = note)
                planViewModel.addPlan(newPlan)
                Toast.makeText(requireContext(), "Планот е зачуван! Ќе добиете потсетник.", Toast.LENGTH_LONG).show()
            }

            parentFragmentManager.popBackStack()
        } else {
            Toast.makeText(requireContext(), "Пополнете ги сите задолжителни полиња", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(requireContext(), "Нотификациите се овозможени!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Дозволете нотификации за да добивате потсетници", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}