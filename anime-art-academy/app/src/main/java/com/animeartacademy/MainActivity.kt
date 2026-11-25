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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Background = Color(0xFF090B12)
private val SurfaceDark = Color(0xFF151A27)
private val SurfaceRaised = Color(0xFF1D2433)
private val Accent = Color(0xFF9C83FF)
private val AccentMint = Color(0xFF74D9B0)
private val Ink = Color(0xFF20232B)
private val Paper = Color(0xFFFAF8F2)

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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AcademyApp() }
    }
}

@Composable
private fun AcademyApp() {
    var selectedLesson by remember { mutableStateOf<Lesson?>(null) }
    var step by remember { mutableIntStateOf(0) }
    MaterialTheme(colorScheme = darkColorScheme(
        background = Background, surface = SurfaceDark, primary = Accent, secondary = AccentMint
    )) {
        Surface(Modifier.fillMaxSize(), color = Background) {
            val current = selectedLesson
            if (current == null) HomeScreen { selectedLesson = it; step = 0 }
            else LessonScreen(current, step, { selectedLesson = null }, { step = it })
        }
    }
}

@Composable
private fun HomeScreen(onSelect: (Lesson) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(13.dp)
    ) {
        item {
            Spacer(Modifier.height(12.dp))
            Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(26.dp)).background(SurfaceDark).padding(20.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                    Text("ANIME ART ACADEMY", color = Accent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("تعلّم الرسم\nخطوة بخطوة", color = Color.White, fontSize = 31.sp, lineHeight = 38.sp, fontWeight = FontWeight.Bold)
                    Text("من أول خط إلى رسم شخصية كاملة", color = Color(0xFFC5C9D6), fontSize = 14.sp)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                        Box(Modifier.size(7.dp).background(AccentMint, CircleShape))
                        Text("الدروس والرسومات تعمل دون إنترنت", color = AccentMint, fontSize = 12.sp)
                    }
                }
            }
        }
        item {
            Text("مسار التعلّم", color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Bold)
            Text("ابدأ بالأساسيات ثم انتقل إلى الشخصيات", color = Color(0xFF9EA5B6), fontSize = 13.sp)
        }
        items(lessons) { lesson -> LessonCard(lesson) { onSelect(lesson) } }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = SurfaceDark), shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("ملاحظة", color = AccentMint, fontWeight = FontWeight.Bold)
                    Text("الرسومات داخل الدروس توضيحية أصلية ومبسطة للتدريب، وليست صورًا رسمية من أعمال الأنمي.",
                        color = Color(0xFFB8BECC), fontSize = 12.sp)
                }
            }
            Spacer(Modifier.height(18.dp))
        }
    }
}

@Composable
private fun LessonCard(lesson: Lesson, onClick: () -> Unit) {
    ElevatedCard(
        onClick = onClick, modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(19.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = SurfaceDark)
    ) {
        Row(Modifier.fillMaxWidth().padding(15.dp), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(13.dp)) {
            Box(Modifier.size(48.dp).clip(RoundedCornerShape(15.dp)).background(lesson.accent.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center) {
                Text(when (lesson.kind) {
                    "eye" -> "◉"; "hair" -> "✦"; "shade" -> "◐"; "body" -> "♙"
                    "pose" -> "↗"; "hands" -> "✋"; "clothes" -> "◇"
                    "jinwoo" -> "影"; "gojo" -> "◇"; "luffy" -> "☀"; "tanjiro" -> "✧"; else -> "◌"
                }, color = lesson.accent, fontSize = 23.sp, fontWeight = FontWeight.Bold)
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(lesson.series, color = lesson.accent, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                Text(lesson.title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Text("${lesson.level}  •  ${lesson.steps.size} خطوات  •  ${lesson.duration}",
                    color = Color(0xFF9EA5B6), fontSize = 11.sp)
            }
            Text("‹", color = Color(0xFFB6BBD0), fontSize = 26.sp)
        }
    }
}

@Composable
private fun LessonScreen(lesson: Lesson, step: Int, onBack: () -> Unit, onStepChange: (Int) -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(13.dp)) {
        TextButton(onClick = onBack) { Text("← العودة إلى الدروس", color = Accent, fontSize = 14.sp) }
        Text(lesson.series.uppercase(), color = lesson.accent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text(lesson.title, color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.Bold)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("الخطوة ${step + 1} من ${lesson.steps.size}", color = Color(0xFFC6CAD6), fontSize = 12.sp)
            Spacer(Modifier.weight(1f))
            Text("${((step + 1) * 100 / lesson.steps.size)}%", color = AccentMint, fontSize = 12.sp)
        }
        LinearProgressIndicator(
            progress = (step + 1).toFloat() / lesson.steps.size.toFloat(),
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
            color = lesson.accent, trackColor = SurfaceRaised
        )
        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark)) {
            Column(Modifier.fillMaxWidth().padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("الرسم التوضيحي", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                GuideIllustration(lesson, step, Modifier.fillMaxWidth().height(225.dp))
                Text("يتطور الرسم مع كل خطوة", color = Color(0xFF9EA5B6), fontSize = 11.sp)
            }
        }
        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(17.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceRaised)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("نفّذ هذه الخطوة", color = lesson.accent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(lesson.steps[step], color = Color.White, fontSize = 16.sp, lineHeight = 25.sp)
                Text("نصيحة: ${lesson.tip}", color = Color(0xFFB8BECC), fontSize = 12.sp, lineHeight = 19.sp)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(9.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = { onStepChange((step - 1).coerceAtLeast(0)) },
                enabled = step > 0, modifier = Modifier.weight(1f)) { Text("السابق") }
            Button(onClick = { onStepChange((step + 1).coerceAtMost(lesson.steps.lastIndex)) },
                enabled = step < lesson.steps.lastIndex, modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = Color.White)) {
                Text("الخطوة التالية")
            }
        }
        Text("مساحة التدريب الخاصة بك", color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Bold)
        Text("ارسم بإصبعك، تراجع عن آخر خط، ثم احفظ عملك كصورة PNG.",
            color = Color(0xFF9EA5B6), fontSize = 12.sp)
        DrawingPad(Modifier.fillMaxWidth())
        Spacer(Modifier.height(20.dp))
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
    val cx = w * 0.5f; val cy = h * 0.49f; val r = w * 0.205f
    drawCircle(guide, r, Offset(cx, cy - h * 0.045f), style = Stroke(2f))
    if (progress >= 1) {
        drawLine(guide, Offset(cx, h * 0.11f), Offset(cx, h * 0.88f), 2f)
        drawLine(guide, Offset(cx - r * 1.2f, cy + h * 0.04f), Offset(cx + r * 1.2f, cy + h * 0.04f), 2f)
    }
    if (progress >= 2) {
        val face = Path().apply {
            moveTo(cx - r * 0.92f, cy - h * 0.06f)
            cubicTo(cx - r * 1.02f, cy + h * 0.34f, cx - r * 0.47f, cy + h * 0.43f, cx, cy + h * 0.45f)
            cubicTo(cx + r * 0.47f, cy + h * 0.43f, cx + r * 1.02f, cy + h * 0.34f, cx + r * 0.92f, cy - h * 0.06f)
        }
        drawPath(face, dark, style = Stroke(3f))
    }
    if (progress >= 3) {
        val eyeY = cy + h * 0.05f
        for (side in listOf(-1f, 1f)) {
            val ex = cx + side * r * 0.43f
            val eye = Path().apply {
                moveTo(ex - r * 0.25f, eyeY)
                quadraticTo(ex, eyeY - h * 0.055f, ex + r * 0.25f, eyeY)
                quadraticTo(ex, eyeY + h * 0.045f, ex - r * 0.25f, eyeY)
            }
            drawPath(eye, dark, style = Stroke(2.7f))
            drawCircle(dark, r * 0.075f, Offset(ex, eyeY))
        }
        drawLine(guide, Offset(cx, eyeY + h * 0.02f), Offset(cx - r * 0.04f, eyeY + h * 0.14f), 2f)
        val mouth = Path().apply {
            moveTo(cx - r * 0.17f, cy + h * 0.30f)
            quadraticTo(cx, cy + h * 0.34f, cx + r * 0.17f, cy + h * 0.30f)
        }
        drawPath(mouth, dark, style = Stroke(2.5f))
    }
    if (progress >= 4) {
        val hair = Path().apply {
            moveTo(cx - r * 1.02f, cy - h * 0.02f)
            quadraticTo(cx - r * 1.22f, cy - h * 0.34f, cx - r * 0.62f, cy - h * 0.37f)
            quadraticTo(cx - r * 0.32f, cy - h * 0.55f, cx, cy - h * 0.35f)
            quadraticTo(cx + r * 0.42f, cy - h * 0.54f, cx + r * 0.82f, cy - h * 0.28f)
            quadraticTo(cx + r * 1.18f, cy - h * 0.18f, cx + r * 1.02f, cy + h * 0.02f)
        }
        val hairColor = if (kind == "gojo") Color(0xFF7E8492) else if (kind == "luffy") Color(0xFF3A302B) else accent
        drawPath(hair, hairColor, style = Stroke(5f))
        when (kind) {
            "jinwoo" -> for (i in -2..2) {
                val x = cx + i * r * 0.32f
                val spike = Path().apply {
                    moveTo(x - r * 0.12f, cy - h * 0.27f)
                    lineTo(x + r * 0.02f, cy - h * 0.43f - (if (i % 2 == 0) h * 0.03f else 0f))
                    lineTo(x + r * 0.18f, cy - h * 0.24f)
                }
                drawPath(spike, hairColor, style = Stroke(4f))
            }
            "gojo" -> drawLine(Color(0xFF353943), Offset(cx - r * 0.85f, cy + h * 0.04f),
                Offset(cx + r * 0.85f, cy + h * 0.04f), 8f)
            "luffy" -> {
                val hat = Path().apply {
                    moveTo(cx - r * 1.23f, cy - h * 0.27f)
                    quadraticTo(cx, cy - h * 0.51f, cx + r * 1.23f, cy - h * 0.27f)
                    lineTo(cx + r * 1.26f, cy - h * 0.19f)
                    quadraticTo(cx, cy - h * 0.12f, cx - r * 1.26f, cy - h * 0.19f)
                    close()
                }
                drawPath(hat, Color(0xFFD9B66F), style = Stroke(4f))
                drawLine(Color(0xFFB64F45), Offset(cx - r * 1.18f, cy - h * 0.20f),
                    Offset(cx + r * 1.18f, cy - h * 0.20f), 4f)
                drawLine(dark, Offset(cx - r * 0.38f, cy + h * 0.16f), Offset(cx - r * 0.18f, cy + h * 0.12f), 2f)
            }
            "tanjiro" -> {
                drawLine(Color(0xFF9D554C), Offset(cx - r * 0.08f, cy - h * 0.24f), Offset(cx + r * 0.22f, cy - h * 0.30f), 5f)
                drawCircle(Color(0xFFB77B45), r * 0.11f, Offset(cx - r * 1.12f, cy + h * 0.38f), style = Stroke(2f))
                drawCircle(Color(0xFFB77B45), r * 0.11f, Offset(cx + r * 1.12f, cy + h * 0.38f), style = Stroke(2f))
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
private fun DrawingPad(modifier: Modifier = Modifier) {
    val strokes = remember { mutableStateListOf<InkStroke>() }
    val redoStrokes = remember { mutableStateListOf<InkStroke>() }
    var currentPoints by remember { mutableStateOf<List<Offset>>(emptyList()) }
    var selectedColor by remember { mutableStateOf(Color(0xFF242631)) }
    var brushWidth by remember { mutableStateOf(0.006f) }
    var pendingSave by remember { mutableStateOf<List<InkStroke>>(emptyList()) }
    val context = androidx.compose.ui.platform.LocalContext.current
    val saveLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("image/png")) { uri: Uri? ->
        if (uri != null) {
            val ok = writeDrawing(context, uri, pendingSave)
            Toast.makeText(context, if (ok) "تم حفظ الرسم بنجاح" else "تعذر حفظ الصورة", Toast.LENGTH_SHORT).show()
        }
    }

    Column(modifier, verticalArrangement = Arrangement.spacedBy(9.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(Color(0xFF242631), Color(0xFF9B3848), Color(0xFF315FAD), Color(0xFF24815E), Color(0xFFD39A37), Paper).forEach { color ->
                Box(Modifier.size(29.dp).clip(CircleShape).background(color)
                    .then(if (selectedColor == color) Modifier.border(2.dp, Color.White, CircleShape) else Modifier)
                    .clickable { selectedColor = color })
            }
            Spacer(Modifier.weight(1f))
            Text("حجم القلم", color = Color(0xFFB9BECC), fontSize = 11.sp)
            TextButton(onClick = { brushWidth = if (brushWidth < 0.008f) 0.012f else if (brushWidth < 0.015f) 0.020f else 0.004f }) {
                Text(if (brushWidth < 0.008f) "رفيع" else if (brushWidth < 0.015f) "متوسط" else "عريض", color = Accent, fontSize = 11.sp)
            }
        }
        Canvas(
            Modifier.fillMaxWidth().aspectRatio(1f).shadow(3.dp, RoundedCornerShape(17.dp))
                .clip(RoundedCornerShape(17.dp)).background(Paper)
                .pointerInput(selectedColor, brushWidth) {
                    val canvasWidth = size.width.coerceAtLeast(1).toFloat()
                    val canvasHeight = size.height.coerceAtLeast(1).toFloat()
                    detectDragGestures(
                        onDragStart = { p -> currentPoints = listOf(Offset(p.x / canvasWidth, p.y / canvasHeight)) },
                        onDrag = { change, _ ->
                            currentPoints = currentPoints + Offset(
                                (change.position.x / canvasWidth).coerceIn(0f, 1f),
                                (change.position.y / canvasHeight).coerceIn(0f, 1f)
                            )
                        },
                        onDragEnd = {
                            if (currentPoints.isNotEmpty()) {
                                strokes.add(InkStroke(currentPoints, selectedColor, brushWidth))
                                redoStrokes.clear()
                            }
                            currentPoints = emptyList()
                        },
                        onDragCancel = { currentPoints = emptyList() }
                    )
                }
        ) {
            drawRect(Paper)
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
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            OutlinedButton(onClick = { if (strokes.isNotEmpty()) redoStrokes.add(strokes.removeAt(strokes.lastIndex)) },
                Modifier.weight(0.85f), enabled = strokes.isNotEmpty()) { Text("تراجع") }
            OutlinedButton(onClick = { if (redoStrokes.isNotEmpty()) strokes.add(redoStrokes.removeAt(redoStrokes.lastIndex)) },
                Modifier.weight(0.85f), enabled = redoStrokes.isNotEmpty()) { Text("إعادة") }
            OutlinedButton(onClick = { strokes.clear(); redoStrokes.clear(); currentPoints = emptyList() },
                Modifier.weight(0.8f)) { Text("مسح") }
            Button(onClick = {
                pendingSave = strokes.toList()
                saveLauncher.launch("anime-art-${System.currentTimeMillis()}.png")
            }, Modifier.weight(1.2f), enabled = strokes.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = Accent)) {
                Text("حفظ PNG")
            }
        }
        Text("تُحفظ الصورة في المكان الذي تختاره من نافذة الحفظ في هاتفك.", color = Color(0xFF9EA5B6), fontSize = 11.sp)
    }
}

private fun writeDrawing(context: Context, uri: Uri, strokes: List<InkStroke>): Boolean {
    return try {
        val bitmap = Bitmap.createBitmap(1200, 1200, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bitmap)
        canvas.drawColor(android.graphics.Color.rgb(250, 248, 242))
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
