package com.example.cryptoexchange.presentation.ui.screens

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cryptoexchange.presentation.ui.components.CryptoDetailContent
import com.example.cryptoexchange.presentation.ui.components.CryptoListContent
import com.example.cryptoexchange.presentation.viewmodel.CryptoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CryptoExchangeScreen(
    viewModel: CryptoViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    // У портретному режимі системна кнопка «Назад» закриває деталі й повертає до списку
    if (!isLandscape && uiState.isDetailOpen) {
        BackHandler(onBack = viewModel::onCloseDetail)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Біржа криптовалют",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        if (isLandscape) {
            // Горизонтальний режим
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                CryptoListContent(
                    uiState = uiState,
                    listState = listState,
                    onCryptoClick = viewModel::onCryptoSelect,
                    onRefreshClick = viewModel::onRefreshClick,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )

                VerticalDivider()

                CryptoDetailContent(
                    crypto = uiState.selectedCrypto,
                    showBackButton = false,
                    onBackClick = {},
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }
        } else {
            // Портретний режим: або список, або деталі обраної монети
            if (uiState.isDetailOpen && uiState.selectedCrypto != null) {
                CryptoDetailContent(
                    crypto = uiState.selectedCrypto,
                    showBackButton = true,
                    onBackClick = viewModel::onCloseDetail,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            } else {
                CryptoListContent(
                    uiState = uiState,
                    listState = listState,
                    onCryptoClick = viewModel::onCryptoSelect,
                    onRefreshClick = viewModel::onRefreshClick,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
        }
    }
}