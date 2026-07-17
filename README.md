# Silicon Valley: The Tech Cartel

پیاده‌سازی کامل بازی رومیزی «Silicon Valley: The Tech Cartel» با زبان جاوا و رابط کاربری JavaFX،
به عنوان پروژه پایان‌ترم درس برنامه‌سازی پیشرفته (دانشگاه فردوسی مشهد).

## نحوه اجرای پروژه

### پیش‌نیازها
- JDK 17 یا بالاتر
- Maven 3.8+
- (کتابخانه‌های JavaFX به‌صورت خودکار توسط Maven از مخزن مرکزی دانلود می‌شوند)

### اجرا با Maven
```bash
mvn clean javafx:run
```

### ساخت jar اجرایی و اجرای مستقیم
```bash
mvn clean package
java --module-path <مسیر SDK جاوا‌اف‌ایکس> --add-modules javafx.controls,javafx.graphics -jar target/SiliconValley.jar
```
یا در صورت استفاده از JDK همراه با JavaFX (مثل Liberica Full / Zulu FX)، صرفاً:
```bash
java -jar target/SiliconValley.jar
```

پس از اجرا، ابتدا صفحه‌ی تنظیمات باز می‌شود (تعداد بازیکنان، نام‌ها و سایز نقشه)، سپس بازی اصلی
آغاز می‌شود.

## توضیح کلی منطق بازی

- **نقشه**: در هر اجرا یک نقشه‌ی مربعی جدید و تصادفی (پیش‌فرض 5×5) با توزیع منطقی سکتورها و
  اعداد فعال‌سازی ساخته می‌شود (`GameMap`). گره‌ها (`Vertex`) در تقاطع ۴ سکتور و یال‌ها
  (`Edge`) روی مرز مشترک دو سکتور مجاور قرار می‌گیرند.
- **فاز راه‌اندازی**: هر بازیکن می‌تواند یک نقش بنیان‌گذار انتخاب کند، سپس در دو دور (رفت و
  برگشت) یک MVP + یک Partnership رایگان می‌گذارد و در پایان دور دوم منابع اولیه می‌گیرد.
- **چرخه‌ی نوبت**: انداختن تاس → تولید منبع یا بحران قانونی (در صورت ۷) → اقدامات اختیاری
  (ساخت Partnership/MVP، ارتقا به Unicorn، معامله با بازار یا بازیکنان) → پایان نوبت و بررسی
  شرط برد (۱۰ امتیاز).
- **بحران قانونی**: مالیات (بازگرداندن نیمی از کارت‌ها برای بازیکنان با بیش از ۷ کارت) و جابه‌جایی
  بازرس قانونی که سکتور مقصد را از تولید منبع مسدود می‌کند.
- **بازار پویا**: قیمت هر منبع بین ۲ تا ۶ سرمایه در نوسان است؛ خرید قیمت را بالا می‌برد و
  ۳ نوبت متوالی بدون خرید قیمت را پایین می‌آورد.
- **امتیازدهی**: امتیاز هر بازیکن همواره به‌صورت پویا از روی سازه‌های فعلی، جریمه‌ی انتخاب نقش
  و مالکیت «بلندترین شبکه Partnership» محاسبه می‌شود (`Player.getVictoryPoints`).

## توضیح UML کلاس‌ها (خلاصه)

```
CompanyStructure (abstract)
 ├── MVP
 ├── Unicorn
 └── Partnership

GameMap
 ├── Sector[][]
 ├── List<Vertex>   (هر Vertex می‌تواند یک CompanyStructure داشته باشد)
 └── List<Edge>     (هر Edge می‌تواند یک Partnership داشته باشد)

Player
 ├── Map<Resource,Integer> resources
 ├── List<CompanyStructure> structures
 └── FounderRole role

GameState  (کل وضعیت قابل ذخیره‌سازی بازی: map, players, market, dice, log, ...)

GameController  (تنها نقطه‌ی ورودی برای منطق بازی؛ view هیچگاه مدل را مستقیم تغییر نمی‌دهد)

پکیج‌بندی:
 com.silicontycoon
  ├── model       (Sector, Vertex, Edge, GameMap, CompanyStructure و زیرکلاس‌ها, Player,
  │                Market, Dice, Auditor, FounderRole, GameEvent, GameState, Resource, SectorType)
  ├── controller  (GameController, GameListener, TurnPhase)
  ├── view        (BoardView, PlayerPanel, MarketPanel, EventLogPanel, GameView, SetupDialog)
  ├── exception   (GameException, InvalidPlacementException,
  │                InsufficientResourcesException, CorruptedSaveException)
  └── util        (Constants, SaveLoadManager)
```

نمودار کامل کلاس‌ها (PlantUML) در فایل `docs/UML.puml` قابل مشاهده و رندر است.

## تقسیم کار بین اعضای گروه

| بخش | مسئول |
|---|---|
| مدل داده (Sector/Vertex/Edge/GameMap) و منطق قوانین مکانی | عضو ۱ |
| GameController، فاز راه‌اندازی، بازار پویا، بحران قانونی، سیستم نقش‌ها | عضو ۲ |
| رابط کاربری JavaFX (BoardView و پنل‌ها)، مدیریت Thread، ذخیره/بارگذاری | هر دو عضو مشترک |

*(این جدول را بر اساس تقسیم‌کار واقعی گروه خودتان ویرایش کنید.)*

## الگوهای طراحی استفاده‌شده

- **Template Method / سلسله‌مراتب انتزاعی**: کلاس انتزاعی `CompanyStructure` با متدهای
  `produce()` و `getVictoryPoints()` که در `MVP`، `Unicorn` و `Partnership` بازنویسی می‌شوند.
- **Observer**: رابط `GameListener` که `GameController` هنگام تغییر وضعیت یا ثبت رویداد،
  ناظرهای ثبت‌شده (لایه‌ی view) را مطلع می‌کند، بدون آنکه به پیاده‌سازی UI وابسته باشد.
- **Strategy**: `FounderRole` به‌عنوان استراتژی رفتاری که نرخ معامله با بازار و هزینه‌ی ارتقا را
  بر اساس نقش انتخابی بازیکن تغییر می‌دهد.
- **MVC**: جداسازی کامل model / controller / view در پکیج‌های جداگانه.

## مدیریت Thread

- منطق سنگین (حل تاس/تولید/بحران) در یک `ExecutorService` تک‌نخی جدا از JavaFX Application
  Thread اجرا می‌شود؛ هر بازگشت به UI از طریق `Platform.runLater(...)` انجام می‌شود
  (`GameView.doRollDice`).
- ذخیره و بارگذاری فایل به‌صورت کاملاً async روی نخ‌های مجزا (`SaveLoadManager.saveAsync` /
  `loadAsync`) اجرا می‌شود تا UI هرگز در حین I/O بلوک نشود.

## ذخیره/بارگذاری و کنترل خطا

- فرمت ذخیره: سریال‌سازی استاندارد جاوا (`ObjectOutputStream`) روی کل شیء `GameState`.
- در صورت بارگذاری فایل خراب/ناقص/خالی، `CorruptedSaveException` (یکی از سه Exception
  سفارشی پروژه) پرتاب و به کاربر در قالب Alert نمایش داده می‌شود.
- سایر خطاهای منطقی بازی (مکان نامعتبر، منابع ناکافی) با
  `InvalidPlacementException` و `InsufficientResourcesException` مدیریت می‌شوند و بازی را
  کرش نمی‌کنند.

## وضعیت امتیازات اضافه

این تحویل، الزامات پایه (۱۰۰ نمره جدول اصلی) را به‌طور کامل پیاده‌سازی کرده و پایه‌ای آماده برای
افزودن امتیازات اضافه (AI, Undo/Redo, سایز نقشه متغیر که از پیش پشتیبانی می‌شود، و ...) فراهم
می‌کند. برای پیاده‌سازی AI، `GameController` تمام متدهای لازم (roll/build/upgrade/trade) را
به‌صورت عمومی و بدون وابستگی به UI در اختیار می‌گذارد، بنابراین افزودن یک `AIPlayerController`
که همان متدها را به‌جای کلیک‌های ماوس فراخوانی کند، ساده است.
