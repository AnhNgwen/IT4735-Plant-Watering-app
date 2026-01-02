package com.example.plantwatering.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.plantwatering.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginButton.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToMainScreenFragment()
            this.findNavController().navigate(action)
        }

        binding.registerTv.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            this.findNavController().navigate(action)
        }

        binding.forgetPasswordTv.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToForgetPasswordFragment()
            this.findNavController().navigate(action)
        }

        binding.btnGoogle.setOnClickListener {
            Toast.makeText(requireContext(), "Đang mở Google Login", Toast.LENGTH_SHORT).show()
        }

        binding.btnBack.setOnClickListener {
            activity?.finish()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

}