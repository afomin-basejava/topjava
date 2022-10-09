# Стажировка <a href="https://github.com/JavaWebinar/topjava">Topjava</a>

## [Патчи занятия](https://drive.google.com/drive/u/1/folders/1ZsPX879m6x4Va0Wy3D1EQIBsnZUOOvao)

## ![hw](https://cloud.githubusercontent.com/assets/13649199/13672719/09593080-e6e7-11e5-81d1-5cb629c438ca.png) Финальные правки:

Один из вариантов сокрытия полей в примерах Swagger - сделать специальный TO класс. Но можно сделать проще через специальные аннотации: [Hide a Request Field in Swagger API](https://www.baeldung.com/spring-swagger-hide-field)
- поставил `@ApiModelProperty(hidden = true)` для `User.meals` (это поле не нужно при создании, но отображается при запросах `/with-meals`)
- скрыл `Meal.user` через `@JsonIgnore`. Поля нет ни при создании, ни при отображении еды
- скрыл `@ApiModelProperty(hidden = true)` поле `AbstractBaseEntity.id`. Теперь, при создании/обновлении еды и пользователя, `id` не предлагается для ввода в шаблоне Swagger - проверьте.

#### Apply 11_16_HW_fix_swagger.patch

Мелкие правки

#### Apply 11_17_fix.patch

## Миграция на Spring Boot
За основу взят [финальный код проекта BootJava (без Spring Data Rest)](https://javaops.ru/view/bootjava/lesson07)  
Вычекайте в отдельную папку (как отдельный проект) ветку `spring_boot` нашего проекта (так удобнее, не придется постоянно переключаться между ветками):
```
git clone --branch spring_boot --single-branch https://github.com/JavaWebinar/topjava.git topjava_boot
```  
Если будете его менять, [настройте `git remote`](https://javaops.ru/view/bootjava/lesson01#project)  
> Если захотите сами накатить патчи, сделайте ветку `spring_boot` от первого `initial` и в корне **создайте каталог `src\test`**  

----

#### Apply 12_1_init_boot_java
Оставил как в TopJava:
- название приложения  `Calories Management`
- имя базы `topjava`
- пользователей:  `user@yandex.ru`, `admin@gmail.com`, `guest@gmail.com`
- добавил `WebConfig` для перенаправления из корня на документацию Swagger
- в `AppConfig` сохранение `objectMapper` должно проходить только после регистрации `Hibernate5Module` - объединил их в `configureAndStoreObjectMapper`

#### Apply 12_2_add_calories_meals

Добавил из TopJava: 
- Еду, кэширование, калории
- Таблицы назвал в единственном числе: `user_role, meal` (кроме `users`, _user_ зарезервированное слово)
- Общие вещи (пусть небольшие) вынес в сервис : `MealService`
- Проверку принадлежности еды делаю в `MealRepository.checkBelong` с исключением `DataConflictException` (не зависит от `org.springframework.web`)
- Вместо своих конверторов использую `@DateTimeFormat`
- Мигрировал все тесты контроллеров. В выпускном проекте столько тестов необязательно! Достаточно нескольких, на основные юзкейсы.
- Кэширование в выпускном желательно. 7 раз подумайте, что будете кэшировать! **Максимально просто, самые частые запросы, которые редко изменяются**.
- **Добавьте в свой выпускной OpenApi/Swagger - это будет большим плюсом и избавит от необходимости писать документацию**.

### За основу выпускного предлагаю взять этот код миграции, сделав свой выпускной МАКСИМАЛЬНО в этом стиле.
### Успехов с выпускным проектом и в карьере! 
