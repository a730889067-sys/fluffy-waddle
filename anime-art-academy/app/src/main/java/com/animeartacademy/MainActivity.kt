package com.animeartacademy

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint as AndroidPaint
import android.graphics.Path as AndroidPath
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

private val Background = Color(0xFFF4F5FA)
private val SurfaceDark = Color(0xFFFFFFFF)
private val SurfaceRaised = Color(0xFFEBEDF5)
private val Accent = Color(0xFF7359C8)
private val AccentMint = Color(0xFF2D9479)
private val Ink = Color(0xFF292A3D)
private val Muted = Color(0xFF73788C)
private val Paper = Color(0xFFFFFEFC)

private data class Lesson(
    val id: String, val title: String, val series: String, val level: String,
    val duration: String, val kind: String, val accent: Color,
    val steps: List<String>, val tip: String
)

private val lessons = listOf(
    Lesson("face", "بناء الرأس والوجه", "الأساسيات", "مبتدئ", "10 دقائق", "face", Color(0xFF9C83FF),
        listOf("ابدأ بدائرة خفيفة تمثل الجزء العلوي من الرأس. لا تضغط بالقلم.",
            "أضف خطًا رأسيًا في المنتصف وخطًا أفقيًا لمستوى العينين.",
            "ارسم جانبي الوجه والفك حتى يلتقيا عند الذقن.",
            "أضف العينين والأنف والفم مع مراعاة التوازن بين الجانبين.",
            "نظّف خطوط البناء، ثم قوّ الخطوط النهائية وأضف الظلال البسيطة."),
        "قارن المسافة بين العينين بعرض عين واحدة تقريبًا، وعدّلها قبل تثبيت الخط."),
    Lesson("eye", "رسم عين أنمي", "الأساسيات", "مبتدئ", "8 دقائق", "eye", Color(0xFF64C8D8),
        listOf("ارسم خطًا علويًا مقوسًا؛ فهو يحدد شكل الجفن والرموش.",
            "أضف منحنى سفليًا أخف، واترك فراغًا صغيرًا عند الزاوية الداخلية.",
            "ارسم القزحية داخل العين ثم أضف الحدقة في المنتصف.",
            "اترك دائرتين صغيرتين بلا تلوين لتكونا انعكاس الضوء.",
            "ظلّل أعلى القزحية والجفن، ثم نظّف حدود العين."),
        "لا تجعل العينين دائرتين كاملتين؛ اختلاف سماكة الجفن يعطي طابع الأنمي."),
    Lesson("hair", "تقسيم الشعر إلى خصل", "الأساسيات", "مبتدئ", "12 دقيقة", "hair", Color(0xFFE4A65C),
        listOf("ارسم كتلة الشعر العامة حول الرأس قبل رسم الخصل المنفردة.",
            "قسّم الكتلة إلى مجموعات كبيرة تتجه نحو نقاط مختلفة.",
            "حوّل كل مجموعة إلى خصل مدببة بخطوط منحنية.",
            "أضف خصلًا أمام الجبهة، مع ترك فراغات بين المجموعات.",
            "حدّد الظلال تحت الخصل وأبقِ مناطق مضيئة لإظهار الحجم."),
        "ارسم الشعر ككتل كبيرة أولًا؛ كثرة الشعيرات الصغيرة مبكرًا تجعل الرسم مزدحمًا."),
    Lesson("shade", "الظل والإضاءة", "الأساسيات", "متوسط", "10 دقائق", "shade", Color(0xFF77C59A),
        listOf("اختر اتجاهًا واحدًا لمصدر الضوء قبل إضافة أي ظل.",
            "حدّد الجهة البعيدة عن الضوء بخط خفيف.",
            "أضف ظلًا تحت الشعر والأنف والذقن.",
            "اجعل الظلال العميقة في مناطق التقاء الأشكال فقط.",
            "امزج الدرجات تدريجيًا واترك مناطق بيضاء للّمعان."),
        "ثبّت اتجاه الضوء في كامل الوجه والشعر والملابس حتى لا يبدو الرسم متناقضًا."),
    Lesson("body", "نِسَب الجسم وتوزيعه", "الأساسيات", "مبتدئ", "14 دقيقة", "body", Color(0xFF81B7C9),
        listOf("ابدأ بخط رأسي خفيف يحدد طول الجسم واتجاهه.",
            "قسّم الجسم إلى الرأس والصدر والحوض مع مراعاة النسب.",
            "حدد الكتفين والخصر والحوض بأشكال هندسية بسيطة.",
            "أضف الذراعين والساقين مع تحديد المفاصل.",
            "حوّل الأشكال إلى خطوط جسم أنمي نظيفة ومتوازنة."),
        "لا تبدأ برسم الملابس؛ اضبط هيكل الجسم أولًا."),
    Lesson("pose", "رسم وضعية وحركة", "الأساسيات", "متوسط", "12 دقيقة", "pose", Color(0xFFB58AE6),
        listOf("ارسم خط الحركة الرئيسي من الرأس إلى القدمين.",
            "ضع دوائر صغيرة لمفاصل الكتفين والمرفقين والركبتين.",
            "حدد اتجاه الصدر والحوض بخطين متعاكسين عند الحاجة.",
            "اربط المفاصل بأشكال مبسطة للذراعين والساقين.",
            "نظّف الشكل وأضف توازنًا واضحًا للوزن والحركة."),
        "الوضعية المقروءة أهم من التفاصيل؛ اجعل خط الحركة واضحًا."),
    Lesson("hands", "تبسيط رسم اليدين", "الأساسيات", "متوسط", "12 دقيقة", "hands", Color(0xFFD48B9B),
        listOf("ارسم شكلًا مبسطًا لراحة اليد دون تفاصيل.",
            "حدد اتجاه الرسغ ومحور راحة اليد.",
            "أضف الإبهام ككتلة منفصلة عن بقية الأصابع.",
            "قسّم الأصابع إلى مفاصل وأطوال مختلفة.",
            "أكمل الخطوط والأظافر والظلال الخفيفة."),
        "الأصابع ليست متساوية الطول؛ راقب أطوالها واتجاهاتها."),
    Lesson("clothes", "طيّات الملابس والزي", "الأساسيات", "متوسط", "10 دقائق", "clothes", Color(0xFFB6A16B),
        listOf("ابدأ بشكل الجسم العام تحت الملابس.",
            "حدد مناطق الشد عند الكتف والمرفق والخصر.",
            "ارسم خطوط الطيات باتجاه نقاط الشد.",
            "اجعل الطيات متقاربة في مناطق الضغط ومتباعدة في القماش الواسع.",
            "أضف الظلال أسفل الطيات لإظهار حجم القماش."),
        "لا ترسم طيات عشوائية؛ كل طية لها نقطة شد أو سقوط."),
    Lesson("jinwoo", "دراسة ملامح سون جين وو", "تطبيق الشخصيات", "متوسط", "15 دقيقة", "jinwoo", Color(0xFF7598F4),
        listOf("ابدأ بدائرة الرأس وخط منتصف الوجه لتثبيت النسب.",
            "شكّل فكًا واضحًا وذقنًا أنحف مع ميل بسيط للرأس.",
            "ضع عينين هادئتين وحاجبين مستقيمين نسبيًا.",
            "ابنِ الشعر على شكل خصل داكنة متجهة للأعلى والجوانب.",
            "أضف الظلال حول العين وتحت الشعر والرقبة."),
        "هذه لوحة تدريب أصلية مبسطة للنسب والملامح، وليست صورة رسمية من العمل."),
    Lesson("gojo", "دراسة ملامح غوجو", "تطبيق الشخصيات", "متوسط", "15 دقيقة", "gojo", Color(0xFF78CBEA),
        listOf("ارسم شكل الرأس والفك بخطوط خفيفة ومتوازنة.",
            "حدد خط العينين ومكان العصابة أو العينين قبل التفاصيل.",
            "قسّم الشعر الفاتح إلى خصل كبيرة متداخلة حول الجبهة.",
            "أضف الياقة العالية وخطوط الملابس الأساسية.",
            "أكمل التباين بين الشعر الفاتح والظلال الداكنة."),
        "ابدأ بخصل قليلة وواضحة؛ التفاصيل الكثيرة لا تعوّض ضعف شكل الرأس."),
    Lesson("luffy", "دراسة ملامح لوفي", "تطبيق الشخصيات", "مبتدئ", "13 دقيقة", "luffy", Color(0xFFE7A45F),
        listOf("ارسم دائرة الرأس والفك، واترك مساحة كافية للقبعة.",
            "حدد العينين والأنف والفم والندبة الصغيرة أسفل العين.",
            "ارسم ابتسامة واسعة بخط منحني بسيط.",
            "أضف الشعر أسفل حافة القبعة.",
            "ارسم قبعة القش وحافتها العريضة ثم أضف الظلال."),
        "اجعل القبعة متوازنة مع الرأس؛ لا تبدأ بتفاصيل القش قبل ضبط شكلها."),
    Lesson("tanjiro", "دراسة ملامح تانجيرو", "تطبيق الشخصيات", "متوسط", "15 دقيقة", "tanjiro", Color(0xFFD77C68),
        listOf("حدد الرأس والفك واتجاه الوجه بخطوط بناء خفيفة.",
            "ارسم العينين والحاجبين مع تعبير هادئ ومركز.",
            "قسّم الشعر إلى خصل قصيرة غير منتظمة.",
            "أضف علامة الجبهة والأقراط بشكل مبسط.",
            "أكمل الظلال حول الشعر والرقبة وتفاصيل الزي."),
        "لا تبدأ بالعلامة أو الأقراط؛ تثبيت موضع الوجه أولًا يجعل التفاصيل أدق.")
)

private data class InkStroke(val points: List<Offset>, val color: Color, val widthFraction: Float)

private fun encodeDrawingStrokes(strokes: List<InkStroke>): String {
    return strokes.takeLast(350).joinToString("|") { stroke ->
        val points = stroke.points.joinToString(";") { "${it.x},${it.y}" }
        "${stroke.color.toArgb()}~${stroke.widthFraction}~$points"
    }
}

private fun decodeDrawingStrokes(encoded: String?): List<InkStroke> {
    if (encoded.isNullOrBlank()) return emptyList()
    return encoded.split("|").mapNotNull { record ->
        val parts = record.split("~", limit = 3)
        if (parts.size != 3) return@mapNotNull null
        val colorValue = parts[0].toIntOrNull() ?: return@mapNotNull null
        val widthValue = parts[1].toFloatOrNull()?.coerceIn(0.002f, 0.05f) ?: return@mapNotNull null
        val points = parts[2].split(";").mapNotNull { pair ->
            val coordinates = pair.split(",", limit = 2)
            if (coordinates.size != 2) return@mapNotNull null
            val x = coordinates[0].toFloatOrNull() ?: return@mapNotNull null
            val y = coordinates[1].toFloatOrNull() ?: return@mapNotNull null
            Offset(x.coerceIn(0f, 1f), y.coerceIn(0f, 1f))
        }
        if (points.isEmpty()) null else InkStroke(points, Color(colorValue), widthValue)
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AcademyApp() }
    }
}

@Composable
private fun AcademyApp() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val preferences = remember {
        context.getSharedPreferences("anime_art_academy_progress", Context.MODE_PRIVATE)
    }
    var selectedLesson by remember { mutableStateOf<Lesson?>(null) }
    var step by remember { mutableIntStateOf(0) }
    var completedIds by remember {
        mutableStateOf(
            preferences.getStringSet("completed_lesson_ids", mutableSetOf<String>())?.toSet()
                ?: emptySet()
        )
    }
    var lastLessonId by remember { mutableStateOf(preferences.getString("last_lesson_id", null)) }
    var lastStep by remember { mutableIntStateOf(preferences.getInt("last_step", 0)) }

    fun storeLastPosition(lesson: Lesson, position: Int) {
        val safePosition = position.coerceIn(0, lesson.steps.lastIndex)
        lastLessonId = lesson.id
        lastStep = safePosition
        preferences.edit()
            .putString("last_lesson_id", lesson.id)
            .putInt("last_step", safePosition)
            .apply()
    }

    fun openLesson(lesson: Lesson, resume: Boolean = false) {
        selectedLesson = lesson
        val startAt = if (resume && lastLessonId == lesson.id) lastStep else 0
        step = startAt.coerceIn(0, lesson.steps.lastIndex)
        storeLastPosition(lesson, step)
    }

    BackHandler(enabled = true) {
        val activeLesson = selectedLesson
        if (activeLesson == null) {
            Toast.makeText(context, "أنت في الصفحة الرئيسية", Toast.LENGTH_SHORT).show()
        } else if (step > 0) {
            step -= 1
            storeLastPosition(activeLesson, step)
        } else {
            selectedLesson = null
        }
    }

    fun completeLesson(lesson: Lesson) {
        val updated = completedIds + lesson.id
        completedIds = updated
        preferences.edit()
            .putStringSet("completed_lesson_ids", updated.toMutableSet())
            .apply()
        Toast.makeText(context, "أحسنت! تم إكمال الدرس وحفظ تقدمك.", Toast.LENGTH_SHORT).show()
    }

    MaterialTheme(colorScheme = lightColorScheme(
        background = Background, surface = SurfaceDark, primary = Accent, secondary = AccentMint
    )) {
        Surface(Modifier.fillMaxSize(), color = Background) {
            val current = selectedLesson
            if (current == null) {
                val resumeLesson = lessons.firstOrNull { it.id == lastLessonId }
                HomeScreen(
                    onSelect = { openLesson(it) },
                    completedIds = completedIds,
                    lastLesson = resumeLesson,
                    lastStep = lastStep,
                    onResume = { resumeLesson?.let { openLesson(it, resume = true) } }
                )
            } else {
                LessonScreen(
                    lesson = current,
                    step = step,
                    onBack = { selectedLesson = null },
                    onStepChange = {
                        step = it.coerceIn(0, current.steps.lastIndex)
                        storeLastPosition(current, step)
                    },
                    isCompleted = current.id in completedIds,
                    onComplete = { completeLesson(current) }
                )
            }
        }
    }
}

@Composable
private fun HomeScreen(
    onSelect: (Lesson) -> Unit,
    completedIds: Set<String>,
    lastLesson: Lesson?,
    lastStep: Int,
    onResume: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(13.dp)
    ) {
        item {
            Spacer(Modifier.height(12.dp))
            Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(26.dp)).background(SurfaceDark).padding(20.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                    Text("ANIME ART ACADEMY", color = Accent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("تعلّم الرسم\nخطوة بخطوة", color = Ink, fontSize = 31.sp, lineHeight = 38.sp, fontWeight = FontWeight.Bold)
                    Text("من أول خط إلى رسم شخصية كاملة", color = Muted, fontSize = 14.sp)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                        Box(Modifier.size(7.dp).background(AccentMint, CircleShape))
                        Text("الدروس والرسومات تعمل دون إنترنت", color = AccentMint, fontSize = 12.sp)
                    }
                    Spacer(Modifier.height(5.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("تقدمك المحفوظ", color = Muted, fontSize = 12.sp)
                        Spacer(Modifier.weight(1f))
                        Text("${completedIds.size} من ${lessons.size} دروس", color = AccentMint, fontSize = 12.sp)
                    }
                    LinearProgressIndicator(
                        progress = completedIds.size.toFloat() / lessons.size.toFloat(),
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                        color = AccentMint,
                        trackColor = SurfaceRaised
                    )
                }
            }
        }
        if (lastLesson != null) {
            item {
                ElevatedCard(
                    onClick = onResume,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = SurfaceRaised)
                ) {
                    Row(
                        Modifier.fillMaxWidth().padding(15.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            Modifier.size(42.dp).clip(CircleShape).background(Accent.copy(alpha = 0.16f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("▶", color = Accent, fontSize = 17.sp)
                        }
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Text("تابع من حيث توقفت", color = Accent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(lastLesson.title, color = Ink, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                            Text("الخطوة ${lastStep + 1} من ${lastLesson.steps.size}", color = Muted, fontSize = 11.sp)
                        }
                        Text("‹", color = Muted, fontSize = 24.sp)
                    }
                }
            }
        }
        item {
            Text("مسار التعلّم", color = Ink, fontSize = 21.sp, fontWeight = FontWeight.Bold)
            Text("ابدأ بالأساسيات ثم انتقل إلى الشخصيات", color = Muted, fontSize = 13.sp)
        }
        items(lessons) { lesson ->
            LessonCard(lesson, isCompleted = lesson.id in completedIds) { onSelect(lesson) }
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = SurfaceDark), shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("ملاحظة", color = AccentMint, fontWeight = FontWeight.Bold)
                    Text("الرسومات داخل الدروس توضيحية أصلية ومبسطة للتدريب، وليست صورًا رسمية من أعمال الأنمي.",
                        color = Muted, fontSize = 12.sp)
                }
            }
            Spacer(Modifier.height(18.dp))
        }
    }
}

@Composable
private fun LessonCard(lesson: Lesson, isCompleted: Boolean, onClick: () -> Unit) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(19.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = SurfaceDark)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val portrait = characterPortrait(lesson)
            if (portrait != 0) {
                Image(
                    painter = painterResource(portrait),
                    contentDescription = "مرجع مرسوم للشخصية",
                    modifier = Modifier.size(58.dp).clip(RoundedCornerShape(13.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    Modifier.size(48.dp).clip(RoundedCornerShape(15.dp)).background(lesson.accent.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        when (lesson.kind) {
                            "eye" -> "◉"; "hair" -> "✦"; "shade" -> "◐"; "body" -> "♙"
                            "pose" -> "↗"; "hands" -> "✋"; "clothes" -> "◇"
                            else -> "◌"
                        },
                        color = lesson.accent,
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(lesson.series, color = lesson.accent, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                Text(lesson.title, color = Ink, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Text("${lesson.level}  •  ${lesson.steps.size} خطوات  •  ${lesson.duration}", color = Muted, fontSize = 11.sp)
            }
            Text(if (isCompleted) "✓" else "‹", color = if (isCompleted) AccentMint else Muted, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }
    }
}


private fun stepExplanation(lesson: Lesson, step: Int): String {
    val details = when (lesson.kind) {
        "face" -> listOf(
            "خفّف ضغط القلم وارسم دائرة واسعة بخط متقطع أو خفيف. هذه الدائرة تمثل الجمجمة فقط، واترك مساحة أسفلها لإضافة الفك.",
            "مرّر خطًا رأسيًا خفيفًا في منتصف الدائرة ليحدد اتجاه الوجه. أضف خط العينين أفقيًا، واجعله يلتف قليلًا إذا كان الرأس مائلًا.",
            "ابدأ من جانبي الدائرة وانزل بخطين ناعمين نحو الذقن. اجعل الفك أضيق من أعلى الرأس ولا تحوّل الوجه إلى مربع.",
            "ضع العينين على خط العينين، ثم الأنف أسفل المنتصف بقليل والفم أسفل الأنف. اترك المسافة بين العينين قريبة من عرض عين واحدة.",
            "امسح خطوط البناء الزائدة تدريجيًا، ثم قوّ حدود الفك والعينين. اجعل الظل تحت الشعر والذقن خفيفًا ومتسقًا مع اتجاه الضوء."
        )
        "eye" -> listOf(
            "ابدأ من الزاوية الخارجية وارسم قوس الجفن العلوي باتجاه الزاوية الداخلية. اجعل منتصف القوس أعلى قليلًا من الطرفين.",
            "ارسم الجفن السفلي بخط أرق وأهدأ، ولا تغلق الشكل بقوة عند الزاويتين. اترك فراغًا صغيرًا يعطي العين شكلًا طبيعيًا.",
            "ارسم دائرة القزحية داخل فتحة العين، واجعل جزءًا منها مختفيًا خلف الجفن العلوي. ضع الحدقة في مركز القزحية.",
            "اترك نقطتين صغيرتين من الورق الأبيض داخل القزحية، واحدة أكبر من الأخرى. لا تمرر التلوين فوقهما حتى يبقى انعكاس الضوء واضحًا.",
            "غلّظ الجفن العلوي والرموش الخارجية، ثم ظلّل أعلى القزحية تدريجيًا. أبقِ أسفل العين أفتح حتى لا تبدو العين ثقيلة."
        )
        "hair" -> listOf(
            "ارسم حدود كتلة الشعر حول الرأس كأنها قبعة كبيرة. لا تبدأ بالشعيرات الفردية؛ ركّز أولًا على الشكل الخارجي العام.",
            "قسّم الكتلة إلى ثلاث أو أربع مجموعات رئيسية. اجعل كل مجموعة تتجه من الجذور إلى طرف واضح بدل توزيع خطوط عشوائية.",
            "حوّل أطراف المجموعات إلى خصل مدببة باستخدام منحنيات طويلة. غيّر طول الخصل واتجاهها قليلًا لتجنب شكل الشعر المتكرر.",
            "أضف خصل الجبهة فوق خط الوجه، وارسم بعضها أمام بعض. اترك فراغات صغيرة بين الخصل حتى لا تصبح كتلة سوداء واحدة.",
            "اجعل الظل أسفل كل خصلة متداخلة، واترك شريطًا رفيعًا مضيئًا على الجهة المواجهة للنور. لا تظلّل كل الشعر بالدرجة نفسها."
        )
        "shade" -> listOf(
            "اختر اتجاه الضوء أولًا، مثل أعلى اليسار. ضع سهمًا صغيرًا خارج الرسم لتتذكر الاتجاه طوال التظليل.",
            "ظلّل الجهة المقابلة لمصدر الضوء بخفة شديدة. ابدأ بدرجة فاتحة ثم قوّ الظل تدريجيًا بدل الضغط بقوة من البداية.",
            "أضف ظلالًا قصيرة أسفل الشعر والأنف والذقن، لأنها مناطق يحجب فيها شكلٌ الضوء عن الجزء الذي تحته.",
            "اجعل أغمق الدرجات في نقاط التقاء الأشكال فقط، مثل أسفل الغرة أو بين طيات الملابس. اترك وسط الخد والجبهة أفتح.",
            "ادمج حدود الظل بحركات صغيرة متدرجة، واترك مساحات بيضاء واضحة للّمعان. لا تغيّر اتجاه الضوء بين أجزاء الرسم."
        )
        "body" -> listOf(
            "ارسم خط حركة رأسيًا خفيفًا من الرأس إلى الحوض. حدّد طول الشخصية قبل رسم الملابس أو العضلات.",
            "ضع شكلًا بيضاويًا للصدر وآخر أصغر للحوض. اجعل بينهما مساحة للخصر، ووجّه الشكلين حسب ميل الجسم.",
            "حدّد عرض الكتفين وعرض الحوض بخطين بسيطين. لا تجعل الجانبين متطابقين إذا كانت الشخصية تقف بزاوية.",
            "ارسم الذراعين والساقين كأنابيب بسيطة، وضع دائرة صغيرة عند كل مفصل. راعِ أن المرفق والركبة ليسا في منتصف الطرف تمامًا.",
            "حوّل الهيكل إلى حدود جسم ناعمة. نظّف خطوط البناء بعد التأكد من توازن الوقفة وطول الأطراف."
        )
        "pose" -> listOf(
            "ابدأ بخط طويل واحد يوضح اتجاه الحركة من الرأس إلى القدمين. اجعله منحنيًا إذا كانت الوضعية ديناميكية.",
            "ضع دوائر صغيرة للكتفين والمرفقين والوركين والركبتين. هذه النقاط تحدد مكان اتصال الأطراف قبل رسمها.",
            "ارسم خطًا لاتجاه الصدر وآخر للحوض. يمكن أن يميل كل واحد باتجاه مختلف ليظهر الالتواء والحركة.",
            "صل نقاط المفاصل بأشكال أسطوانية بسيطة. تحقق من طول الطرفين قبل إضافة تفاصيل العضلات أو الملابس.",
            "قوّ حدود الجسم، واجعل القدمين واضحتين على الأرض أو في اتجاه الحركة. احذف خطوط البناء التي لم تعد تحتاجها."
        )
        "hands" -> listOf(
            "ارسم راحة اليد ككتلة تشبه مستطيلًا مستديرًا أو شبه منحرف. لا ترسم الأصابع الآن؛ حدّد حجم الكف أولًا.",
            "أضف خط محور من الرسغ إلى منتصف الكف، وحدد اتجاه اليد. يجب أن يتبع الرسغ اتجاه الساعد لا أن يكون منفصلًا عنه.",
            "ارسم الإبهام خارج جانب الكف ككتلة قصيرة مائلة. انتبه إلى أنه يبدأ من جانب راحة اليد وليس من نهاية الأصابع.",
            "قسّم الأصابع الأربعة إلى أطوال متدرجة، ثم ضع علامات المفاصل. اجعل الإصبع الأوسط غالبًا الأطول والبنصر أقصر قليلًا.",
            "ارسم حدود الأصابع حول المفاصل، ثم أضف الأظافر والظلال الخفيفة. اترك فراغات صغيرة بين الأصابع حتى لا تلتصق."
        )
        "clothes" -> listOf(
            "ابدأ بخطوط الجسم البسيطة تحت القماش، حتى تعرف أين يقع الكتف والخصر والركبة قبل رسم الثنيات.",
            "حدّد نقاط الشد عند الكتف والمرفق والخصر والركبة. ضع نقطة صغيرة عند كل مكان يشد القماش نحوه.",
            "ارسم الطيات من نقاط الشد إلى الخارج بخطوط متباعدة. اجعل اتجاه الطية يتبع حركة الجسم والجاذبية.",
            "قرّب الطيات من بعضها قرب المفصل أو منطقة الضغط، ووسّع المسافات في الأجزاء الفضفاضة من القماش.",
            "أضف ظلًا رفيعًا أسفل كل طية، واترك حافة مضيئة على الجهة المقابلة. لا ترسم خطوطًا متساوية السمك في كل مكان."
        )
        "jinwoo" -> listOf(
            "ابدأ بدائرة الجمجمة في منتصف الورقة، واجعل أسفلها مساحة كافية للذقن والرقبة. الصورة المرجعية تساعدك على ملاحظة شكل الرأس الهادئ.",
            "مدّ جانبي الوجه إلى فك أنحف وذقن واضح. ارسم خط المنتصف مائلًا قليلًا إذا أردت تقليد زاوية الصورة المرجعية.",
            "ضع عينين ضيقتين نسبيًا وحاجبين هادئين. ركّز على اتجاه النظرة والمسافة بين العينين بدل تكبيرهما أكثر من اللازم.",
            "قسّم الشعر الداكن إلى خصل كبيرة متجهة للأعلى والجوانب. ارسم silhouette الشعر أولًا، ثم أضف الخصل الداخلية واحدة بعد الأخرى.",
            "قوّ ظل الغرة حول العين والرقبة، واترك لمعات صغيرة في الشعر. اجعل المعطف ذا ياقة مرتفعة بخطين واضحين."
        )
        "gojo" -> listOf(
            "ارسم دائرة الرأس وحدد خط الذقن والرقبة. راجع الصورة المرجعية لتلاحظ شكل الوجه الطويل قليلًا والابتسامة الواثقة.",
            "حدّد مستوى العينين ومكان العصابة قبل التفاصيل. إذا رسمت العصابة، فاجعلها شريطًا يلتف حول الرأس لا خطًا عابرًا فوق الوجه.",
            "قسّم الشعر الفاتح إلى خصل كبيرة بارزة حول الرأس. ابدأ بالخصل الخارجية ثم أضف التداخل فوق الجبهة.",
            "ارسم الياقة العالية حول الرقبة والكتفين. اجعلها أعرض من الرقبة مع طيات قليلة واضحة بدل خطوط كثيرة.",
            "وازن بين الشعر الفاتح والعصابة والملابس الداكنة. أضف درجات ظل رمادية باردة داخل الخصل واترك مساحات بيضاء للضوء."
        )
        "luffy" -> listOf(
            "ارسم دائرة الرأس والفك، واترك فراغًا كبيرًا أعلى الرأس للقبعة. انظر إلى الصورة المرجعية لتحدد عرض القبعة بالنسبة للوجه.",
            "ضع العينين والأنف والفم والندبة أسفل العين. لا تجعل العينين ضخمتين جدًا؛ ابتسامة لوفي هي العنصر الأوضح في التعبير.",
            "ارسم ابتسامة واسعة بقوس علوي وسفلي بسيط، وحدد الأسنان بخطوط قليلة. حافظ على الخدين مرتفعين ليظهر التعبير المرح.",
            "أضف الشعر الأسود القصير أسفل حافة القبعة، واترك الخصل متفرقة على الجبهة. لا تدع الشعر يغطي العينين.",
            "ارسم تاج القبعة أولًا كقوس عريض ثم أضف الحافة والشريط الأحمر. بعد ذلك أضف ظلالًا خفيفة على القش والملابس."
        )
        "tanjiro" -> listOf(
            "ابدأ بدائرة الرأس وخط اتجاه الوجه. اترك موضعًا أعلى الجبهة للعلامة، وراجع الصورة المرجعية لتعرف مكانها النسبي.",
            "ارسم العينين والحاجبين بتعبير مركز وهادئ. حافظ على التوازن بين الجانبين قبل إضافة العلامة أو الأقراط.",
            "قسّم الشعر العنابي إلى خصل قصيرة متكسرة. ارسم حدود الكتلة أولًا، ثم أضف أطرافًا صغيرة مختلفة الاتجاه.",
            "ضع علامة الجبهة فوق العين في موضعها الصحيح، ثم ارسم الأقراط على جانبي الوجه بخطين رفيعين ومستطيلين صغيرين.",
            "أضف تباينًا بين الشعر الداكن والزي، وارسم نمط المربعات على أطراف الهاوري بخطوط متساوية. لا تزدحم التفاصيل حول الوجه."
        )
        else -> listOf(
            "ابدأ بخطوط خفيفة وحدد الشكل الكبير قبل التفاصيل.",
            "قِس موضع الخط بالنسبة إلى مركز الشكل وحوافه.",
            "أضف الخطوط الأساسية ببطء، واجعلها متصلة وسلسة.",
            "راجع النسب والمسافات قبل إضافة التفاصيل الصغيرة.",
            "نظّف الخطوط وأضف اللمسات النهائية بعد التأكد من الشكل."
        )
    }
    return details.getOrElse(step) { details.last() }
}

private fun stepLocation(lesson: Lesson, step: Int): String {
    val locations = when (lesson.kind) {
        "face" -> listOf("منتصف مساحة الرسم، في الجزء العلوي", "منتصف الدائرة، ثم مستوى العينين", "جانبا الرأس نزولًا إلى الذقن", "الثلث الأوسط من الوجه", "حدود الوجه والشعر وأسفل الذقن")
        "eye" -> listOf("الحافة العلوية لفتحة العين", "أسفل القوس العلوي مباشرة", "وسط فتحة العين", "داخل القزحية بعيدًا عن الحواف", "الجفن العلوي وأعلى القزحية")
        "hair" -> listOf("حول الرأس من الخارج", "داخل كتلة الشعر", "نهايات المجموعات", "مقدمة الرأس فوق الجبهة", "داخل الخصل وتحت مناطق التداخل")
        "shade" -> listOf("خارج الشكل لتحديد اتجاه الضوء", "الجهة البعيدة عن الضوء", "تحت الشعر والأنف والذقن", "نقاط التقاء الأشكال", "حواف الظل ومناطق اللمعان")
        "body" -> listOf("محور الجسم من الرأس إلى الحوض", "منطقة الصدر والحوض", "طرفا الكتفين والحوض", "على امتداد الذراعين والساقين", "الحدود الخارجية للجسم")
        "pose" -> listOf("خط الحركة الرئيسي وسط الورقة", "أماكن المفاصل", "داخل الصدر والحوض", "بين المفاصل", "الحدود الخارجية والقدمين")
        "hands" -> listOf("منتصف راحة اليد", "من الرسغ إلى منتصف الكف", "جانب الكف المقابل للخنصر", "أعلى راحة اليد", "حول الأصابع والمفاصل")
        "clothes" -> listOf("داخل حدود الجسم", "نقاط الكتف والمرفق والخصر", "من نقطة الشد إلى أطراف القماش", "مناطق الضغط والفراغ", "أسفل خطوط الطيات")
        "jinwoo" -> listOf("منتصف أعلى مساحة الرسم", "جانبا الوجه وأسفل الذقن", "على مستوى العينين", "حول أعلى الرأس والجبهة", "حول العين والرقبة وياقة المعطف")
        "gojo" -> listOf("منتصف مساحة الرسم", "مستوى العينين وحول الرأس", "محيط الرأس والجبهة", "أسفل الذقن وحول الرقبة", "الشعر والعصابة والياقة")
        "luffy" -> listOf("منتصف مساحة الرسم وأعلاها", "منتصف الوجه", "أسفل الأنف والخدين", "تحت حافة القبعة", "أعلى الرأس وحافة القبعة")
        "tanjiro" -> listOf("منتصف مساحة الرسم", "مستوى العينين والحاجبين", "حول أعلى الرأس", "الجبهة وجانبا الوجه", "الشعر والزي حول الكتفين")
        else -> listOf("وسط مساحة الرسم", "حول الشكل الأساسي", "داخل حدود الرسم", "مواضع التفاصيل", "الخطوط النهائية")
    }
    return locations.getOrElse(step) { locations.last() }
}

private fun characterPortrait(lesson: Lesson): Int = when (lesson.kind) {
    "jinwoo" -> R.drawable.character_jinwoo
    "gojo" -> R.drawable.character_gojo
    "luffy" -> R.drawable.character_luffy
    "tanjiro" -> R.drawable.character_tanjiro
    else -> 0
}


@Composable
private fun LessonScreen(
    lesson: Lesson,
    step: Int,
    onBack: () -> Unit,
    onStepChange: (Int) -> Unit,
    isCompleted: Boolean,
    onComplete: () -> Unit
) {
    var showReferenceImage by remember(lesson.id) { mutableStateOf(false) }
    val portrait = characterPortrait(lesson)

    Column(
        Modifier.fillMaxSize().background(Background).padding(horizontal = 11.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().heightIn(min = 42.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            TextButton(onClick = onBack, contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)) {
                Text("‹ الدروس", color = Accent, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(1.dp)) {
                Text(lesson.title, color = Ink, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                Text(lesson.series, color = Muted, fontSize = 10.sp)
            }
            Text("${step + 1} / ${lesson.steps.size}", color = Accent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        LinearProgressIndicator(
            progress = (step + 1).toFloat() / lesson.steps.size.toFloat(),
            modifier = Modifier.fillMaxWidth().height(5.dp).clip(CircleShape),
            color = lesson.accent,
            trackColor = SurfaceRaised
        )

        // Keep the exact instruction visible above the drawing area; the learner never needs
        // to scroll away from the step they are copying.
        Card(
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(15.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                Modifier.fillMaxWidth().padding(horizontal = 9.dp, vertical = 7.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (portrait != 0) {
                        Image(
                            painter = painterResource(portrait),
                            contentDescription = "الصورة المرجعية للشخصية، اضغط للتكبير",
                            modifier = Modifier
                                .size(58.dp)
                                .clip(RoundedCornerShape(11.dp))
                                .border(1.dp, SurfaceRaised, RoundedCornerShape(11.dp))
                                .clickable { showReferenceImage = true },
                            contentScale = ContentScale.Crop
                        )
                    }
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                if (portrait != 0) "المرجع والخطوة الحالية" else "الخطوة الحالية",
                                color = lesson.accent,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (portrait != 0) {
                                TextButton(
                                    onClick = { showReferenceImage = true },
                                    contentPadding = PaddingValues(horizontal = 5.dp, vertical = 0.dp)
                                ) {
                                    Text("تكبير الصورة", color = Accent, fontSize = 10.sp)
                                }
                            }
                        }
                        Text(
                            lesson.steps[step],
                            color = Ink,
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Text(
                    "شرح تفصيلي: ${stepExplanation(lesson, step)}",
                    color = Ink,
                    fontSize = 10.5.sp,
                    lineHeight = 14.sp
                )
                Text(
                    "مكان الخط: ${stepLocation(lesson, step)}",
                    color = Accent,
                    fontSize = 10.sp,
                    lineHeight = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "ملاحظة: ${lesson.tip}",
                    color = Muted,
                    fontSize = 9.5.sp,
                    lineHeight = 12.sp
                )
            }
        }

        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("ارسم فوق المخطط الباهت مباشرة", color = Ink, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text("الشرح والرسم أمامك دائمًا", color = Muted, fontSize = 9.sp)
        }

        DrawingPad(lesson, step, Modifier.fillMaxWidth().weight(1f))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = { onStepChange((step - 1).coerceAtLeast(0)) },
                enabled = step > 0,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) { Text("الخطوة السابقة") }

            Button(
                onClick = {
                    if (step < lesson.steps.lastIndex) onStepChange(step + 1)
                    else if (!isCompleted) onComplete()
                },
                enabled = step < lesson.steps.lastIndex || !isCompleted,
                modifier = Modifier.weight(1.2f),
                contentPadding = PaddingValues(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = Color.White)
            ) {
                Text(
                    when {
                        step < lesson.steps.lastIndex -> "الخطوة التالية"
                        isCompleted -> "اكتمل الدرس"
                        else -> "إكمال الدرس"
                    }
                )
            }
        }
        if (isCompleted) {
            Text("✓ تم حفظ إكمال هذا الدرس على هاتفك.", color = AccentMint, fontSize = 10.sp)
        }
    }

    if (showReferenceImage && portrait != 0) {
        Dialog(onDismissRequest = { showReferenceImage = false }) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                color = Color.White,
                shadowElevation = 12.dp
            ) {
                Column(
                    Modifier.fillMaxWidth().padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(9.dp)
                ) {
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("الصورة المرجعية للشخصية", color = Ink, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        TextButton(onClick = { showReferenceImage = false }) {
                            Text("إغلاق", color = Accent)
                        }
                    }
                    Image(
                        painter = painterResource(portrait),
                        contentDescription = "مرجع مكبر للرسم",
                        modifier = Modifier.fillMaxWidth().aspectRatio(1f).clip(RoundedCornerShape(15.dp)),
                        contentScale = ContentScale.Fit
                    )
                    Text(
                        "قارن شكل الرأس والشعر والملابس بهذه الصورة، ثم ارجع إلى لوحة الرسم.",
                        color = Muted,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun GuideIllustration(lesson: Lesson, step: Int, modifier: Modifier = Modifier) {
    Canvas(modifier.clip(RoundedCornerShape(15.dp)).background(Paper)) {
        drawRect(Paper)
        val w = size.width
        val h = size.height
        val guide = Color(0xFFB8B7B0)
        val dark = Color(0xFF30323A)
        for (i in 1..3) {
            val y = h * i / 4f
            drawLine(Color(0xFFECE8DF), Offset(w * 0.08f, y), Offset(w * 0.92f, y), 1f)
        }
        when (lesson.kind) {
            "eye" -> drawEyeStudy(step, dark, lesson.accent, guide, w, h)
            "hair" -> drawHairStudy(step, dark, lesson.accent, guide, w, h)
            "shade" -> drawShadeStudy(step, dark, lesson.accent, guide, w, h)
            "body" -> drawBodyStudy(step, dark, lesson.accent, guide, w, h)
            "pose" -> drawPoseStudy(step, dark, lesson.accent, guide, w, h)
            "hands" -> drawHandsStudy(step, dark, lesson.accent, guide, w, h)
            "clothes" -> drawClothesStudy(step, dark, lesson.accent, guide, w, h)
            else -> drawFaceStudy(step, lesson.kind, dark, lesson.accent, guide, w, h)
        }
    }
}

private fun DrawScope.drawEyeStudy(progress: Int, dark: Color, accent: Color, guide: Color, w: Float, h: Float) {
    val cx = w * 0.5f; val cy = h * 0.52f
    val eyeW = w * 0.63f; val eyeH = h * 0.30f
    drawOval(guide.copy(alpha = 0.65f), Offset(cx - eyeW / 2f, cy - eyeH / 2f),
        androidx.compose.ui.geometry.Size(eyeW, eyeH), style = Stroke(2f))
    if (progress >= 1) {
        val upper = Path().apply {
            moveTo(cx - eyeW / 2f, cy)
            cubicTo(cx - eyeW * 0.22f, cy - eyeH * 0.95f, cx + eyeW * 0.22f, cy - eyeH * 0.95f, cx + eyeW / 2f, cy)
        }
        drawPath(upper, dark, style = Stroke(7f))
        val lower = Path().apply {
            moveTo(cx - eyeW / 2f, cy)
            cubicTo(cx - eyeW * 0.20f, cy + eyeH * 0.90f, cx + eyeW * 0.20f, cy + eyeH * 0.90f, cx + eyeW / 2f, cy)
        }
        drawPath(lower, dark, style = Stroke(4f))
    }
    if (progress >= 2) {
        drawOval(accent.copy(alpha = 0.65f), Offset(cx - eyeW * 0.19f, cy - eyeH * 0.42f),
            androidx.compose.ui.geometry.Size(eyeW * 0.38f, eyeH * 0.88f))
        drawCircle(dark, eyeH * 0.28f, Offset(cx, cy + eyeH * 0.02f))
    }
    if (progress >= 3) {
        drawCircle(Color.White, eyeH * 0.12f, Offset(cx - eyeW * 0.07f, cy - eyeH * 0.17f))
        drawCircle(Color.White, eyeH * 0.055f, Offset(cx + eyeW * 0.10f, cy + eyeH * 0.14f))
    }
    if (progress >= 4) {
        drawLine(dark, Offset(cx - eyeW * 0.52f, cy - eyeH * 0.40f), Offset(cx - eyeW * 0.38f, cy - eyeH * 0.62f), 3f)
        drawLine(dark, Offset(cx - eyeW * 0.34f, cy - eyeH * 0.57f), Offset(cx - eyeW * 0.24f, cy - eyeH * 0.70f), 3f)
        drawLine(dark, Offset(cx + eyeW * 0.35f, cy - eyeH * 0.58f), Offset(cx + eyeW * 0.48f, cy - eyeH * 0.45f), 3f)
    }
}

private fun DrawScope.drawFaceStudy(progress: Int, kind: String, dark: Color, accent: Color, guide: Color, w: Float, h: Float) {
    val cx = w * 0.5f
    val cy = h * 0.49f
    val r = w * 0.205f
    val hairColor = when (kind) {
        "gojo" -> Color(0xFFB9C4D6)
        "luffy" -> Color(0xFF3A302B)
        "tanjiro" -> Color(0xFF392A2A)
        else -> Color(0xFF242735)
    }

    // Stage 1: head construction.
    drawCircle(guide, r, Offset(cx, cy - h * 0.045f), style = Stroke(2f))
    if (progress >= 1) {
        drawLine(guide, Offset(cx, h * 0.11f), Offset(cx, h * 0.88f), 2f)
        drawLine(guide, Offset(cx - r * 1.2f, cy + h * 0.04f), Offset(cx + r * 1.2f, cy + h * 0.04f), 2f)
        drawLine(guide.copy(alpha = 0.55f), Offset(cx - r * 0.85f, cy + h * 0.20f), Offset(cx + r * 0.85f, cy + h * 0.20f), 1.5f)
    }

    // Stage 2: jaw, cheeks and ears.
    if (progress >= 2) {
        val face = Path().apply {
            moveTo(cx - r * 0.92f, cy - h * 0.06f)
            cubicTo(cx - r * 1.02f, cy + h * 0.34f, cx - r * 0.47f, cy + h * 0.43f, cx, cy + h * 0.45f)
            cubicTo(cx + r * 0.47f, cy + h * 0.43f, cx + r * 1.02f, cy + h * 0.34f, cx + r * 0.92f, cy - h * 0.06f)
        }
        drawPath(face, dark, style = Stroke(3f))
        drawOval(dark.copy(alpha = 0.70f), Offset(cx - r * 1.02f, cy + h * 0.03f), androidx.compose.ui.geometry.Size(r * 0.22f, h * 0.16f), style = Stroke(2f))
        drawOval(dark.copy(alpha = 0.70f), Offset(cx + r * 0.80f, cy + h * 0.03f), androidx.compose.ui.geometry.Size(r * 0.22f, h * 0.16f), style = Stroke(2f))
    }

    // Stage 3: individual facial expression and landmarks.
    if (progress >= 3) {
        val eyeY = cy + h * 0.05f
        val eyeWidth = if (kind == "luffy") r * 0.24f else r * 0.27f
        for (side in listOf(-1f, 1f)) {
            val ex = cx + side * r * 0.43f
            val eye = Path().apply {
                moveTo(ex - eyeWidth, eyeY)
                quadraticTo(ex, eyeY - h * if (kind == "jinwoo" || kind == "gojo") 0.025f else 0.055f, ex + eyeWidth, eyeY)
                quadraticTo(ex, eyeY + h * 0.045f, ex - eyeWidth, eyeY)
            }
            drawPath(eye, dark, style = Stroke(2.7f))
            drawCircle(dark, r * 0.075f, Offset(ex, eyeY))
            val browY = eyeY - h * 0.075f
            drawLine(dark, Offset(ex - eyeWidth, browY), Offset(ex + eyeWidth, browY - h * 0.012f), 2.4f)
        }
        drawLine(guide, Offset(cx, eyeY + h * 0.02f), Offset(cx - r * 0.04f, eyeY + h * 0.14f), 2f)
        val mouth = Path().apply {
            moveTo(cx - r * if (kind == "luffy") 0.22f else 0.17f, cy + h * 0.30f)
            quadraticTo(cx, cy + h * if (kind == "luffy") 0.40f else 0.34f, cx + r * if (kind == "luffy") 0.22f else 0.17f, cy + h * 0.30f)
        }
        drawPath(mouth, dark, style = Stroke(if (kind == "luffy") 3.5f else 2.5f))
        if (kind == "luffy") {
            drawLine(Color(0xFF9E4D42), Offset(cx - r * 0.46f, cy + h * 0.01f), Offset(cx - r * 0.38f, cy + h * 0.13f), 2.5f)
        }
    }

    // Stage 4: character-specific hair, signature details and shoulders.
    if (progress >= 4) {
        when (kind) {
            "jinwoo" -> {
                val cap = Path().apply {
                    moveTo(cx - r * 1.02f, cy - h * 0.02f)
                    quadraticTo(cx - r * 1.20f, cy - h * 0.36f, cx - r * 0.48f, cy - h * 0.36f)
                    quadraticTo(cx - r * 0.15f, cy - h * 0.58f, cx + r * 0.05f, cy - h * 0.35f)
                    quadraticTo(cx + r * 0.55f, cy - h * 0.55f, cx + r * 0.98f, cy - h * 0.20f)
                }
                drawPath(cap, hairColor, style = Stroke(5f))
                for (i in -2..2) {
                    val x = cx + i * r * 0.32f
                    val spike = Path().apply {
                        moveTo(x - r * 0.12f, cy - h * 0.27f)
                        lineTo(x + r * 0.02f, cy - h * 0.43f - (if (i % 2 == 0) h * 0.03f else 0f))
                        lineTo(x + r * 0.18f, cy - h * 0.24f)
                    }
                    drawPath(spike, hairColor, style = Stroke(4f))
                }
                drawLine(Color(0xFF343A4D), Offset(cx - r * 0.44f, cy + h * 0.55f), Offset(cx, cy + h * 0.48f), 4f)
                drawLine(Color(0xFF343A4D), Offset(cx, cy + h * 0.48f), Offset(cx + r * 0.44f, cy + h * 0.55f), 4f)
            }
            "gojo" -> {
                val fringe = Path().apply {
                    moveTo(cx - r * 1.02f, cy - h * 0.02f)
                    quadraticTo(cx - r * 1.10f, cy - h * 0.42f, cx - r * 0.55f, cy - h * 0.43f)
                    lineTo(cx - r * 0.22f, cy - h * 0.58f)
                    lineTo(cx + r * 0.03f, cy - h * 0.39f)
                    lineTo(cx + r * 0.37f, cy - h * 0.57f)
                    lineTo(cx + r * 0.50f, cy - h * 0.34f)
                    quadraticTo(cx + r * 1.12f, cy - h * 0.35f, cx + r * 1.02f, cy + h * 0.02f)
                }
                drawPath(fringe, hairColor, style = Stroke(5f))
                val blindfold = Path().apply {
                    moveTo(cx - r * 0.88f, cy + h * 0.025f)
                    quadraticTo(cx, cy - h * 0.04f, cx + r * 0.88f, cy + h * 0.025f)
                    lineTo(cx + r * 0.82f, cy + h * 0.13f)
                    quadraticTo(cx, cy + h * 0.19f, cx - r * 0.82f, cy + h * 0.13f)
                    close()
                }
                drawPath(blindfold, Color(0xFF303746))
                drawLine(Color(0xFF68758B), Offset(cx - r * 0.72f, cy + h * 0.075f), Offset(cx + r * 0.72f, cy + h * 0.075f), 1.5f)
                drawLine(Color(0xFF303746), Offset(cx - r * 0.40f, cy + h * 0.55f), Offset(cx, cy + h * 0.48f), 4f)
                drawLine(Color(0xFF303746), Offset(cx, cy + h * 0.48f), Offset(cx + r * 0.40f, cy + h * 0.55f), 4f)
            }
            "luffy" -> {
                val hair = Path().apply {
                    moveTo(cx - r * 0.92f, cy - h * 0.09f)
                    quadraticTo(cx - r * 0.45f, cy - h * 0.34f, cx, cy - h * 0.19f)
                    quadraticTo(cx + r * 0.55f, cy - h * 0.35f, cx + r * 0.92f, cy - h * 0.08f)
                }
                drawPath(hair, hairColor, style = Stroke(4f))
                val hat = Path().apply {
                    moveTo(cx - r * 1.28f, cy - h * 0.25f)
                    quadraticTo(cx, cy - h * 0.57f, cx + r * 1.28f, cy - h * 0.25f)
                    lineTo(cx + r * 1.33f, cy - h * 0.16f)
                    quadraticTo(cx, cy - h * 0.09f, cx - r * 1.33f, cy - h * 0.16f)
                    close()
                }
                drawPath(hat, Color(0xFFD9B66F), style = Stroke(4f))
                drawLine(Color(0xFFB64F45), Offset(cx - r * 1.25f, cy - h * 0.20f), Offset(cx + r * 1.25f, cy - h * 0.20f), 4f)
                drawLine(dark, Offset(cx - r * 0.38f, cy + h * 0.16f), Offset(cx - r * 0.18f, cy + h * 0.12f), 2f)
                drawLine(Color(0xFF30343D), Offset(cx - r * 0.42f, cy + h * 0.55f), Offset(cx, cy + h * 0.47f), 4f)
                drawLine(Color(0xFF30343D), Offset(cx, cy + h * 0.47f), Offset(cx + r * 0.42f, cy + h * 0.55f), 4f)
            }
            "tanjiro" -> {
                val fringe = Path().apply {
                    moveTo(cx - r * 1.02f, cy - h * 0.01f)
                    quadraticTo(cx - r * 0.95f, cy - h * 0.43f, cx - r * 0.50f, cy - h * 0.38f)
                    lineTo(cx - r * 0.22f, cy - h * 0.55f)
                    lineTo(cx + r * 0.02f, cy - h * 0.34f)
                    lineTo(cx + r * 0.30f, cy - h * 0.52f)
                    lineTo(cx + r * 0.52f, cy - h * 0.33f)
                    quadraticTo(cx + r * 1.05f, cy - h * 0.39f, cx + r * 1.02f, cy - h * 0.01f)
                }
                drawPath(fringe, hairColor, style = Stroke(5f))
                drawLine(Color(0xFF9D554C), Offset(cx - r * 0.18f, cy - h * 0.22f), Offset(cx + r * 0.24f, cy - h * 0.31f), 5f)
                drawLine(Color(0xFFD0A15B), Offset(cx - r * 0.12f, cy - h * 0.20f), Offset(cx + r * 0.20f, cy - h * 0.27f), 1.5f)
                for (side in listOf(-1f, 1f)) {
                    drawCircle(Color(0xFFB77B45), r * 0.11f, Offset(cx + side * r * 1.12f, cy + h * 0.38f), style = Stroke(2f))
                    drawLine(Color(0xFFB77B45), Offset(cx + side * r * 1.12f, cy + h * 0.49f), Offset(cx + side * r * 1.12f, cy + h * 0.57f), 2f)
                }
                drawLine(Color(0xFF435344), Offset(cx - r * 0.42f, cy + h * 0.55f), Offset(cx, cy + h * 0.48f), 4f)
                drawLine(Color(0xFF435344), Offset(cx, cy + h * 0.48f), Offset(cx + r * 0.42f, cy + h * 0.55f), 4f)
            }
        }
    }
}

private fun DrawScope.drawHairStudy(progress: Int, dark: Color, accent: Color, guide: Color, w: Float, h: Float) {
    val cx = w * 0.5f; val cy = h * 0.53f; val r = w * 0.22f
    if (progress >= 0) drawCircle(guide, r, Offset(cx, cy), style = Stroke(2f))
    if (progress >= 1) {
        val cap = Path().apply {
            moveTo(cx - r * 1.2f, cy)
            quadraticTo(cx - r * 0.9f, cy - h * 0.46f, cx, cy - h * 0.43f)
            quadraticTo(cx + r * 0.9f, cy - h * 0.46f, cx + r * 1.2f, cy)
        }
        drawPath(cap, dark, style = Stroke(4f))
    }
    if (progress >= 2) for (i in -3..3) {
        val x = cx + i * r * 0.30f
        val strand = Path().apply {
            moveTo(x, cy - h * 0.30f)
            cubicTo(x - r * 0.28f, cy - h * 0.15f, x + r * 0.22f, cy - h * 0.02f, x + r * 0.06f, cy + h * 0.18f)
        }
        drawPath(strand, accent, style = Stroke(5f))
    }
    if (progress >= 3) for (i in -2..2) {
        val x = cx + i * r * 0.32f
        val fringe = Path().apply {
            moveTo(x - r * 0.18f, cy - h * 0.22f)
            quadraticTo(x, cy + h * 0.05f, x + r * 0.12f, cy + h * 0.20f)
        }
        drawPath(fringe, dark, style = Stroke(3f))
    }
    if (progress >= 4) {
        drawLine(Color.White, Offset(cx - r * 0.45f, cy - h * 0.28f), Offset(cx - r * 0.12f, cy - h * 0.35f), 3f)
        drawLine(Color.White, Offset(cx + r * 0.10f, cy - h * 0.31f), Offset(cx + r * 0.36f, cy - h * 0.34f), 3f)
    }
}

private fun DrawScope.drawShadeStudy(progress: Int, dark: Color, accent: Color, guide: Color, w: Float, h: Float) {
    val cx = w * 0.5f; val cy = h * 0.5f; val r = w * 0.22f
    drawCircle(guide, r, Offset(cx, cy), style = Stroke(2f))
    if (progress >= 1) drawLine(accent, Offset(cx - r * 0.7f, cy - r * 0.6f),
        Offset(cx + r * 0.7f, cy - r * 0.6f), 3f)
    if (progress >= 2) drawArc(dark.copy(alpha = 0.20f), 190f, 160f, true,
        Offset(cx - r, cy - r), androidx.compose.ui.geometry.Size(r * 2f, r * 2f))
    if (progress >= 3) {
        drawCircle(dark.copy(alpha = 0.18f), r * 0.45f, Offset(cx + r * 0.45f, cy + r * 0.25f))
        drawCircle(dark.copy(alpha = 0.28f), r * 0.22f, Offset(cx - r * 0.38f, cy + r * 0.38f))
    }
    if (progress >= 4) {
        drawCircle(Color.White, r * 0.30f, Offset(cx - r * 0.40f, cy - r * 0.35f))
        drawCircle(Color(0xFFE5E1D8), r * 0.12f, Offset(cx + r * 0.32f, cy - r * 0.55f))
    }
}

private fun DrawScope.drawBodyStudy(progress: Int, dark: Color, accent: Color, guide: Color, w: Float, h: Float) {
    val cx = w * 0.5f
    val headY = h * 0.18f
    drawLine(guide, Offset(cx, h * 0.08f), Offset(cx, h * 0.94f), 2f)
    if (progress >= 0) drawCircle(guide, w * 0.055f, Offset(cx, headY), style = Stroke(2f))
    if (progress >= 1) {
        drawLine(accent, Offset(cx, h * 0.27f), Offset(cx, h * 0.55f), 5f)
        drawLine(accent, Offset(cx - w * 0.15f, h * 0.34f), Offset(cx + w * 0.15f, h * 0.34f), 5f)
        drawLine(accent, Offset(cx - w * 0.12f, h * 0.57f), Offset(cx + w * 0.12f, h * 0.57f), 5f)
    }
    if (progress >= 2) {
        drawOval(guide, Offset(cx - w * 0.13f, h * 0.27f), androidx.compose.ui.geometry.Size(w * 0.26f, h * 0.31f), style = Stroke(3f))
        drawOval(guide, Offset(cx - w * 0.15f, h * 0.51f), androidx.compose.ui.geometry.Size(w * 0.30f, h * 0.14f), style = Stroke(3f))
    }
    if (progress >= 3) {
        drawLine(dark, Offset(cx - w * 0.13f, h * 0.34f), Offset(cx - w * 0.31f, h * 0.58f), 5f)
        drawLine(dark, Offset(cx + w * 0.13f, h * 0.34f), Offset(cx + w * 0.31f, h * 0.58f), 5f)
        drawLine(dark, Offset(cx - w * 0.09f, h * 0.62f), Offset(cx - w * 0.13f, h * 0.91f), 6f)
        drawLine(dark, Offset(cx + w * 0.09f, h * 0.62f), Offset(cx + w * 0.13f, h * 0.91f), 6f)
    }
    if (progress >= 4) {
        drawCircle(accent, 4f, Offset(cx - w * 0.13f, h * 0.58f))
        drawCircle(accent, 4f, Offset(cx + w * 0.13f, h * 0.58f))
        drawLine(dark, Offset(cx - w * 0.19f, h * 0.91f), Offset(cx - w * 0.07f, h * 0.91f), 5f)
        drawLine(dark, Offset(cx + w * 0.07f, h * 0.91f), Offset(cx + w * 0.19f, h * 0.91f), 5f)
    }
}

private fun DrawScope.drawPoseStudy(progress: Int, dark: Color, accent: Color, guide: Color, w: Float, h: Float) {
    val a = Offset(w * 0.40f, h * 0.12f)
    val b = Offset(w * 0.57f, h * 0.39f)
    val c = Offset(w * 0.45f, h * 0.69f)
    val d = Offset(w * 0.67f, h * 0.91f)
    if (progress >= 0) {
        val action = Path().apply { moveTo(a.x, a.y); quadraticTo(w * 0.68f, h * 0.47f, d.x, d.y) }
        drawPath(action, accent, style = Stroke(4f))
    }
    if (progress >= 1) listOf(a, b, c, d, Offset(w * 0.28f, h * 0.53f)).forEach {
        drawCircle(guide, w * 0.025f, it, style = Stroke(2f))
    }
    if (progress >= 2) {
        drawLine(dark, Offset(w * 0.30f, h * 0.32f), Offset(w * 0.65f, h * 0.39f), 7f)
        drawLine(dark, Offset(w * 0.36f, h * 0.65f), Offset(w * 0.60f, h * 0.70f), 7f)
    }
    if (progress >= 3) {
        drawLine(dark, Offset(w * 0.34f, h * 0.34f), Offset(w * 0.17f, h * 0.60f), 6f)
        drawLine(dark, Offset(w * 0.62f, h * 0.38f), Offset(w * 0.83f, h * 0.25f), 6f)
        drawLine(dark, Offset(w * 0.43f, h * 0.72f), Offset(w * 0.31f, h * 0.93f), 7f)
        drawLine(dark, Offset(w * 0.59f, h * 0.72f), Offset(w * 0.75f, h * 0.91f), 7f)
    }
    if (progress >= 4) {
        drawCircle(accent, w * 0.035f, a)
        drawCircle(accent, w * 0.035f, b)
        drawCircle(accent, w * 0.035f, c)
        drawCircle(accent, w * 0.035f, d)
    }
}

private fun DrawScope.drawHandsStudy(progress: Int, dark: Color, accent: Color, guide: Color, w: Float, h: Float) {
    val palm = Path().apply {
        moveTo(w * 0.39f, h * 0.78f)
        quadraticTo(w * 0.28f, h * 0.60f, w * 0.36f, h * 0.43f)
        quadraticTo(w * 0.50f, h * 0.32f, w * 0.64f, h * 0.43f)
        lineTo(w * 0.68f, h * 0.73f)
        quadraticTo(w * 0.55f, h * 0.91f, w * 0.39f, h * 0.78f)
    }
    if (progress >= 0) drawPath(palm, guide, style = Stroke(3f))
    if (progress >= 1) drawLine(accent, Offset(w * 0.50f, h * 0.77f), Offset(w * 0.50f, h * 0.46f), 3f)
    if (progress >= 2) {
        val thumb = Path().apply {
            moveTo(w * 0.37f, h * 0.55f)
            quadraticTo(w * 0.19f, h * 0.50f, w * 0.22f, h * 0.38f)
        }
        drawPath(thumb, dark, style = Stroke(7f))
    }
    if (progress >= 3) {
        val starts = listOf(0.39f, 0.46f, 0.54f, 0.62f)
        val ends = listOf(0.20f, 0.13f, 0.16f, 0.28f)
        starts.indices.forEach { i ->
            drawLine(dark, Offset(w * starts[i], h * 0.45f), Offset(w * (starts[i] - 0.01f), h * ends[i]), 7f)
        }
    }
    if (progress >= 4) {
        drawLine(accent, Offset(w * 0.36f, h * 0.68f), Offset(w * 0.64f, h * 0.68f), 2f)
        drawCircle(dark, 3f, Offset(w * 0.39f, h * 0.23f))
        drawCircle(dark, 3f, Offset(w * 0.46f, h * 0.15f))
        drawCircle(dark, 3f, Offset(w * 0.54f, h * 0.18f))
        drawCircle(dark, 3f, Offset(w * 0.62f, h * 0.30f))
    }
}

private fun DrawScope.drawClothesStudy(progress: Int, dark: Color, accent: Color, guide: Color, w: Float, h: Float) {
    val body = Path().apply {
        moveTo(w * 0.35f, h * 0.25f)
        lineTo(w * 0.22f, h * 0.40f)
        lineTo(w * 0.30f, h * 0.88f)
        lineTo(w * 0.70f, h * 0.88f)
        lineTo(w * 0.78f, h * 0.40f)
        lineTo(w * 0.65f, h * 0.25f)
        close()
    }
    if (progress >= 0) drawPath(body, guide, style = Stroke(3f))
    if (progress >= 1) {
        drawCircle(accent, 5f, Offset(w * 0.35f, h * 0.29f))
        drawCircle(accent, 5f, Offset(w * 0.65f, h * 0.29f))
        drawCircle(accent, 5f, Offset(w * 0.50f, h * 0.80f))
    }
    if (progress >= 2) {
        drawLine(dark, Offset(w * 0.35f, h * 0.29f), Offset(w * 0.43f, h * 0.52f), 3f)
        drawLine(dark, Offset(w * 0.65f, h * 0.29f), Offset(w * 0.57f, h * 0.52f), 3f)
        drawLine(dark, Offset(w * 0.50f, h * 0.80f), Offset(w * 0.42f, h * 0.56f), 3f)
    }
    if (progress >= 3) {
        drawLine(accent, Offset(w * 0.28f, h * 0.49f), Offset(w * 0.39f, h * 0.67f), 3f)
        drawLine(accent, Offset(w * 0.72f, h * 0.49f), Offset(w * 0.61f, h * 0.67f), 3f)
        drawLine(accent, Offset(w * 0.43f, h * 0.57f), Offset(w * 0.50f, h * 0.72f), 3f)
    }
    if (progress >= 4) {
        drawLine(dark.copy(alpha = 0.45f), Offset(w * 0.31f, h * 0.76f), Offset(w * 0.39f, h * 0.84f), 5f)
        drawLine(dark.copy(alpha = 0.45f), Offset(w * 0.69f, h * 0.76f), Offset(w * 0.61f, h * 0.84f), 5f)
    }
}

@Composable
private fun DrawingPad(lesson: Lesson, step: Int, modifier: Modifier = Modifier) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val drawingPreferences = remember(lesson.id) {
        context.getSharedPreferences("anime_art_academy_drawings", Context.MODE_PRIVATE)
    }
    val strokes = remember(lesson.id) {
        mutableStateListOf<InkStroke>().apply {
            addAll(decodeDrawingStrokes(drawingPreferences.getString("drawing_${lesson.id}", null)))
        }
    }
    val redoStrokes = remember(lesson.id) { mutableStateListOf<InkStroke>() }
    var currentPoints by remember(lesson.id) { mutableStateOf<List<Offset>>(emptyList()) }
    var selectedColor by remember(lesson.id) { mutableStateOf(Color(0xFF242631)) }
    var brushWidth by remember(lesson.id) { mutableStateOf(0.006f) }
    var pendingSave by remember(lesson.id) { mutableStateOf<List<InkStroke>>(emptyList()) }
    var pendingCanvasSize by remember(lesson.id) { mutableStateOf(IntSize.Zero) }
    var zoomMode by remember(lesson.id) { mutableStateOf(false) }
    var zoomScale by remember(lesson.id) { mutableFloatStateOf(1f) }
    var zoomPan by remember(lesson.id) { mutableStateOf(Offset.Zero) }
    var viewportSize by remember(lesson.id) { mutableStateOf(IntSize.Zero) }
    fun updateZoom(targetScale: Float) {
        val nextScale = targetScale.coerceIn(1f, 3.5f)
        zoomScale = nextScale
        if (nextScale <= 1f) {
            zoomPan = Offset.Zero
        } else {
            val maxX = viewportSize.width * (nextScale - 1f) / 2f
            val maxY = viewportSize.height * (nextScale - 1f) / 2f
            zoomPan = Offset(
                zoomPan.x.coerceIn(-maxX, maxX),
                zoomPan.y.coerceIn(-maxY, maxY)
            )
        }
    }

    val zoomState = rememberTransformableState { zoomChange, panChange, _ ->
        val newScale = (zoomScale * zoomChange).coerceIn(1f, 3.5f)
        zoomScale = newScale
        if (newScale <= 1f) {
            zoomPan = Offset.Zero
        } else {
            val maxX = viewportSize.width * (newScale - 1f) / 2f
            val maxY = viewportSize.height * (newScale - 1f) / 2f
            val requested = zoomPan + panChange
            zoomPan = Offset(
                requested.x.coerceIn(-maxX, maxX),
                requested.y.coerceIn(-maxY, maxY)
            )
        }
    }

    fun persistDrawing() {
        drawingPreferences.edit()
            .putString("drawing_${lesson.id}", encodeDrawingStrokes(strokes.toList()))
            .apply()
    }

    val saveLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("image/png")) { uri: Uri? ->
        if (uri != null) {
            val ok = writeDrawing(context, uri, pendingSave, pendingCanvasSize)
            Toast.makeText(context, if (ok) "تم حفظ الرسم بنجاح" else "تعذر حفظ الصورة", Toast.LENGTH_SHORT).show()
        }
    }

    Column(modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            Modifier.fillMaxWidth().heightIn(min = 29.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf(Color(0xFF242631), Color(0xFF9B3848), Color(0xFF315FAD), Color(0xFF24815E), Color(0xFFD39A37), Paper).forEach { color ->
                Box(
                    Modifier.size(27.dp).clip(CircleShape).background(color)
                        .then(if (selectedColor == color) Modifier.border(2.dp, Accent, CircleShape) else Modifier)
                        .clickable { selectedColor = color }
                )
            }
        }
        Row(
            Modifier.fillMaxWidth().heightIn(min = 30.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            TextButton(
                onClick = { zoomMode = false },
                contentPadding = PaddingValues(horizontal = 5.dp, vertical = 0.dp)
            ) { Text(if (!zoomMode) "✓ رسم" else "رسم", color = if (!zoomMode) AccentMint else Accent, fontSize = 10.sp) }
            TextButton(
                onClick = { zoomMode = true },
                contentPadding = PaddingValues(horizontal = 5.dp, vertical = 0.dp)
            ) { Text(if (zoomMode) "✓ تكبير" else "تكبير", color = if (zoomMode) AccentMint else Accent, fontSize = 10.sp) }
            TextButton(
                onClick = { zoomMode = true; updateZoom(zoomScale / 1.25f) },
                contentPadding = PaddingValues(horizontal = 5.dp, vertical = 0.dp)
            ) { Text("−", color = Accent, fontSize = 16.sp) }
            TextButton(
                onClick = { zoomMode = true; updateZoom(zoomScale * 1.25f) },
                contentPadding = PaddingValues(horizontal = 5.dp, vertical = 0.dp)
            ) { Text("+", color = Accent, fontSize = 16.sp) }
            Spacer(Modifier.weight(1f))
            TextButton(
                onClick = { updateZoom(1f) },
                contentPadding = PaddingValues(horizontal = 5.dp, vertical = 0.dp)
            ) { Text("إعادة 1×", color = Muted, fontSize = 10.sp) }
        }

        Row(
            Modifier.fillMaxWidth().heightIn(min = 28.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("القلم", color = Muted, fontSize = 10.sp)
            TextButton(onClick = { brushWidth = 0.004f }, contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)) {
                Text(if (brushWidth == 0.004f) "✓ رفيع" else "رفيع", color = if (brushWidth == 0.004f) AccentMint else Accent, fontSize = 10.sp)
            }
            TextButton(onClick = { brushWidth = 0.012f }, contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)) {
                Text(if (brushWidth == 0.012f) "✓ متوسط" else "متوسط", color = if (brushWidth == 0.012f) AccentMint else Accent, fontSize = 10.sp)
            }
            TextButton(onClick = { brushWidth = 0.020f }, contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)) {
                Text(if (brushWidth == 0.020f) "✓ عريض" else "عريض", color = if (brushWidth == 0.020f) AccentMint else Accent, fontSize = 10.sp)
            }
            TextButton(onClick = { selectedColor = Paper }, contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)) {
                Text("ممحاة", color = Accent, fontSize = 10.sp)
            }
        }

        Box(
            Modifier.fillMaxWidth().weight(1f).clip(RoundedCornerShape(15.dp))
                .background(Paper).border(1.dp, Color(0xFFE0E2EA), RoundedCornerShape(15.dp))
                .onSizeChanged { viewportSize = it }
                .transformable(state = zoomState, enabled = zoomMode)
        ) {
            Box(
                Modifier.fillMaxSize().graphicsLayer {
                    scaleX = zoomScale
                    scaleY = zoomScale
                    translationX = zoomPan.x
                    translationY = zoomPan.y
                }
            ) {
                GuideIllustration(lesson, step, Modifier.fillMaxSize().alpha(0.30f))
                Canvas(
                    Modifier.fillMaxSize().pointerInput(selectedColor, brushWidth, lesson.id, zoomMode) {
                        if (!zoomMode) {
                            val canvasWidth = size.width.coerceAtLeast(1).toFloat()
                            val canvasHeight = size.height.coerceAtLeast(1).toFloat()
                            detectDragGestures(
                                onDragStart = { p ->
                                    currentPoints = listOf(Offset(p.x / canvasWidth, p.y / canvasHeight))
                                },
                                onDrag = { change, _ ->
                                    currentPoints = currentPoints + Offset(
                                        (change.position.x / canvasWidth).coerceIn(0f, 1f),
                                        (change.position.y / canvasHeight).coerceIn(0f, 1f)
                                    )
                                },
                                onDragEnd = {
                                    if (currentPoints.isNotEmpty()) {
                                        strokes.add(InkStroke(currentPoints, selectedColor, brushWidth))
                                        if (strokes.size > 350) strokes.removeAt(0)
                                        redoStrokes.clear()
                                        persistDrawing()
                                    }
                                    currentPoints = emptyList()
                                },
                                onDragCancel = { currentPoints = emptyList() }
                            )
                        }
                    }
                ) {
                val allStrokes = strokes.toList() + if (currentPoints.isNotEmpty())
                    listOf(InkStroke(currentPoints, selectedColor, brushWidth)) else emptyList()
                allStrokes.forEach { stroke ->
                    if (stroke.points.isNotEmpty()) {
                        val first = stroke.points.first()
                        val path = Path().apply {
                            moveTo(first.x * size.width, first.y * size.height)
                            stroke.points.drop(1).forEach { lineTo(it.x * size.width, it.y * size.height) }
                            if (stroke.points.size == 1) lineTo(first.x * size.width + 0.5f, first.y * size.height + 0.5f)
                        }
                        if (stroke.points.size == 1) {
                            drawCircle(stroke.color, stroke.widthFraction * size.width / 2f,
                                Offset(first.x * size.width, first.y * size.height))
                        } else {
                            drawPath(path, stroke.color, style = Stroke(
                                width = stroke.widthFraction * size.width,
                                cap = androidx.compose.ui.graphics.StrokeCap.Round,
                                join = androidx.compose.ui.graphics.StrokeJoin.Round
                            ))
                        }
                    }
                }
            }
            }
            if (zoomMode) {
                Text(
                    "حرّك إصبعين للتنقل، وباعد بينهما للتكبير",
                    Modifier.align(Alignment.TopCenter).padding(top = 6.dp)
                        .clip(RoundedCornerShape(9.dp)).background(Color.White.copy(alpha = 0.88f))
                        .padding(horizontal = 9.dp, vertical = 4.dp),
                    color = Ink,
                    fontSize = 9.sp
                )
            }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            OutlinedButton(
                onClick = {
                    if (strokes.isNotEmpty()) {
                        redoStrokes.add(strokes.removeAt(strokes.lastIndex))
                        persistDrawing()
                    }
                },
                Modifier.weight(0.85f).heightIn(min = 34.dp),
                enabled = strokes.isNotEmpty(),
                contentPadding = PaddingValues(vertical = 3.dp)
            ) { Text("تراجع", fontSize = 11.sp) }

            OutlinedButton(
                onClick = {
                    if (redoStrokes.isNotEmpty()) {
                        strokes.add(redoStrokes.removeAt(redoStrokes.lastIndex))
                        persistDrawing()
                    }
                },
                Modifier.weight(0.85f).heightIn(min = 34.dp),
                enabled = redoStrokes.isNotEmpty(),
                contentPadding = PaddingValues(vertical = 3.dp)
            ) { Text("إعادة", fontSize = 11.sp) }

            OutlinedButton(
                onClick = {
                    strokes.clear()
                    redoStrokes.clear()
                    currentPoints = emptyList()
                    persistDrawing()
                },
                Modifier.weight(0.8f).heightIn(min = 34.dp),
                contentPadding = PaddingValues(vertical = 3.dp)
            ) { Text("مسح", fontSize = 11.sp) }

            Button(
                onClick = {
                    pendingSave = strokes.toList()
                    pendingCanvasSize = viewportSize
                    saveLauncher.launch("anime-art-${lesson.id}-${System.currentTimeMillis()}.png")
                },
                Modifier.weight(1.2f).heightIn(min = 34.dp),
                enabled = strokes.isNotEmpty(),
                contentPadding = PaddingValues(vertical = 3.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Accent)
            ) { Text("حفظ PNG", fontSize = 11.sp) }
        }
    }
}

private fun writeDrawing(context: Context, uri: Uri, strokes: List<InkStroke>, canvasSize: IntSize): Boolean {
    return try {
        val outputWidth = 1200
        val ratio = if (canvasSize.width > 0 && canvasSize.height > 0) {
            canvasSize.height.toFloat() / canvasSize.width.toFloat()
        } else {
            1f
        }
        val outputHeight = (outputWidth * ratio).toInt().coerceIn(600, 2400)
        val bitmap = Bitmap.createBitmap(outputWidth, outputHeight, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bitmap)
        canvas.drawColor(Paper.toArgb())
        strokes.forEach { stroke ->
            if (stroke.points.isNotEmpty()) {
                val path = AndroidPath()
                val first = stroke.points.first()
                path.moveTo(first.x * bitmap.width, first.y * bitmap.height)
                stroke.points.drop(1).forEach { path.lineTo(it.x * bitmap.width, it.y * bitmap.height) }
                val paint = AndroidPaint(AndroidPaint.ANTI_ALIAS_FLAG).apply {
                    color = stroke.color.toArgb()
                    style = AndroidPaint.Style.STROKE
                    strokeCap = AndroidPaint.Cap.ROUND
                    strokeJoin = AndroidPaint.Join.ROUND
                    strokeWidth = (stroke.widthFraction * bitmap.width).coerceAtLeast(2f)
                }
                if (stroke.points.size == 1) {
                    canvas.drawCircle(first.x * bitmap.width, first.y * bitmap.height, paint.strokeWidth / 2f, paint.apply { style = AndroidPaint.Style.FILL })
                } else {
                    canvas.drawPath(path, paint)
                }
            }
        }
        val stream = context.contentResolver.openOutputStream(uri)
        if (stream == null) {
            bitmap.recycle()
            false
        } else {
            val saved = stream.use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
            bitmap.recycle()
            saved
        }
    } catch (_: Exception) {
        false
    }
}
