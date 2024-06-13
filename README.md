# ComposeScrollIssueOnZebraDevices

Demo project to show scrolling issues on Zebra devices like MC18 and PS20.
This issue is reproducible on Android 5, 9, and 11.

## Issue

When scrolling a list, the Velocity of the scroll is not calculated correctly.
In short, `onPreFling` always receives zero available Velocity after I stop dragging.

Here is a video of how it looks
like: https://drive.google.com/file/d/1ywykvopiEvzPfkEcrftyVay4dmqf_FG5/view?usp=sharing



https://github.com/antslava/ComposeScrollIssueOnZebraDevices/assets/506456/d4a84ad8-4a51-4a81-b787-22024b8b608b



## Root cause

On Zebra devices, the delay between `MotionEvent.ACTION_MOVE` and `MotionEvent.ACTION_UP` is
more significant than value defined at `AssumePointerMoveStoppedMilliseconds` in `VelocityTracker`
class. In my case, the delay usually was between 41-60ms.
Because of that, `onPreFling` receives zero available Velocity.

```kotlin
private const val AssumePointerMoveStoppedMilliseconds: Int = 40
```

```kotlin
// Because ACTION_UP is delayed, the sample count value is always 1, which is insufficient for the Lsq2 strategy.  
if (age > HorizonMilliseconds || delta > AssumePointerMoveStoppedMilliseconds) {
    break
}
```

### Logs

```
PointerEvent Initial: PointerInputChange(id=PointerId(value=0), uptimeMillis=21707996, position=Offset(246.5, 568.2), pressed=true, pressure=0.49803925, previousUptimeMillis=21707996, previousPosition=Offset(246.5, 568.2), previousPressed=false, isConsumed=false, type=Touch, historical=[],scrollDelta=Offset(0.0, 0.0))
PointerEvent Initial: PointerInputChange(id=PointerId(value=0), uptimeMillis=21708026, position=Offset(249.4, 512.3), pressed=true, pressure=0.49803925, previousUptimeMillis=21707996, previousPosition=Offset(246.5, 568.2), previousPressed=true, isConsumed=false, type=Touch, historical=[HistoricalChange(uptimeMillis=21708015, position=Offset(247.5, 549.3))],scrollDelta=Offset(0.0, 0.0))
onPreScroll available: Offset(0.0, -43.9) - Drag
onPostScroll available: Offset(0.0, 0.0) - Drag, consumed: Offset(0.0, -43.9)
PointerEvent Initial: PointerInputChange(id=PointerId(value=0), uptimeMillis=21708057, position=Offset(280.4, 342.5), pressed=true, pressure=0.49803925, previousUptimeMillis=21708026, previousPosition=Offset(249.4, 512.3), previousPressed=true, isConsumed=false, type=Touch, historical=[HistoricalChange(uptimeMillis=21708036, position=Offset(254.4, 456.4)), HistoricalChange(uptimeMillis=21708046, position=Offset(265.4, 399.5))],scrollDelta=Offset(0.0, 0.0))
onPreScroll available: Offset(0.0, -169.7) - Drag
onPostScroll available: Offset(0.0, 0.0) - Drag, consumed: Offset(0.0, -169.7)
PointerEvent Initial: PointerInputChange(id=PointerId(value=0), uptimeMillis=21708085, position=Offset(332.3, 210.7), pressed=true, pressure=0.49803925, previousUptimeMillis=21708057, previousPosition=Offset(280.4, 342.5), previousPressed=true, isConsumed=false, type=Touch, historical=[HistoricalChange(uptimeMillis=21708066, position=Offset(303.3, 278.6))],scrollDelta=Offset(0.0, 0.0))
PointerEvent Initial: PointerInputChange(id=PointerId(value=0), uptimeMillis=21708138, position=Offset(332.3, 210.7), pressed=false, pressure=0.49803925, previousUptimeMillis=21708085, previousPosition=Offset(332.3, 210.7), previousPressed=true, isConsumed=false, type=Touch, historical=[],scrollDelta=Offset(0.0, 0.0))
onPreScroll available: Offset(0.0, -131.8) - Drag
onPostScroll available: Offset(0.0, 0.0) - Drag, consumed: Offset(0.0, -131.8)
onPreFling available: (0.0, 0.0) px/sec
onPostFling available: (0.0, 0.0) px/sec, consumed: (0.0, 0.0) px/sec

Breakpoint reached at androidx.compose.ui.input.pointer.util.VelocityTracker1D.calculateVelocity(VelocityTracker.kt:228)
[null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null]
Breakpoint reached at androidx.compose.ui.input.pointer.util.VelocityTracker1D.calculateVelocity(VelocityTracker.kt:228)
[null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null]
```

## Workaround

A workaround to fix this issue is by overriding `MotionEvent.eventTime`.
Please check `ComposeViewWithScrollWorkaround.kt` for more details.

I also have another idea: with a Kotlin Compiler plugin, we can
modify the `AssumePointerMoveStoppedMilliseconds` value at compile time.
But I still need to check it.

Video with
workaround: https://drive.google.com/file/d/1AHR-68z_0B-yvcx4oG9ucYTZxptxNTwm/view?usp=sharing



https://github.com/antslava/ComposeScrollIssueOnZebraDevices/assets/506456/25b0b2af-9e01-4556-bb83-08d51a2393b7



### Logs

```
PointerEvent Initial: PointerInputChange(id=PointerId(value=0), uptimeMillis=21764049, position=Offset(274.4, 594.2), pressed=true, pressure=0.49803925, previousUptimeMillis=21764049, previousPosition=Offset(274.4, 594.2), previousPressed=false, isConsumed=false, type=Touch, historical=[],scrollDelta=Offset(0.0, 0.0))
PointerEvent Initial: PointerInputChange(id=PointerId(value=0), uptimeMillis=21764060, position=Offset(274.4, 590.2), pressed=true, pressure=0.49803925, previousUptimeMillis=21764049, previousPosition=Offset(274.4, 594.2), previousPressed=true, isConsumed=false, type=Touch, historical=[],scrollDelta=Offset(0.0, 0.0))
PointerEvent Initial: PointerInputChange(id=PointerId(value=0), uptimeMillis=21764070, position=Offset(274.4, 563.3), pressed=true, pressure=0.49803925, previousUptimeMillis=21764060, previousPosition=Offset(274.4, 590.2), previousPressed=true, isConsumed=false, type=Touch, historical=[],scrollDelta=Offset(0.0, 0.0))
onPreScroll available: Offset(0.0, -18.9) - Drag
onPostScroll available: Offset(0.0, 0.0) - Drag, consumed: Offset(0.0, -18.9)
PointerEvent Initial: PointerInputChange(id=PointerId(value=0), uptimeMillis=21764120, position=Offset(379.2, 185.7), pressed=true, pressure=0.49803925, previousUptimeMillis=21764070, previousPosition=Offset(274.4, 563.3), previousPressed=true, isConsumed=false, type=Touch, historical=[HistoricalChange(uptimeMillis=21764080, position=Offset(279.4, 513.3)), HistoricalChange(uptimeMillis=21764090, position=Offset(289.4, 438.4)), HistoricalChange(uptimeMillis=21764101, position=Offset(309.3, 361.5)), HistoricalChange(uptimeMillis=21764111, position=Offset(339.3, 274.6))],scrollDelta=Offset(0.0, 0.0))
onPreScroll available: Offset(0.0, -377.5) - Drag
onPostScroll available: Offset(0.0, 0.0) - Drag, consumed: Offset(0.0, -377.5)
PointerEvent Initial: PointerInputChange(id=PointerId(value=0), uptimeMillis=21764160, position=Offset(379.2, 185.7), pressed=false, pressure=0.49803925, previousUptimeMillis=21764120, previousPosition=Offset(379.2, 185.7), previousPressed=true, isConsumed=false, type=Touch, historical=[],scrollDelta=Offset(0.0, 0.0))
onPreFling available: (0.0, -11126.068) px/sec
onPreScroll available: Offset(0.0, -0.2) - Fling
onPostScroll available: Offset(0.0, 0.0) - Fling, consumed: Offset(0.0, -0.2)
onPreScroll available: Offset(0.0, -366.6) - Fling
onPostScroll available: Offset(0.0, 0.0) - Fling, consumed: Offset(0.0, -366.6)
onPreScroll available: Offset(0.0, -910.9) - Fling
onPostScroll available: Offset(0.0, 0.0) - Fling, consumed: Offset(0.0, -910.9)
onPreScroll available: Offset(0.0, -1538.9) - Fling
onPostScroll available: Offset(0.0, 0.0) - Fling, consumed: Offset(0.0, -1538.9)
onPreScroll available: Offset(0.0, -2092.1) - Fling
onPostScroll available: Offset(0.0, 0.0) - Fling, consumed: Offset(0.0, -2092.1)
onPreScroll available: Offset(0.0, -1758.6) - Fling
onPostScroll available: Offset(0.0, 0.0) - Fling, consumed: Offset(0.0, -1758.6)
onPreScroll available: Offset(0.0, -888.2) - Fling
onPostScroll available: Offset(0.0, -850.3) - Fling, consumed: Offset(0.0, -37.8)
onPostFling available: (0.0, -3514.9597) px/sec, consumed: (0.0, -7611.1084) px/sec

Breakpoint reached at androidx.compose.ui.input.pointer.util.VelocityTracker1D.calculateVelocity(VelocityTracker.kt:228)
[null, 
DataPointAtTime(time=21916294, dataPoint=255.46777),
DataPointAtTime(time=21916316, dataPoint=256.4657),
DataPointAtTime(time=21916326, dataPoint=259.45947),
DataPointAtTime(time=21916336, dataPoint=266.44492),
DataPointAtTime(time=21916346, dataPoint=278.41995),
DataPointAtTime(time=21916357, dataPoint=294.3867),
DataPointAtTime(time=21916367, dataPoint=317.33887),
DataPointAtTime(time=21916377, dataPoint=354.26196),
DataPointAtTime(time=21916387, dataPoint=402.16217),
null, null, null, null, null, null, null, null, null, null]
Breakpoint reached at androidx.compose.ui.input.pointer.util.VelocityTracker1D.calculateVelocity(VelocityTracker.kt:228)
[null, 
DataPointAtTime(time=21916294, dataPoint=645.19354),
DataPointAtTime(time=21916316, dataPoint=624.2197),
DataPointAtTime(time=21916326, dataPoint=584.26965),
DataPointAtTime(time=21916336, dataPoint=519.3508),
DataPointAtTime(time=21916346, dataPoint=452.43448),
DataPointAtTime(time=21916357, dataPoint=381.5231),
DataPointAtTime(time=21916367, dataPoint=304.61923),
DataPointAtTime(time=21916377, dataPoint=217.72784),
DataPointAtTime(time=21916387, dataPoint=120.84894),
null, null, null, null, null, null, null, null, null, null]
```

## Issue Tracker

https://issuetracker.google.com/issues/288499763
https://issuetracker.google.com/issues/204895043
https://issuetracker.google.com/issues/238654963
