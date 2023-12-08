package hu.ait.tovisitmapapp.ui.screen

import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import hu.ait.tovisitmapapp.data.ToVisitCategory
import hu.ait.tovisitmapapp.data.ToVisitItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToVisitListScreen(
    modifier: Modifier = Modifier,
    toVisitListViewModel: ToVisitListViewModel = hiltViewModel(),
    onNavigateToMap: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    val toVisitList by toVisitListViewModel.getAllToVisitList().collectAsState(emptyList())

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
//                    IconButton(onClick = {
//                        coroutineScope.launch {
//                            val foodItems = toVisitListViewModel.getFoodItemsNum()
//                            val electronicsItems = toVisitListViewModel.getElectronicsItemsNum()
//                            val bookItems = toVisitListViewModel.getBookItemsNum()
//                            onNavigateToSummary(
//                                foodItems, electronicsItems, bookItems
//                            )
//                        }
//                    }) {
//                        Icon(Icons.Filled.Info, null)
//                    }
                    IconButton(onClick = {
                        toVisitItemToEdit = null
                        showAddToVisitDialog = true
                    }) {
                        Icon(Icons.Filled.AddCircle, null)
                    }
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = ("to map"),
                        modifier = Modifier.clickable {
                            onNavigateToMap()
                        }
                    )
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ))
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

        var toVisitItemPriority by rememberSaveable {
            mutableStateOf(toVisitItemToEdit?.priority ?: "")
        }

        var toVisitItemCategory by rememberSaveable {
            mutableStateOf(toVisitItemToEdit?.category ?: ToVisitCategory.DINING)
        }

        var toVisitItemVisited by rememberSaveable {
            mutableStateOf(toVisitItemToEdit?.haveVisited ?: false
            )
        }

        var errorText by rememberSaveable {mutableStateOf("")}
        var nameError by rememberSaveable {mutableStateOf(false)}
        var priorityError by rememberSaveable {mutableStateOf(false)}

//        fun validatePrice() {
//            try {
//                val floatPrice = toVisitItemPriority.toFloat()
//                priorityError = floatPrice < 0f
//                if (priorityError) {
//                    errorText = "Please enter a positive number."
//                }
//            } catch (e: Exception) {
//                priorityError = true
//                errorText = "Please enter a valid number."
//            }
//        }

//        fun adjustPrice() {
//            var floatPrice = toVisitItemPriority.toFloat()
//            toVisitItemPriority = floatPrice.toString()
//        }

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

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = toVisitItemPriority,
                onValueChange = {
                    toVisitItemPriority = it
//                    validatePrice()
                },
                label = { Text(text = "Enter priority of place here") },//TODO: maybe turn into a spinner?
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                trailingIcon = {
                    if (priorityError) {
                        Icon(
                            Icons.Filled.Warning, "Error",
                            tint = MaterialTheme.colorScheme.error)
                    }
                }
            )

            if (priorityError) {
                Text(
                    text = errorText,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            SpinnerSample(
                listOf("Dining",
                    "Entertainment", "Museum", "Nature"
                ),
                preselected = "Dining",
                onSelectionChanged = {
                    toVisitItemCategory = ToVisitCategory.DINING
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
                    if (toVisitItemName == "" || toVisitItemPriority == "" || priorityError) {
                        if (toVisitItemName == "") {
                            nameError = true
                        }
                        if (toVisitItemPriority == "") {
                            priorityError = true
                            errorText = "Price cannot be empty."
                        }
                    }
                    else if (toVisitItemToEdit == null) {
//                        adjustPrice()
                        toVisitListViewModel.addToVisitItem(
                            ToVisitItem(
                                0,
                                toVisitItemName,
                                toVisitItemDescription,
                                toVisitItemPriority,
                                toVisitItemCategory,
                                toVisitItemVisited
                            )
                        )
                        onDialogDismiss()
                    } else {
//                        adjustPrice()
                        var toVisitItemEdited = toVisitItemToEdit.copy(
                            name = toVisitItemName,
                            description = toVisitItemDescription,
                            priority = toVisitItemPriority,
                            category = toVisitItemCategory,
                            haveVisited = toVisitItemVisited
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
            .padding(20.dp)
            .animateContentSize()) {
            Row(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
//                Image(
//                    painter = painterResource(id = toVisitItem.category.getIcon()), // called using int id
//                    contentDescription = stringResource(R.string.priority),
//                    modifier = Modifier
//                        .size(40.dp)
//                        .padding(end = 10.dp)
//                )
                Column {
                    Text(toVisitItem.name, modifier = Modifier.fillMaxWidth(0.4f))
                    Text(toVisitItem.priority, modifier = Modifier.fillMaxWidth(0.4f))
                }
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