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
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import hu.ait.tovisitmapapp.R
import hu.ait.tovisitmapapp.data.ToVisitCategory
import hu.ait.tovisitmapapp.data.ToVisitItem
import hu.ait.tovisitmapapp.ui.theme.GoodBlue
import hu.ait.tovisitmapapp.ui.theme.GoodRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToVisitListScreen(
    toVisitListViewModel: ToVisitListViewModel = hiltViewModel(),
    onNavigateToMap: () -> Unit,
    name: String = ""
) {
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current

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

    val robotoFont = FontFamily(
        Font(R.font.roboto, FontWeight.Light)
    )



    Column{
        Column {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.the_list), fontFamily = robotoFont, fontWeight = FontWeight.Bold)
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
                Text(text = stringResource(R.string.no_places_added_go_to_the_map_and_find_places_you_want_to_go))
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
                label = { Text(text = stringResource(R.string.enter_name_of_place_to_visit)) },
                trailingIcon = {
                    if (nameError) {
                        Icon(
                            Icons.Filled.Warning, stringResource(R.string.error),
                            tint = MaterialTheme.colorScheme.error)
                    }
                }
            )

            if (nameError) {
                Text(
                    text = stringResource(R.string.name_cannot_be_empty),
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
                label = { Text(text = stringResource(R.string.enter_description_of_place_here)) }
            )

            Spacer(modifier = Modifier.height(10.dp))
            Text(text = stringResource(R.string.priority))
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

            SpinnerSample(
                list = listOf(
                    stringResource(R.string.dining),
                    stringResource(R.string.study),
                    stringResource(R.string.entertainment),
                    stringResource(R.string.other)
                ),
                preselected =
                when (toVisitItemCategory)
                    {
                        ToVisitCategory.DINING -> stringResource(R.string.dining)
                        ToVisitCategory.STUDY -> stringResource(R.string.study)
                        ToVisitCategory.ENTERTAINMENT -> stringResource(R.string.entertainment)
                        else -> stringResource(R.string.other)
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
                Text(text = stringResource(R.string.visited))
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
                    Text(text = stringResource(R.string.save))
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
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ),
        modifier = Modifier.padding(5.dp)
    ) {
        var expanded by rememberSaveable {
            mutableStateOf(false)
        }

        val robotoFont = FontFamily(
            Font(R.font.roboto, FontWeight.Light)
        )

        Column(modifier = Modifier
            .padding(10.dp)
            .animateContentSize()) {
            Row(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = if (toVisitItem.priority < 0.25f) "🧍"
                    else if (toVisitItem.priority > 0.75f) "🏃"
                    else "🚶",
                    fontSize = 30.sp)
                Image(
                    painter = painterResource(id = toVisitItem.category.getIcon()),
                    contentDescription = stringResource(R.string.category),
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 10.dp)
                )
                Text(toVisitItem.name, modifier = Modifier.fillMaxWidth(0.5f))
                Spacer(modifier = Modifier.fillMaxSize(0.05f))
                Checkbox(
                    checked = toVisitItem.haveVisited,
                    onCheckedChange = { onToVisitItemCheckChange(it) }
                )
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = stringResource(R.string.delete),
                    modifier = Modifier.clickable {
                        onRemoveItem()
                    },
                    tint = GoodRed
                )
                Spacer(modifier = Modifier.fillMaxSize(0.05f))
                Icon(
                    imageVector = Icons.Filled.Build,
                    contentDescription = stringResource(R.string.edit),
                    modifier = Modifier.clickable {
                        onEditItem(toVisitItem)
                    },
                    tint = GoodBlue
                )
                Spacer(modifier = Modifier.fillMaxSize(0.05f))
                Icon(
                    imageVector = if (expanded)
                        Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = if (expanded) {
                        stringResource(R.string.less)
                    } else {
                        stringResource(R.string.more)
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
                        fontStyle = FontStyle.Italic
                    )
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = stringResource(R.string.address, toVisitItem.address),
                    style = TextStyle(
                        fontSize = 12.sp,
                    )
                )
                Text(
                    text = stringResource(
                        R.string.co_ords,
                        toVisitItem.latitude,
                        toVisitItem.longitude
                    ),
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