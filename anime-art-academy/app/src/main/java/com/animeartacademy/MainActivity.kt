package com.animeartacademy
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val bg=Color(0xFF0B0D12)
private val panel=Color(0xFF171B25)
private val violet=Color(0xFF9B83FF)
private data class Lesson(val title:String,val series:String,val level:String,val steps:List<String>)
private val lessons=listOf(
 Lesson("أساسيات الرأس والوجه","الأساسيات","مبتدئ",listOf("ارسم دائرة خفيفة لتحديد الجمجمة.","أضف خطًا رأسيًا لتحديد منتصف الوجه.","ارسم خط العينين أفقيًا أسفل منتصف الدائرة.","حدد الفك والذقن بخطين ناعمين.","أضف العينين والأنف والفم ثم امسح خطوط البناء.")),
 Lesson("رسم عين أنمي","الأساسيات","مبتدئ",listOf("ارسم خطًا علويًا منحنيًا للرموش.","أضف قوسًا سفليًا أخف.","ارسم القزحية والحدقة داخل العين.","اترك لمعة بيضاء داخل القزحية.","ظلّل الجزء العلوي من العين بدرجة أغمق.")),
 Lesson("سون جين وو – دراسة ملامح","Solo Leveling","متوسط",listOf("ابدأ بشكل الرأس وخط المنتصف.","حدد الفك الحاد والذقن.","ضع العينين الضيقتين والحاجبين.","ارسم خصل الشعر المتجهة للأعلى.","أضف الظلال حول العين والرقبة.")),
 Lesson("غوجو ساتورو – الوجه والشعر","Jujutsu Kaisen","متوسط",listOf("ارسم شكل الوجه وخط العينين.","حدد مكان العصابة أو العينين.","قسّم الشعر إلى خصل كبيرة متداخلة.","ارسم الياقة العالية للملابس.","أضف التظليل وتفاصيل الشعر.")),
 Lesson("مونكي دي لوفي – الرأس والقبعة","One Piece","مبتدئ",listOf("ارسم دائرة الرأس والفك.","حدد مكان العينين والندبة تحت العين اليسرى.","ارسم الابتسامة الواسعة.","أضف الشعر أسفل القبعة.","ارسم قبعة القش وحافتها.")),
 Lesson("تانجيرو – الوجه والشعر","Demon Slayer","متوسط",listOf("حدد الرأس والفك.","ارسم العينين والحاجبين.","قسّم الشعر إلى خصل غير منتظمة.","أضف القرطين بشكل مبسط.","ارسم الندبة والتظليل النهائي."))
)
class MainActivity:ComponentActivity(){ override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);setContent{ AcademyApp() }}}
@Composable fun AcademyApp(){
 var selected by remember{mutableStateOf<Lesson?>(null)}; var step by remember{mutableIntStateOf(0)}
 MaterialTheme(colorScheme=darkColorScheme(background=bg,surface=panel,primary=violet)){
 Surface(Modifier.fillMaxSize(),color=bg){
 if(selected==null) LazyColumn(Modifier.fillMaxSize().padding(18.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
 item{Text("ANIME ART",color=violet,fontSize=13.sp);Text("ACADEMY",color=Color.White,fontSize=30.sp);Text("ارسم شخصياتك المفضلة خطوة بخطوة",color=Color.LightGray);Text("دروس تعمل دون إنترنت",color=Color(0xFF77D9A6));Text("اختر درسًا للبدء",color=Color.White,fontSize=20.sp)}
 items(lessons){l->Card(colors=CardDefaults.cardColors(containerColor=panel),shape=RoundedCornerShape(18.dp),onClick={selected=l;step=0}){Column(Modifier.fillMaxWidth().padding(16.dp)){Text(l.series,color=violet,fontSize=12.sp);Text(l.title,color=Color.White,fontSize=18.sp);Text("المستوى: ${l.level} • ${l.steps.size} خطوات",color=Color.LightGray,fontSize=12.sp)}}}
 item{Text("نسخة تأسيسية: الدروس ولوحة الرسم جاهزتان للتطوير بإضافة الرسوم التوضيحية وأدوات متقدمة.",color=Color.Gray,fontSize=12.sp)}
 } else {
 val l=selected!!
 Column(Modifier.fillMaxSize().padding(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
 TextButton(onClick={selected=null}){Text("← رجوع",color=violet)}
 Text(l.title,color=Color.White,fontSize=22.sp);Text("الخطوة ${step+1} من ${l.steps.size}",color=violet)
 Card(colors=CardDefaults.cardColors(containerColor=panel)){Column(Modifier.padding(16.dp)){Text(l.steps[step],color=Color.White,fontSize=18.sp);Spacer(Modifier.height(10.dp));Text("نصيحة: ارسم بخط خفيف أولًا ولا تضغط بقوة.",color=Color.LightGray,fontSize=13.sp)}}
 Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){OutlinedButton(onClick={if(step>0)step--},enabled=step>0){Text("السابق")};Button(onClick={if(step<l.steps.lastIndex)step++ else{selected=null;step=0}}){Text(if(step<l.steps.lastIndex)"التالي" else "إنهاء")}}
 Text("مساحة التدريب",color=Color.White,fontSize=18.sp);DrawingPad(Modifier.weight(1f).fillMaxWidth())
 }
 }
 }
 }
}
@Composable fun DrawingPad(modifier:Modifier=Modifier){
 val strokes= remember{mutableStateListOf<List<Offset>>()};var current by remember{mutableStateOf<List<Offset>>(emptyList())}
 Column(modifier,verticalArrangement=Arrangement.spacedBy(8.dp)){
 Canvas(Modifier.weight(1f).fillMaxWidth().background(Color(0xFFF7F5F0),RoundedCornerShape(14.dp)).pointerInput(Unit){detectDragGestures(onDragStart={current=listOf(it)},onDrag={c,_->current=current+c.position},onDragEnd={if(current.isNotEmpty())strokes.add(current);current= emptyList()})}){
 (strokes+listOf(current)).forEach{pts->if(pts.size>1){val p=Path().apply{moveTo(pts.first().x,pts.first().y);pts.drop(1).forEach{lineTo(it.x,it.y)}};drawPath(p,Color(0xFF20232B),style=androidx.compose.ui.graphics.drawscope.Stroke(width=5f))}}
 }
 Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){OutlinedButton(onClick={if(strokes.isNotEmpty())strokes.removeAt(strokes.lastIndex)}){Text("تراجع")};OutlinedButton(onClick={strokes.clear();current= emptyList()}){Text("مسح اللوحة")}}
 }
}
