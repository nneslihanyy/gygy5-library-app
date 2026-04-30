package com.turkcell.libraryapp.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.turkcell.libraryapp.ui.viewmodel.AuthViewModel
import com.turkcell.libraryapp.ui.viewmodel.BookViewModel


@Composable
fun HomeScreen (authViewModel: AuthViewModel,
                bookViewModel: BookViewModel
) {

    val profileState by authViewModel.profile.collectAsState()
    val books by bookViewModel.books.collectAsState();
    val isLoading by bookViewModel.isLoading.collectAsState();

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isLoading)
        {
            CircularProgressIndicator(modifier=Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onPrimary)
        }else{
            Text("Yüklendi ${books.count()}")
        }

    }
}