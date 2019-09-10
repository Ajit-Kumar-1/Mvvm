package com.example.mvvm

import com.example.mvvm.viewModel.ProfileViewModel
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class FirstUnitTest {

    @Mock
    private lateinit var viewModel: ProfileViewModel

    @Test
    fun firstTest(){}
}