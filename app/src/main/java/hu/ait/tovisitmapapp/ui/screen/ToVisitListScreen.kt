package hu.ait.tovisitmapapp.ui.screen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import hu.ait.tovisitmapapp.data.ToVisitCategory
import hu.ait.tovisitmapapp.data.ToVisitItem
//import com.wajahatkarim.flippable.FlipAnimationType
//import com.wajahatkarim.flippable.Flippable
//import com.wajahatkarim.flippable.FlippableController
//import com.wajahatkarim.flippable_demo.ui.theme.FlippableDemoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToVisitListScreen(
    toVisitListViewModel: ToVisitListViewModel = hiltViewModel(),
    onNavigateToMap: () -> Unit,
    name: String = ""
) {
    val coroutineScope = rememberCoroutineScope()

    val toVisitList by
    if (name == "") {
        toVisitListViewModel.getAllToVisitList().collectAsState(emptyList())
    } else {
        toVisitListViewModel.getToVisitItemsLike(name).collectAsState(emptyList())
    }

    var showAddToVisitDialog by rememberSaveable {
        mutableStateOf(false)
    }

    var toVisitItemToEdit: ToVisitItem? by rememberSaveable {
        mutableStateOf(null)
    }

    Column{
        Column {
            TopAppBar(
                title = {
                    Text("To Visit List")
                },
                actions = {
                    IconButton(onClick = {
                        toVisitListViewModel.clearAllToVisitItems()
                    }) {
                        Icon(Icons.Filled.Delete, null)
                    }
                    IconButton(onClick = {
                        onNavigateToMap()
                    }) {
                        Icon(Icons.Filled.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            )
        }

        Column(modifier = Modifier.padding(10.dp)) {
            if (showAddToVisitDialog) {
                AddNewToVisitItemForm(toVisitListViewModel, { showAddToVisitDialog = false }, toVisitItemToEdit)
            }

            if (toVisitList.isEmpty()) {
                Text(text = "No Items")
            } else {

                LazyColumn(modifier = Modifier.fillMaxHeight()) {
                    items(toVisitList) {
                        ToVisitItemCard(toVisitItem = it,
                            onRemoveItem = {toVisitListViewModel.removeToVisitItem(it)},
                            onToVisitItemCheckChange = { checkState ->
                                toVisitListViewModel.changeToVisitState(it, checkState)
                            },
                            onEditItem = {
                                    editedToVisitItem ->
                                toVisitItemToEdit = editedToVisitItem
                                showAddToVisitDialog = true
                            }
                        )
                    }
                }
            }
        }
    }
}




@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun AddNewToVisitItemForm(
    toVisitListViewModel: ToVisitListViewModel,
    onDialogDismiss: () -> Unit = {},
    toVisitItemToEdit: ToVisitItem? = null
) {
    Dialog(
        onDismissRequest = onDialogDismiss
    ) {
        var toVisitItemName by rememberSaveable {
            mutableStateOf(toVisitItemToEdit?.name ?: "")
        }

        var toVisitItemDescription by rememberSaveable {
            mutableStateOf(toVisitItemToEdit?.description ?: "")
        }

        var toVisitItemCategory by rememberSaveable {
            mutableStateOf(toVisitItemToEdit?.category ?: ToVisitCategory.DINING)
        }

        var toVisitItemVisited by rememberSaveable {
            mutableStateOf(toVisitItemToEdit?.haveVisited ?: false
            )
        }

        var toVisitItemAddress by rememberSaveable {
            mutableStateOf(toVisitItemToEdit?.address ?: "")
        }

        var toVisitItemLatitude by rememberSaveable {
            mutableDoubleStateOf(toVisitItemToEdit?.latitude ?: 0.0)
        }

        var toVisitItemLongitude by rememberSaveable {
            mutableDoubleStateOf(toVisitItemToEdit?.longitude ?: 0.0)
        }

        var toVisitItemPriority by remember {
            mutableFloatStateOf(toVisitItemToEdit?.priority ?: 0.5f)
        }

        var nameError by rememberSaveable {mutableStateOf(false)}

        Column(
            modifier = Modifier
                .padding(10.dp)
                .background(
                    MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(10.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = toVisitItemName,
                onValueChange = {
                    toVisitItemName = it
                    nameError = toVisitItemName == ""
                },
                label = { Text(text = "Enter name of place to visit") },
                trailingIcon = {
                    if (nameError) {
                        Icon(
                            Icons.Filled.Warning, "Error",
                            tint = MaterialTheme.colorScheme.error)
                    }
                }
            )

            if (nameError) {
                Text(
                    text = "Name cannot be empty.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = toVisitItemDescription,
                onValueChange = {
                    toVisitItemDescription = it
                },
                label = { Text(text = "Enter description of place here.") }
            )

            Slider(
                value = toVisitItemPriority,
                onValueChange = { toVisitItemPriority = it }
            )
            Row(horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()) {
                Text(text = "🧍",
                    fontSize = 30.sp)
                Text(text = "🚶",
                    fontSize = 30.sp)
                Text(text = "🏃",
                    fontSize = 30.sp)
            }
            Text(text = toVisitItemPriority.toString())

            SpinnerSample(
                listOf("Dining",
                    "Study", "Entertainment", "Other"
                ),
                preselected =
                when (toVisitItemCategory)
                    {
                        ToVisitCategory.DINING -> "Dining"
                        ToVisitCategory.STUDY -> "Study"
                        ToVisitCategory.ENTERTAINMENT -> "Entertainment"
                        else -> "Other"
                    },
                onSelectionChanged = {
                    toVisitItemCategory =
                        when (it) {
                            "Dining" -> ToVisitCategory.DINING
                            "Study" -> ToVisitCategory.STUDY
                            "Entertainment" -> ToVisitCategory.ENTERTAINMENT
                            else -> ToVisitCategory.OTHER
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = toVisitItemVisited, onCheckedChange = { toVisitItemVisited = it })
                Text(text = "Visited")
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = {
                    if (toVisitItemName == "") {
                        nameError = true
                    }
                    else if (toVisitItemToEdit == null) {
                        toVisitListViewModel.addToVisitItem(
                            ToVisitItem(
                                0,
                                toVisitItemName,
                                toVisitItemDescription,
                                toVisitItemPriority,
                                toVisitItemCategory,
                                toVisitItemVisited,
                                toVisitItemAddress,
                                toVisitItemLatitude,
                                toVisitItemLongitude
                            )
                        )
                        onDialogDismiss()
                    } else {
                        var toVisitItemEdited = toVisitItemToEdit.copy(
                            name = toVisitItemName,
                            description = toVisitItemDescription,
                            priority = toVisitItemPriority,
                            category = toVisitItemCategory,
                            haveVisited = toVisitItemVisited,
                            address = toVisitItemAddress,
                            latitude = toVisitItemLatitude,
                            longitude = toVisitItemLongitude
                        )
                        toVisitListViewModel.editToVisitItem(toVisitItemEdited)
                        onDialogDismiss()
                    }
                }) {
                    Text(text = "Save")
                }
            }
        }
    }
}

@Composable
fun ToVisitItemCard(
    toVisitItem: ToVisitItem,
    onToVisitItemCheckChange: (Boolean) -> Unit = {},
    onRemoveItem: () -> Unit = {},
    onEditItem: (ToVisitItem) -> Unit = {}
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation( // shading / shadow behind card
            defaultElevation = 10.dp
        ),
        modifier = Modifier.padding(5.dp)
    ) {
        var expanded by rememberSaveable {
            mutableStateOf(false)
        }

        Column(modifier = Modifier
            .padding(10.dp)
            .animateContentSize()) {
            Row(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = if (toVisitItem.priority < 0.333f) "🧍"
                    else if (toVisitItem.priority > 0.667f) "🏃"
                    else "🚶",
                    fontSize = 30.sp)
                Image(
                    painter = painterResource(id = toVisitItem.category.getIcon()), // called using int id
                    contentDescription = "Category",
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 10.dp)
                )
//                Column {
                Text(toVisitItem.name, modifier = Modifier.fillMaxWidth(0.5f))
//                    Text("Priority: ${toVisitItem.priority}", modifier = Modifier.fillMaxWidth(0.4f))
//                }
                Spacer(modifier = Modifier.fillMaxSize(0.05f))
                Checkbox(
                    checked = toVisitItem.haveVisited,
                    onCheckedChange = { onToVisitItemCheckChange(it) }
                )
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete",
                    modifier = Modifier.clickable {
                        onRemoveItem()
                    },
                    tint = Color.Red
                )
                Spacer(modifier = Modifier.fillMaxSize(0.05f))
                Icon(
                    imageVector = Icons.Filled.Build,
                    contentDescription = "Edit",
                    modifier = Modifier.clickable {
                        onEditItem(toVisitItem)
                    },
                    tint = Color.Blue
                )
                Spacer(modifier = Modifier.fillMaxSize(0.05f))
                Icon(
                    imageVector = if (expanded)
                        Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = if (expanded) {
                        "Less"
                    } else {
                        "More"
                    },
                    modifier = Modifier.clickable {
                        expanded = !expanded
                    }
                )
            }

            if (expanded) {
                Text(
                    text = toVisitItem.description,
                    style = TextStyle(
                        fontSize = 12.sp,
                    )
                )
                Text(
                    text = "Address: ${toVisitItem.address}",
                    style = TextStyle(
                        fontSize = 12.sp,
                    )
                )
                Text(
                    text = "Co-ords: ${toVisitItem.latitude}, ${toVisitItem.longitude}",
                    style = TextStyle(
                        fontSize = 12.sp,
                    )
                )
            }
        }
    }
}

@Composable
fun SpinnerSample(
    list: List<String>,
    preselected: String,
    onSelectionChanged: (myData: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selected by remember { mutableStateOf(preselected) }
    var expanded by remember { mutableStateOf(false) } // initial value
    OutlinedCard(
        modifier = modifier.clickable {
            expanded = !expanded
        }
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Text(
                text = selected,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
            Icon(Icons.Outlined.ArrowDropDown, null, modifier =
            Modifier.padding(8.dp))
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                list.forEach { listEntry ->
                    DropdownMenuItem(
                        onClick = {
                            selected = listEntry
                            expanded = false
                            onSelectionChanged(selected)
                        },
                        text = {
                            Text(
                                text = listEntry,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.Start)
                            )
                        },
                    )
                }
            }
        }
    }
}