package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.BackHandler
import com.example.ui.theme.BgCharcoal
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextSilver
import com.example.ui.theme.TextWhite

data class CountryDialingItem(
    val name: String,
    val code: String
)

@Composable
fun CountrySelectionScreen(
    currentLang: String = "English",
    onCountrySelected: (String, String) -> Unit, // (CountryName, CountryCode)
    onBack: () -> Unit = {}
) {
    BackHandler(enabled = true) { onBack() }
    val countries = remember {
        listOf(
            CountryDialingItem("India", "+91"),
            CountryDialingItem("Japan", "+81"),
            CountryDialingItem("United States", "+1"),
            CountryDialingItem("United Kingdom", "+44"),
            CountryDialingItem("Canada", "+1"),
            CountryDialingItem("Australia", "+61"),
            CountryDialingItem("Nepal", "+977"),
            CountryDialingItem("China", "+86"),
            CountryDialingItem("Germany", "+49"),
            CountryDialingItem("France", "+33"),
            CountryDialingItem("Brazil", "+55"),
            CountryDialingItem("South Korea", "+82"),
            CountryDialingItem("Italy", "+39"),
            CountryDialingItem("Spain", "+34"),
            CountryDialingItem("Mexico", "+52"),
            CountryDialingItem("Russia", "+7"),
            CountryDialingItem("South Africa", "+27"),
            CountryDialingItem("Singapore", "+65"),
            CountryDialingItem("United Arab Emirates", "+971"),
            CountryDialingItem("New Zealand", "+64"),
            CountryDialingItem("Switzerland", "+41")
        )
    }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCountry by remember { mutableStateOf<CountryDialingItem?>(null) }

    val filteredCountries = countries.filter {
        it.name.contains(searchQuery, ignoreCase = true) || it.code.contains(searchQuery)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgCharcoal)
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Text(
                text = Strings.get("select_country_title", currentLang),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Text(
                text = Strings.get("select_country_desc", currentLang),
                fontSize = 13.sp,
                color = TextSilver,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Prominent Search Bar at top
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text(Strings.get("search_country_placeholder", currentLang), color = TextSilver) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSilver) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TextWhite,
                    unfocusedBorderColor = TextSilver.copy(alpha = 0.4f),
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedContainerColor = SurfaceDark,
                    unfocusedContainerColor = SurfaceDark
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                singleLine = true
            )

            // Scrollable List of Countries Worldwide
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredCountries) { country ->
                    val isSelected = selectedCountry?.name == country.name
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) SurfaceDark else SurfaceDark.copy(alpha = 0.7f))
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) TextWhite else Color.White.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { selectedCountry = country }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = country.name,
                            color = TextWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = BgCharcoal,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = country.code,
                                    color = TextWhite,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            if (isSelected) {
                                Spacer(modifier = Modifier.width(12.dp))
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TextWhite, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }

            // Proceed Button
            if (selectedCountry != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        onCountrySelected(selectedCountry!!.name, selectedCountry!!.code)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TextWhite),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = "${Strings.get("proceed_lang_selection", currentLang)} (${selectedCountry!!.name} - ${selectedCountry!!.code})",
                        color = BgCharcoal,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}
