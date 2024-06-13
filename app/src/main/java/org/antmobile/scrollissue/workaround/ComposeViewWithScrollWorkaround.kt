package org.antmobile.scrollissue.workaround

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.pointer.util.VelocityTrackerAddPointsFix
import androidx.compose.ui.platform.AbstractComposeView

@OptIn(ExperimentalComposeUiApi::class)
internal class ComposeViewWithScrollWorkaround @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AbstractComposeView(context, attrs, defStyleAttr) {

    /**
     * Should be in sync with VelocityTracker.AssumePointerMoveStoppedMilliseconds.
     */
    private val assumePointerMoveStoppedMilliseconds = 40L
    private val assumePointerMoveStoppedUpperBoundMilliseconds = 60L
    private var lastMoveEventTime: Long = 0

    //region ComposeView copy-paste

    private val content = mutableStateOf<(@Composable () -> Unit)?>(null)

    @Suppress("RedundantVisibilityModifier")
    protected override var shouldCreateCompositionOnAttachedToWindow: Boolean = false
        private set

    init {
        VelocityTrackerAddPointsFix = true
    }

    @Composable
    override fun Content() {
        content.value?.invoke()
    }

    override fun getAccessibilityClassName(): CharSequence {
        return javaClass.name
    }

    /**
     * Set the Jetpack Compose UI content for this view.
     * Initial composition will occur when the view becomes attached to a window or when
     * [createComposition] is called, whichever comes first.
     */
    fun setContent(content: @Composable () -> Unit) {
        shouldCreateCompositionOnAttachedToWindow = true
        this.content.value = content
        if (isAttachedToWindow) {
            createComposition()
        }
    }
    //endregion

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        return when (ev.action) {
            MotionEvent.ACTION_MOVE -> {
                lastMoveEventTime = ev.eventTime
                super.dispatchTouchEvent(ev)
            }

            MotionEvent.ACTION_UP -> {
                val moveEventTime = lastMoveEventTime
                lastMoveEventTime = 0
                if (ev.isRequireTimeOverride(moveEventTime)) {
                    val overridenMotionEvent = ev.copy(
                        eventTime = ev.calculateActionUpEventTime(moveEventTime)
                    )
                    super.dispatchTouchEvent(overridenMotionEvent).also {
                        overridenMotionEvent.recycle()
                    }
                } else {
                    super.dispatchTouchEvent(ev)
                }
            }

            else -> super.dispatchTouchEvent(ev)
        }
    }

    private fun MotionEvent.calculateActionUpEventTime(
        lastMoveEventTime: Long,
    ): Long {
        val delta = eventTime - lastMoveEventTime
        return lastMoveEventTime + delta.coerceAtMost(assumePointerMoveStoppedMilliseconds)
    }

    private fun MotionEvent.isRequireTimeOverride(lastMoveEventTime: Long): Boolean {
        if (lastMoveEventTime <= 0 || lastMoveEventTime >= eventTime) return false
        val delta = eventTime - lastMoveEventTime
        return delta in assumePointerMoveStoppedMilliseconds..assumePointerMoveStoppedUpperBoundMilliseconds
    }
}

private fun MotionEvent.copy(
    downTime: Long = getDownTime(),
    eventTime: Long = getEventTime(),
    action: Int = getAction(),
    pointerCount: Int = getPointerCount(),
    pointerProperties: Array<MotionEvent.PointerProperties>? = (0 until getPointerCount()).map { index ->
        MotionEvent.PointerProperties().also { pointerProperties ->
            getPointerProperties(index, pointerProperties)
        }
    }.toTypedArray(),
    pointerCoords: Array<MotionEvent.PointerCoords>? = (0 until getPointerCount()).map { index ->
        MotionEvent.PointerCoords().also { pointerCoords ->
            getPointerCoords(index, pointerCoords)
        }
    }.toTypedArray(),
    metaState: Int = getMetaState(),
    buttonState: Int = getButtonState(),
    xPrecision: Float = getXPrecision(),
    yPrecision: Float = getYPrecision(),
    deviceId: Int = getDeviceId(),
    edgeFlags: Int = getEdgeFlags(),
    source: Int = getSource(),
    flags: Int = getFlags()
): MotionEvent = MotionEvent.obtain(
    downTime,
    eventTime,
    action,
    pointerCount,
    pointerProperties,
    pointerCoords,
    metaState,
    buttonState,
    xPrecision,
    yPrecision,
    deviceId,
    edgeFlags,
    source,
    flags
)
