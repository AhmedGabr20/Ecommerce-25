تمام جدًا 👏🔥
برافو إنك خلصت أول 3 أيام من المشروع (setup + CRUD + Swagger + Validation) 💪
وده بالضبط هو الأساس اللي بنبني عليه.
النهاردة نكمل باقي أيام الأسبوع الأول (Day 4 → Day 7) من الخطة — يعني نبدأ ندخل في التنظيم المتقدم شوية (Profiles، Lombok، Exception، Pagination… إلخ).
هنكمل بنفس مشروع الـ E-commerce API اللي بدأناه.

🗓️ Day 4 – Lombok + تحسين الكود + Profiles
🎯 الهدف:

تتأكد إنك بتستخدم Lombok صح لتقليل الـ boilerplate.

تبدأ تجهز profiles (بيئات dev/prod/test) علشان المشروع يكون جاهز للتوسع.

✅ 1. تأكد من Lombok

أنت بالفعل مستخدم Lombok (بـ @Data, @Builder)، لكن تأكد من:

تفعيل Annotation Processing في IntelliJ:

File → Settings → Build → Compiler → Annotation Processors → ✅ Enable.

لو IDE بيعرض errors في Lombok annotations، اعمل:

mvn clean install ثم أعد تشغيل المشروع.

✅ 2. تنظيم الـ Packages

ابدأ تنظّم المجلدات بالشكل المثالي (عشان هيساعد لما تدخل testing وsecurity):

com.example.ecommerce
 ├─ config/
 ├─ dto/
 ├─ entity/
 ├─ exception/
 ├─ repository/
 ├─ service/
 │   └─ impl/
 ├─ web/
 └─ EcommerceApiApplication.java

✅ 3. إعداد الـ Profiles (بيئات مختلفة)

هنضيف 2 profiles:

dev (بيئة التطوير — H2)

prod (بيئة الإنتاج — MySQL مثلًا لاحقًا)

📁 application.yml

خلّي الأساسي مختصر جدًا:

spring:
  profiles:
    active: dev

📁 application-dev.yml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:h2:mem:ecomdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    driverClassName: org.h2.Driver
    username: sa
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate.format_sql: true
  h2:
    console:
      enabled: true

📁 application-prod.yml

(هنفعّله لاحقًا لما نضيف MySQL)

server:
  port: 8081
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ecomdb
    username: root
    password: root
  jpa:
    hibernate:
      ddl-auto: none
    show-sql: false


🧩 اختبار:

جرّب تغير الـ profile:

spring:
  profiles:
    active: prod


ثم شغّل المشروع ولاحظ الـ port يتغير.

🗓️ Day 5 – Pagination, Sorting, Search
🎯 الهدف:

تحسين REST APIs بإضافة Pagination, Sorting, وSearch لمنتجاتك.

✅ 1. تحديث Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameContainingIgnoreCase(String name);
}

✅ 2. تعديل Service Layer
@Override
public List<ProductDto> getAll(int page, int size, String sortBy) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
    return repo.findAll(pageable)
               .stream()
               .map(this::toDto)
               .collect(Collectors.toList());
}

public List<ProductDto> searchByName(String name) {
    return repo.findByNameContainingIgnoreCase(name)
               .stream()
               .map(this::toDto)
               .collect(Collectors.toList());
}

✅ 3. تحديث Controller
@GetMapping
public ResponseEntity<List<ProductDto>> getAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size,
        @RequestParam(defaultValue = "id") String sortBy) {
    return ResponseEntity.ok(service.getAll(page, size, sortBy));
}

@GetMapping("/search")
public ResponseEntity<List<ProductDto>> search(@RequestParam String name) {
    return ResponseEntity.ok(service.searchByName(name));
}

✅ 4. اختبار في Postman

GET /api/products?page=0&size=3&sortBy=price

GET /api/products/search?name=hammer

🗓️ Day 6 – Global Error Handling + Logging
🎯 الهدف:

تخلي المشروع أكثر احترافية بإدارة الأخطاء بشكل منظم + Logging للعمليات المهمة.

✅ 1. تحديث الـ GlobalExceptionHandler

أنت عندك واحد بالفعل — نضيف عليه تحسين صغير ليعيد JSON منسق:

record ErrorResponse(String error, String message, int status) {}

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(EntityNotFoundException ex){
        return ResponseEntity.status(404).body(new ErrorResponse("Not Found", ex.getMessage(), 404));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex){
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(new ErrorResponse("Validation Error", msg, 400));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleOthers(Exception ex){
        return ResponseEntity.internalServerError().body(new ErrorResponse("Internal Error", ex.getMessage(), 500));
    }
}

✅ 2. Logging (باستخدام Lombok)

ضيف في الخدمات @Slf4j:

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    ...
    @Override
    public ProductDto create(ProductDto dto) {
        log.info("Creating product: {}", dto.getName());
        Product saved = repo.save(toEntity(dto));
        log.info("Created product id={}", saved.getId());
        return toDto(saved);
    }
}


شغّل المشروع وشوف الـ logs في الـ console.

🗓️ Day 7 – مراجعة شاملة + تجهيز لأسبوع 2
🎯 الهدف:

تراجع اللي اتعمل وتربط كل العناصر ببعض، وتجهز المشروع لاستخدام الأدوات المتقدمة في الأسبوع الثاني (Security, AOP, Testing).

✅ مراجعة سريعة:

✅ CRUD للـ Products

✅ Validation

✅ Global Exception

✅ Swagger UI

✅ Profiles (dev/prod)

✅ Pagination & Search

✅ Logging

✅ شوية إضافات لتحسين الهيكل

أضف Entity جديدة: Category

@Entity
@Data
public class Category {
    @Id @GeneratedValue
    private Long id;
    private String name;
}


أضف علاقة في Product:

@ManyToOne
private Category category;


عدّل DTO + Service + Controller accordingly.

Swagger Documentation

أضف @Tag(name = "Products") على الـ controller.

أضف @Operation(summary = "...") على الـ methods.

@Tag(name = "Products", description = "Product management APIs")
@RestController
@RequestMapping("/api/products")
public class ProductController { ... }


Add README.md بسيط للمشروع:

# E-commerce API
- Spring Boot 3.x
- CRUD Products + Categories
- Swagger
- Validation + Error Handling
- Profiles (dev/prod)

✅ جاهز لأسبوع 2 (Security, JWT, AOP, Testing)

من الأسبوع الجاي نبدأ بـ:

🔐 Day 8–9 → Spring Security + JWT

⚙️ Day 10 → AOP Logging

🧪 Day 11–12 → Unit + Integration Tests

🐳 Day 13 → Docker

🚀 Day 14 → Final polish

هل تحب أبدأ أجهز لك الآن خطة أسبوع 2 (Days 8–14) بالتفصيل مع كود Spring Security + JWT كامل (AuthController, JWT Filter, Token Utils)؟