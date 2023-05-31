package com.capstone.Capstone2Project.utils.etc

import android.app.Person
import android.graphics.BitmapFactory
import android.util.Log
import android.view.DragAndDropPermissions
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.capstone.Capstone2Project.utils.extensions.generateRandomText
import com.capstone.Capstone2Project.utils.theme.mustard_yellow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonNull.content
import kotlin.math.roundToInt

data class PersonUiItem(
    val name: String,
    val id: String,
    val backgroundColor: Color
)

val LocalDragTargetInfo = compositionLocalOf {
    DragTargetInfo()
}

@Composable
private fun <T> DropItem(
    modifier: Modifier,
    content: @Composable (BoxScope.(isInBound: Boolean, data: T?) -> Unit)
) {
    val dragInfo = LocalDragTargetInfo.current
    val dragPosition = dragInfo.dragPosition
    val dragOffset = dragInfo.dragOffset
    var isCurrentDropTarget by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = modifier
            .onGloballyPositioned {
                /*
                노션에 LayoutCoordinates 페이지에 정리해둠
                Window(상태바포함)로 부터 잰 바운드
                 */
                it.boundsInWindow().let { rect ->
                    /*
                    rect 안에 offset이 포함이 되는지를 체크하는거임
                     */
                    isCurrentDropTarget = rect.contains(dragPosition + dragOffset)
                }
            }
    ) {
        val data =
            if (isCurrentDropTarget && !dragInfo.isDragging) dragInfo.dataToDrop as T? else null

        content(isCurrentDropTarget, data)
    }


}

@Composable
private fun DraggableScreen(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val state = remember {
        DragTargetInfo()
    }
    CompositionLocalProvider(
        LocalDragTargetInfo provides state
    ) {

        Box(
            modifier = modifier.fillMaxSize()
        ) {
            content()
            if (state.isDragging) {
                var targetSize by remember {
                    mutableStateOf(IntSize.Zero)
                }

                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            val offset = (state.dragPosition + state.dragOffset)
                            scaleX = 1.3f
                            scaleY = 1.3f
                            alpha = if (targetSize == IntSize.Zero) 0f else 0.9f
                            translationX = offset.x.minus(targetSize.width / 2)
                            translationY = offset.y.minus(targetSize.height / 2)
                        }
                        .onGloballyPositioned {
                            targetSize = it.size
                        }
                ) {
                    state.draggableComposable?.invoke()
                }
            }
        }
    }


}

@Composable
private fun <T> DragTarget(
    modifier: Modifier = Modifier,
    dataToDrop: T,
    viewModel: DragViewModel,
    content: @Composable () -> Unit
) {
    var currentPosition by remember {
        mutableStateOf(Offset.Zero)
    }
    val currentState = LocalDragTargetInfo.current

    Box(
        modifier = modifier
            .onGloballyPositioned {
                /*
                전역 위치가 변경될 경우 호출됨
                Compose 후에 호출 됨
                레이아웃 좌표에 대한 정보를 알 수 있음
                 */
                /*
                localToWindow 는 상대적인 로컬 포지션을 윈도우 포지션으로 바꿔줌
                 */
                currentPosition = it.localToWindow(Offset.Zero)
            }
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDragStart = {

                        viewModel.startDragging()
                        currentState.dataToDrop = dataToDrop
                        currentState.isDragging = true
                        currentState.dragPosition = currentPosition + it
                        currentState.draggableComposable = content

                    },
                    onDrag = { change, dragAmount ->
                        /*
                        consume()은 부모가 이벤트를 소비하지 못하도록 함
                         */
                        change.consume()
                        currentState.dragOffset += Offset(
                            dragAmount.x,
                            dragAmount.y
                        )
                    },
                    onDragEnd = {
                        viewModel.stopDragging()
                        currentState.dragOffset = Offset.Zero
                        currentState.isDragging = false
                    },
                    onDragCancel = {
                        viewModel.stopDragging()
                        currentState.dragOffset = Offset.Zero
                        currentState.isDragging = false
                    }
                )
            }
    ) {
        content()
    }

}

class DragTargetInfo {
    var isDragging: Boolean by mutableStateOf(false)
    var dragPosition: Offset by mutableStateOf(Offset.Zero)
    var dragOffset: Offset by mutableStateOf(Offset.Zero)
    var draggableComposable: (@Composable () -> Unit)? by mutableStateOf(null)
    var dataToDrop: Any? by mutableStateOf(null)


}


@Preview(showBackground = true)
@Composable
private fun DragAndDropPreview() {

    val viewModel = viewModel<DragViewModel>()

    DraggableScreen {
        DragAndDropScreen(viewModel = viewModel)
    }

}

@Composable
private fun DragAndDropScreen(
    viewModel: DragViewModel
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(50.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            viewModel.items.forEach { personUiItem ->
                DragTarget(
                    dataToDrop = personUiItem,
                    viewModel = viewModel
                ) {
                    Box(
                        modifier = Modifier
                            .size(Dp(screenWidth / 5f))
                            .clip(RoundedCornerShape(15.dp))
                            .shadow(5.dp, RoundedCornerShape(15.dp))
                            .background(personUiItem.backgroundColor, RoundedCornerShape(15.dp)),
                        contentAlignment = Alignment.Center

                    ) {
                        Text(
                            text = personUiItem.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            viewModel.isCurrentlyDragging,
            enter = slideInHorizontally {
                it
            },

            ) {
            DropItem<PersonUiItem>(
                modifier = Modifier.size(Dp(screenWidth / 3.5f))
            ) { isInBound: Boolean, data: PersonUiItem? ->

                if (data != null) {
                    LaunchedEffect(data) {
                        viewModel.addPerson(data)
                    }
                }

                if (isInBound) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(1.dp, color = White, shape = RoundedCornerShape(15.dp))
                            .background(
                                Color.Black.copy(
                                    alpha = 0.6f
                                ),
                                RoundedCornerShape(15.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Add Person",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Black
                        )
                    }
                }
            }

        }
    }

}

class DragViewModel : ViewModel() {
    var isCurrentlyDragging by mutableStateOf(false)
        private set
    var items by mutableStateOf(emptyList<PersonUiItem>())
        private set

    var addedPersons = mutableStateListOf<PersonUiItem>()
        private set

    init {
        items = listOf(
            PersonUiItem(
                "Michael", "1", Color.Gray
            ),
            PersonUiItem(
                "Larissa", "2", Color.Red
            ),
            PersonUiItem(
                "Marc", "2", Color.Green
            )

        )
    }

    fun startDragging() {
        isCurrentlyDragging = true
    }

    fun stopDragging() {
        isCurrentlyDragging = false
    }

    fun addPerson(personUiItem: PersonUiItem) {
        addedPersons.add(personUiItem)
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {

    val selected = remember {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        SelectedItem(
            modifier = Modifier
        )
    }

}

@Composable
private fun SelectedItem(
    modifier: Modifier = Modifier
) {

    val focusState = remember {
        mutableStateOf(false)
    }


    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        Box(
            modifier = modifier
                .clickable {
                    focusState.value = !focusState.value
                }
                .padding(16.dp)
                .drawWithContent {
                    drawContent()
                    if (focusState.value) {
                        drawRect(
                            color = White,
                            style = Stroke(
                                width = 4f,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            ),
                            topLeft = Offset(x = -10f, y = -10f),
                            size = Size(
                                this.size.width + 20f,
                                this.size.height + 20f
                            )
                        )
                        drawRect(
                            color = mustard_yellow,
                            style = Stroke(
                                width = 2f,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            ),
                            topLeft = Offset(x = -10f, y = -10f),
                            size = Size(
                                this.size.width + 20f,
                                this.size.height + 20f
                            )
                        )
                    }
                }
        ) {
            Text(generateRandomText(20))
        }
    }


}

@Composable
private fun AnimatedItem(
    modifier: Modifier,
    selected: Boolean,
    onClicked: () -> Unit
) {

    val scaleState = remember {
        Animatable(initialValue = 1f)
    }

    LaunchedEffect(selected) {
        if (selected) {
            launch {
                scaleState.animateTo(
                    targetValue = 0.8f,
                    animationSpec = tween(
                        durationMillis = 50
                    )
                )
                scaleState.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioHighBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }
        }
    }

    Column(
        modifier = modifier
            .clip(
                shape = MaterialTheme.shapes.medium
            )
            .clickable { onClicked() }
            .scale(scaleState.value)
            .border(
                width = 1.dp,
                color = Color.Black,
                shape = MaterialTheme.shapes.medium
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "자막 예시 텍스트",
            color = Color.Black
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Preview(showBackground = true)
@Composable
private fun AnimateScreen() {

    val coroutine = rememberCoroutineScope()

    val offsetState = remember {
        mutableStateOf(Offset.Zero)
    }



    val dragDelta = remember {
        mutableStateOf("")
    }

    val dragStartPosition = remember {
        mutableStateOf("")
    }

    val dragStopVelocity = remember {
        mutableStateOf("")
    }

    val draggableState = rememberDraggableState(onDelta = { delta: Float ->
        dragDelta.value = delta.toString()
    })

    val draggableModifier = Modifier.draggable(
        state = draggableState,
        orientation = Orientation.Vertical,
        onDragStarted = { startedPosition: Offset ->
            coroutine.launch {
                dragStartPosition.value = startedPosition.toString()
            }
        },
        onDragStopped = {
            coroutine.launch {
                dragStopVelocity.value = it.toString()
            }
        }
    )

    val scrollState = rememberScrollableState(
        consumeScrollDelta = { delta: Float ->
            0f
        }
    )



    Column(
        modifier = draggableModifier
            .fillMaxSize()
            .background(
                color = Color.White
            )
            .drawWithContent {
                drawContent()
                drawCircle(
                    color = Color.LightGray,
                    radius = 10f,
                    center = offsetState.value
                )
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("${offsetState.value}")
        Text("${dragDelta.value}, ${dragStartPosition.value}, ${dragStopVelocity.value}")
    }
}

